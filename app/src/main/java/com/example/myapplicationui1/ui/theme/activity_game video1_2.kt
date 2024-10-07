package com.example.myapplicationui1 // パッケージ名を適切に変更してください

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.MediaController
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.Toast
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity

class GameVideo12Activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gamevideo11)

        // VideoViewの参照を取得
        val videoView = findViewById<VideoView>(R.id.videoView)

        // 動画のパスを取得
        val packageName = "android.resource://" + this.packageName + "/" + R.raw.pinpon

        // 動画のパスまたはURLを指定
        val videoUri = Uri.parse(packageName)

        // VideoViewに動画をセット
        videoView.setVideoURI(videoUri)

        // MediaControllerを設定して再生/停止ボタンを追加
        val mediaController = MediaController(this)
        // メディアコントローラのレイアウトを指定
        val layoutParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.WRAP_CONTENT,
            Gravity.CENTER_HORIZONTAL
        )
        mediaController.layoutParams = layoutParams

        videoView.setMediaController(mediaController)
        mediaController.setAnchorView(videoView)

        if(videoUri == null) {
            Log.d("VideoView", "videoUri is null")
        }

        videoView.setOnPreparedListener {
            Log.d("VideoView", "再生する")
            videoView.start()
        }

        videoView.setOnErrorListener {
                mp, what, extra ->
            Log.d("VideoView", "エラー what: $what, mp: $mp, extra: $extra")
            true
        }


        // ゲームへボタンの処理
        val backToMenuButton: Button = findViewById(R.id.backToMenuButton)
        backToMenuButton.setOnClickListener {
            // activity_gameplay1-1に遷移
            val intent = Intent(this, GamePlay12Activity::class.java)
            startActivity(intent)
        }
    }
}
