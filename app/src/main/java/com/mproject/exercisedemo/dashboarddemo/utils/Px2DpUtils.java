/* 
 * HXH Create at 2014-10-27 下午2:04:47
 */
package com.mproject.exercisedemo.dashboarddemo.utils;

import android.content.Context;

/**
 * px和dp转化的工具类
 *
 * @author lcl
 *         created by 2017-01-04 11:32
 */
public class Px2DpUtils {

    /**
     * dp转化为px
     *
     * @param context
     * @param dp
     * @return
     */
    public static int dp2Px(Context context, int dp) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    /**
     * px转化为dp
     *
     * @param context
     * @param px
     * @return
     */
    public static int px2Dp(Context context, int px) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (px / scale + 0.5f);
    }
}
