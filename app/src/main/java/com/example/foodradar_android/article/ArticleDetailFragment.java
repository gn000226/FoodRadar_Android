package com.example.foodradar_android.article;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.example.foodradar_android.Common;
import com.example.foodradar_android.R;
import com.example.foodradar_android.task.CommonTask;
import com.example.foodradar_android.task.ImageTask;
import com.example.foodradar_android.user.UserAccount;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

public class ArticleDetailFragment extends Fragment {
    private static final String TAG = "TAG_Detail";
    private RecyclerView rvArticleImage, rvComment;
    private Article article;
    private List<Img> imgs;
    private List<Comment> comments;
    private NavController navController;
    private Activity activity;
    private TextView tvDetailArticleTitle, tvDetailUserName, tvDtdailResCategoryInfo, tvDetailArticleTime, tvDetailResName,
            tvDetailAvgCon, tvArticleText, tvDetailGoodCount, tvDetailCommentCount, tvDetailFavoriteArticle, tvCommentUserName;
    private ImageView ivDetailUserIcon, ivDetailSetting, user, ivDetailGoodIcon, ivDetailArticleCommentIcon, ivDetailFavoriteIcon;
    private EditText etComment;   //??????
    private Button btCommentSend;   //????????????
    private CommonTask articleGetAllTask;
    private CommonTask commentGetAllTask;
    private CommonTask commentDeleteTask;
    private CommonTask articleDeleteTask;
    private List<ImageTask> imageTasks;
    private Integer articleIdBox = Article.ARTICLE_ID;
    private Integer userIdBox = Common.USER_ID; //?????????????????????ID

    private int imageSize;
    private ConstraintLayout articleConstraintLayout;
    private UserAccount userAccount; //???????????????????????????
    private ConstraintLayout layoutCommentBar;
    final String[] articleSetting = {"????????????", "????????????"};
    private CommentGood commentGood;

