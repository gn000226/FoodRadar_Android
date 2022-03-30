package com.example.foodradar_android.user;

import androidx.annotation.NonNull;
import androidx.navigation.ActionOnlyNavDirections;
import androidx.navigation.NavDirections;
import com.example.foodradar_android.R;

public class UserMyCouponFragmentDirections {
  private UserMyCouponFragmentDirections() {
  }

  @NonNull
  public static NavDirections actionUserMyCouponFragmentToCouponDetailFragment() {
    return new ActionOnlyNavDirections(R.id.action_userMyCouponFragment_to_couponDetailFragment);
  }
}
