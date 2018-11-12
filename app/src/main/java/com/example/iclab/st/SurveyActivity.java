package com.example.iclab.st;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.support.annotation.IdRes;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import cz.msebera.android.httpclient.Header;

import static com.example.iclab.st.IntroActivity.addressData;
import static com.example.iclab.st.IntroActivity.httpAddr;
import static com.example.iclab.st.MapActivity.addressStr;
import static com.example.iclab.st.MapActivity.getAddressName;
import static com.example.iclab.st.NewplaceActivity.GCSurvey;
import static com.example.iclab.st.RootActivity.imageId;

// 실측 액티비티(수목 실측): 지도에서 마커를 찍으면 넘어오는 화면
public class SurveyActivity extends AppCompatActivity {
    Spinner sp1;
    Spinner sp2;
    Spinner sp3;
    FrameLayout frame;
    ImageView  point4;
    EditText inputTN;

    RadioGroup rg,rg1,rg2;
    TextView noTree;
    int index = 0;
    CheckBox ckBox;
    EditText inputP[];// 기존의 input_P
    String points[] = new String[4]; // 뿌리 값
    String sido;
    String goon;
    String gu;

    EditText et;
    String etStr="";
    ArrayList<String> list1=new ArrayList<>();
    ArrayList<String> list2=new ArrayList<>();
    ArrayList<String> list3=new ArrayList<>();

    String str1,str2;

    static ArrayList<String> plateId=new ArrayList<>();
    static ArrayList<String> frameId=new ArrayList<>();
    AlertDialog.Builder alt_bld;
    TextView plateView;
    boolean frameCh=false;
    boolean gagakCh=true;
    boolean jijuguCh=true;
    static String store ="";
    Button completeBtn;
    double latitude;
    double longitude;
    boolean sujung=false;
    AlertDialog alert;
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey);

        //
        plateView=findViewById(R.id.selectPP);


        //


        //
        if(store.length() != 0)
            plateView.setText(store);

        // 위도 경도 좌표 값
        Intent preIntent = getIntent();
       latitude = preIntent.getDoubleExtra("latitude", 0.0f);
       longitude = preIntent.getDoubleExtra("longitude", 0.0f);
        Button nextBtn = findViewById(R.id.nextBtn);
        final Button startBtn = findViewById(R.id.SurveyStart);
        Button rootBtn = findViewById(R.id.rootBtn);
        completeBtn =findViewById(R.id.completeBtn);
        Button modifyBtn = findViewById(R.id.modifyBtn);
        final Button memobutton=findViewById(R.id.memobutton);
