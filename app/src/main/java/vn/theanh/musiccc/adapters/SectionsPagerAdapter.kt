package vn.theanh.musiccc.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import vn.theanh.musiccc.views.fragments.AlbumsFragment
import vn.theanh.musiccc.views.fragments.ArtistsFragment
import vn.theanh.musiccc.views.fragments.PlaylistsFragment
import vn.theanh.musiccc.views.fragments.TracksFragment

@Suppress("DEPRECATION")
class SectionsPagerAdapter(fm: FragmentManager) :
    FragmentStatePagerAdapter(fm) {
    private val tracksFrag = TracksFragment()
    private val artistsFrag = ArtistsFragment()
    private val albumsFrag = AlbumsFragment()
    private val playlistFrag = PlaylistsFragment()
    private val mFragmentList: ArrayList<Fragment> =
        arrayListOf(tracksFrag, artistsFrag, albumsFrag, playlistFrag)

    override fun getItem(position: Int): Fragment {
        return mFragmentList[position]
    }

    override fun getCount(): Int {
        return mFragmentList.size
    }
}