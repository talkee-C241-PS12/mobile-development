package com.bangkit.talkee.fragment.home

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bangkit.talkee.GameHomeActivity
import com.bangkit.talkee.LeaderboardActivity
import com.bangkit.talkee.LearnHomeActivity
import com.bangkit.talkee.R
import com.bangkit.talkee.databinding.FragmentHistoryBinding
import com.bangkit.talkee.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.viewClass.setOnClickListener {
            val i = Intent(activity, LearnHomeActivity::class.java)
            startActivity(i)
        }

        binding.viewGame.setOnClickListener {
            val i = Intent(activity, GameHomeActivity::class.java)
            startActivity(i)
        }

        binding.leaderboard.setOnClickListener {
            val i = Intent(activity, LeaderboardActivity::class.java)
            startActivity(i)
        }
    }
}