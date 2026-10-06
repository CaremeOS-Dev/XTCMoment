$ErrorActionPreference='Stop'
function Rep([string]$f,[string]$a,[string]$b){ if(Test-Path $f){ $t=[System.IO.File]::ReadAllText($f); if($t.Contains($a)){ $t=$t.Replace($a,$b); [System.IO.File]::WriteAllText($f,$t,(New-Object System.Text.UTF8Encoding $false)); Write-Output "fixed $f" } else { Write-Output "no-match $f :: $a" } } else { Write-Output "missing $f" } }

# View constants imports
Rep 'app\src\main\java\com\xtc\moment\module\widget\PhotoPreviewActivity.java' 'import android.view.View;' 'import android.view.View;'
Rep 'app\src\main\java\com\xtc\moment\module\widget\livephotoView\PlayLivePhotoActivity.java' 'import android.view.View;' 'import android.view.View;'

# styleable names
Rep 'app\src\main\java\com\xtc\ui\widget\scalablecontainer\AppNestedScrollView.java' 'R.styleable.AppNestedScrollView_asv_anim_scale' 'R.styleable.AppNestedScrollView_AppNestedScrollView_asv_anim_scale'
Rep 'app\src\main\java\com\xtc\ui\widget\recycler\HorizontalDividerDecoration.java' 'DividerDecoration.HORIZONTAL' 'DividerDecoration.ORIENTATION_HORIZONTAL'
Rep 'app\src\main\java\com\xtc\moment\util\switchs\WeiChatFunSwitchUtil.java' 'FunSwitchUtil.refresh();' 'FunSwitchUtil.clearCache();'

# MomentVideoViewComment clickView(context,...)
Rep 'app\src\main\java\com\xtc\moment\module\widget\MomentVideoViewComment.java' 'clickView(moment, listener);' 'clickView(getContext(), moment, listener);'

# BuildConfig self-reference
Rep 'app\src\main\java\com\xtc\moment\MomentApplication.java' 'BuildConfig.GitSHA' 'com.xtc.moment.BuildConfig.GitSHA'
Rep 'app\src\main\java\com\xtc\moment\MomentApplication.java' 'BuildConfig.BaseLine' 'com.xtc.moment.BuildConfig.BaseLine'

# Glide.get(context) -> Glide.with(context)
Rep 'app\src\main\java\com\xtc\virtualselfapi\generate\DynamicsVisualStrategy.java' 'Glide.get(context)' 'Glide.with(context)'
Rep 'app\src\main\java\com\xtc\virtualselfapi\generate\StaticVisualStrategy.java' 'Glide.get(this.context)' 'Glide.with(this.context)'

# PhotoView display matrix
Rep 'app\src\main\java\com\xtc\moment\module\publish\multi\adapter\BigPictureAdapter.java' 'photoView.getDisplayMatrix(matrix);' 'photoView.getAttacher().getDisplayMatrix(matrix);'