package com.example.foodradar_android.coupon;

import androidx.annotation.NonNull;
import androidx.navigation.ActionOnlyNavDirections;
import androidx.navigation.NavDirections;
import com.example.foodradar_android.R;

public class CouponMaintainFragmentDirections {
  private CouponMaintainFragmentDirections() {
  }

  @NonNull
  public static NavDirections actionCouponMaintainFragmentToResAddressFragment() {
    return new ActionOnlyNavDirections(R.id.action_couponMaintainFragment_to_resAddressFragment);
  }
}
