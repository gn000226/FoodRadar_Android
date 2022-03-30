package com.example.foodradar_android.article;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Gravity;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.example.foodradar_android.Common;
import com.example.foodradar_android.R;
import com.example.foodradar_android.article.ResAddress;
import com.example.foodradar_android.task.CommonTask;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;


public class ResAddressFragment extends Fragment {
    private static final String TAG = "TAG_ResListFragment";
    private Activity activity;
    private RecyclerView rvResAddress;
    private SearchView svResAddress;
    private CommonTask resAddressGetAllTask;
    private List<ResAddress> resAddresses;
    private NavController navController;
    private final static String PREFERENCES_NAME = "Res";
    private SharedPreferences preferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();

        // 顯示左上角的返回箭頭
        Common.setBackArrow(true, activity);
        setHasOptionsMenu(true);

        navController =
                Navigation.findNavController(activity, R.id.mainFragment);
    }

    // 顯示右上角的OptionMenu選單
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
//        inflater.inflate(R.menu.appbar_menu,menu);  // 從res取用選項的清單“R.menu.my_menu“
//        super.onCreateOptionsMenu(menu, inflater);
    }
    // 顯示右上角的OptionMenu選單
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
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
        activity.setTitle("選餐廳");
        return inflater.inflate(R.layout.fragment_res_address, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        svResAddress = view.findViewById(R.id.svResAddress);    //搜尋
        rvResAddress = view.findViewById(R.id.rvResAddress);    //餐廳列表
        rvResAddress.setLayoutManager(new LinearLayoutManager(activity));

        preferences = activity.getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE);

        //分隔線
        rvResAddress.addItemDecoration(new DividerItemDecoration(activity, DividerItemDecoration.VERTICAL));
        resAddresses = getRes();
        showRess(resAddresses);

        //  searchView
        svResAddress.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    showRess(resAddresses);
                } else {
                    List<ResAddress> searchRes = new ArrayList<>();
                    for (ResAddress res : resAddresses) {
                        if ((res.getResName().toUpperCase().contains(newText.toUpperCase())) ||
                                (res.getResAddress().toUpperCase().contains(newText.toUpperCase()))) {
                            searchRes.add(res);
                        }
                    }
                    showRess(searchRes);
                }
                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
        });
    }

    private List<ResAddress> getRes() {
        List<ResAddress> resAddresses = null;
        if (Common.networkConnected(activity)) {
            String url = Common.URL_SERVER + "ResAddressServlet"; //連線server端
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "getRes");
            String jsonOut = jsonObject.toString();
            resAddressGetAllTask = new CommonTask(url, jsonOut);
            try {
                String jsonInp = resAddressGetAllTask.execute().get();
                Type listType = new TypeToken<List<ResAddress>>() {
                }.getType();
                resAddresses = new Gson().fromJson(jsonInp, listType);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        } else {
            Common.showToast(activity, R.string.textNoNetwork);
        }
        return resAddresses;
    }

    private void showRess(List<ResAddress> resAddresses) {
        if (resAddresses == null || resAddresses.isEmpty()) {
            Common.showToast(activity, R.string.textNoRessFound);
            Log.e(TAG, "resAddress:" + resAddresses);
        }
            ResAddressAdapter resAddressAdapter = (ResAddressAdapter) rvResAddress.getAdapter();
            if (resAddressAdapter == null) {
                rvResAddress.setAdapter(new ResAddressAdapter(activity, resAddresses));
            } else {
                resAddressAdapter.setResAddresses(resAddresses);
                resAddressAdapter.notifyDataSetChanged();
            }
    }

    private class ResAddressAdapter extends RecyclerView.Adapter<ResAddressAdapter.MyViewHolder> {
        private LayoutInflater layoutInflater;  //橋接器
        private List<ResAddress> resAddresses;


        //??
        ResAddressAdapter(Context context, List<ResAddress> resAddresses) {
            layoutInflater = LayoutInflater.from(context);
            this.resAddresses = resAddresses;
        }

        void setResAddresses(List<ResAddress> resAddresses) {
            this.resAddresses = resAddresses;
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            TextView tvAddressResName, tvResAddress;

            MyViewHolder(@NonNull View itemView) {
                super(itemView);
                tvAddressResName = itemView.findViewById(R.id.tvAddressResName);
                tvResAddress = itemView.findViewById(R.id.tvResAddress);
            }
        }

        @Override
        public int getItemCount() {
            return resAddresses == null ? 0 : resAddresses.size();
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = layoutInflater.inflate(R.layout.res_adress_view, parent, false);
            return new MyViewHolder(itemView);  //將itemView放入MyViewHolder中
        }

//        @Override
//        public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
//            super.onAttachedToRecyclerView(recyclerView);
//            View headLayout = layoutInflater.inflate(R.layout.article_image_pick, null);
//          ResAddressAdapter.addHeaderView
//
//        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int position) {
            final ResAddress resAddress = resAddresses.get(position);
            myViewHolder.tvAddressResName.setText(resAddress.getResName());
            myViewHolder.tvResAddress.setText(resAddress.getResAddress());

            myViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   preferences.edit()
                           .putString("ResName", resAddress.getResName())
                           .putString("Category", resAddress.getResCategoryInfo())
                           .putInt("resId", resAddress.getResId())
                           .apply();

                    navController.popBackStack();
//                    Bundle bundle = new Bundle();
//                    bundle.putSerializable("ResAddress", resAddress);
//                    Navigation.findNavController(v).navigate(R.id.action_resAddressFragment_to_articleInsertFragment, bundle);
                }

            });
        }

    }

    @Override
    public void onStop() {
        super.onStop();
        if (resAddressGetAllTask != null) {
            resAddressGetAllTask.cancel(true);
            resAddressGetAllTask = null;
        }
    }
}
