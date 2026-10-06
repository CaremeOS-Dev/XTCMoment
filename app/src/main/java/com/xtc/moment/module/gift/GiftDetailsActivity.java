package com.xtc.moment.module.gift;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.xtc.architecture.mvp.BaseActivity;
import com.xtc.moment.R;
import com.xtc.moment.db.bean.DbMoment;
import com.xtc.moment.net.bean.GiftDataBean;
import com.xtc.utils.encode.JSONUtil;

import java.util.List;

/**
 * 礼物详情页面。
 */
public class GiftDetailsActivity extends BaseActivity<IGiftDetailsActivityView, GiftDetailsPresenter>
        implements IGiftDetailsActivityView {

    private static final String TAG = "GiftDetailsActivity";

    public static final String GIFT_MOMENT = "gift_moment";
    public static final String GIFT_TYPE = "gift_type";
    public static final String GIFT_TYPE_EGG = "gift_type_egg";
    public static final String GIFT_TYPE_FLOWER = "gift_type_flower";
    public static final String STRING_TITLE = "title_text";

    private RecyclerView rvGiftView;
    private GiftAdapter giftAdapter;

    @Override
    public void initData() {
    }

    @Override
    public void initView() {
    }

    @Override
    public GiftDetailsPresenter createPresenter() {
        return new GiftDetailsPresenter(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gift);
        String titleText = getIntent().getStringExtra(STRING_TITLE);
        String giftTypeName = getIntent().getStringExtra(GIFT_TYPE);
        DbMoment moment = JSONUtil.fromJSON(getIntent().getStringExtra(GIFT_MOMENT), DbMoment.class);
        int giftType = GIFT_TYPE_FLOWER.equals(giftTypeName) ? 1 : 2;
        this.rvGiftView = (RecyclerView) findViewById(R.id.rv_gift);
        this.rvGiftView.setLayoutManager(new LinearLayoutManager(this));
        this.giftAdapter = new GiftAdapter(this, titleText);
        this.rvGiftView.setAdapter(this.giftAdapter);
        this.presenter.getGiftList(giftType, moment);
    }

    @Override
    public void getGiftDataSuccess(List<GiftDataBean> giftData) {
        this.giftAdapter.setGiftList(giftData);
    }

    @Override
    public void getGiftDataFail(List<GiftDataBean> giftData) {
        this.giftAdapter.setGiftList(giftData);
    }
}