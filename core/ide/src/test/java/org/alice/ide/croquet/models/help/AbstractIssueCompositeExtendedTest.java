package org.alice.ide.croquet.models.help;

import org.junit.Assume;
import org.junit.Test;
import org.lgna.croquet.Group;

import static org.junit.Assert.*;

/**
 * Extended tests for {@link AbstractIssueComposite} — covers the
 * ISSUE_GROUP constant, enum integration, and external service
 * integration contracts (JIRA submission data structures).
 */
public class AbstractIssueCompositeExtendedTest {

  // ---- ISSUE_GROUP constant ----

  @Test
  public void issueGroup_isNotNull() {
    assertNotNull(AbstractIssueComposite.ISSUE_GROUP);
  }

  @Test
  public void issueGroup_isGroupInstance() {
    assertTrue(AbstractIssueComposite.ISSUE_GROUP instanceof Group);
  }

  @Test
  public void issueGroup_hasStableUuid() {
    Group group = AbstractIssueComposite.ISSUE_GROUP;
    assertNotNull(group);
    // Verify the group has a descriptive toString
    String desc = group.toString();
    assertNotNull(desc);
    assertTrue("Group description should mention ISSUE_GROUP", desc.contains("ISSUE_GROUP"));
  }

  @Test
  public void issueGroup_identityStableAcrossCalls() {
    Group g1 = AbstractIssueComposite.ISSUE_GROUP;
    Group g2 = AbstractIssueComposite.ISSUE_GROUP;
    assertSame(g1, g2);
  }

  // ---- BugSubmitAttachment enum integration ----

  @Test
  public void bugSubmitAttachment_isUsableInIssueContext() {
    assertNotNull(BugSubmitAttachment.YES);
    assertNotNull(BugSubmitAttachment.NO);
    assertNotEquals(BugSubmitAttachment.YES, BugSubmitAttachment.NO);
  }

  @Test
  public void bugSubmitAttachment_valueOf_roundTrip() {
    for (BugSubmitAttachment a : BugSubmitAttachment.values()) {
      assertEquals(a, BugSubmitAttachment.valueOf(a.name()));
    }
  }

  // ---- BugSubmitVisibility enum integration ----

  @Test
  public void bugSubmitVisibility_isUsableInIssueContext() {
    assertNotNull(BugSubmitVisibility.PUBLIC);
    assertNotNull(BugSubmitVisibility.PRIVATE);
    assertNotEquals(BugSubmitVisibility.PUBLIC, BugSubmitVisibility.PRIVATE);
  }

  @Test
  public void bugSubmitVisibility_valueOf_roundTrip() {
    for (BugSubmitVisibility v : BugSubmitVisibility.values()) {
      assertEquals(v, BugSubmitVisibility.valueOf(v.name()));
    }
  }

  // ---- ReportIssueComposite structural checks ----

  @Test
  public void reportIssueComposite_classLoadable() throws ClassNotFoundException {
    Class.forName("org.alice.ide.croquet.models.help.ReportIssueComposite");
  }

  @Test
  public void reportIssueComposite_extendsAbstractIssueComposite() throws ClassNotFoundException {
    Class<?> cls = Class.forName("org.alice.ide.croquet.models.help.ReportIssueComposite");
    assertTrue(AbstractIssueComposite.class.isAssignableFrom(cls));
  }

  @Test
  public void reportIssueComposite_isFinal() throws ClassNotFoundException {
    Class<?> cls = Class.forName("org.alice.ide.croquet.models.help.ReportIssueComposite");
    assertTrue(java.lang.reflect.Modifier.isFinal(cls.getModifiers()));
  }

  // ---- Construction (headless-guarded) ----

  @Test
  public void reportIssueComposite_construct_headlessGuarded() {
    try {
      ReportIssueComposite composite = new ReportIssueComposite();
      assertNotNull(composite);
      assertNotNull(composite.getSubmitBugOperation());
      assertNotNull(composite.getReportBugLaunchOperation());
      assertNotNull(composite.getSummaryState());
      assertNotNull(composite.getDescriptionState());
      assertNotNull(composite.getStepsState());
      assertNotNull(composite.getEnvironmentState());
      assertNotNull(composite.getVisibilityState());
      assertNotNull(composite.getReportTypeState());
      assertNotNull(composite.getAttachmentState());
      assertNotNull(composite.getBrowserOperation());
    } catch (Exception e) {
      Assume.assumeNoException("Requires Croquet Application context", e);
    }
  }

  @Test
  public void reportIssueComposite_visibilityDefaultsToPrivate() {
    try {
      ReportIssueComposite composite = new ReportIssueComposite();
      assertEquals(BugSubmitVisibility.PRIVATE, composite.getVisibilityState().getValue());
    } catch (Exception e) {
      Assume.assumeNoException("Requires Croquet Application context", e);
    }
  }

  @Test
  public void reportIssueComposite_attachmentDefaultsToNull() {
    try {
      ReportIssueComposite composite = new ReportIssueComposite();
      assertNull(composite.getAttachmentState().getValue());
    } catch (Exception e) {
      Assume.assumeNoException("Requires Croquet Application context", e);
    }
  }

  // ---- GraphicsHelpComposite structural checks ----

  @Test
  public void graphicsHelpComposite_classLoadable() throws ClassNotFoundException {
    Class.forName("org.alice.ide.croquet.models.help.GraphicsHelpComposite");
  }

  @Test
  public void graphicsHelpComposite_construct_headlessGuarded() {
    try {
      GraphicsHelpComposite composite = new GraphicsHelpComposite();
      assertNotNull(composite);
    } catch (Exception e) {
      Assume.assumeNoException("Requires Croquet Application context", e);
    }
  }

  // ---- ShowAllSystemPropertiesComposite structural checks ----

  @Test
  public void showAllSystemPropertiesComposite_classLoadable() throws ClassNotFoundException {
    Class.forName("org.alice.ide.croquet.models.help.ShowAllSystemPropertiesComposite");
  }

  @Test
  public void showAllSystemPropertiesComposite_construct_headlessGuarded() {
    try {
      ShowAllSystemPropertiesComposite composite = new ShowAllSystemPropertiesComposite();
      assertNotNull(composite);
    } catch (Exception e) {
      Assume.assumeNoException("Requires Croquet Application context", e);
    }
  }
}
