/*
 * (c) VAP Communications Group, 2020
 */


package online.vapcom.swcomp.repo

import online.vapcom.swcomp.data.DictionaryWord
import online.vapcom.swcomp.data.ErrorDescription
import online.vapcom.swcomp.data.MeaningDetails

/*
 * Ответы от репозитория
 */

sealed class RepoReplySearch {
    /**
     * Список найденных слов
     */
    class FoundWords(val words: List<DictionaryWord>) : RepoReplySearch()

    /**
     * Слово не найдено
     */
    object UnknownWord: RepoReplySearch()

    /**
     * Ошибка при обработке запроса
     */
    class Error(val error: ErrorDescription) : RepoReplySearch()
}

sealed class RepoReplyWordMeaning {
    class WordMeaningDetails(val meaningDetails: MeaningDetails) : RepoReplyWordMeaning()

    /**
     * Значение слова не найдено
     */
    object UnknownMeaning: RepoReplyWordMeaning()

    /**
     * Ошибка при обработке запроса
     */
    class Error(val error: ErrorDescription) : RepoReplyWordMeaning()
}
