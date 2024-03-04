package com.example.be_perfectly_safe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;

public class map extends AppCompatActivity implements OnMapReadyCallback {


    private GoogleMap mMap;
    private static final int LOCATION_PERMISSION_RUQUEST_CODE = 1;
    private GoogleMap googleMap;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private Timer timer;
    SupportMapFragment supportMapFragment;
    FusedLocationProviderClient client;
    private LocationCallback locationCallback;
    private String IP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        SharedPreferences getPrefs = PreferenceManager
                .getDefaultSharedPreferences(this);

        IP = getPrefs.getString("IP", "null");

        supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.google_maps);
        // 初始化 FusedLocationProviderClient
        client = LocationServices.getFusedLocationProviderClient(this);

        // 初始化位置回调
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult != null) {
                    Location location = locationResult.getLastLocation();
                    if (location != null) {
                        // 更新地图
                        updateMap(new LatLng(location.getLatitude(), location.getLongitude()));

                        // 現在將位置資訊發送到資料庫
                        sendLocationToPhpMyAdmin(location.getLatitude(), location.getLongitude());
                    }
                }
            }
        };
        // 初始化 Google 地圖
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.google_maps);
        mapFragment.getMapAsync(this);
        // 请求定位權限
        requestLocationPermission();


    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setZoomControlsEnabled(true);


    }

    private void sendLocationToPhpMyAdmin(double latitude, double longitude) {
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String url = "http://"+IP+"/location/insertlocation.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {


                        Log.i("狀態", response);

                        String status = "Success";

                        if (response.trim().equals("Success")) {
//                            Toast.makeText(getApplicationContext(), "Registration successful", Toast.LENGTH_SHORT).show();

                        } else {
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> paramV = new HashMap<>();
                paramV.put("latitude", String.valueOf(latitude));
                paramV.put("longitude", String.valueOf(longitude));

                return paramV;
            }
        };
        queue.add(stringRequest);
    }

    private void updateMap(LatLng latLng) {
        // 更新Google地图上的位置標記
        if (mMap != null) {
            mMap.clear();
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
            mMap.addMarker(new MarkerOptions().position(latLng).title("Current Location"));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_RUQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdates();
            } else {
                Toast.makeText(this, "需要定位權限以使用此應用程式", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        supportMapFragment.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        supportMapFragment.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (timer != null) {
            timer.cancel();
        }

        if (supportMapFragment != null) {
            supportMapFragment.onDestroy();
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        supportMapFragment.onLowMemory();
    }

    private void requestLocationPermission() {
        // 检查是否已授予定位權限
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_RUQUEST_CODE);
        } else {
            startLocationUpdates();
        }
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // 權限已被授予，啟動位置更新
            LocationRequest locationRequest = LocationRequest.create()
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                    .setInterval(60000); // 更新間隔為 60 秒

            client.requestLocationUpdates(locationRequest, locationCallback, null);
        } else {
            // 權限未被授予，請求權限
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_RUQUEST_CODE);
        }


    }
}