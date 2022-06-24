package vn.theanh.musiccc.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.AUDIO_SERVICE
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Build
import android.os.PowerManager
import androidx.annotation.RequiresApi
import vn.theanh.musiccc.models.Track
import vn.theanh.musiccc.utils.MusicPreference
import vn.theanh.musiccc.utils.PlaybackListener
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

class PlayerHolder internal constructor(private val mMusicService: MusicService?) :
    MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
    MediaPlayer.OnCompletionListener, AudioManager.OnAudioFocusChangeListener,
    IPlayerHolder {

    inner class NotiReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (action != null) {
                when (action) {
                    NotificationMusiccc.PREV_ACTION -> playPrev()
                    NotificationMusiccc.RESUME_PAUSE_ACTION -> resumePause()
                    NotificationMusiccc.NEXT_ACTION -> playNext()
                    AudioManager.ACTION_AUDIO_BECOMING_NOISY -> pause()
                }
            }
        }
    }

    private var context = mMusicService!!.applicationContext
    private var mediaPlayer: MediaPlayer? = null
    private var playbackListener: PlaybackListener? = null
    private var audioManager: AudioManager
    private var notificationMusiccc: NotificationMusiccc? = null
    private var notiReceiver: NotiReceiver? = null
    private var scheduledExecutorService: ScheduledExecutorService? = null
    private var seekBarTask: Runnable? = null

    init {
        audioManager = context.getSystemService(AUDIO_SERVICE) as AudioManager
    }

    private val musicPreference = MusicPreference(context)
    private var tracks: MutableList<Track>? = null
    private var currentTrackPos = 0
    private var resumePosition = 0
    private var repeatState = 0
    private var isShuffle = false

    @PlaybackListener.State
    private var state: Int = PlaybackListener.State.INVALID

    fun registerNotiReceiver() {
        notiReceiver = NotiReceiver()
        val intentFilter = IntentFilter()
        intentFilter.addAction(NotificationMusiccc.PREV_ACTION)
        intentFilter.addAction(NotificationMusiccc.RESUME_PAUSE_ACTION)
        intentFilter.addAction(NotificationMusiccc.NEXT_ACTION)
        intentFilter.addAction(AudioManager.ACTION_AUDIO_BECOMING_NOISY)
        mMusicService!!.registerReceiver(notiReceiver, intentFilter)
    }

    private fun unregisterNotiReceiver() {
        if (mMusicService != null && notiReceiver != null) {
            mMusicService.unregisterReceiver(notiReceiver)
        }
    }

    override fun onPrepared(mp: MediaPlayer) {
        mp.start()
        startSeekBarThread()
        setPlaybackState(PlaybackListener.State.PLAYING)
    }

    private fun startSeekBarThread() {
        if (scheduledExecutorService == null) {
            scheduledExecutorService = Executors.newSingleThreadScheduledExecutor()
        }
        if (seekBarTask == null) {
            seekBarTask = Runnable {
                if (isPlayerExist() && mediaPlayer!!.isPlaying && playbackListener != null) {
                    playbackListener!!.onPositionChanged(getCurrentPosition()!!)
                }
            }
        }
        scheduledExecutorService!!.scheduleAtFixedRate(
            seekBarTask, 0, 1000, TimeUnit.MILLISECONDS
        )
    }

    override fun onError(mp: MediaPlayer, what: Int, extra: Int): Boolean {
        mp.reset()
        return false
    }

    override fun onCompletion(mp: MediaPlayer) {
        if (playbackListener != null) {
            playbackListener!!.onStateChanged(PlaybackListener.State.COMPLETED)
            playbackListener!!.onPlaybackCompleted()
        }
        when (repeatState) {
            0 -> if (currentTrackPos == tracks!!.size - 1) {
                currentTrackPos = 0
                resumePosition = 0
                play()
                pause()
                setPlaybackState(PlaybackListener.State.PAUSED)
            }
            1 -> {
                play()
            }
            else -> playNext()
        }
    }

    private fun setPlaybackState(@PlaybackListener.State state: Int) {
        this.state = state
        if (playbackListener != null) {
            playbackListener!!.onStateChanged(state)
        }
    }

    override fun isPlayerExist(): Boolean {
        return mediaPlayer != null
    }

    override fun isPlaying(): Boolean = isPlayerExist() && mediaPlayer!!.isPlaying

    override fun toForeground() {
        mMusicService!!.startForeground(
            NotificationMusiccc.NOTIFICATION_ID,
            notificationMusiccc!!.buildNotification()
        )
    }

    override fun initPlayer() {
        if (mediaPlayer != null) mediaPlayer!!.reset()
        else {
            mediaPlayer = MediaPlayer()
            mediaPlayer!!.setWakeMode(context, PowerManager.PARTIAL_WAKE_LOCK)
            @Suppress("DEPRECATION")
            mediaPlayer!!.setAudioStreamType(AudioManager.STREAM_MUSIC)
            mediaPlayer!!.setOnPreparedListener(this)
            mediaPlayer!!.setOnErrorListener(this)
            mediaPlayer!!.setOnCompletionListener(this)
            notificationMusiccc = mMusicService!!.notificationMusiccc
        }
    }

    override fun updateTracks(tracks: MutableList<Track>, currentTrackPos: Int) {
        this.tracks = tracks
        this.currentTrackPos = currentTrackPos
    }

    override fun setCurrentTrackByPos(position: Int) {
        currentTrackPos = position
    }

    override fun setRandomTrackPos() {
        val r = Random()
        currentTrackPos = if (tracks.isNullOrEmpty()) {
            0
        } else r.nextInt(tracks!!.size)
    }

    override fun getCurrentTrack(): Track? {
        musicPreference.currentTrackPos = currentTrackPos
        musicPreference.currentTrackBookmark =
            if (getCurrentPosition() != null) getCurrentPosition()!! else 0
        return tracks?.get(currentTrackPos)
    }

    override fun getResumePosition(): Int {
        return resumePosition
    }

    override fun getCurrentPosition(): Int? {
        return mediaPlayer?.currentPosition
    }

    override fun play() {
        if (currentTrackPos > -1) {
            initPlayer()
            takeAudioFocus()
            mediaPlayer!!.reset()
            mediaPlayer!!.setDataSource(tracks!![currentTrackPos].data)
            mediaPlayer!!.prepareAsync()
        }
        toForeground()
    }

    override fun pause() {
        mediaPlayer!!.pause()
        resumePosition = mediaPlayer!!.currentPosition
        setPlaybackState(PlaybackListener.State.PAUSED)
        toForeground()
    }

    override fun resume() {
        takeAudioFocus()
        mediaPlayer!!.seekTo(resumePosition)
        mediaPlayer!!.start()
        setPlaybackState(PlaybackListener.State.RESUMED)
        toForeground()
    }

    override fun resumePause() {
        if (isPlaying()) pause()
        else resume()
    }

    override fun seek(position: Int) {
        mediaPlayer!!.seekTo(position)
    }

    override fun playPrev() {
        when {
            isShuffle -> setRandomTrackPos()
            currentTrackPos == 0 -> currentTrackPos = tracks!!.size - 1
            else -> currentTrackPos--
        }
        play()
    }

    override fun playNext() {
        when {
            isShuffle -> setRandomTrackPos()
            currentTrackPos == tracks!!.size - 1 -> currentTrackPos = 0
            else -> currentTrackPos++
        }
        play()
    }

    override fun changeShuffleState() {
        isShuffle = !isShuffle
    }

    override fun getShuffleState(): Boolean {
        return isShuffle
    }

    override fun changeRepeatState() {
        repeatState = when (repeatState) {
            0 -> 1
            1 -> 2
            else -> 0
        }
    }

    override fun getRepeatState(): Int {
        return repeatState
    }

    override fun onAudioFocusChange(focusChange: Int) {
        when (focusChange) {
            AudioManager.AUDIOFOCUS_GAIN -> {
                resume()
                mediaPlayer!!.setVolume(1.0f, 1.0f)
            }
            AudioManager.AUDIOFOCUS_LOSS -> mediaPlayer!!.pause()
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> mediaPlayer!!.pause()
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> mediaPlayer!!.setVolume(0.1f, 0.1f)
        }
    }

    private fun takeAudioFocus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) takeAudioFocusAndroidO()
        else takeAudioFocusAndroidPreO()
    }

    private fun takeAudioFocusAndroidPreO() {
        @Suppress("DEPRECATION")
        audioManager.requestAudioFocus(
            this,
            AudioManager.STREAM_MUSIC,
            AudioManager.AUDIOFOCUS_GAIN
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun takeAudioFocusAndroidO() {
        audioManager.requestAudioFocus(getAudioFocusRequestAndroidO())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getAudioFocusRequestAndroidO(): AudioFocusRequest {
        val playbackAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
            .build()
        return AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
            .setAudioAttributes(playbackAttributes)
            .setAcceptsDelayedFocusGain(true)
            .setOnAudioFocusChangeListener(this)
            .build()
    }

    private fun leaveAudioFocus() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) audioManager.abandonAudioFocusRequest(
            getAudioFocusRequestAndroidO()
        )
        else audioManager.abandonAudioFocus(this)
    }

    override fun setPlaybackListener(playbackListener: PlaybackListener) {
        this.playbackListener = playbackListener
    }

    override fun getState(): Int {
        return state
    }

    override fun stop() {
        unregisterNotiReceiver()
        mediaPlayer!!.stop()
        mediaPlayer!!.release()
        mediaPlayer = null
        leaveAudioFocus()
    }
}