package com.example.foodradar_android.article;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.foodradar_android.Common;
import com.example.foodradar_android.R;
import com.example.foodradar_android.res.Res;
import com.example.foodradar_android.task.CommonTask;
import com.example.foodradar_android.task.ImageTask;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.yalantis.ucrop.UCrop;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

public class ArticleInsertFragment extends Fragment {
    private final static String TAG = "ArticleInsertFragment";
    private AppCompatActivity appCompatActivity;
    public Activity activity;
    private EditText etConNum, etArticleTitle, etArticleText, etConAmount;
    private TextView tvResName;
    private RecyclerView rvInsertImage;
    private ImageView ivPlaceIcon;
    //    private List<ResAddress> resAddresses;
    private ResAddress resAddresses;
    private static final int TYPE_PICK = 0; //rvImage
    private static final int TYPE_IMAGE = 1;
    private List<ImageTask> imageTasks;
    private byte[] imgbit;
    private List<Img> imgs;
    private Img img;
    private Res res;
    private List<Bitmap> imgList;
    private Uri imageUri;
    private static final int REQ_TAKE_PICTURE = 0;  //設定拍照圖片請求狀態碼
    private static final int REQ_PICK_PICTURE = 1;  //設定取得圖片請求狀態碼
    private static final int REQ_CROP_PICTURE = 2;  //設定裁切圖片請求狀態碼
    private Bitmap bitmap = null;
    private NavController navController;
    private int userIdBox = Common.USER_ID;

    private final static String PREFERENCES_NAME = "Res";   //儲存檔名
    private final static String DEFAULT_FILE_NAME = " "; //抓不到檔案就顯示
    private SharedPreferences preferences;
    private String conNumStr, conAmountStr, articleTitle, articleText;
    private int newArticle;
    private Button strButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        setHasOptionsMenu(true);

        // 顯示左上角的返回箭頭
        Common.setBackArrow(true, activity);
        setHasOptionsMenu(true);
        navController =
                Navigation.findNavController(activity, R.id.mainFragment);

