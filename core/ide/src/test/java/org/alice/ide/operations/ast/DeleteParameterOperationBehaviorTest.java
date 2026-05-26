package org.alice.ide.operations.ast;

import edu.cmu.cs.dennisc.pattern.Criterion;
import org.alice.ide.IDE;
import org.alice.ide.ProjectDocumentFrame;
import org.alice.ide.cascade.ExpressionCascadeManager;
import org.alice.ide.croquet.edits.ast.DeleteParameterEdit;
import org.alice.ide.frametitle.IdeFrameTitleGenerator;
import org.alice.ide.sceneeditor.AbstractSceneEditor;
import org.alice.ide.testing.TestIdeBootstrap;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.lgna.croquet.Application;
import org.lgna.croquet.Operation;
import org.lgna.croquet.history.UserActivity;
import org.lgna.project.ast.AbstractMethod;
import org.lgna.project.ast.BlockStatement;
import org.lgna.project.ast.Declaration;
import org.lgna.project.ast.IntegerLiteral;
import org.lgna.project.ast.MethodInvocation;
import org.lgna.project.ast.NamedUserType;
import org.lgna.project.ast.NodeListProperty;
import org.lgna.project.ast.NullLiteral;
import org.lgna.project.ast.SimpleArgument;
import org.lgna.project.ast.SimpleArgumentListProperty;
import org.lgna.project.ast.StringLiteral;
import org.lgna.project.ast.UserCode;
import org.lgna.project.ast.UserMethod;
import org.lgna.project.ast.UserParameter;
import org.lgna.project.virtualmachine.VirtualMachine;
import sun.misc.Unsafe;

import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class DeleteParameterOperationBehaviorTest {
  private static final class TestIde extends IDE {
    private List<SimpleArgumentListProperty> argumentLists;
    private List<MethodInvocation> methodInvocations;

    private TestIde() {
      super(null, null);
    }

    static TestIde create(List<SimpleArgumentListProperty> argumentLists, List<MethodInvocation> methodInvocations) {
      try {
        Field unsafeField = Unsafe.class.getDeclaredField("theUnsafe");
        unsafeField.setAccessible(true);
        Unsafe unsafe = (Unsafe) unsafeField.get(null);
        TestIde ide = (TestIde) unsafe.allocateInstance(TestIde.class);
        ide.argumentLists = argumentLists;
        ide.methodInvocations = methodInvocations;
        return ide;
      } catch (ReflectiveOperationException roe) {
        throw new AssertionError(roe);
      }
    }

    @Override
    public List<SimpleArgumentListProperty> getArgumentLists(UserCode code) {
      return this.argumentLists;
    }

    @Override
    public List<MethodInvocation> getMethodInvocations(AbstractMethod method) {
      return this.methodInvocations;
    }

    @Override
    public ProjectDocumentFrame getDocumentFrame() {
      return null;
    }

    @Override
    public AbstractSceneEditor getSceneEditor() {
      return null;
    }

    @Override
    public UserMethod getPerformEditorGeneratedSetUpMethod() {
      return null;
    }

    @Override
    protected Criterion<Declaration> getDeclarationFilter() {
      return declaration -> true;
    }

    @Override
    public ExpressionCascadeManager getExpressionCascadeManager() {
      return null;
    }

    @Override
    protected void promptForLicenseAgreements() {
    }

    @Override
    protected void registerAdaptersForSceneEditorVm(VirtualMachine vm) {
    }

    @Override
    protected String getInnerCommentForMethodName(String methodName) {
      return null;
    }

    @Override
    public boolean isInstanceCreationAllowableFor(NamedUserType userType) {
      return true;
    }

    @Override
    protected IdeFrameTitleGenerator createFrameTitleGenerator() {
      return null;
    }

    @Override
    protected BufferedImage createThumbnail() {
      return null;
    }

    @Override
    public void forceProjectCodeUpToDate() {
    }

    @Override
    public void ensureProjectCodeUpToDate() {
    }

    @Override
    protected Operation getAboutOperation() {
      return null;
    }

    @Override
    protected void handleOpenFiles(List<File> files) {
    }

    @Override
    protected void handleWindowOpened(WindowEvent e) {
    }
  }

  private static final class TestDeleteParameterOperation extends DeleteParameterOperation {
    private final UserCode codeOverride;

    private TestDeleteParameterOperation(NodeListProperty<UserParameter> parametersProperty, UserParameter parameter, UserCode codeOverride) {
      super(parametersProperty, parameter);
      this.codeOverride = codeOverride;
    }

    private void invokePerform(UserActivity activity) {
      super.perform(activity);
    }

    @Override
    protected UserCode getCode() {
      return this.codeOverride != null ? this.codeOverride : super.getCode();
    }
  }

  private Application<?> previousApplication;
  private UserMethod method;
  private UserParameter keptParameter;
  private UserParameter deletedParameter;
  private MethodInvocation invocation;

  @Before
  public void setUp() {
    keptParameter = new UserParameter("kept", String.class);
    deletedParameter = new UserParameter("deleted", Integer.class);
    method = new UserMethod("sample", Object.class, new UserParameter[]{keptParameter, deletedParameter}, new BlockStatement());
    invocation = new MethodInvocation(new NullLiteral(), method,
        new SimpleArgument(keptParameter, new StringLiteral("alpha")),
        new SimpleArgument(deletedParameter, new IntegerLiteral(5)));

    previousApplication = Application.getActiveInstance();
    TestIdeBootstrap.setActiveApplication(TestIde.create(Collections.singletonList(invocation.requiredArguments), Collections.emptyList()));
  }

  @After
  public void tearDown() {
    TestIdeBootstrap.setActiveApplication(previousApplication);
  }

  @Test
  public void perform_withoutAccessesOrInvocations_commitsDeleteEditAndRemovesTrackedArgument() {
    TestDeleteParameterOperation operation = new TestDeleteParameterOperation(method.getRequiredParamtersProperty(), deletedParameter, null);
    UserActivity activity = new UserActivity();

    operation.invokePerform(activity);

    assertFalse(activity.isCanceled());
    assertTrue(activity.isSuccessfullyCompleted());
    assertTrue(activity.getEdit() instanceof DeleteParameterEdit);
    assertEquals(1, method.requiredParameters.size());
    assertSame(keptParameter, method.requiredParameters.get(0));
    assertEquals(1, invocation.requiredArguments.size());
    assertSame(keptParameter, invocation.requiredArguments.get(0).parameter.getValue());
  }

  @Test
  public void committedEdit_canUndoDeletedParameterAndArgument() {
    TestDeleteParameterOperation operation = new TestDeleteParameterOperation(method.getRequiredParamtersProperty(), deletedParameter, null);
    UserActivity activity = new UserActivity();

    operation.invokePerform(activity);
    activity.getEdit().undo();

    assertEquals(2, method.requiredParameters.size());
    assertSame(deletedParameter, method.requiredParameters.get(1));
    assertEquals(2, invocation.requiredArguments.size());
    assertSame(deletedParameter, invocation.requiredArguments.get(1).parameter.getValue());
  }

  @Test
  public void perform_whenParameterIsNotRequired_cancelsWithoutMutation() {
    UserParameter missingParameter = new UserParameter("missing", Double.class);
    TestDeleteParameterOperation operation = new TestDeleteParameterOperation(method.getRequiredParamtersProperty(), missingParameter, method);
    UserActivity activity = new UserActivity();

    operation.invokePerform(activity);

    assertTrue(activity.isCanceled());
    assertNull(activity.getEdit());
    assertEquals(2, method.requiredParameters.size());
    assertEquals(2, invocation.requiredArguments.size());
  }
}
