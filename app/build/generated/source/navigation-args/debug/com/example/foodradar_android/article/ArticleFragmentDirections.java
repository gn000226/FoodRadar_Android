package com.example.foodradar_android.article;

import androidx.annotation.NonNull;
import androidx.navigation.ActionOnlyNavDirections;
import androidx.navigation.NavDirections;
import com.example.foodradar_android.R;

public class ArticleFragmentDirections {
  private ArticleFragmentDirections() {
  }

  @NonNull
  public static NavDirections actionArticleFragmentToNewArticleFragment2() {
    return new ActionOnlyNavDirections(R.id.action_articleFragment_to_newArticleFragment2);
  }

  @NonNull
  public static NavDirections actionArticleFragmentToArticleInsertFragment() {
    return new ActionOnlyNavDirections(R.id.action_articleFragment_to_articleInsertFragment);
  }

  @NonNull
  public static NavDirections actionArticleFragmentToArticleDetailFragment() {
    return new ActionOnlyNavDirections(R.id.action_articleFragment_to_articleDetailFragment);
  }

  @NonNull
  public static NavDirections actionArticleFragmentToUserDataSetupFragment() {
    return new ActionOnlyNavDirections(R.id.action_articleFragment_to_userDataSetupFragment);
  }
}
