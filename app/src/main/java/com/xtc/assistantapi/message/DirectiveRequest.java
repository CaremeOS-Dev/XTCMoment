package com.xtc.assistantapi.message;

import android.content.Context;

/**
 * 指令请求，携带指令本体与上下文。
 */
public class DirectiveRequest {

    private Directive directive;
    private Context context;

    private DirectiveRequest() {
    }

    public DirectiveRequest(Directive directive, Context context) {
        this.directive = directive;
        this.context = context;
    }

    public Directive getDirective() {
        return directive;
    }

    public void setDirective(Directive directive) {
        this.directive = directive;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}