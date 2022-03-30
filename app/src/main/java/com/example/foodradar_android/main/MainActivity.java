package com.example.foodradar_android.main;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.foodradar_android.Common;
import com.example.foodradar_android.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "TAG_MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 設定app在背景時收到FCM，會自動顯示notification（前景時則不會自動顯示）
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create channel to show notifications.
            String channelId = getString(R.string.default_notification_channel_id);
            String channelName = getString(R.string.default_notification_channel_name);
            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(new NotificationChannel(channelId,
                    channelName, NotificationManager.IMPORTANCE_DEFAULT));
        }

        // 當notification被點擊時才會取得自訂資料
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String data = bundle.getString("data");
            Log.d(TAG, "data: " + data);
            Common.showToast(this, "data: " + data);
        }

        BottomNavigationView bottomNavigationView = findViewById(R.id.BottomNavigation);
        NavController navController = Navigation.findNavController(this, R.id.mainFragment);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static void enableBottomBar(Activity activity, int i) {
        BottomNavigationView bottomNavigationView = activity.findViewById(R.id.BottomNavigation);
        int k = bottomNavigationView.getSelectedItemId();
        if (i == k) {
            bottomNavigationView.getMenu().getItem(i).setEnabled(false);
        } else {
            bottomNavigationView.getMenu().getItem(i).setEnabled(true);
        }
    }


}


// Main Activity右上角選單
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater(); //inflater載入器
//        inflater.inflate(R.menu.appbar_menu, menu);
//        return super.onCreateOptionsMenu(menu);
//    }
//
//    @Override
//    public boolean onContextItemSelected(@NonNull MenuItem item) {
//
//        return super.onContextItemSelected(item);
//    }


