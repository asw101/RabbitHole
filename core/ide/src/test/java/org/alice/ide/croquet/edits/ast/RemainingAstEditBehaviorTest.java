package org.alice.ide.croquet.edits.ast;

import edu.cmu.cs.dennisc.pattern.Criterion;
import org.alice.ide.IDE;
import org.alice.ide.ProjectDocumentFrame;
import org.alice.ide.ReasonToDisableSomeAmountOfRendering;
import org.alice.ide.cascade.ExpressionCascadeManager;
import org.alice.ide.croquet.models.ast.cascade.ExpressionListPropertyCascade;
import org.alice.ide.croquet.models.ast.cascade.MoreCascade;
import org.alice.ide.frametitle.IdeFrameTitleGenerator;
import org.alice.ide.sceneeditor.AbstractSceneEditor;
import org.alice.ide.testing.TestIdeBootstrap;
import org.alice.math.immutable.AffineMatrix4x4;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.lgna.croquet.Application;
import org.lgna.croquet.history.UserActivity;
import org.lgna.croquet.preferences.PreferencesManager;
import org.lgna.project.ast.AbstractField;
import org.lgna.project.ast.BlockStatement;
import org.lgna.project.ast.Comment;
import org.lgna.project.ast.Declaration;
import org.lgna.project.ast.Expression;
import org.lgna.project.ast.ExpressionStatement;
import org.lgna.project.ast.IntegerLiteral;
import org.lgna.project.ast.JavaMethod;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.ManagementLevel;
import org.lgna.project.ast.MethodInvocation;
import org.lgna.project.ast.NamedUserType;
import org.lgna.project.ast.NullLiteral;
import org.lgna.project.ast.SimpleArgument;
import org.lgna.project.ast.SimpleArgumentListProperty;
import org.lgna.project.ast.Statement;
import org.lgna.project.ast.StatementListProperty;
import org.lgna.project.ast.StringLiteral;
import org.lgna.project.ast.UserCode;
import org.lgna.project.ast.UserField;
import org.lgna.project.ast.UserMethod;
import org.lgna.project.ast.UserParameter;
import org.lgna.project.ast.UserType;
import org.lgna.project.virtualmachine.VirtualMachine;
import sun.misc.Unsafe;

import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.*;

public class RemainingAstEditBehaviorTest {
  private static Unsafe getUnsafe() {
    try {
      Field field = Unsafe.class.getDeclaredField("theUnsafe");
      field.setAccessible(true);
      return (Unsafe) field.get(null);
    } catch (ReflectiveOperationException roe) {
      throw new AssertionError(roe);
    }
  }

  @SuppressWarnings("unchecked")
  private static <T> T allocate(Class<T> type) {
    try {
      return (T) getUnsafe().allocateInstance(type);
    } catch (InstantiationException ie) {
      throw new AssertionError(ie);
    }
  }

  private static void setNextLongerInChain(JavaMethod shorter, JavaMethod longer) {
    try {
      java.lang.reflect.Method method = JavaMethod.class.getDeclaredMethod("setNextLongerInChain", JavaMethod.class);
      method.setAccessible(true);
      method.invoke(shorter, longer);
    } catch (ReflectiveOperationException roe) {
      throw new AssertionError(roe);
    }
  }

  private static void setField(Class<?> owner, Object instance, String fieldName, Object value) {
    try {
      Field field = owner.getDeclaredField(fieldName);
      field.setAccessible(true);
      field.set(instance, value);
    } catch (ReflectiveOperationException roe) {
      throw new AssertionError(roe);
    }
  }

  private static final class RecordingSceneEditor extends AbstractSceneEditor {
    private int addCount;
    private int removeCount;
    private UserType<?> addedDeclaringType;
    private UserField addedField;
    private int addedIndex;
    private Statement[] addedStatements;
    private UserType<?> removedDeclaringType;
    private UserField removedField;
    private Statement[] removedStatements;
    private UserField revertedField;
    private UserField setStateField;
    private Statement[] setStateStatements;
    private UserField stateRequestField;
    private Statement currentStateCode;

    @Override
    public void disableRendering(ReasonToDisableSomeAmountOfRendering reasonToDisableSomeAmountOfRendering) {
    }

    @Override
    public void enableRendering(ReasonToDisableSomeAmountOfRendering reasonToDisableSomeAmountOfRendering) {
    }

    @Override
    public void generateCodeForSetUp(StatementListProperty bodyStatementsProperty) {
    }

    @Override
    public Statement[] getDoStatementsForAddField(UserField field, AffineMatrix4x4 initialTransform) {
      return new Statement[0];
    }

    @Override
    public Statement[] getDoStatementsForCopyField(UserField fieldToCopy, UserField newField, AffineMatrix4x4 initialTransform) {
      return new Statement[0];
    }

    @Override
    public Statement[] getUndoStatementsForAddField(UserField field) {
      return new Statement[0];
    }

    @Override
    public Statement[] getDoStatementsForRemoveField(UserField field, Map<AbstractField, Statement> riders) {
      return new Statement[0];
    }

