$ErrorActionPreference='Stop'
function Rep([string]$f,[string]$a,[string]$b){ if(Test-Path $f){ $t=[System.IO.File]::ReadAllText($f); if($t.Contains($a)){ $t=$t.Replace($a,$b); [System.IO.File]::WriteAllText($f,$t,(New-Object System.Text.UTF8Encoding $false)); Write-Output "fixed $f" } else { Write-Output "no-match $f" } } }

Rep 'app\src\main\java\com\xtc\moment\module\widget\MomentVideoView.java' '.into(this.mContent);' '.into(this.videoPlayLogo);'
Rep 'app\src\main\java\com\xtc\moment\module\widget\MomentVideoView.java' 'if (this.mContent == null) {
            LogUtil.d(TAG, "mContent == null");
            return;
        }
    }

    private void clickView' 'if (this.mContent == null) {
            LogUtil.d(TAG, "mContent == null");
            return;
        }
        this.mContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickView(context, moment, listener);
            }
        });
    }

    private void clickView'
Rep 'app\src\main\java\com\xtc\moment\module\publish\multi\adapter\BigPictureAdapter.java' 'photoView.getAttacher().getDisplayMatrix(matrix);' 'photoView.getAttacher().b(matrix);'
Rep 'app\src\main\java\com\xtc\moment\module\widget\PhotoPreviewActivity.java' 'mPvPreview.getDisplayMatrix(matrix);' 'mPvPreview.getAttacher().b(matrix);'
Rep 'app\src\main\java\com\xtc\ui\widget\recycler\VerticalDividerDecoration.java' 'DividerDecoration.VERTICAL' 'DividerDecoration.VERTICAL_LIST'
Rep 'app\src\main\java\com\xtc\ui\widget\scalablecontainer\AppNestedScrollView.java' 'R.styleable.AppNestedScrollView_AppNestedScrollView_asv_anim_scale' 'R.styleable.AppNestedScrollView_asv_anim_scale2'
Rep 'app\src\main\java\com\xtc\moment\share\view\ShareToMomentActivity.java' 'private final String videoPath' 'private String videoPath'
Rep 'app\src\main\java\com\xtc\moment\share\presenter\ShareToMomentPresenter.java' 'public String call(byte[] thumbData) throws Throwable {' 'public String call(byte[] thumbData) {'
Rep 'app\src\main\java\com\xtc\moment\share\presenter\ShareToMomentPresenter.java' 'public String call(XTCImageObject imageObject) throws Throwable {' 'public String call(XTCImageObject imageObject) {'