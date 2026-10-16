param(
    [string[]]$Tasks = @(':app:assembleDebug'),
    [switch]$NoDaemon,
    # 编译期网络开关，等价于 -Pmoment.baseUrl=... / -Pmoment.encrypt=...，
    # 但走环境变量，避免 PowerShell 把值里的 ":" 当成参数分隔符。
    #   .\build.ps1 -BaseUrl 127.0.0.1:8000 -Encrypt false
    [string]$BaseUrl = '',
    [string]$Encrypt = ''
)
$env:JAVA_HOME = 'D:\Program Files\Java\jdk-17'
New-Item -ItemType Directory -Force -Path 'C:\temp' | Out-Null
$env:TEMP = 'C:\temp'
$env:TMP  = 'C:\temp'
if ($BaseUrl) { $env:MOMENT_BASE_URL = $BaseUrl }
if ($Encrypt) { $env:MOMENT_ENCRYPT = $Encrypt }
# 用工程自带的 wrapper，不再依赖某个固定路径的 gradle 发行版。
$gradlew = Join-Path $PSScriptRoot 'gradlew.bat'
$args = @('--console=plain')
if ($NoDaemon) { $args += '--no-daemon' }
$args += $Tasks
& $gradlew @args
exit $LASTEXITCODE
