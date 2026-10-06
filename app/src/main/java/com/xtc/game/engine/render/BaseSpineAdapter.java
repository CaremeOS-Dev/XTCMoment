package com.xtc.game.engine.render;

import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.backends.android.AndroidGraphics;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.esotericsoftware.spine.Animation;
import com.esotericsoftware.spine.AnimationState;
import com.esotericsoftware.spine.AnimationStateData;
import com.esotericsoftware.spine.Bone;
import com.esotericsoftware.spine.Event;
import com.esotericsoftware.spine.Skeleton;
import com.esotericsoftware.spine.SkeletonData;
import com.esotericsoftware.spine.SkeletonJson;
import com.esotericsoftware.spine.SkeletonRenderer;
import com.esotericsoftware.spine.Skin;
import com.esotericsoftware.spine.attachments.Attachment;
import com.xtc.game.engine.bean.NeedRenderEntity;
import com.xtc.game.engine.bean.ScaleNeedRenderEntity;
import com.xtc.game.engine.bean.SkinKey;
import com.xtc.game.engine.bean.SlotAttachmentBean;
import com.xtc.game.engine.bean.SpineCodeLoadBean;
import com.xtc.game.engine.container.BasicSpineRenderConfig;
import com.xtc.game.engine.support.SkeletonHelper;
import com.xtc.game.engine.util.SingleTaskExecutor;
import com.xtc.log.LogUtil;
import com.xtc.virtualselfapi.constants.Constants;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.functions.Action1;

/**
 * 骨骼渲染适配器基类：负责加载骨骼资源、换装、播放动画以及按需渲染。
 */
public abstract class BaseSpineAdapter extends ApplicationAdapter implements InputProcessor {

    private static final String TAG = BaseSpineAdapter.class.getSimpleName();

    private static final int ANIM_EXECUTE_DELAY_TIME = 100;
    private static final float DEFAULT_SCALE = 0.8f;
    private static final String EMPTY_ANIM = "<empty>";
    private static final float MAX_MISS_ATTACHMENT = 1.0f;

    public List<SpineCodeLoadBean> entityList;

    private final ArrayMap<String, List<SlotAttachmentBean>> areaAttachmentMap = new ArrayMap<>();
    private String[] areaNames;
    private BasicSpineRenderConfig basicSpineRenderConfig;
    private OrthographicCamera camera;
    private AndroidGraphics graphics;
    private boolean isAniming;
    private AnimationStateListener animationStateListener;
    private LoadDataListener loadDataListener;
    private List<NeedRenderEntity> needRenderEntities;
    private OnClickListener onClickListener;

    private volatile boolean isNeedRender = true;
    private boolean supportAreaChangeSkin = false;
    private boolean isTouchDown = false;
    private boolean isTouchDragged = false;
    private boolean initDataFinish = false;
    private boolean allowAutoStopRender;

    private final AnimationState.AnimationStateListener spineAnimationStateListener = new AnimationState.AnimationStateListener() {
        @Override
        public void event(AnimationState.TrackEntry trackEntry, Event event) {
        }

        @Override
        public void interrupt(AnimationState.TrackEntry trackEntry) {
        }

        @Override
        public void dispose(AnimationState.TrackEntry trackEntry) {
        }

        @Override
        public void start(AnimationState.TrackEntry trackEntry) {
            LogUtil.i(TAG, "animation start :" + isAniming + " isNeedRender:" + isNeedRender);
            isAniming = true;
            if (animationStateListener != null) {
                animationStateListener.onAnimationStart();
            }
        }

        @Override
        public void end(AnimationState.TrackEntry trackEntry) {
            LogUtil.i(TAG, "animation end :" + isAniming + " isNeedRender:" + isNeedRender);
        }

        @Override
        public void complete(final AnimationState.TrackEntry trackEntry) {
            Observable.timer(ANIM_EXECUTE_DELAY_TIME, TimeUnit.MILLISECONDS).subscribe(new Action1<Long>() {
                @Override
                public void call(Long value) {
                    isAniming = false;
                    isNeedRender = false;
                    for (SpineCodeLoadBean loadBean : entityList) {
                        AnimationState state = loadBean.getState();
                        if (!loadBean.isAnimRunning()) {
                            LogUtil.i(TAG, "complete now is emptyAnimation callback continue");
                        } else {
                            state.update(0.0f);
                            loadBean.setAnimRunning(false);
                        }
                    }
                    LogUtil.i(TAG, "动画结束停止渲染");
                    float animationTime = trackEntry.getAnimationTime() * 1000.0f;
                    LogUtil.i(TAG, "animation complete :" + isAniming + " isNeedRender:" + isNeedRender
                            + " animationTime :" + animationTime);
                    if (animationStateListener != null) {
                        animationStateListener.onAnimationEnd();
                    }
                }
            }, new Action1<Throwable>() {
                @Override
                public void call(Throwable throwable) {
                    LogUtil.w(TAG, "animation end error :" + throwable);
                }
            });
        }
    };

