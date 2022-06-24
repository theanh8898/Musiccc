package vn.theanh.musiccc.models

import java.io.Serializable

data class Album(
    var id: Long,
    var name: String,
    var tracks: MutableList<Track>
) : Serializable