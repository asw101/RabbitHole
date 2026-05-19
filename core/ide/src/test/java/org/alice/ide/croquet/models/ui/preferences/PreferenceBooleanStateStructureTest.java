package org.alice.ide.croquet.models.ui.preferences;

import org.junit.Test;
import org.lgna.croquet.preferences.PreferenceBooleanState;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

public class PreferenceBooleanStateStructureTest {

  // Classes that follow the PreferenceBooleanState singleton pattern
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
      IsNullAllowedForLocalInitializers.class,
  };

  @Test
  public void booleanStateClassCount_is13() {
    assertEquals(13, BOOLEAN_STATE_CLASSES.length);
  }

  @Test
  public void allBooleanStates_extendPreferenceBooleanState() {
    for (Class<?> cls : BOOLEAN_STATE_CLASSES) {
      assertTrue(cls.getSimpleName() + " must extend PreferenceBooleanState",
          PreferenceBooleanState.class.isAssignableFrom(cls));
    }
  }

  @Test
  public void allBooleanStates_haveSingletonHolderClass() {
    for (Class<?> cls : BOOLEAN_STATE_CLASSES) {
      boolean found = false;
      for (Class<?> inner : cls.getDeclaredClasses()) {
        if ("SingletonHolder".equals(inner.getSimpleName())) {
          found = true;
          break;
        }
      }
      assertTrue(cls.getSimpleName() + " must have inner class SingletonHolder", found);
    }
  }

  @Test
  public void allBooleanStates_haveGetInstanceMethod() throws NoSuchMethodException {
    for (Class<?> cls : BOOLEAN_STATE_CLASSES) {
      Method m = cls.getMethod("getInstance");
      assertTrue(cls.getSimpleName() + ".getInstance() must be public",
          Modifier.isPublic(m.getModifiers()));
      assertTrue(cls.getSimpleName() + ".getInstance() must be static",
          Modifier.isStatic(m.getModifiers()));
    }
  }

  @Test
  public void allBooleanStates_havePrivateConstructor() {
    for (Class<?> cls : BOOLEAN_STATE_CLASSES) {
      boolean hasPrivate = false;
      for (Constructor<?> ctor : cls.getDeclaredConstructors()) {
        if (Modifier.isPrivate(ctor.getModifiers())) {
          hasPrivate = true;
          break;
        }
      }
      assertTrue(cls.getSimpleName() + " must have a private constructor", hasPrivate);
    }
  }

  @Test
  public void allBooleanStates_getInstanceReturnType_matchesDeclaringClass() throws NoSuchMethodException {
    for (Class<?> cls : BOOLEAN_STATE_CLASSES) {
      Method m = cls.getMethod("getInstance");
      assertEquals(cls.getSimpleName() + ".getInstance() return type must be itself",
          cls, m.getReturnType());
    }
  }

  @Test
  public void allBooleanStates_classNamesAreUnique() {
    Set<String> names = new HashSet<>();
    for (Class<?> cls : BOOLEAN_STATE_CLASSES) {
      assertTrue("Duplicate class name: " + cls.getSimpleName(),
          names.add(cls.getSimpleName()));
    }
  }

  // --- IsEmphasizingClassesState: simple singleton (not PreferenceBooleanState) ---

  @Test
  public void emphasizingClasses_hasSingletonHolder() {
    boolean found = false;
    for (Class<?> inner : IsEmphasizingClassesState.class.getDeclaredClasses()) {
      if ("SingletonHolder".equals(inner.getSimpleName())) {
        found = true;
        break;
      }
    }
    assertTrue(found);
  }

  @Test
  public void emphasizingClasses_hasPrivateConstructor() {
    boolean hasPrivate = false;
    for (Constructor<?> ctor : IsEmphasizingClassesState.class.getDeclaredConstructors()) {
      if (Modifier.isPrivate(ctor.getModifiers())) {
        hasPrivate = true;
        break;
      }
    }
    assertTrue(hasPrivate);
  }

  @Test
  public void emphasizingClasses_hasGetInstance() throws NoSuchMethodException {
    Method m = IsEmphasizingClassesState.class.getMethod("getInstance");
    assertTrue(Modifier.isPublic(m.getModifiers()));
    assertTrue(Modifier.isStatic(m.getModifiers()));
  }

  // --- IsIncludingImportAndExportType: static utility class ---

  @Test
  public void importExport_hasStaticGetValue() throws NoSuchMethodException {
    Method m = IsIncludingImportAndExportType.class.getMethod("getValue");
    assertTrue(Modifier.isPublic(m.getModifiers()));
    assertTrue(Modifier.isStatic(m.getModifiers()));
    assertEquals(boolean.class, m.getReturnType());
  }
}
