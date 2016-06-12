package com.showjoy.tashow.home.newestlive;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.Gson;
import com.showjoy.tashow.R;
import com.showjoy.tashow.base.PortUtils;
import com.showjoy.tashow.base.TaShowGlobal;
import com.showjoy.tashow.data.RoomData;
import com.showjoy.tashow.home.hotlive.HotRecyclerViewAdapter;
import com.showjoy.tashow.listener.MyItemClickListener;
import com.showjoy.tashow.localLive.WatchLiveActivity;
import com.showjoy.tashow.okhttp.OkHttpClientManager;
import com.showjoy.tashow.utils.ArrayUtils;
import com.showjoy.tashow.utils.SerializeToFlatByte;
import com.showjoy.tashow.utils.UIUtils;
import com.squareup.okhttp.Request;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class NewestLiveFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, MyItemClickListener{
    private int NETWORK_SUCCESS = 1;

    private RecyclerView mRecyclerView;

    private SwipeRefreshLayout swipeRefreshLayout;


    private NewestRecyclerViewAdapter adapter;

    private GridLayoutManager gl;

    private int lastVisibleItem = 0;

    private int page = 1;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_newest_live, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        initEvent();
        getNewestRooms();
    }

    private void initView() {
        swipeRefreshLayout.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_orange_light,
                android.R.color.holo_green_light);
        swipeRefreshLayout.setOnRefreshListener(this);
        // 这句话是为了，第一次进入页面的时候显示加载进度条
        swipeRefreshLayout.setProgressViewOffset(true, 0, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics()));
    }

    private void initEvent() {
        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                swipeRefreshLayout.setRefreshing(true);
//                if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem == adapter.getItemCount()) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        page++;
                        getNewestRooms();
                    }
                }, 2000);
//                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastVisibleItem = gl.findLastVisibleItemPosition();
            }

        });

        mRecyclerView.setHasFixedSize(true);
        gl = new GridLayoutManager(getActivity(),3);
        mRecyclerView.setLayoutManager(gl);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    private void getNewestRooms() {
        OkHttpClientManager.postAsyn(PortUtils.newestRoom, PortUtils.getNewestRooms(String.valueOf(page)), new OkHttpClientManager.ResultCallback<String>() {
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
                                List<RoomData> roomDataArrayList = ArrayUtils.arrayToList(roomDatas);
                                if (adapter==null || page==1){
                                    adapter = new NewestRecyclerViewAdapter(roomDataArrayList);
                                    mRecyclerView.setAdapter(adapter);
                                    adapter.setOnItemClickListener(NewestLiveFragment.this);
                                }else {
                                    adapter.setDatas(roomDatas);
                                    adapter.notifyDataSetChanged();
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

    @Override
    public void onRefresh() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(false);
                page=1;
                getNewestRooms();
            }
        },1000);
    }

    Handler handler = new Handler();

    @Override
    public void onItemClick(View view, int position) {
        RoomData roomData = adapter.getRoomDataList().get(position);
        Bundle bundle = new Bundle();
        bundle.putByteArray("roomData", SerializeToFlatByte.serializeToByte(roomData));
        UIUtils.gotoActivityWithBundle(getActivity(), WatchLiveActivity.class, bundle);
    }
}
