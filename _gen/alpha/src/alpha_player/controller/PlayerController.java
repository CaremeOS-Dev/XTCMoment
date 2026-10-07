package com.ss.ugc.android.alpha_player.controller;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.OnLifecycleEvent;
import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import com.ss.ugc.android.alpha_player.IMonitor;
import com.ss.ugc.android.alpha_player.IPlayerAction;
import com.ss.ugc.android.alpha_player.model.AlphaVideoViewType;
import com.ss.ugc.android.alpha_player.model.Configuration;
import com.ss.ugc.android.alpha_player.model.DataSource;
import com.ss.ugc.android.alpha_player.model.ScaleType;
import com.ss.ugc.android.alpha_player.model.VideoInfo;
import com.ss.ugc.android.alpha_player.player.DefaultSystemPlayer;
import com.ss.ugc.android.alpha_player.player.IMediaPlayer;
import com.ss.ugc.android.alpha_player.player.PlayerState;
import com.ss.ugc.android.alpha_player.render.VideoRenderer;
import com.ss.ugc.android.alpha_player.widget.AlphaVideoGLSurfaceView;
import com.ss.ugc.android.alpha_player.widget.AlphaVideoGLTextureView;
import com.ss.ugc.android.alpha_player.widget.IAlphaVideoView;
import com.xtc.log.LogUtil;
import com.xtc.moment.module.Constants;
import com.xtc.system.account.WatchAccountBase;
import com.xtc.system.location.core.LocationRequest;
import java.io.File;
import java.io.IOException;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: compiled from: PlayerController.kt */
/* JADX INFO: loaded from: classes.dex */
@Metadata(d1 = {"\u0000²\u0001\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0010\u000b\n\u0002\b\u0004\n\u0002\u0010\b\n\u0002\b\u0005\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0012\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0017\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\f*\u0002#2\u0018\u0000 \u008e\u00012\u00020\u00012\u00020\u00022\u00020\u0003:\u0002\u008e\u0001B%\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\t\u0012\u0006\u0010\n\u001a\u00020\u000b¢\u0006\u0002\u0010\fJ\u0010\u0010U\u001a\u00020V2\u0006\u0010W\u001a\u00020XH\u0016J\u0010\u0010Y\u001a\u00020V2\u0006\u0010W\u001a\u00020XH\u0016J\b\u0010Z\u001a\u00020VH\u0002J\u001a\u0010[\u001a\u00020\\2\u0006\u0010]\u001a\u00020\u001d2\b\u0010^\u001a\u0004\u0018\u00010_H\u0002J\b\u0010`\u001a\u00020aH\u0016J\b\u0010b\u001a\u00020cH\u0016J\u0012\u0010d\u001a\u00020\u00182\b\u0010e\u001a\u0004\u0018\u00010\\H\u0016J\u0010\u0010f\u001a\u00020V2\u0006\u0010\u0006\u001a\u00020\u0007H\u0002J\b\u0010g\u001a\u00020VH\u0002J\b\u0010h\u001a\u00020VH\u0002J\b\u0010i\u001a\u00020VH\u0003J,\u0010j\u001a\u00020V2\u0006\u0010k\u001a\u00020\u00182\b\b\u0002\u0010]\u001a\u00020\u001d2\b\b\u0002\u0010l\u001a\u00020\u001d2\u0006\u0010m\u001a\u00020aH\u0002J\b\u0010n\u001a\u00020VH\u0007J\b\u0010o\u001a\u00020VH\u0007J\b\u0010p\u001a\u00020VH\u0007J\b\u0010q\u001a\u00020VH\u0007J\b\u0010r\u001a\u00020VH\u0003J\b\u0010s\u001a\u00020VH\u0016J\b\u0010t\u001a\u00020VH\u0003J\b\u0010u\u001a\u00020VH\u0016J\b\u0010v\u001a\u00020VH\u0016J\b\u0010w\u001a\u00020VH\u0016J\u0010\u0010x\u001a\u00020V2\u0006\u0010e\u001a\u00020\\H\u0002J\u0010\u0010y\u001a\u00020V2\u0006\u0010z\u001a\u00020{H\u0003J\u0010\u0010|\u001a\u00020V2\u0006\u0010}\u001a\u00020\u001dH\u0016J\u0010\u0010~\u001a\u00020V2\u0006\u0010j\u001a\u00020&H\u0016J\u0011\u0010\u007f\u001a\u00020V2\u0007\u0010\u0080\u0001\u001a\u00020,H\u0016J\u0013\u0010\u0081\u0001\u001a\u00020V2\b\u0010\u0082\u0001\u001a\u00030\u0083\u0001H\u0016J\u0011\u0010\u0084\u0001\u001a\u00020V2\u0006\u0010z\u001a\u00020{H\u0003J\u001b\u0010\u0085\u0001\u001a\u00020V2\u0007\u0010\u0086\u0001\u001a\u00020\u001d2\u0007\u0010\u0087\u0001\u001a\u00020\u001dH\u0016J\u0012\u0010\u0088\u0001\u001a\u00020V2\u0007\u0010\u0089\u0001\u001a\u00020\u001dH\u0016J\u0011\u0010H\u001a\u00020V2\u0007\u0010\u008a\u0001\u001a\u00020\u0018H\u0016J\u0011\u0010\u008b\u0001\u001a\u00020V2\u0006\u0010z\u001a\u00020{H\u0016J\t\u0010\u008c\u0001\u001a\u00020VH\u0003J\t\u0010\u008d\u0001\u001a\u00020VH\u0016R\u001a\u0010\r\u001a\u00020\u000eX\u0086.¢\u0006\u000e\n\u0000\u001a\u0004\b\u000f\u0010\u0010\"\u0004\b\u0011\u0010\u0012R\u0011\u0010\b\u001a\u00020\t¢\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0014R\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0016R\u001a\u0010\u0017\u001a\u00020\u0018X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0017\u0010\u0019\"\u0004\b\u001a\u0010\u001bR\u001a\u0010\u001c\u001a\u00020\u001dX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u001e\u0010\u001f\"\u0004\b \u0010!R\u0010\u0010\"\u001a\u00020#X\u0082\u0004¢\u0006\u0004\n\u0002\u0010$R\u001c\u0010%\u001a\u0004\u0018\u00010&X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b'\u0010(\"\u0004\b)\u0010*R\u001c\u0010+\u001a\u0004\u0018\u00010,X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b-\u0010.\"\u0004\b/\u00100R\u0010\u00101\u001a\u000202X\u0082\u0004¢\u0006\u0004\n\u0002\u00103R\u0011\u00104\u001a\u000205¢\u0006\b\n\u0000\u001a\u0004\b6\u00107R\u001a\u0010\n\u001a\u00020\u000bX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b8\u00109\"\u0004\b:\u0010;R\u001c\u0010<\u001a\u0004\u0018\u00010=X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b>\u0010?\"\u0004\b@\u0010AR\u001a\u0010B\u001a\u00020CX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\bD\u0010E\"\u0004\bF\u0010GR\u001a\u0010H\u001a\u00020\u0018X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\bI\u0010\u0019\"\u0004\bJ\u0010\u001bR\u001a\u0010K\u001a\u00020\u001dX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\bL\u0010\u001f\"\u0004\bM\u0010!R\u001a\u0010N\u001a\u00020\u001dX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\bO\u0010\u001f\"\u0004\bP\u0010!R\u001c\u0010Q\u001a\u0004\u0018\u000105X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\bR\u00107\"\u0004\bS\u0010T¨\u0006\u008f\u0001"}, d2 = {"Lcom/ss/ugc/android/alpha_player/controller/PlayerController;", "Lcom/ss/ugc/android/alpha_player/controller/IPlayerControllerExt;", "Landroid/arch/lifecycle/LifecycleObserver;", "Landroid/os/Handler$Callback;", "context", "Landroid/content/Context;", "owner", "Landroid/arch/lifecycle/LifecycleOwner;", "alphaVideoViewType", "Lcom/ss/ugc/android/alpha_player/model/AlphaVideoViewType;", "mediaPlayer", "Lcom/ss/ugc/android/alpha_player/player/IMediaPlayer;", "(Landroid/content/Context;Landroid/arch/lifecycle/LifecycleOwner;Lcom/ss/ugc/android/alpha_player/model/AlphaVideoViewType;Lcom/ss/ugc/android/alpha_player/player/IMediaPlayer;)V", "alphaVideoView", "Lcom/ss/ugc/android/alpha_player/widget/IAlphaVideoView;", "getAlphaVideoView", "()Lcom/ss/ugc/android/alpha_player/widget/IAlphaVideoView;", "setAlphaVideoView", "(Lcom/ss/ugc/android/alpha_player/widget/IAlphaVideoView;)V", "getAlphaVideoViewType", "()Lcom/ss/ugc/android/alpha_player/model/AlphaVideoViewType;", "getContext", "()Landroid/content/Context;", "isPlaying", "", "()Z", "setPlaying", "(Z)V", "looperTime", "", "getLooperTime", "()I", "setLooperTime", "(I)V", "mErrorListener", "com/ss/ugc/android/alpha_player/controller/PlayerController$mErrorListener$1", "Lcom/ss/ugc/android/alpha_player/controller/PlayerController$mErrorListener$1;", "mMonitor", "Lcom/ss/ugc/android/alpha_player/IMonitor;", "getMMonitor", "()Lcom/ss/ugc/android/alpha_player/IMonitor;", "setMMonitor", "(Lcom/ss/ugc/android/alpha_player/IMonitor;)V", "mPlayerAction", "Lcom/ss/ugc/android/alpha_player/IPlayerAction;", "getMPlayerAction", "()Lcom/ss/ugc/android/alpha_player/IPlayerAction;", "setMPlayerAction", "(Lcom/ss/ugc/android/alpha_player/IPlayerAction;)V", "mPreparedListener", "com/ss/ugc/android/alpha_player/controller/PlayerController$mPreparedListener$1", "Lcom/ss/ugc/android/alpha_player/controller/PlayerController$mPreparedListener$1;", "mainHandler", "Landroid/os/Handler;", "getMainHandler", "()Landroid/os/Handler;", "getMediaPlayer", "()Lcom/ss/ugc/android/alpha_player/player/IMediaPlayer;", "setMediaPlayer", "(Lcom/ss/ugc/android/alpha_player/player/IMediaPlayer;)V", "playThread", "Landroid/os/HandlerThread;", "getPlayThread", "()Landroid/os/HandlerThread;", "setPlayThread", "(Landroid/os/HandlerThread;)V", "playerState", "Lcom/ss/ugc/android/alpha_player/player/PlayerState;", "getPlayerState", "()Lcom/ss/ugc/android/alpha_player/player/PlayerState;", "setPlayerState", "(Lcom/ss/ugc/android/alpha_player/player/PlayerState;)V", "showFirstFrameAtEnd", "getShowFirstFrameAtEnd", "setShowFirstFrameAtEnd", "videoDefaultHeight", "getVideoDefaultHeight", "setVideoDefaultHeight", "videoDefaultWidth", "getVideoDefaultWidth", "setVideoDefaultWidth", "workHandler", "getWorkHandler", "setWorkHandler", "(Landroid/os/Handler;)V", "attachAlphaView", "", "parentView", "Landroid/view/ViewGroup;", "detachAlphaView", "emitEndSignal", "getMessage", "Landroid/os/Message;", "what", "obj", "", "getPlayerType", "", "getView", "Landroid/view/View;", "handleMessage", "msg", "init", "initAlphaView", "initMediaPlayer", "initPlayer", "monitor", Constants.ProviderConstants.MOMENT_ITEM_STATE_PATH, LocationRequest.ServiceParams.e, "errorInfo", "onDestroy", "onPause", "onResume", "onStop", "parseVideoSize", "pause", "prepareAsync", "release", "reset", "resume", "sendMessage", "setDataSource", "dataSource", "Lcom/ss/ugc/android/alpha_player/model/DataSource;", "setLooperCount", "looperCount", "setMonitor", "setPlayerAction", "playerAction", "setSurface", "surface", "Landroid/view/Surface;", "setVideoFromFile", "setViewDefaultWidthAndHeight", "width", WatchAccountBase.KEY_HEIGHT, "setVisibility", "visibility", "show", "start", "startPlay", "stop", "Companion", "alpha_player_release"}, k = 1, mv = {1, 6, 0}, xi = 48)
public final class PlayerController implements LifecycleObserver, Handler.Callback, IPlayerControllerExt {
    public static final Companion a = new Companion(null);
    public static final int c = 1;
    public static final int d = 2;
    public static final int e = 3;
    public static final int f = 4;
    public static final int g = 5;
    public static final int h = 6;
    public static final int i = 7;
    public static final int j = 8;
    public static final int k = 9;
    private final PlayerController$mErrorListener$1 A;
    public IAlphaVideoView b;
    private final AlphaVideoViewType l;
    private int m;
    private boolean n;
    private boolean o;
    private PlayerState p;
    private final Context q;
    private IMonitor r;
    private IPlayerAction s;
    private IMediaPlayer t;
    private int u;
    private int v;
    private Handler w;
    private final Handler x;
    private HandlerThread y;
    private final PlayerController$mPreparedListener$1 z;

