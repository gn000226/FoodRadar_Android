package com.example.foodradar_android.article;

import androidx.annotation.NonNull;
import androidx.navigation.ActionOnlyNavDirections;
import androidx.navigation.NavDirections;
import com.example.foodradar_android.R;

public class RankArticleFragmentDirections {
  private RankArticleFragmentDirections() {
  }

  @NonNull
  public static NavDirections actionRankArticleFragmentToArticleDetailFragment() {
    return new ActionOnlyNavDirections(R.id.action_rankArticleFragment_to_articleDetailFragment);
  }

  @NonNull
  public static NavDirections actionRankArticleFragmentToArticleInsertFragment() {
    return new ActionOnlyNavDirections(R.id.action_rankArticleFragment_to_articleInsertFragment);
  }
}
