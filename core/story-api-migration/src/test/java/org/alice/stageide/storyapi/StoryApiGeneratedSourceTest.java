package org.alice.stageide.storyapi;

import org.junit.Test;
import org.lgna.project.ast.AstMethodLookupHelpers;
import org.lgna.project.ast.AstUtilities;
import org.lgna.project.ast.BlockStatement;
import org.lgna.project.ast.ConstructorBlockStatement;
import org.lgna.project.ast.DoubleLiteral;
import org.lgna.project.ast.FieldAccess;
import org.lgna.project.ast.IntegerLiteral;
import org.lgna.project.ast.JavaCodeGenerator;
import org.lgna.project.ast.JavaMethod;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.LambdaExpression;
import org.lgna.project.ast.NamedUserConstructor;
import org.lgna.project.ast.NamedUserType;
import org.lgna.project.ast.NullLiteral;
import org.lgna.project.ast.ParameterAccess;
import org.lgna.project.ast.StringLiteral;
import org.lgna.project.ast.ThisExpression;
import org.lgna.project.ast.TypeExpression;
import org.lgna.project.ast.UserField;
import org.lgna.project.ast.UserLambda;
import org.lgna.project.ast.UserMethod;
import org.lgna.project.ast.UserParameter;
import org.lgna.story.AddTimeListener;
import org.lgna.story.Color;
import org.lgna.story.Paint;
import org.lgna.story.SBox;
import org.lgna.story.SModel;
import org.lgna.story.SScene;
import org.lgna.story.SProgram;
import org.lgna.story.Say;
import org.lgna.story.SetAtmosphereColor;
import org.lgna.story.SetFogDensity;
import org.lgna.story.SetOpacity;
import org.lgna.story.SetPaint;
import org.lgna.story.ast.JavaCodeUtilities;
import org.lgna.story.event.SceneActivationEvent;
import org.lgna.story.event.SceneActivationListener;
import org.lgna.story.event.TimeEvent;
import org.lgna.story.event.TimeListener;

import static org.junit.Assert.assertTrue;

public class StoryApiGeneratedSourceTest {

  @Test
  public void programSourceRendersStoryProgramMethodCallsWithoutNetBeansPackaging() {
    String source = generate(programTypeWithProgramStoryCalls());

    assertTrue(source, source.contains("void configureStory()"));
    assertTrue(source, source.contains("this.setSimulationSpeedFactor(1.5);"));
    assertTrue(source, source.contains("void clearScene()"));
    assertTrue(source, source.contains("this.setActiveScene(null);"));
  }

  @Test
  public void programAndSceneSourcesRenderStoryModelSceneAndEventCallsWithoutNetBeansPackaging() {
    NamedUserType sceneType = sceneTypeWithEventAndRenderingCalls();
    String programSource = generate(programTypeWithSceneModelAndRenderingCalls(sceneType));
    String sceneSource = generate(sceneType);

    assertTrue(programSource, programSource.contains("this.setActiveScene(this.scene);"));
    assertTrue(programSource, programSource.contains("this.box.setPaint(Color.RED);"));
    assertTrue(programSource, programSource.contains("this.box.setOpacity(0.5);"));
    assertTrue(programSource, programSource.contains("this.box.say(\"hello box\");"));
    assertTrue(sceneSource, sceneSource.contains("this.setAtmosphereColor(Color.BLUE);"));
    assertTrue(sceneSource, sceneSource.contains("this.setFogDensity(0.25);"));
    assertTrue(sceneSource, sceneSource.contains("this.addTimeListener(null,1);"));
    assertTrue(sceneSource, sceneSource.contains("this.addSceneActivationListener(null);"));
  }

  @Test
  public void sceneSourceRendersStoryEventListenerLambdasWithoutNetBeansPackaging() {
    String sceneActivationSource = generate(sceneTypeWithSceneActivationListenerLambda());
    String timeDispatchSource = generate(sceneTypeWithTimeListenerDispatchLambda());
    String timeListenerSource = generate(sceneTypeWithTimeListenerElapsedLambda());

    assertTrue(sceneActivationSource, sceneActivationSource.contains(
        "public void handleActiveChanged(Boolean isActive,Integer activationCount)"));
    assertTrue(sceneActivationSource, sceneActivationSource.contains(
        "this.addSceneActivationListener((SceneActivationEvent p0) ->"));
    assertTrue(sceneActivationSource, sceneActivationSource.contains(
        "StoryApiGeneratedSourceTest.recordSceneActivationRuntimeDispatch(p0);"));
    assertTrue(timeDispatchSource, timeDispatchSource.contains("this.addTimeListener((TimeEvent p0) ->"));
    assertTrue(timeDispatchSource, timeDispatchSource.contains("StoryApiGeneratedSourceTest.recordTimeEvent();"));
    assertTrue(timeListenerSource, timeListenerSource.contains("this.addTimeListener((TimeEvent p0) ->"));
    assertTrue(timeListenerSource, timeListenerSource.contains(
        "StoryApiGeneratedSourceTest.recordTimeEventElapsed(p0.getTimeSinceLastFire());"));
  }

