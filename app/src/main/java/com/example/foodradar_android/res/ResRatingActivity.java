package com.example.foodradar_android.res;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;

import com.example.foodradar_android.Common;
import com.example.foodradar_android.R;
import com.example.foodradar_android.task.CommonTask;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class ResRatingActivity extends AppCompatActivity {
    private final static String TAG = "TAG_ResRatingActivity";
    private Res res;
    private RatingBar ratingBar;
    private Float rating;
    private ResRating resRating;
    private CommonTask getRatingTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_res_rating);
        res = (Res) (this.getIntent() != null ? this.getIntent().getSerializableExtra("res") : null);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (res != null) {
            setTitle("您給" + res.getResName() + "的評價");
        } else {
            Common.showToast(this, R.string.textNoRessFound);
            finish();
        }

        ratingBar = findViewById(R.id.ratingBar);
        getRating();

        Button btResRatingInsert = findViewById(R.id.btResRatingInsert);
        btResRatingInsert.setOnClickListener(v -> {
            rating = ratingBar.getRating();

            if (Common.networkConnected(this)) {
                String url = Common.URL_SERVER + "ResServlet";
                if (resRating == null) {
                    ResRating resRating = new ResRating(0, res.getResId(), Common.USER_ID, rating);
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("action", "insertResRating");
                    jsonObject.addProperty("resRating", new Gson().toJson(resRating));

                    int count = 0;
                    try {
                        String result = new CommonTask(url, jsonObject.toString()).execute().get();
                        count = Integer.parseInt(result);
                    } catch (Exception e) {
                        Log.e(TAG, e.toString());
                    }
                    if (count == 0) {
                        Common.showToast(this, R.string.textInsertResRatingFail);
                    } else {
                        Common.showToast(this, R.string.textInsertResRatingSuccess);
                    }
                } else {
                    resRating.setRating(rating);
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("action", "updateResRating");
                    jsonObject.addProperty("resRating", new Gson().toJson(resRating));

                    int count = 0;
                    try {
                        String result = new CommonTask(url, jsonObject.toString()).execute().get();
                        count = Integer.parseInt(result);
                    } catch (Exception e) {
                        Log.e(TAG, e.toString());
                    }
                    if (count == 0) {
                        Common.showToast(this, R.string.textUpdateResRatingFail);
                    } else {
                        Common.showToast(this, R.string.textUpdateResRatingSuccess);
                    }
                }
            } else {
                Common.showToast(this, R.string.textNoNetwork);
            }

            finish();
        });

        Button btCancel = findViewById(R.id.btCancel);
        btCancel.setOnClickListener(v -> {
            finish();
        });
    }

    private void getRating() {
        if (Common.networkConnected(this)) {
            String url = Common.URL_SERVER + "ResServlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "findRatingByResIdAndUserId");
            jsonObject.addProperty("resId", res.getResId());
            jsonObject.addProperty("userId", Common.USER_ID);
            String jsonOut = jsonObject.toString();
            getRatingTask = new CommonTask(url, jsonOut);
            try {
                String jsonIn = getRatingTask.execute().get();
                Type listType = new TypeToken<ResRating>() {
                }.getType();
                resRating = new Gson().fromJson(jsonIn, listType);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        } else {
            Common.showToast(this, R.string.textNoNetwork);
        }

        if (resRating != null) {
            ratingBar.setRating(resRating.getRating());
        }
    }
}