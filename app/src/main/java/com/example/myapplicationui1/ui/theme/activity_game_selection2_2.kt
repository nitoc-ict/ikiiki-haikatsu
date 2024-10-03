package com.example.myapplicationui1 // パッケージ名を適切に変更してください

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity

class GameSelection22Activity : AppCompatActivity() {

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
    }
}
