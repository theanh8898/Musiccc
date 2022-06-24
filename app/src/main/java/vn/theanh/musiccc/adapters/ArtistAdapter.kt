package vn.theanh.musiccc.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_artist.view.*
import vn.theanh.musiccc.R
import vn.theanh.musiccc.models.Artist

class ArtistAdapter(listArtists: MutableList<Artist>) :
    RecyclerView.Adapter<ArtistAdapter.ArtistHolder>() {
    private var artists: MutableList<Artist>? = null
    private var mListener: OnClickListener? = null
    var viewPool = RecyclerView.RecycledViewPool()
    fun setOnClickListener(listener: OnClickListener) {
        mListener = listener
    }

    init {
        artists = listArtists
        notifyDataSetChanged()
    }

    inner class ArtistHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindData(artist: Artist, parentPosition: Int) {
            itemView.artists_tv_name.text = artist.name
            val artistTrackAdapter = TrackAdapter(artist.tracks, true)
            artistTrackAdapter.setOnClickListener(object : TrackAdapter.OnClickListener {
                override fun onTrackClick(position: Int) {
                    mListener?.onItemTrackClick(position, parentPosition)
                }

                override fun onPlusMenuClick(position: Int) {
                    mListener?.onItemPlusMenuClick(position, parentPosition)
                }

            })
            itemView.recycler_artists_tracks.apply {
                layoutManager = LinearLayoutManager(itemView.recycler_artists_tracks.context)
                adapter = artistTrackAdapter
                setRecycledViewPool(viewPool)
            }
            itemView.recycler_artists_tracks.visibility =
                if (artist.expand) View.VISIBLE else View.GONE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArtistHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_artist, parent, false)
        return ArtistHolder(v)
    }

    override fun getItemCount(): Int {
        return artists?.size ?: 0
    }

    override fun onBindViewHolder(holder: ArtistHolder, position: Int) {
        val artist = artists?.get(position)
        artist.let { holder.bindData(it!!, position) }
        holder.itemView.setOnClickListener {
            artist!!.expand = !artist.expand
            notifyItemChanged(position)
        }
        holder.itemView.artists_img_play.setOnClickListener {
            mListener?.onPlayIconClick(position)
        }
    }

    fun updateData(listArtists: MutableList<Artist>) {
        artists = listArtists
        notifyDataSetChanged()
    }

    interface OnClickListener {
        fun onPlayIconClick(position: Int)
        fun onItemTrackClick(position: Int, parentPosition: Int)
        fun onItemPlusMenuClick(position: Int, parentPosition: Int)
    }
}