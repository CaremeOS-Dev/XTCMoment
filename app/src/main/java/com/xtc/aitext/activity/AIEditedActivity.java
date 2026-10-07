package com.xtc.aitext.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xtc.moment.R;
import com.xtc.aitext.activity.view.IAIEditedView;
import com.xtc.aitext.adapter.AwardAdapter;
import com.xtc.aitext.bean.AICreatStatusBean;
import com.xtc.aitext.bean.AIDataBean;
import com.xtc.aitext.bean.AIStyleTextBean;
import com.xtc.aitext.bean.UserAccessBean;
import com.xtc.aitext.bean.WatchBoxContent;
import com.xtc.aitext.behavior.AIBehaviorUtil;
import com.xtc.aitext.constant.Constant;
import com.xtc.aitext.net.AINetErrorAction;
import com.xtc.aitext.presenter.AIEditedPresenter;
import com.xtc.aitext.service.AIOverTimeServe;
import com.xtc.aitext.util.AIModuleUtil;
import com.xtc.aitext.util.AITextClickTimeUtil;
import com.xtc.aitext.util.AITextRxUtils;
import com.xtc.aitext.util.AITextShareUtil;
import com.xtc.aitext.util.SoftKeyboardUtils;
import com.xtc.aitext.weight.AIPaintDialog;
import com.xtc.aitext.weight.AISuccessDialog;
import com.xtc.aitext.weight.EmojiInputFilter;
import com.xtc.aitext.weight.FirstTipDialog;
import com.xtc.aitext.weight.PermissionDialog;
import com.xtc.aitext.weight.SwitchingDialog;
import com.xtc.aitext.weight.callback.ActivityControllListener;
import com.xtc.aitext.weight.callback.ActivityFinishCallback;
import com.xtc.aitext.weight.callback.DialogDismissCallback;
import com.xtc.architecture.mvp.BaseActivity;
import com.xtc.architecture.mvp.PermissionListener;
import com.xtc.database.ormlite.CollectionUtil;
import com.xtc.log.LogUtil;
import com.xtc.ui.widget.dialog.DoubleFlatBtnWithTitleDialog;
import com.xtc.ui.widget.toast.view.ToastUtil;
import com.xtc.ui.widget.util.DialogUtil;
import com.xtc.utils.encode.JSONUtil;
import com.xtc.utils.ui.DimenUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * AI 文案编辑主页，负责文案输入、风格选择、权益领取与创作状态处理。
 */
