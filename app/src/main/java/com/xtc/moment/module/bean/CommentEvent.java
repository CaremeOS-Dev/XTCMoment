package com.xtc.moment.module.bean;

/** Event carrying the result of a comment action. */
public class CommentEvent<T> {

    private T bean;
    private String result;

    public CommentEvent() {
    }

    public CommentEvent(String result, T bean) {
        this.result = result;
        this.bean = bean;
    }

    public String getResult() {
        return this.result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public T getBean() {
        return this.bean;
    }

    public void setBean(T bean) {
        this.bean = bean;
    }
}