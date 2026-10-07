package com.ss.ugc.android.alpha_player.model;

import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;

/* JADX INFO: compiled from: ScaleType.kt */
/* JADX INFO: loaded from: classes2.dex */
@Metadata(d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0000\n\u0002\u0010\b\n\u0002\b\u000e\b\u0086\u0001\u0018\u0000 \u00102\b\u0012\u0004\u0012\u00020\u00000\u0001:\u0001\u0010B\u000f\b\u0002\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0002\u0010\u0004j\u0002\b\u0005j\u0002\b\u0006j\u0002\b\u0007j\u0002\b\bj\u0002\b\tj\u0002\b\nj\u0002\b\u000bj\u0002\b\fj\u0002\b\rj\u0002\b\u000ej\u0002\b\u000f¨\u0006\u0011"}, d2 = {"Lcom/ss/ugc/android/alpha_player/model/ScaleType;", "", "index", "", "(Ljava/lang/String;II)V", "ScaleToFill", "ScaleAspectFitCenter", "ScaleAspectFill", "TopFill", "BottomFill", "LeftFill", "RightFill", "TopFit", "BottomFit", "LeftFit", "RightFit", "Companion", "alpha_player_release"}, k = 1, mv = {1, 6, 0}, xi = 48)
public enum ScaleType {
    ScaleToFill(0),
    ScaleAspectFitCenter(1),
    ScaleAspectFill(2),
    TopFill(3),
    BottomFill(4),
    LeftFill(5),
    RightFill(6),
    TopFit(7),
    BottomFit(8),
    LeftFit(9),
    RightFit(10);


    /* JADX INFO: renamed from: Companion, reason: from kotlin metadata */
    public static final Companion INSTANCE = new Companion(null);

    ScaleType(int i) {
    }

    /* JADX INFO: compiled from: ScaleType.kt */
    @Metadata(d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002J\u000e\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006¨\u0006\u0007"}, d2 = {"Lcom/ss/ugc/android/alpha_player/model/ScaleType$Companion;", "", "()V", "convertFrom", "Lcom/ss/ugc/android/alpha_player/model/ScaleType;", "index", "", "alpha_player_release"}, k = 1, mv = {1, 6, 0}, xi = 48)
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }

        public final ScaleType a(int i) {
            switch (i) {
                case 1:
                    return ScaleType.ScaleAspectFitCenter;
                case 2:
                    return ScaleType.ScaleAspectFill;
                case 3:
                    return ScaleType.TopFill;
                case 4:
                    return ScaleType.BottomFill;
                case 5:
                    return ScaleType.LeftFill;
                case 6:
                    return ScaleType.RightFill;
                case 7:
                    return ScaleType.TopFit;
                case 8:
                    return ScaleType.BottomFit;
                case 9:
                    return ScaleType.LeftFit;
                case 10:
                    return ScaleType.RightFit;
                default:
                    return ScaleType.ScaleToFill;
            }
        }
    }
}
