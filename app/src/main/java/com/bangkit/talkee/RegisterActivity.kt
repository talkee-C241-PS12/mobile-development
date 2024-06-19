package com.bangkit.talkee

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bangkit.talkee.databinding.ActivityRegisterBinding
import com.bangkit.talkee.fragment.dialog.ImageDialogFragment
import com.bangkit.talkee.utils.getImageUri

class RegisterActivity : AppCompatActivity(), ImageDialogFragment.ImageDialogListener {

    private lateinit var binding: ActivityRegisterBinding
    private var currentImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnContinue.setOnClickListener {
            val i = Intent(this@RegisterActivity, HomeActivity::class.java)
            startActivity(i)
            finish()
        }

        binding.btnCamera.setOnClickListener {
            showImageDialog()
        }
    }

    private fun startGallery() {
        launcherGallery.launch("image/*")
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            currentImageUri = it
            showImage()
        } ?: showToast("Tidak ada gambar yang dipilih")
    }

    private fun startCamera() {
        currentImageUri = getImageUri(this)
        launcherCamera.launch(currentImageUri!!)
    }

    private val launcherCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            showImage()
        }
    }

    private fun showImage() {
        currentImageUri?.let {
            binding.imgProfile.setImageURI(it)
        }
    }

    override fun onCamera() {
        startCamera()
    }

    override fun onGallery() {
        startGallery()
    }

    private fun showImageDialog() {
        val imageDialog = ImageDialogFragment.newInstance(
            getString(R.string.default_image_title),
            getString(R.string.default_image_message),
            getString(R.string.default_image_button_text_camera),
            getString(R.string.default_image_button_text_gallery),
        )
        imageDialog.listener = this
        imageDialog.show(supportFragmentManager, "imageDialog")
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}