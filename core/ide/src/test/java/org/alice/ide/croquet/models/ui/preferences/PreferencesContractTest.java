package org.alice.ide.croquet.models.ui.preferences;

import org.junit.Test;
import org.lgna.croquet.preferences.PreferenceBooleanState;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

public class PreferencesContractTest {
  private static final Class<?>[] BOOLEAN_STATE_CLASSES = {
      IsExposingReassignableStatusState.class,
      IsFullTypeHierarchyDesiredState.class,
      IsIncludingConstructors.class,
      IsIncludingManagedUserMethods.class,
      IsIncludingPackagePrivateUserMethods.class,
      IsIncludingPrivateUserMethods.class,
      IsIncludingProgramType.class,
      IsIncludingProtectedUserMethods.class,
      IsIncludingThisForFieldAccessesState.class,
      IsIncludingTypeFeedbackForExpressionsState.class,
      IsJavaCodeOnTheSideState.class,
      IsNullAllowedForFieldInitializers.class,
      IsNullAllowedForLocalInitializers.class
  };

  private static final Class<?>[] SINGLETON_CLASSES = {
      IsExposingReassignableStatusState.class,
      IsFullTypeHierarchyDesiredState.class,
      IsIncludingConstructors.class,
      IsIncludingManagedUserMethods.class,
      IsIncludingPackagePrivateUserMethods.class,
      IsIncludingPrivateUserMethods.class,
      IsIncludingProgramType.class,
      IsIncludingProtectedUserMethods.class,
      IsIncludingThisForFieldAccessesState.class,
      IsIncludingTypeFeedbackForExpressionsState.class,
      IsJavaCodeOnTheSideState.class,
      IsNullAllowedForFieldInitializers.class,
      IsNullAllowedForLocalInitializers.class,
      IsEmphasizingClassesState.class
  };

  private static Class<?> getSingletonHolderClass(Class<?> cls) {
    for (Class<?> inner : cls.getDeclaredClasses()) {
      if ("SingletonHolder".equals(inner.getSimpleName())) {
        return inner;
      }
    }
    return null;
  }

  private static Object invokeGetInstance(Class<?> cls) throws Exception {
    return cls.getMethod("getInstance").invoke(null);
  }

  @Test
  public void singletonClassCountIsFourteen() {
    assertEquals(14, SINGLETON_CLASSES.length);
  }

  @Test
  public void booleanStateClassCountIsThirteen() {
    assertEquals(13, BOOLEAN_STATE_CLASSES.length);
  }

  @Test
  public void singletonClassSimpleNamesAreUnique() {
    Set<String> names = new HashSet<String>();
    for (Class<?> cls : SINGLETON_CLASSES) {
      assertTrue(names.add(cls.getSimpleName()));
    }
  }

  @Test
  public void allSingletonClassesLiveInPreferencePackage() {
    for (Class<?> cls : SINGLETON_CLASSES) {
      assertEquals(IsEmphasizingClassesState.class.getPackage(), cls.getPackage());
    }
  }

  @Test
  public void allSingletonClassesArePublicConcreteTypes() {
    for (Class<?> cls : SINGLETON_CLASSES) {
      assertTrue(Modifier.isPublic(cls.getModifiers()));
      assertFalse(Modifier.isAbstract(cls.getModifiers()));
    }
  }

  @Test
  public void allBooleanStateClassesExtendPreferenceBooleanState() {
    for (Class<?> cls : BOOLEAN_STATE_CLASSES) {
      assertTrue(PreferenceBooleanState.class.isAssignableFrom(cls));
    }
  }

  @Test
  public void emphasizingClassesStateDoesNotExtendPreferenceBooleanState() {
    assertFalse(PreferenceBooleanState.class.isAssignableFrom(IsEmphasizingClassesState.class));
  }

  @Test
  public void allSingletonClassesDeclareSingletonHolder() {
    for (Class<?> cls : SINGLETON_CLASSES) {
      assertNotNull(cls.getSimpleName(), getSingletonHolderClass(cls));
    }
  }

