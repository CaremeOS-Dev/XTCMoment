package com.xtc.moment.module.comment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.TextView;

import com.xtc.log.LogUtil;
import com.xtc.moment.R;
import com.xtc.moment.net.bean.MomentLbs;

import java.text.MessageFormat;

/**
 * 位置评价详情页面。
 */
public class CommentLbsDetailActivity extends Activity {

    private static final String TAG = "CommentLbsDetailActivity";
    private static final String EXTRA_NAME = "NAME";
    private static final String EXTRA_LBS = "LBS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_lbs_detail);
        MomentLbs momentLbs = getIntent().getParcelableExtra(EXTRA_LBS);
        if (momentLbs == null) {
            LogUtil.i(TAG, "onCreate() momentLbs = null");
            finish();
            return;
        }
        String friendName = getIntent().getStringExtra(EXTRA_NAME);
        TextView tvSelfLabel = (TextView) findViewById(R.id.tv_i_label);
        TextView tvFriendLabel = (TextView) findViewById(R.id.tv_friend_label);
        TextView tvTip = (TextView) findViewById(R.id.tv_tip);
        SpannableString starLabel = new SpannableString(getStarLabel(momentLbs));
        starLabel.setSpan(new ForegroundColorSpan(-83149), 0, starLabel.length(), 17);
        if (TextUtils.isEmpty(friendName)) {
            tvSelfLabel.setText(MessageFormat.format(getString(R.string.lbs_i_star_label), getString(R.string.me)));
            tvSelfLabel.append(starLabel);
            tvTip.setVisibility(View.GONE);
        } else {
            if (!momentLbs.hasEvaluation() || momentLbs.getStar() <= 2) {
                tvSelfLabel.setVisibility(View.GONE);
            } else {
                tvSelfLabel.setText(MessageFormat.format(getString(R.string.lbs_i_star_label), friendName));
                tvSelfLabel.append(starLabel);
            }
            if (!momentLbs.hasAlready()) {
                tvTip.setVisibility(View.VISIBLE);
            }
        }
        SpannableString friendStarLabel = new SpannableString(
                getResources().getString(R.string.lbs_friend_star_label1));
        friendStarLabel.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.white)), 0,
                friendStarLabel.length(), 17);
        tvFriendLabel.setText(MessageFormat.format(getString(R.string.lbs_friend_star_label),
                Integer.valueOf(momentLbs.getRecommend())));
        tvFriendLabel.append(friendStarLabel);
    }

    private String getStarLabel(MomentLbs momentLbs) {
        int star = momentLbs.getStar();
        if (star == 1) {
            return getString(R.string.lbs_star_1);
        }
        if (star == 2) {
            return getString(R.string.lbs_star_2);
        }
        if (star == 3) {
            return getString(R.string.lbs_star_3);
        }
        if (star == 4) {
            return getString(R.string.lbs_star_4);
        }
        if (star == 5) {
            return getString(R.string.lbs_star_5);
        }
        return getString(R.string.lbs_star_1);
    }

    public static void start(Context context, String friendName, MomentLbs momentLbs) {
        Intent intent = new Intent(context, CommentLbsDetailActivity.class);
        intent.putExtra(EXTRA_NAME, friendName);
        intent.putExtra(EXTRA_LBS, momentLbs);
        context.startActivity(intent);
    }
}