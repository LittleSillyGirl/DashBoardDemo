package com.mproject.exercisedemo.dashboarddemo.utils;

import android.view.View;

/**
 * 测量view的工具类
 * @author lcl
 * created by 2017-01-04 11:40
 */
public class MeasureUtils {

    /**
     *
     * 作用是返回一个默认的值，如果MeasureSpec没有强制限制的话则使用提供的大小.否则在允许范围内可任意指定大小
     * 第一个参数size为提供的默认大小，第二个参数为测量的大小
     *
     * @param size
     * @param measureSpec
     * @return
     */
    public static int getDefaultSize(int size, int measureSpec) {
        int result = size;
        int specMode = View.MeasureSpec.getMode(measureSpec);
        int specSize = View.MeasureSpec.getSize(measureSpec);

        switch (specMode) {
            // Mode = UNSPECIFIED时使用提供的默认大小
            case View.MeasureSpec.AT_MOST:
            case View.MeasureSpec.UNSPECIFIED:
                result = size;
                break;
//            // Mode = AT_MOST时 “最多”只能是specSize中指定的大小！
//            case View.MeasureSpec.AT_MOST:
//                if (size>specSize){
//                    result = specSize;
//                }else{
//                    result = size;
//                }
//                break;
            // Mode = EXACTLY时使用测量的大小
            case View.MeasureSpec.EXACTLY:
                result = specSize;
                break;
        }
        return result;
    }
}