    /* JADX INFO: compiled from: PlayerController.kt */
    /* JADX INFO: loaded from: classes2.dex */
    @Metadata(k = 3, mv = {1, 6, 0}, xi = 48)
    public /* synthetic */ class WhenMappings {
        public static final /* synthetic */ int[] a;
        public static final /* synthetic */ int[] b;

        static {
            int[] iArr = new int[AlphaVideoViewType.values().length];
            iArr[AlphaVideoViewType.GL_SURFACE_VIEW.ordinal()] = 1;
            iArr[AlphaVideoViewType.GL_TEXTURE_VIEW.ordinal()] = 2;
            a = iArr;
            int[] iArr2 = new int[PlayerState.values().length];
            iArr2[PlayerState.PREPARED.ordinal()] = 1;
            iArr2[PlayerState.PAUSED.ordinal()] = 2;
            iArr2[PlayerState.NOT_PREPARED.ordinal()] = 3;
            iArr2[PlayerState.STOPPED.ordinal()] = 4;
            iArr2[PlayerState.STARTED.ordinal()] = 5;
            b = iArr2;
        }
    }

    /* JADX WARN: Type inference failed for: r4v3, types: [com.ss.ugc.android.alpha_player.controller.PlayerController$mPreparedListener$1] */
    /* JADX WARN: Type inference failed for: r4v4, types: [com.ss.ugc.android.alpha_player.controller.PlayerController$mErrorListener$1] */
    public PlayerController(Context context, LifecycleOwner owner, AlphaVideoViewType alphaVideoViewType, IMediaPlayer mediaPlayer) {
        Intrinsics.g(context, "context");
        Intrinsics.g(owner, "owner");
        Intrinsics.g(alphaVideoViewType, "alphaVideoViewType");
        Intrinsics.g(mediaPlayer, "mediaPlayer");
        this.l = alphaVideoViewType;
        this.p = PlayerState.NOT_PREPARED;
        this.x = new Handler(Looper.getMainLooper());
        this.z = new IMediaPlayer.OnPreparedListener() { // from class: com.ss.ugc.android.alpha_player.controller.PlayerController$mPreparedListener$1
            @Override // com.ss.ugc.android.alpha_player.player.IMediaPlayer.OnPreparedListener
            public void a() {
                PlayerController playerController = this.a;
                playerController.a(playerController.a(3, (Object) null));
            }
        };
        this.A = new IMediaPlayer.OnErrorListener() { // from class: com.ss.ugc.android.alpha_player.controller.PlayerController$mErrorListener$1
            @Override // com.ss.ugc.android.alpha_player.player.IMediaPlayer.OnErrorListener
            public void a(int i2, int i3, String desc) {
                Intrinsics.g(desc, "desc");
                this.a.a(false, i2, i3, "mediaPlayer error, info: " + desc);
                this.a.C();
            }
        };
        this.q = context;
        this.t = mediaPlayer;
        a(owner);
        w();
        x();
    }

