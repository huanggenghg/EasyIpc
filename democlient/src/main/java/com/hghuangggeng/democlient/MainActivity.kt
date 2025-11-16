package com.hghuangggeng.democlient

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
import com.hghuangggeng.democlient.ui.theme.EasyIPCTheme
import com.hghuangggeng.easyipc_baseclient.IEasyIpcClient
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var easyIpcClient: IEasyIpcClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EasyIPCTheme {
                FilledButtonExample {
                    val result =
                        easyIpcClient.invoke("call", TestCallParams2(11, "TestCallParams2"))
                    (result as? TestCallResult)?.let {
                        Log.i(TAG, "client demo call ipc result:${it.data}")
                    } ?: let {
                        Log.i(TAG, "client demo call ipc result: null!")
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        easyIpcClient.start(this, lifecycle)
    }

    companion object {
        private const val TAG = "MainActivity"
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