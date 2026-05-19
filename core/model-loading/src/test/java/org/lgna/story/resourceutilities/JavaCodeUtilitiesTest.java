package org.lgna.story.resourceutilities;

import org.junit.Test;
import java.io.File;
import static org.junit.Assert.*;

public class JavaCodeUtilitiesTest {

  @Test
  public void lineReturnConstant() {
    assertEquals("\r\n", JavaCodeUtilities.LINE_RETURN);
  }

  @Test
  public void getCopyrightComment_isNotNull() {
    String comment = JavaCodeUtilities.getCopyrightComment();
    assertNotNull(comment);
  }

  @Test
  public void getCopyrightComment_isNotEmpty() {
    String comment = JavaCodeUtilities.getCopyrightComment();
    assertFalse(comment.isEmpty());
  }

  @Test
  public void getCopyrightComment_startsWithBlockCommentOpen() {
    String comment = JavaCodeUtilities.getCopyrightComment();
    assertTrue(comment.startsWith("/*"));
  }

  @Test
  public void getCopyrightComment_endsWithBlockCommentClose() {
    String comment = JavaCodeUtilities.getCopyrightComment();
    assertTrue(comment.contains("*/"));
  }

  @Test
  public void getCopyrightComment_containsCopyright() {
    String comment = JavaCodeUtilities.getCopyrightComment();
    assertTrue(comment.contains("Copyright"));
  }

  @Test
  public void getCopyrightComment_containsCarnegieMellon() {
    String comment = JavaCodeUtilities.getCopyrightComment();
    assertTrue(comment.contains("Carnegie Mellon"));
  }

  @Test
  public void getCopyrightComment_withCustomLineEnding() {
    String comment = JavaCodeUtilities.getCopyrightComment("\n");
    assertNotNull(comment);
    assertTrue(comment.startsWith("/*"));
    assertTrue(comment.contains("Copyright"));
  }

  @Test
  public void getCopyrightComment_isCached() {
    String first = JavaCodeUtilities.getCopyrightComment();
    String second = JavaCodeUtilities.getCopyrightComment();
    assertSame(first, second);
  }

  @Test
  public void getDirectoryStringForPackage_singleSegment() {
    String result = JavaCodeUtilities.getDirectoryStringForPackage("org");
    assertEquals("org" + File.separator, result);
  }

  @Test
  public void getDirectoryStringForPackage_multipleSegments() {
    String result = JavaCodeUtilities.getDirectoryStringForPackage("org.lgna.story");
    assertEquals("org" + File.separator + "lgna" + File.separator + "story" + File.separator, result);
  }

  @Test
  public void getDirectoryStringForPackage_twoSegments() {
    String result = JavaCodeUtilities.getDirectoryStringForPackage("com.example");
    assertEquals("com" + File.separator + "example" + File.separator, result);
  }
}
