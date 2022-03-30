package com.example.foodradar_android.user;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
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
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Switch;
import android.widget.TextView;

import com.example.foodradar_android.Common;
import com.example.foodradar_android.R;
import com.example.foodradar_android.task.CommonTask;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

import static android.content.Context.MODE_PRIVATE;

public class UserManagementFragment extends Fragment {
    private Activity activity;
    private NavController navController;

    private List<UserAccount> userAccountList = new ArrayList<>();
    private List<UserImageTask> imageTasks= new ArrayList<>();
    private RecyclerView rcvUserManagement;
    private String URL_SERVER = "http://10.0.2.2:8080/FoodRadar_Web/";
    private String USERACCOUNT_SERVLET = URL_SERVER + "UserAccountServlet";
    private Integer imageSize = 50;
    final private String TAG = "UserManagementFragment";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();

        // 顯示左上角的返回箭頭
        new Common();
        Common.setBackArrow(true, activity);
        setHasOptionsMenu(true);

        navController =
                Navigation.findNavController(activity, R.id.mainFragment);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity.setTitle(R.string.title_of_res_user_management);
        return inflater.inflate(R.layout.fragment_user_management, container, false);
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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rcvUserManagement = view.findViewById(R.id.id_rcvUserManagement);
        rcvUserManagement.setLayoutManager(new LinearLayoutManager(activity));

        SearchView searchViewUserManagement = view.findViewById(R.id.searchViewUserManagement);
        searchViewUserManagement.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) { return false; }
            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.trim().isEmpty()) {
                    showUserAccount(userAccountList);
                } else {
                    List<UserAccount> searchUserAccountList = new ArrayList<>();
                    for(UserAccount userAccount : userAccountList){
                        if(userAccount.getUserPhone().toUpperCase().contains(newText.toUpperCase())){
                            searchUserAccountList.add(userAccount);
                        }
                    }
                    showUserAccount(searchUserAccountList);
                }
                return true;
            }
        });

        userAccountList = getUserAccount();
//        Log.d("TAG","myResList: " + myResList);
        showUserAccount(userAccountList);

    }

