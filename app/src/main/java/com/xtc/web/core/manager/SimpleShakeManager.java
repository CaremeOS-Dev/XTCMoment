package com.xtc.web.core.manager;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Message;

import com.xtc.log.LogUtil;
import com.xtc.web.core.XtcWebView;
import com.xtc.web.core.data.resp.RespAccelerometer;

import java.util.ArrayList;
import java.util.Iterator;

/** 摇一摇检测：监听加速度传感器并在检测到摇动时回调 H5。 */
public class SimpleShakeManager {

    private static final int START_SHAKE = 1;
    private static final int AGAIN_SHAKE = 2;
    private static final int END_SHAKE = 3;
    private static final long DELAY_TIME = 1000L;
    private static final float LOW_PASS_ALPHA = 0.8f;
    private static final float HIGH_PASS_ALPHA = 0.19999999f;
    private static final float SHAKE_THRESHOLD_X_Y = 22.0f;
    private static final float SHAKE_THRESHOLD_Z = 25.0f;
    private static final String TAG = "WebCore_SimpleShakeManager";
    private static SimpleShakeManager instance;

    Context mContext;
    ArrayList<Listener> mListeners;
    SensorManager mSensorManager;
    private MyHandler mHandler;
    private XtcWebView xtcWebView;
    private boolean isShake = false;

    /** 摇动监听。 */
    public interface Listener {
        void hearShake();

        void stopShake();
    }

    SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() != Sensor.TYPE_ACCELEROMETER) {
                return;
            }
            float[] gravity = new float[3];
            gravity[0] = (gravity[0] * LOW_PASS_ALPHA) + (event.values[0] * HIGH_PASS_ALPHA);
            gravity[1] = (gravity[1] * LOW_PASS_ALPHA) + (event.values[1] * HIGH_PASS_ALPHA);
            gravity[2] = (gravity[2] * LOW_PASS_ALPHA) + (HIGH_PASS_ALPHA * event.values[2]);
            float linearX = event.values[0] - gravity[0];
            float linearY = event.values[1] - gravity[1];
            float linearZ = event.values[2] - gravity[2];
            if ((Math.abs(linearX) > SHAKE_THRESHOLD_X_Y || Math.abs(linearY) > SHAKE_THRESHOLD_X_Y
                    || Math.abs(linearZ) > SHAKE_THRESHOLD_Z) && !isShake) {
                LogUtil.d(TAG, "11Math.abs(x)" + Math.abs(linearX) + ",Math.abs(y)" + Math.abs(linearY)
                        + ",Math.abs(z)" + Math.abs(linearZ));
                LogUtil.d(TAG, "onSensorChanged: 摇动");
                isShake = true;
                mHandler.sendEmptyMessageDelayed(START_SHAKE, DELAY_TIME);
            }
        }
    };

    public static synchronized SimpleShakeManager getInstance(Context context) {
        if (instance == null) {
            instance = new SimpleShakeManager(context);
        }
        return instance;
    }

    public synchronized void setXtcWebView(XtcWebView xtcWebView) {
        this.xtcWebView = xtcWebView;
    }

    public synchronized void release() {
        this.xtcWebView = null;
    }

    public void onAccelerometer() {
        start();
    }

    public void offAccelerometer() {
        stop();
    }

    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            super.handleMessage(message);
            int what = message.what;
            if (what == START_SHAKE) {
                LogUtil.w(TAG, "START_SHAKE");
                notifyListeners();
                isShake = false;
            } else if (what == AGAIN_SHAKE || what == END_SHAKE) {
                isShake = false;
                notifyListenersStop();
                LogUtil.w(TAG, "END_SHAKE");
            }
        }
    }

    public SimpleShakeManager(Context context) {
        if (context == null) {
            return;
        }
        this.mContext = context;
        this.mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        this.mHandler = new MyHandler();
        this.mListeners = new ArrayList<>();
    }

    public void registerOnShakeListener(Listener listener) {
        if (this.mListeners.contains(listener)) {
            return;
        }
        this.mListeners.add(listener);
    }

    public void unregisterOnShakeListener(Listener listener) {
        this.mListeners.remove(listener);
    }

    /** 开始监听加速度传感器。 */
    public void start() {
        SensorManager sensorManager = this.mSensorManager;
        if (sensorManager == null) {
            LogUtil.i(TAG, "mSensorManager is null");
            return;
        }
        if (sensorManager.registerListener(this.sensorEventListener,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_FASTEST)) {
            return;
        }
        LogUtil.e(TAG, " shake sensor open fail！");
        RespAccelerometer response = new RespAccelerometer();
        response.setCode(RespAccelerometer.Code.FAIL);
        XtcWebView xtcWebView = this.xtcWebView;
        if (xtcWebView != null) {
            xtcWebView.callHandler("onAccelerometerCallback", new Object[]{response});
        }
    }

    /** 停止监听。 */
    public void stop() {
        SensorManager sensorManager = this.mSensorManager;
        if (sensorManager != null) {
            sensorManager.unregisterListener(this.sensorEventListener);
            for (int index = 0; index < this.mListeners.size(); index++) {
                unregisterOnShakeListener(this.mListeners.get(index));
                LogUtil.i(TAG, "notifyListenersStop");
            }
            LogUtil.i(TAG, "notifyListenersStop");
        }
    }

    private void notifyListeners() {
        Iterator<Listener> iterator = this.mListeners.iterator();
        while (iterator.hasNext()) {
            iterator.next().hearShake();
        }
        RespAccelerometer response = new RespAccelerometer();
        response.setCode(RespAccelerometer.Code.SUCCESS);
        XtcWebView xtcWebView = this.xtcWebView;
        if (xtcWebView != null) {
            xtcWebView.callHandler("onAccelerometerCallback", new Object[]{response});
        }
    }

    private void notifyListenersStop() {
        for (int index = 0; index < this.mListeners.size(); index++) {
            this.mListeners.get(index).stopShake();
            LogUtil.i(TAG, "notifyListenersStop");
        }
    }
}