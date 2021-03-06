package com.example.foodradar_android.user;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.example.foodradar_android.Common;
import com.example.foodradar_android.R;
import com.example.foodradar_android.article.Article;
import com.example.foodradar_android.task.CommonTask;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ArticleManagementFragment extends Fragment {

    private Activity activity;
    private NavController navController;
    private List<MyArticle> myArticleList = new ArrayList<>();
    private List<UserImageTask> imageTasks= new ArrayList<>();
    private RecyclerView rcvArticleManagement;

    //    private Integer userId;
    private String URL_SERVER = "http://10.0.2.2:8080/FoodRadar_Web/";
    private String MYARTICLE_SERVLET = URL_SERVER + "MyArticleServlet";
    private Integer imageSize = 200;
    final private String TAG = "ArticleManagementFrag";
    private EditText etUsManageUserPhone;
    private TextView etManageArticleDate;
//    private ColorStateList edTextdefaultColor;
    private DatePickerDialog datePickerDialog;
    private DatePickerDialog.OnDateSetListener dateSetListener;
    private SwipeRefreshLayout swipeRefreshLayoutAM;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();

        // ??????????????????????????????
        Common.setBackArrow(true, activity);
        setHasOptionsMenu(true);

        navController =
                Navigation.findNavController(activity, R.id.mainFragment);
    }

    // ??????????????????OptionMenu??????
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
//        inflater.inflate(R.menu.appbar_menu,menu);  // ???res????????????????????????R.menu.my_menu???
//        super.onCreateOptionsMenu(menu, inflater);
    }
    // ??????????????????OptionMenu??????
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
        activity.setTitle(R.string.title_of_res_article_management);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_article_management, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etUsManageUserPhone = view.findViewById(R.id.etUsManageUserPhone);
        view.findViewById(R.id.tvUsManageUserPhone).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userPhone = etUsManageUserPhone.getText().toString();
                etUsManageUserPhone.setText(new Common().getUserPhoneByArticleManage(userPhone));
            }
        });
        etManageArticleDate = view.findViewById(R.id.tvManageArticleDate);
        etManageArticleDate.setText(new SimpleDateFormat("yyyy-MM-dd").format(System.currentTimeMillis()));
        dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                // ???????????????0??????1????????????month + 1???????????????
                month++;
                String yyyyMMdd = new StringBuilder()
                        .append(year).append("-")
                        .append((month < 10 ? "0" + month : month)).append("-")
                        .append((dayOfMonth < 10 ? "0" + dayOfMonth : dayOfMonth))
                        .toString();etManageArticleDate.setText(yyyyMMdd);
//                etManageArticleDate.setTextColor(edTextdefaultColor);
            }
        };

        etManageArticleDate.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN){
                    // ???????????????show()????????????
                    datePickerDialog.show();
                }
                return false;
            }
        });

        etManageArticleDate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (etManageArticleDate.getText().equals("")) {
//                    etManageArticleDate.setTextColor(getResources().getColor(R.color.colorTextHint));
//                    etManageArticleDate.setText(getResources().getString(R.string.textArticleDate));
                    etManageArticleDate.setText(new SimpleDateFormat("yyyy-MM-dd").format(System.currentTimeMillis()));
                } else {
//                    etManageArticleDate.setTextColor(edTextdefaultColor);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
        setDatePicker();


        swipeRefreshLayoutAM = view.findViewById(R.id.swipeRefreshLayoutArticleManagement);
        swipeRefreshLayoutAM.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                String userPhone = etUsManageUserPhone.getText().toString();
                String articleDate = etManageArticleDate.getText().toString();
                swipeRefreshLayoutAM.setRefreshing(true);
                showArticleByUserPhone(getArticleByUserPhone(userPhone, articleDate));
                swipeRefreshLayoutAM.setRefreshing(false);
            }
        });


        // ??????????????????
        view.findViewById(R.id.btUsManageSearchArticle)
                .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                boolean isOpen=imm.isActive();//isOpen?????????true???????????????????????????
                if (isOpen == true) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(),0);
                }

                String userPhone = etUsManageUserPhone.getText().toString();
                String articleDate = etManageArticleDate.getText().toString();
                swipeRefreshLayoutAM.setRefreshing(true);
                showArticleByUserPhone(getArticleByUserPhone(userPhone, articleDate));
                swipeRefreshLayoutAM.setRefreshing(false);
            }
        });

        view.findViewById(R.id.btUsManageCancel)
                .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etUsManageUserPhone.setText("");
                etManageArticleDate.setText(new SimpleDateFormat("yyyy-MM-dd").format(System.currentTimeMillis()));
                showArticleByUserPhone(null);
            }
        });

        rcvArticleManagement = view.findViewById(R.id.id_rcvArticleManagement);
        rcvArticleManagement.setLayoutManager(new LinearLayoutManager(activity));
