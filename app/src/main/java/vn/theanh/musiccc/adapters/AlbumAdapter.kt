package vn.theanh.musiccc.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_album.view.*
import vn.theanh.musiccc.R
import vn.theanh.musiccc.models.Album
import vn.theanh.musiccc.utils.DoAsync
import vn.theanh.musiccc.utils.Tool

class AlbumAdapter(listAlbums: MutableList<Album>) :
    RecyclerView.Adapter<AlbumAdapter.AlbumHolder>() {
    private var albums = mutableListOf<Album>()
    private var mListener: OnClickListener? = null

    fun setOnClickListener(listener: OnClickListener) {
        mListener = listener
    }

    init {
        albums = listAlbums
        notifyDataSetChanged()
    }

    inner class AlbumHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @SuppressLint("SetTextI18n")
        fun bindData(album: Album) {
            itemView.album_txt_name.text = album.name
            itemView.album_txt_countTracks.text =
                "${album.tracks.size} track" + if (album.tracks.size > 1) "s" else " only"
            if (album.tracks.size < 4) {
                itemView.grid_album_art.visibility = View.GONE
                itemView.album_img_art.visibility = View.VISIBLE
                DoAsync {
                    val art = Tool.getTrackPicture(album.tracks[0].data)
                    itemView.rootView.post {
                        if (art != null) {
                            itemView.album_img_art.setImageBitmap(art)
                        } else {
                            itemView.album_img_art.setImageResource(R.mipmap.ic_launcher_foreground)
                        }
                    }
                }
            } else {
                itemView.grid_album_art.visibility = View.VISIBLE
                itemView.album_img_art.visibility = View.GONE
                DoAsync {
                    val art1 = Tool.getTrackPicture(album.tracks[0].data)
                    val art2 = Tool.getTrackPicture(album.tracks[1].data)
                    val art3 = Tool.getTrackPicture(album.tracks[2].data)
                    val art4 = Tool.getTrackPicture(album.tracks[3].data)
                    itemView.rootView.post {
                        if (art1 != null) {
                            itemView.album_img_art1.setImageBitmap(art1)
                        } else {
                            itemView.album_img_art1.setImageResource(R.mipmap.ic_launcher_foreground)
                        }
                        if (art2 != null) {
                            itemView.album_img_art2.setImageBitmap(art2)
                        } else {
                            itemView.album_img_art2.setImageResource(R.mipmap.ic_launcher_foreground)
                        }
                        if (art3 != null) {
                            itemView.album_img_art3.setImageBitmap(art3)
                        } else {
                            itemView.album_img_art3.setImageResource(R.mipmap.ic_launcher_foreground)
                        }
                        if (art4 != null) {
                            itemView.album_img_art4.setImageBitmap(art4)
                        } else {
                            itemView.album_img_art4.setImageResource(R.mipmap.ic_launcher_foreground)
                        }
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_album, parent, false)
        return AlbumHolder(v)
    }

    override fun getItemCount(): Int {
        return albums.size
    }

    override fun onBindViewHolder(holder: AlbumHolder, position: Int) {
        val album = albums[position]
        album.let { holder.bindData(it) }
        holder.itemView.setOnClickListener {
            mListener?.onAlbumClick(position)
        }
    }

    fun updateData(listAlbums: MutableList<Album>) {
        albums = listAlbums
        notifyDataSetChanged()
    }

    interface OnClickListener {
        fun onAlbumClick(position: Int)
    }
}