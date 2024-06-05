package com.bangkit.talkee.fragment.home

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bangkit.talkee.EditProfileActivity
import com.bangkit.talkee.GameHomeActivity
import com.bangkit.talkee.LeaderboardActivity
import com.bangkit.talkee.LearnHomeActivity
import com.bangkit.talkee.OnBoardActivity
import com.bangkit.talkee.databinding.FragmentProfileBinding
import com.bangkit.talkee.fragment.dialog.ExitDialogFragment

class ProfileFragment : Fragment(), ExitDialogFragment.ExitDialogListener {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnEditProfile.setOnClickListener {
            val i = Intent(activity, EditProfileActivity::class.java)
            startActivity(i)
        }

        binding.btnSupport.setOnClickListener {
            sendEmail()
        }

        binding.btnLogOut.setOnClickListener {
            showExitDialog()
        }
    }

    override fun onExit() {
        val i = Intent(activity, OnBoardActivity::class.java)
        startActivity(i)
        activity?.finish()
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
}