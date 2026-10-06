package com.xtc.virtualselfapi.generate.interfaces;

import com.xtc.virtualselfapi.generate.bean.GenerateVirtualBean;

/**
 * 虚拟形象渲染策略接口。
 */
public interface IGenerateVisualStrategy {

    void generateVisual(GenerateVirtualBean generateVirtualBean, int status);
}