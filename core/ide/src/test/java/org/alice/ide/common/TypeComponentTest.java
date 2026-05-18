package org.alice.ide.common;

import org.junit.Assume;
import org.junit.Test;
import org.lgna.project.ast.AbstractType;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.NamedUserType;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

/**
 * Characterization tests for {@link TypeComponent}.
 *
 * <p>TypeComponent extends SwingComponentView which requires AWT/Swing context.
 * Pure factory and structural tests are always safe. GUI-dependent tests
 * use Assume guards.
 */
public class TypeComponentTest {

  // ── factory method ─────────────────────────────────────────────────

  @Test
  public void createInstance_returnsNonNull() {
    try {
      TypeComponent tc = TypeComponent.createInstance(JavaType.getInstance(String.class));
      assertNotNull(tc);
    } catch (ExceptionInInitializerError | NoClassDefFoundError e) {
      Assume.assumeNoException("TypeBorder init failed in headless", e);
    }
  }

  @Test
  public void createInstance_withNamedUserType_returnsNonNull() {
    try {
      NamedUserType type = new NamedUserType();
      type.name.setValue("TestType");
      type.superType.setValue(JavaType.getInstance(Object.class));
      TypeComponent tc = TypeComponent.createInstance(type);
      assertNotNull(tc);
    } catch (ExceptionInInitializerError | NoClassDefFoundError e) {
      Assume.assumeNoException("TypeBorder init failed in headless", e);
    }
  }

  @Test
  public void createInstance_differentTypes_returnDifferentInstances() {
    try {
      TypeComponent a = TypeComponent.createInstance(JavaType.getInstance(String.class));
      TypeComponent b = TypeComponent.createInstance(JavaType.getInstance(Integer.class));
      assertNotSame(a, b);
    } catch (ExceptionInInitializerError | NoClassDefFoundError e) {
      Assume.assumeNoException("TypeBorder init failed in headless", e);
    }
  }

  // ── class hierarchy ────────────────────────────────────────────────

  @Test
  public void typeComponent_extendsSwingComponentView() {
    Class<?> superClass = TypeComponent.class.getSuperclass();
    assertEquals("org.lgna.croquet.views.SwingComponentView", superClass.getName());
  }

  @Test
  public void typeComponent_isPublic() {
    assertTrue(Modifier.isPublic(TypeComponent.class.getModifiers()));
  }

  @Test
  public void typeComponent_isNotAbstract() {
    assertFalse(Modifier.isAbstract(TypeComponent.class.getModifiers()));
  }

  // ── private constructor ────────────────────────────────────────────

  @Test
  public void constructor_isPrivate() throws Exception {
    Constructor<?>[] ctors = TypeComponent.class.getDeclaredConstructors();
    assertEquals("Should have exactly one constructor", 1, ctors.length);
    assertTrue("Constructor should be private",
        Modifier.isPrivate(ctors[0].getModifiers()));
  }

  @Test
  public void constructor_takesAbstractType() throws Exception {
    Constructor<?>[] ctors = TypeComponent.class.getDeclaredConstructors();
    Class<?>[] paramTypes = ctors[0].getParameterTypes();
    assertEquals(1, paramTypes.length);
    assertEquals(AbstractType.class, paramTypes[0]);
  }

  // ── factory method structure ───────────────────────────────────────

  @Test
  public void createInstance_isPublicStatic() throws Exception {
    Method m = TypeComponent.class.getMethod("createInstance", AbstractType.class);
    assertTrue("createInstance should be static", Modifier.isStatic(m.getModifiers()));
    assertTrue("createInstance should be public", Modifier.isPublic(m.getModifiers()));
  }

  @Test
  public void createInstance_returnsTypeComponent() throws Exception {
    Method m = TypeComponent.class.getMethod("createInstance", AbstractType.class);
    assertEquals(TypeComponent.class, m.getReturnType());
  }

  // ── createAwtComponent override ────────────────────────────────────

  @Test
  public void createAwtComponent_isDeclared() throws Exception {
    Method m = TypeComponent.class.getDeclaredMethod("createAwtComponent");
    assertNotNull(m);
    assertTrue("createAwtComponent should be protected",
        Modifier.isProtected(m.getModifiers()));
  }

  @Test
  public void createAwtComponent_returnsJLabel() throws Exception {
    Method m = TypeComponent.class.getDeclaredMethod("createAwtComponent");
    assertEquals("javax.swing.JLabel", m.getReturnType().getName());
  }

  // ── type field ─────────────────────────────────────────────────────

  @Test
  public void typeField_isPrivate() throws Exception {
    java.lang.reflect.Field typeField = TypeComponent.class.getDeclaredField("type");
    assertTrue("type field should be private",
        Modifier.isPrivate(typeField.getModifiers()));
  }

  @Test
  public void typeField_isAbstractType() throws Exception {
    java.lang.reflect.Field typeField = TypeComponent.class.getDeclaredField("type");
    assertEquals(AbstractType.class, typeField.getType());
  }
}
