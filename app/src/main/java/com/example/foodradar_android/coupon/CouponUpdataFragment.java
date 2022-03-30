package com.example.foodradar_android.coupon;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.Environment;
import android.os.TestLooperManager;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.example.foodradar_android.Common;
import com.example.foodradar_android.R;
import com.example.foodradar_android.task.CommonTask;
import com.example.foodradar_android.task.ImageTask;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.yalantis.ucrop.UCrop;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;


public class CouponUpdataFragment extends Fragment implements DatePickerDialog.OnDateSetListener {
    private Activity activity;
    private NavController navController;
    public final String TAG = "TAG_CouponUpdataFragment";

    private ImageView imcouPonPhoto;
    private ImageButton ibphotoAdd;
    private Button tvcouPonStartDate, tvcouPonEndDate, btPlaceRes;
    private byte[] image;

    private TextView textView, tvId;

    private TextView etcouPonStartDate, etcouPonStartDateDivider, tvPlaceRes, tvPlaceResDivider;
    private TextView etcouPonEndDate, etcouPonEndDateDivider;

    private TextView tvcouPonType;
    private Spinner spType;

    private TextView tvcouPonEnable;
    private Spinner spEnable;

    private TextView tvcouPonInfo;
    private EditText etcouPonInfo;

    private Button btUpdataAdd, btUpdataCancel;

    private static int year, month, day, hour, minute;
    private static final int REQ_TAKE_PICTURE = 0;
    private static final int REQ_PICK_PICTURE = 1;
    private static final int REQ_CROP_PICTURE = 2;
    private static final int PER_EXTERNAL_STORAGE = 201;
    private Boolean bolAccessExternalStorage;
    private Uri imageUri;
    private SharedPreferences preferences;
    private final static String PREFERENCES_NAME = "Res";   //儲存檔名
    private final static String DEFAULT_FILE_NAME = " "; //抓不到檔案就顯示
    private int statusInt ;
    private boolean spTypeBoolean;
    private boolean spEnableBoolean;
    private Coupon coupon;
    private int userIdBox = Common.USER_ID;
    private int couPonId;
    private int userId;
    private int resId;
    private int newRescoupon;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity.setTitle(R.string.title_of_res_coupon_update);
        return inflater.inflate(R.layout.fragment_coupon_updata, container, false);
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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final NavController navController = Navigation.findNavController(view);

        imcouPonPhoto = view.findViewById(R.id.imcouPonPhoto);
        tvId = view.findViewById(R.id.tvId);
        tvcouPonInfo = view.findViewById(R.id.tvcouPonInfo);
        etcouPonInfo = view.findViewById(R.id.etcouPonInfo);
        tvcouPonStartDate = view.findViewById(R.id.tvcouPonStartDate);
        etcouPonStartDate = view.findViewById(R.id.etcouPonStartDate);
        tvcouPonEndDate = view.findViewById(R.id.tvcouPonEndDate);
        etcouPonEndDate = view.findViewById(R.id.etcouPonEndDate);

        Bundle bundle = getArguments();
        if (bundle == null || bundle.getSerializable("coupon") == null) {
            //Common.showToast(activity, R.string.textNoSpotsFound);
            navController.popBackStack();
            return;
        }
        coupon = (Coupon) bundle.getSerializable("coupon");
        showCoupon();

//        Bundle bundle1 = getArguments();
//        newRescoupon = bundle1.getInt("newArticle");
//        if (newRescoupon == 2) {
//            tvPlaceRes.setText("店名：請選擇餐廳");
//            //將bundle內的資料(int)改成0
//            bundle1.putInt("newArticle", 0);
//        } else {
//            tvPlaceRes.setText("店名：請選擇餐廳2");
//
//        }

