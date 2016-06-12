package com.showjoy.tashow;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.showjoy.tashow.base.TaShowGlobal;

public class TaShowApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		Fresco.initialize(this);
		TaShowGlobal.appContext = this;
	}
}