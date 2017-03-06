package com.mproject.exercisedemo.dashboarddemo.view;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.mproject.exercisedemo.dashboarddemo.R;
import com.mproject.exercisedemo.dashboarddemo.utils.DisplayUtil;
import com.mproject.exercisedemo.dashboarddemo.utils.MeasureUtils;
import com.mproject.exercisedemo.dashboarddemo.utils.Px2DpUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * 自定义仪表盘
 *
 * @author lcl
 * created by 2017-01-03 16:49
 */
public class DashBoardViewbat extends View implements  Runnable{

    private final String TAG = DashBoardViewbat.class.getSimpleName();
    private Context mContext ;

    /**控件的宽高*/
    private int mWidth;
    private int mHeight;
    private int screenWidth;

    /**刻度的区分 360分成12等份  没等份的刻度*/
    private int scaleInterval;
    /**刻度的宽度*/
    private float scaleWidth;
    /**刻度的高度*/
    private float scaleHeight;
    /**刻度颜色*/
    private int scaleColor;
    /**指示器颜色*/
    private int indicatorColor;
    /**指示器drawable*/
    private Drawable indicatorDrawable;
    /**刻度距离内圆间隔*/
    private float scaleToInnerCircleInterval;
    /**内圆的半径*/
    private float innerCircleRadius;
    /**内圆的颜色*/
    private int innerCircleColor;
    /**内圆的边框大小*/
    private float innerCircleStroke;
    /**外圆的半径大小*/
    private float circumCircleRadius;
    /**外圆的颜色*/
    private int circumCircleColor;
    /**外圆的边框大小*/
    private float circumCircleStroke;

    /**圆的画笔*/
    private Paint mPaint;
    /**刻度的画笔*/
    private Paint mScalePaint;
    /**文字的画笔*/
    private TextPaint mTextPaint;
    /**指示器的文字大小*/
    private int indicatorTextSize;
    /**指示器的文字颜色*/
    private int indicatorTextColor;
    /**指示器的文字对齐方式*/
    private int indicatorTextAlgin;
    /**指示器的文字类型*/
    private int indicatorTextStyle;

    private float defaultAngle;
    private float sweepAngle;
    private float startAngle ;
    private float preAngle = 0;
    private float endAngle;

    private RectF mRectFOuter ;
    private RectF mRectFMid ;
    private RectF mRectFInner ;

    private float paddingLeft ;
    private float paddingTop ;
    /**圆的百分比*/
    private float[] percentAges;
    private int[] percentAgeColors;
    private int[] indicatorDrawableResIds;

    private int indicatorDrawableHeight ;
    private int indicatorDrawableWidth ;

    private int preIndicatorPosition;
    private int sleepTime = 500; //默认是500ms
    private boolean isAnimate = true;//默认是开启动画的
    private List percentIndexLists;
    private float rotaAngle ;
    private double scaleAngle ;
    private Resources res ;


    private int preScaleNum1;
    private int preScaleNum2;
    private int preScaleNum3;
    private int preScaleNum4;


    public DashBoardViewbat(Context context) {
        super(context);
        this.mContext = context;
        init(null, 0);
    }

    public DashBoardViewbat(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        init(attrs, 0);
    }

    public DashBoardViewbat(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mContext = context;
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        //TODO 初始化仪表盘的属性

        res = getResources();

        final TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.DashBoardView, defStyle, 0);

        scaleInterval = a.getInteger(R.styleable.DashBoardView_scaleInterval, 30);
        scaleColor = a.getColor(R.styleable.DashBoardView_scaleColor, Color.RED);
        scaleWidth = a.getDimension(R.styleable.DashBoardView_scaleWidth, Px2DpUtils.dp2Px(mContext, 5));
        scaleHeight = a.getDimension(R.styleable.DashBoardView_scaleHeight, Px2DpUtils.dp2Px(mContext, 10));
        scaleToInnerCircleInterval = a.getDimension(R.styleable.DashBoardView_scaleToInnerCircleInterval, Px2DpUtils.dp2Px(mContext, 5));
        indicatorDrawable = a.getDrawable(R.styleable.DashBoardView_indicatorDrawable);

        //TODO 设置圆的画笔
        initPaint(a);

        //TODO 设置指示器的画笔
        initTextPaint(a);

        percentIndexLists = new ArrayList();

