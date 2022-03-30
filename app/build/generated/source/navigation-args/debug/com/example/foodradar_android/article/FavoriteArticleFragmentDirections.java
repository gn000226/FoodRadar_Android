package com.example.foodradar_android.article;

import androidx.annotation.NonNull;
import androidx.navigation.ActionOnlyNavDirections;
import androidx.navigation.NavDirections;
import com.example.foodradar_android.R;

public class FavoriteArticleFragmentDirections {
  private FavoriteArticleFragmentDirections() {
  }

  @NonNull
  public static NavDirections actionFavoriteArticleFragmentToArticleDetailFragment() {
    return new ActionOnlyNavDirections(R.id.action_favoriteArticleFragment_to_articleDetailFragment);
  }

  @NonNull
  public static NavDirections actionFavoriteArticleFragmentToArticleInsertFragment() {
    return new ActionOnlyNavDirections(R.id.action_favoriteArticleFragment_to_articleInsertFragment);
  }
}
