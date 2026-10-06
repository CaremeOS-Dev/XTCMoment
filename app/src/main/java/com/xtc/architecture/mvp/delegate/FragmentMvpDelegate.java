package com.xtc.architecture.mvp.delegate;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.xtc.architecture.mvp.core.MvpPresenter;
import com.xtc.architecture.mvp.core.MvpView;

/**
 * Forwards a fragment's lifecycle to the MVP machinery.
 *
 * <p>Method names mirror the fragment callbacks that drive them.
 */
public interface FragmentMvpDelegate<V extends MvpView, P extends MvpPresenter<V>> {

    void onCreate(Bundle savedInstanceState);

    void onCreateView(View view, Bundle savedInstanceState);

    void onActivityCreated(Bundle savedInstanceState);

    void onStart();

    void onResume();

    void onPause();

    void onStop();

    void onSaveInstanceState(Bundle outState);

    void onDestroyView();

    void onDestroy();

    void onAttach(Activity activity);

    void onDetach();
}
