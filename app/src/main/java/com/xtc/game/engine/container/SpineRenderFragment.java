package com.xtc.game.engine.container;

import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.backends.android.AndroidFragmentApplication;
import com.xtc.game.engine.bean.SpineCodeLoadBean;
import com.xtc.game.engine.render.BaseSpineAdapter;
import com.xtc.log.LogUtil;

import rx.Single;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * 骨骼渲染 Fragment，负责初始化适配器并把加载结果回传给宿主。
 */
public abstract class SpineRenderFragment extends AndroidFragmentApplication implements BaseSpineAdapter.LoadDataListener {

    private static final String TAG = SpineRenderFragment.class.getSimpleName();

    private BasicSpineRenderConfig basicSpineRenderConfig;
    private BaseSpineAdapter.LoadDataListener loadDataListener;

    public abstract BasicSpineRenderConfig buildRenderFragment(BasicSpineRenderConfig config);

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        initData();
        BaseSpineAdapter spineAdapter = this.basicSpineRenderConfig.getSpineAdapter();
        spineAdapter.setBasicSpineRenderConfig(this.basicSpineRenderConfig);
        spineAdapter.setLoadDataListener(this);
        return createGLAlpha(spineAdapter);
    }

    private void initData() {
        this.basicSpineRenderConfig = new BasicSpineRenderConfig();
        this.basicSpineRenderConfig = buildRenderFragment(this.basicSpineRenderConfig);
    }

    public void setLoadDataListener(BaseSpineAdapter.LoadDataListener loadDataListener) {
        this.loadDataListener = loadDataListener;
    }

    public void setOnClickListener(BaseSpineAdapter.OnClickListener onClickListener) {
        BaseSpineAdapter spineAdapter = this.basicSpineRenderConfig.getSpineAdapter();
        if (spineAdapter == null) {
            return;
        }
        spineAdapter.setOnClickListener(onClickListener);
    }

    public void setOnAnimationStateListener(BaseSpineAdapter.AnimationStateListener animationStateListener) {
        BaseSpineAdapter spineAdapter = this.basicSpineRenderConfig.getSpineAdapter();
        if (spineAdapter == null) {
            return;
        }
        spineAdapter.setAnimationStateListener(animationStateListener);
    }

    public void clearLoadDataListener() {
        LogUtil.w(TAG, "clearLoadDataListener");
        BaseSpineAdapter.LoadDataListener listener = this.loadDataListener;
        if (listener != null) {
            listener.onLoadDataDestroy();
        }
        this.loadDataListener = null;
    }

    @Override
    public void onLoadDataFinish(final SpineCodeLoadBean loadBean) {
        Single.just(true).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Boolean>() {
            @Override
            public void call(Boolean value) {
                if (SpineRenderFragment.this.loadDataListener == null) {
                    LogUtil.w(TAG, "onLoadDataFinish but mLoadDataListener is null");
                } else {
                    SpineRenderFragment.this.loadDataListener.onLoadDataFinish(loadBean);
                    SpineRenderFragment.this.basicSpineRenderConfig.getSpineAdapter().setGraphics(SpineRenderFragment.this.graphics);
                }
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                LogUtil.e(TAG, "onLoadDataFinish error :" + throwable);
            }
        });
    }

    @Override
    public void onLoadDataError(final int code, final String message) {
        Single.just(true).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Boolean>() {
            @Override
            public void call(Boolean value) {
                if (SpineRenderFragment.this.loadDataListener == null) {
                    return;
                }
                SpineRenderFragment.this.loadDataListener.onLoadDataError(code, message);
            }
        });
    }

    @Override
    public void onLoadDataDestroy() {
        Single.just(true).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Boolean>() {
            @Override
            public void call(Boolean value) {
                if (SpineRenderFragment.this.loadDataListener == null) {
                    return;
                }
                SpineRenderFragment.this.loadDataListener.onLoadDataDestroy();
            }
        });
    }

    private View createGLAlpha(ApplicationListener listener) {
        AndroidApplicationConfiguration configuration = new AndroidApplicationConfiguration();
        configuration.r = 8;
        configuration.g = 8;
        configuration.b = 8;
        configuration.a = 8;
        configuration.useGL30 = false;
        configuration.useAccelerometer = false;
        configuration.useWakelock = this.basicSpineRenderConfig.isTransparentBackground();
        View view = initializeForView(listener, configuration);
        if (view instanceof SurfaceView) {
            GLSurfaceView surfaceView = (GLSurfaceView) this.graphics.getView();
            surfaceView.getHolder().setFormat(-2);
            surfaceView.setZOrderMediaOverlay(true);
            surfaceView.setZOrderOnTop(true);
        }
        return view;
    }

    public BaseSpineAdapter getSpineRender() {
        BasicSpineRenderConfig config = this.basicSpineRenderConfig;
        if (config == null) {
            return null;
        }
        return config.getSpineAdapter();
    }
}