//        rcvArticleManagement.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL));

    }

//    private int getUserId() { return Common.USER_ID; }

    private void setDatePicker() {
        Calendar calendar = Calendar.getInstance();
//        calendar.setTime(new Date());
//        int year = calendar.get(Calendar.YEAR) - 12; // ?????????????????????12???
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        if (!etManageArticleDate.getText().equals("")
                && !etManageArticleDate.getText().equals(getResources().getString(R.string.textArticleDate))) {

            //????????????????????????
            String dateString = etManageArticleDate.getText().toString();
            //??????????????????
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            //????????????
            Date dateBirth = new Date();
            try {
                dateBirth = sdf.parse(dateString);
            } catch (ParseException e) {
                Log.d(TAG,"DateParse: Exeception");
                e.printStackTrace();
            }
            calendar.setTime(dateBirth);
            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH);
            day = calendar.get(Calendar.DAY_OF_MONTH);
        }

        datePickerDialog = new DatePickerDialog(activity, DatePickerDialog.THEME_DEVICE_DEFAULT_LIGHT, dateSetListener, year, month, day);
        DatePicker datePicker = datePickerDialog.getDatePicker();


        Calendar calendarMin = Calendar.getInstance();
        calendarMin.add(Calendar.YEAR, -100); // ?????????????????????????????????130???
        datePicker.setMinDate(calendarMin.getTimeInMillis());

        Calendar calendarMax = Calendar.getInstance();
