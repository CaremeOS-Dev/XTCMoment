$ErrorActionPreference='Stop'
$aj="$env:LOCALAPPDATA\Android\Sdk\platforms\android-34\android.jar"
$cp="$aj;prebuilt-libs\gdx-1.9.10.jar;prebuilt-libs\gdx-backend-android-1.9.10.jar"
Remove-Item -Recurse -Force _gen\tv\out -ErrorAction SilentlyContinue
New-Item -ItemType Directory -Force -Path _gen\tv\out | Out-Null
$srcs = (Get-ChildItem _gen\tv\src -Recurse -Filter *.java | Where-Object { $_.Name -ne 'GLTextureView20.java' }).FullName
& 'D:\Program Files\Java\jdk-17\bin\javac.exe' -source 8 -target 8 -nowarn -cp $cp -d _gen\tv\out $srcs 2>&1 | Select-Object -Last 10
Write-Output "classes: $((Get-ChildItem _gen\tv\out -Recurse -File -ErrorAction SilentlyContinue).Count)"