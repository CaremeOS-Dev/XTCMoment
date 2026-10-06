package com.xtc.ui.widget.permission.cta;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.xtc.moment.R;
import com.xtc.ui.widget.button.DoubleFlatButton;
import com.xtc.ui.widget.dialog.bean.noIcon.CtaPermissionBean;
import com.xtc.ui.widget.permission.cta.adapter.CtaPermissionAdapter;
import com.xtc.ui.widget.permission.cta.interfaces.ICtaPermissionCallback;
import com.xtc.ui.widget.util.CtaPermissionUtil;
import com.xtc.ui.widget.util.UiCommonUtil;

import java.text.MessageFormat;

/** The body of the CTA dialog: a permission list plus an agree/refuse pair. */
public class CtaPermissionView extends RelativeLayout {

    public CtaPermissionAdapter adapter;
    public DoubleFlatButton bfbOperate;
    public ICtaPermissionCallback iCtaPermissionCallback;
    public RecyclerView lvPermission;

    public CtaPermissionView(Context context) {
        this(context, null);
    }

    public CtaPermissionView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CtaPermissionView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_cta_permission, this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        this.lvPermission = (RecyclerView) findViewById(R.id.lv_permission);
        this.lvPermission.setLayoutManager(linearLayoutManager);
        this.lvPermission.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                if (parent.getChildLayoutPosition(view) < parent.getAdapter().getItemCount() - 1) {
                    outRect.bottom = 0;
                } else {
                    outRect.bottom = UiCommonUtil.dp2Px(CtaPermissionView.this.getContext(), 10.0f);
                }
            }
        });
        this.bfbOperate = (DoubleFlatButton) findViewById(R.id.dfb_operate);
        this.bfbOperate.getLeftButton().setText(R.string.cancel);
        this.bfbOperate.getRightButton().setText(R.string.agree);
    }

    public void setCallBack(ICtaPermissionCallback callback) {
        this.iCtaPermissionCallback = callback;
        this.bfbOperate.getLeftButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (CtaPermissionView.this.iCtaPermissionCallback != null) {
                    CtaPermissionView.this.iCtaPermissionCallback.refuse();
                }
            }
        });
        this.bfbOperate.getRightButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (CtaPermissionView.this.iCtaPermissionCallback != null) {
                    CtaPermissionView.this.iCtaPermissionCallback.allow();
                    try {
                        CtaPermissionUtil.setPackageWhiteListByLauncher(CtaPermissionView.this.getContext());
                    } catch (Throwable ignored) {
                    }
                }
            }
        });
    }

    public void initData(CtaPermissionBean bean) {
        String title = bean.getTitle();
        String headerText;
        if (bean.isAppStore()) {
            headerText = MessageFormat.format(getContext().getString(R.string.cta_allow_permission_pre_icon), title);
        } else {
            headerText = MessageFormat.format(getContext().getString(R.string.cta_allow_permission), title);
        }
        this.lvPermission.setAdapter(new CtaPermissionAdapter(getContext(), bean.getData(), headerText, bean.getTip()));
    }
}