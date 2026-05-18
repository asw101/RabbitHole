package org.alice.stageide;

import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

public class MemberCompositeTest {

  private static final String MEMBER_PACKAGE = "org.alice.stageide.member.";
  private static final String FILTERED_METHODS_SUB_COMPOSITE = "org.alice.ide.member.FilteredMethodsSubComposite";

  private static final String[] PROCEDURE_COMPOSITES = {
      "PositionProceduresComposite",
      "OrientationProceduresComposite",
      "SizeProceduresComposite",
      "AppearanceProceduresComposite",
      "TextProceduresComposite",
      "VehicleProceduresComposite",
      "TimingProceduresComposite",
      "AudioProceduresComposite",
      "SayThinkProceduresComposite",
      "AtmosphereProceduresComposite",
      "FieldOfViewProceduresComposite",
      "AddListenerProceduresComposite",
      "PositionAndOrientationProceduresComposite"
  };

  private static final String[] FUNCTION_COMPOSITES = {
      "AppearanceFunctionsComposite",
      "SizeFunctionsComposite",
      "AtmosphereFunctionsComposite",
      "PromptUserFunctionsComposite",
      "FieldOfViewFunctionsComposite",
      "JointFunctionsComposite",
      "SpatialRelationFunctionsComposite"
  };

  private static Class<?> loadComposite(String simpleName) throws Exception {
    return Class.forName(MEMBER_PACKAGE + simpleName, false, MemberCompositeTest.class.getClassLoader());
  }

  private static Class<?> loadFilteredMethodsSubComposite() throws Exception {
    return Class.forName(FILTERED_METHODS_SUB_COMPOSITE, false, MemberCompositeTest.class.getClassLoader());
  }

  private static void assertPrivateConstructorsOnly(Class<?> cls) {
    Constructor<?>[] constructors = cls.getDeclaredConstructors();
    assertTrue(constructors.length >= 1);
    for (Constructor<?> constructor : constructors) {
      assertTrue(cls.getName() + " constructors should be private", Modifier.isPrivate(constructor.getModifiers()));
    }
  }

  private static void assertSingletonAccessor(Class<?> cls) throws Exception {
    Method getInstance = cls.getMethod("getInstance");
    assertTrue(Modifier.isPublic(getInstance.getModifiers()));
    assertTrue(Modifier.isStatic(getInstance.getModifiers()));
    assertSame(cls, getInstance.getReturnType());

    Object first = getInstance.invoke(null);
    Object second = getInstance.invoke(null);
    assertSame(cls.getName() + " should be a singleton", first, second);
    assertTrue(cls.isInstance(first));
  }

  private static void assertMemberCompositeContract(String simpleName) throws Exception {
    Class<?> cls = loadComposite(simpleName);
    Class<?> filteredMethodsSubComposite = loadFilteredMethodsSubComposite();

    assertTrue(simpleName + " should be public", Modifier.isPublic(cls.getModifiers()));
    assertFalse(simpleName + " should be concrete", Modifier.isAbstract(cls.getModifiers()));
    assertFalse(simpleName + " should not be an interface", cls.isInterface());
    assertTrue(simpleName + " should extend FilteredMethodsSubComposite",
        filteredMethodsSubComposite.isAssignableFrom(cls));

    assertSingletonAccessor(cls);
    assertPrivateConstructorsOnly(cls);
  }

  @Test
  public void procedureCompositeCount_matchesRequestedCoverage() {
    assertEquals(13, PROCEDURE_COMPOSITES.length);
  }

  @Test
  public void functionCompositeCount_matchesRequestedCoverage() {
    assertEquals(7, FUNCTION_COMPOSITES.length);
  }

  @Test
  public void allProcedureComposites_followSingletonFilteredMethodsContract() throws Exception {
    for (String simpleName : PROCEDURE_COMPOSITES) {
      assertMemberCompositeContract(simpleName);
    }
  }

  @Test
  public void allFunctionComposites_followSingletonFilteredMethodsContract() throws Exception {
    for (String simpleName : FUNCTION_COMPOSITES) {
      assertMemberCompositeContract(simpleName);
    }
  }

  @Test
  public void procedureComposites_areLoadable() throws Exception {
    for (String simpleName : PROCEDURE_COMPOSITES) {
      assertNotNull(loadComposite(simpleName));
    }
  }

  @Test
  public void functionComposites_areLoadable() throws Exception {
    for (String simpleName : FUNCTION_COMPOSITES) {
      assertNotNull(loadComposite(simpleName));
    }
  }

  @Test
  public void allTwentyMemberComposites_areCovered() {
    assertEquals(20, PROCEDURE_COMPOSITES.length + FUNCTION_COMPOSITES.length);
  }

  @Test
  public void stageIde_classLoadsAndExposesGetActiveInstance() throws Exception {
    Class<?> stageIde = Class.forName("org.alice.stageide.StageIDE", false, MemberCompositeTest.class.getClassLoader());
    Method method = stageIde.getMethod("getActiveInstance");
    assertTrue(Modifier.isPublic(method.getModifiers()));
    assertTrue(Modifier.isStatic(method.getModifiers()));
    assertSame(stageIde, method.getReturnType());
  }

  @Test
  public void memberComposites_haveNoPublicConstructors() throws Exception {
    for (String simpleName : PROCEDURE_COMPOSITES) {
      Class<?> cls = loadComposite(simpleName);
      for (Constructor<?> constructor : cls.getDeclaredConstructors()) {
        assertFalse(Modifier.isPublic(constructor.getModifiers()));
      }
    }
    for (String simpleName : FUNCTION_COMPOSITES) {
      Class<?> cls = loadComposite(simpleName);
      for (Constructor<?> constructor : cls.getDeclaredConstructors()) {
        assertFalse(Modifier.isPublic(constructor.getModifiers()));
      }
    }
  }

  @Test
  public void singletonAccessorsAreConsistentlyTyped() throws Exception {
    for (String simpleName : PROCEDURE_COMPOSITES) {
      Class<?> cls = loadComposite(simpleName);
      Method getInstance = cls.getMethod("getInstance");
      assertSame(cls, getInstance.getReturnType());
    }
    for (String simpleName : FUNCTION_COMPOSITES) {
      Class<?> cls = loadComposite(simpleName);
      Method getInstance = cls.getMethod("getInstance");
      assertSame(cls, getInstance.getReturnType());
    }
  }

  @Test
  public void procedureCompositesRemainAssignableToFilteredMethodsSubComposite() throws Exception {
    Class<?> filteredMethodsSubComposite = loadFilteredMethodsSubComposite();
    for (String simpleName : PROCEDURE_COMPOSITES) {
      assertTrue(filteredMethodsSubComposite.isAssignableFrom(loadComposite(simpleName)));
    }
  }

  @Test
  public void functionCompositesRemainAssignableToFilteredMethodsSubComposite() throws Exception {
    Class<?> filteredMethodsSubComposite = loadFilteredMethodsSubComposite();
    for (String simpleName : FUNCTION_COMPOSITES) {
      assertTrue(filteredMethodsSubComposite.isAssignableFrom(loadComposite(simpleName)));
    }
  }
}
