package org.lgna.croquet;

import edu.cmu.cs.dennisc.java.awt.font.TextAttribute;
import org.junit.Before;
import org.junit.Test;
import org.lgna.croquet.views.ImmutableEditorPane;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.UUID;

import static org.junit.Assert.*;

public class HtmlStringValueCoverageTest {

  private Constructor<HtmlStringValue> constructor;
  private Method scaledEditorPaneMethod;
  private Method varArgsEditorPaneMethod;
  private Method[] declaredMethods;

  @Before
  public void setUp() throws Exception {
    constructor = HtmlStringValue.class.getDeclaredConstructor(UUID.class);
    scaledEditorPaneMethod = HtmlStringValue.class.getDeclaredMethod(
        "createImmutableEditorPane", float.class, TextAttribute[].class);
    varArgsEditorPaneMethod = HtmlStringValue.class.getDeclaredMethod(
        "createImmutableEditorPane", TextAttribute[].class);
    declaredMethods = HtmlStringValue.class.getDeclaredMethods();
  }

  // ── Hierarchy ──────────────────────────────────────────────────────

  @Test
  public void class_isAbstract() {
    assertTrue(Modifier.isAbstract(HtmlStringValue.class.getModifiers()));
  }

  @Test
  public void class_extendsStringValue() {
    assertEquals(StringValue.class, HtmlStringValue.class.getSuperclass());
  }

  @Test
  public void class_extendsAbstractElementTransitively() {
    assertTrue(AbstractElement.class.isAssignableFrom(HtmlStringValue.class));
  }

  @Test
  public void class_isNotFinal() {
    assertFalse(Modifier.isFinal(HtmlStringValue.class.getModifiers()));
  }

  // ── Constructor ────────────────────────────────────────────────────

  @Test
  public void constructor_signature() {
    assertNotNull(constructor);
    assertTrue(Modifier.isPublic(constructor.getModifiers()));
    assertArrayEquals(new Class<?>[]{UUID.class}, constructor.getParameterTypes());
  }

  // ── Methods ────────────────────────────────────────────────────────

  @Test
  public void createImmutableEditorPane_withFontScalarSignature() {
    assertNotNull(scaledEditorPaneMethod);
    assertEquals(ImmutableEditorPane.class, scaledEditorPaneMethod.getReturnType());
    assertTrue(scaledEditorPaneMethod.isVarArgs());
    assertArrayEquals(new Class<?>[]{float.class, TextAttribute[].class},
        scaledEditorPaneMethod.getParameterTypes());
  }

  @Test
  public void createImmutableEditorPane_varArgsSignature() {
    assertNotNull(varArgsEditorPaneMethod);
    assertEquals(ImmutableEditorPane.class, varArgsEditorPaneMethod.getReturnType());
    assertTrue(varArgsEditorPaneMethod.isVarArgs());
    assertArrayEquals(new Class<?>[]{TextAttribute[].class},
        varArgsEditorPaneMethod.getParameterTypes());
  }

  @Test
  public void methodCount_matchesExpectedOverloads() {
    assertEquals(2, declaredMethods.length);
  }

  @Test
  public void declaredMethods_checkOnlyCreateImmutableEditorPaneOverloads() {
    for (Method method : declaredMethods) {
      assertEquals("createImmutableEditorPane", method.getName());
      assertTrue(Modifier.isPublic(method.getModifiers()));
    }
  }
}
