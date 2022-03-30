package com.example.foodradar_android.main;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.foodradar_android.Common;
import com.example.foodradar_android.R;
import com.example.foodradar_android.task.CommonTask;
import com.example.foodradar_android.task.ImageTask;
import com.example.foodradar_android.user.UserAccount;
import com.google.android.gms.common.api.Api;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;
import java.util.zip.InflaterOutputStream;


public class CategoryFragment extends Fragment {
    private static final String TAG = "TAG_CategoryFragment";
    private SwipeRefreshLayout swipeRefreshLayout;
    private Activity activity;
    private NavController navController;
    private RecyclerView rvMain;
    private CommonTask CategoryGetAllTask;
    private CommonTask CategoryDeleteTask;
    private List<ImageTask> imageTasks;
    private List<Category> categorys;
    private String categoryType;




    @Override
    public void onCreate(@NonNull Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        new Common();
        Common.setBackArrow(true, activity);
        setHasOptionsMenu(true);
        imageTasks = new ArrayList<>();
//        mains = getMains();
        navController = Navigation.findNavController(activity, R.id.mainFragment);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {

        if(getUserId() <= 0){
            inflater.inflate(R.menu.appbar_ezlogin,menu);
        }else if(getUserId() > 0){
            setHasOptionsMenu(false);
            return;
        }

        //inflater.inflate(R.menu.appbar_ezlogout,menu);
        //navController.navigate(R.id.action_mainFragment_to_loginFragment);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.ezLogin:
                navController.navigate(R.id.action_mainFragment_to_loginFragment);
                return true;
            case android.R.id.home:
                navController.popBackStack();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }


    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        rvMain = view.findViewById(R.id.rvRes);
        rvMain.setLayoutManager(new LinearLayoutManager(activity));
        categorys = getCategorys();
        showCategorys(categorys);




        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                showCategorys(categorys);
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        // 取得使用者目前的登入狀態，並把使用者的ID設定到 Common.USER_ID 裡，以供其他功能識別使用
        UserAccount userAccount = Common.getUserLoin(activity);
    }




    private List<Category> getCategorys() {
        List<Category> categorys = null;
        if (Common.networkConnected(activity)) {
            String url = Common.URL_SERVER + "CategoryServlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "getAll");
            String jsonOut = jsonObject.toString();
            CategoryGetAllTask = new CommonTask(url, jsonOut);
            try {
                String jsonIn = CategoryGetAllTask.execute().get();
                Log.d("TAG_", "jsonIn: " + jsonIn);
                Type listType = new TypeToken<List<Category>>() {
                }.getType();
                categorys = new Gson().fromJson(jsonIn, listType);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        }else {
            Common.showToast(activity, R.string.textNoNetwork);
        }
//        Log.d("TAG_", "categorys.size: " + String.valueOf(categorys.size()));
        return categorys;
    }
//        mains.add(new Main(R.drawable.chinacate, "中式料理餐廳"));
//        mains.add(new Main(R.drawable.wecate, "西式料理餐廳"));
//        mains.add(new Main(R.drawable.japancate, "日式料理餐廳"));
//        mains.add(new Main(R.drawable.koreacate, "韓式料理餐廳"));
//        mains.add(new Main(R.drawable.cucate, "異式料理餐廳"));

    private void showCategorys(List<Category> categorys) {
        if(categorys == null || categorys.isEmpty()){
            Common.showToast(activity, R.string.textNoMainFound);
        }
        CategoryAdapter categoryAdapter = (CategoryAdapter) rvMain.getAdapter();
        if(categoryAdapter == null){
            rvMain.setAdapter(new CategoryAdapter(activity, categorys));
        }else{
            categoryAdapter.setCategorys(categorys);
            categoryAdapter.notifyDataSetChanged();
        }
    }
//    private void showMains(List<Main> mains) {
//        if (mains == null || mains.isEmpty()){
//            Common.showToast(activity, R.string.textNoMainFound);
//        }
//        MainAdapter mainAdapter = (MainAdapter) rvMain.getAdapter();
//        if(mainAdapter == null){
//            rvMain.setAdapter(new MainAdapter(activity, mains));
//        }else{
//            mainAdapter.setMains(mains);
//            mainAdapter.notifyDataSetChanged();
//        }
//    }


    private class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.MyViewHolder> {
        private LayoutInflater layoutInflater;
        private List<Category> categorys;
        private int imageSize;

//        Context context;
//        List<Main> mains;

        CategoryAdapter(Context context, List<Category> categorys) {
            layoutInflater = LayoutInflater.from(context);
            //this.context = context;
            this.categorys = categorys;
            imageSize = getResources().getDisplayMetrics().widthPixels /4;
        }
        void setCategorys(List<Category> categorys) {
            this.categorys = categorys;
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            ImageView imageView;
            TextView resCategoryInfo;

            MyViewHolder(View itemView) {
                super(itemView);
                imageView = itemView.findViewById(R.id.ibcate);
                resCategoryInfo = itemView.findViewById(R.id.resCategoryInfo);
            }
        }
        @Override
        public int getItemCount() {
            return categorys == null ? 0 : categorys.size();
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = layoutInflater.inflate(R.layout.item_view_main, parent, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder myviewHolder, int position) {
            final Category category = categorys.get(position);
            String url = Common.URL_SERVER + "CategoryServlet";
            int id = category.getId();
            ImageTask imageTask = new ImageTask(url, id, imageSize, myviewHolder.imageView);
            imageTask.execute();
            imageTasks.add(imageTask);
            //viewHolder.imageButton.setImageResource(main.getImageId());
            categoryType = category.getInfo();
            myviewHolder.resCategoryInfo.setText(category.getInfo());
            myviewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle bundle = new Bundle();
                    bundle.putInt("resId",id);
                    bundle.putSerializable("category", category);
//                    ImageButton imageButton = new ImageButton(context);
//                    imageButton.setImageResource(main.getImageId());
//                    Toast toast = new Toast(context);
//                    toast.setView(imageView);
//                    toast.setDuration(Toast.LENGTH_SHORT);
//                    toast.show();
                    bundle.putString("categoryType", categoryType);

                    Navigation.findNavController(view).navigate(R.id.action_mainFragment_to_chinaRestaurantFragment, bundle);
                }
            });

        }
    }
    private int getUserId() {
        return Common.USER_ID;
    }

    @Override
    public void onResume() {
        super.onResume();
        // 設定畫面到首頁一律不顯示返回鍵
        new Common().setBackArrow(false,activity);
        // 設定首頁AppBar(ActionBar)的Title(抬頭)
        activity.setTitle(R.string.home);

    }
    @Override
    public void onStop() {
        super.onStop();
        if (CategoryGetAllTask != null) {
            CategoryGetAllTask.cancel(true);
            CategoryGetAllTask = null;
        }

        if (imageTasks != null && imageTasks.size() > 0) {
            for (ImageTask imageTask : imageTasks) {
                imageTask.cancel(true);
            }
            imageTasks.clear();
        }

        if (CategoryDeleteTask != null) {
            CategoryDeleteTask.cancel(true);
            CategoryDeleteTask = null;
        }
    }


}



