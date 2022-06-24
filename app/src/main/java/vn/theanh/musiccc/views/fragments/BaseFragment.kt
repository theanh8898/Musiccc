package vn.theanh.musiccc.views.fragments

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.fragment.app.Fragment
import vn.theanh.musiccc.services.IPlayerHolder
import vn.theanh.musiccc.services.MusicService
import vn.theanh.musiccc.services.NotificationMusiccc
import vn.theanh.musiccc.utils.PlaybackListener
import vn.theanh.musiccc.views.activities.MainActivity


abstract class BaseFragment : Fragment() {
    var musicService: MusicService? = null
    var iPlayerHolder: IPlayerHolder? = null
    var notificationMusiccc: NotificationMusiccc? = null
    var mPlaybackListener: MyPlaybackListener? = null
    var mainActivity: MainActivity? = null
    private lateinit var playerIntent: Intent

    var serviceBound = false

    inner class MyPlaybackListener : PlaybackListener() {
        override fun onStateChanged(state: Int) {
            if (iPlayerHolder?.getState() != State.RESUMED &&
                iPlayerHolder?.getState() != State.PAUSED
            ) {
                mainActivity!!.updateFooter()
            }
        }
    }

    private var serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            musicService = (service as MusicService.MusicBinder).service
            iPlayerHolder = musicService!!.playerHolder
            notificationMusiccc = musicService!!.notificationMusiccc
            this@BaseFragment.onServiceConnected()
            if (mPlaybackListener == null) {
                mPlaybackListener = MyPlaybackListener()
                iPlayerHolder!!.setPlaybackListener(mPlaybackListener!!)
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            musicService = null
        }
    }

    abstract fun onServiceConnected()
    fun binSer() {
        playerIntent = Intent(activity, MusicService::class.java)
        playerIntent.action = ""
        requireActivity().bindService(playerIntent, serviceConnection, Context.BIND_AUTO_CREATE)
        requireActivity().startService(playerIntent)
        serviceBound = true
    }

    private fun unbindSer() {
        if (serviceBound) {
            requireActivity().unbindService(serviceConnection)
            serviceBound = false
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = activity as MainActivity
    }

    override fun onDestroy() {
        super.onDestroy()
        mPlaybackListener = null
        unbindSer()
    }
}