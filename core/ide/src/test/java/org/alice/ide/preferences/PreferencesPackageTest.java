package org.alice.ide.preferences;

import org.alice.ide.preferences.recursion.IsAccessToRecursionPreferenceAllowedState;
import org.alice.ide.preferences.recursion.IsRecursionAllowedState;
import org.junit.Test;

import static org.junit.Assert.*;

public class PreferencesPackageTest {
  @Test
  public void toolbarShowingRemainsDisabledByDefault() {
    assertFalse(IsToolBarShowing.getValue());
  }

  @Test
  public void recursionPreferencesExposeStableSingletonStates() {
    assertSame(IsRecursionAllowedState.getInstance(), IsRecursionAllowedState.getInstance());
    assertSame(IsAccessToRecursionPreferenceAllowedState.getInstance(), IsAccessToRecursionPreferenceAllowedState.getInstance());
    assertFalse(IsRecursionAllowedState.getInstance().getValue());
    assertFalse(IsAccessToRecursionPreferenceAllowedState.getInstance().getValue());
  }
}