//        calendarMax.add(Calendar.MONTH, -1); // ??????????????????????????????????????????
        datePicker.setMaxDate(calendarMax.getTimeInMillis());
    }


    // ?????????????????????(MyArticleAdapter) ?????? RecyclerView.Adapter
    // ????????????????????? MyViewHolder ?????? RecyclerView.ViewHolder ????????????????????? ItemView ??????????????????
    private class MyArticleAdapter extends RecyclerView.Adapter<ArticleManagementFragment.MyArticleAdapter.MyViewHolder> {
        private LayoutInflater layoutInflater; // ????????????????????? Context / Activity
        private List<MyArticle> myArticleListInAdp; // ????????????????????? ??????List
        private Integer imageSize;

        // ?????????????????????(MyArticleAdapter)????????????????????????????????? Context ?????????????????????List ???
        MyArticleAdapter (Context context, List<MyArticle> myArticleListAdp) {
            layoutInflater = LayoutInflater.from(context);
            this.myArticleListInAdp = myArticleListAdp;
            this.imageSize = getResources().getDisplayMetrics().widthPixels / 4; // ??????????????????????????????????????????
        }

        // ??????????????????(MyArticleAdapter)?????????(setMyArticleListAdp)???????????????????????? ??????List
        public void setMyArticleListAdp (List<MyArticle> myArticleListAdp) {
            this.myArticleListInAdp = myArticleListAdp;
        }

        // ??????????????????(MyViewHolder) ?????? RecyclerView.ViewHolder???
        // ??????????????????Item View???????????????????????????????????? onBindViewHolder ????????????????????????
        class MyViewHolder extends RecyclerView.ViewHolder {
            ImageView imAtMaResImg;
            TextView tvAtMaArticleTitle;
            TextView tvAtMaArticleTime;
            TextView tvAtMaUserName;
            TextView tvAtMaArticleText;
            Switch swArticleManageStatus;
            Button btAtMaGoArticle;
            public MyViewHolder(@NonNull View itemView) {
                super(itemView);
                imAtMaResImg = itemView.findViewById(R.id.imAtMaResImg);
                tvAtMaArticleTitle = itemView.findViewById(R.id.tvAtMaArticleTitle);
                tvAtMaArticleTime = itemView.findViewById(R.id.tvAtMaArticleTime);
                tvAtMaUserName = itemView.findViewById(R.id.tvAtMaUserName);
                tvAtMaArticleText = itemView.findViewById(R.id.tvAtMaArticleText);
                swArticleManageStatus = itemView.findViewById(R.id.swArticleManageStatus);
                btAtMaGoArticle = itemView.findViewById(R.id.btAtMaGoArticle);
            }
        }

        @NonNull
        @Override
        public ArticleManagementFragment.MyArticleAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            // ????????????????????? res\layout ?????? Item view XML???
            View myItemView = layoutInflater
                    .inflate(R.layout.item_view_article_management,parent,false);
            // ??? layout??????view?????????RecyclerView???ViewHolder(MyViewHolder)
            return new ArticleManagementFragment.MyArticleAdapter.MyViewHolder(myItemView);
        }

        @Override
        public void onBindViewHolder(@NonNull ArticleManagementFragment.MyArticleAdapter.MyViewHolder holder, int position) {
            MyArticle myAtBindVH = myArticleListInAdp.get(position);
            Resources res = activity.getResources();
            holder.tvAtMaArticleTitle.setText(myAtBindVH.getArticleTitle());

            String textAccountStatus = "??????";
            String textOfEnable = "??????";
            String textOfDisable = "??????";
            String strAccountStatus = textAccountStatus + ": "
                    + (myAtBindVH.getArticleStatus() ? textOfEnable : textOfDisable);
            holder.swArticleManageStatus.setText(strAccountStatus);

            if ( myAtBindVH.getCommentId() == 0) {
                String strAtArticleTime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(myAtBindVH.getArticleTime());
                holder.tvAtMaArticleTime.setText(res.getString(R.string.textArticleDate) + ": " + strAtArticleTime);
                holder.tvAtMaUserName.setText(res.getString(R.string.textArticleWirter) + ": " + myAtBindVH.getUserName());
                holder.tvAtMaArticleText.setText(res.getString(R.string.textArticleText) + ": " + myAtBindVH.getArticleText());
                holder.swArticleManageStatus.setChecked(myAtBindVH.getArticleStatus());
                if (myAtBindVH.getArticleStatus()) {
                    holder.swArticleManageStatus.setTextColor(Color.BLUE);
                } else {
                    holder.swArticleManageStatus.setTextColor(Color.RED);
                }
            } else {
                String strCommentTime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(myAtBindVH.getCommentTime());
                holder.tvAtMaArticleTime.setText(res.getString(R.string.textCommentDate) + ": " + strCommentTime);
                holder.tvAtMaUserName.setText(res.getString(R.string.textArticleWirter) + ": " + myAtBindVH.getUserName());
                holder.tvAtMaArticleText.setText(res.getString(R.string.textCommentText) + ": " + myAtBindVH.getCommentText());
                holder.swArticleManageStatus.setChecked(myAtBindVH.getCommentStatus());
                if (myAtBindVH.getCommentStatus()) {
                    holder.swArticleManageStatus.setTextColor(Color.BLUE);
                } else {
                    holder.swArticleManageStatus.setTextColor(Color.RED);
                }
            }

            final Integer articleId = myAtBindVH.getArticleId(); // ??????ID
            Log.d(TAG,"articleId: " + articleId);
            Log.d(TAG,"MYARTICLE_SERVLET: " + MYARTICLE_SERVLET);
            UserImageTask userImageTask = new UserImageTask(MYARTICLE_SERVLET, articleId, imageSize, holder.imAtMaResImg);
            userImageTask.execute(); // .execute() => UserImage.doInBackground
            imageTasks.add(userImageTask);

            // ??????????????????
            holder.swArticleManageStatus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean isChecked = !myAtBindVH.getArticleStatus();
                    ArticleManagementFragment amf = new ArticleManagementFragment();
                    amf.setArticleWithConfirm(activity, holder, myAtBindVH, isChecked);
                }
            });

            // ????????????????????????
            holder.btAtMaGoArticle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (myAtBindVH.getArticleStatus() == false) {
                        Common.showToast(activity, "??????????????????????????????");
                        return;
                    }

                    // ?????????????????????????????????????????????detail??????
                    Article.ARTICLE_ID = articleId;
                    Article.USER_ID = myAtBindVH.getUserId();
                    MyArticle.goToMyArticleDetail = true;
                    navController.navigate(R.id.articleFragment);
                }
            });

            // ??????????????????
            holder.imAtMaResImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                    View dialogView = getLayoutInflater().inflate(R.layout.item_view_article_management_img, null);
