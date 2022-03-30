package com.example.foodradar_android;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.example.foodradar_android.task.CommonTask;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.JsonObject;


public class FcmFragment extends Fragment {
    private static final String TAG = "TAG_FcmFragment";
    private Activity activity;
    private NavController navController;
    private EditText etTitle, etBody, etData;
    private TextView textView;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        getTokenSendServer();
        Common.setBackArrow(true, activity);
        setHasOptionsMenu(true);
        navController =
                Navigation.findNavController(activity, R.id.mainFragment);
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_fcm_item, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        etTitle = view.findViewById(R.id.etTitle);
        etBody = view.findViewById(R.id.etBody);
        etData = view.findViewById(R.id.etData);
        textView = view.findViewById(R.id.textView);

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String topic = "health";
                switch (v.getId()) {
                    // 發送訊息至模擬器不成功時，需將其冷開機
                    case R.id.btSendSingleFcm:
                        sendFcm(true);
                        break;
                    case R.id.btSendGroupFcm:
                        sendFcm(false);
                        break;
                    case R.id.btSubscribeHealth:
                        subscribe(topic); // 訂閱主題
                        break;
                    case R.id.btSendSubscriber:
                        sendSubscriber(topic); // 發送訂閱
                        break;
                    case R.id.btGetToken:
                        getTokenSendServer();
                        break;
                    case R.id.btClear:
                        clearFields();
                        break;
                }
            }
        };

        view.findViewById(R.id.btSendSingleFcm).setOnClickListener(onClickListener);
        view.findViewById(R.id.btSendGroupFcm).setOnClickListener(onClickListener);
        view.findViewById(R.id.btSubscribeHealth).setOnClickListener(onClickListener);
        view.findViewById(R.id.btSendSubscriber).setOnClickListener(onClickListener);
        view.findViewById(R.id.btGetToken).setOnClickListener(onClickListener);
        view.findViewById(R.id.btClear).setOnClickListener(onClickListener);
    }

    private void subscribe(String topic) {
        FirebaseMessaging.getInstance().subscribeToTopic(topic)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Common.showToast(activity, R.string.textSubscribed);
                        } else {
                            Common.showToast(activity, R.string.textSubscribeFailed);
                        }
                    }
                });
    }

    private void sendSubscriber(String topic) {
        if (Common.networkConnected(activity)) {
            String url = Common.URL_SERVER + "FcmBasicServlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "subscriberFcm");
            jsonObject.addProperty("topic", topic);
            String jsonOut = jsonObject.toString();
            CommonTask sendFcmTask = new CommonTask(url, jsonOut);
            try {
                String result = sendFcmTask.execute().get();
                textView.setText(result);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Common.showToast(activity, R.string.textNoNetwork);
        }
    }

    private void sendFcm(boolean single) {
        // 區別發送單一或是群組FCM
        String action = single ? "singleFcm" : "groupFcm";
        if (Common.networkConnected(activity)) {
            String url = Common.URL_SERVER + "FcmBasicServlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", action);
            jsonObject.addProperty("title", etTitle.getText().toString());
            jsonObject.addProperty("body", etBody.getText().toString());
            jsonObject.addProperty("data", etData.getText().toString());
            String jsonOut = jsonObject.toString();
            CommonTask sendFcmTask = new CommonTask(url, jsonOut);
            try {
                String result = sendFcmTask.execute().get();
                textView.setText(result);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Common.showToast(activity, R.string.textNoNetwork);
        }
    }
    private void getTokenSendServer() {
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult() != null) {
                                String token = task.getResult().getToken();
                                Common.sendTokenToServer(token, activity);
                                textView.setText(token);
                                Log.d(TAG, "token: " + token);
                            }
                        } else {
                            Log.e(TAG, "getInstanceId failed: " + task.getException());
                        }
                    }
                });
    }
    private void clearFields() {
        etTitle.setText("");
        etBody.setText("");
        textView.setText("");
    }
    public FcmFragment() {
        // Required empty public constructor
    }
}