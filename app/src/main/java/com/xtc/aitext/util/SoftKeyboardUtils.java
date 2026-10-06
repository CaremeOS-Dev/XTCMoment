package com.xtc.aitext.util;

import android.content.Context;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/**
 * 软键盘工具。
 */
public class SoftKeyboardUtils {

    /** 显示软键盘。 */
    public static void showKeyboard(EditText editText) {
        editText.requestFocus();
        ((InputMethodManager) editText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE))
                .showSoftInput(editText, 0);
    }

    static void hideKeyboard(EditText editText) {
        ((InputMethodManager) editText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE))
                .hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    static void toggleKeyboard(EditText editText) {
        editText.requestFocus();
        ((InputMethodManager) editText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE))
                .toggleSoftInput(0, 0);
    }
}