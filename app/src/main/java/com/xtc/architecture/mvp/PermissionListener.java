package com.xtc.architecture.mvp;

import java.util.List;

/** Result callback for a runtime-permission request. */
public interface PermissionListener {
    void onGranted();

    void onPartPermissionDenied(List<String> granted, List<String> denied);
}
