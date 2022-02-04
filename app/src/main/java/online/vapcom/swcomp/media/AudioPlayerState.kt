/*
 * (c) VAP Communications Group, 2021
 */

package online.vapcom.swcomp.media

import online.vapcom.swcomp.data.ErrorDescription

/**
 * Состояния аудиоплеера для UI
 */
sealed class AudioPlayerState {
    object Stop: AudioPlayerState() // остановлен
    object Play: AudioPlayerState() // воспроизводит звуковой файл
    class Error(val error: ErrorDescription): AudioPlayerState() // ошибка воспроизведения
}