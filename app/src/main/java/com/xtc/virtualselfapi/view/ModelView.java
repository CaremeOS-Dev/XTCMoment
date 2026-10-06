package com.xtc.virtualselfapi.view;

import android.os.Build;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.RequestManager;
import com.xtc.log.LogUtil;
import com.xtc.system.account.WatchAccountBase;
import com.xtc.virtualselfapi.bean.State;
import com.xtc.virtualselfapi.bean.ViewInfo;
import com.xtc.virtualselfapi.bean.db.DbCostume;
import com.xtc.virtualselfapi.bean.db.DbPosition;
import com.xtc.virtualselfapi.bean.net.req.RespPostion;
import com.xtc.virtualselfapi.bean.net.resp.RespCurrentCostumeInfo;
import com.xtc.virtualselfapi.bean.net.resp.RespUserFormat;
import com.xtc.virtualselfapi.constants.Constants;
import com.xtc.virtualselfapi.manager.HttpManager;
import com.xtc.virtualselfapi.manager.VirtualSelfDBServeImpl;
import com.xtc.virtualselfapi.manager.VirtualSelfInitManager;
import com.xtc.virtualselfapi.utils.ScreenUtil;
import com.xtc.virtualselfapi.utils.TimeUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import rx.Observable;
import rx.functions.Func1;
import rx.functions.Func2;

/**
 * 虚拟形象视图模型：把用户数据转换为可渲染的展示元素并补全布局。
 */
public class ModelView {

    private static final String TAG = "Virtual_Self_Api_ModelView";

    private final HttpManager httpManager;

    public ModelView(HttpManager httpManager) {
        this.httpManager = httpManager;
    }

    public int getNormalCostume(int gender) {
        return gender == 0 ? 9 : 10;
    }

    public Observable<List<ViewInfo>> getViewInfoFromUserInfo(RespUserFormat userFormat) {
        return getViewInfoFromUserInfoNotSort(userFormat)
                .flatMap(new Func1<List<ViewInfo>, Observable<ViewInfo>>() {
                    @Override
                    public Observable<ViewInfo> call(List<ViewInfo> viewInfoList) {
                        return Observable.from(viewInfoList);
                    }
                })
                .toSortedList(new Func2<ViewInfo, ViewInfo, Integer>() {
                    @Override
                    public Integer call(ViewInfo first, ViewInfo second) {
                        return sortViewInfoFromLayer(first, second);
                    }
                });
    }

    private Observable<List<ViewInfo>> getViewInfoFromUserInfoNotSort(RespUserFormat userFormat) {
        List<Integer> decoration = userFormat.getDecoration();
        if (decoration == null) {
            decoration = new ArrayList<>();
        }
        Integer suitCostumeId = null;
        List<ViewInfo> viewInfoList = new ArrayList<>();
        boolean supportCardCostume = WatchAccountBase.queryModuleSwitchByBoolean(
                VirtualSelfInitManager.getInstance().getAppContext(),
                Constants.ModuleSwitch.MODULE_SWITCH_COLLECT_CARD, false);
        RespCurrentCostumeInfo currentCollectCardInfo = userFormat.getCurrentCollectCardInfo();
        int skillCostumeId;
        if (currentCollectCardInfo != null) {
            State state = currentCollectCardInfo.getState();
            if (TimeUtils.isSkillCardExpired(state)) {
                skillCostumeId = 0;
            } else {
                skillCostumeId = (int) state.getCostunmeId();
            }
        } else {
            skillCostumeId = 0;
        }
        if (skillCostumeId != 0) {
            DbCostume skillCostume = VirtualSelfDBServeImpl.getInstance().getCostume(skillCostumeId);
            if (skillCostume != null && skillCostume.getType() == 0) {
                LogUtil.d(TAG, "技能需要更换人物套装，不支持替换英雄卡");
                supportCardCostume = false;
            }
        }
        LogUtil.d(TAG, "supportCardCostume = " + supportCardCostume + " skillState:" + skillCostumeId
                + " currentCollectCardInfo:" + currentCollectCardInfo);
        for (Integer decorationId : decoration) {
            ViewInfo viewInfo = new ViewInfo();
            DbCostume costume = VirtualSelfDBServeImpl.getInstance().getCostume(decorationId.intValue());
            if (costume == null) {
                LogUtil.w(TAG, "not found costume :" + decorationId);
            } else {
                LogUtil.d(TAG, "costume = " + costume);
                if (costume.getType() == 0) {
                    LogUtil.i(TAG, "用户使用套装：" + costume.getId());
                    suitCostumeId = Integer.valueOf(costume.getCostumeId());
                    if (supportCardCostume) {
                        DbCostume cardCostume = loadCardCostumeViewInfo(userFormat);
                        LogUtil.i(TAG, "用户使用集卡套装：" + cardCostume);
                        if (cardCostume != null && cardCostume.getType() == 0) {
                            LogUtil.i(TAG, "替换用户套装");
                            suitCostumeId = Integer.valueOf(cardCostume.getCostumeId());
                            costume = cardCostume;
                        }
                    }
                    setSuitPosition(viewInfo);
                }
                convertCostumeToViewInfo(viewInfo, costume);
                viewInfoList.add(viewInfo);
            }
        }
        if (suitCostumeId == null) {
            suitCostumeId = Integer.valueOf(getNormalCostume(userFormat.getGender()));
            DbCostume defaultCostume = VirtualSelfDBServeImpl.getInstance().getCostume(suitCostumeId.intValue());
            if (defaultCostume != null) {
                ViewInfo viewInfo = new ViewInfo();
                setSuitPosition(viewInfo);
                convertCostumeToViewInfo(viewInfo, defaultCostume);
                viewInfoList.add(viewInfo);
            } else {
                LogUtil.w(TAG, "getViewInfoFromUserInfoNotSort suitId costume is null");
            }
        }
        List<Integer> missingPositionIds = new ArrayList<>();
        for (int index = 0; index < viewInfoList.size(); index++) {
            ViewInfo viewInfo = viewInfoList.get(index);
            if (viewInfo.getType() != 0) {
                DbPosition position = VirtualSelfDBServeImpl.getInstance().getPosition(suitCostumeId.intValue(), viewInfo.getId());
                LogUtil.d(TAG, viewInfo.getId() + " position = " + position);
                if (position == null) {
                    missingPositionIds.add(Integer.valueOf(viewInfo.getId()));
                } else {
                    convertDbPositionToViewInfo(position, viewInfo);
                }
            }
        }
        if (missingPositionIds.size() == 0) {
            return Observable.just(viewInfoList);
        }
        LogUtil.d(TAG, "update position from net!");
        return updatePosition(suitCostumeId, viewInfoList, missingPositionIds);
    }

