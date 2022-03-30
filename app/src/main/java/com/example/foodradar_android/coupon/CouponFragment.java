package com.example.foodradar_android.coupon;

import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.view.menu.MenuView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.foodradar_android.Common;
import com.example.foodradar_android.R;
import com.example.foodradar_android.task.CommonTask;
import com.example.foodradar_android.user.MyCoupon;
import com.example.foodradar_android.user.UserAccount;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.example.foodradar_android.task.ImageTask;

public class CouponFragment extends Fragment {
    private static final String TAG = "TAG_CouponFragment";
    private SwipeRefreshLayout swipeRefreshLayout;
    private SwipeRefreshLayout swRf;
    private RecyclerView rvCoupon;
    private RecyclerView rvSample;
    private FragmentActivity activity;
    private NavController navController;
    private TextView tvCouInfo, couPonStartDate, couPonEndDate, btUsCoupon;
    private ImageView imageAlert, imageAlert2, ivNoUse;
    private ImageButton ibUseCard;
    private Timestamp Date;
    private boolean couPonType;
    private boolean couPonEnable;
    private CommonTask couponGetAllTask;
    private CommonTask couponDeleteTask;
    private List<ImageTask> imageTasks;
    private List<Coupon> coupons = new ArrayList<>();
    private List<Coupon> couacts = new ArrayList<>();
    private int userIdBox = Common.USER_ID;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        imageTasks = new ArrayList<>();
        setHasOptionsMenu(true);
        navController = Navigation.findNavController(activity, R.id.mainFragment);
        //new Common().setBackArrow(false,activity);
    }

    private void showDialog(int imageId, int imageId2) {
        final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();

        new Common().setBackArrow(true, activity);
        alertDialog.show();
        Window window = alertDialog.getWindow();
        window.setGravity(Gravity.BOTTOM);
        window.setWindowAnimations(R.style.popupAnimation);
        View view = View.inflate(activity, R.layout.alert_dialog_view, null);
        imageAlert = view.findViewById(R.id.imageAlert);
        imageAlert.setImageResource(imageId);
        imageAlert2 = view.findViewById(R.id.imageAlert2);
        imageAlert2.setImageResource(imageId2);
        ibUseCard = view.findViewById(R.id.ibUseCard);
        ibUseCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
//        alertView = view.findViewById(R.id.tvUseInfo1);
//        alertView.setText(tvUseInfo1);
//        alertView2 = view.findViewById(R.id.tvUseInfo2);
//        alertView2.setText(tvUseInfo2);

        window.setContentView(view);
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

        alertDialog.setCanceledOnTouchOutside(true);
        alertDialog.setCancelable(true);

    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        //navController.navigate(R.id.action_couponFragment_to_fcmFragment);
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.appbar_ezfcm, menu);


    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.ezFcm:
                showDialog(R.drawable.couponconfig, R.drawable.use_image);
                return true;
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
        activity.setTitle(R.string.coupon);
        return inflater.inflate(R.layout.fragment_coupon, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        rvSample = view.findViewById(R.id.rvSample);
        rvCoupon = view.findViewById(R.id.rvCoupon);
//        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        swRf = view.findViewById(R.id.swRf);
        rvSample.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL));

        rvCoupon.setLayoutManager(new LinearLayoutManager(activity));

//        setCouactsGroup();
//        showCouacts(couacts);
//
//        setCouponsGroup();
//        showCoupons(coupons);

        couacts = getCouacts();
        showCouacts(couacts);
        coupons = getCoupons();
        showCoupons(coupons);

        swRf.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swRf.setRefreshing(true);
//                setCouactsGroup();
//                setCouponsGroup();
                showCouacts(couacts);
                showCoupons(coupons);
                swRf.setRefreshing(false);
            }
        });
