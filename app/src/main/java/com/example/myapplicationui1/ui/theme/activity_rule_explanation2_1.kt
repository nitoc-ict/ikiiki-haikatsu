package com.example.myapplicationui1 // パッケージ名を適切に変更してください

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class RuleExplanation21Activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rule_explanation2_1)

        // はいボタンの処理（ゲーム動画へ遷移）
        val yesButton: Button = findViewById(R.id.yesButton)
        yesButton.setOnClickListener {
            val intent = Intent(this, GameVideo21Activity::class.java)
            startActivity(intent)
        }

        // いいえボタンの処理（ゲームプレイ2-1へ遷移）
        val noButton: Button = findViewById(R.id.noButton)
        noButton.setOnClickListener {
            val intent = Intent(this, GamePlay21Activity::class.java)
            startActivity(intent)
        }
    }
}
