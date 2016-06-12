package com.showjoy.tashow.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ScrollView;

import com.showjoy.tashow.image.CropperImageActivity;

import java.io.Serializable;
import java.util.List;

public class UIUtils {
	/**
	 * 通用的不带参数的界面跳转
	 * 
	 */
	public static void gotoActivity(Context cxt, Class<?> cls) {
		Intent intent = new Intent(cxt, cls);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		cxt.startActivity(intent);
	}

	/**
	 * 通用的带boolean参数的界面跳转
	 * 
	 */
	public static final String KEY_BOOLEAN = "key_boolean";

	public static void gotoActivity(Context cxt, Class<?> cls, boolean keyBoolean) {
		Intent intent = new Intent(cxt, cls);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra(KEY_BOOLEAN, keyBoolean);
		cxt.startActivity(intent);
	}
	/**
	 * 通用的带boolean参数的界面跳转
	 *
	 */
	public static final String KEY_STRING = "key_string";
	/**
	 * 通用的带String参数的界面跳转
	 *
	 */
	public static void gotoActivityWithString(Context cxt, Class<?> cls, String value) {
		Intent intent = new Intent(cxt, cls);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra(KEY_STRING, value);
		cxt.startActivity(intent);
	}
	/**
	 * 通用的带String参数的界面跳转
	 *
	 */
	public static void gotoActivityWithBundle(Context cxt, Class<?> cls, Bundle bundle) {
		Intent intent = new Intent(cxt, cls);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtras(bundle);
		cxt.startActivity(intent);
	}

	/**
	 * startActivityForResult
	 * 
	 * @param activity
	 * @param cls
	 * @param requestCode
	 */
	public static void gotoActivityForResult(Activity activity, Class<?> cls, int requestCode) {
		Intent intent = new Intent(activity, cls);
		activity.startActivityForResult(intent, requestCode);
	}

	// scroll滑动到顶部
	public static void movePageTop(final ScrollView scrollView) {
		scrollView.post(new Runnable() {
			@Override
			public void run() {
				scrollView.fullScroll(ScrollView.FOCUS_UP);
			}
		});
	}

	/**
	 * dp转px
	 * 
	 * @param context
	 * @param pxValue
	 * @return
	 */
	public static int dip2px(Context context, float pxValue) {
		float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue * scale + 0.5f);
	}

	public static void gotoActivityWithStringForResult(Activity activity, Class<?> cls, String path, int requestCode) {
		Intent intent = new Intent(activity, cls);
		intent.putExtra(KEY_STRING, path);
		activity.startActivityForResult(intent,requestCode);
	}
}