    /* JADX INFO: renamed from: h, reason: from getter */
    public final AlphaVideoViewType getL() {
        return this.l;
    }

    /* JADX INFO: compiled from: PlayerController.kt */
    /* JADX INFO: loaded from: classes2.dex */
    @Metadata(d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\t\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002J\u001a\u0010\r\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\u00102\n\b\u0002\u0010\u0011\u001a\u0004\u0018\u00010\u0012R\u000e\u0010\u0003\u001a\u00020\u0004X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0004X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0004X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\u0004X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\u0004X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u0004X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\u0004X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\u0004X\u0086T¢\u0006\u0002\n\u0000¨\u0006\u0013"}, d2 = {"Lcom/ss/ugc/android/alpha_player/controller/PlayerController$Companion;", "", "()V", "DESTROY", "", "INIT_MEDIA_PLAYER", "PAUSE", "RESET", "RESUME", "SET_DATA_SOURCE", "START", "STOP", "SURFACE", "get", "Lcom/ss/ugc/android/alpha_player/controller/PlayerController;", "configuration", "Lcom/ss/ugc/android/alpha_player/model/Configuration;", "mediaPlayer", "Lcom/ss/ugc/android/alpha_player/player/IMediaPlayer;", "alpha_player_release"}, k = 1, mv = {1, 6, 0}, xi = 48)
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }

