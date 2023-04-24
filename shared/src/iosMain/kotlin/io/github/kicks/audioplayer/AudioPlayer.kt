package io.github.kicks.audioplayer

import io.kicks.ObserverProtocol
import kotlinx.cinterop.*
import platform.AVFAudio.AVAudioSession
import platform.AVFAudio.AVAudioSessionCategoryPlayback
import platform.AVFAudio.setActive
import platform.AVFoundation.*
import platform.CoreMedia.CMTime
import platform.CoreMedia.CMTimeGetSeconds
import platform.CoreMedia.CMTimeMakeWithSeconds
import platform.Foundation.*
import platform.darwin.Float64
import platform.darwin.NSEC_PER_SEC
import platform.darwin.NSObject
import kotlin.time.DurationUnit
import kotlin.time.toDuration



actual class AudioPlayer actual constructor(private val playerState: PlayerState) {
    private val playerItems = mutableListOf<AVPlayerItem>()
    private val avAudioPlayer: AVPlayer = AVPlayer()

    private var currentItemIndex = -1
    private lateinit var timeObserver: Any

    private val observer: (CValue<CMTime>) -> Unit =  { time: CValue<CMTime> ->
        playerState.isBuffering = avAudioPlayer.currentItem?.isPlaybackLikelyToKeepUp() != true
        playerState.isPlaying = avAudioPlayer.timeControlStatus == AVPlayerTimeControlStatusPlaying
        val rawTime: Float64 = CMTimeGetSeconds(time)
        val parsedTime = rawTime.toDuration(DurationUnit.SECONDS).inWholeSeconds
        playerState.currentTime = parsedTime
        if (avAudioPlayer.currentItem != null){
            val cmTime = CMTimeGetSeconds(avAudioPlayer.currentItem!!.duration)
            playerState.duration = if (cmTime.isNaN()) 0 else cmTime.toDuration(DurationUnit.SECONDS).inWholeSeconds
        }
    }

    init {
        setUpAudioSession()
        playerState.isPlaying = avAudioPlayer.timeControlStatus == AVPlayerTimeControlStatusPlaying
    }

    actual fun play() {
        if (currentItemIndex == -1){
            play(0)
        }else{
            avAudioPlayer.play()
            playerState.isPlaying = true
        }
    }

    actual fun pause() {
        avAudioPlayer.pause()
        playerState.isPlaying = false
    }
    actual fun play(songIndex: Int) {
        playerState.isBuffering = true
        if (songIndex < playerItems.size){
            currentItemIndex = songIndex
            playWithIndex(currentItemIndex)
        }
    }

    actual fun seekTo(time: Double) {
        playerState.isBuffering = true
        val cmTime = CMTimeMakeWithSeconds(time, NSEC_PER_SEC.toInt())
        avAudioPlayer.currentItem?.seekToTime(time = cmTime, completionHandler = {
            playerState.isBuffering = false
        })
    }

    actual fun next() {
        playerState.canNext = (currentItemIndex + 1) < playerItems.size
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

    actual fun addSongsUrls(songsUrl: List<String>) {
        //TODO: report bad url
        val converted = songsUrl.map {
            NSURL.URLWithString(URLString = it)!!
        }
        playerItems.addAll(converted.map { AVPlayerItem(uRL = it) })
    }
    private fun setUpAudioSession() {
        try {
            val audioSession = AVAudioSession.sharedInstance()
            audioSession.setCategory(AVAudioSessionCategoryPlayback, null)
            audioSession.setActive(true, null)
        } catch (e: Exception) {
            println("Error setting up audio session: ${e.message}")
        }
    }

    private fun startTimeObserver(){
        val interval = CMTimeMakeWithSeconds(1.0, NSEC_PER_SEC.toInt())
        timeObserver = avAudioPlayer.addPeriodicTimeObserverForInterval(interval, null, observer)
        NSNotificationCenter.defaultCenter.addObserverForName(
            name = AVPlayerItemDidPlayToEndTimeNotification,
            `object` = avAudioPlayer.currentItem,
            queue = NSOperationQueue.mainQueue,
            usingBlock = {
                next()
            }
        )
    }

    private fun stop(){
        if (::timeObserver.isInitialized) avAudioPlayer.removeTimeObserver(timeObserver)
        avAudioPlayer.pause()
        avAudioPlayer.currentItem?.seekToTime(CMTimeMakeWithSeconds(0.0, NSEC_PER_SEC.toInt()))
    }

    private fun playWithIndex(currentItemIndex: Int){
        stop()
        startTimeObserver()
        playerState.isBuffering = true
        playerState.currentItemIndex = currentItemIndex
        val playItem = playerItems[currentItemIndex]
        avAudioPlayer.replaceCurrentItemWithPlayerItem(playItem)
        avAudioPlayer.play()
    }

    actual fun cleanUp(){
        stop()
    }


}
class AudioObserver: ObserverProtocol, NSObject() {
    override fun observeValueForKeyPath(
        keyPath: String?,
        ofObject: Any?,
        change: Map<Any?, *>?,
        context: COpaquePointer?
    ) {
        println("keyPath $keyPath")
        println("ofObject $ofObject")
        println("change $change")
        println("context $context")
    }

}

