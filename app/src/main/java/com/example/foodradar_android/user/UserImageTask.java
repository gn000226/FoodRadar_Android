package com.example.foodradar_android.user;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.example.foodradar_android.R;
import com.google.gson.JsonObject;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;


public class UserImageTask extends AsyncTask<Object, Integer, Bitmap> {
    private final static String TAG = "TAG_ImageTask";
    private String url;
    private int id, imageSize;
    private WeakReference<ImageView> imageViewWeakReference;
    private Activity activity;

    // 取單張圖片
    public UserImageTask(String url, int id, int imageSize) {
        this(url, id, imageSize, null);
    }

//    public UserMyResImage(String url, int id, Activity activity, ImageView imageView) {
//        this.url = url;
//        this.id = id;
//        this.activity = activity;
//        this.imageViewWeakReference = new WeakReference<>(imageView);
//    }

    // 取完圖片後使用傳入的ImageView顯示，適用於顯示多張圖片
    public UserImageTask(String url, int id, int imageSize, ImageView imageView) {
        this.url = url;
        this.id = id;
        this.imageSize = imageSize;
        this.imageViewWeakReference = new WeakReference<>(imageView);
    }

    @Override
    protected Bitmap doInBackground(Object... params) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("action", "getImage");
        jsonObject.addProperty("id", id);
        jsonObject.addProperty("imageSize", imageSize);
        Log.d(TAG,"doInBackground.bitmap: " + id);
        return getRemoteImage(url, jsonObject.toString());
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        ImageView imageView = imageViewWeakReference.get();
        if (isCancelled() || imageView == null) {
            return;
        }
        Log.d(TAG,"onPostExecute.bitmap: " + bitmap);
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
            Log.d(TAG,"bitmap: " + bitmap);

//            if (activity != null){
//                BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
//                Bitmap drawableＧetBitmap = drawable.getBitmap();
//                new Common().setUserAvatra(activity, drawableＧetBitmap);
//            }

        } else {
            imageView.setImageResource(R.drawable.no_image);
        }
    }

    private Bitmap getRemoteImage(String url, String jsonOut) {
        HttpURLConnection connection = null;
        Bitmap bitmap = null;
        try {
            connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setDoInput(true); // allow inputs
            connection.setDoOutput(true); // allow outputs
            connection.setUseCaches(false); // do not use a cached copy
            connection.setRequestMethod("POST");
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
            bw.write(jsonOut);
            Log.d(TAG, "output: " + jsonOut);
            bw.close();

            int responseCode = connection.getResponseCode();

            if (responseCode == 200) {
                bitmap = BitmapFactory.decodeStream(
                        new BufferedInputStream(connection.getInputStream()));
            } else {
                Log.d(TAG, "response code: " + responseCode);
            }
        } catch (IOException e) {
            Log.e(TAG, e.toString());
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return bitmap;
    }
}