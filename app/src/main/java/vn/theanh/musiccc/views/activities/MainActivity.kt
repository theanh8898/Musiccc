package vn.theanh.musiccc.views.activities

import android.content.Context
import android.content.Intent
import android.content.pm.ResolveInfo
import android.os.Build
import android.os.Bundle
import android.view.View
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_footer_player.*
import vn.theanh.musiccc.R
import vn.theanh.musiccc.adapters.SectionsPagerAdapter
import vn.theanh.musiccc.data.DataLoader
import vn.theanh.musiccc.utils.MusicPreference
import vn.theanh.musiccc.utils.PlaybackListener
import vn.theanh.musiccc.utils.Tool
import vn.theanh.musiccc.views.fragments.PlayerFragment
import java.util.*
import kotlin.system.exitProcess


class MainActivity : BaseActivity(), View.OnClickListener {
    companion object {
        fun getStartIntent(context: Context): Intent = Intent(context, MainActivity::class.java)
    }

    private var firstPlay = true
    private var mPlaybackListener: MyPlaybackListener? = null
    private var musicPreference: MusicPreference? = null

    private val playerFrag = PlayerFragment()
    private var sectionsPagerAdapter: SectionsPagerAdapter? = null

    inner class MyPlaybackListener : PlaybackListener() {
        override fun onStateChanged(state: Int) {
            if (iPlayerHolder?.getState() == State.PLAYING) {
                updateFooter()
            }
            if (iPlayerHolder?.getState() != State.RESUMED &&
                iPlayerHolder?.getState() != State.PAUSED
            ) {
                updateFooter()
            }
        }
    }

    override fun onServiceConnected() {
        if (mPlaybackListener == null) {
            mPlaybackListener = MyPlaybackListener()
            iPlayerHolder!!.setPlaybackListener(mPlaybackListener!!)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        binSer()
        setupViewPager()
        footer_img_previous.setOnClickListener(this)
        footer_img_playPause.setOnClickListener(this)
        footer_img_next.setOnClickListener(this)
        frame_footer.setOnClickListener(this)
        close.setOnClickListener(this)
        send_mail.setOnClickListener(this)
        musicPreference = MusicPreference(this)
    }

    private fun setupViewPager() {
        tabs.apply {
            addTab(this.newTab().setText("Tracks"))
            addTab(this.newTab().setText("Artists"))
            addTab(this.newTab().setText("Albums"))
            addTab(this.newTab().setText("Playlists"))
            this.tabGravity = TabLayout.GRAVITY_FILL
        }
        sectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)
        view_pager.apply {
            adapter = sectionsPagerAdapter
            currentItem = 0
            addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabs))
        }
        tabs.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(view_pager))
    }

    override fun onStart() {
        super.onStart()
        if (mPlaybackListener == null) {
            mPlaybackListener = MyPlaybackListener()
            iPlayerHolder?.setPlaybackListener(mPlaybackListener!!)
        }
    }

    override fun onPause() {
        super.onPause()
        unbindSer()
    }

    override fun onResume() {
        super.onResume()
        binSer()
        if (musicService != null) {
            updateFooter()
        }
    }

    override fun onBackPressed() {
        when {
            supportFragmentManager.findFragmentByTag("albumTracksFrag.tag") != null -> {
                supportFragmentManager.popBackStack()
                main_content.visibility = View.VISIBLE
                app_bar.visibility = View.VISIBLE
            }
            supportFragmentManager.findFragmentByTag("playlistTracksFrag.tag") != null -> {
                supportFragmentManager.popBackStack()
                main_content.visibility = View.VISIBLE
                app_bar.visibility = View.VISIBLE
            }
            supportFragmentManager.backStackEntryCount == 1 -> {
                supportFragmentManager.popBackStackImmediate()
                main_content.visibility = View.VISIBLE
                app_bar.visibility = View.VISIBLE
            }
            else -> {
                val intent = Intent()
                intent.action = Intent.ACTION_MAIN
                intent.addCategory(Intent.CATEGORY_HOME)
                startActivity(intent)
            }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.footer_img_previous -> playPrev()
            R.id.footer_img_playPause -> resumePause()
            R.id.footer_img_next -> playNext()
            R.id.close -> {
                finish()
                exitProcess(0)
            }
            R.id.send_mail -> {
                val emailIntent = Intent(Intent.ACTION_SEND)
                val aEmailList = arrayOf("16021551@vnu.edu.vn")
                emailIntent.putExtra(Intent.EXTRA_EMAIL, aEmailList)
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Gop y Musiccc")
                emailIntent.type = "text/plain"
                val pm = packageManager
                val matches =
                    pm.queryIntentActivities(intent, 0)
                var best: ResolveInfo? = null
                for (info in matches) if (info.activityInfo.packageName.endsWith(".gm") ||
                    info.activityInfo.name.toLowerCase(Locale.ROOT).contains("gmail")
                ) best = info
                if (best != null) intent.setClassName(
                    best.activityInfo.packageName,
                    best.activityInfo.name
                )
                startActivity(emailIntent)
            }
            R.id.frame_footer -> if (!firstPlay) {
                val playerTag = playerFrag.tag
                val popped = supportFragmentManager.popBackStackImmediate(playerTag, 0)
                if (!popped && supportFragmentManager.findFragmentByTag(playerTag) == null) {
                    supportFragmentManager.beginTransaction().addToBackStack(playerTag)
                        .add(R.id.full_frame, playerFrag, playerTag).show(playerFrag).commit()
                }
                hideView()
            }
        }
    }

    fun hideView() {
        main_content.visibility = View.GONE
        app_bar.visibility = View.GONE
    }

    private fun playPrev() {
        if (iPlayerHolder?.isPlayerExist()!! && !firstPlay) {
            iPlayerHolder!!.playPrev()
            updateFooter()
        }
    }

    private fun resumePause() {
        if (firstPlay) {
            iPlayerHolder!!.initPlayer()
            iPlayerHolder!!.updateTracks(DataLoader(this).queryTracks(), 0)
            iPlayerHolder!!.setRandomTrackPos()
            iPlayerHolder!!.play()
        } else iPlayerHolder!!.resumePause()
        updateFooter()
    }

    private fun playNext() {
        if (iPlayerHolder?.isPlayerExist()!! && !firstPlay) {
            iPlayerHolder!!.playNext()
            updateFooter()
        }
    }

    fun updateFooter() {
        firstPlay = false
        val track = iPlayerHolder!!.getCurrentTrack()!!
        footer_txt_title.text = track.title
        footer_txt_artist.text = track.artist
        val art = Tool.getTrackPicture(track.data)
        if (art != null) {
            footer_img_art.setImageBitmap(art)
        } else {
            footer_img_art.setImageResource(R.mipmap.ic_launcher_foreground)
        }
        @Suppress("DEPRECATION")
        if (iPlayerHolder!!.getState() == PlaybackListener.State.PAUSED) {
            footer_img_playPause.setImageResource(R.mipmap.ic_play_foreground)
        } else {
            footer_img_playPause.setImageResource(R.mipmap.ic_pause_foreground)
        }
    }
}