    /** 动画状态监听。 */
    public interface AnimationStateListener {
        void onAnimationStart();

        void onAnimationEnd();
    }

    /** 资源加载监听。 */
    public interface LoadDataListener {
        void onLoadDataFinish(SpineCodeLoadBean loadBean);

        void onLoadDataError(int code, String message);

        void onLoadDataDestroy();
    }

    /** 点击监听。 */
    public interface OnClickListener {
        void onClick();
    }

    public abstract List<NeedRenderEntity> initRender();

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }

    @Override
    public void create() {
        this.camera = new OrthographicCamera();
        this.needRenderEntities = initRender();
        this.entityList = new ArrayList<>();
        try {
            initData();
            initDefaultRender();
        } catch (Exception e) {
            LogUtil.e(TAG, "BaseSpineAdapter initData onFail:", e);
            if (this.loadDataListener != null) {
                this.loadDataListener.onLoadDataError(3, "初始化失败");
            }
        }
    }
    public void setGraphics(AndroidGraphics graphics) {
        this.graphics = graphics;
    }

    public AndroidGraphics getGraphics() {
        return this.graphics;
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public void setAnimationStateListener(AnimationStateListener animationStateListener) {
        this.animationStateListener = animationStateListener;
    }

    private void setSupportAreaChangeSkin(boolean supportAreaChangeSkin, String... areaNames) {
        if (this.supportAreaChangeSkin) {
            return;
        }
        this.supportAreaChangeSkin = supportAreaChangeSkin;
        if (supportAreaChangeSkin) {
            this.areaNames = areaNames;
            return;
        }
        this.areaAttachmentMap.clear();
        for (String ignored : areaNames) {
        }
    }

    public ArrayMap<String, List<SlotAttachmentBean>> getAreaAttachmentMap() {
        return this.areaAttachmentMap;
    }

    public void setBasicSpineRenderConfig(BasicSpineRenderConfig basicSpineRenderConfig) {
        this.basicSpineRenderConfig = basicSpineRenderConfig;
    }

    public void setLoadDataListener(LoadDataListener loadDataListener) {
        this.loadDataListener = loadDataListener;
    }

    /** 加载完成后应用默认皮肤。 */
    public void initDefaultRender() {
        if (this.basicSpineRenderConfig.isSupportAreaChangeSkin()) {
            setSupportAreaChangeSkin(true, this.basicSpineRenderConfig.getAreaNames());
        }
        int missCount = 0;
        for (SpineCodeLoadBean loadBean : this.entityList) {
            NeedRenderEntity entity = loadBean.getNeedRenderEntity();
            String defaultSkinName = entity.getDefaultSkinName();
            List<SlotAttachmentBean> attachments;
            if (!TextUtils.isEmpty(defaultSkinName)) {
                attachments = SkeletonHelper.findSlotAttachments(loadBean, defaultSkinName);
            } else {
                List<SlotAttachmentBean> found = new ArrayList<>();
                for (SlotAttachmentBean bean : entity.getDefaultAttachments()) {
                    SlotAttachmentBean matched = SkeletonHelper.findSlotAttachment(loadBean, bean.getSuitName(), bean.getAttachment().getName());
                    if (matched == null) {
                        LogUtil.w(TAG, "defaultAttachment：" + bean + " don't find local data Skeleton check version");
                        missCount++;
                    } else {
                        found.add(matched);
                    }
                }
                attachments = found;
            }
            if (attachments.size() == 0 || missCount > MAX_MISS_ATTACHMENT) {
                if (this.loadDataListener != null) {
                    this.loadDataListener.onLoadDataError(2, "资源缺失数超过最大接受值，渲染失败");
                }
                return;
            }
            this.initDataFinish = true;
            setSkinAttachment(attachments, loadBean);
            if (this.loadDataListener != null) {
                this.loadDataListener.onLoadDataFinish(loadBean);
            }
        }
    }

    private void initData() {
        List<NeedRenderEntity> entities = this.needRenderEntities;
        if (entities == null || entities.size() == 0) {
            return;
        }
        for (NeedRenderEntity entity : this.needRenderEntities) {
            Log.i(TAG, "====开始加载 数据资源:" + entity + "=====");
            if (!checkResourceValid(entity)) {
                Log.w(TAG, "====数据效验失败:" + entity + "=====");
                if (this.loadDataListener == null) {
                    break;
                }
                this.loadDataListener.onLoadDataError(1, "数据效验失败");
                break;
            }
            SpineCodeLoadBean loadBean = loadResource(entity.getAtlasPath(), entity.getSkeletonPath(),
                    entity.getX(), entity.getY(), entity.getScale());
            loadBean.setNeedRenderEntity(entity);
            loadSkin(loadBean);
            parseData(loadBean);
            loadAnim(loadBean);
            dealBoneScale(entity, loadBean);
            this.entityList.add(loadBean);
            printResourceInfo(loadBean);
        }
        Gdx.input.setInputProcessor(this);
        this.allowAutoStopRender = this.basicSpineRenderConfig.isAllowAutoStopRender();
    }

    private void dealBoneScale(NeedRenderEntity entity, SpineCodeLoadBean loadBean) {
        if (entity instanceof ScaleNeedRenderEntity) {
            Bone bone = loadBean.getSkeleton().getRootBone();
            ScaleNeedRenderEntity scaleEntity = (ScaleNeedRenderEntity) entity;
            bone.setScaleX(scaleEntity.getScaleX());
            bone.setScaleY(scaleEntity.getScaleY());
        }
    }

    private boolean checkResourceValid(NeedRenderEntity entity) {
        return new File(entity.getAtlasPath()).exists() && new File(entity.getSkeletonPath()).exists();
    }

    private SpineCodeLoadBean loadResource(String atlasPath, String skeletonPath, int x, int y, float scale) {
        SpineCodeLoadBean loadBean = new SpineCodeLoadBean();
        TextureAtlas atlas = new TextureAtlas(Gdx.files.internal(atlasPath));
        SkeletonJson skeletonJson = new SkeletonJson(atlas);
        if (scale <= 0.0f) {
            scale = DEFAULT_SCALE;
        }
        skeletonJson.setScale(scale);
        SkeletonData skeletonData = skeletonJson.readSkeletonData(Gdx.files.internal(skeletonPath));
        Skeleton skeleton = new Skeleton(skeletonData);
        skeleton.setPosition(x, y);
        loadBean.setAtlas(atlas);
        loadBean.setSkeleton(skeleton);
        loadBean.setSkeletonData(skeletonData);
        return loadBean;
    }

    private void loadSkin(SpineCodeLoadBean loadBean) {
        loadBean.setSkins(loadBean.getSkeletonData().getSkins());
    }

    private void parseData(SpineCodeLoadBean loadBean) {
        loadBean.setAnimations(loadBean.getSkeletonData().getAnimations());
        for (Skin skin : loadBean.getSkins()) {
            findSkinPart(loadBean, skin);
        }
    }

    private void findSkinPart(SpineCodeLoadBean loadBean, Skin skin) {
        if (skin == null) {
            return;
        }
        List<SkinKey> skinKeys = loadBean.getSkinKeys();
        HashMap<SkinKey, List<Attachment>> skinPartMap = loadBean.getSkinPartMap();
        try {
            Field attachmentsField = skin.getClass().getDeclaredField("attachments");
            attachmentsField.setAccessible(true);
            ObjectMap attachments = (ObjectMap) attachmentsField.get(skin);
            if (attachments != null && attachments.size != 0) {
                for (Object keyObject : attachments.keys()) {
                    Class<?> keyClass = keyObject.getClass();
                    Field slotIndexField = keyClass.getDeclaredField("slotIndex");
                    slotIndexField.setAccessible(true);
                    int slotIndex = ((Integer) slotIndexField.get(keyObject)).intValue();
                    Field nameField = keyClass.getDeclaredField("name");
                    nameField.setAccessible(true);
                    SkinKey skinKey = new SkinKey(skin.getName(), slotIndex, (String) nameField.get(keyObject));
                    skinKeys.add(skinKey);
                    Attachment attachment = (Attachment) attachments.get(keyObject);
                    List<Attachment> list = skinPartMap.get(skinKey);
                    if (list == null) {
                        list = new ArrayList<>();
                        skinPartMap.put(skinKey, list);
                    }
                    list.add(attachment);
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    private void loadAnim(SpineCodeLoadBean loadBean) {
        AnimationState state = new AnimationState(new AnimationStateData(loadBean.getSkeletonData()));
        state.addListener(this.spineAnimationStateListener);
        loadBean.setState(state);
    }

    private void printResourceInfo(SpineCodeLoadBean loadBean) {
        Log.i(TAG, "==============资源加载完成======================");
        Array<Skin> skins = loadBean.getSkins();
        List<SkinKey> skinKeys = loadBean.getSkinKeys();
        Array<Animation> animations = loadBean.getAnimations();
        Log.w(TAG, "资源拥有套装数:" + skins.size);
        for (Skin skin : skins) {
            Log.i(TAG, "套装名称:" + skin);
        }
        Log.w(TAG, "资源拥有皮肤数:" + skinKeys.size());
        for (SkinKey skinKey : skinKeys) {
            Log.i(TAG, "皮肤名称:" + skinKey);
        }
        Log.w(TAG, "动画拥有数:" + animations.size);
        for (Animation animation : animations) {
            Log.i(TAG, "动画名称:" + animation.getName());
        }
        Log.i(TAG, "==============资源数据打印完成==================");
    }
    @Override
    public void render() {
        if (!this.initDataFinish) {
            return;
        }
        if (!this.isNeedRender && this.allowAutoStopRender && !this.isAniming) {
            if (this.graphics != null) {
                Log.i(TAG, "当前未执行动画，人物为静止状态，不在执行render回调，降低功耗");
                this.graphics.setContinuousRendering(false);
                this.isNeedRender = true;
            }
            return;
        }
        try {
            Gdx.gl.glClear(16384);
            for (SpineCodeLoadBean loadBean : this.entityList) {
                AnimationState state = loadBean.getState();
                Skeleton skeleton = loadBean.getSkeleton();
                PolygonSpriteBatch batch = loadBean.getBatch();
                SkeletonRenderer renderer = loadBean.getRenderer();
                state.update(Gdx.graphics.getDeltaTime());
                state.apply(skeleton);
                skeleton.updateWorldTransform();
                this.camera.update();
                batch.getProjectionMatrix().set(this.camera.combined);
                batch.begin();
                renderer.draw(batch, skeleton);
                batch.end();
            }
        } catch (Exception e) {
            LogUtil.e(TAG, "render fail :", e);
        }
    }

    @Override
    public void resize(int width, int height) {
        this.camera.setToOrtho(false);
    }

    @Override
    public void dispose() {
        List<SpineCodeLoadBean> list = this.entityList;
        if (list == null) {
            return;
        }
        for (SpineCodeLoadBean loadBean : list) {
            loadBean.getAtlas().dispose();
        }
    }

    public void setSkin(SpineCodeLoadBean loadBean, Skin skin) {
        executeRenderWork();
        Skeleton skeleton = loadBean.getSkeleton();
        skeleton.setSkin(skin);
        skeleton.setSlotsToSetupPose();
    }

    public void setSkinAttachment(List<SlotAttachmentBean> attachments, SpineCodeLoadBean loadBean) {
        Skeleton skeleton = loadBean.getSkeleton();
        if (skeleton == null) {
            return;
        }
        Skin skin = skeleton.getSkin();
        if (skin == null) {
            skin = new Skin("default");
        }
        if (this.supportAreaChangeSkin) {
            setCaseAreaUpdateSlotAttachmentIntoSkin(skin, attachments);
        } else {
            setSlotAttachmentIntoSkin(skin, attachments);
        }
        skeleton.setSkin(skin);
        skeleton.setSlotsToSetupPose();
        executeRenderWork();
        if (this.allowAutoStopRender) {
            SingleTaskExecutor.schedule(new Runnable() {
                @Override
                public void run() {
                    isNeedRender = false;
                }
            }, Constants.DEFAULT_INIT_DELAY_TIME);
        }
    }

    private void setSlotAttachmentIntoSkin(Skin skin, List<SlotAttachmentBean> attachments) {
        for (SlotAttachmentBean bean : attachments) {
            if (bean == null) {
                LogUtil.w(TAG, "setSlotAttachmentIntoSkin slotAttachmentBean is null continue");
            } else {
                Log.w(TAG, "设置皮肤咯~~ 插槽要设置的皮肤数据是 :" + bean);
                skin.addAttachment(bean.getSlotIndex(), bean.getSlotName(), bean.getAttachment());
            }
        }
    }

    private void setCaseAreaUpdateSlotAttachmentIntoSkin(Skin skin, List<SlotAttachmentBean> attachments) {
        ArrayMap<String, List<SlotAttachmentBean>> grouped = new ArrayMap<>();
        for (SlotAttachmentBean bean : attachments) {
            if (bean == null) {
                LogUtil.w(TAG, "setCaseAreaUpdateSlotAttachmentIntoSkin slotAttachmentBean is null continue");
            } else {
                String area = parseSkinAttachmentArea(bean);
                if (TextUtils.isEmpty(area)) {
                    setSlotAttachmentIntoSkin(skin, Collections.singletonList(bean));
                } else {
                    List<SlotAttachmentBean> list = grouped.get(area);
                    if (list == null) {
                        list = new ArrayList<>();
                    }
                    list.add(bean);
                    grouped.put(area, list);
                }
            }
        }
        for (String key : grouped.keySet()) {
            List<SlotAttachmentBean> list = grouped.get(key);
            if (this.areaAttachmentMap.containsKey(key)) {
                this.areaAttachmentMap.replace(key, list);
            } else {
                this.areaAttachmentMap.put(key, list);
            }
        }
        Collection<List<SlotAttachmentBean>> values = this.areaAttachmentMap.values();
        skin.clear();
        for (List<SlotAttachmentBean> list : values) {
            setSlotAttachmentIntoSkin(skin, list);
        }
    }

    public void executeRenderWork() {
        if (this.allowAutoStopRender && this.graphics != null) {
            this.isNeedRender = true;
            Log.i(TAG, "执行渲染工作");
            this.graphics.requestRendering();
        }
    }

    public void executeContinuousRenderWork(long duration) {
        if (this.allowAutoStopRender && this.graphics != null) {
            this.graphics.setContinuousRendering(true);
            executeRenderWork();
            if (duration <= 0) {
                return;
            }
            SingleTaskExecutor.schedule(new Runnable() {
                @Override
                public void run() {
                    isNeedRender = false;
                    Log.i(TAG, "延迟停止渲染");
                }
            }, duration);
        }
    }

    private String parseSkinAttachmentArea(SlotAttachmentBean bean) {
        if (bean == null) {
            return null;
        }
        String slotName = bean.getSlotName();
        for (String areaName : this.areaNames) {
            if (slotName.contains(areaName)) {
                Log.i(TAG, "解析附件数据属于 " + areaName + "   附件数据为：" + bean);
                return areaName;
            }
        }
        return null;
    }

    public void executeVisualAnimation(final SpineCodeLoadBean loadBean, final String animationName, final boolean loop) {
        if (loadBean.isAnimRunning()) {
            LogUtil.i(TAG, "当前动画还在执行，不继续执行动画响应请求");
            return;
        }
        if (this.graphics != null) {
            this.graphics.setContinuousRendering(true);
            executeRenderWork();
        }
        final AnimationState state = loadBean.getState();
        loadBean.setAnimRunning(true);
        Observable.timer(ANIM_EXECUTE_DELAY_TIME, TimeUnit.MILLISECONDS).subscribe(new Action1<Long>() {
            @Override
            public void call(Long value) {
                if (state == null) {
                    return;
                }
                state.clearTracks();
                state.setAnimation(loadBean.getIndex(), animationName, loop);
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                LogUtil.w(TAG, "executeVisualAnimation:" + throwable);
            }
        });
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        this.isTouchDown = true;
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        this.isTouchDown = false;
        this.isTouchDragged = false;
        if (this.onClickListener != null) {
            this.onClickListener.onClick();
        }
        LogUtil.i(TAG, "点击了spine容器");
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        this.isTouchDragged = true;
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        this.isTouchDragged = false;
        return false;
    }
}