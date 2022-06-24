package vn.theanh.musiccc.views.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.fragment_album_tracks.*
import vn.theanh.musiccc.R
import vn.theanh.musiccc.adapters.TrackAdapter
import vn.theanh.musiccc.models.Track
import java.lang.reflect.Type

class AlbumTracksFragment : BaseFragment() {
    private var listTracksString: String? = null
    private var listAlbumTracks = mutableListOf<Track>()
    private var albumTracksAdapter: TrackAdapter? = null
    private lateinit var recyclerAlbumTracks: RecyclerView

    override fun onServiceConnected() {

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        binSer()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_album_tracks, container, false)
        listTracksString = arguments?.getString("album.bundle")
        stringToList()
        val manager = LinearLayoutManager(requireContext())
        recyclerAlbumTracks = rootView.findViewById(R.id.recycler_album_tracks)
        recyclerAlbumTracks.layoutManager = manager
        albumTracksAdapter = TrackAdapter(listAlbumTracks, true)
        return rootView
    }

    private fun stringToList() {
        if (listTracksString != null) {
            val gson = Gson()
            val type: Type = object : TypeToken<MutableList<Track>>() {}.type
            val tracks = gson.fromJson<MutableList<Track>>(listTracksString, type)
            if (tracks != null && tracks.isNotEmpty()) {
                listAlbumTracks = tracks
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        album_tracks_txt_name.text = listAlbumTracks[0].album
        recyclerAlbumTracks.adapter = albumTracksAdapter
        albumTracksAdapter!!.setOnClickListener(object : TrackAdapter.OnClickListener {
            override fun onTrackClick(position: Int) {
                iPlayerHolder!!.updateTracks(listAlbumTracks, position)
                iPlayerHolder!!.play()
                mainActivity!!.updateFooter()
            }

            override fun onPlusMenuClick(position: Int) {
                Toast.makeText(
                    this@AlbumTracksFragment.context,
                    "Track position: $position",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        })
        album_img_play.setOnClickListener {
            iPlayerHolder!!.updateTracks(listAlbumTracks, 0)
            iPlayerHolder!!.play()
            mainActivity!!.updateFooter()
        }
    }
}
