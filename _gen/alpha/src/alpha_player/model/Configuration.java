package com.ss.ugc.android.alpha_player.model;

import android.arch.lifecycle.LifecycleOwner;
import android.content.Context;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: compiled from: Configuration.kt */
/* JADX INFO: loaded from: classes2.dex */
@Metadata(d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\r\u0018\u00002\u00020\u0001B\u0015\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005¢\u0006\u0002\u0010\u0006R\u001a\u0010\u0007\u001a\u00020\bX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\t\u0010\n\"\u0004\b\u000b\u0010\fR\u001a\u0010\u0002\u001a\u00020\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\r\u0010\u000e\"\u0004\b\u000f\u0010\u0010R\u001a\u0010\u0004\u001a\u00020\u0005X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0011\u0010\u0012\"\u0004\b\u0013\u0010\u0014¨\u0006\u0015"}, d2 = {"Lcom/ss/ugc/android/alpha_player/model/Configuration;", "", "context", "Landroid/content/Context;", "lifecycleOwner", "Landroid/arch/lifecycle/LifecycleOwner;", "(Landroid/content/Context;Landroid/arch/lifecycle/LifecycleOwner;)V", "alphaVideoViewType", "Lcom/ss/ugc/android/alpha_player/model/AlphaVideoViewType;", "getAlphaVideoViewType", "()Lcom/ss/ugc/android/alpha_player/model/AlphaVideoViewType;", "setAlphaVideoViewType", "(Lcom/ss/ugc/android/alpha_player/model/AlphaVideoViewType;)V", "getContext", "()Landroid/content/Context;", "setContext", "(Landroid/content/Context;)V", "getLifecycleOwner", "()Landroid/arch/lifecycle/LifecycleOwner;", "setLifecycleOwner", "(Landroid/arch/lifecycle/LifecycleOwner;)V", "alpha_player_release"}, k = 1, mv = {1, 6, 0}, xi = 48)
public final class Configuration {
    private Context a;
    private LifecycleOwner b;
    private AlphaVideoViewType c;

    public Configuration(Context context, LifecycleOwner lifecycleOwner) {
        Intrinsics.g(context, "context");
        Intrinsics.g(lifecycleOwner, "lifecycleOwner");
        this.a = context;
        this.b = lifecycleOwner;
        this.c = AlphaVideoViewType.GL_SURFACE_VIEW;
    }

    /* JADX INFO: renamed from: a, reason: from getter */
    public final Context getA() {
        return this.a;
    }

    public final void a(Context context) {
        Intrinsics.g(context, "<set-?>");
        this.a = context;
    }

    public final void a(LifecycleOwner lifecycleOwner) {
        Intrinsics.g(lifecycleOwner, "<set-?>");
        this.b = lifecycleOwner;
    }

    /* JADX INFO: renamed from: b, reason: from getter */
    public final LifecycleOwner getB() {
        return this.b;
    }

    public final void a(AlphaVideoViewType alphaVideoViewType) {
        Intrinsics.g(alphaVideoViewType, "<set-?>");
        this.c = alphaVideoViewType;
    }

    /* JADX INFO: renamed from: c, reason: from getter */
    public final AlphaVideoViewType getC() {
        return this.c;
    }
}
