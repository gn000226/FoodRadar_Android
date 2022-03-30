package com.example.foodradar_android.main;

import androidx.annotation.NonNull;
import androidx.navigation.ActionOnlyNavDirections;
import androidx.navigation.NavDirections;
import com.example.foodradar_android.R;

public class CategoryTypeFragmentDirections {
  private CategoryTypeFragmentDirections() {
  }

  @NonNull
  public static NavDirections actionCategoryTypeFragmentToLoginFragment() {
    return new ActionOnlyNavDirections(R.id.action_CategoryTypeFragment_to_loginFragment);
  }

  @NonNull
  public static NavDirections actionCategoryTypeFragmentToUserDataSetupFragment() {
    return new ActionOnlyNavDirections(R.id.action_CategoryTypeFragment_to_userDataSetupFragment);
  }

  @NonNull
  public static NavDirections actionCategoryTypeFragmentToResDetailFragment() {
    return new ActionOnlyNavDirections(R.id.action_CategoryTypeFragment_to_resDetailFragment);
  }
}
