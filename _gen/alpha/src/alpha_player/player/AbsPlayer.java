package com.ss.ugc.android.alpha_player.player;

import android.content.Context;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: compiled from: AbsPlayer.kt */
/* JADX INFO: loaded from: classes2.dex */
@Metadata(d1 = {"\u0000:\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0002\b\u0004\b&\u0018\u00002\u00020\u0001B\u0011\u0012\n\b\u0002\u0010\u0002\u001a\u0004\u0018\u00010\u0003¢\u0006\u0002\u0010\u0004J\u0010\u0010\u001d\u001a\u00020\u001e2\u0006\u0010\u0005\u001a\u00020\u0006H\u0016J\u0010\u0010\u001f\u001a\u00020\u001e2\u0006\u0010\u000b\u001a\u00020\fH\u0016J\u0010\u0010 \u001a\u00020\u001e2\u0006\u0010\u0011\u001a\u00020\u0012H\u0016J\u0010\u0010!\u001a\u00020\u001e2\u0006\u0010\u0017\u001a\u00020\u0018H\u0016R\u001c\u0010\u0005\u001a\u0004\u0018\u00010\u0006X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0007\u0010\b\"\u0004\b\t\u0010\nR\u001c\u0010\u000b\u001a\u0004\u0018\u00010\fX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\r\u0010\u000e\"\u0004\b\u000f\u0010\u0010R\u001c\u0010\u0011\u001a\u0004\u0018\u00010\u0012X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0013\u0010\u0014\"\u0004\b\u0015\u0010\u0016R\u001c\u0010\u0017\u001a\u0004\u0018\u00010\u0018X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0019\u0010\u001a\"\u0004\b\u001b\u0010\u001c¨\u0006\""}, d2 = {"Lcom/ss/ugc/android/alpha_player/player/AbsPlayer;", "Lcom/ss/ugc/android/alpha_player/player/IMediaPlayer;", "context", "Landroid/content/Context;", "(Landroid/content/Context;)V", "completionListener", "Lcom/ss/ugc/android/alpha_player/player/IMediaPlayer$OnCompletionListener;", "getCompletionListener", "()Lcom/ss/ugc/android/alpha_player/player/IMediaPlayer$OnCompletionListener;", "setCompletionListener", "(Lcom/ss/ugc/android/alpha_player/player/IMediaPlayer$OnCompletionListener;)V", "errorListener", "Lcom/ss/ugc/android/alpha_player/player/IMediaPlayer$OnErrorListener;", "getErrorListener", "()Lcom/ss/ugc/android/alpha_player/player/IMediaPlayer$OnErrorListener;", "setErrorListener", "(Lcom/ss/ugc/android/alpha_player/player/IMediaPlayer$OnErrorListener;)V", "firstFrameListener", "Lcom/ss/ugc/android/alpha_player/player/IMediaPlayer$OnFirstFrameListener;", "getFirstFrameListener", "()Lcom/ss/ugc/android/alpha_player/player/IMediaPlayer$OnFirstFrameListener;", "setFirstFrameListener", "(Lcom/ss/ugc/android/alpha_player/player/IMediaPlayer$OnFirstFrameListener;)V", "preparedListener", "Lcom/ss/ugc/android/alpha_player/player/IMediaPlayer$OnPreparedListener;", "getPreparedListener", "()Lcom/ss/ugc/android/alpha_player/player/IMediaPlayer$OnPreparedListener;", "setPreparedListener", "(Lcom/ss/ugc/android/alpha_player/player/IMediaPlayer$OnPreparedListener;)V", "setOnCompletionListener", "", "setOnErrorListener", "setOnFirstFrameListener", "setOnPreparedListener", "alpha_player_release"}, k = 1, mv = {1, 6, 0}, xi = 48)
public abstract class AbsPlayer implements IMediaPlayer {
    private IMediaPlayer.OnCompletionListener a;
    private IMediaPlayer.OnPreparedListener b;
    private IMediaPlayer.OnErrorListener c;
    private IMediaPlayer.OnFirstFrameListener d;

    /* JADX WARN: Multi-variable type inference failed */
    public AbsPlayer() {
        this(null, 1, 0 == true ? 1 : 0);
    }

    public AbsPlayer(Context context) {
    }

    public /* synthetic */ AbsPlayer(Context context, int i, DefaultConstructorMarker defaultConstructorMarker) {
        this((i & 1) != 0 ? null : context);
    }

    /* JADX INFO: renamed from: a, reason: from getter */
    public final IMediaPlayer.OnCompletionListener getA() {
        return this.a;
    }

    public final void a(IMediaPlayer.OnCompletionListener onCompletionListener) {
        this.a = onCompletionListener;
    }

    public final void a(IMediaPlayer.OnPreparedListener onPreparedListener) {
        this.b = onPreparedListener;
    }

    /* JADX INFO: renamed from: b, reason: from getter */
    public final IMediaPlayer.OnPreparedListener getB() {
        return this.b;
    }

    public final void a(IMediaPlayer.OnErrorListener onErrorListener) {
        this.c = onErrorListener;
    }

    /* JADX INFO: renamed from: c, reason: from getter */
    public final IMediaPlayer.OnErrorListener getC() {
        return this.c;
    }

    public final void a(IMediaPlayer.OnFirstFrameListener onFirstFrameListener) {
        this.d = onFirstFrameListener;
    }

    /* JADX INFO: renamed from: d, reason: from getter */
    public final IMediaPlayer.OnFirstFrameListener getD() {
        return this.d;
    }

    @Override // com.ss.ugc.android.alpha_player.player.IMediaPlayer
    public void b(IMediaPlayer.OnCompletionListener completionListener) {
        Intrinsics.g(completionListener, "completionListener");
        this.a = completionListener;
    }

    @Override // com.ss.ugc.android.alpha_player.player.IMediaPlayer
    public void b(IMediaPlayer.OnPreparedListener preparedListener) {
        Intrinsics.g(preparedListener, "preparedListener");
        this.b = preparedListener;
    }

    @Override // com.ss.ugc.android.alpha_player.player.IMediaPlayer
    public void b(IMediaPlayer.OnErrorListener errorListener) {
        Intrinsics.g(errorListener, "errorListener");
        this.c = errorListener;
    }

    @Override // com.ss.ugc.android.alpha_player.player.IMediaPlayer
    public void b(IMediaPlayer.OnFirstFrameListener firstFrameListener) {
        Intrinsics.g(firstFrameListener, "firstFrameListener");
        this.d = firstFrameListener;
    }
}
