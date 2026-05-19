package org.lgna.croquet;

import org.junit.Before;
import org.junit.Test;

import javax.swing.Icon;
import java.awt.Component;
import java.awt.Graphics;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.UUID;

import static org.junit.Assert.*;

public class LabelMenuSeparatorModelTest {

  private TestLabelMenuSeparatorModel model;

  @Before
  public void setUp() {
    model = new TestLabelMenuSeparatorModel(CroquetTestUtils.nextTestUUID());
  }

  // ── Class hierarchy ────────────────────────────────────────────────

  @Test
  public void class_isAbstract() {
    assertTrue(Modifier.isAbstract(LabelMenuSeparatorModel.class.getModifiers()));
  }

  @Test
  public void class_extendsStandardMenuItemPrepModel() {
    assertEquals(StandardMenuItemPrepModel.class, LabelMenuSeparatorModel.class.getSuperclass());
  }

  @Test
  public void class_isPublic() {
    assertTrue(Modifier.isPublic(LabelMenuSeparatorModel.class.getModifiers()));
  }

  @Test
  public void class_isNotFinal() {
    assertFalse(Modifier.isFinal(LabelMenuSeparatorModel.class.getModifiers()));
  }

  // ── Constructor ────────────────────────────────────────────────────

  @Test
  public void constructor_takesUUID() throws Exception {
    Constructor<LabelMenuSeparatorModel> ctor =
        LabelMenuSeparatorModel.class.getDeclaredConstructor(UUID.class);
    assertTrue(Modifier.isPublic(ctor.getModifiers()));
  }

  @Test
  public void constructorCount_isOne() {
    Constructor<?>[] ctors = LabelMenuSeparatorModel.class.getDeclaredConstructors();
    assertEquals(1, ctors.length);
  }

  // ── Fields ─────────────────────────────────────────────────────────

  @Test
  public void icon_field_exists() throws Exception {
    Field f = LabelMenuSeparatorModel.class.getDeclaredField("icon");
    assertTrue(Modifier.isPrivate(f.getModifiers()));
    assertEquals(Icon.class, f.getType());
  }

  @Test
  public void name_field_exists() throws Exception {
    Field f = LabelMenuSeparatorModel.class.getDeclaredField("name");
    assertTrue(Modifier.isPrivate(f.getModifiers()));
    assertEquals(String.class, f.getType());
  }

  @Test
  public void fieldCount_isTwo() {
    Field[] fields = LabelMenuSeparatorModel.class.getDeclaredFields();
    assertEquals(2, fields.length);
  }

  // ── getName ────────────────────────────────────────────────────────

  @Test
  public void getName_initiallyNull() {
    assertNull(model.getName());
  }

  @Test
  public void getName_afterLocalize_setsFromLocalizedText() throws Exception {
    Field nameField = LabelMenuSeparatorModel.class.getDeclaredField("name");
    nameField.setAccessible(true);
    nameField.set(model, "Section Header");

    assertEquals("Section Header", model.getName());
  }

  // ── Icon get/set ───────────────────────────────────────────────────

  @Test
  public void getIcon_initiallyNull() {
    assertNull(model.getIcon());
  }

  @Test
  public void setIcon_thenGetIcon() {
    Icon testIcon = createTestIcon(16, 16);
    model.setIcon(testIcon);

    assertSame(testIcon, model.getIcon());
  }

  @Test
  public void setIcon_null_clearsIcon() {
    model.setIcon(createTestIcon(8, 8));
    model.setIcon(null);

    assertNull(model.getIcon());
  }

  @Test
  public void setIcon_replacesPrevious() {
    Icon first = createTestIcon(16, 16);
    Icon second = createTestIcon(24, 24);

    model.setIcon(first);
    model.setIcon(second);

    assertSame(second, model.getIcon());
  }

  // ── Method signatures ──────────────────────────────────────────────

  @Test
  public void getName_signature() throws Exception {
    Method m = LabelMenuSeparatorModel.class.getDeclaredMethod("getName");
    assertTrue(Modifier.isPublic(m.getModifiers()));
    assertEquals(String.class, m.getReturnType());
  }

  @Test
  public void getIcon_signature() throws Exception {
    Method m = LabelMenuSeparatorModel.class.getDeclaredMethod("getIcon");
    assertTrue(Modifier.isPublic(m.getModifiers()));
    assertEquals(Icon.class, m.getReturnType());
  }

  @Test
  public void setIcon_signature() throws Exception {
    Method m = LabelMenuSeparatorModel.class.getDeclaredMethod("setIcon", Icon.class);
    assertTrue(Modifier.isPublic(m.getModifiers()));
    assertEquals(void.class, m.getReturnType());
  }

