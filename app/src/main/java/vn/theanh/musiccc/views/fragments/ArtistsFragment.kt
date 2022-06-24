package vn.theanh.musiccc.views.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_artists.*
import vn.theanh.musiccc.R
import vn.theanh.musiccc.adapters.ArtistAdapter
import vn.theanh.musiccc.data.DataLoader
import vn.theanh.musiccc.models.Artist

class ArtistsFragment : BaseFragment() {
    private var listArtists = mutableListOf<Artist>()
    private var artistAdapter: ArtistAdapter? = null
    private lateinit var recyclerArtist: RecyclerView

    override fun onServiceConnected() {
        initData()
    }

    private fun initData() {
        listArtists = DataLoader(requireContext()).getArtists()
        if (listArtists.isNotEmpty()) {
            artistAdapter!!.updateData(listArtists)
            tv_empty_artists.visibility = View.GONE
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        binSer()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_artists, container, false)
        val manager = LinearLayoutManager(requireContext())
        recyclerArtist = rootView.findViewById(R.id.recycler_artists)
        recyclerArtist.layoutManager = manager
        artistAdapter = ArtistAdapter(listArtists)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerArtist.adapter = artistAdapter
        artistAdapter!!.setOnClickListener(object : ArtistAdapter.OnClickListener {
            override fun onPlayIconClick(position: Int) {
                iPlayerHolder!!.updateTracks(listArtists[position].tracks, 0)
                iPlayerHolder!!.play()
                mainActivity!!.updateFooter()
            }

            override fun onItemTrackClick(position: Int, parentPosition: Int) {
                iPlayerHolder!!.updateTracks(listArtists[parentPosition].tracks, position)
                iPlayerHolder!!.play()
                mainActivity!!.updateFooter()
            }

            override fun onItemPlusMenuClick(position: Int, parentPosition: Int) {
                Toast.makeText(
                    this@ArtistsFragment.context,
                    "Item $position of Parent $parentPosition",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        })
    }
}
