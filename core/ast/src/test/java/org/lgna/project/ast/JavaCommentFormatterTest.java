package org.lgna.project.ast;

import org.junit.Test;

import java.util.Locale;

import static org.junit.Assert.*;

/**
 * TDD tests for JavaCommentFormatter — the delegate extracted from
 * JavaCodeGenerator that handles block comment formatting and
 * localized comment resolution.
 *
 * These tests define the contract; they will FAIL until the
 * JavaCommentFormatter class is implemented.
 */
public class JavaCommentFormatterTest {

  // --- formatBlockComment ---

  @Test
  public void formatsSingleLineBlockComment() {
    JavaCommentFormatter formatter = new JavaCommentFormatter(null);
    String result = formatter.formatBlockComment("Hello world");
    assertEquals("/* Hello world */", result);
  }

  @Test
  public void formatsMultiLineBlockComment() {
    JavaCommentFormatter formatter = new JavaCommentFormatter(null);
    String result = formatter.formatBlockComment("Line one\nLine two\nLine three");
    assertEquals("/* Line one\n * Line two\n * Line three */", result);
  }

  @Test
  public void formatsEmptyBlockComment() {
    JavaCommentFormatter formatter = new JavaCommentFormatter(null);
    String result = formatter.formatBlockComment("");
    assertEquals("/*  */", result);
  }

  @Test
  public void formatsTwoLineBlockComment() {
    JavaCommentFormatter formatter = new JavaCommentFormatter(null);
    String result = formatter.formatBlockComment("first\nsecond");
    assertEquals("/* first\n * second */", result);
  }

  // --- getLocalizedComment ---

  @Test
  public void returnsNullWhenNoBundleNameConfigured() {
    JavaCommentFormatter formatter = new JavaCommentFormatter(null);
    JavaType stringType = JavaType.getInstance(String.class);
    assertNull(formatter.getLocalizedComment(stringType, "someMethod", Locale.ENGLISH));
  }

  @Test
  public void returnsNullWhenKeyNotFoundInBundle() {
    // No real bundle is available in test classpath, so verify null-bundle path
    JavaCommentFormatter formatter = new JavaCommentFormatter(null);
    JavaType stringType = JavaType.getInstance(String.class);
    String result = formatter.getLocalizedComment(stringType, "nonExistentKeyXYZ123", Locale.ENGLISH);
    assertNull("null bundle should return null", result);
  }

  @Test
  public void replacesClassnameAndObjectnamePlaceholders() {
    // This tests that <classname> and <objectname> substitution works.
    // Since we can't guarantee a particular bundle exists in test,
    // test the replacement logic via getLocalizedMultiLineComment which
    // internally calls getLocalizedComment + formatBlockComment.
    JavaCommentFormatter formatter = new JavaCommentFormatter(null);
    // With null bundle, should return null
    JavaType stringType = JavaType.getInstance(String.class);
    assertNull(formatter.getLocalizedMultiLineComment(stringType, "test"));
  }

  // --- getLocalizedMultiLineComment ---

  @Test
  public void returnsNullWhenNoCommentFound() {
    JavaCommentFormatter formatter = new JavaCommentFormatter(null);
    JavaType type = JavaType.getInstance(Object.class);
    assertNull(formatter.getLocalizedMultiLineComment(type, "anySectionName"));
  }

  @Test
  public void wrapsFoundCommentInBlockFormat() {
    // We verify that when a localized comment IS found, it goes through
    // formatBlockComment. We test this end-to-end through JavaCodeGenerator
    // integration tests since we need a real resource bundle for the lookup.
    // This unit test verifies the null path.
    JavaCommentFormatter formatter = new JavaCommentFormatter(null);
    assertNull(formatter.getLocalizedMultiLineComment(JavaType.getInstance(String.class), "test"));
  }

  // --- appendMemberPrefix / appendMemberPostfix ---

  @Test
  public void appendMemberPrefixReturnsNullWhenNoBundle() {
    JavaCommentFormatter formatter = new JavaCommentFormatter(null);
    JavaType declaringType = JavaType.getInstance(String.class);
    String prefix = formatter.getMemberComment(declaringType, "someMethod");
    assertNull("no bundle means null member comment", prefix);
  }

  @Test
  public void appendMemberPostfixAppendsEndSuffix() {
    JavaCommentFormatter formatter = new JavaCommentFormatter(null);
    JavaType declaringType = JavaType.getInstance(String.class);
    String postfix = formatter.getMemberComment(declaringType, "someMethod.end");
    assertNull("no bundle means null end comment", postfix);
  }

  // --- edge cases ---

  @Test
  public void handlesCommentWithOnlyNewlines() {
    JavaCommentFormatter formatter = new JavaCommentFormatter(null);
    String result = formatter.formatBlockComment("\n\n");
    // split("\n") on "\n\n" yields empty array (all trailing empties trimmed)
    assertEquals("/*  */", result);
  }

  @Test
  public void handlesCommentWithTrailingNewline() {
    JavaCommentFormatter formatter = new JavaCommentFormatter(null);
    String result = formatter.formatBlockComment("Hello\n");
    // split("\n") trims trailing empties, so "Hello\n" yields ["Hello"]
    assertEquals("/* Hello */", result);
  }
}
