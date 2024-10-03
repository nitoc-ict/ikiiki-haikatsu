package com.example.myapplicationui1 // パッケージ名を適切に変更してください

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class GameVideo12Activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gamevideo12)

        // ゲームへボタンの処理
        val backToMenuButton: Button = findViewById(R.id.backToMenuButton)
        backToMenuButton.setOnClickListener {
            // activity_gameplay1-2に遷移
            val intent = Intent(this, GamePlay12Activity::class.java)
            startActivity(intent)
        }
    }
}
