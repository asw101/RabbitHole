package org.lgna.croquet;

import org.junit.Before;
import org.junit.Test;
import org.lgna.croquet.views.MenuItemContainer;
import org.lgna.croquet.views.MenuTextSeparator;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.UUID;

import static org.junit.Assert.*;

public class LabelMenuSeparatorModelCoverageTest {

  private UUID id;
  private TestLabelMenuSeparatorModel model;

  @Before
  public void setUp() {
    this.id = CroquetTestUtils.nextTestUUID();
    this.model = new TestLabelMenuSeparatorModel(this.id);
  }

  // ── Hierarchy ─────────────────────────────────────────────────────

  @Test
  public void class_isAbstract() {
    assertTrue(Modifier.isAbstract(LabelMenuSeparatorModel.class.getModifiers()));
  }

  @Test
  public void class_extendsStandardMenuItemPrepModel() {
    assertTrue(StandardMenuItemPrepModel.class.isAssignableFrom(LabelMenuSeparatorModel.class));
  }

  // ── Constructor and method signatures ────────────────────────────

  @Test
  public void constructor_signature_takesUuid() throws Exception {
    Constructor<LabelMenuSeparatorModel> constructor =
        LabelMenuSeparatorModel.class.getDeclaredConstructor(UUID.class);

    assertNotNull(constructor);
    assertTrue(Modifier.isPublic(constructor.getModifiers()));
  }

  @Test
  public void getName_method_exists_and_returnsString() throws Exception {
    Method method = LabelMenuSeparatorModel.class.getDeclaredMethod("getName");

    assertEquals(String.class, method.getReturnType());
    assertPublicInstanceMethod(method);
  }

  @Test
  public void getIcon_and_setIcon_methods_have_expected_signatures() throws Exception {
    Method getIcon = LabelMenuSeparatorModel.class.getDeclaredMethod("getIcon");
    Method setIcon = LabelMenuSeparatorModel.class.getDeclaredMethod("setIcon", Icon.class);

    assertEquals(Icon.class, getIcon.getReturnType());
    assertEquals(void.class, setIcon.getReturnType());
    assertPublicInstanceMethod(getIcon);
    assertPublicInstanceMethod(setIcon);
  }

  @Test
  public void createMenuItemAndAddTo_method_has_expected_signature() throws Exception {
    Method method = LabelMenuSeparatorModel.class.getDeclaredMethod(
        "createMenuItemAndAddTo", MenuItemContainer.class);

    assertEquals(MenuTextSeparator.class, method.getReturnType());
    assertPublicInstanceMethod(method);
  }

  // ── Concrete stub behavior ───────────────────────────────────────

  @Test
  public void concreteStub_supportsConstruction_and_initialIconIsNull() {
    assertNotNull(this.model);
    assertNull(this.model.getIcon());
  }

  @Test
  public void concreteStub_getName_returnsExpectedValue() throws Exception {
    this.model.initializeIfNecessary();
    setPrivateName(this.model, "Expected Label");

    assertEquals("Expected Label", this.model.getName());
  }

  @Test
  public void concreteStub_setIcon_and_getIcon_roundTrip() {
    Icon icon = new ImageIcon();

    this.model.setIcon(icon);

    assertSame(icon, this.model.getIcon());
  }

  @Test
  public void createMenuItemAndAddTo_withName_addsTextSeparator() throws Exception {
    this.model.initializeIfNecessary();
    setPrivateName(this.model, "Visible Label");
    SeparatorCapture capture = new SeparatorCapture();

    MenuTextSeparator separator = this.model.createMenuItemAndAddTo(createContainer(capture));

    assertNotNull(separator);
    assertSame(separator, capture.textSeparator);
    assertFalse(capture.plainSeparatorAdded);
  }

  @Test
  public void createMenuItemAndAddTo_withoutNameOrIcon_addsPlainSeparator() {
    SeparatorCapture capture = new SeparatorCapture();

    MenuTextSeparator separator = this.model.createMenuItemAndAddTo(createContainer(capture));

    assertNull(separator);
    assertTrue(capture.plainSeparatorAdded);
    assertNull(capture.textSeparator);
  }

  private static void assertPublicInstanceMethod(Method method) {
    assertTrue(Modifier.isPublic(method.getModifiers()));
    assertFalse(Modifier.isStatic(method.getModifiers()));
  }

  private static void setPrivateName(LabelMenuSeparatorModel model, String value) throws Exception {
    Field field = LabelMenuSeparatorModel.class.getDeclaredField("name");
    field.setAccessible(true);
    field.set(model, value);
  }

  private static MenuItemContainer createContainer(final SeparatorCapture capture) {
    return (MenuItemContainer) Proxy.newProxyInstance(
        MenuItemContainer.class.getClassLoader(),
        new Class<?>[] { MenuItemContainer.class },
        (proxy, method, args) -> {
          if ("addSeparator".equals(method.getName())) {
            if ((args == null) || (args.length == 0)) {
              capture.plainSeparatorAdded = true;
            } else {
              capture.textSeparator = (MenuTextSeparator) args[0];
            }
            return null;
          }
          return defaultValue(method.getReturnType());
        });
  }

  private static Object defaultValue(Class<?> returnType) {
    if (returnType == boolean.class) {
      return false;
    }
    if (returnType == int.class) {
      return 0;
    }
    if (returnType == long.class) {
      return 0L;
    }
    if (returnType == double.class) {
      return 0.0d;
    }
    if (returnType == float.class) {
      return 0.0f;
    }
    if (returnType == short.class) {
      return (short) 0;
    }
    if (returnType == byte.class) {
      return (byte) 0;
    }
    if (returnType == char.class) {
      return (char) 0;
    }
    return null;
  }

  private static final class SeparatorCapture {
    private boolean plainSeparatorAdded;
    private MenuTextSeparator textSeparator;
  }

  private static final class TestLabelMenuSeparatorModel extends LabelMenuSeparatorModel {
    private TestLabelMenuSeparatorModel(UUID id) {
      super(id);
    }
  }
}
