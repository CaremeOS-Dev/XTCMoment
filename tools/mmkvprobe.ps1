$ErrorActionPreference='Continue'
foreach($v in @('1.2.0','1.2.1','1.2.2','1.2.3','1.2.4','1.2.5','1.2.6','1.2.7','1.2.8','1.2.9','1.2.10','1.2.11','1.2.12','1.2.13','1.2.14','1.2.15','1.2.16')){
  $d="_mmkv\x$v"
  if(Test-Path $d){ Remove-Item -Recurse -Force $d -ErrorAction SilentlyContinue }
  Expand-Archive -Path "_mmkv\$v.aar" -DestinationPath $d -Force -ErrorAction SilentlyContinue
  $so=Get-Item "$d\jni\armeabi\libmmkv.so" -ErrorAction SilentlyContinue
  $sig=''
  if(Test-Path "$d\classes.jar"){ $sig = (& 'D:\Program Files\Java\jdk-17\bin\javap.exe' -classpath "$d\classes.jar" com.tencent.mmkv.MMKV 2>&1 | Select-String -Pattern 'isFileValid') -join ' | ' }
  if($so){ Write-Output "$v so=$($so.Length) :: $sig" } else { Write-Output "$v NO-SO :: $sig" }
}