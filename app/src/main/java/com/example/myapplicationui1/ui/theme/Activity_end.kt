package com.example.myapplicationui1.ui.theme

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.util.Log
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplicationui1.GamePlay11Activity
import com.example.myapplicationui1.GameSelection11Activity
import com.example.myapplicationui1.R

class ActivityEnd : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_end)

        val retryButton: Button = findViewById(R.id.retry_button)
        retryButton.setOnClickListener {
            // activity_gameplay1-1に遷移
            val intent = Intent(this, GameSelection11Activity::class.java)
            startActivity(intent)
        }
        val quitButton: Button = findViewById(R.id.retry_button)
        quitButton.setOnClickListener {
            val intent = Intent(this, GameSelection11Activity::class.java)
            startActivity(intent)
        }
    }
}