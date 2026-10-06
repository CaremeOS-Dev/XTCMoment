$ErrorActionPreference='Stop'
$f='app\src\main\java\com\xtc\moment\module\share\adapter\ShareAdapter.java'
$t=[System.IO.File]::ReadAllText($f)
$t=$t.Replace('            final boolean roundedCorner, final DbMoment moment, final RecyclerView.ViewHolder holder, long deadline,','            final boolean roundedCorner, final DbMoment moment, final AbsViewHolder holder, long deadline,')
$t=$t.Replace('            DbMoment moment, String downloadUrl, RecyclerView.ViewHolder holder) {','            DbMoment moment, String downloadUrl, AbsViewHolder holder) {')
[System.IO.File]::WriteAllText($f,$t,(New-Object System.Text.UTF8Encoding $false))
Write-Output "reverted"