    private DbCostume loadCardCostumeViewInfo(RespUserFormat userFormat) {
        RespCurrentCostumeInfo currentCollectCardInfo = userFormat.getCurrentCollectCardInfo();
        if (currentCollectCardInfo == null || currentCollectCardInfo.getCostume() == null
                || currentCollectCardInfo.getCostume().getCurrent() == 0) {
            return null;
        }
        return VirtualSelfDBServeImpl.getInstance().getCostume(currentCollectCardInfo.getCostume().getCurrent());
    }

    private void setSuitPosition(ViewInfo viewInfo) {
        viewInfo.setWidth(320);
        viewInfo.setHeight(360);
        viewInfo.setX(0);
        viewInfo.setY(0);
    }

    private void convertCostumeToViewInfo(ViewInfo viewInfo, DbCostume costume) {
        viewInfo.setId(costume.getCostumeId());
        viewInfo.setUrl(costume.getUrl());
        viewInfo.setType(costume.getType());
        viewInfo.setLayer(costume.getLayer());
    }

    private Observable<List<ViewInfo>> updatePosition(Integer suitCostumeId, final List<ViewInfo> viewInfoList, List<Integer> ornamentIds) {
        return this.httpManager.getPosition(suitCostumeId.intValue(), ornamentIds)
                .map(new Func1<RespPostion, List<ViewInfo>>() {
                    @Override
                    public List<ViewInfo> call(RespPostion response) {
                        return updatePosition(viewInfoList, response.getList());
                    }
                });
    }

    private List<ViewInfo> updatePosition(List<ViewInfo> viewInfoList, List<DbPosition> positions) {
        VirtualSelfDBServeImpl.getInstance().insertPositionForBatch(positions);
        for (int positionIndex = 0; positionIndex < positions.size(); positionIndex++) {
            DbPosition position = positions.get(positionIndex);
            for (int viewIndex = 0; viewIndex < viewInfoList.size(); viewIndex++) {
                ViewInfo viewInfo = viewInfoList.get(viewIndex);
                if (viewInfo.getId() == position.getOrnamentId()) {
                    convertDbPositionToViewInfo(position, viewInfo);
                    break;
                }
            }
        }
        return viewInfoList;
    }

    private void convertDbPositionToViewInfo(DbPosition position, ViewInfo viewInfo) {
        viewInfo.setX(position.getX());
        viewInfo.setY(position.getY());
        viewInfo.setWidth(position.getWidth());
        viewInfo.setHeight(position.getHeight());
        viewInfo.setRotateX(position.getRotateX());
        viewInfo.setRotateY(position.getRotateY());
        viewInfo.setAngle(position.getAngle());
    }

    private int sortViewInfoFromLayer(ViewInfo first, ViewInfo second) {
        if (first.getLayer() > second.getLayer()) {
            return 1;
        }
        return Objects.equals(Integer.valueOf(first.getLayer()), Integer.valueOf(second.getLayer())) ? 0 : -1;
    }

    public static void loadView(RelativeLayout parent, ViewInfo viewInfo, RequestManager requestManager) {
        ImageView imageView = new ImageView(parent.getContext());
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(viewInfo.getWidth(), viewInfo.getHeight());
        float y = viewInfo.getY();
        if (viewInfo.getType() == 4 || viewInfo.getType() == 3 || viewInfo.getType() == 5) {
            layoutParams.width = ScreenUtil.getScreenWidth(parent.getContext());
            layoutParams.height = ScreenUtil.getScreenHeight(parent.getContext());
        } else if (Build.VERSION.SDK_INT >= 30) {
            layoutParams.width = (int) (layoutParams.width * 1.3f);
            layoutParams.height = (int) (layoutParams.height * 1.3f);
            y *= 1.3f;
        }
        layoutParams.topMargin = (int) y;
        layoutParams.leftMargin = viewInfo.getX();
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        parent.addView(imageView, layoutParams);
        if (viewInfo.getAngle() != 0 && viewInfo.getRotateX() > 0.0f && viewInfo.getRotateY() > 0.0f) {
            LogUtil.e(TAG, "rotation view = " + viewInfo.getId());
            imageView.setPivotX(viewInfo.getRotateX());
            imageView.setPivotY(viewInfo.getRotateY());
            imageView.setRotation((float) viewInfo.getAngle());
        }
        requestManager.load(viewInfo.getUrl()).into(imageView);
    }
}