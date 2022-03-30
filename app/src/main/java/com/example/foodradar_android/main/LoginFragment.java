package com.example.foodradar_android.main;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.foodradar_android.Common;
import com.example.foodradar_android.R;
import com.example.foodradar_android.task.CommonTask;
import com.example.foodradar_android.user.UserAccount;
import com.example.foodradar_android.user.UserDataSetupFragment;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static android.view.View.combineMeasuredStates;


public class LoginFragment extends Fragment {
    private Activity activity;
    private NavController navController;
    private final String TAG = "TAG_LoginFragment";
    private final String USERACCOUNT_SERVLET = Common.URL_SERVER + "UserAccountServlet";
    private UserAccount userAccount;
    List<UserAccount> getUserAccountList = null;
    private CommonTask LoginGetAllTask;
    private enum ProcModeEnum { LOGIN, REGISTER, LOGOUT } // 目前操作模式識別
    private ProcModeEnum procMode;
    private ColorStateList edTextdefaultColor;

    private TextView tvuserPhone;
    private EditText userphone;

    private TextView tvuserPwd;
    private EditText etUserPwd;

    private TextView tvuserPwdConfirm;
    private EditText etuserPwdConfirm;

    private ImageView ivRedStarPasswordConfirm;
    private TextView tvPasswordConfirm;
    private EditText etPasswordConfirm;

    private Button login;
    private Button cancel;
    private int UserId;

//    private Boolean loggedin = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        //new Common().setBackArrow(true, activity);
//        setHasOptionsMenu(true);
        navController =
                Navigation.findNavController(activity, R.id.mainFragment);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        activity.setTitle("登入頁面");
        return inflater.inflate(R.layout.fragment_login, container, false);


    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //final NavController navController = Navigation.findNavController(view);
//        view.findViewById(R.id.tvuserPhone);
//        view.findViewById(R.id.userPhone);
//        userphone.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                userphone.setHintTextColor(getResources().getColor(R.color.colorTextHint));
//            }
//            @Override
//            public void afterTextChanged(Editable s) {}
//        });
        tvuserPhone = view.findViewById(R.id.tvuserPhone);
        userphone = view.findViewById(R.id.userPhone);

        tvuserPwd = view.findViewById(R.id.tvuserPwd);
        etUserPwd = view.findViewById(R.id.userPwd);

        login = view.findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener() {
                                     @Override
                                     public void onClick(View v) {

                                         if (!Common.networkConnected(activity)) {
                                             Common.showToast(activity, R.string.textNetworkConnectedFail);
                                             return;

                                         } else if (checkInput() == false) {
                                             return;

                                         } else {

                                             int logInResult = Common.userLogin(activity, userphone.getText().toString(), etUserPwd.getText().toString());
                                             if (logInResult <= 0) {
                                                 Common.showToast(activity, "登入失敗");
                                             } else {
                                                 Common.showToast(activity, "登入成功");
                                                 navController.navigate(R.id.mainFragment);
                                                 setHasOptionsMenu(false);
                                             }
                                         }
                                     }
                                 });
        cancel = view.findViewById(R.id.regcancel);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                navController.popBackStack();
               // Navigation.findNavController(v).navigate(R.id.action_loginFragment_to_userDataSetupFragment);
//                navController.navigate(R.id.action_loginFragment_to_userDataSetupFragment);
//                AlertDialog.Builder d = new AlertDialog.Builder(activity);
//                d.setTitle("警告！")
//                        .setMessage("已註冊")
//                        .setCancelable(false);
//                d.setPositiveButton("確定", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//
//
//                    }
//                });
//                d.show();
            }
        });

        view.findViewById(R.id.btAutoLogin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String  userPhone = "0900888888";
                String userPwd = "888888";
                userphone.setText(userPhone);
                etUserPwd.setText(userPwd);
            }
        }
        );


//                            UserAccount userAccount = new Common().getUserLoin(activity);
//                            Common.USER_ID = userAccount.getUserId();

