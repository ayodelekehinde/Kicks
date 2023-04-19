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

    private var currentItemIndex = 0
    private val audioObserver = AudioObserver()

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

//    private val av = try {
//        AVAudioPlayer(contentsOfURL = url, error = null)
//    }catch (e: NullPointerException){
//        println("TRY: ${e.printStackTrace()}")
//        null
//    }

    init {
        setUpAudioSession()
        setUpPlayerState()
        playerState.isPlaying = avAudioPlayer.timeControlStatus == AVPlayerTimeControlStatusPlaying
    }

    actual fun play() {
        avAudioPlayer.play()
        playerState.isPlaying = true
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
        if (time > 0){
            playerState.isBuffering = true
            val cmTime = CMTimeMakeWithSeconds(time, NSEC_PER_SEC.toInt())
            avAudioPlayer.seekToTime(time = cmTime, completionHandler = {
                playerState.isBuffering = false
            })
        }
    }

    actual fun next() {
        playerState.canNext = (currentItemIndex + 1) < playerItems.size
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

    actual fun addSongsUrls(songsUrl: List<String>) {
        //TODO: report bad url
        val converted = songsUrl.map {
            NSURL.URLWithString(URLString = it)!!
        }
        playerItems.addAll(converted.map { AVPlayerItem(uRL = it) })
        //playWithIndex(currentItemIndex])
    }

    private fun isValidAudioUrl(url: String): Boolean {
        val regex = Regex("(http(s)?://)?([\\w-]+\\.)+[\\w-]+(/[\\w- ./?%&=]*)?")
        if (!regex.matches(url)) {
            return false
        }
        if (!url.endsWith(".mp3") || !url.endsWith(".wav") || !url.endsWith(".ogg")) {
            return false
        }
        return true
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

    private fun setUpPlayerState(){
        val interval = CMTimeMakeWithSeconds(1.0, NSEC_PER_SEC.toInt())
        avAudioPlayer.addPeriodicTimeObserverForInterval(interval, null, observer)

    }

    private fun stop(){
        avAudioPlayer.pause()
        avAudioPlayer.seekToTime(CMTimeMakeWithSeconds(0.0, NSEC_PER_SEC.toInt()))
    }

    private fun playWithIndex(currentItemIndex: Int){
        stop()
        playerState.currentItemIndex = currentItemIndex
        val playItem = playerItems[currentItemIndex]
        playItem.addObserver(audioObserver, forKeyPath = "status", options = NSKeyValueObservingOptionNew, context = null)
        avAudioPlayer.replaceCurrentItemWithPlayerItem(playItem)
        avAudioPlayer.play()
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