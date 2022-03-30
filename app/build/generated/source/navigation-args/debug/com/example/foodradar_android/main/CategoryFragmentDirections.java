package com.example.foodradar_android.main;

import androidx.annotation.NonNull;
import androidx.navigation.ActionOnlyNavDirections;
import androidx.navigation.NavDirections;
import com.example.foodradar_android.R;

public class CategoryFragmentDirections {
  private CategoryFragmentDirections() {
  }

  @NonNull
  public static NavDirections actionMainFragmentToResMapFragment() {
    return new ActionOnlyNavDirections(R.id.action_mainFragment_to_resMapFragment);
  }

  @NonNull
  public static NavDirections actionMainFragmentToCouponFragment() {
    return new ActionOnlyNavDirections(R.id.action_mainFragment_to_couponFragment);
  }

  @NonNull
  public static NavDirections actionMainFragmentToUserAreaFragment() {
    return new ActionOnlyNavDirections(R.id.action_mainFragment_to_userAreaFragment);
  }

  @NonNull
  public static NavDirections actionMainFragmentToChinaRestaurantFragment() {
    return new ActionOnlyNavDirections(R.id.action_mainFragment_to_chinaRestaurantFragment);
  }

  @NonNull
  public static NavDirections actionMainFragmentToArticleFragment() {
    return new ActionOnlyNavDirections(R.id.action_mainFragment_to_articleFragment);
  }

  @NonNull
  public static NavDirections actionMainFragmentToLoginFragment() {
    return new ActionOnlyNavDirections(R.id.action_mainFragment_to_loginFragment);
  }
}
