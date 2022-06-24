package vn.theanh.musiccc.data

import android.annotation.SuppressLint
import android.database.Cursor
import android.database.CursorWrapper
import android.provider.MediaStore
import vn.theanh.musiccc.models.Track


class TrackCursorWrapper(cursor: Cursor) : CursorWrapper(cursor) {
    @SuppressLint("InlinedApi")
    fun getTrack(): Track {
        val id = getLong(getColumnIndex(MediaStore.Audio.Media._ID))
        val title = getString(getColumnIndex(MediaStore.Audio.Media.TITLE))
        val artist = getString(getColumnIndex(MediaStore.Audio.Media.ARTIST))
        val artistID = getLong(getColumnIndex(MediaStore.Audio.Media.ARTIST_ID))
        val album = getString(getColumnIndex(MediaStore.Audio.Media.ALBUM))
        val albumID = getLong(getColumnIndex(MediaStore.Audio.Media.ALBUM_ID))
        val duration = getLong(getColumnIndex(MediaStore.Audio.Media.DURATION))
        @Suppress("DEPRECATION") val data = getString(getColumnIndex(MediaStore.Audio.Media.DATA))
        return Track(id, title, artist, artistID, album, albumID, "", duration, data)
    }
}