/*
 * (c) VAP Communications Group, 2021
 */

package online.vapcom.swcomp.ui.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import online.vapcom.swcomp.R
import online.vapcom.swcomp.ui.icons.Clear

//private const val TAG = "SrcWordInp"

/**
 * Stateful ввод текста с иконкой поиска.
 *
 * @param startWord начальное значение текстового поля ввода слова
 * @param onWordComplete событие завершения ввода текста
 * @param onWordChanged событие изменения текста
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SearchWordInput(startWord: String, onWordComplete: (String) -> Unit, onWordChanged: () -> Unit) {
    val (text, onTextChange) = rememberSaveable { mutableStateOf(startWord) }
    val keyboardController = LocalSoftwareKeyboardController.current

    fun onTextChangeExtended(text: String) {
        onWordChanged()
        onTextChange(text)
    }

    WordInput(
        text = text,
        onTextChange = ::onTextChangeExtended,
        submit = {
            if (text.isNotBlank()) {
                onWordComplete(text)
            }
        }
    ) {
        IconButton(
            onClick = {
                if (text.isNotBlank()) {
                    keyboardController?.hide()
                    onWordComplete(text)
                }
            },
            enabled = text.isNotBlank()) {
            Icon(
                painter = painterResource(id = R.drawable.ic_arrow_right),
                contentDescription = null,
                tint = if(text.isNotBlank()) MaterialTheme.colors.primary
                else MaterialTheme.colors.onSurface //TODO: добавить серый цвет неактивност
            )
        }
    }
}

/**
 * Текстовое поле ввода с кнопкой
 *
 * @param text текущий текст
 * @param onTextChange обработчик события изменения текста
 * @param submit извещение о нажатии пользователем [ImeAction.Done]
 * @param buttonSlot слот для кнопки справа от текста
 */
@Composable
fun WordInput(
    text: String,
    onTextChange: (String) -> Unit,
    submit: () -> Unit,
    buttonSlot: @Composable () -> Unit,
) {
    Row(
        Modifier
            .padding(horizontal = 16.dp)
            .padding(top = 16.dp)
            .height(IntrinsicSize.Min)
    ) {
        WordInputText(
            text = text,
            onTextChange = onTextChange,
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp),
            onImeAction = submit
        )
        Spacer(modifier = Modifier.width(8.dp))
        Box(Modifier.align(Alignment.CenterVertically)) { buttonSlot() }
    }
}

/**
 * [TextField] для ввода слова
 *
 * @param text текущий текст для отображения
 * @param onTextChange обработчик события изменения текста
 * @param modifier модификатор элемента
 * @param onImeAction обработчик события [ImeAction.Done]
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun WordInputText(
    text: String,
    onTextChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    onImeAction: () -> Unit = {}
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    TextField(
        value = text,
        onValueChange = onTextChange,
        colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.Transparent),
        singleLine = true, //NOTE: несмотря на этот флаг, можно ввести \n с железной клавиатуры
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = {
            onImeAction()
            keyboardController?.hide()
        }),
        label = { Text(stringResource(R.string.enter_word)) },
        trailingIcon = { ClearIcon(text, onTextChange) },
        modifier = modifier

        //FIXME: когда у TextField появится maxLength, добавить его сюда
    )
}

/**
 * Крестик clear all, если текст не пустой.
 */
@Composable
fun ClearIcon(text: String, onTextChange: (String) -> Unit) {
    if (text.isNotEmpty()) {
        Icon(Clear, contentDescription = stringResource(R.string.clear_text),
            tint = MaterialTheme.colors.onSurface,
            modifier = Modifier.offset(x = 8.dp).clickable { onTextChange("") }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun WordInputTextPreview() {
    Box(modifier= Modifier.background(MaterialTheme.colors.secondaryVariant)) {
        WordInputText(
            text = "add",
            onTextChange = {},
        )
    }
}