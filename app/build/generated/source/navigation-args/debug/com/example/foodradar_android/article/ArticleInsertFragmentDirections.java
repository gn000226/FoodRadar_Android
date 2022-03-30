package com.example.foodradar_android.article;

import androidx.annotation.NonNull;
import androidx.navigation.ActionOnlyNavDirections;
import androidx.navigation.NavDirections;
import com.example.foodradar_android.R;

public class ArticleInsertFragmentDirections {
  private ArticleInsertFragmentDirections() {
  }

  @NonNull
  public static NavDirections actionArticleInsertFragmentToArticleDetailFragment() {
    return new ActionOnlyNavDirections(R.id.action_articleInsertFragment_to_articleDetailFragment);
  }

  @NonNull
  public static NavDirections actionArticleInsertFragmentToResAddressFragment2() {
    return new ActionOnlyNavDirections(R.id.action_articleInsertFragment_to_resAddressFragment2);
  }
}
