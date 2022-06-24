package vn.theanh.musiccc.services

import vn.theanh.musiccc.models.Track
import vn.theanh.musiccc.utils.PlaybackListener

interface IPlayerHolder {
    fun isPlayerExist(): Boolean
    fun isPlaying(): Boolean
    fun toForeground()

    fun initPlayer()
    fun updateTracks(tracks: MutableList<Track>, currentTrackPos: Int)
    fun setCurrentTrackByPos(position: Int)
    fun setRandomTrackPos()
    fun getCurrentTrack(): Track?
    fun getResumePosition(): Int
    fun getCurrentPosition(): Int?

    fun play()
    fun pause()
    fun resume()
    fun resumePause()
    fun seek(position: Int)
    fun playPrev()
    fun playNext()

    fun changeShuffleState()
    fun getShuffleState(): Boolean
    fun changeRepeatState()
    fun getRepeatState(): Int

    fun setPlaybackListener(playbackListener: PlaybackListener)

    @PlaybackListener.State
    fun getState(): Int
    fun stop()
}