# TTS Server Android Project

基于 [jwoo1982217/My-bd-tts-server-AndroidProject](https://github.com/jwoo1982217/My-bd-tts-server-AndroidProject) fork 的 TTS Server Android 项目。

## 功能特性

### 1. 系统 TTS 转发
- 将 Android 系统 TTS 转发为 HTTP 服务
- 支持自定义端口配置
- 后台保活服务

### 2. 独立的转发器日志页面
- 新增独立的 HTTP 请求日志显示页面
- Tab 切换：日志 / 配置 / 网页
- 记录 HTTP 方法、URI、远程地址
- 支持清空日志和复制日志内容

### 3. WebDAV 备份系统
- 自动备份配置到 WebDAV 服务器
- 支持手动备份和恢复
- 后台定时同步

### 4. 批量标签管理
- 批量编辑 TTS 引擎标签
- 快速筛选和管理

### 5. 后台白名单工具
- 管理后台运行白名单应用
- 提高后台存活率

## 版本信息

- **当前版本**: v1.26.05042257
- **APK**: `newapk/TTS-Server-v1.26.05042257.apk`

## 框架版本

| 组件 | 版本 |
|------|------|
| Kotlin | 2.2.21 |
| AGP | 8.9.0 |
| Gradle | 8.11.1 |
| Room | 2.7.1 |
| Compose BOM | 2025.02.00 |
| compileSdk | 36 |
| targetSdk | 36 |
| minSdk | 26 |

## 改动日志

### v1.26.05042257 (2025-05-04)
**修复**
- 修复 AbsForwarderService 构造函数参数冲突
- 修复 Netty 端口复用问题 (SO_REUSEADDR)

**功能**
- 添加独立的转发器日志页面 (ForwarderLogScreen.kt)
- BasicForwarderScreen 添加日志/配置/网页三个 Tab
- ConfigViewModel 添加 requestLogs 列表管理
- 通过 BroadcastReceiver 将服务端请求日志传递到前端
- 日志页面支持清空和复制功能

### v1.26.05022057 (框架升级)
**升级**
- Kotlin 2.1.10 → 2.2.21
- AGP 8.8.1 → 8.9.0
- Room 2.6.1 → 2.7.1
- Compose BOM 2024.10 → 2025.02.00
- compileSdk 34 → 36

### v1.26.05012057 (功能迁移)
**新增功能**
- WebDAV 备份系统
- 后台保活系统
- 批量标签管理
- 后台白名单工具

## 项目结构

```
My-bd-tts-server/
├── app/                          # Android 应用模块
│   └── src/main/java/.../
│       ├── compose/               # Compose UI
│       │   ├── forwarder/        # 转发器相关页面
│       │   │   ├── ForwarderLogScreen.kt  # 独立的转发器日志页面
│       │   │   ├── BasicForwarderScreen.kt # 转发器主页面
│       │   │   ├── ConfigViewModel.kt     # 配置 ViewModel
│       │   │   └── systts/               # 系统 TTS 转发
│       │   └── systts/           # 系统 TTS 日志页面
│       └── service/              # 服务层
│           └── forwarder/        # 转发器服务
│               ├── AbsForwarderService.kt
│               └── system/
│                   └── SysTtsForwarderService.kt
├── lib-server/                    # 服务器核心库
│   └── src/main/java/.../
│       └── server/
│           ├── BaseCallback.kt    # HTTP 回调基类 (含请求日志)
│           ├── MyNettty.kt        # Netty 服务器 (端口复用)
│           └── forwarder/
│               └── SystemTtsForwardServer.kt
└── newapk/                       # 编译输出目录
    └── TTS-Server-v1.26.05042257.apk
```

## 关键文件修改

### lib-server 模块
| 文件 | 改动 |
|------|------|
| `BaseCallback.kt` | 添加 `RequestLogEntry` 数据类、`requestLogList` 列表和 `logRequest()` 方法 |
| `MyNettty.kt` | 添加 `.isReuseAddress(true)` 修复端口占用 |
| `SystemTtsForwardServer.kt` | 在 HTTP intercept 中调用 `callback.logRequest()` |

### app 模块
| 文件 | 改动 |
|------|------|
| `ForwarderLogScreen.kt` | 新增：独立的转发器日志页面组件 |
| `BasicForwarderScreen.kt` | 添加日志/配置/网页三个 Tab |
| `ConfigViewModel.kt` | 添加 `requestLogs` 列表管理 |
| `BasicConfigScreen.kt` | 添加 `actionOnRequestLog` 参数 |
| `SysTtsForwarderService.kt` | 实现 `logRequest` 回调，发送 `ACTION_ON_REQUEST_LOG` 广播 |
| `AbsForwarderService.kt` | 添加 `sendRequestLog()` 方法 |

## 安装 APK

```bash
# 通过 ADB 安装
adb install newapk/TTS-Server-v1.26.05042257.apk
```

## 构建

```bash
# Debug 版本
./gradlew :app:assembleDebug

# Release 版本
./gradlew :app:assembleRelease
```

## 许可证

遵循原项目许可证
