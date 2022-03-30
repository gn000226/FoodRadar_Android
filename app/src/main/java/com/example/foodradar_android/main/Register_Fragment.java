package com.example.foodradar_android.main;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.foodradar_android.Common;
import com.example.foodradar_android.R;
import com.example.foodradar_android.task.CommonTask;
import com.example.foodradar_android.user.UserAccount;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class Register_Fragment extends Fragment implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    private static final String TAG = "TAG_Register_Fragment";

    private FragmentActivity activity;
    private NavController navController;
    private EditText userPhone, userName, userPwd;
    private TextView tvDateTime;
    private Calendar calendar;
    private static int year, month, day, hour, minute;
    private CommonTask RegisterGetAllTask;
    private CommonTask RegisterDeleteTask;
    private Button registerOk, regCancel, btDatePicker;
    private ImageView ivAvatar;
    private byte[] image;
    private int userId;
    private Uri contentUri;
//    public static Register_Fragment newInstance(String param1, String param2) {
//        Register_Fragment fragment = new Register_Fragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
//        return fragment;
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        new Common().setBackArrow(true, activity);

        setHasOptionsMenu(true);
        navController =
                Navigation.findNavController(activity, R.id.mainFragment);


//        if (getArguments() != null) {
//            userPhone = getArguments().getString(userPhone);
//            userPwd = getArguments().getString(userPwd);
//            userName = getArguments().getString(userName);
//            userBirth = getArguments().getBoolean(userBirth);
//        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity.setTitle(R.string.action_register);
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final NavController navController = Navigation.findNavController(view);
//        final Register register = registers.get(position);
//        String url = Common.URL_SERVER + "UserAccountServlet";
//        int id = UserAccount.getUserId();
        view.findViewById(R.id.linearLayout);
        view.findViewById(R.id.ivAvatar);
        userPhone = view.findViewById(R.id.userPhone);
        userPwd = view.findViewById(R.id.userpwd);
        userName = view.findViewById(R.id.userName);
        tvDateTime = view.findViewById(R.id.tvDateTime);
        registerOk = view.findViewById(R.id.registerok);
        regCancel = view.findViewById(R.id.regcancel);
        btDatePicker = view.findViewById(R.id.btDatePicker);
        showNow();
//        registers = getRegisters();
//        showRegisters(registers);

        btDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog =
                        new DatePickerDialog(
                                activity, Register_Fragment.this,
                                Register_Fragment.year, Register_Fragment.month, Register_Fragment.day);
                // 取得DatePicker物件方可設定可選取的日期區間
                DatePicker datePicker = datePickerDialog.getDatePicker();
                // 設定可選取的開始日為今日
                java.util.Calendar calendar = java.util.Calendar.getInstance();
                datePicker.setMaxDate(calendar.getTimeInMillis());
                // 設定可選取的結束日為一個月後
                calendar.add(java.util.Calendar.MONTH, 1);
                datePicker.setMaxDate(calendar.getTimeInMillis());
                // 最後要呼叫show()方能顯示
                datePickerDialog.show();
            }

        });


        registerOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userphone = userPhone.getText().toString().trim();
                if (userphone.length() <= 0 && userphone.length() >= 10) {
                    Common.showToast(activity, "手機號碼長度不正確");
                    return;
                }
                String userpwd = userPwd.getText().toString().trim();
                if (userpwd.length() <= 4) {
                    Common.showToast(activity, "密碼長度不可低於4位數");
                }
                String username = userName.getText().toString().trim();
                String strUserBirth = tvDateTime.getText().toString().trim();
                strUserBirth += " 00:00:00";

//                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
//                Calendar c = Calendar.getInstance();
//                strUserBirth = formatter.format(c.getTime());
                Log.d(TAG,"strUserBirth" + strUserBirth);
//                String strUserBirth = "yyyy-MM-dd 00:00:00";
                Timestamp tsmpUserBirth = Timestamp.valueOf(strUserBirth);
                if (Common.networkConnected(activity)) {
                    String url = Common.URL_SERVER + "UserAccountServlet";
                    UserAccount userAccount  = new UserAccount(userphone, userpwd, tsmpUserBirth, username);
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("action", "userAccountRegister");
                    jsonObject.addProperty("userAccount", new Gson().toJson(userAccount) + "");
//                    // 有圖才上傳
//                    if (image != null) {
//                        jsonObject.addProperty("imageBase64", Base64.encodeToString(image, Base64.DEFAULT));
//                    }
                    int count = 0;
                    try {
                        String result = new CommonTask(url, jsonObject.toString()).execute().get();
                        Log.d(TAG, "result=> " + result + " <=");
                        count = Integer.parseInt(result);
                    } catch (Exception e) {

                        Log.e(TAG, e.toString());
                    }
                    if (count == 0) {
                        Common.showToast(activity, R.string.textInsertFail);
                    } else {
                        Common.showToast(activity, R.string.textInsertSuccess);
                    }
                } else {
                    Common.showToast(activity, R.string.textNoNetwork);
                }
                /* 回前一個Fragment */
                navController.popBackStack();

            }
        });

        regCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* 回前一個Fragment */
                navController.popBackStack();
            }
        });

    }


    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Register_Fragment.year = year;
        Register_Fragment.month = month;
        Register_Fragment.day = day;
        updateDisplay();
    }

    private void showNow() {
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        year = calendar.get(java.util.Calendar.YEAR);
        month = calendar.get(java.util.Calendar.MONTH);
        day = calendar.get(java.util.Calendar.DAY_OF_MONTH);
//        hour = calendar.get(java.util.Calendar.HOUR_OF_DAY);
//        minute = calendar.get(java.util.Calendar.MINUTE);
        updateDisplay();
    }

    private void updateDisplay() {
        tvDateTime.setText(new StringBuilder().append(year).append("-")
                .append(pad(month + 1)).append("-").append(pad(day)));
//                .append(" ").append(pad(hour)).append(":")
//                .append(pad(minute)));
    }

    private String pad(int number) {
        if (number >= 10) {
            return String.valueOf(number);
        } else {
            return "0" + number;
        }
    }

//    private List<Register> getRegisters() {
//        List<Register> registers = null;
//        if (Common.networkConnected(activity)) {
//            String url = Common.URL_SERVER + "UserAccountServlet";
//            JsonObject jsonObject = new JsonObject();
//            jsonObject.addProperty("action", "getAll");
//            String jsonOut = jsonObject.toString();
//            RegisterGetAllTask = new CommonTask(url, jsonOut);
//            try {
//                String jsonIn = RegisterGetAllTask.execute().get();
//                Log.d("TAG_", "jsonIn: " + jsonIn);
//                Type listType = new TypeToken<List<Register>>() {
//                }.getType();
//                registers = new Gson().fromJson(jsonIn, listType);
//            } catch (Exception e) {
//                Log.e(TAG, e.toString());
//            }
//        } else {
//            Common.showToast(activity, R.string.textNoNetwork);
//        }
//        return registers;
//    }
//
//
//    private void showRegisters(List<Register> registers) {
//        if (registers == null || registers.isEmpty()) {
//            Common.showToast(activity, R.string.textNoMainFound);
//            //RegisterAdapter registerAdapter = (RegisterAdapter) linearLayout.getAdapter();
//        }
//    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        Register_Fragment.hour = hour;
        Register_Fragment.minute = minute;
        updateDisplay();

    }

}