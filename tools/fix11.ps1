$ErrorActionPreference='Stop'
# DesUtils in com.xtc.bigdata.common.utils
$f='app\src\main\java\com\xtc\bigdata\common\utils\DesUtils.java'
$t=[System.IO.File]::ReadAllText($f)
$t=$t.Replace('} catch (UnsupportedEncodingException e) {','} catch (Exception e) {')
[System.IO.File]::WriteAllText($f,$t,(New-Object System.Text.UTF8Encoding $false))
Write-Output "DesUtils(bigdata) patched"

# ShareAdapter: loadNoFailureUrl holder param -> AbsViewHolder (callers pass AbsViewHolder)
$f='app\src\main\java\com\xtc\moment\module\share\adapter\ShareAdapter.java'
$t=[System.IO.File]::ReadAllText($f)
$t=$t.Replace('            DbMoment moment, String downloadUrl, RecyclerView.ViewHolder holder) {','            DbMoment moment, String downloadUrl, AbsViewHolder holder) {')
[System.IO.File]::WriteAllText($f,$t,(New-Object System.Text.UTF8Encoding $false))
Write-Output "ShareAdapter patched"