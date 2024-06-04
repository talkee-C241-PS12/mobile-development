package com.bangkit.talkee

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bangkit.talkee.databinding.ActivityGameCameraBinding
import com.bangkit.talkee.databinding.ActivityGameWordBinding

class GameCameraActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGameCameraBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }
}