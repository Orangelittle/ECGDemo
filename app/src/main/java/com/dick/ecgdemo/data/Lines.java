package com.dick.ecgdemo.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dick on 2017/9/4.
 */

public class Lines {
    private List<Line> lineList=new ArrayList<>();

    public List<Line> getLineList() {
        return lineList;
    }

    public void setLineList(List<Line> lineList) {
        this.lineList = lineList;
    }


    public void addLine(Line line){
        lineList.add(line);
    }


}
