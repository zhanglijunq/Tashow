package com.showjoy.tashow.image;


import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.muzhi.mtools.utils.ImageUtils;
import com.showjoy.tashow.R;
import com.showjoy.tashow.common.Config;

import uk.co.senab.photoview.PhotoViewAttacher;


//裁剪界面
public class CropperImageActivity extends BaseActivity {
	
	private String imagePath; 	// 要裁剪图片的URL
	private TextView crop_select; // 选取按钮
	private ProgressBar bar;// 等待框

	private int flag = 0;// 图像大小的标志变量 0是大图 1表示图像全部显示
	public Bitmap itbmp; // 需要传递的Bitmap

	float n; // 根据宽度缩放图像的系数
	
	public static ImageView cropimage;// 显示图片控件
	private PhotoViewAttacher mAttacher; //控制图像属性
	private Bitmap bitmap;//从相片和图库中获取的原始Bitmap
	
	private float minimumScale = 1.0f; //图像最小放缩比例 图像高度与屏幕高度一致

	// Handle机制
	protected Handler mHandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 0x111) {
					bar.setVisibility(View.GONE);
					String path=ImageUtils.saveAsBitmap(CropperImageActivity.this, itbmp, Config.folderName, null);
					itbmp.recycle();
					setResult(3,getIntent().putExtra("crop_path",path));
					finish();
				}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.camerasdk_crop_image);
		showLeftIcon();
		setActionBarTitle("裁剪图片");
		crop_select = (TextView)  findViewById(R.id.camerasdk_title_txv_right_text);
		crop_select.setText("选取");
		crop_select.setVisibility(View.VISIBLE);
		imagePath=getIntent().getExtras().getString("key_string");
		bar = (ProgressBar) findViewById(R.id.bar);
		cropimage = (ImageView) findViewById(R.id.cropimage);
		//获取屏幕宽度和高度
		DisplayMetrics dm = getResources().getDisplayMetrics();  
		int w = dm.widthPixels;
		int h = dm.widthPixels;

		// 设置编辑图片View的大小
		LayoutParams rlp = cropimage.getLayoutParams();
		rlp.height = w;
		rlp.width = h;
		cropimage.setLayoutParams(rlp);
		
		cropimage.setImageBitmap(ImageUtils.getBitmap(imagePath));
		mAttacher = new PhotoViewAttacher(cropimage);
		mAttacher.setScaleType(ScaleType.CENTER_CROP);
		
		//设置最小缩放大小
		mAttacher.setMinimumScale(minimumScale);
		initEvent();
	}

	private void initEvent(){
		crop_select.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				crop_select.setEnabled(false);
				bar.setVisibility(View.VISIBLE);
				// 启动线程来执行任务
				new Thread() {
					public void run() {
						getViewBitmap();
						Message m = new Message();
						m.what = 0x111;
						mHandler.sendMessage(m);
					}
				}.start();
			}
		});
	}

	protected void onDestroy() {
		super.onDestroy();
	}
	private void show(){
		if (flag == 0) {
//			crop_img.setImageResource(R.drawable.crop_img_sma);
			flag = 1;
			mAttacher.setScaleType(ScaleType.FIT_CENTER);
			mAttacher.setZoomable(false);
			//Toast.makeText(Activity_Cropper.this, "屏幕已锁定,请按全屏键解锁",Toast.LENGTH_SHORT).show();
			// 再次点击 恢复之前可以放缩状态
		} 
		else if (flag == 1) {
//			crop_img.setImageResource(R.drawable.crop_img_big);
			flag = 0;
			//cropimage.setImageBitmap(bitmap);
			mAttacher.setScaleType(ScaleType.CENTER_CROP);
			//图像恢复放大缩小
			mAttacher.setZoomable(true);
		}
	}
	
	
	private void getViewBitmap() {
		cropimage.setDrawingCacheEnabled(true);
		Bitmap bitmap = Bitmap.createBitmap(cropimage.getDrawingCache());
		// 清缓存
		cropimage.destroyDrawingCache();
		int w = cropimage.getWidth();
		int h = cropimage.getHeight();
		//Log.i("TAG", "w" + w);
		//Log.i("TAG", "h" + h);

		//itbmp = Bitmap.createBitmap(bitmap, 0, 0, 640, 640);
		
		itbmp = Bitmap.createBitmap(bitmap, 0, 0, w, w);
//		itbmp = PictureUtil.compressImage(itbmp);
        bitmap=ThumbnailUtils.extractThumbnail(bitmap, 640, 640);// 缩放图片到指定的宽高到640px
	}

}
