# XTCMoment 移植准则

参考：`D:\CaremiumWorkspace\Research\SystemApps\XTCMoment\{decompiled\sources,smali}`（只读）

## 硬性要求（来自 goal）
1. 原厂用什么语言（java/kotlin）写 class，就用什么语言重写。
2. 界面沿用原厂框架（XML + support/appcompat，非 Compose）。
3. `.so` 放 `prebuilt/`。
4. 第一方 Java/Kotlin **纯手写**，禁止直接复制反编译 java 改一改。
5. 所有方法名/变量名/类名必须可读、有注释、**无混淆痕迹**（如 `Glide.c()` → `Glide.with()`）。
6. 行为、样式、文本、特征与原厂一致，不改原逻辑。
7. gradle/maven/google 走阿里云镜像。
8. 反编译产物里的第三方库 → 外部引入（真实上游库），不手写。
9. 结果必须可编译、可用、稳定、可替换原厂 APK。

## 已否决的方案（证据）
- **不参考 i3launcher**：其代码与本项目耦合方式不同、包结构已分叉（引用 `com.xtc.i3launcher.*`），
  强行复用导致 25+ 文件无法编译，故全部弃用。仅借鉴其 Gradle/prebuilt 目录组织思路。

## 已确认的架构结论（证据）
- apktool 的 `res/` 是**已合并**的完整资源集（含 appcompat/lottie 等库资源），第三方库**不能用 AAR 依赖**
  （资源会重复），必须用从真实 AAR 抽出的 `classes.jar`（见 `prebuilt-libs/`）。
- 原厂对第三方库做过混淆+插桩（如 `okhttp3.Cookie` 反向 import `com.xtc.moment.module.Constants`），
  但第一方去混淆后调用**真实上游 API**，故采用真实上游库。
- 构建环境：JDK 17（`D:\Program Files\Java\jdk-17`），Gradle 8.9，AGP 8.6.0，Kotlin 1.9.24。
- Gradle 需 `-Djdk.net.unixdomain.tmpdir=C:/temp`（默认 TEMP 是 8.3 短名，AF_UNIX 建管道失败）。

## 规模
- 第一方约 2080 个 class（`com.xtc.*`），Kotlin 约 70 个，其余 Java。
