package com.bangkit.talkee

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bangkit.talkee.databinding.ActivityEditProfileBinding
import com.bangkit.talkee.fragment.dialog.ConfirmationDialogFragment

class EditProfileActivity : AppCompatActivity(), ConfirmationDialogFragment.ConfirmationDialogListener {

    private lateinit var binding: ActivityEditProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSave.setOnClickListener {
            showConfirmDialog()
        }

        binding.btnClose.setOnClickListener { finish() }
    }

    override fun onConfirm() {
        finish()
    }

    private fun showConfirmDialog() {
        val confirmDialog = ConfirmationDialogFragment.newInstance(
            "Konfirmasi",
            "Apakah perubahan sudah benar?",
            "Batal",
            "Simpan"
        )
        confirmDialog.listener = this
        confirmDialog.show(supportFragmentManager, "confirmDialog")
    }
}