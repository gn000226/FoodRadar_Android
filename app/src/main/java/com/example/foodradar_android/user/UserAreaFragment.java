package com.example.foodradar_android.user;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.solver.state.State;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.foodradar_android.Common;
import com.example.foodradar_android.R;


public class UserAreaFragment extends Fragment implements View.OnClickListener {
    private Activity activity;
    private NavController navController;
    // 我的餐廳收藏
    private ImageView ivUsArMyRes;
    private Button btUsArMyRes;
    // 我的優惠券收藏
    private ImageView ivUsArMyCoupon;
    private Button btUsArMyCoupon;
    // 我的社群活動
    private ImageView ivUsArMyArticle;
    private Button btUsArMyArticle;
    // 會員資料設定
    private ImageView ivUsArUserData;
    private Button btUsArUserData;
    // 系統設定
    private ImageView ivUsArSysSetup;
    private Button btUsArSysSetup;
    private ConstraintLayout constraintLayout;
    // 聯繫我們
    private ImageView ivUsArContactUs;
    private Button btUsArContactUs;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();

        // 顯示左上角的返回箭頭
        Common.setBackArrow(false, activity);
        setHasOptionsMenu(true);

        navController = Navigation.findNavController(activity, R.id.mainFragment);
    }

    // 顯示右上角的OptionMenu選單
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
//        inflater.inflate(R.menu.appbar_menu,menu);  // 從res取用選項的清單“R.menu.my_menu“
//            super.onCreateOptionsMenu(menu, inflater);
    }
    // 顯示右上角的OptionMenu選單
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
//            case R.id.Finish:
//                navController.navigate(R.id.action_userAreaFragment_to_userDataSetupFragment);
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
        activity.setTitle(R.string.user);
        return inflater.inflate(R.layout.fragment_user_area, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        // 我的餐廳收藏
        ivUsArMyRes = view.findViewById(R.id.ivUsArMyRes);
        btUsArMyRes = view.findViewById(R.id.btUsArMyRes);
        btUsArMyRes.setOnClickListener(this);
        // 我的優惠券收藏
        ivUsArMyCoupon = view.findViewById(R.id.ivUsArMyCoupon);
        btUsArMyCoupon = view.findViewById(R.id.btUsArMyCoupon);
        btUsArMyCoupon.setOnClickListener(this);
        // 我的社群活動
        ivUsArMyArticle = view.findViewById(R.id.ivUsArMyArticle);
        btUsArMyArticle = view.findViewById(R.id.btUsArMyArticle);
        btUsArMyArticle.setOnClickListener(this);
        // 會員資料設定
        ivUsArUserData = view.findViewById(R.id.ivUsArUserData);
        btUsArUserData = view.findViewById(R.id.btUsArUserData);
        btUsArUserData.setOnClickListener(this);
        // 系統設定
        ivUsArSysSetup = view.findViewById(R.id.ivUsArSysSetup);
        btUsArSysSetup = view.findViewById(R.id.btUsArSysSetup);
        btUsArSysSetup.setOnClickListener(this);
        // 非管理員角色關閉系統設定
        constraintLayout = view.findViewById(R.id.constvUsArSysSetup);
        if (new Common().getIsAdmin(activity)) {
            constraintLayout.setVisibility(View.VISIBLE);
        }
        // 聯繫我們
        ivUsArContactUs = view.findViewById(R.id.ivUsArContactUs);
        btUsArContactUs = view.findViewById(R.id.btUsArContactUs);
        btUsArContactUs.setOnClickListener(this);

    }

    private int getUserId() { return Common.USER_ID; }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            // 我的餐廳收藏
            case R.id.btUsArMyRes:
                navController.navigate(R.id.action_userAreaFragment_to_userMyResFragment);
                break;
            // 我的優惠券收藏
            case R.id.btUsArMyCoupon:
                navController.navigate(R.id.action_userAreaFragment_to_userMyCouponFragment);
                break;
            // 我的社群活動
            case R.id.btUsArMyArticle:
                navController.navigate(R.id.userMyArticleFragment);
                break;
            // 會員資料設定
            case R.id.btUsArUserData:
                navController.navigate(R.id.action_userAreaFragment_to_userDataSetupFragment);
                break;
            // 系統設定
            case R.id.btUsArSysSetup:
                navController.navigate((R.id.action_userAreaFragment_to_userSysSetupFragment));
                break;
            // 聯繫我們
            case R.id.btUsArContactUs:
                final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                View dialogView = getLayoutInflater().inflate(R.layout.dialog_contact_us, null);
                    alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                alertDialog.setView(dialogView);
//                ImageView imAtMaResImg = dialogView.findViewById(R.id.imAtMaResImg);
//                final Integer articleId = myAtBindVH.getArticleId(); // 文章ID
//                UserImageTask userImageTask = new UserImageTask(MYARTICLE_SERVLET, articleId, 500, imAtMaResImg);
//                userImageTask.execute(); // .execute() => UserImage.doInBackground
//                imageTasks.add(userImageTask);
////                    imAtMaResImg.setMaxWidth(imageSize);
////                    imAtMaResImg.layout(imageSize,imageSize,imageSize,imageSize);
////                    this.imageSize = getResources().getDisplayMetrics().widthPixels / 4; // 取得螢幕寬度當圖片尺寸的基準
                alertDialog.show();


                break;
            default:
                break;
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        if (getUserId() <= 0) {
            setButton(false);
        } else {
            setButton(true);
        }
        // 左上角的返回箭頭顯示控制
        Common.setBackArrow(false, activity);
    }


    // 畫面功能Enable控制
    private void setButton(boolean setEnable) {
        Integer intTextColor = 0;
        Float floAlpha;
        if (setEnable == true) {
            intTextColor = getResources().getColor(R.color.mainPink);
            floAlpha = 1.0f;
        } else {
            intTextColor = getResources().getColor(R.color.colorTextHint);
            floAlpha = 0.2f;
        }

        // 我的餐廳收藏
        ivUsArMyRes.setAlpha(floAlpha);
        btUsArMyRes.setEnabled(setEnable);
        btUsArMyRes.setTextColor(intTextColor);
        // 我的優惠券收藏
        ivUsArMyCoupon.setAlpha(floAlpha);
        btUsArMyCoupon.setEnabled(setEnable);
        btUsArMyCoupon.setTextColor(intTextColor);
        // 我的社群活動
        ivUsArMyArticle.setAlpha(floAlpha);
        btUsArMyArticle.setEnabled(setEnable);
        btUsArMyArticle.setTextColor(intTextColor);
        // 會員資料設定
//        ivUsArUserData.setAlpha(floAlpha);
//        btUsArUserData.setEnabled(setEnable);
//        btUsArUserData.setTextColor(intTextColor);
        // 系統設定
//        ivUsArSysSetup.setAlpha(floAlpha);
//        btUsArSysSetup.setEnabled(setEnable);
//        btUsArSysSetup.setTextColor(intTextColor);
        // 非管理員角色關閉系統設定
        if (new Common().getIsAdmin(activity)) {
            constraintLayout.setVisibility(View.VISIBLE);
        }
        // 聯繫我們
//        ivUsArContactUs.setAlpha(floAlpha);
//        btUsArContactUs.setEnabled(setEnable);
//        btUsArMyRes.setTextColor(intTextColor);
    }

}