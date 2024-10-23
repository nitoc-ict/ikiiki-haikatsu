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
import androidx.constraintlayout.widget.ConstraintLayout
import android.view.WindowManager.LayoutParams.SCREEN_ORIENTATION_CHANGED
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AppCompatActivity
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

class GamePlay22Activity: UnityPlayerActivity() {
    // TAGs
    private val TAG1 = "BluetoothAdapter"
    private val TAG2 = "BluetoothConnectDevice"
    private val TAG3 = "ReadDatafromBlutooth"
    private val TAG4 = "FaildReadData"
    private val TAG5 = "CloseConnection"

    // About device information
    private val DEVICE_NAME21 = "ESP32_21"
    private val DEVICE_NAME22 = "ESP32_22"

    private var devices = mutableListOf<BluetoothDevice>()
    private val SPP_UUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

    // BluetoothAdapter接続
    private var bluetoothAdapter: BluetoothAdapter? = null
    private var sockets = mutableListOf<BluetoothSocket>()

    // BluetoothValue
    private var PERMISSION_BLUETOOTH_CONNECT_CODE = (1)
    private var BT_ONOFF_CONF = (3)

    // readDataを管理する変数
    private var isConnected: Boolean = false

    private var stateSendValue: String = "0"

    // ゲームの終了を知らせるフラグ
    private var isfinishGame: Boolean = false

    // CountDownLatch for synchronization
    private val latch = CountDownLatch(1)

    companion object {
        private const val REQUEST_CODE_BLUETOOTH_CONNECT = 1
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if(requestCode == REQUEST_CODE_BLUETOOTH_CONNECT) {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                connectToDevice()
            } else {
                Log.e(TAG1, "Required permission not granted")
                finish()
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gameplay22)
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
                    UnityPlayer.UnitySendMessage("SceneSelect", "ReceiveMessage", "Kinko")
                    latch.await()
                }
                Log.e(TAG1, "Finish async SceneSelect")
                // bluetoothにマイコンが接続されていないとき、ゲームを停止して接続処理をする
                if(bluetoothAdapter == null) {
                    Log.e(TAG1, "Micon is not connecting")
                    UnityPlayer.UnitySendMessage("KinkoGameStateManager", "PauseGame", "")
                    reconnectToDevice()
                } else {
                    Log.d(TAG1, "Connected micon")
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

        Log.d("GamePlay22Activity", "とめたわよ～")
        val intent = Intent(this, ActivityEnd::class.java)
        // Endに送信する値を設定
        val passValue = "22"
        intent.putExtra("PASS_KEY", passValue)
        startActivity(intent)
    }

    private fun CheckPermissionBluetoothAdapter() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        Log.d(TAG1, "bluetoothAdapter is ${bluetoothAdapter}")

        if(bluetoothAdapter?.isEnabled == false) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(
                    Manifest.permission.BLUETOOTH_CONNECT,
                    Manifest.permission.BLUETOOTH_SCAN,
                    Manifest.permission.BLUETOOTH),PERMISSION_BLUETOOTH_CONNECT_CODE) //リクエストパーミッションにbluetooth_connectを追加
            }
            startActivityForResult(enableBtIntent, BT_ONOFF_CONF)
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.BLUETOOTH_CONNECT), REQUEST_CODE_BLUETOOTH_CONNECT)
            CheckPermissionBluetoothAdapter()
        } else {
            Log.d(TAG1, "Permission Available 03")
            connectToDevice()
        }
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
                pairedDevices?.forEach { device ->
                    when(device.name) {
                        DEVICE_NAME21 -> {
                            Log.d(TAG1, "01Device is ${device}")
                            devices.add(device)
                        }
                        DEVICE_NAME22 -> {
                            Log.d(TAG1, "02Device is ${device}")
                            devices.add(device)
                        }
                    }
                }

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
                UnityPlayer.UnitySendMessage("KinkoGameStateManager", "ResumeGame", "")
                Log.d(TAG1, "Resume Game")

                isConnected = true
                readData()
            } catch (e: Exception) {
                Log.e(TAG1, "connected out: ${e.message}")
                isConnected = false
                closeConnection()
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

                while(isConnected) {
                    try {
                        delay(700)
                        val bytes = inputStream.read(buffer) ?: 0
                        if(bytes > 0) {
                            var incomingData = String(buffer, 0, bytes)
                            Log.d(TAG3, "Rechieved: ${incomingData}")
                            if (bytes != null) {
                                stateSendValue = incomingData
                                Log.d(TAG1, "incomingData is Null")
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
                        UnityPlayer.UnitySendMessage("KinkoGameStateManager", "PauseGame", "")
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
                        UnityPlayer.UnitySendMessage("KinkoSystemManager", "PauseGame", "")
                        reconnectToDevice()
                    }
                }
            }
        }
    }

    private fun reconnectToDevice() {
        if (isfinishGame == false) {
            CoroutineScope(Dispatchers.IO).launch {
                Log.d(TAG1, "Now Connecting...")
                closeConnection()
                devices.clear()
                delay(400)
                CheckPermissionBluetoothAdapter()
            }
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