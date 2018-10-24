package com.example.iclab.st;

import android.os.AsyncTask;
import android.os.NetworkOnMainThreadException;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public  class JSoupParse extends AsyncTask<URL, Void,AddressData> {

    AddressData addressData=new AddressData();
    static boolean check;
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected AddressData doInBackground(URL... urls) {
        sidoParse();
        for(int i=0;i<addressData.code.size();i++) {
            goonParse(addressData.code.get(i));
        }
        return addressData;
    }


//        String html="http://www.juso.go.kr/info/RoadNameDataList.do?type=search&roadCd=&keyword=&city1="+city+"&county1="+country+"&town1="+town+"&searchType=0&extend=false";
    // 시
    public void sidoParse(){
        String html="http://www.juso.go.kr/info/RoadNameDataList.do?type=search&roadCd=&keyword=&city1=&county1=&town1=&searchType=0&extend=false";
        check=false;
        Connection.Response response = null;
        try {
            response = Jsoup.connect(html)
                    .method(Connection.Method.GET)
                    .execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Document document = null;
        try {
            document = response.parse();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Elements elements=document.select("option");
        // html == text // val== 옵션의 값! ( 이게 지역 코드,... )
        for(Element element:elements){
            if(element.text().equals(":: 시/군/구 ::"))
                break;
            if( check){
                addressData.name.add(element.text());
                addressData.code.add(Integer.parseInt(element.val()));
            }
            if(element.text().equals(":: 시/도 ::")) {
                check = true;
            }
        }
    }
    /// 군
    public void goonParse(int sidoCode){
        String html="http://www.juso.go.kr/info/RoadNameDataList.do?type=search&roadCd=&keyword=&city1="+sidoCode+"&county1=&town1=&searchType=0&extend=false";
        check=false;
        goonData tmpGoonData=new goonData();

        Connection.Response response = null;
        try {
            response = Jsoup.connect(html)
                    .method(Connection.Method.GET)
                    .execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Document document = null;
        try {
            document = response.parse();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Elements elements=document.select("option");
        // html == text // val== 옵션의 값! ( 이게 지역 코드,... )
        for(Element element:elements){
            if(element.text().equals(":: 읍/면/동 ::"))
                break;
            if( check){
                tmpGoonData.goonName.add(element.text());
                tmpGoonData.goonCode.add(Integer.parseInt(element.val()));
                tmpGoonData.guDatas.add(guParse(sidoCode, Integer.parseInt(element.val())));
            }
            if(element.text().equals(":: 시/군/구 ::")) {
                check = true;
            }
        }
//        Log.e("TMPGOON ",tmpGoonData.goonName+"");

        addressData.goonDatas.add(tmpGoonData);

    }
    /// 군
    public guData guParse(int sidoCode, int goonCode){
        String html="http://www.juso.go.kr/info/RoadNameDataList.do?type=search&roadCd=&keyword=&city1="+sidoCode+"&county1="+goonCode+"&town1=&searchType=0&extend=false";
        check=false;
//        ArrayList<guData> arrayList=new ArrayList<>();
        guData tmpGuData=new guData();

        Connection.Response response = null;
        try {
            response = Jsoup.connect(html)
                    .method(Connection.Method.GET)
                    .execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Document document = null;
        try {
            document = response.parse();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Elements elements=document.select("option");
        // html == text // val== 옵션의 값! ( 이게 지역 코드,... )
        for(Element element:elements){
            if(check){
                tmpGuData.guName.add(element.text());
                tmpGuData.guCode.add(Integer.parseInt(element.val()));

//                Log.e("guDATA", " "+tmpGuData.guName);
            }
            if(element.text().equals(":: 읍/면/동 ::")) {
                check = true;
            }
        }

        return tmpGuData;
//        addressData.goonDatas.get(addressData.goonDatas.size()).guDatas.add(tmpGuData);
//        Log.d("guDATA", " "+addressData.goonDatas.get(addressData.goonDatas.size()-1).guDatas.get().guName);
    }



    ///// JSoup
}
