package org.alice.stageide.ast;

import org.alice.stageide.StageIDE;
import org.junit.Test;
import org.lgna.project.annotations.ArrayTemplate;
import org.lgna.project.ast.AstUtilities;
import org.lgna.project.ast.BlockStatement;
import org.lgna.project.ast.ExpressionStatement;
import org.lgna.project.ast.JavaMethod;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.LambdaExpression;
import org.lgna.project.ast.MethodInvocation;
import org.lgna.project.ast.NamedUserType;
import org.lgna.project.ast.ThisExpression;
import org.lgna.project.ast.UserMethod;
import org.lgna.story.SJoint;
import org.lgna.story.SScene;
import org.lgna.story.ast.EventListenerMethodUtilities;
import org.lgna.story.event.SceneActivationListener;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static org.junit.Assert.*;

public class StageIDEAstUtilitiesBehaviorTest {
  @Test
  public void jointGetterRecognizesPublicJavaMethodReturningSJoint() throws Exception {
    JavaMethod method = JavaMethod.getInstance(SampleJointMethods.class.getMethod("getTail"));

    assertTrue(JointMethodUtilities.isJointGetter(method));
    assertEquals("Tail", JointMethodUtilities.getJointName(method, Locale.JAPANESE));
  }

  @Test
  public void jointArrayGetterUsesArrayTemplateLengthWhenPresent() throws Exception {
    JavaMethod method = JavaMethod.getInstance(SampleJointMethods.class.getMethod("getWings"));

    assertTrue(JointMethodUtilities.isJointArrayGetter(method));
    assertEquals(3, JointMethodUtilities.getJointArrayLength(method));
  }

  @Test
  public void jointArrayGetterWithoutArrayTemplateReturnsNegativeOne() throws Exception {
    JavaMethod method = JavaMethod.getInstance(SampleJointMethods.class.getMethod("getFins"));

    assertTrue(JointMethodUtilities.isJointArrayGetter(method));
    assertEquals(-1, JointMethodUtilities.getJointArrayLength(method));
    assertEquals("swim", JointMethodUtilities.getJointName(JavaMethod.getInstance(SampleJointMethods.class.getMethod("swim")), Locale.ENGLISH));
  }

  @Test
  public void sceneActivationTraversalDeduplicatesMethodsAcrossListeners() {
    NamedUserType sceneType = AstUtilities.createType("Scene", JavaType.getInstance(SScene.class));
    UserMethod initialize = createMethod(StageIDE.INITIALIZE_EVENT_LISTENERS_METHOD_NAME);
    UserMethod first = createMethod("first");
    UserMethod second = createMethod("second");
    sceneType.methods.add(first);
    sceneType.methods.add(second);
    sceneType.methods.add(initialize);

    initialize.body.getValue().statements.add(createSceneActivationStatement(first, second, first));
    initialize.body.getValue().statements.add(createSceneActivationStatement(second));

    List<UserMethod> methods = StoryApiSpecificAstUtilities.getUserMethodsInvokedSceneActivationListeners(sceneType);

    assertEquals(Arrays.asList(first, second), methods);
  }

  @Test
  public void sceneActivationTraversalIgnoresDirectMethodInvocationsOutsideListeners() {
    NamedUserType sceneType = AstUtilities.createType("Scene", JavaType.getInstance(SScene.class));
    UserMethod initialize = createMethod(StageIDE.INITIALIZE_EVENT_LISTENERS_METHOD_NAME);
    UserMethod helper = createMethod("helper");
    sceneType.methods.add(helper);
    sceneType.methods.add(initialize);

    initialize.body.getValue().statements.add(new ExpressionStatement(new MethodInvocation(new ThisExpression(), helper)));

    assertTrue(StoryApiSpecificAstUtilities.getUserMethodsInvokedSceneActivationListeners(sceneType).isEmpty());
  }

  private static ExpressionStatement createSceneActivationStatement(UserMethod... methods) {
    org.lgna.project.ast.UserLambda lambda = AstUtilities.createUserLambda(SceneActivationListener.class);
    for (UserMethod method : methods) {
      lambda.body.getValue().statements.add(new ExpressionStatement(new MethodInvocation(new ThisExpression(), method)));
    }
    return AstUtilities.createMethodInvocationStatement(
        new ThisExpression(),
        EventListenerMethodUtilities.ADD_SCENE_ACTIVATION_LISTENER_METHOD,
        new LambdaExpression(lambda));
  }

  private static UserMethod createMethod(String name) {
    return new UserMethod(name, JavaType.VOID_TYPE, new org.lgna.project.ast.UserParameter[0], new BlockStatement());
  }

  @SuppressWarnings("unused")
  private static final class SampleJointMethods {
    public SJoint getTail() {
      return null;
    }

    @ArrayTemplate(length = 3)
    public SJoint[] getWings() {
      return null;
    }

    public SJoint[] getFins() {
      return null;
    }

    public void swim() {
    }
  }
}
