$ErrorActionPreference='Continue'
$vers=@()
foreach($pat in 0..30){ $vers += "1.2.$pat" }
foreach($pat in 0..10){ $vers += "1.3.$pat" }
foreach($pat in 0..6){ $vers += "1.4.$pat" }
foreach($v in $vers){
  $d="_mmkv\c\x$v"
  if(-not (Test-Path $d)){ continue }
  $abis = Get-ChildItem "$d\jni" -Directory -ErrorAction SilentlyContinue | ForEach-Object { $_.Name }
  foreach($abi in $abis){
    $so = Get-Item "$d\jni\$abi\libmmkv.so" -ErrorAction SilentlyContinue
    if($so){ Write-Output ("{0,-8} {1,-14} {2}" -f $v,$abi,$so.Length) }
  }
}