/*
 * (c) VAP Communications Group, 2021
 */

package online.vapcom.swcomp.ui.meaning

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import online.vapcom.swcomp.data.ErrorDescription
import online.vapcom.swcomp.data.UIErrno
import online.vapcom.swcomp.media.AudioPlayer
import online.vapcom.swcomp.media.AudioPlayerState
import online.vapcom.swcomp.repo.DictRepository
import online.vapcom.swcomp.repo.RepoReplyWordMeaning

private const val TAG = "MeaningVM."

/**
 * Модель отображение описания выбранного в поиске значения слова
 */
class MeaningViewModel(private val meaningID: String, private val dictRepository: DictRepository,
                       private val audioPlayer: AudioPlayer) : ViewModel() {

    // model/state экрана значения слова
    private val _state = MutableStateFlow(MeaningState())
    val state = _state.asStateFlow()

    private var pronunciationURL: String = ""   // URL аудиофайла с произношением слова

    init {
        loadMeaning()

        viewModelScope.launch {
            audioPlayer.playerState.collect(::updatePlayerState)
        }
    }

    /**
     * Загружает данные по значениею слова из репозитория
     */
    private fun loadMeaning() {
        Log.i(TAG, "++++ LOAD MEANING: '$meaningID'")
        _state.value = MeaningState(isLoading = true)

        viewModelScope.launch {
            val reply = dictRepository.getMeaningDetails(meaningID)
            Log.i(TAG, "++++ getMeaningDetails: reply: $reply")
            when (reply) {
                is RepoReplyWordMeaning.WordMeaningDetails -> {
                    Log.i(TAG,"++++ getMeaningDetails: WordMeaningDetails: ${reply.meaningDetails}")
                    pronunciationURL = reply.meaningDetails.pronunciationURL
                    _state.value = MeaningState(
                        word = reply.meaningDetails.text,
                        pos = reply.meaningDetails.pos,
                        transcription = reply.meaningDetails.transcription,
                        pronunciationButton = if(pronunciationURL.isNotBlank()) PronunciationButtonType.SPEAKER
                                              else PronunciationButtonType.NONE,
                        translation = reply.meaningDetails.translation,
                        mnemonics = replaceXMLTags(reply.meaningDetails.mnemonics),
                        imageURL = reply.meaningDetails.imageURL
                    )
                }

                RepoReplyWordMeaning.UnknownMeaning -> {
                    // отображать как ошибку, т.к. это баг на сервере или в его базе данных
                    _state.value = MeaningState(
                        isError = true,
                        error = ErrorDescription(false, UIErrno.DATA_NOT_FOUND.errno, "")
                    )
                }

                is RepoReplyWordMeaning.Error -> {
                    _state.value = MeaningState(isError = true, error = reply.error)
                }
            }
        }
    }

    /**
     * Заменяет теги <ru>, <en> на HTML-теги для подсветки подстрок
     */
    private fun replaceXMLTags(mnemonics: String): String {
        return mnemonics.replace("<ru>", "<b>")
            .replace("</ru>", "</b>")
            // английские слова подсвечиваем серым
            .replace("<en>", "<b><span style=\"background-color:#DDDDDD;\">")
            .replace("</en>", "</span></b>")
    }

    /**
     * Обработчик кнопки воспроизведения файла с произношением слова.
     * Если звук не воспроизводится, он запускается на воспроизведение с начала, иначе он останавливается.
     * Паузы не делаем. Поведение аналогично translate.google.com
     */
    fun playPronunciation() {
        Log.i(TAG, "++++ PLAY: '$pronunciationURL'")

        audioPlayer.startStop(pronunciationURL)
    }

    /**
     * Обработчик индикаций от аудиоплеера, при изменении его состояния меняем вид кнопки воспроизведения
     * произношения.
     */
    private fun updatePlayerState(state: AudioPlayerState) {
        Log.i(TAG, "++++ player state: $state")
        _state.value = _state.value.copy(
            pronunciationButton = when(state) {
                AudioPlayerState.Play -> PronunciationButtonType.STOP
                AudioPlayerState.Stop -> PronunciationButtonType.SPEAKER
                is AudioPlayerState.Error -> {
                    Log.e(TAG, "++++ player error: ${state.error}")
                    PronunciationButtonType.ERROR
                }
            }
        )
    }

    override fun onCleared() {
        super.onCleared()
        audioPlayer.stop()
    }

}
