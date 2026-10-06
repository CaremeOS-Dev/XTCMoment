$ErrorActionPreference='Stop'
$aj="$env:LOCALAPPDATA\Android\Sdk\platforms\android-34\android.jar"
$cp="$aj;prebuilt-libs\support-compat-25.4.0.jar;prebuilt-libs\support-core-ui-25.4.0.jar;prebuilt-libs\support-annotations-25.4.0.jar"
Remove-Item -Recurse -Force _gen\pv\out -ErrorAction SilentlyContinue
New-Item -ItemType Directory -Force -Path _gen\pv\out | Out-Null
$srcs = (Get-ChildItem _gen\pv\src\com\github\chrisbanes\photoview\*.java).FullName
& 'D:\Program Files\Java\jdk-17\bin\javac.exe' -source 8 -target 8 -nowarn -cp $cp -d _gen\pv\out $srcs
Write-Output "classes: $((Get-ChildItem _gen\pv\out -Recurse -File).Count)"