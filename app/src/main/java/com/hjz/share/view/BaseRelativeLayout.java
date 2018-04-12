package com.hjz.share.view;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

/**
 * Created by hjz on 17-12-6.
 * for:
 */

public class BaseRelativeLayout extends RelativeLayout {
    public static final String TAG = "BaseRelativeLayout";
    private DisplayMetrics metrics;//We get width and height in pixels here

    public BaseRelativeLayout(Context context) {
        super(context);
        init();
    }

    public BaseRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BaseRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public BaseRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        metrics = getContext().getResources().getDisplayMetrics();
        down_line = metrics.heightPixels / 8 * 5;
        middle_line = metrics.heightPixels / 5 * 2;
        up_line = metrics.heightPixels / 15 * 4;
        top_line = (int) (12 * metrics.density);
    }
    private int down_line;
    private int middle_line;
    private int up_line;
    private int top_line;
    private FullScreen fragment;

    private boolean fullScreen = false;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.d(TAG, "onTouchEvent ACTION_DOWN");
                break;
            case MotionEvent.ACTION_MOVE:
                if (!fullScreen) {
                    float dy = event.getRawY() - lastY;
                    setY(getY() + dy);
                    Log.d(TAG, "onTouchEvent ACTION_MOVE,dy=" + dy);
                    //不要让Y有小于top_line的机会
                    if (getY() < top_line) {
                        setY(top_line);
                    }
                    //不要让Y有小于down_line的机会
                    if (getY() > down_line) {
                        setY(down_line);
                    }
                    invalidate();
                    lastX = event.getRawX();
                    lastY = event.getRawY();
                }
                break;
            case MotionEvent.ACTION_UP:
                Log.d(TAG, "onTouchEvent ACTION_UP");
                if (up_line < getY() && getY() <= down_line) {
                    ObjectAnimator animator = ObjectAnimator.ofFloat(this, "y", middle_line);
                    animator.setDuration(300);
                    animator.start();
                }
                if (getY() < up_line) {
                    fullScreen = true;
                    ObjectAnimator animator = ObjectAnimator.ofFloat(this, "y", top_line);
                    animator.setDuration(500);
                    animator.addUpdateListener(animation -> {
                        if (animation.getAnimatedFraction() == 1) {
                            if (fragment != null) {
                                fragment.enterFull();
                            }
                        }
                    });
                    animator.start();

                }
                interceptTouchEvent = false;
                break;
        }
        return true;
    }
    private float lastX;
    private float lastY;
    private boolean interceptTouchEvent = false;

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                lastX = event.getRawX();
                lastY = event.getRawY();
                Log.w(TAG, "dispatchTouchEvent ACTION_DOWN,interceptTouchEvent=" + interceptTouchEvent);
                break;
            case MotionEvent.ACTION_MOVE:
                if (!fullScreen) {
                    int dx = (int) (event.getRawX() - lastX);
                    int dy = (int) (event.getRawY() - lastY);
                    Log.w(TAG, "dispatchTouchEvent ACTION_MOVE,dx=" + dx + ",dy=" + dy);

                    if (Math.abs(dy) < 2 && Math.abs(dx) < 2) {
                        //click
                        Log.w(TAG, "dispatchTouchEvent 不拦截");
                        interceptTouchEvent = false;
                    } else {
                        Log.w(TAG, "dispatchTouchEvent 拦截");
                        interceptTouchEvent = true;
                    }
                }
                break;
        }
        return super.dispatchTouchEvent(event);
    }
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return interceptTouchEvent ? true : super.onInterceptTouchEvent(ev);
    }

    public boolean isFullScreen() {
        return fullScreen;
    }

    public void setFullScreen(boolean fullScreen) {
        this.fullScreen = fullScreen;
    }
    public void setFragment(FullScreen fragment) {
        this.fragment = fragment;
    }
}

