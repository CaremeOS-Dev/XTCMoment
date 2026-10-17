package com.xtc.moment.module.publish.text;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xtc.architecture.mvp.BaseActivity;
import com.xtc.architecture.mvp.PermissionListener;
import com.xtc.log.LogUtil;
import com.xtc.moment.R;
import com.xtc.moment.asynclayout.AsyncLayoutLoader;
import com.xtc.moment.constants.ModuleSwitchConstant;
import com.xtc.moment.db.bean.DbMoment;
import com.xtc.moment.db.bean.DbTemplate;
import com.xtc.moment.module.Constants;
import com.xtc.moment.module.bean.FriendsVisibleBean;
import com.xtc.moment.module.bean.MaxTextLengthBean;
import com.xtc.moment.module.bean.PoiBean;
import com.xtc.moment.module.main.MomentActivity;
import com.xtc.moment.module.publish.location.PublishLocationActivity;
import com.xtc.moment.module.publish.multi.ConstraintLayoutView;
import com.xtc.moment.module.publish.multi.SaveDynamic;
import com.xtc.moment.module.publish.topic.ShowTopicAdapter;
import com.xtc.moment.module.publish.topic.TopicActivity;
import com.xtc.moment.net.bean.TopicListBean;
import com.xtc.moment.third.behavior.MomentBehavior;
import com.xtc.utils.common.CollectionUtil;
import com.xtc.moment.module.publish.visible.VisibleTypeActivity;
import com.xtc.moment.module.widget.LoadingPupWindowHolder;
import com.xtc.moment.module.widget.MaxLengthWatcher;
import com.xtc.moment.module.widget.MomentContentView;
import com.xtc.moment.module.widget.MomentVisibleShowUtil;
import com.xtc.moment.util.EventData;
import com.xtc.moment.util.HandlerUtil;
import com.xtc.moment.util.PermissionStringUtils;
import com.xtc.moment.util.PublishErrorUtil;
import com.xtc.moment.util.SystemUtil;
import com.xtc.moment.util.ToastUtil;
import com.xtc.moment.util.SharedTool;
import com.xtc.moment.util.Utils;
import com.xtc.moment.util.switchs.ModuleSwitchUtil;
import com.xtc.ui.widget.button.DoubleFlatButton;
import com.xtc.ui.widget.dialog.DoubleFlatBtnWithTitleDialog;
import com.xtc.ui.widget.dialog.bean.noIcon.DoubleFlatBtnWithTitleBean;
import com.xtc.ui.widget.util.DialogUtil;
import com.xtc.utils.encode.JSONUtil;
import com.xtc.utils.system.NetworkUtils;

import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.Callable;

import org.greenrobot.eventbus.EventBus;

import rx.Completable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * 文字/心情动态发布页：支持心情状态、纯位置、纯文字三种发布类型，
 * 负责草稿恢复、可见范围选择与位置选择。
 */
