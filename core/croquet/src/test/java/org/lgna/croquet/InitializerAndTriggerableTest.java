package org.lgna.croquet;

import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.UUID;

import static org.junit.Assert.*;

public class InitializerAndTriggerableTest {

  // ── Initializer interface ──────────────────────────────────────────

  @Test
  public void initializer_isInterface() {
    assertTrue(Initializer.class.isInterface());
  }

  @Test
  public void initializer_isPublic() {
    assertTrue(Modifier.isPublic(Initializer.class.getModifiers()));
  }

  @Test
  public void initializer_hasInitializeMethod() throws Exception {
    Method m = Initializer.class.getDeclaredMethod("initialize", Object.class);
    assertNotNull(m);
    assertEquals(void.class, m.getReturnType());
  }

  @Test
  public void initializer_hasOneTypeParameter() {
    assertEquals(1, Initializer.class.getTypeParameters().length);
    assertEquals("T", Initializer.class.getTypeParameters()[0].getName());
  }

  @Test
  public void initializer_methodCount_isOne() {
    Method[] methods = Initializer.class.getDeclaredMethods();
    int count = 0;
    for (Method m : methods) {
      if (!m.isSynthetic() && !m.isBridge() && !m.isDefault()) {
        count++;
      }
    }
    assertEquals(1, count);
  }

  @Test
  public void initializer_lambda_works() {
    final boolean[] called = {false};
    Initializer<String> init = value -> called[0] = true;
    init.initialize("test");
    assertTrue(called[0]);
  }

  @Test
  public void initializer_lambda_receivesValue() {
    final String[] captured = {null};
    Initializer<String> init = value -> captured[0] = value;
    init.initialize("hello");
    assertEquals("hello", captured[0]);
  }

  // ── Triggerable interface ──────────────────────────────────────────

  @Test
  public void triggerable_isInterface() {
    assertTrue(Triggerable.class.isInterface());
  }

  @Test
  public void triggerable_isPublic() {
    assertTrue(Modifier.isPublic(Triggerable.class.getModifiers()));
  }

  @Test
  public void triggerable_hasFireMethod() throws Exception {
    Method m = Triggerable.class.getDeclaredMethod("fire",
        org.lgna.croquet.history.UserActivity.class);
    assertNotNull(m);
    assertEquals(void.class, m.getReturnType());
  }

  @Test
  public void triggerable_methodCount_isOne() {
    Method[] methods = Triggerable.class.getDeclaredMethods();
    int count = 0;
    for (Method m : methods) {
      if (!m.isSynthetic() && !m.isBridge() && !m.isDefault()) {
        count++;
      }
    }
    assertEquals(1, count);
  }

  // ── Element interface ──────────────────────────────────────────────

  @Test
  public void element_isInterface() {
    assertTrue(Element.class.isInterface());
  }

  @Test
  public void element_isPublic() {
    assertTrue(Modifier.isPublic(Element.class.getModifiers()));
  }

  // ── Model interface ────────────────────────────────────────────────

  @Test
  public void model_isInterface() {
    assertTrue(Model.class.isInterface());
  }

  @Test
  public void model_isPublic() {
    assertTrue(Modifier.isPublic(Model.class.getModifiers()));
  }

  // ── PrepModel interface ────────────────────────────────────────────

  @Test
  public void prepModel_isInterface() {
    assertTrue(PrepModel.class.isInterface());
  }

  @Test
  public void prepModel_isPublic() {
    assertTrue(Modifier.isPublic(PrepModel.class.getModifiers()));
  }

  // ── CompletionModel interface ──────────────────────────────────────

  @Test
  public void completionModel_isInterface() {
    assertTrue(CompletionModel.class.isInterface());
  }

  @Test
  public void completionModel_isPublic() {
    assertTrue(Modifier.isPublic(CompletionModel.class.getModifiers()));
  }

  // ── OperationOwningComposite interface ─────────────────────────────

  @Test
  public void operationOwningComposite_isInterface() {
    assertTrue(OperationOwningComposite.class.isInterface());
  }

  @Test
  public void operationOwningComposite_extendsComposite() {
    assertTrue(Composite.class.isAssignableFrom(OperationOwningComposite.class));
  }

  @Test
  public void operationOwningComposite_hasPerformMethod() throws Exception {
    Method m = OperationOwningComposite.class.getDeclaredMethod("perform",
        org.lgna.croquet.history.UserActivity.class);
    assertNotNull(m);
  }

  @Test
  public void operationOwningComposite_hasModifyNameMethod() throws Exception {
    Method m = OperationOwningComposite.class.getDeclaredMethod("modifyNameIfNecessary",
        String.class);
    assertNotNull(m);
    assertEquals(String.class, m.getReturnType());
  }
}
