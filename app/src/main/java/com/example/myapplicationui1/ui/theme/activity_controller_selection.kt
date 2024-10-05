package com.example.myapplicationui1 // パッケージ名を適切に変更してください

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class ControllerSelectionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_controller_selection)

        // 戻るボタンの設定
        val backButton: ImageView = findViewById(R.id.backButton)
        backButton.setOnClickListener {
            // 前のアクティビティに戻る処理
            val intent = Intent(this, ExplanationActivity::class.java)
            startActivity(intent)
        }

        // スパイロメータのアイコンを押したときの遷移
        val spiroMeterIcon: ImageView = findViewById(R.id.spiroMeterIcon)
        spiroMeterIcon.setOnClickListener {
            val intent = Intent(this, GameSelection21Activity::class.java) // activity_game_selection2_1への遷移
            startActivity(intent)
        }

        // ピロピロのアイコンを押したときの遷移
        val piropiroIcon: ImageView = findViewById(R.id.piropiroIcon)
        piropiroIcon.setOnClickListener {
            val intent = Intent(this, GameSelection11Activity::class.java) // activity_game_selection1_1への遷移
            startActivity(intent)
        }
    }
}
