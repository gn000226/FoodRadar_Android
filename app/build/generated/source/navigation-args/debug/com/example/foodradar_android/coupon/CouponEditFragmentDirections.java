package com.example.foodradar_android.coupon;

import androidx.annotation.NonNull;
import androidx.navigation.ActionOnlyNavDirections;
import androidx.navigation.NavDirections;
import com.example.foodradar_android.R;

public class CouponEditFragmentDirections {
  private CouponEditFragmentDirections() {
  }

  @NonNull
  public static NavDirections actionCouponEditFragmentToCouponMaintainFragment() {
    return new ActionOnlyNavDirections(R.id.action_couponEditFragment_to_couponMaintainFragment);
  }

  @NonNull
  public static NavDirections actionCouponEditFragmentToCouponUpdataFragment() {
    return new ActionOnlyNavDirections(R.id.action_couponEditFragment_to_couponUpdataFragment);
  }
}
