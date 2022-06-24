package vn.theanh.musiccc.services

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder

class MusicService : Service() {
    private val musicBind: IBinder = MusicBinder()
    var playerHolder: PlayerHolder? = null
        private set
    var notificationMusiccc: NotificationMusiccc? = null
        private set

    inner class MusicBinder : Binder() {
        val service: MusicService
            get() = this@MusicService
    }

    override fun onBind(intent: Intent): IBinder? {
        if (playerHolder == null) {
            playerHolder = PlayerHolder(this)
            notificationMusiccc = NotificationMusiccc(this)
            playerHolder!!.registerNotiReceiver()
        }
        return musicBind
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        notificationMusiccc = null
        playerHolder!!.stop()
        super.onDestroy()
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        stopSelf()
    }
}