    private BottomNavigationView bottomNavigationView;
    private ImageView imageShow;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        imageTasks = new ArrayList<>();
        imgs = new ArrayList<>();
        // ??????????????????????????????
        new Common().setBackArrow(false, activity);
        setHasOptionsMenu(false);
        navController = Navigation.findNavController(activity, R.id.mainFragment);
    }

    @Override
    public void onStart() {
        super.onStart();
        //?????? floatingActionButton
        Common.faButtonControl(activity, false);

        //??????bottomNav
        ArticleFragment.bottomNavSet(activity, 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_article_detail, container, false);
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvDetailArticleTitle = view.findViewById(R.id.tvDetailArticleTitle);
        tvDetailUserName = view.findViewById(R.id.tvDetailUserName);
        tvDetailArticleTime = view.findViewById(R.id.tvDetailArticleTime);
        tvDtdailResCategoryInfo = view.findViewById(R.id.tvDtdailResCategoryInfo);
        tvDetailResName = view.findViewById(R.id.tvDetailResName);
        tvDetailAvgCon = view.findViewById(R.id.tvDetailAvgCon);
        tvArticleText = view.findViewById(R.id.tvArticleText);
        ivDetailUserIcon = view.findViewById(R.id.ivDetailUserIcon);
        ivDetailGoodIcon = view.findViewById(R.id.ivDetailGoodIcon);    //???Icon
        tvDetailGoodCount = view.findViewById(R.id.tvDetailGoodCount);  //??????
        ivDetailArticleCommentIcon = view.findViewById(R.id.ivDetailArticleCommentIcon);    //??????Icon
        tvDetailCommentCount = view.findViewById(R.id.tvDetailCommentCount);    //?????????
        ivDetailFavoriteIcon = view.findViewById(R.id.ivDetailFavoriteIcon);    //??????Icon
        tvDetailFavoriteArticle = view.findViewById(R.id.tvDetailFavoriteArticle);  //?????????
        ivDetailSetting = view.findViewById(R.id.ivDetailSetting);  //??????????????????
        user = view.findViewById(R.id.user); //??????Bar?????????Icon

        //????????????????????????????????????Bar
        layoutCommentBar = view.findViewById(R.id.layoutCommentBar);    //??????Bar
        if (userIdBox == 0) {
            layoutCommentBar.setVisibility(View.GONE);
        } else {
            layoutCommentBar.setVisibility(View.VISIBLE);
        }

        //????????????Bar > ????????????
        etComment = view.findViewById(R.id.etComment);  //??????Ed
        btCommentSend = view.findViewById(R.id.btCommentSend);  //????????????
        btCommentSend.setOnClickListener(v -> {
            String comment = etComment.getText().toString().trim();
            if (comment.length() <= 0) {
                Common.showToast(activity, R.string.commentNoInsert);
                etComment.setError("???????????????");
                return;
            }
            if (Common.networkConnected(activity)) {
                String url = Common.URL_SERVER + "CommentServlet";
                Comment commentInsert = new Comment(0, articleIdBox, userIdBox, true, comment);
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("action", "commentInsert");
                jsonObject.addProperty("comment", new Gson().toJson(commentInsert));
                int count = 0;
                try {
                    String result = new CommonTask(url, jsonObject.toString()).execute().get();
                    count = Integer.parseInt(result);
                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                }
                if (count == 0) {
                    Common.showToast(activity, R.string.commentInsertError);
                } else {
                    Common.showToast(activity, R.string.commentInsertSuccess);
                }
            } else {
                Common.showToast(activity, "????????????");
            }

            Bundle bundle = new Bundle();
            bundle.putString("comment", comment);

            //??????????????????????????????List???????????????show?????????????????? > ?????????????????????
            comments = getComments();
            showComments(comments);

            //????????????
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(activity.INPUT_METHOD_SERVICE);
            if (imm.isActive()) {
                // ???????????????????????? > ??????
                imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT,
                        InputMethodManager.HIDE_NOT_ALWAYS);
            }
            //???????????????edText
            etComment.setText("");
        });

        //???server?????????????????????
        String url = Common.URL_SERVER + "ArticleServlet";
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("action", "findById");
        jsonObject.addProperty("articleId", articleIdBox); //articleIdBox > ????????????List???????????????ID
        jsonObject.addProperty("loginUserId", userIdBox);
        String jsonOut = jsonObject.toString();
        articleGetAllTask = new CommonTask(url, jsonOut);
        try {
            String jsonIn = articleGetAllTask.execute().get();
            Type listType = new TypeToken<Article>() {
            }.getType();
            article = new Gson().fromJson(jsonIn, listType);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }

        //?????????????????????????????????????????????
        String title = article.getArticleTitle();   //??????
        tvDetailArticleTitle.setText(title);
        String userName = article.getUserName();    //???????????????
        tvDetailUserName.setText(userName);
        String resCategory = article.getResCategoryInfo();  //????????????
        tvDtdailResCategoryInfo.setText(resCategory);
        String resName = article.getResName();  //????????????
        tvDetailResName.setText("?????????" + resName);

        String DetailArticleTime = article.getArticleTime();  //??????????????????(???????????????????????????)
        String ModifyArticleTime = article.getModifyTime();  //??????????????????
        if(DetailArticleTime.equals(ModifyArticleTime)) {
            tvDetailArticleTime.setText("???????????????" + DetailArticleTime);
        } else {
            tvDetailArticleTime.setText("???????????????" + ModifyArticleTime);
        }

        String articleText = article.getArticleText();  //????????????
        tvArticleText.setText(articleText);
        String DetailAvgCon = article.avgCon(); //??????????????????
        tvDetailAvgCon.setText(DetailAvgCon);
        String DetailCommentCount = article.getCommentCount() + "";     //???????????????
        tvDetailCommentCount.setText(DetailCommentCount);

        /* ??????????????????????????????????????????Update???????????? */
        int conNum = article.getConNum();
        String conNumStr = Integer.toString(conNum);
        int conAmount = article.getConAmount();
        String conAmountStr = Integer.toString(conAmount);

        //?????????user??????????????????????????????????????????
        ivDetailSetting.setImageResource(R.drawable.ic_baseline_more_vert_24);
        if (article.getUserId() == userIdBox) {
            ivDetailSetting.setVisibility(View.VISIBLE);
            ivDetailSetting.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /* AlertDialog????????????????????? */
                    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                    builder.setItems(articleSetting, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (which == 0) {
                                /* ????????????????????? */
                                //????????????
                                Bundle bundle = new Bundle();
                                bundle.putInt("articleId", articleIdBox);
                                bundle.putInt("userId", userIdBox);
                                bundle.putString("articleTitle", title);
                                bundle.putString("articleText", articleText);
                                bundle.putString("conNum", conNumStr);   //????????????
                                bundle.putString("conAmount", conAmountStr); //????????????
                                bundle.putString("resName", resName);   //????????????
                                bundle.putString("resCategory", resCategory);   //????????????
                                Navigation.findNavController(v).navigate(R.id.action_articleDetailFragment_to_articleUpdateFragment, bundle);

                            } else {
                                //???????????????
                                if (Common.networkConnected(activity)) {
                                    String url = Common.URL_SERVER + "ArticleServlet";
                                    article.setStatus(false);   //??????????????????
                                    JsonObject jsonObject = new JsonObject();
                                    jsonObject.addProperty("action", "articleDelete");
                                    jsonObject.addProperty("article", new Gson().toJson(article));
                                    int count = 0; //????????????????????????
                                    try {
                                        //????????????????????????json???????????????(??????url)
                                        String result = new CommonTask(url, jsonObject.toString()).execute().get();
                                        count = Integer.parseInt(result);   //???result"??????????????????"??????int?????????
                                    } catch (Exception e) {
                                        Log.e(TAG, e.toString());
                                    }
                                    if (count == 0) {   //??????????????????0???
                                        Common.showToast(activity, "??????????????????");
                                    } else {
                                        Common.showToast(activity, "??????????????????");
                                    }
                                    Navigation.findNavController(v).navigate(R.id.action_articleDetailFragment_to_newArticleFragment2);
                                }
                            }
                        }
                    });
                    builder.setNeutralButton("??????", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    //????????????
                    AlertDialog mDialog = builder.create();
                    mDialog.show();
                    Window window = mDialog.getWindow();
                    assert window != null;
                    window.setGravity(Gravity.BOTTOM);
                    window.setWindowAnimations(R.style.popupAnimation);
                    mDialog.setCanceledOnTouchOutside(true);
                    mDialog.setCancelable(true);
                }
            });
        } else {
            ivDetailSetting.setVisibility(View.INVISIBLE);
        }

        //??????user??????(?????????) > ??????
        String urlIcon = Common.URL_SERVER + "UserAccountServlet";
        int userId = article.getUserId();
        ImageTask imageTaskIcon = new ImageTask(urlIcon, userId, imageSize, ivDetailUserIcon);
        imageTaskIcon.execute();
        imageTasks.add(imageTaskIcon);

        //?????????????????????(?????????) > ??????Bar
        if (userIdBox != 0) {
            ImageTask imageTaskCommentIcon = new ImageTask(urlIcon, userIdBox, imageSize, user);
            imageTaskCommentIcon.execute();
            imageTasks.add(imageTaskCommentIcon);
        } else {
            user.setImageResource(R.drawable.ic_awesome_user_circle);
        }

        //??????????????????
        //??????recyclerView
        rvArticleImage = view.findViewById(R.id.rvArticleImage);    //??????recyclerView
        rvArticleImage.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));
        imgs = getImgs();
        showImgs(imgs);

        //????????????
        rvComment = view.findViewById(R.id.rvComment);  //??????recyclerView
        rvComment.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, true));
        comments = getComments();
        showComments(comments);


        //?????????????????????1.??????????????????
        //2.?????????????????????????????????
        ImageView goodIcon = ivDetailGoodIcon;
        if (userIdBox == 0) { //0 > ??????????????????????????????
            article.setArticleGoodStatus(false);
            goodIcon.setColorFilter(Color.parseColor("#424242"));
        } else {    //??????????????????????????????
            if (article.isArticleGoodStatus()) {
                goodIcon.setColorFilter(Color.parseColor("#1877F2"));
                article.setArticleGoodStatus(true);
            } else {
                goodIcon.setColorFilter(Color.parseColor("#424242"));
                article.setArticleGoodStatus(false);
            }
        }
        ivDetailGoodIcon.setImageResource(R.drawable.ic_baseline_thumb_up_24);
        tvDetailGoodCount.setText((article.getArticleGoodCount() + ""));
        //3.???????????????
        if (userIdBox != 0) {
            ivDetailGoodIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!article.isArticleGoodStatus()) {
                        if (Common.networkConnected(activity)) {
                            String insertGoodUrl = Common.URL_SERVER + "ArticleServlet";
                            JsonObject jsonObject = new JsonObject();
                            jsonObject.addProperty("action", "articleGoodInsert");
                            jsonObject.addProperty("articleId", articleIdBox);
                            jsonObject.addProperty("loginUserId", userIdBox);
                            int count = 0;
                            try {
                                String result = new CommonTask(insertGoodUrl, jsonObject.toString()).execute().get();
                                count = Integer.parseInt(result);
                            } catch (Exception e) {
                                Log.e(TAG, e.toString());
                            }
                            if (count == 0) {
                                Common.showToast(activity, "????????????");
                            } else {
                                article.setArticleGoodCount(article.getArticleGoodCount() + 1);
                                tvDetailGoodCount.setText((article.getArticleGoodCount() + ""));
                                goodIcon.setColorFilter(Color.parseColor("#1877F2"));
                                article.setArticleGoodStatus(true);
                            }
                        } else {
                            Common.showToast(activity, "??????????????????");
                        }
                    } else {
                        if (Common.networkConnected(activity)) {
                            String deleteGoodUrl = Common.URL_SERVER + "ArticleServlet";
                            JsonObject jsonObject = new JsonObject();
                            jsonObject.addProperty("action", "articleGoodDelete");
                            jsonObject.addProperty("articleId", articleIdBox);
                            jsonObject.addProperty("userId", userIdBox);
                            int count = 0;
                            try {
                                articleDeleteTask = new CommonTask(deleteGoodUrl, jsonObject.toString());
                                String result = articleDeleteTask.execute().get();
                                count = Integer.parseInt(result);
                            } catch (Exception e) {
                                Log.e(TAG, e.toString());
                            }
                            if (count == 0) { //????????????????????????????????????
                                Common.showToast(activity, "????????????");
                            } else {
                                article.setArticleGoodCount(article.getArticleGoodCount() - 1);
                                tvDetailGoodCount.setText(((article.getArticleGoodCount()) + ""));
                                goodIcon.setColorFilter(Color.parseColor("#424242"));
                                article.setArticleGoodStatus(false);
                            }
                        } else {
                            Common.showToast(activity, "?????????????????????");
                        }
                    }
                }
            });
        }

        //?????????????????????1.??????????????????
        //2.?????????????????????????????????
        final boolean articleFavoriteStatus = article.isArticleFavoriteStatus();
        ImageView favoriteIcon = ivDetailFavoriteIcon;
        if (userIdBox == 0) {    //0 > ??????????????????????????????
            article.setArticleFavoriteStatus(false);
            goodIcon.setColorFilter(Color.parseColor("#424242"));
        } else {
            if (articleFavoriteStatus) {
                favoriteIcon.setColorFilter(Color.parseColor("#EADDAB"));
                article.setArticleFavoriteStatus(true);
            } else {
                favoriteIcon.setColorFilter(Color.parseColor("#424242"));
                article.setArticleFavoriteStatus(false);
            }
        }
        ivDetailFavoriteIcon.setImageResource(R.drawable.ic_baseline_favorite_24);
        tvDetailFavoriteArticle.setText((article.getFavoriteCount() + ""));
        //3.???????????????   >   ??????
        if (userIdBox != 0) {
            ivDetailFavoriteIcon.setOnClickListener(v -> {
                if (!article.isArticleFavoriteStatus()) {
                    if (Common.networkConnected(activity)) {
                        String insertFavoriteUrl = Common.URL_SERVER + "ArticleServlet";
                        JsonObject jsonObject1 = new JsonObject();
                        jsonObject1.addProperty("action", "articleFavoriteInsert");
                        jsonObject1.addProperty("articleId", article.getArticleId());
                        jsonObject1.addProperty("loginUserId", userIdBox);
                        int count = 0;
                        try {
                            String result = new CommonTask(insertFavoriteUrl, jsonObject1.toString()).execute().get();
                            count = Integer.parseInt(result);
                        } catch (Exception e) {
                            Log.e(TAG, e.toString());
                        }
                        if (count == 0) {
                            Common.showToast(activity, "????????????");
                        } else {
                            article.setFavoriteCount((article.getFavoriteCount() + 1));
                            tvDetailFavoriteArticle.setText(((article.getFavoriteCount()) + ""));
                            ivDetailFavoriteIcon.setColorFilter(Color.parseColor("#EADDAB"));
                            article.setArticleFavoriteStatus(true);
                        }
                    } else {
                        Common.showToast(activity, "??????????????????");
                    }
                } else {
                    if (Common.networkConnected(activity)) {
                        String deleteFavoriteUrl = Common.URL_SERVER + "ArticleServlet";
                        JsonObject jsonObject1 = new JsonObject();
                        jsonObject1.addProperty("action", "articleFavoriteDelete");
                        jsonObject1.addProperty("loginUserId", userIdBox);
                        jsonObject1.addProperty("articleId", article.getArticleId());
                        int count = 0;
                        try {
                            articleDeleteTask = new CommonTask(deleteFavoriteUrl, jsonObject1.toString());
                            String result = articleDeleteTask.execute().get();
                            count = Integer.parseInt(result);
                        } catch (Exception e) {
                            Log.e(TAG, e.toString());
                        }
                        if (count == 0) { //????????????????????????????????????
                            Common.showToast(activity, "????????????");
                        } else {
                            article.setFavoriteCount((article.getFavoriteCount() - 1));
                            tvDetailFavoriteArticle.setText((article.getFavoriteCount() + ""));
                            favoriteIcon.setColorFilter(Color.parseColor("#424242"));
                            article.setArticleFavoriteStatus(false);
                        }
                    } else {
                        Common.showToast(activity, "??????????????????");
                    }
                }
            });
        }
    }

    //???????????? > ??????????????????????????????(imgId,articleId)????????????????????????
    private List<Img> getImgs() {
        List<Img> imgs = null;
        if (Common.networkConnected(activity)) {
            String url = Common.URL_SERVER + "ImgServlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "getAllById");
            jsonObject.addProperty("articleId", article.getArticleId());
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

    //???????????? > ??????imgs
    private void showImgs(List<Img> imgs) {
        if (imgs == null || imgs.isEmpty()) {
            Common.showToast(activity, R.string.textNoImgFound);
        }
        ImgAdpter imgAdpter = (ImgAdpter) rvArticleImage.getAdapter();
        if (imgAdpter == null) {
            rvArticleImage.setAdapter(new ImgAdpter(activity, imgs));
        } else {
            imgAdpter.setImgs(imgs);
            imgAdpter.notifyDataSetChanged();
        }
    }

    private class ImgAdpter extends RecyclerView.Adapter<ImgAdpter.MyViewHolder> {
        private LayoutInflater layoutInflater;
        private List<Img> imgs;
        private int imageSize;

        ImgAdpter(Context context, List<Img> imgs) {
            layoutInflater = LayoutInflater.from(context);
            this.imgs = imgs;
            /* ??????????????????2????????????????????? */
            imageSize = getResources().getDisplayMetrics().widthPixels;
        }

        @Override
        public int getItemCount() {
            return imgs == null ? 0 : imgs.size();
        }

        void setImgs(List<Img> imgs) {
            this.imgs = imgs;
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            ImageView ivArticleImage;

            public MyViewHolder(@NonNull View itemView) {
                super(itemView);
                ivArticleImage = itemView.findViewById(R.id.ivArticleImage);
            }
        }

        @NonNull
        @Override
        public ImgAdpter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = layoutInflater.inflate(R.layout.article_image_view, parent, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull ImgAdpter.MyViewHolder myViewHolder, int position) {
            final Img img = imgs.get(position);
            /* ?????????????????? */
            String url = Common.URL_SERVER + "ImgServlet";
            int imgId = img.getImgId();
            int articleId = img.getArticleId();
            //??????ImageTask
//            ImageTask imageTask = new ImageTask(url, imgId, imageSize, myViewHolder.ivArticleImage, articleId);
//            imageTask.execute();
//            imageTasks.add(imageTask);

            /* ??????Base64 */
            JsonObject jsonObjectBase = new JsonObject();
            jsonObjectBase.addProperty("action", "getImageBase");
            jsonObjectBase.addProperty("id", imgId);
            CommonTask BaseTaskTask = new CommonTask(url, jsonObjectBase.toString());
            byte[] imageByte;
            try {
                String jsonIn = BaseTaskTask.execute().get();
                JsonObject jObject = new Gson().fromJson(jsonIn, JsonObject.class);
                imageByte = Base64.decode(jObject.get("imageBase64").getAsString(), Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(imageByte, 0, imageByte.length);
                if (bitmap != null) {
                    new Common().setImage(activity, bitmap);
                    if (myViewHolder.ivArticleImage != null) {
                        myViewHolder.ivArticleImage.setImageBitmap(bitmap);

                        /* ????????????> ?????? */
                        saveToInternalStorage(bitmap);
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }


            /* alertDiolog????????????  >   ???????????? */
            myViewHolder.ivArticleImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                    View view = getLayoutInflater().inflate(R.layout.show_image_item, null);
                    alertDialog.setView(view);
                    ImageTask bigImageTask = new ImageTask(url, imgId, getResources().getDisplayMetrics().widthPixels, view.findViewById(R.id.ivShowImage));
                    bigImageTask.execute();
                    imageTasks.add(bigImageTask);
                    //???????????????????????????
                    alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                    alertDialog.setCancelable(true);
                    alertDialog.show();

                }
            });

        }

    }

    //??????comment > ????????????comment???????????????
    public List<Comment> getComments() {
        List<Comment> comments = null;
        if (Common.networkConnected(activity)) {
            String url = Common.URL_SERVER + "CommentServlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "findCommentById");
            jsonObject.addProperty("articleId", articleIdBox);
            jsonObject.addProperty("loginUserId", userIdBox);
            String jsonOut = jsonObject.toString();
            commentGetAllTask = new CommonTask(url, jsonOut);
            try {
                String jsonIn = commentGetAllTask.execute().get();
                Type listType = new TypeToken<List<Comment>>() {
                }.getType();
                comments = new Gson().fromJson(jsonIn, listType);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        } else {
            Common.showToast(activity, R.string.textNoNetwork);
        }
        return comments;
    }

    public void showComments(List<Comment> commentList) {
        if (commentList == null || commentList.isEmpty()) {
            Common.showToast(activity, R.string.textNoCommentFound);
        }
        CommentAdapter commentAdapter = (CommentAdapter) rvComment.getAdapter();
        if (commentAdapter == null) {
            rvComment.setAdapter(new CommentAdapter(activity, comments));
        } else {
            commentAdapter.setComments(commentList);   //???comments???????????????commentsAdapter???
            commentAdapter.notifyDataSetChanged();
        }
    }

    private class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.MyViewHolder> {
        private LayoutInflater layoutInflater;
        private List<Comment> comments;
        private int imageSize;

        CommentAdapter(Context context, List<Comment> comments) {
            layoutInflater = LayoutInflater.from(context);
            this.comments = comments;
            /* ??????????????????4????????????????????? */
            imageSize = getResources().getDisplayMetrics().widthPixels * 4;
        }

        //comments > setter??????List<Comment> ??????CommentAdapter????????????
        public void setComments(List<Comment> comments) {
            this.comments = comments;
        }

        public CommentAdapter(List<Comment> comments) {
            this.comments = comments;
        }

        @Override
        public int getItemCount() {
            return comments == null ? 0 : comments.size();
        }

        //???????????????????????? > comment
        class MyViewHolder extends RecyclerView.ViewHolder {
            ImageView ivCommentUserAvatar, ivCommentSetting, ivCommentGoodIcon;
            TextView tvCommentUserName, tvCommentText, tvCommentTime, tvCommentGood;

            MyViewHolder(@NonNull View itemView) {
                super(itemView);
                ivCommentUserAvatar = itemView.findViewById(R.id.ivCommentUserAvatar);
                ivCommentSetting = itemView.findViewById(R.id.ivCommentSetting);
                ivCommentGoodIcon = itemView.findViewById(R.id.ivCommentGoodIcon);
                tvCommentUserName = itemView.findViewById(R.id.tvCommentUserName);
                tvCommentText = itemView.findViewById(R.id.tvCommentText);
                tvCommentTime = itemView.findViewById(R.id.tvCommentTime);
                tvCommentGood = itemView.findViewById(R.id.tvCommentGood);
            }
        }

        //????????????(???????????????) > comment
        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = layoutInflater.inflate(R.layout.article_comment_view, parent, false);
            return new MyViewHolder(itemView);
        }

        /* ????????????MyViewHolder??????????????????????????? > comment */
        @Override
        public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int position) {
            final Comment comment = comments.get(position);

            //???????????????????????? > ????????????List
            String urlIcon = Common.URL_SERVER + "UserAccountServlet";
            int userId = comment.getUserId();
            ImageTask imageTaskIcon = new ImageTask(urlIcon, userId, imageSize, myViewHolder.ivCommentUserAvatar);
            imageTaskIcon.execute();
            imageTasks.add(imageTaskIcon);

            //????????????
            myViewHolder.tvCommentUserName.setText(comment.getUserName());
            myViewHolder.tvCommentText.setText(comment.getCommentText());

            /* ???????????????????????? != ??????????????????(????????????) > ???????????????????????????(?????????)?????????????????????????????????(?????????) */
            if (!comment.getCommentText().equals(comment.getCommentModifyTime())) {
                myViewHolder.tvCommentTime.setText(comment.getCommentModifyTime());
            } else {
                myViewHolder.tvCommentTime.setText(comment.getCommentTime());
            }

            //??????????????????????????????setting??????
            myViewHolder.ivCommentSetting.setImageResource(R.drawable.ic_baseline_more_vert_24);//????????????????????????optionMenu
            if (comment.getUserId() == userIdBox) {
                myViewHolder.ivCommentSetting.setVisibility(View.VISIBLE);
                //Popmenu   >   ??????setting
                myViewHolder.ivCommentSetting.setOnClickListener(v -> {
                    PopupMenu popupMenu = new PopupMenu(activity, v);
                    popupMenu.getMenuInflater().inflate(R.menu.comment_setting_menu, popupMenu.getMenu());
                    //Popmenu?????????
                    popupMenu.setOnMenuItemClickListener(item -> {
                        String url = Common.URL_SERVER + "CommentServlet";
                        switch (item.getItemId()) {
                            case R.id.itUpdateComment:

                                /*??????????????????*/
                                //1.????????????
                                if (Common.networkConnected(activity)) {
                                    JsonObject jsonObject = new JsonObject();
                                    jsonObject.addProperty("action", "findById");
                                    jsonObject.addProperty("commentId", new Gson().toJson(comment.getCommentId()));
                                    String jsonOut = jsonObject.toString();
                                    articleGetAllTask = new CommonTask(url, jsonOut);
                                    try {
                                        String jsonIn = articleGetAllTask.execute().get();
                                        Type listType = new TypeToken<Comment>() {
                                        }.getType();
                                        article = new Gson().fromJson(jsonIn, listType);
                                    } catch (Exception e) {
                                        Log.e(TAG, e.toString());
                                    }
                                }
                                //2.?????????editText???
                                etComment.setText("");
                                etComment.setText(comment.getCommentText());
                                //??????????????????
                                InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                                // ???????????????????????? > ??????
                                imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
                                etComment.requestFocus();//setFocus???????????? //addAddressRemarkInfo is EditText

                                //3.????????????
                                btCommentSend.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (Common.networkConnected(activity)) {
                                            JsonObject jsonObject = new JsonObject();
                                            int commentId = comment.getCommentId();
                                            String commentText = etComment.getText().toString().trim();
                                            /* ?????????????????????????????????????????? */
                                            SimpleDateFormat nowDate = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                            nowDate.setTimeZone(TimeZone.getTimeZone("GMT+8"));    //????????????
                                            String strDate = nowDate.format(new java.util.Date());    //??????????????????

                                            String commentModifyTime = strDate;
                                            comment.setComment(commentId, commentText, commentModifyTime);
                                            jsonObject.addProperty("action", "commentUpdate");
                                            jsonObject.addProperty("comment", new Gson().toJson(comment));
                                            int count = 0;
                                            try {
                                                //????????????????????????json???????????????(??????url)
                                                String result = new CommonTask(url, jsonObject.toString()).execute().get();
                                                count = Integer.parseInt(result);   //???result"??????????????????"??????int?????????
                                            } catch (Exception e) {
                                                Log.e(TAG, e.toString());
                                            }
                                            if (count == 0) {   //??????????????????0???
                                                Common.showToast(activity, "??????????????????");
                                            } else {
                                                Common.showToast(activity, "??????????????????");
                                            }
                                            comments = getComments();
                                            showComments(comments);
                                            //????????????
                                            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                                            if (imm.isActive()) {
                                                // ???????????????????????? > ??????
                                                imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT,
                                                        InputMethodManager.HIDE_NOT_ALWAYS);
                                            }
                                            //???????????????edText
                                            etComment.setText("");
                                        }
                                    }
                                });
                                break;

                            case R.id.itDeleteComment:
                                //????????????
                                if (Common.networkConnected(activity)) {
//                                    String url = Common.URL_SERVER + "CommentServlet";
                                    comment.setStatus(false);  //??????????????????
                                    JsonObject jsonObject = new JsonObject();
                                    jsonObject.addProperty("action", "commentDelete");
                                    jsonObject.addProperty("comment", new Gson().toJson(comment));
                                    int count = 0; //????????????????????????
                                    try {
                                        //????????????????????????json???????????????(??????url)
                                        String result = new CommonTask(url, jsonObject.toString()).execute().get();
                                        count = Integer.parseInt(result);   //???result"??????????????????"??????int?????????
                                    } catch (Exception e) {
                                        Log.e(TAG, e.toString());
                                    }
                                    if (count == 0) {   //??????????????????0???
                                        Common.showToast(activity, "??????????????????");
                                    } else {
                                        Common.showToast(activity, "??????????????????");
                                    }
                                    comments = getComments();
                                    showComments(comments);
                                }
                                break;
                        }
                        return false;
                    });
                    popupMenu.show();
                });
            } else {
                myViewHolder.ivCommentSetting.setVisibility(View.GONE);
            }

            /* ?????? ???????????? ?????? */
            //????????????????????????????????? > comment
