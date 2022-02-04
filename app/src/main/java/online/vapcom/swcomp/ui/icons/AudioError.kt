/*
 * (c) VAP Communications Group, 2021
 */

package online.vapcom.swcomp.ui.icons

import androidx.compose.material.icons.materialIcon
import androidx.compose.material.icons.materialPath
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Иконка ошибки воспроизведения звука, сделана на основе VolumeUp и картинки из pics/ic_audio_error.svg
 */
val AudioError: ImageVector
    get() {
        if (_audioError != null) {
            return _audioError!!
        }
        _audioError = materialIcon(name = "Filled.AudioError") {
            materialPath {
                // динамик
                moveTo(3.0f, 9.0f)
                verticalLineToRelative(6.0f)
                horizontalLineToRelative(4.0f)
                lineToRelative(5.0f, 5.0f)
                lineTo(12.0f, 4.0f)
                lineTo(7.0f, 9.0f)
                lineTo(3.0f, 9.0f)
                close()

                // крест из двух прямоугольников
                // m 14,7 -1,1 8,8 1,-1 z
                moveTo(14.0f, 7.0f)
                lineToRelative(-1.0f, 1.0f)
                lineToRelative(8.0f, 8.0f)
                lineToRelative(1.0f, -1.0f)
                close()

                // m 13,15 1,1 8,-8 -1,-1 z
                moveTo(13.0f, 15.0f)
                lineToRelative(1.0f, 1.0f)
                lineToRelative(8.0f, -8.0f)
                lineToRelative(-1.0f, -1.0f)
                close()
            }
        }
        return _audioError!!
    }

private var _audioError: ImageVector? = null