//        if(store.length() == 0)
//            completeBtn.setEnabled(false);
        noTree=findViewById(R.id.number);

        noTree.setText("No. "+(GCSurvey.list.size()+1));
        inputTN = findViewById(R.id.inputTN);
        rg = findViewById(R.id.radioGroup);
        rg1 = findViewById(R.id.radioGroup1);
        rg2 = findViewById(R.id.radioGroup2);

        frame =findViewById(R.id.frame);
        point4 = findViewById(R.id.point4);
        ckBox = findViewById(R.id.checkBox);
        inputP=new EditText[4];
        for(int k=0;k<4;k++){


            inputP[k]=new EditText(SurveyActivity.this);
            int pointId=R.id.inputP4_1+k;
            inputP[k]=findViewById(pointId);
        }

        changeView(index); // 실측화면 초기화
        imageId=null;
        // 체크박스 제어(수목번호 유무)
        ckBox.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                if(ckBox.isChecked()) { //없음이 체크되면
                    inputTN.setText(null); //기존에 입력된 내용 지우기
                    inputTN.setFocusableInTouchMode(false);
                    inputTN.setFocusable(false); // 입력창 비활성화
                }
                else { // 없음 체크 해제
                    inputTN.setFocusableInTouchMode(true);
                    inputTN.setFocusable(true); // 입력창 다시 활성화
                }
            }
        });


        // 라디오버튼 제어(설치전, 설치후)
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public  void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                if(i == R.id.beforeRadio) {
                    index = 1;
                }
                else if(i == R.id.afterRadio) {
                    index = 2;
                }
            }
        });

        // 가각 제어(설치전, 설치후)
        rg1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public  void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                if(i == R.id.beforeRadio1) {
                    gagakCh=true;

                }
                else if(i == R.id.afterRadio1) {
                    gagakCh=false;

                }
            }
        });
        // 지주구 제어(설치전, 설치후)
        rg2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public  void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                if(i == R.id.beforeRadio2) {
                    jijuguCh=false;
                }
                else if(i == R.id.afterRadio2) {
                    jijuguCh=false;
                }
            }
        });

        // 실측시작 버튼 누르면 실측값 입력 화면 출력
        startBtn.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                changeView(index);
            }
        });
        //

        final AsyncHttpClient client=new AsyncHttpClient();
        client.setCookieStore(new PersistentCookieStore(SurveyActivity.this));
        client.get(SurveyActivity.this,httpAddr+"/plates",new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
                Log.e("RESPONSE :: :: ", "  "+response);
                for(int i=0;i<response.length();i++) {
                    try {
                        JSONObject object =response.getJSONObject(i);
//                        Log.e("PLATE",object.getString("plate_id"));
                        plateId.add(object.getString("plate_id"));
//                        Log.d("PLATE_id",plateId.get(plateId.size()-1));
                        JSONObject frameobject=object.getJSONObject("frame");
//                        Log.e("FRAMEOBJ::::"," "+frameobject);
                        frameId.add(frameobject.getString("frame_id"));

                        String[] splitID=plateId.get(i).split("-");
                        if(!list1.contains(splitID[0])){
                            list1.add(splitID[0]);
//                            Log.e("list 1","  "+splitID[0]);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
            @Override
            public void onFailure(int statusCode, Header[] headers,String s, Throwable throwable) {
                super.onFailure(statusCode, headers,s, throwable);
                Log.e("Error(plate)","실패 "+statusCode);

            }
        });


        // 수정 버튼 누르면 보호판 선택 화면으로 전환
        modifyBtn.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                RadioGroup frameRg;
                sujung=true;
                alt_bld = new AlertDialog.Builder(SurveyActivity.this);
                alt_bld.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        store=plateView.getText().toString();
                    }
                });

                // xml파일을 dialog로 붙이기
                LayoutInflater inflater = getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.survey_dialog, null);

                sp1 = dialogView.findViewById(R.id.sp1);
                sp2= dialogView.findViewById(R.id.sp2);
                sp3= dialogView.findViewById(R.id.sp3);

                // sp1.getItem 같은 뭔가 있는거 확인
	// store = sp1.getItem(0).getText() +g;

                sp1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int ind, long l) {
                        str1=list1.get(ind);

                        sp2.invalidate();
                        list2.clear();
                        for(int i=0;i<plateId.size();i++) {
                            String[] splitID = plateId.get(i).split("-");
                            if (plateId.get(i).contains(list1.get(ind))) {
                                if(!list2.contains(splitID[1])) {
                                    list2.add(splitID[1]);
                                    Log.e("list  2"," "+splitID[1]);
                                }
                            }
                        }

                        Collections.sort(list2);
                        ArrayAdapter<String> listAdap2 = new ArrayAdapter<String>(SurveyActivity.this, R.layout.support_simple_spinner_dropdown_item, list2);
                        sp2.setAdapter(listAdap2);

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });



                sp2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int ind, long l) {
                        str2=str1+"-"+list2.get(ind);
                        plateView.setText(str2);
                        list3.clear();
//                completeBtn.setEnabled(true);
                        store = plateView.getText().toString();

                        for(int i=0;i<plateId.size();i++) {
                            String[] splitID = plateId.get(i).split("-");
                            if(plateId.get(i).equals(str2))
                                continue;
                            else if (plateId.get(i).contains(str2)) {
                                if(!list3.contains(splitID[2])) {
                                    list3.add(splitID[2]);
                                    Log.e("list  3"," "+splitID[2]);
                                }
                            }
                        }
                        Collections.sort(list3);
                        list3.add(0,"없음");
                        ArrayAdapter<String> listAdap3 = new ArrayAdapter<String>(SurveyActivity.this, R.layout.support_simple_spinner_dropdown_item, list3);
                        sp3.setAdapter(listAdap3);

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });

                // plateView 에 출력
                sp3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int ind, long l) {
//                completeBtn.setEnabled(true);
                        if(ind==0){
                            plateView.setText(str2);
                            startBtn.setVisibility(View.VISIBLE);
//                    GCSurvey.list.get(GCSurvey.list.size()-1).plate_id=str2+"-"+list3.get(ind);
                        }
                        else{
                            plateView.setText(str2+"-"+list3.get(ind));
                            store = plateView.getText().toString();
                            startBtn.setVisibility(View.INVISIBLE);
//                    GCSurvey.list.get(GCSurvey.list.size()-1).plate_id=str2;
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {
                        plateView.setText(str2);
                        store = plateView.getText().toString();
                    }
                });
                frameRg=dialogView.findViewById(R.id.group);
                frameRg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public  void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                        if(i == R.id.radioButton) {// 1
                            frameCh=true;
                        }
                        else if(i == R.id.radioButton2) {// 2
                            frameCh=false;
                        }

                    }
                });
                alt_bld.setView(dialogView);
                alt_bld.setTitle("보호판 선택");
                Collections.sort(list1);
                ArrayAdapter<String> listAdap1 = new ArrayAdapter<String>(SurveyActivity.this, R.layout.support_simple_spinner_dropdown_item, list1);
                sp1.setAdapter(listAdap1);
                alt_bld.show();
            }


        });

        // 다음 버튼 누르면 맵 화면으로 전환
        nextBtn.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                store = plateView.getText().toString();
                if (!store.equals("선택한 보호판")) {
                    Intent intent = new Intent(getApplicationContext(), MapActivity.class);//  테스트로 인해 잠시 변경
                    intent.putExtra("latitude", latitude);
                    intent.putExtra("longitude", longitude);
                    make_list(latitude, longitude); // 저장
                    Gson newGson = new Gson();
                    String json = newGson.toJson(GCSurvey);
                    SaveSharedPreference.setUserData(SurveyActivity.this, json);
                    startActivity(intent);
                    finish();
                }
                else{
                    Toast.makeText(SurveyActivity.this, "보호판을 선택하세요", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 수목뿌리 버튼 누르면 액티비티 전환
        rootBtn.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RootActivity.class);

                startActivity(intent);
            }
        });

        // 실측완료 버튼 누르면 결과출력 화면으로 전환
        completeBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if(event.getAction()==MotionEvent.ACTION_UP) {
                    store = plateView.getText().toString();
                    if (!store.equals("선택한 보호판")) {
                        Intent intent = new Intent(getApplicationContext(), CompleteActivity.class);
                        make_list(latitude, longitude); // 저장
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(SurveyActivity.this, "보호판을 선택하세요", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                }
                return true;
            }
        });


        // 메모 버튼

        et=new EditText(getApplicationContext());

        memobutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });

    }
    public void showDialog(){
        AlertDialog.Builder alt_bld = new AlertDialog.Builder(SurveyActivity.this);
        et.setHint("여기에 메모를 입력하세요.");
        if(etStr!="")
            et.setText(etStr);
//                et.setMaxEms(100);

        alt_bld.setPositiveButton("완료", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                etStr=et.getText().toString();
                alert.dismiss();
            }
        });
        alt_bld.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                alert.dismiss();
            }
        });
        if(et.getParent()!=null)
            ((ViewGroup)et.getParent()).removeView(et);
        alt_bld.setView(et);
        alert = alt_bld.create();
        alert.setTitle("메모 기능 (100자 제한)");
        alert.show();
    }

    // 설치 전(3군데) - 설치 후(4군데)에 대한 view 전환
    public void changeView(int index) {
        frame.removeView(point4);
        for(int k=0;k<4;k++)
            frame.removeView(inputP[k]);
        if(index!=0){
            frame.addView(point4);
            for(int k=0;k<2+index;k++)
                frame.addView(inputP[k]);
        }

    }
    void make_list(double la, double lo)
    {
        for(int k=0;index==2?k<4:k<3;k++)
            points[k]=inputP[k].getText().toString();

        getAddressName(latitude,longitude);
        //  시 군 구 바꾸기
        String add[]=addressStr.split(" ");

        add[0]=shortToLong(add[0]);

        sido=Integer.toString(addressData.sidoMap.get(add[0]));

        int sidoLoc=addressData.name.indexOf(add[0]);
        if(checkAdd(add[1])) {
            int goonLoc=addressData.goonDatas.get(sidoLoc).goonName.indexOf(add[1]+" "+add[2]);
            goon =Integer.toString(addressData.goonDatas.get(sidoLoc).goonMap.get(add[1]+" "+add[2]));
            gu=Integer.toString(addressData.goonDatas.get(sidoLoc).guDatas.get(goonLoc).guMap.get(add[3]));
        }
        else{
            int goonLoc=addressData.goonDatas.get(sidoLoc).goonName.indexOf(add[1]);
            goon =Integer.toString(addressData.goonDatas.get(sidoLoc).goonMap.get(add[1]));
            gu=Integer.toString(addressData.goonDatas.get(sidoLoc).guDatas.get(goonLoc).guMap.get(add[2]));
        }


        Log.e("시군구코드: "," "+ sido+", "+goon+", "+gu +" store : "+store);
        String tnStr=inputTN.getText().toString();
        Log.e("메모 :" , etStr);
        CSurvey.add_list(store,ckBox.isChecked()?null:tnStr,index ==2,points, la,lo,imageId,sido,goon,gu,etStr
               ,frameCh,gagakCh,jijuguCh);

    }
    //, 경기도 수원시 같이 구이름이 두개인게있음, (충북 청주시) (충남 천안) (전북 전주) (경북 포항) (경기 수원, 성남, 안양, 부천, 안산, 고양, 용인)
    public boolean checkAdd(String add){
        switch(add){
            case "청주시": case "천안시":case "전주시":case "포항시":case "수원시":case "성남시":case "안양시":case "부천시":case "안산시":case "고양시":case "용인시":
                return true;
                default:
                    return false;
        }
    }
    public String shortToLong(String shortStr){
        String longStr="";
        switch(shortStr){
            case "서울": longStr="서울특별시";break;
            case "경기": longStr="경기도";break;
            case "충남": longStr="충청남도";break;
            case "충북": longStr="충청북도";break;
            case "전남": longStr="전라남도";break;
            case "전북": longStr="전라북도";break;
            case "경남": longStr="경상남도";break;
            case "경북": longStr="경상북도";break;
            case "인천": longStr="인천광역시";break;
            case "부산": longStr="부산광역시";break;
            case "대구": longStr="대구광역시";break;
            case "광주": longStr="광주광역시";break;
            case "울산": longStr="울산광역시";break;
            case "제주특별자치도": longStr="제주특별자치도";break;
            case "강원": longStr="강원도";break;
        }
        return longStr;
    }
}
