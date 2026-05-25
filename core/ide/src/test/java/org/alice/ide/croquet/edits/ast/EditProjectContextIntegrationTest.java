package org.alice.ide.croquet.edits.ast;

import edu.cmu.cs.dennisc.pattern.Criterion;
import org.alice.ide.IDE;
import org.alice.ide.ProjectDocumentFrame;
import org.alice.ide.ast.delete.edits.DeleteStatementEdit;
import org.alice.ide.cascade.ExpressionCascadeManager;
import org.alice.ide.croquet.edits.ast.rename.RenameDeclarationEdit;
import org.alice.ide.declarationseditor.CodeComposite;
import org.alice.ide.declarationseditor.DeclarationComposite;
import org.alice.ide.declarationseditor.DeclarationTabState;
import org.alice.ide.frametitle.IdeFrameTitleGenerator;
import org.alice.ide.sceneeditor.AbstractSceneEditor;
import org.alice.ide.testing.ProjectContextFixture;
import org.alice.ide.testing.TestIdeBootstrap;
import org.junit.After;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.lgna.croquet.Application;
import org.lgna.croquet.Operation;
import org.lgna.croquet.history.UserActivity;
import org.lgna.project.ast.BlockStatement;
import org.lgna.project.ast.Comment;
import org.lgna.project.ast.Declaration;
import org.lgna.project.ast.ExpressionStatement;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.MethodInvocation;
import org.lgna.project.ast.NamedUserType;
import org.lgna.project.ast.NullLiteral;
import org.lgna.project.ast.SimpleArgument;
import org.lgna.project.ast.SimpleArgumentListProperty;
import org.lgna.project.ast.StringLiteral;
import org.lgna.project.ast.UserCode;
import org.lgna.project.ast.UserField;
import org.lgna.project.ast.UserMethod;
import org.lgna.project.ast.UserParameter;
import org.lgna.project.virtualmachine.VirtualMachine;
import sun.misc.Unsafe;

