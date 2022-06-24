package vn.theanh.musiccc.views.fragments

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_playlists.*
import vn.theanh.musiccc.R
import vn.theanh.musiccc.adapters.PlaylistAdapter
import vn.theanh.musiccc.adapters.TrackAdapter
import vn.theanh.musiccc.data.DataLoader
import vn.theanh.musiccc.models.Playlist
import vn.theanh.musiccc.models.Track


class PlaylistsFragment : BaseFragment(), PlaylistTracksFragment.OnSaveListener {
    private var listPlaylists = mutableListOf<Playlist>()
    private var playlistAdapter: PlaylistAdapter? = null
    private lateinit var recyclerPlaylist: RecyclerView

    override fun onServiceConnected() {
        initData()
    }

    private fun initData() {
        listPlaylists = DataLoader(requireContext()).genPlaylist()
        if ((listPlaylists.isNotEmpty())) {
            playlistAdapter!!.updateData(listPlaylists)
            tv_empty_playlists.visibility = View.GONE
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        binSer()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_playlists, container, false)
        val manager = LinearLayoutManager(requireContext())
        recyclerPlaylist = rootView.findViewById(R.id.recycler_playlists)
        recyclerPlaylist.layoutManager = manager
        playlistAdapter = PlaylistAdapter(listPlaylists)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerPlaylist.adapter = playlistAdapter
        playlistAdapter!!.setOnClickListener(object : PlaylistAdapter.OnClickListener {
            override fun onPlaylistClick(position: Int) {
                if (position == 0) {
                    val listAdd = mutableListOf<Track>()
                    val listAllTracks = DataLoader(requireContext()).queryTracks()
                    val v = layoutInflater.inflate(R.layout.playlist_add_dialog, null)
                    val edtName: EditText = v.findViewById(R.id.playlist_new_edt_name)
                    val recyclerAddTrack: RecyclerView =
                        v.findViewById(R.id.recycler_playlist_new_add)
                    val manager = LinearLayoutManager(requireContext())
                    recyclerAddTrack.layoutManager = manager
                    val playlistNewAddAdapter = TrackAdapter(listAllTracks, true)
                    recyclerAddTrack.adapter = playlistNewAddAdapter
                    playlistNewAddAdapter.setOnClickListener(object : TrackAdapter.OnClickListener {
                        override fun onTrackClick(position: Int) {
                            Toast.makeText(
                                this@PlaylistsFragment.context,
                                "Track: ${listAllTracks[position].title}\n" +
                                        "Artist: ${listAllTracks[position].artist}\n" +
                                        "Album: ${listAllTracks[position].album}",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }

                        override fun onPlusMenuClick(position: Int) {
                            listAdd.add(listAllTracks[position])
                            listAdd.sortBy { it.title }
                            playlistNewAddAdapter.removeTrack(position)
                        }
                    })
                    val add = AlertDialog.Builder(this@PlaylistsFragment.activity)
                    add.setCancelable(true)
                        .setTitle("Create new playlist")
                        .setView(v)
                        .setPositiveButton("Add") { _, _ ->
                            if (listAdd.isNotEmpty() && edtName.text.isNotBlank()) {
                                val name = edtName.text.toString()
                                listPlaylists.add(Playlist(name, listAdd))
                                listPlaylists.sortBy { it.name }
                                playlistAdapter!!.notifyDataSetChanged()
                            } else {
                                Toast.makeText(
                                    this@PlaylistsFragment.context,
                                    "Can't create",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            }
                        }
                    val dialog = add.create()
                    dialog.show()
                } else {
                    val gson = Gson()
                    val gsonString = gson.toJson(listPlaylists[position].tracks)
                    val playlistTracksFrag = PlaylistTracksFragment()
                    playlistTracksFrag.setOnSaveListener(this@PlaylistsFragment)
                    val bundle = Bundle()
                    bundle.putString("playlist.bundle", gsonString)
                    bundle.putString("playlistName.bundle", listPlaylists[position].name)
                    bundle.putInt("playlistPos.bundle", position)
                    playlistTracksFrag.arguments = bundle
                    val playlistTracksTag = "playlistTracksFrag.tag"
                    mainActivity!!.supportFragmentManager.beginTransaction()
                        .replace(R.id.full_frame, playlistTracksFrag, playlistTracksTag)
                        .addToBackStack(null)
                        .commit()
                    mainActivity!!.hideView()
                }
            }

            override fun onPlayImgClick(position: Int) {
                iPlayerHolder!!.updateTracks(listPlaylists[position].tracks, 0)
                iPlayerHolder!!.play()
                mainActivity!!.updateFooter()
            }
        })
    }

    override fun onSaveClicked(list: MutableList<Track>, pos: Int) {
        if (list.isNotEmpty()) {
            playlistAdapter!!.updateNewList(pos, list)
        }
    }
}