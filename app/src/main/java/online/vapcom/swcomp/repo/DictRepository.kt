/*
 * (c) VAP Communications Group, 2020
 */

package online.vapcom.swcomp.repo

interface DictRepository {
    suspend fun searchWord(word: String): RepoReplySearch
    suspend fun getMeaningDetails(meaningID: String): RepoReplyWordMeaning
}
