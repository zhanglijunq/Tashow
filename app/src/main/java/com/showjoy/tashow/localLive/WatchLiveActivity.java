/*
 * Copyright (C) 2013 yixia.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.showjoy.tashow.localLive;


import android.app.Activity;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.alertview.AlertView;
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
import com.showjoy.tashow.data.RoomDTO;
import com.showjoy.tashow.data.RoomData;
import com.showjoy.tashow.data.UserDTO;
import com.showjoy.tashow.data.UserInfoDTO;
import com.showjoy.tashow.listener.MyItemClickListener;
import com.showjoy.tashow.okhttp.OkHttpClientManager;
import com.showjoy.tashow.tcpclient.SocketTransceiver;
import com.showjoy.tashow.tcpclient.TCPHeader;
import com.showjoy.tashow.tcpclient.TcpClient;
import com.showjoy.tashow.tcpclient.TcpCommand;
import com.showjoy.tashow.tcpclient.TcpHandler;
import com.showjoy.tashow.utils.ArrayUtils;
import com.showjoy.tashow.utils.PreferencesUtils;
import com.showjoy.tashow.utils.SerializeToFlatByte;
import com.showjoy.tashow.utils.SpaceItemDecoration;
import com.showjoy.tashow.utils.UIUtils;
import com.showjoy.tashow.utils.WeakHandler;
import com.showjoy.tashow.widget.MediaController;
import com.showjoy.tashow.widget.VideoView;
import com.squareup.okhttp.Request;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;


public class WatchLiveActivity extends Activity implements IMediaPlayer.OnCompletionListener,MyItemClickListener{

	private int NETWORK_SUCCESS=1;

	private boolean isLoginSocket = false;

	private VideoView mVideoView;
	private View mBufferingIndicator;
	private MediaController mMediaController;

	private EditText messageEt;

	private TextView sendTxt,countTxt,attentionTxt;

	private LinearLayout messageLl;

	private String mVideoPath;

	private WeakHandler handler = new WeakHandler(Looper.getMainLooper());

	private MessageDTO messageDTO = new MessageDTO();

	private String userId, roomId,followId;

	private String currentFollowId;

	private ListView messageLv;

	private RelativeLayout watchRl;

	private LinearLayout closeLl;

	private MessageAdapter messageAdapter;

	private int page=1;

	private SimpleDraweeView liveHeadImg;

	private RecyclerView headsRv;

	private PopupWindow pop;

	private UserInfoDTO userInfoDTO;

	private ViewHolder viewHolder = null;

	private HeadsAdapter adapter;

	private boolean isShowPop=false;
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
					Toast.makeText(WatchLiveActivity.this, "连接失败",Toast.LENGTH_SHORT).show();
				}
			});
		}

		@Override
		public void onReceive(final SocketTransceiver transceiver, final String response) {
			handler.post(new Runnable() {
				@Override
				public void run() {
					Log.d("response",response);
					if (isLoginSocket) {
						joinRoom();
					}
					if (!TextUtils.isEmpty(response)){
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
//					Toast.makeText(WatchLiveActivity.this, response,Toast.LENGTH_SHORT).show();
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
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_player);
		userId = PreferencesUtils.getString(TaShowGlobal.appContext, LoginData.USER_ID);
		connect();
		IjkMediaPlayer.loadLibrariesOnce(null);
		IjkMediaPlayer.native_profileBegin("libijkplayer.so");
		messageDTO.from = Integer.valueOf(userId);
		messageDTO.fromUsername=PreferencesUtils.getString(TaShowGlobal.appContext, LoginData.USER_NAME);
		initView();
		initPop();
		initEvent();
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
//				Toast.makeText(this, "端口错误", Toast.LENGTH_SHORT).show();
				e.printStackTrace();
			}
		}
	}

	private void initView() {
		mVideoView = (VideoView) findViewById(R.id.video_view);
		mBufferingIndicator = findViewById(R.id.buffering_indicator);
		messageEt = (EditText) findViewById(R.id.et_message);
		sendTxt = (TextView) findViewById(R.id.txt_send);
		messageLl = (LinearLayout) findViewById(R.id.ll_message);
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

	private void initEvent() {
		sendTxt.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				sendStr();
			}
		});
		closeLl.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				leaveRoom();
				logout();
			}
		});

		watchRl.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				isShowPop=true;
				currentFollowId = followId;
				getUserInfo();
			}
		});

		messageEt.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (s.length() >= 15) {
					Toast.makeText(TaShowGlobal.appContext, "字数太多哦", Toast.LENGTH_SHORT).show();
				}
			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});
	}

	private void showPop() {
		pop.showAtLocation(mVideoView, Gravity.CENTER, 0, 0);

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

	private void initData() {
		RoomData roomData = (RoomData) SerializeToFlatByte.restoreObject(getIntent().getByteArrayExtra("roomData"));
		roomId = roomData.id;
		followId = roomData.userId;
		this.currentFollowId = followId;
		messageDTO.to=Integer.valueOf(roomId);
		messageDTO.type=2;
		String rtmpPlayUrl = roomData.rtmpPlayUrl;
		if (!TextUtils.isEmpty(rtmpPlayUrl)){
			mVideoPath = rtmpPlayUrl;
			playfunction();
		}
		getUserDatas();
		getUserInfo();
	}
	//每3秒调一次请求
	private void getUserDatas() {
		handler.postDelayed(task, 0);//延迟调用
		handler.post(task);//立即调用
	}

	private Runnable task = new Runnable() {
		public void run() {
			// TODO Auto-generated method stub
			handler.postDelayed(this, 3 * 1000);//设置延迟时间3秒
			getUsers();
		}
	};

	private void playfunction(){
		mMediaController = new MediaController(TaShowGlobal.appContext);
		//是否显示进度条，直播不显示
//        mVideoView.setMediaController(mMediaController);
		mVideoView.setMediaBufferingIndicator(mBufferingIndicator);
		mVideoView.setOnCompletionListener(this);
		mVideoView.setVideoPath(mVideoPath);
		mVideoView.requestFocus();
		mVideoView.start();
	}

	/**
	 * 发送数据
	 */
	private void sendStr() {
		try {
			String message = messageEt.getText().toString();
			if (TextUtils.isEmpty(message)){
				Toast.makeText(TaShowGlobal.appContext,"信息不能为空",Toast.LENGTH_SHORT).show();
				return;
			}
			messageEt.setText("");
			messageDTO.message=message;
			if (isLoginSocket){
				isLoginSocket = false;
			}
			TCPHeader tcpHeader = new TCPHeader();
			tcpHeader.setHandlerId(TcpHandler.MESSAGE_HANDLER);
			tcpHeader.setCommandId(TcpCommand.MESSAGE_SEND);
			tcpHeader.setCodecId(TcpCommand.CODE_ID);
			String s = new Gson().toJson(messageDTO);
			client.getTransceiver().send(tcpHeader, s);
		} catch (Exception e) {
			e.printStackTrace();
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
		isLoginSocket = true;
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
		isLoginSocket = false;
		TCPHeader tcpHeader = new TCPHeader();
		tcpHeader.setHandlerId(TcpHandler.USER_HANDLER);
		tcpHeader.setCommandId(TcpCommand.USER_LOGOUT);
		tcpHeader.setCodecId(TcpCommand.CODE_ID);
		if (client!=null){
			client.getTransceiver().send(tcpHeader, "");
		}
		finish();
	}

	/**
	 * 加入房间
	 */
	private void joinRoom() {
		isLoginSocket = false;
		TCPHeader tcpHeader = new TCPHeader();
		tcpHeader.setHandlerId(TcpHandler.ROOM_HANDLER);
		tcpHeader.setCommandId(TcpCommand.ROOM_JOIN);
		tcpHeader.setCodecId(TcpCommand.CODE_ID);
		RoomDTO roomDTO = new RoomDTO();
		roomDTO.setUserId(userId);
		roomDTO.setRoomId(roomId);
		String roomStr = new Gson().toJson(roomDTO);
		client.getTransceiver().send(tcpHeader, roomStr);
	}

	/**
	 * 离开房间
	 */
	private void leaveRoom() {
		isLoginSocket = false;
		TCPHeader tcpHeader = new TCPHeader();
		tcpHeader.setHandlerId(TcpHandler.ROOM_HANDLER);
		tcpHeader.setCommandId(TcpCommand.ROOM_LEAVE);
		tcpHeader.setCodecId(TcpCommand.CODE_ID);
		RoomDTO roomDTO = new RoomDTO();
		roomDTO.setUserId(userId);
		roomDTO.setRoomId(roomId);
		String roomStr = new Gson().toJson(roomDTO);
		if (client!=null){
			client.getTransceiver().send(tcpHeader, roomStr);
		}
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
				if (response!=null && NETWORK_SUCCESS == response.isSuccess){
					if (!TextUtils.isEmpty(response.msg)){
						Toast.makeText(TaShowGlobal.appContext,response.msg,Toast.LENGTH_SHORT).show();
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
					Log.d("response", response);
					try {
						JSONObject jsonObject = new JSONObject(response);
						if (jsonObject.has("data")) {
							userInfoDTO = new Gson().fromJson(jsonObject.getString("data"), UserInfoDTO.class);
							if (userInfoDTO != null) {
								if (viewHolder!=null){
									viewHolder.headImg.setImageURI(Uri.parse(userInfoDTO.image));
									if (!TextUtils.isEmpty(userInfoDTO.username)){
										viewHolder.nameTxt.setText(userInfoDTO.username);
									}else {
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
									}else {
										viewHolder.likeBtn.setBackgroundResource(R.drawable.shape_attente);
										viewHolder.likeBtn.setText("关注");
									}
								}

								if (followId.equals(userInfoDTO.userId)){
									if (userInfoDTO.isFollow) {
										attentionTxt.setText("已关注");
										attentionTxt.setBackgroundResource(R.color.tab_unselect);
									} else {
										attentionTxt.setText("关注");
										attentionTxt.setBackgroundResource(R.color.colorBind);
									}
									liveHeadImg.setImageURI(Uri.parse(userInfoDTO.image));
									if (isShowPop){
										showPop();
									}
								}else {
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

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			leaveRoom();
			logout();
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (handler!=null){
			handler.removeCallbacksAndMessages(task);
		}
	}

	@Override
	public void onCompletion(IMediaPlayer mp) {
		if (!mp.isPlaying()) {
			new AlertView("直播结束", null, "确定", null, null, this,
					AlertView.Style.Alert, null).show();
		}
	}

	@Override
	public void onItemClick(View view, int position) {
		if (position!=-1){
			UserDTO userDto = adapter.getUsers().get(position);
			currentFollowId = userDto.id;
			getUserInfo();
		}
	}
}
