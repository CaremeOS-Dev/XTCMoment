package com.xtc.moment.module.community;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.xtc.moment.R;
import com.xtc.moment.constants.ModuleSwitchConstant;
import com.xtc.moment.util.switchs.ModuleSwitchUtil;
import com.xtc.ui.widget.item.clickScale.RightIconItem;
import com.xtc.ui.widget.privacy.PrivacyUriActivity;
import com.xtc.ui.widget.privacy.setting.AbstractSettingActivity;

/**
 * 关于动态设置页。
 */
public class AboutMomentActivity extends AbstractSettingActivity {

    private static final String TAG = AboutMomentActivity.class.getSimpleName();

    @Override
    protected int setContentViewId() {
        return R.layout.activity_setting;
    }

    public static void jumpIntent(Context context) {
        context.startActivity(new Intent(context, AboutMomentActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    private void initView() {
        final RightIconItem conversationItem = (RightIconItem) findViewById(R.id.community_conversation_rii);
        conversationItem.setVisibility(View.GONE);
        if (ModuleSwitchUtil.queryModuleSwitchByBoolean(this,
                ModuleSwitchConstant.MODULE_SWITCH_COMMUNITY_CONVERSATION, false)) {
            conversationItem.setVisibility(View.VISIBLE);
            conversationItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CommunityConversationActivity.start(AboutMomentActivity.this);
                }
            });
        }
        setItemViewStateByViewId(R.id.about_rii_privacy, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PrivacyUriActivity.start(AboutMomentActivity.this, getString(R.string.https_privacy));
            }
        });
    }
}