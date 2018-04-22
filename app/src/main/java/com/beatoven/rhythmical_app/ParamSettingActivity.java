package com.beatoven.rhythmical_app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by SCITMASTER on 2018-04-04.
 */

public class ParamSettingActivity extends Activity implements SensorEventListener{

    private String TAG = "Sensors";
    private Sensor mAccelerometer;
    private SensorManager mSensorManager;

    float var0 = 0, var1 = 0, var2 = 0;
    int cnt = 1;
    boolean isSent = false;

    private String isX;
    private String isY;
    private String isZ;

    private float maxX = 0;
    private float maxZ = 0;

    private String code;

    private TextView motionResult,threshold;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paramsetting);

        Intent intent = getIntent();
        code = intent.getStringExtra("code");

        Toast.makeText(this, code, Toast.LENGTH_SHORT).show();


        mSensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        motionResult = findViewById(R.id.motionResult);
        threshold = findViewById(R.id.threshold);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //3. 이벤트 리스너 설정
        //SENSOR_DELAY_NORMAL --> 0.2 sec
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        Log.d(TAG, "onAccuracyChanged()");
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        synchronized (this)
        {
            //4. Sensor row data 수신
            switch (sensorEvent.sensor.getType()) {
                case Sensor.TYPE_ACCELEROMETER:
                    var0 = sensorEvent.values[0];
                    var1 = sensorEvent.values[1];
                    var2 = sensorEvent.values[2];
                    break;
                default:
                    break;
            }
            if (isSent) {
                if (cnt % 40 != 0){
                    cnt++;
                }else{
                    cnt = 1;
                    isSent = false;
                }
            }else{
                Log.d("var",var0+"//"+var1+"//"+var2);
                //Log.d("Threshold",MainActivity.threshold+"");
                String motion = motionCheck(var0,var1,var2);
                threshold.setText(MainActivity.threshold+"");
                motionResult.setText(motion);
                Log.d("Motion",motion);
                isSent = true;
            }

        }
    }



    public String motionCheck(float x, float y, float z) {
        int point1,point2,point3;

        point1 = MainActivity.threshold;
        point2 = MainActivity.threshold * 2;
        point3 = MainActivity.threshold * 3;

        String motion = "No Motion";

        if (y > point2){
            //찌르기
            motion = "POINT";
            return motion;
        }

        if (Math.abs(z) > point3) {
            if (z > 0) {
                if (maxZ < z) {
                    maxZ = z;
                }else{
                    isZ = "+Point3";
                }
            }else {
                if (maxZ > z) {
                    maxZ = z;
                }else{
                    isZ = "-Point3";
                }
            }
        }else if (Math.abs(z) > point2){
            if (z > 0) {
                if (maxZ < z) {
                    maxZ = z;
                }else if (isZ == null){
                    isZ = "+Point2";
                }
            }else {
                if (maxZ > z) {
                    maxZ = z;
                }else if (isZ == null){
                    isZ = "-Point2";
                }
            }
        }else if (Math.abs(z) > point1){
            if (z > 0) {
                if (maxZ < z) {
                    maxZ = z;
                }else if (isZ == null){
                    isZ = "+Point1";
                }
            }else {
                if (maxZ > z) {
                    maxZ = z;
                }else if (isZ == null){
                    isZ = "-Point1";
                }
            }
        }else {
            isZ = null;
            maxZ = 0;
        }

        if (Math.abs(x) > point3) {
            if (x > 0) {
                if (maxX < x) {
                    maxX = x;
                }else{
                    isX = "+Point3";
                }
            }else {
                if (maxX > x) {
                    maxX = x;
                }else{
                    isX = "-Point3";
                }
            }
        }else if (Math.abs(x) > point2){
            if (x > 0) {
                if (maxX < x) {
                    maxX = x;
                }else if (isX == null){
                    isX = "+Point2";
                }
            }else {
                if (maxX > x) {
                    maxX = x;
                }else if (isX == null){
                    isX = "-Point2";
                }
            }
        }else if (Math.abs(x) > point1){
            if (x > 0) {
                if (maxX < x) {
                    maxX = x;
                }else if (isX == null){
                    isX = "+Point1";
                }
            }else {
                if (maxX > x) {
                    maxX = x;
                }else if (isX == null){
                    isX = "-Point1";
                }
            }
        }else {
            isX = null;
            maxX = 0;
        }

        if (y < (-1)*point3) {
            isY = "-Point3";
        }else{
            isY = null;
        }

        if (isY != null && isZ != null){
            if (isY.equals("-Point3") && isZ.equals("+Point3")){
                motion = "LEFT";
                return motion;
            }
            if (isY.equals("-Point3") && (isZ.equals("-Point3") || (isZ.equals("-Point2")))){
                motion = "RIGHT";
                return motion;
            }
        }
        if (isY != null && isX != null){
            if (isY.equals("-Point3") && (isX.equals("-Point2")||isX.equals("-Point3"))){
                motion = "DOWN";
                return motion;
            }
            if (isY.equals("-Point3") && isX.equals("+Point3")){
                motion = "UP";
                return motion;
            }
        }
        return motion;
    }

    public void setThreshold(View view){
        Intent intent = new Intent(this,RhythmiActivity.class);
        intent.putExtra("code",code);
        startActivity(intent);
    }

    /*protected void setThreshold(View view){
        Button btn = (Button) view;

        Toast.makeText(this,"Buttons",Toast.LENGTH_SHORT).show();

        switch (btn.getText().toString()){
            case "increase":
                if (MainActivity.threshold > 4) {
                    MainActivity.threshold -= 2;
                }
                threshold.setText(MainActivity.threshold+"");
                break;
            case "decrease":
                if (MainActivity.threshold < 20) {
                    MainActivity.threshold += 2;
                }
                threshold.setText(MainActivity.threshold+"");
                break;
            case "ok":
                threshold.setText(MainActivity.threshold+"");
                break;
            default:break;
        }
    }*/
}
