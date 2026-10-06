package com.xtc.moment.behavior;

/**
 * 发布埋点实体的单例持有者。
 */
public class DigitalManager {

    private static volatile DigitalManager instance;
    private DigitalEntity digitalEntity = new DigitalEntity();

    public static DigitalManager getInstance() {
        if (instance == null) {
            synchronized (DigitalManager.class) {
                if (instance == null) {
                    instance = new DigitalManager();
                }
            }
        }
        return instance;
    }

    public void clearDigitalEntity() {
        if (this.digitalEntity == null) {
            this.digitalEntity = new DigitalEntity();
        } else {
            this.digitalEntity = new DigitalEntity();
        }
    }

    public DigitalEntity getDigitalEntity() {
        return this.digitalEntity;
    }
}