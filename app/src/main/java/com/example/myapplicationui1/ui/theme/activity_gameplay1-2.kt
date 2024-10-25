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
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import android.view.WindowManager.LayoutParams.SCREEN_ORIENTATION_CHANGED
import android.widget.ActionMenuView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.internal.enableLiveLiterals
import com.example.myapplicationui1.ui.theme.ActivityEnd
import com.unity3d.player.UnityPlayerActivity
import com.unity3d.player.UnityPlayer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.util.UUID
import java.util.concurrent.CountDownLatch

class GamePlay12Activity: UnityPlayerActivity() {
    // TAGs
    private val TAG1 = "BluetoothAdapter"
    private val TAG2 = "BluetoothConnectDevice"
    private val TAG3 = "ReadDatafromBlutooth"
    private val TAG4 = "FaildReadData"
    private val TAG5 = "CloseConnection"

    // About device information
    private val DEVICE_NAME11 = "ESP32_11"
    private val DEVICE_NAME12 = "ESP32_12"

    private var devices = mutableListOf<BluetoothDevice>()
    private val SPP_UUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

    // BluetoothAdapter接続
    private var bluetoothAdapter: BluetoothAdapter? = null
    private var sockets = mutableListOf<BluetoothSocket>()

    // BluetoothValue
    private val REQUEST_ENABLE_BT = 1
    private val REQUEST_PERMISSIONS = 2

    // readDataを管理する変数
    private var isConnected: Boolean = false

    private var stateSendValue: String = "0"

    // ゲームの終了を知らせるフラグ
    private var isfinishGame: Boolean = false

    // CountDownLatch for synchronization
    private val latch = CountDownLatch(1)

    var toastCall: Int = 0

