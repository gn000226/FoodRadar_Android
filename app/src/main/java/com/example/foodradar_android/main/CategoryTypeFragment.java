package com.example.foodradar_android.main;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.foodradar_android.Common;
import com.example.foodradar_android.R;
import com.example.foodradar_android.res.Res;
import com.example.foodradar_android.task.CommonTask;
import com.example.foodradar_android.task.ImageTask;
import com.example.foodradar_android.user.MyRes;
import com.example.foodradar_android.user.UserImageTask;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class CategoryTypeFragment extends Fragment {
    private static final String TAG = "TAG_CategoryType";
    private SwipeRefreshLayout swipeRefreshLayout;
    private Activity activity;
    private NavController navController;
    private RecyclerView rvRess;
    private ImageView ibcate;
    private TextView resSecName;
    private List<Res> ress;
    private List<MyRes> myResList = new ArrayList<>();
    private List<ImageTask> imageTasks;
    private ImageTask imageTask;
    private int imageSize;
    private CommonTask ResGetAllTask;
    private CommonTask ResDeleteTask;
    private int categoryId;
    private int resId;
    private String URL_SERVER = "http://10.0.2.2:8080/FoodRadar_Web/";
    private String MYRES_SERVLET = URL_SERVER + "MyResServlet";




    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        new Common();
        Common.setBackArrow(true, activity);
        setHasOptionsMenu(true);
        navController = Navigation.findNavController(activity, R.id.mainFragment);
        imageTasks = new ArrayList<>();


    }
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                navController.popBackStack();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_china_restaurant, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle bundle = getArguments();
        categoryId = bundle.getInt("resId");

        Category category = (Category)bundle.getSerializable("category");

        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);

        rvRess = view.findViewById(R.id.rvSec);
        rvRess.setLayoutManager(new LinearLayoutManager(activity));
        ress = getRess();
        showRess(ress);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                showRess(ress);
                swipeRefreshLayout.setRefreshing(false);
            }
        });
//        ibcate = view.findViewById(R.id.ibcate);
//        caName = view.findViewById(R.id.resName);

//        String url = Common.URL_SERVER + "ResServlet";
//        int id = ress.getresName();
//        imageSize = getResources().getDisplayMetrics().widthPixels / 2;
//        imageTask = new ImageTask(url, id, imageSize, ibcate);
//        imageTask.execute();


        //Book book = (Book) bundle.getSerializable("book");
        //Bundle bundle = new Bundle();
