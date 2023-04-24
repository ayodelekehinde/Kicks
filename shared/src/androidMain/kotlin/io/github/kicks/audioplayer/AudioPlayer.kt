package io.github.kicks.audioplayer


import android.os.Handler
import android.os.Looper
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import io.github.kicks.KicksApp


actual class AudioPlayer actual constructor(private val playerState: PlayerState): Runnable  {

    private val handler = Handler(Looper.getMainLooper())

    private val mediaPlayer = ExoPlayer.Builder(KicksApp.appContext).build()
    private val mediaItems = mutableListOf<MediaItem>()
    private var currentItemIndex = -1
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
        handler.removeCallbacks(this)
    }
    private fun scheduleUpdate(){
        stopUpdate()
        handler.postDelayed(this, 100)
    }
    actual fun play() {
        if (currentItemIndex == -1){
            play(0)
        }else{
            mediaPlayer.play()
        }
    }

    actual fun pause() {
        mediaPlayer.pause()
    }

    actual fun addSongsUrls(songsUrl: List<String>) {
        mediaItems += songsUrl.map { MediaItem.fromUri(it) }
        mediaPlayer.addListener(listener)
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
        when{
            playerState.currentTime > 3 ->{
                seekTo(0.0)
            }
            else ->{
                playerState.canPrev = (currentItemIndex - 1) >= 0
                if (playerState.canPrev) {
                    currentItemIndex -= 1
                    playWithIndex(currentItemIndex)
                }
            }
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

    actual fun cleanUp(){
        mediaPlayer.release()
        mediaPlayer.removeListener(listener)
    }

}