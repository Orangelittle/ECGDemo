package com.dick.ecgdemo.render;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

import com.dick.ecgdemo.ControlPoint;
import com.dick.ecgdemo.data.Entry;
import com.dick.ecgdemo.data.Line;
import com.dick.ecgdemo.data.Lines;

import java.util.List;

/**
 * Created by dick on 2017/9/4.
 */

public class LineRender {

    private Lines mLines;

    private Paint linePaint;//曲线画笔
    private Paint fillPaint;//填充画笔


    private Path mPath;

    public LineRender() {
        linePaint=new Paint(Paint.ANTI_ALIAS_FLAG);
        mPath=new Path();
    }

    public void render(Canvas canvas){

        for (Line line : mLines.getLineList()) {
            linePaint.setStrokeWidth(line.getLineWidth());
            linePaint.setColor(line.getLineColor());
            linePaint.setStyle(Paint.Style.STROKE);

            List<Entry> list=line.getEntryList();
            int size = list.size();
            if (line.isBezierCurve()) {
                //是否是平滑曲线图
                List<ControlPoint> controlPoints1 = ControlPoint.getControlPointList(list);
                for (int i=0; i<controlPoints1.size(); i++){
                    if (i == 0){
                        mPath.moveTo(list.get(i).getX(),list.get(i).getY());
                    }
                    //画三价贝塞尔曲线
                    mPath.cubicTo(
                            controlPoints1.get(i).getConPoint1().x,controlPoints1.get(i).getConPoint1().y,
                            controlPoints1.get(i).getConPoint2().x,controlPoints1.get(i).getConPoint2().y,
                            list.get(i+1).getX(), list.get(i+1).getY()
                    );
                }
                canvas.drawPath(mPath,linePaint);
            }else{
                //绘制折线图
                for (int i = 0; i < size; i++) {
                    if (i == 0) {
//                    canvas.drawCircle(list.get(i).getX(),list.get(i).getY(),5,linePaint);
                        mPath.moveTo(list.get(i).getX() , list.get(i).getY());
                    } else {
                        mPath.lineTo(list.get(i).getX() , list.get(i).getY());
                        canvas.drawCircle(list.get(i).getX(),list.get(i).getY(),5,linePaint);
                    }
                }
                canvas.drawPath(mPath,linePaint);
            }


            // TODO: 2017/9/4 填充
            if (line.isFill()) {
                Entry max = list.get(size - 1);
                fillPaint=new Paint();
                fillPaint.setStyle(Paint.Style.FILL);
                fillPaint.setAlpha(50);
                fillPaint.setColor(line.getFillColor());

                mPath.lineTo(max.getX(),150);
                mPath.lineTo(0,150);
                mPath.close();

                canvas.drawPath(mPath,fillPaint);
            }



        }

    }

    public Lines getmLines() {
        return mLines;
    }

    public void setmLines(Lines mLines) {
        this.mLines = mLines;
    }
}
