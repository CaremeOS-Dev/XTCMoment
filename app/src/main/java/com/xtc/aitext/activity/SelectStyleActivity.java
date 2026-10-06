package com.xtc.aitext.activity;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.xtc.aitext.R;
import com.xtc.aitext.activity.view.ISelectStyleView;
import com.xtc.aitext.adapter.SelectStyleAdapter;
import com.xtc.aitext.bean.AIStyleTextBean;
import com.xtc.aitext.constant.Constant;
import com.xtc.aitext.net.AINetErrorAction;
import com.xtc.aitext.presenter.SelectStylePresenter;
import com.xtc.aitext.util.AITextRxUtils;
import com.xtc.architecture.mvp.BaseActivity;
import com.xtc.database.ormlite.CollectionUtil;
import com.xtc.log.LogUtil;
import com.xtc.ui.widget.scalablecontainer.AppRecyclerView;
import com.xtc.utils.encode.JSONUtil;
import com.xtc.utils.ui.DimenUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * AI 文案风格选择页。
 */
public class SelectStyleActivity extends BaseActivity<ISelectStyleView, SelectStylePresenter> implements ISelectStyleView {

    private static final String TAG = "ai_text_SelectStyleActivity";
    private static final int GRID_SPAN_COUNT = 2;
    private static final float BOTTOM_MARGIN_DP = 8.0f;

    private AppRecyclerView selectStyleRv;
    private SelectStyleAdapter adapter;
    private AIStyleTextBean selectedStyle;
    private List<AIStyleTextBean> styleList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_style);
        initView();
        initData();
    }

    @Override
    public SelectStylePresenter createPresenter() {
        return new SelectStylePresenter();
    }

    @Override
    public void initView() {
        this.selectStyleRv = findViewById(R.id.select_style_rv);
        this.adapter = new SelectStyleAdapter(this, new ArrayList<AIStyleTextBean>());
        this.selectStyleRv.setLayoutManager(new GridLayoutManager(this, GRID_SPAN_COUNT) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        this.selectStyleRv.setAdapter(this.adapter);
        this.adapter.setClickStyleBack(new SelectStyleAdapter.ClickStyleBack() {
            @Override
            public void onStyleChanged() {
                Intent intent = new Intent();
                intent.putExtra(Constant.IntentExtras.EXTRA_SELECT_STYLE, JSONUtil.toJSON(adapter.getSelectedStyle()));
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        this.selectStyleRv.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                if (parent.getChildAdapterPosition(view) == parent.getAdapter().getItemCount() - 1) {
                    outRect.bottom = DimenUtil.dp2px(SelectStyleActivity.this, BOTTOM_MARGIN_DP);
                }
            }
        });
    }

    @Override
    public void initData() {
        AITextRxUtils.fromCallableOnIo(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                selectedStyle = JSONUtil.fromJSON(getIntent().getStringExtra(Constant.IntentExtras.EXTRA_SELECT_STYLE),
                        AIStyleTextBean.class);
                styleList = JSONUtil.fromJSON(getIntent().getStringExtra(Constant.IntentExtras.EXTRA_SELECT_STYLE_LIST),
                        List.class, AIStyleTextBean.class);
                if (CollectionUtil.isEmpty(styleList)) {
                    finish();
                }
                return true;
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Boolean>() {
            @Override
            public void call(Boolean result) {
                adapter.setStyleList(styleList);
                adapter.selectStyle(selectedStyle);
            }
        }, new AINetErrorAction(getClass(), new AINetErrorAction.ErrorCallback() {
            @Override
            public void onError(Throwable throwable) {
                LogUtil.e(TAG, "initData: ", throwable);
                finish();
            }
        }, "initData"));
    }
}