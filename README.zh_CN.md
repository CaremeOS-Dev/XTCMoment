<div align="center">

<img src="app/src/main/res/drawable-xhdpi/ic_app_more_moment.png" width="112" height="112" alt="好友圈应用图标">

# 好友圈 · XTCMoment

**小天才手表「好友圈」的开源实现。**

抬手发一条动态，看好友给你点赞、评论、送礼物——发布、可见范围、互动、
个人主页这整套社交闭环，都跑在儿童手表上。

[![License](https://img.shields.io/badge/license-GPL--3.0-blue.svg)](LICENSE)
[![Platform](https://img.shields.io/badge/platform-Android%20Wear%20(watch)-3ddc84.svg)](#运行要求与限制)
[![minSdk](https://img.shields.io/badge/minSdk-19-orange.svg)](#运行要求与限制)
[![ABI](https://img.shields.io/badge/ABI-armeabi-lightgrey.svg)](#运行要求与限制)
[![Version](https://img.shields.io/badge/version-5.3.11-ff8a00.svg)](#)
[![Java](https://img.shields.io/badge/Java-1.8-007396.svg)](#技术架构)
[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.24-7f52ff.svg)](#技术架构)

[English](README.md) · [简体中文](README.zh_CN.md)

</div>

---

## 这是什么

XTCMoment 就是小天才手表上的**好友圈**客户端——手表里的社交动态应用。
本仓库是它的可读、可编译重写版本：动态流、发布流程、互动能力、可见范围控制
和个人主页，全部用第一方 Java/Kotlin 从零编写，没有残留的混淆痕迹。

下面这些能力都在本仓库里真正实现，由应用自己的界面驱动，而不是占位桩。

| | |
|---|---|
| 📝 **丰富的发布方式**<br>文字、心情状态、相册、视频、实况照片、位置 | 💬 **真实的互动**<br>点赞、评论与回复、视频弹幕、礼物 |
| 👀 **精细的可见范围**<br>公开、私密、部分好友可见、不给谁看 | 🧑 **个人主页**<br>头像装扮、签名气泡、生日与星座 |
| ✨ **AI 美文**（已弃用）<br>文案风格、每日次数、创作记录——已弃用，仅部分机型支持 | ⌚ **为手表而生**<br>下拉刷新、草稿保存、消息提醒、语音助手入口 |

---

## 一 · 动态流

主页（`MomentActivity`）把整个社交闭环收在一屏里。

| 能力 | 说明 |
|---|---|
| **动态列表** | 好友动态卡片列表，异步渲染布局，视频、实况照片、H5 与分享内容混排 |
| **下拉刷新** | 自研刷新控件，带"有新动态"提示 |
| **上拉加载** | 滚动增量分页，可见项监听触发预加载 |
| **未读提醒** | 未读点赞/评论以角标呈现，并提供可关闭的提醒开关 |
| **实时更新** | IM 推送接收器直接刷新列表与角标，无需重新打开应用 |
| **页面入口** | 发布按钮、搜索动态、收到的爱心、个人主页、设置、语音助手与分享入口 |
| **搜索动态** | 主页 header 的搜索入口（`SearchMomentActivity`）：对已缓存动态做关键词检索，命中正文、分享文案、位置与作者昵称，结果就是常规动态卡片，可点赞、评论、直接进详情 |
| **空态与异常** | 友好的空页面、重试路径与版本不匹配提示 |

## 二 · 发布动态

一个发布入口，分发到全部内容类型
（`PublishActivity` → `PushTextActivity`、`PushPictureActivity`、`PushVideoOrPhotoServer`）。

| 内容类型 | 能做什么 |
|---|---|
| **文字** | 纯文字动态，字数上限由服务端下发，输入框实时计数 |
| **心情 / 状态** | 直接挑插画化的心情、状态模板，不用手打（`MoodOrStateActivity`） |
| **相册** | 多选网格选图，默认最多 9 张（上限可由服务端调整），支持应用内预览与大图浏览 |
| **视频** | 拍摄或选择视频，上传后在独立播放器中播放 |
| **实况照片** | 自动识别"图片 + 短视频"组合，作为独立动态类型发布，并带动态效果回放 |
| **位置** | 为任意动态附加 POI，浏览 LBS 动态并看到位置星级动画 |
| **H5 动态** | 网页内容以卡片形式嵌入动态流（`ShowH5Activity`、`view_photo_text_h5_moment`） |
| **外部分享进来** | 手表上其它应用可通过分享组件发到好友圈（分享协议 v4、`ShareToMomentActivity`） |

发布全程**草稿不丢**：文字、图片、视频、位置、可见范围以及 AI 生成的文案都会
持久化（`SaveDynamic`），所以发到一半退出页面甚至进程重启后还能接着发。

## 三 · 互动

| 互动 | 说明 |
|---|---|
| **点赞** | 点赞与取消点赞，卡片上有点赞动效 |
| **收到的爱心** | "收到的爱心 / 鲜花"列表，孩子能看到都有谁给自己点了赞（`NewLikeActivity`、`MomentLikesActivity`） |
| **点赞规则** | 规则页说明每天给同一好友点赞的等级，等级表直接在应用内展示（`LikeRuleShowActivity`） |
| **评论** | 文字评论、楼中楼回复，以及删除自己的评论 |
| **评论限制** | 每日评论上限、"这条评论已删除"状态，均以弹窗形式呈现 |
| **视频弹幕** | 视频动态上的弹幕评论，并提供关闭开关（`BarrageFragment`、`BarragePresenter`） |
| **礼物** | 在视频动态上送礼物，并查看全部赠送者列表（`GiftDetailsActivity`） |
| **举报** | 按可选原因举报内容或好友，带每日举报次数与禁言处理 |
| **内容安全** | 发布前校验会弹出违规内容、敏感内容提示，而不是静默失败 |

## 四 · 谁可以看

可见范围是每次发布的正式一环，而不是附属选项
（`VisibleTypeActivity`、`FriendsVisibleRangeActivity`）。

| 范围 | 含义 |
|---|---|
| **公开** | 所有好友可见 |
| **私密** | 仅自己可见 |
| **部分好友可见** | 白名单——精确挑选谁能看到 |
| **不给谁看** | 黑名单——精确挑选谁不能看到 |

- **好友选择页**选完回到范围页，并且每种范围都会**记住上次的选择**。
- 滚动时确认按钮会**滑出屏幕**，列表停下后再滑回来，小屏幕上不挡内容。
- 发布后仍可**修改可见范围**，受服务端每日修改次数限制（"明天再试吧"是界面上
  真实存在的状态）。

## 五 · 个人主页

`AccountInfoActivity` 是好友点进来看到的主页。

| 元素 | 说明 |
|---|---|
| **头像与装扮** | 头像配合 SVGA 渲染的挂件，通过手表装扮服务绑定（`HeadDressManager`、`DressProxy`） |
| **昵称** | 来自手表联系人库，联系人变化时自动刷新 |
| **个性签名** | 签名以气泡形式向上飘动展示，三种气泡样式轮换 |
| **生日与星座** | 生日文案搭配对应星座图标与主题色，十二星座齐全 |
| **性别样式** | 根据性别切换背景图 |
| **账号信息** | 从系统账号 Provider 读取手表账号资料 |

## 六 · AI 美文（已弃用）

> **已弃用，且仅部分机型支持。** AI 美文的上游服务已经下线，入口也按机型单独
> 开关。请把这个模块当作保留的参考实现，而不是可以依赖的功能——缺少配套后端与
> 模块开关时，入口不会出现，或只会落到错误态。

AI 美文（`com.xtc.aitext`）负责在动态发出去之前，把一句话变得更漂亮。

| 能力 | 说明 |
|---|---|
| **入口** | 在发布流程中作为独立发布类型提供，受机型模块开关与服务可用性控制 |
| **文案风格** | 风格选择页，风格由服务端下发（`SelectStyleActivity`） |
| **生成流程** | 独立编辑页，生成中有进度动画，中途退出会提示风险 |
| **每日次数** | 剩余次数计数，并支持从手表宝箱奖励中领取额外次数 |
| **创作记录** | "我的 AI 写作集"——分页展示你生成过的全部文案（`AIRecordActivity`） |
| **合规提示** | 明确提示内容由 AI 生成、仅供参考 |

## 七 · 手表系统集成

它按系统应用的方式在手表上工作，而不是一个与世隔绝的演示应用。

| 集成点 | 说明 |
|---|---|
| **语音助手** | 暴露 `PostStatus` 指令，助手可以直接发起状态发布（`com.xtc.assistantapi.directive`） |
| **联系人** | 联系人更新与删除会触发刷新，好友来自手表联系人库 |
| **消息提醒** | IM 推送接收器、未读计数，以及自启动提醒开关 |
| **模块开关** | 响应桌面模块开关与功能管理广播 |
| **本地存储** | ORMLite 数据库存动态、评论、点赞、昵称与模板，偏好设置走 MMKV |
| **分享能力** | 动态、评论、点赞、截图与应用分享各自的 ContentProvider |
| **保活** | 保活服务，保证推送能持续到达 |

---

## 技术架构

| 层面 | 选型 |
|---|---|
| **语言** | 原厂是 Java 的用 Java，原厂是 Kotlin 的用 Kotlin（第一方约 2,080 个类） |
| **界面** | XML 布局 + `support`/`appcompat` 25.4.0，刻意**不使用** Compose，以贴合原厂框架 |
| **架构模式** | 手写 MVP（`MvpActivity` / `MvpFragment` / `MvpPresenter`），以委托方式接管生命周期 |
| **网络** | OkHttp 3.12 + Retrofit 2.9（Gson、protobuf、RxJava 三种适配器） |
| **异步** | RxJava 1 + RxAndroid，跨组件事件用 EventBus 3 |
| **媒体** | Glide 4.9（含 transformations）、Lottie 2.7、SVGA Player 2.5 负责头像与特效动画 |
| **持久化** | ORMLite 5.1 基于 SQLite，MMKV 负责快速键值存储 |
| **渲染扩展** | libGDX + Spine，弹幕用 DanmakuFlameMaster，图片缩放用 PhotoView |
| **工具链** | Gradle 8.9（wrapper）、AGP 8.6.0、Kotlin 1.9.24，用 JDK 17 运行 Gradle，字节码目标 1.8 |

## 构建

<details>
<summary><b>环境要求与命令</b></summary>

**环境要求**

- **JDK 17** —— 用于运行 Gradle（字节码目标仍是 1.8）。
- **Android SDK**，需安装 **Platform 34** 并接受 SDK 许可。
- 需要联网下载 Gradle 与 Maven 依赖。wrapper 与仓库已优先指向阿里云镜像，
  并保留 Google / Maven Central 作为回退。

**构建（Windows，PowerShell，在仓库根目录执行）**

```powershell
$env:JAVA_HOME    = '<你的 JDK 17 目录>'
$env:ANDROID_HOME = '<你的 Android SDK 目录>'

# gradle.properties 把 Unix domain socket 临时目录固定为 C:/temp
New-Item -ItemType Directory -Force 'C:\temp' | Out-Null

.\gradlew.bat :app:assembleDebug --console=plain
# 或使用 PowerShell 原生 wrapper：
.\gradlew.ps1 :app:assembleDebug --console=plain
```

Debug APK 输出在 `app/build/outputs/apk/debug/app-debug.apk`。

**构建（Linux / macOS）**

```bash
export JAVA_HOME='<你的 JDK 17 目录>'
export ANDROID_HOME='<你的 Android SDK 目录>'
sh ./gradlew '-Dorg.gradle.jvmargs=-Xmx4g -Dfile.encoding=UTF-8 -Duser.language=en -Duser.country=US -Djdk.net.unixdomain.tmpdir=/tmp' \
  :app:assembleDebug --console=plain
```

仓库里的 `gradlew` 没有可执行位，请通过 `sh` 调用。

**说明**

- `build.ps1` 是为某一台开发机准备的便捷脚本，里面硬编码了 JDK 路径、Gradle
  发行包缓存和项目路径。请改用上面的 wrapper 命令。
- 本地预编译依赖**已提交到仓库**，普通 clone 就包含当前构建脚本引用的全部内容：
  `prebuilt-local/` 下 9 个 JAR，以及 `app/src/main/prebuilt/` 下的 `armeabi` `.so`。
- `local.properties` 已被 git 忽略，请把 `sdk.dir` 指向你自己的 SDK。
- Release 构建**没有配置签名**——release 变体能编出来，但本仓库不提供签名配置。

</details>

## 运行要求与限制

在期待它直接跑起来之前，请先看这几条：

- **这是手表应用。** manifest 声明了 `android.hardware.type.watch`，界面也是为
  小尺寸圆润屏幕设计的，它不是手机客户端。
- **只打包了 32 位 `armeabi`。** 没有 `arm64-v8a`、`armeabi-v7a` 或 `x86`
  二进制，因此纯 64 位设备与 x86 模拟器按现状不在支持范围内。
- **它依赖小天才手表的系统环境。** IM 走桌面 `BridgeService`，账号来自手表账号
  Provider，好友来自手表联系人库，定位来自桌面定位服务。在通用 Android 设备上，
  依赖这些服务的能力无法工作。
- **云端能力需要配套后端。** 礼物赠送与赠送者列表、评论同步、点赞都要访问远端
  接口；接口不可达时只会走到各自的错误态。
- **AI 美文已弃用，且仅部分机型支持。** 它只在部分手表机型上提供，上游服务也已
  下线，所以不要指望它在每台设备上都可用——甚至不要指望它还能用。
- **"能编译"和"能跑"是两道不同的门槛。** 代码可以构建出 APK；若要完整替代系统
  应用，还取决于设备的签名与系统集成策略。

## 项目结构

```
XTCMoment/
├── app/
│   ├── build.gradle                     # 应用模块：SDK 版本、依赖、打包配置
│   └── src/main/
│       ├── AndroidManifest.xml          # 组件、权限、系统元数据
│       ├── java/com/xtc/                # 第一方 Java 源码
│       │   ├── moment/module/           # 动态流、发布、评论、点赞、礼物、弹幕、
│       │   │                            # 详情、个人资料、可见范围、分享、举报……
│       │   ├── aitext/                  # AI 美文（编辑、风格、创作记录、服务）
│       │   ├── ui/widget/               # 共用手表 UI 组件（按钮、弹窗、隐私）
│       │   ├── architecture/mvp/        # MVP 基类与生命周期委托
│       │   └── …                        # 日志、网络、数据库、联系人/账号/定位客户端
│       ├── kotlin/com/xtc/              # Kotlin 源码（配置、IPC、透明视频播放器等）
│       ├── res/                         # 布局、drawable、values、动画
│       ├── assets/                      # 内置静态资源
│       └── prebuilt/armeabi/            # 直接打包的 JNI 库
├── prebuilt-local/                      # 公共仓库无法获取的 JAR
├── proto/                               # request/domain 协议定义
├── docs/PORTING_NOTES.md                # 重写移植笔记
├── tools/                               # 迁移与依赖排查辅助脚本
├── gradle/wrapper/                      # Gradle 8.9 wrapper
└── build.gradle · settings.gradle       # 根构建，仅 :app 一个模块
```

## 参与贡献

欢迎提 Issue 和 PR。几条建议：

- 第一方代码保持**可读、无混淆**——类名、方法名、字段名要有意义，逻辑微妙处加注释。
- 界面沿用原厂框架（XML 布局 + `support`/`appcompat`），不要换成 Compose。
- 优先使用**真实上游库**，而不是重新反编译的副本；也不要引入会与已合并进 `res/`
  的资源重复的依赖。

## 许可证

以 [GNU General Public License v3.0](LICENSE) 发布。

<div align="center">
<sub>好友圈 · Friends Moments —— 为小小的手腕和热热闹闹的朋友圈而生。</sub>
</div>