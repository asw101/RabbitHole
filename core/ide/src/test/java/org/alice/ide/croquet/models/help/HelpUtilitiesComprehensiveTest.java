package org.alice.ide.croquet.models.help;

import edu.cmu.cs.dennisc.issue.IssueType;
import edu.cmu.cs.dennisc.jira.JIRAReport;
import org.junit.Test;
import org.lgna.croquet.LaunchOperationUnadornedDialogCoreComposite;
import org.lgna.croquet.LazyOperationUnadornedDialogCoreComposite;
import org.lgna.croquet.Operation;
import org.lgna.croquet.SimpleOperationUnadornedDialogCoreComposite;
import org.lgna.croquet.StringState;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

public class HelpUtilitiesComprehensiveTest {

  @Test
  public void bugSubmitVisibility_isEnumWithTwoValues() {
    assertTrue(BugSubmitVisibility.class.isEnum());
    assertEquals(2, BugSubmitVisibility.values().length);
  }

  @Test
  public void bugSubmitVisibility_valueOrderMatchesSource() {
    assertEquals(BugSubmitVisibility.PUBLIC, BugSubmitVisibility.values()[0]);
    assertEquals(BugSubmitVisibility.PRIVATE, BugSubmitVisibility.values()[1]);
  }

  @Test
  public void bugSubmitVisibility_valueOfResolvesBothValues() {
    assertEquals(BugSubmitVisibility.PUBLIC, BugSubmitVisibility.valueOf("PUBLIC"));
    assertEquals(BugSubmitVisibility.PRIVATE, BugSubmitVisibility.valueOf("PRIVATE"));
  }

  @Test
  public void bugSubmitAttachment_isEnumWithTwoValues() {
    assertTrue(BugSubmitAttachment.class.isEnum());
    assertEquals(2, BugSubmitAttachment.values().length);
  }

  @Test
  public void bugSubmitAttachment_valueOrderMatchesSource() {
    assertEquals(BugSubmitAttachment.YES, BugSubmitAttachment.values()[0]);
    assertEquals(BugSubmitAttachment.NO, BugSubmitAttachment.values()[1]);
  }

  @Test
  public void bugSubmitAttachment_valueOfResolvesBothValues() {
    assertEquals(BugSubmitAttachment.YES, BugSubmitAttachment.valueOf("YES"));
    assertEquals(BugSubmitAttachment.NO, BugSubmitAttachment.valueOf("NO"));
  }

  @Test
  public void abstractIssueComposite_isPublicAbstractLaunchComposite() {
    assertTrue(Modifier.isPublic(AbstractIssueComposite.class.getModifiers()));
    assertTrue(Modifier.isAbstract(AbstractIssueComposite.class.getModifiers()));
    assertTrue(LaunchOperationUnadornedDialogCoreComposite.class.isAssignableFrom(AbstractIssueComposite.class));
  }

  @Test
  public void abstractIssueComposite_declaresPublicStaticFinalIssueGroup() throws Exception {
    Field field = AbstractIssueComposite.class.getField("ISSUE_GROUP");
    assertTrue(Modifier.isPublic(field.getModifiers()));
    assertTrue(Modifier.isStatic(field.getModifiers()));
    assertTrue(Modifier.isFinal(field.getModifiers()));
    assertNotNull(field.get(null));
  }

  @Test
  public void abstractIssueComposite_declaresExpectedPublicAccessors() throws Exception {
    assertEquals(StringState.class, AbstractIssueComposite.class.getMethod("getStepsState").getReturnType());
    assertEquals(StringState.class, AbstractIssueComposite.class.getMethod("getEnvironmentState").getReturnType());
    assertEquals(Operation.class, AbstractIssueComposite.class.getMethod("getSubmitBugOperation").getReturnType());
    assertEquals(JIRAReport.class, AbstractIssueComposite.class.getMethod("generateIssue").getReturnType());
  }

  @Test
  public void abstractIssueComposite_declaresExpectedProtectedAbstractHooks() throws Exception {
    assertProtectedAbstract("isClearedToSubmitBug");
    assertProtectedAbstract("isPublic");
    assertProtectedAbstract("getReportType");
    assertProtectedAbstract("getSummaryText");
    assertProtectedAbstract("getDescriptionText");
    assertProtectedAbstract("getThread");
    assertProtectedAbstract("getThrowable");
    assertProtectedAbstract("isProjectAttachmentDesired");
  }

  @Test
  public void abstractIssueComposite_declaresProtectedGoldenRatioPolicyOverride() throws Exception {
    Method method = AbstractIssueComposite.class.getDeclaredMethod("getGoldenRatioPolicy");
    assertTrue(Modifier.isProtected(method.getModifiers()));
    assertEquals("GoldenRatioPolicy", method.getReturnType().getSimpleName());
    assertEquals("org.lgna.croquet.AbstractWindowComposite$GoldenRatioPolicy", method.getReturnType().getName());
  }