        public static /* synthetic */ PlayerController a(Companion companion, Configuration configuration, IMediaPlayer iMediaPlayer, int i, Object obj) {
            if ((i & 2) != 0) {
                iMediaPlayer = null;
            }
            return companion.a(configuration, iMediaPlayer);
        }

        public final PlayerController a(Configuration configuration, IMediaPlayer iMediaPlayer) {
            Intrinsics.g(configuration, "configuration");
            Context a = configuration.getA();
            LifecycleOwner b = configuration.getB();
            AlphaVideoViewType c = configuration.getC();
            if (iMediaPlayer == null) {
                iMediaPlayer = new DefaultSystemPlayer();
            }
            return new PlayerController(a, b, c, iMediaPlayer);
        }
    }

    public final void c(int i2) {
        this.m = i2;
    }

    /* JADX INFO: renamed from: i, reason: from getter */
    public final int getM() {
        return this.m;
    }

    public final void b(boolean z) {
        this.n = z;
    }

    /* JADX INFO: renamed from: j, reason: from getter */
    public final boolean getN() {
        return this.n;
    }

    public final void c(boolean z) {
        this.o = z;
    }

    /* JADX INFO: renamed from: k, reason: from getter */
    public final boolean getO() {
        return this.o;
    }

    public final void a(PlayerState playerState) {
        Intrinsics.g(playerState, "<set-?>");
        this.p = playerState;
    }

    /* JADX INFO: renamed from: l, reason: from getter */
    public final PlayerState getP() {
        return this.p;
    }

    /* JADX INFO: renamed from: m, reason: from getter */
    public final Context getQ() {
        return this.q;
    }

    public final void b(IMonitor iMonitor) {
        this.r = iMonitor;
    }

    /* JADX INFO: renamed from: n, reason: from getter */
    public final IMonitor getR() {
        return this.r;
    }

    public final void b(IPlayerAction iPlayerAction) {
        this.s = iPlayerAction;
    }

    /* JADX INFO: renamed from: o, reason: from getter */
    public final IPlayerAction getS() {
        return this.s;
    }

    public final void a(IMediaPlayer iMediaPlayer) {
        Intrinsics.g(iMediaPlayer, "<set-?>");
        this.t = iMediaPlayer;
    }

    /* JADX INFO: renamed from: p, reason: from getter */
    public final IMediaPlayer getT() {
        return this.t;
    }

    public final void d(int i2) {
        this.u = i2;
    }

    /* JADX INFO: renamed from: q, reason: from getter */
    public final int getU() {
        return this.u;
    }

    public final void e(int i2) {
        this.v = i2;
    }

    /* JADX INFO: renamed from: r, reason: from getter */
    public final int getV() {
        return this.v;
    }

    public final void a(IAlphaVideoView iAlphaVideoView) {
        Intrinsics.g(iAlphaVideoView, "<set-?>");
        this.b = iAlphaVideoView;
    }

    public final IAlphaVideoView s() {
        IAlphaVideoView iAlphaVideoView = this.b;
        if (iAlphaVideoView != null) {
            return iAlphaVideoView;
        }
        Intrinsics.d("alphaVideoView");
        return null;
    }

    public final void a(Handler handler) {
        this.w = handler;
    }

    /* JADX INFO: renamed from: t, reason: from getter */
    public final Handler getW() {
        return this.w;
    }

    /* JADX INFO: renamed from: u, reason: from getter */
    public final Handler getX() {
        return this.x;
    }

    public final void a(HandlerThread handlerThread) {
        this.y = handlerThread;
    }

    /* JADX INFO: renamed from: v, reason: from getter */
    public final HandlerThread getY() {
        return this.y;
    }

    private final void a(LifecycleOwner lifecycleOwner) {
        lifecycleOwner.getLifecycle().a(this);
        this.y = new HandlerThread("alpha-play-thread", 10);
        HandlerThread handlerThread = this.y;
        Intrinsics.a(handlerThread);
        handlerThread.start();
        HandlerThread handlerThread2 = this.y;
        Intrinsics.a(handlerThread2);
        this.w = new Handler(handlerThread2.getLooper(), this);
    }

    private final void w() {
        AlphaVideoGLSurfaceView alphaVideoGLSurfaceView;
        int i2 = WhenMappings.a[this.l.ordinal()];
        if (i2 == 1) {
            alphaVideoGLSurfaceView = new AlphaVideoGLSurfaceView(this.q, null);
        } else {
            if (i2 != 2) {
                throw new NoWhenBranchMatchedException();
            }
            alphaVideoGLSurfaceView = new AlphaVideoGLTextureView(this.q, null);
        }
        a(alphaVideoGLSurfaceView);
        IAlphaVideoView iAlphaVideoViewS = s();
        iAlphaVideoViewS.setLayoutParams(new ViewGroup.LayoutParams(-1, -1));
        iAlphaVideoViewS.setPlayerController(this);
        iAlphaVideoViewS.setVideoRenderer(new VideoRenderer(iAlphaVideoViewS));
    }

    private final void x() {
        a(a(1, (Object) null));
    }

    @Override // com.ss.ugc.android.alpha_player.controller.IPlayerController
    public void a(IPlayerAction playerAction) {
        Intrinsics.g(playerAction, "playerAction");
        this.s = playerAction;
    }

    @Override // com.ss.ugc.android.alpha_player.controller.IPlayerController
    public void a(IMonitor monitor) {
        Intrinsics.g(monitor, "monitor");
        this.r = monitor;
    }

    @Override // com.ss.ugc.android.alpha_player.controller.IPlayerController
    public void b(int i2) {
        s().setVisibility(i2);
        if (i2 == 0) {
            s().bringToFront();
        }
    }

    @Override // com.ss.ugc.android.alpha_player.controller.IPlayerController
    public void a(int i2) {
        this.m = i2;
    }

    @Override // com.ss.ugc.android.alpha_player.controller.IPlayerController
    public void a(int i2, int i3) {
        this.u = i2;
        this.v = i3;
    }

    @Override // com.ss.ugc.android.alpha_player.controller.IPlayerController
    public void a(boolean z) {
        this.o = z;
    }

    @Override // com.ss.ugc.android.alpha_player.controller.IPlayerController
    public void a(ViewGroup parentView) {
        Intrinsics.g(parentView, "parentView");
        s().a(parentView);
    }

    @Override // com.ss.ugc.android.alpha_player.controller.IPlayerController
    public void b(ViewGroup parentView) {
        Intrinsics.g(parentView, "parentView");
        s().b(parentView);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public final void onPause() {
        a();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public final void onResume() {
        b();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public final void onStop() {
        c();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public final void onDestroy() {
        e();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final void a(Message message) {
        HandlerThread handlerThread = this.y;
        if (handlerThread == null || !handlerThread.isAlive() || handlerThread.isInterrupted()) {
            return;
        }
        if (this.w == null) {
            this.w = new Handler(handlerThread.getLooper(), this);
        }
        Handler handler = this.w;
        Intrinsics.a(handler);
        handler.sendMessageDelayed(message, 0L);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final Message a(int i2, Object obj) {
        Message message = Message.obtain();
        message.what = i2;
        message.obj = obj;
        Intrinsics.c(message, "message");
        return message;
    }

    @Override // com.ss.ugc.android.alpha_player.controller.IPlayerControllerExt
    public void a(Surface surface) {
        Intrinsics.g(surface, "surface");
        a(a(8, surface));
    }

    @Override // com.ss.ugc.android.alpha_player.controller.IPlayerController
    public void a(DataSource dataSource) {
        Intrinsics.g(dataSource, "dataSource");
        if (dataSource.f()) {
            b(0);
            a(a(2, dataSource));
        } else {
            C();
            a(this, false, 0, 0, "dataSource is invalid!", 6, null);
        }
    }

    @Override // com.ss.ugc.android.alpha_player.controller.IPlayerController
    public void a() {
        a(a(4, (Object) null));
    }

    @Override // com.ss.ugc.android.alpha_player.controller.IPlayerController
    public void b() {
        a(a(5, (Object) null));
    }

    @Override // com.ss.ugc.android.alpha_player.controller.IPlayerController
    public void c() {
        a(a(6, (Object) null));
    }

    @Override // com.ss.ugc.android.alpha_player.controller.IPlayerController
    public void d() {
        a(a(9, (Object) null));
    }

    @Override // com.ss.ugc.android.alpha_player.controller.IPlayerController
    public void e() {
        a(a(7, (Object) null));
    }

    @Override // com.ss.ugc.android.alpha_player.controller.IPlayerController
    public View f() {
        return s().getView();
    }

    @Override // com.ss.ugc.android.alpha_player.controller.IPlayerController
    public String g() {
        return this.t.o();
    }

    private final void y() throws Exception {
        try {
            this.t.h();
        } catch (Exception unused) {
            this.t = new DefaultSystemPlayer();
            this.t.h();
        }
        this.t.a(false);
        this.t.b(new IMediaPlayer.OnFirstFrameListener() { // from class: com.ss.ugc.android.alpha_player.controller.PlayerController$initPlayer$1
            @Override // com.ss.ugc.android.alpha_player.player.IMediaPlayer.OnFirstFrameListener
            public void a() {
                IPlayerAction s = this.a.getS();
                if (s != null) {
                    s.onFirstFrameStart();
                }
                this.a.s().c();
            }
        });
        this.t.b(new IMediaPlayer.OnCompletionListener() { // from class: com.ss.ugc.android.alpha_player.controller.PlayerController$initPlayer$2
            @Override // com.ss.ugc.android.alpha_player.player.IMediaPlayer.OnCompletionListener
            public void a() {
                if (this.a.getM() == -1) {
                    if (this.a.getP() == PlayerState.PREPARED || this.a.getP() == PlayerState.STARTED) {
                        this.a.a(PlayerState.PREPARED);
                        this.a.A();
                        return;
                    }
                    return;
                }
                if (this.a.getM() <= 0) {
                    this.a.C();
                    this.a.s().d();
                    if (this.a.getO()) {
                        this.a.s().c();
                    }
                    this.a.a(PlayerState.PAUSED);
                    PlayerController.a(this.a, true, 0, 0, "", 6, null);
                    return;
                }
                PlayerController playerController = this.a;
                playerController.c(playerController.getM() - 1);
                this.a.a(PlayerState.PREPARED);
                this.a.A();
            }
        });
    }

    private final void b(DataSource dataSource) {
        try {
            c(dataSource);
        } catch (Exception e2) {
            e2.printStackTrace();
            a(this, false, 0, 0, "alphaVideoView set dataSource failure: " + Log.getStackTraceString(e2), 6, null);
            C();
        }
    }

    private final void c(DataSource dataSource) throws IOException {
        this.t.m();
        this.p = PlayerState.NOT_PREPARED;
        int i2 = this.q.getResources().getConfiguration().orientation;
        String strA = dataSource.a(i2);
        ScaleType scaleTypeB = dataSource.b(i2);
        if (TextUtils.isEmpty(strA) || !new File(strA).exists()) {
            a(this, false, -1, 0, "dataPath is empty or File is not exists. path = " + strA, 4, null);
            C();
            return;
        }
        if (scaleTypeB != null) {
            s().setScaleType(scaleTypeB);
        }
        this.t.b(strA);
        if (s().getF()) {
            z();
        }
    }

    private final void z() {
        IMediaPlayer iMediaPlayer = this.t;
        if (this.p == PlayerState.NOT_PREPARED || this.p == PlayerState.STOPPED) {
            iMediaPlayer.b(this.z);
            iMediaPlayer.b(this.A);
            iMediaPlayer.i();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final void A() {
        int i2 = WhenMappings.b[this.p.ordinal()];
        if (i2 == 1) {
            this.t.j();
            this.n = true;
            this.p = PlayerState.STARTED;
            this.x.post(new Runnable() { // from class: com.ss.ugc.android.alpha_player.controller.-$$Lambda$PlayerController$Eqz4nc41XrSQ4C30deBeR_iW-CE
                @Override // java.lang.Runnable
                public final void run() {
                    PlayerController.c(this.f$0);
                }
            });
            return;
        }
        if (i2 == 2) {
            this.t.j();
            this.p = PlayerState.STARTED;
        } else if (i2 == 3 || i2 == 4) {
            try {
                z();
            } catch (Exception e2) {
                e2.printStackTrace();
                a(this, false, 0, 0, "prepare and start MediaPlayer failure!", 6, null);
                C();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void c(PlayerController this$0) {
        Intrinsics.g(this$0, "this$0");
        try {
            IPlayerAction iPlayerAction = this$0.s;
            if (iPlayerAction != null) {
                iPlayerAction.startAction(this$0.t.p());
            }
        } catch (Exception e2) {
            e2.printStackTrace();
            a(this$0, false, 0, 0, "start Action failure!", 6, null);
            this$0.C();
        }
    }

    private final void B() throws Exception {
        final VideoInfo videoInfoA = this.t.a(this.u, this.v);
        s().a(videoInfoA.getA() / 2, videoInfoA.getB());
        final ScaleType i2 = s().getI();
        this.x.post(new Runnable() { // from class: com.ss.ugc.android.alpha_player.controller.-$$Lambda$PlayerController$uUkS4IVNsiQ_-p-axbGUDCMWLMM
            @Override // java.lang.Runnable
            public final void run() {
                PlayerController.a(this.f$0, videoInfoA, i2);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void a(PlayerController this$0, VideoInfo videoInfo, ScaleType scaleType) {
        Intrinsics.g(this$0, "this$0");
        Intrinsics.g(videoInfo, "$videoInfo");
        Intrinsics.g(scaleType, "$scaleType");
        IPlayerAction iPlayerAction = this$0.s;
        if (iPlayerAction != null) {
            iPlayerAction.onVideoSizeChanged(videoInfo.getA() / 2, videoInfo.getB(), scaleType);
        }
    }

    @Override // android.os.Handler.Callback
    public boolean handleMessage(Message msg) throws Exception {
        if (msg == null) {
            return true;
        }
        switch (msg.what) {
            case 1:
                y();
                return true;
            case 2:
                Object obj = msg.obj;
                if (obj == null) {
                    throw new NullPointerException("null cannot be cast to non-null type com.ss.ugc.android.alpha_player.model.DataSource");
                }
                b((DataSource) obj);
                return true;
            case 3:
                try {
                    B();
                    this.p = PlayerState.PREPARED;
                    A();
                    return true;
                } catch (Exception e2) {
                    a(this, false, 0, 0, "start video failure: " + Log.getStackTraceString(e2), 6, null);
                    C();
                    return true;
                }
            case 4:
                if (WhenMappings.b[this.p.ordinal()] != 5) {
                    return true;
                }
                this.t.k();
                this.p = PlayerState.PAUSED;
                return true;
            case 5:
                if (!this.n && this.m <= 0) {
                    return true;
                }
                A();
                return true;
            case 6:
                int i2 = WhenMappings.b[this.p.ordinal()];
                if (i2 != 2 && i2 != 5) {
                    return true;
                }
                this.t.k();
                this.p = PlayerState.PAUSED;
                return true;
            case 7:
                LogUtil.d("Pet_PlayerController", "DESTROY start");
                s().onPause();
                if (this.p == PlayerState.STARTED) {
                    this.t.k();
                    this.p = PlayerState.PAUSED;
                }
                if (this.p == PlayerState.PAUSED) {
                    this.t.l();
                    this.p = PlayerState.STOPPED;
                }
                this.t.n();
                s().e();
                this.p = PlayerState.RELEASE;
                HandlerThread handlerThread = this.y;
                if (handlerThread != null) {
                    handlerThread.quit();
                    handlerThread.interrupt();
                }
                LogUtil.d("Pet_PlayerController", "DESTROY end");
                return true;
            case 8:
                Object obj2 = msg.obj;
                if (obj2 == null) {
                    throw new NullPointerException("null cannot be cast to non-null type android.view.Surface");
                }
                this.t.a((Surface) obj2);
                IPlayerAction iPlayerAction = this.s;
                if (iPlayerAction == null) {
                    return true;
                }
                iPlayerAction.onPlayerReady();
                return true;
            case 9:
                this.t.m();
                this.p = PlayerState.NOT_PREPARED;
                this.n = false;
                return true;
            default:
                return true;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final void C() {
        this.n = false;
        this.x.post(new Runnable() { // from class: com.ss.ugc.android.alpha_player.controller.-$$Lambda$PlayerController$z3XzCd_KM59dNaC3rOKgB1h0x_M
            @Override // java.lang.Runnable
            public final void run() {
                PlayerController.d(this.f$0);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void d(PlayerController this$0) {
        Intrinsics.g(this$0, "this$0");
        IPlayerAction iPlayerAction = this$0.s;
        if (iPlayerAction != null) {
            iPlayerAction.endAction();
        }
    }

    static /* synthetic */ void a(PlayerController playerController, boolean z, int i2, int i3, String str, int i4, Object obj) {
        if ((i4 & 2) != 0) {
            i2 = 0;
        }
        if ((i4 & 4) != 0) {
            i3 = 0;
        }
        playerController.a(z, i2, i3, str);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final void a(boolean z, int i2, int i3, String str) {
        IMonitor iMonitor = this.r;
        if (iMonitor != null) {
            iMonitor.monitor(z, g(), i2, i3, str);
        }
    }
}
