package vn.theanh.musiccc.views.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_albums.*
import vn.theanh.musiccc.R
import vn.theanh.musiccc.adapters.AlbumAdapter
import vn.theanh.musiccc.data.DataLoader
import vn.theanh.musiccc.models.Album

class AlbumsFragment : BaseFragment() {
    private var listAlbums = mutableListOf<Album>()
    private var albumAdapter: AlbumAdapter? = null
    private lateinit var recyclerAlbum: RecyclerView

    override fun onServiceConnected() {
        initData()
    }

    private fun initData() {
        listAlbums = DataLoader(requireContext()).getAlbums()
        if (listAlbums.isNotEmpty()) {
            albumAdapter!!.updateData(listAlbums)
            tv_empty_albums.visibility = View.GONE
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        binSer()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_albums, container, false)
        val manager = GridLayoutManager(requireContext(), 2)
        recyclerAlbum = rootView.findViewById(R.id.recycler_albums)
        recyclerAlbum.layoutManager = manager
        albumAdapter = AlbumAdapter(listAlbums)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerAlbum.adapter = albumAdapter
        albumAdapter!!.setOnClickListener(object : AlbumAdapter.OnClickListener {
            override fun onAlbumClick(position: Int) {
                val gson = Gson()
                val gsonString = gson.toJson(listAlbums[position].tracks)
                val albumTracksFrag = AlbumTracksFragment()
                val bundle = Bundle()
                bundle.putString("album.bundle", gsonString)
                albumTracksFrag.arguments = bundle
                val albumTracksTag = "albumTracksFrag.tag"
                mainActivity!!.supportFragmentManager.beginTransaction()
                    .replace(R.id.full_frame, albumTracksFrag, albumTracksTag)
                    .addToBackStack(null)
                    .commit()
                mainActivity!!.hideView()
            }
        })
    }
}
