package com.example.foodradar_android.article;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.ColorUtils;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import com.example.foodradar_android.Common;
import com.example.foodradar_android.R;
import com.example.foodradar_android.res.Res;
import com.example.foodradar_android.task.CommonTask;
import com.example.foodradar_android.task.ImageTask;
import com.example.foodradar_android.user.MyArticle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class NewArticleFragment extends Fragment {
    private static final String TAG = "TAG_ArticleFragment";
    private RecyclerView rvArticle;
    private List<Article> articles;
    private Activity activity;
    private List<ImageTask> imageTasks;
    private List<ArticleImageTask> articleImageTasks;
    private SwipeRefreshLayout swipeRefreshLayout;
    private CommonTask articleGetAllTask;
    private CommonTask articleDeleteTask;
    private NavController navController;
    private int userIdBox = Common.USER_ID;
    private SearchView articleSearchView;
    private Res res;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imageTasks = new ArrayList<>();
        articleImageTasks = new ArrayList<>();
        activity = getActivity();


        // 顯示左上角的返回箭頭
        new Common();
        Common.setBackArrow(false, activity);
        setHasOptionsMenu(false);
        navController =
                Navigation.findNavController(activity, R.id.mainFragment);
    }

    //顯示floating
    @Override
    public void onStart() {
        super.onStart();
        //隱藏 floatingActionButton
        if (userIdBox == 0) {
            Common.faButtonControl(activity, false);
        } else {
            Common.faButtonControl(activity, true);
        }

        //顯示bottomNav
        ArticleFragment.bottomNavSet(activity, 1);

    }

    // 顯示右上角的OptionMenu選單
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.appbar_menu, menu);  // 從res取用選項的清單“R.menu.my_menu“
        super.onCreateOptionsMenu(menu, inflater);
    }

    // 設定右上及左上的OptionMenu選單
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.Finish:
                navController.navigate(R.id.action_userAreaFragment_to_userSysSetupFragment);
                break;
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
        return inflater.inflate(R.layout.fragment_new_article, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // SearchView articleSearchView = view.findViewById(R.id.articleSearchView);
        rvArticle = view.findViewById(R.id.rvArticle);
        rvArticle.setItemViewCacheSize(50); //設定緩存rvArticle數量為50，避免重複利用問題

        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        articleSearchView = view.findViewById(R.id.articleSearchView);
        articleSearchView.setIconifiedByDefault(false);
        articleSearchView.setIconified(true);
        articleSearchView.setMaxWidth(1030);    //searchView底線長度

        rvArticle.setLayoutManager(new LinearLayoutManager(activity));
        articles = getArticle();
        showArticle(articles);

        /* 餐廳細節跳轉bundle判斷*/
        Intent intent = getActivity().getIntent();
        if (intent != null) {
            res = (Res) intent.getSerializableExtra("res");
        }
        intent.removeExtra("res");  //清空intent > 避免bundle一直存在
        //res = (Res) (bundle != null ? bundle.getSerializable("res") : null);
        if (res == null) {
            showArticle(articles);
        }
        else {
            articleSearchView.setQuery(res.getResName(), false);
            List<Article> searchArticle = new ArrayList<>();
            // 當 article.getResName() 等於 res.getResName() 顯示搜尋的文章
            for (Article article : articles) {
                if (article.getResName().toUpperCase().contains(res.getResName().toUpperCase())) {
                    searchArticle.add(article);
                }
            }
            showArticle(searchArticle);
        }

        //swipeRefreshLayout
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                articles = getArticle();
                showArticle(articles);
                swipeRefreshLayout.setRefreshing(false);
            }
        });
