package com.hghuangggeng.democlient

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hghuangggeng.democlient.ui.theme.EasyIPCTheme
import com.hghuangggeng.easyipc_baseclient.IEasyIpcClient
import com.hghuangggeng.easyipc_core.IEasyIpcDataCallback
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
                CenteredButtonsScreen(easyIpcClient)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        easyIpcClient.start(this, "com.hghuangggeng.demoserver")
    }

    companion object {
        const val TAG = "MainActivity"
    }
}

@Composable
fun CenteredButtonsScreen(easyIpcClient: IEasyIpcClient) {
    // Column 用于垂直排列子元素
    Column(
        modifier = Modifier
            .fillMaxSize() // 使 Column 填充整个屏幕
            .padding(16.dp), // 添加整体边距
        verticalArrangement = Arrangement.Center, // 垂直方向居中对齐
        horizontalAlignment = Alignment.CenterHorizontally // 水平方向居中对齐
    ) {
        // 第一个按钮
        FilledButtonExample {
            val result =
                easyIpcClient.invoke("call", TestCallParams2(11, "TestCallParams2"))
            (result as? TestCallResult)?.let {
                Log.i(MainActivity.TAG, "client demo ipc invoke result:${it.data}")
            } ?: let {
                Log.i(MainActivity.TAG, "client demo ipc invoke result: null!")
            }
        }

        // 添加一个垂直间隔
        Spacer(modifier = Modifier.height(16.dp))

        // 第二个按钮
        FilledButtonExample2 {
            easyIpcClient.asyncInvoke(
                "hello1",
                param = emptyArray(),
                callback = object : IEasyIpcDataCallback {
                    override fun onCallback(data: Any?) {
                        (data as? String)?.let {
                            Log.i(MainActivity.TAG, "client demo ipc asyncInvoke result:$it")
                        } ?: let {
                            Log.i(MainActivity.TAG, "client demo ipc asyncInvoke result: $data!")
                        }
                    }
                })
        }
    }
}


@Composable
fun FilledButtonExample(onClick: () -> Unit) {
    Button(
        modifier = Modifier
            .wrapContentSize(Alignment.Center),
        onClick = { onClick() }) {
        Text("invoke")
    }
}

@Composable
fun FilledButtonExample2(onClick: () -> Unit) {
    Button(
        modifier = Modifier
            .wrapContentSize(Alignment.Center),
        onClick = { onClick() }) {
        Text("asyncInvoke")
    }
}