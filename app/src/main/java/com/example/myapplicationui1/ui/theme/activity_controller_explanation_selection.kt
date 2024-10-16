package com.example.myapplicationui1

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplicationui1.R

class ControllerExplanationSelectionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_controller_explanation_selection)

        // activity_spiro_meter に遷移
        val spirometer: Button = findViewById(R.id.spirometer)
        spirometer.setOnClickListener {
            val intent = Intent(this, SpirometerActivity::class.java)
            startActivity(intent)
        }

        // activity_spiro_meter に遷移
        val spirometerSelection: Button = findViewById(R.id.spirometerselection)
        spirometerSelection.setOnClickListener {
            val intent = Intent(this, SpirometerGameSelection::class.java)
            startActivity(intent)
        }

        // activity_piropiro に遷移
        val piropiro: Button = findViewById(R.id.piropiro)
        piropiro.setOnClickListener {
            val intent = Intent(this, PiropiroActivity::class.java)
            startActivity(intent)
        }

        val piropiroSelect: Button = findViewById(R.id.piropiroselection)
        piropiroSelect.setOnClickListener {
            val intent = Intent(this, PiropiroGameSelection::class.java)
            startActivity(intent)
        }
    }
}