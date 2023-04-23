package io.github.kicks.audioplayer

import android.content.Context
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.Timeline
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import io.github.kicks.KicksApp
import kotlinx.coroutines.*
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit
import kotlin.time.Duration.Companion.seconds

actual class AudioPlayer actual constructor(private val playerState: PlayerState): java.lang.Runnable  {

    private val handler = Handler(Looper.getMainLooper())

    private val executorService = Executors.newSingleThreadScheduledExecutor()
    private lateinit var schedule: ScheduledFuture<*>

    private val mediaPlayer = ExoPlayer.Builder(KicksApp.appContext).build()
    private val mediaItems = mutableListOf<MediaItem>()
    private var currentItemIndex = 0
    private val listener = object: Player.Listener{

        override fun onPlaybackStateChanged(playbackState: Int) {
            when(playbackState){
                Player.STATE_IDLE ->{
                }
                Player.STATE_BUFFERING -> {
                   playerState.isBuffering = true
                }
                Player.STATE_ENDED -> {
                    if (playerState.isPlaying){
                        next()
                    }
                }
                Player.STATE_READY -> {
                    playerState.isBuffering = false
                    playerState.duration =  mediaPlayer.duration / 1000
                }
            }
        }
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            playerState.isPlaying = isPlaying
            if (isPlaying){
                scheduleUpdate()
            }else{
                stopUpdate()
            }
        }
    }
    private fun stopUpdate(){
//        if (::schedule.isInitialized) {
//            schedule.cancel(false)
//        }
        handler.removeCallbacks(this)
    }
    private fun scheduleUpdate(){
        stopUpdate()
//        if (!executorService.isShutdown) {
//            schedule = executorService.scheduleAtFixedRate(
//                this,
//                100,
//                1000,
//                TimeUnit.MILLISECONDS
//            )
//        }
        handler.postDelayed(this, 100)
    }
    actual fun play() {
        mediaPlayer.play()
    }

    actual fun pause() {
        mediaPlayer.pause()
    }

    actual fun addSongsUrls(songsUrl: List<String>) {
        // Build the media item.
        mediaItems += songsUrl.map { MediaItem.fromUri(it) }
        mediaPlayer.addListener(listener)
        //mediaPlayer.setMediaItems(mediaItems)
        mediaPlayer.prepare()


    }

    actual fun next() {
        playerState.canNext = (currentItemIndex + 1) < mediaItems.size
        if (playerState.canNext){
            currentItemIndex += 1
            playWithIndex(currentItemIndex)
        }
    }

    actual fun prev() {
        playerState.canPrev = (currentItemIndex - 1) >= 0
        if (playerState.canPrev) {
            currentItemIndex -= 1
            playWithIndex(currentItemIndex)
        }
    }

    actual fun play(songIndex: Int) {
        if (songIndex < mediaItems.size){
            currentItemIndex = songIndex
            playWithIndex(currentItemIndex)
        }
    }

    actual fun seekTo(time: Double) {
        val seekTime = time * 1000
        println("Seeking to: ${seekTime.toLong()}")
        mediaPlayer.seekTo(seekTime.toLong())
    }

    private fun playWithIndex(index: Int){
        playerState.currentItemIndex = index
        val playItem = mediaItems[index]
        mediaPlayer.setMediaItem(playItem)
        mediaPlayer.play()
    }

    override fun run() {
        playerState.currentTime = mediaPlayer.currentPosition / 1000
        handler.postDelayed(this, 1000)
    }

}