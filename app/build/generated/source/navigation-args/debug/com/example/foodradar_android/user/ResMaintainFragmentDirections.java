package com.example.foodradar_android.user;

import androidx.annotation.NonNull;
import androidx.navigation.ActionOnlyNavDirections;
import androidx.navigation.NavDirections;
import com.example.foodradar_android.R;

public class ResMaintainFragmentDirections {
  private ResMaintainFragmentDirections() {
  }

  @NonNull
  public static NavDirections actionResMaintainFragmentToResInsertFragment() {
    return new ActionOnlyNavDirections(R.id.action_resMaintainFragment_to_resInsertFragment);
  }

  @NonNull
  public static NavDirections actionResMaintainFragmentToResUpdateFragment() {
    return new ActionOnlyNavDirections(R.id.action_resMaintainFragment_to_resUpdateFragment);
  }
}
