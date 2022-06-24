package vn.theanh.musiccc.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_playlist.view.*
import vn.theanh.musiccc.R
import vn.theanh.musiccc.models.Playlist
import vn.theanh.musiccc.models.Track
import vn.theanh.musiccc.utils.DoAsync
import vn.theanh.musiccc.utils.Tool

class PlaylistAdapter(listPlaylists: MutableList<Playlist>) :
    RecyclerView.Adapter<PlaylistAdapter.PlaylistHolder>() {
    private var playlists = mutableListOf<Playlist>()
    private var mListener: OnClickListener? = null

    fun setOnClickListener(listener: OnClickListener) {
        mListener = listener
    }

    init {
        playlists = listPlaylists
        notifyDataSetChanged()
    }

    inner class PlaylistHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindData(playlist: Playlist) {
            itemView.playlist_txt_name.text = playlist.name
            if (playlist.name == "Create a new playlist") {
                itemView.grid_playlist_art.visibility = View.GONE
                itemView.playlist_img_play.visibility = View.GONE
                itemView.playlist_img_art.visibility = View.VISIBLE
                itemView.playlist_img_art.setImageResource(R.mipmap.ic_launcher_foreground)
            } else {
                if (playlist.tracks.size < 4) {
                    itemView.grid_playlist_art.visibility = View.GONE
                    itemView.playlist_img_art.visibility = View.VISIBLE
                    DoAsync {
                        val art = Tool.getTrackPicture(playlist.tracks[0].data)
                        itemView.rootView.post {
                            if (art != null) {
                                itemView.playlist_img_art.setImageBitmap(art)
                            } else {
                                itemView.playlist_img_art.setImageResource(R.mipmap.ic_launcher_foreground)
                            }
                        }
                    }
                } else {
                    itemView.grid_playlist_art.visibility = View.VISIBLE
                    itemView.playlist_img_art.visibility = View.GONE
                    DoAsync {
                        val art1 = Tool.getTrackPicture(playlist.tracks[0].data)
                        val art2 = Tool.getTrackPicture(playlist.tracks[1].data)
                        val art3 = Tool.getTrackPicture(playlist.tracks[2].data)
                        val art4 = Tool.getTrackPicture(playlist.tracks[3].data)
                        itemView.rootView.post {
                            if (art1 != null) {
                                itemView.playlist_img_art1.setImageBitmap(art1)
                            } else {
                                itemView.playlist_img_art1.setImageResource(R.mipmap.ic_launcher_foreground)
                            }
                            if (art2 != null) {
                                itemView.playlist_img_art2.setImageBitmap(art2)
                            } else {
                                itemView.playlist_img_art2.setImageResource(R.mipmap.ic_launcher_foreground)
                            }
                            if (art3 != null) {
                                itemView.playlist_img_art3.setImageBitmap(art3)
                            } else {
                                itemView.playlist_img_art3.setImageResource(R.mipmap.ic_launcher_foreground)
                            }
                            if (art4 != null) {
                                itemView.playlist_img_art4.setImageBitmap(art4)
                            } else {
                                itemView.playlist_img_art4.setImageResource(R.mipmap.ic_launcher_foreground)
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_playlist, parent, false)
        return PlaylistHolder(v)
    }

    override fun getItemCount(): Int {
        return playlists.size
    }

    override fun onBindViewHolder(holder: PlaylistHolder, position: Int) {
        val playlist = playlists[position]
        playlist.let { holder.bindData(it) }
        holder.itemView.setOnClickListener {
            mListener?.onPlaylistClick(position)
        }
        holder.itemView.playlist_img_play.setOnClickListener {
            mListener?.onPlayImgClick(position)
        }
    }

    fun updateData(listPlaylists: MutableList<Playlist>) {
        playlists = listPlaylists
        notifyDataSetChanged()
    }

    fun updateNewList(position: Int, newPlaylistTracks: MutableList<Track>) {
        playlists[position].tracks = newPlaylistTracks
        notifyDataSetChanged()
    }

    interface OnClickListener {
        fun onPlaylistClick(position: Int)
        fun onPlayImgClick(position: Int)
    }
}