public class AIEditedActivity extends BaseActivity<IAIEditedView, AIEditedPresenter>
        implements IAIEditedView, AIOverTimeServe.AIOverTimeListener, ActivityFinishCallback, DialogDismissCallback {

    private static final String TAG = "ai_text_AIEditedActivity";

    private static final int REQUEST_SELECT_STYLE = 1;
    private static final int DEFAULT_TEXT_LIMIT = 50;
    private static final int ANIM_DURATION = 3000;
    private static final long PAINT_EXTRA_SECONDS = 5L;
    private static final float AWARD_BOTTOM_MARGIN_DP = 15.0f;

    private static final String[] STORAGE_PERMISSIONS = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE"
    };

    private EditText editInput;
    private TextView tvCount;
    private TextView tvStyle;
    private TextView tvNum;
    private Button btnStart;
    private RelativeLayout editLlRecord;
    private RecyclerView rvEdit;
    private LinearLayout llStyle;
    private TextView tvAward;

    private FirstTipDialog firstTipDialog;
    private AIPaintDialog paintDialog;
    private AISuccessDialog successDialog;
    private SwitchingDialog switchingDialog;
    private DoubleFlatBtnWithTitleDialog watchBoxDialog;

    private AIStyleTextBean selectedStyle;
    private List<AIStyleTextBean> styleList;
    private AIDataBean homeData;
    private List<UserAccessBean> accessList;
    private int remainTimes;
    private int textLimit = DEFAULT_TEXT_LIMIT;

    private EmojiInputFilter emojiInputFilter;
    private RefreshDataReceiver refreshDataReceiver;
    private AwardAdapter awardAdapter;
    private AnimatorSet awardAnimatorSet;

    private AIOverTimeServe overTimeServe;

    private long lastStyleClickTime = 0;
    private long lastStartClickTime = 0;
    private long lastRecordClickTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edited);
        checkPermission();
    }

    private void checkPermission() {
        if (AITextShareUtil.hasAgreePermission(this)) {
            requestStoragePermission();
        } else {
            showPermissionDialog();
        }
    }

    private void showPermissionDialog() {
        final PermissionDialog permissionDialog = new PermissionDialog(this);
        permissionDialog.setPermissionCallback(new PermissionDialog.PermissionCallback() {
            @Override
            public void clickAgree() {
                LogUtil.d(TAG, "onClick: agree permission");
                AITextShareUtil.setAgreePermission(AIEditedActivity.this);
                requestStoragePermission();
                DialogUtil.dismissDialog(permissionDialog);
            }

            @Override
            public void clickNoAgree() {
                LogUtil.d(TAG, "onClick: refuse permission");
                finish();
            }
        });
        permissionDialog.show();
    }

    @Override
    public AIEditedPresenter createPresenter() {
        return new AIEditedPresenter(this);
    }

    private void requestStoragePermission() {
        requestRunTimePermission(STORAGE_PERMISSIONS, new PermissionListener() {
            @Override
            public void onGranted() {
                LogUtil.d(TAG, "onRequestPermissionsResult: ok");
                initData();
            }

            @Override
            public void onPartPermissionDenied(List<String> granted, List<String> denied) {
                LogUtil.i(TAG, "access denied for user");
                finish();
            }
        });
    }

    @Override
    public void initView() {
        AIBehaviorUtil.reportEnterHome();
        this.editLlRecord = findViewById(R.id.edit_ll_record);
        this.editInput = findViewById(R.id.edit_input);
        this.llStyle = findViewById(R.id.ll_style);
        this.tvCount = findViewById(R.id.tv_count);
        this.tvStyle = findViewById(R.id.tv_style);
        this.tvNum = findViewById(R.id.tv_num);
        this.btnStart = findViewById(R.id.btn_start);
        this.rvEdit = findViewById(R.id.rv_edit);
        this.tvAward = findViewById(R.id.tv_award);
        this.tvCount.setText(String.format(getResources().getString(R.string.string_edit_text_number),
                editInput.getText().length(), textLimit));
        if (this.selectedStyle != null) {
            this.tvStyle.setText(getString(R.string.string_ai_style, selectedStyle.getAiStyleName()));
        }
        this.rvEdit.setLayoutManager(new LinearLayoutManager(this));
        this.rvEdit.setHasFixedSize(true);
        this.rvEdit.setNestedScrollingEnabled(false);
        this.awardAdapter = new AwardAdapter(this, new ArrayList<UserAccessBean>());
        this.rvEdit.setAdapter(this.awardAdapter);
        registerRefreshReceiver();
        initListeners();
    }

    private void registerRefreshReceiver() {
        this.refreshDataReceiver = new RefreshDataReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constant.ACTION_REFRESH_DATA);
        registerReceiver(this.refreshDataReceiver, intentFilter);
    }

    private void initListeners() {
        this.llStyle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (AITextClickTimeUtil.isFastClick(lastStyleClickTime)) {
                    return;
                }
                lastStyleClickTime = SystemClock.elapsedRealtime();
                jumpSelectStyle();
            }
        });
        this.btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (AITextClickTimeUtil.isFastClick(lastStartClickTime)) {
                    return;
                }
                lastStartClickTime = SystemClock.elapsedRealtime();
                if (selectedStyle == null) {
                    return;
                }
                String content = editInput.getText().toString();
                if (content.length() == 0) {
                    SoftKeyboardUtils.showKeyboard(editInput);
                    return;
                }
                if (!AIModuleUtil.isContentConform(content)) {
                    clearInput();
                    ToastUtil.showShortCover(AIEditedActivity.this, getString(R.string.string_change_text));
                } else {
                    overTimeServe.cancel();
                    AIBehaviorUtil.reportSelectStyle(selectedStyle.getAiStyleName());
                    getPresenter().createText(content, selectedStyle.getId(), remainTimes);
                }
            }
        });
        this.tvCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SoftKeyboardUtils.showKeyboard(editInput);
            }
        });
        this.editLlRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (AITextClickTimeUtil.isFastClick(lastRecordClickTime)) {
                    return;
                }
                lastRecordClickTime = SystemClock.elapsedRealtime();
                jumpRecord();
            }
        });
        this.awardAdapter.setItemOnClickListener(new AwardAdapter.ItemOnClickListener() {
            @Override
            public void onItemClick(UserAccessBean userAccessBean) {
                if (userAccessBean == null) {
                    return;
                }
                LogUtil.d(TAG, "awardAdapter onClick: userAccessBean = " + userAccessBean);
                if (getPresenter().needShowAwardDialog(userAccessBean)) {
                    userAccessBean.getButtonDescription().setObtainStatus(0);
                    awardAdapter.updateAccess(userAccessBean);
                }
            }
        });
        this.rvEdit.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                if (parent.getChildAdapterPosition(view) == parent.getAdapter().getItemCount() - 1) {
                    outRect.bottom = DimenUtil.dp2px(AIEditedActivity.this, AWARD_BOTTOM_MARGIN_DP);
                }
            }
        });
        ActivityControllListener.getInstance().addListener(this);
        this.overTimeServe = new AIOverTimeServe(this);
    }
    @Override
    public void initData() {
        AITextRxUtils.fromCallableOnIo(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                String lastStyleJson = AITextShareUtil.getLastStyleJson(AIEditedActivity.this);
                selectedStyle = JSONUtil.fromJSON(lastStyleJson, AIStyleTextBean.class);
                return AITextShareUtil.isAiFirst(AIEditedActivity.this);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean isFirst) {
                        initView();
                        bindInputLimit(textLimit);
                        if (!isFirst) {
                            getPresenter().queryData();
                            showFirstTipDialog();
                        } else {
                            showSwitchingDialog();
                            getPresenter().queryData();
                        }
                    }
                }, new AINetErrorAction(getClass(), new AINetErrorAction.ErrorCallback() {
                    @Override
                    public void onError(Throwable throwable) {
                    }
                }, "initData"));
    }

    private void bindInputLimit(final int limit) {
        this.editInput.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER) {
                    updateTextCount();
                }
                return false;
            }
        });
        this.emojiInputFilter = new EmojiInputFilter(limit, this);
        this.editInput.setFilters(new InputFilter[]{this.emojiInputFilter});
        this.editInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                int length = editable.length();
                LogUtil.d(TAG, "afterTextChanged: textLength = " + length);
                if (length >= limit) {
                    ToastUtil.showShortCover(AIEditedActivity.this,
                            getString(R.string.string_toast_limit_word, String.valueOf(textLimit)));
                }
            }
        });
    }

    /** 刷新字数统计与颜色。 */
    private void updateTextCount() {
        this.tvCount.setText(String.format(getResources().getString(R.string.string_edit_text_number),
                editInput.getText().length(), textLimit));
        int color;
        if (textLimit == 0 || editInput.getText().length() != textLimit) {
            color = getResources().getColor(R.color.color_808080);
        } else {
            color = getResources().getColor(R.color.edit_D95A5A);
        }
        this.tvCount.setTextColor(color);
    }

    private void showFirstTipDialog() {
        AITextShareUtil.setAiUsed(this);
        this.firstTipDialog = new FirstTipDialog(this);
        this.firstTipDialog.setClickCallback(new FirstTipDialog.ClickCallback() {
            @Override
            public void clickButton() {
            }
        });
        DialogUtil.showDialog(this.firstTipDialog);
    }

    private void showSwitchingDialog() {
        this.switchingDialog = new SwitchingDialog(this);
        this.switchingDialog.setLlChangingBackground(ContextCompat.getDrawable(this, R.drawable.bg_gauss));
        DialogUtil.showDialog(this.switchingDialog);
    }

    @Override
    public void showHomeData(AIDataBean dataBean) {
        DialogUtil.dismissDialog(this.switchingDialog);
        this.homeData = dataBean;
        this.styleList = dataBean.getAiStyleTextVos();
        this.remainTimes = dataBean.getAiTextCount();
        this.textLimit = dataBean.getAiTextLength();
        this.tvCount.setText(String.format(getResources().getString(R.string.string_edit_text_number),
                editInput.getText().length(), textLimit));
        this.accessList = dataBean.getUserAccessVoList();
        this.awardAdapter.setAccessList(this.accessList);
        bindInputLimit(this.textLimit);
        this.tvNum.setText(String.valueOf(this.remainTimes));
        selectDefaultStyle();
    }

    private void selectDefaultStyle() {
        boolean styleExists = false;
        if (this.selectedStyle != null) {
            Iterator<AIStyleTextBean> iterator = this.styleList.iterator();
            while (iterator.hasNext()) {
                if (Objects.equals(iterator.next().getAiStyleName(), this.selectedStyle.getAiStyleName())) {
                    styleExists = true;
                    break;
                }
            }
        }
        if (styleExists) {
            return;
        }
        this.selectedStyle = this.styleList.get(AIModuleUtil.randomInt(this.styleList.size()));
        AITextShareUtil.saveLastStyle(this, this.selectedStyle);
        this.editInput.setHint(this.selectedStyle.getDefaultTextTips());
        this.tvStyle.setText(getString(R.string.string_ai_style, this.selectedStyle.getAiStyleName()));
    }

    @Override
    public void showRemainTimes(int remainTimes) {
        this.remainTimes = remainTimes;
        this.tvNum.setText(String.valueOf(this.remainTimes));
    }

    @Override
    public void showToast(final String message) {
        if (this.awardAnimatorSet == null) {
            AnimatorSet fadeIn = new AnimatorSet();
            fadeIn.playTogether(ObjectAnimator.ofFloat(this.tvAward, "alpha", 0.0f, 1.0f).setDuration(ANIM_DURATION));
            fadeIn.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    tvAward.setVisibility(View.VISIBLE);
                }
            });
            AnimatorSet fadeOut = new AnimatorSet();
            fadeOut.playTogether(ObjectAnimator.ofFloat(this.tvAward, "alpha", 1.0f, 0.0f).setDuration(ANIM_DURATION));
            fadeOut.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    tvAward.setVisibility(View.GONE);
                }
            });
            this.awardAnimatorSet = new AnimatorSet();
            this.awardAnimatorSet.playSequentially(fadeIn, fadeOut);
        }
        if (this.awardAnimatorSet.isRunning()) {
            AITextRxUtils.runOnMainDelay(new Runnable() {
                @Override
                public void run() {
                    showToast(message);
                }
            }, ANIM_DURATION);
        } else {
            this.tvAward.setText(message);
            this.awardAnimatorSet.start();
        }
    }

    @Override
    public void showEmpty() {
        DialogUtil.dismissDialog(this.switchingDialog);
        ToastUtil.showShortCover(this, getString(R.string.string_network_anomaly));
    }

    @Override
    public void showUserAccess(UserAccessBean userAccessBean) {
        this.awardAdapter.updateAccess(userAccessBean);
    }

    @Override
    public void showWatchBoxContent(final WatchBoxContent watchBoxContent, final UserAccessBean userAccessBean) {
        if (userAccessBean == null || userAccessBean.getButtonDescription() == null
                || userAccessBean.getButtonDescription().getWatchBoxContent() == null) {
            return;
        }
        final WatchBoxContent content = userAccessBean.getButtonDescription().getWatchBoxContent();
        if (this.watchBoxDialog == null) {
            this.watchBoxDialog = new DoubleFlatBtnWithTitleDialog(this, true);
            this.watchBoxDialog.setWholeBackgroud(R.color.color_ff000000);
            this.watchBoxDialog.getBottomBtn().getLeftButton().setText(R.string.cancel);
            this.watchBoxDialog.getBottomBtn().getLeftButton().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    watchBoxDialog.dismiss();
                }
            });
            this.watchBoxDialog.getBottomBtn().getRightButton().setText(R.string.string_award_can_award);
            this.watchBoxDialog.getBottomBtn().setRightBgColorIdArray(new int[]{R.color.color_5988FF, R.color.color_2873FF});
            this.watchBoxDialog.getTvTitle().setTextColor(getResources().getColor(R.color.color_ff00EDF3));
            this.watchBoxDialog.getTvTitle().setTextSize(18.0f);
            this.watchBoxDialog.getTvContent().setGravity(GravityCompat.START);
            this.watchBoxDialog.getTvContent().setTextSize(15.0f);
            this.watchBoxDialog.getTvExtra().setGravity(GravityCompat.START);
            this.watchBoxDialog.getTvExtra().setTextSize(13.0f);
            this.watchBoxDialog.getTvExtra().setTextColor(getResources().getColor(R.color.color_808080));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                this.watchBoxDialog.getTvContent().setBreakStrategy(0);
                this.watchBoxDialog.getTvExtra().setBreakStrategy(0);
            }
        }
        this.watchBoxDialog.getBottomBtn().getRightButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Objects.equals(content.getClickStatus(), Constant.ClickStatus.CLICK_REQUEST_NETWORK)) {
                    getPresenter().obtainForTimes(userAccessBean, false);
                }
                DialogUtil.dismissDialog(watchBoxDialog);
            }
        });
        this.watchBoxDialog.getTvTitle().setText(content.getTitle());
        this.watchBoxDialog.getTvContent().setText(content.getContent());
        this.watchBoxDialog.getTvExtra().setText(content.getRemarks());
        DialogUtil.showDialog(this.watchBoxDialog);
    }

    @Override
    public void showUserAccessWatchBox(final UserAccessBean userAccessBean) {
        if (userAccessBean == null || userAccessBean.getButtonDescription() == null) {
            return;
        }
        final WatchBoxContent watchBoxContent = userAccessBean.getButtonDescription().getWatchBoxContent();
        if (watchBoxContent == null) {
            return;
        }
        if (this.watchBoxDialog == null) {
            this.watchBoxDialog = new DoubleFlatBtnWithTitleDialog(this, true);
            this.watchBoxDialog.setWholeBackgroud(R.color.color_ff000000);
            this.watchBoxDialog.getBottomBtn().getLeftButton().setText(R.string.cancel);
            this.watchBoxDialog.getBottomBtn().getLeftButton().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    watchBoxDialog.dismiss();
                }
            });
            this.watchBoxDialog.getBottomBtn().getRightButton().setText(R.string.string_award_can_award);
            this.watchBoxDialog.getTvTitle().setTextColor(getResources().getColor(R.color.edit_00DE71));
            this.watchBoxDialog.getTvTitle().setTextSize(18.0f);
            this.watchBoxDialog.getTvContent().setGravity(GravityCompat.START);
            this.watchBoxDialog.getTvContent().setTextSize(15.0f);
            this.watchBoxDialog.getTvExtra().setGravity(GravityCompat.START);
            this.watchBoxDialog.getTvExtra().setTextSize(13.0f);
            this.watchBoxDialog.getTvExtra().setTextColor(getResources().getColor(R.color.color_808080));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                this.watchBoxDialog.getTvContent().setBreakStrategy(0);
                this.watchBoxDialog.getTvExtra().setBreakStrategy(0);
            }
        }
        if (!TextUtils.isEmpty(watchBoxContent.getButtonText())) {
            this.watchBoxDialog.getBottomBtn().getRightButton().setText(watchBoxContent.getButtonText());
        }
        this.watchBoxDialog.getBottomBtn().getRightButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Objects.equals(watchBoxContent.getClickStatus(), Constant.ClickStatus.CLICK_REQUEST_NETWORK)) {
                    getPresenter().obtainForTimes(userAccessBean, false);
                }
                DialogUtil.dismissDialog(watchBoxDialog);
            }
        });
        this.watchBoxDialog.getTvTitle().setText(watchBoxContent.getTitle());
        this.watchBoxDialog.getTvContent().setText(watchBoxContent.getContent());
        this.watchBoxDialog.getTvExtra().setText(watchBoxContent.getRemarks());
        DialogUtil.showDialog(this.watchBoxDialog);
    }

    @Override
    public int getTimesSuffix() {
        return this.remainTimes;
    }

    private void jumpSelectStyle() {
        if (CollectionUtil.isEmpty(this.styleList)) {
            return;
        }
        AITextRxUtils.fromCallablePair(new Callable<Pair<String, String>>() {
            @Override
            public Pair<String, String> call() throws Exception {
                return new Pair<>(JSONUtil.toJSON(selectedStyle), JSONUtil.toJSON(styleList));
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Pair<String, String>>() {
            @Override
            public void call(Pair<String, String> pair) {
                Intent intent = new Intent(AIEditedActivity.this, SelectStyleActivity.class);
                intent.putExtra(Constant.IntentExtras.EXTRA_SELECT_STYLE, pair.first);
                intent.putExtra(Constant.IntentExtras.EXTRA_SELECT_STYLE_LIST, pair.second);
                startActivityForResult(intent, REQUEST_SELECT_STYLE);
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                LogUtil.e(TAG, "jumpSelectStyle: ", throwable);
            }
        });
    }

    private void jumpRecord() {
        AIBehaviorUtil.reportViewRecord();
        startActivity(new Intent(this, AIRecordActivity.class));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null && requestCode == REQUEST_SELECT_STYLE) {
            this.selectedStyle = JSONUtil.fromJSON(data.getStringExtra(Constant.IntentExtras.EXTRA_SELECT_STYLE),
                    AIStyleTextBean.class);
            this.editInput.setHint(this.selectedStyle.getDefaultTextTips());
            this.tvStyle.setText(getString(R.string.string_ai_style, this.selectedStyle.getAiStyleName()));
            AITextShareUtil.saveLastStyle(this, this.selectedStyle);
        }
    }

    private void dealAIStatus(AICreatStatusBean statusBean) {
        if (statusBean == null) {
            return;
        }
        int status = statusBean.getStatus();
        LogUtil.d(TAG, "dealAIStatus: " + status);
        if (status == 0) {
            DialogUtil.dismissDialog(this.paintDialog);
            ToastUtil.showShortCover(this, getString(R.string.string_change_text));
            return;
        }
        if (status == 1) {
            DialogUtil.dismissDialog(this.paintDialog);
            this.paintDialog = new AIPaintDialog(this);
            this.paintDialog.setDialogDismissCallback(this);
            long waitingTime = this.paintDialog.setWaitingTime(statusBean.getWaitingTime()) + PAINT_EXTRA_SECONDS;
            this.paintDialog.startFlipping();
            this.paintDialog.setProgress();
            DialogUtil.showDialog(this.paintDialog);
            this.overTimeServe.setOverTimeSeconds(waitingTime);
            this.overTimeServe.start();
            return;
        }
        if (status == 2) {
            this.overTimeServe.cancel();
            if (DialogUtil.isDialogShowing(this.paintDialog)) {
                this.paintDialog.setMaxProgress();
                this.successDialog = new AISuccessDialog(this);
                this.successDialog.setTvContent(statusBean.getAIBackContent());
                DialogUtil.showDialog(this.successDialog);
                DialogUtil.dismissDialog(this.paintDialog);
            }
            return;
        }
        if (status == 3) {
            DialogUtil.dismissDialog(this.paintDialog);
            ToastUtil.showShortCover(this, getString(R.string.string_change_text));
            showRemainTimes(statusBean.getRemainTimes());
            this.overTimeServe.cancel();
            return;
        }
        if (status == 4) {
            clearInput();
        } else if (status == 5) {
            ToastUtil.showShortCover(this, getString(R.string.string_network_anomaly));
        }
    }

    private void dismissAllDialogs() {
        DialogUtil.dismissDialog(this.firstTipDialog);
        DialogUtil.dismissDialog(this.switchingDialog);
        DialogUtil.dismissDialog(this.paintDialog);
        DialogUtil.dismissDialog(this.successDialog);
        DialogUtil.dismissDialog(this.watchBoxDialog);
    }

    private void clearInput() {
        this.editInput.getText().clear();
        updateTextCount();
    }

    @Override
    public void onDestroy() {
        ActivityControllListener.getInstance().removeListener(this);
        dismissAllDialogs();
        RefreshDataReceiver receiver = this.refreshDataReceiver;
        if (receiver != null) {
            unregisterReceiver(receiver);
        }
        if (this.awardAnimatorSet != null) {
            this.awardAnimatorSet = null;
        }
        AIOverTimeServe overTimeServe = this.overTimeServe;
        if (overTimeServe != null) {
            overTimeServe.cancel();
        }
        super.onDestroy();
    }

    @Override
    public void finishActivity() {
        LogUtil.d(TAG, "finishActivity: ");
        finish();
    }

    @Override
    public void onOverTime() {
        if (DialogUtil.isDialogShowing(this.paintDialog)) {
            DialogUtil.dismissDialog(this.paintDialog);
            ToastUtil.showShortCover(this, getString(R.string.string_change_text));
            AIOverTimeServe overTimeServe = this.overTimeServe;
            if (overTimeServe != null) {
                overTimeServe.cancel();
            }
        }
    }

    @Override
    public void dismiss() {
        this.overTimeServe.cancel();
    }

    /**
     * 创作状态刷新广播接收者。
     */
    class RefreshDataReceiver extends BroadcastReceiver {
        RefreshDataReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && Objects.equals(intent.getAction(), Constant.ACTION_REFRESH_DATA)) {
                dealAIStatus(JSONUtil.fromJSON(intent.getStringExtra(Constant.IntentExtras.EXTRA_REFRESH_DATA),
                        AICreatStatusBean.class));
            }
        }
    }
}