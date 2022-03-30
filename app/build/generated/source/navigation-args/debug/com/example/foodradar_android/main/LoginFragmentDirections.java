package com.example.foodradar_android.main;

import androidx.annotation.NonNull;
import androidx.navigation.ActionOnlyNavDirections;
import androidx.navigation.NavDirections;
import com.example.foodradar_android.R;

public class LoginFragmentDirections {
  private LoginFragmentDirections() {
  }

  @NonNull
  public static NavDirections actionLoginFragmentToMainFragment() {
    return new ActionOnlyNavDirections(R.id.action_loginFragment_to_mainFragment);
  }

  @NonNull
  public static NavDirections actionLoginFragmentToRegisterFragment() {
    return new ActionOnlyNavDirections(R.id.action_loginFragment_to_register_Fragment);
  }

  @NonNull
  public static NavDirections actionLoginFragmentToUserDataSetupFragment() {
    return new ActionOnlyNavDirections(R.id.action_loginFragment_to_userDataSetupFragment);
  }
}
