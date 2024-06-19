package com.bangkit.talkee

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.view.ViewTreeObserver
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bangkit.talkee.databinding.ActivityGameCameraBinding
import com.bangkit.talkee.fragment.dialog.ExitDialogFragment

class GameCameraActivity : AppCompatActivity(), ExitDialogFragment.ExitDialogListener {

    private lateinit var binding: ActivityGameCameraBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val title = intent.getStringExtra("TITLE")
        binding.gameTitle.text = title

        binding.root.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                binding.root.viewTreeObserver.removeOnGlobalLayoutListener(this)

                val screenWidth = binding.root.width
                setGameTextBox(screenWidth)
                setButtonCamera(screenWidth)

                binding.videoPreview.setOnClickListener {
                    binding.videoPreview.start()
                }
            }
        })

        binding.btnClose.setOnClickListener {
            showExitDialog()
        }
    }

    private fun setGameTextBox(screenWidth: Int) {
        val marginInDp = 20
        val marginInPx = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            marginInDp.toFloat(),
            resources.displayMetrics
        ).toInt()

        val availableWidth = screenWidth - 2 * marginInPx
        val boxHeight = availableWidth * 4 / 3

        val layoutParams = binding.questionBox.layoutParams
        layoutParams.width = availableWidth
        layoutParams.height = boxHeight
        binding.questionBox.layoutParams = layoutParams
    }

    private fun setButtonCamera(screenWidth: Int) {
        val marginInDp = 20
        val marginInPx = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            marginInDp.toFloat(),
            resources.displayMetrics
        ).toInt()

        val availableWidth = screenWidth - 2 * marginInPx

        val layoutParams = binding.questionBox.layoutParams
        layoutParams.width = availableWidth
        binding.btnCamera.layoutParams.width = layoutParams.width

        binding.btnCamera.setOnClickListener {
            val intent = Intent(this@GameCameraActivity, RecordVideoActivity::class.java)
            videoResultLauncher.launch(intent)
        }
    }

    private val videoResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val videoUri = result.data?.data
            videoUri?.let {
                binding.videoPreview.visibility = View.VISIBLE
                binding.videoPreview.setVideoURI(it)
                binding.videoPreview.start()
            }
        }
    }

    override fun onExit() {
        finish()
    }

    private fun showExitDialog() {
        val exitDialog = ExitDialogFragment.newInstance(
            "Keluar",
            "Apakah kamu yakin ingin keluar? Game tidak akan disimpan.",
            "Keluar"
        )
        exitDialog.listener = this
        exitDialog.show(supportFragmentManager, "exitDialog")
    }
}