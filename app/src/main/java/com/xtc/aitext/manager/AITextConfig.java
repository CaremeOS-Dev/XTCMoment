package com.xtc.aitext.manager;

import android.view.View;

/**
 * AI 文案配置，包含客户端类型与宿主视图。
 */
public class AITextConfig {

    private int clientType;
    private View hostView;

    public AITextConfig() {
        this.clientType = 1;
    }

    public AITextConfig(AITextConfigBuilder builder) {
        this.clientType = 1;
        this.clientType = builder.clientType;
        this.hostView = builder.hostView;
    }

    public int getClientType() {
        return clientType;
    }

    public void setClientType(int clientType) {
        this.clientType = clientType;
    }

    public View getHostView() {
        return hostView;
    }

    public void setHostView(View hostView) {
        this.hostView = hostView;
    }

    /**
     * 配置构建器。
     */
    public static class AITextConfigBuilder {

        private int clientType = 1;
        private View hostView;

        public AITextConfigBuilder clientType(int clientType) {
            this.clientType = clientType;
            return this;
        }

        public AITextConfigBuilder hostView(View hostView) {
            this.hostView = hostView;
            return this;
        }

        public AITextConfig build() {
            return new AITextConfig(this);
        }
    }
}