  @Test
  public void reportIssueComposite_isFinalConcreteSubclass() {
    assertTrue(Modifier.isFinal(ReportIssueComposite.class.getModifiers()));
    assertTrue(AbstractIssueComposite.class.isAssignableFrom(ReportIssueComposite.class));
  }

  @Test
  public void reportIssueComposite_defaultConstructorIsPublic() {
    assertEquals(1, ReportIssueComposite.class.getConstructors().length);
    assertTrue(Modifier.isPublic(ReportIssueComposite.class.getConstructors()[0].getModifiers()));
  }

  @Test
  public void reportIssueComposite_exposesExpectedStateAccessors() throws Exception {
    assertNotNull(ReportIssueComposite.class.getMethod("getVisibilityState"));
    assertNotNull(ReportIssueComposite.class.getMethod("getReportTypeState"));
    assertNotNull(ReportIssueComposite.class.getMethod("getSummaryState"));
    assertNotNull(ReportIssueComposite.class.getMethod("getDescriptionState"));
    assertNotNull(ReportIssueComposite.class.getMethod("getAttachmentState"));
  }

  @Test
  public void reportIssueComposite_exposesExpectedOperations() throws Exception {
    assertEquals(Operation.class, ReportIssueComposite.class.getMethod("getBrowserOperation").getReturnType());
    assertEquals(Operation.class, ReportIssueComposite.class.getMethod("getReportBugLaunchOperation").getReturnType());
  }

  @Test
  public void reportIssueComposite_canBeInstantiatedSafely() {
    ReportIssueComposite composite = new ReportIssueComposite();
    assertNotNull(composite);
  }

  @Test
  public void reportIssueComposite_initialStatesAreAvailable() {
    ReportIssueComposite composite = new ReportIssueComposite();
    assertNotNull(composite.getVisibilityState());
    assertNotNull(composite.getReportTypeState());
    assertNotNull(composite.getSummaryState());
    assertNotNull(composite.getDescriptionState());
    assertNotNull(composite.getAttachmentState());
  }

  @Test
  public void reportIssueComposite_operationsAreAvailable() {
    ReportIssueComposite composite = new ReportIssueComposite();
    assertNotNull(composite.getBrowserOperation());
    assertNotNull(composite.getReportBugLaunchOperation());
    assertNotNull(composite.getSubmitBugOperation());
  }

  @Test
  public void graphicsHelpComposite_isPublicConcreteLazyComposite() {
    assertTrue(Modifier.isPublic(GraphicsHelpComposite.class.getModifiers()));
    assertFalse(Modifier.isAbstract(GraphicsHelpComposite.class.getModifiers()));
    assertTrue(LazyOperationUnadornedDialogCoreComposite.class.isAssignableFrom(GraphicsHelpComposite.class));
  }

  @Test
  public void graphicsHelpComposite_hasPublicNoArgConstructor() {
    Constructor<?> constructor = GraphicsHelpComposite.class.getConstructors()[0];
    assertTrue(Modifier.isPublic(constructor.getModifiers()));
    assertEquals(0, constructor.getParameterTypes().length);
  }

  @Test
  public void showPathPropertyComposite_isAbstractSimpleComposite() {
    assertTrue(Modifier.isAbstract(ShowPathPropertyComposite.class.getModifiers()));
    assertTrue(SimpleOperationUnadornedDialogCoreComposite.class.isAssignableFrom(ShowPathPropertyComposite.class));
  }

  @Test
  public void showPathPropertyComposite_declaresPrivateFinalPropertyNameField() throws Exception {
    Field field = ShowPathPropertyComposite.class.getDeclaredField("propertyName");
    assertTrue(Modifier.isPrivate(field.getModifiers()));
    assertTrue(Modifier.isFinal(field.getModifiers()));
  }

  @Test
  public void showPathPropertyComposite_declaresGetPropertyNameAccessor() throws Exception {
    Method method = ShowPathPropertyComposite.class.getMethod("getPropertyName");
    assertEquals(String.class, method.getReturnType());
    assertTrue(Modifier.isPublic(method.getModifiers()));
  }

  @Test
  public void helpRelatedClasses_areLoadable() throws Exception {
    Class.forName("org.alice.ide.croquet.models.help.ShowAllSystemPropertiesComposite");
    Class.forName("org.alice.ide.croquet.models.help.ShowClassPathPropertyComposite");
    Class.forName("org.alice.ide.croquet.models.help.ShowLibraryPathPropertyComposite");
  }

  @Test
  public void reportTypeState_isBackedByIssueTypeEnum() {
    ReportIssueComposite composite = new ReportIssueComposite();
    assertNotNull(composite.getReportTypeState());
    assertNull(composite.getReportTypeState().getValue());
  }

  private static void assertProtectedAbstract(String methodName) throws Exception {
    Method method = AbstractIssueComposite.class.getDeclaredMethod(methodName);
    assertTrue(Modifier.isProtected(method.getModifiers()));
    assertTrue(Modifier.isAbstract(method.getModifiers()));
  }
}
