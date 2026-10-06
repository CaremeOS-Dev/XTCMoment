$ErrorActionPreference='Stop'
function RepAll([string]$f,[string]$a,[string]$b){ if(Test-Path $f){ $t=[System.IO.File]::ReadAllText($f); if($t.Contains($a)){ $t=$t.Replace($a,$b); [System.IO.File]::WriteAllText($f,$t,(New-Object System.Text.UTF8Encoding $false)); Write-Output "fixed $f" } else { Write-Output "no-match $f :: $a" } } }

# MomentVideoView: mContent -> mIvContent (inherited name). Check base field first
RepAll 'app\src\main\java\com\xtc\moment\module\widget\MomentVideoView.java' '.into(this.mContent);' '.into(this.videoPlayLogo);'
RepAll 'app\src\main\java\com\xtc\moment\module\widget\MomentVideoView.java' 'this.mContent = (ImageView) view.findViewById(R.id.chat_msg_item_photo_iv);' 'this.videoPlayLogo = (ImageView) view.findViewById(R.id.view_video_moment_video_logo);'
RepAll 'app\src\main\java\com\xtc\moment\module\widget\MomentVideoView.java' 'if (this.mContent == null) {' 'if (this.videoPlayLogo == null) {'
RepAll 'app\src\main\java\com\xtc\moment\module\widget\MomentVideoView.java' 'this.mContent.setOnClickListener(new View.OnClickListener() {' 'this.videoPlayLogo.setOnClickListener(new View.OnClickListener() {'
RepAll 'app\src\main\java\com\xtc\moment\module\widget\MomentVideoView.java' '        this.videoPlayLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickView(context, moment, listener);
            }
        });
' ''

# GlideRoundTransform: CenterCrop(context) unsupported in glide 4.9
RepAll 'app\src\main\java\com\xtc\moment\module\publish\multi\util\GlideRoundTransform.java' '    public GlideRoundTransform(Context context, int radiusDp) {
        super(context);
        radius = DimenUtil.dp2px(context, radiusDp);
    }' '    public GlideRoundTransform(Context context, int radiusDp) {
        radius = DimenUtil.dp2px(context, radiusDp);
    }'

# MomentCommentView final fields
RepAll 'app\src\main\java\com\xtc\moment\module\widget\MomentCommentView.java' 'private final ForegroundColorSpan colorSpan;' 'private ForegroundColorSpan colorSpan;'
RepAll 'app\src\main\java\com\xtc\moment\module\widget\MomentCommentView.java' 'private final ForegroundColorSpan colorSpan2;' 'private ForegroundColorSpan colorSpan2;'
RepAll 'app\src\main\java\com\xtc\moment\module\widget\MomentCommentView.java' 'private final String unknownName;' 'private String unknownName;'
RepAll 'app\src\main\java\com\xtc\moment\module\widget\MomentCommentView.java' 'private final String replyString;' 'private String replyString;'
RepAll 'app\src\main\java\com\xtc\moment\module\widget\MomentCommentView.java' 'private final String separatorString;' 'private String separatorString;'