package org.alice.ide.ast.draganddrop;

import org.junit.Test;
import org.lgna.croquet.DropSite;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import static org.junit.Assert.*;

public class BlockStatementIndexPairTest {
  @Test
  public void implementsDropSite() {
    assertTrue(DropSite.class.isAssignableFrom(BlockStatementIndexPair.class));
  }
  @Test
  public void isFinalClass() {
    assertTrue(Modifier.isFinal(BlockStatementIndexPair.class.getModifiers()));
  }
  @Test
  public void hasBlockStatementAndIndexConstructor() throws Exception {
    Constructor<?> c = BlockStatementIndexPair.class.getConstructor(
        org.lgna.project.ast.BlockStatement.class, int.class);
    assertTrue(Modifier.isPublic(c.getModifiers()));
  }
  @Test
  public void hasGetBlockStatementMethod() throws Exception {
    Method m = BlockStatementIndexPair.class.getMethod("getBlockStatement");
    assertTrue(Modifier.isPublic(m.getModifiers()));
  }
  @Test
  public void hasGetIndexMethod() throws Exception {
    Method m = BlockStatementIndexPair.class.getMethod("getIndex");
    assertTrue(Modifier.isPublic(m.getModifiers()));
  }
  @Test
  public void hasEqualsOverride() throws Exception {
    Method m = BlockStatementIndexPair.class.getMethod("equals", Object.class);
    assertEquals(BlockStatementIndexPair.class, m.getDeclaringClass());
  }
  @Test
  public void hasHashCodeOverride() throws Exception {
    Method m = BlockStatementIndexPair.class.getMethod("hashCode");
    assertEquals(BlockStatementIndexPair.class, m.getDeclaringClass());
  }
  @Test
  public void hasToStringOverride() throws Exception {
    Method m = BlockStatementIndexPair.class.getMethod("toString");
    assertEquals(BlockStatementIndexPair.class, m.getDeclaringClass());
  }
  @Test
  public void hasCreateInstanceFromChildStatementMethod() throws Exception {
    Method m = BlockStatementIndexPair.class.getMethod("createInstanceFromChildStatement", org.lgna.project.ast.Statement.class);
    assertTrue(Modifier.isPublic(m.getModifiers()));
    assertTrue(Modifier.isStatic(m.getModifiers()));
  }
}
