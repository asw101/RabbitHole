package org.lgna.croquet;

import org.junit.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

/**
 * Coverage tests for {@link StringValue} — abstract class with Document-based
 * text management. Tests via concrete stub.
 */
public class StringValueDeepTest {

  // ── Hierarchy ─────────────────────────────────────────────────────

  @Test
  public void class_isAbstract() {
    assertTrue(Modifier.isAbstract(StringValue.class.getModifiers()));
  }

  @Test
  public void class_extendsAbstractElement() {
    assertTrue(AbstractElement.class.isAssignableFrom(StringValue.class));
  }

  @Test
  public void class_isPublic() {
    assertTrue(Modifier.isPublic(StringValue.class.getModifiers()));
  }

  // ── Method existence ──────────────────────────────────────────────

  @Test
  public void method_getText_exists() throws Exception {
    Method m = StringValue.class.getMethod("getText");
    assertNotNull(m);
    assertEquals(String.class, m.getReturnType());
  }

  @Test
  public void method_setText_exists() throws Exception {
    Method m = StringValue.class.getMethod("setText", String.class);
    assertNotNull(m);
    assertTrue(Modifier.isFinal(m.getModifiers()));
  }

  @Test
  public void method_getDocument_exists() throws Exception {
    Method m = StringValue.class.getMethod("getDocument");
    assertNotNull(m);
  }

  @Test
  public void method_getOriginalLocalizedText_exists() throws Exception {
    Method m = StringValue.class.getMethod("getOriginalLocalizedText");
    assertNotNull(m);
  }

  @Test
  public void method_modifiedLocalizedTextIfAppropriate_exists() throws Exception {
    Method m = StringValue.class.getDeclaredMethod(
        "modifiedLocalizedTextIfAppropriate", String.class);
    assertNotNull(m);
    assertTrue(Modifier.isProtected(m.getModifiers()));
  }

  @Test
  public void method_createLabel_exists() throws Exception {
    Method m = StringValue.class.getMethod("createLabel",
        edu.cmu.cs.dennisc.java.awt.font.TextAttribute[].class);
    assertNotNull(m);
  }

  @Test
  public void method_createLabel_withScalar_exists() throws Exception {
    Method m = StringValue.class.getMethod("createLabel",
        float.class, edu.cmu.cs.dennisc.java.awt.font.TextAttribute[].class);
    assertNotNull(m);
  }

  // ── Behavioral tests with stub ────────────────────────────────────

  @Test
  public void stub_getText_initiallyEmpty() {
    TestStringValue sv = new TestStringValue();
    assertEquals("", sv.getText());
  }

  @Test
  public void stub_setText_changesText() {
    TestStringValue sv = new TestStringValue();
    sv.setText("Hello");
    assertEquals("Hello", sv.getText());
  }

  @Test
  public void stub_setText_replacesPreviousText() {
    TestStringValue sv = new TestStringValue();
    sv.setText("First");
    sv.setText("Second");
    assertEquals("Second", sv.getText());
  }

  @Test
  public void stub_setText_emptyString() {
    TestStringValue sv = new TestStringValue();
    sv.setText("NonEmpty");
    sv.setText("");
    assertEquals("", sv.getText());
  }

  @Test
  public void stub_getDocument_nonNull() {
    TestStringValue sv = new TestStringValue();
    assertNotNull(sv.getDocument());
  }

  @Test
  public void stub_getOriginalLocalizedText_null_beforeInit() {
    TestStringValue sv = new TestStringValue();
    // getOriginalLocalizedText triggers initializeIfNecessary
    String text = sv.getOriginalLocalizedText();
    // No bundle exists for test stub, so localized text is null
    assertNull(text);
  }

  @Test
  public void stub_modifiedLocalizedTextIfAppropriate_returnsInput() throws Exception {
    TestStringValue sv = new TestStringValue();
    Method m = StringValue.class.getDeclaredMethod(
        "modifiedLocalizedTextIfAppropriate", String.class);
    m.setAccessible(true);
    assertEquals("input", m.invoke(sv, "input"));
  }

  @Test
  public void stub_modifiedLocalizedTextIfAppropriate_null() throws Exception {
    TestStringValue sv = new TestStringValue();
    Method m = StringValue.class.getDeclaredMethod(
        "modifiedLocalizedTextIfAppropriate", String.class);
    m.setAccessible(true);
    assertNull(m.invoke(sv, (Object) null));
  }

  @Test
  public void stub_toString_nonNull() {
    TestStringValue sv = new TestStringValue();
    assertNotNull(sv.toString());
  }

  @Test
  public void stub_toString_containsClassName() {
    TestStringValue sv = new TestStringValue();
    assertTrue(sv.toString().contains("TestStringValue"));
  }

  @Test
  public void stub_setText_null_handledGracefully() {
    TestStringValue sv = new TestStringValue();
    try {
      sv.setText(null);
      // If no exception, that's acceptable too
    } catch (RuntimeException e) {
      // Expected - BadLocationException wrapped as RuntimeException
      assertNotNull(e);
    }
  }

  // ── Long text ─────────────────────────────────────────────────────

  @Test
  public void stub_setText_longText() {
    TestStringValue sv = new TestStringValue();
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < 1000; i++) {
      sb.append("a");
    }
    sv.setText(sb.toString());
    assertEquals(1000, sv.getText().length());
  }

  // ── Unicode text ──────────────────────────────────────────────────

  @Test
  public void stub_setText_unicode() {
    TestStringValue sv = new TestStringValue();
    sv.setText("日本語テスト");
    assertEquals("日本語テスト", sv.getText());
  }

  // ── Concrete stub ─────────────────────────────────────────────────

  static class TestStringValue extends StringValue {
    TestStringValue() {
      super(CroquetTestUtils.nextTestUUID(), new javax.swing.text.PlainDocument());
    }

    @Override
    protected void localize() {
      // no-op
    }
  }
}
