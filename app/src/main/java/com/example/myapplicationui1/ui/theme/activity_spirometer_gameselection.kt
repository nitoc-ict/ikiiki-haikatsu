package com.example.myapplicationui1

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplicationui1.R

class SpirometerGameSelection : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_spirometer_gameselection)

        val game11selection: Button = findViewById(R.id.game21)
        game11selection.setOnClickListener {
            val intent = Intent(this, GameSelection21Activity::class.java)
            startActivity(intent)
        }

        val game12Selection: Button = findViewById(R.id.game22)
        game12Selection.setOnClickListener {
            val intent = Intent(this, GameSelection22Activity::class.java)
            startActivity(intent)
        }

        val game13Selection: Button = findViewById(R.id.game23)
        game13Selection.setOnClickListener {
            val intent = Intent(this, GameSelection23Activity::class.java)
            startActivity(intent)
        }
    }
}