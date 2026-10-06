package com.xtc.virtualselfapi.bean;

import java.util.List;

/**
 * 装扮拥有情况：已拥有列表与当前使用项。
 */
public class Costume {

    private int current;
    private List<Integer> owned;

    public void setOwned(List<Integer> owned) {
        this.owned = owned;
    }

    public List<Integer> getOwned() {
        return this.owned;
    }

    public void setCurrent(int current) {
        this.current = current;
    }

    public int getCurrent() {
        return this.current;
    }

    @Override
    public String toString() {
        return "Costume{owned=" + this.owned + ", current=" + this.current + '}';
    }
}