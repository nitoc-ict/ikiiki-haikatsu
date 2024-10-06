package com.example.myapplicationui1

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import android.view.WindowManager.LayoutParams.SCREEN_ORIENTATION_CHANGED
import com.unity3d.player.UnityPlayerActivity
import com.unity3d.player.UnityPlayer

class GamePlay11Activity: UnityPlayerActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gameplay11)

        window.clearFlags(SCREEN_ORIENTATION_CHANGED)
        findViewById<ConstraintLayout>(R.id.unity)?.addView(
            mUnityPlayer, ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
    }
}