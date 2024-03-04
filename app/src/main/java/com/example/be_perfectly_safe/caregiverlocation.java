package com.example.be_perfectly_safe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class caregiverlocation extends AppCompatActivity implements OnMapReadyCallback {

    private LocationManager locationManager;
    private GoogleMap cMap;
    private SupportMapFragment mapFragment;
    private Timer timer;

    private Button btnStartDrawing, btnFinishDrawing, btnClearDrawing, btnDeleteDrawing;
    private List<LatLng> polygonPoints;
    private Polygon currentPolygon;
    private Polyline drawingPolyline;
    private List<Polygon> drawnPolygons;  // 儲存多邊形
    private List<Polyline> drawingPolylines;
    private boolean isDrawing = false;
    private String IP;
    private Location currentLocation;

    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private FusedLocationProviderClient fusedLocationClient;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caregiverlocation);

        SharedPreferences getPrefs = PreferenceManager
                .getDefaultSharedPreferences(this);

        IP = getPrefs.getString("IP", "null");

        //--------------------------------------------------------------------------------------------------------
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        requestLocationPermission();

        FragmentManager fragmentManager = getSupportFragmentManager();
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.google_maps2);

        if (mapFragment == null){
            mapFragment = SupportMapFragment.newInstance();
            fragmentManager.beginTransaction().replace(R.id.google_maps2, mapFragment).commit();
        }

        //-------------------------------------------------------------------------------------------------------
        // 當地圖準備好時，調用 onMapReady 方法
        mapFragment.getMapAsync(this);

        btnStartDrawing = findViewById(R.id.btnStartDrawing);
        btnFinishDrawing = findViewById(R.id.btnFinishDrawing);
        btnClearDrawing = findViewById(R.id.btnClearDrawing);
        btnDeleteDrawing = findViewById(R.id.btnDeleteDrawing);

        polygonPoints = new ArrayList<>();
        currentPolygon = null;
        drawingPolyline = null;
        drawnPolygons = new ArrayList<>();
        drawingPolylines = new ArrayList<>();

        // 初始化計時器，每一分鐘執行一次
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                // 在這裡添加每一分鐘自動發送請求的代碼
                fetchDataFromPhpMyAdmin();
            }
        }, 0, 30000); // 延遲 0 毫秒，每 60000 毫秒（一分鐘）執行一次
    }

    private void requestLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        }
    }

    private void fetchDataFromPhpMyAdmin() {
        String url = "http://" + IP + "/location/caregiverlocation.php"; // 替換 PHPMyAdmin 端點

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        // 創建一個 StringRequest 對象
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.i("JSONtest", response);
                        try {
                            // 解析 JSON 數據
                            JSONObject jsonObject = new JSONObject(response);

                            double latitude1 = jsonObject.getDouble("latitude");
                            double longitude1 = jsonObject.getDouble("longitude");
                            Log.i("test3", String.valueOf(latitude1));
                            Log.i("test4", String.valueOf(longitude1));

                            // 在 Google Map 上顯示位置
                            showLocationOnMap1(latitude1, longitude1);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // 處理錯誤
                        Log.e("Volley Error", error.toString());
                    }
                });

        // 將請求添加到 RequestQueue 中
        requestQueue.add(stringRequest);
    }

    private void showLocationOnMap1(double latitude1, double longitude1) {
        if (cMap != null) {
            // 創建 LatLng 對象，使用從 PHP 腳本獲取的 latitude 和 longitude
            LatLng location1 = new LatLng(latitude1, longitude1);
            // 在地圖上添加標記
            cMap.addMarker(new MarkerOptions().position(location1).title("病患位置"));
            // 移動地圖到標記位置
            cMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location1, 15));
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        cMap = googleMap;
        getCurrentLocation();
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        double latitude2 = location.getLatitude();
                        double longitude2 = location.getLongitude();
                        showLocationOnMap2(latitude2, longitude2);
                    }
                }
            });
        }
    }

    private void showLocationOnMap2(double latitude2, double longitude2) {
        if (cMap != null) {
            LatLng Location2 = new LatLng(latitude2, longitude2);

            MarkerOptions markerOptions = new MarkerOptions().position(Location2).title("照顧者位置").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
            cMap.addMarker(markerOptions);
            cMap.moveCamera(CameraUpdateFactory.newLatLng(Location2));
            cMap.animateCamera(CameraUpdateFactory.newLatLngZoom(Location2, 15));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // 權限請求結果
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            } else {
            }
        }
    }


    private void startLocationUpdates() {
        // 設置時間間隔
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000L, (float) 10F, (LocationListener) this);
    }

    public void startDrawing(View view) {
        // 啟用繪製
        isDrawing = true;
        polygonPoints.clear();
        currentPolygon = null;
        drawingPolyline = null;

        // 監聽點擊
        cMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (isDrawing) {
                    polygonPoints.add(latLng);

                    // 畫折線
                    if (drawingPolyline != null) {
                        drawingPolyline.remove();
                    }
                    PolylineOptions polylineOptions = new PolylineOptions()
                            .color(Color.BLUE)
                            .addAll(polygonPoints);
                    drawingPolyline = cMap.addPolyline(polylineOptions);

                    // 畫多邊形
                    if (polygonPoints.size() > 2) {
                        if (currentPolygon != null) {
                            currentPolygon.remove();
                        }

                        PolygonOptions polygonOptions = new PolygonOptions()
                                .addAll(polygonPoints)
                                .strokeColor(Color.BLUE)
                                .fillColor(Color.argb(50, 0, 0, 255));
                        currentPolygon = cMap.addPolygon(polygonOptions);
                    }
                }
            }
        });
    }

    public void finishDrawing(View view) {

        // 停止繪製
        isDrawing = false;

        // 停止監聽點擊
        cMap.setOnMapClickListener(null);

        // 拿到繪製的圖形
        View dialogView = getLayoutInflater().inflate(R.layout.activity_dialog, null);
        EditText nameEditText = dialogView.findViewById(R.id.nameEditText);
//        Spinner daySpinner = dialogView.findViewById(R.id.daySpinner);
        TimePicker startTimePicker = dialogView.findViewById(R.id.startTimePicker);
        TimePicker endTimePicker = dialogView.findViewById(R.id.endTimePicker);

        TextView mondayTextView = dialogView.findViewById(R.id.mondayTextView);
        TextView tuesdayTextView = dialogView.findViewById(R.id.tuesdayTextView);
        TextView wednesdayTextView = dialogView.findViewById(R.id.wednesdayTextView);
        TextView thursdayTextView = dialogView.findViewById(R.id.thursdayTextView);
        TextView fridayTextView = dialogView.findViewById(R.id.fridayTextView);
        TextView saturdayTextView = dialogView.findViewById(R.id.saturdayTextView);
        TextView sundayTextView = dialogView.findViewById(R.id.sundayTextView);

        final int originalColor = mondayTextView.getCurrentTextColor();
        final boolean[] monisColorChanged = {false};
        final boolean[] tuesisColorChanged = {false};
        final boolean[] wedisColorChanged = {false};
        final boolean[] thursisColorChanged = {false};
        final boolean[] friisColorChanged = {false};
        final boolean[] satisColorChanged = {false};
        final boolean[] sunisColorChanged = {false};


        mondayTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 更改星期一的文字顏色
                if (monisColorChanged[0]) {
                    // 如果顏色已更改，則恢復原來的顏色
                    mondayTextView.setTextColor(originalColor);
                    monisColorChanged[0] = false;
                } else {
                    // 否則，更改為新的顏色
                    mondayTextView.setTextColor(Color.GREEN);
                    monisColorChanged[0] = true;
                }


            }
        });

        tuesdayTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 更改星期一的文字顏色
                if (tuesisColorChanged[0]) {
                    // 如果顏色已更改，則恢復原來的顏色
                    tuesdayTextView.setTextColor(originalColor);
                    tuesisColorChanged[0] = false;
                } else {
                    // 否則，更改為新的顏色
                    tuesdayTextView.setTextColor(Color.GREEN);
                    tuesisColorChanged[0] = true;
                }


            }
        });

        wednesdayTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 更改星期一的文字顏色
                if (wedisColorChanged[0]) {
                    // 如果顏色已更改，則恢復原來的顏色
                    wednesdayTextView.setTextColor(originalColor);
                    wedisColorChanged[0] = false;
                } else {
                    // 否則，更改為新的顏色
                    wednesdayTextView.setTextColor(Color.GREEN);
                    wedisColorChanged[0] = true;
                }


            }
        });

        thursdayTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 更改星期一的文字顏色
                if (thursisColorChanged[0]) {
                    // 如果顏色已更改，則恢復原來的顏色
                    thursdayTextView.setTextColor(originalColor);
                    thursisColorChanged[0] = false;
                } else {
                    // 否則，更改為新的顏色
                    thursdayTextView.setTextColor(Color.GREEN);
                    thursisColorChanged[0] = true;
                }


            }
        });

        fridayTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 更改星期一的文字顏色
                if (friisColorChanged[0]) {
                    // 如果顏色已更改，則恢復原來的顏色
                    fridayTextView.setTextColor(originalColor);
                    friisColorChanged[0] = false;
                } else {
                    // 否則，更改為新的顏色
                    fridayTextView.setTextColor(Color.GREEN);
                    friisColorChanged[0] = true;
                }


            }
        });

        saturdayTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 更改星期一的文字顏色
                if (satisColorChanged[0]) {
                    // 如果顏色已更改，則恢復原來的顏色
                    saturdayTextView.setTextColor(originalColor);
                    satisColorChanged[0] = false;
                } else {
                    // 否則，更改為新的顏色
                    saturdayTextView.setTextColor(Color.GREEN);
                    satisColorChanged[0] = true;
                }


            }
        });

        sundayTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 更改星期一的文字顏色
                if (sunisColorChanged[0]) {
                    // 如果顏色已更改，則恢復原來的顏色
                    sundayTextView.setTextColor(originalColor);
                    sunisColorChanged[0] = false;
                } else {
                    // 否則，更改為新的顏色
                    sundayTextView.setTextColor(Color.GREEN);
                    sunisColorChanged[0] = true;
                }


            }
        });

        // 创建对话框
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("設置多邊形屬性");

        // 设置按钮
        builder.setPositiveButton("確定", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 获取对话框中的输入字段的值
                String name = nameEditText.getText().toString();

                // 获取所选的开始时间和结束时间
                int selectedStartHour = startTimePicker.getHour();
                int selectedStartMinute = startTimePicker.getMinute();
                int selectedEndHour = endTimePicker.getHour();
                int selectedEndMinute = endTimePicker.getMinute();


                // 在这里处理所选的名称、开始时间、结束时间和星期几
                Log.i("Name", name);
                Log.i("SelectedStartHour", String.valueOf(selectedStartHour));
                Log.i("SelectedStartMinute", String.valueOf(selectedStartMinute));
                Log.i("SelectedEndHour", String.valueOf(selectedEndHour));
                Log.i("SelectedEndMinute", String.valueOf(selectedEndMinute));
                Toast.makeText(caregiverlocation.this, "繪製成功", Toast.LENGTH_SHORT).show();
                // 保存或上传数据，你可以在这里处理数据的保存或上传
            }

        });
