package com.xtc.dispatch.sort;

import java.util.List;

/** Node of the task dependency graph. */
public interface IDepend {

    /** @return the task classes that must complete before this task runs. */
    List<Class<? extends IDepend>> dependsOn();
}