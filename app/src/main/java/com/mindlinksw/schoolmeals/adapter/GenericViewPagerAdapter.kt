package com.mindlinksw.schoolmeals.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import java.util.*

/**
 * General ViewPager Adapter
 */
class GenericViewPagerAdapter(
        mFragmentManager: FragmentManager
) : FragmentPagerAdapter(mFragmentManager) {

    private val mFragments = ArrayList<Fragment>()

    public fun addFragment(fragment: Fragment) {
        mFragments.add(fragment)
    }

    override fun getItem(position: Int): Fragment {
        return mFragments[position]
    }

    override fun getCount(): Int {
        return mFragments.size
    }

    override fun getItemPosition(`object`: Any): Int {
        // fragment update
        return POSITION_NONE
    }
}