//        Common.showToast(activity,"TEST");
//        Common.showToast(activity,bundle.getString("categoryType"));
        activity.setTitle(category.getInfo());
    }

    private List<Res> getRess() {
        List<Res> ress = null;
        if (Common.networkConnected(activity)) {
            String url = Common.URL_SERVER + "ResServlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "categoryfindById");
            jsonObject.addProperty("id",categoryId);

            String jsonOut = jsonObject.toString();
            ResGetAllTask = new CommonTask(url, jsonOut);
            try {
                String jsonIn = ResGetAllTask.execute().get();
                Log.d("TAG_", "jsonIn: " + jsonIn);
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
    private void showRess(List<Res> ress){
        if(ress == null || ress.isEmpty()){
            Common.showToast(activity, R.string.textNoMainFound);
        }
        ResAdapter resAdapter = (ResAdapter) rvRess.getAdapter();
           if(resAdapter == null){
               rvRess.setAdapter(new ResAdapter(activity, ress));
           }else{
               resAdapter.setRess(ress);
               resAdapter.notifyDataSetChanged();
           }
    }
//    @Override
//    public void onResume() {
//        super.onResume();
//        // 設定畫面到首頁一律不顯示返回鍵
//        new Common().setBackArrow(false,activity);
//        // 設定首頁AppBar(ActionBar)的Title(抬頭)
////        activity.setTitle(R.string.chinacategory);
//
//    }


    private class ResAdapter extends RecyclerView.Adapter<ResAdapter.MyViewHolder>{
        private LayoutInflater layoutInflater;
        private List<Res> ress;

        private int imageSize;

        ResAdapter(Context context, List<Res> ress){
            layoutInflater = LayoutInflater.from(context);
            this.ress = ress;
            imageSize = getResources().getDisplayMetrics().widthPixels /4;
        }
        public void setRess(List<Res> ress) {
            this.ress = ress;
        }
        public class MyViewHolder extends RecyclerView.ViewHolder {
            ImageView imageView;
            TextView tvResName, tvResHours, tvResTel, tvResAddress;
            TextView resSecName;
            public MyViewHolder(@NonNull View itemView) {
                super(itemView);
                imageView = itemView.findViewById(R.id.ibcate);
                tvResName = itemView.findViewById(R.id.tvResName);
                tvResHours = itemView.findViewById(R.id.tvResHours);
                tvResTel = itemView.findViewById(R.id.tvResTel);
                tvResAddress = itemView.findViewById(R.id.tvResAddress);
                resSecName = itemView.findViewById(R.id.resSecName);
            }
        }
        @Override
        public int getItemCount() {
            return ress == null ? 0 : ress.size();
        }

        @NonNull
        @Override
        public ResAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = layoutInflater.inflate(R.layout.item_view_category, parent, false);
            return new CategoryTypeFragment.ResAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull ResAdapter.MyViewHolder myViewHolder, int position) {
            Res res = ress.get(position);
           // myViewHolder.tvResName.setText(res.getResName());
           // myViewHolder.tvResHours.setText(getResources().getString(R.string.textResHours)) + ": " + res.getResHours());
           // myViewHolder.tvResTel.setText(getResources().getString(R.string.textResTel)) + ": " + res.getResTel();
           // myViewHolder.tvResAddress.setText(getResources().getString(R.string.textResAddress) + ": " + res.getResAddress());

            Integer resID = res.getResId(); // 餐廳ID
            String url = Common.URL_SERVER + "ResServlet";
            int id = res.getResId();
//            Log.d(TAG, "id: " + res.getResId());
//            Log.d(TAG, "id: " + res.getResName());
//
            myViewHolder.resSecName.setText(res.getResName());
//
            ImageTask imageTask = new ImageTask(url, id, imageSize, myViewHolder.imageView);
            imageTask.execute();
            //Log.d(TAG, "imageTask: " + imageTaskBVH);
            imageTasks.add(imageTask);

            myViewHolder.itemView.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View view) {
                    Integer resID = res.getResId();
                    List<Res> resBidVH = new ArrayList<>();
                    CommonTask getResTask;

                    if (Common.networkConnected(activity)) {
                        String url = MYRES_SERVLET;
                        Log.d(TAG, "getResById.url: " + url);
                        JsonObject jsonObject = new JsonObject();
                        jsonObject.addProperty("action", "getResById");
                        jsonObject.addProperty("id", resID);
                        jsonObject.addProperty("userId", Common.USER_ID);
                        String jsonOut = jsonObject.toString();
                        Log.d(TAG, "getResById.jsonOut: " + jsonOut);
                        getResTask = new CommonTask(url, jsonOut);
                        try {
                            String jsonIn = getResTask.execute().get();
                            Log.d(TAG, "getResById.jsonIn: " + jsonIn);
                            Type listType = new TypeToken<List<Res>>() {
                            }.getType();
                            resBidVH = new Gson().fromJson(jsonIn, listType);
                        } catch (Exception e) {
                            Log.e(TAG, e.toString());
                        }
                    } else {
                        Common.showToast(activity, R.string.textNoNetwork);
                    }

                    //Navigation.findNavController(view).navigate(R.id.action_CategoryTypeFragment_to_resDetailFragment, bundle);
                    if (getUserId() <= 0) {
                        AlertDialog.Builder d = new AlertDialog.Builder(activity);
                        d.setTitle("警告！")
                                .setMessage("您還未加入會員")
                                .setCancelable(false);
                        d.setPositiveButton("登入會員", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
//                                Bundle bundle = new Bundle();
//                                bundle.putSerializable("res", res);
                                navController.navigate(R.id.action_CategoryTypeFragment_to_userDataSetupFragment);
                            }
                        });
                        d.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                        d.setCancelable(false) // 必須點擊按鈕方能關閉，預設為true
                                .show();
                    } else if (getUserId() > 0 && resBidVH != null) {
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("res", resBidVH.get(0));
                        Navigation.findNavController(view)
                                .navigate(R.id.action_CategoryTypeFragment_to_resDetailFragment, bundle);
                        //Navigation.findNavController(view).navigate(R.id.action_CategoryTypeFragment_to_resDetailFragment, bundle);
                    } else {
                        Common.showToast(activity, R.string.textNoRessFound);
                    }
                }
        });
        }

    }
    private int getUserId(){
        return Common.USER_ID;
    }
}