package org.alice.ide.croquet.models.help;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests for help-related enums and utilities.
 * Covers BugSubmitVisibility, BugSubmitAttachment enums
 * and structural checks on help composites.
 */
public class HelpUtilitiesTest {

  // -- BugSubmitVisibility -----------------------------------------------

  @Test
  public void bugSubmitVisibility_hasPublicValue() {
    assertEquals(BugSubmitVisibility.PUBLIC, BugSubmitVisibility.valueOf("PUBLIC"));
  }

  @Test
  public void bugSubmitVisibility_hasPrivateValue() {
    assertEquals(BugSubmitVisibility.PRIVATE, BugSubmitVisibility.valueOf("PRIVATE"));
  }

  @Test
  public void bugSubmitVisibility_valueCount() {
    assertEquals(2, BugSubmitVisibility.values().length);
  }

  @Test
  public void bugSubmitVisibility_ordinals() {
    assertEquals(0, BugSubmitVisibility.PUBLIC.ordinal());
    assertEquals(1, BugSubmitVisibility.PRIVATE.ordinal());
  }

  // -- BugSubmitAttachment -----------------------------------------------

  @Test
  public void bugSubmitAttachment_hasYesValue() {
    assertEquals(BugSubmitAttachment.YES, BugSubmitAttachment.valueOf("YES"));
  }

  @Test
  public void bugSubmitAttachment_hasNoValue() {
    assertEquals(BugSubmitAttachment.NO, BugSubmitAttachment.valueOf("NO"));
  }

  @Test
  public void bugSubmitAttachment_valueCount() {
    assertEquals(2, BugSubmitAttachment.values().length);
  }

  @Test
  public void bugSubmitAttachment_ordinals() {
    assertEquals(0, BugSubmitAttachment.YES.ordinal());
    assertEquals(1, BugSubmitAttachment.NO.ordinal());
  }

  // -- Structural checks on composites -----------------------------------

  @Test
  public void showAllSystemPropertiesComposite_classLoadable() throws ClassNotFoundException {
    Class.forName("org.alice.ide.croquet.models.help.ShowAllSystemPropertiesComposite");
  }

  @Test
  public void reportIssueComposite_classLoadable() throws ClassNotFoundException {
    Class.forName("org.alice.ide.croquet.models.help.ReportIssueComposite");
  }

  @Test
  public void graphicsHelpComposite_classLoadable() throws ClassNotFoundException {
    Class.forName("org.alice.ide.croquet.models.help.GraphicsHelpComposite");
  }

  @Test
  public void showPathPropertyComposite_classLoadable() throws ClassNotFoundException {
    Class.forName("org.alice.ide.croquet.models.help.ShowPathPropertyComposite");
  }

  @Test
  public void showClassPathPropertyComposite_classLoadable() throws ClassNotFoundException {
    Class.forName("org.alice.ide.croquet.models.help.ShowClassPathPropertyComposite");
  }

  @Test
  public void showLibraryPathPropertyComposite_classLoadable() throws ClassNotFoundException {
    Class.forName("org.alice.ide.croquet.models.help.ShowLibraryPathPropertyComposite");
  }

  @Test
  public void abstractIssueComposite_classLoadable() throws ClassNotFoundException {
    Class.forName("org.alice.ide.croquet.models.help.AbstractIssueComposite");
  }
}
