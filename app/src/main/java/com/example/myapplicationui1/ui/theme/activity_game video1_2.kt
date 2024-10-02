package com.example.yourappname // パッケージ名を適切に変更してください

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class GameVideo1_2Activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_video1_2)

        // ゲームへボタンの処理
        val backToMenuButton: Button = findViewById(R.id.backToMenuButton)
        backToMenuButton.setOnClickListener {
            // activity_gameplay1-2に遷移
            val intent = Intent(this, GamePlay1_2Activity::class.java)
            startActivity(intent)
        }
    }
}
