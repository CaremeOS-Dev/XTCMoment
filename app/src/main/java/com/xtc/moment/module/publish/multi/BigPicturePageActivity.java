package com.xtc.moment.module.publish.multi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.xtc.log.LogUtil;
import com.xtc.moment.MomentApp;
import com.xtc.moment.R;
import com.xtc.moment.asynclayout.AsyncLayoutLoader;
import com.xtc.moment.db.bean.DbMoment;
import com.xtc.moment.module.bean.MultiPhotoContent;
import com.xtc.moment.module.bean.PhotoMD5Value;
import com.xtc.moment.module.publish.multi.adapter.BigPictureAdapter;
import com.xtc.moment.module.publish.multi.view.PointerViewPager;
import com.xtc.utils.common.CollectionUtil;
import com.xtc.utils.encode.JSONUtil;
import com.xtc.utils_screenshot_carry_data.ScreenshotCallback;
import com.xtc.utils_screenshot_carry_data.ScreenshotUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 多图大图预览页面：左右滑动查看动态中的全部图片，并支持截图携带数据。
 */
public class BigPicturePageActivity extends Activity {

    private static final String CURRENT_MOMENT_BEAN = "current_moment_bean";
    private static final String CURRENT_PAGE = "current_page";
    private static final String ENLARGE_PHOTO_LIST = "enlarge_photo_list";
    private static final String TAG = "BigPicturePageActivity";

    private BigPictureAdapter adapter;
    private DbMoment dbMoment;
    private ArrayList<String> photoLists = new ArrayList<>();
    private PointerViewPager vp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AsyncLayoutLoader.getInstance().setContentView(this, R.layout.activity_big_picture_page);
        initView();
    }

    private void initView() {
        this.vp = (PointerViewPager) findViewById(R.id.vp_view);
        Intent intent = getIntent();
        this.photoLists = intent.getStringArrayListExtra(ENLARGE_PHOTO_LIST);
        int currentPage = intent.getIntExtra(CURRENT_PAGE, 0);
        if (CollectionUtil.isEmpty(this.photoLists)) {
            LogUtil.i(TAG, "photoLists is null");
            finish();
            return;
        }
        this.adapter = new BigPictureAdapter(this, this.photoLists);
        this.vp.setAdapter(this.adapter);
        if (currentPage < this.photoLists.size()) {
            this.vp.setCurrentItem(currentPage);
        }
        this.adapter.notifyDataSetChanged();
        setScreenshotMd5();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ScreenshotUtils.unregister(this);
    }

    private void setScreenshotMd5() {
        LogUtil.i(TAG, "setScreenshotMd5");
        ScreenshotUtils.register(this, new ScreenshotCallback() {
            @Override
            public String getScreenshotCarryData() {
                if (BigPicturePageActivity.this.vp != null) {
                    int currentItem = BigPicturePageActivity.this.vp.getCurrentItem();
                    String trackMd5Value = BigPicturePageActivity.this.getTrackMd5Value(currentItem);
                    if (TextUtils.isEmpty(trackMd5Value)) {
                        if (currentItem < 0 || currentItem >= BigPicturePageActivity.this.photoLists.size()) {
                            return null;
                        }
                        String carryData = ScreenshotUtils.initScreenshotData(BigPicturePageActivity.this.photoLists.get(currentItem), MomentApp.getWatchId());
                        LogUtil.i(BigPicturePageActivity.TAG, "getScreenshotCarryData ,carryData = " + carryData);
                        return carryData;
                    }
                    LogUtil.i(BigPicturePageActivity.TAG, "getScreenshotCarryData ,trackMd5Value = " + trackMd5Value);
                    return trackMd5Value;
                }
                LogUtil.i(BigPicturePageActivity.TAG, "getScreenshotCarryData , viewpager is null");
                return null;
            }
        });
    }

    private String getTrackMd5Value(int position) {
        if (this.dbMoment == null) {
            String momentJson = getIntent().getStringExtra(CURRENT_MOMENT_BEAN);
            if (TextUtils.isEmpty(momentJson)) {
                return null;
            }
            this.dbMoment = JSONUtil.fromJSON(momentJson, DbMoment.class);
            LogUtil.i(TAG, "dbMoment = " + this.dbMoment);
            if (this.dbMoment == null) {
                return null;
            }
        }
        String content = this.dbMoment.getContent();
        if (TextUtils.isEmpty(content)) {
            return null;
        }
        MultiPhotoContent multiPhotoContent = JSONUtil.fromJSON(content, MultiPhotoContent.class);
        if (multiPhotoContent == null) {
            return null;
        }
        List<PhotoMD5Value> trackMd5Values = multiPhotoContent.getTrackMd5Values();
        if (!CollectionUtil.isEmpty(trackMd5Values) && position >= 0 && position < trackMd5Values.size()) {
            return JSONUtil.toJSON(trackMd5Values.get(position));
        }
        return null;
    }
}