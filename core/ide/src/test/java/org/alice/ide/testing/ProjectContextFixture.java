package org.alice.ide.testing;

import org.alice.stageide.StageIDE;
import org.lgna.common.resources.AudioResource;
import org.lgna.common.resources.ImageResource;
import org.lgna.project.Project;
import org.lgna.project.ast.AstUtilities;
import org.lgna.project.ast.BlockStatement;
import org.lgna.project.ast.Comment;
import org.lgna.project.ast.ExpressionStatement;
import org.lgna.project.ast.IntegerLiteral;
import org.lgna.project.ast.JavaMethod;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.LambdaExpression;
import org.lgna.project.ast.NamedUserType;
import org.lgna.project.ast.NullLiteral;
import org.lgna.project.ast.ReturnStatement;
import org.lgna.project.ast.ThisExpression;
import org.lgna.project.ast.UserField;
import org.lgna.project.ast.UserLambda;
import org.lgna.project.ast.UserMethod;
import org.lgna.project.ast.UserParameter;
import org.lgna.story.SBiped;
import org.lgna.story.SProgram;
import org.lgna.story.SScene;
import org.lgna.story.event.SceneActivationListener;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

public final class ProjectContextFixture {
  public final Project project;
  public final NamedUserType programType;
  public final NamedUserType sceneType;
  public final NamedUserType actorType;
  public final UserField sceneField;
  public final UserField actorField;
  public final UserMethod sceneProcedure;
  public final UserMethod sceneFunction;
  public final UserMethod initializeEventListenersMethod;
  public final UserMethod performGeneratedSetUpMethod;
  public final AudioResource audioResource;
  public final ImageResource imageResource;

  private ProjectContextFixture(Project project, NamedUserType programType, NamedUserType sceneType,
      NamedUserType actorType, UserField sceneField, UserField actorField, UserMethod sceneProcedure,
      UserMethod sceneFunction, UserMethod initializeEventListenersMethod,
      UserMethod performGeneratedSetUpMethod, AudioResource audioResource, ImageResource imageResource) {
    this.project = project;
    this.programType = programType;
    this.sceneType = sceneType;
    this.actorType = actorType;
    this.sceneField = sceneField;
    this.actorField = actorField;
    this.sceneProcedure = sceneProcedure;
    this.sceneFunction = sceneFunction;
    this.initializeEventListenersMethod = initializeEventListenersMethod;
    this.performGeneratedSetUpMethod = performGeneratedSetUpMethod;
    this.audioResource = audioResource;
    this.imageResource = imageResource;
  }

  public static ProjectContextFixture create() {
    NamedUserType sceneType = AstUtilities.createType("Scene", JavaType.getInstance(SScene.class));
    NamedUserType actorType = AstUtilities.createType("Actor", JavaType.getInstance(SBiped.class));
    NamedUserType programType = AstUtilities.createType("Program", JavaType.getInstance(SProgram.class));

    UserField sceneField = new UserField("myScene", sceneType);
    UserField actorField = new UserField("hero", actorType, new NullLiteral());
    programType.fields.add(sceneField);
    sceneType.fields.add(actorField);

    UserMethod sceneProcedure = new UserMethod("storyAction", JavaType.VOID_TYPE, new UserParameter[0],
        new BlockStatement(new Comment("project context procedure")));
    sceneType.methods.add(sceneProcedure);

    UserMethod sceneFunction = new UserMethod("score", JavaType.INTEGER_PRIMITIVE_TYPE, new UserParameter[0],
        new BlockStatement(new ReturnStatement(JavaType.INTEGER_PRIMITIVE_TYPE, new IntegerLiteral(7))));
    sceneType.methods.add(sceneFunction);

    JavaMethod addSceneActivationListener = JavaMethod.getInstance(
        SScene.class, "addSceneActivationListener", SceneActivationListener.class);
    LambdaExpression listenerLambda = AstUtilities.createLambdaExpression(SceneActivationListener.class);
    UserLambda userLambda = (UserLambda) listenerLambda.value.getValue();
    userLambda.body.getValue().statements.add(
        AstUtilities.createMethodInvocationStatement(new ThisExpression(), sceneProcedure));
    ExpressionStatement listenerStatement = AstUtilities.createMethodInvocationStatement(
        new ThisExpression(), addSceneActivationListener, listenerLambda);
    UserMethod initializeEventListenersMethod = new UserMethod(
        StageIDE.INITIALIZE_EVENT_LISTENERS_METHOD_NAME, JavaType.VOID_TYPE, new UserParameter[0],
        new BlockStatement(listenerStatement));
    sceneType.methods.add(initializeEventListenersMethod);

    UserMethod performGeneratedSetUpMethod = new UserMethod(
        StageIDE.PERFORM_GENERATED_SET_UP_METHOD_NAME, JavaType.VOID_TYPE, new UserParameter[0],
        new BlockStatement(new Comment("generated setup")));
    sceneType.methods.add(performGeneratedSetUpMethod);

    AudioResource audioResource = new AudioResource(UUID.randomUUID());
    audioResource.setName("context.wav");
    audioResource.setOriginalFileName("context.wav");
    audioResource.setContent(AudioResource.getContentType("context.wav"), new byte[] {1, 2, 3});

    ImageResource imageResource = new ImageResource(UUID.randomUUID());
    imageResource.setName("context.png");
    imageResource.setOriginalFileName("context.png");
    imageResource.setContent(ImageResource.getContentType("context.png"), new byte[] {4, 5, 6});

    Set<NamedUserType> namedUserTypes = new LinkedHashSet<>();
    namedUserTypes.add(programType);
    namedUserTypes.add(sceneType);
    namedUserTypes.add(actorType);

    Set<org.lgna.common.Resource> resources = new LinkedHashSet<>();
    resources.add(audioResource);
    resources.add(imageResource);

    Project project = new Project(programType, namedUserTypes, resources, Project.SceneCameraType.WindowCamera);
    return new ProjectContextFixture(project, programType, sceneType, actorType, sceneField, actorField,
        sceneProcedure, sceneFunction, initializeEventListenersMethod, performGeneratedSetUpMethod,
        audioResource, imageResource);
  }

  public static ProjectContextFixture load() {
    ProjectContextFixture fixture = create();
    TestIdeBootstrap.loadProject(fixture.project);
    return fixture;
  }
}
