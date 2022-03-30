package com.example.foodradar_android.coupon;

import androidx.annotation.NonNull;
import androidx.navigation.ActionOnlyNavDirections;
import androidx.navigation.NavDirections;
import com.example.foodradar_android.R;

public class CouponFragmentDirections {
  private CouponFragmentDirections() {
  }

  @NonNull
  public static NavDirections actionCouponFragmentToCouponDetailFragment() {
    return new ActionOnlyNavDirections(R.id.action_couponFragment_to_couponDetailFragment);
  }

  @NonNull
  public static NavDirections actionCouponFragmentToLoginFragment() {
    return new ActionOnlyNavDirections(R.id.action_couponFragment_to_loginFragment);
  }

  @NonNull
  public static NavDirections actionCouponFragmentToCategoryDetailFragment() {
    return new ActionOnlyNavDirections(R.id.action_couponFragment_to_category_Detail_Fragment);
  }
}
