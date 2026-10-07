package com.ss.ugc.android.alpha_player.player;

import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.text.TextUtils;
import android.view.Surface;
import com.ss.ugc.android.alpha_player.model.VideoInfo;
import com.xtc.log.LogUtil;
import java.io.IOException;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: compiled from: DefaultSystemPlayer.kt */
/* JADX INFO: loaded from: classes2.dex */
@Metadata(d1 = {"\u0000L\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0007\n\u0002\u0010\u000b\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0003\u0018\u00002\u00020\u0001B\u0005¢\u0006\u0002\u0010\u0002J\b\u0010\u0013\u001a\u00020\u0014H\u0016J\b\u0010\u0015\u001a\u00020\u0014H\u0016J\b\u0010\u0016\u001a\u00020\u0004H\u0016J\u0018\u0010\u0017\u001a\u00020\u00182\u0006\u0010\u0019\u001a\u00020\u00142\u0006\u0010\u001a\u001a\u00020\u0014H\u0016J\b\u0010\u001b\u001a\u00020\u001cH\u0016J\b\u0010\u001d\u001a\u00020\u001cH\u0016J\b\u0010\u001e\u001a\u00020\u001cH\u0016J\b\u0010\u001f\u001a\u00020\u001cH\u0016J\b\u0010 \u001a\u00020\u001cH\u0016J\u0010\u0010!\u001a\u00020\u001c2\u0006\u0010\u0003\u001a\u00020\u0004H\u0016J\u0010\u0010\"\u001a\u00020\u001c2\u0006\u0010#\u001a\u00020$H\u0016J\u0010\u0010%\u001a\u00020\u001c2\u0006\u0010&\u001a\u00020$H\u0016J\u0010\u0010'\u001a\u00020\u001c2\u0006\u0010(\u001a\u00020)H\u0016J\b\u0010*\u001a\u00020\u001cH\u0016J\b\u0010+\u001a\u00020\u001cH\u0016R\u001a\u0010\u0003\u001a\u00020\u0004X\u0086.¢\u0006\u000e\n\u0000\u001a\u0004\b\u0005\u0010\u0006\"\u0004\b\u0007\u0010\bR\u001a\u0010\t\u001a\u00020\nX\u0086.¢\u0006\u000e\n\u0000\u001a\u0004\b\u000b\u0010\f\"\u0004\b\r\u0010\u000eR\u0011\u0010\u000f\u001a\u00020\u0010¢\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0012¨\u0006,"}, d2 = {"Lcom/ss/ugc/android/alpha_player/player/DefaultSystemPlayer;", "Lcom/ss/ugc/android/alpha_player/player/AbsPlayer;", "()V", "dataPath", "", "getDataPath", "()Ljava/lang/String;", "setDataPath", "(Ljava/lang/String;)V", "mediaPlayer", "Landroid/media/MediaPlayer;", "getMediaPlayer", "()Landroid/media/MediaPlayer;", "setMediaPlayer", "(Landroid/media/MediaPlayer;)V", "retriever", "Landroid/media/MediaMetadataRetriever;", "getRetriever", "()Landroid/media/MediaMetadataRetriever;", "getCurrentPosition", "", "getDuration", "getPlayerType", "getVideoInfo", "Lcom/ss/ugc/android/alpha_player/model/VideoInfo;", "defaultWidth", "defaultHeight", "initMediaPlayer", "", "pause", "prepareAsync", "release", "reset", "setDataSource", "setLooping", "looping", "", "setScreenOnWhilePlaying", "onWhilePlaying", "setSurface", "surface", "Landroid/view/Surface;", "start", "stop", "alpha_player_release"}, k = 1, mv = {1, 6, 0}, xi = 48)
public final class DefaultSystemPlayer extends AbsPlayer {
    public MediaPlayer a;
    public String b;
    private final MediaMetadataRetriever c;

    @Override // com.ss.ugc.android.alpha_player.player.IMediaPlayer
    public String o() {
        return "DefaultSystemPlayer";
    }

    public DefaultSystemPlayer() {
        super(null, 1, null);
        this.c = new MediaMetadataRetriever();
    }

    public final void a(MediaPlayer mediaPlayer) {
        Intrinsics.g(mediaPlayer, "<set-?>");
        this.a = mediaPlayer;
    }

    public final MediaPlayer e() {
        MediaPlayer mediaPlayer = this.a;
        if (mediaPlayer != null) {
            return mediaPlayer;
        }
        Intrinsics.d("mediaPlayer");
        return null;
    }

    /* JADX INFO: renamed from: f, reason: from getter */
    public final MediaMetadataRetriever getC() {
        return this.c;
    }

    public final void a(String str) {
        Intrinsics.g(str, "<set-?>");
        this.b = str;
    }

    public final String g() {
        String str = this.b;
        if (str != null) {
            return str;
        }
        Intrinsics.d("dataPath");
        return null;
    }

