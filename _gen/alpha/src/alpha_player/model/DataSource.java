package com.ss.ugc.android.alpha_player.model;

import android.text.TextUtils;
import java.io.File;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;

/* JADX INFO: compiled from: DataSource.kt */
/* JADX INFO: loaded from: classes2.dex */
@Metadata(d1 = {"\u0000,\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\b\n\u0002\u0018\u0002\n\u0002\b\f\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0007\u0018\u00002\u00020\u0001B\u0005¢\u0006\u0002\u0010\u0002J\u000e\u0010\u0018\u001a\u00020\u00042\u0006\u0010\u0019\u001a\u00020\u001aJ\u0010\u0010\u001b\u001a\u0004\u0018\u00010\r2\u0006\u0010\u0019\u001a\u00020\u001aJ\u0006\u0010\u001c\u001a\u00020\u001dJ\u000e\u0010\u0007\u001a\u00020\u00002\u0006\u0010\u0003\u001a\u00020\u0004J\u0016\u0010\u001e\u001a\u00020\u00002\u0006\u0010\u001f\u001a\u00020\u00042\u0006\u0010 \u001a\u00020\u001aJ\u0016\u0010!\u001a\u00020\u00002\u0006\u0010\"\u001a\u00020\u00042\u0006\u0010#\u001a\u00020\u001aR\u001a\u0010\u0003\u001a\u00020\u0004X\u0086.¢\u0006\u000e\n\u0000\u001a\u0004\b\u0005\u0010\u0006\"\u0004\b\u0007\u0010\bR\u001a\u0010\t\u001a\u00020\u0004X\u0086.¢\u0006\u000e\n\u0000\u001a\u0004\b\n\u0010\u0006\"\u0004\b\u000b\u0010\bR\u001c\u0010\f\u001a\u0004\u0018\u00010\rX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u000e\u0010\u000f\"\u0004\b\u0010\u0010\u0011R\u001a\u0010\u0012\u001a\u00020\u0004X\u0086.¢\u0006\u000e\n\u0000\u001a\u0004\b\u0013\u0010\u0006\"\u0004\b\u0014\u0010\bR\u001c\u0010\u0015\u001a\u0004\u0018\u00010\rX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0016\u0010\u000f\"\u0004\b\u0017\u0010\u0011¨\u0006$"}, d2 = {"Lcom/ss/ugc/android/alpha_player/model/DataSource;", "", "()V", "baseDir", "", "getBaseDir", "()Ljava/lang/String;", "setBaseDir", "(Ljava/lang/String;)V", "landPath", "getLandPath", "setLandPath", "landScaleType", "Lcom/ss/ugc/android/alpha_player/model/ScaleType;", "getLandScaleType", "()Lcom/ss/ugc/android/alpha_player/model/ScaleType;", "setLandScaleType", "(Lcom/ss/ugc/android/alpha_player/model/ScaleType;)V", "portPath", "getPortPath", "setPortPath", "portScaleType", "getPortScaleType", "setPortScaleType", "getPath", "orientation", "", "getScaleType", "isValid", "", "setLandscapePath", "landscapePath", "landscapeScaleType", "setPortraitPath", "portraitPath", "portraitScaleType", "alpha_player_release"}, k = 1, mv = {1, 6, 0}, xi = 48)
public final class DataSource {
    public String a;
    public String b;
    public String c;
    private ScaleType d;
    private ScaleType e;

    public final String a() {
        String str = this.a;
        if (str != null) {
            return str;
        }
        Intrinsics.d("baseDir");
        return null;
    }

    public final void a(String str) {
        Intrinsics.g(str, "<set-?>");
        this.a = str;
    }

    public final String b() {
        String str = this.b;
        if (str != null) {
            return str;
        }
        Intrinsics.d("portPath");
        return null;
    }

    public final void b(String str) {
        Intrinsics.g(str, "<set-?>");
        this.b = str;
    }

    public final String c() {
        String str = this.c;
        if (str != null) {
            return str;
        }
        Intrinsics.d("landPath");
        return null;
    }

    public final void c(String str) {
        Intrinsics.g(str, "<set-?>");
        this.c = str;
    }

    public final void a(ScaleType scaleType) {
        this.d = scaleType;
    }

    /* JADX INFO: renamed from: d, reason: from getter */
    public final ScaleType getD() {
        return this.d;
    }

    public final void b(ScaleType scaleType) {
        this.e = scaleType;
    }

    /* JADX INFO: renamed from: e, reason: from getter */
    public final ScaleType getE() {
        return this.e;
    }

    public final DataSource d(String baseDir) {
        Intrinsics.g(baseDir, "baseDir");
        String separator = File.separator;
        Intrinsics.c(separator, "separator");
        if (!StringsKt.c(baseDir, separator, false, 2, (Object) null)) {
            baseDir = baseDir + File.separator;
        }
        a(baseDir);
        return this;
    }

    public final DataSource a(String portraitPath, int i) {
        Intrinsics.g(portraitPath, "portraitPath");
        b(portraitPath);
        this.d = ScaleType.INSTANCE.a(i);
        return this;
    }

    public final DataSource b(String landscapePath, int i) {
        Intrinsics.g(landscapePath, "landscapePath");
        c(landscapePath);
        this.e = ScaleType.INSTANCE.a(i);
        return this;
    }

    public final String a(int i) {
        StringBuilder sb = new StringBuilder();
        sb.append(a());
        sb.append(1 == i ? b() : c());
        return sb.toString();
    }

    public final ScaleType b(int i) {
        return 1 == i ? this.d : this.e;
    }

    public final boolean f() {
        return (TextUtils.isEmpty(b()) || TextUtils.isEmpty(c()) || this.d == null || this.e == null) ? false : true;
    }
}
