package com.example.myapplicationui1 // パッケージ名を適切に変更してください

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.MediaController
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity

class GameVideo11Activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gamevideo11)

        // VideoViewの参照を取得
        val videoView: VideoView = findViewById(R.id.videoView)

        // 動画のパスまたはURLを指定
        val videoUri: Uri = Uri.parse("android.resource://" + packageName + "/" + R.raw.ringo)

        // VideoViewに動画をセット
        videoView.setVideoURI(videoUri)

        // MediaControllerを設定して再生/停止ボタンを追加
        val mediaController = MediaController(this)
        videoView.setMediaController(mediaController)
        mediaController.setAnchorView(videoView)

        // 動画を再生
        videoView.start()

        // ゲームへボタンの処理
        val backToMenuButton: Button = findViewById(R.id.backToMenuButton)
        backToMenuButton.setOnClickListener {
            // activity_gameplay1-1に遷移
            val intent = Intent(this, GamePlay11Activity::class.java)
            startActivity(intent)
        }
    }
}