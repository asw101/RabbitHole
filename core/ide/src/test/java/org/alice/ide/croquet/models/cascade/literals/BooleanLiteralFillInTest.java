package org.alice.ide.croquet.models.cascade.literals;

import org.alice.ide.croquet.models.cascade.ExpressionFillInWithoutBlanks;
import org.junit.Test;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import static org.junit.Assert.*;

public class BooleanLiteralFillInTest {
  @Test
  public void extendsExpressionFillInWithoutBlanks() {
    assertTrue(ExpressionFillInWithoutBlanks.class.isAssignableFrom(BooleanLiteralFillIn.class));
  }
  @Test
  public void isPublicConcrete() {
    int mods = BooleanLiteralFillIn.class.getModifiers();
    assertTrue(Modifier.isPublic(mods));
    assertFalse(Modifier.isAbstract(mods));
  }
  @Test
  public void hasGetInstanceStaticMethod() throws Exception {
    Method m = BooleanLiteralFillIn.class.getMethod("getInstance", boolean.class);
    assertTrue(Modifier.isPublic(m.getModifiers()));
    assertTrue(Modifier.isStatic(m.getModifiers()));
  }
  @Test
  public void getInstance_true_returnsNonNull() {
    assertNotNull(BooleanLiteralFillIn.getInstance(true));
  }
  @Test
  public void getInstance_false_returnsNonNull() {
    assertNotNull(BooleanLiteralFillIn.getInstance(false));
  }
  @Test
  public void getInstance_true_returnsSameInstance() {
    assertSame(BooleanLiteralFillIn.getInstance(true), BooleanLiteralFillIn.getInstance(true));
  }
  @Test
  public void getInstance_false_returnsSameInstance() {
    assertSame(BooleanLiteralFillIn.getInstance(false), BooleanLiteralFillIn.getInstance(false));
  }
  @Test
  public void getInstance_trueAndFalse_areDifferentInstances() {
    assertNotSame(BooleanLiteralFillIn.getInstance(true), BooleanLiteralFillIn.getInstance(false));
  }
}