  @Test
  public void allSingletonHoldersArePrivateStatic() {
    for (Class<?> cls : SINGLETON_CLASSES) {
      int modifiers = getSingletonHolderClass(cls).getModifiers();
      assertTrue(Modifier.isPrivate(modifiers));
      assertTrue(Modifier.isStatic(modifiers));
    }
  }

  @Test
  public void allSingletonClassesDeclarePrivateConstructors() {
    for (Class<?> cls : SINGLETON_CLASSES) {
      Constructor<?>[] constructors = cls.getDeclaredConstructors();
      assertEquals(1, constructors.length);
      assertTrue(Modifier.isPrivate(constructors[0].getModifiers()));
    }
  }

  @Test
  public void allSingletonClassesDeclarePublicStaticGetInstance() throws Exception {
    for (Class<?> cls : SINGLETON_CLASSES) {
      Method method = cls.getMethod("getInstance");
      assertTrue(Modifier.isPublic(method.getModifiers()));
      assertTrue(Modifier.isStatic(method.getModifiers()));
    }
  }

  @Test
  public void allSingletonClassesGetInstanceReturnsOwnType() throws Exception {
    for (Class<?> cls : SINGLETON_CLASSES) {
      assertEquals(cls, cls.getMethod("getInstance").getReturnType());
    }
  }

  @Test
  public void allSingletonClassesReturnNonNullInstances() throws Exception {
    for (Class<?> cls : SINGLETON_CLASSES) {
      assertNotNull(invokeGetInstance(cls));
    }
  }

  @Test
  public void allSingletonClassesReturnSameInstanceRepeatedly() throws Exception {
    for (Class<?> cls : SINGLETON_CLASSES) {
      Object first = invokeGetInstance(cls);
      Object second = invokeGetInstance(cls);
      assertSame(cls.getSimpleName(), first, second);
    }
  }

  @Test
  public void allSingletonClassesStartWithIs() {
    for (Class<?> cls : SINGLETON_CLASSES) {
      assertTrue(cls.getSimpleName().startsWith("Is"));
    }
  }

  @Test
  public void singletonClassNamesMatchExpectedSet() {
    Set<String> actual = new HashSet<String>();
    for (Class<?> cls : SINGLETON_CLASSES) {
      actual.add(cls.getSimpleName());
    }
    assertTrue(actual.containsAll(Arrays.asList(
        "IsExposingReassignableStatusState",
        "IsFullTypeHierarchyDesiredState",
        "IsIncludingConstructors",
        "IsIncludingManagedUserMethods",
        "IsIncludingPackagePrivateUserMethods",
        "IsIncludingPrivateUserMethods",
        "IsIncludingProgramType",
        "IsIncludingProtectedUserMethods",
        "IsIncludingThisForFieldAccessesState",
        "IsIncludingTypeFeedbackForExpressionsState",
        "IsJavaCodeOnTheSideState",
        "IsNullAllowedForFieldInitializers",
        "IsNullAllowedForLocalInitializers",
        "IsEmphasizingClassesState")));
  }

  @Test
  public void importAndExportTypeDeclaresStaticBooleanValueField() throws Exception {
    Field field = IsIncludingImportAndExportType.class.getDeclaredField("value");
    assertEquals(boolean.class, field.getType());
    assertTrue(Modifier.isStatic(field.getModifiers()));
    assertTrue(Modifier.isFinal(field.getModifiers()));
  }

  @Test
  public void importAndExportTypeDeclaresPublicStaticGetValue() throws Exception {
    Method method = IsIncludingImportAndExportType.class.getMethod("getValue");
    assertEquals(boolean.class, method.getReturnType());
    assertTrue(Modifier.isPublic(method.getModifiers()));
    assertTrue(Modifier.isStatic(method.getModifiers()));
  }

  @Test
  public void importAndExportTypeGetValueIsStable() {
    assertEquals(IsIncludingImportAndExportType.getValue(), IsIncludingImportAndExportType.getValue());
  }
}
