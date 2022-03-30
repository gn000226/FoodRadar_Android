package com.example.foodradar_android.res;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;

import com.example.foodradar_android.Common;
import com.example.foodradar_android.R;
import com.example.foodradar_android.task.ImageTask;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.foodradar_android.Common;
import com.example.foodradar_android.R;
import com.example.foodradar_android.article.ArticleDetailFragment;
import com.example.foodradar_android.article.Img;
import com.example.foodradar_android.task.CommonTask;
import com.example.foodradar_android.task.ImageTask;
import com.example.foodradar_android.user.MyRes;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;


public class ResDetailFragment extends Fragment {
    private NavController navController;
    private final static String TAG = "TAG_ResDetailFragment";
    private FragmentActivity activity;
    private List<ImageTask> imageTasks;
    private ImageTask imageTask;
    private int imageSize;
    private Res res;
    private ImageView ivRes, ivImage;
    private TextView tvResName, tvResRating, tvResCategoryInfo, tvResAddress, tvResHours, tvResTel, tvImageNumber, tvResHoursDetail;
    private RecyclerView rvImage;
    private JsonObject jsonHours;
    private CommonTask imgGetAllTask;
    private List<Img> imgs;
    private LocationCallback locationCallback;
    private LocationRequest locationRequest;
    private Location lastLocation;
    private FusedLocationProviderClient fusedLocationClient;
    //private static final int PER_ACCESS_LOCATION = 0;
    private static final int REQ_CHECK_SETTINGS = 101;
    private static final int REQ_RATING = 0;
    private SharedPreferences preferences;
    private final static String PREFERENCES_NAME = "Res";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activity = getActivity();
        res = (Res) (getArguments() != null ? getArguments().getSerializable("res") : null);

        locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10000)
                .setSmallestDisplacement(1000);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                lastLocation = locationResult.getLastLocation();
            }
        };

        imageTasks = new ArrayList<>();
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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        activity.setTitle(R.string.titleResDetail);
        return inflater.inflate(R.layout.fragment_res_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ivRes = view.findViewById(R.id.ivRes);
        String url = Common.URL_SERVER + "ResServlet";
        int id = res.getResId();
        imageSize = getResources().getDisplayMetrics().widthPixels / 4;
        imageTask = new ImageTask(url, id, imageSize, ivRes);
        imageTask.execute();
//        Log.d(TAG, "imageTask: " + imageTaskBVH);

        ivRes.setOnClickListener(v -> {
            final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
            View dialogView = getLayoutInflater().inflate(R.layout.dialog_res_img, null);
            alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            alertDialog.setView(dialogView);
            ImageTask bigImageTask = new ImageTask(url, id, getResources().getDisplayMetrics().widthPixels, dialogView.findViewById(R.id.imageView));
            bigImageTask.execute();
            imageTasks.add(bigImageTask);
            alertDialog.show();
        });

        Bundle bundle = getArguments();
        if (bundle == null || bundle.getSerializable("res") == null) {
            Common.showToast(activity, R.string.textNoRessFound);
            navController.popBackStack();
            return;
        }


        tvResName = view.findViewById(R.id.tvResName);
        tvResRating = view.findViewById(R.id.tvResRating);
        RatingBar ratingBar = view.findViewById(R.id.ratingBar);
        tvResCategoryInfo = view.findViewById(R.id.tvResCategoryInfo);
        tvResAddress = view.findViewById(R.id.tvResAddress);
        tvResTel = view.findViewById(R.id.tvResTel);
        ivImage = view.findViewById(R.id.ivImage);
        tvImageNumber = view.findViewById(R.id.tvImageNumber);

        tvResName.setText(res.getResName());
        Float rating = res.getRating();
        if (rating != null) {
            if (rating >= 0) {
                tvResRating.setText(String.format("%.1f", rating));
                if (rating < 0.75) {
                    ratingBar.setRating(0.5f);
                } else if (rating < 1.25) {
                    ratingBar.setRating(1f);
                } else if (rating < 1.75) {
                    ratingBar.setRating(1.5f);
                } else if (rating < 2.25) {
                    ratingBar.setRating(2f);
                } else if (rating < 2.75) {
                    ratingBar.setRating(2.5f);
                } else if (rating < 3.25) {
                    ratingBar.setRating(3f);
                } else if (rating < 3.75) {
                    ratingBar.setRating(3.5f);
                } else if (rating < 4.25) {
                    ratingBar.setRating(4f);
                } else if (rating < 4.75) {
                    ratingBar.setRating(4.5f);
                } else {
                    ratingBar.setRating(5f);
                }
            } else {
                tvResRating.setText(R.string.textNoRating);
                ratingBar.setVisibility(View.GONE);
            }
        }
        tvResCategoryInfo.setText(res.getResCategoryInfo());
        tvResAddress.setText(res.getResAddress());
        tvResTel.setText(res.getResTel());

        showResImg();

        tvResHours = view.findViewById(R.id.tvResHours);
        jsonHours = JsonParser.parseString(res.getResHours()).getAsJsonObject();
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        //測試時用來加小時
        //cal.add(Calendar.HOUR, 2);
        int nowDay = cal.get(Calendar.DAY_OF_WEEK);
        StringBuilder strResHours = new StringBuilder();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        Calendar calStart = Calendar.getInstance();
        Calendar calEnd = Calendar.getInstance();
        boolean open = false;

        try {
            cal.setTime(sdf.parse(cal.get(Calendar.HOUR_OF_DAY) + ":" + cal.get(Calendar.MINUTE)));
            switch (nowDay) {
                case 1:
                    if (jsonHours.get("71") == null && jsonHours.get("72") == null && jsonHours.get("73") == null) {
                        strResHours.append("本日公休");
                    } else {
                        if (jsonHours.get("71") != null) {
                            calStart.setTime(sdf.parse(jsonHours.get("71").getAsString().split("~")[0]));
                            calEnd.setTime(sdf.parse(jsonHours.get("71").getAsString().split("~")[1]));
                            if (cal.after(calStart) && cal.before(calEnd)) {
                                open = true;
                            }
                        }
                        if (jsonHours.get("72") != null) {
                            calStart.setTime(sdf.parse(jsonHours.get("72").getAsString().split("~")[0]));
                            calEnd.setTime(sdf.parse(jsonHours.get("72").getAsString().split("~")[1]));
                            if (cal.after(calStart) && cal.before(calEnd)) {
                                open = true;
                            }
                        }
                        if (jsonHours.get("73") != null) {
                            calStart.setTime(sdf.parse(jsonHours.get("73").getAsString().split("~")[0]));
                            calEnd.setTime(sdf.parse(jsonHours.get("73").getAsString().split("~")[1]));
                            if (cal.after(calStart) && cal.before(calEnd)) {
                                open = true;
                            }
                        }

                        if (open) {
                            strResHours.append("營業中 ");
                        } else {
                            strResHours.append("休息中 ");
                        }

                        if (jsonHours.get("71") != null) {
                            strResHours.append(jsonHours.get("71").getAsString() + "\n");
                        }
                        if (jsonHours.get("72") != null) {
                            strResHours.append("\t\t\t\t\t\t\t");
                            strResHours.append(jsonHours.get("72").getAsString() + "\n");
                        }
                        if (jsonHours.get("73") != null) {
                            strResHours.append("\t\t\t\t\t\t\t");
                            strResHours.append(jsonHours.get("73").getAsString() + "\n");
                        }

                        strResHours.deleteCharAt(strResHours.length() - 1);
                    }
                    break;
                case 2:
                    if (jsonHours.get("11") == null && jsonHours.get("12") == null && jsonHours.get("13") == null) {
                        strResHours.append("本日公休");
                    } else {
                        if (jsonHours.get("11") != null) {
                            calStart.setTime(sdf.parse(jsonHours.get("11").getAsString().split("~")[0]));
                            calEnd.setTime(sdf.parse(jsonHours.get("11").getAsString().split("~")[1]));
                            if (cal.after(calStart) && cal.before(calEnd)) {
                                open = true;
                            }
                        }
                        if (jsonHours.get("12") != null) {
                            calStart.setTime(sdf.parse(jsonHours.get("12").getAsString().split("~")[0]));
                            calEnd.setTime(sdf.parse(jsonHours.get("12").getAsString().split("~")[1]));
                            if (cal.after(calStart) && cal.before(calEnd)) {
                                open = true;
                            }
                        }
                        if (jsonHours.get("13") != null) {
                            calStart.setTime(sdf.parse(jsonHours.get("13").getAsString().split("~")[0]));
                            calEnd.setTime(sdf.parse(jsonHours.get("13").getAsString().split("~")[1]));
                            if (cal.after(calStart) && cal.before(calEnd)) {
                                open = true;
                            }
                        }

                        if (open) {
                            strResHours.append("營業中 ");
                        } else {
                            strResHours.append("休息中 ");
                        }

                        if (jsonHours.get("11") != null) {
                            strResHours.append(jsonHours.get("11").getAsString() + "\n");
                        }
                        if (jsonHours.get("12") != null) {
                            strResHours.append("\t\t\t\t\t\t\t");
                            strResHours.append(jsonHours.get("12").getAsString() + "\n");
                        }
                        if (jsonHours.get("13") != null) {
                            strResHours.append("\t\t\t\t\t\t\t");
                            strResHours.append(jsonHours.get("13").getAsString() + "\n");
                        }

                        strResHours.deleteCharAt(strResHours.length() - 1);
                    }
                    break;
                case 3:
                    if (jsonHours.get("21") == null && jsonHours.get("22") == null && jsonHours.get("23") == null) {
                        strResHours.append("本日公休");
                    } else {
                        if (jsonHours.get("21") != null) {
                            calStart.setTime(sdf.parse(jsonHours.get("21").getAsString().split("~")[0]));
                            calEnd.setTime(sdf.parse(jsonHours.get("21").getAsString().split("~")[1]));
                            if (cal.after(calStart) && cal.before(calEnd)) {
                                open = true;
                            }
                        }
                        if (jsonHours.get("22") != null) {
                            calStart.setTime(sdf.parse(jsonHours.get("22").getAsString().split("~")[0]));
                            calEnd.setTime(sdf.parse(jsonHours.get("22").getAsString().split("~")[1]));
                            if (cal.after(calStart) && cal.before(calEnd)) {
                                open = true;
                            }
                        }
                        if (jsonHours.get("23") != null) {
                            calStart.setTime(sdf.parse(jsonHours.get("23").getAsString().split("~")[0]));
                            calEnd.setTime(sdf.parse(jsonHours.get("23").getAsString().split("~")[1]));
                            if (cal.after(calStart) && cal.before(calEnd)) {
                                open = true;
                            }
                        }

                        if (open) {
                            strResHours.append("營業中 ");
                        } else {
                            strResHours.append("休息中 ");
                        }

                        if (jsonHours.get("21") != null) {
                            strResHours.append(jsonHours.get("21").getAsString() + "\n");
                        }
                        if (jsonHours.get("22") != null) {
                            strResHours.append("\t\t\t\t\t\t\t");
                            strResHours.append(jsonHours.get("22").getAsString() + "\n");
                        }
                        if (jsonHours.get("23") != null) {
                            strResHours.append("\t\t\t\t\t\t\t");
                            strResHours.append(jsonHours.get("23").getAsString() + "\n");
                        }

                        strResHours.deleteCharAt(strResHours.length() - 1);
                    }
                    break;
                case 4:
                    if (jsonHours.get("31") == null && jsonHours.get("32") == null && jsonHours.get("33") == null) {
                        strResHours.append("本日公休");
                    } else {
                        if (jsonHours.get("31") != null) {
                            calStart.setTime(sdf.parse(jsonHours.get("31").getAsString().split("~")[0]));
                            calEnd.setTime(sdf.parse(jsonHours.get("31").getAsString().split("~")[1]));
                            if (cal.after(calStart) && cal.before(calEnd)) {
                                open = true;
                            }
                        }
                        if (jsonHours.get("32") != null) {
                            calStart.setTime(sdf.parse(jsonHours.get("32").getAsString().split("~")[0]));
                            calEnd.setTime(sdf.parse(jsonHours.get("32").getAsString().split("~")[1]));
                            if (cal.after(calStart) && cal.before(calEnd)) {
                                open = true;
                            }
                        }
                        if (jsonHours.get("33") != null) {
                            calStart.setTime(sdf.parse(jsonHours.get("33").getAsString().split("~")[0]));
                            calEnd.setTime(sdf.parse(jsonHours.get("33").getAsString().split("~")[1]));
                            if (cal.after(calStart) && cal.before(calEnd)) {
                                open = true;
                            }
                        }

                        if (open) {
                            strResHours.append("營業中 ");
                        } else {
                            strResHours.append("休息中 ");
                        }

                        if (jsonHours.get("31") != null) {
                            strResHours.append(jsonHours.get("31").getAsString() + "\n");
                        }
                        if (jsonHours.get("32") != null) {
                            strResHours.append("\t\t\t\t\t\t\t");
                            strResHours.append(jsonHours.get("32").getAsString() + "\n");
                        }
                        if (jsonHours.get("33") != null) {
                            strResHours.append("\t\t\t\t\t\t\t");
                            strResHours.append(jsonHours.get("33").getAsString() + "\n");
                        }

                        strResHours.deleteCharAt(strResHours.length() - 1);
                    }
                    break;
                case 5:
                    if (jsonHours.get("41") == null && jsonHours.get("42") == null && jsonHours.get("43") == null) {
                        strResHours.append("本日公休");
                    } else {
                        if (jsonHours.get("41") != null) {
                            calStart.setTime(sdf.parse(jsonHours.get("41").getAsString().split("~")[0]));
                            calEnd.setTime(sdf.parse(jsonHours.get("41").getAsString().split("~")[1]));
                            if (cal.after(calStart) && cal.before(calEnd)) {
                                open = true;
                            }
                        }
                        if (jsonHours.get("42") != null) {
                            calStart.setTime(sdf.parse(jsonHours.get("42").getAsString().split("~")[0]));
                            calEnd.setTime(sdf.parse(jsonHours.get("42").getAsString().split("~")[1]));
                            if (cal.after(calStart) && cal.before(calEnd)) {
                                open = true;
                            }
                        }
                        if (jsonHours.get("43") != null) {
                            calStart.setTime(sdf.parse(jsonHours.get("43").getAsString().split("~")[0]));
                            calEnd.setTime(sdf.parse(jsonHours.get("43").getAsString().split("~")[1]));
                            if (cal.after(calStart) && cal.before(calEnd)) {
                                open = true;
                            }
                        }

                        if (open) {
                            strResHours.append("營業中 ");
                        } else {
                            strResHours.append("休息中 ");
                        }

                        if (jsonHours.get("41") != null) {
                            strResHours.append(jsonHours.get("41").getAsString() + "\n");
                        }
                        if (jsonHours.get("42") != null) {
                            strResHours.append("\t\t\t\t\t\t\t");
                            strResHours.append(jsonHours.get("42").getAsString() + "\n");
                        }
                        if (jsonHours.get("43") != null) {
                            strResHours.append("\t\t\t\t\t\t\t");
                            strResHours.append(jsonHours.get("43").getAsString() + "\n");
                        }

                        strResHours.deleteCharAt(strResHours.length() - 1);
                    }
                    break;
                case 6:
                    if (jsonHours.get("51") == null && jsonHours.get("52") == null && jsonHours.get("53") == null) {
                        strResHours.append("本日公休");
                    } else {
                        if (jsonHours.get("51") != null) {
                            calStart.setTime(sdf.parse(jsonHours.get("51").getAsString().split("~")[0]));
                            calEnd.setTime(sdf.parse(jsonHours.get("51").getAsString().split("~")[1]));
                            if (cal.after(calStart) && cal.before(calEnd)) {
                                open = true;
                            }
                        }
                        if (jsonHours.get("52") != null) {
                            calStart.setTime(sdf.parse(jsonHours.get("52").getAsString().split("~")[0]));
                            calEnd.setTime(sdf.parse(jsonHours.get("52").getAsString().split("~")[1]));
                            if (cal.after(calStart) && cal.before(calEnd)) {
                                open = true;
                            }
                        }
                        if (jsonHours.get("53") != null) {
                            calStart.setTime(sdf.parse(jsonHours.get("53").getAsString().split("~")[0]));
                            calEnd.setTime(sdf.parse(jsonHours.get("53").getAsString().split("~")[1]));
                            if (cal.after(calStart) && cal.before(calEnd)) {
                                open = true;
                            }
                        }

                        if (open) {
                            strResHours.append("營業中 ");
                        } else {
                            strResHours.append("休息中 ");
                        }

                        if (jsonHours.get("51") != null) {
                            strResHours.append(jsonHours.get("51").getAsString() + "\n");
                        }
                        if (jsonHours.get("52") != null) {
                            strResHours.append("\t\t\t\t\t\t\t");
                            strResHours.append(jsonHours.get("52").getAsString() + "\n");
                        }
                        if (jsonHours.get("53") != null) {
                            strResHours.append("\t\t\t\t\t\t\t");
                            strResHours.append(jsonHours.get("53").getAsString() + "\n");
                        }

                        strResHours.deleteCharAt(strResHours.length() - 1);
                    }
                    break;
                case 7:
                    if (jsonHours.get("61") == null && jsonHours.get("62") == null && jsonHours.get("63") == null) {
                        strResHours.append("本日公休");
                    } else {
                        if (jsonHours.get("61") != null) {
                            calStart.setTime(sdf.parse(jsonHours.get("61").getAsString().split("~")[0]));
                            calEnd.setTime(sdf.parse(jsonHours.get("61").getAsString().split("~")[1]));
                            if (cal.after(calStart) && cal.before(calEnd)) {
                                open = true;
                            }
                        }
                        if (jsonHours.get("62") != null) {
                            calStart.setTime(sdf.parse(jsonHours.get("62").getAsString().split("~")[0]));
                            calEnd.setTime(sdf.parse(jsonHours.get("62").getAsString().split("~")[1]));
                            if (cal.after(calStart) && cal.before(calEnd)) {
                                open = true;
                            }
                        }
                        if (jsonHours.get("63") != null) {
                            calStart.setTime(sdf.parse(jsonHours.get("63").getAsString().split("~")[0]));
                            calEnd.setTime(sdf.parse(jsonHours.get("63").getAsString().split("~")[1]));
                            if (cal.after(calStart) && cal.before(calEnd)) {
                                open = true;
                            }
                        }

                        if (open) {
                            strResHours.append("營業中 ");
                        } else {
                            strResHours.append("休息中 ");
                        }

                        if (jsonHours.get("61") != null) {
                            strResHours.append(jsonHours.get("61").getAsString() + "\n");
                        }
                        if (jsonHours.get("62") != null) {
                            strResHours.append("\t\t\t\t\t\t\t");
                            strResHours.append(jsonHours.get("62").getAsString() + "\n");
                        }
                        if (jsonHours.get("63") != null) {
                            strResHours.append("\t\t\t\t\t\t\t");
                            strResHours.append(jsonHours.get("63").getAsString() + "\n");
                        }

                        strResHours.deleteCharAt(strResHours.length() - 1);
                    }
                    break;
                default:
                    break;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        tvResHours.setText(strResHours);

        tvResHoursDetail = view.findViewById(R.id.tvResHoursDetail);
        final boolean[] hoursDetailVisible = {false};
        StringBuilder strResHoursDetail = new StringBuilder(strResHours);
        strResHoursDetail.append("\n\n星期一 ");
        if (jsonHours.get("11") == null
                && jsonHours.get("12") == null
                && jsonHours.get("13") == null) {
            strResHoursDetail.append("休息\n");
        } else {
            if (jsonHours.get("11") != null) {
                strResHoursDetail.append(jsonHours.get("11").getAsString() + "\n");
            }
            if (jsonHours.get("12") != null) {
                strResHoursDetail.append("\t\t\t\t\t\t\t");
                strResHoursDetail.append(jsonHours.get("12").getAsString() + "\n");
            }
            if (jsonHours.get("13") != null) {
                strResHoursDetail.append("\t\t\t\t\t\t\t");
                strResHoursDetail.append(jsonHours.get("13").getAsString() + "\n");
            }
        }

        strResHoursDetail.append("星期二 ");
        if (jsonHours.get("21") == null
                && jsonHours.get("22") == null
                && jsonHours.get("23") == null) {
            strResHoursDetail.append("休息\n");
        } else {
            if (jsonHours.get("21") != null) {
                strResHoursDetail.append(jsonHours.get("21").getAsString() + "\n");
            }
            if (jsonHours.get("22") != null) {
                strResHoursDetail.append("\t\t\t\t\t\t\t");
                strResHoursDetail.append(jsonHours.get("22").getAsString() + "\n");
            }
            if (jsonHours.get("23") != null) {
                strResHoursDetail.append("\t\t\t\t\t\t\t");
                strResHoursDetail.append(jsonHours.get("23").getAsString() + "\n");
            }
        }

        strResHoursDetail.append("星期三 ");
        if (jsonHours.get("31") == null
                && jsonHours.get("32") == null
                && jsonHours.get("33") == null) {
            strResHoursDetail.append("休息\n");
        } else {
            if (jsonHours.get("31") != null) {
                strResHoursDetail.append(jsonHours.get("31").getAsString() + "\n");
            }
            if (jsonHours.get("32") != null) {
                strResHoursDetail.append("\t\t\t\t\t\t\t");
                strResHoursDetail.append(jsonHours.get("32").getAsString() + "\n");
            }
            if (jsonHours.get("33") != null) {
                strResHoursDetail.append("\t\t\t\t\t\t\t");
                strResHoursDetail.append(jsonHours.get("33").getAsString() + "\n");
            }
        }

        strResHoursDetail.append("星期四 ");
        if (jsonHours.get("41") == null
                && jsonHours.get("42") == null
                && jsonHours.get("43") == null) {
            strResHoursDetail.append("休息\n");
        } else {
            if (jsonHours.get("41") != null) {
                strResHoursDetail.append(jsonHours.get("41").getAsString() + "\n");
            }
            if (jsonHours.get("42") != null) {
                strResHoursDetail.append("\t\t\t\t\t\t\t");
                strResHoursDetail.append(jsonHours.get("42").getAsString() + "\n");
            }
            if (jsonHours.get("43") != null) {
                strResHoursDetail.append("\t\t\t\t\t\t\t");
                strResHoursDetail.append(jsonHours.get("43").getAsString() + "\n");
            }
        }

        strResHoursDetail.append("星期五 ");
        if (jsonHours.get("51") == null
                && jsonHours.get("52") == null
                && jsonHours.get("53") == null) {
            strResHoursDetail.append("休息\n");
        } else {
            if (jsonHours.get("51") != null) {
                strResHoursDetail.append(jsonHours.get("51").getAsString() + "\n");
            }
            if (jsonHours.get("52") != null) {
                strResHoursDetail.append("\t\t\t\t\t\t\t");
                strResHoursDetail.append(jsonHours.get("52").getAsString() + "\n");
            }
            if (jsonHours.get("53") != null) {
                strResHoursDetail.append("\t\t\t\t\t\t\t");
                strResHoursDetail.append(jsonHours.get("53").getAsString() + "\n");
            }
        }

        strResHoursDetail.append("星期六 ");
        if (jsonHours.get("61") == null
                && jsonHours.get("62") == null
                && jsonHours.get("63") == null) {
            strResHoursDetail.append("休息\n");
        } else {
            if (jsonHours.get("61") != null) {
                strResHoursDetail.append(jsonHours.get("61").getAsString() + "\n");
            }
            if (jsonHours.get("62") != null) {
                strResHoursDetail.append("\t\t\t\t\t\t\t");
                strResHoursDetail.append(jsonHours.get("62").getAsString() + "\n");
            }
            if (jsonHours.get("63") != null) {
                strResHoursDetail.append("\t\t\t\t\t\t\t");
                strResHoursDetail.append(jsonHours.get("63").getAsString() + "\n");
            }
        }

        strResHoursDetail.append("星期日 ");
        if (jsonHours.get("71") == null
                && jsonHours.get("72") == null
                && jsonHours.get("73") == null) {
            strResHoursDetail.append("休息\n");
        } else {
            if (jsonHours.get("71") != null) {
                strResHoursDetail.append(jsonHours.get("71").getAsString() + "\n");
            }
            if (jsonHours.get("72") != null) {
                strResHoursDetail.append("\t\t\t\t\t\t\t");
                strResHoursDetail.append(jsonHours.get("72").getAsString() + "\n");
            }
            if (jsonHours.get("73") != null) {
                strResHoursDetail.append("\t\t\t\t\t\t\t");
                strResHoursDetail.append(jsonHours.get("73").getAsString() + "\n");
            }
        }

        strResHoursDetail.deleteCharAt(strResHoursDetail.length() - 1);

        tvResHoursDetail.setOnClickListener(v -> {
            if (hoursDetailVisible[0]) {
                tvResHoursDetail.setText("▼");
                hoursDetailVisible[0] = false;
                tvResHours.setText(strResHours);
            } else {
                tvResHoursDetail.setText("▲");
                hoursDetailVisible[0] = true;
                tvResHours.setText(strResHoursDetail);
            }

        });

        rvImage = view.findViewById(R.id.rvImage);    //圖片recyclerView
        rvImage.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));


        imgs = getImgs();
        if (imgs.size() == 0) {
            rvImage.setVisibility(View.GONE);
            tvImageNumber.setVisibility(View.GONE);
            ivImage.setVisibility(View.GONE);
        } else {
            tvImageNumber.setText(String.valueOf(imgs.size()) + "張照片");
            showImgs(imgs);
        }

        Button btDirect = view.findViewById(R.id.btDirect);

        checkLocationSettings();

        btDirect.setOnClickListener(v -> {
            String destination = res.getResAddress();
            if (destination.isEmpty() || lastLocation == null) {
                Toast.makeText(activity, R.string.textLocationEmpty, Toast.LENGTH_SHORT).show();
                return;
            }

            // reverse geocode
            double latitude = lastLocation.getLatitude();
            double longitude = lastLocation.getLongitude();
            Address addressOrigin = reverseGeocode(latitude, longitude);
            Address addressDestination = getAddress(destination);
            boolean notFound = false;
            // 檢查是否可將起點、目的地轉成address
            if (addressOrigin == null) {
                Toast.makeText(activity, R.string.textOriginNotFound, Toast.LENGTH_SHORT).show();
                notFound = true;
            }
            if (addressDestination == null) {
                Toast.makeText(activity, R.string.textDestinationNotFound, Toast.LENGTH_SHORT).show();
                notFound = true;
            }
            if (notFound) {
                return;
            }

            double fromLat = addressOrigin.getLatitude();
            double fromLng = addressOrigin.getLongitude();
            double toLat = addressDestination.getLatitude();
            double toLng = addressDestination.getLongitude();

            direct(fromLat, fromLng, toLat, toLng);
        });

        //評價
        Button btResRating = view.findViewById(R.id.btResRating);
        btResRating.setOnClickListener(v -> {
            if (Common.USER_ID <= 0) {
                new AlertDialog.Builder(activity)
                        .setTitle("您尚未登入，要進行登入嗎？")
                        .setPositiveButton(R.string.textOK, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Navigation.findNavController(view)
                                        .navigate(R.id.action_resDetailFragment_to_loginFragment);
                            }
                        }).setNegativeButton(R.string.textCancel, null).create()
                        .show();
            } else {
                rating();
            }
        });

        //收藏
        ImageView ivMyRes = view.findViewById(R.id.ivMyRes);

        if (res.isMyRes()) {
            ivMyRes.setImageResource(R.drawable.ic_baseline_turned_in_24);
            ivMyRes.setColorFilter(Color.parseColor("#1877F2"));
        }

        ivMyRes.setOnClickListener(v -> {
            String urlMyRes = Common.URL_SERVER + "MyResServlet";
            if (Common.USER_ID <= 0) {
                new AlertDialog.Builder(activity)
                        .setTitle("您尚未登入，要進行登入嗎？")
                        .setPositiveButton(R.string.textOK, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Navigation.findNavController(v)
                                        .navigate(R.id.action_resDetailFragment_to_loginFragment);
                            }
                        }).setNegativeButton(R.string.textCancel, null).create()
                        .show();
            } else if (res.isMyRes()) {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("action", "myResDelete");
                jsonObject.addProperty("userId", Common.USER_ID);
                jsonObject.addProperty("resId", res.getResId());

                int count = 0;
                try {
                    String result = new CommonTask(urlMyRes, jsonObject.toString()).execute().get();
                    count = Integer.parseInt(result);
                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                }
                if (count == 0) {
                    Common.showToast(activity, R.string.textDeleteMyResFail);
                } else {
                    Common.showToast(activity, R.string.textDeleteMyResSuccess);
                    ivMyRes.setImageResource(R.drawable.ic_baseline_turned_in_not_24);
                    ivMyRes.setColorFilter(Color.parseColor("#424242"));
                    res.setMyRes(false);
                }
            } else {
                MyRes myRes = new MyRes(0, Common.USER_ID, res.getResId(), null);
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("action", "myResInsert");
                jsonObject.addProperty("myres", new Gson().toJson(myRes));

                int count = 0;
                try {
                    String result = new CommonTask(urlMyRes, jsonObject.toString()).execute().get();
                    count = Integer.parseInt(result);
                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                }
                if (count == 0) {
                    Common.showToast(activity, R.string.textInsertMyResFail);
                } else {
                    Common.showToast(activity, R.string.textInsertMyResSuccess);
                    ivMyRes.setImageResource(R.drawable.ic_baseline_turned_in_24);
                    ivMyRes.setColorFilter(Color.parseColor("#1877F2"));
                    res.setMyRes(true);
                }
            }

        });

        //todo 分享
