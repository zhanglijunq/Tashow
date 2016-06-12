package com.showjoy.tashow;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.showjoy.tashow.widget.SlideLinearLayout;

/**
 * Created by mac on 16/5/9.
 */
public abstract class BaseActionBarActivity extends Activity{
    private SlideLinearLayout rootSliedLayout;
    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(createRootView(LayoutInflater.from(this).inflate(layoutResID, null)));
        initActionBar();
    }

    protected abstract void initActionBar();

    /**
     * 初始化界面控件
     */
    protected abstract void initView();

    /**
     * 初始化按钮事件
     */
    protected abstract void initButtonEvent();
    /**
     * 创建根View
     *
     * @param view
     * @return
     */
    private SlideLinearLayout createRootView(View view) {
        rootSliedLayout = new SlideLinearLayout(this);
        rootSliedLayout.setBackgroundColor(getResources().getColor(R.color.white));
        rootSliedLayout.setOrientation(LinearLayout.VERTICAL);
        LayoutInflater.from(this).inflate(R.layout.activity_base, rootSliedLayout);
        rootSliedLayout.addView(view, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        return rootSliedLayout;
    }

    /**
     * 设置是否支持滑动返回
     *
     * @param enabled
     */
    protected void enableSlideLayout(boolean enabled) {
        rootSliedLayout.enableSlide(enabled);
    }
}
