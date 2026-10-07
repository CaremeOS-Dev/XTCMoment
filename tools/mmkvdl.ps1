$ErrorActionPreference='Continue'
foreach($v in @('1.2.10','1.2.11','1.2.12','1.2.13','1.2.14','1.2.15','1.2.16','1.2.17','1.2.18','1.2.19','1.2.20','1.2.21','1.2.22','1.2.23','1.2.24','1.2.25')){
  $aar="_mmkv\c\$v.aar"
  if(-not (Test-Path $aar)){
    try{ Invoke-WebRequest -Uri "https://maven.aliyun.com/repository/public/com/tencent/mmkv/$v/mmkv-$v.aar" -OutFile $aar -UseBasicParsing -TimeoutSec 15; Write-Output "got $v" }catch{ Write-Output "miss $v"; continue }
  }
  $d="_mmkv\c\x$v"
  if(Test-Path $d){ Remove-Item -Recurse -Force $d -ErrorAction SilentlyContinue }
  Expand-Archive -Path $aar -DestinationPath $d -Force -ErrorAction SilentlyContinue
  Get-ChildItem "$d\jni" -Directory -ErrorAction SilentlyContinue | ForEach-Object {
    $so=Get-Item (Join-Path $_.FullName 'libmmkv.so') -ErrorAction SilentlyContinue
    if($so){ Write-Output ("{0,-8} {1,-14} {2}" -f $v,$_.Name,$so.Length) }
  }
}