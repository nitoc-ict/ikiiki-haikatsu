package com.example.myapplicationui1

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.view.WindowManager
import androidx.constraintlayout.widget.ConstraintLayout
import android.view.WindowManager.LayoutParams.SCREEN_ORIENTATION_CHANGED
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.myapplicationui1.ui.theme.ActivityEnd
import com.unity3d.player.UnityPlayerActivity
import com.unity3d.player.UnityPlayer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.UUID
import java.util.concurrent.CountDownLatch

class GamePlay11Activity: UnityPlayerActivity() {
    // TAGs
    private val TAG1 = "BluetoothAdapter"
    private val TAG2 = "BluetoothConnectDevice"
    private val TAG3 = "ReadDatafromBlutooth"
    private val TAG4 = "FaildReadData"
    private val TAG5 = "CloseConnection"

    // About device information
    private val DEVICE_NAME = "ESP32_11"
    private val SPP_UUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

    // BluetoothSettingsValue
    private var inputStream: InputStream? = null
    private var outputStream: OutputStream? = null

    // BluetoothAdapter接続
    private var bluetoothAdapter: BluetoothAdapter? = null
    private var bluetoothSocket: BluetoothSocket? = null

    // BluetoothValue
    private val REQUEST_ENABLE_BT = 1
    private val REQUEST_PERMISSIONS = 2

    // readDataを管理する変数
    private var isConnected: Boolean = false

    private var stateSendValue: String = "0"

    //
    private var isfinishGame: Boolean = false

    // CountDownLatch for synchronization
    private val latch = CountDownLatch(1)

    private var toastCall: Int = 0

    private val REQUIRED_PERMISSIONS = arrayOf(
        Manifest.permission.BLUETOOTH_CONNECT,
        Manifest.permission.BLUETOOTH_SCAN,
        Manifest.permission.BLUETOOTH
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gameplay11)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        try {
            mUnityPlayer = UnityPlayer(this as Activity)
            findViewById<ConstraintLayout>(R.id.unity)?.addView(
                mUnityPlayer, ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            )

            // Broadcastを定義
            val filter = IntentFilter().apply {
                addAction(BluetoothDevice.ACTION_ACL_CONNECTED)
                addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED)
            }
            registerReceiver(bluetoothReceiver, filter)

            mUnityPlayer.requestFocus()
            window.clearFlags(SCREEN_ORIENTATION_CHANGED)
            setUpUnity()

