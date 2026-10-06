$ErrorActionPreference='Stop'
# DesUtils: new String(raw,i,2,UTF_8) does not throw UnsupportedEncodingException -> catch Exception
$f='app\src\main\java\com\xtc\utils\encode\DesUtils.java'
$t=[System.IO.File]::ReadAllText($f)
$t=$t.Replace('            } catch (UnsupportedEncodingException e) {','            } catch (Exception e) {')
[System.IO.File]::WriteAllText($f,$t,(New-Object System.Text.UTF8Encoding $false))
Write-Output "DesUtils patched"

# ShareAdapter: glideWithInto holder -> RecyclerView.ViewHolder
$f='app\src\main\java\com\xtc\moment\module\share\adapter\ShareAdapter.java'
$t=[System.IO.File]::ReadAllText($f)
$t=$t.Replace('    private void glideWithInto(Context context, ImageView imageView, String url, boolean roundedCorner,','    private void glideWithInto(Context context, ImageView imageView, String url, boolean roundedCorner,')
[System.IO.File]::WriteAllText($f,$t,(New-Object System.Text.UTF8Encoding $false))
Write-Output "done"