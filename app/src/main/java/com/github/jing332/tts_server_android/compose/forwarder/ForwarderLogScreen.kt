package com.github.jing332.tts_server_android.compose.forwarder

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Parcelable
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.github.jing332.common.utils.toast
import com.github.jing332.tts_server_android.R
import kotlinx.coroutines.delay
import kotlinx.parcelize.Parcelize

/**
 * HTTP 请求日志条目（可序列化）
 */
@Parcelize
data class RequestLogEntry(
    val id: Long,
    val method: String,
    val uri: String,
    val remoteAddress: String,
    val time: String,
) : Parcelable

/**
 * 独立的转发器日志页面
 * 显示 HTTP 请求日志，不与系统TTS日志混在一起
 */
@Composable
fun ForwarderLogScreen(
    modifier: Modifier = Modifier,
    logs: List<RequestLogEntry>,
    onClearLogs: (() -> Unit)? = null,
    listState: LazyListState = rememberLazyListState(),
) {
    val context = LocalContext.current
    val darkTheme = isSystemInDarkTheme()

    var showClearDialog by rememberSaveable { mutableStateOf(false) }

    // 刷新计时器
    var refreshTick by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(500)
            refreshTick++
        }
    }

    fun copyLogToClipboard(log: RequestLogEntry) {
        val text = "${log.time}  ${log.method}: ${log.uri} (${log.remoteAddress})"
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.setPrimaryClip(
            ClipData.newPlainText("forwarder-log", text)
        )
        context.toast("已复制该条日志")
    }

    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = { Text("清空日志") },
            text = { Text("确定要清空当前日志吗？") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showClearDialog = false
                        if (onClearLogs != null) {
                            onClearLogs()
                        } else {
                            context.toast("当前页面未接入清空日志")
                        }
                    }
                ) {
                    Text("确定")
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) {
                    Text("取消")
                }
            }
        )
    }

    val displayLogs = remember(logs, refreshTick) {
        logs.takeLast(500) // 最多显示500条
    }

    Column(modifier.fillMaxSize()) {
        // 标题栏
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 24.dp, end = 8.dp, top = 8.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.forwarder_log),
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "${displayLogs.size} 条",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            IconButton(onClick = { showClearDialog = true }) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "清空日志"
                )
            }
        }

        // 日志列表
        Box(modifier = Modifier.fillMaxSize()) {
            if (displayLogs.isEmpty()) {
                Box(Modifier.align(Alignment.Center)) {
                    Text(
                        text = stringResource(R.string.empty_list),
                        style = MaterialTheme.typography.displaySmall
                    )
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                state = listState
            ) {
                itemsIndexed(
                    displayLogs,
                    key = { _, log -> log.id }
                ) { index, log ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { copyLogToClipboard(log) }
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = log.time,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }

                        Text(
                            text = "${log.method}: ${log.uri}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = getMethodColor(log.method, darkTheme)
                        )

                        Text(
                            text = "remote: ${log.remoteAddress}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        if (index < displayLogs.size - 1) {
                            HorizontalDivider(
                                modifier = Modifier.padding(top = 8.dp),
                                thickness = 0.5.dp
                            )
                        }
                    }
                }

                item(key = "bottom_spacer") {
                    Spacer(Modifier.navigationBarsPadding())
                }
            }
        }
    }
}

/**
 * 根据 HTTP 方法返回对应颜色
 */
@Composable
private fun getMethodColor(method: String, isDarkTheme: Boolean): Color {
    return when (method.uppercase()) {
        "GET" -> Color(0xFF43A047) // Green
        "POST" -> Color(0xFF1E88E5) // Blue
        "PUT" -> Color(0xFFFFA000) // Orange
        "DELETE" -> Color(0xFFE53935) // Red
        "PATCH" -> Color(0xFF8E24AA) // Purple
        else -> if (isDarkTheme) Color(0xFFE0E0E0) else Color(0xFF212121)
    }
}
