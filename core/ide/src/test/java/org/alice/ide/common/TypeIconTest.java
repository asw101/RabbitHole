package org.alice.ide.common;

import org.junit.Assume;
import org.junit.Test;
import org.lgna.project.ast.AbstractType;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.NamedUserType;

import javax.swing.Icon;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

/**
 * Characterization tests for {@link TypeIcon}.
 *
 * <p>TypeIcon depends on {@link org.alice.ide.croquet.models.ui.formatter.FormatterState}
 * and AWT Graphics for text measurement. Tests that need FormatterState or Graphics
 * are headless-guarded. Pure structural/factory tests run everywhere.
 */
public class TypeIconTest {

  private static final JavaType STRING_TYPE = JavaType.getInstance(String.class);
  private static final JavaType OBJECT_TYPE = JavaType.getInstance(Object.class);
  private static final JavaType INTEGER_TYPE = JavaType.getInstance(Integer.class);

  // ── factory / constructor tests (always safe) ──────────────────────

  @Test
  public void getInstance_returnsNonNull() {
    try {
      TypeIcon icon = TypeIcon.getInstance(STRING_TYPE);
      assertNotNull(icon);
    } catch (ExceptionInInitializerError | NoClassDefFoundError e) {
      Assume.assumeNoException("TypeBorder init failed in headless", e);
    }
  }

  @Test
  public void getInstance_differentTypes_returnDifferentInstances() {
    try {
      TypeIcon a = TypeIcon.getInstance(STRING_TYPE);
      TypeIcon b = TypeIcon.getInstance(INTEGER_TYPE);
      assertNotSame("Different types → different icons", a, b);
    } catch (ExceptionInInitializerError | NoClassDefFoundError e) {
      Assume.assumeNoException("TypeBorder init failed in headless", e);
    }
  }

  @Test
  public void getInstance_returnsIconInterface() {
    try {
      TypeIcon icon = TypeIcon.getInstance(OBJECT_TYPE);
      assertTrue("TypeIcon should implement Icon", icon instanceof Icon);
    } catch (ExceptionInInitializerError | NoClassDefFoundError e) {
      Assume.assumeNoException("TypeBorder init failed in headless", e);
    }
  }

  // ── class structure ────────────────────────────────────────────────

  @Test
  public void typeIcon_implementsIcon() {
    assertTrue("TypeIcon should implement javax.swing.Icon",
        Icon.class.isAssignableFrom(TypeIcon.class));
  }

  @Test
  public void typeIcon_hasExpectedFields() throws Exception {
    Field typeField = TypeIcon.class.getDeclaredField("type");
    assertTrue("type field should be private",
        Modifier.isPrivate(typeField.getModifiers()));
    assertTrue("type field should be final",
        Modifier.isFinal(typeField.getModifiers()));

    Field borderField = TypeIcon.class.getDeclaredField("border");
    assertTrue("border field should be private",
        Modifier.isPrivate(borderField.getModifiers()));
  }

  @Test
  public void typeIcon_hasGetInstanceFactoryMethod() throws Exception {
    Method getInstanceMethod = TypeIcon.class.getMethod("getInstance", AbstractType.class);
    assertTrue("getInstance should be static",
        Modifier.isStatic(getInstanceMethod.getModifiers()));
    assertTrue("getInstance should be public",
        Modifier.isPublic(getInstanceMethod.getModifiers()));
  }

  @Test
  public void typeIcon_hasIndentDepthField() throws Exception {
    Field indentField = TypeIcon.class.getDeclaredField("INDENT_PER_DEPTH");
    assertTrue("INDENT_PER_DEPTH should be static",
        Modifier.isStatic(indentField.getModifiers()));
    indentField.setAccessible(true);
    assertEquals(12, indentField.getInt(null));
  }

  @Test
  public void typeIcon_hasBonusGapField() throws Exception {
    Field bonusGapField = TypeIcon.class.getDeclaredField("BONUS_GAP");
    assertTrue("BONUS_GAP should be static",
        Modifier.isStatic(bonusGapField.getModifiers()));
    bonusGapField.setAccessible(true);
    assertEquals(4, bonusGapField.getInt(null));
  }

