package com.example.foodradar_android.article;

import androidx.annotation.NonNull;
import androidx.navigation.ActionOnlyNavDirections;
import androidx.navigation.NavDirections;
import com.example.foodradar_android.R;

public class ArticleUpdateFragmentDirections {
  private ArticleUpdateFragmentDirections() {
  }

  @NonNull
  public static NavDirections actionArticleUpdateFragmentToArticleDetailFragment() {
    return new ActionOnlyNavDirections(R.id.action_articleUpdateFragment_to_articleDetailFragment);
  }
}
