package com.ss.ugc.android.alpha_player.widget;

import android.view.View;
import android.view.ViewGroup;
import com.ss.ugc.android.alpha_player.controller.IPlayerControllerExt;
import com.ss.ugc.android.alpha_player.model.ScaleType;
import com.ss.ugc.android.alpha_player.render.IRender;
import kotlin.Metadata;

/* JADX INFO: compiled from: IAlphaVideoView.kt */
/* JADX INFO: loaded from: classes2.dex */
@Metadata(d1 = {"\u0000T\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\u0007\n\u0002\b\t\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0003\bf\u0018\u00002\u00020\u0001J\u0010\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H&J\b\u0010\u0006\u001a\u00020\u0003H&J\b\u0010\u0007\u001a\u00020\bH&J\b\u0010\t\u001a\u00020\bH&J\b\u0010\n\u001a\u00020\u000bH&J\b\u0010\f\u001a\u00020\rH&J\b\u0010\u000e\u001a\u00020\u000fH&J\u0018\u0010\u0010\u001a\u00020\u00032\u0006\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020\u0012H&J\b\u0010\u0014\u001a\u00020\u0003H&J\b\u0010\u0015\u001a\u00020\u0003H&J\b\u0010\u0016\u001a\u00020\u0003H&J\b\u0010\u0017\u001a\u00020\u0003H&J\u0010\u0010\u0018\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H&J\b\u0010\u0019\u001a\u00020\u0003H&J\u0012\u0010\u001a\u001a\u00020\u00032\b\u0010\u001b\u001a\u0004\u0018\u00010\u001cH&J\u0010\u0010\u001d\u001a\u00020\u00032\u0006\u0010\u001e\u001a\u00020\u001fH&J\u0010\u0010 \u001a\u00020\u00032\u0006\u0010!\u001a\u00020\u000bH&J\u0010\u0010\"\u001a\u00020\u00032\u0006\u0010#\u001a\u00020$H&J\u0010\u0010%\u001a\u00020\u00032\u0006\u0010&\u001a\u00020\bH&¨\u0006'"}, d2 = {"Lcom/ss/ugc/android/alpha_player/widget/IAlphaVideoView;", "", "addParentView", "", "parentView", "Landroid/view/ViewGroup;", "bringToFront", "getMeasuredHeight", "", "getMeasuredWidth", "getScaleType", "Lcom/ss/ugc/android/alpha_player/model/ScaleType;", "getView", "Landroid/view/View;", "isSurfaceCreated", "", "measureInternal", "videoWidth", "", "videoHeight", "onCompletion", "onFirstFrame", "onPause", "release", "removeParentView", "requestRender", "setLayoutParams", "params", "Landroid/view/ViewGroup$LayoutParams;", "setPlayerController", "playerController", "Lcom/ss/ugc/android/alpha_player/controller/IPlayerControllerExt;", "setScaleType", "scaleType", "setVideoRenderer", "renderer", "Lcom/ss/ugc/android/alpha_player/render/IRender;", "setVisibility", "visibility", "alpha_player_release"}, k = 1, mv = {1, 6, 0}, xi = 48)
public interface IAlphaVideoView {
    void a(float f, float f2);

    void a(ViewGroup viewGroup);

    boolean a();

    void b(ViewGroup viewGroup);

    void bringToFront();

    void c();

    void d();

    void e();

    int getMeasuredHeight();

    int getMeasuredWidth();

    ScaleType getScaleType();

    View getView();

    void onPause();

    void requestRender();

    void setLayoutParams(ViewGroup.LayoutParams params);

    void setPlayerController(IPlayerControllerExt playerController);

    void setScaleType(ScaleType scaleType);

    void setVideoRenderer(IRender renderer);

    void setVisibility(int visibility);
}