//        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//
//            @Override
//            public void onRefresh() {
//                swipeRefreshLayout.setRefreshing(true);
//                showCoupons(coupons);
//                swipeRefreshLayout.setRefreshing(false);
//
//            }
//        });

    }
    //    <<---===============================以下是上面的rvsample.recyclerview======================--->>

    private List<Coupon> getCoupons() {
        List<Coupon> coupons = null;
        if (Common.networkConnected(activity)) {
            String url = Common.URL_SERVER + "CouponServlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "getAllcouPonType");
            jsonObject.addProperty("userId", Common.USER_ID);
            jsonObject.addProperty("couPonType", true);
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
            Common.showToast(activity, R.string.textNoFindCoupon);
        }
        CouponAdapter couponAdapter = (CouponAdapter) rvSample.getAdapter();

        if (couponAdapter == null) {
            rvSample.setAdapter(new CouponAdapter(activity, coupons));
        } else {
            couponAdapter.setCoupons(coupons);
            couponAdapter.notifyDataSetChanged();
        }
    }

    private class CouponAdapter extends RecyclerView.Adapter<CouponAdapter.MyViewHolder> {
        private LayoutInflater layoutInflater;
        private List<Coupon> coupons;
        private int imageSize;


        CouponAdapter(Context context, List<Coupon> coupons) {
            layoutInflater = LayoutInflater.from(context);
            this.coupons = coupons;
            imageSize = getResources().getDisplayMetrics().widthPixels / 4;
        }


        public void setCoupons(List<Coupon> coupons) {
            this.coupons = coupons;
        }


        class MyViewHolder extends RecyclerView.ViewHolder {
            ImageView imageView;
            TextView upcouPonInfo, couPonStartDateUp, couPonEndDateUp;
            //Button btUsCoupon;

            public MyViewHolder(@NonNull View itemView) {
                super(itemView);
                imageView = itemView.findViewById(R.id.ivDemoUp);
                upcouPonInfo = itemView.findViewById(R.id.upCouInfo);
                couPonStartDateUp = itemView.findViewById(R.id.couPonStartDateUp);
                //couPonEndDateUp = itemView.findViewById(R.id.couPonEndDateUp);
                //btUsCoupon = itemView.findViewById(R.id.btUsCoupon);

            }
        }

        @Override
        public int getItemCount() {
            return coupons == null ? 0 : coupons.size();
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = layoutInflater.inflate(R.layout.item_view_coupon, parent, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull CouponAdapter.MyViewHolder myViewHolder, int position) {
            //UserAccount userAccount = new Common().getUserLoin(activity);
            //Common.USER_ID = userAccount.getUserId();
            //Common.showToast(activity,"TAG_ UserAreaFragment.USER_ID: " + String.valueOf(getUserId()));

            final Coupon coupon = coupons.get(position);
            String url = Common.URL_SERVER + "CouponServlet";
            int id = coupon.getCouPonId();
            ImageTask imageTask = new ImageTask(url, id, imageSize, myViewHolder.imageView);
            imageTask.execute();
            imageTasks.add(imageTask);
//            myViewHolder.tvCouName.setText(coupon.getResName());
            // Log.d(TAG, "resName" + coupon);
            myViewHolder.upcouPonInfo.setText(coupon.getCouPonInfo());
            myViewHolder.couPonStartDateUp.setText(coupon.getCouPonStartDate() + "~" + coupon.getCouPonEndDate());
            //myViewHolder.couPonEndDateUp.setText(coupon.getCouPonEndDate());
            myViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

//                    Bundle bundle = new Bundle();
//                    bundle.putSerializable("coupon", coupon);
//                    Navigation.findNavController(view)
//                            .navigate(R.id.action_couponFragment_to_couponDetailFragment, bundle);
                }
            });
        }


    }
