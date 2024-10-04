package com.example.myapplicationui1

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.os.Build
import android.os.Build.VERSION
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts

class ExplanationActivity : AppCompatActivity() {
    private var btPermission = false;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_explanation)

        val yesButton: Button = findViewById(R.id.yes_button)
        val noButton: Button = findViewById(R.id.no_button)
        val connectButton: Button = findViewById(R.id.connect_button)

        // 「はい」ボタンがクリックされた時、activity_controller_explanation_selection へ遷移
        yesButton.setOnClickListener {
            val intent = Intent(this, ControllerExplanationSelectionActivity::class.java)
            startActivity(intent)
        }

        // 「いいえ」ボタンがクリックされた時、activity_controller_selection へ遷移
        noButton.setOnClickListener {
            val intent = Intent(this, ControllerSelectionActivity::class.java)
            startActivity(intent)
        }

        connectButton.setOnClickListener {
            scanBt()
        }
    }

    fun scanBt() {
        val bluetoothManager: BluetoothManager = getSystemService(BluetoothManager::class.java)
        val bluetoothAdapter: BluetoothAdapter? = bluetoothManager.adapter

        if(bluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth接続が許可されていません", Toast.LENGTH_LONG).show()
        } else {
            if(VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                blueToothPermissionLauncher.launch(android.Manifest.permission.BLUETOOTH_CONNECT)
            } else {
                blueToothPermissionLauncher.launch(android.Manifest.permission.BLUETOOTH)
            }
        }
    }

    private val blueToothPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
            isGranted: Boolean ->
        if(isGranted) {
            val bluetoothManager: BluetoothManager = getSystemService(BluetoothManager::class.java)
            val bluetoothAdapter: BluetoothAdapter? = bluetoothManager.adapter
            btPermission = true

            if(bluetoothAdapter?.isEnabled == false) {
                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                btActivityResultLauncher.launch(enableBtIntent)
            } else {
                canBtScanComment()
            }
        } else {
            btPermission = false
        }
    }
    private val btActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if(it.resultCode == RESULT_OK) {
            canBtScanComment()
        }
    }
    private fun canBtScanComment() = Toast.makeText(this, "Bluetooth接続が可能です", Toast.LENGTH_LONG).show()
}