            initializedBluetooth()

        } catch (e: Exception) {
            Log.d("Error Try method", "${e}")
        }
    }

    private fun checkBluetoothPermissions(): Boolean {
        return REQUIRED_PERMISSIONS.all { permission ->
            ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun initializedBluetooth() {
        try {
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
            when {
                bluetoothAdapter == null -> {
                    expressionToast("Bluetoothをサポートしていません")
                    return
                }
                !bluetoothAdapter!!.isEnabled -> {
                    try {
                        val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
                    } catch (e: SecurityException) {
                        Log.e(TAG1, "Failed to Bluetooth ${e.message}")
                        handlePermissionDenied()
                    }
                }
                else -> {
                    checkAndRequestPermissions()
                }
            }
        } catch (e: SecurityException) {
            Log.e(TAG1, "Security error in initializeed Bluetooth: ${e.message}")
        } catch (e: Exception) {
            Log.e(TAG1, "Error in initializeBluetooth: ${e.message}")
            handleError(e)
        }
    }

    private fun handleError(e: Exception) {
        Log.e(TAG1, "Error occurred: ${e.message}")
        runOnUiThread {
            expressionToast("エラーが発生しました")
        }
    }

    private fun checkAndRequestPermissions() {
        val missingPermissions = REQUIRED_PERMISSIONS.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        when {
            missingPermissions.isEmpty() -> {
                // 全ての権限が許可されている
                connectToDevice()
            }
            missingPermissions.any { permission ->
                ActivityCompat.shouldShowRequestPermissionRationale(this, permission)
            } -> {
                // 権限が必要な理由を説明
                showPermissionRationaleDialog(missingPermissions.toTypedArray())
            }
            else -> {
                // 権限をリクエスト
                ActivityCompat.requestPermissions(
                    this,
                    missingPermissions.toTypedArray(),
                    REQUEST_PERMISSIONS
                )
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_ENABLE_BT -> {
                if (resultCode == Activity.RESULT_OK) {
                    checkAndRequestPermissions()
                } else {
                    expressionToast("Bluetoothを有効にしてください")
                    reconnectToDevice()
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_PERMISSIONS -> {
                if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                    // 全ての権限が許可された
                    connectToDevice()
                } else {
                    // 一部またはすべての権限が拒否された
                    handlePermissionDenied()
                }
            }
        }
    }

    private fun showPermissionRationaleDialog(permissions: Array<String>) {
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("権限が必要です")
            .setMessage("Bluetoothデバイスに接続するために権限が必要です。")
            .setPositiveButton("許可する") { _, _ ->
                ActivityCompat.requestPermissions(
                    this,
                    permissions,
                    REQUEST_PERMISSIONS
                )
            }
            .setNegativeButton("キャンセル") { _, _ ->
                expressionToast("Bluetooth機能を使用するには権限が必要です")
                handlePermissionDenied()
            }
            .show()
    }

    private fun setUpUnity() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                withContext(Dispatchers.IO) {
                    UnityPlayer.UnitySendMessage("SceneSelect", "ReceiveMessage", "AppleFarm")
                    UnityPlayer.UnitySendMessage("AppleGameStateManager", "PauseGame", "")
                    latch.await()
                }
                Log.e(TAG1, "Finish async SceneSelect")
                // bluetoothにマイコンが接続されていないとき、ゲームを停止して接続処理をする
                if(bluetoothAdapter == null) {
                    Log.e(TAG1, "Micon is not connecting")
                    UnityPlayer.UnitySendMessage("AppleGameStateManager", "PauseGame", "")
                    expressionToast("Bluetooth接続が許可されていません")
                    reconnectToDevice()
                } else {
                    Log.d(TAG1, "Connected micon")
                }
            } catch (e: Exception){
                Log.d(TAG1, "Error is: ", e)
            }
            Log.d(TAG1, "はじめるわよ～")
        }
    }

    // Unityで呼ぶ、latchの処理を進めるコールバック関数
    fun onUnityMessageReceived() {
        latch.countDown()
    }

    // Unity側で呼ぶ、別画面へ遷移する関数
    private fun returnSelectActivity() {
        mUnityPlayer.onStop()
        closeConnection()

        isfinishGame = true
        unregisterReceiver(bluetoothReceiver)

        Log.d("GamePlay11Activity", "とめたわよ～")
        val intent = Intent(this, ActivityEnd::class.java)
        // Endに送信する値を設定
        val passValue = "11"
        intent.putExtra("PASS_KEY", passValue)
        startActivity(intent)
    }

    private fun handlePermissionDenied() {
        // 永続的に権限が拒否されたかチェック
        val permanentlyDenied = REQUIRED_PERMISSIONS.any { permission ->
            !ActivityCompat.shouldShowRequestPermissionRationale(this, permission)
        }
        if (permanentlyDenied) {
            showSettingsDialog()
        } else {
            expressionToast("Bluetooth機能を使用するには権限が必要です")
            reconnectToDevice()
        }
    }

    private fun showSettingsDialog() {
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("権限が必要です")
            .setMessage("設定画面から権限を許可してください")
            .setPositiveButton("設定を開く") { _, _ ->
                val intent = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = android.net.Uri.fromParts("package", packageName, null)
                }
                startActivity(intent)
            }
            .setNegativeButton("キャンセル") { _, _ ->
                expressionToast("Bluetooth機能を使用するには権限が必要です")
                reconnectToDevice()
            }
            .show()
    }

    private fun reconnectToDevice() {
        Log.d(TAG1, "reconnect to Device")
        if (!isfinishGame) {
            CoroutineScope(Dispatchers.IO).launch {
                UnityPlayer.UnitySendMessage("AppleGameStateManager", "PauseGame", "")
                expressionToast("Bluetoothに接続しています...")
                delay(2000)
                Log.d(TAG1, "Now Connecting...")
                initializedBluetooth()
            }
        }
    }

    private fun connectToDevice() {
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.BLUETOOTH_CONNECT
            ) == PackageManager.PERMISSION_GRANTED) {
            try {
                Log.d(TAG1, "Permission Available 04")
                val pairedDevices: Set<BluetoothDevice>? = bluetoothAdapter?.bondedDevices
                // ペアリングしているデバイスと接続
                val device = pairedDevices?.find { it.name == DEVICE_NAME }
                Log.d(TAG1, "Device is ${device}")

                if(device != null) {
                    Log.d(TAG1, "Permission Available 05")
                    CoroutineScope(Dispatchers.IO).launch {
                        var retryCount = 0
                        val maxRetries = 3
                        val duration = 5000L

                        while (retryCount < maxRetries) {
                            try {
                                Log.d(TAG1, "Attempting to connect, try #$retryCount")
                                withTimeout(duration) {
                                    bluetoothSocket = device.createRfcommSocketToServiceRecord(SPP_UUID)
                                    Log.d(TAG1, "Permission Available 06")
                                    Log.i(TAG2, "BluetoothSocket is:$bluetoothSocket")
                                    bluetoothSocket?.connect()
                                    Log.d(TAG1, "Permission Available 07")
                                }
                                inputStream = bluetoothSocket?.inputStream
                                Log.d(TAG1, "Permission Available 08")
                                outputStream = bluetoothSocket?.outputStream
                                Log.d(TAG1, "Permission Available 09")
                                Log.d(TAG2, "Connected to $DEVICE_NAME")
                                UnityPlayer.UnitySendMessage("WankosobaGameStateManager", "ResumeGame", "")
                                isConnected = true
                                readData()
                                break // 接続が成功した場合、ループを終了
                            } catch (e: IOException) {
                                Log.e(TAG5, "Connection failed due to IOException: ${e.message}")
                                retryCount++
                                if (retryCount >= maxRetries) {
                                    Log.e(TAG5, "Max retries reached. Could not connect.")
                                    reconnectToDevice()
                                }
                            } catch (e: Exception) {
                                Log.e(TAG5, "Connection failed due to Exception: ${e.message}")
                                reconnectToDevice()
                                break // 非IO例外の場合、ループを終了
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                isConnected = false
                reconnectToDevice()
            }
        }
    }

    private suspend fun readData() {
        Log.d(TAG1, "Permission Available 10")
        try {
            val buffer = ByteArray(1024)
            while (isConnected) {
                try {
                    val bytes = inputStream?.read(buffer) ?: 0
                    if (bytes > 0) {
                        var incomingData = String(buffer, 0, bytes)
                        Log.d(TAG3, "Rechieved: $incomingData")
                        if (bytes != null) {
                            stateSendValue = incomingData
                            Log.d(TAG1, "incomingData is not Null")
                        } else {
                            incomingData = stateSendValue
                        }
                        delay(300)
                        UnityPlayer.UnitySendMessage("AppleBlocker", "ReceiveMessage", "${incomingData.first()}")
                        Log.e(TAG1, "First riteral is ${incomingData.first()}")
                    }
                } catch (e: Exception) {
                    Log.e(TAG4, "値読み取りエラー: ${e.message}")
                    isConnected = false
                    UnityPlayer.UnitySendMessage("AppleGameStateManager", "PauseGame", "")
                    reconnectToDevice()
                }
            }
        } catch (e: SecurityException) {
            Log.e("SecutiryExcception", "SercurityExceotion is ${e.message}")
        }
    }

    private val bluetoothReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val action = intent?.action
            when(action) {
                BluetoothDevice.ACTION_ACL_CONNECTED -> {
                    Log.d(TAG1, "Bluetoothに再接続しました")
                }
                BluetoothDevice.ACTION_ACL_DISCONNECTED -> {
                    Log.d(TAG1, "Bluetoothが切断されました")
                    UnityPlayer.UnitySendMessage("AppleGameStateManager", "PauseGame", "")
                    reconnectToDevice()
                }
            }
        }
    }

    private fun expressionToast(text: String) {
        if(toastCall <= 3) {
            runOnUiThread {
                Toast.makeText(this@GamePlay11Activity, "${text}", Toast.LENGTH_SHORT).show()
                Log.d("TOAST", "ToastText is $text")
            }
            toastCall = toastCall + 1
        } else {
            Log.e("TOAST_ERROR", "Toastの呼び出し数が基準を超えました。Toastを表示できません。")
        }
    }

    private fun closeConnection() {
        try {
            inputStream?.close()
            outputStream?.close()
            bluetoothSocket?.close()
            Log.d(TAG5, "Connection closed")
        } catch (e: Exception) {
            Log.e(TAG5, "Error string connection: ${e.message}")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(bluetoothReceiver)
        closeConnection()
    }
}