        ibphotoAdd = view.findViewById(R.id.ibphotoAdd);
        ibphotoAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQ_PICK_PICTURE);
            }
        });

        tvcouPonStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                statusInt = 0;
                DatePickerDialog datePickerDialog = new DatePickerDialog(activity, CouponUpdataFragment.this, CouponUpdataFragment.year, CouponUpdataFragment.month, CouponUpdataFragment.day);
                // 取得DatePicker物件方可設定可選取的日期區間
                DatePicker datePicker = datePickerDialog.getDatePicker();
                // 設定可選取的開始日為今日
                Calendar calendar = Calendar.getInstance();
                //datePicker.setMinDate(calendar.getTimeInMillis());
                // 設定可選取的結束日為一個月後
                calendar.add(Calendar.MONTH, 1);
                datePicker.setMaxDate(calendar.getTimeInMillis());
                // 最後要呼叫show()方能顯示
                datePickerDialog.show();

            }
        });
        showStart();

        tvcouPonEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                statusInt = 1;
                DatePickerDialog datePickerDialog =
                        new DatePickerDialog(
                                activity, CouponUpdataFragment.this,
                                CouponUpdataFragment.year, CouponUpdataFragment.month, CouponUpdataFragment.day);
                // 取得DatePicker物件方可設定可選取的日期區間
                DatePicker datePicker = datePickerDialog.getDatePicker();
                // 設定可選取的開始日為今日
                Calendar calendar = Calendar.getInstance();
                //datePicker.setMinDate(calendar.getTimeInMillis());
                // 設定可選取的結束日為一個月後
                calendar.add(java.util.Calendar.MONTH, 2);
                datePicker.setMaxDate(calendar.getTimeInMillis());
                // 最後要呼叫show()方能顯示
                datePickerDialog.show();

            }
        });
        showEnd();

        tvcouPonType = view.findViewById(R.id.tvcouPonType);
        spType = view.findViewById(R.id.spType);
//        spType.setSelection(0, true);
        spType.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        //spType.getSelectedItem().toString().equals(false);
                        //spTypeBoolean = getResources().toString().equals(0);
                        spTypeBoolean = false;
//                        spType.getSelectedItem().toString().equals("公告");
                       // Common.showToast(activity, "優惠券活動");
                        break;

                    case 1:
                        //spType.getSelectedItem().toString().equals(true);
                        //spTypeBoolean = getResources().toString().equals(1);
                        spTypeBoolean = true;
//                        spType.getSelectedItem().toString().equals("優惠券");
                       // Common.showToast(activity, "公告活動");
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        tvcouPonEnable = view.findViewById(R.id.tvcouPonEnable);
        spEnable = view.findViewById(R.id.spEnable);
        //spEnable.setSelection(0, true);
        spEnable.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        spEnableBoolean = false;
                        //spEnable.getSelectedItem().toString().equals("使用");
                        //Common.showToast(activity, "使用優惠券");
                        break;

                    case 1:
                        spEnableBoolean = true;
                        //spEnable.getSelectedItem().toString().equals("未使用");
                        //Common.showToast(activity, "未使用優惠券");
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

//        Spinner.OnItemSelectedListener listener = new Spinner.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                switch (position){
//                    case 0:
//                        spTypeBoolean = false;
////                        spType.getSelectedItem().toString().equals("公告");
//                        Common.showToast(activity, "優惠券活動");
//                        break;
//
//                    case 1:
//                        spTypeBoolean = true;
////                        spType.getSelectedItem().toString().equals("優惠券");
//                        Common.showToast(activity, "公告活動");
//                        break;
//                }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        };




//        String[] types = {"公告", "優惠券"};
//        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(activity,
//                android.R.layout.simple_spinner_item, types);
//        typeAdapter.setDropDownViewResource(
//                android.R.layout.simple_spinner_dropdown_item);



