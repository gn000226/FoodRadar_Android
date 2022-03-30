package com.example.foodradar_android.res;

import androidx.annotation.NonNull;
import androidx.navigation.ActionOnlyNavDirections;
import androidx.navigation.NavDirections;
import com.example.foodradar_android.R;

public class ResDetailFragmentDirections {
  private ResDetailFragmentDirections() {
  }

  @NonNull
  public static NavDirections actionResDetailFragmentToLoginFragment() {
    return new ActionOnlyNavDirections(R.id.action_resDetailFragment_to_loginFragment);
  }

  @NonNull
  public static NavDirections actionResDetailFragmentToResImgFragment() {
    return new ActionOnlyNavDirections(R.id.action_resDetailFragment_to_resImgFragment);
  }

  @NonNull
  public static NavDirections actionResDetailFragmentToArticleFragment() {
    return new ActionOnlyNavDirections(R.id.action_resDetailFragment_to_articleFragment);
  }

  @NonNull
  public static NavDirections actionResDetailFragmentToArticleInsertFragment() {
    return new ActionOnlyNavDirections(R.id.action_resDetailFragment_to_articleInsertFragment);
  }
}
