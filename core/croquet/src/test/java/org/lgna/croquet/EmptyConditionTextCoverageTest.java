package org.lgna.croquet;

import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

/**
 * Coverage tests for {@link EmptyConditionText} — package-private class
 * extending PlainStringValue. Uses reflection since the class has
 * package-private visibility and needs supplier arguments.
 */
public class EmptyConditionTextCoverageTest {

  // ── Class visibility ──────────────────────────────────────────────

  @Test
  public void class_isPackagePrivate() {
    int mods = EmptyConditionText.class.getModifiers();
    assertFalse(Modifier.isPublic(mods));
    assertFalse(Modifier.isProtected(mods));
    assertFalse(Modifier.isPrivate(mods));
  }

  @Test
  public void class_isNotAbstract() {
    assertFalse(Modifier.isAbstract(EmptyConditionText.class.getModifiers()));
  }

  // ── Hierarchy ─────────────────────────────────────────────────────

  @Test
  public void class_extendsPlainStringValue() {
    assertTrue(PlainStringValue.class.isAssignableFrom(
        EmptyConditionText.class));
  }

  @Test
  public void class_extendsStringValue() {
    assertTrue(StringValue.class.isAssignableFrom(
        EmptyConditionText.class));
  }

  @Test
  public void class_extendsAbstractElement() {
    assertTrue(AbstractElement.class.isAssignableFrom(
        EmptyConditionText.class));
  }

  // ── Constructor ───────────────────────────────────────────────────

  @Test
  public void constructor_exists() {
    Constructor<?>[] ctors = EmptyConditionText.class.getDeclaredConstructors();
    assertTrue(ctors.length > 0);
  }

  @Test
  public void constructor_isPackagePrivate() {
    Constructor<?>[] ctors = EmptyConditionText.class.getDeclaredConstructors();
    for (Constructor<?> ctor : ctors) {
      assertFalse(Modifier.isPublic(ctor.getModifiers()));
    }
  }

  @Test
  public void constructor_takesTwoSuppliers() {
    Constructor<?>[] ctors = EmptyConditionText.class.getDeclaredConstructors();
    boolean found = false;
    for (Constructor<?> ctor : ctors) {
      if (ctor.getParameterCount() == 2) {
        found = true;
      }
    }
    assertTrue("Should have 2-param constructor", found);
  }

  // ── Method existence ──────────────────────────────────────────────

  @Test
  public void method_getClassUsedForLocalization_exists() throws Exception {
    Method m = EmptyConditionText.class.getDeclaredMethod(
        "getClassUsedForLocalization");
    assertNotNull(m);
    assertTrue(Modifier.isProtected(m.getModifiers()));
  }

  @Test
  public void method_getSubKeyForLocalization_exists() throws Exception {
    Method m = EmptyConditionText.class.getDeclaredMethod(
        "getSubKeyForLocalization");
    assertNotNull(m);
    assertTrue(Modifier.isProtected(m.getModifiers()));
  }

  // ── Behavioral test with real instance ────────────────────────────

  @Test
  public void construction_withSuppliers_nonNull() {
    EmptyConditionText ect = new EmptyConditionText(
        () -> EmptyConditionText.class,
        () -> null
    );
    assertNotNull(ect);
  }

  @Test
  public void getDocument_nonNull() {
    EmptyConditionText ect = new EmptyConditionText(
        () -> EmptyConditionText.class,
        () -> null
    );
    assertNotNull(ect.getDocument());
  }

  @Test
  public void getText_initiallyEmpty() {
    EmptyConditionText ect = new EmptyConditionText(
        () -> EmptyConditionText.class,
        () -> null
    );
    assertEquals("", ect.getText());
  }

  @Test
  public void setText_changesText() {
    EmptyConditionText ect = new EmptyConditionText(
        () -> EmptyConditionText.class,
        () -> null
    );
    ect.setText("No items");
    assertEquals("No items", ect.getText());
  }

  @Test
  public void getSubKeyForLocalization_containsEmptyConditionText() throws Exception {
    EmptyConditionText ect = new EmptyConditionText(
        () -> EmptyConditionText.class,
        () -> null
    );
    Method m = EmptyConditionText.class.getDeclaredMethod("getSubKeyForLocalization");
    m.setAccessible(true);
    String subKey = (String) m.invoke(ect);
    assertTrue(subKey.contains("emptyConditionText"));
  }

  @Test
  public void getSubKeyForLocalization_withParentSubKey_prependsIt() throws Exception {
    EmptyConditionText ect = new EmptyConditionText(
        () -> EmptyConditionText.class,
        () -> "items"
    );
    Method m = EmptyConditionText.class.getDeclaredMethod("getSubKeyForLocalization");
    m.setAccessible(true);
    String subKey = (String) m.invoke(ect);
    assertEquals("items.emptyConditionText", subKey);
  }

  @Test
  public void getClassUsedForLocalization_returnsSuppliedClass() throws Exception {
    EmptyConditionText ect = new EmptyConditionText(
        () -> EmptyConditionText.class,
        () -> null
    );
    Method m = EmptyConditionText.class.getDeclaredMethod("getClassUsedForLocalization");
    m.setAccessible(true);
    assertEquals(EmptyConditionText.class, m.invoke(ect));
  }

  // ── toString ──────────────────────────────────────────────────────

  @Test
  public void toString_nonNull() {
    EmptyConditionText ect = new EmptyConditionText(
        () -> EmptyConditionText.class,
        () -> null
    );
    assertNotNull(ect.toString());
  }

  @Test
  public void toString_containsClassName() {
    EmptyConditionText ect = new EmptyConditionText(
        () -> EmptyConditionText.class,
        () -> null
    );
    assertTrue(ect.toString().contains("EmptyConditionText"));
  }
}
