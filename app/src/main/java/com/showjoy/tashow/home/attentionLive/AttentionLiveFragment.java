package com.showjoy.tashow.home.attentionLive;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.showjoy.tashow.R;
import com.showjoy.tashow.base.PortUtils;
import com.showjoy.tashow.base.TaShowGlobal;
import com.showjoy.tashow.data.LoginData;
import com.showjoy.tashow.data.RoomData;
import com.showjoy.tashow.home.hotlive.HotRecyclerViewAdapter;
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

import java.util.List;

@SuppressLint("ValidFragment")
public class AttentionLiveFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener,MyItemClickListener{

    private int NETWORK_SUCCESS = 1;

    private TextView noAttentionTxt;

    private TabLayout tabLayout;

    private RecyclerView mRecyclerView;

    private SwipeRefreshLayout swipeRefreshLayout;

    private String userId = PreferencesUtils.getString(TaShowGlobal.appContext, LoginData.USER_ID);

    private int page=1;

    private AttentionRecyclerViewAdapter adapter;

    private LinearLayoutManager lm;
    private int lastVisibleItem = 0;


    public AttentionLiveFragment(TabLayout tabLayout) {
        this.tabLayout = tabLayout;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_attention_live, container, false);
        noAttentionTxt = (TextView) view.findViewById(R.id.txt_no_attention);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        String noAttentionStr = "您还未关注任何好友\n先去看看其他精彩直播吧~";
        int index = noAttentionStr.indexOf("其");
        SpannableString spanString = new SpannableString(noAttentionStr);
        ForegroundColorSpan span = new ForegroundColorSpan(getResources().getColor(R.color.colorBind));
        spanString.setSpan(span, index, noAttentionStr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        noAttentionTxt.append(spanString);
        initView();
        return view;
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
        adapter = new AttentionRecyclerViewAdapter(getActivity(), null);
        mRecyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(this);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getAtRooms();
        initEvent();
    }

    private void getAtRooms() {
        OkHttpClientManager.postAsyn(PortUtils.followedRoom, PortUtils.getFollowList(userId, String.valueOf(page)), new OkHttpClientManager.ResultCallback<String>() {
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
                            if (jsonObject.has("data")){
                                noAttentionTxt.setVisibility(View.GONE);
                                RoomData[] roomDatas = new Gson().fromJson(jsonObject.getString("data"), RoomData[].class);
                                if (roomDatas != null) {
                                    creatRoomView(ArrayUtils.arrayToList(roomDatas));
                                }
                            }else {
                                if (page==1){
                                    noAttentionTxt.setVisibility(View.VISIBLE);
                                    mRecyclerView.setVisibility(View.GONE);
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

    private void creatRoomView(List<RoomData> roomDatas) {
        if (adapter==null || 1==page){
            adapter = new AttentionRecyclerViewAdapter(getActivity(), roomDatas);
            mRecyclerView.setAdapter(adapter);
        }else {
            adapter.setData(roomDatas);
            adapter.notifyDataSetChanged();
        }
        adapter.setOnItemClickListener(this);
    }

    private void initEvent() {
        noAttentionTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TabLayout.Tab tab = tabLayout.getTabAt(0);
                tab.select();
            }
        });
        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem + 1 == 10) {
                    page++;
                    swipeRefreshLayout.setRefreshing(true);
                    getAtRooms();
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
                getAtRooms();
            }
        },1000);
    }

    Handler handler = new Handler();
    @Override
    public void onItemClick(View view, int position) {
        RoomData roomData = adapter.getRoomDataList().get(position+1);
        Bundle bundle = new Bundle();
        bundle.putByteArray("roomData", SerializeToFlatByte.serializeToByte(roomData));
        UIUtils.gotoActivityWithBundle(getActivity(), WatchLiveActivity.class, bundle);
    }
}
