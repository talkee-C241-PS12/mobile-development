package com.bangkit.talkee.adapter

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.bangkit.talkee.fragment.activity.FinishedClassFragment
import com.bangkit.talkee.fragment.activity.FinishedGameFragment
import com.bangkit.talkee.fragment.activity.ProgressFragment

class ActivityFragmentAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {
    override fun createFragment(position: Int): Fragment {
        var fragment: Fragment? = null
        when (position) {
            0 -> fragment = ProgressFragment()
            1 -> fragment = FinishedClassFragment()
            2 -> fragment = FinishedGameFragment()
        }
        return fragment as Fragment
    }

    override fun getItemCount(): Int {
        return 3
    }
}