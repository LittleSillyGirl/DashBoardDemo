package com.mproject.exercisedemo.dashboarddemo;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.mproject.exercisedemo.dashboarddemo.view.DashBoardView;

public class MainActivity extends AppCompatActivity {
    private TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DashBoardView dbv = (DashBoardView) findViewById(R.id.dbv);
        tv = (TextView) findViewById(R.id.tv);
        float[] percentAges = new float[4];
        int[] percentAgeColors = new int[4];
        int[] indicatorDrawableResIds = new int[4];
        for (int i=0;i<4;i++){
            float percentAge = 0 ;
            int percentAgeColor = 0 ;
            int indicatorDrawableResId = 0;
            switch (i){
                case 0:
                    percentAge = 1f ;
                    percentAge = 0.4f ;
                    percentAge = 0f ;
                    percentAge = 0.6f ;
                    percentAgeColor = R.color.stat_support_signed;
                    indicatorDrawableResId =R.drawable.stat_support_indicate_signed;
                    break;
                case 1:
                    percentAge = 0.2f ;
                    percentAge = 0f ;
                    percentAgeColor =  R.color.stat_support_signing;
                    indicatorDrawableResId =R.drawable.stat_support_indicate_signing;
                    break;
                case 2:
                    percentAge = 0.7f ;
                    percentAge = 0.1f ;
                    percentAge = 0f ;
                    percentAgeColor = R.color.stat_support_propeling;
                    indicatorDrawableResId =R.drawable.stat_support_indicate_propeling;
                    break;
                case 3:
                    percentAge = 0.3f ;
                    percentAge = 0f ;
                    percentAge = 0.4f ;
                    percentAgeColor = R.color.stat_support_supporting;
                    indicatorDrawableResId =R.drawable.stat_support_indicate_supporting;
                    break;
            }
            percentAges[i] = percentAge ;
            percentAgeColors[i] = percentAgeColor ;
            indicatorDrawableResIds[i] = indicatorDrawableResId ;

        }
        dbv.setDefaultAngle(135);
        dbv.setSweepAngle(270);
        dbv.setPercentAgeColors(percentAgeColors);
//        dbv.setPercentAges(null);
//        dbv.setPercentAges(new float[]{});
        dbv.setPercentAges(percentAges);
        dbv.setIndicatorDrawableResIds(indicatorDrawableResIds);
        dbv.setSleepTime(50);
        dbv.setAnimate(true);
        dbv.setmOnUpdateProgress(new DashBoardView.OnUpdateProgress() {
            @Override
            public void updateProgress(float progressAngle) {
//                tv.setText((int) ((progressAngle/270)*200));
                Log.i("etst", "============" + (int) ((progressAngle/270)*200));
            }
        });
    }




}
