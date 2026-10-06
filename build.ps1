param([string[]]$Tasks = @(':app:assembleDebug'), [switch]$NoDaemon)
$env:JAVA_HOME = 'D:\Program Files\Java\jdk-17'
New-Item -ItemType Directory -Force -Path 'C:\temp' | Out-Null
$env:TEMP = 'C:\temp'
$env:TMP  = 'C:\temp'
$gradle = 'C:\Users\Administrator\.gradle\wrapper\dists\gradle-8.9-bin\1wcpju915gw2zs5iloejomdjk\gradle-8.9\bin\gradle.bat'
$args = @('-p', 'D:\CaremiumWorkspace\Sources\moment', '--console=plain')
if ($NoDaemon) { $args += '--no-daemon' }
$args += $Tasks
& $gradle @args
