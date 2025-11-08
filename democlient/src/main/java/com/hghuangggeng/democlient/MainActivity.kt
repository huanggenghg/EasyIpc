package com.hghuangggeng.democlient

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.hghghgh.text.TestCallParamsProtocol
import com.hghuangggeng.democlient.ui.theme.EasyIPCTheme
import com.hghuangggeng.easyipc_transport_aidl_client.MyServiceConnection

class MainActivity : ComponentActivity() {

    private var connection: MyServiceConnection? = null
    private var count = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EasyIPCTheme {
                FilledButtonExample {
                    connection?.invoke(TestCallParams(11))
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val intent = Intent()
        intent.action = "com.hghuangggeng.easyipc_transport_aidl.MyService"
        intent.setPackage("com.hghuangggeng.demoserver")
        connection = MyServiceConnection()
        bindService(intent, connection!!, BIND_AUTO_CREATE).also {
            Log.i("TAG", "bindService:$it")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        connection?.let {
            it.destroy()
            unbindService(it)
        }
    }
}

@Composable
fun FilledButtonExample(onClick: () -> Unit) {
    Button(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.Center),
        onClick = { onClick() }) {
        Text("开启悬浮窗")
    }
}