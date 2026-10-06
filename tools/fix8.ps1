$ErrorActionPreference='Stop'
function SetLine([string]$f,[int]$ln,[string]$text){ $lines=[System.IO.File]::ReadAllLines($f); $lines[$ln-1]=$text; [System.IO.File]::WriteAllLines($f,$lines); Write-Output "set $f : $ln" }

# CrashHandler: catch Exception (writeString does not throw IOException)
$f='app\src\main\java\com\xtc\bigdata\collector\exception\CrashHandler.java'
$lines=[System.IO.File]::ReadAllLines($f)
for($i=0;$i -lt $lines.Count;$i++){ if($lines[$i] -match 'FileUtils\.writeString\(new File\(filePath\), stackTrace, true\);'){ $lines[$i+1]='        } catch (Exception e) {'; break } }
[System.IO.File]::WriteAllLines($f,$lines)

# ShareAdapter: remove redundant bridge override (lines 185-189)
$f='app\src\main\java\com\xtc\moment\module\share\adapter\ShareAdapter.java'
$lines=New-Object System.Collections.Generic.List[string]
$skip=0
foreach($l in [System.IO.File]::ReadAllLines($f)){
  if($l -match '^    public void onBindViewHolder\(com\.xtc\.ui\.widget\.recycler\.BaseHolder baseHolder, int position, List<Object> payloads\) \{'){ $skip=3; continue }
  if($skip -gt 0){ $skip--; continue }
  [void]$lines.Add($l)
}
[System.IO.File]::WriteAllLines($f,$lines)
Write-Output "shareadapter bridge removed"

# make loadDiskPhoto non-private and widen pullNewUrl holder param
$f='app\src\main\java\com\xtc\moment\module\share\adapter\ShareAdapter.java'
$t=[System.IO.File]::ReadAllText($f)
$t=$t.Replace('        private void loadDiskPhoto(PhotoMsg photoMsg, boolean roundedCorner, final DbMoment moment,','        void loadDiskPhoto(PhotoMsg photoMsg, boolean roundedCorner, final DbMoment moment,')
$t=$t.Replace('            final boolean roundedCorner, final DbMoment moment, final AbsViewHolder holder, long deadline,','            final boolean roundedCorner, final DbMoment moment, final RecyclerView.ViewHolder holder, long deadline,')
[System.IO.File]::WriteAllText($f,$t,(New-Object System.Text.UTF8Encoding $false))
Write-Output "widened"