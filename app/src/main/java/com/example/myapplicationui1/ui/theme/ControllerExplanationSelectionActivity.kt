package com.example.myapplicationui1

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplicationui1.R

class ControllerExplanationSelectionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_controller_explanation_selection)

        // スパイロメータのアイコンをタップで activity_spiro_meter に遷移
        val spiroMeterIcon: ImageView = findViewById(R.id.spiroMeterIcon)
        spiroMeterIcon.setOnClickListener {
            val intent = Intent(this, SpiroMeterActivity::class.java)
            startActivity(intent)
        }

        // ピロピロ笛のアイコンをタップで activity_piropiro に遷移
        val piropiroIcon: ImageView = findViewById(R.id.piropiroIcon)
        piropiroIcon.setOnClickListener {
            val intent = Intent(this, PiropiroActivity::class.java)
            startActivity(intent)
        }
    }
}
