/*
 * (c) VAP Communications Group, 2020
 */

package online.vapcom.swcomp.repo

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import online.vapcom.swcomp.network.GetMeaningDetailsCall
import online.vapcom.swcomp.network.SearchWordCall

private const val TAG = "DictRepo.."
//private const val ERROR_MODULE_NUM = 10     // номер модуля в кодах ошибок, см. ErrorDescription
// последний использованный код ошибки: 10

/**
 * Рабочая реализация репозитория словаря
 */
class DictRepositoryImpl: DictRepository{

    /**
     * Поиск слова в словаре
     */
    override suspend fun searchWord(word: String): RepoReplySearch = withContext(Dispatchers.IO) {
        Log.i(TAG, ">>> searchWord: '$word'")

        //TODO: поиск слова в кэше

        val call = SearchWordCall(word.trim())
        call.execute()
        if (call.success) {
            if(call.foundWords.isEmpty())
                RepoReplySearch.UnknownWord
            else RepoReplySearch.FoundWords(call.foundWords)
            //TODO: добавить слово в кэш
        } else {
            RepoReplySearch.Error(call.errorDescription)
        }
    }

    /**
     * Загрузка подробных данных значения слова по его ID
     */
    override suspend fun getMeaningDetails(meaningID: String): RepoReplyWordMeaning = withContext(Dispatchers.IO) {
        Log.i(TAG, ">>> getMeaningDetails: ID: '$meaningID'")

        val call = GetMeaningDetailsCall(meaningID)
        call.execute()
        if (call.success) {
            if(call.hasDetails) //TODO: кэшировать полученные данные
                RepoReplyWordMeaning.WordMeaningDetails(call.meaningDetails)
            else RepoReplyWordMeaning.UnknownMeaning
        } else {
            RepoReplyWordMeaning.Error(call.errorDescription)
        }
    }
}
