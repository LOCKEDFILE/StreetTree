package com.example.iclab.st;

import android.annotation.SuppressLint;
import android.app.DialogFragment;
import android.app.Dialog;
import android.app.DatePickerDialog;
import android.support.annotation.IdRes;
import android.widget.DatePicker;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

// 신규현장실측(현장명입력) 액티비티

public class NewplaceActivity extends AppCompatActivity {

    static public CSurvey GCSurvey=new CSurvey();
    EditText inputHyunjang;
    EditText inputBalju;
    EditText inputMarketingname;
    EditText inputDefference;
    TextView contentTxV;
    protected static TextView displayDate;
    static  boolean dateCh=false;
    long now = System.currentTimeMillis();
    Date date;
    String nowDate;
    RadioGroup rg;
    String level;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newplace);

        inputMarketingname=findViewById(R.id.inputMarketingname);
        inputDefference=findViewById(R.id.inputDefference);
        rg = findViewById(R.id.radioGroup2);
        Button datePick = findViewById(R.id.datePick);
        displayDate = findViewById(R.id.inputDelivery);
        Button saveBtn = findViewById(R.id.save);
        final Button okBtn = findViewById(R.id.ok);
        inputHyunjang = findViewById(R.id.inputHyunjang);
        inputBalju = findViewById(R.id.inputBalju);
        contentTxV = findViewById(R.id.contentView);
        date = new Date(now);
        inputHyunjang.setPrivateImeOptions("defaultInputmode=korean; ");
        inputBalju.setPrivateImeOptions("defaultInputmode=korean; ");

        datePick.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                DatePickerFragment mDatePicker = new DatePickerFragment();
                mDatePicker.show(getFragmentManager(), "select date");
            }
        });

        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public  void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                if(i == R.id.beforeRadio2) {
                    level="관급";
                }
                else if(i == R.id.afterRadio2) {
                    level="사급";
                }
            }
        });


        inputBalju.setImeOptions(EditorInfo.IME_ACTION_DONE);
        inputBalju.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if(i== EditorInfo.IME_ACTION_DONE) {
                    InputMethodManager im=(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    im.hideSoftInputFromWindow(inputBalju.getWindowToken(),0);
                    okBtn.performClick();
                    return true;
                }
                return false;
            }
        });
        // 현재 날짜를 해당 포맷으로 받아옴
        SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy-MM-dd");
        nowDate = sdfNow.format(date);


        // 레이아웃 배치를 위해 임시로 텍스트 띄우는 기능만 (담당자X// - 아직 로그인 구현X) // 0730 로그인 구현 // 출력부분 수정 필요
        okBtn.setOnClickListener(new Button.OnClickListener() {
            @SuppressLint("SetTextI18n")
            public void onClick(View v) {
                contentTxV.setText("  현장명 :  " + inputHyunjang.getText() +"\n" + "  발주처 :  " + inputBalju.getText()
                        +"\n" + "  날짜 :  " + nowDate+ "\n" + "  담당자 :  "+GCSurvey.authorFullName + "\n" + "  영업담당자 :  "+inputMarketingname.getText() + "\n"
                        + "  직급 :  " +level+ "\n" + "  납품예정일 :  " +displayDate.getText()+ "\n" + "  공차값 :  "+inputDefference.getText());
            }
        });
        // 저장 버튼 누르면 지도 화면으로 전환
        saveBtn.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                if(dateCh) {
                    GCSurvey.siteName = inputHyunjang.getText().toString(); // 데이터저장
                    GCSurvey.createdAt = nowDate;
                    GCSurvey.deliveryTarget = level;
                    GCSurvey.deliveryDate = displayDate.getText().toString();
                    GCSurvey.differenceValue = inputDefference.getText().toString();
                    GCSurvey.clientName = inputBalju.getText().toString();
                    GCSurvey.salespersonName = inputMarketingname.getText().toString();


                    Intent intent = new Intent(getApplicationContext(), MapActivity.class); // 원래 , 맵으로이동이지만 잠시 테스트 하기 위해
//                Intent intent = new Intent(getApplicationContext(), SurveyActivity.class); // 잠시 테스트하기 위해 변경
                    startActivity(intent);
                }
                else{
                    Toast.makeText(NewplaceActivity.this,"납품예정일을 입력하세요",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            displayDate.setText(String.format("%04d",year) + "-" + String.format("%02d",month+1) + "-" + String.format("%02d",day));
            dateCh=true;
        }
    }
}

