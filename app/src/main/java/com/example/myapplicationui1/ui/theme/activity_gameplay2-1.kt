package com.example.myapplicationui1

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import android.view.WindowManager.LayoutParams.SCREEN_ORIENTATION_CHANGED
import com.unity3d.player.UnityPlayerActivity
import com.unity3d.player.UnityPlayer

class GamePlay21Activity: UnityPlayerActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gameplay21)
        try {
            mUnityPlayer = UnityPlayer(this as Activity)
            findViewById<ConstraintLayout>(R.id.unity)?.addView(
                mUnityPlayer, ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            )
            mUnityPlayer.requestFocus()
            window.clearFlags(SCREEN_ORIENTATION_CHANGED)
            UnityPlayer.UnitySendMessage("SceneSelect", "ReceiveMessage", "AppleFarm")
            Log.d("GamePlay21Activity", "はじめるわよ～")
        } catch (e: Exception) {
            Log.d("Error Try method", "${e}")
        }

    }
    private fun returnSelectActivity() {
        mUnityPlayer.onStop()
        Log.d("GamePlay11Activity", "とめたわよ～")
        val intent = Intent(this, ControllerSelectionActivity::class.java)
        startActivity(intent)
    }
}