//                    alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                    alertDialog.setView(dialogView);
                    ImageView imAtMaResImg = dialogView.findViewById(R.id.imAtMaResImg);
                    final Integer articleId = myAtBindVH.getArticleId(); // ??????ID
                    UserImageTask userImageTask = new UserImageTask(MYARTICLE_SERVLET, articleId, 500, imAtMaResImg);
                    userImageTask.execute(); // .execute() => UserImage.doInBackground
                    imageTasks.add(userImageTask);
//                    imAtMaResImg.setMaxWidth(imageSize);
//                    imAtMaResImg.layout(imageSize,imageSize,imageSize,imageSize);
//                    this.imageSize = getResources().getDisplayMetrics().widthPixels / 4; // ??????????????????????????????????????????
                    alertDialog.show();
                }
            });

            // ??????????????????
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                    View dialogView = getLayoutInflater().inflate(R.layout.item_view_article_management, null);
//                    alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                    alertDialog.setView(dialogView);

                    dialogView.findViewById(R.id.swArticleManageStatus).setVisibility(View.GONE);
                    dialogView.findViewById(R.id.btAtMaGoArticle).setVisibility(View.GONE);
                    dialogView.findViewById(R.id.imAtMaResImg).setVisibility(View.GONE);

                    TextView tvAtMaArticleTitle = dialogView.findViewById(R.id.tvAtMaArticleTitle);
                    TextView tvAtMaArticleTime = dialogView.findViewById(R.id.tvAtMaArticleTime);
                    TextView tvAtMaUserName = dialogView.findViewById(R.id.tvAtMaUserName);
                    TextView tvAtMaArticleText = dialogView.findViewById(R.id.tvAtMaArticleText);

                    tvAtMaArticleTitle.setText(myAtBindVH.getArticleTitle());
                    String strAtArticleTime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(myAtBindVH.getArticleTime());
                    tvAtMaArticleTime.setText(res.getString(R.string.textArticleDate) + ": " + strAtArticleTime);
                    tvAtMaUserName.setText(res.getString(R.string.textArticleWirter) + ": " + myAtBindVH.getUserName());
                    tvAtMaArticleText.setText(res.getString(R.string.textArticleText) + ": " + myAtBindVH.getArticleText());

                    alertDialog.show();

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


    // ??????????????????
    private List<MyArticle> getArticleByUserPhone(String userPhone, String articleDate){
        List<MyArticle> getArticleByUserPhoneList = null;

        if (Common.networkConnected(activity)) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "getArticleByUserPhone");
            jsonObject.addProperty("userPhone", userPhone);
            jsonObject.addProperty("articleDate", articleDate);
            String jsonOut = jsonObject.toString();
            CommonTask getAllTask;
            getAllTask = new CommonTask(MYARTICLE_SERVLET, jsonOut);
            try {
                String jsonIn = getAllTask.execute().get();
                Type listType = new TypeToken<List<MyArticle>>() {
                }.getType();
                getArticleByUserPhoneList = new Gson().fromJson(jsonIn, listType);
                Log.d(TAG,"getArticleByUserPhoneList: " + getArticleByUserPhoneList);
            } catch (Exception e) {
//                Log.e(TAG, e.toString());
                e.printStackTrace();
            }

        } else {
            Common.showToast(activity,"NetworkConnected: fail");
        }

        return getArticleByUserPhoneList;
    }


    private void showArticleByUserPhone(List<MyArticle> showArticleByPhoneList) {

        ArticleManagementFragment.MyArticleAdapter myArticleAdapter
                = (ArticleManagementFragment.MyArticleAdapter) rcvArticleManagement.getAdapter();
//        Log.d(TAG,"myArticleAdapter: " + myArticleAdapter);
        if (myArticleAdapter == null){
//            Log.d(TAG,"showArticleByPhoneList: " + showArticleByPhoneList);
            rcvArticleManagement.setAdapter(new ArticleManagementFragment.MyArticleAdapter(activity, showArticleByPhoneList));
        } else {
//            Log.d(TAG,"showArticleByPhoneList: " + showArticleByPhoneList);
            myArticleAdapter.setMyArticleListAdp(showArticleByPhoneList);
            myArticleAdapter.notifyDataSetChanged();
        }

    }


    // ?????????????????????????????????????????????????????????
    public void setArticleWithConfirm(Activity activity, MyArticleAdapter.MyViewHolder holder , MyArticle mA, Boolean enableStatus) {

        /* ??????positive???negative????????????????????????????????????????????? */
        androidx.appcompat.app.AlertDialog.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:

                        Resources res = activity.getResources();
                        String textAccountStatus = "??????"; // res.getString(R.string.textAccountStatus);
                        String textOfEnable = "??????"; // res.getString(R.string.textOfEnable);
                        String textOfDisable = "??????"; // res.getString(R.string.textOfDisable);

                        if (setArticleStatus(mA.getArticleId(), enableStatus)){
                            String strAccountStatus = textAccountStatus + ": "
                                    + (enableStatus ? textOfEnable : textOfDisable);
                            holder.swArticleManageStatus.setText(strAccountStatus);

                            if (enableStatus) {
                                holder.swArticleManageStatus.setTextColor(Color.BLUE);
                            } else {
                                holder.swArticleManageStatus.setTextColor(Color.RED);
                            }

                            holder.swArticleManageStatus.setChecked(enableStatus);
                            mA.setArticleStatus(enableStatus);
                            Common.showToast(activity, activity.getResources().getString(R.string.textSetSuccess));
                        } else {
                            holder.swArticleManageStatus.setChecked(!enableStatus);
                            mA.setArticleStatus(!enableStatus);
                            Common.showToast(activity, activity.getResources().getString(R.string.textSetFail));
                        }
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
//                    break;
                    default:
                        Common.showToast(activity, R.string.textCancel);
                        /* ?????????????????? */
                        holder.swArticleManageStatus.setChecked(!enableStatus);
                        dialog.cancel();

                        break;
                }
            }
        };

