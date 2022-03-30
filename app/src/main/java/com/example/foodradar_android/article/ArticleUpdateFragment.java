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
    private static final int REQ_TAKE_PICTURE = 0;  //設定拍照圖片請求狀態碼
    private static final int REQ_PICK_PICTURE = 1;  //設定取得圖片請求狀態碼
    private static final int REQ_CROP_PICTURE = 2;  //設定裁切圖片請求狀態碼
    private static final int TYPE_PICK = 0;
    private static final int TYPE_IMAGE = 1;
    private Article article;
    private Integer articleIdBox = Article.ARTICLE_ID;
    private Integer userIdBox = Common.USER_ID; //已登入的使用者ID
    private NavController navController; //返回鍵
    private Fragment fragment;
    private RecyclerView rvDefaultImage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        imageTasks = new ArrayList<>();
        imgs = new ArrayList<>();
    }


    //隱藏bottomNav
    @Override
    public void onStart() {
        super.onStart();
        /* 顯示ActionBar */
        setHasOptionsMenu(true);
        /* 左上返回鍵 */
        Common.setBackArrow(false, activity);
        setHasOptionsMenu(true);
        navController = Navigation.findNavController(activity, R.id.mainFragment);

        ArticleFragment.bottomNavSet(activity, 0);
    }


    /* 右上角，送出按鈕 */
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.article_insert_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            /* 左上返回鍵 */
            case android.R.id.home:
                navController.popBackStack();
                break;
            /* 右上送出修改發文 */
            case R.id.menuSend:
                boolean textError = true;
                String conAmountStr = etConAmountUpdate.getText().toString();   //消費金額
                String conNumStr = etConNumUpdate.getText().toString(); //消費人數
                String articleTitle = etArticleTitleUpdate.getText().toString(); //文章標題
                String articleText = etArticleTextUpdate.getText().toString();  //內文

                if (conAmountStr.isEmpty()) {
                    etConAmountUpdate.setError("請輸入正確消費金額");
                    textError = false;
                }
                if (conNumStr.isEmpty()) {
                    etConNumUpdate.setError("請輸入正確消費人數");
                    textError = false;
                }
                if (articleTitle.isEmpty()) {
                    etArticleTitleUpdate.setError("請輸入文章標題");
                    textError = false;
                }
                if (articleText.isEmpty()) {
                    etArticleTextUpdate.setError("請輸入文章內容");
                    textError = false;
                }
                if (!textError) {
                    return false;
                }
                int conAmountInt = Integer.parseInt(conAmountStr);
                int conNumInt = Integer.parseInt(conNumStr);

                /* 取得現在時間並格式化時間格式 */
                SimpleDateFormat nowdate = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                nowdate.setTimeZone(TimeZone.getTimeZone("GMT+8"));    //時區設定
                String strDate = nowdate.format(new java.util.Date());    //取得現在時間
                String commentModifyTime = strDate;

                //送出文章更新資料(不含圖片)
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
                        Common.showToast(activity, "資料更新失敗");
                    } else {
                        Common.showToast(activity, "發文更新成功");
                    }
                } else {
                    Common.showToast(activity, "連線失敗");
                }
                /* 新增圖片(Insert) */
                if (Common.networkConnected(activity)) {
                    String url = Common.URL_SERVER + "ImgServlet";
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("action", "imgInsert");
                    Img img = new Img(0, articleIdBox);
                    jsonObject.addProperty("img", new Gson().toJson(img));

                    //確認是否有取得圖檔才會上傳
                    int count = 0;
                    //for迴圈 > 迭代取出imgList的資料
                    for (int i = 0; i <= imgList.size() - 1; i++) {
                        byte[] imgBytes = Common.bitmapToByte(imgList.get(i));  //bitmap轉為Byte
                        jsonObject.addProperty("imageBase64", Base64.encodeToString(imgBytes, Base64.DEFAULT));
                        try {
                            String result = new CommonTask(url, jsonObject.toString()).execute().get();
                            count = Integer.parseInt(result);
                        } catch (Exception e) {
                            Log.e(TAG, e.toString());
                        }
                    }
                    if (count != 0) {
                        Common.showToast(activity, "圖片更新成功");
                    }
                } else {
                    Common.showToast(activity, "連線失敗");
                }
                /* 結束動作後返回前一頁 > 第二次進Update會閃退 */
