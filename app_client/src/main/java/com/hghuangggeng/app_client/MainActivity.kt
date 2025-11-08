package com.hghuangggeng.app_client

import android.annotation.SuppressLint
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.hghuangggeng.easyipc_transport_aidl_client.MyServiceConnection

class MainActivity : AppCompatActivity() {

    private var connection: MyServiceConnection? = null

    private var count = 0

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        findViewById<View>(R.id.clickBtn).setOnClickListener {
            connection?.sendMsg("from 客户端，当前第 ${count++} 次")
        }
    }

    override fun onResume() {
        super.onResume()
        val intent = Intent()
        intent.action = "com.hghuangggeng.easyipc_transport_aidl.MyService"
        intent.setPackage("com.hghuangggeng.app_server")
        connection = MyServiceConnection()
        bindService(intent, connection!!, BIND_AUTO_CREATE)
    }

    override fun onDestroy() {
        super.onDestroy()
        connection?.let {
            it.destroy()
            unbindService(it)
        }
    }
}