    @Override
    public Statement[] getUndoStatementsForRemoveField(UserField field, Map<AbstractField, Statement> riders) {
      return new Statement[0];
    }

    @Override
    public void preScreenCapture() {
    }

    @Override
    public void postScreenCapture() {
    }

    @Override
    protected void handleExpandContractChange(boolean isExpanded) {
    }

    @Override
    public void addField(UserType<?> declaringType, UserField field, int index, Statement... statements) {
      this.addCount++;
      this.addedDeclaringType = declaringType;
      this.addedField = field;
      this.addedIndex = index;
      this.addedStatements = statements;
      declaringType.fields.add(index, field);
    }

    @Override
    public void removeField(UserType<?> declaringType, UserField field, Statement... statements) {
      this.removeCount++;
      this.removedDeclaringType = declaringType;
      this.removedField = field;
      this.removedStatements = statements;
      int index = declaringType.fields.indexOf(field);
      if (index >= 0) {
        declaringType.fields.remove(index);
      }
    }

    @Override
    public Statement getCurrentStateCodeForField(UserField field) {
      this.stateRequestField = field;
      return this.currentStateCode;
    }

    @Override
    public void revertFieldToInitialState(UserField field) {
      this.revertedField = field;
    }

    @Override
    public void setFieldToState(UserField field, Statement... statements) {
      this.setStateField = field;
      this.setStateStatements = statements;
    }
  }

  private static final class TestIde extends IDE {
    private AbstractSceneEditor sceneEditor;
    private ProjectDocumentFrame documentFrame;

    private TestIde() {
      super(null, null);
    }

    static TestIde create(AbstractSceneEditor sceneEditor, ProjectDocumentFrame documentFrame) {
      TestIde ide = allocate(TestIde.class);
      ide.sceneEditor = sceneEditor;
      ide.documentFrame = documentFrame;
      setField(Application.class, ide, "systemActivity", new UserActivity());
      setField(Application.class, ide, "preferencesManager", new PreferencesManager(ide));
      return ide;
    }

    @Override
    public List<SimpleArgumentListProperty> getArgumentLists(UserCode code) {
      return Collections.emptyList();
    }

    @Override
    public ProjectDocumentFrame getDocumentFrame() {
      return this.documentFrame;
    }

