package com.example.foodradar_android.coupon;

import android.app.Dialog;
import android.content.DialogInterface;
import android.icu.text.Transliterator;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.view.menu.ActionMenuItemView;
import androidx.appcompat.view.menu.MenuView;
import androidx.appcompat.widget.AlertDialogLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.transition.TransitionValues;
import android.transition.Visibility;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowAnimationFrameStats;
import android.view.WindowManager;
import android.view.animation.AnimationSet;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.foodradar_android.Common;
import com.example.foodradar_android.R;
import com.example.foodradar_android.task.CommonTask;
import com.example.foodradar_android.task.ImageTask;
import com.example.foodradar_android.user.MyCoupon;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.List;

import dagger.BindsOptionalOf;


public class CouponDetailFragment extends Fragment {
    private final static String TAG = "TAG_CouponDetailFragment";
    private NavController navController;
    private FragmentActivity activity;
    private List<ImageTask> imageTasks;
    private ImageTask imageTask;
    private int imageSize;
    private TextView couPonEndDate, couPonStartDate;
    private ImageView ivCoupon, ivNoUse;
    private CommonTask couponGetAllTask;
    private CommonTask couponDeleteTask;
    private String URL_SERVER = "http://10.0.2.2:8080/FoodRadar_Web/";
    private String MYCOUPON_SERVLET = URL_SERVER + "MyCouponServlet";
    private boolean couponIsUsedBl;
    private MyCoupon mycoupon;
    private Coupon coupon;
    private int userIdBox = Common.USER_ID;
    private byte[] image;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        navController = Navigation.findNavController(activity, R.id.mainFragment);
        //coupon = (Coupon) (getArguments() != null ? getArguments().getSerializable("coupon") : null);
        new Common().setBackArrow(true, activity);
        setHasOptionsMenu(true);
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

        return inflater.inflate(R.layout.fragment_coupon_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final NavController navController = Navigation.findNavController(view);
        Bundle bundle = getArguments();
        //MyCoupon myCouponBidVH = myCouponListAdpt.get(position);
        MyCoupon myCouponBidVH =(MyCoupon) bundle.getSerializable("myCouponBidVH");
        ivCoupon = view.findViewById(R.id.ivDemo);

        //ivCoupon.setImageResource(R.drawable.no_image);
        couPonStartDate = view.findViewById(R.id.couPonStartDate);
       // couPonEndDate = view.findViewById(R.id.couPonEndDate);

        ivNoUse = view.findViewById(R.id.ivNoUse);
        String url = Common.URL_SERVER + "CouponServlet";
        int id = myCouponBidVH.getCouPonId();
        //int id = coupon.getCouPonId();
        imageSize = getResources().getDisplayMetrics().widthPixels;
        ImageTask imageTask = new ImageTask(url, id, imageSize, ivCoupon);
        imageTask.execute();


        FloatingActionButton btUse = view.findViewById(R.id.btUse);
        btUse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                if (getUserId() > 0){
//                    setIvNoUse(ivNoUse, true, 1);
//                    ivNoUse.setImageResource(R.drawable.use_image);
//                }else if (getUserId() <= 0){
//                    navController.popBackStack();
//                }
                if (userIdBox != 0){
                    if (Common.networkConnected(activity)){
                        int id = myCouponBidVH.getCouPonId();
                        JsonObject jsonObject = new JsonObject();
                        jsonObject.addProperty("action", "isUsedUpdate");
                        jsonObject.addProperty("couPonId", id);
                        jsonObject.addProperty("userId", userIdBox);
                        jsonObject.addProperty("couPonIsUsed", couponIsUsedBl);
                        int count = 0;
                        try {
                            String result = new CommonTask(MYCOUPON_SERVLET, jsonObject.toString()).execute().get(); // Insert可等待回應確認是否新增成功
                            count = Integer.parseInt(result);
                        } catch (Exception e) {
                            //Log.e(TAG, e.toString());
                        }

                        if (count == 0) {
                            Common.showToast(activity, R.string.textUpdateUseLose);
                        } else {

                            setIvNoUse(ivNoUse, true, 1);
                            ivNoUse.setImageResource(R.drawable.use_image);
                            ivNoUse.setVisibility(View.VISIBLE);
                            Common.showToast(activity, R.string.textUpdateUseOk);
                        }
                    }
                }



            }
        });


    }




    public CouponDetailFragment() {
        // Required empty public constructor
    }

    private int getUserId(){
        return Common.USER_ID;
    }

    private void setIvNoUse(ImageView ivNoUse, Boolean couponIsUsedBl, Integer value){
//        ivNoUse.setVisibility(View.INVISIBLE);
        switch (value){
            case 0:
                value.equals(false);
                couponIsUsedBl = false;
                ivNoUse.setVisibility(View.INVISIBLE);
            case 1:
                value.equals(true);
                couponIsUsedBl = true;
                ivNoUse.setVisibility(View.VISIBLE);
        }

//      if (value == 0){
//          couponIsUsedBl = false;
//          ivNoUse.setVisibility(View.INVISIBLE);
//      }else if (value == 1){
//          couponIsUsedBl = true;
//          ivNoUse.setVisibility(View.VISIBLE);
//        }
//        Common.networkConnected(activity);
//        String url = Common.URL_SERVER + "MyCouponServlet";

        //jsonObject.addProperty("MyCouPon", new Gson().toJson(mycoupon));


    }


}
