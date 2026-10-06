package com.xtc.ui.widget.numberpicker.interfaces;

import com.xtc.ui.widget.numberpicker.view.NumberPickerView;

/** 相对原始索引的取值变化回调。 */
public interface IValueChangeListenerRelativeToRaw {
    void onValueChangeRelativeToRaw(NumberPickerView picker, int oldIndex, int newIndex, String[] displayedValues);
}