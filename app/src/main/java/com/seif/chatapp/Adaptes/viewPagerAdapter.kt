package com.seif.chatapp.Adaptes

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter


// class for viewPager adapter
class viewPagerAdapter(fragmentManager: FragmentManager) :
    FragmentPagerAdapter(fragmentManager) {
    val fragments = ArrayList<Fragment>()
    val titles = ArrayList<String>()
    // used to return the selected fragment
    override fun getItem(position: Int): Fragment {
        return fragments[position]
    }
    // used to return the number of tabs in tab layout
    override fun getCount(): Int {
        return titles.size
    }
    // used to return tab title clicked
    override fun getPageTitle(position: Int): CharSequence? {
        return titles[position]
    }
    // used to add fragment and title of tab
    fun addFragment(fragment: Fragment, title: String) {
        fragments.add(fragment)
        titles.add(title)
    }
}
