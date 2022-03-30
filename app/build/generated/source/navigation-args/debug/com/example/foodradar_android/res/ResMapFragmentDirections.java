package com.example.foodradar_android.res;

import androidx.annotation.NonNull;
import androidx.navigation.ActionOnlyNavDirections;
import androidx.navigation.NavDirections;
import com.example.foodradar_android.R;

public class ResMapFragmentDirections {
  private ResMapFragmentDirections() {
  }

  @NonNull
  public static NavDirections actionResMapFragmentToResDetailFragment() {
    return new ActionOnlyNavDirections(R.id.action_resMapFragment_to_resDetailFragment);
  }

  @NonNull
  public static NavDirections actionResMapFragmentToResListFragment() {
    return new ActionOnlyNavDirections(R.id.action_resMapFragment_to_resListFragment);
  }

  @NonNull
  public static NavDirections actionResMapFragmentToLoginFragment() {
    return new ActionOnlyNavDirections(R.id.action_resMapFragment_to_loginFragment);
  }
}