    @Override // com.ss.ugc.android.alpha_player.player.IMediaPlayer
    public void h() {
        a(new MediaPlayer());
        LogUtil.d("Pet_DefaultSystemPlayer", "initMediaPlayer: " + e());
        e().setOnCompletionListener(new MediaPlayer.OnCompletionListener() { // from class: com.ss.ugc.android.alpha_player.player.-$$Lambda$DefaultSystemPlayer$Gww3Mp5NMj0akjUZPBo9KuRY_hU
            @Override // android.media.MediaPlayer.OnCompletionListener
            public final void onCompletion(MediaPlayer mediaPlayer) {
                DefaultSystemPlayer.a(this.f$0, mediaPlayer);
            }
        });
        e().setOnPreparedListener(new MediaPlayer.OnPreparedListener() { // from class: com.ss.ugc.android.alpha_player.player.-$$Lambda$DefaultSystemPlayer$D0vily5HO43fPpCG_4pzUcEpfD0
            @Override // android.media.MediaPlayer.OnPreparedListener
            public final void onPrepared(MediaPlayer mediaPlayer) {
                DefaultSystemPlayer.b(this.f$0, mediaPlayer);
            }
        });
        e().setOnErrorListener(new MediaPlayer.OnErrorListener() { // from class: com.ss.ugc.android.alpha_player.player.-$$Lambda$DefaultSystemPlayer$BX-vsh2oSIPwwPzjNNSneTEoiZU
            @Override // android.media.MediaPlayer.OnErrorListener
            public final boolean onError(MediaPlayer mediaPlayer, int i, int i2) {
                return DefaultSystemPlayer.a(this.f$0, mediaPlayer, i, i2);
            }
        });
        e().setOnInfoListener(new MediaPlayer.OnInfoListener() { // from class: com.ss.ugc.android.alpha_player.player.-$$Lambda$DefaultSystemPlayer$HiM00cXZBPgAVtfgUx5u52XtEtQ
            @Override // android.media.MediaPlayer.OnInfoListener
            public final boolean onInfo(MediaPlayer mediaPlayer, int i, int i2) {
                return DefaultSystemPlayer.b(this.f$0, mediaPlayer, i, i2);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void a(DefaultSystemPlayer this$0, MediaPlayer mediaPlayer) {
        Intrinsics.g(this$0, "this$0");
        IMediaPlayer.OnCompletionListener onCompletionListenerA = this$0.getA();
        if (onCompletionListenerA != null) {
            onCompletionListenerA.a();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void b(DefaultSystemPlayer this$0, MediaPlayer mediaPlayer) {
        Intrinsics.g(this$0, "this$0");
        IMediaPlayer.OnPreparedListener onPreparedListenerB = this$0.getB();
        if (onPreparedListenerB != null) {
            onPreparedListenerB.a();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final boolean a(DefaultSystemPlayer this$0, MediaPlayer mediaPlayer, int i, int i2) {
        Intrinsics.g(this$0, "this$0");
        IMediaPlayer.OnErrorListener onErrorListenerC = this$0.getC();
        if (onErrorListenerC == null) {
            return false;
        }
        onErrorListenerC.a(i, i2, "");
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final boolean b(DefaultSystemPlayer this$0, MediaPlayer mediaPlayer, int i, int i2) {
        IMediaPlayer.OnFirstFrameListener onFirstFrameListenerD;
        Intrinsics.g(this$0, "this$0");
        if (i != 3 || (onFirstFrameListenerD = this$0.getD()) == null) {
            return false;
        }
        onFirstFrameListenerD.a();
        return false;
    }

    @Override // com.ss.ugc.android.alpha_player.player.IMediaPlayer
    public void a(Surface surface) {
        Intrinsics.g(surface, "surface");
        e().setSurface(surface);
    }

    @Override // com.ss.ugc.android.alpha_player.player.IMediaPlayer
    public void b(String dataPath) throws IOException {
        Intrinsics.g(dataPath, "dataPath");
        a(dataPath);
        e().setDataSource(dataPath);
    }

    @Override // com.ss.ugc.android.alpha_player.player.IMediaPlayer
    public void i() {
        e().prepareAsync();
    }

    @Override // com.ss.ugc.android.alpha_player.player.IMediaPlayer
    public void j() {
        e().start();
    }

    @Override // com.ss.ugc.android.alpha_player.player.IMediaPlayer
    public void k() {
        e().pause();
    }

    @Override // com.ss.ugc.android.alpha_player.player.IMediaPlayer
    public void l() {
        e().stop();
    }

    @Override // com.ss.ugc.android.alpha_player.player.IMediaPlayer
    public void m() {
        e().reset();
        a("");
    }

    @Override // com.ss.ugc.android.alpha_player.player.IMediaPlayer
    public void n() {
        e().release();
        a("");
    }

    @Override // com.ss.ugc.android.alpha_player.player.IMediaPlayer
    public void a(boolean z) {
        e().setLooping(z);
    }

    @Override // com.ss.ugc.android.alpha_player.player.IMediaPlayer
    public void b(boolean z) {
        e().setScreenOnWhilePlaying(z);
    }

    @Override // com.ss.ugc.android.alpha_player.player.IMediaPlayer
    public VideoInfo a(int i, int i2) throws Exception {
        if (TextUtils.isEmpty(g())) {
            throw new Exception("dataPath is null, please set setDataSource firstly!");
        }
        this.c.setDataSource(g());
        String strExtractMetadata = this.c.extractMetadata(18);
        String strExtractMetadata2 = this.c.extractMetadata(19);
        if (TextUtils.isEmpty(strExtractMetadata) || TextUtils.isEmpty(strExtractMetadata2)) {
            return new VideoInfo(i, i2);
        }
        String strExtractMetadata3 = this.c.extractMetadata(18);
        Intrinsics.c(strExtractMetadata3, "retriever.extractMetadat…METADATA_KEY_VIDEO_WIDTH)");
        int i3 = Integer.parseInt(strExtractMetadata3);
        String strExtractMetadata4 = this.c.extractMetadata(19);
        Intrinsics.c(strExtractMetadata4, "retriever.extractMetadat…ETADATA_KEY_VIDEO_HEIGHT)");
        return new VideoInfo(i3, Integer.parseInt(strExtractMetadata4));
    }

    @Override // com.ss.ugc.android.alpha_player.player.IMediaPlayer
    public int p() {
        return e().getDuration();
    }

    @Override // com.ss.ugc.android.alpha_player.player.IMediaPlayer
    public int q() {
        return e().getCurrentPosition();
    }
}