//    private int getUserId(){ return Common.USER_ID; }

    private class UserAccountAdapter extends RecyclerView.Adapter<UserAccountAdapter.MyViewHolder> {
        private LayoutInflater layoutInflater;
        private List<UserAccount> userAccountListAdp;
        /* 螢幕寬度除以4當作將圖的尺寸 */
        private final int imgSize = getResources().getDisplayMetrics().widthPixels / 4;

        UserAccountAdapter(Context context, List<UserAccount> userAccountListAdp) {
            this.layoutInflater = LayoutInflater.from(context);
            this.userAccountListAdp = userAccountListAdp;
        }

        public void setUserAccountListAdp(List<UserAccount> userAccountListAdp) {
            this.userAccountListAdp = userAccountListAdp;
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            ImageView imUserManageAvatar;
            TextView tvUserManagePhone,tvUserManageRegisterTime,tvUserManageEditTime,tvUserManageAccountStatus;
            Switch swUserManageStatus;
            MyViewHolder(@NonNull View itemView) {
                super(itemView);
                imUserManageAvatar = itemView.findViewById(R.id.imUserManageAvatar);
                tvUserManagePhone = itemView.findViewById(R.id.tvUserManagePhone);
                tvUserManageRegisterTime = itemView.findViewById(R.id.tvUserManageRegisterTime);
                tvUserManageEditTime = itemView.findViewById(R.id.tvUserManageEditTime);
                tvUserManageAccountStatus = itemView.findViewById(R.id.tvUserManageAccountStatus);
                swUserManageStatus = itemView.findViewById(R.id.swUserManageStatus);
            }
        }


        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = layoutInflater
                    .inflate(R.layout.item_view_user_management,parent,false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            UserAccount uA = userAccountListAdp.get(position);
            Resources res = getResources();
            holder.tvUserManagePhone.setText(getResources().getString(R.string.textUserPhone) + ": " + uA.getUserPhone() + " ( ID: " + String.valueOf(uA.getUserId()) + " )");
            holder.tvUserManageRegisterTime.setText(getResources().getString(R.string.textRegisterDate) + ": " + uA.getCreateDate().toString());
            holder.tvUserManageEditTime.setText(getResources().getString(R.string.textModifyDate) + ": " + uA.getModifyDate().toString());

            String strAccountStatus = getResources().getString(R.string.textAccountStatus) + ": "
                    + (uA.getIsEnable() ? getResources().getString(R.string.textOfEnable) : getResources().getString(R.string.textOfDisable) );
            holder.tvUserManageAccountStatus.setText(strAccountStatus);
            holder.swUserManageStatus.setChecked(uA.getIsEnable());
            if (uA.getIsEnable()) {
                holder.tvUserManageAccountStatus.setTextColor(Color.BLUE);
            } else {
                holder.tvUserManageAccountStatus.setTextColor(Color.RED);
            }

            if (Common.USER_ID.equals(uA.getUserId())) {
                Log.d("TAG","uA.getUserId(): " + uA.getUserId());
                holder.swUserManageStatus.setVisibility(View.INVISIBLE);
                holder.tvUserManageAccountStatus.setText(strAccountStatus + "      (以管理員身份登入)");
            }


            Integer userId = uA.getUserId(); // UserID
//            final MyRes bookOnBVH = books.get(position);
            Log.d(TAG,"userId: " + userId);
            Log.d(TAG,"USERACCOUNT_SERVLET: " + USERACCOUNT_SERVLET);
            UserImageTask userImageTask = new UserImageTask(USERACCOUNT_SERVLET, userId, imageSize, holder.imUserManageAvatar);
            userImageTask.execute(); // .execute() => UserImage.doInBackground
            imageTasks.add(userImageTask);

            holder.swUserManageStatus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

//                }
//            });
//            holder.swUserManageStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//                @Override
//                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    boolean isChecked = !uA.getIsEnable();
                    UserManagementFragment umf = new UserManagementFragment();

                    umf.setEnableWithConfirm(activity, holder, uA, isChecked);
                    // 因為用 AlertDialog，所以UI呈現的判斷與資料庫的處理全移到 AlertDialog 的onClick裡處理
//                    Resources res = getResources();
//                    String textAccountStatus = res.getString(R.string.textAccountStatus);
//                    String textOfEnable = res.getString(R.string.textOfEnable);
//                    String textOfDisable = res.getString(R.string.textOfDisable);
//
//                    if (umf.setEnableWithConfirm(activity, holder, uA, isChecked)) {
//                        String strAccountStatus = textAccountStatus + ": "
//                                + (isChecked ? textOfEnable : textOfDisable);
//                        holder.tvUserManageAccountStatus.setText(strAccountStatus);
//
//                        if (isChecked) {
//                            holder.tvUserManageAccountStatus.setTextColor(Color.BLUE);
//                        } else {
//                            holder.tvUserManageAccountStatus.setTextColor(Color.RED);
//                        }
////                        Common.showToast(activity, "設定完成");
//                    } else {
//                        holder.swUserManageStatus.setChecked(!isChecked);
////                        Common.showToast(activity, "設定未完成");
//                    }

                }
            });

        }

        @Override
        public int getItemCount() {
            if (userAccountListAdp == null) {
                return 0;
            } else {
                return userAccountListAdp.size();
            }
        }

    }

    private List<UserAccount> getUserAccount(){
        List<UserAccount> getUserAccountList = null;

        if (Common.networkConnected(activity)) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "getAll");
//            jsonObject.addProperty("id", getUserId());
            String jsonOut = jsonObject.toString();
