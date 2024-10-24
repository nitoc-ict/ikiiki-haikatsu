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
import android.widget.Toast
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

class GamePlay23Activity: UnityPlayerActivity() {
    // TAGs
    private val TAG1 = "BluetoothAdapter"
    private val TAG2 = "BluetoothConnectDevice"
    private val TAG3 = "ReadDatafromBlutooth"
    private val TAG4 = "FaildReadData"
    private val TAG5 = "CloseConnection"

    // About device information
    private val DEVICE_NAME21 = "ESP32_21"
    private val DEVICE_NAME22 = "ESP32_22"
    private val DEVICE_NAME23 = "ESP32_23"
    private val DEVICE_NAME24 = "ESP32_24"

    private val DEVICES = arrayOf(DEVICE_NAME21, DEVICE_NAME22, DEVICE_NAME23, DEVICE_NAME24)

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

    // Playerの数をintentから受け取る変数
    private var playerNum: Int = 3

    companion object {
        private const val REQUEST_CODE_BLUETOOTH_CONNECT = 1
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode) {
            PERMISSION_BLUETOOTH_CONNECT_CODE -> {
                if((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    reconnectToDevice()
                } else {
                    Toast.makeText(this, "Bluetooth機能が使用する権限がありません", Toast.LENGTH_LONG).show()
                }
                return
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode) {
            BT_ONOFF_CONF -> {
                when(resultCode) {
                    Activity.RESULT_OK -> {
                        Toast.makeText(this@GamePlay23Activity, "Bluetooth権限を許可しました", Toast.LENGTH_LONG).show()
                    }
                    Activity.RESULT_CANCELED -> {
                        Toast.makeText(this@GamePlay23Activity, "Bluetooth権限を許可してください...", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val text = intent.getStringExtra("PLAYERNUM")
        if(text != null) {
            playerNum = text.toInt()
        } else {
            playerNum = 3
            Log.e("NullError", "PlayerNum is null")
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gameplay23)
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
            Log.d(TAG1, "01PauseGame")
            UnityPlayer.UnitySendMessage("SuisuiGameStateManager", "PauseGame", "")
            setUpUnity()

        } catch (e: Exception) {
            Log.d("Error Try method", "${e}")
        }
    }

    private fun setUpUnity() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                withContext(Dispatchers.IO) {
                    UnityPlayer.UnitySendMessage("SceneSelect", "ReceiveMessage", "Suisui")
                    UnityPlayer.UnitySendMessage("SuisuiSystemManager", "SettingsPlayers", "$playerNum")
                    latch.await()
                }
                Log.e(TAG1, "Finish async SceneSelect")
                // bluetoothにマイコンが接続されていないとき、ゲームを停止して接続処理をする
                if(bluetoothAdapter == null) {
                    Log.d(TAG1, "02PauseGame")
                    UnityPlayer.UnitySendMessage("SuisuiGameStateManager", "PauseGame", "")
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

        Log.d("GamePlay23Activity", "とめたわよ～")
        val intent = Intent(this, ActivityEnd::class.java)
        // Endに送信する値を設定
        val passValue = "23"
        intent.putExtra("PASS_KEY", passValue)
        startActivity(intent)
    }

    private suspend fun CheckPermissionBluetoothAdapter() {
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
            checkAndConnectDevices()
        }
    }

    private suspend fun checkAndConnectDevices() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                withContext(Dispatchers.IO) {
                    if(ActivityCompat.checkSelfPermission(
                        this@GamePlay23Activity,
                        Manifest.permission.BLUETOOTH_CONNECT
                    ) != PackageManager.PERMISSION_GRANTED)
                    {
                        withContext(Dispatchers.Main) {
                            ActivityCompat.requestPermissions(
                                this@GamePlay23Activity,
                                arrayOf(Manifest.permission.BLUETOOTH_CONNECT),
                                REQUEST_CODE_BLUETOOTH_CONNECT
                            )
                        }
                    }
                    val pairedDevices: Set<BluetoothDevice>? = bluetoothAdapter?.bondedDevices
                    if(pairedDevices == null || pairedDevices.size < playerNum) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@GamePlay23Activity, "接続されるデバイスが不足しています", Toast.LENGTH_LONG).show()
                            UnityPlayer.UnitySendMessage("SuisuiGameStateManager", "PauseGame", "")
                            Log.d(TAG1, "03PauseGame")
                        }
                        return@withContext
                    } else {
                        pairedDevices.take(playerNum).forEach { device ->
                            DEVICES.forEach { name ->
                                if(name == device.name) {
                                    devices.add(device)
                                }
                            }
                            connectToDevice(device)
                        }
                        isConnected = true
                    }
                }
            } catch (e: Exception) {
                Log.d("WARNING", "Error is: ${e.message}")
                reconnectToDevice()
            }
        }.join()

        if(isConnected == true) {
            UnityPlayer.UnitySendMessage("SuisuiSystemManager", "ResumeGame", "")
            readData()
        } else {
            reconnectToDevice()
        }
    }

    // Socketに接続する関数
    private fun connectToDevice(bluetoothDevice: BluetoothDevice) {
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.BLUETOOTH_CONNECT
            ) == PackageManager.PERMISSION_GRANTED) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val socket = bluetoothDevice.createInsecureRfcommSocketToServiceRecord(SPP_UUID)
                    socket.connect()
                    sockets.add(socket)
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@GamePlay23Activity, "Bluetoothの接続に成功しました", Toast.LENGTH_LONG).show()
                    }
                } catch (e: Exception) {
                    Log.e("WARNING", "Error is:${e.message}")
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@GamePlay23Activity, "Socketの接続に失敗しました: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                }
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
                        Log.d(TAG1, "04PauseGame")
                        UnityPlayer.UnitySendMessage("SuisuiGameStateManager", "PauseGame", "")
                        Log.d(TAG1, "05PauseGame")
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
            UnityPlayer.UnitySendMessage("SuisuiSystemManager", "ReceiveMessage", "${sendData}")
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
                        UnityPlayer.UnitySendMessage("SuisuiGameStateManager", "PauseGame", "")
                        Log.d(TAG1, "10PauseGame")
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
                delay(5000)
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