/*
 * (c) VAP Communications Group, 2021
 */

package online.vapcom.swcomp.ui.search

import online.vapcom.swcomp.data.DictionaryWord
import online.vapcom.swcomp.data.ErrorDescription

/**
 * Модель/состояние экрана поиска слова
 */
data class SearchState(
    val word: String = "",                              // слово для поиска
    val isLoading: Boolean = false,                     // отображать ли прогресс загрузки
    val foundWords: List<DictionaryWord> = emptyList(), // список найденных слов
    val unknownWord: Boolean = false,                   // неизвестное слово
    val isError: Boolean = false,                       // true - отображать ошибку из error
    val error: ErrorDescription = ErrorDescription.EMPTY
)
