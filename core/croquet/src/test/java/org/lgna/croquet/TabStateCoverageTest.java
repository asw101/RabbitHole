package org.lgna.croquet;

import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

/**
 * Reflection-only coverage tests for {@link TabState} — class structure,
 * hierarchy, and method presence. Cannot instantiate headlessly due to
 * TabComposite infrastructure dependency.
 */
public class TabStateCoverageTest {

  // ── Class hierarchy ───────────────────────────────────────────────

  @Test
  public void tabState_extendsSingleSelectListState() {
    assertTrue(SingleSelectListState.class.isAssignableFrom(TabState.class));
  }

  @Test
  public void tabState_extendsItemState() {
    assertTrue(ItemState.class.isAssignableFrom(TabState.class));
  }

  @Test
  public void tabState_extendsState() {
    assertTrue(State.class.isAssignableFrom(TabState.class));
  }

  @Test
  public void tabState_isAbstract() {
    assertTrue(Modifier.isAbstract(TabState.class.getModifiers()));
  }

  @Test
  public void tabState_isPublic() {
    assertTrue(Modifier.isPublic(TabState.class.getModifiers()));
  }

  // ── Method presence ───────────────────────────────────────────────

  @Test
  public void tabState_hasGetValue() throws NoSuchMethodException {
    assertNotNull(State.class.getMethod("getValue"));
  }

  @Test
  public void tabState_hasSetValueTransactionlessly() throws NoSuchMethodException {
    assertNotNull(State.class.getMethod("setValueTransactionlessly", Object.class));
  }

  @Test
  public void tabState_hasGetItemCount() throws NoSuchMethodException {
    assertNotNull(SingleSelectListState.class.getMethod("getItemCount"));
  }

  @Test
  public void tabState_hasGetItemAt() throws NoSuchMethodException {
    assertNotNull(SingleSelectListState.class.getMethod("getItemAt", int.class));
  }

  @Test
  public void tabState_hasGetSelectedIndex() throws NoSuchMethodException {
    assertNotNull(SingleSelectListState.class.getMethod("getSelectedIndex"));
  }

  @Test
  public void tabState_hasSetSelectedIndex() throws NoSuchMethodException {
    assertNotNull(SingleSelectListState.class.getMethod("setSelectedIndex", int.class));
  }

  // ── Constructor presence ──────────────────────────────────────────

  @Test
  public void tabState_hasConstructor() {
    Constructor<?>[] ctors = TabState.class.getDeclaredConstructors();
    assertTrue("TabState should have at least one constructor", ctors.length > 0);
  }

  // ── Package ───────────────────────────────────────────────────────

  @Test
  public void tabState_isInCroquetPackage() {
    assertEquals("org.lgna.croquet", TabState.class.getPackage().getName());
  }

  // ── SimpleTabState subclass exists ────────────────────────────────

  @Test
  public void simpleTabState_extendsTabState() {
    assertTrue(TabState.class.isAssignableFrom(SimpleTabState.class));
  }

  @Test
  public void simpleTabState_isAbstract() {
    assertTrue(Modifier.isAbstract(SimpleTabState.class.getModifiers()));
  }
}
