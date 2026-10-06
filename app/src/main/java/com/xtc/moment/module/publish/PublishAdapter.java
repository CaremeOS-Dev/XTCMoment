package com.xtc.moment.module.publish;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xtc.aitext.util.AIModuleUtil;
import com.xtc.log.LogUtil;
import com.xtc.moment.R;
import com.xtc.moment.constants.ModuleSwitchConstant;
import com.xtc.moment.module.bean.FunctionBean;
import com.xtc.moment.util.RxViewOnClick;
import com.xtc.moment.util.switchs.ModuleSwitchUtil;
import com.xtc.utils.system.CameraUtils;
import com.xtc.virtualselfapi.constants.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Subscriber;

/**
 * 发布页功能入口列表适配器：根据相机能力与全网开关动态生成可用的发布类型。
 */
public class PublishAdapter extends RecyclerView.Adapter<PublishAdapter.ViewHolder> {

    private static final int ITEM_TYPE_FOOTER = 11;
    private static final int ITEM_TYPE_HEADER = 10;
    public static final int ITEM_TYPE_NORMAL = 12;
    private static final String TAG = PublishAdapter.class.getSimpleName();

    private List<FunctionBean> functions = new ArrayList<>();
    private OnItemClickListener listener;
    private Context mContext;
    private View mFooterView;
    private View mHeaderView;

    public interface OnItemClickListener {
        void onAboutMomentClick();

        void onCommunityConversationClick();

        void onItemClick(int type);
    }

    public PublishAdapter(Context context) {
        this.mContext = context;
        boolean hasCamera = CameraUtils.hasCamera(this.mContext);
        boolean canPublishPhoto = hasCamera && ModuleSwitchUtil.queryModuleSwitchByBoolean(context, 112, false);
        boolean canPublishVideo = hasCamera && ModuleSwitchUtil.queryModuleSwitchByBoolean(context, ModuleSwitchConstant.MODULE_SWITCH_MOMENT_SEND_VIDEO_FUNCTION, false);
        boolean canPublishLocation = ModuleSwitchUtil.queryModuleSwitchByBoolean(context, ModuleSwitchConstant.MODULE_SWITCH_MOMENT_PUBLISH_LOCATION, false);
        boolean canPublishText = ModuleSwitchUtil.queryModuleSwitchByBoolean(context, 102, true);
        boolean supportAIText = AIModuleUtil.isSupportAIText(context, context.getPackageName());
        if (canPublishPhoto) {
            this.functions.add(new FunctionBean(R.drawable.circle_photo, this.mContext.getString(R.string.photo), 5));
        }
        if (canPublishPhoto) {
            this.functions.add(new FunctionBean(R.drawable.circle_pictures, this.mContext.getString(R.string.picture), 4));
        } else {
            LogUtil.d(TAG, "没有相机 或 好友圈发图片全网开关关闭");
        }
        if (canPublishVideo) {
            this.functions.add(new FunctionBean(R.drawable.circle_video, this.mContext.getString(R.string.video), 6));
        } else {
            LogUtil.d(TAG, "发视频的全网开关 关闭");
        }
        if (canPublishText) {
            this.functions.add(new FunctionBean(R.drawable.circle_vioce, this.mContext.getString(R.string.text), 3));
        } else {
            LogUtil.d(TAG, "好友圈发文字全网开关关闭");
        }
        if (supportAIText) {
            this.functions.add(new FunctionBean(R.drawable.ic_aitext_icon, this.mContext.getString(R.string.ai_text), 29));
        }
        if (canPublishLocation) {
            this.functions.add(new FunctionBean(R.drawable.circle_location, this.mContext.getString(R.string.location), 2));
        } else {
            LogUtil.d(TAG, "好友圈发位置全网开关关闭");
        }
        this.functions.add(new FunctionBean(R.drawable.circle_mood, this.mContext.getString(R.string.mood), 0));
        this.functions.add(new FunctionBean(R.drawable.circle_state, this.mContext.getString(R.string.state), 1));
        initFootView();
    }

