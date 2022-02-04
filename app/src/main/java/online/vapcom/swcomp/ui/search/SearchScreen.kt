/*
 * (c) VAP Communications Group, 2021
 */

package online.vapcom.swcomp.ui.search

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import online.vapcom.swcomp.R
import online.vapcom.swcomp.data.DictionaryWord
import online.vapcom.swcomp.data.ErrorDescription
import online.vapcom.swcomp.data.Meaning
import online.vapcom.swcomp.data.PartOfSpeech
import online.vapcom.swcomp.ui.common.ChipGroup
import online.vapcom.swcomp.ui.common.ErrorBody

/**
 * Экран ввода слова и отображения его значений, найденных в словаре
 */
@Composable
fun SearchScreen(viewModel: SearchViewModel,
                 onMeaningClick: (meaningId: String) -> Unit,
                 modifier: Modifier = Modifier) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.app_name)) }
            )
        },
        modifier = modifier
    ) { innerPadding -> // паддинги для верхнеуровнего элемента

        SearchBody(
            startWord = state.word,
            isLoading = state.isLoading,
            foundWords = state.foundWords,
            unknownWord = state.unknownWord,
            isError = state.isError,
            error = state.error,
            onWordComplete = viewModel::searchWord,
            onWordChanged = viewModel::wordChanged,
            onMeaningClick = onMeaningClick,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
fun SearchBody(startWord: String,
               isLoading: Boolean,
               foundWords: List<DictionaryWord>,
               unknownWord: Boolean,
               isError: Boolean,
               error: ErrorDescription,
               onWordComplete: (String) -> Unit,
               onWordChanged: () -> Unit,
               onMeaningClick: (meaningId: String) -> Unit,
               modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        SearchWordInput(
            startWord = startWord,
            onWordComplete = onWordComplete,
            onWordChanged = onWordChanged
        )

        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            if(isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .height(48.dp)
                        .width(48.dp)
                        .align(Alignment.Center)
                        .semantics { testTag = "progress" }, // тестовый тег для UI-тестов
                    color = MaterialTheme.colors.secondary
                )
            }

            if(unknownWord) {
                Text(
                    text = stringResource(R.string.word_not_found),
                    color = MaterialTheme.colors.error,
                    modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp)
                )
            } else {
                if(isError) {
                    ErrorBody(error)
                } else if(foundWords.isNotEmpty()) {
                    FoundWordsList(foundWords, onMeaningClick)
                }
            }
        }
    }
}

/**
 * Скроллируемый список слов с их значениями в чипах
 */
@Composable
private fun FoundWordsList(
    foundWords: List<DictionaryWord>,
    onMeaningClick: (meaningId: String) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(bottom = 16.dp),
        modifier = Modifier.fillMaxWidth() // чтобы тени по краям скролла были на всю ширину экрана
                           .semantics { testTag = "wordsList" }
    ) {
        items(items = foundWords) { dictionaryWord ->
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                    text = dictionaryWord.text,
                    style = MaterialTheme.typography.subtitle1,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colors.primary,
                    modifier = Modifier.padding(top = 24.dp)
                )
                if (dictionaryWord.meanings.isNotEmpty()) {
                    val translations = dictionaryWord.meanings.map {
                        it.translation
                    }

                    // callback для перевода индекса нажатого чипа в ID значения
                    val onClick: (index: Int) -> Unit = { index ->
                        onMeaningClick(dictionaryWord.meanings[index].id)
                    }

                    ChipGroup(
                        items = translations,
                        onChipClick = onClick,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    SearchBody(
        startWord = "see",
        isLoading = false,
        foundWords = listOf(
            DictionaryWord("1", "see",
                listOf(
                    Meaning("1", PartOfSpeech.VERB, "видеть", ""),
                    Meaning("2", PartOfSpeech.VERB, "глядеть", "")
                )
            )
        ),
        unknownWord = false,
        isError = false,
        error = ErrorDescription.EMPTY,
        onWordComplete = {},
        onWordChanged = {},
        onMeaningClick = {}
    )
}