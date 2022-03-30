package com.example.foodradar_android.user;

import androidx.annotation.NonNull;
import androidx.navigation.ActionOnlyNavDirections;
import androidx.navigation.NavDirections;
import com.example.foodradar_android.R;

public class ResInsertFragmentDirections {
  private ResInsertFragmentDirections() {
  }

  @NonNull
  public static NavDirections actionResInsertFragmentToArticleFragment() {
    return new ActionOnlyNavDirections(R.id.action_resInsertFragment_to_articleFragment);
  }
}
