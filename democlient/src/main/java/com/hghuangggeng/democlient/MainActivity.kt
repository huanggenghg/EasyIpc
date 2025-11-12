package com.hghuangggeng.democlient

import android.os.Bundle
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
import com.hghuangggeng.democlient.ui.theme.EasyIPCTheme
import com.hghuangggeng.easyipc_transport_aidl_client.EasyIpcAIDLClient

class MainActivity : ComponentActivity() {
    private val easyIpcClient = EasyIpcAIDLClient();

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EasyIPCTheme {
                FilledButtonExample {
                    easyIpcClient.invoke("call", TestCallParams12(11))
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        easyIpcClient.start(this, lifecycle)
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