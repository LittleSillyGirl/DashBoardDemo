/* 
 * HXH Create at 2014-10-22 下午2:19:17
 */
package com.mproject.exercisedemo.dashboarddemo.utils;

import android.app.Activity;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;

import java.lang.reflect.Method;

/**
 * 获取手机的宽、高、密度
 *
 * @author lcl
 * created by 2017-01-04 12:03
 */
final public class DisplayUtil {
	private DisplayUtil() {

	}

	/**
	 * @param activity
	 * @return 宽
	 */
	public static int getWidth(Activity activity) {
		int width = 0;
		DisplayMetrics dm = new DisplayMetrics();
		Display display = activity.getWindowManager().getDefaultDisplay();
		display.getMetrics(dm);
		width = dm.widthPixels;
		return width;
	}

	/**
	 * @param activity
	 * @throws Exception
	 * @return 高
	 */
	public static int getHeight(Activity activity) throws Exception {
		int height = 0;
		int ver = Build.VERSION.SDK_INT;
		DisplayMetrics dm = new DisplayMetrics();
		Display display = activity.getWindowManager().getDefaultDisplay();
		display.getMetrics(dm);
		if (ver < 13) {
			height = dm.heightPixels;
		} else if (ver == 13) {
			Method mt = display.getClass().getMethod("getRealHeight");
			height = (Integer) mt.invoke(display);
		} else if (ver > 13) {
			Method mt = display.getClass().getMethod("getRawHeight");
			height = (Integer) mt.invoke(display);
		}
		return height;
	}
	
	public static int getScreenHeight(Activity activity) {
		DisplayMetrics metric = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(metric); 
		return metric.heightPixels; 
	}

	/**
	 * @param activity
	 * @return 宽*高
	 * @throws Exception
	 */
	public static String getDisplay(Activity activity) throws Exception {
		return getWidth(activity) + "*" + getHeight(activity);
	}

	/**
	 * 根据View的宽重新按比例设置View的高度
	 * 
	 * @param view
	 * @param showViewWidth
	 * @param viewWidth
	 * @param viewHeight
	 */
	public static void setViewHeight(View view, int showViewWidth,
									 int viewWidth, int viewHeight) {
		view.getLayoutParams().height = viewHeight * showViewWidth / viewWidth;
	}

	/**
	 * @param activity
	 * @return
	 */
	public static float getDensity(Activity activity) {
		DisplayMetrics metric = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(metric);
		return metric.density;
	}
}
