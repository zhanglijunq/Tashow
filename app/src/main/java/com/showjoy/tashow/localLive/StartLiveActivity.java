package com.showjoy.tashow.localLive;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.showjoy.tashow.R;
import com.showjoy.tashow.base.PortUtils;
import com.showjoy.tashow.base.TaShowGlobal;
import com.showjoy.tashow.common.Config;
import com.showjoy.tashow.data.BaseResponse;
import com.showjoy.tashow.data.LoginDTO;
import com.showjoy.tashow.data.LoginData;
import com.showjoy.tashow.data.MessageDTO;
import com.showjoy.tashow.data.StartRoomData;
import com.showjoy.tashow.data.UserDTO;
import com.showjoy.tashow.data.UserInfoDTO;
import com.showjoy.tashow.image.CropperImageActivity;
import com.showjoy.tashow.listener.MyItemClickListener;
import com.showjoy.tashow.okhttp.OkHttpClientManager;
import com.showjoy.tashow.rtmp.RtmpPublisher;
import com.showjoy.tashow.tcpclient.SocketTransceiver;
import com.showjoy.tashow.tcpclient.TCPHeader;
import com.showjoy.tashow.tcpclient.TcpClient;
import com.showjoy.tashow.tcpclient.TcpCommand;
import com.showjoy.tashow.tcpclient.TcpHandler;
import com.showjoy.tashow.utils.ArrayUtils;
import com.showjoy.tashow.utils.FileUtils;
import com.showjoy.tashow.utils.PreferencesUtils;
import com.showjoy.tashow.utils.SpaceItemDecoration;
import com.showjoy.tashow.utils.UIUtils;
import com.showjoy.tashow.utils.WeakHandler;
import com.squareup.okhttp.Request;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class StartLiveActivity extends Activity implements SurfaceHolder.Callback, Camera.PreviewCallback, MyItemClickListener{

    private static final String TAG = "Yasea";

    private static final int NETWORK_SUCCESS = 1;

    private AudioRecord mic = null;
    private boolean aloop = false;
    private Thread aworker = null;

    private SurfaceView mCameraView = null;
    private Camera mCamera = null;

    private int mPreviewRotation = 90;
    private int mDisplayRotation = 90;
    private int mCamId = Camera.getNumberOfCameras() - 1; // default camera
    private byte[] mYuvFrameBuffer = new byte[SrsEncoder.VWIDTH * SrsEncoder.VHEIGHT * 3 / 2];

    private String mNotifyMsg;

    private ImageView switchCameraImg;

    private SimpleDraweeView coverImg;

    private LinearLayout chooseCoverRl;

    private TextView chooseCoverTxt,countTxt,attentionTxt;

    private LinearLayout closeLl;

    private MessageAdapter messageAdapter;

    private ListView messageLv;

    private SimpleDraweeView liveHeadImg;

    private View mBufferingIndicator;

    private RelativeLayout watchRl;

    private WeakHandler handler = new WeakHandler(Looper.getMainLooper());

    private String userId, currentFollowId,roomId;

    private int page=1;

    private RecyclerView headsRv;

    private PopupWindow pop;

    private UserInfoDTO userInfoDTO;

    private ViewHolder viewHolder = null;

    private HeadsAdapter adapter;

    private boolean isShowPop=false;

    private SrsEncoder mEncoder = new SrsEncoder(new RtmpPublisher.EventHandler() {
        @Override
        public void onRtmpConnecting(String msg) {
            mNotifyMsg = msg;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
//                    Toast.makeText(getApplicationContext(), mNotifyMsg, Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void onRtmpConnected(String msg) {
            mNotifyMsg = msg;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
//                    Toast.makeText(getApplicationContext(), mNotifyMsg, Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void onRtmpVideoStreaming(String msg) {
        }

        @Override
        public void onRtmpAudioStreaming(String msg) {
        }

        @Override
        public void onRtmpStopped(String msg) {
            mNotifyMsg = msg;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
//                    Toast.makeText(getApplicationContext(), mNotifyMsg, Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void onRtmpDisconnected(String msg) {
            mNotifyMsg = msg;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
//                    Toast.makeText(getApplicationContext(), mNotifyMsg, Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void onRtmpOutputFps(final double fps) {
            Log.i(TAG, String.format("Output Fps: %f", fps));
        }
    });

    private TcpClient client = new TcpClient() {

        @Override
        public void onConnect(SocketTransceiver transceiver) {
            refreshUI(true);
        }

        @Override
        public void onDisconnect(SocketTransceiver transceiver) {
            refreshUI(false);
        }

        @Override
        public void onConnectFailed() {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(StartLiveActivity.this, "连接失败",Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void onReceive(final SocketTransceiver transceiver, final String response) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Log.d("response",response);
                    if (!TextUtils.isEmpty(response)){
                        if (View.GONE == messageLv.getVisibility()){
                            messageLv.setVisibility(View.VISIBLE);
                        }
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.has("isSuccess")&& jsonObject.getBoolean("isSuccess")&&jsonObject.has("data")){
                                MessageDTO messageDTO = new Gson().fromJson(jsonObject.getString("data"),MessageDTO.class);
                                if (messageDTO!=null){
                                    creatMessageView(messageDTO);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
//                    Toast.makeText(StartLiveActivity.this, response,Toast.LENGTH_SHORT).show();
                }
            });
        }
    };

    private void creatMessageView(MessageDTO messageDTO) {
        if (messageAdapter == null){
            List<MessageDTO> messageDTOs = new ArrayList<>();
            messageDTOs.add(messageDTO);
            messageAdapter = new MessageAdapter(messageDTOs,TaShowGlobal.appContext);
            messageLv.setAdapter(messageAdapter);
        }else {
            messageAdapter.addMessage(messageDTO);
            messageAdapter.notifyDataSetChanged();
            messageLv.smoothScrollToPosition(messageAdapter.getCount()-1);
        }
    }

    /**
     * 刷新界面显示
     *
     * @param isConnected
     */
    private void refreshUI(final boolean isConnected) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (isConnected) {
                    login();
                }
            }
        });
    }

    //登录
    private void login() {
        TCPHeader tcpHeader = new TCPHeader();
        tcpHeader.setHandlerId(TcpHandler.USER_HANDLER);
        tcpHeader.setCommandId(TcpCommand.USER_LOGIN);
        tcpHeader.setCodecId(TcpCommand.CODE_ID);
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setUsername(PreferencesUtils.getString(TaShowGlobal.appContext, LoginData.TEL));
        loginDTO.setTicket("123");
        String loginStr = new Gson().toJson(loginDTO);
        client.getTransceiver().send(tcpHeader, loginStr);
    }
    //登出
    private void logout() {
        TCPHeader tcpHeader = new TCPHeader();
        tcpHeader.setHandlerId(TcpHandler.USER_HANDLER);
        tcpHeader.setCommandId(TcpCommand.USER_LOGOUT);
        tcpHeader.setCodecId(TcpCommand.CODE_ID);
        if (client!=null){
            client.getTransceiver().send(tcpHeader, "");
        }
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userId = PreferencesUtils.getString(TaShowGlobal.appContext, LoginData.USER_ID);
        currentFollowId = userId;
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_start_live);
        connect();
        initView();
        initPop();
        initData();
    }

    /**
     * 设置IP和端口地址,连接或断开
     */
    private void connect() {
        if (client.isConnected()) {
            // 断开连接
            client.disconnect();
        } else {
            try {
                String hostIP = "192.168.0.133";
                int port = Integer.parseInt("7001");
                client.connect(hostIP, port);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "端口错误", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }

    private void initView() {
        mBufferingIndicator = findViewById(R.id.buffering_indicator);
        messageLv = (ListView) findViewById(R.id.lv_message);
        watchRl = (RelativeLayout) findViewById(R.id.rl_watch);
        countTxt = (TextView) findViewById(R.id.txt_count);
        liveHeadImg = (SimpleDraweeView) findViewById(R.id.live_head);
        attentionTxt = (TextView) findViewById(R.id.txt_attention);
        closeLl = (LinearLayout) findViewById(R.id.ll_close);
        headsRv = (RecyclerView) findViewById(R.id.rv_heads);
        // 创建一个线性布局管理器
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        SpaceItemDecoration spaceItemDecoration = new SpaceItemDecoration(10);
        headsRv.addItemDecoration(spaceItemDecoration);
        // 设置布局管理器
        headsRv.setLayoutManager(layoutManager);
        mCameraView = (SurfaceView) findViewById(R.id.preview);
        mCameraView.getHolder().addCallback(this);
        coverImg = (SimpleDraweeView) findViewById(R.id.img_cover);
        chooseCoverRl = (LinearLayout) findViewById(R.id.ll_choose_cover);
        chooseCoverTxt = (TextView) findViewById(R.id.txt_choose_cover);
        messageLv = (ListView) findViewById(R.id.lv_message);
        switchCameraImg = (ImageView) findViewById(R.id.img_switch);
        switchCameraImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCamera != null && mEncoder != null) {
                    mCamId = (mCamId + 1) % Camera.getNumberOfCameras();
                    stopCamera();
                    mEncoder.swithCameraFace();
                    startCamera();
                }
            }
        });
        LinearLayout closeLl = (LinearLayout) findViewById(R.id.ll_close);
        closeLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopPublish();
                finish();
            }
        });
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable ex) {
                mNotifyMsg = ex.getMessage();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        Toast.makeText(getApplicationContext(), mNotifyMsg, Toast.LENGTH_SHORT).show();
                        stopPublish();
                    }
                });
            }
        });

        findViewById(R.id.ll_choose_cover).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(intent, 4);
            }
        });

        watchRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isShowPop = true;
                currentFollowId = userId;
                getUserInfo();
            }
        });
    }

    private void initPop() {
        if (viewHolder==null){
            View view = LayoutInflater.from(TaShowGlobal.appContext).inflate(R.layout.pop_anchor_introduce, null);
            viewHolder = new ViewHolder(view);
        }

        if (pop == null) {
            pop = new PopupWindow(viewHolder.itemView, UIUtils.dip2px(TaShowGlobal.appContext, 295), UIUtils.dip2px(TaShowGlobal.appContext, 300));
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==0){
            return;
        }
       if (requestCode==4){
           String path = FileUtils.getPath(TaShowGlobal.appContext, data.getData());
            if (path.indexOf(Config.folderName)!=-1){
                startLive(path);
            }else {
                UIUtils.gotoActivityWithStringForResult(StartLiveActivity.this, CropperImageActivity.class, path, 3);
            }
        }
        if (3==resultCode){
            String path = data.getStringExtra("crop_path");
            startLive(path);
        }
    }
    private void initData() {
        chooseCoverTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseCoverRl.setVisibility(View.GONE);
            }
        });
    }

    //每3秒调一次请求
    private void getUserDatas() {
        handler.post(task);//立即调用
    }

    private Runnable task = new Runnable() {
        public void run() {
            handler.postDelayed(this, 3 * 1000);//设置延迟时间3秒
            getUsers();
        }
    };

    private void startLive(String path) {
        File file = new File(path);
        OkHttpClientManager.getUploadDelegate().postAsyn(PortUtils.startRoom, "image", file, PortUtils.startRoom(userId), new OkHttpClientManager.ResultCallback<String>() {
            @Override
            public void onError(Request request, Exception e) {

            }

            @Override
            public void onResponse(String response) {
                if (!TextUtils.isEmpty(response)) {
                    Log.e("response", response);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.has("data")) {
                            StartRoomData startRoomData = new Gson().fromJson(jsonObject.getString("data"), StartRoomData.class);
                            coverImg.setImageURI(Uri.parse(startRoomData.image));
                            chooseCoverTxt.setBackgroundResource(R.drawable.shape_attente);
                            chooseCoverTxt.setEnabled(true);
                            chooseCoverTxt.setText("开启直播");
                            SrsEncoder.vbitrate = 1000;
                            SrsEncoder.rtmpUrl = startRoomData.rtmpPushUrl;
                            roomId = startRoomData.id;
                            startPublish();
                            getUserInfo();
                            getUserDatas();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, "startRoom");
    }

    @Override
    public void onItemClick(View view, int position) {
        if (position!=-1){
            UserDTO userDto = adapter.getUsers().get(position);
            currentFollowId = userDto.id;
            getUserInfo();
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public SimpleDraweeView headImg;
        TextView nameTxt;
        ImageView sexImg;
        TextView followNumTxt ;
        TextView followerNumTxt;
        RelativeLayout closeRl;
        Button likeBtn;
        public ViewHolder(View itemView) {
            super(itemView);
            headImg = (SimpleDraweeView) itemView.findViewById(R.id.img_head);
            nameTxt = (TextView) itemView.findViewById(R.id.txt_name);
            sexImg = (ImageView) itemView.findViewById(R.id.img_sex);
            followNumTxt = (TextView) itemView.findViewById(R.id.txt_follow_num);
            followerNumTxt = (TextView) itemView.findViewById(R.id.txt_follower_num);
            closeRl = (RelativeLayout) itemView.findViewById(R.id.rl_close);
            likeBtn = (Button) itemView.findViewById(R.id.btn_like);
        }
    }
    /**
     * 获取用户信息
     */
    private void getUserInfo() {
        OkHttpClientManager.postAsyn(PortUtils.getUserInfo, PortUtils.getUserInfo(currentFollowId, userId), new OkHttpClientManager.ResultCallback<String>() {
            @Override
            public void onError(Request request, Exception e) {
                Toast.makeText(TaShowGlobal.appContext, e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(String response) {
                if (!TextUtils.isEmpty(response)) {
                    if (View.GONE == watchRl.getVisibility()){
                        watchRl.setVisibility(View.VISIBLE);
                    }
                    Log.d("response", response);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.has("data")) {
                            userInfoDTO = new Gson().fromJson(jsonObject.getString("data"), UserInfoDTO.class);
                            if (userInfoDTO != null) {
                                if (viewHolder != null) {
                                    viewHolder.headImg.setImageURI(Uri.parse(userInfoDTO.image));
                                    if (!TextUtils.isEmpty(userInfoDTO.username)) {
                                        viewHolder.nameTxt.setText(userInfoDTO.username);
                                    } else {
                                        viewHolder.nameTxt.setText(userInfoDTO.userId);
                                    }
                                    if (Config.BOY == userInfoDTO.sex) {
                                        viewHolder.sexImg.setBackgroundResource(R.drawable.gender_boy);
                                    } else if (Config.GIRL == userInfoDTO.sex) {
                                        viewHolder.sexImg.setBackgroundResource(R.drawable.sex_girl);
                                    } else {
                                        viewHolder.sexImg.setBackgroundResource(R.drawable.sex_unknown);
                                    }
                                    viewHolder.followNumTxt.setText("关注:" + userInfoDTO.followNum);
                                    viewHolder.followerNumTxt.setText("粉丝:" + userInfoDTO.followerNum);
                                    if (userInfoDTO.isFollow) {
                                        viewHolder.likeBtn.setBackgroundResource(R.drawable.shape_already_attente);
                                        viewHolder.likeBtn.setText("已关注");
                                    } else {
                                        viewHolder.likeBtn.setBackgroundResource(R.drawable.shape_attente);
                                        viewHolder.likeBtn.setText("关注");
                                    }
                                }

                                if (userId.equals(userInfoDTO.userId)) {
                                    if (userInfoDTO.isFollow) {
                                        attentionTxt.setText("已关注");
                                        attentionTxt.setBackgroundResource(R.color.tab_unselect);
                                    } else {
                                        attentionTxt.setText("关注");
                                        attentionTxt.setBackgroundResource(R.color.colorBind);
                                    }
                                    liveHeadImg.setImageURI(Uri.parse(userInfoDTO.image));
                                    if (isShowPop) {
                                        showPop();
                                    }
                                } else {
                                    showPop();
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

    private void showPop() {
        pop.showAtLocation(mCameraView, Gravity.CENTER, 0, 0);

        viewHolder.likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userId.equals(userInfoDTO.userId)){
                    Toast.makeText(TaShowGlobal.appContext,"不能关注自己",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!userInfoDTO.isFollow) {
                    userLike(viewHolder.likeBtn);
                } else {
                    cancelFollow(viewHolder.likeBtn);
                }
            }
        });
        viewHolder.closeRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pop != null) {
                    pop.dismiss();
                }
            }
        });
    }

    /**
     * 用户关注
     * @param likeBtn
     */
    private void userLike(final Button likeBtn) {
        OkHttpClientManager.postAsyn(PortUtils.follow, PortUtils.follow(userId, currentFollowId), new OkHttpClientManager.ResultCallback<BaseResponse>() {
            @Override
            public void onError(Request request, Exception e) {
                Toast.makeText(TaShowGlobal.appContext, e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(BaseResponse baseResponse) {
                if (baseResponse != null) {
                    if (!TextUtils.isEmpty(baseResponse.msg)) {
                        Toast.makeText(TaShowGlobal.appContext, baseResponse.msg, Toast.LENGTH_SHORT).show();
                    }
                    if (NETWORK_SUCCESS == baseResponse.isSuccess) {
                        likeBtn.setBackgroundResource(R.drawable.shape_already_attente);
                        likeBtn.setText("已关注");
                    }
                    getUserInfo();
                }

            }
        });
    }

    /**
     * 取消关注
     */
    private void cancelFollow(final Button likeBtn) {
        OkHttpClientManager.postAsyn(PortUtils.unFollow, PortUtils.getUnFollow(userId, currentFollowId), new OkHttpClientManager.ResultCallback<BaseResponse>() {
            @Override
            public void onError(Request request, Exception e) {
                Toast.makeText(TaShowGlobal.appContext, e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(BaseResponse response) {
                if (response != null && NETWORK_SUCCESS == response.isSuccess) {
                    if (!TextUtils.isEmpty(response.msg)) {
                        Toast.makeText(TaShowGlobal.appContext, response.msg, Toast.LENGTH_SHORT).show();
                    }
                    if (NETWORK_SUCCESS == response.isSuccess) {
                        likeBtn.setBackgroundResource(R.drawable.shape_attente);
                        likeBtn.setText("关注TA");
                    }
                    getUserInfo();
                }

            }
        });
    }

    private void getUsers() {
        OkHttpClientManager.postAsyn(PortUtils.getUsers, PortUtils.getUsers(roomId, String.valueOf(page)), new OkHttpClientManager.ResultCallback<String>() {
            @Override
            public void onError(Request request, Exception e) {
                Toast.makeText(TaShowGlobal.appContext, e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(String response) {
                if (!TextUtils.isEmpty(response)) {
                    if (View.GONE == headsRv.getVisibility()){
                        headsRv.setVisibility(View.VISIBLE);
                    }
                    Log.d("response", response);
                    try {
                        int count = 0;
                        List<UserDTO> userDtolist = new ArrayList<UserDTO>();
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.has("count")) {
                            count = jsonObject.getInt("count");
                        }
                        if (jsonObject.has("data")) {
                            UserDTO[] userDTOs = new Gson().fromJson(jsonObject.getString("data"), UserDTO[].class);
                            if (userDTOs != null) {
                                userDtolist = ArrayUtils.arrayToList(userDTOs);
                            }
                        }
                        creatHeadView(count, userDtolist);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        });
    }

    private void creatHeadView(int count, List<UserDTO> userDtolist) {
        String countStr = count+"人在看";
        SpannableString spanString = new SpannableString(countStr);
        AbsoluteSizeSpan span = new AbsoluteSizeSpan(32);
        StyleSpan styleSpan = new StyleSpan(Typeface.BOLD);//加粗
        spanString.setSpan(span, 0, countStr.indexOf("人"), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spanString.setSpan(styleSpan, 0, countStr.indexOf("人"), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        if (countTxt!=null){
            countTxt.setText("");
        }
        countTxt.append(spanString);
        adapter = new HeadsAdapter(userDtolist,TaShowGlobal.appContext);
        adapter.setOnItemClickListener(this);
        // 设置Adapter
        headsRv.setAdapter(adapter);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            logout();
        }
        return super.onKeyDown(keyCode, event);
    }

    private void startCamera() {
        if (mCamera != null) {
            Log.d(TAG, "start camera, already started. return");
            return;
        }
        if (mCamId > (Camera.getNumberOfCameras() - 1) || mCamId < 0) {
            Log.e(TAG, "####### start camera failed, inviald params, camera No.="+ mCamId);
            return;
        }

        mCamera = Camera.open(mCamId);

        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(mCamId, info);
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT){
            mDisplayRotation = (mPreviewRotation + 180) % 360;
            mDisplayRotation = (360 - mDisplayRotation) % 360;
        } else {
            mDisplayRotation = mPreviewRotation;
        }

        Camera.Parameters params = mCamera.getParameters();
		/* preview size  */
        Size size = mCamera.new Size(SrsEncoder.VWIDTH, SrsEncoder.VHEIGHT);
        if (!params.getSupportedPreviewSizes().contains(size)) {
            Thread.getDefaultUncaughtExceptionHandler().uncaughtException(Thread.currentThread(),
                    new IllegalArgumentException(String.format("Unsupported preview size %dx%d", size.width, size.height)));
        }

        /* picture size  */
        if (!params.getSupportedPictureSizes().contains(size)) {
            Thread.getDefaultUncaughtExceptionHandler().uncaughtException(Thread.currentThread(),
                    new IllegalArgumentException(String.format("Unsupported picture size %dx%d", size.width, size.height)));
        }

        /***** set parameters *****/
        //params.set("orientation", "portrait");
        //params.set("orientation", "landscape");
        //params.setRotation(90);
        params.setPictureSize(SrsEncoder.VWIDTH, SrsEncoder.VHEIGHT);
        params.setPreviewSize(SrsEncoder.VWIDTH, SrsEncoder.VHEIGHT);
        int[] range = findClosestFpsRange(SrsEncoder.VFPS, params.getSupportedPreviewFpsRange());
        params.setPreviewFpsRange(range[0], range[1]);
        params.setPreviewFormat(SrsEncoder.VFORMAT);
        params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
        params.setWhiteBalance(Camera.Parameters.WHITE_BALANCE_AUTO);
        params.setSceneMode(Camera.Parameters.SCENE_MODE_AUTO);
        mCamera.setParameters(params);

        mCamera.setDisplayOrientation(mPreviewRotation);

        mCamera.addCallbackBuffer(mYuvFrameBuffer);
        mCamera.setPreviewCallbackWithBuffer(this);
        try {
            mCamera.setPreviewDisplay(mCameraView.getHolder());
        } catch (IOException e) {
            e.printStackTrace();
        }
        mCamera.startPreview();
    }

    private void stopCamera() {
        if (mCamera != null) {
            // need to SET NULL CB before stop preview!!!
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    private void onGetYuvFrame(byte[] data) {
        mEncoder.onGetYuvFrame(data);
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera c) {
        onGetYuvFrame(data);
        c.addCallbackBuffer(mYuvFrameBuffer);
    }

    private void onGetPcmFrame(byte[] pcmBuffer, int size) {
        mEncoder.onGetPcmFrame(pcmBuffer, size);
    }

    private void startAudio() {
        if (mic != null) {
            return;
        }

        int bufferSize = 2 * AudioRecord.getMinBufferSize(SrsEncoder.ASAMPLERATE, SrsEncoder.ACHANNEL, SrsEncoder.AFORMAT);
        mic = new AudioRecord(MediaRecorder.AudioSource.MIC, SrsEncoder.ASAMPLERATE, SrsEncoder.ACHANNEL, SrsEncoder.AFORMAT, bufferSize);
        mic.startRecording();

        byte pcmBuffer[] = new byte[4096];
        while (aloop && !Thread.interrupted()) {
            int size = mic.read(pcmBuffer, 0, pcmBuffer.length);
            if (size <= 0) {
                Log.e(TAG, "***** audio ignored, no data to read.");
                break;
            }
            onGetPcmFrame(pcmBuffer, size);
        }
    }

    private void stopAudio() {
        aloop = false;
        if (aworker != null) {
            Log.i(TAG, "stop audio worker thread");
            aworker.interrupt();
            try {
                aworker.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
                aworker.interrupt();
            }
            aworker = null;
        }

        if (mic != null) {
            mic.setRecordPositionUpdateListener(null);
            mic.stop();
            mic.release();
            mic = null;
        }
    }

    private void startPublish() {
        int ret = mEncoder.start();
        if (ret < 0) {
            return;
        }

        startCamera();

        aworker = new Thread(new Runnable() {
            @Override
            public void run() {
                android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_AUDIO);
                startAudio();
            }
        });
        aloop = true;
        aworker.start();
    }

    private void stopPublish() {
        stopAudio();
        stopCamera();
        mEncoder.stop();
    }

    private int[] findClosestFpsRange(int expectedFps, List<int[]> fpsRanges) {
        expectedFps *= 1000;
        int[] closestRange = fpsRanges.get(0);
        int measure = Math.abs(closestRange[0] - expectedFps) + Math.abs(closestRange[1] - expectedFps);
        for (int[] range : fpsRanges) {
            if (range[0] <= expectedFps && range[1] >= expectedFps) {
                int curMeasure = Math.abs(range[0] - expectedFps) + Math.abs(range[1] - expectedFps);
                if (curMeasure < measure) {
                    closestRange = range;
                    measure = curMeasure;
                }
            }
        }
        return closestRange;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.d(TAG, "surfaceChanged");
    }

    @Override
    public void surfaceCreated(SurfaceHolder arg0) {
        Log.d(TAG, "surfaceCreated");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder arg0) {
        Log.d(TAG, "surfaceDestroyed");
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopPublish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopPublish();
        if (handler!=null){
            handler.removeCallbacksAndMessages(task);
        }
    }
}
