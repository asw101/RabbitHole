package org.lgna.croquet;

import org.junit.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

/**
 * Coverage tests for {@link PlainStringValue} — abstract class extending
 * StringValue with PlainDocument. Tests via concrete stub.
 */
public class PlainStringValueDeepTest {

  // ── Hierarchy ─────────────────────────────────────────────────────

  @Test
  public void class_isAbstract() {
    assertTrue(Modifier.isAbstract(PlainStringValue.class.getModifiers()));
  }

  @Test
  public void class_extendsStringValue() {
    assertTrue(StringValue.class.isAssignableFrom(PlainStringValue.class));
  }

  @Test
  public void class_extendsAbstractElement() {
    assertTrue(AbstractElement.class.isAssignableFrom(PlainStringValue.class));
  }

  // ── Constructor ───────────────────────────────────────────────────

  @Test
  public void constructor_takesUUID() throws Exception {
    var ctor = PlainStringValue.class.getDeclaredConstructor(java.util.UUID.class);
    assertNotNull(ctor);
    assertTrue(Modifier.isPublic(ctor.getModifiers()));
  }

  // ── Method existence ──────────────────────────────────────────────

  @Test
  public void method_createImmutableTextArea_exists() throws Exception {
    Method m = PlainStringValue.class.getMethod("createImmutableTextArea",
        edu.cmu.cs.dennisc.java.awt.font.TextAttribute[].class);
    assertNotNull(m);
  }

  @Test
  public void method_createImmutableTextArea_withScalar_exists() throws Exception {
    Method m = PlainStringValue.class.getMethod("createImmutableTextArea",
        float.class, edu.cmu.cs.dennisc.java.awt.font.TextAttribute[].class);
    assertNotNull(m);
  }

  @Test
  public void method_createImmutableTextField_exists() throws Exception {
    Method m = PlainStringValue.class.getMethod("createImmutableTextField",
        edu.cmu.cs.dennisc.java.awt.font.TextAttribute[].class);
    assertNotNull(m);
  }

  @Test
  public void method_createImmutableTextField_withScalar_exists() throws Exception {
    Method m = PlainStringValue.class.getMethod("createImmutableTextField",
        float.class, edu.cmu.cs.dennisc.java.awt.font.TextAttribute[].class);
    assertNotNull(m);
  }

  // ── Behavioral tests with stub ────────────────────────────────────

  @Test
  public void stub_construction_nonNull() {
    TestPlainStringValue sv = new TestPlainStringValue();
    assertNotNull(sv);
  }

  @Test
  public void stub_getDocument_nonNull() {
    TestPlainStringValue sv = new TestPlainStringValue();
    assertNotNull(sv.getDocument());
  }

  @Test
  public void stub_getDocument_isPlainDocument() {
    TestPlainStringValue sv = new TestPlainStringValue();
    assertTrue(sv.getDocument() instanceof javax.swing.text.PlainDocument);
  }

  @Test
  public void stub_getText_initiallyEmpty() {
    TestPlainStringValue sv = new TestPlainStringValue();
    assertEquals("", sv.getText());
  }

  @Test
  public void stub_setText_changesText() {
    TestPlainStringValue sv = new TestPlainStringValue();
    sv.setText("Hello World");
    assertEquals("Hello World", sv.getText());
  }

  @Test
  public void stub_setText_emptyString() {
    TestPlainStringValue sv = new TestPlainStringValue();
    sv.setText("Something");
    sv.setText("");
    assertEquals("", sv.getText());
  }

  @Test
  public void stub_setText_multipleChanges() {
    TestPlainStringValue sv = new TestPlainStringValue();
    sv.setText("First");
    sv.setText("Second");
    sv.setText("Third");
    assertEquals("Third", sv.getText());
  }

  @Test
  public void stub_toString_containsClassName() {
    TestPlainStringValue sv = new TestPlainStringValue();
    assertTrue(sv.toString().contains("TestPlainStringValue"));
  }

  @Test
  public void stub_initializeIfNecessary_callsLocalize() {
    TestPlainStringValue sv = new TestPlainStringValue();
    assertFalse(sv.localizeCalled);
    sv.initializeIfNecessary();
    assertTrue(sv.localizeCalled);
  }

  // ── Test stub ─────────────────────────────────────────────────────

  static class TestPlainStringValue extends PlainStringValue {
    boolean localizeCalled = false;

    TestPlainStringValue() {
      super(CroquetTestUtils.nextTestUUID());
    }

    @Override
    protected void localize() {
      localizeCalled = true;
    }
  }
}
