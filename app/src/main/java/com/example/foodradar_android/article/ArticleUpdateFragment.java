package com.example.foodradar_android.article;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodradar_android.Common;
import com.example.foodradar_android.R;
import com.example.foodradar_android.task.CommonTask;
import com.example.foodradar_android.task.ImageTask;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.yalantis.ucrop.UCrop;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.ObjectInputStream;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import static android.app.Activity.RESULT_OK;


public class ArticleUpdateFragment extends Fragment {
    private static final String TAG = "TAG_articleUpadte";
    public Activity activity;
    private EditText etConNumUpdate, etArticleTitleUpdate, etArticleTextUpdate, etConAmountUpdate;
    private TextView tvResName;
    private RecyclerView rvUpdateImage;
    private ImageView ivPickIcon;
    private CommonTask articleGetAllTask;
    private CommonTask imgBaseTask;
    private CommonTask imageDeleteTask;
    private List<ImageTask> imageTasks;
    private List<Bitmap> imgList;
    private List<byte[]> imgBits;
    private Bitmap bitmap = null;
    private byte[] imgbit;
    private List<Img> imgs;
    private List<Img> imgsList;
    private Uri imageUri;
    private static final int REQ_TAKE_PICTURE = 0;  //?????????????????????????????????
    private static final int REQ_PICK_PICTURE = 1;  //?????????????????????????????????
    private static final int REQ_CROP_PICTURE = 2;  //?????????????????????????????????
    private static final int TYPE_PICK = 0;
    private static final int TYPE_IMAGE = 1;
    private Article article;
    private Integer articleIdBox = Article.ARTICLE_ID;
    private Integer userIdBox = Common.USER_ID; //?????????????????????ID
    private NavController navController; //?????????
    private Fragment fragment;
    private RecyclerView rvDefaultImage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        imageTasks = new ArrayList<>();
        imgs = new ArrayList<>();
    }


    //??????bottomNav
    @Override
    public void onStart() {
        super.onStart();
        /* ??????ActionBar */
        setHasOptionsMenu(true);
        /* ??????????????? */
        Common.setBackArrow(false, activity);
        setHasOptionsMenu(true);
        navController = Navigation.findNavController(activity, R.id.mainFragment);

        ArticleFragment.bottomNavSet(activity, 0);
    }


    /* ???????????????????????? */
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.article_insert_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            /* ??????????????? */
            case android.R.id.home:
                navController.popBackStack();
                break;
            /* ???????????????????????? */
            case R.id.menuSend:
                boolean textError = true;
                String conAmountStr = etConAmountUpdate.getText().toString();   //????????????
                String conNumStr = etConNumUpdate.getText().toString(); //????????????
                String articleTitle = etArticleTitleUpdate.getText().toString(); //????????????
                String articleText = etArticleTextUpdate.getText().toString();  //??????

                if (conAmountStr.isEmpty()) {
                    etConAmountUpdate.setError("???????????????????????????");
                    textError = false;
                }
                if (conNumStr.isEmpty()) {
                    etConNumUpdate.setError("???????????????????????????");
                    textError = false;
                }
                if (articleTitle.isEmpty()) {
                    etArticleTitleUpdate.setError("?????????????????????");
                    textError = false;
                }
                if (articleText.isEmpty()) {
                    etArticleTextUpdate.setError("?????????????????????");
                    textError = false;
                }
                if (!textError) {
                    return false;
                }
                int conAmountInt = Integer.parseInt(conAmountStr);
                int conNumInt = Integer.parseInt(conNumStr);

                /* ?????????????????????????????????????????? */
                SimpleDateFormat nowdate = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                nowdate.setTimeZone(TimeZone.getTimeZone("GMT+8"));    //????????????
                String strDate = nowdate.format(new java.util.Date());    //??????????????????
                String commentModifyTime = strDate;

                //????????????????????????(????????????)
                if (Common.networkConnected(activity)) {
                    String url = Common.URL_SERVER + "ArticleServlet";
                    Article article = new Article(articleTitle, articleText, commentModifyTime, conAmountInt, conNumInt, articleIdBox);
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("action", "articleUpdate");
                    jsonObject.addProperty("article", new Gson().toJson(article));
                    int count = 0;
                    try {
                        String result = new CommonTask(url, jsonObject.toString()).execute().get();
                        count = Integer.parseInt(result);
                    } catch (Exception e) {
                        Log.e(TAG, e.toString());
                    }
                    if (count == 0) {
                        Common.showToast(activity, "??????????????????");
                    } else {
                        Common.showToast(activity, "??????????????????");
                    }
                } else {
                    Common.showToast(activity, "????????????");
                }
                /* ????????????(Insert) */
                if (Common.networkConnected(activity)) {
                    String url = Common.URL_SERVER + "ImgServlet";
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("action", "imgInsert");
                    Img img = new Img(0, articleIdBox);
                    jsonObject.addProperty("img", new Gson().toJson(img));

                    //???????????????????????????????????????
                    int count = 0;
                    //for?????? > ????????????imgList?????????
                    for (int i = 0; i <= imgList.size() - 1; i++) {
                        byte[] imgBytes = Common.bitmapToByte(imgList.get(i));  //bitmap??????Byte
                        jsonObject.addProperty("imageBase64", Base64.encodeToString(imgBytes, Base64.DEFAULT));
                        try {
                            String result = new CommonTask(url, jsonObject.toString()).execute().get();
                            count = Integer.parseInt(result);
                        } catch (Exception e) {
                            Log.e(TAG, e.toString());
                        }
                    }
                    if (count != 0) {
                        Common.showToast(activity, "??????????????????");
                    }
                } else {
                    Common.showToast(activity, "????????????");
                }
                /* ?????????????????????????????? > ????????????Update????????? */
