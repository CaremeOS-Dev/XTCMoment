package com.xtc.im.core.common.request.entity;

import com.xtc.im.core.common.anotation.CommandValue;
import com.xtc.im.core.common.anotation.TagValue;

@CommandValue(7)
public class HeartBeatRequestEntity extends RequestEntity {

    @Override
    public String toString() {
        return "}";
    }
}