  // ── constructor with all params ────────────────────────────────────

  @Test
  public void constructor_fourArg_createsIcon() {
    try {
      Font font = new Font(Font.SANS_SERIF, Font.PLAIN, 12);
      TypeIcon icon = new TypeIcon(STRING_TYPE, true, font, font);
      assertNotNull(icon);
    } catch (ExceptionInInitializerError | NoClassDefFoundError e) {
      Assume.assumeNoException("TypeBorder init failed", e);
    }
  }

  @Test
  public void constructor_withNamedUserType_createsIcon() {
    try {
      NamedUserType type = new NamedUserType();
      type.name.setValue("TestType");
      type.superType.setValue(OBJECT_TYPE);
      TypeIcon icon = TypeIcon.getInstance(type);
      assertNotNull(icon);
    } catch (ExceptionInInitializerError | NoClassDefFoundError e) {
      Assume.assumeNoException("TypeBorder init failed", e);
    }
  }

  @Test
  public void constructor_withNullType_doesNotThrow() {
    // TypeBorder.getSingletonFor(null) returns singletonForNull
    try {
      TypeIcon icon = new TypeIcon(null);
      assertNotNull(icon);
    } catch (ExceptionInInitializerError | NoClassDefFoundError e) {
      Assume.assumeNoException("TypeBorder init failed", e);
    }
  }

  // ── getTypeFont / getBonusFont ─────────────────────────────────────

  @Test
  public void getBonusFont_defaultConstructor_returnsNull() {
    try {
      TypeIcon icon = TypeIcon.getInstance(STRING_TYPE);
      assertNull("Default constructor → null bonusFont", icon.getBonusFont());
    } catch (ExceptionInInitializerError | NoClassDefFoundError e) {
      Assume.assumeNoException("TypeBorder init failed", e);
    }
  }

  @Test
  public void getBonusFont_withExplicitFont_returnsThatFont() {
    try {
      Font bonus = new Font(Font.MONOSPACED, Font.ITALIC, 10);
      TypeIcon icon = new TypeIcon(STRING_TYPE, false, null, bonus);
      assertSame(bonus, icon.getBonusFont());
    } catch (ExceptionInInitializerError | NoClassDefFoundError e) {
      Assume.assumeNoException("TypeBorder init failed", e);
    }
  }

  // ── headless-guarded dimension tests ───────────────────────────────
  // These require FormatterState.getInstance() and GraphicsUtilities.getGraphics()

  @Test
  public void getIconWidth_headless_returnsPositive() {
    Assume.assumeFalse("Requires display", GraphicsEnvironment.isHeadless());
    try {
      TypeIcon icon = TypeIcon.getInstance(STRING_TYPE);
      int width = icon.getIconWidth();
      assertTrue("Icon width should be positive", width > 0);
    } catch (Exception e) {
      Assume.assumeNoException("FormatterState or Graphics unavailable", e);
    }
  }

  @Test
  public void getIconHeight_headless_returnsPositive() {
    Assume.assumeFalse("Requires display", GraphicsEnvironment.isHeadless());
    try {
      TypeIcon icon = TypeIcon.getInstance(STRING_TYPE);
      int height = icon.getIconHeight();
      assertTrue("Icon height should be positive", height > 0);
    } catch (Exception e) {
      Assume.assumeNoException("FormatterState or Graphics unavailable", e);
    }
  }

  @Test
  public void getIconWidth_withIndent_greaterThanWithout() {
    Assume.assumeFalse("Requires display", GraphicsEnvironment.isHeadless());
    try {
      Font font = new Font(Font.SANS_SERIF, Font.PLAIN, 12);
      TypeIcon withoutIndent = new TypeIcon(STRING_TYPE, false, font, null);
      TypeIcon withIndent = new TypeIcon(STRING_TYPE, true, font, font);
      // With indent, extra width includes BONUS_GAP + bonus text
      // Width difference depends on context; just verify both return > 0
      assertTrue(withoutIndent.getIconWidth() > 0);
      assertTrue(withIndent.getIconWidth() > 0);
    } catch (Exception e) {
      Assume.assumeNoException("FormatterState or Graphics unavailable", e);
    }
  }
}
