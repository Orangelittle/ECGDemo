package com.dick.ecgdemo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by dick on 2017/8/28.
 * 绘制背景
 */

public class BackGroundView extends View {
    public BackGroundView(Context context) {
        super(context);
    }

    public BackGroundView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BackGroundView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    private float grid_gap;//网格间距
    private int width,height;
    private int grid_hori,grid_ver;//横纵线条数
    private int dataNum_per_grid=18;//每小格内的 数据个数

    private Paint mPaint;
    private Path mPath;
    private PathEffect effect=new DashPathEffect(new float[]{1,5},1);
    private int paintColor= Color.parseColor("#0a7b14");
    private void drawGrid(Canvas canvas){
//        canvas.drawColor(Color.BLACK);
        //横线
        for (int i = 0; i < grid_hori; i++) {
            Paint mPaint=new Paint();
            Path mPath=new Path();
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setColor(paintColor);


            mPath.moveTo(0,grid_gap*i+(height-grid_hori*grid_gap)/2);
            mPath.lineTo(width,grid_gap*i+(height-grid_hori*grid_gap)/2);
            if(i%5!=0){//画虚线
                mPaint.setPathEffect(new DashPathEffect(new float[]{1,5},1));
            }
            canvas.drawPath(mPath,mPaint);
        }
        //竖线
        for (int i = 0; i < grid_ver; i++) {
            Paint mPaint=new Paint();
            Path mPath=new Path();
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setColor(paintColor);
            mPaint.setStrokeWidth(1);
            mPath.moveTo(grid_gap*i+(width-grid_ver*grid_gap)/2,0);
            mPath.lineTo(grid_gap*i+(width-grid_ver*grid_gap)/2,height);
            if(i%5!=0){//画虚线
                mPaint.setPathEffect(new DashPathEffect(new float[]{1,5},1));
            }
            canvas.drawPath(mPath,mPaint);
//            mPaint.reset();
        }
    }

    //网格宽度
    private float mGridWidth = 200f;
    //小网格的宽度
    private float mSGridWidth = 40f;
    //网格颜色
    private int mGridColor = Color.parseColor("#0a7b14");

    //小网格颜色
    private int mSGridColor = Color.parseColor("#83409447");

    private void drawBackground(Canvas canvas) {
        //画小网格

        //竖线个数
        int vSNum = (int) (width /mSGridWidth);

        //横线个数
        int hSNum = (int) (height/mSGridWidth);
        mPaint.setColor(mSGridColor);
        mPaint.setStrokeWidth(2);
        //画竖线
        for(int i = 0;i<vSNum+1;i++){
            canvas.drawLine(i*mSGridWidth,0,i*mSGridWidth,height,mPaint);
        }
        //画横线
        for(int i = 0;i<hSNum+1;i++){

            canvas.drawLine(0,i*mSGridWidth,width,i*mSGridWidth,mPaint);
        }

        //竖线个数
        int vNum = (int) (width / mGridWidth);
        //横线个数
        int hNum = (int) (height / mGridWidth);
        mPaint.setColor(mGridColor);
        mPaint.setStrokeWidth(2);
        //画竖线
        for(int i = 0;i<vNum+1;i++){
            canvas.drawLine(i*mGridWidth,0,i*mGridWidth,height,mPaint);
        }
        //画横线
        for(int i = 0;i<hNum+1;i++){
            canvas.drawLine(0,i*mGridWidth,width,i*mGridWidth,mPaint);
        }

    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {

        if (changed) {
            grid_gap=30.0f;
            width=getWidth();
            height=getHeight();
            grid_hori=height/(int)grid_gap;
            grid_ver=width/(int)grid_gap;
        }


        super.onLayout(changed, left, top, right, bottom);
    }

    private void init(){
        //网格
        mPaint=new Paint();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(paintColor);
        mPaint.setStrokeWidth(1);
        mPath=new Path();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.BLACK);
//        drawBackground(canvas);
        drawGrid(canvas);
    }
}
