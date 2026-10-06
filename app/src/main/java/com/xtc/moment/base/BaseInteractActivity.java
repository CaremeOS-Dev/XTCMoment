package com.xtc.moment.base;

import android.text.InputFilter;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.xtc.log.LogUtil;
import com.xtc.moment.R;
import com.xtc.moment.constants.ModuleSwitchConstant;
import com.xtc.moment.db.bean.DbMoment;
import com.xtc.moment.db.bean.DbMomentComment;
import com.xtc.moment.module.report.adapter.AbsInteractionAdapter;
import com.xtc.moment.module.widget.LoadingPupWindowHolder;
import com.xtc.moment.module.widget.MaxLengthWatcher;
import com.xtc.moment.serve.AccountInfoServerImpl;
import com.xtc.moment.third.bean.PushCommentBean;
import com.xtc.moment.third.behavior.MomentBehavior;
import com.xtc.moment.util.HandlerUtil;
import com.xtc.moment.util.MomentTypeUtil;
import com.xtc.moment.util.ToastUtil;
import com.xtc.moment.util.Utils;
import com.xtc.moment.util.switchs.ModuleSwitchUtil;
import com.xtc.utils.system.NetworkUtils;

import org.greenrobot.eventbus.EventBus;

/**
 * 互动 Activity 基类：评论输入框、加载动画与列表事件绑定。
 */
public abstract class BaseInteractActivity<V extends IBaseInteractView, P extends BaseInteractPresenter<V>> extends BaseCtaPermissionActivity<V, P> {

    protected String arguedId;
    protected String arguedName;
    protected String commentId;
    protected int commentType;
    protected EditText etHint;
    protected InputFilter[] filters_text_10;
    protected InputFilter[] filters_text_40;
    protected InputMethodManager inputManager;
    protected LoadingPupWindowHolder loadingPupWindowHolder;
    protected MaxLengthWatcher maxLengthWatcher;
    protected String momentId;
    protected int momentType;
    protected String momentWatchId;
    protected String parentWatchId;

    @Override
    public void beforeDealPermission() {
    }

    public abstract String getLogTag();

