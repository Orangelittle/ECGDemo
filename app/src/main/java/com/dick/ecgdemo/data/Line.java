package com.dick.ecgdemo.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dick on 2017/9/4.
 */

public class Line {

    private List<Entry> entryList=new ArrayList<>();

    private int lineColor;

    private int lineWidth=1;

    private boolean isFill;//是否填充

    private int fillColor;//填充的颜色


    private boolean isBezierCurve;//是否绘制平滑曲线，默认是折线



    public void addEntry(Entry entry){
        entryList.add(entry);
    }

    public int getFillColor() {
        return fillColor;
    }

    public void setFillColor(int fillColor) {
        this.fillColor = fillColor;
    }

    public boolean isBezierCurve() {
        return isBezierCurve;
    }

    public void setBezierCurve(boolean bezierCurve) {
        isBezierCurve = bezierCurve;
    }

    public boolean isFill() {
        return isFill;
    }

    public void setFill(boolean fill) {
        isFill = fill;
    }

    public List<Entry> getEntryList() {
        return entryList;
    }

    public void setEntryList(List<Entry> entryList) {
        this.entryList = entryList;
    }

    public int getLineColor() {
        return lineColor;
    }

    public void setLineColor(int lineColor) {
        this.lineColor = lineColor;
    }

    public int getLineWidth() {
        return lineWidth;
    }

    public void setLineWidth(int lineWidth) {
        this.lineWidth = lineWidth;
    }



}
