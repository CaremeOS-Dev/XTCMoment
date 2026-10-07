package com.ss.ugc.android.alpha_player.player;

import android.view.Surface;
import com.ss.ugc.android.alpha_player.model.VideoInfo;
import com.xtc.system.location.core.LocationRequest;
import java.io.IOException;
import kotlin.Metadata;

/* JADX INFO: compiled from: IMediaPlayer.kt */
/* JADX INFO: loaded from: classes2.dex */
@Metadata(d1 = {"\u0000X\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\b\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0007\bf\u0018\u00002\u00020\u0001:\u0004)*+,J\b\u0010\u0002\u001a\u00020\u0003H&J\b\u0010\u0004\u001a\u00020\u0003H&J\b\u0010\u0005\u001a\u00020\u0006H&J\u0018\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\u00032\u0006\u0010\n\u001a\u00020\u0003H&J\b\u0010\u000b\u001a\u00020\fH&J\b\u0010\r\u001a\u00020\fH&J\b\u0010\u000e\u001a\u00020\fH&J\b\u0010\u000f\u001a\u00020\fH&J\b\u0010\u0010\u001a\u00020\fH&J\u0010\u0010\u0011\u001a\u00020\f2\u0006\u0010\u0012\u001a\u00020\u0006H&J\u0010\u0010\u0013\u001a\u00020\f2\u0006\u0010\u0014\u001a\u00020\u0015H&J\u0010\u0010\u0016\u001a\u00020\f2\u0006\u0010\u0017\u001a\u00020\u0018H&J\u0010\u0010\u0019\u001a\u00020\f2\u0006\u0010\u001a\u001a\u00020\u001bH&J\u0010\u0010\u001c\u001a\u00020\f2\u0006\u0010\u001d\u001a\u00020\u001eH&J\u0010\u0010\u001f\u001a\u00020\f2\u0006\u0010 \u001a\u00020!H&J\u0010\u0010\"\u001a\u00020\f2\u0006\u0010#\u001a\u00020\u0015H&J\u0010\u0010$\u001a\u00020\f2\u0006\u0010%\u001a\u00020&H&J\b\u0010'\u001a\u00020\fH&J\b\u0010(\u001a\u00020\fH&¨\u0006-"}, d2 = {"Lcom/ss/ugc/android/alpha_player/player/IMediaPlayer;", "", "getCurrentPosition", "", "getDuration", "getPlayerType", "", "getVideoInfo", "Lcom/ss/ugc/android/alpha_player/model/VideoInfo;", "defaultWidth", "defaultHeight", "initMediaPlayer", "", "pause", "prepareAsync", "release", "reset", "setDataSource", "dataPath", "setLooping", "looping", "", "setOnCompletionListener", "completionListener", "Lcom/ss/ugc/android/alpha_player/player/IMediaPlayer$OnCompletionListener;", "setOnErrorListener", "errorListener", "Lcom/ss/ugc/android/alpha_player/player/IMediaPlayer$OnErrorListener;", "setOnFirstFrameListener", "firstFrameListener", "Lcom/ss/ugc/android/alpha_player/player/IMediaPlayer$OnFirstFrameListener;", "setOnPreparedListener", "preparedListener", "Lcom/ss/ugc/android/alpha_player/player/IMediaPlayer$OnPreparedListener;", "setScreenOnWhilePlaying", "onWhilePlaying", "setSurface", "surface", "Landroid/view/Surface;", "start", "stop", "OnCompletionListener", "OnErrorListener", "OnFirstFrameListener", "OnPreparedListener", "alpha_player_release"}, k = 1, mv = {1, 6, 0}, xi = 48)
public interface IMediaPlayer {

    /* JADX INFO: compiled from: IMediaPlayer.kt */
    @Metadata(d1 = {"\u0000\u0010\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0000\bf\u0018\u00002\u00020\u0001J\b\u0010\u0002\u001a\u00020\u0003H&¨\u0006\u0004"}, d2 = {"Lcom/ss/ugc/android/alpha_player/player/IMediaPlayer$OnCompletionListener;", "", "onCompletion", "", "alpha_player_release"}, k = 1, mv = {1, 6, 0}, xi = 48)
    public interface OnCompletionListener {
        void a();
    }

    /* JADX INFO: compiled from: IMediaPlayer.kt */
    @Metadata(d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\bf\u0018\u00002\u00020\u0001J \u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00052\u0006\u0010\u0007\u001a\u00020\bH&¨\u0006\t"}, d2 = {"Lcom/ss/ugc/android/alpha_player/player/IMediaPlayer$OnErrorListener;", "", "onError", "", "what", "", LocationRequest.ServiceParams.e, "desc", "", "alpha_player_release"}, k = 1, mv = {1, 6, 0}, xi = 48)
    public interface OnErrorListener {
        void a(int i, int i2, String str);
    }

    /* JADX INFO: compiled from: IMediaPlayer.kt */
    @Metadata(d1 = {"\u0000\u0010\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0000\bf\u0018\u00002\u00020\u0001J\b\u0010\u0002\u001a\u00020\u0003H&¨\u0006\u0004"}, d2 = {"Lcom/ss/ugc/android/alpha_player/player/IMediaPlayer$OnFirstFrameListener;", "", "onFirstFrame", "", "alpha_player_release"}, k = 1, mv = {1, 6, 0}, xi = 48)
    public interface OnFirstFrameListener {
        void a();
    }

    /* JADX INFO: compiled from: IMediaPlayer.kt */
    @Metadata(d1 = {"\u0000\u0010\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0000\bf\u0018\u00002\u00020\u0001J\b\u0010\u0002\u001a\u00020\u0003H&¨\u0006\u0004"}, d2 = {"Lcom/ss/ugc/android/alpha_player/player/IMediaPlayer$OnPreparedListener;", "", "onPrepared", "", "alpha_player_release"}, k = 1, mv = {1, 6, 0}, xi = 48)
    public interface OnPreparedListener {
        void a();
    }

    VideoInfo a(int i, int i2) throws Exception;

    void a(Surface surface);

    void a(boolean z);

    void b(OnCompletionListener onCompletionListener);

    void b(OnErrorListener onErrorListener);

    void b(OnFirstFrameListener onFirstFrameListener);

    void b(OnPreparedListener onPreparedListener);

    void b(String str) throws IOException;

    void b(boolean z);

    void h() throws Exception;

    void i();

    void j();

    void k();

    void l();

    void m();

    void n();

    String o();

    int p();

    int q();
}
