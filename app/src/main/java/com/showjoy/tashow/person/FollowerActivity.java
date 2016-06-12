package com.showjoy.tashow.person;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
 * 绑定手机
 * Created by mac on 16/5/9.
 */
public class FollowerActivity extends ActionBarActivity {

    private SwipeListView swipeListView;

    private int page=1;

    private String userId;

    private FollowAdapter followAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        userId = PreferencesUtils.getString(TaShowGlobal.appContext, LoginData.USER_ID);
        setContentView(R.layout.activity_follow);
        setTitle("粉丝");
        initView();
        initButtonEvent();
        initData();
    }

    @Override
    protected void initView() {
        swipeListView = (SwipeListView) findViewById(R.id.swipeListView);
        enableSlideLayout(false);
    }

    @Override
    protected void initButtonEvent() {

    }

    private void initData(){
        OkHttpClientManager.postAsyn(PortUtils.followerList, PortUtils.getFollowList(userId, String.valueOf(page)), new OkHttpClientManager.ResultCallback<String>() {
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
                            UserDTO[] userDTOs = new Gson().fromJson(jsonObject.getString("data"), UserDTO[].class);
                            if (userDTOs != null) {
                                followAdapter = new FollowAdapter(TaShowGlobal.appContext, userDTOs, false, new FollowItemClickListener() {
                                    @Override
                                    public void onItemClick(View view, Object object) {
                                        UserDTO userDTO = (UserDTO) object;
                                        if (userDTO != null && !userDTO.isFollow) {
                                            userLike(userDTO.id);
                                        }
                                    }
                                });
                                swipeListView.setAdapter(followAdapter);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        });
    }

    /**
     * 用户关注
     * @param followId
     */
    private void userLike(String followId) {
        OkHttpClientManager.postAsyn(PortUtils.follow, PortUtils.follow(userId, followId), new OkHttpClientManager.ResultCallback<BaseResponse>() {
            @Override
            public void onError(Request request, Exception e) {
                Toast.makeText(TaShowGlobal.appContext, e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(BaseResponse baseResponse) {
                if (baseResponse != null && NETWORK_SUCCESS == baseResponse.isSuccess) {
                    EventBus.getDefault().post(new UnFollowSuccess(true));
                    initData();
                    if (!TextUtils.isEmpty(baseResponse.msg)) {
                        Toast.makeText(TaShowGlobal.appContext, baseResponse.msg, Toast.LENGTH_SHORT).show();
                    }
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
