package com.example.foodradar_android.user;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;

import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.foodradar_android.Common;
import com.example.foodradar_android.R;

import com.example.foodradar_android.task.CommonTask;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.yalantis.ucrop.UCrop;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import static android.app.Activity.RESULT_OK;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

public class UserDataSetupFragment extends Fragment implements View.OnClickListener { // , View.OnFocusChangeListener
    private Activity activity;
    private NavController navController;
    public final String TAG = "TAG_UserDataSetupFrag";

    private ImageView ivAvatar;

    private final String USERACCOUNT_SERVLET = Common.URL_SERVER + "UserAccountServlet";
    private final String userAvatraFileName = Common.USER_AVATAR_FILENAME;
    List<UserAccount> getUserAccountList = null;

    private File file;
    private Uri contentUri;
    private static final int REQ_TAKE_PICTURE = 0;
    private static final int REQ_PICK_PICTURE = 1;
    private static final int REQ_CROP_PICTURE = 2;
//    private static final int REQ_TAKE_PICTURE = 101;
//    private static final int REQ_PICK_PICTURE = 102;
//    private static final int REQ_CROP_PICTURE = 103;
    private static final int PER_EXTERNAL_STORAGE = 201;

    private Bitmap bitmapAvatra = null;

    private ImageButton btImgCamera;
    private Boolean bolAccessExternalStorage;
    private Spinner spAvatraSourceSelect;

    private TextView tvUserPhone;
    private EditText etUserPhone;

    private TextView tvPassword;
    private EditText etPassword;
    private ColorStateList edTextdefaultColor;

    private ImageView ivRedStarPasswordConfirm;
    private TextView tvPasswordConfirm;
    private EditText etPasswordConfirm;

    private TextView tvUserName;
    private EditText etUserName;

    private TextView tvUserBirth;
    private TextView etUserBirth;
    private TextView tvUserBirthDivider;
    private DatePickerDialog datePickerDialog;
    private DatePickerDialog.OnDateSetListener dateSetListener;

    private Button btnLogInOut;
    private Button btUserChangConfrim;

    private UserAccount userAccount;

    private enum ProcModeEnum { LOGIN, REGISTER, LOGOUT } // ????????????????????????
    private ProcModeEnum procMode;

    private int rndIntTemp = 0;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        // ??????????????????????????????
        Common.setBackArrow(true, activity);
        setHasOptionsMenu(true);
        navController = Navigation.findNavController(activity, R.id.mainFragment);
    }

    // ??????????????????OptionMenu??????
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
//        inflater.inflate(R.menu.appbar_menu,menu);  // ???res????????????????????????R.menu.my_menu???
//        super.onCreateOptionsMenu(menu, inflater);
    }
    // ??????????????????OptionMenu??????
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
        activity.setTitle(R.string.title_of_userdata);
        return inflater.inflate(R.layout.fragment_user_data_setup, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // ????????????????????????
        ivAvatar = view.findViewById(R.id.ivAvatar);
        ivAvatar.setImageResource(R.drawable.ic_awesome_user_circle);

        // ????????????
//        view.findViewById(R.id.btImgCamera).setOnClickListener(this);
        btImgCamera = view.findViewById(R.id.btImgCamera);
        // ??????????????????ContextMenu?????? ?????? ??? ????????????
        registerForContextMenu(btImgCamera);
        btImgCamera.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN){
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        btImgCamera.showContextMenu(event.getX(), event.getY());
                    } else {
                        btImgCamera.showContextMenu();
                    }
                }
                return false;
            }
        });

        tvUserPhone = view.findViewById(R.id.tvUsManageUserPhone);
        etUserPhone = view.findViewById(R.id.etUsManageUserPhone);
//        etUserPhone.setOnFocusChangeListener(this);
        etUserPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                etUserPhone.setHintTextColor(getResources().getColor(R.color.colorTextHint));
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        tvPassword = view.findViewById(R.id.tvPassword);
        etPassword = view.findViewById(R.id.etPassword);
        edTextdefaultColor =  etPassword.getTextColors();
