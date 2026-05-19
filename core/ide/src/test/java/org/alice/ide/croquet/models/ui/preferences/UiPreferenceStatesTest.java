package org.alice.ide.croquet.models.ui.preferences;

import org.junit.Test;
import java.lang.reflect.Modifier;
import static org.junit.Assert.*;

public class UiPreferenceStatesTest {
  @Test
  public void isEmphasizingClassesState_isPublic() {
    assertTrue(Modifier.isPublic(IsEmphasizingClassesState.class.getModifiers()));
  }
  @Test
  public void isExposingReassignableStatusState_isPublic() {
    assertTrue(Modifier.isPublic(IsExposingReassignableStatusState.class.getModifiers()));
  }
  @Test
  public void isFullTypeHierarchyDesiredState_isPublic() {
    assertTrue(Modifier.isPublic(IsFullTypeHierarchyDesiredState.class.getModifiers()));
  }
  @Test
  public void isIncludingConstructors_isPublic() {
    assertTrue(Modifier.isPublic(IsIncludingConstructors.class.getModifiers()));
  }
  @Test
  public void isIncludingImportAndExportType_isPublic() {
    assertTrue(Modifier.isPublic(IsIncludingImportAndExportType.class.getModifiers()));
  }
  @Test
  public void isIncludingManagedUserMethods_isPublic() {
    assertTrue(Modifier.isPublic(IsIncludingManagedUserMethods.class.getModifiers()));
  }
  @Test
  public void isIncludingPackagePrivateUserMethods_isPublic() {
    assertTrue(Modifier.isPublic(IsIncludingPackagePrivateUserMethods.class.getModifiers()));
  }
  @Test
  public void isIncludingPrivateUserMethods_isPublic() {
    assertTrue(Modifier.isPublic(IsIncludingPrivateUserMethods.class.getModifiers()));
  }
  @Test
  public void isIncludingProgramType_isPublic() {
    assertTrue(Modifier.isPublic(IsIncludingProgramType.class.getModifiers()));
  }
  @Test
  public void isIncludingProtectedUserMethods_isPublic() {
    assertTrue(Modifier.isPublic(IsIncludingProtectedUserMethods.class.getModifiers()));
  }
  @Test
  public void isIncludingThisForFieldAccessesState_isPublic() {
    assertTrue(Modifier.isPublic(IsIncludingThisForFieldAccessesState.class.getModifiers()));
  }
  @Test
  public void isIncludingTypeFeedbackForExpressionsState_isPublic() {
    assertTrue(Modifier.isPublic(IsIncludingTypeFeedbackForExpressionsState.class.getModifiers()));
  }
  @Test
  public void isJavaCodeOnTheSideState_isPublic() {
    assertTrue(Modifier.isPublic(IsJavaCodeOnTheSideState.class.getModifiers()));
  }
  @Test
  public void isNullAllowedForFieldInitializers_isPublic() {
    assertTrue(Modifier.isPublic(IsNullAllowedForFieldInitializers.class.getModifiers()));
  }
  @Test
  public void isNullAllowedForLocalInitializers_isPublic() {
    assertTrue(Modifier.isPublic(IsNullAllowedForLocalInitializers.class.getModifiers()));
  }
}
