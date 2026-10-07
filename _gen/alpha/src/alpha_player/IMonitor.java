package com.ss.ugc.android.alpha_player;

import com.xtc.system.location.core.LocationRequest;
import kotlin.Metadata;

/* JADX INFO: compiled from: IMonitor.kt */
/* JADX INFO: loaded from: classes2.dex */
@Metadata(d1 = {"\u0000$\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0002\b\u0003\bf\u0018\u00002\u00020\u0001J0\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\t2\u0006\u0010\u000b\u001a\u00020\u0007H&¨\u0006\f"}, d2 = {"Lcom/ss/ugc/android/alpha_player/IMonitor;", "", "monitor", "", "result", "", "playType", "", "what", "", LocationRequest.ServiceParams.e, "errorInfo", "alpha_player_release"}, k = 1, mv = {1, 6, 0}, xi = 48)
public interface IMonitor {
    void monitor(boolean result, String playType, int what, int extra, String errorInfo);
}
