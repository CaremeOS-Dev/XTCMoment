package com.ss.ugc.android.alpha_player.controller;

import android.view.View;
import android.view.ViewGroup;
import com.ss.ugc.android.alpha_player.IMonitor;
import com.ss.ugc.android.alpha_player.IPlayerAction;
import com.ss.ugc.android.alpha_player.model.DataSource;
import com.xtc.system.account.WatchAccountBase;
import kotlin.Metadata;

/* JADX INFO: compiled from: IPlayerController.kt */
/* JADX INFO: loaded from: classes.dex */
@Metadata(d1 = {"\u0000N\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\bf\u0018\u00002\u00020\u0001J\u0010\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H&J\u0010\u0010\u0006\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H&J\b\u0010\u0007\u001a\u00020\bH&J\b\u0010\t\u001a\u00020\nH&J\b\u0010\u000b\u001a\u00020\u0003H&J\b\u0010\f\u001a\u00020\u0003H&J\b\u0010\r\u001a\u00020\u0003H&J\b\u0010\u000e\u001a\u00020\u0003H&J\u0010\u0010\u000f\u001a\u00020\u00032\u0006\u0010\u0010\u001a\u00020\u0011H&J\u0010\u0010\u0012\u001a\u00020\u00032\u0006\u0010\u0013\u001a\u00020\u0014H&J\u0010\u0010\u0015\u001a\u00020\u00032\u0006\u0010\u0016\u001a\u00020\u0017H&J\u0018\u0010\u0018\u001a\u00020\u00032\u0006\u0010\u0019\u001a\u00020\u00112\u0006\u0010\u001a\u001a\u00020\u0011H&J\u0010\u0010\u001b\u001a\u00020\u00032\u0006\u0010\u001c\u001a\u00020\u0011H&J\u0010\u0010\u001d\u001a\u00020\u00032\u0006\u0010\u001e\u001a\u00020\u001fH&J\u0010\u0010 \u001a\u00020\u00032\u0006\u0010!\u001a\u00020\"H&J\b\u0010#\u001a\u00020\u0003H&¨\u0006$"}, d2 = {"Lcom/ss/ugc/android/alpha_player/controller/IPlayerController;", "", "attachAlphaView", "", "parentView", "Landroid/view/ViewGroup;", "detachAlphaView", "getPlayerType", "", "getView", "Landroid/view/View;", "pause", "release", "reset", "resume", "setLooperCount", "looperCount", "", "setMonitor", "monitor", "Lcom/ss/ugc/android/alpha_player/IMonitor;", "setPlayerAction", "playerAction", "Lcom/ss/ugc/android/alpha_player/IPlayerAction;", "setViewDefaultWidthAndHeight", "width", WatchAccountBase.KEY_HEIGHT, "setVisibility", "visibility", "showFirstFrameAtEnd", "show", "", "start", "dataSource", "Lcom/ss/ugc/android/alpha_player/model/DataSource;", "stop", "alpha_player_release"}, k = 1, mv = {1, 6, 0}, xi = 48)
public interface IPlayerController {
    void a();

    void a(int i);

    void a(int i, int i2);

    void a(ViewGroup viewGroup);

    void a(IMonitor iMonitor);

    void a(IPlayerAction iPlayerAction);

    void a(DataSource dataSource);

    void a(boolean z);

    void b();

    void b(int i);

    void b(ViewGroup viewGroup);

    void c();

    void d();

    void e();

    View f();

    String g();
}
