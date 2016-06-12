package com.showjoy.tashow;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by mac on 16/5/9.
 */
public abstract  class ActionBarActivity extends BaseActionBarActivity{
    public int NETWORK_SUCCESS=1;
    private RelativeLayout actionBarRl;
    private TextView titleTxt;
    private LinearLayout backLl;

    @Override
    protected void initActionBar() {
        this.actionBarRl = (RelativeLayout) findViewById(R.id.rl_actionbar);
        this.titleTxt = (TextView) findViewById(R.id.txt_title);
        this.backLl = (LinearLayout) findViewById(R.id.ll_back);
        backLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    protected void setTitle(String title){
        titleTxt.setText(title);
    }

    /**
     * 隐藏头部控件
     */
    protected void hideActionBar(){
        this.actionBarRl.setVisibility(View.GONE);
    }
}
