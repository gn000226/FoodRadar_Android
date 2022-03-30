package com.example.foodradar_android.user;

import androidx.annotation.NonNull;
import androidx.navigation.ActionOnlyNavDirections;
import androidx.navigation.NavDirections;
import com.example.foodradar_android.R;

public class UserAreaFragmentDirections {
  private UserAreaFragmentDirections() {
  }

  @NonNull
  public static NavDirections actionUserAreaFragmentToUserSysSetupFragment() {
    return new ActionOnlyNavDirections(R.id.action_userAreaFragment_to_userSysSetupFragment);
  }

  @NonNull
  public static NavDirections actionUserAreaFragmentToResMaintainFragment() {
    return new ActionOnlyNavDirections(R.id.action_userAreaFragment_to_resMaintainFragment);
  }

  @NonNull
  public static NavDirections actionUserAreaFragmentToUserMyResFragment() {
    return new ActionOnlyNavDirections(R.id.action_userAreaFragment_to_userMyResFragment);
  }

  @NonNull
  public static NavDirections actionUserAreaFragmentToUserDataSetupFragment() {
    return new ActionOnlyNavDirections(R.id.action_userAreaFragment_to_userDataSetupFragment);
  }

  @NonNull
  public static NavDirections actionUserAreaFragmentToUserMyCouponFragment() {
    return new ActionOnlyNavDirections(R.id.action_userAreaFragment_to_userMyCouponFragment);
  }
}
