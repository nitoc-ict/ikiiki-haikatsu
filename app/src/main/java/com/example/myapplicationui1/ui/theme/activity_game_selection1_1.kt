package com.example.yourappname // パッケージ名を適切に変更してください

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity

class GameSelection1_1Activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_selection1_1)

        // 左のボタンを押したときの遷移
        val leftButton: ImageButton = findViewById(R.id.leftButton)
        leftButton.setOnClickListener {
            val intent = Intent(this, GameSelection1_3Activity::class.java) // activity_game_selection1_3への遷移
            startActivity(intent)
        }

        // 右のボタンを押したときの遷移
        val rightButton: ImageButton = findViewById(R.id.rightButton)
        rightButton.setOnClickListener {
            val intent = Intent(this, GameSelection1_2Activity::class.java) // activity_game_selection1_2への遷移
            startActivity(intent)
        }

        // あそぶボタンの処理（ルール説明へ遷移）
        val ruleButton: Button = findViewById(R.id.ruleButton) // ルール説明へ遷移するボタンのIDを適切に設定
        ruleButton.setOnClickListener {
            val intent = Intent(this, activity_rule_explanation1_1::class.java)
            startActivity(intent)        }
    }
}
