package com.showjoy.tashow.person;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.showjoy.tashow.ActionBarActivity;
import com.showjoy.tashow.LoginActivity;
import com.showjoy.tashow.MainActivity;
import com.showjoy.tashow.R;
import com.showjoy.tashow.TaShowApplication;
import com.showjoy.tashow.base.TaShowGlobal;
import com.showjoy.tashow.data.LoginData;
import com.showjoy.tashow.utils.PreferencesUtils;
import com.showjoy.tashow.utils.UIUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * 关注的人
 * Created by mac on 16/5/9.
 */
public class SettingActivity extends ActionBarActivity{

    private TextView exitTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        setContentView(R.layout.activity_setting);
        setTitle("设置");
        initView();
        initButtonEvent();
        initData();
    }


    protected  void initView() {
        exitTxt = (TextView) findViewById(R.id.txt_exit);
    }

    protected void initButtonEvent() {
        exitTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PreferencesUtils.putString(TaShowGlobal.appContext, LoginData.USER_ID, "");
                PreferencesUtils.putString(TaShowGlobal.appContext, LoginData.TEL, "");
                PreferencesUtils.putString(TaShowGlobal.appContext, LoginData.USER_NAME, "");
                EventBus.getDefault().post(new SettingEvent(true));
                UIUtils.gotoActivity(TaShowGlobal.appContext, LoginActivity.class);
                finish();
            }
        });
    }

    private void initData(){

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onEventMainThread(SettingEvent event){

    }
}