    protected void initEditText() {
        EditText editText = this.etHint;
        if (editText == null) {
            return;
        }
        editText.setInputType(111);
        this.filters_text_40 = new InputFilter[]{new InputFilter.LengthFilter(40)};
        this.filters_text_10 = new InputFilter[]{new InputFilter.LengthFilter(10)};
        this.maxLengthWatcher = new MaxLengthWatcher(this, 40, getString(R.string.content_is_over));
        this.etHint.setFilters(this.filters_text_40);
        this.etHint.addTextChangedListener(this.maxLengthWatcher);
        this.etHint.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent event) {
                if (keyCode != KeyEvent.KEYCODE_ENTER) {
                    if (keyCode != KeyEvent.KEYCODE_DEL) {
                        return false;
                    }
                    Utils.hideSoftInputFromWindow(BaseInteractActivity.this.getInputMethodManager(), view);
                    return true;
                }
                Utils.hideSoftInputFromWindow(BaseInteractActivity.this.getInputMethodManager(), view);
                if (BaseInteractActivity.this.etHint == null) {
                    return true;
                }
                String content = BaseInteractActivity.this.etHint.getText().toString();
                BaseInteractActivity.this.etHint.setText("");
                LogUtil.d(BaseInteractActivity.this.getLogTag(), "onKey: inputContent = " + content);
                if (!TextUtils.isEmpty(content) && !TextUtils.isEmpty(content.trim())) {
                    BaseInteractActivity.this.showLoading();
                    String watchId = AccountInfoServerImpl.getInstance(BaseInteractActivity.this).getWatchAccountInfo().getWatchId(BaseInteractActivity.this);
                    DbMomentComment comment = BaseInteractActivity.this.getDbMomentComment(watchId, content);
                    if (!MomentTypeUtil.isOfficialType(BaseInteractActivity.this.momentType)) {
                        BaseInteractActivity.this.presenter.commentMoment(comment);
                        MomentBehavior.commentMoment(BaseInteractActivity.this, new PushCommentBean(watchId, BaseInteractActivity.this.momentId, BaseInteractActivity.this.arguedId, 1));
                    } else {
                        LogUtil.d(BaseInteractActivity.this.getLogTag(), "isOfficialType momentType=" + BaseInteractActivity.this.momentType);
                        BaseInteractActivity.this.presenter.publishAdvertComment(comment);
                        MomentBehavior.commentOfficialMoment(BaseInteractActivity.this, new PushCommentBean(watchId, BaseInteractActivity.this.momentId, BaseInteractActivity.this.arguedId, 1, content));
                    }
                } else {
                    ToastUtil.showShort(BaseInteractActivity.this, BaseInteractActivity.this.getString(R.string.content_is_null));
                }
                return true;
            }
        });
    }

    private DbMomentComment getDbMomentComment(String watchId, String content) {
        DbMomentComment comment = new DbMomentComment();
        comment.setWatchId(watchId);
        comment.setComment(content);
        comment.setChecked(true);
        comment.setType(this.commentType);
        comment.setMomentId(this.momentId);
        comment.setMomentWatchId(this.momentWatchId);
        comment.setMediaType(1);
        comment.setCommentId(this.commentId);
        comment.setParentWatchId(this.parentWatchId);
        if (2 == this.commentType) {
            comment.setReplyId(this.arguedId);
            comment.setReplyName(this.arguedName);
            comment.setParentWatchId(this.parentWatchId);
        }
        return comment;
    }

    protected void showLoading() {
        LogUtil.d(getLogTag(), "showLoading() called");
        if (this.loadingPupWindowHolder == null) {
            this.loadingPupWindowHolder = new LoadingPupWindowHolder(this);
            this.loadingPupWindowHolder.setOnSuccessAction(new Runnable() {
                @Override
                public void run() {
                    BaseInteractActivity.this.dismissLoading();
                }
            });
        }
        this.loadingPupWindowHolder.showLoading(getWindow().getDecorView());
    }

    protected void showLoadingSuccess() {
        LogUtil.d(getLogTag(), "showLoadingSuccess() called");
        LoadingPupWindowHolder holder = this.loadingPupWindowHolder;
        if (holder == null) {
            return;
        }
        holder.showSuccess();
    }

    protected void dismissLoading() {
        LogUtil.d(getLogTag(), "dismissLoading() called");
        LoadingPupWindowHolder holder = this.loadingPupWindowHolder;
        if (holder == null) {
            return;
        }
        holder.dismissLoading();
    }

    protected void jumpToActivity(DbMoment moment, int type, String replyWatchId, String replyName, String commentId, String parentWatchId) {
        this.commentType = type;
        this.commentId = commentId;
        this.parentWatchId = parentWatchId;
        this.momentId = moment.getMomentId();
        this.momentWatchId = moment.getWatchId();
        this.momentType = moment.getType().intValue();
        LogUtil.d(getLogTag(), "jumpToActivity name :" + this.momentType);
        if (2 == type) {
            this.arguedId = replyWatchId;
            this.arguedName = replyName;
        }
        getInputMethodManager().showSoftInput(this.etHint, 0);
    }

    protected void checkMoment(DbMoment moment) {
        if (MomentTypeUtil.checkMomentIsFunVideo(moment)) {
            this.etHint.setFilters(this.filters_text_10);
            this.maxLengthWatcher.setMaxLen(10);
            this.etHint.addTextChangedListener(this.maxLengthWatcher);
        } else {
            this.etHint.setFilters(this.filters_text_40);
            this.maxLengthWatcher.setMaxLen(40);
            this.etHint.addTextChangedListener(this.maxLengthWatcher);
        }
    }

    protected InputMethodManager getInputMethodManager() {
        if (this.inputManager == null) {
            this.inputManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        }
        return this.inputManager;
    }

    public void initCommonRvListener(AbsInteractionAdapter adapter) {
        adapter.setOnMomentCommentListener(new AbsInteractionAdapter.OnMomentCommentListener() {
            @Override
            public void comment(DbMoment moment) {
                BaseInteractActivity.this.checkMoment(moment);
                BaseInteractActivity.this.commentMoment(moment);
                LogUtil.d(BaseInteractActivity.this.getLogTag(), "comment :" + moment.getType());
            }

            @Override
            public void reply(DbMoment moment, DbMomentComment comment) {
                LogUtil.d(BaseInteractActivity.this.getLogTag(), "setOnMomentCommentListener reply" + moment.getType());
                BaseInteractActivity.this.checkMoment(moment);
                BaseInteractActivity.this.replyMoment(moment, comment);
            }

            @Override
            public void loadMoreComment(DbMoment moment) {
                BaseInteractActivity.this.presenter.loadMoreComment(moment);
            }
        });
        adapter.setOnLikeMomentListener(new AbsInteractionAdapter.OnLikeMomentListener() {
            @Override
            public void likeMoment(String momentId, String momentWatchId) {
                BaseInteractActivity.this.presenter.likeMoment(momentId, momentWatchId, false);
            }

            @Override
            public void cancelLikeMoment(String momentId, String momentWatchId, DbMoment moment) {
                BaseInteractActivity.this.presenter.cancelLikeMoment(momentId, momentWatchId, moment, false);
            }

            @Override
            public void likeAdvertise(String momentId, String momentWatchId) {
                BaseInteractActivity.this.presenter.likeMoment(momentId, momentWatchId, true);
            }

            @Override
            public void cancelLikeAdvertise(String momentId, String momentWatchId, DbMoment moment) {
                BaseInteractActivity.this.presenter.cancelLikeMoment(momentId, momentWatchId, moment, true);
            }
        });
        adapter.setOnDeleteItemListener(new AbsInteractionAdapter.OnDeleteItemListener() {
            @Override
            public void onDeleteItem(DbMoment moment) {
                boolean canDelete = ModuleSwitchUtil.queryModuleSwitchByBoolean(BaseInteractActivity.this.getApplicationContext(), 113, true);
                boolean canChangeRange = ModuleSwitchUtil.queryModuleSwitchByBoolean(BaseInteractActivity.this.getApplicationContext(), ModuleSwitchConstant.MODULE_VISIBLE_RANGE, false)
                        && MomentTypeUtil.ableChangeVisibleRangeType(moment.getType().intValue())
                        && moment.getPermissionType() != -1;
                if (canDelete && !canChangeRange) {
                    BaseInteractActivity.this.presenter.deleteMoment(BaseInteractActivity.this, moment, 1);
                    return;
                }
                if (!canDelete && canChangeRange) {
                    BaseInteractActivity.this.presenter.deleteMoment(BaseInteractActivity.this, moment, 3);
                } else if (canDelete && canChangeRange) {
                    BaseInteractActivity.this.presenter.deleteMoment(BaseInteractActivity.this, moment, 13);
                } else {
                    LogUtil.d(BaseInteractActivity.this.getLogTag(), "好友圈删除说说全网开关为关");
                }
            }
        });
        adapter.setOnDeleteCommentListener(new AbsInteractionAdapter.OnDeleteCommentListener() {
            @Override
            public void onDeleteComment(DbMomentComment comment, DbMoment moment, boolean isSelf) {
                LogUtil.d(BaseInteractActivity.this.getLogTag(), "onDeleteComment 删除" + moment.getType());
                if (!ModuleSwitchUtil.queryModuleSwitchByBoolean(BaseInteractActivity.this, ModuleSwitchConstant.MODULE_SWITCH_MOMENT_COMMENT_DELETE, true)) {
                    LogUtil.d(BaseInteractActivity.this.getLogTag(), "好友圈删除评论全网开关为关");
                } else if (isSelf) {
                    BaseInteractActivity.this.presenter.deleteComment(BaseInteractActivity.this, comment, moment);
                } else {
                    BaseInteractActivity.this.presenter.deleteCommentSync(comment, moment);
                }
            }
        });
    }

    public void initShareViewRvListener(AbsInteractionAdapter adapter) {
        adapter.setOnLikeClickListener(new AbsInteractionAdapter.OnLikeClickListener() {
            @Override
            public void onClick(DbMoment moment) {
                BaseInteractActivity.this.presenter.likeMoment(moment.getMomentId(), moment.getWatchId(), false);
            }

            @Override
            public void cancelLike(DbMoment moment) {
                BaseInteractActivity.this.presenter.cancelLikeMoment(moment.getMomentId(), moment.getWatchId(), moment, false);
            }
        });
    }

    private void commentMoment(DbMoment moment) {
        LogUtil.d(getLogTag(), "comment name :" + this.momentType);
        jumpToActivity(moment, 1, null, null, null, null);
    }

    private void replyMoment(DbMoment moment, DbMomentComment comment) {
        LogUtil.d(getLogTag(), "reply name :" + this.momentType);
        jumpToActivity(moment, 2, comment.getWatchId(), comment.getWatchName(), comment.getCommentId(), comment.getParentWatchId());
    }

    public void dealLikeError(String message) {
        if (!TextUtils.isEmpty(message) && (message.contains("1003") || message.contains("1002"))) {
            ToastUtil.showShortCover(this, getString(R.string.frequent_request));
            return;
        }
        boolean networkAvailable = NetworkUtils.isNetworkAvailable(this);
        LogUtil.d("moment", "点赞失败回调 —— 网络是否可用: " + networkAvailable);
        if (!networkAvailable) {
            ToastUtil.showNoConnected(this);
        } else {
            ToastUtil.showShortCover(this, getString(R.string.like_error));
        }
    }

    @Override
    public void refusePermission() {
        finish();
    }

    @Override
    public void afterDealPermission() {
        getWindow().getDecorView().setBackground(null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        HandlerUtil.runOnBackground(new Runnable() {
            @Override
            public void run() {
                Utils.hideSoftInputFromWindow(BaseInteractActivity.this.getInputMethodManager(), BaseInteractActivity.this.etHint);
            }
        });
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        dismissLoading();
    }
}