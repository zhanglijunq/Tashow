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
import com.showjoy.tashow.R;
import com.showjoy.tashow.base.PortUtils;
import com.showjoy.tashow.base.TaShowGlobal;
import com.showjoy.tashow.common.Config;
import com.showjoy.tashow.data.UserInfoDTO;
import com.showjoy.tashow.image.CropperImageActivity;
import com.showjoy.tashow.okhttp.OkHttpClientManager;
import com.showjoy.tashow.utils.FileUtils;
import com.showjoy.tashow.utils.SerializeToFlatByte;
import com.showjoy.tashow.utils.UIUtils;
import com.squareup.okhttp.Request;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

/**
 * 关注的人
 * Created by mac on 16/5/9.
 */
public class EditNameActivity extends ActionBarActivity{

    private SimpleDraweeView headImg;

    private TextView nameTxt,idTxt;

    private ImageView sexImg;

    private File file;

    private String path="";

    private UserInfoDTO userInfoDTO;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_information);
        setTitle("昵称");
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
                UIUtils.gotoActivityWithStringForResult(EditNameActivity.this, CropperImageActivity.class, path, 3);
            }
        }else if (requestCode==4){
            String path = FileUtils.getPath(TaShowGlobal.appContext,data.getData());
            if (path.indexOf(Config.folderName)!=-1){
                this.path = path;
                uploadHead();
            }else {
                UIUtils.gotoActivityWithStringForResult(EditNameActivity.this, CropperImageActivity.class, path, 3);
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
        OkHttpClientManager.postAsyn(PortUtils.updateUserInfo, PortUtils.updataParam(userInfoDTO.userId, userInfoDTO.username, String.valueOf(userInfoDTO.sex)), new OkHttpClientManager.ResultCallback<String>() {
            @Override
            public void onError(Request request, Exception e) {
                Log.d("errpr", e.getMessage());
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
            nameTxt.setText(userInfoDTO.username);
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
}