//<<--============分隔線以下是rvcoupon下方的recyclerview=============================-->>

    private List<Coupon> getCouacts() {
        List<Coupon> couacts = null;
        if (Common.networkConnected(activity)) {
            String url = Common.URL_SERVER + "CouponServlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "getAllcouPonType");
            jsonObject.addProperty("userId", Common.USER_ID);
            jsonObject.addProperty("couPonType", false);
            String jsonOut = jsonObject.toString();
            couponGetAllTask = new CommonTask(url, jsonOut);
            try {
                String jsonIn = couponGetAllTask.execute().get();
                Type listType = new TypeToken<List<Coupon>>() {
                }.getType();
                couacts = new Gson().fromJson(jsonIn, listType);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        } else {
            Common.showToast(activity, R.string.textNoNetwork);
        }
        return couacts;
    }

    private void showCouacts(List<Coupon> couacts) {
        if (couacts == null || couacts.isEmpty()) {
            Common.showToast(activity, R.string.textNoFindCoupon);
        }
        CouactAdapter couactAdapter = (CouactAdapter) rvCoupon.getAdapter();

        if (couactAdapter == null) {
            rvCoupon.setAdapter(new CouactAdapter(activity, couacts));
        } else {
            couactAdapter.setCouacts(couacts);
            couactAdapter.notifyDataSetChanged();
        }
    }

    private class CouactAdapter extends RecyclerView.Adapter<CouactAdapter.MyViewHolder> {
        private LayoutInflater layoutInflater;
        private List<Coupon> couacts;
        private int imageSize;

        public CouactAdapter(Context context, List<Coupon> couacts) {
            layoutInflater = LayoutInflater.from(context);
            this.couacts = couacts;
            imageSize = getResources().getDisplayMetrics().widthPixels / 4;
        }

        public void setCouacts(List<Coupon> couacts) {
            this.couacts = couacts;
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            ImageView imageView;
            TextView couPonInfo, couPonStartDate, couPonEndDate, btUsCoupon, UseCoupon;


            public MyViewHolder(@NonNull View itemView) {
                super(itemView);
                imageView = itemView.findViewById(R.id.ivDemo);
                couPonInfo = itemView.findViewById(R.id.tvCouInfo);
                couPonStartDate = itemView.findViewById(R.id.couPonStartDate);
                //couPonEndDate = itemView.findViewById(R.id.couPonEndDate);
                btUsCoupon = itemView.findViewById(R.id.btUsCoupon);
                UseCoupon = itemView.findViewById(R.id.UseCoupon);

            }
        }

        @NonNull
        @Override
        public CouactAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = layoutInflater.inflate(R.layout.item_view_couact, parent, false);
            return new CouactAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            final Coupon couact = couacts.get(position);
            String url = Common.URL_SERVER + "CouponServlet";
            int id = couact.getCouPonId();
            Log.d(TAG, "couPonId: " + couact.getCouPonId());
            ImageTask imageTask = new ImageTask(url, id, imageSize, holder.imageView);
            imageTask.execute();
            imageTasks.add(imageTask);

            //boolean couPonEnable = couact.isCouPonEnable();
            boolean CouPonLoveStatus = couact.isCouPonLoveStatus();
            Log.d(TAG, "DSADFDASFASF:::" + CouPonLoveStatus);
            TextView UseCoupon = holder.UseCoupon;
            TextView btCoupon = holder.btUsCoupon;
            String couPonLoveStatus = getResources().getString(R.string.textAccountStatus) + ":"
                    + (couact.getId());
            if (userIdBox <= 0) {    //0 > 訪客，一律設為看不見btUsCoupon
                couact.setCouPonLoveStatus(false);
                btCoupon.setVisibility(View.INVISIBLE);
                UseCoupon.setVisibility(View.INVISIBLE);
                holder.couPonInfo.setText(couact.getCouPonInfo());
                holder.couPonStartDate.setText(couact.getCouPonStartDate() + "~" + couact.getCouPonEndDate());
            } else {//否則就判斷是否有收藏
                if (couact.isCouPonLoveStatus()) {
                    couact.setCouPonLoveStatus(true);
                    btCoupon.setVisibility(View.INVISIBLE);
                    UseCoupon.setVisibility(View.VISIBLE);
                    holder.couPonInfo.setText(couact.getCouPonInfo());
                    holder.couPonStartDate.setText(couact.getCouPonStartDate() + "~" + couact.getCouPonEndDate());
                } else {
                    couact.setCouPonLoveStatus(false);
                    holder.couPonInfo.setText(couact.getCouPonInfo());
                    holder.couPonStartDate.setText(couact.getCouPonStartDate() + "~" + couact.getCouPonEndDate());
                }
            }
//<<--======================分隔線以下是收藏優惠券點擊事件================================================================-->>
            holder.btUsCoupon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (userIdBox != 0) {
                        if (!couact.isCouPonLoveStatus()) {
                            if (Common.networkConnected(activity)) {
                                String insertLoveUrl = Common.URL_SERVER + "CouponServlet";
                                int loveCouponId = couact.getCouPonId();
                                //Log.d(TAG, "couPonId: " + coupon.getId());
                                JsonObject jsonObject = new JsonObject();
                                jsonObject.addProperty("action", "couponLoveInsert");
                                jsonObject.addProperty("loginUserId", userIdBox);
                                jsonObject.addProperty("couPonId", loveCouponId);
                                jsonObject.addProperty("couPonIsUsed", 0);
                                int count = 0;
                                try {
                                    String result = new CommonTask(insertLoveUrl, jsonObject.toString()).execute().get();
                                    count = Integer.parseInt(result);
                                } catch (Exception e) {
                                    Log.e(TAG, e.toString());
                                }
                                if (count == 0) {
                                    Common.showToast(activity, "收藏失敗");
                                } else {
                                    Common.showToast(activity, "收藏成功");
                                    btCoupon.setVisibility(View.INVISIBLE);
                                    UseCoupon.setVisibility(View.VISIBLE);
                                    //couact.setCouPonLoveStatus(false);
                                    couact.setCouPonLoveStatus(true);
                                }
                            } else {
                                Common.showToast(activity, "取得連線失敗");
                            }
                        }
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return couacts == null ? 0 : couacts.size();
        }

        private int getUserId() {
            return Common.USER_ID;
        }
//        protected RecyclerView.LayoutManager initLayoutManger(){
//            LinearLayoutManager manager = new LinearLayoutManager(getContext());
//            manager.setOrientation(LinearLayoutManager.VERTICAL);
//            return manager;
//        }
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        setCouactsGroup();
//        setCouponsGroup();
//        showCouacts(couacts);
//        showCoupons(coupons);
//
//    }

//    private void setCouactsGroup(){
//        List<Coupon> newcouacts = getCouacts();
//        couacts.clear();//畫面出來之前先把原本的東西清掉
//        if (newcouacts != null){
//            for (Coupon coupon : newcouacts){
//                if (coupon.isCouPonType() == false){
//                    couacts.add(coupon);
//                }
//            }
//        }
//    }
//
//    private void setCouponsGroup(){
//        List<Coupon> newcouacts = getCoupons();
//        coupons.clear();//畫面出來之前先把原本的東西清掉
//        if (newcouacts != null){
//            for (Coupon coupon : newcouacts){
//                if (coupon.isCouPonType() == true){
//                    coupons.add(coupon);
//                }
//            }
//        }
//    }

//    private void schedule(TimerTask task, long delay, long period){
//        final int[] position = {0};
//        Timer timer = new Timer();
//        TimerTask timerTask = new TimerTask() {
//            @Override
//            public void run() {
//                if (position[0] == coupons.size()){
//                    position[0] = 0;
//                    position[0]++;
//                }else{
//                    position[0]++;
//                }
//                rvSample.getLayoutManager().scrollToPosition(position[0]);
//                timer.scheduleAtFixedRate(task, 1000, 3000);
//                //timer.schedule(task, 1000, 3000);
//
//            }
//
//        };

        //timer.schedule(timerTask, 1000, 3000);
//          List<Coupon> coupon = new ArrayList<>();
//          coupons = getCoupons();
//          int index = coupon.indexOf(coupons);
//          rvSample.smoothScrollToPosition(index);

    }

}