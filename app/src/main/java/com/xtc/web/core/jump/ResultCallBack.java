package com.xtc.web.core.jump;

import android.content.Intent;

/** 跳转第三方页面后的结果回调。 */
public interface ResultCallBack {

    void onActivityResult(int requestCode, int resultCode, Intent data);

    void onFailed();
}