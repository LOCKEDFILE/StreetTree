package com.example.iclab.st;

import android.content.Intent;
import android.graphics.Region;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import cz.msebera.android.httpclient.Header;

import static com.example.iclab.st.IntroActivity.addressData;
import static com.example.iclab.st.NamesrchActivity.newCS;
import static com.example.iclab.st.NewplaceActivity.GCSurvey;

// 지역으로 찾기 액티비티
public class RegionsrchActivity extends AppCompatActivity {

    LinkedHashMap<String, String> sidoMap;
    LinkedHashMap<String, String> goonMap;
    LinkedHashMap<String, String> guMap;
    String sidocode;
    String gooncode;
    String gucode; // 동 코드
    int spin1,spin2;
    List<String> listName=new ArrayList<>();
    boolean ch1 = false, ch2 = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regionsrch);

        final Spinner top = findViewById(R.id.top);
        final Spinner mid = findViewById(R.id.mid);
        final Spinner leaf = findViewById(R.id.leaf);
        final ListView hList = findViewById(R.id.hyunjangList1);
        final Button button=findViewById(R.id.srchBtn1);

        // 현장명 리스트뷰 어댑터 생성
        final ArrayAdapter<String> listAdapter = new ArrayAdapter<String> (this, android.R.layout.simple_list_item_1, listName);
        hList.setAdapter(listAdapter);

        // 리스트뷰 아이템 클릭 시 액티비티 이동
        hList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(RegionsrchActivity.this, ValueprintActivity.class);
                intent.putExtra("position",position);
                startActivity(intent);
            }
        });

        // sido Spinner에 데이터 저장
        ArrayAdapter<String> sidoAdapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, addressData.name);    //sidoList);
        top.setAdapter(sidoAdapter);


        // 시도 부분 spinner 클릭했을 때
        top.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int index, long l) {
                spin1=index;
                sidocode=Integer.toString(addressData.code.get(spin1));
                // spinner에 값 저장
                ArrayAdapter<String> goonAdapter = new ArrayAdapter<String>(RegionsrchActivity.this, R.layout.support_simple_spinner_dropdown_item, addressData.goonDatas.get(spin1).goonName);

                mid.setAdapter(goonAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        // 군 부분 Spinner를 클릭했을 때
        mid.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int index, long l) {
                // spinner에 값 저장
                spin2=index;
                gooncode=Integer.toString(addressData.goonDatas.get(spin1).goonCode.get(spin2));
                ArrayAdapter<String> guAdapter = new ArrayAdapter<String>(RegionsrchActivity.this, R.layout.support_simple_spinner_dropdown_item, addressData.goonDatas.get(spin1).guDatas.get(spin2).guName);

                leaf.setAdapter(guAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        /***********************************************************************
         *  여기 아래에 있는 'code'가 동의 코드를 나타냅니다.
         *  동을 가리키는 spinner 를 클릭하였을 때 선택한 동의 코드를 가져오는데
         *  처음에 default로 청운동의 코드를 가져옵니다.
         *  (spinner에 처음부터 청운동이 떠있기 때문)
         *  따라서 최종 확인을 했을때 디비로 데이터를 전송하게 하거나
         *  spinner에 초기 값을 데이터의 첫 값이 아닌
         *  시 / 군 / 구 등으로 수정해줘도 좋을 것 같습니다!
         **********************************************************************/
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listName.clear();

                final AsyncHttpClient client = new AsyncHttpClient();
                client.setCookieStore(new PersistentCookieStore(RegionsrchActivity.this));
                String url = "http://220.69.209.49/measureset/region/";
                if(sidocode == null)
                    url = "http://220.69.209.49/measureset/region/";
                else if(gooncode == null)
                    url += sidocode;
                else if(gucode == null)
                    url += sidocode +"/"+gooncode;
                else if (gucode != null)
                    url += sidocode +"/"+gooncode +"/"+gucode;


                client.get(url,new JsonHttpResponseHandler(){
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                        super.onSuccess(statusCode, headers, response);
                        newCS.clear();
                        for(int i =0;i<response.length();i++) {
                            try {
                                newCS.add(new CSurvey(response.getJSONObject(i)));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            listName.add(newCS.get(i).siteName +"  ("+newCS.get(i).createdAt+")");
                        }
                        hList.setAdapter(listAdapter);
                    }
                    @Override
                    public void onFailure(int statusCode, Header[] headers,String s, Throwable throwable) {
                        super.onFailure(statusCode, headers, s, throwable);
                    }
                });



            }
        });
        leaf.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                gucode =Integer.toString(addressData.goonDatas.get(spin1).guDatas.get(spin2).guCode.get(i));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }
}
