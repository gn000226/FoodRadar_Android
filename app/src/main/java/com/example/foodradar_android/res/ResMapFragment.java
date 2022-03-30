package com.example.foodradar_android.res;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import com.example.foodradar_android.Common;
import com.example.foodradar_android.R;
import com.example.foodradar_android.task.CommonTask;
import com.example.foodradar_android.task.ImageTask;
import com.example.foodradar_android.user.MyRes;
import com.example.foodradar_android.user.ResMaintainFragment;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_IDLE;
import static androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_SETTLING;

public class ResMapFragment extends Fragment {
    private NavController navController;
    private static final int PER_ACCESS_LOCATION = 0;
    private static final String TAG = "TAG_ResMapFragment";
    private static final int REQ_CHECK_SETTINGS = 101;
    private LocationCallback locationCallback;
    private LocationRequest locationRequest;
    private Location lastLocation;
    private FusedLocationProviderClient fusedLocationClient;
    private Activity activity;
    private MapView mapView;
    private SearchView searchView;
    private GoogleMap map;
    private CommonTask resGetAllTask;
    private CommonTask resDeleteTask;
    private List<ImageTask> imageTasks;
    private List<Res> ress;
    private RecyclerView rvRes;
    private List<Res> nearRess = new ArrayList<>();
    private LatLng cameraLatLng;
    private LatLng LastCameraLatLng;
    private Button btSearchResAgain;
    private List<Marker> markers;
    int[] nowPos = new int[1];

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10000)
                .setSmallestDisplacement(1000);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                lastLocation = locationResult.getLastLocation();
            }
        };

        imageTasks = new ArrayList<>();

        navController = Navigation.findNavController(activity, R.id.mainFragment);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity.setTitle(R.string.map);
        return inflater.inflate(R.layout.fragment_res_map, container, false);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.res_map_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.resListFragment:
                navController.navigate(R.id.action_resMapFragment_to_resListFragment);
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        searchView = view.findViewById(R.id.searchView);
        rvRes = view.findViewById(R.id.rvRes);
        btSearchResAgain = view.findViewById(R.id.btSearchResAgain);

        rvRes.setLayoutManager(new StaggeredGridLayoutManager(1,
                StaggeredGridLayoutManager.HORIZONTAL));
        PagerSnapHelper pagerSnapHelper = new PagerSnapHelper();
        pagerSnapHelper.attachToRecyclerView(rvRes);


        mapView = view.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync((googleMap) -> {
            map = googleMap;


            ress = getRess();
            checkLocationSettings();
            if (ress != null) {
//                if (nearRess.size() == 0) {
//                    for (Res res : ress) {
//                        addMarker(new LatLng(res.getResLat(), res.getResLon()), res.getResName());
//                    }
//                    showRess(ress);
//                }

                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextChange(String newText) {
                        // 如果搜尋條件為空字串，就顯示原始資料；否則就顯示搜尋後結果
                        if (newText.isEmpty()) {
                            btSearchResAgain.performClick();
                        } else {
                            map.clear();
                            List<Res> searchRess = new ArrayList<>();
                            markers = new ArrayList<>();
                            // 搜尋原始資料內有無包含關鍵字(不區別大小寫)
                            for (Res res : ress) {
                                if (res.getResName().toUpperCase().contains(newText.toUpperCase())) {
                                    searchRess.add(res);
                                    addMarker(new LatLng(res.getResLat(), res.getResLon()), res.getResName());
                                }
                            }
                            showRess(searchRess);
                            if (searchRess.size() != 0) {
                                moveMap(new LatLng(searchRess.get(0).getResLat(), searchRess.get(0).getResLon()));
                            }
                        }
                        return true;
                    }

                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        return false;
                    }
                });
            }

            map.setOnMarkerClickListener(marker -> {
                marker.showInfoWindow();
                int index = markers.indexOf(marker);
                rvRes.smoothScrollToPosition(index);
                return true;
            });

            btSearchResAgain.setOnClickListener(v -> {
                map.clear();
                List<Res> newNearRess = new ArrayList<>();
                markers = new ArrayList<>();
                float[] results = new float[1];
                cameraLatLng = map.getCameraPosition().target;
                for (Res res : ress) {
                    Location.distanceBetween(cameraLatLng.latitude, cameraLatLng.longitude,
                            res.getResLat(), res.getResLon(), results);
                    if (results[0] != 0 && results[0] <= 2000.0) {
                        newNearRess.add(res);
                        addMarker(new LatLng(res.getResLat(), res.getResLon()), res.getResName());
                    }
                }
                showRess(newNearRess);
                LastCameraLatLng = cameraLatLng;
                btSearchResAgain.setVisibility(View.GONE);
            });

            Button btMyRes = view.findViewById(R.id.btMyRes);
            btMyRes.setOnClickListener(v -> {
                if (Common.USER_ID <= 0) {
                    new AlertDialog.Builder(activity)
                            .setTitle("您尚未登入，要進行登入嗎？")
                            .setPositiveButton(R.string.textOK, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Navigation.findNavController(v)
                                            .navigate(R.id.action_resMapFragment_to_loginFragment);
                                }
                            }).setNegativeButton(R.string.textCancel, null).create()
                            .show();
                } else if (!btMyRes.getText().equals(getResources().getString(R.string.textNearRess))) {
                    map.clear();
                    List<Res> myRess = new ArrayList<>();
                    markers = new ArrayList<>();
                    for (Res res : ress) {
                        if (res.isMyRes()) {
                            myRess.add(res);
                            addMarker(new LatLng(res.getResLat(), res.getResLon()), res.getResName());
                        }
                    }

                    if (myRess.size() != 0) {
                        showRess(myRess);
                        moveMap(new LatLng(myRess.get(0).getResLat(), myRess.get(0).getResLon()));
                        btMyRes.setText(R.string.textNearRess);
                    } else {
                        btSearchResAgain.performClick();
                        Common.showToast(activity, R.string.textNoMyRessFound);
                    }

                } else {
                    btSearchResAgain.performClick();
                    btMyRes.setText(R.string.textMyRes);
                }
            });
        });

        rvRes.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView rv, int newState) {
                super.onScrollStateChanged(rv, newState);
                if (newState == SCROLL_STATE_IDLE) {
                    StaggeredGridLayoutManager lm = (StaggeredGridLayoutManager) rv.getLayoutManager();
                    if (lm != null) {

                        lm.findFirstVisibleItemPositions(nowPos);
                        View itemView = lm.getChildAt(0);
                        if (itemView != null) {
                            TextView tvResAddress = itemView.findViewById(R.id.tvResAddress);
                            String address = tvResAddress.getText().toString();
                            List<Address> addressList;
                            // 如果地址無法解析成經緯度，就設為-181，因為經度為-180~+180
                            double resLat = -181.0;
                            double resLon = -181.0;
                            try {
                                addressList = new Geocoder(activity).getFromLocationName(address, 1);
                                if (addressList != null && addressList.size() > 0) {
                                    resLat = addressList.get(0).getLatitude();
                                    resLon = addressList.get(0).getLongitude();
                                }
                            } catch (Exception e) {
                                Log.e(TAG, e.toString());
                            }
                            if (resLat != -181.0) {
                                moveMap(new LatLng(resLat, resLon));
                                markers.get(nowPos[0]).showInfoWindow();
                            }
                        }
                    }
                }
            }

        });

        //今天吃什麼
        Button btEatWhatGone = view.findViewById(R.id.btEatWhatGone);
        Button btEatWhat = view.findViewById(R.id.btEatWhat);
        btEatWhat.setOnClickListener(v -> {
            new Thread() {
                public void run() {
                    for (int i = 0; i <= 5; i++) {
                        btEatWhatGone.post(new Runnable() {
                            @Override
                            public void run() {
                                btEatWhatGone.performClick();
                            }
                        });
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    rvRes.post(new Runnable() {
                        @Override
                        public void run() {
                            if (nowPos != null) {
                                rvRes.findViewHolderForAdapterPosition(nowPos[0]).itemView.performClick();
                            }
                        }
                    });
                }
            }.start();
        });


        btEatWhatGone.setOnClickListener(v -> {
            StaggeredGridLayoutManager lm = (StaggeredGridLayoutManager) rvRes.getLayoutManager();
            if (lm != null) {
                lm.findFirstVisibleItemPositions(nowPos);
                int resNumber = markers.size();
                int nowNumber = nowPos[0];
                int randomNumber = (int) (Math.random() * resNumber);
                while (nowNumber == randomNumber) {
                    randomNumber = (int) (Math.random() * resNumber);
                }
                nowNumber = (int) (Math.random() * resNumber);
                markers.get(nowNumber).showInfoWindow();
                rvRes.smoothScrollToPosition(nowNumber);
            }
        });
    }

    private void showRess(List<Res> ress) {
        if (ress == null || ress.isEmpty()) {
            Common.showToast(activity, R.string.textNoRessFound);
        }
        ResMapFragment.ResAdapter resAdapter = (ResMapFragment.ResAdapter) rvRes.getAdapter();
        // 如果resAdapter不存在就建立新的，否則續用舊有的
        if (resAdapter == null) {
            rvRes.setAdapter(new ResMapFragment.ResAdapter(activity, ress));
        } else {
            resAdapter.setRess(ress);
            resAdapter.notifyDataSetChanged();
        }
        if (markers.size() != 0) {
            markers.get(0).showInfoWindow();
        }
    }

    private List<Res> getRess() {
        List<Res> ress = null;
        if (Common.networkConnected(activity)) {
            String url = Common.URL_SERVER + "ResServlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "getAllEnable");
            jsonObject.addProperty("userId", Common.USER_ID);
            String jsonOut = jsonObject.toString();
            resGetAllTask = new CommonTask(url, jsonOut);
            try {
                String jsonIn = resGetAllTask.execute().get();
                Type listType = new TypeToken<List<Res>>() {
                }.getType();
                ress = new Gson().fromJson(jsonIn, listType);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        } else {
            Common.showToast(activity, R.string.textNoNetwork);
        }
        return ress;
    }

    @Override
    public void onStart() {
        super.onStart();
        Common.setBackArrow(false, activity);
        mapView.onStart();
        askAccessLocationPermission();
        if (lastLocation != null && fusedLocationClient != null) {
            if (ActivityCompat.checkSelfPermission(activity,
                    Manifest.permission.ACCESS_FINE_LOCATION) ==
                    PackageManager.PERMISSION_DENIED) {
                //todo 如果拒絕
                return;
            }
            fusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    if (task.isSuccessful()) {
                        lastLocation = task.getResult();
                        if (lastLocation != null) {
                            moveMap(new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude()));
                            map.clear();
                            markers = new ArrayList<>();
                            for (Res res : nearRess) {
                                addMarker(new LatLng(res.getResLat(), res.getResLon()), res.getResName());
                            }
                            showRess(nearRess);

                            rvRes.scrollToPosition(0);

                            if (map != null) {
                                map.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
                                    @Override
                                    public void onCameraMove() {
                                        float[] results = new float[1];
                                        cameraLatLng = map.getCameraPosition().target;

                                        if (LastCameraLatLng == null) {
                                            Location.distanceBetween(lastLocation.getLatitude(), lastLocation.getLongitude(),
                                                    cameraLatLng.latitude, cameraLatLng.longitude, results);
                                        } else {
                                            Location.distanceBetween(LastCameraLatLng.latitude, LastCameraLatLng.longitude,
                                                    cameraLatLng.latitude, cameraLatLng.longitude, results);
                                        }
                                        if (results[0] != 0 && results[0] > 1000.0) {
                                            btSearchResAgain.setVisibility(View.VISIBLE);
                                        } else {
                                            btSearchResAgain.setVisibility(View.GONE);
                                        }
                                    }
                                });
                            }
                        } else {
                            //lLisNull = true;
                        }
                    }
                }
            });
            fusedLocationClient.requestLocationUpdates(
                    locationRequest, locationCallback, null);
        }
    }

    private void askAccessLocationPermission() {
        String[] permissions = {
                Manifest.permission.ACCESS_FINE_LOCATION
        };

        int result = ActivityCompat.checkSelfPermission(activity, permissions[0]);
        if (result == PackageManager.PERMISSION_DENIED) {
            requestPermissions(permissions, PER_ACCESS_LOCATION);
        }
    }

    private void checkLocationSettings() {
        // 必須將LocationRequest設定加入檢查
        LocationSettingsRequest.Builder builder =
                new LocationSettingsRequest.Builder()
                        .addLocationRequest(locationRequest);
        Task<LocationSettingsResponse> task =
                LocationServices.getSettingsClient(activity)
                        .checkLocationSettings(builder.build());
        task.addOnSuccessListener(activity, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                // 取得並顯示最新位置
                showMyLocation();
            }
        });
        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    try {
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        // 跳出Location設定的對話視窗
                        resolvable.startResolutionForResult(activity, REQ_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException sendEx) {
                        Log.e(TAG, e.getMessage());
                    }
                }
            }
        });
    }

    private void showMyLocation() {
        if (ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            map.setMyLocationEnabled(true);
        }

        if (fusedLocationClient == null) {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity);
            if (ActivityCompat.checkSelfPermission(activity,
                    Manifest.permission.ACCESS_FINE_LOCATION) ==
                    PackageManager.PERMISSION_DENIED) {
                //todo 如果拒絕
                return;
            }
            fusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    if (task.isSuccessful()) {
                        lastLocation = task.getResult();
                        if (lastLocation != null) {
                            moveMap(new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude()));
                            if (nearRess.size() == 0 && ress != null && map != null) {
                                map.clear();
                                markers = new ArrayList<>();
                                float[] results = new float[1];
                                for (Res res : ress) {
                                    Location.distanceBetween(lastLocation.getLatitude(), lastLocation.getLongitude(),
                                            res.getResLat(), res.getResLon(), results);
                                    if (results[0] != 0 && results[0] <= 2000.0) {
                                        nearRess.add(res);
                                        addMarker(new LatLng(res.getResLat(), res.getResLon()), res.getResName());
                                    }
                                }
                                showRess(nearRess);
                            }

                            if (map != null) {
                                map.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
                                    @Override
                                    public void onCameraMove() {
                                        float[] results = new float[1];
                                        cameraLatLng = map.getCameraPosition().target;

                                        if (LastCameraLatLng == null) {
                                            Location.distanceBetween(lastLocation.getLatitude(), lastLocation.getLongitude(),
                                                    cameraLatLng.latitude, cameraLatLng.longitude, results);
                                        } else {
                                            Location.distanceBetween(LastCameraLatLng.latitude, LastCameraLatLng.longitude,
                                                    cameraLatLng.latitude, cameraLatLng.longitude, results);
                                        }
                                        if (results[0] != 0 && results[0] > 1000.0) {
                                            btSearchResAgain.setVisibility(View.VISIBLE);
                                        } else {
                                            btSearchResAgain.setVisibility(View.GONE);
                                        }
                                    }
                                });
                            }
                            btSearchResAgain.performClick();
                        } else {
                            //lLisNull = true;
                        }
                    }
                }
            });

            // 持續取得最新位置。looper設為null代表以現行執行緒呼叫callback方法，而非使用其他執行緒
            fusedLocationClient.requestLocationUpdates(
                    locationRequest, locationCallback, null);
        }
    }

    private void moveMap(LatLng latLng) {
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latLng)
                .zoom(15)
                .build();
        CameraUpdate cameraUpdate = CameraUpdateFactory
                .newCameraPosition(cameraPosition);
        //map.animateCamera(cameraUpdate);
        map.moveCamera(cameraUpdate);
    }

    // 打標記
    private void addMarker(LatLng latLng, String resName) {
        //BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.pink_marker);
        Address address = reverseGeocode(latLng.latitude, latLng.longitude);
        if (address == null) {
            //Toast.makeText(activity, R.string.textLocationNotFound, Toast.LENGTH_SHORT).show();
            return;
        }
        // 取得地址當作說明文字
        String snippet = address.getAddressLine(0);
        Marker marker = map.addMarker(new MarkerOptions()
                .position(latLng)
                .title(resName) //餐廳名稱當做標題
                .snippet(snippet));
        //.icon(icon));
        markers.add(marker);
    }

    private Address reverseGeocode(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(activity);
        List<Address> addressList = null;
        try {
            addressList = geocoder.getFromLocation(latitude, longitude, 1);
        } catch (IOException e) {
            Log.e(TAG, e.toString());
        }

        if (addressList == null || addressList.isEmpty()) {
            return null;
        } else {
            return addressList.get(0);
        }
    }

    private class ResAdapter extends RecyclerView.Adapter<ResMapFragment.ResAdapter.MyViewHolder> {
        private LayoutInflater layoutInflater;
        private List<Res> ress;
        private int imageSize;

        ResAdapter(Context context, List<Res> ress) {
            layoutInflater = LayoutInflater.from(context);
            this.ress = ress;
            /* 螢幕寬度除以4當作將圖的尺寸 */
            imageSize = getResources().getDisplayMetrics().widthPixels / 4;
        }

        void setRess(List<Res> ress) {
            this.ress = ress;
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            ImageView imageView, ivMyRes;
            TextView tvResName, tvResRating, tvResAddress, tvResCategoryInfo, tvResDistance;

            MyViewHolder(View itemView) {
                super(itemView);
                imageView = itemView.findViewById(R.id.ivRes);
                tvResName = itemView.findViewById(R.id.tvResName);
                tvResRating = itemView.findViewById(R.id.tvResRating);
                tvResAddress = itemView.findViewById(R.id.tvResAddress);
                tvResCategoryInfo = itemView.findViewById(R.id.tvResCategoryInfo);
                tvResDistance = itemView.findViewById(R.id.tvResDistance);
                ivMyRes = itemView.findViewById(R.id.ivMyRes);
            }
        }

        @Override
        public int getItemCount() {
            return ress == null ? 0 : ress.size();
        }

        @NonNull
        @Override
        public ResMapFragment.ResAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = layoutInflater.inflate(R.layout.item_view_res_map, parent, false);
            return new ResMapFragment.ResAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull ResMapFragment.ResAdapter.MyViewHolder myViewHolder, int position) {
            final Res res = ress.get(position);
            String url = Common.URL_SERVER + "ResServlet";
            int id = res.getResId();
            ImageTask imageTask = new ImageTask(url, id, imageSize, myViewHolder.imageView);
            imageTask.execute();
            imageTasks.add(imageTask);
            myViewHolder.tvResName.setText(res.getResName());
            myViewHolder.tvResAddress.setText(res.getResAddress());
            if (res.getRating() >= 0) {
                myViewHolder.tvResRating.setText(String.format("%.1f", res.getRating()));
            } else {
                myViewHolder.tvResRating.setText(R.string.textNoRating);
            }
            myViewHolder.tvResCategoryInfo.setText(res.getResCategoryInfo());
            if (lastLocation != null) {
                float[] results = new float[1];
                Location.distanceBetween(lastLocation.getLatitude(), lastLocation.getLongitude(),
                        res.getResLat(), res.getResLon(), results);
                if (results[0] < 1000) {
                    myViewHolder.tvResDistance.setText(String.format("%.0f公尺", results[0]));
                } else {
                    myViewHolder.tvResDistance.setText(String.format("%.2f公里", results[0] / 1000f));
                }
            }

            if (res.isMyRes()) {
                myViewHolder.ivMyRes.setImageResource(R.drawable.ic_baseline_turned_in_24);
                myViewHolder.ivMyRes.setColorFilter(Color.parseColor("#1877F2"));
            } else {
                myViewHolder.ivMyRes.setImageResource(R.drawable.ic_baseline_turned_in_not_24);
                myViewHolder.ivMyRes.setColorFilter(Color.parseColor("#424242"));
            }

            myViewHolder.ivMyRes.setOnClickListener(v -> {
                String urlMyRes = Common.URL_SERVER + "MyResServlet";
                if (Common.USER_ID <= 0) {
                    new AlertDialog.Builder(activity)
                            .setTitle("您尚未登入，要進行登入嗎？")
                            .setPositiveButton(R.string.textOK, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Navigation.findNavController(v)
                                            .navigate(R.id.action_resMapFragment_to_loginFragment);
                                }
                            }).setNegativeButton(R.string.textCancel, null).create()
                            .show();
                } else if (res.isMyRes()) {
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("action", "myResDelete");
                    jsonObject.addProperty("userId", Common.USER_ID);
                    jsonObject.addProperty("resId", res.getResId());

                    int count = 0;
                    try {
                        String result = new CommonTask(urlMyRes, jsonObject.toString()).execute().get();
                        count = Integer.parseInt(result);
                    } catch (Exception e) {
                        Log.e(TAG, e.toString());
                    }
                    if (count == 0) {
                        Common.showToast(activity, R.string.textDeleteMyResFail);
                    } else {
                        Common.showToast(activity, R.string.textDeleteMyResSuccess);
                        myViewHolder.ivMyRes.setImageResource(R.drawable.ic_baseline_turned_in_not_24);
                        myViewHolder.ivMyRes.setColorFilter(Color.parseColor("#424242"));
                        res.setMyRes(false);
                    }
                } else {
                    MyRes myRes = new MyRes(0, Common.USER_ID, res.getResId(), null);
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("action", "myResInsert");
                    jsonObject.addProperty("myres", new Gson().toJson(myRes));

                    int count = 0;
                    try {
                        String result = new CommonTask(urlMyRes, jsonObject.toString()).execute().get();
                        count = Integer.parseInt(result);
                    } catch (Exception e) {
                        Log.e(TAG, e.toString());
                    }
                    if (count == 0) {
                        Common.showToast(activity, R.string.textInsertMyResFail);
                    } else {
                        Common.showToast(activity, R.string.textInsertMyResSuccess);
                        myViewHolder.ivMyRes.setImageResource(R.drawable.ic_baseline_turned_in_24);
                        myViewHolder.ivMyRes.setColorFilter(Color.parseColor("#1877F2"));
                        res.setMyRes(true);
                    }
                }
            });

            myViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("res", res);
                    Navigation.findNavController(v)
                            .navigate(R.id.action_resMapFragment_to_resDetailFragment, bundle);
                }
            });

        }

    }

    @Override
    public void onPause() {
        super.onPause();
        if (fusedLocationClient != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (resGetAllTask != null) {
            resGetAllTask.cancel(true);
            resGetAllTask = null;
        }

        if (imageTasks != null && imageTasks.size() > 0) {
            for (ImageTask imageTask : imageTasks) {
                imageTask.cancel(true);
            }
            imageTasks.clear();
        }

        if (resDeleteTask != null) {
            resDeleteTask.cancel(true);
            resDeleteTask = null;
        }
    }
}