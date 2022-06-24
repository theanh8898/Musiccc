package vn.theanh.musiccc.utils

import android.content.Context
import androidx.preference.PreferenceManager

class MusicPreference(context: Context) {
    companion object {
        private const val CURRENT_TRACK_POS = "current.track.pos"
        private const val CURRENT_TRACK_BOOKMARK = "current.track.bookmark"
    }

    private val preferences = PreferenceManager.getDefaultSharedPreferences(context)

    var currentTrackPos = preferences.getInt(CURRENT_TRACK_POS, 0)
        set(value) = preferences.edit().putInt(CURRENT_TRACK_POS, value).apply()
    var currentTrackBookmark = preferences.getInt(CURRENT_TRACK_BOOKMARK, 0)
        set(value) = preferences.edit().putInt(CURRENT_TRACK_BOOKMARK, value).apply()
}
