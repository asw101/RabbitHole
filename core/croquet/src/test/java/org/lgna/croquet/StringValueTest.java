package org.lgna.croquet;

import org.junit.Before;
import org.junit.Test;

import javax.swing.text.AbstractDocument;
import java.util.UUID;

import static org.junit.Assert.*;

/**
 * Tests for {@link StringValue} via its concrete subclass {@link PlainStringValue}.
 * Covers getText, setText, getDocument, getOriginalLocalizedText, and createLabel.
 */
public class StringValueTest {

  private PlainStringValue stringValue;

  @Before
  public void setUp() {
    stringValue = new TestPlainStringValue();
  }

  @Test
  public void getDocument_returnsNonNull() {
    assertNotNull(stringValue.getDocument());
  }

  @Test
  public void getText_initiallyEmpty() {
    assertEquals("", stringValue.getText());
  }

  @Test
  public void setText_updatesText() {
    stringValue.setText("hello");
    assertEquals("hello", stringValue.getText());
  }

  @Test
  public void setText_empty() {
    stringValue.setText("something");
    stringValue.setText("");
    assertEquals("", stringValue.getText());
  }

  @Test
  public void setText_replaceExisting() {
    stringValue.setText("first");
    stringValue.setText("second");
    assertEquals("second", stringValue.getText());
  }

  @Test
  public void getOriginalLocalizedText_afterInit_returnsNull() {
    // Without a resource bundle, findDefaultLocalizedText returns null
    stringValue.initializeIfNecessary();
    assertNull(stringValue.getOriginalLocalizedText());
  }

  @Test
  public void createLabel_returnsNonNull() {
    assertNotNull(stringValue.createLabel());
  }

  @Test
  public void createLabel_withFontScalar_returnsNonNull() {
    assertNotNull(stringValue.createLabel(1.5f));
  }

  static class TestPlainStringValue extends PlainStringValue {
    TestPlainStringValue() {
      super(UUID.randomUUID());
    }

    @Override
    protected Class<? extends Element> getClassUsedForLocalization() {
      return TestPlainStringValue.class;
    }

    @Override
    protected String getSubKeyForLocalization() {
      return "test";
    }
  }
}
