/*
 * (c) VAP Communications Group, 2022
 */

package online.vapcom.swcomp.ui.search

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import online.vapcom.swcomp.data.DictionaryWord
import online.vapcom.swcomp.data.ErrorDescription
import online.vapcom.swcomp.data.Meaning
import online.vapcom.swcomp.data.PartOfSpeech
import online.vapcom.swcomp.ui.theme.SkywordTheme
import org.junit.Assert.*
import org.junit.Rule

import org.junit.Test

/**
 * Пример базовых тестов composable-функций
 */
class SearchScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun searchBodyLoading() {
        composeTestRule.setContent {
            SkywordTheme {
                SearchBody(startWord = "add", isLoading = true,
                    foundWords = emptyList(), unknownWord = false, isError = false,
                    error = ErrorDescription.EMPTY, {}, {}, {}
                )
            }
        }

        composeTestRule.onNodeWithText("add").assertIsDisplayed()
        composeTestRule.onNodeWithTag(testTag = "progress").assertIsDisplayed()
    }

    @Test
    fun searchBodyShowResults() {
        composeTestRule.setContent {
            SkywordTheme {
                SearchBody(startWord = "add", isLoading = false,
                    foundWords = listOf(
                        DictionaryWord("1", "add", listOf(
                            Meaning("100", PartOfSpeech.VERB, "добавлять", ""),
                            Meaning("101", PartOfSpeech.VERB, "складывать", "")
                        )),
                        DictionaryWord("2", "address", listOf(
                            Meaning("103", PartOfSpeech.NOUN, "адрес", "")
                        )),
                    ),
                    unknownWord = false, isError = false,
                    error = ErrorDescription.EMPTY, {}, {}, {}
                )
            }
        }

        composeTestRule.onNodeWithTag(testTag = "progress").assertDoesNotExist()    // прогресс не добавляется в иерархию

        composeTestRule.onNodeWithTag(testTag = "wordsList").assertIsDisplayed()
        composeTestRule.onNodeWithTag(testTag = "wordsList").onChildAt(0).assertTextEquals("add")                                                                               //NOTE: использовать индексы нод не рекомендуется, но для простоты примера тестирования списков оставлю так
        composeTestRule.onNodeWithTag(testTag = "wordsList").onChildAt(1).assertTextEquals("добавлять")
        composeTestRule.onNodeWithTag(testTag = "wordsList").onChildAt(2).assertTextEquals("складывать")
        composeTestRule.onNodeWithTag(testTag = "wordsList").onChildAt(3).assertTextEquals("address")
        composeTestRule.onNodeWithTag(testTag = "wordsList").onChildAt(4).assertTextEquals("адрес")
    }

    @Test
    fun searchBodyClickOnMeaningChip() {
        var wasMeaningClick = false

        composeTestRule.setContent {
            SkywordTheme {
                SearchBody(startWord = "add", isLoading = false,
                    foundWords = listOf(
                        DictionaryWord("1", "add", listOf(
                            Meaning("100", PartOfSpeech.VERB, "добавлять", ""),
                        ))
                    ),
                    unknownWord = false, isError = false, error = ErrorDescription.EMPTY,
                    onWordComplete = {}, onWordChanged =  {},
                    onMeaningClick = {
                        wasMeaningClick = true
                    })
            }
        }

        composeTestRule.onNodeWithTag(testTag = "wordsList").assertIsDisplayed()
        composeTestRule.onNodeWithText("добавлять").performClick()  // тапаем на значение слова
        assertTrue(wasMeaningClick) // должен сработать колбэк onMeaningClick
    }

}