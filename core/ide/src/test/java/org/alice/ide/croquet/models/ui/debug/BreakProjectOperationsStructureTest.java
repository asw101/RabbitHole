package org.alice.ide.croquet.models.ui.debug;

import org.alice.ide.operations.InconsequentialActionOperation;
import org.junit.Test;
import org.lgna.croquet.ActionOperation;
import org.lgna.croquet.history.UserActivity;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

public class BreakProjectOperationsStructureTest {

  private static final Class<?>[] INCONSEQUENTIAL_OPERATIONS = {
      ThrowBogusLgnaExceptionOperation.class,
      ThrowBogusGlExceptionOperation.class,
      ThrowBogusExceptionOperation.class,
      RaiseAnomalousSituationOperation.class
  };

  private static void assertSingletonShape(Class<?> type) throws Exception {
    assertNotNull(Class.forName(type.getName() + "$SingletonHolder", false, type.getClassLoader()));

    Method getInstance = type.getMethod("getInstance");
    assertTrue(Modifier.isPublic(getInstance.getModifiers()));
    assertTrue(Modifier.isStatic(getInstance.getModifiers()));
    assertEquals(type, getInstance.getReturnType());

    Constructor<?> constructor = type.getDeclaredConstructor();
    assertTrue(Modifier.isPrivate(constructor.getModifiers()));
  }

  @Test
  public void breakProjectAddNullMethodOperation_whenReflected_isPublicConcreteActionOperation() {
    assertTrue(Modifier.isPublic(BreakProjectAddNullMethodOperation.class.getModifiers()));
    assertFalse(Modifier.isAbstract(BreakProjectAddNullMethodOperation.class.getModifiers()));
    assertEquals(ActionOperation.class, BreakProjectAddNullMethodOperation.class.getSuperclass());
  }

  @Test
  public void breakProjectAddNullMethodOperation_singletonPattern_whenReflected_isPresent() throws Exception {
    assertSingletonShape(BreakProjectAddNullMethodOperation.class);
  }

  @Test
  public void breakProjectAddNullMethodOperation_methods_whenReflected_matchExpectedShape() throws Exception {
    Method localize = BreakProjectAddNullMethodOperation.class.getDeclaredMethod("localize");
    assertTrue(Modifier.isProtected(localize.getModifiers()));
    assertEquals(void.class, localize.getReturnType());

    Method perform = BreakProjectAddNullMethodOperation.class.getDeclaredMethod("perform", UserActivity.class);
    assertTrue(Modifier.isProtected(perform.getModifiers()));
    assertEquals(void.class, perform.getReturnType());
  }

  @Test
  public void debugExceptionOperations_whenCounted_includeFourInconsequentialOperations() {
    assertEquals(4, INCONSEQUENTIAL_OPERATIONS.length);
  }

  @Test
  public void debugExceptionOperations_whenReflected_arePublicConcreteInconsequentialOperations() {
    for (Class<?> type : INCONSEQUENTIAL_OPERATIONS) {
      assertTrue(Modifier.isPublic(type.getModifiers()));
      assertFalse(Modifier.isAbstract(type.getModifiers()));
      assertEquals(InconsequentialActionOperation.class, type.getSuperclass());
    }
  }

  @Test
  public void debugExceptionOperations_singletonPattern_whenReflected_isPresent() throws Exception {
    for (Class<?> type : INCONSEQUENTIAL_OPERATIONS) {
      assertSingletonShape(type);
    }
  }

  @Test
  public void debugExceptionOperations_localize_whenReflected_isProtected() throws Exception {
    for (Class<?> type : INCONSEQUENTIAL_OPERATIONS) {
      Method localize = type.getDeclaredMethod("localize");
      assertTrue(type.getSimpleName() + " localize must be protected",
          Modifier.isProtected(localize.getModifiers()));
      assertEquals(void.class, localize.getReturnType());
    }
  }

  @Test
  public void debugExceptionOperations_performInternal_whenReflected_isProtectedVoid() throws Exception {
    for (Class<?> type : INCONSEQUENTIAL_OPERATIONS) {
      Method performInternal = type.getDeclaredMethod("performInternal");
      assertTrue(type.getSimpleName() + " performInternal must be protected",
          Modifier.isProtected(performInternal.getModifiers()));
      assertEquals(void.class, performInternal.getReturnType());
    }
  }

  @Test
  public void throwBogusLgnaExceptionOperation_whenLoaded_isAccessible() {
    assertNotNull(ThrowBogusLgnaExceptionOperation.class);
  }

  @Test
  public void throwBogusGlExceptionOperation_whenLoaded_isAccessible() {
    assertNotNull(ThrowBogusGlExceptionOperation.class);
  }

  @Test
  public void throwBogusExceptionOperation_whenLoaded_isAccessible() {
    assertNotNull(ThrowBogusExceptionOperation.class);
  }

  @Test
  public void raiseAnomalousSituationOperation_whenLoaded_isAccessible() {
    assertNotNull(RaiseAnomalousSituationOperation.class);
  }

  @Test
  public void debugExceptionOperations_getInstance_whenReflected_haveNoParameters() throws Exception {
    for (Class<?> type : INCONSEQUENTIAL_OPERATIONS) {
      Method method = type.getMethod("getInstance");
      assertEquals(0, method.getParameterCount());
      assertEquals(type, method.getReturnType());
    }
  }
}
