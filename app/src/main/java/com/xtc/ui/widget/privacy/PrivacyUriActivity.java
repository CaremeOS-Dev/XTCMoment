package com.xtc.ui.widget.privacy;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.xtc.log.LogUtil;
import com.xtc.moment.R;

import java.util.HashMap;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func0;
import rx.schedulers.Schedulers;

/** Shows a URL as a QR code (privacy / agreement / disclaimer pages). */
public class PrivacyUriActivity extends Activity {

    private static final String TAG = "PrivacyUriActivity";

    private ImageView imageView;
    private TextView textView;
    private int mQrWidth = 0;
    private int mQrHeight = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_url);
        this.imageView = (ImageView) findViewById(R.id.iv_url);
        this.textView = (TextView) findViewById(R.id.tv_string);
        ViewGroup.LayoutParams layoutParams = this.imageView.getLayoutParams();
        this.mQrWidth = layoutParams.width;
        this.mQrHeight = layoutParams.height;
        buildQrCode();
        String custom = getIntent().getStringExtra(PrivacyCommon.PrivacyExtras.EXTRA_CUSTOM_STRING);
        if (TextUtils.isEmpty(custom)) {
            this.textView.setText(R.string.privacy_url);
        } else {
            this.textView.setText(String.format(getString(R.string.privacy_custom), custom));
        }
    }

    private void buildQrCode() {
        Observable.fromCallable(new Func0<Bitmap>() {
            @Override
            public Bitmap call() {
                PrivacyUriActivity activity = PrivacyUriActivity.this;
                return activity.createQRImage(
                        activity.getIntent().getStringExtra(PrivacyCommon.PrivacyExtras.EXTRA_URL),
                        PrivacyUriActivity.this.getIntent().getIntExtra(PrivacyCommon.PrivacyExtras.EXTRA_URL_TYPE, 1));
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Bitmap>() {
            @Override
            public void call(Bitmap bitmap) {
                if (!PrivacyUriActivity.this.isActivityEnable() || bitmap == null) {
                    return;
                }
                PrivacyUriActivity.this.imageView.setImageBitmap(bitmap);
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                LogUtil.e(PrivacyUriActivity.TAG, "buildQrCode throwable: " + throwable);
            }
        });
    }

    public Bitmap createQRImage(String url) {
        return createQRImage(url, 1);
    }

    public Bitmap createQRImage(String url, int type) {
        try {
            String jointUrl = PrivacyUtilities.jointUrl(url, this, getIntent().getStringExtra(PrivacyCommon.PrivacyExtras.EXTRA_PACKAGE_NAME), type);
            if (TextUtils.isEmpty(jointUrl)) {
                LogUtil.w(TAG, "NewUrl is empty");
                return null;
            }
            LogUtil.d(TAG, "createQRImage: " + jointUrl);
            HashMap<EncodeHintType, Object> hints = new HashMap<EncodeHintType, Object>();
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
            hints.put(EncodeHintType.MARGIN, 0);
            BitMatrix bitMatrix = new QRCodeWriter().encode(jointUrl, BarcodeFormat.QR_CODE, this.mQrWidth, this.mQrHeight, hints);
            int[] pixels = new int[this.mQrWidth * this.mQrHeight];
            for (int y = 0; y < this.mQrHeight; y++) {
                for (int x = 0; x < this.mQrWidth; x++) {
                    if (bitMatrix.get(x, y)) {
                        pixels[(this.mQrWidth * y) + x] = 0xFF05329E;
                    } else {
                        pixels[(this.mQrWidth * y) + x] = -1;
                    }
                }
            }
            Bitmap bitmap = Bitmap.createBitmap(this.mQrWidth, this.mQrHeight, Bitmap.Config.RGB_565);
            if (bitmap == null) {
                return null;
            }
            bitmap.setPixels(pixels, 0, this.mQrWidth, 0, 0, this.mQrWidth, this.mQrHeight);
            return bitmap;
        } catch (WriterException e) {
            LogUtil.e(TAG, "createQRImage()", e);
            return null;
        }
    }

    public static void start(Context context, String url) {
        start(context, url, "");
    }

    public static void start(Context context, String url, String custom) {
        start(context, url, custom, 1, "");
    }

    public static void start(Context context, String url, String custom, int type) {
        start(context, url, custom, type, "");
    }

    public static void start(Context context, String url, String custom, int type, String packageName) {
        Intent intent = new Intent(context, PrivacyUriActivity.class);
        intent.putExtra(PrivacyCommon.PrivacyExtras.EXTRA_URL, url);
        intent.putExtra(PrivacyCommon.PrivacyExtras.EXTRA_CUSTOM_STRING, custom);
        intent.putExtra(PrivacyCommon.PrivacyExtras.EXTRA_URL_TYPE, type);
        intent.putExtra(PrivacyCommon.PrivacyExtras.EXTRA_PACKAGE_NAME, packageName);
        context.startActivity(intent);
    }

    private boolean isActivityEnable() {
        return !isDestroyed() && !isFinishing();
    }
}