import java.awt.GraphicsEnvironment;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class EditProjectContextIntegrationTest {
  private static final class TestIde extends IDE {
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

  private ProjectContextFixture fixture;
  private Application<?> previousApplication;

  @Before
  public void setUp() {
    Assume.assumeFalse("requires TestIdeBootstrap project context", GraphicsEnvironment.isHeadless());
    fixture = ProjectContextFixture.create();
    TestIdeBootstrap.loadProject(fixture.project);
    previousApplication = Application.getActiveInstance();
  }

  @After
  public void tearDown() {
    TestIdeBootstrap.setActiveApplication(previousApplication);
    TestIdeBootstrap.reset();
  }

  @Test
  public void declareMethodEdit_commitUndoRedo_updatesDeclarationsEditorSelection() {
    TestIdeBootstrap.onEdt(() -> {
      DeclarationTabState tabState = TestIdeBootstrap.getDocumentFrame().getDeclarationsEditorComposite().getTabState();
      DeclarationComposite previousSelection = tabState.getValue();
      UserActivity activity = new UserActivity();
      DeclareMethodEdit edit = new DeclareMethodEdit(activity, fixture.sceneType, "projectContextMethod", JavaType.VOID_TYPE);

      activity.commitAndInvokeDo(edit);

      UserMethod createdMethod = fixture.sceneType.getDeclaredMethod("projectContextMethod");
      assertNotNull(createdMethod);
      assertSame(createdMethod, fixture.sceneType.methods.get(fixture.sceneType.methods.size() - 1));
      assertSame(CodeComposite.getInstance(createdMethod), tabState.getValue());

      edit.undo();

      assertNull(fixture.sceneType.getDeclaredMethod("projectContextMethod"));
      assertSame(previousSelection, tabState.getValue());

      edit.doOrRedo(false);

      assertSame(createdMethod, fixture.sceneType.getDeclaredMethod("projectContextMethod"));
      assertSame(CodeComposite.getInstance(createdMethod), tabState.getValue());
      return null;
    });
  }

  @Test
  public void declareNonGalleryFieldEdit_commitUndoRedo_updatesSceneFields() {
    TestIdeBootstrap.onEdt(() -> {
      UserField field = new UserField("projectContextField", JavaType.getInstance(String.class), new StringLiteral("value"));
      UserActivity activity = new UserActivity();
      DeclareNonGalleryFieldEdit edit = new DeclareNonGalleryFieldEdit(activity, fixture.sceneType, field);

      activity.commitAndInvokeDo(edit);

      assertSame(field, fixture.sceneType.getDeclaredField("projectContextField"));
      assertSame(field, fixture.sceneType.fields.get(fixture.sceneType.fields.size() - 1));

      edit.undo();

      assertNull(fixture.sceneType.getDeclaredField("projectContextField"));

      edit.doOrRedo(false);

      assertSame(field, fixture.sceneType.getDeclaredField("projectContextField"));
      return null;
    });
  }

  @Test
  public void deleteStatementEdit_commitUndoRedo_preservesBlockOrdering() {
    TestIdeBootstrap.onEdt(() -> {
      BlockStatement body = fixture.sceneProcedure.body.getValue();
      body.statements.clear();
      Comment first = new Comment("first");
      Comment middle = new Comment("middle");
      Comment last = new Comment("last");
      body.statements.add(first);
      body.statements.add(middle);
      body.statements.add(last);

      UserActivity activity = new UserActivity();
      DeleteStatementEdit edit = new DeleteStatementEdit(activity, middle);
      activity.commitAndInvokeDo(edit);

      assertEquals(2, body.statements.size());
      assertSame(first, body.statements.get(0));
      assertSame(last, body.statements.get(1));

      edit.undo();

      assertEquals(3, body.statements.size());
      assertSame(first, body.statements.get(0));
      assertSame(middle, body.statements.get(1));
      assertSame(last, body.statements.get(2));

      edit.doOrRedo(false);

      assertEquals(2, body.statements.size());
      assertSame(first, body.statements.get(0));
      assertSame(last, body.statements.get(1));
      return null;
    });
  }

  @Test
  public void renameDeclarationEdit_commitUndoRedo_updatesFieldLookup() {
    TestIdeBootstrap.onEdt(() -> {
      UserActivity activity = new UserActivity();
      RenameDeclarationEdit edit = new RenameDeclarationEdit(activity, fixture.actorField, "hero", "heroRenamed");

      activity.commitAndInvokeDo(edit);

      assertEquals("heroRenamed", fixture.actorField.getName());
      assertSame(fixture.actorField, fixture.sceneType.getDeclaredField("heroRenamed"));
      assertNull(fixture.sceneType.getDeclaredField("hero"));

      edit.undo();

      assertEquals("hero", fixture.actorField.getName());
      assertSame(fixture.actorField, fixture.sceneType.getDeclaredField("hero"));

      edit.doOrRedo(false);

      assertEquals("heroRenamed", fixture.actorField.getName());
      assertSame(fixture.actorField, fixture.sceneType.getDeclaredField("heroRenamed"));
      return null;
    });
  }

  @Test
  public void addParameterEdit_commitUndoRedo_updatesTrackedInvocationArguments() {
    TestIdeBootstrap.onEdt(() -> {
      UserParameter existingParameter = new UserParameter("existing", String.class);
      UserMethod method = new UserMethod("configure", Object.class, new UserParameter[] {existingParameter}, new BlockStatement());
      fixture.sceneType.methods.add(method);
      MethodInvocation invocation = new MethodInvocation(new NullLiteral(), method,
          new SimpleArgument(existingParameter, new StringLiteral("alpha")));
      fixture.sceneProcedure.body.getValue().statements.add(new ExpressionStatement(invocation));
      TestIdeBootstrap.setActiveApplication(TestIde.create(Collections.singletonList(invocation.requiredArguments)));

      UserParameter addedParameter = new UserParameter("added", Integer.class);
      UserActivity activity = new UserActivity();
      AddParameterEdit edit = new AddParameterEdit(activity, method, addedParameter);

      activity.commitAndInvokeDo(edit);

      assertEquals(2, method.requiredParameters.size());
      assertSame(addedParameter, method.requiredParameters.get(1));
      assertEquals(2, invocation.requiredArguments.size());
      SimpleArgument addedArgument = invocation.requiredArguments.get(1);
      assertSame(addedParameter, addedArgument.parameter.getValue());
      assertTrue(addedArgument.expression.getValue() instanceof NullLiteral);

      edit.undo();

      assertEquals(1, method.requiredParameters.size());
      assertEquals(1, invocation.requiredArguments.size());

      edit.doOrRedo(false);

      assertEquals(2, method.requiredParameters.size());
      assertSame(addedParameter, method.requiredParameters.get(1));
      assertEquals(2, invocation.requiredArguments.size());
      assertSame(addedArgument, invocation.requiredArguments.get(1));
      return null;
    });
  }

  @Test
  public void deleteParameterEdit_commitUndoRedo_restoresTrackedInvocationArguments() {
    TestIdeBootstrap.onEdt(() -> {
      UserParameter keptParameter = new UserParameter("kept", String.class);
      UserParameter deletedParameter = new UserParameter("deleted", Integer.class);
      UserMethod method = new UserMethod("configure", Object.class, new UserParameter[] {keptParameter, deletedParameter}, new BlockStatement());
      fixture.sceneType.methods.add(method);
      MethodInvocation invocation = new MethodInvocation(new NullLiteral(), method,
          new SimpleArgument(keptParameter, new StringLiteral("alpha")),
          new SimpleArgument(deletedParameter, new NullLiteral()));
      fixture.sceneProcedure.body.getValue().statements.add(new ExpressionStatement(invocation));
      TestIdeBootstrap.setActiveApplication(TestIde.create(Collections.singletonList(invocation.requiredArguments)));

      UserActivity activity = new UserActivity();
      DeleteParameterEdit edit = new DeleteParameterEdit(activity, method, deletedParameter);
      SimpleArgument removedArgument = invocation.requiredArguments.get(1);

      activity.commitAndInvokeDo(edit);

      assertEquals(1, method.requiredParameters.size());
      assertSame(keptParameter, method.requiredParameters.get(0));
      assertEquals(1, invocation.requiredArguments.size());
      assertSame(keptParameter, invocation.requiredArguments.get(0).parameter.getValue());

      edit.undo();

      assertEquals(2, method.requiredParameters.size());
      assertSame(deletedParameter, method.requiredParameters.get(1));
      assertEquals(2, invocation.requiredArguments.size());
      assertSame(removedArgument, invocation.requiredArguments.get(1));

      edit.doOrRedo(false);

      assertEquals(1, method.requiredParameters.size());
      assertEquals(1, invocation.requiredArguments.size());
      assertSame(keptParameter, invocation.requiredArguments.get(0).parameter.getValue());
      return null;
    });
  }
}
