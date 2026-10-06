# XTCMoment 重写工程规范（所有参与者必须遵守）

## 参考物（只读，禁止修改）
- 反编译 Java 源码：`D:\CaremiumWorkspace\Research\SystemApps\XTCMoment\decompiled\sources`
- 反编译资源/apktool 产物：`D:\CaremiumWorkspace\Research\SystemApps\XTCMoment\smali`
- 资源以 apktool 合并产物为准（`smali` 同级的 `res` 才是标准资源；`decompiled\res` 不标准，不要用）

## 产物位置
- 工程根：`D:\CaremiumWorkspace\Sources\moment`
- Java：`app\src\main\java\...`
- Kotlin：`app\src\main\kotlin\...`
- 预编译 so：`app\src\main\prebuilt\`（按 ABI 子目录）
- 预编译第三方 jar：`prebuilt-libs\`

## 硬性规则
1. **语言必须与原厂一致**：判断依据是反编译类里有没有 `kotlin.Metadata` 注解 / `JADX INFO: compiled from: X.kt`。有 → 写 Kotlin；没有 → 写 Java。
2. **纯手写**：禁止把反编译 Java 复制过来改几个字。必须重新组织代码：变量名、参数名、局部变量名全部改成可读英文名，并加必要注释解释业务含义。
3. **不得有混淆痕迹**：`a()`、`b`、`c`、`AnonymousClass1`、`$1`、`p0`、`v0` 这类名字一律不允许出现在产物中。
   - 反编译里的短名要还原成语义名，例如：
     - `Glide.b(ctx)` → `Glide.get(ctx)`（Glide 4.9 的混淆名，实际 API 是 `get`）
     - `Glide.with(view)`、`Glide.getPhotoCacheDir(ctx)`
     - `JSONUtil.a(str, key)` → `JSONUtil.parseToJson(str, key)`
     - `RSAUtil.b(...)` → `RSAUtil.encryptWithPublicKey(...)`
     - `SharedManager.a(ctx)` → `SharedManager.getInstance(ctx)`
   - 如果调用的是**第三方库**里已存在的混淆类，不要改第三方库；只保证**本工程写的代码**没有混淆痕迹。
4. **不得修改原逻辑**：行为、判断分支、常量、字符串文本、文案、资源 id、Manifest 声明都必须一致。
5. **第三方库一律外部引入**，不手写。已放进 `prebuilt-libs` 的库见下节；若发现缺失的第三方库，先告诉主控，不要自己造。
6. **Gradle / Maven / Google 全部走阿里云镜像**：`https://maven.aliyun.com/repository/public/`、`.../google/`、`.../gradle-plugin/`。
7. 每个类写完后，若引用了尚未重写的类，属于正常现象（工程会阶段性编译失败），不要为此删逻辑或造桩；但自己文件的语法必须正确。

## 已就绪的第三方 jar（prebuilt-libs）
support 25.4.0 全家桶、design 25.4.0、constraint-layout 1.1.3、glide 4.9.0、glide-transformations 4.3.0、
rxjava 1.3.8、rxandroid 1.2.1、rxbinding 1.0.1、gson 2.8.9、fastjson 1.2.62、okhttp 3.12.0、okio 1.15.0、
retrofit 2.9.0 + converter-gson/protobuf + adapter-rxjava、wire-runtime 2.2.0、protobuf-java 3.11.4、
ormlite-android/core 5.1、eventbus 3.1.1、lottie 2.7.0、mmkv 1.2.10、qiniu-android-sdk 8.5.2、
commons-lang3 3.9、exifinterface 25.4.0、kotlinx-coroutines 1.5.2、zxing core 3.3.3。
**尚未引入**：SVGA-Player（`com.opensource.svgaplayer`，Kotlin）、DanmakuFlameMaster（`master.flame.danmaku`）、
gdx（`com.badlogic.gdx`）、mp4parser（`com.googlecode.mp4parser`）、spine、baidu speech、`com.github.chrisbanes`（PhotoView）、
`com.github.moduth`（BlockCanary）、`com.ss.ugc`、`com.oyp.nsfw`、`com.google.webp`、`tv.cjump.jni`、`androidx.annotation`。
遇到这些包的引用先照常写调用，主控会补 jar。

## 已重写完成的包（可直接调用，不要重复实现）
`com.xtc.log`、`com.xtc.architecture.mvp`、`com.xtc.ui.widget.*`、`com.xtc.utils.*`、`com.xtc.utils_screenshot_carry_data`、
`com.xtc.funlist`、`com.xtc.bigdata.collector`、`com.xtc.bigdata.common`、`com.xtc.common.bigdata`、`com.xtc.httplib`、
`com.xtc.system.account`、`com.xtc.dns.api`、`com.xtc.dns.client`、`com.xtc.domain`、`com.xtc.im.transpond`、`com.xtc.dispatch`、
`com.xtc.database.ormlite`、`com.xtc.ipc.client`、`com.xtc.dataservice.api`、`com.xtc.watch`、`com.xtc.web.core.CoreConstants`、
`com.xtc.qiniu`、`com.xtc.xtcoco`、`com.xtc.data.common.database`、`com.xtc.virtualselfapi.constants`、`com.xtc.virtualselfapi.bean.db`、
`com.xtc.virtualselfapi.helper.VirtualSelfDbHelper`、`com.xtc.moment.db`、`com.xtc.moment.util`、`com.xtc.moment.net.bean`、
`com.xtc.moment.module.bean`（部分）

## 构建
```
& pwsh -NoProfile -File 'D:\CaremiumWorkspace\Sources\moment\build.ps1' -Tasks ':app:compileDebugJavaWithJavac'
& pwsh -NoProfile -File 'D:\CaremiumWorkspace\Sources\moment\build.ps1' -Tasks ':app:compileDebugKotlin'
```
JDK17：`D:\Program Files\Java\jdk-17`。Gradle 8.9 wrapper 已配置。

## 查缺失文件
```
& pwsh -NoProfile -File tools\missing.ps1 -Filter moment/module/widget
& pwsh -NoProfile -File tools\missing.ps1 -Summary
```

## 提交
用 git，小步提交，message 形如 `feat(shareapi): hand-write XxxObject and share strategy`。
提交前不要 `git add` 别人的未完成改动（用 `git add <具体路径>`）。