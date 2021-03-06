package com.example.foodradar_android.article;

import android.app.Activity;
import android.content.Context;
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
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import com.example.foodradar_android.Common;
import com.example.foodradar_android.R;
import com.example.foodradar_android.task.CommonTask;
import com.example.foodradar_android.task.ImageTask;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class RankArticleFragment extends Fragment {
    private static final String TAG = "TAG_ArticleFragment";
    private RecyclerView rvArticleRank;
    private List<Article> articles;
    private Activity activity;
    private List<ImageTask> imageTasks;
    private List<ArticleImageTask> articleImageTasks;
    private SwipeRefreshLayout swipeRefreshLayoutRank;
    private CommonTask articleGetAllTask;
    private CommonTask articleDeleteTask;
    private NavController navController;
    private int userIdBox = Common.USER_ID;
    private SearchView articleSearchView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imageTasks = new ArrayList<>();
        articleImageTasks = new ArrayList<>();
        activity = getActivity();
    }

    //??????floating
    @Override
    public void onStart() {
        super.onStart();
        //?????? floatingActionButton
        if (userIdBox == 0) {
            Common.faButtonControl(activity, false);
        } else {
            Common.faButtonControl(activity, true);
        }

        //??????bottomNav
        ArticleFragment.bottomNavSet(activity, 1);
    }

    // ??????????????????OptionMenu??????
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.appbar_menu, menu);  // ???res????????????????????????R.menu.my_menu???
        super.onCreateOptionsMenu(menu, inflater);
    }

    // ????????????????????????OptionMenu??????
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
        return inflater.inflate(R.layout.fragment_rank_article, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvArticleRank = view.findViewById(R.id.rvArticleRank);
        swipeRefreshLayoutRank = view.findViewById(R.id.swipeRefreshLayoutRank);

        rvArticleRank.setLayoutManager(new LinearLayoutManager(activity));
        rvArticleRank.setItemViewCacheSize(50); //????????????rvArticle?????????50???????????????????????????
        articles = getArticle();
        showArticle(articles);

        //swipeRefreshLayout
        swipeRefreshLayoutRank.setOnRefreshListener(() -> {
            swipeRefreshLayoutRank.setRefreshing(true);
            articles = getArticle();
            showArticle(articles);
            swipeRefreshLayoutRank.setRefreshing(false);
        });

        /* searchView */
        articleSearchView = view.findViewById(R.id.articleSearchView);
        articleSearchView.setIconifiedByDefault(false);
        articleSearchView.setIconified(true);
        articleSearchView.setMaxWidth(1030);
        articleSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String nextText) {
                // ??????searchView?????????????????????????????????????????????????????????????????????
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

    //???server?????????Article??????
    private List<Article> getArticle() {
        List<Article> articles = null;
        if (Common.networkConnected(activity)) {
            String url = Common.URL_SERVER + "ArticleServlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "getAllByIdRank");
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
            //??????Toast????????????????????????????????????
            Common.showToast(activity, R.string.textNoNetwork);
        }
        return articles;
    }

    private void showArticle(List<Article> articleList) {
        if (articleList == null || articleList.isEmpty()) {
            //??????Toast????????????????????????????????????
            Common.showToast(activity, R.string.textNoArticleFound);
            Log.e(TAG, "article:" + articleList);
        }
        ArticleAdapter articleAdapter = (ArticleAdapter) rvArticleRank.getAdapter();
        if (articleAdapter == null) {
            rvArticleRank.setAdapter(new ArticleAdapter(activity, articleList));
        } else {
            articleAdapter.setArticleList(articleList);
            articleAdapter.notifyDataSetChanged();
        }
    }

    private class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.MyViewHolder> {
        private LayoutInflater layoutInflater;
        private List<Article> articles;
        private int imageSize;

        //????????????????????????????????????????????????ArticleAdapter????????????
        ArticleAdapter(Context context, List<Article> articleList) {
            layoutInflater = LayoutInflater.from(context);
            this.articles = articleList;
            //?????????????????????????????????
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
                tvCommentCount = itemView.findViewById(R.id.tvCommentCount);
                tvGoodCount = itemView.findViewById(R.id.tvgoodCount);
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
            //article?????? > ?????????????????????????????????
            final Article article = articles.get(position);
            //onBindViewHolder???????????????????????????????????????
            //????????????
            String url = Common.URL_SERVER + "ImgServlet";
            int articleId = article.getArticleId();
            ArticleImageTask articleImageTask = new ArticleImageTask(url, articleId, imageSize, myViewHolder.imgView);
            articleImageTask.execute();
            articleImageTasks.add(articleImageTask);

            //?????????????????????
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

            /* ?????????????????????????????????????????????????????? */
            if (article.getArticleTime().equals(article.getModifyTime())) {
                myViewHolder.tvArticleTime.setText("???????????????" + article.getArticleTime());
            } else {
                myViewHolder.tvArticleTime.setText("???????????????" + article.getModifyTime());
            }

            myViewHolder.tvCommentCount.setText(commentCount);
            myViewHolder.ivArticleCommentIcon.setImageResource(R.drawable.ic_baseline_chat_bubble_24);


            //?????????????????????1.???????????????????????????????????????
            //2.?????????????????????????????????
            //            boolean articleGoodStatus = article.isArticleGoodStatus();
            ImageView goodIcon = myViewHolder.ivGoodIcon;
            if (userIdBox == 0) {    //0 > ??????????????????????????????
                article.setArticleGoodStatus(false);
                goodIcon.setColorFilter(Color.parseColor("#424242"));
            } else { //??????????????????????????????
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


            //3.???????????????   >   ??????
            if (userIdBox != 0) {
                myViewHolder.ivGoodIcon.setOnClickListener(v -> {
                    if (!article.isArticleGoodStatus()) {
                        if (Common.networkConnected(activity)) {
                            String insertGoodUrl = Common.URL_SERVER + "ArticleServlet";
//                        int insertUserId = article.getUserId();
//                        int insertArticleId = article.getArticleId();
//                        Article articleGood = new Article(userIdBox, insertArticleId);
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
                                Common.showToast(activity, "????????????");
                            } else {
                                article.setArticleGoodCount((article.getArticleGoodCount() + 1));
                                myViewHolder.tvGoodCount.setText((article.getArticleGoodCount() + ""));

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
                            if (count == 0) { //????????????????????????????????????
                                Common.showToast(activity, "????????????");
                            } else {
                                article.setArticleGoodCount(article.getArticleGoodCount() - 1);
                                myViewHolder.tvGoodCount.setText(((article.getArticleGoodCount()) + ""));

                                goodIcon.setColorFilter(Color.parseColor("#424242"));
                                article.setArticleGoodStatus(false);
                            }
                        } else {
                            Common.showToast(activity, "?????????????????????");
                        }
                    }
                });
            }

            //?????????????????????1.???????????????????????????????????????
            //2.?????????????????????????????????
            final boolean articleFavoriteStatus = article.isArticleFavoriteStatus();
            ImageView favoriteIcon = myViewHolder.ivFavoriteIcon;
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
            myViewHolder.ivFavoriteIcon.setImageResource(R.drawable.ic_baseline_favorite_24);
            myViewHolder.tvFavoriteArticle.setText((article.getFavoriteCount() + ""));

            //3.???????????????   >   ??????
            if (userIdBox != 0) {
                myViewHolder.ivFavoriteIcon.setOnClickListener(v -> {
                    if (!article.isArticleFavoriteStatus()) {
                        if (Common.networkConnected(activity)) {
                            String insertFavoriteUrl = Common.URL_SERVER + "ArticleServlet";
                            JsonObject jsonObject = new JsonObject();
                            jsonObject.addProperty("action", "articleFavoriteInsert");
                            jsonObject.addProperty("articleId", article.getArticleId());
                            jsonObject.addProperty("loginUserId", userIdBox);
                            int count = 0;
                            try {
                                String result = new CommonTask(insertFavoriteUrl, jsonObject.toString()).execute().get();
                                count = Integer.parseInt(result);
                            } catch (Exception e) {
                                Log.e(TAG, e.toString());
                            }
                            if (count == 0) {
                                Common.showToast(activity, "????????????");
                            } else {
                                article.setFavoriteCount((article.getFavoriteCount() + 1));
                                myViewHolder.tvFavoriteArticle.setText(((article.getFavoriteCount()) + ""));
                                favoriteIcon.setColorFilter(Color.parseColor("#EADDAB"));
                                article.setArticleFavoriteStatus(true);
                            }
                        } else {
                            Common.showToast(activity, "??????????????????");
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
                            if (count == 0) { //????????????????????????????????????
                                Common.showToast(activity, "????????????");
                            } else {
                                article.setFavoriteCount((article.getFavoriteCount() - 1));
                                myViewHolder.tvFavoriteArticle.setText((article.getFavoriteCount() + ""));

                                favoriteIcon.setColorFilter(Color.parseColor("#424242"));
                                article.setArticleFavoriteStatus(false);
                            }
                        } else {
                            Common.showToast(activity, "????????????????????????");
                        }
                    }
                });
            }

            //?????????detail
            myViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Article.ARTICLE_ID = article.getArticleId();
                    Article.USER_ID = article.getUserId();
                    Navigation.findNavController(v).navigate(R.id.action_rankArticleFragment_to_articleDetailFragment);
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