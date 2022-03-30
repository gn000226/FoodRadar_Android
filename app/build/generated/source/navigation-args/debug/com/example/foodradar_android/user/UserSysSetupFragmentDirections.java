package com.example.foodradar_android.user;

import androidx.annotation.NonNull;
import androidx.navigation.ActionOnlyNavDirections;
import androidx.navigation.NavDirections;
import com.example.foodradar_android.R;

public class UserSysSetupFragmentDirections {
  private UserSysSetupFragmentDirections() {
  }

  @NonNull
  public static NavDirections actionUserSysSetupFragmentToFcmFragment() {
    return new ActionOnlyNavDirections(R.id.action_userSysSetupFragment_to_fcmFragment);
  }

  @NonNull
  public static NavDirections actionUserSysSetupFragmentToCouponEditFragment() {
    return new ActionOnlyNavDirections(R.id.action_userSysSetupFragment_to_couponEditFragment);
  }
}
