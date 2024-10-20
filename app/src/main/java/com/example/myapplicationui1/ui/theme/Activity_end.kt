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
import com.example.myapplicationui1.GameSelection12Activity
import com.example.myapplicationui1.GameSelection13Activity
import com.example.myapplicationui1.GameSelection21Activity
import com.example.myapplicationui1.GameSelection22Activity
import com.example.myapplicationui1.GameSelection23Activity
import com.example.myapplicationui1.PiropiroGameSelection
import com.example.myapplicationui1.R
import com.example.myapplicationui1.SpirometerGameSelection

class ActivityEnd : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_end)

        val retryButton: Button = findViewById(R.id.retry_button)
        val text = intent.getStringExtra("PASS_KEY")
        retryButton.setOnClickListener {
            when(text) {
                "11" -> {
                    val intent = Intent(this, GameSelection11Activity::class.java)
                    startActivity(intent)
                }
                "12" -> {
                    val intent = Intent(this, GameSelection12Activity::class.java)
                    startActivity(intent)
                }
                "13" -> {
                    val intent = Intent(this, GameSelection13Activity::class.java)
                    startActivity(intent)
                }
                "21" -> {
                    val intent = Intent(this, GameSelection21Activity::class.java)
                    startActivity(intent)
                }
                "22" -> {
                    val intent = Intent(this, GameSelection22Activity::class.java)
                    startActivity(intent)
                }
                "23" -> {
                    val intent = Intent(this, GameSelection23Activity::class.java)
                    startActivity(intent)
                }
        }
        val quitButton: Button = findViewById(R.id.retry_button)
        quitButton.setOnClickListener {
            try {
                val firstchar: Char? = text?.firstOrNull()
                when (firstchar) {
                    '1' -> {
                        val intent = Intent(this, PiropiroGameSelection::class.java)
                        startActivity(intent)
                    }

                    '2' -> {
                        val intent = Intent(this, SpirometerGameSelection::class.java)
                        startActivity(intent)
                    }
                }
            } catch (e: Exception) {
                Log.d("WARN_GET_VALUE", "Error is: : ${e.message}")
            }
        }
        }
    }
}