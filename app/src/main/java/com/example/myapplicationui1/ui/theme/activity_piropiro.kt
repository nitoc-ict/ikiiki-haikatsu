package com.example.myapplicationui1

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class PiropiroActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_piropiro)

        // 戻るボタンで activity_explanation に遷移
        val backToMenuButton: Button = findViewById(R.id.backToMenuButton)
        backToMenuButton.setOnClickListener {
            val intent = Intent(this, ExplanationActivity::class.java)
            startActivity(intent)
            finish() // 現在のアクティビティを終了
        }
    }
}