/*
 * (c) VAP Communications Group, 2021
 */

package online.vapcom.swcomp.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.flowlayout.FlowRow
import online.vapcom.swcomp.ui.theme.md_theme_dark_surfaceVariant
import online.vapcom.swcomp.ui.theme.md_theme_light_surfaceVariant

/**
 * Имитация Material ChipGroup
 * Использует FlowRow из Аккомпаниста
 */
@Composable
fun ChipGroup(
    items: List<String>,
    onChipClick: (index: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    FlowRow(
        modifier = modifier,
        mainAxisSpacing = 8.dp,
        crossAxisSpacing = 8.dp
    ) {
        items.forEachIndexed { index, text ->
            if(text.isNotBlank()) {
                Text(
                    text = text,
                    style = MaterialTheme.typography.body2,
                    maxLines = 1,
                    modifier = Modifier
                        .background(
                            //TODO in MD3: MaterialTheme.colors.surfaceVariant,
                            color = if(isSystemInDarkTheme()) md_theme_dark_surfaceVariant
                                    else md_theme_light_surfaceVariant,
                            shape = CircleShape
                        )
                        .padding(horizontal = 12.dp)
                        .height(32.dp)
                        .wrapContentHeight()
                        .clickable {
                            onChipClick(index)
                        }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ChipGroupPreview() {
    Column(modifier = Modifier.height(64.dp)) {
        Spacer(modifier = Modifier.height(16.dp))
        ChipGroup(
            items = listOf("start", "middle", "end", ""),
            onChipClick = {}
        )
    }
}