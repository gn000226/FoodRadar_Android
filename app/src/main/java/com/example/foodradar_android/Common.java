package com.example.foodradar_android;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;

import com.example.foodradar_android.article.Img;
import com.example.foodradar_android.task.CommonTask;
import com.example.foodradar_android.user.UserAccountAvatra;
import com.example.foodradar_android.user.UserAccount;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.example.foodradar_android.user.UserImageTask;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;

public class Common {
    private final static String PREFERENCES_NAME = "foodradar_preference"; //prep偏好設定檔名
    private static final String TAG = "TAG_Common";
    public static String URL_SERVER = "http://10.0.2.2:8080/FoodRadar_Web/";
    public static String USERACCOUNT_SERVLET = URL_SERVER + "UserAccountServlet";
    public NavController navController;


    // 使用者登入後的ID(UserAccount.userId)，若 USER_ID <= 0 代表未登入或沒登入成功
    public static Integer USER_ID = 0 ;
    public static Integer ARTICLE_FRAGMENT_ID = 0 ;
//    public static Integer USER_ID = 0;
    private Activity activity;
    public static final String USER_AVATAR_FILENAME = "foodradar_avatar.byte";
    public static final String IMAGE_FILENAME = "foodradar_image.byte";

    public static boolean networkConnected(Activity activity) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) activity.getSystemService(activity.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // API 23支援getActiveNetwork()
                Network network = connectivityManager.getActiveNetwork();
                // API 21支援getNetworkCapabilities()
                NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities(network);
                if (networkCapabilities != null) {
                    String msg = String.format(Locale.getDefault(),
                            "TRANSPORT_WIFI: %b%nTRANSPORT_CELLULAR: %b%nTRANSPORT_ETHERNET: %b%n",
                            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI),
                            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR),
                            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET));
                    Log.d(TAG, msg);
                    return networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET);
                }
            } else {
                // API 29將NetworkInfo列為deprecated
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                return networkInfo != null && networkInfo.isConnected();
            }
        }
        return false;
    }

    public static void showToast(Context context, int messageResId) {
        Toast.makeText(context, messageResId, Toast.LENGTH_SHORT).show();
    }

    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    // 設定Fargement的AppBar是否要顯示返回的箭頭
    public static void setBackArrow(Boolean isDisplay, Activity activity){
        ActionBar actionBar = ((AppCompatActivity)activity).getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(isDisplay);
    }

    // 使用者登入，傳入 Activity、登入的手機號碼(userPhone)、使用者密碼(userPwd)
    // 成功登入回傳使用者ＩＤ(userId)
    // 登入失敗回傳
    //  0=>登入失敗(原因不明)
    // -1=>使用者帳號(手機號碼)不存在
    // -2=>使用者密碼錯誤
    public static int userLogin(@NonNull Activity activity, String userPhone, String userPwd){
        SharedPreferences preferences; // 定義一個存取偏好設定檔的Preferences
        preferences = activity.getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE);
        Log.d(TAG,"userLogin.userPhone: " + userPhone);
        Log.d(TAG,"userLogin.userPwd: " + userPwd);

        // vvvvvv臨時寫的，用來模擬使用者登入
//        String fromDB_userPhone = "0900123456";
//        String fromDB_userPwd = "P@ssw0rd";
//        Integer fromDB_userId = 3;
////
////        if (fromDB_userPhone == userPhone && fromDB_userPwd == userPwd) {
////            userId = fromDB_userId;
////            preferences.edit()
//
//        Timestamp fromDB_userBirth = Timestamp.valueOf("2020-06-15" + " 00:00:00.000");
//        String fromDB_userName = "CloseBeta";
//        Boolean fromDB_allowNotifi = false;
////        Boolean fromDB_isEnable = true;
////        Boolean fromDB_isAdmin = false;
//        byte[] fromDB_userAvatar = null;
//        Calendar rightNow = Calendar.getInstance();
//        rightNow.add(Calendar.DATE, -30 );
//        Timestamp fromDB_createDate = new Timestamp(rightNow.getTimeInMillis());
//        Timestamp fromDB_modifyDate = new Timestamp(System.currentTimeMillis());
        // ^^^^^^臨時寫的，用來模擬使用者登入

        String fromDB_userPhone = "";
        String fromDB_userPwd = "";
        Integer fromDB_userId = 0;
        Timestamp fromDB_userBirth = null;
        String fromDB_userName = "";
        Boolean fromDB_allowNotifi = null;
        Boolean fromDB_isEnable;
        Boolean fromDB_isAdmin;
        byte[] fromDB_userAvatar = null;
        Timestamp fromDB_createDate = null;
        Timestamp fromDB_modifyDate = null;

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("action", "userLogin");
        jsonObject.addProperty("userPhone", userPhone);
        jsonObject.addProperty("userPwd", userPwd);
        String jsonOut = jsonObject.toString();
