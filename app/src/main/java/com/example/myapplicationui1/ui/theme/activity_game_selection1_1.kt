package com.example.myapplicationui1 // パッケージ名を適切に変更してください

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class GameSelection11Activity : AppCompatActivity() {

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_selection1_1)

        // あそぶボタンの処理（ルール説明へ遷移）
        val playButton: Button = findViewById(R.id.playButton) // ルール説明へ遷移するボタンのIDを適切に設定
        playButton.setOnClickListener {
            val intent = Intent(this, RuleExplanation11Activity::class.java)
            startActivity(intent)        }

        // コントローラ選択画面へ戻る処理
        val backButton: Button = findViewById(R.id.backButton)
        backButton.setOnClickListener {
            val intent = Intent(this, PiropiroGameSelection::class.java)
            startActivity(intent)
        }
    }
}
