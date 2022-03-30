package com.example.foodradar_android.res;

import androidx.annotation.NonNull;
import androidx.navigation.ActionOnlyNavDirections;
import androidx.navigation.NavDirections;
import com.example.foodradar_android.R;

public class ResListFragmentDirections {
  private ResListFragmentDirections() {
  }

  @NonNull
  public static NavDirections actionResListFragmentToResDetailFragment() {
    return new ActionOnlyNavDirections(R.id.action_resListFragment_to_resDetailFragment);
  }

  @NonNull
  public static NavDirections actionResListFragmentToLoginFragment() {
    return new ActionOnlyNavDirections(R.id.action_resListFragment_to_loginFragment);
  }

  @NonNull
  public static NavDirections actionResListFragmentToUserMyResFragment() {
    return new ActionOnlyNavDirections(R.id.action_resListFragment_to_userMyResFragment);
  }
}
