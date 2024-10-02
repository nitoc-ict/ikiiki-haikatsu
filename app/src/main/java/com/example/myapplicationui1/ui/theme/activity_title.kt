package com.example.mygame

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_title)

        // 「はじめる」ボタンを取得
        val startButton = findViewById<Button>(R.id.start_button)
        startButton.setOnClickListener {
            // ボタンが押されたとき、activity_explanationへ遷移
            val intent = Intent(this, activity_explanation::class.java)
            startActivity(intent)
        }
    }
}
