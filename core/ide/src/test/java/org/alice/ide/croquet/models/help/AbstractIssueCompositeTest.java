package org.alice.ide.croquet.models.help;

import org.junit.Test;
import org.lgna.croquet.Group;

import java.util.UUID;

import static org.junit.Assert.*;

/**
 * Tests for {@link AbstractIssueComposite} — abstract base for issue reporting composites.
 * Covers ISSUE_GROUP constant and class structure.
 * Cannot instantiate directly (abstract + requires croquet Application context),
 * so tests focus on accessible statics and structural contracts.
 */
public class AbstractIssueCompositeTest {

  // ---- ISSUE_GROUP constant ----

  @Test
  public void issueGroup_isNotNull() {
    assertNotNull(AbstractIssueComposite.ISSUE_GROUP);
  }

  @Test
  public void issueGroup_isInstanceOfGroup() {
    assertTrue(AbstractIssueComposite.ISSUE_GROUP instanceof Group);
  }

  @Test
  public void issueGroup_hasStableId() {
    Group group = AbstractIssueComposite.ISSUE_GROUP;
    // The ISSUE_GROUP is a constant, verify it's accessible and consistent
    assertNotNull(group);
    // Same reference on repeated access
    assertSame(AbstractIssueComposite.ISSUE_GROUP, AbstractIssueComposite.ISSUE_GROUP);
  }

  @Test
  public void issueGroup_returnsConsistentReference() {
    assertSame(AbstractIssueComposite.ISSUE_GROUP, AbstractIssueComposite.ISSUE_GROUP);
  }

  // ---- class structure ----

  @Test
  public void isAbstract() {
    assertTrue(java.lang.reflect.Modifier.isAbstract(AbstractIssueComposite.class.getModifiers()));
  }

  @Test
  public void classLoadable() throws ClassNotFoundException {
    Class.forName("org.alice.ide.croquet.models.help.AbstractIssueComposite");
  }

  @Test
  public void hasConstructor() throws Exception {
    java.lang.reflect.Constructor<?>[] ctors = AbstractIssueComposite.class.getDeclaredConstructors();
    boolean found = false;
    for (java.lang.reflect.Constructor<?> ctor : ctors) {
      Class<?>[] params = ctor.getParameterTypes();
      if (params.length == 2 && params[0] == UUID.class) {
        found = true;
        break;
      }
    }
    assertTrue("Should have a constructor taking (UUID, IsModal)", found);
  }

  // ---- method existence ----

  @Test
  public void hasGetStepsStateMethod() throws NoSuchMethodException {
    assertNotNull(AbstractIssueComposite.class.getMethod("getStepsState"));
  }

  @Test
  public void hasGetEnvironmentStateMethod() throws NoSuchMethodException {
    assertNotNull(AbstractIssueComposite.class.getMethod("getEnvironmentState"));
  }

  @Test
  public void hasGetSubmitBugOperationMethod() throws NoSuchMethodException {
    assertNotNull(AbstractIssueComposite.class.getMethod("getSubmitBugOperation"));
  }

  @Test
  public void hasGenerateIssueMethod() throws NoSuchMethodException {
    assertNotNull(AbstractIssueComposite.class.getMethod("generateIssue"));
  }

  // ---- abstract method contracts ----

  @Test
  public void isClearedToSubmitBug_isAbstract() throws NoSuchMethodException {
    assertTrue(java.lang.reflect.Modifier.isAbstract(
        AbstractIssueComposite.class.getDeclaredMethod("isClearedToSubmitBug").getModifiers()));
  }

  @Test
  public void isPublic_isAbstract() throws NoSuchMethodException {
    assertTrue(java.lang.reflect.Modifier.isAbstract(
        AbstractIssueComposite.class.getDeclaredMethod("isPublic").getModifiers()));
  }

  @Test
  public void getReportType_isAbstract() throws NoSuchMethodException {
    assertTrue(java.lang.reflect.Modifier.isAbstract(
        AbstractIssueComposite.class.getDeclaredMethod("getReportType").getModifiers()));
  }

  @Test
  public void getSummaryText_isAbstract() throws NoSuchMethodException {
    assertTrue(java.lang.reflect.Modifier.isAbstract(
        AbstractIssueComposite.class.getDeclaredMethod("getSummaryText").getModifiers()));
  }

  @Test
  public void getDescriptionText_isAbstract() throws NoSuchMethodException {
    assertTrue(java.lang.reflect.Modifier.isAbstract(
        AbstractIssueComposite.class.getDeclaredMethod("getDescriptionText").getModifiers()));
  }

  @Test
  public void getThread_isAbstract() throws NoSuchMethodException {
    assertTrue(java.lang.reflect.Modifier.isAbstract(
        AbstractIssueComposite.class.getDeclaredMethod("getThread").getModifiers()));
  }

  @Test
  public void getThrowable_isAbstract() throws NoSuchMethodException {
    assertTrue(java.lang.reflect.Modifier.isAbstract(
        AbstractIssueComposite.class.getDeclaredMethod("getThrowable").getModifiers()));
  }

  @Test
  public void isProjectAttachmentDesired_isAbstract() throws NoSuchMethodException {
    assertTrue(java.lang.reflect.Modifier.isAbstract(
        AbstractIssueComposite.class.getDeclaredMethod("isProjectAttachmentDesired").getModifiers()));
  }

  // ---- BugSubmitAttachment enum ----

  @Test
  public void bugSubmitAttachment_values() {
    assertEquals(2, BugSubmitAttachment.values().length);
    assertEquals(BugSubmitAttachment.YES, BugSubmitAttachment.values()[0]);
    assertEquals(BugSubmitAttachment.NO, BugSubmitAttachment.values()[1]);
  }

  @Test
  public void bugSubmitAttachment_valueOf() {
    assertEquals(BugSubmitAttachment.YES, BugSubmitAttachment.valueOf("YES"));
    assertEquals(BugSubmitAttachment.NO, BugSubmitAttachment.valueOf("NO"));
  }

  // ---- BugSubmitVisibility enum ----

  @Test
  public void bugSubmitVisibility_values() {
    assertEquals(2, BugSubmitVisibility.values().length);
    assertEquals(BugSubmitVisibility.PUBLIC, BugSubmitVisibility.values()[0]);
    assertEquals(BugSubmitVisibility.PRIVATE, BugSubmitVisibility.values()[1]);
  }

  @Test
  public void bugSubmitVisibility_valueOf() {
    assertEquals(BugSubmitVisibility.PUBLIC, BugSubmitVisibility.valueOf("PUBLIC"));
    assertEquals(BugSubmitVisibility.PRIVATE, BugSubmitVisibility.valueOf("PRIVATE"));
  }
}
