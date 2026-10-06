package com.xtc.game.engine.support;

import android.util.ArrayMap;

import com.badlogic.gdx.backends.android.textureview.GLTextureView;
import com.esotericsoftware.spine.Skin;
import com.xtc.game.engine.bean.SlotAttachmentBean;
import com.xtc.game.engine.bean.SpineCodeLoadBean;
import com.xtc.game.engine.render.BaseSpineAdapter;
import com.xtc.game.engine.util.ScreenshotFactory;
import com.xtc.game.engine.util.SingleTaskExecutor;

import java.util.List;

/**
 * 骨骼渲染功能代理，转发换装、截图等操作到适配器。
 */
public class SpineFunctionDelegate {

    private static final String TAG = SpineFunctionDelegate.class.getSimpleName();

    private static final long SCREENSHOT_DELAY_MS = 150L;
    private static final long SCREENSHOT_RENDER_DURATION_MS = 500L;

    private final BaseSpineAdapter spineAdapter;

    public SpineFunctionDelegate(BaseSpineAdapter spineAdapter) {
        this.spineAdapter = spineAdapter;
    }

    public void setSkin(SpineCodeLoadBean loadBean, Skin skin) {
        this.spineAdapter.setSkin(loadBean, skin);
    }

    public void setSkinAttachment(List<SlotAttachmentBean> attachments, SpineCodeLoadBean loadBean) {
        this.spineAdapter.setSkinAttachment(attachments, loadBean);
    }

    public ArrayMap<String, List<SlotAttachmentBean>> getAreaAttachmentMap() {
        return this.spineAdapter.getAreaAttachmentMap();
    }

    public void executeContinuousRenderWork(long duration) {
        this.spineAdapter.executeContinuousRenderWork(duration);
    }

    public void saveScreenshot(final GLTextureView textureView, final int x, final int y, final String filePath,
                               final ScreenshotFactory.SaveScreenshotListener listener) {
        if (textureView == null) {
            return;
        }
        this.spineAdapter.executeContinuousRenderWork(SCREENSHOT_RENDER_DURATION_MS);
        SingleTaskExecutor.schedule(new Runnable() {
            @Override
            public void run() {
                textureView.queueEvent(new Runnable() {
                    @Override
                    public void run() {
                        ScreenshotFactory.saveScreenshot(0, 60, x, y, true, filePath, listener);
                    }
                });
            }
        }, SCREENSHOT_DELAY_MS);
    }

    public void executeVisualAnimation(SpineCodeLoadBean loadBean, String animationName, boolean loop) {
        this.spineAdapter.executeVisualAnimation(loadBean, animationName, loop);
    }
}