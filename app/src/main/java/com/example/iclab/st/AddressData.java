package com.example.iclab.st;


import java.util.ArrayList;
import java.util.LinkedHashMap;

public class AddressData {
    ArrayList<String> name; // 시도 이름
    ArrayList<Integer> code;// 시도 코드
    LinkedHashMap<String, Integer> sidoMap;
    ArrayList<goonData> goonDatas;

    public AddressData() {
        name=new ArrayList<>();
        code=new ArrayList<>();
        goonDatas = new ArrayList<>();
        sidoMap=new LinkedHashMap<>();
    }
}
class goonData{
    ArrayList<String> goonName;
    ArrayList<Integer> goonCode;
    LinkedHashMap<String, Integer> goonMap;
    ArrayList<guData> guDatas;
    public goonData(){
        goonName=new ArrayList<>();
        goonCode=new ArrayList<>();
        guDatas=new ArrayList<>();
        goonMap=new LinkedHashMap<>();
    }
}

class guData{
    ArrayList<String> guName;
    ArrayList<Integer> guCode;
    LinkedHashMap<String, Integer> guMap;
    public guData(){
        guName=new ArrayList<>();
        guCode=new ArrayList<>();
        guMap=new LinkedHashMap<>();
    }
}
