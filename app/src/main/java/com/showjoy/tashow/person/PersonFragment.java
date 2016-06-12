package com.showjoy.tashow.person;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.showjoy.tashow.R;
import com.showjoy.tashow.account.AccountSecrityActivity;
import com.showjoy.tashow.base.PortUtils;
import com.showjoy.tashow.base.TaShowGlobal;
import com.showjoy.tashow.common.Config;
import com.showjoy.tashow.data.LoginData;
import com.showjoy.tashow.data.UserInfoDTO;
import com.showjoy.tashow.localLive.WatchLiveActivity;
import com.showjoy.tashow.okhttp.OkHttpClientManager;
import com.showjoy.tashow.utils.PreferencesUtils;
import com.showjoy.tashow.utils.SerializeToFlatByte;
import com.showjoy.tashow.utils.UIUtils;
import com.squareup.okhttp.Request;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Jay on 2015/8/28 0028.
 */
@SuppressLint("ValidFragment")
public class PersonFragment extends Fragment {

    private RelativeLayout bindPhoneRl,settingRl;

    private String userId;

    private SimpleDraweeView headImg;

    private TextView nameTxt,idTxt,followNumTxt, followerNumTxt;

    private ImageView sexImg,editInformationImg;

    private UserInfoDTO userInfoDTO;

    public PersonFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        userId = PreferencesUtils.getString(TaShowGlobal.appContext, LoginData.USER_ID);
        View view = inflater.inflate(R.layout.fragment_person,container,false);
        bindPhoneRl = (RelativeLayout) view.findViewById(R.id.rl_bind_phone);
        headImg = (SimpleDraweeView) view.findViewById(R.id.img_head);
        nameTxt = (TextView) view.findViewById(R.id.txt_name);
        idTxt = (TextView) view.findViewById(R.id.txt_id);
        followNumTxt = (TextView) view.findViewById(R.id.txt_follow_num);
        followerNumTxt = (TextView) view.findViewById(R.id.txt_follower_num);
        sexImg = (ImageView) view.findViewById(R.id.img_sex);
        editInformationImg = (ImageView) view.findViewById(R.id.img_edit_information);
        settingRl = (RelativeLayout) view.findViewById(R.id.rl_setting);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initEvent();
        getUserInfo();
    }

    private void initEvent() {
        bindPhoneRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AccountSecrityActivity.class);
                startActivity(intent);
            }
        });
        followNumTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIUtils.gotoActivity(TaShowGlobal.appContext, FollowActivity.class);
            }
        });
        followerNumTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIUtils.gotoActivity(TaShowGlobal.appContext, FollowerActivity.class);
            }
        });
        editInformationImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putByteArray("userInfoDTO", SerializeToFlatByte.serializeToByte(userInfoDTO));
                UIUtils.gotoActivityWithBundle(TaShowGlobal.appContext, EditInformationActivity.class, bundle);
            }
        });
        settingRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIUtils.gotoActivity(TaShowGlobal.appContext, SettingActivity.class);
            }
        });
    }

    private void getUserInfo() {
        OkHttpClientManager.postAsyn(PortUtils.getUserInfo, PortUtils.getUserInfo(userId, userId), new OkHttpClientManager.ResultCallback<String>() {
            @Override
            public void onError(Request request, Exception e) {
                Toast.makeText(TaShowGlobal.appContext, e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(String response) {
                if (!TextUtils.isEmpty(response)) {
                    Log.d("response", response);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.has("data")) {
                            userInfoDTO = new Gson().fromJson(jsonObject.getString("data"), UserInfoDTO.class);
                            if (userInfoDTO != null) {
                                headImg.setImageURI(Uri.parse(userInfoDTO.image));
                                idTxt.setText("ID:" + userInfoDTO.userId);
                                nameTxt.setText(!TextUtils.isEmpty(userInfoDTO.username)?userInfoDTO.username:userInfoDTO.userId);
                                followNumTxt.setText("关注:" + userInfoDTO.followNum);
                                followerNumTxt.setText("粉丝:" + userInfoDTO.followerNum);
                                //1.男,2女,3性别保密
                                if (Config.BOY == userInfoDTO.sex) {
                                    sexImg.setBackgroundResource(R.drawable.gender_boy);
                                } else if (Config.GIRL == userInfoDTO.sex) {
                                    sexImg.setBackgroundResource(R.drawable.sex_girl);
                                } else {
                                    sexImg.setBackgroundResource(R.drawable.sex_unknown);
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        });
    }

    @Subscribe
    public void onEventMainThread(UnFollowSuccess event){
        if (event.isUnFollow()){
            getUserInfo();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
