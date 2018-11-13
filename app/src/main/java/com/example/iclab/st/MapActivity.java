package com.example.iclab.st;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapReverseGeoCoder;
import net.daum.mf.map.api.MapView;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static com.example.iclab.st.FunctionActivity.track_latitude;
import static com.example.iclab.st.FunctionActivity.track_longitude;
import static com.example.iclab.st.NewplaceActivity.GCSurvey;

// 지도 액티비티
public class MapActivity extends AppCompatActivity implements MapView.MapViewEventListener {
    ImageButton gpsButton = null;
    Button applyButton = null;
    Button cancelButton = null;
    MapPOIItem marker = null;
    MapPoint clickPoint = null;
    boolean isButtonVisible = false;
    double latitude;
    double longitude;
    static String addressStr = "";
    public static MapActivity mapActivity = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        mapActivity = this;

        final MapView mapView = new MapView(MapActivity.this);


        mapView.setDaumMapApiKey("e95ede72416f09346c75c0acb52472ed");
        RelativeLayout container = findViewById(R.id.map);
        container.addView(mapView);

        mapView.setMapViewEventListener(this);


        for (int i = 0; i < GCSurvey.list.size(); i++) {
            marker = new MapPOIItem();
            marker.setItemName("AKM");
            marker.setTag(0);
            marker.setMapPoint(MapPoint.mapPointWithGeoCoord(GCSurvey.list.get(i).latitude, GCSurvey.list.get(i).longitude));
            marker.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
            marker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.
            mapView.addPOIItem(marker);
        }

        gpsButton = findViewById(R.id.gps);

        gpsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moveMapViewCurrentPosition(mapView);
            }
        });

        gpsButton.performClick();
        applyButton = findViewById(R.id.apply);
        cancelButton = findViewById(R.id.cancel);

        // 확인버튼
        applyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MapActivity.this, SurveyActivity.class);

                intent.putExtra("latitude", clickPoint.getMapPointGeoCoord().latitude);
                intent.putExtra("longitude", clickPoint.getMapPointGeoCoord().longitude);

                startActivity(intent);

                // 버튼 비활성화
                applyButton.setVisibility(View.INVISIBLE);
                cancelButton.setVisibility(View.INVISIBLE);

                isButtonVisible = false;

                finish();
            }
        });

        // 취소버튼
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                applyButton.setVisibility(View.INVISIBLE);
                cancelButton.setVisibility(View.INVISIBLE);

                // 마커 삭제
                mapView.removePOIItem(marker);
//                markerList.remove(marker);

                isButtonVisible = false;
            }
        });


    }

    static MapReverseGeoCoder.ReverseGeoCodingResultListener reverseGeoCodingResultListener = new MapReverseGeoCoder.ReverseGeoCodingResultListener() {
        @Override
        public void onReverseGeoCoderFoundAddress(MapReverseGeoCoder mapReverseGeoCoder, String s) {
            Log.e("TEST", "  " + s);
            addressStr = s;
            Toast.makeText(mapActivity, addressStr, Toast.LENGTH_LONG).show();
        }

        @Override
        public void onReverseGeoCoderFailedToFindAddress(MapReverseGeoCoder mapReverseGeoCoder) {

        }
    };

    public static void getAddressName(double latitude, double longitude) {// ex ) 서울 강북구 수유동 / 서울 종로구 부암동 . (시) (구) (동)  / 충남 아산시 도고면 와산리 / 충남 아산시 송악면 동화리
        MapReverseGeoCoder reverseGeoCoder = new MapReverseGeoCoder("e95ede72416f09346c75c0acb52472ed", MapPoint.mapPointWithGeoCoord(latitude, longitude), reverseGeoCodingResultListener, mapActivity);
        reverseGeoCoder.startFindingAddress();
//        reverseGeoCoder.findAddressForMapPointSync("e95ede72416f09346c75c0acb52472ed",MapPoint.mapPointWithGeoCoord(latitude,longitude));
    }

    @Override
    public void onMapViewInitialized(MapView mapView) {
        Log.e("GCSURVEY:::: ", " size : " +GCSurvey.list.size());
        for (int i = 0; i < GCSurvey.list.size(); i++) {
            Log.e("long:::: ", " size : " +GCSurvey.list.get(i).longitude);
            marker = new MapPOIItem();
            marker.setTag(0);
            marker.setMapPoint(MapPoint.mapPointWithGeoCoord(GCSurvey.list.get(i).latitude, GCSurvey.list.get(i).longitude));
            marker.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
            marker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.
            moveMapViewCurrentPosition(mapView);
            mapView.addPOIItem(marker);
        }
        moveMapViewCurrentPosition(mapView);
        if(GCSurvey.list.size()>0){
            latitude=GCSurvey.list.get(GCSurvey.list.size()-1).latitude;
            longitude=GCSurvey.list.get(GCSurvey.list.size()-1).longitude;
        }else{
            if(track_latitude==0) {
                latitude = 37.566535f;
                longitude = 126.97796919999996f;
            }
            else{
                latitude=track_latitude;
                longitude=track_longitude;
            }
        }
        mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(latitude, longitude), true);

    }

    public void moveMapViewCurrentPosition(MapView mapView) {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.removeUpdates(locationListener);

        // 권한이 허용되어있지 않은 경우
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // 위치 정보 접근 요청
            ActivityCompat.requestPermissions(MapActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        }else {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 100, 0, locationListener);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 0, locationListener);
            mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(latitude, longitude), true);
        }
    }
    @Override
    public void onMapViewCenterPointMoved(MapView mapView, MapPoint mapPoint) {
    }


    @Override
    public void onMapViewZoomLevelChanged(MapView mapView, int i) {
    }

    @Override
    public void onMapViewSingleTapped(MapView mapView, MapPoint mapPoint) {
        if (!isButtonVisible) {
            // 마커 생성
            marker = new MapPOIItem();
            marker.setItemName("");
            marker.setTag(0);
            marker.setMapPoint(mapPoint);
            marker.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
            marker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.
            mapView.addPOIItem(marker);

            // 버튼 활성화
            applyButton.setVisibility(View.VISIBLE);
            cancelButton.setVisibility(View.VISIBLE);

            clickPoint = mapPoint;
            longitude=mapPoint.getMapPointGeoCoord().longitude;
            latitude=mapPoint.getMapPointGeoCoord().latitude;
            MapReverseGeoCoder reverseGeoCoder = new MapReverseGeoCoder("e95ede72416f09346c75c0acb52472ed",mapPoint, reverseGeoCodingResultListener, MapActivity.this);
            reverseGeoCoder.startFindingAddress();


            isButtonVisible = true;
        }
    }


    @Override
    public void onMapViewDoubleTapped(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewLongPressed(MapView mapView, MapPoint mapPoint) {
    }

    @Override
    public void onMapViewDragStarted(MapView mapView, MapPoint mapPoint) {
    }

    @Override
    public void onMapViewDragEnded(MapView mapView, MapPoint mapPoint) {
    }

    @Override
    public void onMapViewMoveFinished(MapView mapView, MapPoint mapPoint) {
    }

    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            latitude = location.getLatitude();   //위도
            longitude = location.getLongitude(); //경도

            locationManager.removeUpdates(locationListener);
        }

        @Override
        public void onProviderDisabled(String provider) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }
    };




}
