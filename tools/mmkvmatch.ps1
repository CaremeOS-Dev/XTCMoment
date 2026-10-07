$ErrorActionPreference='Continue'
# native method names declared in the reference MMKV.java (the one that pairs with the shipped so)
$ref = Select-String -Path 'D:\CaremiumWorkspace\Research\SystemApps\XTCMoment\decompiled\sources\com\tencent\mmkv\MMKV.java' -Pattern 'native ' |
  ForEach-Object { if($_ -match 'native\s+[\w\[\]\.<>]+\s+(\w+)\s*\('){ $matches[1] } } | Sort-Object -Unique
Write-Output "REF native count=$($ref.Count)"
Write-Output ("REF: " + ($ref -join ','))
Write-Output ""
foreach($v in @('1.2.0','1.2.1','1.2.2','1.2.3','1.2.4','1.2.5','1.2.6','1.2.7','1.2.8','1.2.9','1.2.10','1.2.11','1.2.12','1.2.13','1.2.14','1.2.15','1.2.16')){
  $jar = "_mmkv\c\x$v\classes.jar"
  if(-not (Test-Path $jar)){ Write-Output "$v (no jar)"; continue }
  $out = & 'D:\Program Files\Java\jdk-17\bin\javap.exe' -p -classpath $jar com.tencent.mmkv.MMKV 2>&1
  $cur = $out | Where-Object { $_ -match 'native' } | ForEach-Object { if($_ -match '\s(\w+)\s*\('){ $matches[1] } } | Sort-Object -Unique
  $missing = $ref | Where-Object { $cur -notcontains $_ }
  $extra   = $cur | Where-Object { $ref -notcontains $_ }
  Write-Output ("{0,-7} n={1,-3} missing=[{2}] extra=[{3}]" -f $v,$cur.Count,($missing -join ','),($extra -join ','))
}