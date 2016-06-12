package com.showjoy.tashow.person;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnItemClickListener;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.showjoy.tashow.ActionBarActivity;
import com.showjoy.tashow.BaseActionBarActivity;
import com.showjoy.tashow.R;
import com.showjoy.tashow.base.PortUtils;
import com.showjoy.tashow.base.TaShowGlobal;
import com.showjoy.tashow.common.Config;
import com.showjoy.tashow.data.BaseResponse;
import com.showjoy.tashow.data.LoginData;
import com.showjoy.tashow.data.UserInfoDTO;
import com.showjoy.tashow.image.CropperImageActivity;
import com.showjoy.tashow.okhttp.OkHttpClientManager;
import com.showjoy.tashow.utils.FileUtils;
import com.showjoy.tashow.utils.PreferencesUtils;
import com.showjoy.tashow.utils.SerializeToFlatByte;
import com.showjoy.tashow.utils.UIUtils;
import com.squareup.okhttp.Request;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

/**
 * 关注的人
 * Created by mac on 16/5/9.
 */
public class EditInformationActivity extends ActionBarActivity{

    private SimpleDraweeView headImg;

    private TextView nameTxt,idTxt;

    private ImageView sexImg;

    private File file;

    private String path="";

    private UserInfoDTO userInfoDTO;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        setContentView(R.layout.activity_edit_information);
        setTitle("编辑资料");
        initView();
        initButtonEvent();
        initData();
    }


    protected  void initView() {
        headImg = (SimpleDraweeView) findViewById(R.id.img_head);
        nameTxt = (TextView) findViewById(R.id.txt_name);
        idTxt = (TextView) findViewById(R.id.txt_hx_id);
        sexImg = (ImageView) findViewById(R.id.img_sex);
    }

    protected void initButtonEvent() {
        headImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertView("上传头像", null, "取消", null,
                        new String[]{"拍照", "从相册中选择"},
                        EditInformationActivity.this, AlertView.Style.ActionSheet, new OnItemClickListener() {
                    public void onItemClick(Object o, int position) {
                        if (0 == position) {
                            showCameraAction();
                        } else if (1 == position) {
                            openPhoto();
                        }
                    }
                }).show();
            }
        });
        findViewById(R.id.rl_sex).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertView(null, null, "取消", null, new String[]{"男", "女", "保密"}, EditInformationActivity.this, AlertView.Style.ActionSheet, new OnItemClickListener() {
                    @Override
                    public void onItemClick(Object o, int position) {
                        if (position != -1) {
                            if (0 == position) {
                                userInfoDTO.sex = 1;
                            } else if (1 == position) {
                                userInfoDTO.sex = 2;
                            } else if (2 == position) {
                                userInfoDTO.sex = 3;
                            }
                            update();
                        }
                    }
                }).show();
            }
        });
    }

    private void initData(){
        UserInfoDTO userInfoDTO = (UserInfoDTO) SerializeToFlatByte.restoreObject(getIntent().getByteArrayExtra("userInfoDTO"));
        creatView(userInfoDTO);
    }

    /**
     * 选择相册
     */
    private void openPhoto() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, 4);
    }
    /**
     * 选择相机
     */
    private void showCameraAction() {
        // 跳转到系统照相机
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(cameraIntent.resolveActivity(getApplicationContext().getPackageManager()) != null){
            // 设置系统相机拍照后的输出路径
            // 创建临时文件
            file = FileUtils.createTmpFile(getApplicationContext());
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
            startActivityForResult(cameraIntent, 2);
        }else{
            Toast.makeText(getApplicationContext(), "有错", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==0){
            return;
        }
        // 相机拍照完成后，返回图片路径
        if(requestCode == 2)
        {
            if (file != null)
            {
                String path = file.getPath();
                UIUtils.gotoActivityWithStringForResult(EditInformationActivity.this, CropperImageActivity.class, path, 3);
            }
        }else if (requestCode==4){
            String path = FileUtils.getPath(TaShowGlobal.appContext,data.getData());
            if (path.indexOf(Config.folderName)!=-1){
                this.path = path;
                uploadHead();
            }else {
                UIUtils.gotoActivityWithStringForResult(EditInformationActivity.this, CropperImageActivity.class, path, 3);
            }
        }
        if (3==resultCode){
            path = data.getStringExtra("crop_path");
            uploadHead();
        }
    }
    //头像上传
    private void uploadHead() {
        File file = new File(path);
        OkHttpClientManager.getUploadDelegate().postAsyn(PortUtils.updateUserInfo, "image", file, PortUtils.updataParam(userInfoDTO.userId, userInfoDTO.username, String.valueOf(userInfoDTO.sex)), new OkHttpClientManager.ResultCallback<String>() {
            @Override
            public void onError(Request request, Exception e) {
                Log.d("response", e.getMessage());
                Toast.makeText(TaShowGlobal.appContext, e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(String response) {
                Log.d("response", response);
                if (!TextUtils.isEmpty(response)) {
                    EventBus.getDefault().post(new UnFollowSuccess(true));
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.has("data")) {
                            UserInfoDTO userInfoDTO = new Gson().fromJson(jsonObject.getString("data"), UserInfoDTO.class);
                            creatView(userInfoDTO);
                        }
                        if (jsonObject.has("msg")) {
                            Toast.makeText(TaShowGlobal.appContext, jsonObject.getString("msg"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, "upload");
    }

    //更新用户信息
    private void update() {
        OkHttpClientManager.postAsyn(PortUtils.updateWithNoImg, PortUtils.updataParam(userInfoDTO.userId, userInfoDTO.username, String.valueOf(userInfoDTO.sex)), new OkHttpClientManager.ResultCallback<String>() {
            @Override
            public void onError(Request request, Exception e) {
                Log.d("response", e.getMessage());
                Toast.makeText(TaShowGlobal.appContext, e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(String response) {
                Log.d("response", response);
                if (!TextUtils.isEmpty(response)) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.has("data")) {
                            UserInfoDTO userInfoDTO = new Gson().fromJson(jsonObject.getString("data"), UserInfoDTO.class);
                            creatView(userInfoDTO);
                        }
                        if (jsonObject.has("msg")) {
                            Toast.makeText(TaShowGlobal.appContext, jsonObject.getString("msg"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void creatView(UserInfoDTO userInfoDTO) {
        this.userInfoDTO = userInfoDTO;
        if (userInfoDTO!=null){
            headImg.setImageURI(Uri.parse(userInfoDTO.image));
            nameTxt.setText(!TextUtils.isEmpty(userInfoDTO.username)?userInfoDTO.username:userInfoDTO.userId);
            idTxt.setText(userInfoDTO.userId);
            if (Config.BOY==userInfoDTO.sex){
                sexImg.setBackgroundResource(R.drawable.gender_boy);
            }else if (Config.GIRL == userInfoDTO.sex){
                sexImg.setBackgroundResource(R.drawable.sex_girl);
            }else {
                sexImg.setBackgroundResource(R.drawable.sex_unknown);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onEventMainThread(SettingEvent event){

    }
}
