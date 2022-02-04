/*
 * (c) VAP Communications Group, 2021
 */

package online.vapcom.swcomp.ui.meaning

import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import online.vapcom.swcomp.R
import online.vapcom.swcomp.ui.common.ErrorBody
import online.vapcom.swcomp.ui.icons.ArrowBack

/**
 * Экран отображения значения слова, выбранного на экране поиска
 */
@Composable
fun MeaningScreen(viewModel: MeaningViewModel,
                  onUpClick: () -> Unit,
                  modifier: Modifier = Modifier) {

    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onUpClick) {
                        Icon(
                            imageVector = ArrowBack,
                            contentDescription = stringResource(R.string.cd_back)
                        )
                    }
                },
                title = {
                    Text(text = state.word, maxLines = 1, overflow = TextOverflow.Ellipsis)
                },
            )
        },
        modifier = modifier
    ) { innerPadding -> // паддинги для верхнеуровнего элемента

        if(state.isError) {
            ErrorBody(
                state.error,
                modifier = Modifier.padding(innerPadding)
            )
        } else {
            MeaningBody(
                isLoading = state.isLoading,
                word = state.word,
                pos = state.pos,
                transcription = state.transcription,
                pronunciationButton = state.pronunciationButton,
                translation = state.translation,
                mnemonics = state.mnemonics,
                imageURL = state.imageURL,
                onPronunciationClick = viewModel::playPronunciation,
                modifier = Modifier.padding(innerPadding)
            )
        }
    }

}