public class PushTextActivity extends BaseActivity<IPublishTextView, PublishTextPresenter>
        implements IPublishTextView {

    private static final String TAG = "PushTextActivity";

    /** 发布类型：纯文字。 */
    private static final int TYPE_TEXT = 3;
    /** 发布类型：纯位置。 */
    private static final int TYPE_LOCATION = 2;
    /** 位置选择页面的请求码。 */
    private static final int REQUEST_LOCATION = 5;
    /** 可见范围选择页面的请求码。 */
    private static final int REQUEST_VISIBLE_RANGE = 6;

    private final Context context = this;

    private DoubleFlatBtnWithTitleDialog dialog;
    private EditText etHint;
    private volatile FriendsVisibleBean friendsVisibleBean;
    private String inputContent;
    private InputMethodManager inputManager;
    private ImageView ivVisibleRange;
    private LinearLayout llLocation;
    private LoadingPupWindowHolder loadingPupWindowHolder;
    private MomentContentView momentContentView;
    private volatile PoiBean poiBean;
    private Button publish;
    private RelativeLayout rlVisibleRange;
    private volatile DbTemplate templateBean;
    private TextView tvLocation;
    private TextView tvVisibleRange;
    private TextView tvVisibleRangeDetails;
    private LinearLayout llTopic;
    private RecyclerView rvTopicList;
    private ShowTopicAdapter showTopicAdapter;
    private final ArrayList<TopicListBean> topicList = new ArrayList<>();
    private boolean topicModuleSwitch = true;
    private String topicSourceType = null;
    private int type;

    @Override
    public PublishTextPresenter createPresenter() {
        return new PublishTextPresenter(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
    }

    @Override
    public void initView() {
        LogUtil.d(TAG, "initView");
        AsyncLayoutLoader.getInstance().setContentView(this, R.layout.activity_push_text);
        this.loadingPupWindowHolder = new LoadingPupWindowHolder(this);
        this.loadingPupWindowHolder.setOnSuccessAction(new Runnable() {
            @Override
            public void run() {
                PushTextActivity.this.setResult(RESULT_OK);
                PushTextActivity.this.finish();
            }
        });
        this.momentContentView = (MomentContentView) findViewById(R.id.tv_moment_content_mood_or_state);
        this.publish = (Button) findViewById(R.id.push_picture);
        this.rlVisibleRange = (RelativeLayout) findViewById(R.id.ll_visible_range);
        this.llLocation = (LinearLayout) findViewById(R.id.ll_location);
        this.tvLocation = (TextView) findViewById(R.id.tv_location);
        this.tvVisibleRange = (TextView) findViewById(R.id.tv_visible_range);
        this.tvVisibleRangeDetails = (TextView) findViewById(R.id.tv_visible_range_details);
        this.ivVisibleRange = (ImageView) findViewById(R.id.iv_visible_range);
        this.etHint = (EditText) findViewById(R.id.et_push_picture_text);
        this.llTopic = (LinearLayout) findViewById(R.id.ll_topic);
        this.llTopic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PushTextActivity.this, TopicActivity.class);
                intent.putParcelableArrayListExtra(TopicActivity.INTENT_CHOOSE_DATA,
                        PushTextActivity.this.topicList);
                PushTextActivity.this.startActivityForResult(intent, Constants.StartActivityOnResultCode.MOMENT_TOPIC_REQUEST_CODE);
            }
        });
        setSwipeBack();
        initEditText();
        this.publish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!SystemUtil.isFastLongDoubleClick()) {
                    PushTextActivity.this.dealPublish();
                } else {
                    LogUtil.i(TAG, "onClick: click too fast.");
                }
            }
        });
        this.rlVisibleRange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                VisibleTypeActivity.startForResult(PushTextActivity.this,
                        PushTextActivity.this.friendsVisibleBean, 1);
            }
        });
        this.llLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PushTextActivity.this.handlerSignClick();
            }
        });
        this.inputManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (ModuleSwitchUtil.queryModuleSwitchByBoolean(this, ModuleSwitchConstant.MODULE_VISIBLE_RANGE, false)) {
            this.rlVisibleRange.setVisibility(View.VISIBLE);
        }
        initShowTopicAdapter();
    }

    private void initEditText() {
        // 111: 多行文本输入类型（与原实现保持一致）。
        this.etHint.setInputType(111);
        this.etHint.setFilters(new InputFilter[]{
                new InputFilter.LengthFilter(MaxTextLengthBean.getInstance(this).getMaxLength())});
        this.etHint.addTextChangedListener(new PushWatcher(this,
                MaxTextLengthBean.getInstance(this).getMaxLength(), getString(R.string.content_is_over)));
    }

    @Override
    public void initData() {
        LogUtil.d(TAG, "initData");
        this.type = getIntent().getIntExtra(Constants.INTENT_EXTRA_PUBLISH_TYPE, -1);
        this.topicModuleSwitch = ModuleSwitchUtil.queryModuleSwitchByBoolean(
                getApplicationContext(), ModuleSwitchConstant.MODULE_SWITCH_TOPIC, true);
        showOrHideTopicModule(this.topicModuleSwitch);
        String textContent = getIntent().getStringExtra(Constants.INTENT_EXTRA_TEXT_CONTENT);
        if (!TextUtils.isEmpty(textContent)) {
            this.etHint.setText(textContent);
        }
        Completable.fromCallable(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                recoverLastState();
                return null;
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action0() {
                    @Override
                    public void call() {
                        dealTopicJson();
                        refreshView();
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        LogUtil.e(TAG, "revocerLastState error: ", throwable);
                    }
                });
    }

    /**
     * 刷新发布页视图：位置文案、可见范围、按发布类型切换输入区与位置入口。
     */
    private void refreshView() {
        if (this.poiBean == null) {
            this.tvLocation.setText(R.string.dont_show_location);
        } else {
            this.tvLocation.setText(this.poiBean.getAddressDesc());
        }
        boolean lbsPublishEnabled = ModuleSwitchUtil.queryModuleSwitchByBoolean(this.context,
                ModuleSwitchConstant.MODULE_SWITCH_LBS_PUBLISH, false);
        MomentVisibleShowUtil.dealVisibleRange(this.context, this.friendsVisibleBean,
                this.ivVisibleRange, this.tvVisibleRange, this.tvVisibleRangeDetails);
        int publishType = this.type;
        if (publishType == 0 || publishType == 1) {
            this.momentContentView.setVisibility(View.VISIBLE);
            this.etHint.setVisibility(View.INVISIBLE);
            if (lbsPublishEnabled) {
                this.llLocation.setVisibility(View.VISIBLE);
            }
            dealMoodOrStateContent();
        } else if (publishType == TYPE_LOCATION) {
            this.momentContentView.setVisibility(View.GONE);
            this.etHint.setVisibility(View.GONE);
            this.llLocation.setVisibility(View.VISIBLE);
        } else if (publishType == TYPE_TEXT) {
            this.momentContentView.setVisibility(View.GONE);
            this.etHint.setVisibility(View.VISIBLE);
            if (lbsPublishEnabled) {
                this.llLocation.setVisibility(View.VISIBLE);
            }
        }
        dealCurrentPushBtnBackground();
    }

    private void dealMoodOrStateContent() {
        String templateJson = getIntent().getStringExtra(Constants.INTENT_EXTRA_DB_TEMPLATE);
        if (!TextUtils.isEmpty(templateJson)) {
            this.templateBean = (DbTemplate) JSONUtil.fromJSON(templateJson, DbTemplate.class);
        }
        if (this.templateBean == null || TextUtils.isEmpty(this.templateBean.getResource())
                || TextUtils.isEmpty(this.templateBean.getContent())) {
            finish();
        } else {
            this.momentContentView.setContext(this.context);
            this.momentContentView.setRichText(this.templateBean.getResource(), this.templateBean.getContent());
        }
    }

    private void handlerSignClick() {
        requestRunTimePermission(PermissionStringUtils.LOCATION_PERMISSIONS, new PermissionListener() {
            @Override
            public void onGranted() {
                LogUtil.i(TAG, "onGranted llLocation Permission");
                PushTextActivity.this.go2PublishLocation();
            }

            @Override
            public void onPartPermissionDenied(List<String> granted, List<String> denied) {
                LogUtil.d(TAG, "onPartPermissionDenied: llLocation Permission");
            }
        });
    }

    private void go2PublishLocation() {
        SystemUtil.setEnterFuncFlag();
        PublishLocationActivity.startForResult(this, this.type);
    }

    private void dealPublish() {
        if (!isConnected()) {
            ToastUtil.showLong(this.context, getString(R.string.net_work_exception));
            return;
        }
        this.loadingPupWindowHolder.showLoading(getWindow().getDecorView());
        int publishType = this.type;
        if (publishType == 0 || publishType == 1) {
            if (this.templateBean == null || TextUtils.isEmpty(this.templateBean.getResource())
                    || TextUtils.isEmpty(this.templateBean.getContent())) {
                this.loadingPupWindowHolder.dismissLoading();
                return;
            }
            this.presenter.publishMoodOrStateMoment(this.type, this.templateBean.getResource(),
                    this.templateBean.getResourceId(), this.templateBean.getContent(),
                    this.poiBean, this.friendsVisibleBean, this.topicList, this.topicSourceType);
            return;
        }
        if (publishType == TYPE_LOCATION) {
            if (this.poiBean == null) {
                ToastUtil.showLong(this.context, getString(R.string.string_after_edit));
                this.loadingPupWindowHolder.dismissLoading();
                return;
            }
            this.presenter.publishMoment(this.poiBean, this.friendsVisibleBean,
                    this.topicList, this.topicSourceType);
            return;
        }
        if (publishType != TYPE_TEXT) {
            return;
        }
        this.inputContent = this.etHint.getText().toString();
        if (!TextUtils.isEmpty(this.inputContent)) {
            this.presenter.publishMoment(this.inputContent, TYPE_TEXT, TYPE_TEXT,
                    this.poiBean, this.friendsVisibleBean, this.topicList, this.topicSourceType);
        } else if (this.poiBean != null) {
            this.presenter.publishMoment(this.poiBean, this.friendsVisibleBean,
                    this.topicList, this.topicSourceType);
        } else {
            ToastUtil.showLong(this.context, getString(R.string.string_after_edit));
            this.loadingPupWindowHolder.dismissLoading();
        }
    }

    /**
     * 恢复上次未完成的发布草稿。
     */
    private void recoverLastState() {
        PublishTextBean savedBean = SaveDynamic.getSavePublishTextBean(this.context);
        if (savedBean == null) {
            return;
        }
        int savedType = savedBean.getType();
        if (savedType == -1) {
            LogUtil.i(TAG, "not exist dynamic data clearCache");
            clearCache();
            return;
        }
        this.type = savedType;
        String savedText = savedBean.getText();
        DbTemplate savedTemplate = savedBean.getDbTemplate();
        this.poiBean = savedBean.getPoiBean();
        this.friendsVisibleBean = savedBean.getFriendsVisibleBean();
        if (!TextUtils.isEmpty(savedText)) {
            this.etHint.setText(savedText);
        }
        if (savedTemplate != null) {
            this.templateBean = savedTemplate;
        }
        if (!CollectionUtil.isEmpty(savedBean.getTopicList()) && this.topicModuleSwitch) {
            this.topicList.addAll(savedBean.getTopicList());
            this.showTopicAdapter.setData(savedBean.getTopicList());
        }
        clearCache();
    }

    private void setSwipeBack() {
        ((ConstraintLayoutView) findViewById(R.id.layout_root)).setCallBack(new ConstraintLayoutView.CallBack() {
            @Override
            public void onSwipeBack() {
                LogUtil.i(TAG, "onSwipeBack");
                String content = PushTextActivity.this.etHint.getText().toString();
                if (TextUtils.isEmpty(content) && PushTextActivity.this.templateBean == null
                        && PushTextActivity.this.poiBean == null
                        && PushTextActivity.this.friendsVisibleBean == null) {
                    LogUtil.i(TAG, "content is null, show the empty notify");
                    PushTextActivity.this.showEmptyReminder();
                    return;
                }
                DoubleFlatBtnWithTitleBean bean = new DoubleFlatBtnWithTitleBean(
                        PushTextActivity.this.context, true,
                        PushTextActivity.this.getString(R.string.exit_remind),
                        PushTextActivity.this.getString(R.string.whether_retain),
                        R.string.not_retain_bug, R.string.retain_but);
                PushTextActivity.this.dialog = DialogUtil.makeDoubleFlatBtnWithTitleDialog(
                        PushTextActivity.this.context, bean);
                PushTextActivity.this.dialog.setWholeBackgroud(R.color.color_000000);
                int[] rightBgColors = {R.color.publish_btn_color_start, R.color.publish_btn_color_end};
                DoubleFlatButton bottomBtn = PushTextActivity.this.dialog.getBottomBtn();
                TextView leftButton = bottomBtn.getLeftButton();
                bottomBtn.setRightBgColorIdArray(rightBgColors);
                leftButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DialogUtil.dismissDialog(PushTextActivity.this.dialog);
                        PushTextActivity.this.clearCache();
                        PushTextActivity.this.startActivity(new Intent(
                                PushTextActivity.this.context, MomentActivity.class));
                        PushTextActivity.this.finish();
                    }
                });
                bottomBtn.getRightArea().setOnClickListener(
                        new SaveDraftOnExitClickListener(content));
                if (PushTextActivity.this.dialog.isShowing()) {
                    return;
                }
                DialogUtil.showDialog(PushTextActivity.this.dialog);
            }
        });
    }

    private void showEmptyReminder() {
        final DoubleFlatBtnWithTitleDialog emptyDialog = DialogUtil.makeDoubleFlatBtnWithTitleDialog(
                this.context, new DoubleFlatBtnWithTitleBean(this.context, true,
                        getString(R.string.exit_remind), getString(R.string.exit_redact),
                        R.string.cancel_btn_text, R.string.exit));
        emptyDialog.setWholeBackgroud(R.color.color_000000);
        int[] rightBgColors = {R.color.publish_btn_color_start, R.color.publish_btn_color_end};
        DoubleFlatButton bottomBtn = emptyDialog.getBottomBtn();
        TextView leftButton = bottomBtn.getLeftButton();
        bottomBtn.setRightBgColorIdArray(rightBgColors);
        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogUtil.dismissDialog(emptyDialog);
            }
        });
        bottomBtn.getRightArea().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PushTextActivity.this.clearCache();
                PushTextActivity.this.startActivity(new Intent(
                        PushTextActivity.this.context, MomentActivity.class));
                PushTextActivity.this.finish();
            }
        });
        if (emptyDialog.isShowing()) {
            return;
        }
        DialogUtil.showDialog(emptyDialog);
    }

    public void clearCache() {
        SaveDynamic.removePublishTextBean(this.context);
    }

    @Override
    public void publishSuccess(DbMoment dbMoment) {
        this.loadingPupWindowHolder.showSuccess();
        EventBus.getDefault().post(dbMoment);
        EventBus.getDefault().post(new EventData(5, dbMoment));
    }

    @Override
    public void publishFail(String message) {
        this.loadingPupWindowHolder.dismissLoading();
        if (!NetworkUtils.isConnected(this)) {
            ToastUtil.showShortCover(this, getString(R.string.net_work_exception));
        } else {
            PublishErrorUtil.showFailMessage(this, message);
        }
    }

    @Override
    public void publishLimited() {
        this.loadingPupWindowHolder.dismissLoading();
        ToastUtil.showShortCover(this, getString(R.string.publish_limit));
    }

    @Override
    public void publishInvalidate() {
        this.loadingPupWindowHolder.dismissLoading();
        ToastUtil.showShortCover(this, getString(R.string.publish_sensitive));
    }

    @Override
    protected void onDestroy() {
        this.loadingPupWindowHolder.dismissLoading();
        LoadingPupWindowHolder holder = this.loadingPupWindowHolder;
        if (holder != null) {
            holder.clean();
        }
        HandlerUtil.runOnBackground(new Runnable() {
            @Override
            public void run() {
                Utils.hideSoftInputFromWindow(PushTextActivity.this.inputManager,
                        PushTextActivity.this.etHint);
            }
        });
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        LogUtil.i(TAG, "onActivityResult requestCode = " + requestCode + " resultCode = " + resultCode);
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_LOCATION) {
            PoiBean resultPoi = (PoiBean) data.getParcelableExtra("poi");
            this.poiBean = resultPoi;
            if (this.poiBean == null) {
                this.tvLocation.setText(R.string.dont_show_location);
            } else {
                this.tvLocation.setText(resultPoi.getAddressDesc());
            }
            dealCurrentPushBtnBackground();
            return;
        }
        if (requestCode != REQUEST_VISIBLE_RANGE) {
            if (requestCode == Constants.StartActivityOnResultCode.MOMENT_TOPIC_REQUEST_CODE
                    && this.topicModuleSwitch) {
                dealTopicResult(data);
            }
            return;
        }
        this.friendsVisibleBean = (FriendsVisibleBean) data.getParcelableExtra("visible");
        if (this.friendsVisibleBean == null) {
            this.friendsVisibleBean = new FriendsVisibleBean();
        }
        LogUtil.d(TAG, "friend bean:" + this.friendsVisibleBean);
        MomentVisibleShowUtil.dealVisibleRange(this.context, this.friendsVisibleBean,
                this.ivVisibleRange, this.tvVisibleRange, this.tvVisibleRangeDetails);
    }

    /**
     * 保存草稿后返回动态主页的点击监听。
     */
    private final class SaveDraftOnExitClickListener implements View.OnClickListener {

        private final String content;

        SaveDraftOnExitClickListener(String content) {
            this.content = content;
        }

        @Override
        public void onClick(View view) {
            DialogUtil.dismissDialog(PushTextActivity.this.dialog);
            final PublishTextBean draftBean = new PublishTextBean();
            draftBean.setText(TextUtils.isEmpty(this.content) ? "" : this.content);
            draftBean.setPoiBean(PushTextActivity.this.poiBean);
            draftBean.setDbTemplate(PushTextActivity.this.templateBean);
            draftBean.setFriendsVisibleBean(PushTextActivity.this.friendsVisibleBean);
            draftBean.setType(PushTextActivity.this.type);
            if (PushTextActivity.this.topicModuleSwitch) {
                draftBean.setTopicList(PushTextActivity.this.topicList);
            }
            HandlerUtil.runOnBackground(new Runnable() {
                @Override
                public void run() {
                    SaveDynamic.savePublishTextBean(PushTextActivity.this.context, draftBean);
                }
            });
            PushTextActivity.this.startActivity(new Intent(PushTextActivity.this.context, MomentActivity.class));
            PushTextActivity.this.finish();
        }
    }

    /**
     * 输入长度监听：文本变化时刷新发布按钮背景。
     */
    private class PushWatcher extends MaxLengthWatcher {

        PushWatcher(Context context, int maxLength, String maxLengthHint) {
            super(context, maxLength, maxLengthHint);
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
            PushTextActivity.this.dealCurrentPushBtnBackground();
        }
    }

    private void dealCurrentPushBtnBackground() {
        if (!TextUtils.isEmpty(this.etHint.getText().toString())) {
            this.publish.setBackgroundResource(R.drawable.bg_btn_release);
            return;
        }
        if (this.templateBean != null) {
            this.publish.setBackgroundResource(R.drawable.bg_btn_release);
        } else if (this.poiBean != null && (this.type == TYPE_LOCATION || this.type == TYPE_TEXT)) {
            this.publish.setBackgroundResource(R.drawable.bg_btn_release);
        } else {
            this.publish.setBackgroundResource(R.drawable.bg_btn_gray);
        }
    }

    public boolean isConnected() {
        return NetworkUtils.isConnected(this.context);
    }

    /**
     * 初始化已选「小括号」横向列表。
     */
    private void initShowTopicAdapter() {
        this.topicList.clear();
        this.rvTopicList = (RecyclerView) findViewById(R.id.rv_topic_list);
        this.showTopicAdapter = new ShowTopicAdapter();
        this.rvTopicList.setLayoutManager(new LinearLayoutManager(this));
        this.rvTopicList.setAdapter(this.showTopicAdapter);
        this.showTopicAdapter.setOnDeleteItemListener(new ShowTopicAdapter.OnDeleteItemListener() {
            @Override
            public void onDeleteItem(int position) {
                if (!CollectionUtil.isEmpty(PushTextActivity.this.topicList)) {
                    PushTextActivity.this.topicList.remove(position);
                }
                PushTextActivity.this.showTopicAdapter.setData(PushTextActivity.this.topicList);
            }
        });
    }

    /**
     * 根据开关显示或隐藏「小括号」入口。
     */
    private void showOrHideTopicModule(boolean show) {
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) this.llLocation.getLayoutParams();
        if (show) {
            this.rvTopicList.setVisibility(View.VISIBLE);
            this.llTopic.setVisibility(View.VISIBLE);
            layoutParams.topMargin = getResources().getDimensionPixelOffset(R.dimen.dp_6);
        } else {
            this.rvTopicList.setVisibility(View.GONE);
            this.llTopic.setVisibility(View.GONE);
            layoutParams.topMargin = getResources().getDimensionPixelOffset(R.dimen.dp_17);
        }
        this.llLocation.setLayoutParams(layoutParams);
    }

    /**
     * 处理外部传入的「小括号」json。
     */
    private void dealTopicJson() {
        if (getIntent() == null) {
            return;
        }
        String topicJson = getIntent().getStringExtra(Constants.INTENT_EXTRA_TOPIC);
        this.topicSourceType = getIntent().getStringExtra(Constants.INTENT_EXTRA_TOPIC_SOURCE);
        if (TextUtils.isEmpty(topicJson)) {
            return;
        }
        if (SharedTool.getMaxTopicCount(this) <= this.topicList.size()) {
            ToastUtil.showShort(this, getString(R.string.string_add_topic_max));
            return;
        }
        ArrayList<String> topicNames = (ArrayList<String>) JSONUtil.fromJSON(topicJson, ArrayList.class, String.class);
        if (CollectionUtil.isEmpty(topicNames)) {
            return;
        }
        for (String topicName : topicNames) {
            if (TextUtils.isEmpty(topicName)) {
                continue;
            }
            if (!topicName.contains(Constants.TOPIC_PREFIX)) {
                topicName = Constants.TOPIC_PREFIX + topicName + Constants.TOPIC_SUFFIX;
            }
            boolean exists = false;
            for (TopicListBean topicListBean : this.topicList) {
                if (topicListBean != null && TextUtils.equals(topicListBean.getTopicName(), topicName)) {
                    exists = true;
                    break;
                }
            }
            if (!exists) {
                TopicListBean topicListBean = new TopicListBean();
                topicListBean.setTopicName(topicName);
                this.topicList.add(topicListBean);
            }
        }
        this.showTopicAdapter.setData(this.topicList);
    }

    /**
     * 处理「小括号」选择页返回结果。
     */
    private void dealTopicResult(Intent data) {
        ArrayList<TopicListBean> chooseList = data.getParcelableArrayListExtra(TopicActivity.INTENT_CHOOSE_DATA);
        if (!CollectionUtil.isEmpty(chooseList)) {
            this.topicList.clear();
            this.topicList.addAll(chooseList);
        }
        TopicListBean topicListBean = data.getParcelableExtra(TopicActivity.INTENT_TOPIC_DATA);
        if (topicListBean != null) {
            if (data.getIntExtra(TopicActivity.INTENT_MAX_TOPIC_COUNT, 0) > this.topicList.size()) {
                this.topicList.add(topicListBean);
            } else {
                MomentBehavior.clickTopicMaxToast();
                ToastUtil.showShort(this, getString(R.string.string_add_topic_max));
            }
        }
        this.showTopicAdapter.setData(this.topicList);
    }
}
