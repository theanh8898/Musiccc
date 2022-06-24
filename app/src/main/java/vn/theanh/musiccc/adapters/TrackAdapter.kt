package vn.theanh.musiccc.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_track.view.*
import vn.theanh.musiccc.R
import vn.theanh.musiccc.models.Track
import vn.theanh.musiccc.utils.DoAsync
import vn.theanh.musiccc.utils.Tool

class TrackAdapter(listTrack: MutableList<Track>?, showPlusButton: Boolean) :
    RecyclerView.Adapter<TrackAdapter.TrackHolder>() {
    private var tracks: MutableList<Track>? = null
    private var showPlusButton = false
    private var mListener: OnClickListener? = null
    fun setOnClickListener(listener: OnClickListener) {
        mListener = listener
    }

    init {
        tracks = listTrack
        this.showPlusButton = showPlusButton
        notifyDataSetChanged()
    }

    inner class TrackHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindData(track: Track) {
            if (!showPlusButton) {
                itemView.track_btn_menu.visibility = View.GONE
            }
            itemView.track_tv_title.text = track.title
            itemView.track_tv_artist.text = track.artist
            itemView.track_tv_duration.text = Tool.formatTime(track.duration)
            DoAsync {
                val art = Tool.getTrackPicture(track.data)
                itemView.rootView.post {
                    if (art != null) {
                        itemView.track_img_art.setImageBitmap(art)
                    } else {
                        itemView.track_img_art.setImageResource(R.mipmap.ic_launcher_foreground)
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
            TrackHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_track, parent, false)
        return TrackHolder(v)
    }

    override fun getItemCount(): Int {
        return tracks?.size ?: 0
    }

    override fun onBindViewHolder(holder: TrackHolder, position: Int) {
        val track = tracks?.get(position)
        track.let { holder.bindData(it!!) }
        holder.itemView.setOnClickListener {
            mListener?.onTrackClick(position)
        }
        holder.itemView.track_btn_menu.setOnClickListener {
            mListener?.onPlusMenuClick(position)
        }
    }

    fun updateData(tracks: MutableList<Track>) {
        this.tracks = tracks
        notifyDataSetChanged()
    }

    fun addTrack(track: Track) {
        tracks!!.add(track)
        tracks!!.sortBy { it.title }
        notifyDataSetChanged()
    }

    fun removeTrack(position: Int) {
        tracks!!.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, tracks!!.size)
    }

    interface OnClickListener {
        fun onTrackClick(position: Int)
        fun onPlusMenuClick(position: Int)
    }
}