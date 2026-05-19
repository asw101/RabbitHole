package org.alice.ide.croquet.models.cascade.literals;

import org.alice.ide.croquet.models.cascade.ExpressionFillInWithoutBlanks;
import org.junit.Test;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import static org.junit.Assert.*;

public class LiteralFillInHierarchyTest {
  @Test
  public void integerLiteralFillIn_extendsBase() {
    assertTrue(ExpressionFillInWithoutBlanks.class.isAssignableFrom(IntegerLiteralFillIn.class));
  }
  @Test
  public void integerLiteralFillIn_hasGetInstance() throws Exception {
    Method m = IntegerLiteralFillIn.class.getMethod("getInstance", int.class);
    assertTrue(Modifier.isStatic(m.getModifiers()));
  }
  @Test
  public void nullLiteralFillIn_extendsBase() {
    assertTrue(ExpressionFillInWithoutBlanks.class.isAssignableFrom(NullLiteralFillIn.class));
  }
  @Test
  public void nullLiteralFillIn_hasGetInstance() throws Exception {
    Method m = NullLiteralFillIn.class.getMethod("getInstance");
    assertTrue(Modifier.isStatic(m.getModifiers()));
  }
  @Test
  public void nullLiteralFillIn_isSingleton() {
    assertSame(NullLiteralFillIn.getInstance(), NullLiteralFillIn.getInstance());
  }
  @Test
  public void stringLiteralFillIn_extendsBase() {
    assertTrue(ExpressionFillInWithoutBlanks.class.isAssignableFrom(StringLiteralFillIn.class));
  }
  @Test
  public void stringLiteralFillIn_hasGetInstance() throws Exception {
    Method m = StringLiteralFillIn.class.getMethod("getInstance", String.class);
    assertTrue(Modifier.isStatic(m.getModifiers()));
  }
  @Test
  public void doubleLiteralFillIn_extendsAbstractDoubleLiteralFillIn() {
    assertTrue(AbstractDoubleLiteralFillIn.class.isAssignableFrom(DoubleLiteralFillIn.class));
  }
  @Test
  public void abstractDoubleLiteralFillIn_extendsBase() {
    assertTrue(ExpressionFillInWithoutBlanks.class.isAssignableFrom(AbstractDoubleLiteralFillIn.class));
  }
  @Test
  public void abstractDoubleLiteralFillIn_isAbstract() {
    assertTrue(Modifier.isAbstract(AbstractDoubleLiteralFillIn.class.getModifiers()));
  }
  @Test
  public void doubleLiteralFillIn_hasGetInstance() throws Exception {
    Method m = DoubleLiteralFillIn.class.getMethod("getInstance", double.class);
    assertTrue(Modifier.isStatic(m.getModifiers()));
  }
  @Test
  public void allLiteralFillIns_arePublic() {
    Class<?>[] fillIns = { BooleanLiteralFillIn.class, IntegerLiteralFillIn.class,
        NullLiteralFillIn.class, StringLiteralFillIn.class,
        DoubleLiteralFillIn.class, AbstractDoubleLiteralFillIn.class };
    for (Class<?> c : fillIns) {
      assertTrue(c.getSimpleName() + " must be public", Modifier.isPublic(c.getModifiers()));
    }
  }
}
