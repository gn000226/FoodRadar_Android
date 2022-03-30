package com.example.foodradar_android.user;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.example.foodradar_android.Common;
import com.example.foodradar_android.R;
import com.example.foodradar_android.main.Category;
import com.example.foodradar_android.res.Res;
import com.example.foodradar_android.task.CommonTask;
import com.example.foodradar_android.task.ImageTask;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.yalantis.ucrop.UCrop;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import static android.app.Activity.RESULT_OK;


public class ResUpdateFragment extends Fragment {
    private NavController navController;
    private final static String TAG = "TAG_ResUpdateFragment";
    private FragmentActivity activity;
    private ImageView ivRes;
    private EditText etResName, etResAddress, etResTel;
    //星期一
    private Spinner spMonStartTime, spMonEndTime, spMonStartTime2, spMonEndTime2, spMonStartTime3, spMonEndTime3;
    private TextView tvTo, tvTo2, tvTo3;
    private Button btMonAddHours, btMonDeleteHours2, btMonAddHours2, btMonDeleteHours3;
    //星期二
    private Spinner spTueStartTime, spTueEndTime, spTueStartTime2, spTueEndTime2, spTueStartTime3, spTueEndTime3;
    private TextView tvTo4, tvTo5, tvTo6;
    private Button btTueAddHours, btTueDeleteHours2, btTueAddHours2, btTueDeleteHours3;
    //星期三
    private Spinner spWedStartTime, spWedEndTime, spWedStartTime2, spWedEndTime2, spWedStartTime3, spWedEndTime3;
    private TextView tvTo7, tvTo8, tvTo9;
    private Button btWedAddHours, btWedDeleteHours2, btWedAddHours2, btWedDeleteHours3;
    //星期四
    private Spinner spThuStartTime, spThuEndTime, spThuStartTime2, spThuEndTime2, spThuStartTime3, spThuEndTime3;
    private TextView tvTo10, tvTo11, tvTo12;
    private Button btThuAddHours, btThuDeleteHours2, btThuAddHours2, btThuDeleteHours3;
    //星期五
    private Spinner spFriStartTime, spFriEndTime, spFriStartTime2, spFriEndTime2, spFriStartTime3, spFriEndTime3;
    private TextView tvTo13, tvTo14, tvTo15;
    private Button btFriAddHours, btFriDeleteHours2, btFriAddHours2, btFriDeleteHours3;
    //星期六
    private Spinner spSatStartTime, spSatEndTime, spSatStartTime2, spSatEndTime2, spSatStartTime3, spSatEndTime3;
    private TextView tvTo16, tvTo17, tvTo18;
    private Button btSatAddHours, btSatDeleteHours2, btSatAddHours2, btSatDeleteHours3;
    //星期日
    private Spinner spSunStartTime, spSunEndTime, spSunStartTime2, spSunEndTime2, spSunStartTime3, spSunEndTime3;
    private TextView tvTo19, tvTo20, tvTo21;
    private Button btSunAddHours, btSunDeleteHours2, btSunAddHours2, btSunDeleteHours3;

    private Res res;
    private Map<String, Boolean> hoursVisibility;
    private JsonObject jsonHours;
    private Spinner spCategory;
    private Switch swResEnable;

    private byte[] image;
    private static final int REQ_TAKE_PICTURE = 0;
    private static final int REQ_PICK_PICTURE = 1;
    private static final int REQ_CROP_PICTURE = 2;
    private Uri contentUri;

