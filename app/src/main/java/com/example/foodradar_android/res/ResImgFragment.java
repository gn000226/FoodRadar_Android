package com.example.foodradar_android.res;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.example.foodradar_android.Common;
import com.example.foodradar_android.R;
import com.example.foodradar_android.article.Img;
import com.example.foodradar_android.task.CommonTask;
import com.example.foodradar_android.task.ImageTask;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


public class ResImgFragment extends Fragment {
    private NavController navController;
    private final static String TAG = "TAG_ResImgFragment";
    private FragmentActivity activity;
    private List<ImageTask> imageTasks;
    private RecyclerView rvImage;
    private List<Img> imgs;
    private CommonTask imgGetAllTask;
    private Res res;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        new Common();
        Common.setBackArrow(true, activity);
        setHasOptionsMenu(true);
        navController = Navigation.findNavController(activity, R.id.mainFragment);
        imageTasks = new ArrayList<>();
        res = (Res) (getArguments() != null ? getArguments().getSerializable("res") : null);
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
        activity.setTitle(R.string.titleResImg);
        return inflater.inflate(R.layout.fragment_res_img, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvImage = view.findViewById(R.id.rvImage);    //圖片recyclerView
        rvImage.setLayoutManager(new GridLayoutManager(activity, 2));

        imgs = getImgs();
        showImgs(imgs);
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
        ResImgFragment.ImgAdapter imgAdapter = (ResImgFragment.ImgAdapter) rvImage.getAdapter();
        if (imgAdapter == null) {
            rvImage.setAdapter(new ResImgFragment.ImgAdapter(activity, imgs));
        } else {
            imgAdapter.setImgs(imgs);
            imgAdapter.notifyDataSetChanged();
        }
    }

    private class ImgAdapter extends RecyclerView.Adapter<ResImgFragment.ImgAdapter.MyViewHolder> {
        private LayoutInflater layoutInflater;
        private List<Img> imgs;
        private int imageSize;

        ImgAdapter(Context context, List<Img> imgs) {
            layoutInflater = LayoutInflater.from(context);
            this.imgs = imgs;
            /* 螢幕寬度除以2當作將圖的尺寸 */
            imageSize = getResources().getDisplayMetrics().widthPixels / 2;
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
                ivImage.getLayoutParams().height = imageSize;
                ivImage.getLayoutParams().width = imageSize;
            }
        }

        @NonNull
        @Override
        public ResImgFragment.ImgAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = layoutInflater.inflate(R.layout.item_view_res_img, parent, false);
            return new ResImgFragment.ImgAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull ResImgFragment.ImgAdapter.MyViewHolder myViewHolder, int position) {
            final Img img = imgs.get(position);
            String url = Common.URL_SERVER + "ImgServlet";
            int imgId = img.getImgId();
            ImageTask imageTask = new ImageTask(url, imgId, imageSize, myViewHolder.ivImage);
            imageTask.execute();
            imageTasks.add(imageTask);

            myViewHolder.itemView.setOnClickListener(v -> {
                final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                View view = getLayoutInflater().inflate(R.layout.dialog_res_img, null);
                alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                alertDialog.setView(view);
                ImageTask bigImageTask = new ImageTask(url, imgId, getResources().getDisplayMetrics().widthPixels, view.findViewById(R.id.imageView));
                bigImageTask.execute();
                imageTasks.add(bigImageTask);
                alertDialog.show();
            });
        }

    }
}