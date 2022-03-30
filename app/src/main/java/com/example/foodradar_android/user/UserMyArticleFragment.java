package com.example.foodradar_android.user;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.foodradar_android.Common;
import com.example.foodradar_android.R;
import com.example.foodradar_android.article.Article;
import com.example.foodradar_android.task.CommonTask;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class UserMyArticleFragment extends Fragment {

    private Activity activity;
    private NavController navController;
    private List<MyArticle> myArticleList = new ArrayList<>();
    private List<UserImageTask> imageTasks= new ArrayList<>();
    private RecyclerView rcvMyArticleCollect;
    private RecyclerView rcvMyArticleIsMy;
    private RecyclerView rcvMyComment;

    //    private Integer userId;
    private String URL_SERVER = "http://10.0.2.2:8080/FoodRadar_Web/";
    private String MYARTICLE_SERVLET = URL_SERVER + "MyArticleServlet";
    private Integer imageSize = 100;
    final private String TAG = "UserMyArticleFragment";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
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
        activity.setTitle(R.string.title_of_myarticle);
        return inflater.inflate(R.layout.fragment_user_my_article, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rcvMyArticleCollect = view.findViewById(R.id.rcvMyArticleCollect);
//        rcvMyArticle.setLayoutManager(new LinearLayoutManager(activity));
        rcvMyArticleCollect.setLayoutManager(
                new StaggeredGridLayoutManager(1,
                        StaggeredGridLayoutManager.HORIZONTAL));

        rcvMyArticleIsMy = view.findViewById(R.id.rcvMyArticleIsMe);
        rcvMyArticleIsMy.setLayoutManager(
                new StaggeredGridLayoutManager(1,
                        StaggeredGridLayoutManager.HORIZONTAL));

        rcvMyComment = view.findViewById(R.id.rcvMyComment);
        rcvMyComment.setLayoutManager(
                new StaggeredGridLayoutManager(1,
                        StaggeredGridLayoutManager.HORIZONTAL));

        myArticleList = getMyArticleCollect();
        Log.d("TAG","myArticleList: " + myArticleList);
        showMyArticle(myArticleList, getMyArticleIsMe(),getMyArticleMyComment());
    }

    private int getUserId(){
        return Common.USER_ID;
    }


    // 自訂的內部類別(MyArticleAdapter) 繼承 RecyclerView.Adapter
    // 並同時自己建立 MyViewHolder 繼承 RecyclerView.ViewHolder 用來放要呈現在 ItemView 裡物件的資料
    private class MyArticleAdapter extends RecyclerView.Adapter<MyArticleAdapter.MyViewHolder> {
        private LayoutInflater layoutInflater; // 放外面傳進來的 Context / Activity
        private List<MyArticle> myArticleListInAdp; // 放外面傳進來的 物件List
        private Integer imageSize;

        // 自訂的內部類別(MyArticleAdapter)的建構子，提供外面傳入 Context 跟要呈現資料的List 用
        MyArticleAdapter (Context context, List<MyArticle> myArticleListAdp) {
            layoutInflater = LayoutInflater.from(context);
            this.myArticleListInAdp = myArticleListAdp;
            this.imageSize = getResources().getDisplayMetrics().widthPixels / 4; // 取得螢幕寬度當圖片尺寸的基準
        }

        // 自訂內部類別(MyArticleAdapter)的方法(setMyArticleListAdp)，接外面傳進來的 物件List
        public void setMyArticleListAdp (List<MyArticle> myArticleListAdp) {
            this.myArticleListInAdp = myArticleListAdp;
        }

        // 自訂內部類別(MyViewHolder) 繼承 RecyclerView.ViewHolder，
        // 用來初始化在Item View裡呈現資料的元件，之後給 onBindViewHolder 呼叫並設定資料用
        class MyViewHolder extends RecyclerView.ViewHolder {
            ImageView imMyAtResImg;
            TextView tvMyAtArticleTitle;
            TextView tvMyAtArticleTime;
            TextView tvMyAtUserName;
            TextView tvMyAtArticleText;
            public MyViewHolder(@NonNull View itemView) {
                super(itemView);
                imMyAtResImg = itemView.findViewById(R.id.imAtMaResImg);
                tvMyAtArticleTitle = itemView.findViewById(R.id.tvAtMaArticleTitle);
                tvMyAtArticleTime = itemView.findViewById(R.id.tvAtMaArticleTime);
                tvMyAtUserName = itemView.findViewById(R.id.tvAtMaUserName);
                tvMyAtArticleText = itemView.findViewById(R.id.tvAtMaArticleText);
            }
        }

        @NonNull
        @Override
        public MyArticleAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            // 載入自己建立在 res\layout 裡的 Item view XML檔
            View myItemView = layoutInflater
                    .inflate(R.layout.item_view_user_myarticle,parent,false);
            // 把 layout檔的view，傳入RecyclerView的ViewHolder(MyViewHolder)
            return new MyViewHolder(myItemView);
        }

        @Override
        public void onBindViewHolder(@NonNull MyArticleAdapter.MyViewHolder holder, int position) {
            MyArticle myArticleInBindViewHolder = myArticleListInAdp.get(position);
            Resources res = activity.getResources();
            holder.tvMyAtArticleTitle.setText(myArticleInBindViewHolder.getArticleTitle());

            if ( myArticleInBindViewHolder.getCommentId() == 0) {

                String strAtArticleTime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(myArticleInBindViewHolder.getArticleTime());
                holder.tvMyAtArticleTime.setText(res.getString(R.string.textArticleDate) + ": " + strAtArticleTime);
                holder.tvMyAtUserName.setText(res.getString(R.string.textArticleWirter) + ": " + myArticleInBindViewHolder.getUserName());
                holder.tvMyAtArticleText.setText(res.getString(R.string.textArticleText) + ": " + myArticleInBindViewHolder.getArticleText());
            } else {
                String strCommentTime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(myArticleInBindViewHolder.getCommentTime());
                holder.tvMyAtArticleTime.setText(res.getString(R.string.textCommentDate) + ": " + strCommentTime);
                holder.tvMyAtUserName.setText(res.getString(R.string.textArticleWirter) + ": " + myArticleInBindViewHolder.getUserName());
                holder.tvMyAtArticleText.setText(res.getString(R.string.textCommentText) + ": " + myArticleInBindViewHolder.getCommentText());
            }

//            holder.imMyAtResImg.setImageBitmap();
            final Integer articleId = myArticleInBindViewHolder.getArticleId(); // 文章ID
            Log.d(TAG,"articleId: " + articleId);
            Log.d(TAG,"MYARTICLE_SERVLET: " + MYARTICLE_SERVLET);
            UserImageTask userImageTask = new UserImageTask(MYARTICLE_SERVLET, articleId, imageSize, holder.imMyAtResImg);
            userImageTask.execute(); // .execute() => UserImage.doInBackground
            imageTasks.add(userImageTask);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 從“我的社群活動”頁面，跳轉到“討論區”頁面並轉到文章detail頁面
                    Article.ARTICLE_ID = articleId;
                    Article.USER_ID = getUserId();
                    MyArticle.goToMyArticleDetail = true;
                    navController.navigate(R.id.articleFragment);
                }
            });

        }

        @Override
        public int getItemCount() {
            if (myArticleListInAdp == null) {
                return 0;
            } else {
                return myArticleListInAdp.size();
            }
        }
    }

    // 我的文章收藏
    private List<MyArticle> getMyArticleCollect(){
        List<MyArticle> getMyArticleCollectList = null;

        if (Common.networkConnected(activity)) {
//            byte[] imgBytes = new byte[1];
//            MyRes myRes = new MyRes(1,"resName","resHours","resTel","resAddress", imgBytes);
//            getMyResList = new ArrayList<>();
//            getMyResList.add(0,myRes);
//            getMyResList.add(1,new MyRes(2,"resName2","resHours","resTel","resAddress", imgBytes));
//            for (int i = 2 ; i <= 13 ; i++){
//                getMyResList.add(i,new MyRes(i,"resName" + String.valueOf(i),"resHours","resTel","resAddress", imgBytes));
//            }
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "getMyArticleCollect");
            jsonObject.addProperty("id", getUserId());
            String jsonOut = jsonObject.toString();
            CommonTask getAllTask;
            getAllTask = new CommonTask(MYARTICLE_SERVLET, jsonOut);
            try {
                String jsonIn = getAllTask.execute().get();
                Type listType = new TypeToken<List<MyArticle>>() {
                }.getType();
                getMyArticleCollectList = new Gson().fromJson(jsonIn, listType);
                Log.d(TAG,"getMyArticleCollectList: " + getMyArticleCollectList);
            } catch (Exception e) {
//                Log.e(TAG, e.toString());
                e.printStackTrace();
            }

        } else {
            Common.showToast(activity,"NetworkConnected: fail");
        }

        return getMyArticleCollectList;
    }

    // 我的發文
    private List<MyArticle> getMyArticleIsMe(){
        List<MyArticle> getMyArticleIsMeList = null;

        if (Common.networkConnected(activity)) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "getMyArticleIsMe");
            jsonObject.addProperty("id", getUserId());
            String jsonOut = jsonObject.toString();
            CommonTask getAllTask;
            getAllTask = new CommonTask(MYARTICLE_SERVLET, jsonOut);
            try {
                String jsonIn = getAllTask.execute().get();
                Type listType = new TypeToken<List<MyArticle>>() {
                }.getType();
                getMyArticleIsMeList = new Gson().fromJson(jsonIn, listType);
                Log.d(TAG,"getMyArticleIsMeList: " + getMyArticleIsMeList);
            } catch (Exception e) {
//                Log.e(TAG, e.toString());
                e.printStackTrace();
            }

        } else {
            Common.showToast(activity,"NetworkConnected: fail");
        }

        return getMyArticleIsMeList;
    }

    // 我的回文
    private List<MyArticle> getMyArticleMyComment(){
        List<MyArticle> getMyArticleMyCommentList = null;

        if (Common.networkConnected(activity)) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "getMyArticleMyComment");
            jsonObject.addProperty("id", getUserId());
            String jsonOut = jsonObject.toString();
            CommonTask getAllTask;
            getAllTask = new CommonTask(MYARTICLE_SERVLET, jsonOut);
            try {
                String jsonIn = getAllTask.execute().get();
                Type listType = new TypeToken<List<MyArticle>>() {
                }.getType();
                getMyArticleMyCommentList = new Gson().fromJson(jsonIn, listType);
                Log.d(TAG,"getMyArticleMyCommentList: " + getMyArticleMyCommentList);
            } catch (Exception e) {
//                Log.e(TAG, e.toString());
                e.printStackTrace();
            }

        } else {
            Common.showToast(activity,"NetworkConnected: fail");
        }

        return getMyArticleMyCommentList;
    }



    private void showMyArticle(List<MyArticle> showMyArticleCollectList
            , List<MyArticle> showMyArticleIsMeList
            , List<MyArticle> showMyArticleMyComment){
//        rcvMyArticleCollect
//        rcvMyArticleIsMy
//        rcvMyComment


        // 我的文章收藏
        UserMyArticleFragment.MyArticleAdapter myArticleAdapter
                = (UserMyArticleFragment.MyArticleAdapter) rcvMyArticleCollect.getAdapter();
//        Log.d(TAG,"myArticleAdapter: " + myArticleAdapter);
        if (myArticleAdapter == null){
//            Log.d(TAG,"showMyArticleList: " + showMyArticleList);
            rcvMyArticleCollect.setAdapter(new UserMyArticleFragment.MyArticleAdapter(activity, showMyArticleCollectList));
        }else{
//            Log.d(TAG,"showMyArticleList: " + showMyArticleList);
            myArticleAdapter.setMyArticleListAdp(showMyArticleCollectList);
            myArticleAdapter.notifyDataSetChanged();
        }

        // 我的發文
        UserMyArticleFragment.MyArticleAdapter myArticleAdapterIsMe
                = (UserMyArticleFragment.MyArticleAdapter) rcvMyArticleIsMy.getAdapter();
//        Log.d(TAG,"rcvMyArticleIsMy: " + myArticleAdapterIsMy);
        if (myArticleAdapterIsMe == null){
//            Log.d(TAG,"showMyArticleIsMeList: " + showMyArticleIsMeList);
            rcvMyArticleIsMy.setAdapter(new UserMyArticleFragment.MyArticleAdapter(activity, showMyArticleIsMeList));
        }else{
//            Log.d(TAG,"showMyArticleIsMeList: " + showMyArticleIsMeList);
            myArticleAdapterIsMe.setMyArticleListAdp(showMyArticleIsMeList);
            myArticleAdapterIsMe.notifyDataSetChanged();
        }

        // 我的回文
        UserMyArticleFragment.MyArticleAdapter myArticleAdapterComment
                = (UserMyArticleFragment.MyArticleAdapter) rcvMyComment.getAdapter();
//        Log.d(TAG,"rcvMyArticleIsMy: " + myArticleAdapterIsMy);
        if (myArticleAdapterComment == null){
//            Log.d(TAG,"showMyArticleMyComment: " + showMyArticleMyComment);
            rcvMyComment.setAdapter(new UserMyArticleFragment.MyArticleAdapter(activity, showMyArticleMyComment));
        }else{
//            Log.d(TAG,"showMyArticleMyComment: " + showMyArticleMyComment);
            myArticleAdapterComment.setMyArticleListAdp(showMyArticleMyComment);
            myArticleAdapterComment.notifyDataSetChanged();
        }

    }


}