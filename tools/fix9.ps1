$ErrorActionPreference='Stop'
$f='app\src\main\java\com\xtc\bigdata\collector\exception\CrashHandler.java'
$lines=[System.IO.File]::ReadAllLines($f)
for($i=0;$i -lt $lines.Count;$i++){ if($lines[$i] -match 'FileUtils\.writeFile\(new File\(filePath\), stackTrace, true\);'){ $lines[$i+1]='        } catch (Exception e) {'; break } }
[System.IO.File]::WriteAllLines($f,$lines)

# ShareAdapter: broaden loadNoFailureUrl holder param
$f='app\src\main\java\com\xtc\moment\module\share\adapter\ShareAdapter.java'
$t=[System.IO.File]::ReadAllText($f)
$t=$t.Replace('            DbMoment moment, String downloadUrl, AbsViewHolder holder) {','            DbMoment moment, String downloadUrl, RecyclerView.ViewHolder holder) {')
# remove misplaced @Override on constructor
$t=$t.Replace('    @Override
    public ShareAdapter(Activity activity, boolean isSelf, String name, String iconPath,','    public ShareAdapter(Activity activity, boolean isSelf, String name, String iconPath,')
[System.IO.File]::WriteAllText($f,$t,(New-Object System.Text.UTF8Encoding $false))
Write-Output "done"