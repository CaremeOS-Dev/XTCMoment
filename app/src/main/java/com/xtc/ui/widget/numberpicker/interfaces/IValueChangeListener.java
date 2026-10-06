package com.xtc.ui.widget.numberpicker.interfaces;

import com.xtc.ui.widget.numberpicker.view.NumberPickerView;

/** 取值变化回调。 */
public interface IValueChangeListener {
    void onValueChange(NumberPickerView picker, int oldValue, int newValue);
}