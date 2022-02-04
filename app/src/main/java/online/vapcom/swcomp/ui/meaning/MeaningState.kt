/*
 * (c) VAP Communications Group, 2021
 */

package online.vapcom.swcomp.ui.meaning

import online.vapcom.swcomp.data.ErrorDescription
import online.vapcom.swcomp.data.PartOfSpeech

/**
 * Модель/состояние экрана значения найденного слова
 */
data class MeaningState(
    val word: String = "",                              // слово или фраза, для которого показывается значение
    val pos: PartOfSpeech = PartOfSpeech.UNKNOWN,       // часть речи
    val transcription: String = "",                     // транскрипция слова, может отсутствовать
    val pronunciationButton: PronunciationButtonType = PronunciationButtonType.NONE,
    val translation: String = "",                       // перевод слова
    val mnemonics: String = "",                         // мнемоника для запоминания слова
    val imageURL: String = "",                          // URL картинки, описывающей значение

    val isLoading: Boolean = false,                     // отображать ли прогресс загрузки
    val isError: Boolean = false,                       // true - отображать ошибку из error
    val error: ErrorDescription = ErrorDescription.EMPTY
)

/**
 * Типы кнопки воспроизведения произношения, берутся из состояния аудиоплеера
 */
enum class PronunciationButtonType {
    NONE,    // не отображать, нет файла с произношением
    SPEAKER, // иконка динамика, звук не воспроизводится
    STOP,    // квадрат Стоп, звук воспроизводится
    ERROR    // динамик с крестиком, звук не воспроизводтия
}