//            Log.d(TAG,"getBooks.Common.URL_SERVLET: " + Common.URL_SERVLET);
        CommonTask getAllTask;
        UserAccount userAccountFromJsonList = null;
        List<UserAccount> getUserAccountList = new ArrayList<>();
        getAllTask = new CommonTask(USERACCOUNT_SERVLET, jsonOut);
        try {
            String jsonIn = getAllTask.execute().get();
            Type listType = new TypeToken<List<UserAccount>>() {
            }.getType();
            getUserAccountList = new Gson().fromJson(jsonIn, listType);

            Log.d(TAG,"getUserAccountList: " + getUserAccountList);
            userAccountFromJsonList = getUserAccountList.get(0);

            for(UserAccount ua : getUserAccountList){
                fromDB_userPhone = ua.getUserPhone();
                fromDB_userPwd = ua.getUserPwd();
                fromDB_userId = ua.getUserId();
                fromDB_userBirth = ua.getUserBirth();
                fromDB_userName = ua.getUserName();
                fromDB_allowNotifi = ua.getAllowNotifi();
                fromDB_isEnable = true;
                fromDB_isAdmin = false;
//                fromDB_userAvatar = ua.getUserAvatar();
                fromDB_createDate = ua.getCreateDate();
                fromDB_modifyDate = ua.getModifyDate();
            }
        } catch (Exception e) {
            Log.e(TAG, "userLogin: EXCEPTION");
            e.printStackTrace();
        }

        if (fromDB_userPhone.equals(userPhone) && fromDB_userPwd.equals(userPwd)) {
            USER_ID = fromDB_userId;
            Log.d(TAG,"userLogin.USER_ID: " + USER_ID);
            new Common().setPreferences(activity, userAccountFromJsonList);
//            preferences.edit()
//                    .putString("userPhone", fromDB_userPhone)
////                    .putString("userPwd", userPwd)
//                    .putString("userId", String.valueOf(USER_ID))
//                    .putString("userBirth", String.valueOf(fromDB_userBirth))
//                    .putString("userName", fromDB_userName)
//                    .putString("allowNotifi", String.valueOf(fromDB_allowNotifi))
////                    .putString("userAvatar", String.valueOf(fromDB_userAvatar))
//                    .putString("createDate", String.valueOf(fromDB_createDate))
//                    .putString("modifyDate", String.valueOf(fromDB_modifyDate))
//                    .apply();
            Log.d(TAG,"jsonObjectAvatar: ");
            JsonObject jsonObjectAvatar = new JsonObject();
            jsonObjectAvatar.addProperty("action", "getImageAvatar");
            jsonObjectAvatar.addProperty("id", USER_ID);
//            private CommonTask dataUploadTask;
//            jsonObjectAvatar.addProperty("imageBase64", Base64.encodeToString(image, Base64.DEFAULT));
            CommonTask dataUploadTask = new CommonTask(USERACCOUNT_SERVLET, jsonObjectAvatar.toString());
            byte [] imageByte;
            try {
                String jsonIn = dataUploadTask.execute().get();
                Log.d(TAG,"dataUploadTask.JsonIn: " + jsonIn);

                JsonObject jObject = new Gson().fromJson(jsonIn, JsonObject.class);
                Log.d(TAG,"jObject.get(imageBase64): " + jObject.get("imageBase64").getAsString());

                imageByte = Base64.decode(jObject.get("imageBase64").getAsString(), Base64.DEFAULT);
                Log.d(TAG,"dataUploadTask.imageBase64: " + imageByte);

                Bitmap bitmap = BitmapFactory.decodeByteArray(imageByte, 0, imageByte.length);

                ImageView imageView = activity.findViewById(R.id.ivAvatar);
                Log.d(TAG,"bitmap: " + bitmap);
                if (bitmap != null) {
                    new Common().setUserAvatra(activity, bitmap);
                    if (imageView != null){
                        imageView.setImageBitmap(bitmap);
                    }
                }

            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
//
//            ImageView imageView = activity.findViewById(R.id.ivAvatar);
//            UserMyResImage userMyResImage = new UserMyResImage(USERACCOUNT_SERVLET,USER_ID,activity,imageView);
//            userMyResImage.execute(); // .execute() => UserImage.doInBackground

            // 使用者登入後，把BottomNavigationView會員專區文字改暫時改為 使用者ID值 或 "會員專區" 供識別
//            BottomNavigationView bottomNavigationView =  activity.findViewById(R.id.BottomNavigation);
//            bottomNavigationView.getMenu().getItem(4).setTitle(String.valueOf(USER_ID));
            new Common().setBottomNavigationViewBadge(activity,4,String.valueOf(USER_ID),true);


            return USER_ID;
        } else if (fromDB_userPhone != userPhone) {
            return  -1;
        } else {
            return  -2;
        }
    }

    public static void sendTokenToServer(String token, Activity activity) {
        if (Common.networkConnected(activity)) {
            String url = Common.URL_SERVER + "FcmBasicServlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "register");
            jsonObject.addProperty("registrationToken", token);
            String jsonOut = jsonObject.toString();
            CommonTask registerTask = new CommonTask(url, jsonOut);
            registerTask.execute();
        } else {
            Common.showToast(activity, R.string.textNoNetwork);
        }

    }

    // 登入成功時，產生偏好設定檔
    public void setPreferences(Activity activity, UserAccount ua) {
        String fromDB_userPhone = "";
        String fromDB_userPwd = "";
        Integer fromDB_userId = 0;
        Timestamp fromDB_userBirth = null;
        String fromDB_userName = "";
        Boolean fromDB_allowNotifi = null;
        Boolean fromDB_isEnable;
        Boolean fromDB_isAdmin;
        byte[] fromDB_userAvatar = null;
        Timestamp fromDB_createDate = null;
        Timestamp fromDB_modifyDate = null;

        fromDB_userPhone = ua.getUserPhone();
        fromDB_userPwd = ua.getUserPwd();
        fromDB_userId = ua.getUserId();
        fromDB_userBirth = ua.getUserBirth();
        String strFromDB_userBirth = new SimpleDateFormat("yyyy-MM-dd 00:00:00").format(fromDB_userBirth);
        fromDB_userName = ua.getUserName();
        fromDB_allowNotifi = ua.getAllowNotifi();
        fromDB_isEnable = true;
        fromDB_isAdmin = false;
        fromDB_userAvatar = ua.getUserAvatar();
        fromDB_createDate = ua.getCreateDate();
        String strFromDB_createDate = new SimpleDateFormat("yyyy-MM-dd 00:00:00").format(fromDB_createDate);
        fromDB_modifyDate = ua.getModifyDate();
        String strFromDB_modifyDate = new SimpleDateFormat("yyyy-MM-dd 00:00:00").format(fromDB_modifyDate);

        SharedPreferences preferences; // 定義一個存取偏好設定檔的Preferences
        preferences = activity.getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE);
        preferences.edit()
                .putString("userPhone", fromDB_userPhone)
