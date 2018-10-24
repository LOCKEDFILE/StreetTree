package com.example.iclab.st;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import static com.example.iclab.st.IntroActivity.addressData;

public class Parsing {

    Parsing(String data) throws JSONException {

        JSONObject sidoJson=new JSONObject(data);
        // sido
        JSONArray sidoName=sidoJson.getJSONArray("name");

        JSONArray sidoCode=sidoJson.getJSONArray("code");
        JSONArray goonDatas=sidoJson.getJSONArray("goonDatas");

        for(int i=0;i<sidoCode.length();i++) {
            addressData.code.add(sidoCode.getInt(i));
            addressData.name.add(sidoName.getString(i));
            addressData.sidoMap.put(sidoName.getString(i),sidoCode.getInt(i));
            addressData.goonDatas.add(addGoon(goonDatas.getJSONObject(i)));
        }

//        // goon
//        for(int i=0;i<goonDatas.length();i++){
//            JSONObject goonJson=goonDatas.getJSONObject(i);
//            // goon
//            JSONArray goonName=goonJson.getJSONArray("goonName");
//            JSONArray goonCode=goonJson.getJSONArray("goonCode");
//            JSONArray guDatas=goonJson.getJSONArray("guDatas");
//
//            // gu
//            for(int j=0;j<guDatas.length();j++){
//                JSONObject guJson=guDatas.getJSONObject(j);
//                // gu
//                JSONArray guName=guJson.getJSONArray("guName");
//                JSONArray guCode=guJson.getJSONArray("guCode");
//            }
//        }
    }
    public goonData addGoon(JSONObject object) throws JSONException {
        goonData tmp=new goonData();

        JSONArray goonName=object.getJSONArray("goonName");
        JSONArray goonCode=object.getJSONArray("goonCode");
        JSONArray guDatas=object.getJSONArray("guDatas");

        for(int i=0;i<goonCode.length();i++) {
            tmp.goonCode.add(goonCode.getInt(i));
            tmp.goonName.add(goonName.getString(i));
            tmp.goonMap.put(goonName.getString(i),goonCode.getInt(i));
            tmp.guDatas.add(addGu(guDatas.getJSONObject(i)));
        }
        return tmp;
    }

    public guData addGu(JSONObject object) throws JSONException {
        guData tmp=new guData();

        JSONArray guName=object.getJSONArray("guName");
        JSONArray guCode=object.getJSONArray("guCode");

        for(int i=0;i<guCode.length();i++){
            tmp.guCode.add(guCode.getInt(i));
            tmp.guName.add(guName.getString(i));
            tmp.guMap.put(guName.getString(i),guCode.getInt(i));
        }
        return tmp;
    }

}
