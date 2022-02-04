/*
 * (c) VAP Communications Group, 2021
 */

package online.vapcom.swcomp.ui.meaning

import android.util.TypedValue
import android.widget.TextView
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.text.HtmlCompat
import online.vapcom.swcomp.R
import online.vapcom.swcomp.data.PartOfSpeech
import online.vapcom.swcomp.ui.icons.AudioError
import online.vapcom.swcomp.ui.icons.Stop
import online.vapcom.swcomp.ui.icons.VolumeUp

/**
 * Тело экрана отображения значения слова, выбранного в поиске
 */
@Composable
fun MeaningBody(isLoading: Boolean,
                word: String,
                pos: PartOfSpeech,
                transcription: String,
                pronunciationButton: PronunciationButtonType,
                translation: String,
                mnemonics: String,
                imageURL: String,
                onPronunciationClick: () -> Unit,
                modifier: Modifier = Modifier
) {

    LazyColumn(modifier = modifier.fillMaxSize()) {
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                if(isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .height(48.dp)
                            .width(48.dp)
                            .align(Alignment.CenterHorizontally)
                            .padding(24.dp),
                        color = MaterialTheme.colors.secondary
                    )
                }

                Text(
                    text = word,
                    style = MaterialTheme.typography.h6,
                    color = MaterialTheme.colors.primary,
                    modifier = Modifier.padding(top = 24.dp)
                )

                if(transcription.isNotBlank() ||
                    pronunciationButton != PronunciationButtonType.NONE) {
                    Row {
                        if (transcription.isNotBlank()) {
                            Text(
                                text = transcription,
                                style = MaterialTheme.typography.body1,
                                color = MaterialTheme.colors.onSurface,
                                modifier = Modifier.align(Alignment.CenterVertically)
                            )
                        }
                        if(pronunciationButton != PronunciationButtonType.NONE) {
                            IconButton(
                                onClick = onPronunciationClick,
                                modifier = Modifier
                                    .padding(start = 16.dp)
                                    .align(Alignment.CenterVertically)
                            ) {
                                AudioIcon(pronunciationButton)
                            }
                        }
                    }
                }

                if(pos != PartOfSpeech.UNKNOWN ) {
                    Text(
                        text = stringResource(id = posToStringID(pos)),
                        style = MaterialTheme.typography.body1,
                        color = MaterialTheme.colors.secondaryVariant,
                    )
                }

                Text(
                    text = translation,
                    style = MaterialTheme.typography.body1,
                    color = MaterialTheme.colors.onSurface,
                    modifier = Modifier.padding(top = 16.dp)
                )

                if(imageURL.isNotBlank()) {
                    MeaningImage(
                        imageURL = imageURL,
                        contentDescription = translation,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    )
                }
                if(mnemonics.isNotBlank()) {
                    Text(
                        text = stringResource(id = R.string.mnemonics),
                        style = MaterialTheme.typography.subtitle1,
                        color = MaterialTheme.colors.secondaryVariant,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                    StyledText(
                        text = if(isSystemInDarkTheme())
                                   updateMnemonicsForDarkTheme(mnemonics, MaterialTheme.colors.primary)
                               else mnemonics,
                        color = MaterialTheme.colors.onSurface,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }
    }

}

/**
 * Для тёмной темы модифицирует бэкграунд выделенных букв мнемоники, заменяя его на цвет текста, см.
 * MeaningViewModel.replaceXMLTags()
 */
fun updateMnemonicsForDarkTheme(mnemonics: String, textColor: Color): String {
    val color = (textColor.toArgb() and 0x00FFFFFF).toString(16)
    // исходная строка: <span style="background-color:#DDDDDD;">
    return mnemonics.replace(Regex("background-color:#.*;"), "color:#$color;")
}


@Composable
fun AudioIcon(type: PronunciationButtonType) {
    when(type) {
        PronunciationButtonType.NONE -> {}
        PronunciationButtonType.SPEAKER -> {
            Icon(
                imageVector = VolumeUp,
                tint = MaterialTheme.colors.secondaryVariant,
                contentDescription = stringResource(R.string.cd_play)
            )
        }
        PronunciationButtonType.STOP -> {
            Icon(
                imageVector = Stop,
                tint = MaterialTheme.colors.onSurface,
                contentDescription = stringResource(R.string.cd_stop)
            )
        }
        PronunciationButtonType.ERROR -> {
            Icon(
                imageVector = AudioError,
                tint = MaterialTheme.colors.error,
                contentDescription = stringResource(R.string.cd_audio_error)
            )
        }
    }
}

/**
 * Возвращает ID строки, соответствующей названию части речи
 */
fun posToStringID(pos: PartOfSpeech): Int {
    return when(pos) {
        PartOfSpeech.UNKNOWN -> R.string.pos_unknown
        PartOfSpeech.NOUN -> R.string.pos_noun
        PartOfSpeech.VERB -> R.string.pos_verb
        PartOfSpeech.ADJECTIVE -> R.string.pos_adjective
        PartOfSpeech.ADVERB -> R.string.pos_adverb
        PartOfSpeech.PREPOSITION -> R.string.pos_preposition
        PartOfSpeech.PRONOUN -> R.string.pos_pronoun
        PartOfSpeech.CARDINAL_NUMBER -> R.string.pos_cardinal_number
        PartOfSpeech.CONJUNCTION -> R.string.pos_conjunction
        PartOfSpeech.INTERJECTION -> R.string.pos_interjection
        PartOfSpeech.ARTICLE -> R.string.pos_article
        PartOfSpeech.ABBREVIATION -> R.string.pos_abbreviation
        PartOfSpeech.PARTICLE -> R.string.pos_particle
        PartOfSpeech.ORDINAL_NUMBER -> R.string.pos_ordinal_number
        PartOfSpeech.MODAL_VERB -> R.string.pos_modal_verb
        PartOfSpeech.PHRASE -> R.string.pos_phrase
        PartOfSpeech.IDIOM -> R.string.pos_idiom
    }
}


/**
 * Пока Compose сам не умеет выводить стилизованный текст, используем старый TextView,
 * пример здесь:
 * https://stackoverflow.com/questions/68549248/android-jetpack-compose-how-to-show-styled-text-from-string-resources
 */
@Deprecated("При появлении в Text() возможности отображения HTML-строк эту композяблю можно будет убрать")
@Composable
fun StyledText(text: String, color: Color, modifier: Modifier = Modifier) {
    AndroidView(
        modifier = modifier,
        factory = { context -> TextView(context) },
        update = {
            it.text = HtmlCompat.fromHtml(text, HtmlCompat.FROM_HTML_MODE_COMPACT)
            it.setTextColor(color.toArgb())

            //TODO: избавится от хардкода размера и стиля, сделать преобразование
            // style = MaterialTheme.typography.body1 в стиль TextView
            it.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16F)
        }
    )
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MeaningBody(
        isLoading = false,
        word = "see",
        pos = PartOfSpeech.VERB,
        transcription = "sē",
        pronunciationButton = PronunciationButtonType.SPEAKER,
        translation = "видеть",
        mnemonics = "I see you!",
        imageURL = "https://upload.wikimedia.org/wikipedia/en/a/a0/See_%28TV_series%29_poster.jpg",
        {}
    )
}