//            final CommentGood commentGood = commentGoods.get(position);
//            boolean commentGoodStatus = comment.isCommentGoodStatus();
            ImageView CommentGoodIcon = myViewHolder.ivCommentGoodIcon;
            if (userIdBox == 0) {
                comment.setCommentGoodStatus(false);
                CommentGoodIcon.setColorFilter(Color.parseColor("#424242"));
            } else {
                Log.d(TAG, "c8 c8 8c 8c8 c8 : " + comment.isCommentGoodStatus());
                if (comment.isCommentGoodStatus() == true) {
                    CommentGoodIcon.setColorFilter(Color.parseColor("#1877F2"));
                    comment.setCommentGoodStatus(true);
                } else {
                    CommentGoodIcon.setColorFilter(Color.parseColor("#424242"));
                    comment.setCommentGoodStatus(false);
                }
            }
            myViewHolder.ivCommentGoodIcon.setImageResource(R.drawable.ic_baseline_thumb_up_24);
            myViewHolder.tvCommentGood.setText((comment.getCommentGoodCount() + ""));
            //???????????????   >   ????????????
            if (userIdBox != 0) {
                myViewHolder.ivCommentGoodIcon.setOnClickListener(v -> {
                    if (!comment.isCommentGoodStatus()) {   //????????????
                        if (Common.networkConnected(activity)) {
                            String commentGoodUrl = Common.URL_SERVER + "CommentGoodServlet";
                            int insertcommentId = comment.getCommentId();
                            Comment commentGood1 = new Comment(userIdBox, insertcommentId);
                            JsonObject jsonObject = new JsonObject();
                            jsonObject.addProperty("action", "commentGoodInsert");
                            jsonObject.addProperty("commentGood", new Gson().toJson(commentGood1));
                            int count = 0;
                            try {
                                String result = new CommonTask(commentGoodUrl, jsonObject.toString()).execute().get();
                                count = Integer.parseInt(result);
                            } catch (Exception e) {
                                Log.e(TAG, e.toString());
                            }
                            if (count == 0) {
                                Common.showToast(activity, "????????????");
                            } else {
                                comment.setCommentGoodCount(comment.getCommentGoodCount() + 1);
                                myViewHolder.tvCommentGood.setText((comment.getCommentGoodCount() + ""));
                                CommentGoodIcon.setColorFilter(Color.parseColor("#1877F2"));
                                comment.setCommentGoodStatus(true);
                            }
                        } else {
                            Common.showToast(activity, "??????????????????");
                        }
                    } else {    //???????????????
                        if (Common.networkConnected(activity)) {
                            String deleteGoodUrl = Common.URL_SERVER + "CommentGoodServlet";
                            JsonObject jsonObject = new JsonObject();
                            jsonObject.addProperty("action", "commentGoodDelete");
                            jsonObject.addProperty("commentId", comment.getCommentId());
                            jsonObject.addProperty("userId", userIdBox);
                            int count = 0;
                            try {
                                commentDeleteTask = new CommonTask(deleteGoodUrl, jsonObject.toString());
                                String result = commentDeleteTask.execute().get();
                                count = Integer.parseInt(result);
                            } catch (Exception e) {
                                Log.e(TAG, e.toString());
                            }
                            if (count == 0) {
                                Common.showToast(activity, "????????????");
                            } else {
                                comment.setCommentGoodCount(comment.getCommentGoodCount() - 1);
                                myViewHolder.tvCommentGood.setText((comment.getCommentGoodCount() + ""));
                                CommentGoodIcon.setColorFilter(Color.parseColor("#424242"));
                                comment.setCommentGoodStatus(false);
                            }
                        } else {
                            Common.showToast(activity, "?????????????????????");
                        }
                    }
                });
            }
        }
    }

    /* ?????????getCacheDir() + ?????????????????????????????? */
    private void saveFile_getCacheDir(String fileName, Img img) {
        File file = new File(activity.getCacheDir(), fileName);
        Log.d(TAG, "getCacheDir() path: " + file.getPath());
        try (ObjectOutputStream out = new ObjectOutputStream(
                new FileOutputStream(file))) {
            out.writeObject(img);
        } catch (IOException e) {
            Log.e(TAG, e.toString());
        } //????????????????????????????????????cache???????????????
    }

    private String saveToInternalStorage(Bitmap bitmapImage) {
        ContextWrapper cw = new ContextWrapper(activity);
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        File mypath = new File(directory, "profile.jpg");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, fos);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return directory.getAbsolutePath();
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