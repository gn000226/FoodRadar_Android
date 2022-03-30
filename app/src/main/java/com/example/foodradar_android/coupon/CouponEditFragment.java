package com.example.foodradar_android.coupon;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.view.DragStartHelper;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.SearchView;
import android.widget.TextView;

import com.example.foodradar_android.Common;
import com.example.foodradar_android.R;
import com.example.foodradar_android.article.Article;
import com.example.foodradar_android.article.ResAddress;
import com.example.foodradar_android.res.Res;
import com.example.foodradar_android.task.CommonTask;
import com.example.foodradar_android.task.ImageTask;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;


public class CouponEditFragment extends Fragment  {
    private static final String TAG = "TAG_CouponEditFragment";
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView rvEditCoupon;
    private NavController navController;
    private Activity activity;
    private DatePickerDialog datePickerDialog;
    private DatePickerDialog.OnDateSetListener dateSetListener;
    private CommonTask couponGetAllTask;
    private CommonTask couponDeleteTask;
    private List<ImageTask> imageTasks;
    private List<Coupon> coupons;
    private Boolean bolAccessExternalStorage;
    private static final int PER_EXTERNAL_STORAGE = 201;
    private final static String PREFERENCES_NAME = "Res";   //儲存檔名
    private final static String DEFAULT_FILE_NAME = " "; //抓不到檔案就顯示
    private SharedPreferences preferences;
    private TextView tvEditResName, couPonStartDate, couPonEndDate, tvEditCouInfo;
    private int newRescoupon;
    private String resName;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        imageTasks = new ArrayList<>();
        setHasOptionsMenu(true);
        navController = Navigation.findNavController(activity, R.id.mainFragment);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        activity.setTitle(R.string.title_of_res_coupon_maintain);
        return inflater.inflate(R.layout.fragment_coupon_edit, container, false);
    }
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
//        inflater.inflate(R.menu.appbar_menu,menu);  // 從res取用選項的清單“R.menu.my_menu“
//        super.onCreateOptionsMenu(menu, inflater);
    }
    // 顯示右上角的OptionMenu選單
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
//            case R.id.Finish:
//                navController.navigate(R.id.action_userAreaFragment_to_userSysSetupFragment);
//                break;
            case android.R.id.home:
                navController.popBackStack();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SearchView searchView = view.findViewById(R.id.searchView);
        rvEditCoupon = view.findViewById(R.id.rvEditCoupon);
        tvEditResName = view.findViewById(R.id.tvEditResName);

        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        rvEditCoupon.setLayoutManager(new LinearLayoutManager(activity));

        coupons = getCoupons();
        showCoupons(coupons);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                showCoupons(coupons);
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String newText) {
                if (newText.isEmpty()) {
                    showCoupons(coupons);
                } else {
                    List<Coupon> searchCoupons = new ArrayList<>();
                    // 搜尋原始資料內有無包含關鍵字(不區別大小寫)
                    for (Coupon coupon : coupons) {
                        if (coupon.getCouPonInfo().toUpperCase().contains(newText.toUpperCase())) {
                            searchCoupons.add(coupon);
                        }
                    }
                    showCoupons(searchCoupons);
                }
                return true;

            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        FloatingActionButton btEditAdd = view.findViewById(R.id.btEditAdd);
        btEditAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle1 = new Bundle();
                int newArticle = 2;    //狀態判斷碼  > bundle帶到優惠管理
                bundle1.putInt("newArticle", newArticle);
                navController.navigate(R.id.couponMaintainFragment, bundle1);
            }
        });

    }


    private List<Coupon> getCoupons() {
        List<Coupon> coupons = null;
        if (Common.networkConnected(activity)) {
            String url = Common.URL_SERVER + "CouponServlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "getAll");
            jsonObject.addProperty("userId", Common.USER_ID);
            String jsonOut = jsonObject.toString();
            couponGetAllTask = new CommonTask(url, jsonOut);
            try {
                String jsonIn = couponGetAllTask.execute().get();
                Type listType = new TypeToken<List<Coupon>>() {
                }.getType();
                coupons = new Gson().fromJson(jsonIn, listType);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        } else {
            Common.showToast(activity, R.string.textNoNetwork);
        }
        return coupons;
    }
    private void showCoupons(List<Coupon> coupons) {
        if (coupons == null || coupons.isEmpty()) {
            Common.showToast(activity, R.string.textNoCouponsFound);
        }

        CouponEditAdapter couponeditAdapter = (CouponEditAdapter) rvEditCoupon.getAdapter();

        if (couponeditAdapter == null) {
            rvEditCoupon.setAdapter(new CouponEditAdapter(activity, coupons));
        } else {
            couponeditAdapter.setCoupons(coupons);
            couponeditAdapter.notifyDataSetChanged();
        }
    }

    private class CouponEditAdapter extends RecyclerView.Adapter<CouponEditAdapter.MyViewHolder> {
        private LayoutInflater layoutInflater;
        private List<Coupon> coupons;
        private int imageSize;

        CouponEditAdapter(Context context, List<Coupon> coupons) {
            layoutInflater = LayoutInflater.from(context);
            this.coupons = coupons;
            imageSize = getResources().getDisplayMetrics().widthPixels / 4;
        }

        public void setCoupons(List<Coupon> coupons) {
            this.coupons = coupons;
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            ImageView imageView;
            TextView tvEditResName, couPonStartDate, tvEditCouInfo, couPonEndDate;


            public MyViewHolder(@NonNull View itemView) {
                super(itemView);
                imageView = itemView.findViewById(R.id.ivEditCoupon);
                tvEditResName = itemView.findViewById(R.id.tvEditResName);
                couPonStartDate = itemView.findViewById(R.id.couPonStartDate);
                couPonEndDate = itemView.findViewById(R.id.couPonEndDate);
                tvEditCouInfo = itemView.findViewById(R.id.tvEditCouInfo);

            }
        }

        @Override
        public int getItemCount() {
            return coupons == null ? 0 : coupons.size();
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = layoutInflater.inflate(R.layout.item_view_editcoupon, parent, false);
            return new CouponEditAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            final Coupon coupon = coupons.get(position);

            String url = Common.URL_SERVER + "CouponServlet";
            int id = coupon.getCouPonId();
//            JsonObject jsonObject = new JsonObject();
//            jsonObject.addProperty("action", "getAll");
//            jsonObject.addProperty("couPonId", id);
//            jsonObject.addProperty("resName", resName);
//            jsonObject.addProperty("coupon", new Gson().toJson(coupon));

            ImageTask imageTask = new ImageTask(url, id, imageSize, holder.imageView);
            imageTask.execute();
            imageTasks.add(imageTask);


            //Bundle bundle = getArguments();
//            newRescoupon = bundle.getInt("newArticle");


//            askExternalStoragePermission();
//            preferences = activity.getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE);
//            //resId = preferences.getInt("resId", 0);
//            String resName = preferences.getString("ResName", DEFAULT_FILE_NAME);
//            String resCategory = preferences.getString("Category", DEFAULT_FILE_NAME);
//            holder.tvEditResName.setText(resCategory + resName);
//            if (newRescoupon == 2) {
//                tvEditResName.setText("店名：請選擇餐廳");
//                //將bundle內的資料(int)改成0
//                bundle.putInt("newArticle", 0);
//            } else {
//                tvEditResName.setText("店名：請選擇餐廳2");
//
//            }

            holder.tvEditResName.setText(coupon.getResName());
            holder.couPonStartDate.setText(coupon.getCouPonStartDate());
            holder.couPonEndDate.setText(coupon.getCouPonEndDate());
            holder.tvEditCouInfo.setText(coupon.getCouPonInfo());



            //holder.tvEditResName.setText(coupon.getResId());

            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(final View view) {
                    PopupMenu popupMenu = new PopupMenu(activity, view, Gravity.END);
                    popupMenu.inflate(R.menu.coupon_popupmenu);
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.update:
                                    Bundle bundle = new Bundle();
                                    bundle.putSerializable("coupon", coupon);
                                    Navigation.findNavController(view)
                                            .navigate(R.id.action_couponEditFragment_to_couponUpdataFragment, bundle);
                                    break;
//                                case R.id.delete:
//
//
//                                    if (Common.networkConnected(activity)) {
//                                        String url = Common.URL_SERVER + "CouponServlet";
//                                        JsonObject jsonObject = new JsonObject();
//                                        jsonObject.addProperty("action", "couponDelete");
//                                        jsonObject.addProperty("couPonId", couPonId);
//                                        int count = 0;
//                                        try {
//                                            couponDeleteTask = new CommonTask(url, jsonObject.toString());
//                                            String result = couponDeleteTask.execute().get();
//                                            count = Integer.parseInt(result);
//                                        } catch (Exception e) {
//                                            Log.e(TAG, e.toString());
//                                        }
//                                        if (count == 0) {
//                                            Common.showToast(activity, R.string.textDeleteFail);
//                                        } else {
//                                            coupons.remove(coupon);
//                                            CouponAdapter.this.notifyDataSetChanged();
//                                            // 外面spots也必須移除選取的spot
//                                            CouponListFragment.this.spots.remove(coupon);
//                                            Common.showToast(activity, R.string.textDeleteSuccess);
//                                        }
//                                    } else {
//                                        Common.showToast(activity, R.string.textNoNetwork);
//                                    }
                            }
                            return true;
                        }
                    });
                    popupMenu.show();
                    return true;
                }
            });

        }
    }

//    @Override
//    public void onStart() {
//        super.onStart();
//        askExternalStoragePermission();
//        preferences = activity.getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE);
//        //resId = preferences.getInt("resId", 0);
//        String resName = preferences.getString("ResName", DEFAULT_FILE_NAME);
//        String resCategory = preferences.getString("Category", DEFAULT_FILE_NAME);
//
//        if (newRescoupon == 0) {
//            tvEditResName.setText(Integer.toString(resId));
//            tvEditResName.setText(resCategory + "餐廳：" + resName);
//        } else {
//            tvEditResName.setText("店名：請選擇餐廳");
//
//        }
//    }

    private void askExternalStoragePermission() {
        String[] permissions = {
                Manifest.permission.READ_EXTERNAL_STORAGE
        };

        int result = ContextCompat.checkSelfPermission(activity, permissions[0]);
        if (result == PackageManager.PERMISSION_DENIED) {
            requestPermissions(permissions, PER_EXTERNAL_STORAGE);
        }
    }


}