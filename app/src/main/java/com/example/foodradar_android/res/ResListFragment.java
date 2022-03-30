package com.example.foodradar_android.res;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class ResListFragment extends Fragment {
    private NavController navController;
    private final static String TAG = "TAG_ResListFragment";
    private FragmentActivity activity;
    private RecyclerView rvRes;
    private CommonTask resGetAllTask;
    private List<ImageTask> imageTasks;
    private List<Res> ress;
    private LocationCallback locationCallback;
    private LocationRequest locationRequest;
    private Location lastLocation;
    private FusedLocationProviderClient fusedLocationClient;
    private static final int REQ_CHECK_SETTINGS = 101;
    private static final int PER_ACCESS_LOCATION = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activity = getActivity();
        new Common();
        Common.setBackArrow(true, activity);
        setHasOptionsMenu(true);
        navController = Navigation.findNavController(activity, R.id.mainFragment);
        imageTasks = new ArrayList<>();
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
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.res_list_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.resMapFragment:
            case android.R.id.home:
                navController.popBackStack();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity.setTitle(R.string.textResList);
        return inflater.inflate(R.layout.fragment_res_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SearchView searchView = view.findViewById(R.id.searchView);
        rvRes = view.findViewById(R.id.rvRes);

        rvRes.setLayoutManager(new LinearLayoutManager(activity));
        askAccessLocationPermission();
        checkLocationSettings();

        ress = getRess();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) {
                // 如果搜尋條件為空字串，就顯示原始資料；否則就顯示搜尋後結果
                if (newText.isEmpty()) {
                    showRess(ress);
                } else {
                    List<Res> searchRess = new ArrayList<>();
                    // 搜尋原始資料內有無包含關鍵字(不區別大小寫)
                    for (Res res : ress) {
                        if (res.getResName().toUpperCase().contains(newText.toUpperCase())) {
                            searchRess.add(res);
                        }
                    }
                    if (lastLocation != null) {
                        sort(searchRess);
                    }
                    showRess(searchRess);
                }
                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
        });

        Button btMyResList = view.findViewById(R.id.btMyResList);
        btMyResList.setOnClickListener(v -> {
            if (Common.USER_ID <= 0) {
                new AlertDialog.Builder(activity)
                        .setTitle("您尚未登入，要進行登入嗎？")
                        .setPositiveButton(R.string.textOK, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Navigation.findNavController(v)
                                        .navigate(R.id.action_resListFragment_to_loginFragment);
                            }
                        }).setNegativeButton(R.string.textCancel, null).create()
                        .show();
            } else {
                Navigation.findNavController(v)
                        .navigate(R.id.action_resListFragment_to_userMyResFragment);
            }
        });
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

    @Override
    public void onStart() {
        super.onStart();
        if (lastLocation != null) {
            sort(ress);
            showRess(ress);
        }
    }

    private void showMyLocation() {
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
                        sort(ress);
                        showRess(ress);
                    }
                }
            });

            // 持續取得最新位置。looper設為null代表以現行執行緒呼叫callback方法，而非使用其他執行緒
            fusedLocationClient.requestLocationUpdates(
                    locationRequest, locationCallback, null);
        }
    }

    private void sort(List<Res> ress) {
        if (lastLocation != null) {
            float[] results = new float[1];
            for (Res res : ress) {
                Location.distanceBetween(lastLocation.getLatitude(), lastLocation.getLongitude(),
                        res.getResLat(), res.getResLon(), results);
                res.setDistance(results[0]);
            }
            Collections.sort(ress);
        }
    }

    private void showRess(List<Res> ress) {
        if (ress == null || ress.isEmpty()) {
            Common.showToast(activity, R.string.textNoRessFound);
        }
        ResListFragment.ResAdapter resAdapter = (ResListFragment.ResAdapter) rvRes.getAdapter();
        // 如果resAdapter不存在就建立新的，否則續用舊有的
        if (resAdapter == null) {
            rvRes.setAdapter(new ResListFragment.ResAdapter(activity, ress));
        } else {
            resAdapter.setRess(ress);
            resAdapter.notifyDataSetChanged();
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

    private class ResAdapter extends RecyclerView.Adapter<ResListFragment.ResAdapter.MyViewHolder> {
        private LayoutInflater layoutInflater;
        private List<Res> ress;
        private int imageSize;
        private View visibleView;

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
            ImageView imageView, ivResLocation, ivMyRes;
            TextView tvResName, tvResRating, tvResAddress, tvResCategoryInfo, tvResDistance;

            MyViewHolder(View itemView) {
                super(itemView);
                itemView.setBackgroundColor(Color.parseColor("#F5F5DC"));
                imageView = itemView.findViewById(R.id.ivRes);
                tvResName = itemView.findViewById(R.id.tvResName);
                tvResRating = itemView.findViewById(R.id.tvResRating);
                tvResAddress = itemView.findViewById(R.id.tvResAddress);
                tvResCategoryInfo = itemView.findViewById(R.id.tvResCategoryInfo);
                tvResDistance = itemView.findViewById(R.id.tvResDistance);
                ivResLocation = itemView.findViewById(R.id.ivResLocation);
                ivResLocation.setBackgroundColor(Color.parseColor("#F5F5DC"));
                ivMyRes = itemView.findViewById(R.id.ivMyRes);
            }
        }

        @Override
        public int getItemCount() {
            return ress == null ? 0 : ress.size();
        }

        @NonNull
        @Override
        public ResListFragment.ResAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = layoutInflater.inflate(R.layout.item_view_res_map, parent, false);
            return new ResListFragment.ResAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull ResListFragment.ResAdapter.MyViewHolder myViewHolder, int position) {
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

            myViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("res", res);
                    Navigation.findNavController(v)
                            .navigate(R.id.action_resListFragment_to_resDetailFragment, bundle);
                }
            });

            if (res.isMyRes()) {
                myViewHolder.ivMyRes.setImageResource(R.drawable.ic_baseline_turned_in_24);
                myViewHolder.ivMyRes.setColorFilter(Color.parseColor("#1877F2"));
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
                                            .navigate(R.id.action_resListFragment_to_loginFragment);
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
//                    jsonObject.addProperty("myres", new Gson().toJson(myRes));
                    // vvvvvv 直接把物件經GSON傳到後端Servlet的寫法，其中日期時間，有特別進行格式處理以免解析時格式無法確認
                    Gson gson =  new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").create();
                    jsonObject.addProperty("myres", gson.toJson(myRes));
                    // ^^^^^^ 直接把物件經GSON傳到後端Servlet的寫法，其中日期時間，有特別進行格式處理以免解析時格式無法確認

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
        }

    }

}