/*
 * (c) VAP Communications Group, 2021
 */

package online.vapcom.swcomp.ui.search

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import online.vapcom.swcomp.repo.DictRepository
import online.vapcom.swcomp.repo.RepoReplySearch

private const val TAG = "SearchVM.."

/**
 * Модель поиска слова в словаре
 */
class SearchViewModel(private val dictRepository: DictRepository) : ViewModel() {

//+++
    init {
        Log.w(TAG, "++++ INIT: ${this.hashCode()}")
    }
//+++

    // model/state экрана поиска слова
    private val _state = MutableStateFlow(SearchState())
    val state: StateFlow<SearchState>
        get() = _state


    /**
     * Запускает поиск слова в словаре
     */
    fun searchWord(word: String) {
        Log.i(TAG, "++++ SEARCH WORD: '$word'")

        _state.value = _state.value.copy(isLoading = true, isError = false, foundWords = emptyList(), unknownWord = false)

        viewModelScope.launch {
            val reply = dictRepository.searchWord(word)
            Log.i(TAG, "++++ searchWord: reply: $reply")
            when(reply) {
                is RepoReplySearch.FoundWords -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        foundWords = reply.words
                        )
                }
                RepoReplySearch.UnknownWord -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        unknownWord = true
                    )
                }
                is RepoReplySearch.Error -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        isError = true,
                        error = reply.error
                    )
                }
            }
        }
    }

    /**
     * Индикация изменения слова в поле ввода, по ней прячем unknownWord
     */
    fun wordChanged() {
        if(_state.value.unknownWord) {
            _state.value = _state.value.copy(
                unknownWord = false
            )
        }
    }

//+++
    override fun onCleared() {
        super.onCleared()
        Log.w(TAG, "++++ ON CLEARED: ${this.hashCode()}")
    }
//+++

}
