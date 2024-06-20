package com.bangkit.talkee.fragment.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bangkit.talkee.GameHomeActivity
import com.bangkit.talkee.LeaderboardActivity
import com.bangkit.talkee.LearnHomeActivity
import com.bangkit.talkee.R
import com.bangkit.talkee.data.preference.TokenManager
import com.bangkit.talkee.data.repository.UserRepository
import com.bangkit.talkee.data.retrofit.ApiConfig
import com.bangkit.talkee.data.viewmodel.UserViewModel
import com.bangkit.talkee.data.viewmodel.UserViewModelFactory
import com.bangkit.talkee.databinding.FragmentHomeBinding
import com.bangkit.talkee.utils.formatNumber
import com.bumptech.glide.Glide
import kotlin.math.roundToInt

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private lateinit var userViewModel: UserViewModel
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

        showUserProfile(false)
        initViewModel()

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

    private fun initViewModel() {
        val apiService = ApiConfig.getApiService()
        val repo = UserRepository(apiService)
        val userViewModelFactory = UserViewModelFactory(repo)
        userViewModel = ViewModelProvider(this, userViewModelFactory)[UserViewModel::class.java]

        initObserver()
    }

    private fun initObserver() {
        userViewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            if (errorMessage.isNotEmpty()) {
                showToast("Koneksi bermasalah, silahkan coba lagi nanti!")
                userViewModel.clearErrorMessage()
            }
        }

        userViewModel.profileResponse.observe(viewLifecycleOwner) { profileResponse ->
            if(profileResponse.nama != null) {
                val userName = profileResponse.nama.toString()
                val userPhotoUrl = profileResponse.image.toString()
                val userLeaderboard = profileResponse.leaderboard
                val userPoints = profileResponse.poin
                val welcomeText = if(userName.length < 20) {
                    "Hai, $userName"
                } else {
                    "Hai!"
                }

                Glide.with(binding.userProfilePic.context).load(userPhotoUrl).into(binding.userProfilePic)
                binding.txtUserName.text = userName
                binding.txtUserPoints.text = formatNumber(userPoints?.roundToInt() ?: 0)
                binding.txtUserRank.text = userLeaderboard.toString()
                binding.txtHi.text = welcomeText

                when(userLeaderboard) {
                    1 -> {
                        binding.imgRank.setImageResource(R.drawable.ic_medal_first)
                        binding.txtUserRank.setTextColor(resources.getColor(R.color.rank_first))
                    }
                    2 -> {
                        binding.imgRank.setImageResource(R.drawable.ic_medal_second)
                        binding.txtUserRank.setTextColor(resources.getColor(R.color.rank_second))
                    }
                    else -> {
                        binding.imgRank.setImageResource(R.drawable.ic_medal_blank)
                        binding.txtUserRank.setTextColor(resources.getColor(R.color.rank_default))
                    }
                }

                showUserProfile(true)
            }
        }

        getUserProfile()
    }

    private fun getUserProfile() {
        val tokenManager = TokenManager(requireContext())
        val idToken = tokenManager.getIDToken()

        if (idToken != null) {
            try {
                userViewModel.getUserProfile(idToken)
            } catch (e: Exception) {
                showToast("Gagal mendapatkan profil.")
            }
        }
    }

    private fun showUserProfile(show: Boolean) {
        if(show) {
            binding.progressCircular.visibility = View.GONE
            binding.leaderboard.visibility = View.VISIBLE
        } else {
            binding.progressCircular.visibility = View.VISIBLE
            binding.leaderboard.visibility = View.INVISIBLE
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(activity, message, Toast.LENGTH_LONG).show()
    }
}