package org.lgna.story.implementation;

import org.alice.math.immutable.Dimension3;
import org.junit.Test;
import org.lgna.story.STextModel;

import java.awt.Font;

import static org.junit.Assert.*;

/**
 * Tests for {@link TextModelImp} via {@link STextModel}.
 *
 * <p>Covers text mutation operations (append, insert, delete, replace,
 * setCharAt), font management, and value/length queries beyond what
 * {@link MoreImplBehaviorTest} already covers via the STextModel facade.
 */
public class TextModelImpCoverageTest {

  // ══════════════════════════════════════════════════════════════════════
  //  Construction
  // ══════════════════════════════════════════════════════════════════════

  @Test
  public void textModel_constructsWithoutError() {
    STextModel model = new STextModel();
    assertNotNull(model.getImplementation());
  }

  @Test
  public void textModel_abstractionRoundTrips() {
    STextModel model = new STextModel();
    assertSame(model, model.getImplementation().getAbstraction());
  }

  @Test
  public void textModel_initialValueIsEmpty() {
    STextModel model = new STextModel();
    TextModelImp imp = model.getImplementation();
    assertEquals("", imp.getValue());
    assertEquals(0, imp.getLength());
  }

  @Test
  public void textModel_extendsSimpleModelImp() {
    assertTrue(SimpleModelImp.class.isAssignableFrom(TextModelImp.class));
  }

  // ══════════════════════════════════════════════════════════════════════
  //  setValue / getValue
  // ══════════════════════════════════════════════════════════════════════

  @Test
  public void textModel_setValue_singleWord() {
    STextModel model = new STextModel();
    TextModelImp imp = model.getImplementation();
    imp.setValue("Hello");
    assertEquals("Hello", imp.getValue());
    assertEquals(5, imp.getLength());
  }

  @Test
  public void textModel_setValue_overwritesPrevious() {
    STextModel model = new STextModel();
    TextModelImp imp = model.getImplementation();
    imp.setValue("First");
    imp.setValue("Second");
    assertEquals("Second", imp.getValue());
  }

  @Test
  public void textModel_setValue_emptyString() {
    STextModel model = new STextModel();
    TextModelImp imp = model.getImplementation();
    imp.setValue("Non-empty");
    imp.setValue("");
    assertEquals("", imp.getValue());
    assertEquals(0, imp.getLength());
  }

  // ══════════════════════════════════════════════════════════════════════
  //  append
  // ══════════════════════════════════════════════════════════════════════

  @Test
  public void textModel_append_toEmpty() {
    STextModel model = new STextModel();
    TextModelImp imp = model.getImplementation();
    imp.append("Hello");
    assertEquals("Hello", imp.getValue());
  }

  @Test
  public void textModel_append_multiple() {
    STextModel model = new STextModel();
    TextModelImp imp = model.getImplementation();
    imp.setValue("A");
    imp.append("B");
    imp.append("C");
    assertEquals("ABC", imp.getValue());
  }

  @Test
  public void textModel_append_number() {
    STextModel model = new STextModel();
    TextModelImp imp = model.getImplementation();
    imp.setValue("Value: ");
    imp.append(42);
    assertEquals("Value: 42", imp.getValue());
  }

  // ══════════════════════════════════════════════════════════════════════
  //  insert
  // ══════════════════════════════════════════════════════════════════════

  @Test
  public void textModel_insert_atBeginning() {
    STextModel model = new STextModel();
    TextModelImp imp = model.getImplementation();
    imp.setValue("World");
    imp.insert(0, "Hello ");
    assertEquals("Hello World", imp.getValue());
  }

  @Test
  public void textModel_insert_inMiddle() {
    STextModel model = new STextModel();
    TextModelImp imp = model.getImplementation();
    imp.setValue("Hllo");
    imp.insert(1, "e");
    assertEquals("Hello", imp.getValue());
  }

  @Test
  public void textModel_insert_atEnd() {
    STextModel model = new STextModel();
    TextModelImp imp = model.getImplementation();
    imp.setValue("Hello");
    imp.insert(5, "!");
    assertEquals("Hello!", imp.getValue());
  }

  // ══════════════════════════════════════════════════════════════════════
  //  delete / deleteCharAt
  // ══════════════════════════════════════════════════════════════════════

  @Test
  public void textModel_delete_range() {
    STextModel model = new STextModel();
    TextModelImp imp = model.getImplementation();
    imp.setValue("Hello World");
    imp.delete(5, 11);
    assertEquals("Hello", imp.getValue());
  }

  @Test
  public void textModel_delete_fromBeginning() {
    STextModel model = new STextModel();
    TextModelImp imp = model.getImplementation();
    imp.setValue("Hello");
    imp.delete(0, 2);
    assertEquals("llo", imp.getValue());
  }

  @Test
  public void textModel_deleteCharAt_first() {
    STextModel model = new STextModel();
    TextModelImp imp = model.getImplementation();
    imp.setValue("ABC");
    imp.deleteCharAt(0);
    assertEquals("BC", imp.getValue());
  }

  @Test
  public void textModel_deleteCharAt_last() {
    STextModel model = new STextModel();
    TextModelImp imp = model.getImplementation();
    imp.setValue("ABC");
    imp.deleteCharAt(2);
    assertEquals("AB", imp.getValue());
  }

