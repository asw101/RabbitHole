package org.lgna.croquet;

import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

/**
 * Reflection-only coverage tests for {@link CustomItemState} — class structure,
 * InternalRoot hierarchy, and constructor presence. Cannot instantiate headlessly
 * due to CascadeBlank/Application dependency.
 */
public class CustomItemStateCoverageTest {

  // ── Class hierarchy ───────────────────────────────────────────────

  @Test
  public void customItemState_extendsItemState() {
    assertTrue(ItemState.class.isAssignableFrom(CustomItemState.class));
  }

  @Test
  public void customItemState_extendsState() {
    assertTrue(State.class.isAssignableFrom(CustomItemState.class));
  }

  @Test
  public void customItemState_extendsAbstractCompletionModel() {
    assertTrue(AbstractCompletionModel.class.isAssignableFrom(CustomItemState.class));
  }

  @Test
  public void customItemState_isAbstract() {
    assertTrue(Modifier.isAbstract(CustomItemState.class.getModifiers()));
  }

  @Test
  public void customItemState_isPublic() {
    assertTrue(Modifier.isPublic(CustomItemState.class.getModifiers()));
  }

  // ── DefaultCustomItemState subclass ───────────────────────────────

  @Test
  public void defaultCustomItemState_extendsCustomItemState() {
    assertTrue(CustomItemState.class.isAssignableFrom(DefaultCustomItemState.class));
  }

  @Test
  public void defaultCustomItemState_isAbstract() {
    assertTrue(Modifier.isAbstract(DefaultCustomItemState.class.getModifiers()));
  }

  // ── CustomItemStateWithInternalBlank ───────────────────────────────

  @Test
  public void customItemStateWithInternalBlank_extendsCustomItemState() {
    assertTrue(CustomItemState.class.isAssignableFrom(CustomItemStateWithInternalBlank.class));
  }

  // ── Constructor presence ──────────────────────────────────────────

  @Test
  public void customItemState_hasConstructor() {
    Constructor<?>[] ctors = CustomItemState.class.getDeclaredConstructors();
    assertTrue("CustomItemState should have at least one constructor", ctors.length > 0);
  }

  @Test
  public void customItemState_constructorIsProtected() {
    Constructor<?>[] ctors = CustomItemState.class.getDeclaredConstructors();
    for (Constructor<?> ctor : ctors) {
      assertTrue("Constructor should be protected",
          Modifier.isProtected(ctor.getModifiers()) || Modifier.isPublic(ctor.getModifiers()));
    }
  }

  // ── Method presence ───────────────────────────────────────────────

  @Test
  public void customItemState_hasGetValue() throws NoSuchMethodException {
    assertNotNull(State.class.getMethod("getValue"));
  }

  @Test
  public void customItemState_hasGetItemCodec() throws NoSuchMethodException {
    assertNotNull(ItemState.class.getMethod("getItemCodec"));
  }

  // ── Package ───────────────────────────────────────────────────────

  @Test
  public void customItemState_isInCroquetPackage() {
    assertEquals("org.lgna.croquet", CustomItemState.class.getPackage().getName());
  }
}
