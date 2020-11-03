package com.libs.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.libs.utils.R;


/**
 * 自定义的双目/三目开关按钮
 *
 * @author Created by qinlang on 2015/11/8.
 */
public class SwitchView extends LinearLayout implements OnClickListener {

    private static final int FLAG_MOVE_TRUE = 1; // 向左滑动标识
    private static final int FLAG_MOVE_FALSE = 2; // 向右滑动标识

    private static final int HANDLE_LAYOUT_CURSOR = 100; // 处理调用开关的layout方法

    private Context context; // 上下文对象
    private RelativeLayout sv_container; // SwitchView的外层Layout
    private ImageButton iv_switch_cursor; // 开关邮标的ImageView
    private TextView switch_text_true; // true的文字信息控件
    private TextView switch_text_false; // false的文字信息控件

    private String textTrue;
    private String textFalse;

    private boolean isChecked = true; // 是否已开
    private boolean checkedChange = false; // isChecked是否有改变
    private OnCheckedChangeListener onCheckedChangeListener; // 用于监听isChecked是否有改变

    private int margin = 0; // 游标离边缘位置(这个值视图片而定, 主要是为了图片能显示正确)
    private int bg_left; // 背景左
    private int bg_right; // 背景右
    private int cursor_left; // 游标左部
    private int cursor_top; // 游标顶部
    private int cursor_right; // 游标右部
    private int cursor_bottom; // 游标底部

    private Animation animation; // 移动动画
    private int currentFlag = FLAG_MOVE_TRUE; // 当前移动方向flag
    private int textSize;

    public String getTextTrue() {
        return textTrue;
    }

    public void setTextTrue(String textTrue) {
        this.textTrue = textTrue;
        switch_text_true.setText(textTrue);
    }

    public String getTextFalse() {
        return textFalse;
    }

    public void setTextFalse(String textFalse) {
        this.textFalse = textFalse;
        switch_text_false.setText(textFalse);
    }

    public void setTextSize(int textSize) {
        this.textSize = textSize;
        switch_text_true.setTextSize(px2sp(context, textSize));
        switch_text_false.setTextSize(px2sp(context, textSize));
    }

    public SwitchView(Context context) {
        super(context);
        this.context = context;
        initView();
    }

    public SwitchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initView();

        //设置滑动开关的显示文本
        TypedArray styledAttrs = context.obtainStyledAttributes(attrs, R.styleable.SwitchView);//TypedArray是一个数组容器
        this.setTextTrue(styledAttrs.getString(R.styleable.SwitchView_textTrue));
        this.setTextFalse(styledAttrs.getString(R.styleable.SwitchView_textFalse));

        //	int c_h=(int) styledAttrs.getDimension(R.styleable.SwitchView_container_Hight, 38);

        int c_w = (int) styledAttrs.getDimension(R.styleable.SwitchView_container_width, LayoutParams.FILL_PARENT);
        int iv_h = (int) styledAttrs.getDimension(R.styleable.SwitchView_cursor_hight, 36);
        int iv_w = (int) styledAttrs.getDimension(R.styleable.SwitchView_cursor_width, 120);
        int c_h = (int) styledAttrs.getDimension(R.styleable.SwitchView_container_hight, 38);

        textSize = (int) styledAttrs.getDimension(R.styleable.SwitchView_textSize, 16);
        setTextSize(textSize);