    private void initFootView() {
        View view = LayoutInflater.from(this.mContext).inflate(R.layout.footer_recycle_publish, null, false);
        LinearLayout aboutMomentLayout = (LinearLayout) view.findViewById(R.id.ll_about_moment);
        ((TextView) view.findViewById(R.id.tv_about_moment)).setText(String.format(this.mContext.getString(R.string.about_settings), this.mContext.getString(R.string.app_name)));
        aboutMomentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtil.d(PublishAdapter.TAG, "click Protocol");
                if (PublishAdapter.this.listener != null) {
                    PublishAdapter.this.listener.onAboutMomentClick();
                }
            }
        });
        setFooterView(view);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (ITEM_TYPE_FOOTER == viewType && hasFooterView()) {
            ViewGroup parentGroup = (ViewGroup) this.mFooterView.getParent();
            if (parentGroup != null) {
                parentGroup.removeView(this.mFooterView);
            }
            return new ViewHolder(this.mFooterView);
        }
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycle_publish, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (getItemViewType(position) == ITEM_TYPE_NORMAL) {
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            if (position == 0 || position == 1) {
                layoutParams.topMargin = (int) this.mContext.getResources().getDimension(R.dimen.func_item_first_line_margin_top);
            } else {
                layoutParams.topMargin = (int) this.mContext.getResources().getDimension(R.dimen.func_item_other_line_margin_top);
            }
            holder.itemView.setLayoutParams(layoutParams);
            int itemPosition = getItemPosition(position);
            holder.setImageResource(this.functions.get(itemPosition).getIcon());
            holder.setTvFragment(this.functions.get(itemPosition).getName());
            holder.setItemViewClickListener(holder.itemView, this.functions.get(itemPosition).getType());
            return;
        }
        getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        int size = this.functions.size();
        if (hasHeaderView()) {
            size++;
        }
        return hasFooterView() ? size + 1 : size;
    }

    @Override
    public int getItemViewType(int position) {
        if (hasHeaderView() && position == 0) {
            return ITEM_TYPE_HEADER;
        }
        return (hasFooterView() && position == getItemCount() - 1) ? ITEM_TYPE_FOOTER : ITEM_TYPE_NORMAL;
    }

    private int getItemPosition(int position) {
        return hasHeaderView() ? position - 1 : position;
    }

    private boolean hasFooterView() {
        return this.mFooterView != null;
    }

    private boolean hasHeaderView() {
        return this.mHeaderView != null;
    }

    public void setHeaderView(View view) {
        this.mHeaderView = view;
    }

    public void setFooterView(View view) {
        this.mFooterView = view;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ivFragment;
        TextView tvFragment;

        public ViewHolder(View itemView) {
            super(itemView);
            this.ivFragment = (ImageView) itemView.findViewById(R.id.iv_fragment);
            this.tvFragment = (TextView) itemView.findViewById(R.id.tv_fragment);
        }

        public void setImageResource(int resId) {
            this.ivFragment.setImageResource(resId);
        }

        public void setTvFragment(String text) {
            this.tvFragment.setText(text);
        }

        public void setItemViewClickListener(View view, final int type) {
            RxViewOnClick.with(view)
                    .throttleFirst(Constants.DEFAULT_INIT_DELAY_TIME, TimeUnit.MILLISECONDS)
                    .subscribe(new Subscriber<View>() {
                        @Override
                        public void onCompleted() {
                        }

                        @Override
                        public void onError(Throwable e) {
                            LogUtil.e(PublishAdapter.TAG, e);
                        }

                        @Override
                        public void onNext(View v) {
                            LogUtil.d(PublishAdapter.TAG, "click item " + type);
                            if (PublishAdapter.this.listener != null) {
                                PublishAdapter.this.listener.onItemClick(type);
                            }
                        }
                    });
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.listener = onItemClickListener;
    }
}