//                if (getFragmentManager() != null) {
//                    getFragmentManager().popBackStack();
//                }
                //????????????????????? > ?????????
                Navigation.findNavController(getView()).navigate(R.id.action_articleUpdateFragment_to_articleDetailFragment);
                break;

            /* ?????? */
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity.setTitle("????????????");
        return inflater.inflate(R.layout.fragment_article_update, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        etConNumUpdate = view.findViewById(R.id.etConNumUpdate);    //????????????edText
        etConAmountUpdate = view.findViewById(R.id.etConAmountUpdate);  //????????????edText
        etArticleTitleUpdate = view.findViewById(R.id.etArticleTitleUpdate);    //????????????edText
        etArticleTextUpdate = view.findViewById(R.id.etArticleTextUpdate);  //????????????edText
        tvResName = view.findViewById(R.id.tvResNameUpdate);    //????????????textView

        imgList = new ArrayList<>();

        /* ??????Bundle??????????????????edText??? */
        Bundle bundle = getArguments();
        if (bundle != null) {
            String conNumUpdate = bundle.getString("conNum");
            etConNumUpdate.setText(conNumUpdate);
            String conAmountUpdate = bundle.getString("conAmount");
            etConAmountUpdate.setText(conAmountUpdate);
            String articleTitleUpdate = bundle.getString("articleTitle");
            etArticleTitleUpdate.setText(articleTitleUpdate);
            String articleTextUpdate = bundle.getString("articleText");
            etArticleTextUpdate.setText(articleTextUpdate);

            /* ???????????????????????????TextView */
            String resName = bundle.getString("resName");
            String resCategory = bundle.getString("resCategory");
            tvResName.setText(resCategory + "\n" + "?????????" + resName);

            /* ImageDefaultRecyclerView > ?????????????????? */
            rvDefaultImage = view.findViewById(R.id.rvDefaultImage);
            rvDefaultImage.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));
            /* ???????????????????????????????????? */
            imgs = getImgs();
            showImgs(imgs);

            /* ImageRecyclerView > ???????????? */
            rvUpdateImage = view.findViewById(R.id.rvUpdateImage);
            rvUpdateImage.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));
            /* ?????????????????? */
            showUpdateImgs(imgList);
        }
    }

    private void showUpdateImgs(List<Bitmap> imgList) {
        if (imgsList == null || imgsList.isEmpty()) {
            UpdateAdapter imageAdapter = (UpdateAdapter) rvUpdateImage.getAdapter();
            if (imageAdapter == null) {
                rvUpdateImage.setAdapter(new UpdateAdapter(ivPickIcon, activity, imgList));
            } else {
                imageAdapter.setImgList(imgList);
                imageAdapter.notifyDataSetChanged();
            }
        }
    }

    /* ???????????? (??????)?????????articleId????????????????????????????????? */
    private List<Img> getImgs() {
        List<Img> imgs = null;
        if (Common.networkConnected(activity)) {
            String url = Common.URL_SERVER + "ImgServlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "getAllById");
            jsonObject.addProperty("articleId", articleIdBox);
            String jsonOut = jsonObject.toString();
            articleGetAllTask = new CommonTask(url, jsonOut);
            try {
                String jsonIn = articleGetAllTask.execute().get();
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

    /* ???????????????????????? */
    private void showImgs(List<Img> imgs) {
        if (imgs == null || imgs.isEmpty()) {
            Common.showToast(activity, R.string.textNoImgFound);
        }
        ImgAdapter imgAdapter = (ImgAdapter) rvDefaultImage.getAdapter();
        if (imgAdapter == null) {
            rvDefaultImage.setAdapter(new ImgAdapter(activity, imgs));
        } else {
            imgAdapter.setImgs(imgs);
            imgAdapter.notifyDataSetChanged();
        }
    }

    /* ??????ImageAdpter > ??????RecyclerView.Adapter */
    //????????????RecyclerView.ViewHolder > ???????????????recyclerView??????
    private class ImgAdapter extends RecyclerView.Adapter<ImgAdapter.MyViewHolder> {
        private LayoutInflater layoutInflater; //??????Inflater > ?????????
        private List<Img> imgs;
        private int imageSize;
        private Bitmap bitmapTest;

        public Bitmap getBitmapTest() {
            return bitmapTest;
        }

        public void setBitmapTest(Bitmap bitmapTest) {
            this.bitmapTest = bitmapTest;
        }

        /* ???????????? > ??? (??????) ???recyclerView??????????????? */
        ImgAdapter(Context context, List<Img> imgs) {
            layoutInflater = LayoutInflater.from(context);
            this.imgs = imgs;
            /* ??????????????????2????????????????????? */
            imageSize = getResources().getDisplayMetrics().widthPixels / 2;
        }

        public LayoutInflater getLayoutInflater() {
            return layoutInflater;
        }

        public void setLayoutInflater(LayoutInflater layoutInflater) {
            this.layoutInflater = layoutInflater;
        }

        public List<Img> getImgs() {
            return imgs;
        }

        public void setImgs(List<Img> imgs) {
            this.imgs = imgs;
        }

        public int getImageSize() {
            return imageSize;
        }

        public void setImageSize(int imageSize) {
            this.imageSize = imageSize;
        }

        /* ??????recyclerView????????? */
        @Override
        public int getItemCount() {
            return imgs == null ? 0 : (imgs.size());
        }

        /* ??????MyViewHolder 1.????????????????????????image > ??????RecyclerView.ViewHolder */
        public class MyViewHolder extends RecyclerView.ViewHolder {
            ImageView ivArticleImageDefault;

            public MyViewHolder(@NonNull View itemView) {
                super(itemView);
                ivArticleImageDefault = itemView.findViewById(R.id.ivArticleImage);
            }
        }

        /* ?????? > ???ImgAdapter?????????myViewHolder > ???????????????????????????????????? */
        @NonNull
        @Override
        public ImgAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView;
            itemView = layoutInflater.inflate(R.layout.article_image_view, parent, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull ImgAdapter.MyViewHolder holder, int position) {
            final Img img = imgs.get(position);
            /* ???????????????????????? */
            /* ?????????????????? > ??????Base64 */
            String url = Common.URL_SERVER + "ImgServlet";
            JsonObject jsonObjectBase = new JsonObject();
            int imgId = img.getImgId();
            jsonObjectBase.addProperty("action", "getImageBase");
            jsonObjectBase.addProperty("id", imgId);
            CommonTask imgBaseTaskTask = new CommonTask(url, jsonObjectBase.toString());
            byte[] imageByte;
            try {
                String jsonIn = imgBaseTaskTask.execute().get();
                JsonObject jObject = new Gson().fromJson(jsonIn, JsonObject.class);
                imageByte = Base64.decode(jObject.get("imageBase64").getAsString(), Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(imageByte, 0, imageByte.length);
                if (bitmap != null) {
                    new Common().setImage(activity, bitmap);
                    if (holder.ivArticleImageDefault != null) {
                        holder.ivArticleImageDefault.setImageBitmap(bitmap);
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
            /* ???????????? */
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    new AlertDialog.Builder(activity)
                            /* ???????????? */
                            .setTitle(R.string.textWarning)
                            /* ???????????? */
                            .setIcon(R.drawable.ic_baseline_warning_24)
                            /* ???????????? */
                            .setMessage(R.string.deleteImage)
                            /* ????????????(???) */
                            .setNegativeButton(R.string.deleteImageYes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    /* ???????????? */
                                    if (Common.networkConnected(activity)) {
                                        String url = Common.URL_SERVER + "ImgServlet";
                                        JsonObject jsonObject = new JsonObject();
                                        jsonObject.addProperty("action", "imgDelete");
                                        jsonObject.addProperty("imgId", imgId);
                                        int count = 0;
                                        try {
                                            imageDeleteTask = new CommonTask(url, jsonObject.toString());
                                            String result = imageDeleteTask.execute().get();
                                            count = Integer.parseInt(result);   //???result??????Int??????????????????
                                        } catch (Exception e) {
                                            Log.e(TAG, e.toString());
                                        }
                                        if (count == 0) {
                                            Common.showToast(activity, "??????????????????");
                                        } else {
                                            /* ??????????????????????????????List */
                                            imgs.remove(img);
                                            ImgAdapter.this.notifyDataSetChanged();
                                            ArticleUpdateFragment.this.imgs.remove(img);
                                        }
                                    } else {
                                        Common.showToast(activity, R.string.textNoNetwork);
                                    }
                                }
                            })
                            /* ????????????(???) */
                            .setPositiveButton(R.string.deleteImageNo, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // ??????????????????
                                    dialog.cancel();
                                }
                            })
                            .setCancelable(false) // ??????????????????????????????????????????true
                            .show();
                    return true;
                }
            });
        }
    }

    /* ????????????Adpter */
    private class UpdateAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private LayoutInflater layoutInflater;
        private List<Img> imgs;
        private ImageView pickIcon;
        private int imageSize;
        private List<Bitmap> imgList;

        public LayoutInflater getLayoutInflater() {
            return layoutInflater;
        }

        public void setLayoutInflater(LayoutInflater layoutInflater) {
            this.layoutInflater = layoutInflater;
        }

        public List<Img> getImgs() {
            return imgs;
        }

        public void setImgs(List<Img> imgs) {
            this.imgs = imgs;
        }

        public ImageView getPickIcon() {
            return pickIcon;
        }

        public void setPickIcon(ImageView pickIcon) {
            this.pickIcon = pickIcon;
        }

        public int getImageSize() {
            return imageSize;
        }

        public void setImageSize(int imageSize) {
            this.imageSize = imageSize;
        }

        public List<Bitmap> getImgList() {
            return imgList;
        }

        public void setImgList(List<Bitmap> imgList) {
            this.imgList = imgList;
        }

        //UpdateAdapter ????????????
        UpdateAdapter(ImageView pickIcon, Context context, List<Bitmap> imgList) {
            layoutInflater = LayoutInflater.from(context);
            this.imgs = imgs;
            this.pickIcon = pickIcon;
            this.imgList = imgList;
            /* ??????????????????2????????????????????? */
            imageSize = getResources().getDisplayMetrics().widthPixels / 2;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView;
            if (viewType == TYPE_PICK) {
                itemView = layoutInflater.inflate(R.layout.article_image_pick, parent, false);
                return new PickViewHolder(itemView);
            } else {
                itemView = layoutInflater.inflate(R.layout.article_image_insert, parent, false);
                return new MyViewHolder(itemView);
            }
        }

        private class PickViewHolder extends RecyclerView.ViewHolder {
            ImageView ivArticleImagePick;

            public PickViewHolder(@NonNull View itemView) {
                super(itemView);
                ivArticleImagePick = itemView.findViewById(R.id.ivArticleImagePick);
            }
        }

        private class MyViewHolder extends RecyclerView.ViewHolder {
            ImageView ivArticleImageInsert;

            public MyViewHolder(@NonNull View itemView) {
                super(itemView);
                ivArticleImageInsert = itemView.findViewById(R.id.ivArticleImageInsert);
            }
        }

        //?????????????????????
        @Override
        public int getItemViewType(int position) {
            if (position == 0) {
                return TYPE_PICK;   //????????????
            } else {
                return TYPE_IMAGE;
            }
        }

        @Override
        public int getItemCount() {
            return imgList == null ? 1 : (1 + imgList.size());
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof PickViewHolder) {
                PickViewHolder pickViewHolder = (PickViewHolder) holder;
                pickViewHolder.ivArticleImagePick.setImageResource(R.drawable.ic_add);
                pickViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //AlertDialog
                        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                        final String[] photo = {"??????", "??????"};
                        builder.setItems(photo, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == 0) {
                                    /* ???????????? */
                                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);    //intent?????? > ?????????????????????
                                    File file = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);   //File??????????????????
                                    file = new File(file, "picture.jpg");
                                    imageUri = FileProvider.getUriForFile(activity, activity.getOpPackageName() + ".provider", file);
                                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                                    //???Intent.resolveActivity()???????????????????????????app
                                    if (intent.resolveActivity(activity.getPackageManager()) != null) {
                                        startActivityForResult(intent, REQ_TAKE_PICTURE);   //????????????app??????????????????????????????
                                    } else {
                                        Common.showToast(activity, "???????????????");
                                    }
                                } else {
                                    /* ???????????? */
                                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                    startActivityForResult(intent, REQ_PICK_PICTURE); //???????????????intent??????????????????????????????
                                }
                            }
                        })
                                .setTitle("??????????????????")
                                .setCancelable(true) // ??????????????????????????????????????????true
                                .show();
                    }
                });
            } else {
                MyViewHolder myViewHolder = (MyViewHolder) holder;
                // position -1 > ??????????????????????????????onBindViewHolder???position????????????1???(0???PickViewHolder??????)
                // ???imgList??????????????????0??????????????????position???1 ??? ?????? position - 1 > ???
                Bitmap bitmapPosition = imgList.get(position - 1);
                myViewHolder.ivArticleImageInsert.setImageBitmap(bitmapPosition);
            }
        }

    }

    /* ?????????????????? */
    private void crop(Uri sourceImageUri) {
        //????????????????????????
        File file = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        file = new File(file, "picture_cropped.jpg"); //????????????????????????
        Uri destinationUri = Uri.fromFile(file); //????????????
        //UCrop > ???????????????
        UCrop.of(sourceImageUri, destinationUri).start(activity, this, REQ_CROP_PICTURE);
    }

    /* ???????????????????????? */
    private void handleCropResult(Intent intent) {

        //????????????????????????
        Uri resultUri = UCrop.getOutput(intent);
        // Bitmap bitmap = null;
        if (resultUri == null) {
            return;
        }
        try {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
                //??????resultUri????????????bitmap(????????????)
                bitmap = BitmapFactory.decodeStream(activity.getContentResolver().openInputStream(resultUri));
            } else {
                //????????????
                ImageDecoder.Source source = ImageDecoder.createSource(activity.getContentResolver(), resultUri);
                bitmap = ImageDecoder.decodeBitmap(source);
            }
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, output);
            imgbit = output.toByteArray();//???output?????????Byte??????
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
        if (bitmap != null) {
            imgList.add(bitmap);
            showUpdateImgs(imgList);
        }
    }

    /* ????????????????????????????????? */
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQ_TAKE_PICTURE:
                    crop(imageUri);
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

    //????????????????????????????????????
    @Override
    public void onStop() {
        super.onStop();
        if (articleGetAllTask != null) {
            articleGetAllTask.cancel(true);
            articleGetAllTask = null;
        }

        if (imageTasks != null && imageTasks.size() > 0) {
            for (ImageTask imageTask : imageTasks) {
                imageTask.cancel(true);
            }
            imageTasks.clear();
        }

        if (articleGetAllTask != null) {
            articleGetAllTask.cancel(true);
            articleGetAllTask = null;
        }
    }

}