        //更改布局大小，用setLayoutParams报错
        sv_container.getLayoutParams().height = c_h;
        sv_container.getLayoutParams().width = c_w;
        Drawable drawable1 = styledAttrs.getDrawable(R.styleable.SwitchView_container_background);
        if (drawable1 != null) {
            sv_container.setBackgroundDrawable(drawable1);

        }
        sv_container.invalidate();
        iv_switch_cursor.getLayoutParams().height = iv_h;
        if (c_w == -1) {
            iv_w = (getWidth(context)) / 2;
        }
        iv_switch_cursor.getLayoutParams().width = iv_w;
        Drawable drawable2 = styledAttrs.getDrawable(R.styleable.SwitchView_cursor_background);
        if (drawable2 != null) {
            iv_switch_cursor.setBackgroundDrawable(drawable2);
        }
        iv_switch_cursor.invalidate();
        System.out.println("iv_h = " + iv_h);

//		iv_switch_cursor.setLayoutParams(new LayoutParams(iv_w,iv_h));
    }

    public void setContainerWidth(int width) {
        sv_container.getLayoutParams().width = width;
        iv_switch_cursor.getLayoutParams().width = width / 2;
    }

    private int getWidth(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        return wm.getDefaultDisplay().getWidth();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (changed) {
            super.onLayout(changed, l, t, r, b);
            // 获取所需要的值
            bg_left = sv_container.getLeft();
            bg_right = sv_container.getRight();
            cursor_left = iv_switch_cursor.getLeft();
            cursor_top = iv_switch_cursor.getTop();
            cursor_right = iv_switch_cursor.getRight();
            cursor_bottom = iv_switch_cursor.getBottom();
        }

    }

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what) {
                case HANDLE_LAYOUT_CURSOR:
                    iv_switch_cursor.layout(cursor_left, cursor_top, cursor_right,
                            cursor_bottom);
                    break;
            }
            return false;
        }
    });


    public void onClick(View v) {
        // 控件点击时触发改变checked值
        if (v == this) {
            changeChecked(!isChecked);
        }
    }

    /**
     * 初始化控件
     */
    private void initView() {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.switch_three_view, this);
        view.setOnClickListener(this);
        sv_container = (RelativeLayout) view.findViewById(R.id.sv_container);
        switch_text_true = (TextView) view.findViewById(R.id.switch_text_true);
        switch_text_false = (TextView) view.findViewById(R.id.switch_text_false);
        changeTextColor();
        iv_switch_cursor = (ImageButton) view.findViewById(R.id.iv_switch_cursor);
        iv_switch_cursor.setClickable(false);
        iv_switch_cursor.setOnTouchListener(new OnTouchListener() {
            int lastX; // 最后的X坐标

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        getParent().requestDisallowInterceptTouchEvent(true);
                        lastX = (int) event.getRawX();

                        cursor_left = v.getLeft();
                        cursor_top = v.getTop();
                        cursor_right = v.getRight();
                        cursor_bottom = v.getBottom();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        getParent().requestDisallowInterceptTouchEvent(true);
                        int dx = (int) event.getRawX() - lastX;

                        cursor_left = v.getLeft() + dx;
                        cursor_right = v.getRight() + dx;

                        // 超出边界处理
                        if (cursor_left <= bg_left + margin) {
                            cursor_left = bg_left + margin;
                            cursor_right = cursor_left + v.getWidth();
                        }
                        if (cursor_right >= bg_right - margin) {
                            cursor_right = bg_right - margin;
                            cursor_left = cursor_right - v.getWidth();
                        }
                        v.layout(cursor_left, cursor_top, cursor_right,
                                cursor_bottom);

                        lastX = (int) event.getRawX();
                        break;
                    case MotionEvent.ACTION_UP:
                        getParent().requestDisallowInterceptTouchEvent(false);
                        calculateIscheck();
                        break;
                }
                return true;
            }
        });
    }

    /**
     * 计算处于true或是false区域, 并做改变处理
     */
    private void calculateIscheck() {
        float center = (float) ((bg_right - bg_left) / 2.0);
        float cursor_center = (float) ((cursor_right - cursor_left) / 2.0);
        if (cursor_left + cursor_center <= center) {
            changeChecked(true);
        } else {
            changeChecked(false);
        }
    }

    /**
     * 改变checked, 根据checked移动游标
     *
     * @param isChecked
     */
    private void changeChecked(boolean isChecked) {

        if (this.isChecked != isChecked) {
            checkedChange = true;
        } else {
            checkedChange = false;
        }
        if (isChecked) {
            currentFlag = FLAG_MOVE_TRUE;
        } else {
            currentFlag = FLAG_MOVE_FALSE;
        }
        cursorMove();
    }

    /**
     * 游标移动
     */
    private void cursorMove() {
        // 这里说明一点, 动画本可设置animation.setFillAfter(true)
        // 令动画进行完后停在最后位置. 但这里使用这样方式的话.
        // 再次拖动图片会出现异常(具体原因我没找到)
        // 所以最后只能使用onAnimationEnd回调方式再layout游标
        animation = null;
        final int toX;
        if (currentFlag == FLAG_MOVE_TRUE) {
            toX = cursor_left - bg_left - margin;
            animation = new TranslateAnimation(0, -toX, 0, 0);
        } else {
            toX = bg_right - margin - cursor_right;
            animation = new TranslateAnimation(0, toX, 0, 0);
        }
        animation.setDuration(100);
        animation.setInterpolator(new LinearInterpolator());
        animation.setAnimationListener(new AnimationListener() {
            public void onAnimationStart(Animation animation) {

            }

            public void onAnimationRepeat(Animation animation) {

            }

            public void onAnimationEnd(Animation animation) {
                // 计算动画完成后游标应在的位置
                if (currentFlag == FLAG_MOVE_TRUE) {
                    cursor_left -= toX;
                    cursor_right = cursor_left + iv_switch_cursor.getWidth();
                } else {
                    cursor_right = bg_right - margin;
                    cursor_left = cursor_right - iv_switch_cursor.getWidth();
                }
                // 这里不能马上layout游标正确位置, 否则会有一点点闪屏
                // 为了美观, 这里迟了一点点调用layout方法, 便不会闪屏
                mHandler.sendEmptyMessageDelayed(HANDLE_LAYOUT_CURSOR, 5);
                // 这里是根据是不是改变了isChecked值进行一些操作
                if (checkedChange) {
                    isChecked = !isChecked;
                    if (onCheckedChangeListener != null) {
                        onCheckedChangeListener.onCheckedChanged(isChecked);
                    }
                    changeTextColor();
                }
            }
        });
        iv_switch_cursor.startAnimation(animation);
    }

    /**
     * 改变字体显示颜色
     */
    private void changeTextColor() {
        if (isChecked) {
            switch_text_true.setTextColor(Color.WHITE);
            switch_text_false.setTextColor(context.getResources().getColor(R.color.lightBlackColor));
        } else {
            switch_text_true.setTextColor(context.getResources().getColor(R.color.lightBlackColor));
            switch_text_false.setTextColor(Color.WHITE);
        }
    }

    private int sp2px(Context context, float spValue) {
        float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    private int px2sp(Context context, float pxValue) {
        float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }


    /**
     * layout游标
     */
    private void layoutCursor() {
        if (isChecked) {
            cursor_left = bg_left + margin;
            cursor_right = bg_left + margin + iv_switch_cursor.getWidth();
        } else {
            cursor_left = bg_right - margin - iv_switch_cursor.getWidth();
            cursor_right = bg_right - margin;
        }
        iv_switch_cursor.layout(cursor_left, cursor_top, cursor_right,
                cursor_bottom);
        changeTextColor();
        postInvalidate();
    }


    /**
     * isChecked值改变监听器
     */
    public interface OnCheckedChangeListener {
        void onCheckedChanged(boolean isChecked);
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean isChecked) {
        if (this.isChecked != isChecked) {
            this.isChecked = isChecked;
            changeChecked(isChecked);
            changeTextColor();
//			layoutCursor();

			/*if (onCheckedChangeListener != null) {
                onCheckedChangeListener.onCheckedChanged(isChecked);
			}*/
//			layoutCursor();
        }
    }

    public void setOnCheckedChangeListener(OnCheckedChangeListener onCheckedChangeListener) {
        this.onCheckedChangeListener = onCheckedChangeListener;
    }

}