        a.recycle();
    }

    /**
     * 初始化 圆的画笔
     * @param a
     */
    private void initPaint(TypedArray a) {
        //消除锯齿
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mScalePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        innerCircleStroke = a.getDimension(R.styleable.DashBoardView_innerCircleStroke, 0f);
        innerCircleColor = a.getInt(R.styleable.DashBoardView_innerCircleColor, Color.WHITE);
        circumCircleStroke = a.getDimension(R.styleable.DashBoardView_circumCircleStroke, 0f);
        circumCircleColor = a.getInt(R.styleable.DashBoardView_circumCircleColor, Color.RED);
        mPaint.setAntiAlias(true);
        //空心
        mPaint.setStyle(Paint.Style.STROKE);

        mScalePaint.setAntiAlias(true);
        mScalePaint.setStyle(Paint.Style.STROKE);
    }

    /**
     * 初始化指示器画笔
     * @param a
     */
    private void initTextPaint(TypedArray a) {
        //消除锯齿
        mTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        //适配手机密度
        mTextPaint.density = res.getDisplayMetrics().density;
        //指示器文字大小
        indicatorTextSize = a.getInt(R.styleable.DashBoardView_indicatorTextSize, 12);
        mTextPaint.setTextSize(indicatorTextSize);
        //指示器文字颜色
        indicatorTextColor = a.getInt(R.styleable.DashBoardView_indicatorTextColor, Color.WHITE);
        mTextPaint.setColor(indicatorTextColor);
        //设置指示器文字的对齐方式
        indicatorTextAlgin = a.getInt(R.styleable.DashBoardView_indicatorTextAlgin, 1);
        if (indicatorTextAlgin == 0) {
            mTextPaint.setTextAlign(Paint.Align.LEFT);
        } else if (indicatorTextAlgin == 1) {
            mTextPaint.setTextAlign(Paint.Align.CENTER);
        } else if (indicatorTextAlgin == 2) {
            mTextPaint.setTextAlign(Paint.Align.RIGHT);
        }

        //是否设置粗体
        indicatorTextStyle = a.getInt(R.styleable.DashBoardView_indicatorTextStyle, 1);
        if (indicatorTextStyle == 0) {
            //参数skewX为倾斜因子，正数表示向左倾斜，负数表示向右倾斜。
            mTextPaint.setTextSkewX(0.0f);
            mTextPaint.setFakeBoldText(false);
        } else if (indicatorTextStyle == 1) {
            mTextPaint.setTextSkewX(0.0f);
            mTextPaint.setFakeBoldText(true);
        } else if (indicatorTextStyle == 2) {
            mTextPaint.setTextSkewX(-0.25f);
            mTextPaint.setFakeBoldText(false);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        //屏幕宽度的2/3
        screenWidth = DisplayUtil.getWidth((Activity) mContext) ;
        int size = screenWidth * 2 / 3;
        //因为是约等于所以高度会缺少 部分， 这里经过测试用1-3
        paddingTop = getPaddingTop() + 1;
        mWidth = MeasureUtils.getDefaultSize(screenWidth, widthMeasureSpec);
        mHeight = MeasureUtils.getDefaultSize((int) (size + paddingTop), heightMeasureSpec);
        circumCircleRadius = size / 2 ;
        paddingLeft = (screenWidth - size) / 2 ;
        setMeasuredDimension(mWidth, mHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        for (int i=0; i<percentAges.length; i++){
            percentIndexLists.add(i);
        }
        indicatorDrawableHeight = indicatorDrawable.getIntrinsicHeight()/2;
        indicatorDrawableWidth = indicatorDrawable.getIntrinsicWidth()/2;

        //最外面线条
        mRectFOuter = new RectF(paddingLeft, paddingTop, screenWidth*2/3 + paddingLeft, mHeight);
        float circumToInnerInterval = indicatorDrawableHeight + scaleToInnerCircleInterval + scaleHeight;
        innerCircleRadius =  circumCircleRadius - circumToInnerInterval;
        mRectFInner = new RectF(paddingLeft + circumToInnerInterval, paddingTop+circumToInnerInterval, screenWidth*2/3 + paddingLeft - circumToInnerInterval, mHeight - circumToInnerInterval);
        mPaint.setStrokeWidth(innerCircleStroke);
        int defaultColor = res.getColor(R.color.stat_support_default);
        mPaint.setColor(defaultColor);
        canvas.drawArc(mRectFInner, defaultAngle - 3, sweepAngle+ 6, false, mPaint);


        mScalePaint.setColor(defaultColor);
        mScalePaint.setStrokeWidth(scaleWidth);
        float circumCircleRadiusMid = innerCircleRadius + scaleToInnerCircleInterval;//包含刻度的半径
        scaleAngle = Math.PI * (180-defaultAngle) / 180 ;
        float xStart = (float) (paddingLeft + (circumCircleRadius - circumCircleRadiusMid * Math.cos(scaleAngle)));
        float yStart = (float) (paddingTop+circumCircleRadius + circumCircleRadiusMid * Math.sin(scaleAngle));
        float xEndLong = (float) (xStart - scaleHeight * Math.cos(scaleAngle));
        float yEndLong = (float) (yStart + scaleHeight * Math.sin(scaleAngle));
        float xEndShort = (float) (xStart - scaleHeight * Math.cos(scaleAngle) * 3/5);
        float yEndShort = (float) (yStart + scaleHeight * Math.sin(scaleAngle) * 3/5);
        //旋转的角度
        rotaAngle = sweepAngle / scaleInterval;
        rotateScale(canvas, xStart, yStart, xEndLong, yEndLong, xEndShort, yEndShort);
        drawIndicator(canvas, scaleAngle, preAngle, indicatorDrawableHeight, indicatorDrawableWidth, indicatorDrawable);
        super.onDraw(canvas);
        Log.i(TAG, sweepAngle + "=====preAngle=====" + preAngle);
        if (percentAges.length>0){
            canvas.save();
            if (isAnimate){
                //TODO 开启动画效果的
                Log.i(TAG, "=====开启开启动画效果   ondraw==");
                if (preAngle<=sweepAngle){
                    setPercentValueAnimate(canvas, xStart, yStart, xEndLong, yEndLong, xEndShort, yEndShort);
                    if (preAngle<sweepAngle+rotaAngle){

                    }
                }
            }else{
                //TODO 不开启开启动画效果的
                Log.i(TAG, "=====不开启开启动画效果的==");
                drawScale(canvas, xStart, yStart, xEndLong, yEndLong, xEndShort, yEndShort, rotaAngle);
                for (int i=0; i<percentAges.length; i++){
                    setPercentValue(canvas, preAngle, i);
                }
                int indicatorIndex = (int) percentIndexLists.get(percentIndexLists.size()-1);
                Log.i(TAG, "=====最后的索引,也就是最大的索引=="+indicatorIndex);
                Drawable indicatorDrawableArgs = res.getDrawable(indicatorDrawableResIds[indicatorIndex]);
                drawIndicator(canvas, scaleAngle, preAngle, indicatorDrawableHeight, indicatorDrawableWidth, indicatorDrawableArgs);
            }
            canvas.restore();
        }

        mPaint.setColor(Color.BLUE);
        mPaint.setStrokeWidth(1);
        canvas.drawArc(mRectFOuter, defaultAngle - 3, sweepAngle+ 6, false, mPaint);
    }

    /**
     * 画指示器
     *
     * @param canvas
     * @param scaleAngle
     * @param rotaAngleCur
     * @param indicatorDrawableHeight
     * @param indicatorDrawableWidth
     */
    private void drawIndicator(Canvas canvas, double scaleAngle, float rotaAngleCur, int indicatorDrawableHeight, int indicatorDrawableWidth, Drawable indicatorDrawableArgs) {
        //绘制时针
        canvas.save();
        float x1 = (float) (paddingLeft + circumCircleRadius - circumCircleRadius * Math.cos(scaleAngle));
        float y1 = (float) (paddingTop + circumCircleRadius + circumCircleRadius * Math.sin(scaleAngle));
        Rect rect = indicatorDrawableArgs.copyBounds();
        rect.top = (int) (y1 - indicatorDrawableHeight);
        rect.bottom = (int) (y1);
        rect.left = (int) (x1 - indicatorDrawableWidth/2);
        rect.right = (int) (x1 + indicatorDrawableWidth/2);
        indicatorDrawableArgs.setBounds(rect);
        canvas.rotate(rotaAngleCur + 0.4f, screenWidth/2, circumCircleRadius);
        canvas.rotate(defaultAngle-90,x1, y1);
        indicatorDrawableArgs.draw(canvas);
        canvas.restore();

    }

    /**
     * 画指示器
     *
     * @param canvas
     * @param scaleAngle
     * @param rotaAngleCur
     * @param indicatorDrawableHeight
     * @param indicatorDrawableWidth
     */
    private void drawIndicatorAnimate(Canvas canvas, double scaleAngle, float rotaAngleCur, int indicatorDrawableHeight, int indicatorDrawableWidth, Drawable indicatorDrawableArgs) {
        //绘制时针
        canvas.save();
        float x1 = (float) (paddingLeft + circumCircleRadius - circumCircleRadius * Math.cos(scaleAngle));
        float y1 = (float) (paddingTop + circumCircleRadius + circumCircleRadius * Math.sin(scaleAngle));
        Rect rect = indicatorDrawableArgs.copyBounds();
        rect.top = (int) (y1 - indicatorDrawableHeight);
        rect.bottom = (int) (y1);
        rect.left = (int) (x1 - indicatorDrawableWidth/2);
        rect.right = (int) (x1 + indicatorDrawableWidth/2);
        indicatorDrawableArgs.setBounds(rect);
        canvas.rotate(rotaAngleCur + 0.4f, screenWidth/2, circumCircleRadius);
        canvas.rotate(defaultAngle-90,x1, y1);
        indicatorDrawableArgs.draw(canvas);
        canvas.restore();

    }

    /**
     * 画刻度线
     *
     * @param canvas
     * @param xStart
     * @param yStart
     * @param xEndLong
     * @param yEndLong
     * @param xEndShort
     * @param yEndShort
     */
    private void rotateScale(Canvas canvas, float xStart, float yStart, float xEndLong, float yEndLong,
                             float xEndShort,float yEndShort) {
        //两种方法
        //方法一 save 然后回退
        //方法二 就是反转
        canvas.save();
        for (int i = 0; i <= scaleInterval; i++) {
            if (i % 5 == 0) {
                mScalePaint.setStrokeWidth(scaleWidth*3);
                canvas.drawLine(xStart, yStart, xEndLong, yEndLong, mScalePaint);
            } else {
                mScalePaint.setStrokeWidth(scaleWidth);
                canvas.drawLine(xStart, yStart, xEndShort, yEndShort, mScalePaint);
            }
            canvas.rotate(rotaAngle,  mWidth/2, mHeight/2);
        }
        canvas.restore();
    }

    /**
     * 给刻度线着色
     *
     * @param canvas
     * @param xStart
     * @param yStart
     * @param xEndLong
     * @param yEndLong
     * @param xEndShort
     * @param yEndShort
     * @param rotaAngle
     */
    private void drawScale(Canvas canvas, float xStart, float yStart, float xEndLong, float yEndLong,
                             float xEndShort,float yEndShort, float rotaAngle) {
        float rotaAngleTotal = 0 ;
        for (int i = 0; i <= scaleInterval; i++) {
            rotaAngleTotal += rotaAngle ;
            if (rotaAngleTotal < percentAges[0] * sweepAngle + rotaAngle){
                mScalePaint.setColor(res.getColor(percentAgeColors[0]));
            }else if (rotaAngleTotal < (percentAges[0]+percentAges[1]) * sweepAngle + rotaAngle){
                mScalePaint.setColor(res.getColor(percentAgeColors[1]));
            }else if (rotaAngleTotal < (percentAges[0]+percentAges[1]+percentAges[2]) * sweepAngle + rotaAngle){
                mScalePaint.setColor(res.getColor(percentAgeColors[2]));
            }else if (rotaAngleTotal < sweepAngle + rotaAngle){
                mScalePaint.setColor(res.getColor(percentAgeColors[3]));
            }
            if (i % 5 == 0) {
                mScalePaint.setStrokeWidth(scaleWidth*3);
                canvas.drawLine(xStart, yStart, xEndLong, yEndLong, mScalePaint);
            } else {
                mScalePaint.setStrokeWidth(scaleWidth);
                canvas.drawLine(xStart, yStart, xEndShort, yEndShort, mScalePaint);
            }
            canvas.rotate(rotaAngle,  mWidth/2, mHeight/2);
        }
        //通过旋转画布 绘制右面的刻度
        canvas.rotate(-rotaAngle * (scaleInterval+1), mWidth/2, mHeight/2);
    }

    private void rotateScaleOld(Canvas canvas, int indicatorDrawableHeight, float circumToInnerInterval, float rotaAngle, int roteteDirection) {

        float rotaAngleTmp = 0f;
        if (roteteDirection==0){
            //TODO  向右旋转
            rotaAngleTmp = rotaAngle ;
        }else if (roteteDirection==1){
            //TODO  向左旋转
            rotaAngleTmp = -rotaAngle ;
        }
        for (int i = 0; i < scaleInterval/2; i++) {
            float scaleHeightStartTmp = 0 ;
            float scaleHeightEndTmp = 0 ;
            if (i % 5 == 0) {
                scaleHeightEndTmp = circumToInnerInterval ;
            } else {
                scaleHeightEndTmp = paddingTop + indicatorDrawableHeight + scaleHeight / 2 ;
            }
            canvas.rotate(rotaAngleTmp, screenWidth/2, circumCircleRadius);
            canvas.drawLine(paddingLeft + circumToInnerInterval + innerCircleRadius, paddingTop + indicatorDrawableHeight,
                            paddingLeft + circumToInnerInterval + innerCircleRadius, scaleHeightEndTmp, mScalePaint);
        }
        //通过旋转画布 绘制右面的刻度
//        canvas.rotate(-rotaAngleTmp * scaleInterval / 2, paddingLeft + circumCircleRadius, circumCircleRadius);
        canvas.rotate(-rotaAngleTmp * scaleInterval / 2, screenWidth/2, circumCircleRadius);
    }

    /**
     * 设值等份
     *
     * @param canvas
     * @param preAngleArgs
     * @param percentType
     */
    private void setPercentValue(Canvas canvas, float preAngleArgs, int percentType) {
        mPaint.setColor(res.getColor(percentAgeColors[percentType]));
        startAngle = getDefaultAngle() + preAngleArgs ;
        endAngle = percentAges[percentType] * sweepAngle ;
        preAngle += endAngle ;
        if (endAngle>0){
            canvas.drawArc(mRectFInner, startAngle, endAngle, false, mPaint);
        }else if (endAngle==0){
            percentIndexLists.remove(percentIndexLists.indexOf(percentType));
        }
    }

    /**
     * 设置带动画的等份
     *
     * @param canvas
     * @param xStart
     * @param yStart
     * @param xEndLong
     * @param yEndLong
     * @param xEndShort
     * @param yEndShort
     */
    private void setPercentValueAnimate(Canvas canvas, float xStart, float yStart, float xEndLong, float yEndLong,
                                        float xEndShort,float yEndShort) {
        float percentAngle1 = percentAges[0] * sweepAngle;
        float percentAngle2 = percentAges[1] * sweepAngle;
        float percentAngle3 = percentAges[2] * sweepAngle;
        float percentAngle4 = percentAges[3] * sweepAngle;



        Log.i(TAG, "=percentAngle4=="+ percentAngle4);
        int scaleNum = (int) (preAngle / rotaAngle);
        if (preAngle <= percentAngle1 + rotaAngle){
            drawIndicator(canvas, scaleAngle, preAngle, indicatorDrawableHeight, indicatorDrawableWidth, res.getDrawable(indicatorDrawableResIds[0]));
            preScaleNum1 = (int) (percentAngle1 / rotaAngle) ;
            Log.i(TAG, "=000===刻度线个数=scaleNum=="+ scaleNum);
            mScalePaint.setColor(res.getColor(percentAgeColors[0]));
            mPaint.setColor(res.getColor(percentAgeColors[0]));
            startAngle = getDefaultAngle();
            endAngle = preAngle ;
        }else if (preAngle <= percentAngle1 + percentAngle2 + rotaAngle){
            drawIndicator(canvas, scaleAngle, preAngle, indicatorDrawableHeight, indicatorDrawableWidth, res.getDrawable(indicatorDrawableResIds[1]));
            Log.i(TAG, "=111===刻度线个数=scaleNum=="+ scaleNum);
            mScalePaint.setColor(res.getColor(percentAgeColors[1]));
            mPaint.setColor(res.getColor(percentAgeColors[1]));
            startAngle = getDefaultAngle() +  percentAngle1 + rotaAngle;
            endAngle = preAngle - percentAngle1;

        }else if (preAngle <= percentAngle1 + percentAngle2 + percentAngle3 + rotaAngle){
            drawIndicator(canvas, scaleAngle, preAngle, indicatorDrawableHeight, indicatorDrawableWidth, res.getDrawable(indicatorDrawableResIds[2]));
            preScaleNum3 = scaleNum - preScaleNum1 - preScaleNum2 ;
            Log.i(TAG, "=111===刻度线个数=scaleNum=="+ scaleNum);
            mScalePaint.setColor(res.getColor(percentAgeColors[2]));
            mPaint.setColor(res.getColor(percentAgeColors[2]));
            startAngle = getDefaultAngle() +  percentAngle1 + percentAngle2 + rotaAngle;
            endAngle = preAngle - percentAngle1 - percentAngle2;
        }else if (preAngle <= sweepAngle + rotaAngle){
            drawIndicator(canvas, scaleAngle, preAngle, indicatorDrawableHeight, indicatorDrawableWidth, res.getDrawable(indicatorDrawableResIds[3]));
            preScaleNum4 = scaleNum - preScaleNum1 - preScaleNum2 - preScaleNum3;
            Log.i(TAG, "=111===刻度线个数=scaleNum=="+ scaleNum);
            mScalePaint.setColor(res.getColor(percentAgeColors[3]));
            mPaint.setColor(res.getColor(percentAgeColors[3]));
            startAngle = getDefaultAngle() +  percentAngle1 + percentAngle2 + percentAngle3 + rotaAngle;
            endAngle = preAngle - percentAngle1 - percentAngle3 - percentAngle2;
        }
        drawScaleInterval(canvas,scaleNum, xStart, yStart, xEndLong, yEndLong, xEndShort, yEndShort);
        canvas.drawArc(mRectFInner, startAngle, endAngle, false, mPaint);
        post(this);




//        Log.i(TAG, "=pre=="+ preAngle);
//        if (percentAngle1>0){
//                Log.i(TAG, "=percentAngle1=="+ percentAngle1);
//                drawIndicator(canvas, scaleAngle, preAngle, indicatorDrawableHeight, indicatorDrawableWidth, res.getDrawable(indicatorDrawableResIds[0]));
//                int scaleNum = (int) (preAngle / rotaAngle);
//                preScaleNum1 = (int) (percentAngle1 / rotaAngle) ;
//                Log.i(TAG, "=000===刻度线个数=scaleNum=="+ scaleNum);
//                mScalePaint.setColor(res.getColor(percentAgeColors[0]));
//                drawScaleInterval(canvas,scaleNum, xStart, yStart, xEndLong, yEndLong, xEndShort, yEndShort);
//                mPaint.setColor(res.getColor(percentAgeColors[0]));
//                startAngle = getDefaultAngle();
//                endAngle = preAngle ;
//                canvas.drawArc(mRectFInner, startAngle, endAngle, false, mPaint);
//                post(this);
//        }
//        if (percentAngle2>0){
//                Log.i(TAG, "=percentAngle2=="+ percentAngle2);
//                if (preAngle <= percentAngle1 + rotaAngle){
//                    drawIndicator(canvas, scaleAngle, preAngle, indicatorDrawableHeight, indicatorDrawableWidth, res.getDrawable(indicatorDrawableResIds[0]));
//                }else if (preAngle <= percentAngle1 + percentAngle2 + rotaAngle){
//                    drawIndicator(canvas, scaleAngle, preAngle, indicatorDrawableHeight, indicatorDrawableWidth, res.getDrawable(indicatorDrawableResIds[1]));
//                }
//                int scaleNum = (int) (preAngle / rotaAngle);
//                preScaleNum2 = scaleNum - preScaleNum1 ;
//                Log.i(TAG, "=111===刻度线个数=scaleNum=="+ scaleNum);
//                mScalePaint.setColor(res.getColor(percentAgeColors[1]));
//                drawScaleInterval(canvas,scaleNum, xStart, yStart, xEndLong, yEndLong, xEndShort, yEndShort);
//                mPaint.setColor(res.getColor(percentAgeColors[1]));
//                startAngle = getDefaultAngle() +  percentAngle1 + rotaAngle;
//                endAngle = preAngle - percentAngle1;
//                canvas.drawArc(mRectFInner, startAngle, endAngle, false, mPaint);
//                post(this);
//        }
//        if (percentAngle3>0){
//                Log.i(TAG, "=percentAngle3=="+ percentAngle3);
//                if (preAngle <= percentAngle1 + rotaAngle){
//                    drawIndicator(canvas, scaleAngle, preAngle, indicatorDrawableHeight, indicatorDrawableWidth, res.getDrawable(indicatorDrawableResIds[0]));
//                }else if (preAngle <= percentAngle1 + percentAngle2 + rotaAngle){
//                    drawIndicator(canvas, scaleAngle, preAngle, indicatorDrawableHeight, indicatorDrawableWidth, res.getDrawable(indicatorDrawableResIds[1]));
//                }else if (preAngle <= percentAngle1 + percentAngle2 + percentAngle2 + rotaAngle){
//                    drawIndicator(canvas, scaleAngle, preAngle, indicatorDrawableHeight, indicatorDrawableWidth, res.getDrawable(indicatorDrawableResIds[2]));
//                }
//                int scaleNum = (int) (preAngle / rotaAngle);
//                preScaleNum3 = scaleNum - preScaleNum1 - preScaleNum2 ;
//                Log.i(TAG, "=111===刻度线个数=scaleNum=="+ scaleNum);
//                mScalePaint.setColor(res.getColor(percentAgeColors[2]));
//                drawScaleInterval(canvas,scaleNum, xStart, yStart, xEndLong, yEndLong, xEndShort, yEndShort);
//                mPaint.setColor(res.getColor(percentAgeColors[2]));
//                startAngle = getDefaultAngle() +  percentAngle1 + percentAngle2 + rotaAngle;
//                endAngle = preAngle - percentAngle1 - percentAngle2;
//                canvas.drawArc(mRectFInner, startAngle, endAngle, false, mPaint);
//                post(this);
//        }
//        if (percentAngle4>0){
//                Log.i(TAG, "=percentAngle4=="+ percentAngle4);
//                if (preAngle <= percentAngle1 + rotaAngle){
//                    drawIndicator(canvas, scaleAngle, preAngle, indicatorDrawableHeight, indicatorDrawableWidth, res.getDrawable(indicatorDrawableResIds[0]));
//                }else if (preAngle <= percentAngle1 + percentAngle2 + rotaAngle){
//                    drawIndicator(canvas, scaleAngle, preAngle, indicatorDrawableHeight, indicatorDrawableWidth, res.getDrawable(indicatorDrawableResIds[1]));
//                }else if (preAngle <= percentAngle1 + percentAngle2 + percentAngle3 + rotaAngle){
//                    drawIndicator(canvas, scaleAngle, preAngle, indicatorDrawableHeight, indicatorDrawableWidth, res.getDrawable(indicatorDrawableResIds[2]));
//                }else if (preAngle <= sweepAngle + rotaAngle){
//                    drawIndicator(canvas, scaleAngle, preAngle, indicatorDrawableHeight, indicatorDrawableWidth, res.getDrawable(indicatorDrawableResIds[3]));
//                }
//
//                int scaleNum = (int) (preAngle / rotaAngle);
//                preScaleNum4 = scaleNum - preScaleNum1 - preScaleNum2 - preScaleNum3;
//                Log.i(TAG, "=111===刻度线个数=scaleNum=="+ scaleNum);
//                mScalePaint.setColor(res.getColor(percentAgeColors[3]));
//                drawScaleInterval(canvas,scaleNum, xStart, yStart, xEndLong, yEndLong, xEndShort, yEndShort);
//                mPaint.setColor(res.getColor(percentAgeColors[3]));
//                startAngle = getDefaultAngle() +  percentAngle1 + percentAngle2 + percentAngle3 + rotaAngle;
//                endAngle = preAngle - percentAngle1 - percentAngle3 - percentAngle2;
//                canvas.drawArc(mRectFInner, startAngle, endAngle, false, mPaint);
//                post(this);
//        }

//        percentAngleValue(canvas, xStart, yStart, xEndLong, yEndLong, xEndShort, yEndShort, percentAngle1, 0);
//        Drawable indicatorDrawableArgs = null ;
//        int indicatorDrawableResId = R.drawable.stat_support_indicate_default;
//        int colorResId = R.color.stat_support_default;
////        float percentArg = 0 ;
//        int index = 0;
//        if (preAngle <= percentAges[0] * sweepAngle + rotaAngle){
//            index = 0 ;
//            colorResId = percentAgeColors[0] ;
////            percentArg = percentAges[0];
//            indicatorDrawableResId = indicatorDrawableResIds[0];
//        }else if (preAngle <= (percentAges[0]+percentAges[1]) * sweepAngle + rotaAngle){
//            index = 1 ;
//            colorResId = percentAgeColors[1] ;
////            percentArg = percentAges[1];
//            indicatorDrawableResId = indicatorDrawableResIds[1];
//        }else if (preAngle <= (percentAges[0]+percentAges[1]+percentAges[2]) * sweepAngle + rotaAngle){
//            index = 2 ;
//            colorResId = percentAgeColors[2] ;
////            percentArg = percentAges[2];
//            indicatorDrawableResId = indicatorDrawableResIds[2];
//        }else if (preAngle <= sweepAngle + rotaAngle){
//            index = 3 ;
//            colorResId = percentAgeColors[3] ;
////            percentArg = percentAges[3];
//            indicatorDrawableResId = indicatorDrawableResIds[3];
////                Log.i(TAG, rotaAngleTotal+"====rotaAngleTotal=3333=="+percentAges[3] * sweepAngle);
//        }
//        Log.i(TAG, colorResId+"====colorResId==="+percentAgeColors[0]);
//        mScalePaint.setColor(res.getColor(colorResId));
//        indicatorDrawableArgs = res.getDrawable(indicatorDrawableResId);
//        drawIndicator(canvas, scaleAngle, preAngle, indicatorDrawableHeight, indicatorDrawableWidth, indicatorDrawableArgs);
//        if (preAngle % rotaAngle == 0) {
//            if (preAngle % 5 == 0 ){
//                mScalePaint.setStrokeWidth(scaleWidth*3);
//                canvas.drawLine(xStart, yStart, xEndLong, yEndLong, mScalePaint);
//            }else {
//                mScalePaint.setStrokeWidth(scaleWidth);
//                canvas.drawLine(xStart, yStart, xEndShort, yEndShort, mScalePaint);
//            }
//            canvas.rotate(preAngle,  mWidth/2, mHeight/2);
//        }
//        mPaint.setColor(res.getColor(colorResId));
//        startAngle = getDefaultAngle() + preAngle ;
//        endAngle = preAngle ;

    }

    private void percentAngleValue(Canvas canvas, float xStart, float yStart, float xEndLong, float yEndLong, float xEndShort, float yEndShort, float percentAngle, int percentType) {

//            canvas.save();
//            Log.i(TAG, "====colorResId==="+percentAgeColors[0]);
        drawIndicator(canvas, scaleAngle, preAngle, indicatorDrawableHeight, indicatorDrawableWidth, res.getDrawable(indicatorDrawableResIds[percentType]));
//            Log.i(TAG, "====percentAngle1==="+(percentAngle1+rotaAngle));
//            int scaleNum = (int) ((percentAngle1 + rotaAngle) / rotaAngle);
        int scaleNum = (int) (preAngle / rotaAngle);
        mScalePaint.setColor(res.getColor(percentAgeColors[percentType]));
//            Log.i(TAG, preAngle + "====刻度线的个数==="+scaleNum);
//        drawScaleInterval(canvas,scaleNum, xStart, yStart, xEndLong, yEndLong, xEndShort, yEndShort);

        if (preAngle <= percentAngle + rotaAngle ){
            // 开始动态的画刻度
            Log.i(TAG, "====开始画弧度==="+preAngle);
            Log.i(TAG, preAngle + "====开始画刻度==="+preAngle % rotaAngle);
////              int scaleNum = (int) ((percentAngle1 + rotaAngle) / rotaAngle);
////                int scaleNum = (int) (preAngle / rotaAngle);
////                Log.i(TAG, preAngle + "====刻度线的个数==="+scaleNum);
//
//                drawScaleInterval(canvas,5, xStart, yStart, xEndLong, yEndLong, xEndShort, yEndShort);
//                if (preAngle % rotaAngle == 0) {
//                    if (preAngle % 5 == 0 ){
//                        mScalePaint.setStrokeWidth(scaleWidth*3);
//                        canvas.drawLine(xStart, yStart, xEndLong, yEndLong, mScalePaint);
//                    }else {
//                        mScalePaint.setStrokeWidth(scaleWidth);
//                        canvas.drawLine(xStart, yStart, xEndShort, yEndShort, mScalePaint);
//                    }
//                    canvas.rotate(preAngle,  mWidth/2, mHeight/2);
//                }
            mPaint.setColor(res.getColor(percentAgeColors[0]));
            startAngle = getDefaultAngle()  ;
            canvas.drawArc(mRectFInner, startAngle, preAngle, false, mPaint);
            if (preAngle < percentAngle + rotaAngle){
                post(this);
            }
        }
    }

    private void drawScaleInterval(Canvas canvas, int scaleNum, float xStart, float yStart, float xEndLong, float yEndLong, float xEndShort, float yEndShort) {
        canvas.save();
        drawInterval(canvas, scaleNum, xStart, yStart, xEndLong, yEndLong, xEndShort, yEndShort);
        canvas.restore();
    }

    private void drawInterval(Canvas canvas, int scaleNum, float xStart, float yStart, float xEndLong, float yEndLong, float xEndShort, float yEndShort) {
        Log.i(TAG, scaleNum + "===111===刻度线个数=preScaleNum1=="+ preScaleNum1);
        Log.i(TAG, "===222===刻度线个数=preScaleNum2=="+ preScaleNum2);
        Log.i(TAG, "=1333===刻度线个数=preScaleNum3=="+ preScaleNum3);
        Log.i(TAG, "=1444===刻度线个数=preScaleNum4=="+ preScaleNum4);
        for (int i = 0; i <= scaleNum; i++) {
            if (i<=preScaleNum1){
                mScalePaint.setColor(res.getColor(percentAgeColors[0]));
            }else if (i<= preScaleNum1 + preScaleNum2){
                mScalePaint.setColor(res.getColor(percentAgeColors[1]));
            }else if (i<= preScaleNum1 + preScaleNum2 + preScaleNum3){
                mScalePaint.setColor(res.getColor(percentAgeColors[2]));
            }else if (i<=scaleInterval){
                mScalePaint.setColor(res.getColor(percentAgeColors[3]));
            }
            if (i % 5 == 0) {
//                Log.i(TAG, preAngle + "====开始画长刻度111==="+preAngle % 5);
                mScalePaint.setStrokeWidth(scaleWidth*3);
                canvas.drawLine(xStart, yStart, xEndLong, yEndLong, mScalePaint);
            } else {
//                Log.i(TAG, preAngle + "====开始画短刻度222==="+preAngle % 5);
                mScalePaint.setStrokeWidth(scaleWidth);
                canvas.drawLine(xStart, yStart, xEndShort, yEndShort, mScalePaint);
            }
            canvas.rotate(rotaAngle,  mWidth/2, mHeight/2);
        }
//        canvas.rotate(rotaAngle,  mWidth/2, mHeight/2);
//        if (scaleNum % 5 == 0) {
//            Log.i(TAG, preAngle + "====开始画长刻度111==="+preAngle % 5);
//            mScalePaint.setStrokeWidth(scaleWidth*3);
//            canvas.drawLine(xStart, yStart, xEndLong, yEndLong, mScalePaint);
//        } else {
//            Log.i(TAG, preAngle + "====开始画短刻度222==="+preAngle % 5);
//            mScalePaint.setStrokeWidth(scaleWidth);
//            canvas.drawLine(xStart, yStart, xEndShort, yEndShort, mScalePaint);
//        }
//        canvas.rotate(rotaAngle * scaleNum,  mWidth/2, mHeight/2);
    }

    @Override
    public void run() {
        if (isAnimate){
            //TODO 如果开启动画
            if (percentAges.length>0){
                //TODO 如果有等份我们就开始动画
                Log.i(TAG, "=====开启开启动画效果   run==");
                preAngle++;
                try {
                    Thread.sleep(sleepTime);
                    postInvalidate();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public int getScaleInterval() {
        return scaleInterval;
    }

    public void setScaleInterval(int scaleInterval) {
        this.scaleInterval = scaleInterval;
    }

    public int getScaleColor() {
        return scaleColor;
    }

    public void setScaleColor(int scaleColor) {
        this.scaleColor = scaleColor;
    }

    public int getIndicatorColor() {
        return indicatorColor;
    }

    public void setIndicatorColor(int indicatorColor) {
        this.indicatorColor = indicatorColor;
    }

    public Drawable getIndicatorDrawable() {
        return indicatorDrawable;
    }

    public void setIndicatorDrawable(Drawable indicatorDrawable) {
        this.indicatorDrawable = indicatorDrawable;
    }

    public float getScaleToInnerCircleInterval() {
        return scaleToInnerCircleInterval;
    }

    public void setScaleToInnerCircleInterval(float scaleToInnerCircleInterval) {
        this.scaleToInnerCircleInterval = scaleToInnerCircleInterval;
        invalidate();
    }

    public double getInnerCircleRadius() {
        return innerCircleRadius;
    }

    public void setInnerCircleRadius(float innerCircleRadius) {
        this.innerCircleRadius = innerCircleRadius;
        invalidate();
    }

    public int getInnerCircleColor() {
        return innerCircleColor;
    }

    public void setInnerCircleColor(int innerCircleColor) {
        this.innerCircleColor = innerCircleColor;
        invalidate();
    }

    public float getInnerCircleStroke() {
        return innerCircleStroke;
    }

    public void setInnerCircleStroke(float innerCircleStroke) {
        this.innerCircleStroke = innerCircleStroke;
        invalidate();
    }

    public double getCircumCircleRadius() {
        return circumCircleRadius;
    }

    public void setCircumCircleRadius(float circumCircleRadius) {
        this.circumCircleRadius = circumCircleRadius;
        invalidate();
    }

    public int getCircumCircleColor() {
        return circumCircleColor;
    }

    public void setCircumCircleColor(int circumCircleColor) {
        this.circumCircleColor = circumCircleColor;
        invalidate();
    }

    public double getCircumCircleStroke() {
        return circumCircleStroke;
    }

    public void setCircumCircleStroke(float circumCircleStroke) {
        this.circumCircleStroke = circumCircleStroke;
    }

    public Paint getmPaint() {
        return mPaint;
    }


    public TextPaint getmTextPaint() {
        return mTextPaint;
    }

    public int getIndicatorTextSize() {
        return indicatorTextSize;
    }

    public void setIndicatorTextSize(int indicatorTextSize) {
        this.indicatorTextSize = indicatorTextSize;
        invalidate();
    }

    public int getIndicatorTextColor() {
        return indicatorTextColor;
    }

    public void setIndicatorTextColor(int indicatorTextColor) {
        this.indicatorTextColor = indicatorTextColor;
        invalidate();
    }

    public int getIndicatorTextAlgin() {
        return indicatorTextAlgin;
    }

    public void setIndicatorTextAlgin(int indicatorTextAlgin) {
        this.indicatorTextAlgin = indicatorTextAlgin;
    }

    public int getIndicatorTextStyle() {
        return indicatorTextStyle;
    }

    public void setIndicatorTextStyle(int indicatorTextStyle) {
        this.indicatorTextStyle = indicatorTextStyle;
        invalidate();
    }

    public float[] getPercentAges() {
        return percentAges;
    }

    public void setPercentAges(float[] percentAges) {
        this.percentAges = percentAges;
        invalidate();
    }

    public int[] getPercentAgeColors() {
        return percentAgeColors;
    }

    public void setPercentAgeColors(int[] percentAgeColors) {
        this.percentAgeColors = percentAgeColors;
        invalidate();
    }

    public float getDefaultAngle() {
        return defaultAngle;
    }

    public void setDefaultAngle(float defaultAngle) {
        this.defaultAngle = defaultAngle;
        invalidate();
    }

    public float getSweepAngle() {
        return sweepAngle;
    }

    public void setSweepAngle(float sweepAngle) {
        this.sweepAngle = sweepAngle;
        invalidate();
    }

    public float getScaleWidth() {
        return scaleWidth;
    }

    public void setScaleWidth(float scaleWidth) {
        this.scaleWidth = scaleWidth;
        invalidate();
    }

    public int[] getIndicatorDrawableResIds() {
        return indicatorDrawableResIds;
    }

    public void setIndicatorDrawableResIds(int[] indicatorDrawableResIds) {
        this.indicatorDrawableResIds = indicatorDrawableResIds;
    }

    public int getSleepTime() {
        return sleepTime;
    }

    public void setSleepTime(int sleepTime) {
        this.sleepTime = sleepTime;
    }

    public boolean isAnimate() {
        return isAnimate;
    }

    public void setAnimate(boolean animate) {
        isAnimate = animate;
    }
}
