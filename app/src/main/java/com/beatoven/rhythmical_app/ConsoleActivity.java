package com.beatoven.rhythmical_app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by SCITMASTER on 2018-04-03.
 */

public class ConsoleActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_console);

        //2. Main Thread에서 네트워크 접속 가능하도록 설정
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    protected void oBtnClicked(View view){
        /*Intent intent = new Intent(this,ParamSettingActivity.class);
        startActivity(intent);
        */
        sendConsoleOrder("enter");
    }

    //묶을것
    protected void xBtnClicked(View view){
        sendConsoleOrder("esc");
    }
    protected void leftBtnClicked(View view){
        sendConsoleOrder("left");
    }
    protected void upBtnClicked(View view){
        sendConsoleOrder("up");
    }
    protected void downBtnClicked(View view){
        sendConsoleOrder("down");
    }
    protected void rightBtnClicked(View view){
        sendConsoleOrder("right");
    }

    public void sendConsoleOrder(String order){
        URL url = null;
        HttpURLConnection con = null;
        String param = "";
        String request = "console";

        HashMap<String, String> params = new HashMap<>();
        //사용자가 입력한 데이터 (서버로 보낼 데이터)를 Map에 저장
        params.put("request",request);
        params.put("order",order);

        //요청시 보낼 쿼리스트림으로 변환
        param = makeParams(params);

        try{
            //서버의 IP주소, PORT번호, Context root, Request Mapping경로
            //url = new URL("http://10.10.10.43:8888/rhythmical/sendConsole");
            url = new URL(Address.ADDRESS_SR11_JJ+"sendConsole");
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

                    while ((line = reader.readLine()) != null) {
                        page += line;
                    }
                    //답변 받은 곳
                    if(page.equals("Rhythmi")){
                        Intent intent = new Intent(this,RhythmiActivity.class);
                        startActivity(intent);
                    }
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
}