    @Override
    public AbstractSceneEditor getSceneEditor() {
      return this.sceneEditor;
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
    protected org.lgna.croquet.Operation getAboutOperation() {
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

  @Before
  public void setUp() {
    previousApplication = Application.getActiveInstance();
  }

  @After
  public void tearDown() {
    TestIdeBootstrap.setActiveApplication(previousApplication);
  }

  @Test
  public void declareGalleryFieldEdit_doUndoRedo_delegatesToSceneEditor() {
    NamedUserType declaringType = createType("GalleryOwner");
    UserField field = createField("galleryField");
    Statement[] doStatements = {new Comment("do")};
    Statement[] undoStatements = {new Comment("undo")};
    RecordingSceneEditor sceneEditor = new RecordingSceneEditor();
    DeclareGalleryFieldEdit edit = new DeclareGalleryFieldEdit(null, sceneEditor, declaringType, field, doStatements, undoStatements);

    edit.doOrRedoInternal(true);

    assertEquals(1, sceneEditor.addCount);
    assertSame(declaringType, sceneEditor.addedDeclaringType);
    assertSame(field, sceneEditor.addedField);
    assertEquals(0, sceneEditor.addedIndex);
    assertArrayEquals(doStatements, sceneEditor.addedStatements);
    assertSame(field, declaringType.fields.get(0));

    edit.undoInternal();

    assertEquals(1, sceneEditor.removeCount);
    assertSame(declaringType, sceneEditor.removedDeclaringType);
    assertSame(field, sceneEditor.removedField);
    assertArrayEquals(undoStatements, sceneEditor.removedStatements);
    assertTrue(declaringType.fields.isEmpty());

    edit.doOrRedoInternal(false);

    assertEquals(2, sceneEditor.addCount);
    assertSame(field, declaringType.fields.get(0));
  }

  @Test
  public void fillInExpressionListPropertyEdit_doUndoRedo_replacesTargetIndexOnly() {
    IntegerLiteral before = new IntegerLiteral(1);
    IntegerLiteral prev = new IntegerLiteral(2);
    IntegerLiteral after = new IntegerLiteral(3);
    Expression[] expressions = {before, prev, after};
    org.lgna.project.ast.ArrayInstanceCreation array = new org.lgna.project.ast.ArrayInstanceCreation(Object[].class, new Integer[]{expressions.length}, expressions);
    Expression next = new IntegerLiteral(9);
    ExpressionListPropertyCascade cascade = new ExpressionListPropertyCascade(Application.PROJECT_GROUP, UUID.randomUUID(), 1, array.expressions, JavaType.getInstance(Object.class));
    UserActivity activity = new UserActivity();
    activity.setCompletionModel(cascade);
    FillInExpressionListPropertyEdit edit = new FillInExpressionListPropertyEdit(activity, prev, next);

    edit.doOrRedoInternal(true);

    assertSame(before, array.expressions.get(0));
    assertSame(next, array.expressions.get(1));
    assertSame(after, array.expressions.get(2));

    edit.undoInternal();

    assertSame(before, array.expressions.get(0));
    assertSame(prev, array.expressions.get(1));
    assertSame(after, array.expressions.get(2));

    edit.doOrRedoInternal(false);

    assertSame(next, array.expressions.get(1));
    assertEquals(3, array.expressions.size());
  }

  @Test
  public void fillInMoreEdit_doUndoRedo_switchesExpressionStatementBetweenInvocationShapes() {
    JavaMethod shorter = JavaMethod.getInstance(String.class, "substring", int.class);
    JavaMethod longer = JavaMethod.getInstance(String.class, "substring", int.class, int.class);
    setNextLongerInChain(shorter, longer);

    MethodInvocation previousInvocation = new MethodInvocation(
        new StringLiteral("hello"),
        shorter,
        new SimpleArgument(shorter.getRequiredParameters().get(0), new IntegerLiteral(1)));
    ExpressionStatement statement = new ExpressionStatement(previousInvocation);
    MoreCascade cascade = MoreCascade.getInstance(previousInvocation);
    UserActivity activity = new UserActivity();
    activity.setCompletionModel(cascade);
    IntegerLiteral addedArgumentExpression = new IntegerLiteral(4);
    FillInMoreEdit edit = new FillInMoreEdit(activity, addedArgumentExpression);

    edit.doOrRedoInternal(true);

    MethodInvocation nextInvocation = cascade.getNextMethodInvocation();
    assertSame(nextInvocation, statement.expression.getValue());
    assertSame(previousInvocation.expression.getValue(), nextInvocation.expression.getValue());
    assertSame(previousInvocation.requiredArguments.get(0).expression.getValue(), nextInvocation.requiredArguments.get(0).expression.getValue());
    assertSame(addedArgumentExpression, nextInvocation.requiredArguments.get(1).expression.getValue());

    edit.undoInternal();

    assertSame(previousInvocation, statement.expression.getValue());
    assertNull(nextInvocation.expression.getValue());
    assertSame(previousInvocation.requiredArguments.get(0).expression.getValue(), previousInvocation.requiredArguments.get(0).expression.getValue());

    edit.doOrRedoInternal(false);

    assertSame(nextInvocation, statement.expression.getValue());
    assertSame(addedArgumentExpression, nextInvocation.requiredArguments.get(1).expression.getValue());
  }

  @Test
  public void revertFieldEdit_doUndoRedo_onManagedField_usesCapturedSceneState() {
    RecordingSceneEditor sceneEditor = new RecordingSceneEditor();
    Statement redoStateCode = new Comment("redoState");
    sceneEditor.currentStateCode = redoStateCode;
    TestIdeBootstrap.setActiveApplication(TestIde.create(sceneEditor, null));
    UserField field = createField("managedField");
    field.managementLevel.setValue(ManagementLevel.MANAGED);
    RevertFieldEdit edit = new RevertFieldEdit((UserActivity) null, field);

    assertSame(field, sceneEditor.stateRequestField);

    edit.doOrRedoInternal(true);

    assertSame(field, sceneEditor.revertedField);

    edit.undoInternal();

    assertSame(field, sceneEditor.setStateField);
    assertArrayEquals(new Statement[]{redoStateCode}, sceneEditor.setStateStatements);

    edit.doOrRedoInternal(false);

    assertSame(field, sceneEditor.revertedField);
  }

  @Test
  public void changeMethodBodyEdit_doUndoRedo_switchesBetweenDistinctBodies() {
    BlockStatement originalBody = new BlockStatement(new Comment("original"));
    BlockStatement replacementBody = new BlockStatement(new Comment("replacement"));
    UserMethod method = new UserMethod("perform", Object.class, new UserParameter[0], originalBody);
    ChangeMethodBodyEdit edit = new ChangeMethodBodyEdit(null, method, replacementBody);

    edit.doOrRedoInternal(true);

    assertSame(replacementBody, method.body.getValue());
    assertEquals("replacement", ((Comment) method.body.getValue().statements.get(0)).text.getValue());

    edit.undoInternal();

    assertSame(originalBody, method.body.getValue());
    assertEquals("original", ((Comment) method.body.getValue().statements.get(0)).text.getValue());

    edit.doOrRedoInternal(false);

    assertSame(replacementBody, method.body.getValue());
    assertEquals("replacement", ((Comment) method.body.getValue().statements.get(0)).text.getValue());
  }

  private static NamedUserType createType(String name) {
    NamedUserType type = new NamedUserType();
    type.name.setValue(name);
    type.superType.setValue(JavaType.getInstance(Object.class));
    return type;
  }

  private static UserField createField(String name) {
    return new UserField(name, JavaType.getInstance(Object.class), new NullLiteral());
  }
}
