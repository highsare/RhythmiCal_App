package com.beatoven.rhythmical_app;

import android.content.Context;
import android.content.Intent;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
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

public class MainActivity extends AppCompatActivity {

    public static String id;
    public static String code;
    public static int threshold;

    private EditText idEt,pwEt,codeEt;
    private Button logInBtn,codeBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        idEt = findViewById(R.id.idEt);
        pwEt = findViewById(R.id.pwEt);
        codeEt = findViewById(R.id.codeEt);
        logInBtn = findViewById(R.id.logInBtn);
        codeBtn = findViewById(R.id.codeBtn);

        this.threshold = 10;

        //2. Main Thread에서 네트워크 접속 가능하도록 설정
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    protected void logInRequest(View view){
        Log.d("Log In Requested","Now");
        sendToServer("logIn");
    }

    protected void codeRequest(View view){
        sendToServer("code");
    }

    public void sendToServer(String request){
        URL url = null;
        HttpURLConnection con = null;
        String param = "";
        String inputId = "";
        String inputPassword = "";
        String inputCode = "";
        HashMap<String, String> params = new HashMap<>();

        params.put("request",request);

        //사용자가 입력한 데이터 (서버로 보낼 데이터)를 Map에 저장
        if (request.equals("logIn")){
            inputId = idEt.getText().toString();
            inputPassword = pwEt.getText().toString();
            params.put("id", inputId);
            params.put("password", inputPassword);
            Log.d("id",inputId);
            Log.d("password",inputPassword);
        }else if(request.equals("code")){
            inputCode = codeEt.getText().toString();
            params.put("code",inputCode);
        }

        //요청시 보낼 쿼리스트림으로 변환
        param = makeParams(params);

        try{
            //서버의 IP주소, PORT번호, Context root, Request Mapping경로
            //url = new URL("http://10.10.10.43:8888/rhythmical/loginApp");
            url = new URL(Address.ADDRESS_SR10_HJ+"loginApp");
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
                        Log.d("LINE:",line);
                    }
                    //답변 받은 곳
                    if (page.equals("logIn")){
                        //역치값 받아와야함
                        Toast.makeText(this, page, Toast.LENGTH_SHORT).show();
                        id = inputId;
                        Intent intent = new Intent(this,ConsoleActivity.class);
                        startActivity(intent);
                    }else if(page.equals("code")){
                        Toast.makeText(this, page, Toast.LENGTH_SHORT).show();
                        code = inputCode;
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

    //커스텀 뷰 전환 메소드

    //서버에 데이터를 보내는 메소드
}
