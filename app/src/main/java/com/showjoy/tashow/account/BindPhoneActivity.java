package com.showjoy.tashow.account;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.showjoy.tashow.ActionBarActivity;
import com.showjoy.tashow.R;
import com.showjoy.tashow.utils.AnimationUtil;

/**
 * 绑定手机
 * Created by mac on 16/5/9.
 */
public class BindPhoneActivity extends ActionBarActivity {

    private static final int PHONE_LENGTH = 11;

    private ImageView backImg,nextImg;

    private RelativeLayout nextRl;

    private EditText inputPhoneEt,inputCodeEt;

    private LinearLayout inputPhoneLl, inputCodeLl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind_phone);
        initView();
        initButtonEvent();
    }

    @Override
    protected void initView() {
        backImg = (ImageView) findViewById(R.id.img_back);
        nextImg = (ImageView) findViewById(R.id.img_next);
        inputPhoneEt = (EditText) findViewById(R.id.et_input_phone);
        inputCodeEt = (EditText) findViewById(R.id.et_input_code);
        inputPhoneLl = (LinearLayout) findViewById(R.id.ll_input_phone);
        inputCodeLl = (LinearLayout) findViewById(R.id.ll_input_code);
        nextRl = (RelativeLayout) findViewById(R.id.rl_next);
        nextRl.setEnabled(false);
    }

    @Override
    protected void initButtonEvent() {
        hideActionBar();
        inputPhoneEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (PHONE_LENGTH == s.length()) {
                    setNext(true);
                } else {
                    setNext(false);
                }
            }
        });
        inputCodeEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length()>0){
                    setNext(true);
                }else {
                    setNext(false);
                }
            }
        });

        backImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (View.VISIBLE == inputPhoneLl.getVisibility()){
                    finish();
                }else {
                    enableSlideLayout(true);
                    inputCodeEt.setText("");
                    inputCodeLl.setVisibility(View.GONE);
                    inputPhoneLl.setVisibility(View.VISIBLE);
                    setNext(true);
                }
            }
        });

        nextRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkPhone(inputPhoneEt.getText().toString())) {
                    setNext(false);
                    enableSlideLayout(false);
                    inputPhoneLl.setVisibility(View.GONE);
                    inputCodeLl.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void setNext(boolean b) {
        if (b){
            nextRl.setEnabled(true);
            nextImg.setBackgroundResource(R.drawable.icon_next_white);
        }else {
            nextRl.setEnabled(false);
            nextImg.setBackgroundResource(R.drawable.icon_next_grey);
        }
    }

    private boolean checkPhone(String s) {
        return true;
    }
}
