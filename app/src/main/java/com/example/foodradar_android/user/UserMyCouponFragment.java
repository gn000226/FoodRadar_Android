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
import com.example.foodradar_android.coupon.Coupon;
import com.example.foodradar_android.res.Res;
import com.example.foodradar_android.task.CommonTask;
import com.example.foodradar_android.task.ImageTask;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


public class UserMyCouponFragment extends Fragment {
    private Activity activity;
    private NavController navController;
    private List<MyCoupon> myCouponList = new ArrayList<>();
    private List<ImageTask> imageTasks= new ArrayList<>();
    private RecyclerView rcvMyCoupon;
    private Integer imageSize;
    private String URL_SERVER = "http://10.0.2.2:8080/FoodRadar_Web/";
    private String MYCOUPON_SERVLET = URL_SERVER + "MyCouponServlet";
    final private String TAG = "UserMyCouponFragment";




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();

        // 顯示左上角的返回箭頭
        Common.setBackArrow(true, activity);
        setHasOptionsMenu(true);

        navController =
                Navigation.findNavController(activity, R.id.mainFragment);
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity.setTitle(R.string.title_of_mycoupon);
        return inflater.inflate(R.layout.fragment_user_my_coupon, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rcvMyCoupon = view.findViewById(R.id.rcvMyCoupon);
        rcvMyCoupon.setLayoutManager(new LinearLayoutManager(activity));
        //        userId = 3; // 取得user登入的 帳號/userId
        myCouponList = getmyCoupon();
        showmyCoupon(myCouponList);
    }
    private int getUserId(){
        return Common.USER_ID;
    }

    private class MyCouponAdapter extends  RecyclerView.Adapter<UserMyCouponFragment.MyCouponAdapter.MyViewHolder>{
        private LayoutInflater layoutInflater;
        private List<MyCoupon> myCouponListAdpt;
        private int imageSize;
        MyCouponAdapter(Context context, List<MyCoupon> myCouponListAdpt) {
//            Log.d(TAG, "MyCouponAdapter: " + myCouponListAdpt.get(0).getCouPonId());
            layoutInflater = LayoutInflater.from(context);
            this.myCouponListAdpt = myCouponListAdpt;
            /* 螢幕寬度除以4當作將圖的尺寸 */
            imageSize = getResources().getDisplayMetrics().widthPixels / 2;
        }

        public LayoutInflater getLayoutInflater() {
            return layoutInflater;
        }

        public void setLayoutInflater(LayoutInflater layoutInflater) {
            this.layoutInflater = layoutInflater;
        }

        public void setMyCouponListAdpt(List<MyCoupon> myCouponListSetAdpt){
            this.myCouponListAdpt = myCouponListSetAdpt;
            //Log.d(TAG, "setMyCouponListAdpt: " + myCouponListAdpt.get(0).getCouPonId());
        }

        class MyViewHolder extends  RecyclerView.ViewHolder {
            ImageView myCouponPhoto;
            TextView myCouponInfo;

            MyViewHolder(@NonNull View itemView) {
                super(itemView);
                myCouponPhoto = itemView.findViewById(R.id.myCouponPhoto);
                myCouponInfo = itemView.findViewById(R.id.myCouponInfo);
            }
        }
        @NonNull
        @Override
        public MyCouponAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemViewMyCoupon = layoutInflater
                    .inflate(R.layout.item_view_user_mycoupon,parent,false);
            return new UserMyCouponFragment.MyCouponAdapter.MyViewHolder(itemViewMyCoupon);
        }

        @Override
        public void onBindViewHolder(@NonNull MyCouponAdapter.MyViewHolder holder, int position) {
            final MyCoupon myCouponBidVH = myCouponListAdpt.get(position);
            holder.myCouponInfo.setText(myCouponBidVH.getCouPonInfo());

            //int id = myCouponBidVH.getMyCouponId();
            Integer couPonId = myCouponBidVH.getCouPonId();
           // Log.d("TAG", "couPonId: " + couPonId);
            ImageTask imageTask = new ImageTask(MYCOUPON_SERVLET, couPonId, imageSize, holder.myCouponPhoto);
            imageTask.execute();
            imageTasks.add(imageTask);


//            List<Coupon> coupon = new ArrayList<>();
//            Integer couponID = myCouponBidVH.getCouPonId();
//            CommonTask getCouponTask;
//            if (Common.networkConnected(activity)) {
//                String url = MYCOUPON_SERVLET;
//                Log.d(TAG,"getCouponById.url: " + url);
//                JsonObject jsonObject = new JsonObject();
//                jsonObject.addProperty("action", "getAllById");
//                jsonObject.addProperty("id",couponID);
//                jsonObject.addProperty("userId",Common.USER_ID);
//                String jsonOut = jsonObject.toString();
//                Log.d(TAG,"getCouponById.jsonOut: " + jsonOut);
//                getCouponTask = new CommonTask(url, jsonOut);
//                try {
//                    String jsonIn = getCouponTask.execute().get();
//                    Log.d(TAG,"getCouponById.jsonIn: " + jsonIn);
//                    Type listType = new TypeToken<List<Coupon>>() {
//                    }.getType();
//                    coupon = new Gson().fromJson(jsonIn, listType);
//                } catch (Exception e) {
//                    Log.e(TAG, e.toString());
//                }
//            } else {
//                Common.showToast(activity, R.string.textNoNetwork);
//            }
//            Log.d(TAG,"coupon.getTvCouInfo(): " + coupon.get(couponID));
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                        Bundle bundle = new Bundle();
                        bundle.putSerializable("myCouponBidVH", myCouponBidVH);
                        Navigation.findNavController(v)
                                .navigate(R.id.action_userMyCouponFragment_to_couponDetailFragment, bundle);

                        //Common.showToast(activity, R.string.textNoRessFound);


                }
            });

        }

        @Override
        public int getItemCount() {
            if (myCouponListAdpt == null){
                return 0;
            }else {
                return myCouponListAdpt.size();
            }
        }
    }
    private List<MyCoupon> getmyCoupon() {
        List<MyCoupon> getmyCouponList = null;
        if (Common.networkConnected(activity)){
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "getAllById");
            jsonObject.addProperty("id", getUserId());
            String jsonOut = jsonObject.toString();
            CommonTask getAllTask;
            getAllTask = new CommonTask(MYCOUPON_SERVLET, jsonOut);
            try {
                String jsonIn = getAllTask.execute().get();
                Type listType = new TypeToken<List<MyCoupon>>() {
                }.getType();
                getmyCouponList = new Gson().fromJson(jsonIn, listType);
                Log.d(TAG,"getMyCouponList: " + getmyCouponList);

            }catch (Exception e){
                e.printStackTrace();
            }
        }else{
            Common.showToast(activity,"NetworkConnected: fail");
        }
        //getmyCouponList.get(0).getCouPonId();
       // Log.d(TAG, "couPonIdList: " + getmyCouponList.get(0).getCouPonId());
        return getmyCouponList;
    }

    private void showmyCoupon(List<MyCoupon> showmyCouponList) {
        //Log.d(TAG, "couPonIdListshow: " + showmyCouponList.get(0).getCouPonId());
        MyCouponAdapter myCouponAdapter = (MyCouponAdapter) rcvMyCoupon.getAdapter();
        if (myCouponAdapter == null){
            rcvMyCoupon.setAdapter(new MyCouponAdapter(activity, showmyCouponList));
        }else{
            myCouponAdapter.setMyCouponListAdpt(showmyCouponList);
            myCouponAdapter.notifyDataSetChanged();
        }
    }
}