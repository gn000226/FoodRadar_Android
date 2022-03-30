package com.example.foodradar_android.coupon;

import androidx.annotation.NonNull;
import androidx.navigation.ActionOnlyNavDirections;
import androidx.navigation.NavDirections;
import com.example.foodradar_android.R;

public class CouponUpdataFragmentDirections {
  private CouponUpdataFragmentDirections() {
  }

  @NonNull
  public static NavDirections actionCouponUpdataFragmentToResAddressFragment() {
    return new ActionOnlyNavDirections(R.id.action_couponUpdataFragment_to_resAddressFragment);
  }
}
