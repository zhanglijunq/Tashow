package com.showjoy.tashow.person;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.showjoy.tashow.ActionBarActivity;
import com.showjoy.tashow.R;
import com.showjoy.tashow.base.PortUtils;
import com.showjoy.tashow.base.TaShowGlobal;
import com.showjoy.tashow.data.BaseResponse;
import com.showjoy.tashow.data.LoginData;
import com.showjoy.tashow.data.UserDTO;
import com.showjoy.tashow.listener.FollowItemClickListener;
import com.showjoy.tashow.okhttp.OkHttpClientManager;
import com.showjoy.tashow.utils.PreferencesUtils;
import com.showjoy.tashow.view.SwipeListView;
import com.squareup.okhttp.Request;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 关注的人
 * Created by mac on 16/5/9.
 */
public class FollowActivity extends ActionBarActivity{
    private SwipeListView swipeListView;

    private int page=1;

    private String userId;

    private TextView noAttentionTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        userId = PreferencesUtils.getString(TaShowGlobal.appContext, LoginData.USER_ID);
        setContentView(R.layout.activity_follow);
        setTitle("关注的人");
        initView();
        initButtonEvent();
        initData();
    }

    @Override
    protected void initView() {
        noAttentionTxt = (TextView) findViewById(R.id.txt_no_attention);
        swipeListView = (SwipeListView) findViewById(R.id.swipeListView);
        enableSlideLayout(false);
    }

    @Override
    protected void initButtonEvent() {

    }

    private void initData(){
        OkHttpClientManager.postAsyn(PortUtils.followList, PortUtils.getFollowList(userId, String.valueOf(page)), new OkHttpClientManager.ResultCallback<String>() {
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
                            UserDTO[] userDTOs = new Gson().fromJson(jsonObject.getString("data"),UserDTO[].class);
                            if (userDTOs!=null){
                                swipeListView.setAdapter(new FollowAdapter(TaShowGlobal.appContext,userDTOs,true, new FollowItemClickListener() {
                                    @Override
                                    public void onItemClick(View view, Object object) {
                                        UserDTO userDTO = (UserDTO) object;
                                        if (userDTO!=null){
                                            cancelFollow(userDTO.id);
                                        }
                                    }
                                }));
                            }else {
                                noAttentionTxt.setVisibility(View.VISIBLE);
                                swipeListView.setVisibility(View.GONE);
                            }
                        }else {
                            noAttentionTxt.setVisibility(View.VISIBLE);
                            swipeListView.setVisibility(View.GONE);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        });
    }

    private void cancelFollow(String followId) {
        OkHttpClientManager.postAsyn(PortUtils.unFollow, PortUtils.getUnFollow(userId, followId), new OkHttpClientManager.ResultCallback<BaseResponse>() {
            @Override
            public void onError(Request request, Exception e) {
                Toast.makeText(TaShowGlobal.appContext, e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(BaseResponse response) {
                if (response!=null && NETWORK_SUCCESS == response.isSuccess){
                    EventBus.getDefault().post(new UnFollowSuccess(true));
                    if (!TextUtils.isEmpty(response.msg)){
                        Toast.makeText(TaShowGlobal.appContext,response.msg,Toast.LENGTH_SHORT).show();
                    }
                    initData();
                }

            }
        });
    }

    @Subscribe
    public void onEventMainThread(SettingEvent event){

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
