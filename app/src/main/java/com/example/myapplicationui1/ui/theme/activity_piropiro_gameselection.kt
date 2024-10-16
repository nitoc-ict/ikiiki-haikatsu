package com.example.myapplicationui1

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplicationui1.R

class PiropiroGameSelection : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_piropiro_gameselection)

        val game11selection: Button = findViewById(R.id.game11)
        game11selection.setOnClickListener {
            val intent = Intent(this, GameSelection11Activity::class.java)
            startActivity(intent)
        }

        val game12Selection: Button = findViewById(R.id.game12)
        game12Selection.setOnClickListener {
            val intent = Intent(this, GameSelection12Activity::class.java)
            startActivity(intent)
        }

        val game13Selection: Button = findViewById(R.id.game13)
        game13Selection.setOnClickListener {
            val intent = Intent(this, GameSelection13Activity::class.java)
            startActivity(intent)
        }
    }
}