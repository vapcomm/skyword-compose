/*
 * (c) VAP Communications Group, 2021
 */

package online.vapcom.swcomp.ui.meaning

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.annotation.ExperimentalCoilApi
import coil.compose.ImagePainter
import coil.compose.rememberImagePainter
import online.vapcom.swcomp.R

/**
 * Картинка, отображающее значение, грузится через Coil.
 * В случае ошибки загрузки показывает рамку с вопросиком.
 */
@OptIn(ExperimentalCoilApi::class)
@Composable
fun MeaningImage(imageURL: String,
                 contentDescription: String,
                 modifier: Modifier = Modifier
) {
    val painter = rememberImagePainter(
        data = imageURL,
        builder = {
            error(R.drawable.image_load_fallback)
        }
    )

    Box(modifier = modifier) {
        // картинка значения слова
        //NOTE: с сервера приходит размер 200x150, что явно не достаточно для современных смартфонов,
        //      но на CDN есть картинки 640x480, которые приходят в описании значений ответ на поиск слова.
        //      В обработчике запроса GetMeaningDetailsCall 200x150 подменяется на 640x480, но остаётся
        //      качество 50.
        //      Лучший вариант реализации картинок - когда приложение заказывает подходящий размер
        //      в зависимости от текущего размера экрана, а сервер возращает наиболее близкое
        //      разрешение, чтобы картинка качественно масштабировалась.
        Image(
            painter = painter,
            contentDescription = contentDescription,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .widthIn(320.dp, 640.dp)
                .heightIn(240.dp, 480.dp)
        )

        when(painter.state) {
            is ImagePainter.State.Loading -> {
                CircularProgressIndicator(
                    color = MaterialTheme.colors.secondary,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            is ImagePainter.State.Error -> {
                Image(
                    painterResource(id = R.drawable.image_load_fallback),
                    contentDescription = stringResource(id = R.string.error),
                    modifier = Modifier.size(width = 160.dp, height = 160.dp)
                )
            }
            else -> {} // do nothing
        }
    }
}
