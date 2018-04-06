package com.beatoven.rhythmical_app;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by SCITMASTER on 2018-04-03.
 */

public class RhythmiActivity extends Activity implements SensorEventListener {

    private String TAG = "Sensors";
    private Sensor mAccelerometer;
    private SensorManager mSensorManager;

    float var0 = 0, var1 = 0, var2 = 0;
    int cnt = 1;
    boolean isSent = false;

    private int motionCnt = 0;

    private String isX;
    private String isY;
    private String isZ;

    private float maxX = 0;
    private float maxZ = 0;

    public TextView tv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rhythmi);

        //1. 디바이스에서 사용 가능한 센서 정보 확인
        mSensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        //2. Main Thread에서 네트워크 접속 가능하도록 설정
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        tv = findViewById(R.id.text);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //3. 이벤트 리스너 설정
        //SENSOR_DELAY_NORMAL --> 0.2 sec
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
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
                String motion = motionCheck(var0,var1,var2);
                tv.setText(motion);
                if (!motion.equals("")){
                    sendMotion(motion);
                    isSent = true;
                }
            }
        }
    }

    public String motionCheck(float x, float y, float z) {
        int point1,point2,point3;

        point1 = MainActivity.threshold;
        point2 = MainActivity.threshold * 2;
        point3 = MainActivity.threshold * 3;

        String motion = "";

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

    //서버로 보낼 데이터를 쿼리 스트링으로 변환 ("?이름=값&이름2=값2" 형식)
    public String makeParams(HashMap<String,String> params){
        StringBuffer sbParam = new StringBuffer();
        String key = "";
        String value = "";
        boolean isAnd = false;

        for(Map.Entry<String,String> elem : params.entrySet()){
            key = elem.getKey();
            value = elem.getValue();

            if(isAnd){
                sbParam.append("&");
            }

            sbParam.append(key).append("=").append(value);

            if(!isAnd){
                if(params.size() >= 2){
                    isAnd = true;
                }
            }
        }
        return sbParam.toString();
    }

    public void sendMotion(String motion){
        URL url = null;
        HttpURLConnection con = null;
        String time = "";
        String param = "";

        //time = new SimpleDateFormat("SSS").format(new Date(System.currentTimeMillis()));
        //사용자가 입력한 데이터 (서버로 보낼 데이터)를 Map에 저장
        HashMap<String, String> params = new HashMap<>();
        params.put("motion", motion);

        //요청시 보낼 쿼리스트림으로 변환
        param = makeParams(params);

        try{
            //서버의 IP주소, PORT번호, Context root, Request Mapping경로
            url = new URL("http://10.10.12.239:8888/rhythmical/sendMotion");
        } catch (MalformedURLException e){
            Toast.makeText(this,"잘못된 URL입니다.", Toast.LENGTH_SHORT).show();
        }
        try{
            con = (HttpURLConnection) url.openConnection();
            if(con != null){
                con.setConnectTimeout(0);	//연결제한시간. 0은 무한대기.
                con.setUseCaches(false);		//캐쉬 사용여부
                con.setRequestMethod("POST"); // URL 요청에 대한 메소드 설정 : POST.
                con.setRequestProperty("Accept-Charset", "UTF-8"); // Accept-Charset 설정.
                con.setRequestProperty("Context_Type", "application/x-www-form-urlencoded;cahrset=UTF-8");

                OutputStream os = con.getOutputStream();
                os.write(param.getBytes("UTF-8"));
                os.flush();
                os.close();

                if(con.getResponseCode() == HttpURLConnection.HTTP_OK){
                    BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));

                    String line;
                    String page = "";

                    while ((line = reader.readLine()) != null){
                        page += line;
                    }
                    Toast.makeText(this, page, Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e){
            Toast.makeText(this, "" + e.toString(), Toast.LENGTH_SHORT).show();
        } finally {
            if(con != null){
                con.disconnect();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        Log.d(TAG, "onAccuracyChanged()");
    }

}
