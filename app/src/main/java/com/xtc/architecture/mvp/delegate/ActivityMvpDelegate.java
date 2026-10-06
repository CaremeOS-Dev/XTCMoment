package com.xtc.architecture.mvp.delegate;

import android.os.Bundle;

import com.xtc.architecture.mvp.core.MvpPresenter;
import com.xtc.architecture.mvp.core.MvpView;

/**
 * Forwards an activity's lifecycle to the MVP machinery.
 *
 * <p>Method names mirror the activity callbacks that drive them.
 */
public interface ActivityMvpDelegate<V extends MvpView, P extends MvpPresenter<V>> {

    void onCreate(Bundle savedInstanceState);

    void onDestroy();

    void onPause();

    void onSaveInstanceState(Bundle outState);

    void onResume();

    void onPostCreate(Bundle savedInstanceState);

    void onStart();

    void onStop();

    void onRestart();

    void onContentChanged();

    Object onRetainCustomNonConfigurationInstance();

    Object getNonMosbyLastCustomNonConfigurationInstance();
}