//        etPassword.setOnFocusChangeListener(this);
        etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                etPassword.setTextColor(edTextdefaultColor);
                etPassword.setHintTextColor(getResources().getColor(R.color.colorTextHint));
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        ivRedStarPasswordConfirm = view.findViewById(R.id.ivRedStarPasswordConfirm);
        tvPasswordConfirm = view.findViewById(R.id.tvPasswordConfirm);
        etPasswordConfirm = view.findViewById(R.id.etPasswordConfirm);
//        etPasswordConfirm.setOnFocusChangeListener(this);
        etPasswordConfirm.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                etPasswordConfirm.setTextColor(edTextdefaultColor);
                etPasswordConfirm.setHintTextColor(getResources().getColor(R.color.colorTextHint));
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        tvUserName = view.findViewById(R.id.tvUserName);
        etUserName = view.findViewById(R.id.etUserName);

        tvUserBirth = view.findViewById(R.id.tvUsManageArticleDate);
        etUserBirth = view.findViewById(R.id.tvManageArticleDate);
        tvUserBirthDivider = view.findViewById(R.id.tvUserBirthDivider);
        dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                // ???????????????0??????1????????????month + 1???????????????
                month++;
                String yyyyMMdd = new StringBuilder()
                        .append(year).append("-")
                        .append((month < 10 ? "0" + month : month)).append("-")
                        .append((dayOfMonth < 10 ? "0" + dayOfMonth : dayOfMonth))
                        .toString();etUserBirth.setText(yyyyMMdd);
                etUserBirth.setTextColor(edTextdefaultColor);
            }
        };

        etUserBirth.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN){
                    // ???????????????show()????????????
                    datePickerDialog.show();
                }
                return false;
            }
        });

        etUserBirth.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (etUserBirth.getText().equals("")) {
                    etUserBirth.setTextColor(getResources().getColor(R.color.colorTextHint));
                    etUserBirth.setText(getResources().getString(R.string.textUserBirth));
                } else {
                    etUserBirth.setTextColor(edTextdefaultColor);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });


        btnLogInOut = view.findViewById(R.id.btUsManageSearchArticle);
        btUserChangConfrim = view.findViewById(R.id.btUsManageCancel);
        // ??????????????????????????????
        if(getUserId() > 0) { // ???????????????
//            Common.showToast(activity, "??????????????????\n????????????\nUserId: " + getUserId());
            setUiIsLogin();
            procMode = ProcModeEnum.LOGIN; // ??????????????????
        } else { // ???????????????
//            Common.showToast(activity, "??????????????????\n????????????\nUserId: " + getUserId());
            setUiIsLogout();
            procMode = ProcModeEnum.LOGOUT; // ??????????????????
        }


        // ??????/??????/???????????? ??????
        view.findViewById(R.id.btUsManageSearchArticle).setOnClickListener(this);
        // ??????/????????????/???????????? ??????
        view.findViewById(R.id.btUsManageCancel).setOnClickListener(this);


        // vvvvvv???????????????????????????????????? ?????? ??? ??????
        // ??????????????? ??????
        view.findViewById(R.id.btTestLogin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String testUserDate = "2020-09-23 00:00:00";
                Log.d(TAG,"testUserDate: " + testUserDate);
                Timestamp testUserDate_TimeStamp = Timestamp.valueOf(testUserDate);
                Log.d(TAG,"testUserDate_TimeStamp: " + testUserDate_TimeStamp);
                Log.d(TAG,"testUserDate_TimeStamp: " + testUserDate_TimeStamp);

                String userPhone = "0900123456";
                String userPwd = "P@ssw0rd";
                if (etUserPhone.getText().toString().equals("0900123456")) {
                    userPhone = "0900222222";
                    userPwd = "222222";
                } else if (etUserPhone.getText().toString().equals("0900222222")) {
                    userPhone = "0900999999";
                    userPwd = "999999";
                }
                etUserPhone.setText(userPhone);
                etPassword.setText(userPwd);
            }
        });

        // ??????????????? ??????
        view.findViewById(R.id.btTestRegister).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SimpleDateFormat sdf = new SimpleDateFormat("MMddhhmmss");
                String testUserPhone = sdf.format(new Date());
                testUserPhone = "09" + String.valueOf(Integer.parseInt(testUserPhone.substring(3,4))+31) + testUserPhone.substring(4,10);
                etUserPhone.setText(testUserPhone);
                String testUserPwd = "P@ssw0rd";
                etPassword.setText(testUserPwd);
                etPasswordConfirm.setText(testUserPwd);

                java.util.Calendar calstart = java.util.Calendar.getInstance();
                calstart.setTime(new Date());
                calstart.add(Calendar.DAY_OF_WEEK, -1 * Integer.parseInt(testUserPhone.substring(8,10)));
                calstart.add(Calendar.YEAR, -1 * Integer.parseInt(testUserPhone.substring(8)));
                sdf.applyPattern("yyyy-MM-dd");
                String testUserBirth = sdf.format(calstart.getTime());
                etUserBirth.setText(testUserBirth);

                int[] images = {R.drawable.ic_add
                        ,R.drawable.x_sue
                        ,R.drawable.x_ivy
                        ,R.drawable.x_mary
                        ,R.drawable.x_cat
                        ,R.drawable.x_dog
                        ,R.drawable.x_mouse
                        ,R.drawable.logo_foodradar};
                Random rand = new Random();
                Integer rndInt = images[rand.nextInt(images.length)];
                view.findViewById(R.id.btTestRegister).setEnabled(false);
                while (rndInt.equals(rndIntTemp)) {
                    rndInt = images[rand.nextInt(images.length)];
                }
                view.findViewById(R.id.btTestRegister).setEnabled(true);
                rndIntTemp = rndInt;
                bitmapAvatra = BitmapFactory.decodeResource(getResources(),rndInt);
                ivAvatar.setImageBitmap(bitmapAvatra);
                String testUserName = getResources().getResourceEntryName(rndInt);
                testUserName = testUserName.substring(0,1).equals("x") ? testUserName.substring(2) : testUserName;
                testUserName += "_" + etUserPhone.getText();
                etUserName.setText(testUserName);
            }
        });
        // ^^^^^^???????????????????????????????????? ?????? ??? ??????


    }

    private int getUserId(){
        return Common.USER_ID;
    }

    private void setUI() {
        if(getUserId() > 0) { // ???????????????
            setUiIsLogin();
        } else { // ???????????????
            setUiIsLogout();
        }
    }

    private void setUiIsLogin() {
        // ?????????????????????(VISIBLE)???(INVISIBLE)??????
        setRegisterUI(VISIBLE);

        userAccount = Common.getUserLoin(activity);
        etUserPhone.setText(userAccount.getUserPhone());
        etUserPhone.setFocusable(false); // ????????????????????????
        etUserPhone.setFocusableInTouchMode(false); // ????????????????????????
        etPassword.setText(userAccount.getUserPwd());
        etPasswordConfirm.setText(userAccount.getUserPwd());
        etUserName.setText(userAccount.getUserName());
        String userBirth = new SimpleDateFormat("yyyy-MM-dd").format(userAccount.getUserBirth());
        etUserBirth.setText(userBirth);
        etUserBirth.setTextColor(edTextdefaultColor);
        setDatePicker();

        bitmapAvatra = new Common().getUserAvatra(activity);
//        Common.setUserAvatra(activity, bitmapAvatra);
        ivAvatar.setImageBitmap(bitmapAvatra);
//        ivAvatar.setImageResource(R.drawable.x_cat);

        btnLogInOut.setText(R.string.textLogout);
        btUserChangConfrim.setText(R.string.textUserChangConfrim);

        // ????????????????????????BottomNavigationView????????????????????????????????? ?????????ID??? ??? "????????????" ?????????
//            BottomNavigationView bottomNavigationView =  activity.findViewById(R.id.BottomNavigation);
//            bottomNavigationView.getMenu().getItem(4).setTitle(String.valueOf(USER_ID));
        new Common().setBottomNavigationViewBadge(activity,4,String.valueOf(getUserId()),true);


    }

    private void setUiIsLogout() {
        // ?????????????????????(VISIBLE)???(INVISIBLE)??????
        setRegisterUI(INVISIBLE);

        etUserPhone.setText("");
        etUserPhone.setFocusable(true); // ???????????????????????????
        etUserPhone.requestFocus(); // ???????????????????????????
        etUserPhone.setFocusableInTouchMode(true); // ???????????????????????????

        etPassword.setText("");
        etPasswordConfirm.setText("");

        btnLogInOut.setText(R.string.action_sign_in);
        btUserChangConfrim.setText(R.string.action_register);
        bitmapAvatra = new Common().getUserAvatra(activity);
        ivAvatar.setImageBitmap(bitmapAvatra);

        // ????????????????????????BottomNavigationView????????????????????????????????? ?????????ID??? ??? "????????????" ?????????
//            BottomNavigationView bottomNavigationView =  activity.findViewById(R.id.BottomNavigation);
//            bottomNavigationView.getMenu().getItem(4).setTitle(String.valueOf(USER_ID));
//        new Common().setBottomNavigationViewBadge(activity,4,String.valueOf(getUserId()),false);

    }

    private void setDatePicker() {
        Calendar calendar = Calendar.getInstance();
//        calendar.setTime(new Date());
        int year = calendar.get(Calendar.YEAR) - 12; // ?????????????????????12???
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        if (!etUserBirth.getText().equals("")
                && !etUserBirth.getText().equals(getResources().getString(R.string.textUserBirth))) {

            //????????????????????????
            String dateString = etUserBirth.getText().toString();
            //??????????????????
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            //????????????
            Date dateBirth = new Date();
            try {
                dateBirth = sdf.parse(dateString);
            } catch (ParseException e) {
                Log.d(TAG,"DateParse: Exeception");
                e.printStackTrace();
            }
            calendar.setTime(dateBirth);
            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH);
            day = calendar.get(Calendar.DAY_OF_MONTH);
        }

        datePickerDialog = new DatePickerDialog(activity, DatePickerDialog.THEME_HOLO_LIGHT, dateSetListener, year, month, day);
        DatePicker datePicker = datePickerDialog.getDatePicker();

        // ?????????????????????????????????130???
        Calendar calendarMin = Calendar.getInstance();
        calendarMin.add(Calendar.YEAR, -100);
        datePicker.setMinDate(calendarMin.getTimeInMillis());
        // ??????????????????????????????????????????
        Calendar calendarMax = Calendar.getInstance();
        calendarMax.add(Calendar.MONTH, -1);
        datePicker.setMaxDate(calendarMax.getTimeInMillis());
    }

    // ?????????????????????(VISIBLE)???(INVISIBLE)??????
    private void setRegisterUI(Integer visibStaus) {

        Resources res = getResources();

        etUserPhone.setFocusable(true); // ???????????????????????????
        etUserPhone.requestFocus(); // ???????????????????????????
        etUserPhone.setFocusableInTouchMode(true); // ???????????????????????????

        etUserPhone.setText("");
        etUserPhone.setHintTextColor(res.getColor(R.color.colorTextHint));

        etPassword.setText("");
        etPassword.setHintTextColor(res.getColor(R.color.colorTextHint));
        etPassword.setTextColor(edTextdefaultColor);

        ivRedStarPasswordConfirm.setVisibility(visibStaus);
        tvPasswordConfirm.setVisibility(visibStaus);
        etPasswordConfirm.setText("");
        etPasswordConfirm.setHintTextColor(res.getColor(R.color.colorTextHint));
        etPasswordConfirm.setTextColor(edTextdefaultColor);
        etPasswordConfirm.setVisibility(visibStaus);

        tvUserName.setVisibility(visibStaus);
        etUserName.setText("");
        etUserName.setVisibility(visibStaus);

        tvUserBirth.setVisibility(visibStaus);
        etUserBirth.setText("????????????");
        etUserBirth.setTextColor(getResources().getColor(R.color.colorTextHint));
        etUserBirth.setVisibility(visibStaus);
        tvUserBirthDivider.setVisibility(visibStaus);
        setDatePicker();

        btImgCamera.setVisibility(visibStaus);

        if (visibStaus == VISIBLE) {
            btnLogInOut.setText(R.string.textRegisterSend);
            btUserChangConfrim.setText(R.string.textRegisterCancel);
        } else {
            btnLogInOut.setText(R.string.action_sign_in);
            btUserChangConfrim.setText(R.string.action_register);
            ivAvatar.setImageResource(R.drawable.ic_awesome_user_circle);
        }

    }

    private boolean checkInput(ProcModeEnum chkProcMode) {
//        Log.d("TAG_checkInput", "checkInput: " + chkProcMode);

        Boolean bolResult = true;

        String strToast = "";
        Resources res = getResources();

        // ?????????
        String strUserPhone = etUserPhone.getText().toString().trim();
        etUserPhone.setText(strUserPhone);
        String strPassword = etPassword.getText().toString().trim();
        etPassword.setText(strPassword);

        if (strUserPhone.equals("")) {
            strToast += ((strToast == "") ? "" : "\n") + tvUserPhone.getText() + space(1) + res.getString(R.string.textMustInput);
//            Common.showToast(activity,tvUserPhone.getText() + "???????????????");
//            tvUserPhone.setTextColor(getResources().getColor(R.color.mainPink));
            etUserPhone.setHintTextColor(res.getColor(R.color.mainPink));
//            etUserPhone.setHint(getResources().getString(R.string.textPlsInpup) + getResources().getString(R.string.textUserPhone));
            bolResult = false;
        }

        if (strPassword.equals("")) {
            strToast += ((strToast == "") ? "" : "\n") + tvPassword.getText() + space(1) + res.getString(R.string.textMustInput);
            etPassword.setHintTextColor(res.getColor(R.color.mainPink));
            bolResult = false;
        }

        String strPasswordConfirm = etPasswordConfirm.getText().toString().trim();
        etPasswordConfirm.setText(strPasswordConfirm);
        // ???????????? ??? ???????????????(?????????????????????)????????????????????????????????????
        if (chkProcMode.equals(ProcModeEnum.REGISTER) || chkProcMode.equals(ProcModeEnum.LOGIN)) {
//            Log.d("TAG","etPassword: " + strPassword);
//            Log.d("TAG","etPasswordConfirm: " + strPasswordConfirm);
            if(strPasswordConfirm.equals("")) {
                strToast += ((strToast == "") ? "" : "\n") + tvPasswordConfirm.getText() + space(1) + res.getString(R.string.textMustInput);
                etPasswordConfirm.setHintTextColor(res.getColor(R.color.mainPink));
                bolResult = false;
            } else if (!strPasswordConfirm.equals(strPassword)) {
                strToast += ((strToast == "") ? "" : "\n") + res.getString(R.string.textPasswordConfirmErr);
                etPassword.setTextColor(res.getColor(R.color.mainPink));
//                etPasswordConfirm.setHintTextColor(res.getColor(R.color.mainPink));
                etPasswordConfirm.setTextColor(res.getColor(R.color.mainPink));
                bolResult = false;
            }

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

    // ????????????
    private boolean userRegister() {

        if (checkInput(procMode) == false) { return false; }

        String userPhone = etUserPhone.getText().toString();
        String userPwd = etPassword.getText().toString();
        String userBirth = etUserBirth.getText().toString();
//        Log.d(TAG,"userBirth: " + userBirth);
        Timestamp userBirth_Timestamp = Timestamp.valueOf(userBirth + " 00:00:00");
//        Log.d(TAG,"userBirth_Timestamp-1: " + userBirth_Timestamp);
//        if (userBirth == null) {
//            userBirth_Timestamp = Timestamp.valueOf(userBirth);
//            Log.d(TAG,"userBirth_Timestamp-2: " + userBirth_Timestamp);
//        }
        String userName = etUserName.getText().toString();
        Boolean allowNotifi_Boolean = true;

        userAccount = new UserAccount(userPhone, userPwd, userBirth_Timestamp, userName , allowNotifi_Boolean);

        // vvvvvv ??????????????????GSON????????????Servlet?????????????????????????????????????????????????????????????????????????????????????????????
        Gson gson =  new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").create();
        // ^^^^^^ ??????????????????GSON????????????Servlet?????????????????????????????????????????????????????????????????????????????????????????????

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("action", "userAccountRegister");
        // vvvvvv ???????????????????????????????????????????????????
        // jsonObject.addProperty("userAccount", new Gson().toJson(userAccount));
        // ^^^^^^ ???????????????????????????????????????????????????

        // vvvvvv ??????????????????GSON????????????Servlet?????????????????????????????????????????????????????????????????????????????????????????????
        jsonObject.addProperty("userAccount", gson.toJson(userAccount));
        // ^^^^^^ ??????????????????GSON????????????Servlet?????????????????????????????????????????????????????????????????????????????????????????????
        // ???????????????
        if (bitmapAvatra != null) {
            jsonObject.addProperty("imageBase64", Base64.encodeToString(Common.bitmapToByte(bitmapAvatra), Base64.DEFAULT));
        }
        int count = 0;
        try {
            String result = new CommonTask(USERACCOUNT_SERVLET, jsonObject.toString()).execute().get(); // Insert???????????????????????????????????????
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

    // ????????????
    private boolean userDataModify() {

        if (checkInput(procMode) == false) { return false; }

        Integer userId = getUserId();
        String userPhone = etUserPhone.getText().toString();
        String userPwd = etPassword.getText().toString();
        String userBirth = etUserBirth.getText().toString();
        Timestamp userBirth_Timestamp = Timestamp.valueOf(userBirth + " 00:00:00");
        String userName = etUserName.getText().toString();
        Boolean allowNotifi_Boolean = new Common().getUserAllowNotifi(activity);;

        userAccount = new UserAccount(userId, userPhone, userPwd, userBirth_Timestamp, userName, allowNotifi_Boolean);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("action", "userAccountModify");
//        jsonObject.addProperty("userAccount", new Gson().toJson(userAccount));
        // vvvvvv ??????????????????GSON????????????Servlet?????????????????????????????????????????????????????????????????????????????????????????????
        jsonObject.addProperty("userAccount", new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").create().toJson(userAccount));

        bitmapAvatra = new Common().getImageView(ivAvatar);
        // ???????????????
        if (bitmapAvatra != null) {
            jsonObject.addProperty("imageBase64", Base64.encodeToString(Common.bitmapToByte(bitmapAvatra), Base64.DEFAULT));
        }
        int count = 0;
        try {
            String result = new CommonTask(USERACCOUNT_SERVLET, jsonObject.toString()).execute().get(); // Insert???????????????????????????????????????
            count = Integer.parseInt(result);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
        if (count == 0) {
            return false;
        } else {
            Common.userLogin(activity, userPhone, userPwd);
//            new Common().setPreferences(activity, userAccount);
//            Common.setUserAvatra(activity, bitmapAvatra);
            return true;
        }
    }

    @Override
    public void onClick(View v) {
        // ????????????????????????????????????
        if (!Common.networkConnected(activity)) {
            Common.showToast(activity,R.string.textNetworkConnectedFail);
            return;
        }

        switch (v.getId()){
            // ??????/?????? ??????
            case R.id.btUsManageSearchArticle:

                if(procMode.equals(ProcModeEnum.LOGIN)) { // ??????
                    logoutWithConfirm();

                } else if(procMode.equals(ProcModeEnum.LOGOUT)) { // ??????
                        if (checkInput(procMode) == false) { return; }
                        int logInResult = Common.userLogin(activity, etUserPhone.getText().toString(), etPassword.getText().toString());
                        if (logInResult <=0){
                            Common.showToast(activity,"????????????");
                        } else {
                            setUI();
                            procMode = ProcModeEnum.LOGIN;
                            Common.showToast(activity,"????????????");
                        }

                } else if(procMode.equals(ProcModeEnum.REGISTER)) { //??????????????????
//                        Log.d(TAG,"????????????: " + activity);
                        Common.showToast(activity,"????????????");
                    if (userRegister()) {
                        Common.showToast(activity, "????????????");
                        Common.showToast(activity, "???????????????");
                        setUiIsLogout(); // ??????????????????????????????????????????
                        procMode = ProcModeEnum.LOGOUT;
                    } else {
                        Common.showToast(activity, "????????????");
                    }

                }
                break;

            // ???????????? / ??????(??????)?????? ??????
            case R.id.btUsManageCancel:
                if(procMode.equals(ProcModeEnum.LOGOUT)) { // ????????????????????????????????????
                    // ?????????????????????(VISIBLE)???(INVISIBLE)??????
                    procMode = ProcModeEnum.REGISTER;
                    setRegisterUI(VISIBLE);
//                    Common.showToast(activity, R.string.action_register);

                } else if (procMode.equals(ProcModeEnum.REGISTER)) { // ????????????????????????????????????
                    procMode = ProcModeEnum.LOGOUT;
                    // ?????????????????????(VISIBLE)???(INVISIBLE)??????
                    setRegisterUI(INVISIBLE);
                    Common.showToast(activity, R.string.textRegisterCancel);

                } else if(procMode.equals(ProcModeEnum.LOGIN)){ // ??????????????????????????????????????????
                    Common.showToast(activity,"????????????");
                    if (userDataModify()) {
                        Common.showToast(activity, "????????????");
//                        Common.showToast(activity, "???????????????");
//                        setUiIsLogout(); // ??????????????????????????????????????????
//                        procMode = ProcModeEnum.LOGOUT;

//                        BitmapDrawable drawable = (BitmapDrawable) ivAvatar.getDrawable();
//                        Bitmap bitmap = drawable.getBitmap();
//                        Common.setUserAvatra(activity, bitmap);
                        new Common().setUserAvatra(activity, new Common().getImageView(ivAvatar));

//                    ivAvatar.setImageBitmap(new Common().getUserAvatra());
//                    bitmapAvatra = showUserAvatra();
                        bitmapAvatra = new Common().getUserAvatra(activity);
                        ivAvatar.setImageBitmap(bitmapAvatra);
//                        Common.showToast(activity,"??????????????????");
                        Log.d(TAG,"???????????? ??? ??????????????????");
                    } else {
                        Common.showToast(activity, "????????????");
                    }
                }
                break;

            // PopMenu
//            case R.id.btImgCamera:
//                PopupMenu popupMenu = new PopupMenu(activity, v);
//                popupMenu.inflate(R.menu.photo_source_menu);
//                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//                    /* ???????????????????????????onMenuItemClick()???????????????????????????????????????item??? */
//                    @Override
//                    public boolean onMenuItemClick(MenuItem item) {
//                        switch (item.getItemId()) {
//                            case R.id.menuItemTakePicture:
//                            case R.id.menuItemPickPicture: // ??????????????????
//                            default:
//                                Common.showToast(activity,"id: " + item.getItemId());
//                                Common.showToast(activity,"id: " + getResources().getResourceEntryName(item.getItemId()));
//                                break;
//                        }
//                        return true;
//                    }
//                });
//                popupMenu.show(); // PopupMenu?????????show??????????????????
//                break;

            default:
                break;

        }
    }


//    @Override
//    public void onFocusChange(View v, boolean hasFocus) {
//        switch (v.getId()) {
//            case R.id.etUserPhone:
////                if (hasFocus) {
//                    etUserPhone.setHintTextColor(getResources().getColor(R.color.colorTextHint));
////                    etUserPhone.setHint(R.string.textUserPhone);
////                }
//                break;
//            case R.id.etPassword:
////                if (hasFocus) {
//                    etPassword.setTextColor(edTextdefaultColor);
//                    etPassword.setHintTextColor(getResources().getColor(R.color.colorTextHint));
////                    etPassword.setHint(R.string.textUserPassword);
////                }
//                break;
//            case R.id.etPasswordConfirm:
////                if (hasFocus) {
//                    etPasswordConfirm.setTextColor(edTextdefaultColor);
//                    etPasswordConfirm.setHintTextColor(getResources().getColor(R.color.colorTextHint));
////                    etPasswordConfirm.setHint(R.string.textPasswordConfirm);
////                }
//                break;
//            default:
//                break;
//        }
//
//    }

    @Override
    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v, @Nullable ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = activity.getMenuInflater();
        inflater.inflate(R.menu.photo_source_menu, menu);

    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            // ????????????
            case R.id.menuItemTakePicture:
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                // ??????????????????
                File dir = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                if (dir != null && !dir.exists()) {
                    if (!dir.mkdirs()) {
                        break;
                    }
                }
                file = new File(dir, "avatar_" + getUserId() + ".jpg");
                if (!file.equals(null)) {
                    contentUri = FileProvider.getUriForFile(
                            activity, activity.getPackageName() + ".provider", file);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);
                }

                if (intent.resolveActivity(activity.getPackageManager()) != null) {
                    startActivityForResult(intent, REQ_TAKE_PICTURE);
                } else {
                    Common.showToast(activity, R.string.textNoCameraApp);
                }
                break;
            // ??????????????????
            case R.id.menuItemPickPicture:
                Intent intentPickPhoto = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                if (intentPickPhoto.resolveActivity(activity.getPackageManager()) != null) {
                    startActivityForResult(intentPickPhoto, REQ_PICK_PICTURE);
                } else {
                    Common.showToast(activity, R.string.textNoImagePickerApp);
                }
                break;
            default:
                break;
        }
        return super.onContextItemSelected(item);
    }

    // ?????????????????????
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQ_TAKE_PICTURE:
                    crop(contentUri);
                    break;
                case REQ_PICK_PICTURE:
                    crop(intent.getData());
                    break;
                case REQ_CROP_PICTURE:
                    handleCropResult(intent);
                    break;
            }
        }
    }

    private void crop(Uri sourceImageUri) {
        File file = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        file = new File(file, "picture_cropped.jpg");
        Uri destinationUri = Uri.fromFile(file);
        UCrop.of(sourceImageUri, destinationUri)
//                .withAspectRatio(16, 9) // ??????????????????
//                .withMaxResultSize(500, 500) // ??????????????????????????????????????????
                .start(activity, this, REQ_CROP_PICTURE);
    }

    @SuppressLint("LongLogTag")
    private void handleCropResult(Intent intent) {
        Uri resultUri = UCrop.getOutput(intent);
        if (resultUri == null) {
            return;
        }

        try {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
                bitmapAvatra = BitmapFactory.decodeStream(
                        activity.getContentResolver().openInputStream(resultUri));
            } else {
                ImageDecoder.Source source =
                        ImageDecoder.createSource(activity.getContentResolver(), resultUri);
                bitmapAvatra = ImageDecoder.decodeBitmap(source);
            }
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            bitmapAvatra.compress(Bitmap.CompressFormat.JPEG, 100, out);
        } catch (IOException e) {
            Log.d(TAG, e.toString());
        }

        if (bitmapAvatra != null) {
            ivAvatar.setImageBitmap(bitmapAvatra);
        } else {
            ivAvatar.setImageResource(R.drawable.ic_awesome_user_circle);
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        askExternalStoragePermission();

//        // ?????????????????????????????????
//        if(getUserId() > 0) { // ???????????????
////            Common.showToast(activity, "??????????????????\n????????????\nUserId: " + getUserId());
//            setUiIsLogin();
//            procMode = ProcModeEnum.LOGIN; // ??????????????????
//
//        } else { // ???????????????
////            Common.showToast(activity, "??????????????????\n????????????\nUserId: " + getUserId());
//            setUiIsLogout();
//            procMode = ProcModeEnum.LOGOUT; // ??????????????????
//        }
    }


    private void askExternalStoragePermission() {
        String[] permissions = {
                Manifest.permission.READ_EXTERNAL_STORAGE
        };

        int result = ContextCompat.checkSelfPermission(activity, permissions[0]);
        if (result == PackageManager.PERMISSION_DENIED) {
            requestPermissions(permissions, PER_EXTERNAL_STORAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @androidx.annotation.NonNull String[] permissions,
                                           @androidx.annotation.NonNull int[] grantResults) {
        if (requestCode == PER_EXTERNAL_STORAGE) {
            // ??????user????????????????????????????????????????????????????????????????????????????????????disable
            if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                Common.showToast(activity,"?????????????????????????????????");
                bolAccessExternalStorage = false;
            } else {
                bolAccessExternalStorage = true;
            }
        }
    }


    // ?????????????????????????????????????????????
    private void logoutWithConfirm() {
        /* ??????positive???negative????????????????????????????????????????????? */
        AlertDialog.OnClickListener listener = new DialogInterface.OnClickListener() {
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
                        /* ?????????????????? */
                        dialog.cancel();
                        break;
                }
            }
        };

        new AlertDialog.Builder(activity)
                /* ???????????? */
                .setTitle(R.string.textLogout)
                /* ???????????? */
                .setIcon(R.drawable.logo_foodradar)
                /* ?????????????????? */
                .setMessage("?????????????????????")
                /* ??????positive???negative????????????????????????????????????????????? */
                .setPositiveButton("??????????????????", listener)
                .setNegativeButton(R.string.textDontWantLogout, listener)
                // ?????????????????????????????????????????????????????????true??????false????????????????????????????????????
                .setCancelable(true)
                .create()
                .show();
    }

}