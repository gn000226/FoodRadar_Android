package com.example.foodradar_android.article;

import androidx.annotation.NonNull;
import androidx.navigation.ActionOnlyNavDirections;
import androidx.navigation.NavDirections;
import com.example.foodradar_android.R;

public class NewArticleFragmentDirections {
  private NewArticleFragmentDirections() {
  }

  @NonNull
  public static NavDirections actionNewArticleFragmentToArticleDetailFragment() {
    return new ActionOnlyNavDirections(R.id.action_newArticleFragment_to_articleDetailFragment);
  }

  @NonNull
  public static NavDirections actionNewArticleFragmentToArticleInsertFragment() {
    return new ActionOnlyNavDirections(R.id.action_newArticleFragment_to_articleInsertFragment);
  }
}
