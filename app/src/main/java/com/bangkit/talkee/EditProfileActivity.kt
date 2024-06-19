package com.bangkit.talkee

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.OpenableColumns
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import com.bangkit.talkee.data.preference.TokenManager
import com.bangkit.talkee.data.repository.UserRepository
import com.bangkit.talkee.data.retrofit.ApiConfig
import com.bangkit.talkee.data.viewmodel.UserViewModel
import com.bangkit.talkee.data.viewmodel.UserViewModelFactory
import com.bangkit.talkee.databinding.ActivityEditProfileBinding
import com.bangkit.talkee.fragment.dialog.ConfirmationDialogFragment
import com.bangkit.talkee.fragment.dialog.ImageDialogFragment
import com.bangkit.talkee.utils.getImageUri
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class EditProfileActivity : AppCompatActivity(), ConfirmationDialogFragment.ConfirmationDialogListener, ImageDialogFragment.ImageDialogListener {

    private lateinit var binding: ActivityEditProfileBinding
    private lateinit var userViewModel: UserViewModel
    private var currentImageUri: Uri? = null
    private var userName: String = ""
    private var userPhotoUrl: String = ""
    private var photoChanged: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSave.visibility = View.INVISIBLE

        intent.getStringExtra("USER_NAME").toString().let { userName = it }
        intent.getStringExtra("USER_PHOTO_URL").toString().let { userPhotoUrl = it }

        initViewModel()

        binding.etFullName.setText(userName)
        Glide.with(binding.imgProfile.context).load(userPhotoUrl).into(binding.imgProfile)

        binding.btnSave.setOnClickListener {
            showConfirmDialog()
        }

        binding.btnCamera.setOnClickListener {
            showImageDialog()
        }

        binding.btnClose.setOnClickListener { finish() }

        binding.etFullName.doOnTextChanged { text, start, before, count ->
            binding.btnSave.visibility = View.VISIBLE
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
        userViewModel.errorMessage.observe(this) { errorMessage ->
            if (errorMessage.isNotEmpty()) {
                showToast("Koneksi bermasalah, silahkan coba lagi nanti!")
                userViewModel.clearErrorMessage()
            }
        }

        userViewModel.successMessage.observe(this) { successMessage ->
            if (successMessage.isNotEmpty()) {
                showToast("Profil berhasil diubah")
                userViewModel.clearSuccessMessage()

                isLoading(false)
                setResult(Activity.RESULT_OK)
                finish()
            }
        }
    }

    override fun onConfirm() {
        isLoading(true)
        val tokenManager = TokenManager(this)
        val idToken = tokenManager.getIDToken()
        var newUserName = binding.etFullName.text.toString()
        val formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss")
        val formattedDateTime = LocalDateTime.now().format(formatter)
        val fileName = "${userName}-profile-pic-$formattedDateTime.jpg"
        var imageFile: File? = null

        if(photoChanged) {
            imageFile = currentImageUri?.let { uriToFile(this, it, fileName) }
            if (idToken != null) {
                userViewModel.updateUser(idToken, imageFile!!, newUserName)
            } else {
                showToast("Terjadi kesalahan, silahkan coba lagi nanti!")
            }
        } else {
            downloadImageToFile(this, userPhotoUrl, fileName) { file ->
                if (file != null) {
                    if (idToken != null) {
                        userViewModel.updateUser(idToken, file, newUserName)
                    } else {
                        showToast("Terjadi kesalahan, silahkan coba lagi nanti!")
                    }
                } else {
                    showToast("Update foto gagal.")
                }
            }
        }
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
        photoChanged = true
        binding.btnSave.visibility = View.VISIBLE
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

    private fun isLoading(isLoading: Boolean) {
        if(isLoading) {
            binding.progressCircular.visibility = View.VISIBLE
        } else {
            binding.progressCircular.visibility = View.INVISIBLE
        }
    }

    private fun uriToFile(context: Context, uri: Uri, fileName: String): File? {
        val file = File(context.cacheDir, fileName)
        try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            val outputStream = FileOutputStream(file)
            inputStream?.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
        return file
    }

    private fun downloadImageToFile(context: Context, imageUrl: String, fileName: String, callback: (File?) -> Unit) {
        Glide.with(context)
            .asBitmap()
            .load(imageUrl)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    val file = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), fileName)
                    try {
                        val outputStream = FileOutputStream(file)
                        resource.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                        outputStream.flush()
                        outputStream.close()
                        callback(file)
                    } catch (e: IOException) {
                        e.printStackTrace()
                        callback(null)
                    }
                }

                override fun onLoadCleared(placeholder: Drawable?) {}

                override fun onLoadFailed(errorDrawable: Drawable?) {
                    super.onLoadFailed(errorDrawable)
                    callback(null)
                }
            })
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}