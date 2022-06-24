package vn.theanh.musiccc.views.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_tracks.*
import vn.theanh.musiccc.R
import vn.theanh.musiccc.adapters.TrackAdapter
import vn.theanh.musiccc.data.DataLoader
import vn.theanh.musiccc.models.Track

class TracksFragment : BaseFragment() {
    private var listTracks = mutableListOf<Track>()
    private var trackAdapter: TrackAdapter? = null
    private lateinit var recyclerTracks: RecyclerView

    override fun onServiceConnected() {
        initData()
    }

    private fun initData() {
        listTracks = DataLoader(requireContext()).queryTracks()
        if (listTracks.isNotEmpty()) {
            iPlayerHolder!!.updateTracks(listTracks, 0)
            trackAdapter!!.updateData(listTracks)
            tv_empty_tracks.visibility = View.GONE
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        binSer()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_tracks, container, false)
        val manager = LinearLayoutManager(requireContext())
        recyclerTracks = rootView.findViewById(R.id.recycler_tracks)
        recyclerTracks.layoutManager = manager
        trackAdapter = TrackAdapter(listTracks, true)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerTracks.adapter = trackAdapter
        trackAdapter!!.setOnClickListener(object : TrackAdapter.OnClickListener {
            override fun onTrackClick(position: Int) {
                iPlayerHolder!!.setCurrentTrackByPos(position)
                iPlayerHolder!!.play()
                mainActivity!!.updateFooter()
            }

            override fun onPlusMenuClick(position: Int) {
                Toast.makeText(
                    this@TracksFragment.context,
                    "OK$position",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        })
    }

    override fun onResume() {
        super.onResume()
        if (!listTracks.isNullOrEmpty()) {
            tv_empty_tracks.visibility = View.GONE
        }
    }
}