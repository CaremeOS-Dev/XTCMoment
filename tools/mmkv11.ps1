$ErrorActionPreference='Continue'
$ref = Select-String -Path 'D:\CaremiumWorkspace\Research\SystemApps\XTCMoment\decompiled\sources\com\tencent\mmkv\MMKV.java' -Pattern 'native ' |
  ForEach-Object { if($_ -match 'native\s+[\w\[\]\.<>]+\s+(\w+)\s*\('){ $matches[1] } } | Sort-Object -Unique
foreach($v in @('1.0.0','1.0.1','1.0.2','1.0.3','1.0.4','1.0.5','1.0.6','1.0.7','1.0.8','1.0.9','1.1.0','1.1.1','1.1.2','1.1.3','1.1.4','1.1.5','1.1.6','1.1.7','1.1.8','1.1.9')){
  $aar="_mmkv\o\$v.aar"
  if(-not (Test-Path $aar)){
    try{ Invoke-WebRequest -Uri "https://maven.aliyun.com/repository/public/com/tencent/mmkv/$v/mmkv-$v.aar" -OutFile $aar -UseBasicParsing -TimeoutSec 15 }catch{ Write-Output "miss $v"; continue }
  }
  $d="_mmkv\o\x$v"
  if(Test-Path $d){ Remove-Item -Recurse -Force $d -ErrorAction SilentlyContinue }
  Expand-Archive -Path $aar -DestinationPath $d -Force -ErrorAction SilentlyContinue
  $so = Get-Item "$d\jni\armeabi\libmmkv.so" -ErrorAction SilentlyContinue
  $jar="$d\classes.jar"
  $cur=@()
  if(Test-Path $jar){ $cur = (& 'D:\Program Files\Java\jdk-17\bin\javap.exe' -p -classpath $jar com.tencent.mmkv.MMKV 2>&1 | Where-Object { $_ -match 'native' } | ForEach-Object { if($_ -match '\s(\w+)\s*\('){ $matches[1] } } | Sort-Object -Unique) }
  $missing = $ref | Where-Object { $cur -notcontains $_ }
  $extra   = $cur | Where-Object { $ref -notcontains $_ }
  $len = if($so){$so.Length}else{'NO-armeabi'}
  Write-Output ("{0,-7} armeabi={1,-10} n={2,-3} missing=[{3}] extra=[{4}]" -f $v,$len,$cur.Count,($missing -join ','),($extra -join ','))
}