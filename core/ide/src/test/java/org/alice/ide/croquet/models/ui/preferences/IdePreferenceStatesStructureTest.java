package org.alice.ide.croquet.models.ui.preferences;

import org.junit.Test;
import org.lgna.croquet.preferences.PreferenceBooleanState;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

public class IdePreferenceStatesStructureTest {

  private static final Class<?>[] PREFERENCE_BOOLEAN_STATES = {
      IsIncludingPrivateUserMethods.class,
      IsNullAllowedForFieldInitializers.class,
      IsNullAllowedForLocalInitializers.class,
      IsFullTypeHierarchyDesiredState.class,
      IsIncludingTypeFeedbackForExpressionsState.class,
      IsIncludingConstructors.class,
      IsIncludingPackagePrivateUserMethods.class,
      IsExposingReassignableStatusState.class,
      IsIncludingProtectedUserMethods.class,
      IsIncludingThisForFieldAccessesState.class,
      IsIncludingProgramType.class,
      IsJavaCodeOnTheSideState.class,
      IsIncludingManagedUserMethods.class
  };

  private static final String[] REQUESTED_CLASS_NAMES = {
      "org.alice.ide.croquet.models.ui.preferences.IsIncludingPrivateUserMethods",
      "org.alice.ide.croquet.models.ui.preferences.IsNullAllowedForFieldInitializers",
      "org.alice.ide.croquet.models.ui.preferences.IsNullAllowedForLocalInitializers",
      "org.alice.ide.croquet.models.ui.preferences.IsEmphasizingClassesState",
      "org.alice.ide.croquet.models.ui.preferences.IsFullTypeHierarchyDesiredState",
      "org.alice.ide.croquet.models.ui.preferences.IsIncludingTypeFeedbackForExpressionsState",
      "org.alice.ide.croquet.models.ui.preferences.IsIncludingConstructors",
      "org.alice.ide.croquet.models.ui.preferences.IsIncludingPackagePrivateUserMethods",
      "org.alice.ide.croquet.models.ui.preferences.IsExposingReassignableStatusState",
      "org.alice.ide.croquet.models.ui.preferences.IsIncludingProtectedUserMethods",
      "org.alice.ide.croquet.models.ui.preferences.IsIncludingThisForFieldAccessesState",
      "org.alice.ide.croquet.models.ui.preferences.IsIncludingProgramType",
      "org.alice.ide.croquet.models.ui.preferences.IsIncludingImportAndExportType",
      "org.alice.ide.croquet.models.ui.preferences.IsJavaCodeOnTheSideState",
      "org.alice.ide.croquet.models.ui.preferences.IsIncludingManagedUserMethods"
  };

  private static void assertPreferenceSingletonShape(Class<?> type) throws Exception {
    assertTrue(Modifier.isPublic(type.getModifiers()));
    assertFalse(Modifier.isAbstract(type.getModifiers()));
    assertTrue(PreferenceBooleanState.class.isAssignableFrom(type));

    Class<?> holder = Class.forName(type.getName() + "$SingletonHolder", false, type.getClassLoader());
    assertTrue(Modifier.isPrivate(holder.getModifiers()));

    Method getInstance = type.getMethod("getInstance");
    assertTrue(Modifier.isPublic(getInstance.getModifiers()));
    assertTrue(Modifier.isStatic(getInstance.getModifiers()));
    assertEquals(type, getInstance.getReturnType());

    Constructor<?> constructor = type.getDeclaredConstructor();
    assertTrue(Modifier.isPrivate(constructor.getModifiers()));
  }

  @Test
  public void requestedPreferenceClasses_whenLoaded_areAllLoadable() throws Exception {
    for (String className : REQUESTED_CLASS_NAMES) {
      assertNotNull(Class.forName(className, false, IdePreferenceStatesStructureTest.class.getClassLoader()));
    }
  }

  @Test
  public void preferenceBooleanStates_whenCounted_matchExpectedTotal() {
    assertEquals(13, PREFERENCE_BOOLEAN_STATES.length);
  }

  @Test
  public void preferenceBooleanStates_whenReflected_areConcretePreferenceBooleanStates() throws Exception {
    for (Class<?> type : PREFERENCE_BOOLEAN_STATES) {
      assertPreferenceSingletonShape(type);
    }
  }

