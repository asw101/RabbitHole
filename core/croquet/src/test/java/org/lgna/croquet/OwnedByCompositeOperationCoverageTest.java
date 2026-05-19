package org.lgna.croquet;

import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;

import static org.junit.Assert.*;

/**
 * Reflection-only coverage for {@link OwnedByCompositeOperation}. The class
 * is intentionally not instantiated in headless tests.
 */
public class OwnedByCompositeOperationCoverageTest {

  private Constructor<?> constructor;
  private Field compositeField;
  private Field subKeyField;

  @Before
  public void setUp() throws Exception {
    constructor = OwnedByCompositeOperation.class.getConstructor(
        Group.class,
        OperationOwningComposite.class,
        OwnedByCompositeOperationSubKey.class,
        Initializer.class);
    compositeField = OwnedByCompositeOperation.class.getDeclaredField("composite");
    subKeyField = OwnedByCompositeOperation.class.getDeclaredField("subKey");
  }

  // ── Class hierarchy ───────────────────────────────────────────────

  @Test
  public void class_isPublicFinalConcreteSubclass() {
    assertTrue(Modifier.isPublic(OwnedByCompositeOperation.class.getModifiers()));
    assertTrue(Modifier.isFinal(OwnedByCompositeOperation.class.getModifiers()));
    assertFalse(Modifier.isAbstract(OwnedByCompositeOperation.class.getModifiers()));
    assertEquals(AbstractOwnedByCompositeOperation.class, OwnedByCompositeOperation.class.getSuperclass());
  }

  @Test
  public void class_declaresSingleBoundedTypeParameter() {
    assertEquals(1, OwnedByCompositeOperation.class.getTypeParameters().length);
    Type bound = OwnedByCompositeOperation.class.getTypeParameters()[0].getBounds()[0];
    assertTrue(bound.getTypeName().contains("OperationOwningComposite"));
  }

  // ── Constructor and fields ───────────────────────────────────────

  @Test
  public void constructor_matchesDocumentedSignature() {
    assertNotNull(constructor);
    assertTrue(Modifier.isPublic(constructor.getModifiers()));
    assertArrayEquals(
        new Class<?>[]{Group.class, OperationOwningComposite.class, OwnedByCompositeOperationSubKey.class, Initializer.class},
        constructor.getParameterTypes());
  }

  @Test
  public void fields_arePrivateFinalAndTypedAsExpected() {
    assertEquals(OperationOwningComposite.class, compositeField.getType());
    assertEquals(OwnedByCompositeOperationSubKey.class, subKeyField.getType());
    assertTrue(Modifier.isPrivate(compositeField.getModifiers()));
    assertTrue(Modifier.isPrivate(subKeyField.getModifiers()));
    assertTrue(Modifier.isFinal(compositeField.getModifiers()));
    assertTrue(Modifier.isFinal(subKeyField.getModifiers()));
  }

  // ── Declared methods ──────────────────────────────────────────────

  @Test
  public void initialize_isProtectedVoidOverride() throws Exception {
    Method method = OwnedByCompositeOperation.class.getDeclaredMethod("initialize");

    assertEquals(void.class, method.getReturnType());
    assertEquals(0, method.getParameterCount());
    assertTrue(Modifier.isProtected(method.getModifiers()));
  }

  @Test
  public void getSubKeyForLocalization_isProtectedStringMethod() throws Exception {
    Method method = OwnedByCompositeOperation.class.getDeclaredMethod("getSubKeyForLocalization");

    assertEquals(String.class, method.getReturnType());
    assertTrue(Modifier.isProtected(method.getModifiers()));
  }

  @Test
  public void getComposite_isPublicAndReturnsOperationOwningComposite() throws Exception {
    Method method = OwnedByCompositeOperation.class.getDeclaredMethod("getComposite");

    assertEquals(OperationOwningComposite.class, method.getReturnType());
    assertTrue(Modifier.isPublic(method.getModifiers()));
  }

  @Test
  public void getClassUsedForLocalization_isProtectedClassMethod() throws Exception {
    Method method = OwnedByCompositeOperation.class.getDeclaredMethod("getClassUsedForLocalization");

    assertEquals(Class.class, method.getReturnType());
    assertTrue(Modifier.isProtected(method.getModifiers()));
  }

  @Test
  public void modifyNameIfNecessary_isProtectedStringToStringMethod() throws Exception {
    Method method = OwnedByCompositeOperation.class.getDeclaredMethod("modifyNameIfNecessary", String.class);

    assertEquals(String.class, method.getReturnType());
    assertEquals(1, method.getParameterCount());
    assertTrue(Modifier.isProtected(method.getModifiers()));
  }

  // ── Inherited API surface ─────────────────────────────────────────

  @Test
  public void inheritedAbstractOwnedByCompositeOperationMethods_arePresent() throws Exception {
    assertNotNull(OwnedByCompositeOperation.class.getMethod("fire", org.lgna.croquet.history.UserActivity.class));
    assertNotNull(OwnedByCompositeOperation.class.getMethod("getMenuItemPrepModel"));
    assertNotNull(OwnedByCompositeOperation.class.getMethod("getPotentialPrepModelPaths", org.lgna.croquet.edits.Edit.class));
    assertNotNull(OwnedByCompositeOperation.class.getMethod("relocalize"));
  }

  @Test
  public void superclass_performInActivity_remainsFinal() throws Exception {
    Method performInActivity = AbstractOwnedByCompositeOperation.class
        .getDeclaredMethod("performInActivity", org.lgna.croquet.history.UserActivity.class);

    assertTrue(Modifier.isProtected(performInActivity.getModifiers()));
    assertTrue(Modifier.isFinal(performInActivity.getModifiers()));
  }
}
