package com.bangkit.talkee.fragment.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.bangkit.talkee.R
import com.bangkit.talkee.adapter.ActivityFragmentAdapter
import com.bangkit.talkee.databinding.FragmentActivityBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class ActivityFragment : Fragment() {
    private var _binding: FragmentActivityBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentActivityBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fragmentActivity = requireActivity()
        val sectionsPagerAdapter = ActivityFragmentAdapter(fragmentActivity)
        val viewPager: ViewPager2 = binding.viewPager
        viewPager.adapter = sectionsPagerAdapter

        val tabs: TabLayout = binding.tabs
        tabs.background = null
        TabLayoutMediator(tabs, viewPager) { tab, position ->
            tab.customView = createTabView(resources.getString(TAB_TITLES[position]))
        }.attach()

        (activity as? AppCompatActivity)?.supportActionBar?.elevation = 0f

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                updateTabSelection(tabs, position)
            }
        })
    }

    private fun createTabView(title: String): View {
        val tabView = LayoutInflater.from(context).inflate(R.layout.custom_tab, null)
        val tabText = tabView.findViewById<TextView>(R.id.tab_text)
        tabText.text = title
        return tabView
    }

    private fun updateTabSelection(tabs: TabLayout, position: Int) {
        for (i in 0 until tabs.tabCount) {
            val tab = tabs.getTabAt(i)
            val tabView = tab?.customView
            tabView?.isSelected = (i == position)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @StringRes
        private val TAB_TITLES = intArrayOf(
            R.string.activity_fragment_progress,
            R.string.activity_fragment_done_class,
            R.string.activity_fragment_done_game,
        )
    }
}