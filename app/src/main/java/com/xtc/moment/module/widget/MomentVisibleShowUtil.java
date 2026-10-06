package com.xtc.moment.module.widget;

import android.content.Context;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.xtc.contactapi.contact.bean.ContactBean;
import com.xtc.contactapi.contact.manager.ContactManager;
import com.xtc.moment.R;
import com.xtc.moment.module.bean.FriendsVisibleBean;
import com.xtc.utils.common.CollectionUtil;

import java.util.List;

/**
 * Renders the "who can see this moment" row of the publish page.
 *
 * <p>The visible range type decides the icon, the label colour and the friend summary:
 * 0 = all friends, 1 = only selected friends, 2 = selected friends excluded, 3 = nobody.
 */
public class MomentVisibleShowUtil {

    /** Maximum number of friend names shown before the summary is collapsed. */
    private static final int SHOW_FRIENDS_NAME_MAX = 3;

    public static void dealVisibleRange(Context context, FriendsVisibleBean visibleBean, ImageView iconView,
                                        TextView titleView, TextView detailView) {
        if (visibleBean == null) {
            visibleBean = new FriendsVisibleBean();
        }
        List<String> friends = visibleBean.getFriends();
        int type = visibleBean.getType();
        if (type == 0) {
            iconView.setImageResource(R.drawable.ic_mine_2);
            titleView.setText(context.getString(R.string.who_can_see));
            titleView.setTextColor(context.getResources().getColor(R.color.white));
            detailView.setText(context.getString(R.string.all_friends_able_visible_details));
            return;
        }
        if (type == 1) {
            iconView.setImageResource(R.drawable.ic_mine_2);
            titleView.setText(context.getString(R.string.who_can_see));
            showFriendsDetails(detailView, friends, context);
            titleView.setTextColor(context.getResources().getColor(R.color.white));
            return;
        }
        if (type == 2) {
            iconView.setImageResource(R.drawable.ic_mine);
            titleView.setText(context.getString(R.string.who_cant_see));
            titleView.setTextColor(context.getResources().getColor(R.color.color_e5382c));
            showFriendsDetails(detailView, friends, context);
            return;
        }
        if (type != 3) {
            return;
        }
        iconView.setImageResource(R.drawable.ic_mine_2);
        titleView.setText(context.getString(R.string.who_can_see));
        titleView.setTextColor(context.getResources().getColor(R.color.white));
        detailView.setText(context.getString(R.string.no_friends_able_visible_details));
    }

    /** Joins up to {@link #SHOW_FRIENDS_NAME_MAX} friend names, collapsing the rest into a count. */
    public static void showFriendsDetails(TextView textView, List<String> watchIds, Context context) {
        if (CollectionUtil.isEmpty(watchIds)) {
            textView.setText("");
            return;
        }
        StringBuilder builder = new StringBuilder();
        int shown = 0;
        for (String watchId : watchIds) {
            if (shown >= SHOW_FRIENDS_NAME_MAX) {
                break;
            }
            ContactBean contact = ContactManager.getInstance(context).getContactByWatchIdSync(watchId);
            String displayName = context.getString(R.string.unknown_watch);
            if (contact != null && !TextUtils.isEmpty(contact.getName())) {
                displayName = contact.getName();
            }
            shown++;
            if (shown == SHOW_FRIENDS_NAME_MAX && watchIds.size() > SHOW_FRIENDS_NAME_MAX) {
                builder.append(String.format(context.getString(R.string.friends_list_show), watchIds.size() + ""));
                break;
            }
            builder.append(displayName);
            if (shown < SHOW_FRIENDS_NAME_MAX && shown < watchIds.size()
                    && (watchIds.size() <= SHOW_FRIENDS_NAME_MAX || shown != 2)) {
                builder.append("、 ");
            }
        }
        textView.setText(builder);
    }
}