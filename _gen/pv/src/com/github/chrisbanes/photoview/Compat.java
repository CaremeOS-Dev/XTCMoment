package com.github.chrisbanes.photoview;

import android.os.Build;
import android.view.View;

/* JADX INFO: loaded from: classes.dex */
class Compat {
    private static final int a = 16;

    Compat() {
    }

    public static void a(View view, Runnable runnable) {
        if (Build.VERSION.SDK_INT >= 16) {
            b(view, runnable);
        } else {
            view.postDelayed(runnable, 16L);
        }
    }

    private static void b(View view, Runnable runnable) {
        view.postOnAnimation(runnable);
    }
}
