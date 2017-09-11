package com.dick.ecgdemo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.widget.Scroller;

import com.dick.ecgdemo.data.Entry;
import com.dick.ecgdemo.data.Line;
import com.dick.ecgdemo.data.Lines;
import com.dick.ecgdemo.render.LineRender;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by King on 2017/8/26.
 */

public class EcgView extends View {


    private int width, height;

//    private float y_center;//中心y值

    private float x_change;//滑动查看时，X坐标的变化
    private static float x_changed;
    private static float startX;//手指touch屏幕时候的X坐标
    private float offset_x_max;//X轴最大偏移量

    private int rect_high = 120;//下方用于显示心电图的矩形的高度
    private float rect_width;//下方用于显示心电图的矩形的宽度
    private float rect_gap_x;//下方矩形区域心电图横坐标的间隔
    private float multiple_for_rect_width;//矩形区域的宽于屏幕款的比

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (changed) {

//            y_center=(height+rect_high)/2;
            x_change = 0.0f;
            x_changed = 0.0f;

            _YSize = (int) (_MaxYNumber / _EveryOneValue);// 垂直格子数量
            _LatticeWidth = (int) (height / _YSize);//Y轴间隔
//            _LatticeWidth = (int) (y_center / _YSize);//Y轴间隔
            _XUnitLength = width / (_PointMaxAmount - 1);// X轴的间距
        }

        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        width = w;
        height = h;
        super.onSizeChanged(w, h, oldw, oldh);
    }

    private void init() {
        //初始化折线画笔
        _PaintLine = new Paint();
        _PaintLine.setStrokeWidth(2.5f);
        _PaintLine.setColor(Color.RED);
        _PaintLine.setStyle(Paint.Style.STROKE);
        _PaintLine.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);


        canvas.translate(0, height);
        canvas.scale(1, -1);

//        canvas.translate(width, 0);
//        canvas.scale(-1, 1);

//        canvas.translate(width, height);
//        canvas.scale(-1, -1);

        drawECG(canvas);
    }


    public EcgView(Context context) {
        super(context);
    }

    public EcgView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
        mScroller = new Scroller(context);
        lineRender = new LineRender();

    }

    public EcgView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }




    Scroller mScroller;
    private void smoothScrollTo(int destX) {
        int scrollX = getScrollX();
        int delta = destX - scrollX;
        mScroller.startScroll(scrollX, 0, delta, 0, 100);
        invalidate();
    }


    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            postInvalidate();
        }
    }

    /**
     * 处理滑动查看历史数据
     *
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = event.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                x_change = event.getX() - startX;




//                Log.d("自定义标签", "drawWave:====offset_x_max" + offset_x_max);
                x_changed += x_change;
                if (x_changed > 0) { //防止向右滑动,超出左边界
                    x_changed = 0;
                } else if (x_changed < offset_x_max) {//防止向左滑动,超出右边界
                    x_changed = offset_x_max;
                }
                smoothScrollTo(-(int) x_changed);
//                invalidate();
//                Log.d("自定义标签", "onTouchEvent:====" + " x_change " + x_change);
//                Log.d("自定义标签", "onTouchEvent:====" + " startX " + startX);
                break;
        }
        return true;
    }


    //实时传入的数据对应的值
    private float _CurX = 0;
    private float _CurY = 0;

    // 设置每_EveryNPointRefresh个点刷新电图
    private int _EveryNPointRefresh = 1;
    //Y轴最大值
    private float _MaxYNumber;

    //设置单元间距
    private float _EveryOneValue = 1;
    // 垂直格子数量
    private int _YSize;
    private int _XSize;
    //Y轴间隔
    private int _LatticeWidth;

    // 屏幕上的数量
    private int _PointMaxAmount;
    private float _XUnitLength;
//http://www.jianshu.com/p/16301de41a18

    private int _CurP = 0;  // 当前加入点总数

    private Paint _PaintLine;//折线画笔
    private List<Entry> mListPoint = new ArrayList<>();//存储一屏的数据点
    private List<Entry> mPoint = new ArrayList<>();//存储所有的数据点，绘制
    private float history_X;//记录历史数据点的X值

    /**
     * 设置 数据
     *
     * @param curY
     */
    public void setLinePoint(float curY, Line line) {
        Entry point = new Entry();
        Entry tempPoint = new Entry();//临时绘制一屏数据点

        tempPoint.setX(_CurX);
        _CurX += _XUnitLength;
        //存储所有点
        point.setX(history_X);
        history_X += _XUnitLength;
        // 计算y真实数据
        float number = curY / _EveryOneValue;// 这个数应该占的格子数
        Log.d("GG", "setLinePoint:number " + number);
        if (height != 0) {
            _CurY = height - (number * _LatticeWidth);
//            _CurY = y_center - (  number * _LatticeWidth);
        }
        tempPoint.setY(_CurY);

        //存储所有点

        point.setY(_CurY);

        mPoint.add(point);
        line.setEntryList(mPoint);
        // 判断当前点是否大于最大点
        if (_CurP < _PointMaxAmount) {
            if (mListPoint.size() == _PointMaxAmount
                    && mListPoint.get(_CurP) != null) {
                mListPoint.clear();
            }

            mListPoint.add(tempPoint);

            _CurP++;
        } else {
            _CurP = 0;
            _CurX = 0;

        }
        if (_CurP % _EveryNPointRefresh == 0) {
            invalidate();
        }

    }

    /**
     * 画点
     *
     * @param canvas
     */
    LineRender lineRender;
    Path mPath=new Path();


    Paint h=new Paint();


    public void drawECG(Canvas canvas) {

        h.setStyle(Paint.Style.STROKE);
        h.setColor(Color.BLUE);
        h.setTextSize(30);
        if (!isOver) {//只绘制一屏数据

            int size = mListPoint.size();
            for (int i = 0; i < size; i++) {
                if (i==0){
                    mPath.moveTo(mListPoint.get(i).getX(),mListPoint.get(i).getY());
//                    canvas.restore();
//
//                    canvas.scale(1,1);
//                    canvas.drawText(""+i,mListPoint.get(i).getX(),mListPoint.get(i).getY(),h);
                }else {
                    mPath.lineTo(mListPoint.get(i).getX(),mListPoint.get(i).getY());
//                    canvas.restore();
//
//
//                    canvas.scale(1,1);
//                    canvas.drawText(""+i,mListPoint.get(i).getX(),mListPoint.get(i).getY(),h);
                }
            }

            canvas.drawPath(mPath,_PaintLine);
            mPath.reset();
//            canvas.restore();
//
//
//            canvas.scale(1,1);
//            canvas.drawText("dfdfdfdfd",width/2,height/2,h);

        } else {//绘制全部数据

            if (mPoint.size() != 0) {

                int size = mPoint.size();
                offset_x_max = width - size * _XUnitLength;//确定X轴滑动最大偏移量
                lineRender.setmLines(lines);
                lineRender.render(canvas);

            } else {
                canvas.drawText("The Data Exception", width / 2, height / 2, _PaintLine);
            }

        }

    }

    private boolean isOver;

    private Lines lines;

    public Lines getLines() {
        return lines;
    }

    public void setLines(Lines lines) {
        this.lines = lines;
    }

    /**
     * 判断是否停止绘制
     *
     * @return
     */
    public void isOver(boolean isOver) {
        this.isOver = isOver;
    }


    // 设置格子的单元间距
    public void setEffticeValue(int value) {
        _EveryOneValue = value;
    }

    // 设置Y轴最大值
    public void setMaxYNumber(float maxYNumber) {
        this._MaxYNumber = maxYNumber;
    }

    // 设置屏幕最多显示点的数量
    public void setMaxPointAmount(int i) {
        _PointMaxAmount = i;
    }

    // 设置每N个点刷新
    public void setEveryNPointRefresh(int num) {
        _EveryNPointRefresh = num;
    }

    //设置Lines

}