//        ImageView ivShare = view.findViewById(R.id.ivShare);
//        ivShare.setOnClickListener(v -> {
//            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
//            sharingIntent.setType("text/plain");
//            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, res.getResAddress());
//            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, res.getResName());
//
//            startActivity(Intent.createChooser(sharingIntent, "chooserTitle"));
//        });

        //食記相關按鈕
        Button btReadArticle = view.findViewById(R.id.btReadArticle);
        Button btWriteArticle = view.findViewById(R.id.btWriteArticle);

        preferences = activity.getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE);

        btReadArticle.setOnClickListener(v -> {
            String showArticle = "SHOW_BACK";    //狀態判斷碼  > bundle帶到文章List
            bundle.putString("showArticle", showArticle);
            getActivity().getIntent().putExtra("res", res);
            Navigation.findNavController(v)
                    .navigate(R.id.action_resDetailFragment_to_articleFragment, bundle);
        });

        btWriteArticle.setOnClickListener(v -> {
            preferences.edit()
                    .putString("ResName", res.getResName())
                    .putString("Category", res.getResCategoryInfo())
                    .putInt("resId", res.getResId())
                    .apply();
            int newArticle = 0;    //狀態判斷碼  > bundle帶到insert文章
            bundle.putInt("newArticle", newArticle);
            Navigation.findNavController(v)
                    .navigate(R.id.action_resDetailFragment_to_articleInsertFragment, bundle);
        });
    }

    private void rating() {
        Bundle bundle = new Bundle();
        bundle.putSerializable("res", res);
        Intent ratingIntent = new Intent(activity, ResRatingActivity.class);
        ratingIntent.putExtras(bundle);
        startActivityForResult(ratingIntent, REQ_RATING);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQ_RATING:
                    //
                    break;
            }
        }
    }


    private class ImgAdapter extends RecyclerView.Adapter<ResDetailFragment.ImgAdapter.MyViewHolder> {
        private LayoutInflater layoutInflater;
        private List<Img> imgs;
        private int imageSize;

        ImgAdapter(Context context, List<Img> imgs) {
            layoutInflater = LayoutInflater.from(context);
            this.imgs = imgs;
            /* 螢幕寬度除以2當作將圖的尺寸 */
            imageSize = getResources().getDisplayMetrics().widthPixels / 3;
        }

        @Override
        public int getItemCount() {
            return imgs == null ? 0 : imgs.size();
        }

        void setImgs(List<Img> imgs) {
            this.imgs = imgs;
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            ImageView ivImage;

            public MyViewHolder(@NonNull View itemView) {
                super(itemView);
                ivImage = itemView.findViewById(R.id.ivImage);
            }
        }

        @NonNull
        @Override
        public ResDetailFragment.ImgAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = layoutInflater.inflate(R.layout.item_view_res_detail_image, parent, false);
            return new ResDetailFragment.ImgAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull ResDetailFragment.ImgAdapter.MyViewHolder myViewHolder, int position) {
            final Img img = imgs.get(position);
            String url = Common.URL_SERVER + "ImgServlet";
            int imgId = img.getImgId();
            ImageTask imageTask = new ImageTask(url, imgId, imageSize, myViewHolder.ivImage);
            imageTask.execute();
            imageTasks.add(imageTask);

            myViewHolder.itemView.setOnClickListener(v -> {
                Bundle bundle = new Bundle();
                bundle.putSerializable("res", res);
                navController.navigate(R.id.action_resDetailFragment_to_resImgFragment, bundle);
            });
        }

    }

    private List<Img> getImgs() {
        List<Img> imgs = null;
        if (Common.networkConnected(activity)) {
            String url = Common.URL_SERVER + "ResServlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "getImgByResId");
            jsonObject.addProperty("resId", res.getResId());
            String jsonOut = jsonObject.toString();
            imgGetAllTask = new CommonTask(url, jsonOut);
            try {
                String jsonIn = imgGetAllTask.execute().get();
                Type listType = new TypeToken<List<Img>>() {
                }.getType();
                imgs = new Gson().fromJson(jsonIn, listType);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        } else {
            Common.showToast(activity, R.string.textNoNetwork);
        }
        return imgs;
    }

    //文章圖片 > 顯示imgs
    private void showImgs(List<Img> imgs) {
        if (imgs == null || imgs.isEmpty()) {
            Common.showToast(activity, R.string.textNoImgFound);
        }
        ResDetailFragment.ImgAdapter imgAdapter = (ResDetailFragment.ImgAdapter) rvImage.getAdapter();
        if (imgAdapter == null) {
            rvImage.setAdapter(new ResDetailFragment.ImgAdapter(activity, imgs));
        } else {
            imgAdapter.setImgs(imgs);
            imgAdapter.notifyDataSetChanged();
        }
    }

    private void showResImg() {
        String url = Common.URL_SERVER + "ResServlet";
        int id = res.getResId();
        int imageSize = getResources().getDisplayMetrics().widthPixels / 3;
        Bitmap bitmap = null;
        try {
            bitmap = new ImageTask(url, id, imageSize).execute().get();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
        if (bitmap != null) {
            ivRes.setImageBitmap(bitmap);
        } else {
            ivRes.setImageResource(R.drawable.no_image);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (imgGetAllTask != null) {
            imgGetAllTask.cancel(true);
            imgGetAllTask = null;
        }

        if (imageTasks != null && imageTasks.size() > 0) {
            for (ImageTask imageTask : imageTasks) {
                imageTask.cancel(true);
            }
            imageTasks.clear();
        }
    }

    private void checkLocationSettings() {
        // 必須將LocationRequest設定加入檢查
        LocationSettingsRequest.Builder builder =
                new LocationSettingsRequest.Builder()
                        .addLocationRequest(locationRequest);
        Task<LocationSettingsResponse> task =
                LocationServices.getSettingsClient(activity)
                        .checkLocationSettings(builder.build());
        task.addOnSuccessListener(activity, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                // 取得並顯示最新位置
                showMyLocation();
            }
        });
        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    try {
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        // 跳出Location設定的對話視窗
                        resolvable.startResolutionForResult(activity, REQ_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException sendEx) {
                        //Log.e(TAG, e.getMessage());
                    }
                }
            }
        });
    }

    private void showMyLocation() {
        if (ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
        }

        if (fusedLocationClient == null) {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity);
            if (ActivityCompat.checkSelfPermission(activity,
                    Manifest.permission.ACCESS_FINE_LOCATION) ==
                    PackageManager.PERMISSION_DENIED) {
                //todo 如果拒絕
                return;
            }
            fusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    if (task.isSuccessful()) {
                        lastLocation = task.getResult();
                    }
                }
            });

            // 持續取得最新位置。looper設為null代表以現行執行緒呼叫callback方法，而非使用其他執行緒
            fusedLocationClient.requestLocationUpdates(
                    locationRequest, locationCallback, null);
        }
    }

    private Address reverseGeocode(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(activity);
        List<Address> addressList = null;
        try {
            addressList = geocoder.getFromLocation(latitude, longitude, 1);
        } catch (IOException e) {
            Log.e(TAG, e.toString());
        }

        if (addressList == null || addressList.isEmpty()) {
            return null;
        } else {
            return addressList.get(0);
        }
    }

    private void direct(double fromLat, double fromLng, double toLat,
                        double toLng) {
        String uriStr = String.format(Locale.CHINESE,
                "https://www.google.com/maps/dir/?api=1" +
                        "&origin=%f,%f&destination=%f,%f&travelmode=driving",
                fromLat, fromLng, toLat, toLng);
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uriStr));
        intent.setClassName("com.google.android.apps.maps",
                "com.google.android.maps.MapsActivity");
        startActivity(intent);
    }

    private Address getAddress(String locationName) {
        Geocoder geocoder = new Geocoder(activity);
        List<Address> addressList = null;
        try {
            addressList = geocoder.getFromLocationName(locationName, 1);
        } catch (IOException e) {
            Log.e(TAG, e.toString());
        }

        if (addressList == null || addressList.isEmpty()) {
            return null;
        } else {
            return addressList.get(0);
        }

    }
}