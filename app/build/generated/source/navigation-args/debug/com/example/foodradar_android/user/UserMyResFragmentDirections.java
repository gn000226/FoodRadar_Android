package com.example.foodradar_android.user;

import androidx.annotation.NonNull;
import androidx.navigation.ActionOnlyNavDirections;
import androidx.navigation.NavDirections;
import com.example.foodradar_android.R;

public class UserMyResFragmentDirections {
  private UserMyResFragmentDirections() {
  }

  @NonNull
  public static NavDirections actionUserMyResFragmentToResDetailFragment() {
    return new ActionOnlyNavDirections(R.id.action_userMyResFragment_to_resDetailFragment);
  }
}
