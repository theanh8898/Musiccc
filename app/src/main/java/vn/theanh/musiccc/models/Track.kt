package vn.theanh.musiccc.models

import java.io.Serializable

data class Track(
    var id: Long,
    var title: String,
    var artist: String,
    var artistID: Long,
    var album: String,
    var albumID: Long,
    var albumArt: String,
    var duration: Long,
    var data: String
) : Serializable

