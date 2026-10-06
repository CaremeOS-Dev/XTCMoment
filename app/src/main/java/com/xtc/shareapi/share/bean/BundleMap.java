package com.xtc.shareapi.share.bean;

import android.os.Bundle;

import com.xtc.shareapi.share.communication.BaseResponse;
import com.xtc.shareapi.share.communication.ShowMessageFromXTC;
import com.xtc.shareapi.share.interfaces.IBundleSerialize;

import java.util.HashMap;

/**
 * 网页分享附带的自定义键值对集合。
 */
public class BundleMap implements IBundleSerialize {

    private HashMap<String, String> map;

    public HashMap<String, String> getMap() {
        return map;
    }

    public void setMap(HashMap<String, String> map) {
        this.map = map;
    }

    @Override
    public void toBundle(Bundle bundle) {
        // 该类不参与 Bundle 写入，仅作为数据载体。
    }

    @Override
    public IBundleSerialize fromBundle(Bundle bundle) {
        return new BundleMap();
    }

    @Override
    public BaseResponse checkArgs() {
        ShowMessageFromXTC.Response response = new ShowMessageFromXTC.Response();
        response.setCode(1);
        return response;
    }

    @Override
    public String toString() {
        return "BundleMap{map=" + map + '}';
    }
}