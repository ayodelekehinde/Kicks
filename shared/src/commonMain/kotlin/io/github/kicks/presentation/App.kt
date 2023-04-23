package io.github.kicks.presentation

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.kicks.audioplayer.AudioPlayer
import io.github.kicks.audioplayer.rememberPlayerState
import io.github.kicks.data.Audio
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource

@Composable
fun App(){
    MaterialTheme {
        val viewModel = remember { HomeViewModel() }
        val audioList = remember { viewModel.getAudios() }
        val playerState = rememberPlayerState()
        val player = remember { AudioPlayer(playerState) }

        LaunchedEffect(Unit){
            player.addSongsUrls(audioList.map { it.streamUrl })
        }
        HomeScreen(
            audioList = audioList,
            currentDuration = playerState.currentTime,
            totalDurationInSeconds = playerState.duration,
            totalDuration = if (playerState.currentItemIndex >= 0) audioList[playerState.currentItemIndex].duration else "0:00",
            isBuffering = playerState.isBuffering,
            isPlaying = playerState.isPlaying,
            currentPlayingIndex = playerState.currentItemIndex,
            onItemClick = { player.play(it) },
            onPause = { player.pause() },
            onPlay = { player.play() },
            onNext = { player.next() },
            onPrev = { player.prev() },
            onSeek = { player.seekTo(it) }
        )

    }
}

@Composable
fun HomeScreen(
    audioList: List<Audio>,
    currentDuration: Long,
    totalDurationInSeconds: Long,
    totalDuration: String,
    isBuffering: Boolean,
    isPlaying: Boolean,
    currentPlayingIndex: Int,
    onItemClick: (Int) -> Unit,
    onPause: () -> Unit,
    onPlay: () -> Unit,
    onNext: () -> Unit,
    onPrev: () -> Unit,
    onSeek: (Double) -> Unit
){
    Column(Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.2f))){
        MusicList(audioList = audioList, onClick = onItemClick, currentPlayingIndex = currentPlayingIndex)
        BottomPlayer(
            modifier = Modifier.fillMaxWidth(),
            currentDuration = currentDuration,
            totalDurationInSeconds = totalDurationInSeconds,
            totalDuration = totalDuration,
            isBuffering = isBuffering,
            isPlaying = isPlaying,
            onPause = onPause,
            onPlay = onPlay,
            onNext = onNext,
            onPrev = onPrev,
            onSeek = onSeek,
        )
    }
}

