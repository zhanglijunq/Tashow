package com.showjoy.tashow.home.hotlive;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.Gson;
import com.showjoy.tashow.LoginActivity;
import com.showjoy.tashow.R;
import com.showjoy.tashow.base.PortUtils;
import com.showjoy.tashow.base.TaShowGlobal;
import com.showjoy.tashow.data.LoginData;
import com.showjoy.tashow.data.LoginResponse;
import com.showjoy.tashow.data.RoomData;
import com.showjoy.tashow.listener.MyItemClickListener;
import com.showjoy.tashow.localLive.WatchLiveActivity;
import com.showjoy.tashow.okhttp.OkHttpClientManager;
import com.showjoy.tashow.utils.ArrayUtils;
import com.showjoy.tashow.utils.PreferencesUtils;
import com.showjoy.tashow.utils.SerializeToFlatByte;
import com.showjoy.tashow.utils.UIUtils;
import com.squareup.okhttp.Request;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HotLiveFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener,MyItemClickListener{

    private int NETWORK_SUCCESS = 1;

    private RecyclerView mRecyclerView;

    private SwipeRefreshLayout swipeRefreshLayout;

    private int count = 0;

    private ArrayList<String> data = new ArrayList<String>();

    private HotRecyclerViewAdapter adapter;

    private LinearLayoutManager lm;

    private int lastVisibleItem = 0;

    private int page=1;

    private String userId = PreferencesUtils.getString(TaShowGlobal.appContext, LoginData.USER_ID);
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        page=1;
        View view = inflater.inflate(R.layout.fragment_hot_live, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        initView();
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initEvent();
        getHotRooms();
        // 此处在现实项目中，请换成网络请求数据代码，sendRequest .....
//        handler.sendEmptyMessageDelayed(0, 3000);
    }

    private void initView() {
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright, android.R.color.holo_orange_light, android.R.color.holo_green_light);
        swipeRefreshLayout.setOnRefreshListener(this);

        // 这句话是为了，第一次进入页面的时候显示加载进度条
        swipeRefreshLayout.setProgressViewOffset(false, 0, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics()));

        mRecyclerView.setHasFixedSize(true);
        lm = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(lm);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter = new HotRecyclerViewAdapter(getActivity(), null);
        mRecyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(this);
    }

    private void getHotRooms() {
        OkHttpClientManager.postAsyn(PortUtils.hotestRoom, PortUtils.getHotestRoom(String.valueOf(page)), new OkHttpClientManager.ResultCallback<String>() {
            @Override
            public void onError(Request request, Exception e) {
                Toast.makeText(TaShowGlobal.appContext, e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(String response) {
                swipeRefreshLayout.setRefreshing(false);
                if (!TextUtils.isEmpty(response)) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.has("isSuccess") && NETWORK_SUCCESS == jsonObject.getInt("isSuccess")) {
                            RoomData[] roomDatas = new Gson().fromJson(jsonObject.getString("data"), RoomData[].class);
                            if (roomDatas != null) {
                                creatRoomView(ArrayUtils.arrayToList(roomDatas));
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        });
    }

    private void creatRoomView(List<RoomData> roomDatas) {
        if (adapter==null || 1==page){
            adapter = new HotRecyclerViewAdapter(getActivity(), roomDatas);
            mRecyclerView.setAdapter(adapter);
        }else {
            adapter.setData(roomDatas);
            adapter.notifyDataSetChanged();
        }
        adapter.setOnItemClickListener(this);
    }

    private void initEvent() {
        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem + 1 == adapter.getItemCount()) {
                    page++;
                    swipeRefreshLayout.setRefreshing(true);
                    getHotRooms();
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastVisibleItem = lm.findLastVisibleItemPosition();
            }

        });
    }


    @Override
    public void onRefresh() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(false);
                page=1;
                getHotRooms();
            }
        },1000);
    }

    Handler handler = new Handler();

    @Override
    public void onItemClick(View view, int position) {
        if (position!=-1){
            RoomData roomData = adapter.getRoomDataList().get(position);
            Bundle bundle = new Bundle();
            bundle.putByteArray("roomData", SerializeToFlatByte.serializeToByte(roomData));
            UIUtils.gotoActivityWithBundle(getActivity(), WatchLiveActivity.class, bundle);
        }
    }
}