/**
 * //                Log.d("自定义标签", "drawWave:====" + " _AllPoint.get(size-1).get(Y_KEY)"+_AllPoint.get(1).get("Y_KEY"));
 //                Log.d("自定义标签", "drawWave:====" + " x_changed"+x_changed);
 ////                path.moveTo( x_changed,_AllPoint.get(0).get(Y_KEY));
 //
 //                for (int i = 0; i <size; i++) {
 //
 //                    if(i==0){
 ////                        path.moveTo(_AllPoint.get(i).get(X_KEY)+x_changed,_AllPoint.get(i).get(Y_KEY));
 //                        path.moveTo(mPoint.get(i).getX()+x_changed,mPoint.get(i).getY());
 //                    }
 //
 ////                    path.lineTo(_AllPoint.get(i).get(X_KEY)+x_changed, _AllPoint.get(i).get(Y_KEY));
 //                    path.lineTo(mPoint.get(i).getX()+ x_changed,mPoint.get(i).getY());
 //
 //                }
 //                canvas.drawPath(path,paint);

 //                /**
 //                 * 绘制下方举行区域
 //
//                rect_gap_x=(float) width/size;
//                rect_width=(float) width*width/(_XUnitLength*size);
//                multiple_for_rect_width=width/rect_width;
//                Paint rect_Paint=new Paint();
//                rect_Paint.setStyle(Paint.Style.FILL);
//                int color=Color.parseColor("#9c0a7b14");
//                rect_Paint.setColor(color);
//                rect_Paint.setStrokeWidth(1);
//                Path rect_path=new Path();
//
//                float rect_xori=(0-x_changed)/multiple_for_rect_width;
////                rect_path.moveTo(rect_xori,height-rect_high);
////                rect_path.lineTo(rect_xori+rect_width,height-rect_high);
////                rect_path.lineTo(rect_xori+rect_width,height);
////                rect_path.lineTo(rect_xori,height);
//                rect_path.moveTo(rect_xori,0);
//                rect_path.lineTo(rect_xori+rect_width,0);
//                rect_path.lineTo(rect_xori+rect_width,rect_high);
//                rect_path.lineTo(rect_xori,rect_high);
//                canvas.drawPath(rect_path,rect_Paint);
//
//
//                path.moveTo( 0,_AllPoint.get(0).get(Y_KEY)/8);
//
//                for (int i = 1; i <size; i++) {
//                    path.lineTo(rect_gap_x * i , _AllPoint.get(i).get(Y_KEY)/8);
//
//                }
//                canvas.drawPath(path,paint);
//
 */