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
    private static final int REQ_TAKE_PICTURE = 0;  //?????????????????????????????????
    private static final int REQ_PICK_PICTURE = 1;  //?????????????????????????????????
    private static final int REQ_CROP_PICTURE = 2;  //?????????????????????????????????
    private Bitmap bitmap = null;
    private NavController navController;
    private int userIdBox = Common.USER_ID;

    private final static String PREFERENCES_NAME = "Res";   //????????????
    private final static String DEFAULT_FILE_NAME = " "; //????????????????????????
    private SharedPreferences preferences;
    private String conNumStr, conAmountStr, articleTitle, articleText;
    private int newArticle;
    private Button strButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        setHasOptionsMenu(true);

        // ??????????????????????????????
        Common.setBackArrow(true, activity);
        setHasOptionsMenu(true);
        navController =
                Navigation.findNavController(activity, R.id.mainFragment);

        //?????? > bundle??????????????????
        resAddresses = (ResAddress) (getArguments() != null ? getArguments().getSerializable("ResAddress") : null);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity.setTitle("??????");
        return inflater.inflate(R.layout.fragment_article_insert, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        etConNum = view.findViewById(R.id.etConNum);    //??????????????????
        etConAmount = view.findViewById(R.id.etConAmount);  //??????????????????
        etArticleTitle = view.findViewById(R.id.etArticleTitle);    //????????????
        etArticleText = view.findViewById(R.id.etArticleText);  //????????????
        tvResName = view.findViewById(R.id.tvResName);
        imgList = new ArrayList<>();

        //????????????
        conNumStr = etConNum.getText().toString().trim();
        conAmountStr = etConAmount.getText().toString().trim();
        articleTitle = etArticleTitle.getText().toString().trim();
        articleText = etArticleText.getText().toString().trim();

        //???????????? > ????????????
        strButton = view.findViewById(R.id.strButton);
        strButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etArticleTitle.setText("??????????????????????????????????????????????????????????????????");
                etArticleText.setText("????????? ???????????????\n" +
                        "????????? ????????????????????????????????????219???3???\n" +
                        "????????? 02-2718-5588\n" +
                        "????????? 600-1000 /???\n" +
                        "???????????????11:00???14:00, 17:00???21:30\n" +
                        "\n" +
                        "????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????? (??????)????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????? ???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????? ?????????????????????????????????????????????????????????\n" +
                        "\n" +
                        "??????????????????????????????????????????????????????????????????????????????????????????????????????????????? ????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????? (???????????????????????????, ????????? ) ??????????????????????????????????????????????????????????????????????????????????????????!! ???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????? ??????????????????????????????????????????\n" +
                        "???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????\n" +
                        "\n" +
                        "\n" +
                        "?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????\n" +
                        "???????????????????????????????????????????????????????????????????????????????????????????????????????????????\n" +
                        "\n" +
                        "??????????????????????????????????????????????????????????????????????????????(???)????????????????????????????????????????????????????????????????????????????????????????????????????????? ????????????????????????????????????(?????????) ???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????? ??????????????????????????????????????????????????????????????????????????????????????????????????????????????????Q????????????????????????????????????????????????Q??????????????????????  ?????????????????????????????????????????????? ????????????????????????????????????google ???!! ( ????????????????????????????????????????????????????????????????????????????????????????????? ) (?????????????????????)\n" +
                        "\n" +
                        "?????????????????????????????????????????????????????????????????????????????????????????????????????????\n");
            }
        });

        //??????????????????????????????(???)
        ivPlaceIcon = view.findViewById(R.id.ivPlaceIcon);
        tvResName = view.findViewById(R.id.tvResName);
        ivPlaceIcon.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_articleInsertFragment_to_resAddressFragment));

        //??????????????????
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

        /* ???Mark???????????????????????? */
        if (preferences == null) {
            /* ????????????????????? */
            if (newArticle == 2) {
                tvResName.setText("????????????????????????");
                //???bundle????????????(int)??????0
                bundle.putInt("newArticle", 0);
            } else {
                tvResName.setText("????????????????????????2");
            }
        } else {
            tvResName.setText(resCategory + "\n" + "?????????" + resName);
            //???bundle????????????(int)??????0
            bundle.putInt("newArticle", 0);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        /* ???????????????????????? > ?????????????????? */
        preferences = activity.getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE);
        String resName = preferences.getString("ResName", DEFAULT_FILE_NAME);
        String resCategory = preferences.getString("Category", DEFAULT_FILE_NAME);

        /* ?????????????????? */
        Bundle bundle = getArguments();
        Res res = (Res) (bundle != null ? bundle.getSerializable("res") : null);
       if (newArticle == 0) {
            tvResName.setText(resCategory + "\n" + "?????????" + resName);
        } else {
            tvResName.setText("????????????????????????");
        }
    }

    //??????UserID??????
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
            /* ?????????????????? */
            case R.id.menuSend:
                boolean textError = true;
                String conAmountStr = etConAmount.getText().toString();   //????????????????????????
                String conNumStr = etConNum.getText().toString();   //????????????????????????
                String articleText = etArticleText.getText().toString();     //??????????????????
                String articleTitle = etArticleTitle.getText().toString();   //??????????????????

                if (conNumStr.isEmpty()) {
                    etConNum.setError("???????????????????????????");
                    textError = false;
                }

                if (conAmountStr.isEmpty()) {
                    textError = false;
                    etConAmount.setError("???????????????????????????");
                }

                  //??????????????????
                if (articleTitle.isEmpty()) {
                    textError = false;
                    etArticleTitle.setError("?????????????????????");
                }

                if (newArticle != 0) {
                    textError = false;
                    etArticleText.setError("?????????????????????");
                    Common.showToast(activity, "???????????????");
                }

                if (articleText.isEmpty()) {
                    textError = false;
                    Common.showToast(activity, "?????????????????????");
                }

                if (!textError) {  //????????????button????????????????????????
                    return false; //return; ?????????????????????????????????????????????
                }

                int conNum = Integer.parseInt(conNumStr);  //???????????????int??????
                int conAmount = Integer.parseInt(conAmountStr);   //?????????????????????int??????

                //???????????????ID
                int resId = preferences.getInt("resId", 0);

                //??????????????????(????????????)
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
                        Common.showToast(activity, "????????????");
                    } else {
                        Common.showToast(activity, "????????????");
                    }
                } else {
                    Common.showToast(activity, "????????????");
                }

                /* ????????????(Insert) */
                if (Common.networkConnected(activity)) {
                    String url = Common.URL_SERVER + "ImgServlet";
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("action", "findByIdMax");
                    Img img = new Img(0, 0);
                    jsonObject.addProperty("img", new Gson().toJson(img));

                    //???????????????????????????????????????
                    int count = 0;
                    //for?????? > ????????????imgList?????????
                    for (int i = 0 ; i <= imgList.size() -1 ; i++) {
                        byte[] imgBytes = Common.bitmapToByte(imgList.get(i));
                        //???????????????????????????????????????
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
                /* ?????????????????????????????? */
                navController.navigate(R.id.action_articleInsertFragment_to_articleFragment);
//              navController.popBackStack();
                break;
            //??????
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
            /* ??????????????????2????????????????????? */
            imageSize = getResources().getDisplayMetrics().widthPixels / 2;
        }

        //??????ViewHolder
        public class MyViewHolder extends RecyclerView.ViewHolder {
            ImageView ivArticleImageInsert;
            public MyViewHolder(@NonNull View itemView) {
                super(itemView);
                ivArticleImageInsert = itemView.findViewById(R.id.ivArticleImageInsert);
            }
        }

        //????????????ViewHolder > header
        public class PickViewHolder extends RecyclerView.ViewHolder {
            ImageView ivArticleImagePick;

            public PickViewHolder(@NonNull View itemView) {
                super(itemView);
                ivArticleImagePick = itemView.findViewById(R.id.ivArticleImagePick);
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

        //????????????
        @Override
        public int getItemCount() {
            return imgList == null ? 1 : (1 + imgList.size());
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof PickViewHolder) {
                PickViewHolder pickViewHolder = (PickViewHolder) holder; //???????????????
                pickViewHolder.ivArticleImagePick.setImageResource(R.drawable.ic_add);
                pickViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // AlertDialog
                        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                        final String[] photo = {"??????", "??????"};
                        builder.setItems(photo, new DialogInterface.OnClickListener() {
                            @RequiresApi(api = Build.VERSION_CODES.Q)
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
                                    // Navigation.findNavController(v).navigate(R.id.action_articleInsertFragment_to_insertImageFragment);
                                }
                            }
                        })
                                .setTitle("??????????????????")
                                .setCancelable(true) // ??????????????????????????????????????????true
                                .show();
                    }
                });
            } else if (holder instanceof MyViewHolder) {
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
            imgbit = output.toByteArray();  //???output?????????Byte??????
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
        if (bitmap != null) {
            imgList.add(bitmap);
            showImgs(imgList);
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

    /* ?????????getCacheDir() + ?????????????????????????????? */
    private void saveFile_getCacheDir(String fileName, Bitmap bitmap){
        File file = new File(activity.getCacheDir(), fileName);
        Log.d(TAG, "getCacheDir() path: " + file.getPath());
        try (ObjectOutputStream out = new ObjectOutputStream(
                new FileOutputStream(file))) {
            out.writeObject(bitmap);
        } catch (IOException e) {
            Log.e(TAG, e.toString());
        } //????????????????????????????????????cache???????????????
    }

}