//                    .putString("userPwd", userPwd)
                .putString("userId", String.valueOf(fromDB_userId))
                .putString("userBirth", strFromDB_userBirth)
                .putString("userName", fromDB_userName)
                .putString("allowNotifi", String.valueOf(fromDB_allowNotifi))
//                    .putString("userAvatar", String.valueOf(fromDB_userAvatar))
                .putString("createDate", strFromDB_createDate)
                .putString("modifyDate", strFromDB_modifyDate)
                .apply();

        ImageView imageView = activity.findViewById(R.id.ivAvatar);
        Integer imageSize = 200;
        UserImageTask userImageTask = new UserImageTask(USERACCOUNT_SERVLET,USER_ID,imageSize,imageView);
        userImageTask.execute(); // .execute() => UserImage.doInBackground
    }


    // 取得推播功能狀態
    public Boolean getUserAllowNotifi(Activity activity){
        SharedPreferences preferences; // 定義一個存取偏好設定檔的Preferences
        preferences = activity.getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE);
        String userId = preferences.getString("userId", "0");

        if (userId.equals("") || userId == null || userId.equals("0")){
            return false;
        } else {
            String allowNotifi = preferences.getString("allowNotifi", "0");
            return Boolean.parseBoolean(allowNotifi);
        }
    }

    // 設定推播功能狀態
    public Boolean setUserAllowNotifi(Activity activity, Boolean notifiStatus){

        String strAllowNotifi = "0";
        if (notifiStatus == true) {
            strAllowNotifi = "1";
        }
        Log.d(TAG,"setUserAllowNotifi.USER_ID: " + USER_ID);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("action", "setNotifiStatus");
        jsonObject.addProperty("id", USER_ID);
        jsonObject.addProperty("notifiStatus", notifiStatus);
        int count = 0;
        try {
            String result = new CommonTask(USERACCOUNT_SERVLET, jsonObject.toString()).execute().get(); // Insert可等待回應確認是否新增成功
            count = Integer.parseInt(result);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
        if (count == 0) {
            return false;
        } else {
            SharedPreferences preferences; // 定義一個存取偏好設定檔的Preferences
            preferences = activity.getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE);
            preferences.edit().putString(strAllowNotifi, "0");
            return true;
        }

    }


    // 取得已登入的使用者ＩＤ (userId)
    // 若未登入或登入狀態不明，則回傳0
    public static UserAccount getUserLoin(Activity activity){
        SharedPreferences preferences; // 定義一個存取偏好設定檔的Preferences
        preferences = activity.getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE);

        String userId = preferences.getString("userId", "0");
        String userPhone = preferences.getString("userPhone", "");
        String userBirth = preferences.getString("userBirth", "");
        String userName = preferences.getString("userName", "");

        String allowNotifi = preferences.getString("allowNotifi","0");
        String createDate = preferences.getString("createDate", "");
        String modifyDate = preferences.getString("modifyDate", "");

        UserAccount userAccount = null;
        Log.d(TAG,"getUserLoin.userId: " + userId);
        if (userId.equals("") || userId == null || userId.equals("0")){
            userAccount = new UserAccount(0, "", null, ""
                    , false, null, null, null);

            // 使用者登入後，把BottomNavigationView會員專區文字改暫時改為 使用者ID值 或 "會員專區" 供識別
//            BottomNavigationView bottomNavigationView =  activity.findViewById(R.id.BottomNavigation);
//            bottomNavigationView.getMenu().getItem(4).setTitle(R.string.user);
            new Common().setBottomNavigationViewBadge(activity,4,String.valueOf(USER_ID),false);

        } else {
            Integer userId_Int = Integer.parseInt(userId);
            USER_ID = userId_Int;

            // 使用者登入後，把BottomNavigationView會員專區文字改暫時改為 使用者ID值 或 "會員專區" 供識別
//            BottomNavigationView bottomNavigationView =  activity.findViewById(R.id.BottomNavigation);
//            bottomNavigationView.getMenu().getItem(4).setTitle(String.valueOf(USER_ID));
            new Common().setBottomNavigationViewBadge(activity,4,String.valueOf(USER_ID),true);

            Timestamp userBirth_Timestamp = null;
            try {
                userBirth_Timestamp = Timestamp.valueOf(userBirth);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Boolean allowNotifi_Boolean = Boolean.parseBoolean(allowNotifi);

            Timestamp createDate_Timestamp = null;
            if (!createDate.equals(null)) { createDate_Timestamp = Timestamp.valueOf(createDate); }
            Timestamp modifyDate_Timestamp = null;
            if (!modifyDate.equals(null)) { modifyDate_Timestamp = Timestamp.valueOf(modifyDate); }

            userAccount = new UserAccount(userId_Int, userPhone, userBirth_Timestamp, userName
                    , allowNotifi_Boolean, null, createDate_Timestamp, modifyDate_Timestamp);
        }
        return userAccount;
    }


    public void setUserAvatra(Activity activity, Bitmap avatraBitmap){
        UserAccountAvatra userAccountAvatraOut = new UserAccountAvatra();
        userAccountAvatraOut.setByteObject(bitmapToByte(avatraBitmap));
        try (ObjectOutputStream oisOut = new ObjectOutputStream(
                activity.openFileOutput(USER_AVATAR_FILENAME, Context.MODE_PRIVATE))) {
            oisOut.writeObject(userAccountAvatraOut);
        } catch (IOException e) {
            Log.d(TAG,"setUserAvatra: Exception");
            Log.d(TAG, e.toString());
        }
    }

    /*  */
    public void setImage(Activity activity, Bitmap imageBitmap){
        UserAccountAvatra userAccountAvatraOut = new UserAccountAvatra();
        Img img = new Img();
        img.setImgByte(bitmapToByte(imageBitmap));
        try (ObjectOutputStream oisOut = new ObjectOutputStream(
                activity.openFileOutput(IMAGE_FILENAME, Context.MODE_PRIVATE))) {
            oisOut.writeObject(userAccountAvatraOut);
        } catch (IOException e) {
            Log.d(TAG, e.toString());
        }
    }

    // 取得使用者頭像，回傳Bitmap
    public Bitmap getUserAvatra(Activity activity){
        UserAccountAvatra userAccountAvatraIn;
        Resources res = activity.getResources();

        try (ObjectInputStream oisIn = new ObjectInputStream(
                activity.openFileInput(USER_AVATAR_FILENAME))) {
            userAccountAvatraIn = (UserAccountAvatra) oisIn.readObject();

            if (userAccountAvatraIn.getByteObject().equals(null) || USER_ID <= 0) {
                Log.d(TAG,"userAccountAvatraIn.getByteObject().equals(null) || USER_ID <= 0: 沒取到頭像");
                // 若取不到頭像，則回傳預設頭像
                new Common().setUserAvatra(activity,BitmapFactory.decodeResource(res,R.drawable.ic_awesome_user_circle));
                return BitmapFactory.decodeResource(res,R.drawable.ic_awesome_user_circle);
            } else {
                Log.d(TAG,"userAccountAvatraIn.getByteObject(): 取到頭像");
                return byteToBitmap(userAccountAvatraIn.getByteObject());
            }

        } catch (Exception e) {
            Log.d(TAG,"getUserAvatra: Exception，回傳預設頭像");
            Log.d(TAG, e.toString());
            // 若取不到頭像，則回傳預設頭像
            new Common().setUserAvatra(activity,BitmapFactory.decodeResource(res,R.drawable.ic_awesome_user_circle));
            return BitmapFactory.decodeResource(res,R.drawable.ic_awesome_user_circle);
        }
    }

    // 使用者登出，傳入 Activity
    public void userLogout(Activity activity){
        String userPhone= "logout", userPwd = "logout";
        // 清除使用者偏好設定
        SharedPreferences preferences; // 定義一個存取偏好設定檔的Preferences
        preferences = activity.getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE);
        preferences.edit()
                .putString("userId", "0")
                .putString("userPhone", "")
                .putString("userPwd", "")
                .putString("userBirth", "")
                .putString("userName", "")
                .putString("allowNotifi", "0")
                .putString("userAvatar", "")
                .putString("createDate", "")
                .putString("modifyDate", "")
                .apply();
        // USER_ID 設為 0(未登入)
        USER_ID = 0;

        // 使用者登入後，把BottomNavigationView會員專區文字改暫時改為 使用者ID值 或 "會員專區" 供識別
