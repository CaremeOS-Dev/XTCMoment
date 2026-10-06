package com.xtc.im.core.common.anotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Marks a field with the tag value used when the entity is serialised. */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface TagValue {
    int value() default 0;
}
