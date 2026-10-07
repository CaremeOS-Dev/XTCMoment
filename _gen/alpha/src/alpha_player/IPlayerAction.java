package com.ss.ugc.android.alpha_player;

import com.ss.ugc.android.alpha_player.model.ScaleType;
import kotlin.Metadata;

/* JADX INFO: compiled from: IPlayerAction.kt */
/* JADX INFO: loaded from: classes2.dex */
@Metadata(d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0004\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\bf\u0018\u00002\u00020\u0001J\b\u0010\u0002\u001a\u00020\u0003H&J\b\u0010\u0004\u001a\u00020\u0003H&J\b\u0010\u0005\u001a\u00020\u0003H&J \u0010\u0006\u001a\u00020\u00032\u0006\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\b2\u0006\u0010\n\u001a\u00020\u000bH&J\u0010\u0010\f\u001a\u00020\u00032\u0006\u0010\r\u001a\u00020\bH&¨\u0006\u000e"}, d2 = {"Lcom/ss/ugc/android/alpha_player/IPlayerAction;", "", "endAction", "", "onFirstFrameStart", "onPlayerReady", "onVideoSizeChanged", "videoWidth", "", "videoHeight", "scaleType", "Lcom/ss/ugc/android/alpha_player/model/ScaleType;", "startAction", "duration", "alpha_player_release"}, k = 1, mv = {1, 6, 0}, xi = 48)
public interface IPlayerAction {
    void endAction();

    void onFirstFrameStart();

    void onPlayerReady();

    void onVideoSizeChanged(int videoWidth, int videoHeight, ScaleType scaleType);

    void startAction(int duration);
}
