package com.showjoy.tashow;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.showjoy.tashow.base.PortUtils;
import com.showjoy.tashow.base.TaShowGlobal;
import com.showjoy.tashow.data.BaseResponse;
import com.showjoy.tashow.data.LoginData;
import com.showjoy.tashow.data.LoginResponse;
import com.showjoy.tashow.okhttp.OkHttpClientManager;
import com.showjoy.tashow.utils.PreferencesUtils;
import com.showjoy.tashow.utils.RegularUtils;
import com.showjoy.tashow.utils.UIUtils;
import com.showjoy.tashow.utils.WeakHandler;
import com.squareup.okhttp.Request;

import java.util.Timer;
import java.util.TimerTask;
/**
 * Created by mac on 16/5/4.
 */
public class LoginActivity extends Activity {

    private static final int NETWORK_SUCCESS = 1;

    private EditText phoneEt, codeEt;

    private TextView getCodeTxt;

    private Button loginBtn;

    private Context context;

    private Timer timer;

    private int count = 60;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = getApplicationContext();
        setContentView(R.layout.activity_login);
        initView();
        initEvent();
    }

    private void initView() {
        this.phoneEt = (EditText) findViewById(R.id.et_phone);
        this.codeEt = (EditText) findViewById(R.id.et_code);
        this.getCodeTxt = (TextView) findViewById(R.id.txt_get_code);
        this.loginBtn = (Button) findViewById(R.id.btn_login);
    }

    private void initEvent() {
        getCodeTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String tel = phoneEt.getText().toString();
                    if (RegularUtils.checkPhone(tel)) {
                        getCodeTxt.setEnabled(false);
                        accessNetWork(tel);
                    }
                } catch (Exception e) {
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String tel = phoneEt.getText().toString();
                    String code = codeEt.getText().toString();
                    if (RegularUtils.checkPhone(tel)&&RegularUtils.checkCode(code) ){
                        login(tel,code);
                    }
                } catch (Exception e) {
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void accessNetWork(String tel) {
        OkHttpClientManager.postAsyn(PortUtils.registerCaptcha, PortUtils.getCode("tel",tel), new OkHttpClientManager.ResultCallback<BaseResponse>() {
            @Override
            public void onError(Request request, Exception e) {
                Toast.makeText(TaShowGlobal.appContext, e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(BaseResponse response) {
                if (response != null && NETWORK_SUCCESS == response.isSuccess) {
                    Toast.makeText(TaShowGlobal.appContext, response.msg, Toast.LENGTH_SHORT).show();
                    timer = new Timer();
                    timer.schedule(new TimerTask() {

                        @Override
                        public void run() {
                            mHandler.sendEmptyMessage(2);
                        }
                    }, 0, 1000);

                }
            }
        });
    }

    private void login(String tel, String code) {
        OkHttpClientManager.postAsyn(PortUtils.login, PortUtils.login(tel,code), new OkHttpClientManager.ResultCallback<LoginResponse>() {
            @Override
            public void onError(Request request, Exception e) {
                Toast.makeText(TaShowGlobal.appContext, e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(LoginResponse response) {
                Log.d("response",response.toString());
                if (response!=null && NETWORK_SUCCESS == response.isSuccess){
                    LoginData loginData = response.data!=null?response.data:new LoginData();
                    PreferencesUtils.putString(TaShowGlobal.appContext, LoginData.USER_ID, loginData.id);
                    PreferencesUtils.putString(TaShowGlobal.appContext, LoginData.TEL, loginData.tel);
                    PreferencesUtils.putString(TaShowGlobal.appContext, LoginData.USER_NAME, loginData.username);
                    UIUtils.gotoActivity(TaShowGlobal.appContext,MainActivity.class);
                    finish();
                }else {
                    if (!TextUtils.isEmpty(response.msg)){
                        Toast.makeText(TaShowGlobal.appContext,response.msg,Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }

    WeakHandler mHandler = new WeakHandler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 2:
                    if (count > 0) {
                        count--;
                        getCodeTxt.setText(count + "s");
                    } else {
                        count=60;
                        if (timer != null) {
                            timer.cancel();
                        }
                        getCodeTxt.setText("重新获取");
                        getCodeTxt.setEnabled(true);
                    }
                    break;
            }
            return false;
        }
    });
}