//        Resources res = activity.getResources();

        String txtEnable = "??????"; // res.getString(R.string.textOfEnable);
        String txtDisable = "??????"; // res.getString(R.string.textOfDisable);
        String txtEnableStatus = (enableStatus? txtEnable : txtDisable);

//        String textPhone = "????????????"; // getResources().getString(R.string.textUserPhone)
//        String textRegisterDate = "????????????"; // getResources().getString(R.string.textRegisterDate)

//        String alertMessage = "\n" + textPhone + ": " + mA.getUserPhone() + " ( ID: " + String.valueOf(uA.getUserId()) + " )";
//        alertMessage += "\n" + textRegisterDate + ": " + mA.getCreateDate().toString();
        String alertMessage = "\n\n" + "??????????????????????????????\n????????????" + txtEnableStatus + "?????????";

        String positiveText = "??????" + txtEnableStatus + "????????????";
        String negativeText = "???????????????";

        new androidx.appcompat.app.AlertDialog.Builder(activity)
                /* ???????????? */
                .setTitle(R.string.title_of_res_article_management)
                /* ???????????? */
                .setIcon(R.drawable.ic_baseline_build_24)
                /* ?????????????????? */
                .setMessage(alertMessage)
                /* ??????positive???negative????????????????????????????????????????????? */
                .setPositiveButton(positiveText, listener)
                .setNegativeButton(negativeText, listener)
                // ?????????????????????????????????????????????????????????true??????false????????????????????????????????????
                .setCancelable(true)
                .create()
                .show();

    }

    public Boolean setArticleStatus(int articleId ,Boolean enableStatus){

        Log.d(TAG,"setArticleStatus.articleId: " + articleId);
        Log.d(TAG,"setArticleStatus.enableStatus: " + enableStatus);

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("action", "setEnableStatus");
        jsonObject.addProperty("id", articleId);
        jsonObject.addProperty("enableStatus", enableStatus);
        int count = 0;
        try {
            String result = new CommonTask(MYARTICLE_SERVLET, jsonObject.toString()).execute().get(); // Insert???????????????????????????????????????
            count = Integer.parseInt(result);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }

        if (count == 0) {
            return false;
        } else {
            return true;
        }

    }

}
