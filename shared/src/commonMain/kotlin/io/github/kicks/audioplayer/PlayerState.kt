package io.github.kicks.audioplayer

import androidx.compose.runtime.*

@Stable
class PlayerState{
    var isPlaying by mutableStateOf(false)
    internal set
    var isBuffering by mutableStateOf(false)
    var currentTime: Long by mutableStateOf(0)
    var duration = 0L
    var currentItemIndex = -1
    var canNext by mutableStateOf(false)
    var canPrev by mutableStateOf(false)
}

@Composable
fun rememberPlayerState(): PlayerState {
    return remember {
        PlayerState()
    }
}