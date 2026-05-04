package com.github.jing332.server

import android.util.Log

interface BaseCallback {
    fun log(level: Int, message: String) {}

    /**
     * 记录 HTTP 请求日志
     * @param method HTTP 方法 (GET, POST 等)
     * @param uri 请求 URI
     * @param remoteAddress 客户端地址
     */
    fun logRequest(method: String, uri: String, remoteAddress: String) {
        log(Log.INFO, "$method: $uri remote: $remoteAddress")
    }
}