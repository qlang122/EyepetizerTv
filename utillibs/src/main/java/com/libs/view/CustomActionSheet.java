/*
 * The MIT License (MIT)
 * 
 * Copyright (c) 2015 baoyongzhang <baoyz94@gmail.com>
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.libs.view;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.LayoutRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.Gravity;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.libs.utils.R;


public class CustomActionSheet extends Fragment implements View.OnClickListener {

    private static final String ARG_CANCELABLE_ONTOUCHOUTSIDE = "cancelable_ontouchoutside";
    private static final int TRANSLATE_DURATION = 200;
    private static final int ALPHA_DURATION = 300;

    private static final String EXTRA_DISMISSED = "extra_dismissed";

    private boolean mDismissed = true;
    private ActionSheetListener mListener;
    private View mView;
    private LinearLayout mPanel;
    private ViewGroup mGroup;
    private View mBg;
    private boolean isCancel = true;
    private Builder mBuilder;

    private ViewCallBack tListener;
    private View contentView;
    private int layoutResID;

    public void show(final FragmentManager manager, final String tag) {
        if (!mDismissed || manager.isDestroyed()) {
            return;
        }
        mDismissed = false;
        new Handler().post(new Runnable() {
            public void run() {
                FragmentTransaction ft = manager.beginTransaction();
                ft.add(CustomActionSheet.this, tag);
                ft.addToBackStack(null);
                ft.commitAllowingStateLoss();
            }
        });
    }

    public void dismiss() {
        if (mDismissed) return;

        mDismissed = true;
        new Handler().post(new Runnable() {
            public void run() {
                getFragmentManager().popBackStack();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.remove(CustomActionSheet.this);
                ft.commitAllowingStateLoss();
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(EXTRA_DISMISSED, mDismissed);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mDismissed = savedInstanceState.getBoolean(EXTRA_DISMISSED);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive()) {
            View focusView = getActivity().getCurrentFocus();
            if (focusView != null) {
                imm.hideSoftInputFromWindow(focusView.getWindowToken(), 0);
            }
        }

        mView = createView();
        mGroup = (ViewGroup) getActivity().getWindow().getDecorView();

        mGroup.addView(mView);
        mBg.startAnimation(createAlphaInAnimation());
        mPanel.startAnimation(createTranslationInAnimation());
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        mPanel.startAnimation(createTranslationOutAnimation());
        mBg.startAnimation(createAlphaOutAnimation());
        mView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mGroup.removeView(mView);
            }
        }, ALPHA_DURATION);
        if (mListener != null) {
            mListener.onDismiss(this, isCancel);
        }
        super.onDestroyView();
    }

    private Animation createTranslationInAnimation() {
        int type = TranslateAnimation.RELATIVE_TO_SELF;
        TranslateAnimation an = new TranslateAnimation(type, 0, type, 0, type, 1, type, 0);
        an.setDuration(TRANSLATE_DURATION);
        return an;
    }

    private Animation createAlphaInAnimation() {
        AlphaAnimation an = new AlphaAnimation(0, 1);
        an.setDuration(ALPHA_DURATION);
        return an;
    }

    private Animation createTranslationOutAnimation() {
        int type = TranslateAnimation.RELATIVE_TO_SELF;
        TranslateAnimation an = new TranslateAnimation(type, 0, type, 0, type, 0, type, 1);
        an.setDuration(TRANSLATE_DURATION);
        an.setFillAfter(true);
        return an;
    }

    private Animation createAlphaOutAnimation() {
        AlphaAnimation an = new AlphaAnimation(1, 0);
        an.setDuration(ALPHA_DURATION);
        an.setFillAfter(true);
        return an;
    }

    private View createView() {
        FrameLayout parent = new FrameLayout(getActivity());
        parent.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        mBg = new View(getActivity());
        mBg.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        mBg.setBackgroundColor(Color.TRANSPARENT);
        mBg.setId(R.id.as_bg_view_id);
        mBg.setOnClickListener(this);

        mPanel = new LinearLayout(getActivity());
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.BOTTOM;
        mPanel.setLayoutParams(params);
        mPanel.setOrientation(LinearLayout.VERTICAL);
        if (layoutResID > 0) {
            setContentView(LayoutInflater.from(getActivity()).inflate(layoutResID, mPanel, false));
        }
        if (getContentView() != null) {
            View contentView = getContentView();
            mPanel.addView(contentView);
            if (tListener != null) {
                tListener.onCallBack(contentView);
            }
        }
        parent.setPadding(0, 0, 0, getNavBarHeight(getActivity()));
        parent.addView(mBg);
        parent.addView(mPanel);
        return parent;
    }

    public int getNavBarHeight(Context c) {
        int result = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            boolean hasMenuKey = ViewConfiguration.get(c).hasPermanentMenuKey();
            boolean hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);

            if (!hasMenuKey && !hasBackKey) {
                //The device has a navigation bar
                Resources resources = c.getResources();

                int orientation = getResources().getConfiguration().orientation;
                int resourceId;
                if (isTablet(c)) {
                    resourceId = resources.getIdentifier(orientation == Configuration.ORIENTATION_PORTRAIT
                            ? "navigation_bar_height" : "navigation_bar_height_landscape", "dimen", "android");
                } else {
                    resourceId = resources.getIdentifier(orientation == Configuration.ORIENTATION_PORTRAIT
                            ? "navigation_bar_height" : "navigation_bar_width", "dimen", "android");
                }

                if (resourceId > 0) {
                    return getResources().getDimensionPixelSize(resourceId);
                }
            }
        }
        return result;
    }

    private boolean isTablet(Context c) {
        return (c.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    public LinearLayout.LayoutParams createLayoutParams() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT);
        return params;
    }

    private boolean getCancelableOnTouchOutside() {
        return getArguments().getBoolean(ARG_CANCELABLE_ONTOUCHOUTSIDE);
    }

    public void setActionSheetListener(ActionSheetListener listener) {
        mListener = listener;
    }

    public View getContentView() {
        return contentView;
    }

    public void setContentView(View contentView) {
        this.contentView = contentView;
    }

    public void setContentView(@LayoutRes int resId) {
        this.layoutResID = resId;
    }

    public void setViewCallBack(ViewCallBack tListener) {
        this.tListener = tListener;
    }

    public boolean isDismissed() {
        return mDismissed;
    }

    @Override
    public void onClick(View v) {
        if (getCancelableOnTouchOutside()) dismiss();
    }

    public static Builder createBuilder(Context context, FragmentManager fragmentManager) {
        return new Builder(context, fragmentManager);
    }

    public static class Builder {
        private Context mContext;
        private FragmentManager mFragmentManager;
        private String mTag = "actionSheet";
        private boolean mCancelableOnTouchOutside;
        private ActionSheetListener mListener;
        private View contentView;
        private ViewCallBack listener;
        private Object obj;
        private int layoutId;

        private CustomActionSheet actionSheet;

        public Builder(Context context, FragmentManager fragmentManager) {
            mContext = context;
            mFragmentManager = fragmentManager;
        }

        public Builder setTag(String tag) {
            mTag = tag;
            return this;
        }

        public Builder setContentView(View view) {
            this.contentView = view;
            return this;
        }

        public Builder setContentView(@LayoutRes int resId) {
            this.layoutId = resId;
            return this;
        }

        public Builder setObject(Object obj) {
            this.obj = obj;
            return this;
        }

        public Object getObject() {
            return obj;
        }

        public Builder setListener(ActionSheetListener listener) {
            this.mListener = listener;
            return this;
        }

        public Builder setViewCallBack(ViewCallBack listener) {
            this.listener = listener;
            return this;
        }

        public Builder setCancelableOnTouchOutside(boolean cancelable) {
            mCancelableOnTouchOutside = cancelable;
            return this;
        }

        public boolean isDismissed() {
            return actionSheet.isDismissed();
        }

        public Bundle prepareArguments() {
            Bundle bundle = new Bundle();
            bundle.putBoolean(ARG_CANCELABLE_ONTOUCHOUTSIDE, mCancelableOnTouchOutside);
            return bundle;
        }

        public CustomActionSheet show() {
            actionSheet = (CustomActionSheet) Fragment.instantiate(mContext,
                    CustomActionSheet.class.getName(), prepareArguments());
            actionSheet.mBuilder = this;
            actionSheet.setActionSheetListener(mListener);
            actionSheet.setContentView(contentView);
            actionSheet.setContentView(layoutId);
            actionSheet.setViewCallBack(listener);
            actionSheet.show(mFragmentManager, mTag);
            return actionSheet;
        }

        public void dismiss() {
            actionSheet.dismiss();
        }
    }

    public Object getObject() {
        if (mBuilder != null) {
            return mBuilder.getObject();
        }
        return null;
    }

    public interface ActionSheetListener {
        void onDismiss(CustomActionSheet actionSheet, boolean isCancel);
    }

    public interface ViewCallBack {
        void onCallBack(View contentView);
    }
}
