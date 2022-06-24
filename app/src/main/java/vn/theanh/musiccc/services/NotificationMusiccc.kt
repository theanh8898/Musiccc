package vn.theanh.musiccc.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import vn.theanh.musiccc.R
import vn.theanh.musiccc.models.Track
import vn.theanh.musiccc.utils.PlaybackListener
import vn.theanh.musiccc.utils.Tool
import vn.theanh.musiccc.views.activities.MainActivity

class NotificationMusiccc internal constructor(private val mMusicService: MusicService) {
    companion object {
        const val NOTIFICATION_ID = 1998
        internal const val PREV_ACTION = "musiccc.PREV"
        internal const val RESUME_PAUSE_ACTION = "musiccc.RESUME_PAUSE"
        internal const val NEXT_ACTION = "musiccc.NEXT"
    }

    private var mediaSession: MediaSessionCompat? = null
    private val context: Context
    private val requestCode = 0
    private var channelId = "vn.theanh.musiccc.channelID"

    init {
        context = mMusicService.application
    }

    private fun playerPendingIntent(action: String): PendingIntent {
        val intent = Intent()
        intent.action = action
        return PendingIntent.getBroadcast(
            mMusicService, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    private fun notificationAction(action: String): NotificationCompat.Action {
        val icon = when (action) {
            PREV_ACTION -> R.mipmap.ic_previous_foreground
            RESUME_PAUSE_ACTION ->
                if (mMusicService.playerHolder?.getState() == PlaybackListener.State.PAUSED)
                    R.mipmap.ic_play_foreground
                else R.mipmap.ic_pause_foreground
            NEXT_ACTION -> R.mipmap.ic_next_foreground
            else -> R.mipmap.ic_next_foreground
        }
        return NotificationCompat.Action.Builder(icon, action, playerPendingIntent(action)).build()
    }

    fun buildNotification(): Notification {
        val track = mMusicService.playerHolder?.getCurrentTrack()
        updateMetaData(track!!)
        val intent = Intent(context, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        val pIntent = PendingIntent.getActivity(
            context,
            System.currentTimeMillis().toInt(), intent, 0
        )
        var art = Tool.getTrackPicture(track.data)
        if (art == null) {
            art = BitmapFactory.decodeResource(context.resources, R.mipmap.ic_launcher_foreground)
        }
        channelId =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) createNotificationChannel() else ""

        @Suppress("DEPRECATION")
        return NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_noti)
            .setLargeIcon(art)
            .setShowWhen(false)
            .setColor(context.resources.getColor(R.color.colorPrimary))
            .setContentTitle(track.title)
            .setContentText(track.artist)
            .setContentIntent(pIntent)
            .addAction(notificationAction(PREV_ACTION))
            .addAction(notificationAction(RESUME_PAUSE_ACTION))
            .addAction(notificationAction(NEXT_ACTION))
            .setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setShowActionsInCompactView(0, 1, 2)
                    .setMediaSession(mediaSession!!.sessionToken)
            )
            .build()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(): String {
        val channelName = "Musiccc Service"
        val chan = NotificationChannel(
            channelId,
            channelName, NotificationManager.IMPORTANCE_HIGH
        )
        chan.lightColor = Color.GREEN
        chan.importance = NotificationManager.IMPORTANCE_NONE
        chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        val service = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        service.createNotificationChannel(chan)
        return channelId
    }

    private fun updateMetaData(track: Track) {
        mediaSession = MediaSessionCompat(context, "Musiccc")
        mediaSession!!.setMetadata(
            MediaMetadataCompat.Builder()
                .putBitmap(
                    MediaMetadataCompat.METADATA_KEY_ALBUM_ART,
                    if (Tool.getTrackPicture(track.data) != null) Tool.getTrackPicture(track.data)
                    else
                        BitmapFactory.decodeResource(
                            context.resources,
                            R.mipmap.ic_launcher_foreground
                        )
                )
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, track.artist)
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, track.title)
                .build()
        )
    }
}