        //三元 > bundle取得餐廳資訊
        resAddresses = (ResAddress) (getArguments() != null ? getArguments().getSerializable("ResAddress") : null);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity.setTitle("發文");
        return inflater.inflate(R.layout.fragment_article_insert, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        etConNum = view.findViewById(R.id.etConNum);    //輸入消費人數
        etConAmount = view.findViewById(R.id.etConAmount);  //輸入消費金額
        etArticleTitle = view.findViewById(R.id.etArticleTitle);    //輸入標題
        etArticleText = view.findViewById(R.id.etArticleText);  //輸入內文
        tvResName = view.findViewById(R.id.tvResName);
        imgList = new ArrayList<>();

        //輸入文字
        conNumStr = etConNum.getText().toString().trim();
        conAmountStr = etConAmount.getText().toString().trim();
        articleTitle = etArticleTitle.getText().toString().trim();
        articleText = etArticleText.getText().toString().trim();

        //隱藏按鈕 > 點擊設定
        strButton = view.findViewById(R.id.strButton);
        strButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etArticleTitle.setText("聚馥園│烤鴨兩吃划算又美味，肴豬腳也值得一試");
                etArticleText.setText("店名： 聚馥園餐廳\n" +
                        "地址： 台北市中山區南京東路三段219號3樓\n" +
                        "電話： 02-2718-5588\n" +
                        "價格： 600-1000 /人\n" +
                        "營業時間：11:00–14:00, 17:00–21:30\n" +
                        "\n" +
                        "文學大師梁實秋在他的《雅舍談吃》裡寫了《生炒鱔魚絲》，少數我覺得大師你還是別吃這道菜的文章之一 (大笑)，他寫道『那鱔魚雖名為炒，卻不是生炒，是煮熟之後再炒，已經十分油膩。上桌之後侍者還要手持一隻又 黑又髒的搪瓷碗（希望不是漱口杯），澆上一股子沸開的油，噝啦一聲，油直冒泡，然後就有熱心人用筷子亂攪拌一陣，還有熱心人猛撒胡椒粉。那鱔魚當中時常羼 上大量筍絲茭白絲之類，有喧賓奪主之勢』\n" +
                        "\n" +
                        "清炒鱔糊是上海菜中偏寧波菜的作法，昔日上海隆記菜飯還在時，就有此一道，這菜 的特色是鱔魚要先煮到五分熟，然後搭配上醬油、鹽、白糖、薑茸、紹酒和上湯的醬汁，把鱔魚『上色』，然後用太白粉勾芡 (有人說不要這道過程, 看人啦 ) ，這時候就可以上桌。然後接下來外場拎著一壺滾燙的熱油，澆上去!! 瞬間油香與爆香的鱔魚味超香的。要盡快拌開，然後就是超棒的清炒鱔糊。至於梁實秋大師吃飯時當時台灣的各式中菜還是黃金期，為什麼會吃到如此黑店，還胡椒 粉筍絲什麼的，實在是亂七八糟\n" +
                        "這邊的清炒鱔糊嘛，味道有及格，但考慮到做法必需要扣分。因為它一上來就是澆上油的，少了這味桌邊表演，就實在讓人傷心。\n" +
                        "\n" +
                        "\n" +
                        "嚴格說台灣的中菜常常亂七八糟混，可說是聚集各地菜色五湖四海啥都有，所以一間好像是江浙菜的餐廳吃烤鴨你也別想太多，很正常\n" +
                        "這邊的鴨子相當巨大，不像中山北路某間烤鴨，給人看都很大，吃的時候沒幾片肉。\n" +
                        "\n" +
                        "以前台灣的上海館子大概不會炒這道菜就代表主廚手藝不好(笑)，這道菜來源有兩種說法，有一說是來自於西湖的龍井蝦仁，又有人引梁實秋說 法是上海靜安賓館引自閔菜(福建菜) 製作而成。我個人覺得偏向前者，畢竟吃過的福建菜色的蝦子都與這道菜差距甚遠。這道菜要用河蝦，炒的火喉是重點，太過頭就吃來蝦有點硬，但想要保持軟度， 那又可能沒煮熟害大家拉肚子，河蝦問題又比海蝦更麻煩，然後還有人寫這道菜給我寫Q彈，你廣東人啊，誰家的清炒蝦仁是Q彈的你加硼砂嘛?  吃東西寫食記可以不要那麼外行嘛? 不懂吃，舌頭又不好你可以google 啊!! ( 這樣想起某人，表示上海菜飯要綠綠的才道地，道你家的地，莫名其妙 ) (半夜寫文心情差)\n" +
                        "\n" +
                        "吃的時候可以單吃，再沾一點白醋，就好吃了。不過這邊的，稍為再加油一點吧\n");
            }
        });

        //跳轉至，選擇餐廳頁面(外)
        ivPlaceIcon = view.findViewById(R.id.ivPlaceIcon);
        tvResName = view.findViewById(R.id.tvResName);
        ivPlaceIcon.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_articleInsertFragment_to_resAddressFragment));

        //橫向顯示圖片
        rvInsertImage = view.findViewById(R.id.rvInsertImage);
        rvInsertImage.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));
        showImgs(imgList);

        Bundle bundle = getArguments();
       newArticle = bundle.getInt("newArticle");
       res = (Res) bundle.getSerializable("res");

        String resName = "";
        String resCategory = "";
       if (preferences != null) {
           resName = preferences.getString("ResName", DEFAULT_FILE_NAME);
           resCategory = preferences.getString("Category", DEFAULT_FILE_NAME);
       }

        /* 從Mark哥的餐廳細節過來 */
        if (preferences == null) {
            /* 從選擇餐廳回來 */
            if (newArticle == 2) {
                tvResName.setText("店名：請選擇餐廳");
                //將bundle內的資料(int)改成0
                bundle.putInt("newArticle", 0);
            } else {
                tvResName.setText("店名：請選擇餐廳2");
            }
        } else {
            tvResName.setText(resCategory + "\n" + "餐廳：" + resName);
            //將bundle內的資料(int)改成0
            bundle.putInt("newArticle", 0);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        /* 偏好設定取得文字 > 帶回餐廳資訊 */
        preferences = activity.getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE);
        String resName = preferences.getString("ResName", DEFAULT_FILE_NAME);
        String resCategory = preferences.getString("Category", DEFAULT_FILE_NAME);

        /* 顯示餐廳資訊 */
        Bundle bundle = getArguments();
        Res res = (Res) (bundle != null ? bundle.getSerializable("res") : null);
       if (newArticle == 0) {
            tvResName.setText(resCategory + "\n" + "餐廳：" + resName);
        } else {
            tvResName.setText("店名：請選擇餐廳");
        }
    }

    //取得UserID方法
    private Integer getUserID() {
        return Common.USER_ID;
    }

    private void showImgs(List<Bitmap> imgList) {
        if (imgs == null || imgs.isEmpty()) {
            ImgAdpter imgAdpter = (ImgAdpter) rvInsertImage.getAdapter();
            if (imgAdpter == null) {
                rvInsertImage.setAdapter(new ImgAdpter(ivPlaceIcon, activity, imgList));
            } else {
                imgAdpter.setImgList(imgList);
                imgAdpter.notifyDataSetChanged();
            }
        }
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
            /* 右上送出發文 */
            case R.id.menuSend:
                boolean textError = true;
                String conAmountStr = etConAmount.getText().toString();   //輸入消費轉為字串
                String conNumStr = etConNum.getText().toString();   //輸入人數轉為字串
                String articleText = etArticleText.getText().toString();     //輸入文章內文
                String articleTitle = etArticleTitle.getText().toString();   //輸入文章標題

                if (conNumStr.isEmpty()) {
                    etConNum.setError("請輸入正確消費人數");
                    textError = false;
                }

                if (conAmountStr.isEmpty()) {
                    textError = false;
                    etConAmount.setError("請輸入正確消費金額");
                }

                  //輸入文章主題
                if (articleTitle.isEmpty()) {
                    textError = false;
                    etArticleTitle.setError("請輸入文章主題");
                }

                if (newArticle != 0) {
                    textError = false;
                    etArticleText.setError("請輸入文章內文");
                    Common.showToast(activity, "請選擇餐廳");
                }

                if (articleText.isEmpty()) {
                    textError = false;
                    Common.showToast(activity, "請輸入文章內容");
                }

                if (!textError) {  //不允許按button送出，否則會閃退
                    return false; //return; 回傳空，目的是不要讓程序往下走
                }

                int conNum = Integer.parseInt(conNumStr);  //輸入人數，int型態
                int conAmount = Integer.parseInt(conAmountStr);   //輸入消費金額，int型態

                //取得餐廳的ID
                int resId = preferences.getInt("resId", 0);

                //送出文章資料(不含圖片)
                if (Common.networkConnected(activity)) {
                    String url = Common.URL_SERVER + "ArticleServlet";
                    Article article = new Article(0, articleTitle, articleText, conNum, conAmount, resId, userIdBox, true);
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("action", "articleInsert");
                    jsonObject.addProperty("article", new Gson().toJson(article));
                    int count = 0;
                    try {
                        String result = new CommonTask(url, jsonObject.toString()).execute().get();
                        count = Integer.parseInt(result);
                    } catch (Exception e) {
                        Log.e(TAG, e.toString());
                    }
                    if (count == 0) {
                        Common.showToast(activity, "上傳失敗");
                    } else {
                        Common.showToast(activity, "發文成功");
                    }
                } else {
                    Common.showToast(activity, "連線失敗");
                }

                /* 上傳圖片(Insert) */
                if (Common.networkConnected(activity)) {
                    String url = Common.URL_SERVER + "ImgServlet";
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("action", "findByIdMax");
                    Img img = new Img(0, 0);
                    jsonObject.addProperty("img", new Gson().toJson(img));

                    //確認是否有取得圖檔才會上傳
                    int count = 0;
                    //for迴圈 > 迭代取出imgList的資料
                    for (int i = 0 ; i <= imgList.size() -1 ; i++) {
                        byte[] imgBytes = Common.bitmapToByte(imgList.get(i));
                        //確認是否有取得圖檔才會上傳
                            jsonObject.addProperty("imageBase64", Base64.encodeToString(imgBytes, Base64.DEFAULT));
                        try {
                            String result = new CommonTask(url, jsonObject.toString()).execute().get();
                            count = Integer.parseInt(result);
                        } catch (Exception e) {
                            Log.e(TAG, e.toString());
                        }
                    }

                    if (count != 0) {
                        Common.showToast(activity, "圖片新增成功");
                    }
                } else {
                    Common.showToast(activity, "連線失敗");
                }
                /* 結束動作後返回前一頁 */
                navController.navigate(R.id.action_articleInsertFragment_to_articleFragment);
//              navController.popBackStack();
                break;
            //預設
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private class ImgAdpter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private LayoutInflater layoutInflater;
        private List<Img> imgs;
        private ImageView pickIcon;
        private int imageSize;
        private List<Bitmap> imgList;

        public List<Bitmap> getImgList() {
            return imgList;
        }

        public void setImgList(List<Bitmap> imgList) {
            this.imgList = imgList;
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

        ImgAdpter(ImageView pickIcon, Context context, List<Bitmap> imgList) {
            layoutInflater = LayoutInflater.from(context);
            this.imgs = imgs;
            this.pickIcon = pickIcon;
            this.imgList = imgList;
            /* 螢幕寬度除以2當作將圖的尺寸 */
            imageSize = getResources().getDisplayMetrics().widthPixels / 2;
        }

        //圖片ViewHolder
        public class MyViewHolder extends RecyclerView.ViewHolder {
            ImageView ivArticleImageInsert;
            public MyViewHolder(@NonNull View itemView) {
                super(itemView);
                ivArticleImageInsert = itemView.findViewById(R.id.ivArticleImageInsert);
            }
        }

        //加入圖片ViewHolder > header
        public class PickViewHolder extends RecyclerView.ViewHolder {
            ImageView ivArticleImagePick;

            public PickViewHolder(@NonNull View itemView) {
                super(itemView);
                ivArticleImagePick = itemView.findViewById(R.id.ivArticleImagePick);
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

        //設定長度
        @Override
        public int getItemCount() {
            return imgList == null ? 1 : (1 + imgList.size());
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof PickViewHolder) {
                PickViewHolder pickViewHolder = (PickViewHolder) holder; //要強轉！！
                pickViewHolder.ivArticleImagePick.setImageResource(R.drawable.ic_add);
                pickViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // AlertDialog
                        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                        final String[] photo = {"相機", "相簿"};
                        builder.setItems(photo, new DialogInterface.OnClickListener() {
                            @RequiresApi(api = Build.VERSION_CODES.Q)
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
                                    // Navigation.findNavController(v).navigate(R.id.action_articleInsertFragment_to_insertImageFragment);
                                }
                            }
                        })
                                .setTitle("選擇圖片來源")
                                .setCancelable(true) // 必須點擊按鈕方能關閉，預設為true
                                .show();
                    }
                });
            } else if (holder instanceof MyViewHolder) {
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
            imgbit = output.toByteArray();  //將output解碼成Byte陣列
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
        if (bitmap != null) {
            imgList.add(bitmap);
            showImgs(imgList);
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

    /* 使用「getCacheDir() + 檔案名稱」將物件存檔 */
    private void saveFile_getCacheDir(String fileName, Bitmap bitmap){
        File file = new File(activity.getCacheDir(), fileName);
        Log.d(TAG, "getCacheDir() path: " + file.getPath());
        try (ObjectOutputStream out = new ObjectOutputStream(
                new FileOutputStream(file))) {
            out.writeObject(bitmap);
        } catch (IOException e) {
            Log.e(TAG, e.toString());
        } //暫存檔案，重要資料不要用cache的方法存取
    }

}