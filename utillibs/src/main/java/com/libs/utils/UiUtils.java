package com.libs.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;

public class UiUtils {
    private static Toast toast;

    public static void showToast(final Context context, final String message) {
        showToast(context, message, false);
    }

    public static void showToast(final Context context, final String message, final boolean center) {
        if (toast == null) {
            toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        } else {
            toast.setText(message);
        }
        if (center) {
            toast.setGravity(Gravity.CENTER, 0, 0);
        }
        toast.show();
    }

    private static long lastViewClickTime = 0L;
    private static int lastViewId = 0;

    /**
     * 针对某个控件判断是否存在快速点击
     *
     * @param clickDurationTime 间隔时间
     * @param viewId            点击 view id
     */
    public static boolean isFastDoubleClick(long clickDurationTime, int viewId) {
        long now = System.currentTimeMillis();
        if (lastViewId == viewId) {
            long timeD = now - lastViewClickTime;
            if (0 < timeD && timeD < clickDurationTime) {
                return true;
            }
        } else {
            lastViewId = viewId;
        }
        lastViewClickTime = now;
        return false;
    }


    /**
     * 获取状态栏高度
     */
    private static int statusBarHeight = -1;

    public static int getStatusBarHeight(@NonNull Context context) {
        if (statusBarHeight != -1) {
            return statusBarHeight;
        }
        // 获得状态栏高度
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        statusBarHeight = context.getResources().getDimensionPixelSize(resourceId);
        return statusBarHeight;
    }

    /**
     * 设置状态栏颜色和高度
     *
     * @param fakeStatusBar 伪造状态栏
     * @param color         状态栏颜色值
     * @param alpha         状态栏透明度，此透明度并非颜色中的透明度
     */
    public static void setStatusBarColorAndHeight(
            @NonNull final View fakeStatusBar,
            @ColorInt final int color,
            @IntRange(from = 0, to = 255) final int alpha
    ) {
        Context context = fakeStatusBar.getContext();
        if (context instanceof Activity) {
            transparentStatusBar((Activity) context);
        }
        ViewGroup.LayoutParams layoutParams = fakeStatusBar.getLayoutParams();
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        layoutParams.height = getStatusBarHeight(context);
        fakeStatusBar.setBackgroundColor(getStatusBarColor(color, alpha));
    }

    public static void transparentStatusBar(final Activity activity) {
        Window window = activity.getWindow();
        int option = View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        window.getDecorView().setSystemUiVisibility(option);
        window.setStatusBarColor(Color.TRANSPARENT);
    }

    private static int getStatusBarColor(final int color, final int alpha) {
        if (alpha == 0) {
            return color;
        }
        float a = 1 - alpha / 255f;
        int red = (color >> 16) & 0xff;
        int green = (color >> 8) & 0xff;
        int blue = color & 0xff;
        red = (int) (red * a + 0.5);
        green = (int) (green * a + 0.5);
        blue = (int) (blue * a + 0.5);
        return Color.argb(255, red, green, blue);
    }

    public static int getScreenWidth(Activity activity) {
        DisplayMetrics outMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getRealMetrics(outMetrics);
        return outMetrics.widthPixels;
    }

    public static int getScreenHeight(Activity activity) {
        DisplayMetrics outMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getRealMetrics(outMetrics);
        return outMetrics.heightPixels;
    }

    public static int dip2px(Context context, float dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

}
