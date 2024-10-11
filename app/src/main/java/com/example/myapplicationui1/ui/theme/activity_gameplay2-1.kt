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
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.myapplicationui1.ui.theme.ActivityEnd
import com.unity3d.player.UnityPlayerActivity
import com.unity3d.player.UnityPlayer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.InputStream
import java.io.OutputStream
import java.util.UUID

class GamePlay21Activity: UnityPlayerActivity() {
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
    private var bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    private var bluetoothSocket: BluetoothSocket? = null

    // BluetoothValue
    private var PERMISSION_BLUETOOTH_CONNECT_CODE = (1)
    private var BT_ONOFF_CONF = (3)

    // readDataを管理する変数
    private var isConnected: Boolean = false

    // contrllerが接続されているかを管理する変数
    private var isController: Boolean = false

    private lateinit var textView: TextView

    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>

    private var stateSendValue: String = "0"

    //
    private var isfinishGame: Boolean = false

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
        setContentView(R.layout.activity_gameplay21)
        try {
            mUnityPlayer = UnityPlayer(this as Activity)
            findViewById<ConstraintLayout>(R.id.unity)?.addView(
                mUnityPlayer, ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            )
            CheckPermissionBluetoothAdapter()
            mUnityPlayer.requestFocus()
            window.clearFlags(SCREEN_ORIENTATION_CHANGED)
            try {
                UnityPlayer.UnitySendMessage("SceneSelect", "ReceiveMessage", "Wankosoba")
            } catch (e: Exception){
                Log.d(TAG1, "Error is: ", e)
            }
            Log.d(TAG1, "はじめるわよ～")

            val filter = IntentFilter().apply {
                addAction(BluetoothDevice.ACTION_ACL_CONNECTED)
                addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED)
            }

            registerReceiver(bluetoothReceiver, filter)

        } catch (e: Exception) {
            Log.d("Error Try method", "${e}")
        }
    }

    // Unity側で呼ぶ、別画面へ遷移する関数
    private fun returnSelectActivity() {
        mUnityPlayer.onStop()
        closeConnection()

        unregisterReceiver(bluetoothReceiver)

        Log.d("GamePlay22Activity", "とめたわよ～")
        isfinishGame = true
        val intent = Intent(this, ActivityEnd::class.java)
        startActivity(intent)
    }

    private fun CheckPermissionBluetoothAdapter() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

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
                // ペアリングしているデバイスと接続
                val device = pairedDevices?.find { it.name == DEVICE_NAME }
                Log.d(TAG1, "Device is ${device}")

                if(device != null) {
                    Log.d(TAG1, "Permission Available 05")
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            bluetoothSocket = device.createRfcommSocketToServiceRecord(SPP_UUID)
                            Log.d(TAG1, "Permission Available 06")
                            bluetoothSocket?.connect()
                            inputStream = bluetoothSocket?.inputStream
                            outputStream = bluetoothSocket?.outputStream
                            Log.d(TAG2, "Connected to $DEVICE_NAME")
                            UnityPlayer.UnitySendMessage("WankosobaSystemManager", "ResumeGame", "")
                            // readDataを許可
                            isConnected = true
                            readData()
                        } catch (e: Exception) {
                            Log.e(TAG5, "Connection failed: ${e.message}")
                        }
                    }
                } else {
                    CheckPermissionBluetoothAdapter()
                }
            } catch (e: Exception) {
                isConnected = false
                connectToDevice()
            }
        }
    }

    private suspend fun readData() {
        val buffer = ByteArray(1024)
        while (isConnected) {
            try {
                delay(85)
                val bytes = inputStream?.read(buffer) ?: 0
                if (bytes > 0) {
                    var incomingData = String(buffer, 0, bytes)
                    Log.d(TAG3, "Rechieved: $incomingData")
                    if (bytes != null) {
                        stateSendValue = incomingData
                        Log.d(TAG1, "incomingData is Null")
                    } else {
                        incomingData = stateSendValue
                    }
                    delay(300)
                    UnityPlayer.UnitySendMessage("WankosobaSystemManager", "ReceiveMessage", "${incomingData}")
                }
            } catch (e: Exception) {
                Log.e(TAG4, "値読み取りエラー: ${e.message}")
                isConnected = false
                UnityPlayer.UnitySendMessage("WankosobaSystemManager", "PauseGame", "")
                reconnectToDevice()
            }
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
                    UnityPlayer.UnitySendMessage("WankosobaSystemManager", "PauseGame", "")
                    reconnectToDevice()
                }
            }
        }
    }

    private fun reconnectToDevice() {
        if (isfinishGame == false) {
            CoroutineScope(Dispatchers.IO).launch {
                delay(2000)
                Log.d(TAG1, "Now Connecting...")
                CheckPermissionBluetoothAdapter()
            }
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