    @Override
    public void onCreate(Bundle savedInstanceState) {
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
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
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
        super.onCreateView(inflater, container, savedInstanceState);
        activity.setTitle(R.string.titleResUpdate);
        return inflater.inflate(R.layout.fragment_res_update, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ivRes = view.findViewById(R.id.ivRes);
        etResName = view.findViewById(R.id.etResName);
        etResAddress = view.findViewById(R.id.etResAddress);
        etResTel = view.findViewById(R.id.etResTel);
        Bundle bundle = getArguments();
        if (bundle == null || bundle.getSerializable("res") == null) {
            Common.showToast(activity, R.string.textNoRessFound);
            navController.popBackStack();
            return;
        }
        res = (Res) bundle.getSerializable("res");
        jsonHours = JsonParser.parseString(res.getResHours()).getAsJsonObject();
        hoursVisibility = getHoursVisibility();

        ArrayAdapter<CharSequence> adapterWithRest = ArrayAdapter.createFromResource(activity, R.array.textHoursArrayWithRest, android.R.layout.simple_spinner_item);
        adapterWithRest.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(activity, R.array.textHoursArray, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spMonStartTime = view.findViewById(R.id.spMonStartTime);
        spMonEndTime = view.findViewById(R.id.spMonEndTime);
        spMonStartTime2 = view.findViewById(R.id.spMonStartTime2);
        spMonEndTime2 = view.findViewById(R.id.spMonEndTime2);
        spMonStartTime3 = view.findViewById(R.id.spMonStartTime3);
        spMonEndTime3 = view.findViewById(R.id.spMonEndTime3);

        tvTo = view.findViewById(R.id.tvTo);
        tvTo2 = view.findViewById(R.id.tvTo2);
        tvTo3 = view.findViewById(R.id.tvTo3);

        btMonAddHours = view.findViewById(R.id.btMonAddHours);
        btMonDeleteHours2 = view.findViewById(R.id.btMonDeleteHours2);
        btMonAddHours2 = view.findViewById(R.id.btMonAddHours2);
        btMonDeleteHours3 = view.findViewById(R.id.btMonDeleteHours3);

        String compareValue;
        int spinnerPosition;
        if (jsonHours.get("11") != null) {
            compareValue = jsonHours.get("11").getAsString().split("~")[0];
            spMonStartTime.setAdapter(adapterWithRest);
            spinnerPosition = adapterWithRest.getPosition(compareValue);
            spMonStartTime.setSelection(spinnerPosition);

            compareValue = jsonHours.get("11").getAsString().split("~")[1];
            spMonEndTime.setAdapter(adapter);
            spinnerPosition = adapter.getPosition(compareValue);
            spMonEndTime.setSelection(spinnerPosition);
        } else {
            spMonStartTime.setSelection(0, true);
            spMonEndTime.setVisibility(View.GONE);
            tvTo.setVisibility(View.GONE);
            btMonAddHours.setVisibility(View.GONE);
        }
        spMonStartTime.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    spMonEndTime.setVisibility(View.VISIBLE);
                    tvTo.setVisibility(View.VISIBLE);
                    btMonAddHours.setVisibility(View.VISIBLE);
                } else {
                    spMonEndTime.setVisibility(View.GONE);
                    tvTo.setVisibility(View.GONE);
                    btMonAddHours.setVisibility(View.GONE);

                    hoursVisibility.put("MonHours2", false);
                    btMonDeleteHours2.setVisibility(View.GONE);
                    spMonStartTime2.setVisibility(View.GONE);
                    tvTo2.setVisibility(View.GONE);
                    spMonEndTime2.setVisibility(View.GONE);
                    btMonAddHours2.setVisibility(View.GONE);

                    hoursVisibility.put("MonHours3", false);
                    btMonDeleteHours3.setVisibility(View.GONE);
                    spMonStartTime3.setVisibility(View.GONE);
                    tvTo3.setVisibility(View.GONE);
                    spMonEndTime3.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //點擊加時段出現第二列
        btMonAddHours.setOnClickListener(v -> {
            if (hoursVisibility.get("MonHours2") == false) {
                hoursVisibility.put("MonHours2", true);
                btMonDeleteHours2.setVisibility(View.VISIBLE);
                spMonStartTime2.setVisibility(View.VISIBLE);
                tvTo2.setVisibility(View.VISIBLE);
                spMonEndTime2.setVisibility(View.VISIBLE);
                btMonAddHours2.setVisibility(View.VISIBLE);
            }
        });

        //點擊刪時段，隱藏該列
        btMonDeleteHours2.setOnClickListener(v -> {
            if (hoursVisibility.get("MonHours2") == true) {
                hoursVisibility.put("MonHours2", false);
                btMonDeleteHours2.setVisibility(View.GONE);
                spMonStartTime2.setVisibility(View.GONE);
                tvTo2.setVisibility(View.GONE);
                spMonEndTime2.setVisibility(View.GONE);
                btMonAddHours2.setVisibility(View.GONE);
            }
        });

        btMonAddHours2.setOnClickListener(v -> {
            if (hoursVisibility.get("MonHours3") == false) {
                hoursVisibility.put("MonHours3", true);
                btMonDeleteHours3.setVisibility(View.VISIBLE);
                spMonStartTime3.setVisibility(View.VISIBLE);
                tvTo3.setVisibility(View.VISIBLE);
                spMonEndTime3.setVisibility(View.VISIBLE);
            }
        });

        btMonDeleteHours3.setOnClickListener(v -> {
            if (hoursVisibility.get("MonHours3") == true) {
                hoursVisibility.put("MonHours3", false);
                btMonDeleteHours3.setVisibility(View.GONE);
                spMonStartTime3.setVisibility(View.GONE);
                tvTo3.setVisibility(View.GONE);
                spMonEndTime3.setVisibility(View.GONE);
            }
        });

        if (jsonHours.get("12") != null) {
            btMonDeleteHours2.setVisibility(View.VISIBLE);
            spMonStartTime2.setVisibility(View.VISIBLE);
            tvTo2.setVisibility(View.VISIBLE);
            spMonEndTime2.setVisibility(View.VISIBLE);
            btMonAddHours2.setVisibility(View.VISIBLE);

            compareValue = jsonHours.get("12").getAsString().split("~")[0];
            spMonStartTime2.setAdapter(adapter);
            spinnerPosition = adapter.getPosition(compareValue);
            spMonStartTime2.setSelection(spinnerPosition);

            compareValue = jsonHours.get("12").getAsString().split("~")[1];
            spMonEndTime2.setAdapter(adapter);
            spinnerPosition = adapter.getPosition(compareValue);
            spMonEndTime2.setSelection(spinnerPosition);
        }

        if (jsonHours.get("13") != null) {
            btMonDeleteHours3.setVisibility(View.VISIBLE);
            spMonStartTime3.setVisibility(View.VISIBLE);
            tvTo3.setVisibility(View.VISIBLE);
            spMonEndTime3.setVisibility(View.VISIBLE);

            compareValue = jsonHours.get("13").getAsString().split("~")[0];
            spMonStartTime3.setAdapter(adapter);
            spinnerPosition = adapter.getPosition(compareValue);
            spMonStartTime3.setSelection(spinnerPosition);

            compareValue = jsonHours.get("13").getAsString().split("~")[1];
            spMonEndTime3.setAdapter(adapter);
            spinnerPosition = adapter.getPosition(compareValue);
            spMonEndTime3.setSelection(spinnerPosition);
        }

        //星期二
        spTueStartTime = view.findViewById(R.id.spTueStartTime);
        spTueEndTime = view.findViewById(R.id.spTueEndTime);
        spTueStartTime2 = view.findViewById(R.id.spTueStartTime2);
        spTueEndTime2 = view.findViewById(R.id.spTueEndTime2);
        spTueStartTime3 = view.findViewById(R.id.spTueStartTime3);
        spTueEndTime3 = view.findViewById(R.id.spTueEndTime3);

        tvTo4 = view.findViewById(R.id.tvTo4);
        tvTo5 = view.findViewById(R.id.tvTo5);
        tvTo6 = view.findViewById(R.id.tvTo6);

        btTueAddHours = view.findViewById(R.id.btTueAddHours);
        btTueDeleteHours2 = view.findViewById(R.id.btTueDeleteHours2);
        btTueAddHours2 = view.findViewById(R.id.btTueAddHours2);
        btTueDeleteHours3 = view.findViewById(R.id.btTueDeleteHours3);

        if (jsonHours.get("21") != null) {
            compareValue = jsonHours.get("21").getAsString().split("~")[0];
            spTueStartTime.setAdapter(adapterWithRest);
            spinnerPosition = adapterWithRest.getPosition(compareValue);
            spTueStartTime.setSelection(spinnerPosition);

            compareValue = jsonHours.get("21").getAsString().split("~")[1];
            spTueEndTime.setAdapter(adapter);
            spinnerPosition = adapter.getPosition(compareValue);
            spTueEndTime.setSelection(spinnerPosition);
        } else {
            spTueStartTime.setSelection(0, true);
            spTueEndTime.setVisibility(View.GONE);
            tvTo4.setVisibility(View.GONE);
            btTueAddHours.setVisibility(View.GONE);
        }

        spTueStartTime.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    spTueEndTime.setVisibility(View.VISIBLE);
                    tvTo4.setVisibility(View.VISIBLE);
                    btTueAddHours.setVisibility(View.VISIBLE);
                } else {
                    spTueEndTime.setVisibility(View.GONE);
                    tvTo4.setVisibility(View.GONE);
                    btTueAddHours.setVisibility(View.GONE);

                    hoursVisibility.put("TueHours2", false);
                    btTueDeleteHours2.setVisibility(View.GONE);
                    spTueStartTime2.setVisibility(View.GONE);
                    tvTo5.setVisibility(View.GONE);
                    spTueEndTime2.setVisibility(View.GONE);
                    btTueAddHours2.setVisibility(View.GONE);

                    hoursVisibility.put("TueHours3", false);
                    btTueDeleteHours3.setVisibility(View.GONE);
                    spTueStartTime3.setVisibility(View.GONE);
                    tvTo6.setVisibility(View.GONE);
                    spTueEndTime3.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //點擊加時段出現第二列
        btTueAddHours.setOnClickListener(v -> {
            if (hoursVisibility.get("TueHours2") == false) {
                hoursVisibility.put("TueHours2", true);
                btTueDeleteHours2.setVisibility(View.VISIBLE);
                spTueStartTime2.setVisibility(View.VISIBLE);
                tvTo5.setVisibility(View.VISIBLE);
                spTueEndTime2.setVisibility(View.VISIBLE);
                btTueAddHours2.setVisibility(View.VISIBLE);
            }
        });

        //點擊刪時段，隱藏該列
        btTueDeleteHours2.setOnClickListener(v -> {
            if (hoursVisibility.get("TueHours2") == true) {
                hoursVisibility.put("TueHours2", false);
                btTueDeleteHours2.setVisibility(View.GONE);
                spTueStartTime2.setVisibility(View.GONE);
                tvTo5.setVisibility(View.GONE);
                spTueEndTime2.setVisibility(View.GONE);
                btTueAddHours2.setVisibility(View.GONE);
            }
        });

        btTueAddHours2.setOnClickListener(v -> {
            if (hoursVisibility.get("TueHours3") == false) {
                hoursVisibility.put("TueHours3", true);
                btTueDeleteHours3.setVisibility(View.VISIBLE);
                spTueStartTime3.setVisibility(View.VISIBLE);
                tvTo6.setVisibility(View.VISIBLE);
                spTueEndTime3.setVisibility(View.VISIBLE);
            }
        });

        btTueDeleteHours3.setOnClickListener(v -> {
            if (hoursVisibility.get("TueHours3") == true) {
                hoursVisibility.put("TueHours3", false);
                btTueDeleteHours3.setVisibility(View.GONE);
                spTueStartTime3.setVisibility(View.GONE);
                tvTo6.setVisibility(View.GONE);
                spTueEndTime3.setVisibility(View.GONE);
            }
        });

        if (jsonHours.get("22") != null) {
            btTueDeleteHours2.setVisibility(View.VISIBLE);
            spTueStartTime2.setVisibility(View.VISIBLE);
            tvTo5.setVisibility(View.VISIBLE);
            spTueEndTime2.setVisibility(View.VISIBLE);
            btTueAddHours2.setVisibility(View.VISIBLE);

            compareValue = jsonHours.get("22").getAsString().split("~")[0];
            spTueStartTime2.setAdapter(adapter);
            spinnerPosition = adapter.getPosition(compareValue);
            spTueStartTime2.setSelection(spinnerPosition);

            compareValue = jsonHours.get("22").getAsString().split("~")[1];
            spTueEndTime2.setAdapter(adapter);
            spinnerPosition = adapter.getPosition(compareValue);
            spTueEndTime2.setSelection(spinnerPosition);
        }

        if (jsonHours.get("23") != null) {
            btTueDeleteHours3.setVisibility(View.VISIBLE);
            spTueStartTime3.setVisibility(View.VISIBLE);
            tvTo6.setVisibility(View.VISIBLE);
            spTueEndTime3.setVisibility(View.VISIBLE);

            compareValue = jsonHours.get("23").getAsString().split("~")[0];
            spTueStartTime3.setAdapter(adapter);
            spinnerPosition = adapter.getPosition(compareValue);
            spTueStartTime3.setSelection(spinnerPosition);

            compareValue = jsonHours.get("23").getAsString().split("~")[1];
            spTueEndTime3.setAdapter(adapter);
            spinnerPosition = adapter.getPosition(compareValue);
            spTueEndTime3.setSelection(spinnerPosition);
        }

        //星期三
        spWedStartTime = view.findViewById(R.id.spWedStartTime);
        spWedEndTime = view.findViewById(R.id.spWedEndTime);
        spWedStartTime2 = view.findViewById(R.id.spWedStartTime2);
        spWedEndTime2 = view.findViewById(R.id.spWedEndTime2);
        spWedStartTime3 = view.findViewById(R.id.spWedStartTime3);
        spWedEndTime3 = view.findViewById(R.id.spWedEndTime3);

        tvTo7 = view.findViewById(R.id.tvTo7);
        tvTo8 = view.findViewById(R.id.tvTo8);
        tvTo9 = view.findViewById(R.id.tvTo9);

        btWedAddHours = view.findViewById(R.id.btWedAddHours);
        btWedDeleteHours2 = view.findViewById(R.id.btWedDeleteHours2);
        btWedAddHours2 = view.findViewById(R.id.btWedAddHours2);
        btWedDeleteHours3 = view.findViewById(R.id.btWedDeleteHours3);

        if (jsonHours.get("31") != null) {
            compareValue = jsonHours.get("31").getAsString().split("~")[0];
            spWedStartTime.setAdapter(adapterWithRest);
            spinnerPosition = adapterWithRest.getPosition(compareValue);
            spWedStartTime.setSelection(spinnerPosition);

            compareValue = jsonHours.get("31").getAsString().split("~")[1];
            spWedEndTime.setAdapter(adapter);
            spinnerPosition = adapter.getPosition(compareValue);
            spWedEndTime.setSelection(spinnerPosition);
        } else {
            spWedStartTime.setSelection(0, true);
            spWedEndTime.setVisibility(View.GONE);
            tvTo7.setVisibility(View.GONE);
            btWedAddHours.setVisibility(View.GONE);
        }

        spWedStartTime.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    spWedEndTime.setVisibility(View.VISIBLE);
                    tvTo7.setVisibility(View.VISIBLE);
                    btWedAddHours.setVisibility(View.VISIBLE);
                } else {
                    spWedEndTime.setVisibility(View.GONE);
                    tvTo7.setVisibility(View.GONE);
                    btWedAddHours.setVisibility(View.GONE);

                    hoursVisibility.put("WedHours2", false);
                    btWedDeleteHours2.setVisibility(View.GONE);
                    spWedStartTime2.setVisibility(View.GONE);
                    tvTo8.setVisibility(View.GONE);
                    spWedEndTime2.setVisibility(View.GONE);
                    btWedAddHours2.setVisibility(View.GONE);

                    hoursVisibility.put("WedHours3", false);
                    btWedDeleteHours3.setVisibility(View.GONE);
                    spWedStartTime3.setVisibility(View.GONE);
                    tvTo9.setVisibility(View.GONE);
                    spWedEndTime3.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //點擊加時段出現第二列
        btWedAddHours.setOnClickListener(v -> {
            if (hoursVisibility.get("WedHours2") == false) {
                hoursVisibility.put("WedHours2", true);
                btWedDeleteHours2.setVisibility(View.VISIBLE);
                spWedStartTime2.setVisibility(View.VISIBLE);
                tvTo8.setVisibility(View.VISIBLE);
                spWedEndTime2.setVisibility(View.VISIBLE);
                btWedAddHours2.setVisibility(View.VISIBLE);
            }
        });

        //點擊刪時段，隱藏該列
        btWedDeleteHours2.setOnClickListener(v -> {
            if (hoursVisibility.get("WedHours2") == true) {
                hoursVisibility.put("WedHours2", false);
                btWedDeleteHours2.setVisibility(View.GONE);
                spWedStartTime2.setVisibility(View.GONE);
                tvTo8.setVisibility(View.GONE);
                spWedEndTime2.setVisibility(View.GONE);
                btWedAddHours2.setVisibility(View.GONE);
            }
        });

        btWedAddHours2.setOnClickListener(v -> {
            if (hoursVisibility.get("WedHours3") == false) {
                hoursVisibility.put("WedHours3", true);
                btWedDeleteHours3.setVisibility(View.VISIBLE);
                spWedStartTime3.setVisibility(View.VISIBLE);
                tvTo9.setVisibility(View.VISIBLE);
                spWedEndTime3.setVisibility(View.VISIBLE);
            }
        });

        btWedDeleteHours3.setOnClickListener(v -> {
            if (hoursVisibility.get("WedHours3") == true) {
                hoursVisibility.put("WedHours3", false);
                btWedDeleteHours3.setVisibility(View.GONE);
                spWedStartTime3.setVisibility(View.GONE);
                tvTo9.setVisibility(View.GONE);
                spWedEndTime3.setVisibility(View.GONE);
            }
        });

        if (jsonHours.get("32") != null) {
            btWedDeleteHours2.setVisibility(View.VISIBLE);
            spWedStartTime2.setVisibility(View.VISIBLE);
            tvTo8.setVisibility(View.VISIBLE);
            spWedEndTime2.setVisibility(View.VISIBLE);
            btWedAddHours2.setVisibility(View.VISIBLE);

            compareValue = jsonHours.get("32").getAsString().split("~")[0];
            spWedStartTime2.setAdapter(adapter);
            spinnerPosition = adapter.getPosition(compareValue);
            spWedStartTime2.setSelection(spinnerPosition);

            compareValue = jsonHours.get("32").getAsString().split("~")[1];
            spWedEndTime2.setAdapter(adapter);
            spinnerPosition = adapter.getPosition(compareValue);
            spWedEndTime2.setSelection(spinnerPosition);
        }

        if (jsonHours.get("33") != null) {
            btWedDeleteHours3.setVisibility(View.VISIBLE);
            spWedStartTime3.setVisibility(View.VISIBLE);
            tvTo9.setVisibility(View.VISIBLE);
            spWedEndTime3.setVisibility(View.VISIBLE);

            compareValue = jsonHours.get("33").getAsString().split("~")[0];
            spWedStartTime3.setAdapter(adapter);
            spinnerPosition = adapter.getPosition(compareValue);
            spWedStartTime3.setSelection(spinnerPosition);

            compareValue = jsonHours.get("33").getAsString().split("~")[1];
            spWedEndTime3.setAdapter(adapter);
            spinnerPosition = adapter.getPosition(compareValue);
            spWedEndTime3.setSelection(spinnerPosition);
        }

        //星期四
        spThuStartTime = view.findViewById(R.id.spThuStartTime);
        spThuEndTime = view.findViewById(R.id.spThuEndTime);
        spThuStartTime2 = view.findViewById(R.id.spThuStartTime2);
        spThuEndTime2 = view.findViewById(R.id.spThuEndTime2);
        spThuStartTime3 = view.findViewById(R.id.spThuStartTime3);
        spThuEndTime3 = view.findViewById(R.id.spThuEndTime3);

        tvTo10 = view.findViewById(R.id.tvTo10);
        tvTo11 = view.findViewById(R.id.tvTo11);
        tvTo12 = view.findViewById(R.id.tvTo12);

        btThuAddHours = view.findViewById(R.id.btThuAddHours);
        btThuDeleteHours2 = view.findViewById(R.id.btThuDeleteHours2);
        btThuAddHours2 = view.findViewById(R.id.btThuAddHours2);
        btThuDeleteHours3 = view.findViewById(R.id.btThuDeleteHours3);

        if (jsonHours.get("41") != null) {
            compareValue = jsonHours.get("41").getAsString().split("~")[0];
            spThuStartTime.setAdapter(adapterWithRest);
            spinnerPosition = adapterWithRest.getPosition(compareValue);
            spThuStartTime.setSelection(spinnerPosition);

            compareValue = jsonHours.get("41").getAsString().split("~")[1];
            spThuEndTime.setAdapter(adapter);
            spinnerPosition = adapter.getPosition(compareValue);
            spThuEndTime.setSelection(spinnerPosition);
        } else {
            spThuStartTime.setSelection(0, true);
            spThuEndTime.setVisibility(View.GONE);
            tvTo10.setVisibility(View.GONE);
            btThuAddHours.setVisibility(View.GONE);
        }

        spThuStartTime.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    spThuEndTime.setVisibility(View.VISIBLE);
                    tvTo10.setVisibility(View.VISIBLE);
                    btThuAddHours.setVisibility(View.VISIBLE);
                } else {
                    spThuEndTime.setVisibility(View.GONE);
                    tvTo10.setVisibility(View.GONE);
                    btThuAddHours.setVisibility(View.GONE);

                    hoursVisibility.put("ThuHours2", false);
                    btThuDeleteHours2.setVisibility(View.GONE);
                    spThuStartTime2.setVisibility(View.GONE);
                    tvTo11.setVisibility(View.GONE);
                    spThuEndTime2.setVisibility(View.GONE);
                    btThuAddHours2.setVisibility(View.GONE);

                    hoursVisibility.put("ThuHours3", false);
                    btThuDeleteHours3.setVisibility(View.GONE);
                    spThuStartTime3.setVisibility(View.GONE);
                    tvTo12.setVisibility(View.GONE);
                    spThuEndTime3.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //點擊加時段出現第二列
        btThuAddHours.setOnClickListener(v -> {
            if (hoursVisibility.get("ThuHours2") == false) {
                hoursVisibility.put("ThuHours2", true);
                btThuDeleteHours2.setVisibility(View.VISIBLE);
                spThuStartTime2.setVisibility(View.VISIBLE);
                tvTo11.setVisibility(View.VISIBLE);
                spThuEndTime2.setVisibility(View.VISIBLE);
                btThuAddHours2.setVisibility(View.VISIBLE);
            }
        });

        //點擊刪時段，隱藏該列
        btThuDeleteHours2.setOnClickListener(v -> {
            if (hoursVisibility.get("ThuHours2") == true) {
                hoursVisibility.put("ThuHours2", false);
                btThuDeleteHours2.setVisibility(View.GONE);
                spThuStartTime2.setVisibility(View.GONE);
                tvTo11.setVisibility(View.GONE);
                spThuEndTime2.setVisibility(View.GONE);
                btThuAddHours2.setVisibility(View.GONE);
            }
        });

        btThuAddHours2.setOnClickListener(v -> {
            if (hoursVisibility.get("ThuHours3") == false) {
                hoursVisibility.put("ThuHours3", true);
                btThuDeleteHours3.setVisibility(View.VISIBLE);
                spThuStartTime3.setVisibility(View.VISIBLE);
                tvTo12.setVisibility(View.VISIBLE);
                spThuEndTime3.setVisibility(View.VISIBLE);
            }
        });

        btThuDeleteHours3.setOnClickListener(v -> {
            if (hoursVisibility.get("ThuHours3") == true) {
                hoursVisibility.put("ThuHours3", false);
                btThuDeleteHours3.setVisibility(View.GONE);
                spThuStartTime3.setVisibility(View.GONE);
                tvTo12.setVisibility(View.GONE);
                spThuEndTime3.setVisibility(View.GONE);
            }
        });

        if (jsonHours.get("42") != null) {
            btThuDeleteHours2.setVisibility(View.VISIBLE);
            spThuStartTime2.setVisibility(View.VISIBLE);
            tvTo11.setVisibility(View.VISIBLE);
            spThuEndTime2.setVisibility(View.VISIBLE);
            btThuAddHours2.setVisibility(View.VISIBLE);

            compareValue = jsonHours.get("42").getAsString().split("~")[0];
            spThuStartTime2.setAdapter(adapter);
            spinnerPosition = adapter.getPosition(compareValue);
            spThuStartTime2.setSelection(spinnerPosition);

            compareValue = jsonHours.get("42").getAsString().split("~")[1];
            spThuEndTime2.setAdapter(adapter);
            spinnerPosition = adapter.getPosition(compareValue);
            spThuEndTime2.setSelection(spinnerPosition);
        }

        if (jsonHours.get("43") != null) {
            btThuDeleteHours3.setVisibility(View.VISIBLE);
            spThuStartTime3.setVisibility(View.VISIBLE);
            tvTo12.setVisibility(View.VISIBLE);
            spThuEndTime3.setVisibility(View.VISIBLE);

            compareValue = jsonHours.get("43").getAsString().split("~")[0];
            spThuStartTime3.setAdapter(adapter);
            spinnerPosition = adapter.getPosition(compareValue);
            spThuStartTime3.setSelection(spinnerPosition);

            compareValue = jsonHours.get("43").getAsString().split("~")[1];
            spThuEndTime3.setAdapter(adapter);
            spinnerPosition = adapter.getPosition(compareValue);
            spThuEndTime3.setSelection(spinnerPosition);
        }

        //星期五
        spFriStartTime = view.findViewById(R.id.spFriStartTime);
        spFriEndTime = view.findViewById(R.id.spFriEndTime);
        spFriStartTime2 = view.findViewById(R.id.spFriStartTime2);
        spFriEndTime2 = view.findViewById(R.id.spFriEndTime2);
        spFriStartTime3 = view.findViewById(R.id.spFriStartTime3);
        spFriEndTime3 = view.findViewById(R.id.spFriEndTime3);

        tvTo13 = view.findViewById(R.id.tvTo13);
        tvTo14 = view.findViewById(R.id.tvTo14);
        tvTo15 = view.findViewById(R.id.tvTo15);

        btFriAddHours = view.findViewById(R.id.btFriAddHours);
        btFriDeleteHours2 = view.findViewById(R.id.btFriDeleteHours2);
        btFriAddHours2 = view.findViewById(R.id.btFriAddHours2);
        btFriDeleteHours3 = view.findViewById(R.id.btFriDeleteHours3);

        if (jsonHours.get("51") != null) {
            compareValue = jsonHours.get("51").getAsString().split("~")[0];
            spFriStartTime.setAdapter(adapterWithRest);
            spinnerPosition = adapterWithRest.getPosition(compareValue);
            spFriStartTime.setSelection(spinnerPosition);

            compareValue = jsonHours.get("51").getAsString().split("~")[1];
            spFriEndTime.setAdapter(adapter);
            spinnerPosition = adapter.getPosition(compareValue);
            spFriEndTime.setSelection(spinnerPosition);
        } else {
            spFriStartTime.setSelection(0, true);
            spFriEndTime.setVisibility(View.GONE);
            tvTo13.setVisibility(View.GONE);
            btFriAddHours.setVisibility(View.GONE);
        }

        spFriStartTime.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    spFriEndTime.setVisibility(View.VISIBLE);
                    tvTo13.setVisibility(View.VISIBLE);
                    btFriAddHours.setVisibility(View.VISIBLE);
                } else {
                    spFriEndTime.setVisibility(View.GONE);
                    tvTo13.setVisibility(View.GONE);
                    btFriAddHours.setVisibility(View.GONE);

                    hoursVisibility.put("FriHours2", false);
                    btFriDeleteHours2.setVisibility(View.GONE);
                    spFriStartTime2.setVisibility(View.GONE);
                    tvTo14.setVisibility(View.GONE);
                    spFriEndTime2.setVisibility(View.GONE);
                    btFriAddHours2.setVisibility(View.GONE);

                    hoursVisibility.put("FriHours3", false);
                    btFriDeleteHours3.setVisibility(View.GONE);
                    spFriStartTime3.setVisibility(View.GONE);
                    tvTo15.setVisibility(View.GONE);
                    spFriEndTime3.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //點擊加時段出現第二列
        btFriAddHours.setOnClickListener(v -> {
            if (hoursVisibility.get("FriHours2") == false) {
                hoursVisibility.put("FriHours2", true);
                btFriDeleteHours2.setVisibility(View.VISIBLE);
                spFriStartTime2.setVisibility(View.VISIBLE);
                tvTo14.setVisibility(View.VISIBLE);
                spFriEndTime2.setVisibility(View.VISIBLE);
                btFriAddHours2.setVisibility(View.VISIBLE);
            }
        });

        //點擊刪時段，隱藏該列
        btFriDeleteHours2.setOnClickListener(v -> {
            if (hoursVisibility.get("FriHours2") == true) {
                hoursVisibility.put("FriHours2", false);
                btFriDeleteHours2.setVisibility(View.GONE);
                spFriStartTime2.setVisibility(View.GONE);
                tvTo14.setVisibility(View.GONE);
                spFriEndTime2.setVisibility(View.GONE);
                btFriAddHours2.setVisibility(View.GONE);
            }
        });

        btFriAddHours2.setOnClickListener(v -> {
            if (hoursVisibility.get("FriHours3") == false) {
                hoursVisibility.put("FriHours3", true);
                btFriDeleteHours3.setVisibility(View.VISIBLE);
                spFriStartTime3.setVisibility(View.VISIBLE);
                tvTo15.setVisibility(View.VISIBLE);
                spFriEndTime3.setVisibility(View.VISIBLE);
            }
        });

        btFriDeleteHours3.setOnClickListener(v -> {
            if (hoursVisibility.get("FriHours3") == true) {
                hoursVisibility.put("FriHours3", false);
                btFriDeleteHours3.setVisibility(View.GONE);
                spFriStartTime3.setVisibility(View.GONE);
                tvTo15.setVisibility(View.GONE);
                spFriEndTime3.setVisibility(View.GONE);
            }
        });

        if (jsonHours.get("52") != null) {
            btFriDeleteHours2.setVisibility(View.VISIBLE);
            spFriStartTime2.setVisibility(View.VISIBLE);
            tvTo14.setVisibility(View.VISIBLE);
            spFriEndTime2.setVisibility(View.VISIBLE);
            btFriAddHours2.setVisibility(View.VISIBLE);

            compareValue = jsonHours.get("52").getAsString().split("~")[0];
            spFriStartTime2.setAdapter(adapter);
            spinnerPosition = adapter.getPosition(compareValue);
            spFriStartTime2.setSelection(spinnerPosition);

            compareValue = jsonHours.get("52").getAsString().split("~")[1];
            spFriEndTime2.setAdapter(adapter);
            spinnerPosition = adapter.getPosition(compareValue);
            spFriEndTime2.setSelection(spinnerPosition);
        }

        if (jsonHours.get("53") != null) {
            btFriDeleteHours3.setVisibility(View.VISIBLE);
            spFriStartTime3.setVisibility(View.VISIBLE);
            tvTo15.setVisibility(View.VISIBLE);
            spFriEndTime3.setVisibility(View.VISIBLE);

            compareValue = jsonHours.get("53").getAsString().split("~")[0];
            spFriStartTime3.setAdapter(adapter);
            spinnerPosition = adapter.getPosition(compareValue);
            spFriStartTime3.setSelection(spinnerPosition);

            compareValue = jsonHours.get("53").getAsString().split("~")[1];
            spFriEndTime3.setAdapter(adapter);
            spinnerPosition = adapter.getPosition(compareValue);
            spFriEndTime3.setSelection(spinnerPosition);
        }

        //星期六
        spSatStartTime = view.findViewById(R.id.spSatStartTime);
        spSatEndTime = view.findViewById(R.id.spSatEndTime);
        spSatStartTime2 = view.findViewById(R.id.spSatStartTime2);
        spSatEndTime2 = view.findViewById(R.id.spSatEndTime2);
        spSatStartTime3 = view.findViewById(R.id.spSatStartTime3);
        spSatEndTime3 = view.findViewById(R.id.spSatEndTime3);

        tvTo16 = view.findViewById(R.id.tvTo16);
        tvTo17 = view.findViewById(R.id.tvTo17);
        tvTo18 = view.findViewById(R.id.tvTo18);

        btSatAddHours = view.findViewById(R.id.btSatAddHours);
        btSatDeleteHours2 = view.findViewById(R.id.btSatDeleteHours2);
        btSatAddHours2 = view.findViewById(R.id.btSatAddHours2);
        btSatDeleteHours3 = view.findViewById(R.id.btSatDeleteHours3);

        if (jsonHours.get("61") != null) {
            compareValue = jsonHours.get("61").getAsString().split("~")[0];
            spSatStartTime.setAdapter(adapterWithRest);
            spinnerPosition = adapterWithRest.getPosition(compareValue);
            spSatStartTime.setSelection(spinnerPosition);

            compareValue = jsonHours.get("61").getAsString().split("~")[1];
            spSatEndTime.setAdapter(adapter);
            spinnerPosition = adapter.getPosition(compareValue);
            spSatEndTime.setSelection(spinnerPosition);
        } else {
            spSatStartTime.setSelection(0, true);
            spSatEndTime.setVisibility(View.GONE);
            tvTo16.setVisibility(View.GONE);
            btSatAddHours.setVisibility(View.GONE);
        }

        spSatStartTime.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    spSatEndTime.setVisibility(View.VISIBLE);
                    tvTo16.setVisibility(View.VISIBLE);
                    btSatAddHours.setVisibility(View.VISIBLE);
                } else {
                    spSatEndTime.setVisibility(View.GONE);
                    tvTo16.setVisibility(View.GONE);
                    btSatAddHours.setVisibility(View.GONE);

                    hoursVisibility.put("SatHours2", false);
                    btSatDeleteHours2.setVisibility(View.GONE);
                    spSatStartTime2.setVisibility(View.GONE);
                    tvTo17.setVisibility(View.GONE);
                    spSatEndTime2.setVisibility(View.GONE);
                    btSatAddHours2.setVisibility(View.GONE);

                    hoursVisibility.put("SatHours3", false);
                    btSatDeleteHours3.setVisibility(View.GONE);
                    spSatStartTime3.setVisibility(View.GONE);
                    tvTo18.setVisibility(View.GONE);
                    spSatEndTime3.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //點擊加時段出現第二列
        btSatAddHours.setOnClickListener(v -> {
            if (hoursVisibility.get("SatHours2") == false) {
                hoursVisibility.put("SatHours2", true);
                btSatDeleteHours2.setVisibility(View.VISIBLE);
                spSatStartTime2.setVisibility(View.VISIBLE);
                tvTo17.setVisibility(View.VISIBLE);
                spSatEndTime2.setVisibility(View.VISIBLE);
                btSatAddHours2.setVisibility(View.VISIBLE);
            }
        });

        //點擊刪時段，隱藏該列
        btSatDeleteHours2.setOnClickListener(v -> {
            if (hoursVisibility.get("SatHours2") == true) {
                hoursVisibility.put("SatHours2", false);
                btSatDeleteHours2.setVisibility(View.GONE);
                spSatStartTime2.setVisibility(View.GONE);
                tvTo17.setVisibility(View.GONE);
                spSatEndTime2.setVisibility(View.GONE);
                btSatAddHours2.setVisibility(View.GONE);
            }
        });

        btSatAddHours2.setOnClickListener(v -> {
            if (hoursVisibility.get("SatHours3") == false) {
                hoursVisibility.put("SatHours3", true);
                btSatDeleteHours3.setVisibility(View.VISIBLE);
                spSatStartTime3.setVisibility(View.VISIBLE);
                tvTo18.setVisibility(View.VISIBLE);
                spSatEndTime3.setVisibility(View.VISIBLE);
            }
        });

        btSatDeleteHours3.setOnClickListener(v -> {
            if (hoursVisibility.get("SatHours3") == true) {
                hoursVisibility.put("SatHours3", false);
                btSatDeleteHours3.setVisibility(View.GONE);
                spSatStartTime3.setVisibility(View.GONE);
                tvTo18.setVisibility(View.GONE);
                spSatEndTime3.setVisibility(View.GONE);
            }
        });

        if (jsonHours.get("62") != null) {
            btSatDeleteHours2.setVisibility(View.VISIBLE);
            spSatStartTime2.setVisibility(View.VISIBLE);
            tvTo17.setVisibility(View.VISIBLE);
            spSatEndTime2.setVisibility(View.VISIBLE);
            btSatAddHours2.setVisibility(View.VISIBLE);

            compareValue = jsonHours.get("62").getAsString().split("~")[0];
            spSatStartTime2.setAdapter(adapter);
            spinnerPosition = adapter.getPosition(compareValue);
            spSatStartTime2.setSelection(spinnerPosition);

            compareValue = jsonHours.get("62").getAsString().split("~")[1];
            spSatEndTime2.setAdapter(adapter);
            spinnerPosition = adapter.getPosition(compareValue);
            spSatEndTime2.setSelection(spinnerPosition);
        }

        if (jsonHours.get("63") != null) {
            btSatDeleteHours3.setVisibility(View.VISIBLE);
            spSatStartTime3.setVisibility(View.VISIBLE);
            tvTo18.setVisibility(View.VISIBLE);
            spSatEndTime3.setVisibility(View.VISIBLE);

            compareValue = jsonHours.get("63").getAsString().split("~")[0];
            spSatStartTime3.setAdapter(adapter);
            spinnerPosition = adapter.getPosition(compareValue);
            spSatStartTime3.setSelection(spinnerPosition);

            compareValue = jsonHours.get("63").getAsString().split("~")[1];
            spSatEndTime3.setAdapter(adapter);
            spinnerPosition = adapter.getPosition(compareValue);
            spSatEndTime3.setSelection(spinnerPosition);
        }

        //星期日
        spSunStartTime = view.findViewById(R.id.spSunStartTime);
        spSunEndTime = view.findViewById(R.id.spSunEndTime);
        spSunStartTime2 = view.findViewById(R.id.spSunStartTime2);
        spSunEndTime2 = view.findViewById(R.id.spSunEndTime2);
        spSunStartTime3 = view.findViewById(R.id.spSunStartTime3);
        spSunEndTime3 = view.findViewById(R.id.spSunEndTime3);

        tvTo19 = view.findViewById(R.id.tvTo19);
        tvTo20 = view.findViewById(R.id.tvTo20);
        tvTo21 = view.findViewById(R.id.tvTo21);

        btSunAddHours = view.findViewById(R.id.btSunAddHours);
        btSunDeleteHours2 = view.findViewById(R.id.btSunDeleteHours2);
        btSunAddHours2 = view.findViewById(R.id.btSunAddHours2);
        btSunDeleteHours3 = view.findViewById(R.id.btSunDeleteHours3);

        if (jsonHours.get("71") != null) {
            compareValue = jsonHours.get("71").getAsString().split("~")[0];
            spSunStartTime.setAdapter(adapterWithRest);
            spinnerPosition = adapterWithRest.getPosition(compareValue);
            spSunStartTime.setSelection(spinnerPosition);

            compareValue = jsonHours.get("71").getAsString().split("~")[1];
            spSunEndTime.setAdapter(adapter);
            spinnerPosition = adapter.getPosition(compareValue);
            spSunEndTime.setSelection(spinnerPosition);
        } else {
            spSunStartTime.setSelection(0, true);
            spSunEndTime.setVisibility(View.GONE);
            tvTo19.setVisibility(View.GONE);
            btSunAddHours.setVisibility(View.GONE);
        }

        spSunStartTime.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    spSunEndTime.setVisibility(View.VISIBLE);
                    tvTo19.setVisibility(View.VISIBLE);
                    btSunAddHours.setVisibility(View.VISIBLE);
                } else {
                    spSunEndTime.setVisibility(View.GONE);
                    tvTo19.setVisibility(View.GONE);
                    btSunAddHours.setVisibility(View.GONE);

                    hoursVisibility.put("SunHours2", false);
                    btSunDeleteHours2.setVisibility(View.GONE);
                    spSunStartTime2.setVisibility(View.GONE);
                    tvTo20.setVisibility(View.GONE);
                    spSunEndTime2.setVisibility(View.GONE);
                    btSunAddHours2.setVisibility(View.GONE);

                    hoursVisibility.put("SunHours3", false);
                    btSunDeleteHours3.setVisibility(View.GONE);
                    spSunStartTime3.setVisibility(View.GONE);
                    tvTo21.setVisibility(View.GONE);
                    spSunEndTime3.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //點擊加時段出現第二列
        btSunAddHours.setOnClickListener(v -> {
            if (hoursVisibility.get("SunHours2") == false) {
                hoursVisibility.put("SunHours2", true);
                btSunDeleteHours2.setVisibility(View.VISIBLE);
                spSunStartTime2.setVisibility(View.VISIBLE);
                tvTo20.setVisibility(View.VISIBLE);
                spSunEndTime2.setVisibility(View.VISIBLE);
                btSunAddHours2.setVisibility(View.VISIBLE);
            }
        });

        //點擊刪時段，隱藏該列
        btSunDeleteHours2.setOnClickListener(v -> {
            if (hoursVisibility.get("SunHours2") == true) {
                hoursVisibility.put("SunHours2", false);
                btSunDeleteHours2.setVisibility(View.GONE);
                spSunStartTime2.setVisibility(View.GONE);
                tvTo20.setVisibility(View.GONE);
                spSunEndTime2.setVisibility(View.GONE);
                btSunAddHours2.setVisibility(View.GONE);
            }
        });

        btSunAddHours2.setOnClickListener(v -> {
            if (hoursVisibility.get("SunHours3") == false) {
                hoursVisibility.put("SunHours3", true);
                btSunDeleteHours3.setVisibility(View.VISIBLE);
                spSunStartTime3.setVisibility(View.VISIBLE);
                tvTo21.setVisibility(View.VISIBLE);
                spSunEndTime3.setVisibility(View.VISIBLE);
            }
        });

        btSunDeleteHours3.setOnClickListener(v -> {
            if (hoursVisibility.get("SunHours3") == true) {
                hoursVisibility.put("SunHours3", false);
                btSunDeleteHours3.setVisibility(View.GONE);
                spSunStartTime3.setVisibility(View.GONE);
                tvTo21.setVisibility(View.GONE);
                spSunEndTime3.setVisibility(View.GONE);
            }
        });

        if (jsonHours.get("72") != null) {
            btSunDeleteHours2.setVisibility(View.VISIBLE);
            spSunStartTime2.setVisibility(View.VISIBLE);
            tvTo20.setVisibility(View.VISIBLE);
            spSunEndTime2.setVisibility(View.VISIBLE);
            btSunAddHours2.setVisibility(View.VISIBLE);

            compareValue = jsonHours.get("72").getAsString().split("~")[0];
            spSunStartTime2.setAdapter(adapter);
            spinnerPosition = adapter.getPosition(compareValue);
            spSunStartTime2.setSelection(spinnerPosition);

            compareValue = jsonHours.get("72").getAsString().split("~")[1];
            spSunEndTime2.setAdapter(adapter);
            spinnerPosition = adapter.getPosition(compareValue);
            spSunEndTime2.setSelection(spinnerPosition);
        }

        if (jsonHours.get("73") != null) {
            btSunDeleteHours3.setVisibility(View.VISIBLE);
            spSunStartTime3.setVisibility(View.VISIBLE);
            tvTo21.setVisibility(View.VISIBLE);
            spSunEndTime3.setVisibility(View.VISIBLE);

            compareValue = jsonHours.get("73").getAsString().split("~")[0];
            spSunStartTime3.setAdapter(adapter);
            spinnerPosition = adapter.getPosition(compareValue);
            spSunStartTime3.setSelection(spinnerPosition);

            compareValue = jsonHours.get("73").getAsString().split("~")[1];
            spSunEndTime3.setAdapter(adapter);
            spinnerPosition = adapter.getPosition(compareValue);
            spSunEndTime3.setSelection(spinnerPosition);
        }

        //餐廳分類
        List<Category> categoryList = getCategories();

        spCategory = view.findViewById(R.id.spCategory);

        String[] categories = new String[categoryList.size()];
        for (int i = 0; i < categoryList.size(); i++) {
            categories[i] = categoryList.get(i).getInfo();
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(activity,
                android.R.layout.simple_spinner_item, categories);
        /* 指定點選時彈出來的選單樣式 */
        arrayAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);
        spCategory.setAdapter(arrayAdapter);
        compareValue = res.getResCategoryInfo();
        spinnerPosition = arrayAdapter.getPosition(compareValue);
        spCategory.setSelection(spinnerPosition);

        //上架狀態
        swResEnable = view.findViewById(R.id.swResEnable);
        swResEnable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    buttonView.setText(R.string.textResIsEnable);
                } else {
                    buttonView.setText(R.string.textResIsNotEnable);
                }
            }
        });
        swResEnable.setChecked(res.isResEnable());

        showRes();

        Button btTakePicture = view.findViewById(R.id.btTakePicture);
        btTakePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                // 指定存檔路徑
                File file = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                file = new File(file, "picture.jpg");
                contentUri = FileProvider.getUriForFile(
                        activity, activity.getPackageName() + ".provider", file);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);

                if (intent.resolveActivity(activity.getPackageManager()) != null) {
                    startActivityForResult(intent, REQ_TAKE_PICTURE);
                } else {
                    Common.showToast(activity, R.string.textNoCameraApp);
                }
            }
        });

        Button btPickPicture = view.findViewById(R.id.btPickPicture);
        btPickPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQ_PICK_PICTURE);
            }
        });

        Button btUpdate = view.findViewById(R.id.btUpdate);
        btUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (image == null) {
                    Common.showToast(activity, R.string.textImageEmpty);
                    return;
                }

                String resName = etResName.getText().toString().trim();
                String resAddress = etResAddress.getText().toString().trim();

                List<Address> addressList;
                // 如果地址無法解析成經緯度，就設為-181，因為經度為-180~+180
                double resLat = -181.0;
                double resLon = -181.0;
                try {
                    addressList = new Geocoder(activity).getFromLocationName(resAddress, 1);
                    if (addressList != null && addressList.size() > 0) {
                        resLat = addressList.get(0).getLatitude();
                        resLon = addressList.get(0).getLongitude();
                    }
                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                }
                if (resLat == -181.0) {
                    Common.showToast(activity, R.string.textAddressError);
                    return;
                }

                String resTel = etResTel.getText().toString().trim();
                if (resName.isEmpty() || resAddress.isEmpty() || resTel.isEmpty()) {
                    Common.showToast(activity, R.string.textInputEmpty);
                    return;
                }

                StringBuilder resHours = new StringBuilder("{");
                //星期一
                if (!spMonStartTime.getSelectedItem().toString().trim().equals("休息")) {
                    if(spMonStartTime.getSelectedItemPosition() > spMonEndTime.getSelectedItemPosition()){
                        Common.showToast(activity, R.string.textHoursError);
                        return;
                    }
                    resHours.append("\"11\": \"");
                    resHours.append(spMonStartTime.getSelectedItem().toString());
                    resHours.append("~");
                    resHours.append(spMonEndTime.getSelectedItem().toString());
                    resHours.append("\",");
                }
                if (hoursVisibility.get("MonHours2") == true) {
                    if(spMonStartTime2.getSelectedItemPosition() >= spMonEndTime2.getSelectedItemPosition()){
                        Common.showToast(activity, R.string.textHoursError);
                        return;
                    }
                    resHours.append("\"12\": \"");
                    resHours.append(spMonStartTime2.getSelectedItem().toString());
                    resHours.append("~");
                    resHours.append(spMonEndTime2.getSelectedItem().toString());
                    resHours.append("\",");
                }
                if (hoursVisibility.get("MonHours3") == true) {
                    if(spMonStartTime3.getSelectedItemPosition() >= spMonEndTime3.getSelectedItemPosition()){
                        Common.showToast(activity, R.string.textHoursError);
                        return;
                    }
                    resHours.append("\"13\": \"");
                    resHours.append(spMonStartTime3.getSelectedItem().toString());
                    resHours.append("~");
                    resHours.append(spMonEndTime3.getSelectedItem().toString());
                    resHours.append("\",");
                }
                //星期二
                if (!spTueStartTime.getSelectedItem().toString().trim().equals("休息")) {
                    if(spTueStartTime.getSelectedItemPosition() > spTueEndTime.getSelectedItemPosition()){
                        Common.showToast(activity, R.string.textHoursError);
                        return;
                    }
                    resHours.append("\"21\": \"");
                    resHours.append(spTueStartTime.getSelectedItem().toString());
                    resHours.append("~");
                    resHours.append(spTueEndTime.getSelectedItem().toString());
                    resHours.append("\",");
                }
                if (hoursVisibility.get("TueHours2") == true) {
                    if(spTueStartTime2.getSelectedItemPosition() >= spTueEndTime2.getSelectedItemPosition()){
                        Common.showToast(activity, R.string.textHoursError);
                        return;
                    }
                    resHours.append("\"22\": \"");
                    resHours.append(spTueStartTime2.getSelectedItem().toString());
                    resHours.append("~");
                    resHours.append(spTueEndTime2.getSelectedItem().toString());
                    resHours.append("\",");
                }
                if (hoursVisibility.get("TueHours3") == true) {
                    if(spTueStartTime3.getSelectedItemPosition() >= spTueEndTime3.getSelectedItemPosition()){
                        Common.showToast(activity, R.string.textHoursError);
                        return;
                    }
                    resHours.append("\"23\": \"");
                    resHours.append(spTueStartTime3.getSelectedItem().toString());
                    resHours.append("~");
                    resHours.append(spTueEndTime3.getSelectedItem().toString());
                    resHours.append("\",");
                }
                //星期三
                if (!spWedStartTime.getSelectedItem().toString().trim().equals("休息")) {
                    if(spWedStartTime.getSelectedItemPosition() > spWedEndTime.getSelectedItemPosition()){
                        Common.showToast(activity, R.string.textHoursError);
                        return;
                    }
                    resHours.append("\"31\": \"");
                    resHours.append(spWedStartTime.getSelectedItem().toString());
                    resHours.append("~");
                    resHours.append(spWedEndTime.getSelectedItem().toString());
                    resHours.append("\",");
                }
                if (hoursVisibility.get("WedHours2") == true) {
                    if(spWedStartTime2.getSelectedItemPosition() >= spWedEndTime2.getSelectedItemPosition()){
                        Common.showToast(activity, R.string.textHoursError);
                        return;
                    }
                    resHours.append("\"32\": \"");
                    resHours.append(spWedStartTime2.getSelectedItem().toString());
                    resHours.append("~");
                    resHours.append(spWedEndTime2.getSelectedItem().toString());
                    resHours.append("\",");
                }
                if (hoursVisibility.get("WedHours3") == true) {
                    if(spWedStartTime3.getSelectedItemPosition() >= spWedEndTime3.getSelectedItemPosition()){
                        Common.showToast(activity, R.string.textHoursError);
                        return;
                    }
                    resHours.append("\"33\": \"");
                    resHours.append(spWedStartTime3.getSelectedItem().toString());
                    resHours.append("~");
                    resHours.append(spWedEndTime3.getSelectedItem().toString());
                    resHours.append("\",");
                }
                //星期四
                if (!spThuStartTime.getSelectedItem().toString().trim().equals("休息")) {
                    if(spThuStartTime.getSelectedItemPosition() > spThuEndTime.getSelectedItemPosition()){
                        Common.showToast(activity, R.string.textHoursError);
                        return;
                    }
                    resHours.append("\"41\": \"");
                    resHours.append(spThuStartTime.getSelectedItem().toString());
                    resHours.append("~");
                    resHours.append(spThuEndTime.getSelectedItem().toString());
                    resHours.append("\",");
                }
                if (hoursVisibility.get("ThuHours2") == true) {
                    if(spThuStartTime2.getSelectedItemPosition() >= spThuEndTime2.getSelectedItemPosition()){
                        Common.showToast(activity, R.string.textHoursError);
                        return;
                    }
                    resHours.append("\"42\": \"");
                    resHours.append(spThuStartTime2.getSelectedItem().toString());
                    resHours.append("~");
                    resHours.append(spThuEndTime2.getSelectedItem().toString());
                    resHours.append("\",");
                }
                if (hoursVisibility.get("ThuHours3") == true) {
                    if(spThuStartTime3.getSelectedItemPosition() >= spThuEndTime3.getSelectedItemPosition()){
                        Common.showToast(activity, R.string.textHoursError);
                        return;
                    }
                    resHours.append("\"43\": \"");
                    resHours.append(spThuStartTime3.getSelectedItem().toString());
                    resHours.append("~");
                    resHours.append(spThuEndTime3.getSelectedItem().toString());
                    resHours.append("\",");
                }
                //星期五
                if (!spFriStartTime.getSelectedItem().toString().trim().equals("休息")) {
                    if(spFriStartTime.getSelectedItemPosition() > spFriEndTime.getSelectedItemPosition()){
                        Common.showToast(activity, R.string.textHoursError);
                        return;
                    }
                    resHours.append("\"51\": \"");
                    resHours.append(spFriStartTime.getSelectedItem().toString());
                    resHours.append("~");
                    resHours.append(spFriEndTime.getSelectedItem().toString());
                    resHours.append("\",");
                }
                if (hoursVisibility.get("FriHours2") == true) {
                    if(spFriStartTime2.getSelectedItemPosition() >= spFriEndTime2.getSelectedItemPosition()){
                        Common.showToast(activity, R.string.textHoursError);
                        return;
                    }
                    resHours.append("\"52\": \"");
                    resHours.append(spFriStartTime2.getSelectedItem().toString());
                    resHours.append("~");
                    resHours.append(spFriEndTime2.getSelectedItem().toString());
                    resHours.append("\",");
                }
                if (hoursVisibility.get("FriHours3") == true) {
                    if(spFriStartTime3.getSelectedItemPosition() >= spFriEndTime3.getSelectedItemPosition()){
                        Common.showToast(activity, R.string.textHoursError);
                        return;
                    }
                    resHours.append("\"53\": \"");
                    resHours.append(spFriStartTime3.getSelectedItem().toString());
                    resHours.append("~");
                    resHours.append(spFriEndTime3.getSelectedItem().toString());
                    resHours.append("\",");
                }
                //星期六
                if (!spSatStartTime.getSelectedItem().toString().trim().equals("休息")) {
                    if(spSatStartTime.getSelectedItemPosition() > spSatEndTime.getSelectedItemPosition()){
                        Common.showToast(activity, R.string.textHoursError);
                        return;
                    }
                    resHours.append("\"61\": \"");
                    resHours.append(spSatStartTime.getSelectedItem().toString());
                    resHours.append("~");
                    resHours.append(spSatEndTime.getSelectedItem().toString());
                    resHours.append("\",");
                }
                if (hoursVisibility.get("SatHours2") == true) {
                    if(spSatStartTime2.getSelectedItemPosition() >= spSatEndTime2.getSelectedItemPosition()){
                        Common.showToast(activity, R.string.textHoursError);
                        return;
                    }
                    resHours.append("\"62\": \"");
                    resHours.append(spSatStartTime2.getSelectedItem().toString());
                    resHours.append("~");
                    resHours.append(spSatEndTime2.getSelectedItem().toString());
                    resHours.append("\",");
                }
                if (hoursVisibility.get("SatHours3") == true) {
                    if(spSatStartTime3.getSelectedItemPosition() >= spSatEndTime3.getSelectedItemPosition()){
                        Common.showToast(activity, R.string.textHoursError);
                        return;
                    }
                    resHours.append("\"63\": \"");
                    resHours.append(spSatStartTime3.getSelectedItem().toString());
                    resHours.append("~");
                    resHours.append(spSatEndTime3.getSelectedItem().toString());
                    resHours.append("\",");
                }
                //星期日
                if (!spSunStartTime.getSelectedItem().toString().trim().equals("休息")) {
                    if(spSunStartTime.getSelectedItemPosition() > spSunEndTime.getSelectedItemPosition()){
                        Common.showToast(activity, R.string.textHoursError);
                        return;
                    }
                    resHours.append("\"71\": \"");
                    resHours.append(spSunStartTime.getSelectedItem().toString());
                    resHours.append("~");
                    resHours.append(spSunEndTime.getSelectedItem().toString());
                    resHours.append("\",");
                }
                if (hoursVisibility.get("SunHours2") == true) {
                    if(spSunStartTime2.getSelectedItemPosition() >= spSunEndTime2.getSelectedItemPosition()){
                        Common.showToast(activity, R.string.textHoursError);
                        return;
                    }
                    resHours.append("\"72\": \"");
                    resHours.append(spSunStartTime2.getSelectedItem().toString());
                    resHours.append("~");
                    resHours.append(spSunEndTime2.getSelectedItem().toString());
                    resHours.append("\",");
                }
                if (hoursVisibility.get("SunHours3") == true) {
                    if(spSunStartTime3.getSelectedItemPosition() >= spSunEndTime3.getSelectedItemPosition()){
                        Common.showToast(activity, R.string.textHoursError);
                        return;
                    }
                    resHours.append("\"73\": \"");
                    resHours.append(spSunStartTime3.getSelectedItem().toString());
                    resHours.append("~");
                    resHours.append(spSunEndTime3.getSelectedItem().toString());
                    resHours.append("\",");
                }
                if (resHours.length() != 1) {
                    resHours.deleteCharAt(resHours.length() - 1);
                }
                resHours.append("}");

                int resCategoryId = categoryList.get(spCategory.getSelectedItemPosition()).getId();

                boolean resEnable = swResEnable.isChecked();

                int userId = Common.USER_ID;

                Timestamp modifyDate = new Timestamp(System.currentTimeMillis());
                if (Common.networkConnected(activity)) {
                    String url = Common.URL_SERVER + "ResServlet";
                    Res resUpdate = new Res(res.getResId(), resName, resAddress, resLat, resLon, resTel, resHours.toString(),
                            resCategoryId, resEnable, userId, modifyDate);
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("action", "resUpdate");
                    //jsonObject.addProperty("res", new Gson().toJson(resUpdate));
                    Gson gson =  new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").create();
                    jsonObject.addProperty("res", gson.toJson(resUpdate));
                    // 有圖才上傳
                    if (image != null) {
                        jsonObject.addProperty("imageBase64", Base64.encodeToString(image, Base64.DEFAULT));
                    }
                    int count = 0;
                    try {
                        String result = new CommonTask(url, jsonObject.toString()).execute().get();
                        count = Integer.parseInt(result);
                    } catch (Exception e) {
                        Log.e(TAG, e.toString());
                    }
                    if (count == 0) {
                        Common.showToast(activity, R.string.textUpdateFail);
                    } else {
                        Common.showToast(activity, R.string.textUpdateSuccess);
                    }
                } else {
                    Common.showToast(activity, R.string.textNoNetwork);
                }
                /* 回前一個Fragment */
                navController.popBackStack();
            }
        });

        Button btCancel = view.findViewById(R.id.btCancel);
        btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* 回前一個Fragment */
                navController.popBackStack();
            }
        });
    }

    private void showRes() {
        String url = Common.URL_SERVER + "ResServlet";
        int id = res.getResId();
        int imageSize = getResources().getDisplayMetrics().widthPixels;
        Bitmap bitmap = null;
        try {
            bitmap = new ImageTask(url, id, imageSize).execute().get();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            image = out.toByteArray();
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
        if (bitmap != null) {
            ivRes.setImageBitmap(bitmap);
        } else {
            ivRes.setImageResource(R.drawable.no_image);
        }
        //tvId.setText(String.valueOf(id));
        etResName.setText(res.getResName());
        etResAddress.setText(res.getResAddress());
        etResTel.setText(res.getResTel());

    }

    private Map<String, Boolean> getHoursVisibility() {
        Map<String, Boolean> map = new HashMap<>();

        if (jsonHours.get("12") == null) {
            map.put("MonHours2", false);
        } else {
            map.put("MonHours2", true);
        }
        if (jsonHours.get("13") == null) {
            map.put("MonHours3", false);
        } else {
            map.put("MonHours3", true);
        }
        if (jsonHours.get("22") == null) {
            map.put("TueHours2", false);
        } else {
            map.put("TueHours2", true);
        }
        if (jsonHours.get("23") == null) {
            map.put("TueHours3", false);
        } else {
            map.put("TueHours3", true);
        }
        if (jsonHours.get("32") == null) {
            map.put("WedHours2", false);
        } else {
            map.put("WedHours2", true);
        }
        if (jsonHours.get("33") == null) {
            map.put("WedHours3", false);
        } else {
            map.put("WedHours3", true);
        }
        if (jsonHours.get("42") == null) {
            map.put("ThuHours2", false);
        } else {
            map.put("ThuHours2", true);
        }
        if (jsonHours.get("43") == null) {
            map.put("ThuHours3", false);
        } else {
            map.put("ThuHours3", true);
        }
        if (jsonHours.get("52") == null) {
            map.put("FriHours2", false);
        } else {
            map.put("FriHours2", true);
        }
        if (jsonHours.get("53") == null) {
            map.put("FriHours3", false);
        } else {
            map.put("FriHours3", true);
        }
        if (jsonHours.get("62") == null) {
            map.put("SatHours2", false);
        } else {
            map.put("SatHours2", true);
        }
        if (jsonHours.get("63") == null) {
            map.put("SatHours3", false);
        } else {
            map.put("SatHours3", true);
        }
        if (jsonHours.get("72") == null) {
            map.put("SunHours2", false);
        } else {
            map.put("SunHours2", true);
        }
        if (jsonHours.get("73") == null) {
            map.put("SunHours3", false);
        } else {
            map.put("SunHours3", true);
        }

        return map;
    }

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
//                .withAspectRatio(16, 9) // 設定裁減比例
//                .withMaxResultSize(500, 500) // 設定結果尺寸不可超過指定寬高
                .start(activity, this, REQ_CROP_PICTURE);
    }

    private void handleCropResult(Intent intent) {
        Uri resultUri = UCrop.getOutput(intent);
        if (resultUri == null) {
            return;
        }
        Bitmap bitmap = null;
        try {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
                bitmap = BitmapFactory.decodeStream(
                        activity.getContentResolver().openInputStream(resultUri));
            } else {
                ImageDecoder.Source source =
                        ImageDecoder.createSource(activity.getContentResolver(), resultUri);
                bitmap = ImageDecoder.decodeBitmap(source);
            }
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            image = out.toByteArray();
        } catch (IOException e) {
            Log.e(TAG, e.toString());
        }
        if (bitmap != null) {
            ivRes.setImageBitmap(bitmap);
        } else {
            ivRes.setImageResource(R.drawable.no_image);
        }
    }

    private List<Category> getCategories() {
        List<Category> categories = null;
        if (Common.networkConnected(activity)) {
            String url = Common.URL_SERVER + "ResServlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "getCategories");
            String jsonOut = jsonObject.toString();
            CommonTask resGetCategoriesTask = new CommonTask(url, jsonOut);
            try {
                String jsonIn = resGetCategoriesTask.execute().get();
                Type listType = new TypeToken<List<Category>>() {
                }.getType();
                categories = new Gson().fromJson(jsonIn, listType);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        } else {
            Common.showToast(activity, R.string.textNoNetwork);
        }
        return categories;
    }
}