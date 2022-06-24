package vn.theanh.musiccc.views.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.fragment_playlist_tracks.*
import vn.theanh.musiccc.R
import vn.theanh.musiccc.adapters.TrackAdapter
import vn.theanh.musiccc.data.DataLoader
import vn.theanh.musiccc.models.Track
import java.lang.reflect.Type

class PlaylistTracksFragment : BaseFragment() {
    private var listTracksString: String? = null
    private var listPlaylistTracks = mutableListOf<Track>()
    private var listAllTracks = mutableListOf<Track>()
    private var playlistTracksAdapter: TrackAdapter? = null
    private var playlistAddAdapter: TrackAdapter? = null
    private lateinit var recyclerPlaylistTracks: RecyclerView
    private lateinit var recyclerPlaylistAdd: RecyclerView
    private lateinit var layoutTracks: LinearLayout
    private lateinit var layoutAdd: LinearLayout
    private lateinit var playlistFragment: PlaylistsFragment
    private lateinit var playlistName: TextView
    private var playlistPos: Int = 0

    override fun onServiceConnected() {

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        binSer()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_playlist_tracks, container, false)
        layoutTracks = rootView.findViewById(R.id.playlist_layout_tracks)
        layoutAdd = rootView.findViewById(R.id.playlist_layout_add)
        layoutTracks.visibility = View.VISIBLE
        layoutAdd.visibility = View.GONE
        playlistName = rootView.findViewById(R.id.playlist_tracks_txt_name)
        playlistName.text = arguments?.getString("playlistName.bundle")!!
        playlistPos = arguments?.getInt("playlistPos.bundle")!!
        listTracksString = arguments?.getString("playlist.bundle")
        stringToList()
        listAllTracks = DataLoader(requireContext()).queryTracks()
        val toRemove = mutableListOf<Track>()
        for (track in listAllTracks) {
            for (existTrack in listPlaylistTracks) {
                if (track == existTrack) {
                    toRemove.add(track)
                    break
                }
            }
        }
        listAllTracks.removeAll(toRemove)
        val managerTracks = LinearLayoutManager(requireContext())
        recyclerPlaylistTracks = rootView.findViewById(R.id.recycler_playlist_tracks)
        recyclerPlaylistTracks.layoutManager = managerTracks
        playlistTracksAdapter = TrackAdapter(listPlaylistTracks, false)

        val managerAdd = LinearLayoutManager(requireContext())
        recyclerPlaylistAdd = rootView.findViewById(R.id.recycler_playlist_add)
        recyclerPlaylistAdd.layoutManager = managerAdd
        playlistAddAdapter = TrackAdapter(listAllTracks, true)
        return rootView
    }

    private fun stringToList() {
        if (listTracksString != null) {
            val gson = Gson()
            val type: Type = object : TypeToken<MutableList<Track>>() {}.type
            val tracks = gson.fromJson<MutableList<Track>>(listTracksString, type)
            if (tracks != null && tracks.isNotEmpty()) {
                listPlaylistTracks = tracks
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        playlistFragment = PlaylistsFragment()
        recyclerPlaylistTracks.adapter = playlistTracksAdapter
        playlistTracksAdapter!!.setOnClickListener(object : TrackAdapter.OnClickListener {
            override fun onTrackClick(position: Int) {
                iPlayerHolder!!.updateTracks(listPlaylistTracks, position)
                iPlayerHolder!!.play()
                mainActivity!!.updateFooter()
            }

            override fun onPlusMenuClick(position: Int) {

            }
        })
        playlist_tracks_img_play.setOnClickListener {
            iPlayerHolder!!.updateTracks(listPlaylistTracks, 0)
            iPlayerHolder!!.play()
            mainActivity!!.updateFooter()
        }
        playlist_img_add.setOnClickListener {
            layoutTracks.visibility = View.GONE
            layoutAdd.visibility = View.VISIBLE
        }
        recyclerPlaylistAdd.adapter = playlistAddAdapter
        playlistAddAdapter!!.setOnClickListener(object : TrackAdapter.OnClickListener {
            override fun onTrackClick(position: Int) {
                Toast.makeText(
                    this@PlaylistTracksFragment.context,
                    "Track: ${listAllTracks[position].title}\n" +
                            "Artist: ${listAllTracks[position].artist}\n" +
                            "Album: ${listAllTracks[position].album}",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }

            override fun onPlusMenuClick(position: Int) {
                playlistTracksAdapter!!.addTrack(listAllTracks[position])
                playlistAddAdapter!!.removeTrack(position)
            }

        })
        playlist_txt_save.setOnClickListener {
            layoutTracks.visibility = View.VISIBLE
            layoutAdd.visibility = View.GONE
//            val gson = Gson()
//            val gsonString = gson.toJson(listPlaylistTracks)
            playlistCallback!!.onSaveClicked(listPlaylistTracks, playlistPos)
        }
    }

    private var playlistCallback: OnSaveListener? = null

    fun setOnSaveListener(callback: OnSaveListener) {
        this.playlistCallback = callback
    }

    interface OnSaveListener {
        fun onSaveClicked(list: MutableList<Track>, pos: Int)
    }
}