package com.example.myapplicationui1 // パッケージ名を適切に変更してください

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class GameSelection22Activity : AppCompatActivity() {

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_selection2_2)

        // 左のボタンを押したときの遷移
        val leftButton: ImageButton = findViewById(R.id.leftButton)
        leftButton.setOnClickListener {
            val intent = Intent(this, GameSelection21Activity::class.java)
            startActivity(intent)
        }

        // 右のボタンを押したときの遷移
        val rightButton: ImageButton = findViewById(R.id.rightButton)
        rightButton.setOnClickListener {
            val intent = Intent(this, GameSelection23Activity::class.java)
            startActivity(intent)
        }

        // あそぶボタンの処理
        val playButton: Button = findViewById(R.id.playButton)
        playButton.setOnClickListener {
            val intent = Intent(this, RuleExplanation22Activity::class.java) // activity_rule_explanation2_1への遷移
            startActivity(intent)
        }

        // コントローラ選択画面へ戻る処理
        val backButton: ImageView = findViewById(R.id.backButton)
        backButton.setOnClickListener {
            val intent = Intent(this, ControllerSelectionActivity::class.java)
        }
    }
}
