package org.alice.stageide.ast;

import org.alice.ide.ProjectDocument;
import org.alice.stageide.StageIDE;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.lgna.croquet.history.UserActivity;
import org.lgna.project.Project;
import org.lgna.project.ast.AstUtilities;
import org.lgna.project.ast.BlockStatement;
import org.lgna.project.ast.ExpressionStatement;
import org.lgna.project.ast.JavaCodeGenerator;
import org.lgna.project.ast.LambdaExpression;
import org.lgna.project.ast.MethodInvocation;
import org.lgna.project.ast.NamedUserType;
import org.lgna.project.ast.NullLiteral;
import org.lgna.project.ast.ThisExpression;
import org.lgna.project.ast.UserField;
import org.lgna.project.ast.UserLambda;
import org.lgna.project.ast.UserMethod;
import org.lgna.story.SScene;
import org.lgna.story.event.SceneActivationListener;
import org.lgna.story.ast.EventListenerMethodUtilities;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.Assert.*;

public class StoryApiSpecificAstUtilitiesComprehensiveTest {
  private JavaCodeGenerator originalGenerator;

  @Before
  public void resetGenerator() throws Exception {
    Field field = StoryApiSpecificAstUtilities.class.getDeclaredField("s_javaCodeGenerator");
    field.setAccessible(true);
    this.originalGenerator = (JavaCodeGenerator) field.get(null);
    field.set(null, null);
  }

  @After
  public void restoreGenerator() throws Exception {
    Field field = StoryApiSpecificAstUtilities.class.getDeclaredField("s_javaCodeGenerator");
    field.setAccessible(true);
    field.set(null, this.originalGenerator);
  }

  @Test
  public void firstProgramFieldIsReturnedEvenWhenItIsNotASceneField() {
    NamedUserType program = createProgramType();
    UserField first = new UserField("notScene", org.lgna.project.ast.JavaType.STRING_TYPE, new NullLiteral());
    UserField second = new UserField("myScene", createSceneType(), new NullLiteral());
    program.fields.add(first);
    program.fields.add(second);

    assertSame(first, StoryApiSpecificAstUtilities.getSceneFieldFromProgramType(program));
  }

  @Test
  public void firstProgramFieldTakesPriorityOverLaterSceneFields() {
    NamedUserType program = createProgramType();
    UserField first = new UserField("sceneOne", createSceneType(), new NullLiteral());
    UserField second = new UserField("sceneTwo", createSceneType(), new NullLiteral());
    program.fields.add(first);
    program.fields.add(second);

    assertSame(first, StoryApiSpecificAstUtilities.getSceneFieldFromProgramType(program));
  }

  @Test
  public void getSceneTypeFromProjectReturnsNullWhenProgramHasNoFields() {
    Project project = new Project(createProgramType(), Project.SceneCameraType.WindowCamera);

    assertNull(StoryApiSpecificAstUtilities.getSceneTypeFromProject(project));
  }

  @Test
  public void getSceneTypeFromDocumentReturnsNullWhenProjectHasNoSceneField() {
    Project project = new Project(createProgramType(), Project.SceneCameraType.WindowCamera);
    ProjectDocument document = new ProjectDocument(project, new UserActivity());

    assertNull(StoryApiSpecificAstUtilities.getSceneTypeFromDocument(document));
  }

  @Test
  public void performGeneratedSetupLookupDoesNotSearchSuperType() {
    NamedUserType baseScene = createSceneType();
    baseScene.methods.add(createMethod(StageIDE.PERFORM_GENERATED_SET_UP_METHOD_NAME));
    NamedUserType childScene = AstUtilities.createType("ChildScene", baseScene);

    assertNull(StoryApiSpecificAstUtilities.getPerformEditorGeneratedSetUpMethod(childScene));
  }

  @Test
  public void initializeEventListenersLookupDoesNotSearchSuperType() {
    NamedUserType baseScene = createSceneType();
    baseScene.methods.add(createMethod(StageIDE.INITIALIZE_EVENT_LISTENERS_METHOD_NAME));
    NamedUserType childScene = AstUtilities.createType("ChildScene", baseScene);

    assertNull(StoryApiSpecificAstUtilities.getInitializeEventListenersMethod(childScene));
  }

  @Test
  public void userMethodsInvokedBySceneActivationListenersPreserveInvocationOrderWithinLambda() {
    NamedUserType sceneType = createSceneType();
    UserMethod initialize = createMethod(StageIDE.INITIALIZE_EVENT_LISTENERS_METHOD_NAME);
    UserMethod first = createMethod("first");
    UserMethod second = createMethod("second");
    sceneType.methods.add(first);
    sceneType.methods.add(second);
    sceneType.methods.add(initialize);

    UserLambda lambda = AstUtilities.createUserLambda(SceneActivationListener.class);
    lambda.body.getValue().statements.add(new ExpressionStatement(new MethodInvocation(new ThisExpression(), first)));
    lambda.body.getValue().statements.add(new ExpressionStatement(new MethodInvocation(new ThisExpression(), second)));
    initialize.body.getValue().statements.add(AstUtilities.createMethodInvocationStatement(
        new ThisExpression(),
        EventListenerMethodUtilities.ADD_SCENE_ACTIVATION_LISTENER_METHOD,
        new LambdaExpression(lambda)));

    List<UserMethod> methods = StoryApiSpecificAstUtilities.getUserMethodsInvokedSceneActivationListeners(sceneType);

    assertEquals(2, methods.size());
    assertSame(first, methods.get(0));
    assertSame(second, methods.get(1));
  }