@Composable
fun ColumnScope.MusicList(
    modifier: Modifier = Modifier,
    audioList: List<Audio>,
    currentPlayingIndex: Int,
    onClick: (Int) -> Unit
){
    val state = rememberLazyListState()
    Column(modifier = modifier.fillMaxWidth().weight(1f).background(Color.Black)){
        LazyColumn(state = state) {
            itemsIndexed(audioList){ index, audio ->
                AudioItem(audio, index == currentPlayingIndex){
                    onClick(index)
                }
            }
        }
    }
    LaunchedEffect(currentPlayingIndex){
        if (currentPlayingIndex != -1) {
            // Check if item at index 5 is visible
            val visibleItems = state.layoutInfo.visibleItemsInfo
            val itemIsVisible = visibleItems.any { it.index == currentPlayingIndex }
            if (!itemIsVisible) {
                state.animateScrollToItem(currentPlayingIndex)
            }
        }
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
fun AudioItem(
    audio: Audio,
    isPlaying: Boolean,
    onClick: () -> Unit
){
    val color = if (isPlaying) Color.Blue.copy(alpha = 0.9f) else Color.DarkGray.copy(alpha = 0.5f)
    val backgroundColor = if (isPlaying) Color.Blue else Color.DarkGray.copy(alpha = 0.3f)
    Row(
        modifier = Modifier
            .padding(vertical = 10.dp, horizontal = 10.dp)
            .background(backgroundColor, RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
        ,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ){
        Image(
            painterResource(audio.imageUrl),
            contentDescription = null,
            modifier = Modifier.padding(10.dp).size(50.dp).clip(RoundedCornerShape(15.dp))
        )
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(5.dp)){
            Text(audio.title, fontSize = 16.sp, color = Color.White)
            Text(audio.artists, fontSize = 14.sp, fontWeight = FontWeight.Normal, color = Color.White)
        }
        Text(
            audio.duration,
            fontSize = 14.sp,
            color = Color.White,
            modifier = Modifier.padding(10.dp).background(color, RoundedCornerShape(5.dp)).padding(3.dp)
        )
    }
}

@Composable
fun BottomPlayer(
    modifier: Modifier = Modifier,
    currentDuration: Long,
    totalDurationInSeconds: Long,
    totalDuration: String,
    isBuffering: Boolean,
    isPlaying: Boolean,
    onPause: () -> Unit,
    onPlay: () -> Unit,
    onNext: () -> Unit,
    onPrev: () -> Unit,
    onSeek: (Double) -> Unit
){
    Column(modifier = modifier.background(Color.Black).padding(horizontal = 20.dp), horizontalAlignment = Alignment.CenterHorizontally){
        SeekBarAndDuration(
            modifier = modifier,
            isPlaying = isPlaying,
            currentDuration = currentDuration,
            totalDurationInSeconds = totalDurationInSeconds,
            totalDuration = totalDuration,
            onSeek = onSeek
        )
        Controls(
            modifier = Modifier.padding(vertical = 10.dp).align(Alignment.CenterHorizontally),
            isBuffering = isBuffering,
            isPlaying = isPlaying,
            onPause = onPause,
            onPlay = onPlay,
            onNext = onNext,
            onPrev = onPrev
        )
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
fun Controls(
    modifier: Modifier = Modifier,
    isBuffering: Boolean,
    isPlaying: Boolean,
    onPause: () -> Unit,
    onPlay: () -> Unit,
    onNext: () -> Unit,
    onPrev: () -> Unit
){
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(15.dp), verticalAlignment = Alignment.CenterVertically){
        IconButton(onClick = {}){
            Icon(
                painter = painterResource("restart.png"),
                contentDescription = "",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
        IconButton(onClick = onPrev,
            Modifier.size(35.dp).background(Color.DarkGray.copy(alpha = 0.5f), RoundedCornerShape(15.dp))
        ){
            Icon(
                painter = painterResource("prev.png"),
                contentDescription = "",
                modifier = Modifier.size(24.dp),
                tint = Color.White
            )
        }
        Box(Modifier.size(70.dp)){
            if (!isBuffering){
                Button(onClick = { if (isPlaying) onPause() else onPlay() },
                    modifier = Modifier.size(70.dp),
                    shape = RoundedCornerShape(25.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.Blue)
                ){
                    Icon(
                        painter = if (isPlaying) painterResource("pause.png") else painterResource("play.png"),
                        contentDescription = "",
                        modifier = Modifier.size(50.dp),
                        tint = Color.White
                    )
                }
            }else{
                CircularProgressIndicator(color = Color.Blue, modifier = Modifier.align(Alignment.Center))
            }
        }
        IconButton(onClick = onNext,
            Modifier.size(35.dp).background(Color.DarkGray.copy(alpha = 0.5f), RoundedCornerShape(15.dp))
        ){
            Icon(
                painter = painterResource("next.png"),
                contentDescription = "",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
        IconButton(onClick = {}){
            Icon(
                painter = painterResource("shuffle.png"),
                contentDescription = "",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun SeekBarAndDuration(
    modifier: Modifier = Modifier,
    isPlaying: Boolean,
    currentDuration: Long,
    totalDurationInSeconds: Long,
    totalDuration: String,
    onSeek: (Double) -> Unit
){
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically){
        Text(parseDurationToTime(currentDuration, "", false), fontSize = 14.sp, color = Color.White)
        Slider(
            parseDurationToFloat(currentDuration, totalDurationInSeconds, totalDuration, isPlaying),
            modifier = Modifier.weight(1f),
            onValueChange = {
               val seekTime = parseFloatToDuration(it, totalDurationInSeconds)
                onSeek(seekTime)
            },
            colors = SliderDefaults.colors(thumbColor = Color.White, inactiveTrackColor = Color.Gray, activeTrackColor = Color.Blue)
        )
        Text(parseDurationToTime(totalDurationInSeconds, totalDuration, isPlaying), fontSize = 14.sp, color = Color.White)
    }
}

private fun parseDurationToTime(totalDuration: Long, otherTotalDuration: String, isPlaying: Boolean): String {
    val seconds = if (isPlaying && totalDuration == 0L) convertTimeToSeconds(otherTotalDuration) else totalDuration
    val minutes = seconds / 60
    val remainingSeconds = seconds % 60
    val formattedSeconds = if (remainingSeconds < 10) "0$remainingSeconds" else remainingSeconds.toString()
    return "$minutes:$formattedSeconds"
}
private fun parseDurationToFloat(currentDuration: Long, max: Long, otherMax: String, isPlaying: Boolean): Float{
    val newMax =  if (isPlaying && max == 0L) convertTimeToSeconds(otherMax) else max
    val percentage =  (currentDuration.toFloat() / newMax.toFloat()).coerceIn(0f, 1f)
    return if (percentage.isNaN()) 0f else percentage
}
private fun parseFloatToDuration(value: Float, max: Long): Double{
    return (max * value).toDouble()
}
fun convertTimeToSeconds(time: String): Long {
    val parts = time.split(":")
    val minutes = parts[0].toLong()
    val seconds = parts[1].toLong()
    return (minutes * 60 + seconds) // total seconds
}