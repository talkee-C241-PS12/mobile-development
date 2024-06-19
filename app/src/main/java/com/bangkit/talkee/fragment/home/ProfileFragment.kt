package com.bangkit.talkee.fragment.home

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import com.bangkit.talkee.EditProfileActivity
import com.bangkit.talkee.OnBoardActivity
import com.bangkit.talkee.data.preference.TokenManager
import com.bangkit.talkee.data.repository.UserRepository
import com.bangkit.talkee.data.retrofit.ApiConfig
import com.bangkit.talkee.data.viewmodel.UserViewModel
import com.bangkit.talkee.data.viewmodel.UserViewModelFactory
import com.bangkit.talkee.databinding.FragmentProfileBinding
import com.bangkit.talkee.fragment.dialog.ExitDialogFragment
import com.bumptech.glide.Glide
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class ProfileFragment : Fragment(), ExitDialogFragment.ExitDialogListener {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var userViewModel: UserViewModel
    private lateinit var auth: FirebaseAuth
    private var userName: String = ""
    private var userPhotoUrl: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        showUserProfile(false)
        auth = Firebase.auth
        initViewModel()

        val userEmail = auth.currentUser?.email
        binding.txtUserEmail.text = userEmail

        binding.btnEditProfile.setOnClickListener {
            val i = Intent(activity, EditProfileActivity::class.java)
            i.putExtra("USER_NAME", userName)
            i.putExtra("USER_PHOTO_URL", userPhotoUrl)
            launcherActivityForResult.launch(i)
        }

        binding.btnSupport.setOnClickListener {
            sendEmail()
        }

        binding.btnLogOut.setOnClickListener {
            showExitDialog()
        }
    }

    private val launcherActivityForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            initViewModel()
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
                userName = profileResponse.nama.toString()
                userPhotoUrl = profileResponse.image.toString()

                binding.txtUserName.text = userName
                Glide.with(binding.imgProfile.context).load(userPhotoUrl).into(binding.imgProfile)

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
            binding.imgProfileBg.visibility = View.VISIBLE
            binding.imgProfile.visibility = View.VISIBLE
            binding.txtUserName.visibility = View.VISIBLE
            binding.txtUserEmail.visibility = View.VISIBLE
        } else {
            binding.progressCircular.visibility = View.VISIBLE
            binding.imgProfileBg.visibility = View.INVISIBLE
            binding.imgProfile.visibility = View.INVISIBLE
            binding.txtUserName.visibility = View.INVISIBLE
            binding.txtUserEmail.visibility = View.INVISIBLE
        }
    }

    override fun onExit() {
        signOutUser()
        val i = Intent(activity, OnBoardActivity::class.java)
        startActivity(i)
        activity?.finish()
    }

    private fun signOutUser() {
        val tokenManager = TokenManager(requireContext())
        tokenManager.clearTokens()
        auth.signOut()
    }

    private fun showExitDialog() {
        val exitDialog = ExitDialogFragment.newInstance(
            "Keluar",
            "Apakah kamu yakin ingin keluar dari akun ini? Semua data perangkat akan dihapus.",
            "Keluar"
        )
        exitDialog.listener = this
        activity?.supportFragmentManager?.let { exitDialog.show(it, "exitDialog") }
    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun sendEmail() {
        val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf("talkee@bangkit.academy"))
            putExtra(Intent.EXTRA_SUBJECT, "Email Support")
            putExtra(Intent.EXTRA_TEXT, "Hai Talkee, saya butuh bantuan.")
        }

        startActivity(emailIntent)
    }

    private fun showToast(message: String) {
        Toast.makeText(activity, message, Toast.LENGTH_LONG).show()
    }
}