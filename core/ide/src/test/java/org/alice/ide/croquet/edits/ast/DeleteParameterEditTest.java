package org.alice.ide.croquet.edits.ast;

import edu.cmu.cs.dennisc.pattern.Criterion;
import org.alice.ide.IDE;
import org.alice.ide.ProjectDocumentFrame;
import org.alice.ide.cascade.ExpressionCascadeManager;
import org.alice.ide.testing.TestIdeBootstrap;
import org.alice.ide.frametitle.IdeFrameTitleGenerator;
import org.alice.ide.sceneeditor.AbstractSceneEditor;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.lgna.croquet.*;
import org.lgna.croquet.history.UserActivity;
import org.lgna.project.ast.*;
import org.lgna.project.virtualmachine.VirtualMachine;
import sun.misc.Unsafe;

import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class DeleteParameterEditTest {
  private static class TestIde extends IDE {
    private List<SimpleArgumentListProperty> argumentLists;

    private TestIde() {
      super(null, null);
    }

    static TestIde create(List<SimpleArgumentListProperty> argumentLists) {
      try {
        Field unsafeField = Unsafe.class.getDeclaredField("theUnsafe");
        unsafeField.setAccessible(true);
        Unsafe unsafe = (Unsafe) unsafeField.get(null);
        TestIde ide = (TestIde) unsafe.allocateInstance(TestIde.class);
        ide.argumentLists = argumentLists;
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
    public ProjectDocumentFrame getDocumentFrame() {
      return TestIdeBootstrap.getDocumentFrame();
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

  private Application<?> previousApplication;
  private UserMethod method;
  private UserParameter keptParameter;
  private UserParameter deletedParameter;
  private MethodInvocation invocation;

  @Before
  public void setUp() {
    TestIdeBootstrap.ensureInstalled();
    keptParameter = new UserParameter("kept", String.class);
    deletedParameter = new UserParameter("deleted", Integer.class);
    method = new UserMethod("sample", Object.class, new UserParameter[]{keptParameter, deletedParameter}, new BlockStatement());
    invocation = new MethodInvocation(new NullLiteral(), method,
        new SimpleArgument(keptParameter, new StringLiteral("alpha")),
        new SimpleArgument(deletedParameter, new IntegerLiteral(5)));

    previousApplication = Application.getActiveInstance();
    TestIdeBootstrap.setActiveApplication(TestIde.create(Collections.singletonList(invocation.requiredArguments)));
  }

  @After
  public void tearDown() {
    TestIdeBootstrap.setActiveApplication(previousApplication);
    TestIdeBootstrap.reset();
  }

  @Test
  public void construct_withNullUserActivity_succeeds() {
    DeleteParameterEdit edit = new DeleteParameterEdit(null, method, deletedParameter);
    assertNotNull(edit);
  }

  @Test
  public void construct_isParameterEdit() {
    DeleteParameterEdit edit = new DeleteParameterEdit(null, method, deletedParameter);

    assertTrue(edit instanceof ParameterEdit);
  }

  @Test
  public void getCode_returnsConstructionMethod() {
    DeleteParameterEdit edit = new DeleteParameterEdit(null, method, deletedParameter);

    assertSame(method, edit.getCode());
  }

  @Test
  public void getParameter_returnsConstructionParameter() {
    DeleteParameterEdit edit = new DeleteParameterEdit(null, method, deletedParameter);

    assertSame(deletedParameter, edit.getParameter());
  }

  @Test
  public void doOrRedoInternal_whenDo_removesParameterFromRequiredParameters() {
    DeleteParameterEdit edit = new DeleteParameterEdit(null, method, deletedParameter);

    edit.doOrRedoInternal(true);

    assertEquals(1, method.requiredParameters.size());
    assertSame(keptParameter, method.requiredParameters.get(0));
  }

  @Test
  public void doOrRedoInternal_whenDo_removesTrackedArgumentAtOriginalIndex() {
    DeleteParameterEdit edit = new DeleteParameterEdit(null, method, deletedParameter);

    edit.doOrRedoInternal(true);

    assertEquals(1, invocation.requiredArguments.size());
    assertSame(keptParameter, invocation.requiredArguments.get(0).parameter.getValue());
  }

  @Test
  public void doOrRedoInternal_preservesLeadingParameterAndArgument() {
    DeleteParameterEdit edit = new DeleteParameterEdit(null, method, deletedParameter);

    edit.doOrRedoInternal(true);

    assertSame(keptParameter, method.requiredParameters.get(0));
    assertSame(keptParameter, invocation.requiredArguments.get(0).parameter.getValue());
  }

  @Test
  public void undoInternal_afterDo_restoresParameterAndArgumentAtSameIndex() {
    DeleteParameterEdit edit = new DeleteParameterEdit(null, method, deletedParameter);

    edit.doOrRedoInternal(true);
    edit.undoInternal();

    assertEquals(2, method.requiredParameters.size());
    assertSame(deletedParameter, method.requiredParameters.get(1));
    assertEquals(2, invocation.requiredArguments.size());
    assertSame(deletedParameter, invocation.requiredArguments.get(1).parameter.getValue());
  }

  @Test
  public void undoInternal_restoresSameRemovedArgumentInstance() {
    DeleteParameterEdit edit = new DeleteParameterEdit(null, method, deletedParameter);
    SimpleArgument removedArgument = invocation.requiredArguments.get(1);

    edit.doOrRedoInternal(true);
    edit.undoInternal();

    assertSame(removedArgument, invocation.requiredArguments.get(1));
  }

  @Test
  public void doUndoRedo_cycle_removesDeletedParameterAgain() {
    DeleteParameterEdit edit = new DeleteParameterEdit(null, method, deletedParameter);

    edit.doOrRedoInternal(true);
    edit.undoInternal();
    edit.doOrRedoInternal(false);

    assertEquals(1, method.requiredParameters.size());
    assertEquals(1, invocation.requiredArguments.size());
    assertSame(keptParameter, method.requiredParameters.get(0));
  }

  @Test
  public void canUndo_returnsTrue() {
    DeleteParameterEdit edit = new DeleteParameterEdit(null, method, deletedParameter);

    assertTrue(edit.canUndo());
  }

  @Test
  public void canRedo_returnsTrue() {
    DeleteParameterEdit edit = new DeleteParameterEdit(null, method, deletedParameter);

    assertTrue(edit.canRedo());
  }

  @Test
  public void appendDescription_viaTerseDescription_startsWithDeletePrefix() {
    DeleteParameterEdit edit = new DeleteParameterEdit(null, method, deletedParameter);

    assertTrue(edit.getTerseDescription().startsWith("delete:"));
  }

  @Test
  public void getUndoPresentation_containsDeletePrefix() {
    DeleteParameterEdit edit = new DeleteParameterEdit(null, method, deletedParameter);

    assertTrue(edit.getUndoPresentation().startsWith("Undo:delete:"));
  }
}
