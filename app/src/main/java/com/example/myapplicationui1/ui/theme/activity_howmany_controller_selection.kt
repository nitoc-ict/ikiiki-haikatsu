package com.example.myapplicationui1

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button

class HowManyControllerSelection : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        var passValue = "3"
        val text = intent.getStringExtra("GAMENUMBER")
        val nextClass = when(text) {
            "13" -> GamePlay13Activity::class.java
            "23" -> GamePlay23Activity::class.java
            else -> GamePlay13Activity::class.java
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_howmany_controller_selection)

        val threeButton: Button = findViewById(R.id.three_button)
        threeButton.setOnClickListener {
            var intent = Intent(this, nextClass)
            passValue = "3"
            intent.putExtra("PLAYERNUM", passValue)
            startActivity(intent)
        }

        val fourButton: Button = findViewById(R.id.four_button)
        fourButton.setOnClickListener {
            val intent = Intent(this, nextClass)
            passValue = "4"
            intent.putExtra("PLAYERNUM", passValue)
            startActivity(intent)
        }
    }
}