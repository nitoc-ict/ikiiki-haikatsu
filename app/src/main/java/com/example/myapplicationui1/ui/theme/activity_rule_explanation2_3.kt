package com.example.myapplicationui1 // パッケージ名を適切に変更してください

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class RuleExplanation23Activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rule_explanation2_3)

        // はいボタンの処理（ゲーム動画へ遷移）
        val yesButton: Button = findViewById(R.id.yesButton)
        yesButton.setOnClickListener {
            val intent = Intent(this, GameVideo23Activity::class.java)
            startActivity(intent)
        }

        // いいえボタンの処理（ゲームプレイ2-3へ遷移）
        val noButton: Button = findViewById(R.id.noButton)
        noButton.setOnClickListener {
            val intent = Intent(this, HowManyControllerSelection::class.java)
            val passValue = "23"
            intent.putExtra("GAMENUMBER", passValue)
            startActivity(intent)
        }
    }
}