  @Test
  public void preferenceBooleanStates_declaredPublicMethods_whenReflected_onlyExposeGetInstance() {
    for (Class<?> type : PREFERENCE_BOOLEAN_STATES) {
      int publicCount = 0;
      for (Method method : type.getDeclaredMethods()) {
        if (Modifier.isPublic(method.getModifiers())) {
          publicCount++;
          assertEquals("getInstance", method.getName());
        }
      }
      assertEquals(type.getSimpleName() + " should declare one public method", 1, publicCount);
    }
  }

  @Test
  public void preferenceBooleanStates_whenCollected_haveUniqueNames() {
    Set<String> names = new HashSet<>();
    for (Class<?> type : PREFERENCE_BOOLEAN_STATES) {
      assertTrue(names.add(type.getSimpleName()));
    }
  }

  @Test
  public void emphasizingClassesState_whenReflected_isPublicConcreteSingleton() throws Exception {
    assertTrue(Modifier.isPublic(IsEmphasizingClassesState.class.getModifiers()));
    assertFalse(Modifier.isAbstract(IsEmphasizingClassesState.class.getModifiers()));
    assertNotNull(Class.forName(IsEmphasizingClassesState.class.getName() + "$SingletonHolder", false,
        IsEmphasizingClassesState.class.getClassLoader()));

    Method getInstance = IsEmphasizingClassesState.class.getMethod("getInstance");
    assertTrue(Modifier.isPublic(getInstance.getModifiers()));
    assertTrue(Modifier.isStatic(getInstance.getModifiers()));
    assertEquals(IsEmphasizingClassesState.class, getInstance.getReturnType());

    Constructor<IsEmphasizingClassesState> constructor = IsEmphasizingClassesState.class.getDeclaredConstructor();
    assertTrue(Modifier.isPrivate(constructor.getModifiers()));
  }

  @Test
  public void emphasizingClassesState_getValue_whenReflected_returnsBoolean() throws Exception {
    Method method = IsEmphasizingClassesState.class.getDeclaredMethod("getValue");
    assertTrue(Modifier.isPublic(method.getModifiers()));
    assertEquals(boolean.class, method.getReturnType());
  }

  @Test
  public void emphasizingClassesState_valueField_whenReflected_isPrivateStatic() throws Exception {
    Field field = IsEmphasizingClassesState.class.getDeclaredField("value");
    assertTrue(Modifier.isPrivate(field.getModifiers()));
    assertTrue(Modifier.isStatic(field.getModifiers()));
    assertEquals(boolean.class, field.getType());
  }

  @Test
  public void includingImportAndExportType_whenReflected_isPublicConcreteUtilityClass() {
    assertTrue(Modifier.isPublic(IsIncludingImportAndExportType.class.getModifiers()));
    assertFalse(Modifier.isAbstract(IsIncludingImportAndExportType.class.getModifiers()));
  }

  @Test
  public void includingImportAndExportType_getValue_whenReflected_isPublicStaticBoolean() throws Exception {
    Method method = IsIncludingImportAndExportType.class.getMethod("getValue");
    assertTrue(Modifier.isPublic(method.getModifiers()));
    assertTrue(Modifier.isStatic(method.getModifiers()));
    assertEquals(boolean.class, method.getReturnType());
  }

  @Test
  public void includingImportAndExportType_valueField_whenReflected_isPrivateStaticFinal() throws Exception {
    Field field = IsIncludingImportAndExportType.class.getDeclaredField("value");
    assertTrue(Modifier.isPrivate(field.getModifiers()));
    assertTrue(Modifier.isStatic(field.getModifiers()));
    assertTrue(Modifier.isFinal(field.getModifiers()));
    assertEquals(boolean.class, field.getType());
  }

  @Test
  public void includingImportAndExportType_constructor_whenReflected_isImplicitPublicNoArg() {
    Constructor<?>[] constructors = IsIncludingImportAndExportType.class.getDeclaredConstructors();
    assertEquals(1, constructors.length);
    assertEquals(0, constructors[0].getParameterCount());
    assertTrue(Modifier.isPublic(constructors[0].getModifiers()));
  }
}
