package com.libs.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Build;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.StyleRes;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import com.libs.utils.R;

/**
 * @author Created by Qlang on 2017/5/26.
 */

public class CustomViewDialog extends Dialog {

    public CustomViewDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
    }

    /**
     * 实例一个对话框
     *
     * @param context
     * @param layoutResID            布局资源id
     * @param cancelable             是否可以按返回键取消
     * @param touchOutsideCancelable 是否可以点击外部取消
     * @param listener               回调
     * @return
     */
    public static CustomViewDialog newInstance(@NonNull Context context, @LayoutRes int layoutResID, boolean cancelable,
                                               boolean touchOutsideCancelable, ViewCallBack listener) {
        View view = LayoutInflater.from(context).inflate(layoutResID, null);

        return newInstance(context, view, cancelable, touchOutsideCancelable, listener);
    }

    /**
     * 实例一个对话框
     *
     * @param context
     * @param view                   view on your show
     * @param cancelable             是否可以按返回键取消
     * @param touchOutsideCancelable 是否可以点击外部取消
     * @param listener               回调
     * @return
     */
    public static CustomViewDialog newInstance(@NonNull Context context, View view, boolean cancelable,
                                               boolean touchOutsideCancelable, final ViewCallBack listener) {
        CustomViewDialog dialog = new CustomViewDialog(context, R.style.CustomDialogTheme);
        dialog.setContentView(view);
        try {
            dialog.getWindow().getAttributes().gravity = Gravity.CENTER;
        } catch (Exception e) {
        }
        dialog.setCanceledOnTouchOutside(touchOutsideCancelable);
        dialog.setCancelable(cancelable);
        if (listener != null) {
            listener.onCallBack(view, dialog);
        }
        return dialog;
    }

    @Override
    public void show() {
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        super.show();
//        fullScreenImmersive();
//        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
    }

    private void fullScreenImmersive() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            int uiOptions = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_FULLSCREEN);
            getWindow().getDecorView().setSystemUiVisibility(uiOptions);
        }
    }

    public interface ViewCallBack {
        void onCallBack(View contentView, CustomViewDialog dialog);
    }
}
