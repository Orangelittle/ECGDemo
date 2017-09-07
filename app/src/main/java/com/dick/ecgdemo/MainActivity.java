package com.dick.ecgdemo;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.dick.ecgdemo.data.Line;
import com.dick.ecgdemo.data.Lines;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

//    private ECGView _ECG;
    private EcgView _ECG;
    private Handler mHandler;
    private Lines lines;
    Line line;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        _ECG = (EcgView) findViewById(R.id.electrocardiogram);
        _ECG.setMaxPointAmount(20);

//        _ECG.setEveryNPoint(5);
        _ECG.setEveryNPointRefresh(1);
        _ECG.setEffticeValue(400);
        _ECG.setMaxYNumber(2000f);

        lines=new Lines();
         line=new Line();
//        line.setEntryList(mPoint);
        line.setLineColor(Color.RED);
        line.setBezierCurve(true);
        line.setFillColor(Color.BLUE);
        line.setFill(true);



        lines.addLine(line);
        _ECG.setLines(lines);

        mHandler=new Handler();

        loadDatas();
    }

    Runnable runnable=new Runnable() {
        @Override
        public void run() {
            _ECG.setLinePoint(new Random().nextInt(1500),line);
//            loadDatas();

            mHandler.postDelayed(this,100);

        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.start:
                _ECG.isOver(false);
                mHandler.postDelayed(runnable, 100);

                break;

            case R.id.stop:
                mHandler.removeCallbacksAndMessages(null);
                _ECG.isOver(true);
                _ECG.invalidate();
                break;

        }

        return true;
    }

    private List<Integer> datas=new ArrayList<>();

    private void loadDatas(){
        try{
            String data0 = "";
            InputStream in = getResources().openRawResource(R.raw.ecgdata);
            int length = in.available();
            byte [] buffer = new byte[length];
            in.read(buffer);
            data0 = new String(buffer);
            in.close();
            String[] data0s = data0.split(",");
            for(String str : data0s){
//                _ECG.setLinePoint(Float.parseFloat(str),line);
                datas.add(Integer.parseInt(str));
            }

//            data0Q.addAll(datas);
//            data1Q.addAll(datas);
        }catch (Exception e){}

    }
}