  public static void recordSceneActivationRuntimeDispatch(SceneActivationEvent event) {
  }

  public static void recordTimeEvent() {
  }

  public static void recordTimeEventElapsed(Double timeSinceLastFire) {
  }

  private static String generate(NamedUserType type) {
    JavaCodeGenerator generator = JavaCodeUtilities.createJavaCodeGeneratorBuilder().build();
    type.process(generator);
    return generator.getText();
  }

  private static NamedUserType programType(String name) {
    NamedUserConstructor constructor = new NamedUserConstructor();
    constructor.body.setValue(new ConstructorBlockStatement());
    NamedUserType type = new NamedUserType();
    type.name.setValue(name);
    type.superType.setValue(JavaType.getInstance(SProgram.class));
    type.constructors.add(constructor);
    return type;
  }

  private static NamedUserType programTypeWithProgramStoryCalls() {
    NamedUserType type = programType("Program");
    JavaMethod setSimulationSpeedFactor =
        AstMethodLookupHelpers.lookupMethod(SProgram.class, "setSimulationSpeedFactor", Number.class);
    JavaMethod setActiveScene = AstMethodLookupHelpers.lookupMethod(SProgram.class, "setActiveScene", SScene.class);
    type.methods.add(new UserMethod(
        "configureStory",
        Void.TYPE,
        new UserParameter[0],
        new BlockStatement(AstUtilities.createMethodInvocationStatement(
            new ThisExpression(),
            setSimulationSpeedFactor,
            new DoubleLiteral(1.5)))));
    type.methods.add(new UserMethod(
        "clearScene",
        Void.TYPE,
        new UserParameter[0],
        new BlockStatement(AstUtilities.createMethodInvocationStatement(
            new ThisExpression(),
            setActiveScene,
            new NullLiteral()))));
    return type;
  }

  private static NamedUserType programTypeWithSceneModelAndRenderingCalls(NamedUserType sceneType) {
    NamedUserType type = programType("Program");
    UserField scene = new UserField("scene", sceneType);
    UserField box = new UserField("box", SBox.class);
    type.fields.add(scene);
    type.fields.add(box);

    JavaMethod setActiveScene = AstMethodLookupHelpers.lookupMethod(SProgram.class, "setActiveScene", SScene.class);
    JavaMethod setPaint = AstMethodLookupHelpers.lookupMethod(SModel.class, "setPaint", Paint.class, SetPaint.Detail[].class);
    JavaMethod setOpacity = AstMethodLookupHelpers.lookupMethod(SModel.class, "setOpacity", Number.class, SetOpacity.Detail[].class);
    JavaMethod say = AstMethodLookupHelpers.lookupMethod(SModel.class, "say", String.class, Say.Detail[].class);
    type.methods.add(new UserMethod(
        "configureWorld",
        Void.TYPE,
        new UserParameter[0],
        new BlockStatement(
            AstUtilities.createMethodInvocationStatement(
                new ThisExpression(),
                setActiveScene,
                new FieldAccess(new ThisExpression(), scene)),
            AstUtilities.createMethodInvocationStatement(
                new FieldAccess(new ThisExpression(), box),
                setPaint,
                AstUtilities.createStaticFieldAccess(Color.class, "RED")),
            AstUtilities.createMethodInvocationStatement(
                new FieldAccess(new ThisExpression(), box),
                setOpacity,
                new DoubleLiteral(0.5)),
            AstUtilities.createMethodInvocationStatement(
                new FieldAccess(new ThisExpression(), box),
                say,
                new StringLiteral("hello box")))));
    return type;
  }

  private static NamedUserType sceneTypeWithEventAndRenderingCalls() {
    NamedUserType type = AstUtilities.createType("Scene", JavaType.getInstance(SScene.class));
    JavaMethod setAtmosphereColor = AstMethodLookupHelpers.lookupMethod(
        SScene.class,
        "setAtmosphereColor",
        Color.class,
        SetAtmosphereColor.Detail[].class);
    JavaMethod setFogDensity = AstMethodLookupHelpers.lookupMethod(
        SScene.class,
        "setFogDensity",
        Number.class,
        SetFogDensity.Detail[].class);
    JavaMethod addTimeListener = AstMethodLookupHelpers.lookupMethod(
        SScene.class,
        "addTimeListener",
        TimeListener.class,
        Number.class,
        AddTimeListener.Detail[].class);
    JavaMethod addSceneActivationListener = AstMethodLookupHelpers.lookupMethod(
        SScene.class,
        "addSceneActivationListener",
        SceneActivationListener.class);
    type.methods.add(new UserMethod(
        "handleActiveChanged",
        Void.TYPE,
        new UserParameter[] {
            new UserParameter("isActive", Boolean.class),
            new UserParameter("activationCount", Integer.class)
        },
        new BlockStatement(
            AstUtilities.createMethodInvocationStatement(
                new ThisExpression(),
                setAtmosphereColor,
                AstUtilities.createStaticFieldAccess(Color.class, "BLUE")),
            AstUtilities.createMethodInvocationStatement(
                new ThisExpression(),
                setFogDensity,
                new DoubleLiteral(0.25)),
            AstUtilities.createMethodInvocationStatement(
                new ThisExpression(),
                addTimeListener,
                new NullLiteral(),
                new IntegerLiteral(1)),
            AstUtilities.createMethodInvocationStatement(
                new ThisExpression(),
                addSceneActivationListener,
                new NullLiteral()))));
    return type;
  }

