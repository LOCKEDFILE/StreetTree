package com.example.iclab.st;

// 실측값 리스트 출력 액티비티 (지역으로 검색, 현장명으로 검색하고 이동)
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import static com.example.iclab.st.NamesrchActivity.newCS;
import static com.example.iclab.st.NewplaceActivity.GCSurvey;

public class ValueprintActivity extends AppCompatActivity {
    int pos = -1;
    static public boolean is_appended = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_valueprint);
        Button appendButton=findViewById(R.id.appendButton);
        appendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GCSurvey =newCS.get(pos);

                is_appended=true;
                Intent mapintent = new Intent(getApplicationContext(), MapActivity.class);
                startActivity(mapintent);

            }
        });

        Intent intent = getIntent();
        final ListView vList = findViewById(R.id.valueList);
        List<String> listinfo=new ArrayList<>();
        final ArrayAdapter<String> listAdapter = new ArrayAdapter<String> (this, android.R.layout.simple_list_item_1, listinfo);


        if(intent.getExtras() != null)
        {
             pos = intent.getIntExtra("position",-1);
        }
        for(int i=0; i< newCS.get(pos).list.size();i++)
        {
            SurveyList sl = newCS.get(pos).list.get(i);
//            String s = "NO."+(i+1)+ "   보호판 : "+sl.plate_id +"\n위도 : "+sl.latitude+"  \n경도 : "+sl.longitude;
            String firstData="";
            String pointSum="";
            for(int j=0;j<sl.points.length;j++)
                pointSum+=sl.points[j]+"  ";

            firstData += "No. " + (i + 1) + "\n보호판 이름: " + sl.plate_id + "\n 뿌리 값: " + pointSum;// 마지막 페이지 출력문

            listinfo.add(firstData);
        }
        vList.setAdapter(listAdapter);
    }
}