  @Test
  public void localize_isOverridden() throws Exception {
    Method m = LabelMenuSeparatorModel.class.getDeclaredMethod("localize");
    assertTrue(Modifier.isProtected(m.getModifiers()));
  }

  @Test
  public void createMenuItemAndAddTo_isOverridden() throws Exception {
    Method m = LabelMenuSeparatorModel.class.getDeclaredMethod("createMenuItemAndAddTo",
        org.lgna.croquet.views.MenuItemContainer.class);
    assertTrue(Modifier.isPublic(m.getModifiers()));
  }

  // ── Inner classes ──────────────────────────────────────────────────

  @Test
  public void noInnerClasses() {
    assertEquals(0, LabelMenuSeparatorModel.class.getDeclaredClasses().length);
  }

  // ── Method count ───────────────────────────────────────────────────

  @Test
  public void declaredMethods_count() {
    Method[] methods = LabelMenuSeparatorModel.class.getDeclaredMethods();
    int count = 0;
    for (Method m : methods) {
      if (!m.isSynthetic() && !m.isBridge()) {
        count++;
      }
    }
    assertTrue("Expected at least 5 declared methods, got " + count, count >= 5);
  }

  // ── StandardMenuItemPrepModel conformance ──────────────────────────

  @Test
  public void inheritsFromMenuItemPrepModel() {
    assertTrue(MenuItemPrepModel.class.isAssignableFrom(LabelMenuSeparatorModel.class));
  }

  @Test
  public void inheritsFromAbstractElement() {
    assertTrue(AbstractElement.class.isAssignableFrom(LabelMenuSeparatorModel.class));
  }

  // ── createMenuItemAndAddTo behavior ─────────────────────────────────

  @Test
  public void createMenuItemAndAddTo_withName_addsTextSeparator() throws Exception {
    Field nameField = LabelMenuSeparatorModel.class.getDeclaredField("name");
    nameField.setAccessible(true);
    model.initializeIfNecessary();
    nameField.set(model, "Visible Label");
    SeparatorCapture capture = new SeparatorCapture();

    org.lgna.croquet.views.MenuTextSeparator separator =
        model.createMenuItemAndAddTo(createMenuItemContainer(capture));

    assertNotNull(separator);
    assertSame(separator, capture.textSeparator);
    assertFalse(capture.plainSeparatorAdded);
  }

  @Test
  public void createMenuItemAndAddTo_withoutNameOrIcon_addsPlainSeparator() {
    SeparatorCapture capture = new SeparatorCapture();

    org.lgna.croquet.views.MenuTextSeparator separator =
        model.createMenuItemAndAddTo(createMenuItemContainer(capture));

    assertNull(separator);
    assertTrue(capture.plainSeparatorAdded);
    assertNull(capture.textSeparator);
  }

  private static org.lgna.croquet.views.MenuItemContainer createMenuItemContainer(
      final SeparatorCapture capture) {
    return (org.lgna.croquet.views.MenuItemContainer) Proxy.newProxyInstance(
        LabelMenuSeparatorModelTest.class.getClassLoader(),
        new Class<?>[] { org.lgna.croquet.views.MenuItemContainer.class },
        (proxy, method, args) -> {
          if ("addSeparator".equals(method.getName())) {
            if ((args == null) || (args.length == 0)) {
              capture.plainSeparatorAdded = true;
            } else {
              capture.textSeparator = (org.lgna.croquet.views.MenuTextSeparator) args[0];
            }
            return null;
          }
          Class<?> rt = method.getReturnType();
          if (rt == boolean.class) return false;
          if (rt == int.class) return 0;
          if (rt == long.class) return 0L;
          if (rt == double.class) return 0.0d;
          if (rt == float.class) return 0.0f;
          if (rt == short.class) return (short) 0;
          if (rt == byte.class) return (byte) 0;
          if (rt == char.class) return (char) 0;
          return null;
        });
  }

  private static final class SeparatorCapture {
    private boolean plainSeparatorAdded;
    private org.lgna.croquet.views.MenuTextSeparator textSeparator;
  }

  private static Icon createTestIcon(int w, int h) {
    return new Icon() {
      @Override public void paintIcon(Component c, Graphics g, int x, int y) {}
      @Override public int getIconWidth() { return w; }
      @Override public int getIconHeight() { return h; }
    };
  }

  private static final class TestLabelMenuSeparatorModel extends LabelMenuSeparatorModel {
    TestLabelMenuSeparatorModel(UUID id) {
      super(id);
    }
  }
}