//        swipeRefreshLayout.setOnRefreshListener(() -> {
//            swipeRefreshLayout.setRefreshing(true);
//            showArticle(articles);
//            swipeRefreshLayout.setRefreshing(false);
//        });

        /* 從“我的社群活動”頁面，跳轉到“討論區”頁面並轉到文章detail頁面 */
        if (MyArticle.goToMyArticleDetail) {
            MyArticle.goToMyArticleDetail = false;
            Navigation.findNavController(view).navigate(R.id.action_newArticleFragment_to_articleDetailFragment);
        }

        /* searchView */
        articleSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String nextText) {
                // 如果searchView為空字串，就顯示全部資料；否則就顯示搜尋後結果
                if (nextText.isEmpty()) {
                    showArticle(articles);
                } else {
                    List<Article> searchArticle = new ArrayList<>();
                    for (Article article : articles) {
                        if ((article.getArticleTitle().toUpperCase().contains(nextText.toUpperCase())) ||
                                (article.getResCategoryInfo().toUpperCase().contains(nextText.toUpperCase())) ||
                                (article.getResName().toUpperCase().contains(nextText.toUpperCase()))) {
                            searchArticle.add(article);
                        }
                    }
                    showArticle(searchArticle);
                }
                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
        });
    }

    //向server端取得Article資料
    private List<Article> getArticle() {
        List<Article> articles = null;
        if (Common.networkConnected(activity)) {
            String url = Common.URL_SERVER + "ArticleServlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "getAllById");
            jsonObject.addProperty("loginUserId", new Gson().toJson(userIdBox));
            String jsonOut = jsonObject.toString();
            articleGetAllTask = new CommonTask(url, jsonOut);
            try {
                String jsonIn = articleGetAllTask.execute().get();
                Type listType = new TypeToken<List<Article>>() {
                }.getType();
                articles = new Gson().fromJson(jsonIn, listType);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        } else {
            //暫定Toast，須修改錯誤時執行的動作
            Common.showToast(activity, R.string.textNoNetwork);
        }
        return articles;
    }


    private void showArticle(List<Article> articleList) {
        if (articleList == null || articleList.isEmpty()) {
            //暫定Toast，須修改錯誤時執行的動作
//            Common.showToast(activity, R.string.textNoArticleFound);
            Log.e(TAG, "article:" + articleList);
        }
        ArticleAdapter articleAdapter = (ArticleAdapter) rvArticle.getAdapter();
        if (articleAdapter == null) {
            rvArticle.setAdapter(new ArticleAdapter(activity, articleList));
        } else {
            articleAdapter.setArticleList(articleList);
            articleAdapter.notifyDataSetChanged();
        }
    }


    private class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.MyViewHolder> {
        private LayoutInflater layoutInflater;
        private List<Article> articles;
        private List<Img> imgList;
        private int imageSize;

        //取得圖片並設定顯示圖片尺寸設定，ArticleAdapter建構方法
        ArticleAdapter(Context context, List<Article> articleList) {
            layoutInflater = LayoutInflater.from(context);
            articles = articleList;

            //螢幕寬度當作將圖的尺寸
            imageSize = getResources().getDisplayMetrics().heightPixels;
        }

        public List<Article> getArticleList() {
            return articles;
        }

        public void setArticleList(List<Article> articleList) {
            articles = articleList;
        }

        @Override
        public int getItemCount() {
            return articles == null ? 0 : articles.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            ImageView userIcon, ivArticleCommentIcon, imgView;
            ImageView ivGoodIcon, ivFavoriteIcon;
            TextView userName, resCategoryInfo, articleTitle, resName, tvArticleTime;
            TextView tvGoodCount, tvCommentCount, tvFavoriteArticle;

            MyViewHolder(@NonNull View itemView) {
                super(itemView);
                userIcon = itemView.findViewById(R.id.userIcon);
                ivArticleCommentIcon = itemView.findViewById(R.id.ivArticleCommentIcon);
                imgView = itemView.findViewById(R.id.imgView);
                userName = itemView.findViewById(R.id.userName);
                resCategoryInfo = itemView.findViewById(R.id.resCategoryInfo);
                articleTitle = itemView.findViewById(R.id.articleTitle);
                resName = itemView.findViewById(R.id.resName);
                tvArticleTime = itemView.findViewById(R.id.tvArticleTime);
                tvGoodCount = itemView.findViewById(R.id.tvgoodCount);
                tvCommentCount = itemView.findViewById(R.id.tvCommentCount);
                tvFavoriteArticle = itemView.findViewById(R.id.tvFavoriteArticle);
                ivGoodIcon = itemView.findViewById(R.id.ivGoodIcon);
                ivFavoriteIcon = itemView.findViewById(R.id.ivFavoriteIcon);

            }
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = layoutInflater.inflate(R.layout.article_item_view, parent, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull ArticleAdapter.MyViewHolder myViewHolder, int position) {
            //article物件 > 包裝要呈現在畫面的資料
            final Article article = articles.get(position);
            Img img = new Img();
//            final Img img = ArticleList.get(position);
            //onBindViewHolder才會向後端發出請求取得圖片
            //取得餐廳大圖
            String url = Common.URL_SERVER + "ImgServlet";
            int articleId = article.getArticleId();
            ArticleImageTask articleImageTask = new ArticleImageTask(url, articleId, imageSize, myViewHolder.imgView);
            articleImageTask.execute();
            articleImageTasks.add(articleImageTask);

            //取得使用者小圖
            String urlIcon = Common.URL_SERVER + "UserAccountServlet";
            int userId = article.getUserId();
            ImageTask imageTaskIcon = new ImageTask(urlIcon, userId, imageSize, myViewHolder.userIcon);
            imageTaskIcon.execute();
            imageTasks.add(imageTaskIcon);

            String commentCount = article.getCommentCount() + "";

            myViewHolder.userName.setText(article.getUserName());
            myViewHolder.resCategoryInfo.setText(article.getResCategoryInfo());
            myViewHolder.articleTitle.setText(article.getArticleTitle());
            myViewHolder.resName.setText(article.getResName());

            /* 判斷顯示是否有更新過文章，並顯示時間 */
            if (article.getArticleTime().equals(article.getModifyTime())) {
                myViewHolder.tvArticleTime.setText("發表時間：" + article.getArticleTime());
            } else {
                myViewHolder.tvArticleTime.setText("修改時間：" + article.getModifyTime());
            }

            myViewHolder.tvCommentCount.setText(commentCount);
            myViewHolder.ivArticleCommentIcon.setImageResource(R.drawable.ic_baseline_chat_bubble_24);


            //設定點讚功能，1.會員登入判斷還沒寫，要候補
            //2.先判斷使用者是否已點讚
//            boolean articleGoodStatus = article.isArticleGoodStatus();
            ImageView goodIcon = myViewHolder.ivGoodIcon;
            if (userIdBox == 0) {    //0 > 訪客，一律設為沒點讚
                article.setArticleGoodStatus(false);
                goodIcon.setColorFilter(Color.parseColor("#424242"));
            } else { //否則就判斷是否有點讚
                if (article.isArticleGoodStatus()) {
                    goodIcon.setColorFilter(Color.parseColor("#1877F2"));
                    article.setArticleGoodStatus(true);
                } else {
                    goodIcon.setColorFilter(Color.parseColor("#424242"));
                    article.setArticleGoodStatus(false);
                }
            }
            myViewHolder.ivGoodIcon.setImageResource(R.drawable.ic_baseline_thumb_up_24);
            myViewHolder.tvGoodCount.setText((article.getArticleGoodCount() + ""));

            //3.設定監聽器   >   點讚
            if (userIdBox != 0) {
                myViewHolder.ivGoodIcon.setOnClickListener(v -> {
                    if (!article.isArticleGoodStatus()) {
                        if (Common.networkConnected(activity)) {
                            String insertGoodUrl = Common.URL_SERVER + "ArticleServlet";
                            int insertUserId = article.getUserId();
                            int insertArticleId = article.getArticleId();
                            Article articleGood = new Article(userIdBox, insertArticleId);
                            JsonObject jsonObject = new JsonObject();
                            jsonObject.addProperty("action", "articleGoodInsert");
                            jsonObject.addProperty("articleId", article.getArticleId());
                            jsonObject.addProperty("loginUserId", userIdBox);
//                        jsonObject.addProperty("articleGood", new Gson().toJson(articleGood));
                            int count = 0;
                            try {
                                String result = new CommonTask(insertGoodUrl, jsonObject.toString()).execute().get();
                                count = Integer.parseInt(result);
                            } catch (Exception e) {
                                Log.e(TAG, e.toString());
                            }
                            if (count == 0) {
                                Common.showToast(activity, "點讚失敗");
                            } else {
                                article.setArticleGoodCount(article.getArticleGoodCount() + 1);
                                myViewHolder.tvGoodCount.setText((article.getArticleGoodCount() + ""));

                                goodIcon.setColorFilter(Color.parseColor("#1877F2"));
                                article.setArticleGoodStatus(true);
                            }
                        } else {
                            Common.showToast(activity, "取得連線失敗");
                        }
                    } else {
                        if (Common.networkConnected(activity)) {
                            String deleteGoodUrl = Common.URL_SERVER + "ArticleServlet";
                            JsonObject jsonObject = new JsonObject();
                            jsonObject.addProperty("action", "articleGoodDelete");
                            jsonObject.addProperty("articleId", article.getArticleId());
                            jsonObject.addProperty("userId", userIdBox);
                            int count = 0;
                            try {
                                articleDeleteTask = new CommonTask(deleteGoodUrl, jsonObject.toString());
                                String result = articleDeleteTask.execute().get();
                                count = Integer.parseInt(result);
                            } catch (Exception e) {
                                Log.e(TAG, e.toString());
                            }
                            if (count == 0) { //如果選擇的資料已經沒東西
                                Common.showToast(activity, "取消失敗");
                            } else {
                                article.setArticleGoodCount(article.getArticleGoodCount() - 1);
                                myViewHolder.tvGoodCount.setText(((article.getArticleGoodCount()) + ""));

                                goodIcon.setColorFilter(Color.parseColor("#424242"));
                                article.setArticleGoodStatus(false);
                            }
                        } else {
                            Common.showToast(activity, "取消讚連線失敗");
                        }
                    }
                });
            }

            //設定收藏功能，1.會員登入判斷還沒寫，要候補
            //2.先判斷使用者是否已收藏
            final boolean articleFavoriteStatus = article.isArticleFavoriteStatus();
            ImageView favoriteIcon = myViewHolder.ivFavoriteIcon;
            if (userIdBox == 0) {    //0 > 訪客，一律設為沒點讚
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
            myViewHolder.ivFavoriteIcon.setImageResource(R.drawable.ic_baseline_favorite_24);
            myViewHolder.tvFavoriteArticle.setText((article.getFavoriteCount() + ""));

            //3.設定監聽器 > 收藏
            if (userIdBox != 0) {
                myViewHolder.ivFavoriteIcon.setOnClickListener(v -> {
                    if (!article.isArticleFavoriteStatus()) {
                        if (Common.networkConnected(activity)) {
                            String insertFavoriteUrl = Common.URL_SERVER + "ArticleServlet";
//                        Article articleFavorite = new Article(userIdBox, favoriteArticleId);
                            JsonObject jsonObject = new JsonObject();
                            jsonObject.addProperty("action", "articleFavoriteInsert");
                            jsonObject.addProperty("articleId", article.getArticleId());
                            jsonObject.addProperty("loginUserId", userIdBox);
//                        jsonObject.addProperty("articleFavorite", new Gson().toJson(articleFavorite));
                            int count = 0;
                            try {
                                String result = new CommonTask(insertFavoriteUrl, jsonObject.toString()).execute().get();
                                count = Integer.parseInt(result);
                            } catch (Exception e) {
                                Log.e(TAG, e.toString());
                            }
                            if (count == 0) {
                                Common.showToast(activity, "收藏失敗");
                            } else {
                                article.setFavoriteCount((article.getFavoriteCount() + 1));
                                myViewHolder.tvFavoriteArticle.setText(((article.getFavoriteCount()) + ""));
                                favoriteIcon.setColorFilter(Color.parseColor("#EADDAB"));
                                article.setArticleFavoriteStatus(true);
                            }
                        } else {
                            Common.showToast(activity, "取得連線失敗");
                        }
                    } else {
                        if (Common.networkConnected(activity)) {
                            String deleteFavoriteUrl = Common.URL_SERVER + "ArticleServlet";
                            JsonObject jsonObject = new JsonObject();
                            jsonObject.addProperty("action", "articleFavoriteDelete");
                            jsonObject.addProperty("loginUserId", userIdBox);
                            jsonObject.addProperty("articleId", article.getArticleId());
                            int count = 0;
                            try {
                                articleDeleteTask = new CommonTask(deleteFavoriteUrl, jsonObject.toString());
                                String result = articleDeleteTask.execute().get();
                                count = Integer.parseInt(result);
                            } catch (Exception e) {
                                Log.e(TAG, e.toString());
                            }
                            if (count == 0) { //如果選擇的資料已經沒東西
                                Common.showToast(activity, "取消失敗");
                            } else {
                                article.setFavoriteCount((article.getFavoriteCount() - 1));
                                myViewHolder.tvFavoriteArticle.setText((article.getFavoriteCount() + ""));

                                favoriteIcon.setColorFilter(Color.parseColor("#424242"));
                                article.setArticleFavoriteStatus(false);
                            }
                        } else {
                            Common.showToast(activity, "取消收藏連線失敗");
                        }
                    }
                });
            }

            //跳轉至detail
            myViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Article.ARTICLE_ID = article.getArticleId();
                    Article.USER_ID = article.getUserId();

                    Navigation.findNavController(v).navigate(R.id.action_newArticleFragment_to_articleDetailFragment);
                }
            });

        }
    }

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