//        Toast.makeText(this, "繪製成功", Toast.LENGTH_SHORT).show();

        // 取消按钮
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        // 设置对话框的视图
        builder.setView(dialogView);

        // 创建并显示对话框
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void clearDrawing(View view) {
        // 清除繪製
        isDrawing = false;
        polygonPoints.clear();

        // 删除當前的圖
        if (currentPolygon != null) {
            currentPolygon.remove();
            currentPolygon = null;
        }
        if (drawingPolyline != null) {
            drawingPolyline.remove();
            drawingPolyline = null;
        }

        // 將圖記錄起來
        if (currentPolygon != null) {
            drawnPolygons.add(currentPolygon);
        }
        if (drawingPolyline != null) {
            drawingPolylines.add(drawingPolyline);
        }

        deleteDataFromPhpMyAdmin();
    }

    public void clearAllDrawings(View view) {
        Log.d("ClearAllDrawings", "Clear all drawings button clicked");
        // 清除所有繪製
        isDrawing = false;
        polygonPoints.clear();

        // 删除當前繪製的東西
        if (currentPolygon != null) {
            currentPolygon.remove();
            currentPolygon = null;
        }
        if (drawingPolyline != null) {
            drawingPolyline.remove();
            drawingPolyline = null;
        }

        // 删除所有東西
        for (Polygon polygon : drawnPolygons) {
            polygon.remove();
        }
        drawnPolygons.clear();

        for (Polyline polyline : drawingPolylines) {
            polyline.remove();
        }
        drawingPolylines.clear();
    }

    public void deleteDrawing(View view) {

    }

    private void uploadPolygonDataToServer() {
        String url = "http://192.168.89.250/location/sendPolygons.php";

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JSONObject requestData = new JSONObject();
        try {
            requestData.put("coordinates", polygonPoints);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // 上傳成功
                        Log.i("Upload Response", response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // 處理錯誤
                        Log.e("Upload Error", error.toString());
                    }
                }) {
            @Override
            public byte[] getBody() {
                // 將JSON傳出去
                return requestData.toString().getBytes();
            }
        };


        requestQueue.add(stringRequest);

    }

    private void deleteDataFromPhpMyAdmin() {
        String url = "http://192.168.89.250/location/delPolygons.php";

        RequestQueue requestQueue = Volley.newRequestQueue(this);


        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // 處理
                        Log.i("Delete Response", response);
                        // 查看是否有處理
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // 處理錯誤
                        Log.e("Delete Error", error.toString());
                    }
                });


        requestQueue.add(stringRequest);
    }
}