//        String[] enables = {"使用", "未使用"};
//        ArrayAdapter<String> enableAdapter = new ArrayAdapter<>(activity,
//                android.R.layout.simple_spinner_item, enables);
//        enableAdapter.setDropDownViewResource(
//                android.R.layout.simple_spinner_dropdown_item);

        btUpdataAdd = view.findViewById(R.id.btUpdataAdd);
        btUpdataAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String couPonInfo = etcouPonInfo.getText().toString().trim();
                if (etcouPonInfo.length() <= 0) {
                    Common.showToast(activity, "未輸入優惠資訊");
                    return;
                }
                //SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                String couPonStartDate = etcouPonStartDate.getText().toString().trim();
                String couPonEndDate = etcouPonEndDate.getText().toString().trim();

                if (Common.networkConnected(activity)) {
                    String url = Common.URL_SERVER + "CouponServlet";
                    int id = coupon.getCouPonId();
                    resId = coupon.getResId();
//                    coupon.setFields(couPonId, couPonStartDate, couPonEndDate, spTypeBoolean, spEnableBoolean, couPonInfo, userIdBox);
                    coupon = new Coupon(id, resId, couPonStartDate, couPonEndDate, spTypeBoolean, couPonInfo, spEnableBoolean, userIdBox);
//                    Coupon coupon = new Coupon(couPonStartDate, couPonEndDate, spTypeBoolean, spEnableBoolean, couPonInfo, userIdBox);
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("action", "couponUpdate");
                    jsonObject.addProperty("couPonId", id);
                    jsonObject.addProperty("loginUserId", userIdBox);
                    jsonObject.addProperty("coupon", new Gson().toJson(coupon));
                    // 有圖才上傳
                    if (image != null) {
                        jsonObject.addProperty("imageBase64", Base64.encodeToString(image, Base64.DEFAULT));
                    }
                    int count = 0;
                    try {
                        String result = new CommonTask(url, jsonObject.toString()).execute().get();
                        count = Integer.parseInt(result);
                    } catch (Exception e) {
                        //Log.e(TAG, e.toString());
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

        btUpdataCancel = view.findViewById(R.id.btUpdataCancel);
        btUpdataCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.popBackStack();
            }
        });

        tvcouPonInfo = view.findViewById(R.id.tvcouPonInfo);
        etcouPonInfo = view.findViewById(R.id.etcouPonInfo);

    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        CouponUpdataFragment.year = year;
        CouponUpdataFragment.month = month;
        CouponUpdataFragment.day = dayOfMonth;

        if (statusInt == 0) {
            updateDisplay();
        } else if ( statusInt == 1 ) {
            updateEndDisplay();
        }

    }

    private void showCoupon() {
        String url = Common.URL_SERVER + "CouponServlet";
        int id = coupon.getCouPonId();
        int imageSize = getResources().getDisplayMetrics().widthPixels / 3;
        Bitmap bitmap = null;
        try {
            bitmap = new ImageTask(url, id, imageSize).execute().get();
        } catch (Exception e) {
            //Log.e(TAG, e.toString());
        }
        if (bitmap != null) {
            imcouPonPhoto.setImageBitmap(bitmap);
        } else {
            imcouPonPhoto.setImageResource(R.drawable.no_image);
        }
        tvId.setText(String.valueOf(id));




        etcouPonStartDate.setText(coupon.getCouPonStartDate());
        etcouPonEndDate.setText(String.valueOf(coupon.getCouPonEndDate()));
        etcouPonInfo.setText(String.valueOf(coupon.getCouPonInfo()));

    }
    private void showStart(){
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        //updateDisplay();
    }

    private void showEnd() {
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
//        hour = calendar.get(java.util.Calendar.HOUR_OF_DAY);
//        minute = calendar.get(java.util.Calendar.MINUTE);
        //updateEndDisplay();

    }
    private void updateDisplay() {

        etcouPonStartDate.setText(new StringBuilder().append(year).append("-")
                .append(pad(month + 1)).append("-").append(pad(day)));

    }

    private void updateEndDisplay(){

        etcouPonEndDate.setText(new StringBuilder().append(year).append("-")
                .append(pad(month + 1)).append("-").append(pad(day)));
    }

    private String pad(int number) {
        if (number >= 10) {
            return String.valueOf(number);
        } else {
            return "0" + number;
        }
    }

//    @Override
//    public void onStart() {
//        super.onStart();
//        askExternalStoragePermission();
//        preferences = activity.getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE);
//        resId = preferences.getInt("resId", 0);
//        String resName = preferences.getString("ResName", DEFAULT_FILE_NAME);
//        String resCategory = preferences.getString("Category", DEFAULT_FILE_NAME);
//
//        if (newRescoupon == 0) {
//            tvPlaceRes.setText(Integer.toString(resId));
//            tvPlaceRes.setText(resCategory + "餐廳：" + resName);
//        } else {
//            tvPlaceRes.setText("店名：請選擇餐廳");
//
//        }
//    }

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
            // 如果user不同意將資料儲存至外部儲存體的公開檔案，就將儲存按鈕設為disable
            if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                Common.showToast(activity,"需存取內部儲存空間權限");
                bolAccessExternalStorage = false;
            } else {
                bolAccessExternalStorage = true;
            }
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQ_TAKE_PICTURE:
                    crop(imageUri);
                case REQ_PICK_PICTURE:
                    crop(intent.getData());
                case REQ_CROP_PICTURE:
                    handleCropResult(intent);
                    break;
            }
        }
    }
    private void crop(Uri sourceImageUri) {
//        Log.d("TAG","sourceImageUri: " + sourceImageUri);
        File file = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        file = new File(file, "picture_cropped.jpg");
//        Log.d("TAG","file: " + file);
        Uri destinationUri = Uri.fromFile(file);
//        Log.d("TAG","destinationUri: " + destinationUri);
        UCrop.of(sourceImageUri, destinationUri).start(activity, this, REQ_CROP_PICTURE);
//        Log.d("TAG","REQ_CROP_PICTURE: " + REQ_CROP_PICTURE);
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
            // Log.e(TAG, e.toString());
        }
        if (bitmap != null) {
            imcouPonPhoto.setImageBitmap(bitmap);
        } else {
            imcouPonPhoto.setImageResource(R.drawable.no_image);
        }
    }








}