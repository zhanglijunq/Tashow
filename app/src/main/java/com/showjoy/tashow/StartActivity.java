package com.showjoy.tashow;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;

import com.showjoy.tashow.base.TaShowGlobal;
import com.showjoy.tashow.data.LoginData;
import com.showjoy.tashow.utils.PreferencesUtils;
import com.showjoy.tashow.utils.UIUtils;

/**
 * Created by mac on 16/5/4.
 */
public class StartActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        String userId = PreferencesUtils.getString(TaShowGlobal.appContext, LoginData.USER_ID);
        if (!TextUtils.isEmpty(userId)){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    UIUtils.gotoActivity(TaShowGlobal.appContext,MainActivity.class);
                    finish();
                }
            },2000);
        }else {
            UIUtils.gotoActivity(TaShowGlobal.appContext,LoginActivity.class);
            finish();
        }
    }
}
