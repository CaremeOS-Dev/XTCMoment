<div align="center">

<img src="app/src/main/res/drawable-xhdpi/ic_app_more_moment.png" width="112" height="112" alt="XTCMoment app icon">

# Friends Moments · XTCMoment

**An open-source reimplementation of the Xiaotiancai (XTC) watch "好友圈" social feed.**

Share a moment from your wrist, watch your friends react, and keep the whole
social loop — publishing, visibility, likes, comments, gifts and the personal
page — running on a kids' smartwatch.

[![License](https://img.shields.io/badge/license-GPL--3.0-blue.svg)](LICENSE)
[![Platform](https://img.shields.io/badge/platform-Android%20Wear%20(watch)-3ddc84.svg)](#requirements--limitations)
[![minSdk](https://img.shields.io/badge/minSdk-19-orange.svg)](#requirements--limitations)
[![ABI](https://img.shields.io/badge/ABI-armeabi-lightgrey.svg)](#requirements--limitations)
[![Version](https://img.shields.io/badge/version-5.3.11-ff8a00.svg)](#)
[![Java](https://img.shields.io/badge/Java-1.8-007396.svg)](#architecture)
[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.24-7f52ff.svg)](#architecture)

[English](README.md) · [简体中文](README.zh_CN.md)

</div>

---

## What it is

XTCMoment is the **好友圈 (Friends Moments)** client — the social feed that lives on
an XTC kids' smartwatch. This repository contains a readable, buildable
reimplementation of that app: the feed, the publishing flows, the interaction
layer, the visibility controls and the personal page, written from scratch as
first-party Java/Kotlin with no obfuscation left behind.

Everything below is implemented in this repository and driven by the app's own
UI, not by a stub.

| | |
|---|---|
| 📝 **Rich publishing**<br>Text, mood/state, photo album, video, Live Photo, location | 💬 **Real interactions**<br>Likes, comments & replies, video barrage, gifts |
| 👀 **Granular visibility**<br>Public, private, allow-list, block-list | 🧑 **Personal page**<br>Avatar dress-up, signature bubbles, birthday & constellation |
| ✨ **AI text polish** *(legacy)*<br>Style presets, daily quota, creation history — deprecated, model-dependent | ⌚ **Watch-first UX**<br>Pull-to-refresh, drafts, notifications, assistant entry |

---

## 1 · The feed

The home page (`MomentActivity`) is the social loop in one screen.

| Capability | Details |
|---|---|
| **Moment list** | Card list of friends' moments, rendered asynchronously, with video, Live Photo, H5 and shared-content cards mixed in |
| **Pull to refresh** | Custom refresh layout with a "new moments" hint |
| **Load more** | Incremental paging as you scroll, with a visible-item listener driving prefetch |
| **Unread reminders** | Unread likes/comments surfaced as badges, plus a reminder switch you can turn off |
| **Live updates** | IM push receiver updates the list and the badges without reopening the app |
| **Feed entries** | Publish button, moment search, likes inbox, personal page, settings, assistant and share targets |
| **Search moments** | Keyword search over cached moments from the header entry (`SearchMomentActivity`): matches text, shared-content captions, locations and author names, and the results are the usual moment cards — like, comment and open details right there |
| **Empty / error states** | Friendly empty views, retry paths and version-mismatch hints |

## 2 · Publishing a moment

One publish entry point fans out to every content type
(`PublishActivity` → `PushTextActivity`, `PushPictureActivity`, `PushVideoOrPhotoServer`).

| Content type | What you get |
|---|---|
| **Text** | Plain text moment with a server-driven length limit and a live character counter |
| **Mood / state** | Pick from illustrated mood and status templates instead of typing (`MoodOrStateActivity`) |
| **Photo album** | Multi-select grid picker (9 images by default, adjustable by the server), in-app preview and multi-image viewer |
| **Video** | Record or pick a video, upload it, then play it in a dedicated player |
| **Live Photo** | Automatically detects an image + short-video pair, publishes it as its own moment type, and plays it back with motion |
| **Location** | Attach a POI to any moment, browse LBS moments and see the location star animation |
| **H5 moment** | Web content rendered as a card inside the feed (`ShowH5Activity`, `view_photo_text_h5_moment`) |
| **Shared in** | Other apps on the watch can share into 好友圈 through the share provider (share API v4, `ShareToMomentActivity`) |

Publishing is **draft-safe**: text, photos, video, location, visibility and the
AI-generated copy are all persisted (`SaveDynamic`), so a half-finished moment
survives leaving the page or a process restart.

## 3 · Interactions

| Interaction | Details |
|---|---|
| **Likes** | Like and un-like a moment, with animated like effects on the card |
| **Like inbox** | "Received hearts / flowers" lists, so a kid can see who reacted (`NewLikeActivity`, `MomentLikesActivity`) |
| **Like rules** | A rules page explaining the daily per-friend like levels, with the level table shown in-app (`LikeRuleShowActivity`) |
| **Comments** | Text comments on moments, threaded replies, and deletion of your own comment |
| **Comment limits** | Daily comment caps and a "this comment was deleted" state, both surfaced as dialogs |
| **Video barrage** | Danmaku comments overlaid on video moments, with a switch to turn them off (`BarrageFragment`, `BarragePresenter`) |
| **Gifts** | Send gifts on video moments and open the list of everyone who gifted (`GiftDetailsActivity`) |
| **Moderation** | Report content or a friend with selectable reasons, a daily report quota, and mute handling for the reported account |
| **Content safety** | Pre-publish checks raise illegal-content and sensitive-content dialogs instead of failing silently |

## 4 · Who can see it

Visibility is a first-class part of every publish, not an afterthought
(`VisibleTypeActivity`, `FriendsVisibleRangeActivity`).

| Range | Meaning |
|---|---|
| **Public** | All friends can see it |
| **Private** | Only you can see it |
| **Some friends** | Allow-list — pick exactly who may see it |
| **Hide from** | Block-list — pick exactly who may not see it |

- The **friend picker** returns to the range page, and the page remembers the
  last selection made for each range type.
- The confirm button **slides away while you scroll** and comes back when the
  list settles, keeping the small screen uncluttered.
- Visibility can be **edited after publishing**, subject to the server's daily
  change limit ("you can change it again tomorrow" is a real state in the UI).

## 5 · The personal page

`AccountInfoActivity` is the profile friends land on.

| Element | Details |
|---|---|
| **Avatar & dress-up** | Avatar with SVGA-rendered accessories bound through the watch's dress service (`HeadDressManager`, `DressProxy`) |
| **Nickname** | Synced from the watch contact store and refreshed on contact change |
| **Personalized signature** | Signature shown in animated bubbles that float up the page, with three rotating bubble styles |
| **Birthday & constellation** | Birthday text plus the matching constellation icon and accent color, all 12 signs |
| **Gender styling** | Gender-driven background artwork |
| **Account info** | Watch account details read from the system account provider |

## 6 · AI text polish *(legacy)*

> **Deprecated, and only available on some watch models.** The AI 美文 service
> has been retired upstream, and the entry is gated per device. Treat this
> module as a preserved reference implementation rather than a feature you can
> rely on — without the matching backend and module switch, the entry either
> does not appear or falls back to its error state.

The AI 美文 feature (`com.xtc.aitext`) turns a plain sentence into something
nicer before it goes out.

| Capability | Details |
|---|---|
| **Entry** | Offered from the publish flow as its own publish type, gated by the device's module switch and service availability |
| **Style presets** | A style picker with server-provided presets (`SelectStyleActivity`) |
| **Generation flow** | Dedicated editing screen with an in-progress animation and a safe exit prompt while generating |
| **Daily quota** | Remaining-uses counter, plus in-app ways to claim more uses from a watch box reward |
| **Creation history** | "My AI writing collection" — a paginated list of everything you generated (`AIRecordActivity`) |
| **Disclosure** | Explicit notices that content is AI-generated and for reference only |

## 7 · Watch integration

The app is built to behave like a system app on the watch, not a sandboxed demo.

| Integration | Details |
|---|---|
| **Voice assistant** | Exposes a `PostStatus` directive so the assistant can start a status post (`com.xtc.assistantapi.directive`) |
| **Contacts** | Contact updates and removals trigger refreshes; friends come from the watch contact store |
| **Notifications** | IM push receiver, unread counters and a self-start reminder switch |
| **Module switch** | Honours the launcher's module-switch and fun-manager broadcasts |
| **Local storage** | ORMLite database for moments, comments, likes, nicknames and templates, plus MMKV-backed preferences |
| **Sharing** | Content providers for moments, comments, likes, screenshots and app-share |
| **Keep-alive** | A keep-alive service so push notifications keep arriving |

---

## Architecture

| Layer | Choice |
|---|---|
| **Language** | Java for the original classes, Kotlin where the original was Kotlin (~2,080 first-party classes) |
| **UI** | XML layouts on `support`/`appcompat` 25.4.0 — deliberately **not** Compose, to match the original framework |
| **Pattern** | Hand-written MVP (`MvpActivity` / `MvpFragment` / `MvpPresenter`) with delegate-based lifecycle wiring |
| **Networking** | OkHttp 3.12 + Retrofit 2.9 (Gson, protobuf and RxJava adapters) |
| **Async** | RxJava 1 + RxAndroid, EventBus 3 for cross-component events |
| **Media** | Glide 4.9 (+ transformations), Lottie 2.7, SVGA Player 2.5 for avatar/effects animation |
| **Persistence** | ORMLite 5.1 over SQLite, MMKV for fast key-value storage |
| **Rendering extras** | libGDX + Spine, DanmakuFlameMaster for barrage, PhotoView for zoom |
| **Toolchain** | Gradle 8.9 (wrapper), AGP 8.6.0, Kotlin 1.9.24, JDK 17 to run Gradle, bytecode target 1.8 |

## Build

<details>
<summary><b>Prerequisites and commands</b></summary>

**Prerequisites**

- **JDK 17** — used to run Gradle (the bytecode target is still 1.8).
- **Android SDK** with **Platform 34** installed and its licences accepted.
- Network access for Gradle and Maven dependencies. The wrapper and the
  repositories are pre-pointed at Aliyun mirrors with Google / Maven Central as
  fallbacks.

**Build (Windows, PowerShell, from the repository root)**

```powershell
$env:JAVA_HOME    = '<path to your JDK 17>'
$env:ANDROID_HOME = '<path to your Android SDK>'

# gradle.properties pins the Unix-domain-socket temp dir to C:/temp
New-Item -ItemType Directory -Force 'C:\temp' | Out-Null

.\gradlew.bat :app:assembleDebug --console=plain
# or, PowerShell-native wrapper:
.\gradlew.ps1 :app:assembleDebug --console=plain
```

The debug APK lands at `app/build/outputs/apk/debug/app-debug.apk`.

**Build (Linux / macOS)**

```bash
export JAVA_HOME='<path to your JDK 17>'
export ANDROID_HOME='<path to your Android SDK>'
sh ./gradlew '-Dorg.gradle.jvmargs=-Xmx4g -Dfile.encoding=UTF-8 -Duser.language=en -Duser.country=US -Djdk.net.unixdomain.tmpdir=/tmp' \
  :app:assembleDebug --console=plain
```

`gradlew` is committed without the executable bit, so invoke it through `sh`.

**Notes**

- `build.ps1` is a convenience script for one specific development machine — it
  hard-codes the JDK path, the Gradle distribution cache and the project path.
  Use the wrapper commands above instead.
- Local prebuilt dependencies are **committed**, so a plain clone has
  everything the current build script references: 9 JARs in `prebuilt-local/`
  and the `armeabi` `.so` files in `app/src/main/prebuilt/`.
- `local.properties` is git-ignored; point `sdk.dir` at your own SDK.
- Release builds are **not signed** — the release variant builds, but this
  repository does not ship signing configuration.

</details>

## Requirements & limitations

Read this before expecting the app to run:

- **It is a watch app.** The manifest declares `android.hardware.type.watch`, and
  the UI is designed for a small round-ish screen. It is not a phone client.
- **Only the 32-bit `armeabi` ABI is packaged.** There are no `arm64-v8a`,
  `armeabi-v7a` or `x86` binaries, so 64-bit-only devices and x86 emulators are
  out of scope as-is.
- **It expects the XTC watch system environment.** IM bridging goes through the
  launcher's `BridgeService`, accounts come from the watch account provider,
  friends come from the watch contact store, and location comes from the
  launcher's location service. On a generic Android device, features that
  depend on those services will not work.
- **Cloud-backed features need the matching backend.** Gift sending and gift
  lists, comment sync and likes all talk to remote endpoints; without a
  reachable service they degrade to their error states.
- **AI 美文 is deprecated and model-dependent.** It is only offered on some
  watch models and its upstream service has been retired, so do not expect it
  to be available on every device — or to work at all.
- **Compiling and running are different bars.** The code builds into an APK; a
  full system-app replacement also depends on the device's signing and system
  integration policy.

## Project layout

```
XTCMoment/
├── app/
│   ├── build.gradle                     # app module: SDK levels, deps, packaging
│   └── src/main/
│       ├── AndroidManifest.xml          # components, permissions, system metadata
│       ├── java/com/xtc/                # first-party Java sources
│       │   ├── moment/module/           # feed, publish, comment, like, gift, barrage,
│       │   │                            # details, personalinfo, scope, share, report…
│       │   ├── aitext/                  # AI text feature (edit, style, history, service)
│       │   ├── ui/widget/               # shared watch UI kit (buttons, dialogs, privacy)
│       │   ├── architecture/mvp/        # MVP base classes and lifecycle delegates
│       │   └── …                        # log, net, db, contact/account/location clients
│       ├── kotlin/com/xtc/              # Kotlin sources (config, IPC, alpha player, …)
│       ├── res/                         # layouts, drawables, values, animations
│       ├── assets/                      # bundled static assets
│       └── prebuilt/armeabi/            # JNI libraries shipped as-is
├── prebuilt-local/                      # JARs not available from public repositories
├── proto/                               # request/domain protocol definitions
├── docs/PORTING_NOTES.md                # reimplementation notes
├── tools/                               # migration and dependency helper scripts
├── gradle/wrapper/                      # Gradle 8.9 wrapper
└── build.gradle · settings.gradle       # root build, single :app module
```

## Contributing

Issues and pull requests are welcome. Useful things to keep in mind:

- Keep first-party code **readable and unobfuscated** — descriptive class,
  method and field names, with comments where behaviour is subtle.
- Match the original UI framework (XML layouts + `support`/`appcompat`), not
  Compose.
- Prefer **real upstream libraries** over re-decompiled copies, and don't add a
  dependency that duplicates resources already merged into `res/`.

## License

Released under the [GNU General Public License v3.0](LICENSE).

<div align="center">
<sub>好友圈 · Friends Moments — built for small wrists and big friend circles.</sub>
</div>