//                            if (UserId > 0) {
//                                navController.navigate(R.id.action_loginFragment_to_mainFragment);
//                                Common.showToast(activity, "input");
//                            }
//                            if (userphone.length() <= 0 && userphone.length() >= 10 || userPwd.length() >= 10) {
//                                new Common().showToast(activity, "login error");
//                                activity.finish();
//                            }
//                            if (UserId <= 0) {
//                                Common.showToast(activity, "查無此帳號");
//
//                            } else {
//                                navController.popBackStack();
//                            }
//                        }
    }


    private boolean checkInput() {

        Boolean bolResult = true;

        String strToast = "";
        Resources res = getResources();

        // 去空白
        String strUserPhone = userphone.getText().toString().trim();
        userphone.setText(strUserPhone);
        String strPassword = etUserPwd.getText().toString().trim();
        etUserPwd.setText(strPassword);

        if (strUserPhone.equals("")) {
            strToast += ((strToast == "") ? "" : "\n") + tvuserPhone.getText() + space(1) + res.getString(R.string.textMustInput);
//            tvUserPhone.setTextColor(getResources().getColor(R.color.mainPink));
//            userphone.setHintTextColor(res.getColor(R.color.mainPink));
//            etUserPhone.setHint(getResources().getString(R.string.textPlsInpup) + getResources().getString(R.string.textUserPhone));
            bolResult = false;
        }

        if (strPassword.equals("")) {
            strToast += ((strToast == "") ? "" : "\n") + tvuserPwd.getText() + space(1) + res.getString(R.string.textMustInput);
//            userPwd.setHintTextColor(res.getColor(R.color.mainPink));
            bolResult = false;
        }


        if (bolResult == false) {
            Common.showToast(activity, strToast);
        }

        return bolResult;
    }

    private String space(int count) {
        String strSpace = "";
        for (int i = 0 ; i<=count ; i++) {
            strSpace += "\u0020";
        }
        return strSpace;
    }

            private void logoutWithConfirm() {
                /* 設定positive與negative按鈕上面的文字與點擊事件監聽器 */
                androidx.appcompat.app.AlertDialog.OnClickListener listener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                new Common().userLogout(activity);
                                setUI();
                                procMode = ProcModeEnum.LOGOUT;
                                Common.showToast(activity, R.string.textLogouted);
                                break;
                            case DialogInterface.BUTTON_NEGATIVE:
//                    break;
                            default:
                                Common.showToast(activity, R.string.textDontWantLogout);
                                /* 關閉對話視窗 */
                                dialog.cancel();
                                break;
                        }
                    }
                };

                new AlertDialog.Builder(activity)
                        /* 設定標題 */
                        .setTitle(R.string.textLogout)
                        /* 設定圖示 */
                        .setIcon(R.drawable.logo_foodradar)
                        /* 設定訊息文字 */
                        .setMessage("確認要登出嗎？")
                        /* 設定positive與negative按鈕上面的文字與點擊事件監聽器 */
                        .setPositiveButton("是的我要登出", listener)
                        .setNegativeButton(R.string.textDontWantLogout, listener)
                        // 是否一定要按按鈕才能離開對話框，預設為true，設false代表必須點擊按鈕方能關閉
                        .setCancelable(true)
                        .create()
                        .show();
            }

    private int getUserId(){
        return Common.USER_ID;
    }

    private void setUI() {
        if(getUserId() > 0) { // 已登入狀態
            setUiIsLogin();
        } else { // 已登出狀態
            setUiIsLogout();
        }
    }
    private void setUiIsLogin() {
        // 設定註冊欄位是(VISIBLE)否(INVISIBLE)顯示
        //setRegisterUI(VISIBLE);

        userAccount = Common.getUserLoin(activity);
        userphone.setText(userAccount.getUserPhone());
        userphone.setFocusable(false); // 手機號碼不可修改
        userphone.setFocusableInTouchMode(false); // 手機號碼不可修改
        etUserPwd.setText(userAccount.getUserPwd());
        etuserPwdConfirm.setText(userAccount.getUserPwd());
//        etUserName.setText(userAccount.getUserName());
//        String userBirth = new SimpleDateFormat("yyyy-MM-dd").format(userAccount.getUserBirth());
//        etUserBirth.setText(userBirth);
//
//        bitmapAvatra = Common.getUserAvatra(activity);
//        Common.setUserAvatra(activity, bitmapAvatra);
//        ivAvatar.setImageBitmap(bitmapAvatra);
//        ivAvatar.setImageResource(R.drawable.x_cat);
          login.setText(R.string.textLogout);
//        btnLogInOut.setText(R.string.textLogout);
//        btUserChangConfrim.setText(R.string.textUserChangConfrim);

    }

    private void setUiIsLogout() {
        // 設定註冊欄位是(VISIBLE)否(INVISIBLE)顯示
        //setRegisterUI(INVISIBLE);

        userphone.setText("");
        userphone.setFocusable(true); // 設定手機號碼可修改
        userphone.requestFocus(); // 設定手機號碼可修改
        userphone.setFocusableInTouchMode(true); // 設定手機號碼可修改

        etUserPwd.setText("");
        etuserPwdConfirm.setText("");
        login.setText(R.string.action_sign_in);
//        btnLogInOut.setText(R.string.action_sign_in);
//        btUserChangConfrim.setText(R.string.action_register);
//        bitmapAvatra = Common.getUserAvatra(activity);
//        ivAvatar.setImageBitmap(bitmapAvatra);
    }

}
