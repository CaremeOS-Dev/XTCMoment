$ErrorActionPreference='Stop'
function Rep([string]$f,[string]$a,[string]$b){ if(Test-Path $f){ $t=[System.IO.File]::ReadAllText($f); if($t.Contains($a)){ $t=$t.Replace($a,$b); [System.IO.File]::WriteAllText($f,$t,(New-Object System.Text.UTF8Encoding $false)); Write-Output "fixed $f" } else { Write-Output "no-match $f" } } }
function RepAll([string]$f,[string]$a,[string]$b){ if(Test-Path $f){ $t=[System.IO.File]::ReadAllText($f); $t=$t.Replace($a,$b); [System.IO.File]::WriteAllText($f,$t,(New-Object System.Text.UTF8Encoding $false)); Write-Output "replaced $f" } }
RepAll 'app\src\main\java\com\xtc\moment\module\widget\PhotoPreviewActivity.java' 'setVisibility(VISIBLE)' 'setVisibility(View.VISIBLE)'
RepAll 'app\src\main\java\com\xtc\moment\module\widget\PhotoPreviewActivity.java' 'setVisibility(GONE)' 'setVisibility(View.GONE)'
RepAll 'app\src\main\java\com\xtc\moment\module\widget\PhotoPreviewActivity.java' 'setVisibility(INVISIBLE)' 'setVisibility(View.INVISIBLE)'
RepAll 'app\src\main\java\com\xtc\moment\module\widget\livephotoView\PlayLivePhotoActivity.java' 'setVisibility(VISIBLE)' 'setVisibility(View.VISIBLE)'
RepAll 'app\src\main\java\com\xtc\moment\module\widget\livephotoView\PlayLivePhotoActivity.java' 'setVisibility(GONE)' 'setVisibility(View.GONE)'
RepAll 'app\src\main\java\com\xtc\moment\module\widget\livephotoView\PlayLivePhotoActivity.java' 'setVisibility(INVISIBLE)' 'setVisibility(View.INVISIBLE)'
Rep 'app\src\main\java\com\xtc\ui\widget\recycler\HorizontalDividerDecoration.java' 'DividerDecoration.ORIENTATION_HORIZONTAL' 'DividerDecoration.HORIZONTAL_LIST'
Rep 'app\src\main\java\com\xtc\moment\util\OneBtnDialog.java' '    @Override
    public void setBackground(Drawable background) {' '    public void setBackgroundDrawable(Drawable background) {'