package com.example.myapplicationui1

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts

class ExplanationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_explanation)

        val yesButton: Button = findViewById(R.id.yes_button)
        val noButton: Button = findViewById(R.id.no_button)

        // 「はい」ボタンがクリックされた時、activity_controller_explanation_selection へ遷移
        yesButton.setOnClickListener {
            val intent = Intent(this, ControllerExplanationSelectionActivity::class.java)
            startActivity(intent)
        }

        // 「いいえ」ボタンがクリックされた時、activity_controller_selection へ遷移
        noButton.setOnClickListener {
            val intent = Intent(this, ControllerSelectionActivity::class.java)
            startActivity(intent)
        }
    }
}
