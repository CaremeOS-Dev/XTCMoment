package com.xtc.httplib.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Marks a request method with the `KeepHead` behaviour. */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface KeepHead {
}