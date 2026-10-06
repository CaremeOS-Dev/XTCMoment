$ErrorActionPreference='Stop'
function RepAll([string]$f,[string]$a,[string]$b){ if(Test-Path $f){ $t=[System.IO.File]::ReadAllText($f); if($t.Contains($a)){ $t=$t.Replace($a,$b); [System.IO.File]::WriteAllText($f,$t,(New-Object System.Text.UTF8Encoding $false)); Write-Output "fixed $f :: $a" } else { Write-Output "no-match $f :: $a" } } }
$f='app\src\main\java\com\xtc\moment\module\share\adapter\ShareAdapter.java'
RepAll $f 'VideoViewHolder videoHolder = null;' 'ShareVideoViewHolder videoHolder = null;'
RepAll $f 'holder instanceof VideoViewHolder' 'holder instanceof ShareVideoViewHolder'
RepAll $f '(VideoViewHolder) holder' '(ShareVideoViewHolder) holder'
RepAll $f 'final VideoViewHolder playLogoHolder = videoHolder;' 'final ShareVideoViewHolder playLogoHolder = videoHolder;'
RepAll $f 'ShareAppStartUtil.startApp(mContext, moment);' 'StartUtils.startApp(mContext, moment);'
RepAll $f 'loadDiskPhoto(photoMsg, roundedCorner, moment, holder);' 'loadDiskPhoto(photoMsg, roundedCorner, moment, (AbsViewHolder) holder);'