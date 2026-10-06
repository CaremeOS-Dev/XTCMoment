package com.xtc.moment.share.view;

import android.os.Bundle;

/**
 * 单进程模式下的 H5 展示页面，销毁时不杀进程。
 */
public class SinglePShowH5Activity extends ShowH5Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setKillProcess(false);
    }
}