    private val REQUIRED_PERMISSIONS = arrayOf(
        Manifest.permission.BLUETOOTH_CONNECT,
        Manifest.permission.BLUETOOTH_SCAN,
        Manifest.permission.BLUETOOTH
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gameplay12)
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

        } catch (e: Exception) {
            Log.d("Error Try method", "${e}")
        }
    }

    private fun setUpUnity() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                withContext(Dispatchers.IO) {
                    UnityPlayer.UnitySendMessage("SceneSelect", "ReceiveMessage", "Pinpon")
                    latch.await()
                }
                Log.e(TAG1, "Finish async SceneSelect")
                // bluetoothにマイコンが接続されていないとき、ゲームを停止して接続処理をする
                if(bluetoothAdapter == null) {
                    Log.e(TAG1, "Micon is not connecting")
                    UnityPlayer.UnitySendMessage("PinponGameStateManager", "PauseGame", "")
                    reconnectToDevice()
                } else {
                    Log.d(TAG1, "Connected micon")
                    expressionToast("マイコンがBluetooth接続されていません")
                }
            } catch (e: Exception){
                Log.d(TAG1, "Error is: ", e)
            }
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

        Log.d("GamePlay12Activity", "とめたわよ～")
        val intent = Intent(this, ActivityEnd::class.java)
        // Endに送信する値を設定
        val passValue = "12"
        intent.putExtra("PASS_KEY", passValue)
        startActivity(intent)
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

    private fun connectToDevice() {
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.BLUETOOTH_CONNECT
            ) == PackageManager.PERMISSION_GRANTED) {
            try {
                Log.d(TAG1, "Permission Available 04")
                val pairedDevices: Set<BluetoothDevice>? = bluetoothAdapter?.bondedDevices
                Log.d(TAG1, "Enable To Use devices: ${bluetoothAdapter?.bondedDevices}")
                // 取得したデバイスをListに格納
                if(pairedDevices != null) {
                    pairedDevices?.forEach { device ->
                        Log.d(TAG1, "device is: $device")
                        when(device.name) {
                            DEVICE_NAME11 -> {
                                Log.d(TAG1, "01Device is ${device}")
                                devices.add(device)
                            }
                            DEVICE_NAME12 -> {
                                Log.d(TAG1, "02Device is ${device}")
                                devices.add(device)
                            }
                        }
                    }
                } else {
                    UnityPlayer.UnitySendMessage("PinponGameStateManager", "PauseGame", "")
                    reconnectToDevice()
                }

                if(devices != null) {
                    // 取得したDeviceすべてをsocketに接続
                    devices.forEach { device ->
                        try {
                            Log.d(TAG1, "DeviceName is: $device")
                            if(device != null) {
                                Log.d(TAG1, "connect socket of device")
                                val socket = device.createRfcommSocketToServiceRecord(SPP_UUID)
                                socket.connect()
                                sockets.add(socket)
                                Log.d(TAG1, "01connected socket of device")
                            }
                        } catch (e: Exception) {
                            Log.e(TAG1, "miss GetSocket: ${e.message}")
                        }
                    }
                    UnityPlayer.UnitySendMessage("PinponGameStateManager", "ResumeGame", "")
                    isConnected = true
                    readData()
                } else {
                    UnityPlayer.UnitySendMessage("PinponGameStateManager", "PauseGame", "")
                    reconnectToDevice()
                }
            } catch (e: Exception) {
                Log.e(TAG1, "connected out: ${e.message}")
                isConnected = false
                reconnectToDevice()
            }
        }
    }

    private fun readData() {
        Log.d(TAG1, "ReadData")
        // マイコン毎にデータを送信
        sockets.forEach { socket->
            CoroutineScope(Dispatchers.IO).launch {
                Log.d(TAG1, "in Couroutine scope")
                val inputStream: InputStream = socket.inputStream
                val buffer = ByteArray(4)
                Log.d(TAG1, "MyIconName: $socket")

                while(isConnected) {
                    try {
                        delay(700)
                        val bytes = inputStream.read(buffer) ?: 0
                        if(bytes > 0) {
                            var incomingData = String(buffer, 0, bytes)
                            Log.d(TAG3, "Rechieved: ${incomingData}")
                            if (bytes != null) {
                                stateSendValue = incomingData
                            } else {
                                incomingData = stateSendValue
                                Log.e(TAG1, "bytes == null")
                            }
                            delay(300)
                            val deviceName = getDeviceName(socket)
                            sendData(deviceName ?: "UnknownDevices", incomingData)
                        }
                    } catch (e: Exception) {
                        Log.e(TAG4, "Read failed: ${e.message}")

                        // 接続停止フラグを起動
                        isConnected = false

                        // PauseのメッセージをUnityに送信
                        UnityPlayer.UnitySendMessage("PinponGameStateManager", "PauseGame", "")
                        reconnectToDevice()
                    }
                }
            }
        }
    }

    private fun getDeviceName(socket: BluetoothSocket): String? {
        return try {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED) {
                socket.remoteDevice.name
            } else {
                null
            }
        } catch (e: SecurityException) {
            Log.e(TAG1, "Permission denied to get device name", e)
            null
        }
    }

    private fun sendData(deviceName: String, data: String) {
        val sendData = data + "," +  deviceName.last() // コントローラ名の末尾でユーザIndexを認識
        if(sendData != null) {
            UnityPlayer.UnitySendMessage("PinponSystemManager", "ReceiveMessage", "${sendData}")
        }
    }

    private val bluetoothReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            CoroutineScope(Dispatchers.IO).launch {
                val action = intent?.action
                delay(1500)
                when(action) {
                    BluetoothDevice.ACTION_ACL_CONNECTED -> {
                        Log.d(TAG1, "Bluetoothに再接続しました")
                    }
                    BluetoothDevice.ACTION_ACL_DISCONNECTED -> {
                        Log.d(TAG1, "Bluetoothが切断されました")
                        UnityPlayer.UnitySendMessage("PinponGameStateManager", "PauseGame", "")
                        reconnectToDevice()
                    }
                }
            }
        }
    }

    private fun reconnectToDevice() {
        if (isfinishGame == false) {
            CoroutineScope(Dispatchers.IO).launch {
                expressionToast("Bluetoothに接続しています...")
                closeConnection()
                devices.clear()
                delay(1000)
                initializedBluetooth()
            }
        }
    }

    private fun expressionToast(text: String, duration: Int = Toast.LENGTH_SHORT) {
        if(toastCall <= 3) {
            runOnUiThread {
                Toast.makeText(this@GamePlay12Activity, "${text}", Toast.LENGTH_SHORT).show()
                Log.d("TOAST", "ToastText is $text")
            }
            toastCall = toastCall + 1
            Handler(Looper.getMainLooper()).postDelayed({
                toastCall--
            }, if (duration == Toast.LENGTH_SHORT) 2500L else 3000L) // 目安の表示時間（ショート: 2秒、ロング: 3.5秒）
        } else {
            Log.e("TOAST_ERROR", "Toastの呼び出し数が基準を超えました。Toastを表示できません。")
        }
    }

    private fun closeConnection() {
        // deviceすべてのSocketを停止
        try {
            sockets.forEach { socket ->
                Log.d(TAG5, "Connection closed $socket")
                socket.close()
                Log.d(TAG5, "Connection closed")
            }
        } catch (e: Exception) {
            Log.e(TAG5, "Error string connection: ${e.message}")
        }
    }
}

