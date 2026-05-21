package org.alice.ide.croquet.edits.ast;

import edu.cmu.cs.dennisc.pattern.Criterion;
import org.alice.ide.IDE;
import org.alice.ide.cascade.ExpressionCascadeManager;
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

public class AddParameterEditTest {
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
  private UserParameter existingParameter;
  private UserParameter addedParameter;
  private MethodInvocation invocation;

  private static void setActiveApplication(Application<?> application) {
    try {
      Field field = Application.class.getDeclaredField("singleton");
      field.setAccessible(true);
      field.set(null, application);
    } catch (ReflectiveOperationException roe) {
      throw new AssertionError(roe);
    }
  }

  @Before
  public void setUp() {
    existingParameter = new UserParameter("existing", String.class);
    addedParameter = new UserParameter("added", Integer.class);
    method = new UserMethod("sample", Object.class, new UserParameter[]{existingParameter}, new BlockStatement());
    invocation = new MethodInvocation(new NullLiteral(), method,
        new SimpleArgument(existingParameter, new StringLiteral("alpha")));

    previousApplication = Application.getActiveInstance();
    setActiveApplication(TestIde.create(Collections.singletonList(invocation.requiredArguments)));
  }

  @After
  public void tearDown() {
    setActiveApplication(previousApplication);
  }

  @Test
  public void construct_withNullUserActivity_succeeds() {
    AddParameterEdit edit = new AddParameterEdit(null, method, addedParameter);
    assertNotNull(edit);
  }

  @Test
  public void construct_isParameterEdit() {
    AddParameterEdit edit = new AddParameterEdit(null, method, addedParameter);

    assertTrue(edit instanceof ParameterEdit);
  }

  @Test
  public void getCode_returnsConstructionMethod() {
    AddParameterEdit edit = new AddParameterEdit(null, method, addedParameter);

    assertSame(method, edit.getCode());
  }

  @Test
  public void getParameter_returnsConstructionParameter() {
    AddParameterEdit edit = new AddParameterEdit(null, method, addedParameter);

    assertSame(addedParameter, edit.getParameter());
  }

  @Test
  public void doOrRedoInternal_whenDo_appendsParameterToRequiredParameters() {
    AddParameterEdit edit = new AddParameterEdit(null, method, addedParameter);

    edit.doOrRedoInternal(true);

    assertEquals(2, method.requiredParameters.size());
    assertSame(existingParameter, method.requiredParameters.get(0));
    assertSame(addedParameter, method.requiredParameters.get(1));
  }

  @Test
  public void doOrRedoInternal_whenDo_appendsTrackedArgumentWithNullLiteral() {
    AddParameterEdit edit = new AddParameterEdit(null, method, addedParameter);

    edit.doOrRedoInternal(true);

    assertEquals(2, invocation.requiredArguments.size());
    SimpleArgument addedArgument = invocation.requiredArguments.get(1);
    assertSame(addedParameter, addedArgument.parameter.getValue());
    assertTrue(addedArgument.expression.getValue() instanceof NullLiteral);
  }

  @Test
  public void doOrRedoInternal_preservesExistingParameterAndArgument() {
    AddParameterEdit edit = new AddParameterEdit(null, method, addedParameter);

    edit.doOrRedoInternal(true);

    assertSame(existingParameter, method.requiredParameters.get(0));
    assertSame(existingParameter, invocation.requiredArguments.get(0).parameter.getValue());
  }

  @Test
  public void undoInternal_afterDo_removesAddedParameterAndArgument() {
    AddParameterEdit edit = new AddParameterEdit(null, method, addedParameter);

    edit.doOrRedoInternal(true);
    edit.undoInternal();

    assertEquals(1, method.requiredParameters.size());
    assertSame(existingParameter, method.requiredParameters.get(0));
    assertEquals(1, invocation.requiredArguments.size());
    assertSame(existingParameter, invocation.requiredArguments.get(0).parameter.getValue());
  }

  @Test
  public void doUndoRedo_cycle_restoresSameRemovedArgumentInstance() {
    AddParameterEdit edit = new AddParameterEdit(null, method, addedParameter);

    edit.doOrRedoInternal(true);
    SimpleArgument addedArgument = invocation.requiredArguments.get(1);
    edit.undoInternal();
    edit.doOrRedoInternal(false);

    assertEquals(2, invocation.requiredArguments.size());
    assertSame(addedArgument, invocation.requiredArguments.get(1));
  }

  @Test
  public void canUndo_returnsTrue() {
    AddParameterEdit edit = new AddParameterEdit(null, method, addedParameter);

    assertTrue(edit.canUndo());
  }

  @Test
  public void canRedo_returnsTrue() {
    AddParameterEdit edit = new AddParameterEdit(null, method, addedParameter);

    assertTrue(edit.canRedo());
  }

  @Test
  public void appendDescription_viaTerseDescription_startsWithDeclarePrefix() {
    AddParameterEdit edit = new AddParameterEdit(null, method, addedParameter);

    assertTrue(edit.getTerseDescription().startsWith("declare:"));
  }

  @Test
  public void getRedoPresentation_containsDeclarePrefix() {
    AddParameterEdit edit = new AddParameterEdit(null, method, addedParameter);

    assertTrue(edit.getRedoPresentation().startsWith("Redo:declare:"));
  }
}
