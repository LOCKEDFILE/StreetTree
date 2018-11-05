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
import static com.example.iclab.st.SurveyActivity.frameId;
import static com.example.iclab.st.SurveyActivity.plateId;

public class ValueprintActivity extends AppCompatActivity {
    int pos = -1;
    static public boolean is_appended = false;
    boolean detailCheck=false;
    Button detail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_valueprint);
        Button appendButton=findViewById(R.id.appendButton);
        detail=findViewById(R.id.list_detail);



        Intent intent = getIntent();
        final ListView vList = findViewById(R.id.valueList);
        final List<String> listinfo=new ArrayList<>();
        final ArrayAdapter<String> listAdapter = new ArrayAdapter<> (this, android.R.layout.simple_list_item_1, listinfo);


        if(intent.getExtras() != null)
        {
             pos = intent.getIntExtra("position",-1);
        }

        appendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GCSurvey =newCS.get(pos);

                is_appended=true;
                Intent mapintent = new Intent(getApplicationContext(), MapActivity.class);
                startActivity(mapintent);

            }
        });

        detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listinfo.clear();
                detailCheck=!detailCheck;

               if(detailCheck){
                   CSurvey tmp =newCS.get(pos);
                   detail.setText("기본");

//
                   ArrayList<String> list = new ArrayList<>();
                   ArrayList<Integer> list1 = new ArrayList<>();
                   ArrayList<String> list3 = new ArrayList<>();
                   ArrayList<Integer> list2 = new ArrayList<>();

                   for(int i=0;i<tmp.list.size();i++)
                   {
                       if(!list.contains(tmp.list.get(i).plate_id))
                       {
                           list.add(tmp.list.get(i).plate_id);
                           list1.add(1);
                       }
                       else
                       {
                           int ch=-1;
                           for(int k=0;k<list.size();k++)
                               if(list.get(k).equals(tmp.list.get(i).plate_id))
                                   ch=k;
                           list1.set(ch,list1.get(ch)+1);
                       }
                       if(tmp.list.get(i).framecheck)
                       {
                           int ch =-1;
                           for(int k=0;k<plateId.size();k++) {

                               if(plateId.get(k).contains(tmp.list.get(i).plate_id))
                                   ch=k;
                           }
                           if(!list3.contains(frameId.get(ch))) {
                               list3.add(frameId.get(ch));
                               list2.add(1);
                           }
                           else {
                               int ch2=-1;
                               for(int j=0;j<list3.size();j++)
                                   if(list3.get(j).equals(frameId.get(ch)))
                                       ch2=j;
                               list2.set(ch2,list2.get(ch2)+1);
                           }

                       }

                   }
                   String longstr ="";
                   for(int i =0;i<list1.size();i++)
                       longstr += list.get(i) +" : " +list1.get(i)+" 조\n";
                   longstr += "\n";
                   for(int i =0;i<list2.size();i++)
                       longstr += list3.get(i) +" : " +list2.get(i)+" 조\n";
                   //

                   listinfo.add(longstr);

                }
                else{
                   detail.setText("상세");
                   for(int i=0; i< newCS.get(pos).list.size();i++)
                   {
                       SurveyList sl = newCS.get(pos).list.get(i);
//            String s = "NO."+(i+1)+ "   보호판 : "+sl.plate_id +"\n위도 : "+sl.latitude+"  \n경도 : "+sl.longitude;
                       String firstData="";
                       String pointSum="";
                       for(int j=0;j<sl.points.length;j++)
                           pointSum+=sl.points[j]+"  ";

                       firstData += "No. " + (i + 1) + "\n 보호판 이름: " + sl.plate_id + "\n 뿌리 값: " + pointSum+"\n 메모 : "+sl.memo;// 뭐 적어주지.

                       listinfo.add(firstData);
                   }
                }

                vList.setAdapter(listAdapter);
            }
        });

        for(int i=0; i< newCS.get(pos).list.size();i++)
        {
            SurveyList sl = newCS.get(pos).list.get(i);
//            String s = "NO."+(i+1)+ "   보호판 : "+sl.plate_id +"\n위도 : "+sl.latitude+"  \n경도 : "+sl.longitude;
            String firstData="";
            String pointSum="";
            for(int j=0;j<sl.points.length;j++)
                pointSum+=sl.points[j]+"  ";

            firstData += "No. " + (i + 1) + "\n 보호판 이름: " + sl.plate_id + "\n 뿌리 값: " + pointSum+"\n 메모 : "+sl.memo;// 뭐 적어주지.

            listinfo.add(firstData);
        }
        vList.setAdapter(listAdapter);
    }
}