//                if (getFragmentManager() != null) {
//                    getFragmentManager().popBackStack();
//                }
                //方法為新增頁面 > 暫時解
                Navigation.findNavController(getView()).navigate(R.id.action_articleUpdateFragment_to_articleDetailFragment);
                break;

            /* 預設 */
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity.setTitle("更新文章");
        return inflater.inflate(R.layout.fragment_article_update, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        etConNumUpdate = view.findViewById(R.id.etConNumUpdate);    //消費人數edText
        etConAmountUpdate = view.findViewById(R.id.etConAmountUpdate);  //消費金額edText
        etArticleTitleUpdate = view.findViewById(R.id.etArticleTitleUpdate);    //文章標題edText
        etArticleTextUpdate = view.findViewById(R.id.etArticleTextUpdate);  //文章內文edText
        tvResName = view.findViewById(R.id.tvResNameUpdate);    //餐廳名稱textView

        imgList = new ArrayList<>();

        /* 取得Bundle資料，顯示在edText上 */
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

            /* 取餐廳資訊，顯示在TextView */
            String resName = bundle.getString("resName");
            String resCategory = bundle.getString("resCategory");
            tvResName.setText(resCategory + "\n" + "餐廳：" + resName);

            /* ImageDefaultRecyclerView > 顯示文章原圖 */
            rvDefaultImage = view.findViewById(R.id.rvDefaultImage);
            rvDefaultImage.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));
            /* 取得原本文章的圖片並顯示 */
            imgs = getImgs();
            showImgs(imgs);

            /* ImageRecyclerView > 新增圖片 */
            rvUpdateImage = view.findViewById(R.id.rvUpdateImage);
            rvUpdateImage.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));
            /* 顯示新增圖片 */
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

    /* 取得圖片 (資訊)，特定articleId的圖片資訊，先不取圖片 */
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

    /* 顯示原本圖片方法 */
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

    /* 宣告ImageAdpter > 繼承RecyclerView.Adapter */
    //泛型需為RecyclerView.ViewHolder > 才能客製化recyclerView項目
    private class ImgAdapter extends RecyclerView.Adapter<ImgAdapter.MyViewHolder> {
        private LayoutInflater layoutInflater; //宣告Inflater > 橋接器
        private List<Img> imgs;
        private int imageSize;
        private Bitmap bitmapTest;

        public Bitmap getBitmapTest() {
            return bitmapTest;
        }

        public void setBitmapTest(Bitmap bitmapTest) {
            this.bitmapTest = bitmapTest;
        }

        /* 建構方法 > 將 (圖片) 與recyclerView接合的方法 */
        ImgAdapter(Context context, List<Img> imgs) {
            layoutInflater = LayoutInflater.from(context);
            this.imgs = imgs;
            /* 螢幕寬度除以2當作將圖的尺寸 */
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

        /* 決定recyclerView的個數 */
        @Override
        public int getItemCount() {
            return imgs == null ? 0 : (imgs.size());
        }

        /* 宣告MyViewHolder 1.顯示出原本文章的image > 繼承RecyclerView.ViewHolder */
        public class MyViewHolder extends RecyclerView.ViewHolder {
            ImageView ivArticleImageDefault;

            public MyViewHolder(@NonNull View itemView) {
                super(itemView);
                ivArticleImageDefault = itemView.findViewById(R.id.ivArticleImage);
            }
        }

        /* 圖片 > 將ImgAdapter接合到myViewHolder > 判斷顯示圖片還是新增圖片 */
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
            /* 取得顯示預設圖片 */
            /* 取得文章圖片 > 透過Base64 */
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
            /* 刪除圖片 */
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    new AlertDialog.Builder(activity)
                            /* 設定標題 */
                            .setTitle(R.string.textWarning)
                            /* 設定圖示 */
                            .setIcon(R.drawable.ic_baseline_warning_24)
                            /* 設定文字 */
                            .setMessage(R.string.deleteImage)
                            /* 設定選項(左) */
                            .setNegativeButton(R.string.deleteImageYes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    /* 刪除動作 */
                                    if (Common.networkConnected(activity)) {
                                        String url = Common.URL_SERVER + "ImgServlet";
                                        JsonObject jsonObject = new JsonObject();
                                        jsonObject.addProperty("action", "imgDelete");
                                        jsonObject.addProperty("imgId", imgId);
                                        int count = 0;
                                        try {
                                            imageDeleteTask = new CommonTask(url, jsonObject.toString());
                                            String result = imageDeleteTask.execute().get();
                                            count = Integer.parseInt(result);   //將result轉為Int型別傳到後端
                                        } catch (Exception e) {
                                            Log.e(TAG, e.toString());
                                        }
                                        if (count == 0) {
                                            Common.showToast(activity, "圖片刪除異常");
                                        } else {
                                            /* 圖時刪除畫面上圖片的List */
                                            imgs.remove(img);
                                            ImgAdapter.this.notifyDataSetChanged();
                                            ArticleUpdateFragment.this.imgs.remove(img);
                                        }
                                    } else {
                                        Common.showToast(activity, R.string.textNoNetwork);
                                    }
                                }
                            })
                            /* 設定選項(右) */
                            .setPositiveButton(R.string.deleteImageNo, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // 關閉對話視窗
                                    dialog.cancel();
                                }
                            })
                            .setCancelable(false) // 必須點擊按鈕方能關閉，預設為true
                            .show();
                    return true;
                }
            });
        }
    }

    /* 新增圖片Adpter */
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

        //UpdateAdapter 建構方法
        UpdateAdapter(ImageView pickIcon, Context context, List<Bitmap> imgList) {
            layoutInflater = LayoutInflater.from(context);
            this.imgs = imgs;
            this.pickIcon = pickIcon;
            this.imgList = imgList;
            /* 螢幕寬度除以2當作將圖的尺寸 */
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

        //區分顯示的種類
        @Override
        public int getItemViewType(int position) {
            if (position == 0) {
                return TYPE_PICK;   //選擇圖片
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
                        final String[] photo = {"相機", "相簿"};
                        builder.setItems(photo, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == 0) {
                                    /* 使用相機 */
                                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);    //intent物件 > 意圖取得圖片檔
                                    File file = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);   //File取得外部圖檔
                                    file = new File(file, "picture.jpg");
                                    imageUri = FileProvider.getUriForFile(activity, activity.getOpPackageName() + ".provider", file);
                                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                                    //叫Intent.resolveActivity()檢查有無⽀援拍照的app
                                    if (intent.resolveActivity(activity.getPackageManager()) != null) {
                                        startActivityForResult(intent, REQ_TAKE_PICTURE);   //找到相機app就發出需求，取得照片
                                    } else {
                                        Common.showToast(activity, "找不到相機");
                                    }
                                } else {
                                    /* 讀取相簿 */
                                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                    startActivityForResult(intent, REQ_PICK_PICTURE); //找到相簿後intent就發出請求，取得相片
                                }
                            }
                        })
                                .setTitle("選擇圖片來源")
                                .setCancelable(true) // 必須點擊按鈕方能關閉，預設為true
                                .show();
                    }
                });
            } else {
                MyViewHolder myViewHolder = (MyViewHolder) holder;
                // position -1 > 因為每增加一筆資料，onBindViewHolder的position會自動加1，(0被PickViewHolder綁住)
                // 但imgList的索引值是從0開始，對不上position的1 ， 所以 position - 1 > 跟
                Bitmap bitmapPosition = imgList.get(position - 1);
                myViewHolder.ivArticleImageInsert.setImageBitmap(bitmapPosition);
            }
        }

    }

    /* 圖片裁減方法 */
    private void crop(Uri sourceImageUri) {
        //取得外部檔案路徑
        File file = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        file = new File(file, "picture_cropped.jpg"); //切割過後儲存檔名
        Uri destinationUri = Uri.fromFile(file); //檔案路徑
        //UCrop > 圖片裁剪框
        UCrop.of(sourceImageUri, destinationUri).start(activity, this, REQ_CROP_PICTURE);
    }

    /* 圖片裁剪後的動作 */
    private void handleCropResult(Intent intent) {

        //取得裁減後的圖片
        Uri resultUri = UCrop.getOutput(intent);
        // Bitmap bitmap = null;
        if (resultUri == null) {
            return;
        }
        try {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
                //取得resultUri後解碼為bitmap(舊版方法)
                bitmap = BitmapFactory.decodeStream(activity.getContentResolver().openInputStream(resultUri));
            } else {
                //新版方法
                ImageDecoder.Source source = ImageDecoder.createSource(activity.getContentResolver(), resultUri);
                bitmap = ImageDecoder.decodeBitmap(source);
            }
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, output);
            imgbit = output.toByteArray();//將output解碼成Byte陣列
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
        if (bitmap != null) {
            imgList.add(bitmap);
            showUpdateImgs(imgList);
        }
    }

    /* 取得上述動作執行的結果 */
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

    //生命週期結束，釋放記憶體
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