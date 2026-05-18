package org.alice.ide.croquet.models.help;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Structural checks on help composite classes.
 * Enum contracts for BugSubmitVisibility/BugSubmitAttachment are in
 * {@link BugSubmitVisibilityTest} and {@link BugSubmitAttachmentTest}.
 */
public class HelpUtilitiesTest {

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