  private static NamedUserType sceneTypeWithSceneActivationListenerLambda() {
    NamedUserType type = AstUtilities.createType("Scene", JavaType.getInstance(SScene.class));
    JavaMethod addSceneActivationListener = AstMethodLookupHelpers.lookupMethod(
        SScene.class,
        "addSceneActivationListener",
        SceneActivationListener.class);
    type.methods.add(new UserMethod(
        "handleActiveChanged",
        Void.TYPE,
        new UserParameter[] {
            new UserParameter("isActive", Boolean.class),
            new UserParameter("activationCount", Integer.class)
        },
        new BlockStatement(AstUtilities.createMethodInvocationStatement(
            new ThisExpression(),
            addSceneActivationListener,
            sceneActivationRuntimeDispatchListenerLambda()))));
    return type;
  }

  private static NamedUserType sceneTypeWithTimeListenerElapsedLambda() {
    NamedUserType type = AstUtilities.createType("Scene", JavaType.getInstance(SScene.class));
    JavaMethod addTimeListener = AstMethodLookupHelpers.lookupMethod(
        SScene.class,
        "addTimeListener",
        TimeListener.class,
        Number.class,
        AddTimeListener.Detail[].class);
    type.methods.add(new UserMethod(
        "handleActiveChanged",
        Void.TYPE,
        new UserParameter[] {
            new UserParameter("isActive", Boolean.class),
            new UserParameter("activationCount", Integer.class)
        },
        new BlockStatement(AstUtilities.createMethodInvocationStatement(
            new ThisExpression(),
            addTimeListener,
            elapsedTimeListenerLambda(),
            new IntegerLiteral(1)))));
    return type;
  }

  private static NamedUserType sceneTypeWithTimeListenerDispatchLambda() {
    NamedUserType type = AstUtilities.createType("Scene", JavaType.getInstance(SScene.class));
    JavaMethod addTimeListener = AstMethodLookupHelpers.lookupMethod(
        SScene.class,
        "addTimeListener",
        TimeListener.class,
        Number.class,
        AddTimeListener.Detail[].class);
    type.methods.add(new UserMethod(
        "handleActiveChanged",
        Void.TYPE,
        new UserParameter[] {
            new UserParameter("isActive", Boolean.class),
            new UserParameter("activationCount", Integer.class)
        },
        new BlockStatement(AstUtilities.createMethodInvocationStatement(
            new ThisExpression(),
            addTimeListener,
            timeListenerDispatchLambda(),
            new IntegerLiteral(1)))));
    return type;
  }

  private static LambdaExpression sceneActivationRuntimeDispatchListenerLambda() {
    LambdaExpression expression = AstUtilities.createLambdaExpression(SceneActivationListener.class);
    UserLambda lambda = (UserLambda) expression.value.getValue();
    UserParameter eventParameter = lambda.requiredParameters.get(0);
    JavaMethod recordEvent = AstMethodLookupHelpers.lookupMethod(
        StoryApiGeneratedSourceTest.class,
        "recordSceneActivationRuntimeDispatch",
        SceneActivationEvent.class);
    lambda.body.getValue().statements.add(AstUtilities.createMethodInvocationStatement(
        new TypeExpression(recordEvent.getDeclaringType()),
        recordEvent,
        new ParameterAccess(eventParameter)));
    return expression;
  }

  private static LambdaExpression timeListenerDispatchLambda() {
    LambdaExpression expression = AstUtilities.createLambdaExpression(TimeListener.class);
    UserLambda lambda = (UserLambda) expression.value.getValue();
    JavaMethod recordEvent = AstMethodLookupHelpers.lookupMethod(
        StoryApiGeneratedSourceTest.class,
        "recordTimeEvent");
    lambda.body.getValue().statements.add(AstUtilities.createMethodInvocationStatement(
        new TypeExpression(recordEvent.getDeclaringType()),
        recordEvent));
    return expression;
  }

  private static LambdaExpression elapsedTimeListenerLambda() {
    LambdaExpression expression = AstUtilities.createLambdaExpression(TimeListener.class);
    UserLambda lambda = (UserLambda) expression.value.getValue();
    UserParameter eventParameter = lambda.requiredParameters.get(0);
    JavaMethod timeSinceLastFire = AstMethodLookupHelpers.lookupMethod(TimeEvent.class, "getTimeSinceLastFire");
    JavaMethod recordEvent = AstMethodLookupHelpers.lookupMethod(
        StoryApiGeneratedSourceTest.class,
        "recordTimeEventElapsed",
        Double.class);
    lambda.body.getValue().statements.add(AstUtilities.createMethodInvocationStatement(
        new TypeExpression(recordEvent.getDeclaringType()),
        recordEvent,
        AstUtilities.createMethodInvocation(
            new ParameterAccess(eventParameter),
            timeSinceLastFire)));
    return expression;
  }
}
