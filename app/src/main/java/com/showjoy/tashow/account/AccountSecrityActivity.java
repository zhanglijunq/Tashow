package com.showjoy.tashow.account;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.showjoy.tashow.ActionBarActivity;
import com.showjoy.tashow.R;

/**
 * Created by mac on 16/5/9.
 */
public class AccountSecrityActivity extends ActionBarActivity {

    private TextView changePhoneTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_security);
        setTitle("绑定手机号");
        initView();
        initButtonEvent();
    }

    @Override
    protected void initView() {
        changePhoneTxt = (TextView) findViewById(R.id.txt_change_phone);
    }

    @Override
    protected void initButtonEvent() {
        changePhoneTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AccountSecrityActivity.this,BindPhoneActivity.class);
                startActivity(intent);
            }
        });
    }
}