//            Log.d(TAG,"getBooks.Common.URL_SERVLET: " + Common.URL_SERVLET);
            CommonTask getAllTask;
            getAllTask = new CommonTask(USERACCOUNT_SERVLET, jsonOut);
            try {
                String jsonIn = getAllTask.execute().get();
                Type listType = new TypeToken<List<UserAccount>>() {
                }.getType();
                getUserAccountList = new Gson().fromJson(jsonIn, listType);
//                Log.d(TAG,"getUserAccountList: " + getUserAccountList);
            } catch (Exception e) {
//                Log.e(TAG, e.toString());
                e.printStackTrace();
            }

        }else{
            Common.showToast(activity,"NetworkConnected: fail");
        }

        return getUserAccountList;
    }

    private void showUserAccount(List<UserAccount> showUAList){
        UserManagementFragment.UserAccountAdapter uAAdapter
                = (UserManagementFragment.UserAccountAdapter) rcvUserManagement.getAdapter();
        Log.d(TAG,"userAccountAdapter: " + uAAdapter);
        if (uAAdapter == null) {
            Log.d(TAG,"uAAdapter = null.showUserAccountList: " + showUAList);
            rcvUserManagement.setAdapter(new UserAccountAdapter(activity, showUAList));
        } else {
            Log.d(TAG,"uAAdapter.showUserAccountList: " + showUAList);
            uAAdapter.setUserAccountListAdp (showUAList);
            uAAdapter.notifyDataSetChanged();
        }

    }


    // 顯示詢問是否調整會員帳號狀態的對話視窗
    public void setEnableWithConfirm(Activity activity, UserAccountAdapter.MyViewHolder holder, UserAccount uA, Boolean enableStatus) {

        /* 設定positive與negative按鈕上面的文字與點擊事件監聽器 */
        AlertDialog.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:

                        Resources res = activity.getResources();
                        String textAccountStatus = res.getString(R.string.textAccountStatus);
                        String textOfEnable = res.getString(R.string.textOfEnable);
                        String textOfDisable = res.getString(R.string.textOfDisable);

                        if (setUserStatus(uA.getUserId(), enableStatus)){
                            String strAccountStatus = textAccountStatus + ": "
                                    + (enableStatus ? textOfEnable : textOfDisable);
                            holder.tvUserManageAccountStatus.setText(strAccountStatus);

                            if (enableStatus) {
                                holder.tvUserManageAccountStatus.setTextColor(Color.BLUE);
                            } else {
                                holder.tvUserManageAccountStatus.setTextColor(Color.RED);
                            }

                            holder.swUserManageStatus.setChecked(enableStatus);
                            uA.setIsEnable(enableStatus);
                            Common.showToast(activity, activity.getResources().getString(R.string.textSetSuccess));
                        } else {
                            holder.swUserManageStatus.setChecked(!enableStatus);
                            uA.setIsEnable(!enableStatus);
                            Common.showToast(activity, activity.getResources().getString(R.string.textSetFail));
                        }
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
//                    break;
                    default:
                        Common.showToast(activity, R.string.textCancel);
                        /* 關閉對話視窗 */
                        holder.swUserManageStatus.setChecked(!enableStatus);
                        dialog.cancel();

                        break;
                }
            }
        };

        Resources res = activity.getResources();

        String txtEnable = "啟用"; // res.getString(R.string.textOfEnable);
        String txtDisable = "停用"; // res.getString(R.string.textOfDisable);
        String txtEnableStatus = (enableStatus? txtEnable : txtDisable);

        String textPhone = "手機號碼"; // getResources().getString(R.string.textUserPhone)
        String textRegisterDate = "註冊日期"; // getResources().getString(R.string.textRegisterDate)

        String alertMessage = "\n" + textPhone + ": " + uA.getUserPhone() + " ( ID: " + String.valueOf(uA.getUserId()) + " )";
        alertMessage += "\n" + textRegisterDate + ": " + uA.getCreateDate().toString();
        alertMessage += "\n\n" + "確認要調整這個會員的\n帳號狀態為“" + txtEnableStatus + "”嗎？";

        String positiveText = "是，" + txtEnableStatus + "此會員";
        String negativeText = "否，不調整";

        new AlertDialog.Builder(activity)
                /* 設定標題 */
                .setTitle(R.string.title_of_res_user_management)
                /* 設定圖示 */
                .setIcon(R.drawable.ic_baseline_build_24)
                /* 設定訊息文字 */
                .setMessage(alertMessage)
                /* 設定positive與negative按鈕上面的文字與點擊事件監聽器 */
                .setPositiveButton(positiveText, listener)
                .setNegativeButton(negativeText, listener)
                // 是否一定要按按鈕才能離開對話框，預設為true，設false代表必須點擊按鈕方能關閉
                .setCancelable(true)
                .create()
                .show();

    }

    public Boolean setUserStatus(int userId ,Boolean enableStatus){

        Log.d(TAG,"setUserEnable.userId: " + userId);
        Log.d(TAG,"setUserEnable.enableStatus: " + enableStatus);

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("action", "setEnableStatus");
        jsonObject.addProperty("id", userId);
        jsonObject.addProperty("enableStatus", enableStatus);
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
            return true;
        }

    }



}