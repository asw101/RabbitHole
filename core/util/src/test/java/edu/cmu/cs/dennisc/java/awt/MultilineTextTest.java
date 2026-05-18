package edu.cmu.cs.dennisc.java.awt;

import org.junit.Test;

import java.lang.reflect.Field;

import static org.junit.Assert.*;

public class MultilineTextTest {

  private static String[] getParagraphs(MultilineText multilineText) throws Exception {
    Field field = MultilineText.class.getDeclaredField("paragraphs");
    field.setAccessible(true);
    return (String[]) field.get(multilineText);
  }

  @Test
  public void constructor_storesOriginalText() {
    MultilineText multilineText = new MultilineText("alpha\nbeta");

    assertEquals("alpha\nbeta", multilineText.getText());
  }

  @Test(expected = AssertionError.class)
  public void constructor_nullTextThrowsAssertionError() {
    new MultilineText(null);
  }

  @Test
  public void constructor_emptyStringProducesSingleEmptyParagraph() throws Exception {
    MultilineText multilineText = new MultilineText("");

    assertArrayEquals(new String[] { "" }, getParagraphs(multilineText));
  }

  @Test
  public void constructor_singleLineProducesSingleParagraph() throws Exception {
    MultilineText multilineText = new MultilineText("single line");

    assertArrayEquals(new String[] { "single line" }, getParagraphs(multilineText));
  }

  @Test
  public void constructor_splitsUnixNewlines() throws Exception {
    MultilineText multilineText = new MultilineText("one\ntwo\nthree");

    assertArrayEquals(new String[] { "one", "two", "three" }, getParagraphs(multilineText));
  }

  @Test
  public void constructor_splitsWindowsNewlines() throws Exception {
    MultilineText multilineText = new MultilineText("one\r\ntwo\r\nthree");

    assertArrayEquals(new String[] { "one", "two", "three" }, getParagraphs(multilineText));
  }

  @Test
  public void constructor_splitsClassicMacNewlines() throws Exception {
    MultilineText multilineText = new MultilineText("one\rtwo\rthree");

    assertArrayEquals(new String[] { "one", "two", "three" }, getParagraphs(multilineText));
  }

  @Test
  public void constructor_splitsMixedNewlines() throws Exception {
    MultilineText multilineText = new MultilineText("one\r\ntwo\nthree\rfour");

    assertArrayEquals(new String[] { "one", "two", "three", "four" }, getParagraphs(multilineText));
  }

  @Test
  public void constructor_preservesInteriorBlankParagraphs() throws Exception {
    MultilineText multilineText = new MultilineText("one\n\nthree");

    assertArrayEquals(new String[] { "one", "", "three" }, getParagraphs(multilineText));
  }

  @Test
  public void constructor_preservesLeadingBlankParagraph() throws Exception {
    MultilineText multilineText = new MultilineText("\nvalue");

    assertArrayEquals(new String[] { "", "value" }, getParagraphs(multilineText));
  }

  @Test
  public void constructor_dropsTrailingBlankParagraphFromSplit() throws Exception {
    MultilineText multilineText = new MultilineText("value\n");

    assertArrayEquals(new String[] { "value" }, getParagraphs(multilineText));
  }
}
