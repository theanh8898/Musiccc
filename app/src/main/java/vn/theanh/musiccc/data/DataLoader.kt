package vn.theanh.musiccc.data

import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import vn.theanh.musiccc.models.Album
import vn.theanh.musiccc.models.Artist
import vn.theanh.musiccc.models.Playlist
import vn.theanh.musiccc.models.Track

class DataLoader(val context: Context) {
    fun queryTracks(): MutableList<Track> {
        val tracks = mutableListOf<Track>()
        val cursor: TrackCursorWrapper? = queryTrack()
        if (cursor != null) {
            cursor.moveToFirst()
            while (!cursor.isAfterLast) {
                val track = cursor.getTrack()
                val sArtworkUri = Uri.parse("content://media/external/audio/albumart")
                track.albumArt = ContentUris.withAppendedId(sArtworkUri, track.albumID).toString()
                tracks.add(track)
                cursor.moveToNext()
            }
            cursor.close()
        }
        return tracks
    }

    fun getArtists(): MutableList<Artist> {
        val artists = mutableListOf<Artist>()
        val tracks = queryTracks()
        if (tracks.isNotEmpty()) {
            for (track in tracks) {
                var exist = false
                for (artist in artists) {
                    if (artist.tracks.isNotEmpty() && artist.tracks[0].artistID == track.artistID) {
                        artist.tracks.add(track)
                        exist = true
                        break
                    }
                }
                if (exist) {
                    continue
                } else {
                    val artist = Artist(track.artistID, track.artist, mutableListOf(track))
                    artists.add(artist)
                }
            }
        }
        if (artists.size > 1) {
            artists.sortWith(Comparator { obj1, obj2 ->
                obj1.name.compareTo(
                    obj2.name,
                    ignoreCase = true
                )
            })
        }
        return artists
    }

    fun getAlbums(): MutableList<Album> {
        val albums = mutableListOf<Album>()
        val tracks = queryTracks()
        if (tracks.isNotEmpty()) {
            for (track in tracks) {
                var exist = false
                for (album in albums) {
                    if (album.tracks.isNotEmpty() && album.tracks[0].albumID == track.albumID) {
                        album.tracks.add(track)
                        exist = true
                        break
                    }
                }
                if (exist) {
                    continue
                } else {
                    val album = Album(track.albumID, track.album, mutableListOf(track))
                    albums.add(album)
                }
            }
        }
        if (albums.size > 1) {
            albums.sortWith(Comparator { obj1, obj2 ->
                obj1.name.compareTo(
                    obj2.name,
                    ignoreCase = true
                )
            })
        }
        return albums
    }

    fun genPlaylist(): MutableList<Playlist> {
        val playlist = mutableListOf<Playlist>()
        val tracks = queryTracks()
        val playlistTracks = mutableListOf<Track>()
        playlistTracks.add(tracks[1])
        playlistTracks.add(tracks[2])
        playlistTracks.add(tracks[5])
        playlistTracks.add(tracks[6])
        playlistTracks.add(tracks[7])
        val playlist0 = Playlist("Create a new playlist", mutableListOf())
        val playlist1 = Playlist("Playlist A", playlistTracks)
        playlist.add(playlist0)
        playlist.add(playlist1)
        return playlist
    }

    @Suppress("DEPRECATION")
    @SuppressLint("InlinedApi", "Recycle")
    private fun queryTrack(): TrackCursorWrapper? {
        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Audio.AudioColumns._ID,
            MediaStore.Audio.AudioColumns.TITLE,
            MediaStore.Audio.AudioColumns.ARTIST,
            MediaStore.Audio.AudioColumns.ARTIST_ID,
            MediaStore.Audio.AudioColumns.ALBUM,
            MediaStore.Audio.AudioColumns.ALBUM_ID,
            MediaStore.Audio.AudioColumns.DURATION,
            MediaStore.Audio.AudioColumns.DATA
        )
        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"
        val sortOrder = "${MediaStore.Audio.AudioColumns.TITLE} COLLATE NOCASE ASC"
        val cursor = context.contentResolver.query(uri, projection, selection, null, sortOrder)
        return TrackCursorWrapper(cursor!!)
    }

}