$ErrorActionPreference='Continue'
foreach($v in @('1.3.0','1.3.1','1.3.2','1.3.3','1.3.4','1.3.5','1.3.6','1.3.7','1.3.8','1.3.9','1.3.10','1.3.11','1.3.12','1.3.13','1.3.14','1.3.15')){
  $aar="_mmkv\c\$v.aar"
  if(-not (Test-Path $aar)){
    try{ Invoke-WebRequest -Uri "https://maven.aliyun.com/repository/public/com/tencent/mmkv/$v/mmkv-$v.aar" -OutFile $aar -UseBasicParsing -TimeoutSec 15 }catch{ Write-Output "miss $v"; continue }
  }
  $d="_mmkv\c\x$v"
  if(Test-Path $d){ Remove-Item -Recurse -Force $d -ErrorAction SilentlyContinue }
  Expand-Archive -Path $aar -DestinationPath $d -Force -ErrorAction SilentlyContinue
  $so = Get-Item "$d\jni\armeabi\libmmkv.so" -ErrorAction SilentlyContinue
  $sig = ''
  if(Test-Path "$d\classes.jar"){ $sig = (& 'D:\Program Files\Java\jdk-17\bin\javap.exe' -classpath "$d\classes.jar" com.tencent.mmkv.MMKV 2>&1 | Select-String -Pattern 'isFileValid|version\(\)') -join ' | ' }
  $len = if($so){$so.Length}else{'NO-armeabi'}
  Write-Output ("{0,-7} armeabi={1,-10} {2}" -f $v,$len,$sig)
}