//        BottomNavigationView bottomNavigationView =  activity.findViewById(R.id.BottomNavigation);
//        bottomNavigationView.getMenu().getItem(4).setTitle(R.string.user);
        new Common().setBottomNavigationViewBadge(activity,4,String.valueOf(USER_ID),false);

        // 頭像設為預設值
        Resources res = activity.getResources();
        Bitmap account_circle_bitmap = BitmapFactory.decodeResource(res,R.drawable.ic_awesome_user_circle);
        new Common().setUserAvatra(activity, account_circle_bitmap);
    }

    public Bitmap getImageView (ImageView imageView) {
        BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
        Bitmap bitmap = drawable.getBitmap();
        return bitmap;
    }

    public static byte[] bitmapToByte(Bitmap bitmap) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            // quality設100代表不壓縮，範圍值0~100
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            return stream.toByteArray();
    }

    public static Bitmap byteToBitmap(byte[] bitmapOfByte) {
            return BitmapFactory.decodeByteArray(bitmapOfByte, 0, bitmapOfByte.length);
    }

    // 設定Bottom Navigation View 的 Badge(訊息數量提示標籤)
    public void setBottomNavigationViewBadge(Activity activity, int bottomMenuIndex, String badgeText, Boolean badgeIsVisibility){
//        https://stackoverflow.com/questions/42682855/display-badge-on-top-of-bottom-navigation-bars-icon
//        BottomNavigationMenuView bottomNavigationMenuView =
//                (BottomNavigationMenuView) navigationView.getChildAt(0);
//        View v = bottomNavigationMenuView.getChildAt(3);
//        BottomNavigationItemView itemView = (BottomNavigationItemView) v;
//
//        View badge = LayoutInflater.from(this)
//                .inflate(R.layout.notification_badge, itemView, true);
// https://stackoverflow.com/questions/42682855/display-badge-on-top-of-bottom-navigation-bars-icon

// https://stackoverflow.com/questions/28071349/the-specified-child-already-has-a-parent-you-must-call-removeview-on-the-chil
//        if(tv.getParent() != null) {
//            ((ViewGroup)tv.getParent()).removeView(tv); // <- fix
//        }
//        layout.addView(tv); //  <==========  ERROR IN THIS LINE DURING 2ND RUN
//
//        if(tvBadgeText.getParent() != null) {
//            ((ViewGroup)tvBadgeText.getParent()).removeView(tvBadgeText); // <- fix
//        }


        if (badgeText.equals("0")) {
            badgeText = "";
        } else {
            badgeText = "";
            if ( getIsAdmin(activity) == true) {
                badgeText = "管理";
            }
        }

        // find id 取得 BottomNavigationView
        BottomNavigationView bottomNavigationView = activity.findViewById(R.id.BottomNavigation);
        // 取出BottomNavigationView的 Child MenuView
        BottomNavigationMenuView menuView = (BottomNavigationMenuView) bottomNavigationView.getChildAt(0);
        //取得要設定的 BottomMenu，BottomMenuIndex: 0 ~ count-1
        View bottomMenu = menuView.getChildAt(bottomMenuIndex);
        BottomNavigationItemView itemView = (BottomNavigationItemView) bottomMenu;

        Log.d(TAG,"itemView.getChildCount(): " + itemView.getChildCount());
        for (int childIndex = 0; childIndex <= itemView.getChildCount()-1 ; childIndex++) {
            Log.d(TAG, "itemView.childIndex: " + childIndex);

            // vvvvvv
            Integer itemID = itemView.getChildAt(childIndex).getId();
//            Log.d(TAG, "itemView.itemID: " + itemID);
//            if (itemID > 0) { Log.d(TAG, "itemView.itemName: " + activity.getResources().getResourceEntryName(itemID)); }
            if (itemID > 0 && activity.getResources().getResourceEntryName(itemID).equals("bnvBadge")) {
                // 把 BottomNavigationItemView 上的 badgeView 移除
                itemView.removeViewAt(childIndex);
            }
            // ^^^^^^

            // vvvvvv
////            Log.d(TAG, "itemView.getTransitionName: " + itemView.getChildAt(childIndex).getTransitionName());
//            if (itemView.getChildAt(childIndex).getTransitionName() != null && itemView.getChildAt(childIndex).getTransitionName().equals("bnvBadge")) {
//                // 把 BottomNavigationItemView 上的 badgeView 移除
//                itemView.removeViewAt(childIndex);
//            }
            // ^^^^^^
        }

        // 設定badgeView是否 顯示(add增加) 或 不顯示(remove移除)
        if (badgeIsVisibility == true) {
            Log.d(TAG,"removeViewAt.badgeIsVisibility: " + badgeIsVisibility);

            Log.d(TAG,"itemView.getChildCount(): " + itemView.getChildCount());
            Log.d(TAG,"badgeText: " + badgeText);
            if (itemView.getChildCount() > 2) {
                itemView.removeViewAt(2);
            }
            // vvvvvv
            // 載入 Badge 的 XML
            View badgeView = LayoutInflater.from(activity)
                    .inflate(R.layout.bottomnavigationview_badge, itemView, true);
            // 設定在 badgeView 裡的 TextView 的文字
            TextView tvBadgeText = (TextView) badgeView.findViewById(R.id.tvBadgeText);
            // ^^^^^^

            // vvvvvv
//            // 產生 Badge 的Layout與View
//            TextView tvBadgeText = BadgeView(activity, badgeText);
//            //在 BottomNavigationItemView 上增加 badgeView
//            itemView.addView(tvBadgeText);
            // ^^^^^^

            // 設定在 badgeView 裡的 TextView 的文字
            tvBadgeText.setText(String.valueOf(badgeText));
//            tvBadgeText.setVisibility(View.VISIBLE);
            Log.d(TAG,"addView.badgeText: " + badgeText);
        }
    }

    //控制BottomNav > articleNavigation
    public static void btControl(Activity activity, boolean isVisible) {
        BottomNavigationView bt = activity.findViewById(R.id.articleNavigation);
        if (isVisible){
            bt.setVisibility(View.VISIBLE);
        } else {
            bt.setVisibility(View.GONE);
        }
    }

    //控制FoatingActionButton
    public static void faButtonControl(Activity activity, boolean isVisible) {
        FloatingActionButton fbArticleInsert = activity.findViewById(R.id.fbArticleInsert);
        if (isVisible){
            fbArticleInsert.setVisibility(View.VISIBLE);
        } else {
            fbArticleInsert.setVisibility(View.GONE);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static void enableBottomBar(Activity activity, boolean enable, int i) {
        BottomNavigationView bottomNavigationView = activity.findViewById(R.id.BottomNavigation);
        bottomNavigationView.getMenu().getItem(i).setEnabled(enable);
    }

    // vvvvvv 產生 Badge 的Layout與View
/*
    private TextView BadgeView (Activity activity, String badgeText) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
//        layoutParams.gravity = Gravity.CENTER_HORIZONTAL;
        layoutParams.leftMargin = 140;
        layoutParams.topMargin = 16;
        // 呼叫TextView()建構式並傳入Context(Activity)物件 以動態建立TextView
        TextView tvBadgeText = new TextView(activity);
        tvBadgeText.setTransitionName("bnvBadge"); // :id="@+id/tvBadgeText"
//        tvBadgeText.setLayoutParams(itemView.getLayoutParams());
        tvBadgeText.setLayoutParams(layoutParams);
//        tvBadgeText.setHeight(50);
//        tvBadgeText.setWidth(100);
        tvBadgeText.setBackground(activity.getResources().getDrawable(R.drawable.button_circle_camera));
//        tvBadgeText.setBackgroundColor(Color.rgb(0,0,255));
        tvBadgeText.setGravity(View.TEXT_ALIGNMENT_GRAVITY);
//        tvBadgeText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        // 設定在 badgeView 裡的 TextView 的文字
        tvBadgeText.setText(String.valueOf(badgeText));
//        tvBadgeText.setTextColor(Color.rgb(0,255,0));
        tvBadgeText.setTextColor(activity.getResources().getColor(R.color.mainYellowLight));
        tvBadgeText.setTextSize(12);
        return tvBadgeText;
    }
*/
    // ^^^^^^ 產生 Badge 的Layout與View

    public String getUserPhoneByArticleManage(String userPhone) {
        if (userPhone.trim().isEmpty()) {
            userPhone = "0900123456"; // 3號使用者
        } else if (userPhone.equals("0900123456")){
            userPhone = "0900666666"; // 6號使用者
        } else {
            userPhone = "0900123456";
        }

        return userPhone;
    }


    // 設定系統管理畫面狀態
    public Boolean getIsAdmin(Activity activity){
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("action", "getIsAdmin");
        jsonObject.addProperty("id", USER_ID);
        int count = 0;
        try {
            String result = new CommonTask(USERACCOUNT_SERVLET, jsonObject.toString()).execute().get(); // Insert可等待回應確認是否新增成功
            count = Integer.parseInt(result);
            Log.d(TAG,"getIsAdmin.count: " + String.valueOf(count));
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
        if (count == 1) {
            return true;
        } else {
            return false;
        }

    }

}