  @Test
  public void textModel_deleteCharAt_middle() {
    STextModel model = new STextModel();
    TextModelImp imp = model.getImplementation();
    imp.setValue("ABC");
    imp.deleteCharAt(1);
    assertEquals("AC", imp.getValue());
  }

  // ══════════════════════════════════════════════════════════════════════
  //  replace
  // ══════════════════════════════════════════════════════════════════════

  @Test
  public void textModel_replace_substring() {
    STextModel model = new STextModel();
    TextModelImp imp = model.getImplementation();
    imp.setValue("Hello World");
    imp.replace(6, 11, "Java");
    assertEquals("Hello Java", imp.getValue());
  }

  @Test
  public void textModel_replace_withLonger() {
    STextModel model = new STextModel();
    TextModelImp imp = model.getImplementation();
    imp.setValue("ab");
    imp.replace(0, 2, "ABCDE");
    assertEquals("ABCDE", imp.getValue());
  }

  @Test
  public void textModel_replace_withShorter() {
    STextModel model = new STextModel();
    TextModelImp imp = model.getImplementation();
    imp.setValue("ABCDE");
    imp.replace(0, 5, "ab");
    assertEquals("ab", imp.getValue());
  }

  // ══════════════════════════════════════════════════════════════════════
  //  charAt / setCharAt
  // ══════════════════════════════════════════════════════════════════════

  @Test
  public void textModel_charAt_eachPosition() {
    STextModel model = new STextModel();
    TextModelImp imp = model.getImplementation();
    imp.setValue("ABC");
    assertEquals('A', imp.charAt(0));
    assertEquals('B', imp.charAt(1));
    assertEquals('C', imp.charAt(2));
  }

  @Test
  public void textModel_setCharAt_roundTrips() {
    STextModel model = new STextModel();
    TextModelImp imp = model.getImplementation();
    imp.setValue("XYZ");
    imp.setCharAt(1, 'Q');
    assertEquals("XQZ", imp.getValue());
  }

  // ══════════════════════════════════════════════════════════════════════
  //  indexOf / lastIndexOf
  // ══════════════════════════════════════════════════════════════════════

  @Test
  public void textModel_indexOf_found() {
    STextModel model = new STextModel();
    TextModelImp imp = model.getImplementation();
    imp.setValue("foobarfoo");
    assertEquals(0, imp.indexOf("foo"));
    assertEquals(3, imp.indexOf("bar"));
  }

  @Test
  public void textModel_indexOf_notFound() {
    STextModel model = new STextModel();
    TextModelImp imp = model.getImplementation();
    imp.setValue("foo");
    assertEquals(-1, imp.indexOf("xyz"));
  }

  @Test
  public void textModel_indexOf_withFromIndex() {
    STextModel model = new STextModel();
    TextModelImp imp = model.getImplementation();
    imp.setValue("foobarfoo");
    assertEquals(6, imp.indexOf("foo", 1));
  }

  @Test
  public void textModel_lastIndexOf_basic() {
    STextModel model = new STextModel();
    TextModelImp imp = model.getImplementation();
    imp.setValue("abcabc");
    assertEquals(3, imp.lastIndexOf("abc"));
  }

  @Test
  public void textModel_lastIndexOf_withFromIndex() {
    STextModel model = new STextModel();
    TextModelImp imp = model.getImplementation();
    imp.setValue("abcabc");
    assertEquals(0, imp.lastIndexOf("abc", 2));
  }

  // ══════════════════════════════════════════════════════════════════════
  //  Font
  // ══════════════════════════════════════════════════════════════════════

  @Test
  public void textModel_getFont_notNull() {
    STextModel model = new STextModel();
    TextModelImp imp = model.getImplementation();
    assertNotNull(imp.getFont());
  }

  @Test
  public void textModel_setFont_roundTrips() {
    STextModel model = new STextModel();
    TextModelImp imp = model.getImplementation();
    Font customFont = new Font("SansSerif", Font.BOLD, 24);
    imp.setFont(customFont);
    assertEquals(customFont, imp.getFont());
  }

  @Test
  public void textModel_initialFontSize() {
    STextModel model = new STextModel();
    TextModelImp imp = model.getImplementation();
    assertEquals(12, imp.getFont().getSize());
  }

  // ══════════════════════════════════════════════════════════════════════
  //  setSize
  // ══════════════════════════════════════════════════════════════════════

  @Test
  public void textModel_setSize_doesNotThrow() {
    STextModel model = new STextModel();
    TextModelImp imp = model.getImplementation();
    imp.setSize(new Dimension3(2.0, 2.0, 2.0));
  }

  // ══════════════════════════════════════════════════════════════════════
  //  Compound operations
  // ══════════════════════════════════════════════════════════════════════

  @Test
  public void textModel_compound_setAppendInsertDelete() {
    STextModel model = new STextModel();
    TextModelImp imp = model.getImplementation();
    imp.setValue("Hello");
    imp.append(" World");
    assertEquals("Hello World", imp.getValue());
    imp.insert(5, ",");
    assertEquals("Hello, World", imp.getValue());
    imp.delete(5, 7);
    assertEquals("HelloWorld", imp.getValue());
    imp.replace(5, 10, " Java");
    assertEquals("Hello Java", imp.getValue());
  }

  @Test
  public void textModel_sgCompositeNotNull() {
    STextModel model = new STextModel();
    assertNotNull(model.getImplementation().getSgComposite());
  }
}
