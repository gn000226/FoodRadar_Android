package com.example.foodradar_android.user;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import android.widget.SearchView;
import android.widget.TextView;

import com.example.foodradar_android.Common;
import com.example.foodradar_android.R;
import com.example.foodradar_android.res.Res;
import com.example.foodradar_android.task.CommonTask;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class UserMyResFragment extends Fragment {
    private Activity activity;
    private SwipeRefreshLayout swipeRefreshLayout;
    private NavController navController;
    private List<MyRes> myResList = new ArrayList<>();
    private List<UserImageTask> imageTasks= new ArrayList<>();
    private RecyclerView rcvMyRes;
//    private Integer userId;
    private String URL_SERVER = "http://10.0.2.2:8080/FoodRadar_Web/";
    private String MYRES_SERVLET = URL_SERVER + "MyResServlet";
    private Integer imageSize = 400;
    final private String TAG = "UserMyResFragment";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();

        // 顯示左上角的返回箭頭
        Common.setBackArrow(true, activity);
        setHasOptionsMenu(true);

        navController =
                Navigation.findNavController(activity, R.id.mainFragment);
    }

    // 顯示右上角的OptionMenu選單
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity.setTitle(R.string.title_of_myres);
        return inflater.inflate(R.layout.fragment_user_my_res, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rcvMyRes = view.findViewById(R.id.id_rcvMyRes);
        rcvMyRes.setLayoutManager(new LinearLayoutManager(activity));

        SearchView searchViewMyRes = view.findViewById(R.id.searchViewMyRes);
        searchViewMyRes.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) { return false; }
            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.trim().isEmpty()) {
                    showMyRes(myResList);
                } else {
                    List<MyRes> searchMyResList = new ArrayList<>();
                    for(MyRes myRes : myResList){
                        if(myRes.getResName().toUpperCase().contains(newText.toUpperCase())){
                            searchMyResList.add(myRes);
                        }
                    }
                    showMyRes(searchMyResList);
                }
                return true;
            }
        });

        myResList = getMyRes();
        showMyRes(myResList);

        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayoutMyRes);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                showMyRes(myResList);
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private int getUserId(){
        return Common.USER_ID;
    }

    private class MyResAdapter extends  RecyclerView.Adapter<MyResAdapter.MyViewHolder>{
        private LayoutInflater layoutInflater;
        private List<MyRes> myResListAdpt;
        private int imgSize;

        MyResAdapter(Context context, List<MyRes> myResMRAList) {
            layoutInflater = LayoutInflater.from(context);
            this.myResListAdpt = myResMRAList;
            /* 螢幕寬度除以4當作將圖的尺寸 */
            imgSize = getResources().getDisplayMetrics().widthPixels / 4;
        }

        public void setMyResListAdpt(List<MyRes> myResListSetAdpt){
            this.myResListAdpt = myResListSetAdpt;
        }

        class MyViewHolder extends  RecyclerView.ViewHolder{
            ImageView imResImg;
            TextView tvResName, tvResHours, tvResTel, tvResAddress;
            MyViewHolder(@NonNull View itemView) {
                super(itemView);
                imResImg = itemView.findViewById(R.id.imResImg);
                tvResName = itemView.findViewById(R.id.tvResName);
                tvResHours = itemView.findViewById(R.id.tvResHours);
                tvResTel = itemView.findViewById(R.id.tvResTel);
                tvResAddress = itemView.findViewById(R.id.tvResAddress);
            }
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemViewMyRes = layoutInflater
                    .inflate(R.layout.item_view_user_myres,parent,false);
            return new MyViewHolder(itemViewMyRes);
        }

        @Override
        public void onBindViewHolder(@NonNull MyResAdapter.MyViewHolder holder, int position) {
            MyRes myResBidVH = myResListAdpt.get(position);
            holder.tvResName.setText(myResBidVH.getResName());
            holder.tvResHours.setText(getResources().getString(R.string.textResHours) + ": " + resHour(myResBidVH.getResHours()));
            holder.tvResTel.setText(getResources().getString(R.string.textResTel) + ": " + myResBidVH.getResTel());
            holder.tvResAddress.setText(getResources().getString(R.string.textResAddress) + ": " + myResBidVH.getResAddress());

            Integer resId = myResBidVH.getResId(); // 餐廳ID
//            final MyRes bookOnBVH = books.get(position);
            Log.d(TAG,"resId: " + resId);
            Log.d(TAG,"MYRES_SERVLET: " + MYRES_SERVLET);
            UserImageTask userImageTask = new UserImageTask(MYRES_SERVLET, resId, imageSize, holder.imResImg);
            userImageTask.execute(); // .execute() => UserImage.doInBackground
            imageTasks.add(userImageTask);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Integer resID = myResBidVH.getResId();
//                    Common.showToast(activity,"餐廳ID: " + resID);
                    List<Res> res = new ArrayList<>();
                    CommonTask getResTask;
                    Log.d(TAG,"getResById: " + resID);
                    if (Common.networkConnected(activity)) {
                        String url = MYRES_SERVLET;
                        Log.d(TAG,"getResById.url: " + url);
                        JsonObject jsonObject = new JsonObject();
                        jsonObject.addProperty("action", "getResById");
                        jsonObject.addProperty("id",resID);
                        jsonObject.addProperty("userId",Common.USER_ID);
                        String jsonOut = jsonObject.toString();
                        Log.d(TAG,"getResById.jsonOut: " + jsonOut);
                        getResTask = new CommonTask(url, jsonOut);
                        try {
                            String jsonIn = getResTask.execute().get();
                            Log.d(TAG,"getResById.jsonIn: " + jsonIn);
                            Type listType = new TypeToken<List<Res>>() {
                            }.getType();
                            res = new Gson().fromJson(jsonIn, listType);
                        } catch (Exception e) {
                            Log.e(TAG, e.toString());
                        }
                    } else {
                        Common.showToast(activity, R.string.textNoNetwork);
                    }

                    if (res != null) {
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("res", res.get(0));
                        Navigation.findNavController(v)
                                .navigate(R.id.action_userMyResFragment_to_resDetailFragment, bundle);
                    } else {
                        Common.showToast(activity, R.string.textNoRessFound);
                    }
                }
            });
        }

        @Override
        public int getItemCount()
        {
            if (myResListAdpt == null) {
                return 0;
            }else {
                return myResListAdpt.size();
            }
        }

    }

    private List<MyRes> getMyRes(){
        List<MyRes> getMyResList = null;

        if (Common.networkConnected(activity)) {
//            byte[] imgBytes = new byte[1];
//            MyRes myRes = new MyRes(1,"resName","resHours","resTel","resAddress", imgBytes);
//            getMyResList = new ArrayList<>();
//            getMyResList.add(0,myRes);
//            getMyResList.add(1,new MyRes(2,"resName2","resHours","resTel","resAddress", imgBytes));
//            for (int i = 2 ; i <= 13 ; i++){
//                getMyResList.add(i,new MyRes(i,"resName" + String.valueOf(i),"resHours","resTel","resAddress", imgBytes));
//            }

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "getAllById");
            jsonObject.addProperty("id", getUserId());
            String jsonOut = jsonObject.toString();

//            Log.d(TAG,"getBooks.Common.URL_SERVLET: " + Common.URL_SERVLET);
            CommonTask getAllTask;
            getAllTask = new CommonTask(MYRES_SERVLET, jsonOut);
            try {
                String jsonIn = getAllTask.execute().get();
                Type listType = new TypeToken<List<MyRes>>() {
                }.getType();
                getMyResList = new Gson().fromJson(jsonIn, listType);
                Log.d(TAG,"getMyResList: " + getMyResList);
            } catch (Exception e) {
//                Log.e(TAG, e.toString());
                e.printStackTrace();
            }

        }else{
            Common.showToast(activity,"NetworkConnected: fail");
        }

        return getMyResList;
    }

    private void showMyRes(List<MyRes> showMyResList){
        MyResAdapter myResAdapter = (MyResAdapter) rcvMyRes.getAdapter();
        Log.d(TAG,"myResAdapter: " + myResAdapter);
        if (myResAdapter == null){
            Log.d(TAG,"showMyResList: " + showMyResList);
            rcvMyRes.setAdapter(new MyResAdapter(activity, showMyResList));
        }else{
            Log.d(TAG,"showMyResList: " + showMyResList);
            myResAdapter.setMyResListAdpt(showMyResList);
            myResAdapter.notifyDataSetChanged();
        }

    }

    public String resHour(String hourJson) {
        JsonObject jsonParser = new JsonObject();
        jsonParser = JsonParser.parseString(hourJson).getAsJsonObject();

        //        List<ResHour> resHourList = new ArrayList<ResHour>();
        List<String> resHourList = new ArrayList<String>();
        for (Map.Entry<String, JsonElement> e : jsonParser.entrySet()) {
//            resHourList.add(new ResHour(e.getKey(), e.getValue()));
            resHourList.add(e.getKey());
//            Log.d("TAG","e.getKey(): " + e.getKey() + ", value:" + e.getValue());
        }

//        Log.d("TAG" ,new SimpleDateFormat("yyyy-MM-dd EEE hh:mm:ss").format(new Date()));
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, 0);
        // 獲得當前日期是一個星期的第幾天
        int weekOfDate = cal.get(Calendar.DAY_OF_WEEK);
        // Calendar星期日是回傳1，所以直接對應到json的7(星期日)，其餘的星期則減1後對應json
        if (weekOfDate == 1) { weekOfDate = 7; } else { weekOfDate = weekOfDate - 1; }
        String week1 = String.valueOf(weekOfDate) + "1";
        String week2 = String.valueOf(weekOfDate) + "2";
        String week3 = String.valueOf(weekOfDate) + "3";
//        Log.d("TAG","Time: " + cal.getTime() + ", weekOfDate: " + String.valueOf(weekOfDate));

        JsonObject jsonHours = JsonParser.parseString(hourJson).getAsJsonObject();

        String resHour = "";
        if (resHourList.indexOf(week1) >= 0) { resHour = jsonHours.get(week1).getAsString(); }
        if (resHourList.indexOf(week2) >= 0) { resHour += resHour==""?"":", " + jsonHours.get(week2).getAsString(); }
        if (resHourList.indexOf(week3) >= 0) { resHour += resHour==""?"":", " + jsonHours.get(week3).getAsString(); }

        if (resHour.equals("")) { resHour = "本日公休"; }
        return resHour;
    }
//    class ResHour {
//        String openWeek;
//        JsonElement openTime;
//        public ResHour(String openWeek, JsonElement openTime) {
//            this.openWeek = openWeek;
//            this.openTime = openTime;
//        }
//        public String getOpenWeek() { return openWeek; }
//        public JsonElement getOpenTime() { return openTime; }
//    }

}