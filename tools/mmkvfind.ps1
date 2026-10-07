$ErrorActionPreference='Continue'
$target=(Get-FileHash 'app\src\main\prebuilt\armeabi\libmmkv.so' -Algorithm SHA256).Hash
$targetLen=(Get-Item 'app\src\main\prebuilt\armeabi\libmmkv.so').Length
Write-Output "target sha256=$target len=$targetLen"
$vers=@()
foreach($maj in 1){ foreach($min in 2){ foreach($pat in 0..30){ $vers += "1.2.$pat" } } }
foreach($maj in 1){ foreach($min in 3){ foreach($pat in 0..10){ $vers += "1.3.$pat" } } }
foreach($v in $vers){
  $aar="_mmkv\c\$v.aar"
  if(-not (Test-Path $aar)){
    try{ Invoke-WebRequest -Uri "https://maven.aliyun.com/repository/public/com/tencent/mmkv/$v/mmkv-$v.aar" -OutFile $aar -UseBasicParsing -TimeoutSec 12 }catch{ continue }
  }
  $d="_mmkv\c\x$v"
  if(Test-Path $d){ Remove-Item -Recurse -Force $d -ErrorAction SilentlyContinue }
  Expand-Archive -Path $aar -DestinationPath $d -Force -ErrorAction SilentlyContinue
  $so=Get-Item "$d\jni\armeabi\libmmkv.so" -ErrorAction SilentlyContinue
  if(-not $so){ $so = Get-Item "$d\jni\armeabi-v7a\libmmkv.so" -ErrorAction SilentlyContinue }
  if($so){
    $h=(Get-FileHash $so.FullName -Algorithm SHA256).Hash
    $mark = if($h -eq $target){'  <<<< MATCH' } elseif($so.Length -eq $targetLen){'  <<<< SAME-LEN'} else {''}
    Write-Output "$v len=$($so.Length)$mark"
  }
}