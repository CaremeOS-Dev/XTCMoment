package com.xtc.im.core.common.request;

import android.os.Parcel;
import android.os.Parcelable;

import com.xtc.im.core.common.LogTag;
import com.xtc.im.core.common.listener.OnReceiveFinishListener;
import com.xtc.im.core.common.listener.OnReceiveListener;
import com.xtc.im.core.common.request.entity.RequestEntity;
import com.xtc.im.core.common.response.PushResponse;
import com.xtc.log.LogUtil;

/** 一次推送请求：请求实体 + 响应监听，支持链式构造。 */
public class PushRequest implements Parcelable {

    private static final String TAG = LogTag.tag("PushRequest");

    public static final Parcelable.Creator<PushRequest> CREATOR = new Parcelable.Creator<PushRequest>() {
        @Override
        public PushRequest createFromParcel(Parcel source) {
            return new PushRequest(source);
        }

        @Override
        public PushRequest[] newArray(int size) {
            return new PushRequest[size];
        }
    };

    private Object extra;
    protected boolean hasOnReceive;
    private OnReceiveFinishListener innerFinishLister;
    private OnReceiveListener innerReceiveListener;
    private boolean mutiResponse;
    private boolean needResponse;
    protected OnReceiveFinishListener onReceiveFinishListener;
    protected OnReceiveListener onReceiveListener;
    private RequestEntity requestEntity;

    private PushRequest(Builder builder) {
        this.innerReceiveListener = new OnReceiveListener() {
            @Override
            public void onReceive(PushRequest pushRequest, PushResponse pushResponse) {
                if (hasOnReceive) {
                    LogUtil.w(TAG, "request:" + pushRequest.entity() + ",response:"
                            + pushResponse.getResponseEntity());
                    return;
                }
                if (onReceiveListener != null) {
                    LogUtil.i(TAG, "onReceive request:" + pushRequest.entity() + ",onReceive response:"
                            + pushResponse.getResponseEntity());
                    onReceiveListener.onReceive(pushRequest, pushResponse);
                }
                hasOnReceive = true;
            }
        };
        this.innerFinishLister = new OnReceiveFinishListener() {
            @Override
            public void onFinish() {
                if (onReceiveFinishListener != null) {
                    onReceiveFinishListener.onFinish();
                }
            }
        };
        this.requestEntity = builder.requestEntity;
        this.needResponse = builder.needResponse;
        this.extra = builder.extra;
        this.mutiResponse = builder.mutiResponse;
        this.onReceiveListener = builder.onReceiveListener;
        this.onReceiveFinishListener = builder.onReceiveFinishListener;
    }

    protected PushRequest(Parcel source) {
        this.innerReceiveListener = new OnReceiveListener() {
            @Override
            public void onReceive(PushRequest pushRequest, PushResponse pushResponse) {
                if (hasOnReceive) {
                    LogUtil.w(TAG, "request:" + pushRequest.entity() + ",response:"
                            + pushResponse.getResponseEntity());
                    return;
                }
                if (onReceiveListener != null) {
                    LogUtil.i(TAG, "onReceive request:" + pushRequest.entity() + ",onReceive response:"
                            + pushResponse.getResponseEntity());
                    onReceiveListener.onReceive(pushRequest, pushResponse);
                }
                hasOnReceive = true;
            }
        };
        this.innerFinishLister = new OnReceiveFinishListener() {
            @Override
            public void onFinish() {
                if (onReceiveFinishListener != null) {
                    onReceiveFinishListener.onFinish();
                }
            }
        };
        this.requestEntity = (RequestEntity) source.readValue(getClass().getClassLoader());
        this.needResponse = source.readByte() != 0;
        this.extra = source.readValue(getClass().getClassLoader());
        this.mutiResponse = source.readByte() != 0;
        this.onReceiveListener = (OnReceiveListener) source.readValue(getClass().getClassLoader());
        this.hasOnReceive = source.readByte() != 0;
        this.onReceiveFinishListener = (OnReceiveFinishListener) source.readValue(getClass().getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.requestEntity);
        dest.writeByte(this.needResponse ? (byte) 1 : (byte) 0);
        dest.writeValue(this.extra);
        dest.writeByte(this.mutiResponse ? (byte) 1 : (byte) 0);
        dest.writeValue(this.onReceiveListener);
        dest.writeByte(this.hasOnReceive ? (byte) 1 : (byte) 0);
        dest.writeValue(this.onReceiveFinishListener);
    }

    public void readFromParcel(Parcel source) {
        this.requestEntity = (RequestEntity) source.readValue(getClass().getClassLoader());
        this.needResponse = source.readByte() != 0;
        this.extra = source.readValue(getClass().getClassLoader());
        this.mutiResponse = source.readByte() != 0;
        this.onReceiveListener = (OnReceiveListener) source.readValue(getClass().getClassLoader());
        this.hasOnReceive = source.readByte() != 0;
        this.onReceiveFinishListener = (OnReceiveFinishListener) source.readValue(getClass().getClassLoader());
    }

    public RequestEntity entity() {
        return this.requestEntity;
    }

    public boolean needResponse() {
        return this.needResponse;
    }

    public boolean mutiResponse() {
        return this.mutiResponse;
    }

    public Object extra() {
        return this.extra;
    }

    public OnReceiveListener innerReceiveListener() {
        return this.innerReceiveListener;
    }

    public OnReceiveFinishListener innerFinisheListener() {
        return this.innerFinishLister;
    }

    @Override
    public String toString() {
        return "PushRequest{, requestEntity=" + this.requestEntity + ", needResponse=" + this.needResponse
                + ", extra=" + this.extra + ", mutiResponse=" + this.mutiResponse + '}';
    }

    /** 推送请求构造器。 */
    public static class Builder {
        private Object extra;
        private boolean mutiResponse;
        private boolean needResponse = true;
        private OnReceiveFinishListener onReceiveFinishListener;
        private OnReceiveListener onReceiveListener;
        private RequestEntity requestEntity;

        public Builder entity(RequestEntity requestEntity) {
            this.requestEntity = requestEntity;
            return this;
        }

        public Builder needResponse(boolean needResponse) {
            this.needResponse = needResponse;
            return this;
        }

        public Builder extra(Object extra) {
            this.extra = extra;
            return this;
        }

        public Builder mutiResponse(boolean mutiResponse) {
            this.mutiResponse = mutiResponse;
            return this;
        }

        public Builder onReceiveListener(OnReceiveListener onReceiveListener) {
            this.onReceiveListener = onReceiveListener;
            return this;
        }

        public Builder onReceiveFinishListener(OnReceiveFinishListener onReceiveFinishListener) {
            this.onReceiveFinishListener = onReceiveFinishListener;
            return this;
        }

        public PushRequest build() {
            if (this.requestEntity == null) {
                throw new IllegalStateException("requestEntity == null");
            }
            return new PushRequest(this);
        }
    }
}