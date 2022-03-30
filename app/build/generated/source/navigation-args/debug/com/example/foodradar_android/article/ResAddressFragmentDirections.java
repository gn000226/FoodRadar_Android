package com.example.foodradar_android.article;

import androidx.annotation.NonNull;
import androidx.navigation.ActionOnlyNavDirections;
import androidx.navigation.NavDirections;
import com.example.foodradar_android.R;

public class ResAddressFragmentDirections {
  private ResAddressFragmentDirections() {
  }

  @NonNull
  public static NavDirections actionResAddressFragmentToArticleInsertFragment() {
    return new ActionOnlyNavDirections(R.id.action_resAddressFragment_to_articleInsertFragment);
  }
}
