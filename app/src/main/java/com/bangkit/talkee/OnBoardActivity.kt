package com.bangkit.talkee

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bangkit.talkee.data.preference.TokenManager
import com.bangkit.talkee.data.repository.UserRepository
import com.bangkit.talkee.data.retrofit.ApiConfig
import com.bangkit.talkee.data.viewmodel.UserViewModel
import com.bangkit.talkee.data.viewmodel.UserViewModelFactory
import com.bangkit.talkee.databinding.ActivityOnBoardBinding
import com.bangkit.talkee.utils.OnCompleteListener
import com.bangkit.talkee.utils.exchangeAuthCodeForAccessToken
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Scope
import com.google.android.gms.tasks.Task
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.Firebase
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class OnBoardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnBoardBinding
    private lateinit var userViewModel: UserViewModel
    private lateinit var auth: FirebaseAuth
    private var tokenManager: TokenManager = TokenManager(this)

    private val requiredPermissions = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.RECORD_AUDIO,
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnBoardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!(allPermissionsGranted())) {
            requestPermissions()
        }

        auth = Firebase.auth
        val currentTime = System.currentTimeMillis()
        val expireToken = tokenManager.getTokenExpire()

        initViewModel()
        if(expireToken > 0L) {
            if(currentTime > expireToken) {
                Log.d("EXPIRE", "EXPIRED")
                loginGoogle()
            } else {
                Log.d("EXPIRE", "VALID: $expireToken")
                updateUI(auth.currentUser)
            }
        } else {
            Log.d("EXPIRE", "ZERO")
        }

        binding.btnGoogle.setOnClickListener {
            loginGoogle()
        }
    }

    private fun allPermissionsGranted(): Boolean {
        return requiredPermissions.all {
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun requestPermissions() {
        requestPermissionsLauncher.launch(requiredPermissions)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (!(grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED })) {
                finish()
            }
        }
    }

    private val requestPermissionsLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (!(permissions.all { it.value })) {
            finish()
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
            isLoading(false)
            if (errorMessage.isNotEmpty()) {
                showToast("Login gagal, silahkan coba lagi nanti!")
                userViewModel.clearErrorMessage()
            }
        }

        userViewModel.profileResponse.observe(this) { result ->
            isLoading(false)
            if (result != null) {
                showToast("Halo, ${result.nama}!")
            } else {
                showToast("Login gagal.")
            }
        }
    }

    private fun loginGoogle() {
        isLoading(true)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestProfile()
            .requestScopes(
                Scope("https://www.googleapis.com/auth/userinfo.profile"),
                Scope("https://www.googleapis.com/auth/userinfo.email")
            )
            .requestServerAuthCode(BuildConfig.WEB_CLIENT_ID)
            .build()

        val googleSignInClient = GoogleSignIn.getClient(this, gso)

        googleSignInClient.silentSignIn()

        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, 9001)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 9001) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            val serverAuthCode = account?.serverAuthCode

            if (serverAuthCode != null) {
                exchangeAuthCodeForAccessToken(this, serverAuthCode, object : OnCompleteListener {
                    override fun onComplete(success: Boolean, accessToken: String?, idToken: String?, expireTokenTime: Long?) {
                        if (success) {
                            if (idToken != null) {
                                handleSignIn(idToken)
                            }
                        } else {
                            Log.e("MyActivity", "Token exchange failed")
                        }
                    }
                })
            }
        } catch (e: ApiException) {
            Log.w("SignInActivity", "signInResult:failed code=" + e.statusCode)
        }
    }


    private fun handleSignIn(idToken: String) {
        try {
            firebaseAuthWithGoogle(idToken)
        } catch (e: GoogleIdTokenParsingException) {
            isLoading(false)
            Log.e("ERROR GOOGLE", "Received an invalid google id token response", e)
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        isLoading(true)
        val credential: AuthCredential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    isLoading(false)
                    Log.d("GOOGLE", "signInWithCredential:success")
                    val user: FirebaseUser? = auth.currentUser
                    if (user != null) {
                        signInUser(user, idToken)
                    }
                } else {
                    isLoading(false)
                    Log.w("GOOGLE", "signInWithCredential:failure", task.exception)
                    updateUI(null)
                }
            }
    }

    private fun updateUI(currentUser: FirebaseUser?) {
        if (currentUser != null) {
            startActivity(Intent(this@OnBoardActivity, HomeActivity::class.java))
            finish()
        }
    }

    private fun signInUser(user: FirebaseUser, token: String) {
        isLoading(true)
        user.getIdToken(true).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("TOKEN", token)
                downloadImageToFile(this, user.photoUrl.toString(), "${user.displayName}-profile-pic.jpg") { file ->
                    if (file != null) {
                        lifecycleScope.launch {
                            val signInJob = async { userViewModel.signInUser(token, file, user.displayName ?: "User", user.email ?: "Email@email.com") }

                            awaitAll(signInJob)
                            isLoading(false)
                            updateUI(user)
                        }
                    } else {
                        showToast("Login gagal.")
                    }
                }
            } else {
                isLoading(false)
                showToast("Login gagal.")
            }
        }
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

    private fun isLoading(isLoading: Boolean) {
        if(isLoading) {
            binding.progressCircular.visibility = View.VISIBLE
        } else {
            binding.progressCircular.visibility = View.INVISIBLE
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    companion object {
        private const val PERMISSION_REQUEST_CODE = 1
    }
}