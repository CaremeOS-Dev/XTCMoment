package com.xtc.httplib.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Per-request timeout overrides, in milliseconds; -1 keeps the default. */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Timeout {
    int all() default -1;

    int connect() default -1;

    int read() default -1;

    int write() default -1;
}