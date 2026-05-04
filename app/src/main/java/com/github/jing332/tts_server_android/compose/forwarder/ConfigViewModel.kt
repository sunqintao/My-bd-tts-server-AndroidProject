package com.github.jing332.tts_server_android.compose.forwarder

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.github.jing332.common.LogEntry
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ConfigViewModel : ViewModel() {
    val logs = mutableStateListOf<LogEntry>()
    val logState by lazy { LazyListState() }

    // HTTP 请求日志列表（独立的转发器日志）
    val requestLogs = mutableStateListOf<RequestLogEntry>()
    // 日志 ID 计数器
    private var requestLogIdCounter = 0L
    private val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

    /**
     * 添加 HTTP 请求日志
     */
    fun addRequestLog(method: String, uri: String, remoteAddress: String) {
        val entry = RequestLogEntry(
            id = requestLogIdCounter++,
            method = method,
            uri = uri,
            remoteAddress = remoteAddress,
            time = timeFormat.format(Date())
        )
        requestLogs.add(entry)
        // 限制最大日志数量，防止内存溢出
        while (requestLogs.size > 1000) {
            requestLogs.removeAt(0)
        }
    }

    /**
     * 清空请求日志
     */
    fun clearRequestLogs() {
        requestLogs.clear()
    }
}
