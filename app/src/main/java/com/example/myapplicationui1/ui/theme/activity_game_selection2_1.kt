package com.example.myapplicationui1// パッケージ名を適切に変更してください

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class GameSelection21Activity : AppCompatActivity() {

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_selection2_1)

        // あそぶボタンの処理
        val playButton: Button = findViewById(R.id.playButton)
        playButton.setOnClickListener {
            val intent = Intent(this, RuleExplanation21Activity::class.java) // activity_rule_explanation1_1への遷移
            startActivity(intent)
        }

        // コントローラ選択画面へ戻る処理
        val backButton: Button = findViewById(R.id.backButton)
        backButton.setOnClickListener {
            val intent = Intent(this, SpirometerGameSelection::class.java)
            startActivity(intent)
        }
    }
}
