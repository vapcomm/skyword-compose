/*
 * (c) VAP Communications Group, 2021
 */

package online.vapcom.swcomp.media

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.media2.player.MediaPlayer
import androidx.media.AudioAttributesCompat
import androidx.media2.common.MediaItem
import androidx.media2.common.SessionPlayer
import androidx.media2.common.UriMediaItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import online.vapcom.swcomp.data.ErrorDescription
import java.util.concurrent.Executor

private const val TAG = "AudioPlayr"

/**
 * Плеер для воспроизведения звуков
 *
 * NOTE: под капотом у androidx.media2.player.MediaPlayer работает ExoPlayer
 */
class AudioPlayer(private val context: Context) {

    private var mediaPlayer: MediaPlayer? = null

    private val _playerState = MutableStateFlow<AudioPlayerState>(AudioPlayerState.Stop)
    val playerState = _playerState.asStateFlow()

    /**
     * Запускает воспроизведение звукового файла по заданному URL.
     * Если он уже был запущен на воспроизведение, то останавливает его и сбрасывает плеер.
     */
    fun startStop(audioURL: String) {
        if(mediaPlayer == null) {
            mediaPlayer = createPlayer(audioURL)
        }

        Log.i(TAG, "#### PLAYER CURRENT STATE: ${mediaPlayer?.playerState}")

        when(mediaPlayer?.playerState) {
            SessionPlayer.PLAYER_STATE_IDLE -> { // плеер только что проинициализирован
                mediaPlayer?.prepare() // запускаем загрузку файла, индикация результата подготовки придёт в PlayerCallback
            }
            SessionPlayer.PLAYER_STATE_PAUSED -> { // файл готов для воспроизведения, либо воспроизведение закончено
                mediaPlayer?.play()
            }
            SessionPlayer.PLAYER_STATE_PLAYING -> { // файл воспроизводится
                //NOTE: в androidx.media2.player.MediaPlayer нет метода stop(),
                //      поэтому здесь закрываем сразу
                closePlayer()
            }

            SessionPlayer.PLAYER_STATE_ERROR -> {
                Log.e(TAG, "#### PLAYER IN ERROR STATE")
                closePlayer()
            }
        }

    }

    /**
     * Создаёт новый инстанс медиаплеера, инициализирует его для воспроизведения файла из audioURL
     */
    private fun createPlayer(audioURL: String): MediaPlayer {
        return MediaPlayer(context).apply {
            setAudioAttributes(
                AudioAttributesCompat.Builder()
                    .setContentType(AudioAttributesCompat.CONTENT_TYPE_SPEECH)
                    .setUsage(AudioAttributesCompat.USAGE_MEDIA)
                    .build()
            )

            // файл для воспроизведения
            setMediaItem(UriMediaItem.Builder(Uri.parse(audioURL)).build())

            // обработчики событий плеера, здесь даём индикации наверх
            registerPlayerCallback(PlayerExecutor(), object : MediaPlayer.PlayerCallback() {
                override fun onError(mp: MediaPlayer, item: MediaItem, what: Int, extra: Int) {
                    Log.e(TAG, "#### ERROR: what: $what, extra: $extra")
                    _playerState.value = AudioPlayerState.Error(
                        ErrorDescription(false, what,"Audio Player error: what: $what, extra: $extra")
                    )
                }

                override fun onInfo(mp: MediaPlayer, item: MediaItem, what: Int, extra: Int) {
                    Log.i(TAG, "#### INFO: state: ${mp.playerState}, what: $what, extra: $extra")

                    when(mp.playerState) {
                        SessionPlayer.PLAYER_STATE_PAUSED -> { // prepare() завершился, можно воспроизводить
                            Log.i(TAG, "#### PLAY STARTED")
                            mp.play()
                        }
                        SessionPlayer.PLAYER_STATE_PLAYING -> {
                            Log.i(TAG, "#### PLAYING")
                            _playerState.value = AudioPlayerState.Play
                        }
                        else -> { }
                    }
                }

                override fun onPlaybackCompleted(player: SessionPlayer) {
                    Log.i(TAG, "#### PLAY ENDED")
                    closePlayer()
                }
            })
        }
    }

    /**
     * Закрывает медиаплеер, освобожадая все системные ресурсы.
     */
    private fun closePlayer(needIndication: Boolean = true) {
        mediaPlayer?.close()
        mediaPlayer = null
        if(needIndication) {
            _playerState.value = AudioPlayerState.Stop
        }
    }

    /**
     * Останавливает воспроизведение
     */
    fun stop() {
        closePlayer(needIndication = false)
    }

    /**
     * Простейший Executor, который просто выполняет команду на текущей нити.
     * Как показывают логи, это не main-нить.
     */
    class PlayerExecutor: Executor {
        override fun execute(command: Runnable?) {
            command?.run()
        }
    }
}