  @Test
  public void emptyListenerLambdasAreIgnoredWhileValidListenersRemain() {
    NamedUserType sceneType = createSceneType();
    UserMethod initialize = createMethod(StageIDE.INITIALIZE_EVENT_LISTENERS_METHOD_NAME);
    UserMethod target = createMethod("target");
    sceneType.methods.add(target);
    sceneType.methods.add(initialize);

    UserLambda emptyLambda = AstUtilities.createUserLambda(SceneActivationListener.class);
    initialize.body.getValue().statements.add(AstUtilities.createMethodInvocationStatement(
        new ThisExpression(),
        EventListenerMethodUtilities.ADD_SCENE_ACTIVATION_LISTENER_METHOD,
        new LambdaExpression(emptyLambda)));
    initialize.body.getValue().statements.add(createSceneActivationStatement(target));

    List<UserMethod> methods = StoryApiSpecificAstUtilities.getUserMethodsInvokedSceneActivationListeners(sceneType);

    assertEquals(1, methods.size());
    assertSame(target, methods.get(0));
  }

  @Test
  public void getInnerCommentForMethodNameLazilyInitializesGenerator() throws Exception {
    assertNull(readGenerator());

    String comment = StoryApiSpecificAstUtilities.getInnerCommentForMethodName(createSceneType(), StageIDE.PERFORM_GENERATED_SET_UP_METHOD_NAME);

    assertNotNull(comment);
    assertNotNull(readGenerator());
  }

  @Test
  public void getInnerCommentForMethodNameReusesCachedGenerator() throws Exception {
    StoryApiSpecificAstUtilities.getInnerCommentForMethodName(createSceneType(), StageIDE.PERFORM_GENERATED_SET_UP_METHOD_NAME);
    JavaCodeGenerator first = readGenerator();
    StoryApiSpecificAstUtilities.getInnerCommentForMethodName(createProgramType(), "main");

    assertSame(first, readGenerator());
  }

  @Test
  public void userTypeNotAssignableToSceneIsRejectedByIsSceneType() {
    NamedUserType type = AstUtilities.createType("Helper", org.lgna.project.ast.JavaType.getInstance(Object.class));

    assertFalse(StoryApiSpecificAstUtilities.isSceneType(type));
  }

  @Test
  public void unknownProgramCommentKeyReturnsNull() {
    assertNull(StoryApiSpecificAstUtilities.getInnerCommentForMethodName(createProgramType(), "missingMethod"));
  }

  @Test
  public void nullProgramTypeHasNoSceneField() {
    assertNull(StoryApiSpecificAstUtilities.getSceneFieldFromProgramType(null));
  }

  @Test
  public void getSceneTypeFromProjectReturnsFirstFieldType() {
    NamedUserType sceneType = createSceneType();
    NamedUserType programType = createProgramType();
    programType.fields.add(new UserField("scene", sceneType, new NullLiteral()));

    assertSame(sceneType, StoryApiSpecificAstUtilities.getSceneTypeFromProject(new Project(programType, Project.SceneCameraType.WindowCamera)));
  }

  @Test
  public void getSceneTypeFromDocumentReturnsFirstFieldType() {
    NamedUserType sceneType = createSceneType();
    NamedUserType programType = createProgramType();
    programType.fields.add(new UserField("scene", sceneType, new NullLiteral()));
    ProjectDocument document = new ProjectDocument(new Project(programType, Project.SceneCameraType.WindowCamera), new UserActivity());

    assertSame(sceneType, StoryApiSpecificAstUtilities.getSceneTypeFromDocument(document));
  }

  private static NamedUserType createSceneType() {
    return AstUtilities.createType("Scene", org.lgna.project.ast.JavaType.getInstance(SScene.class));
  }

  private static NamedUserType createProgramType() {
    return AstUtilities.createType("Program", org.lgna.project.ast.JavaType.getInstance(Object.class));
  }

  private static UserMethod createMethod(String name) {
    return new UserMethod(name, org.lgna.project.ast.JavaType.VOID_TYPE, new org.lgna.project.ast.UserParameter[0], new BlockStatement());
  }

  private static ExpressionStatement createSceneActivationStatement(UserMethod method) {
    UserLambda lambda = AstUtilities.createUserLambda(SceneActivationListener.class);
    lambda.body.getValue().statements.add(new ExpressionStatement(new MethodInvocation(new ThisExpression(), method)));
    return AstUtilities.createMethodInvocationStatement(
        new ThisExpression(),
        EventListenerMethodUtilities.ADD_SCENE_ACTIVATION_LISTENER_METHOD,
        new LambdaExpression(lambda));
  }

  private static JavaCodeGenerator readGenerator() throws Exception {
    Field field = StoryApiSpecificAstUtilities.class.getDeclaredField("s_javaCodeGenerator");
    field.setAccessible(true);
    return (JavaCodeGenerator) field.get(null);
  }
}
