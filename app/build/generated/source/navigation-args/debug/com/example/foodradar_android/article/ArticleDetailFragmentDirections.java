package com.example.foodradar_android.article;

import androidx.annotation.NonNull;
import androidx.navigation.ActionOnlyNavDirections;
import androidx.navigation.NavDirections;
import com.example.foodradar_android.R;

public class ArticleDetailFragmentDirections {
  private ArticleDetailFragmentDirections() {
  }

  @NonNull
  public static NavDirections actionArticleDetailFragmentToRankArticleFragment() {
    return new ActionOnlyNavDirections(R.id.action_articleDetailFragment_to_rankArticleFragment);
  }

  @NonNull
  public static NavDirections actionArticleDetailFragmentToFavoriteArticleFragment() {
    return new ActionOnlyNavDirections(R.id.action_articleDetailFragment_to_favoriteArticleFragment);
  }

  @NonNull
  public static NavDirections actionArticleDetailFragmentToNewArticleFragment2() {
    return new ActionOnlyNavDirections(R.id.action_articleDetailFragment_to_newArticleFragment2);
  }

  @NonNull
  public static NavDirections actionArticleDetailFragmentToArticleUpdateFragment() {
    return new ActionOnlyNavDirections(R.id.action_articleDetailFragment_to_articleUpdateFragment);
  }
}
