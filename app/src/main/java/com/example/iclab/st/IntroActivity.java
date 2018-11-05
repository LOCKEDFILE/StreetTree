package com.example.iclab.st;

import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

import static com.example.iclab.st.NewplaceActivity.GCSurvey;

// 로딩화면 액티비티
public class IntroActivity extends AppCompatActivity {
    private ProgressBar mProgress;
    static AddressData addressData=new AddressData();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // Show the splash screen
//
//        URL url;
//        String html="http://www.juso.go.kr/info/RoadNameDataList.do?type=search&roadCd=&keyword=&city1=&county1=&town1=&searchType=0&extend=false";
//        JSoupParse js=new JSoupParse();
//        try {
//            url=new URL(html);
//            js.execute(url);
//            addressData=js.get();
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        } catch (ExecutionException e) {
//            e.printStackTrace();
//        }
//
//
//        String data=new Gson().toJson(addressData);
//        Log.e("TEXXXCCCCAAAAAVVVV :  ",data);
//
//        ///
//// 파일 생성
//        File saveFile = new File(Environment.getExternalStorageDirectory() + "/camdata"); // 저장 경로
//// 폴더 생성
//        if(!saveFile.exists()){ // 폴더 없을 경우
//            saveFile.mkdir(); // 폴더 생성
//        }
//        try {
//            long now = System.currentTimeMillis(); // 현재시간 받아오기
//            Date date = new Date(now); // Date 객체 생성
//            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//            String nowTime = sdf.format(date);
//
//            BufferedWriter buf = new BufferedWriter(new FileWriter(saveFile+"/CarnumData.txt", true));
////            buf.append(nowTime + " "); // 날짜 쓰기
//            buf.append(data); // 파일 쓰기
//            buf.newLine(); // 개행
//            buf.close();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        try {
            InputStream is = getResources().openRawResource(R.raw.data);
            Writer writer = new StringWriter();
            char[] buffer = new char[1024];
            try {
                Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                int n;
                while ((n = reader.read(buffer)) != -1) {
                    writer.write(buffer, 0, n);
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            String jsonString = writer.toString();
            Parsing parsing=new Parsing(jsonString);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //
        setContentView(R.layout.activity_intro);
        mProgress = (ProgressBar) findViewById(R.id.splash_screen_progress_bar);

        // Start lengthy operation in a background thread
        new Thread(new Runnable() {
            public void run() {
                doWork();
                startApp();
                finish();
            }
        }).start();
    }

    private void doWork() {
        for (int progress=0; progress<100; progress+=20) {
            try {
                Thread.sleep(300);
                mProgress.setProgress(progress);
            } catch (Exception e) {
                e.printStackTrace();
                //Timber.e(e.getMessage());
            }
        }
    }

    private void startApp() {
        Intent intent;
        if(SaveSharedPreference.getUserName(IntroActivity.this).length() == 0) {
            // call Login Activity
            intent = new Intent(IntroActivity.this, LoginActivity.class);
            startActivity(intent);
            this.finish();
        } else {
            // Call Next Activity
            intent = new Intent(IntroActivity.this, FunctionActivity.class);
            intent.putExtra("STD_NUM", SaveSharedPreference.getUserName(this));
            GCSurvey.id=SaveSharedPreference.getUserName(getApplicationContext());
            GCSurvey.authorFullName=SaveSharedPreference.getUserFull(getApplicationContext());

            startActivity(intent);
            this.finish();
        }
    }

    //텍스트내용을 경로의 텍스트 파일에 쓰기
    public void WriteTextFile(File file,String contents) {

    }
}