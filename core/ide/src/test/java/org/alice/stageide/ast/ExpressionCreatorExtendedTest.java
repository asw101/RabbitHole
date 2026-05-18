package org.alice.stageide.ast;

import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class ExpressionCreatorExtendedTest {

  private static final Class<?> EXPRESSION_CREATOR = ExpressionCreator.class;
  private static final Class<?> EXPRESSION_TYPE = org.lgna.project.ast.Expression.class;

  private static Method declaredMethod(String name, Class<?>... parameterTypes) throws Exception {
    return EXPRESSION_CREATOR.getDeclaredMethod(name, parameterTypes);
  }

  private static void assertPrivateHelper(String name, Class<?>... parameterTypes) throws Exception {
    Method method = declaredMethod(name, parameterTypes);
    assertNotNull(method);
    assertTrue(name + " should be private", Modifier.isPrivate(method.getModifiers()));
    assertEquals(name + " should return Expression", EXPRESSION_TYPE, method.getReturnType());
  }

  @Test
  public void expressionCreator_isPublicConcreteSubclassOfIdeExpressionCreator() {
    assertTrue(Modifier.isPublic(EXPRESSION_CREATOR.getModifiers()));
    assertFalse(Modifier.isAbstract(EXPRESSION_CREATOR.getModifiers()));
    assertFalse(EXPRESSION_CREATOR.isInterface());
    assertSame(org.alice.ide.ast.ExpressionCreator.class, EXPRESSION_CREATOR.getSuperclass());
  }

  @Test
  public void createCustomExpression_isProtectedOverride() throws Exception {
    Method method = declaredMethod("createCustomExpression", Object.class);
    assertTrue(Modifier.isProtected(method.getModifiers()));
    assertEquals(EXPRESSION_TYPE, method.getReturnType());
    assertNotNull(org.alice.ide.ast.ExpressionCreator.class.getDeclaredMethod("createCustomExpression", Object.class));
  }

  @Test
  public void privateHelperMethods_existWithExpectedParameterTypes() throws Exception {
    Map<String, Class<?>> expectations = new HashMap<String, Class<?>>();
    expectations.put("createPositionExpression", org.lgna.story.Position.class);
    expectations.put("createOrientationExpression", org.lgna.story.Orientation.class);
    expectations.put("createScaleExpression", org.lgna.story.Scale.class);
    expectations.put("createSizeExpression", org.lgna.story.Size.class);
    expectations.put("createColorExpression", org.lgna.story.Color.class);
    expectations.put("createFontExpression", org.lgna.story.Font.class);
    expectations.put("createPaintExpression", org.lgna.story.Paint.class);
    expectations.put("createPoseExpression", org.lgna.story.Pose.class);
    expectations.put("createJointIdExpression", org.lgna.story.resources.JointId.class);
    expectations.put("createImageSourceExpression", org.lgna.story.ImageSource.class);
    expectations.put("createImagePaintExpression", org.lgna.story.ImagePaint.class);

    assertEquals(11, expectations.size());
    for (Map.Entry<String, Class<?>> entry : expectations.entrySet()) {
      assertPrivateHelper(entry.getKey(), entry.getValue());
    }
  }

  @Test
  public void primaryHelpers_coverRequestedStoryTypes() throws Exception {
    assertPrivateHelper("createPositionExpression", org.lgna.story.Position.class);
    assertPrivateHelper("createOrientationExpression", org.lgna.story.Orientation.class);
    assertPrivateHelper("createScaleExpression", org.lgna.story.Scale.class);
    assertPrivateHelper("createSizeExpression", org.lgna.story.Size.class);
    assertPrivateHelper("createColorExpression", org.lgna.story.Color.class);
    assertPrivateHelper("createPaintExpression", org.lgna.story.Paint.class);
  }

  @Test
  public void buildField_existsAndIsPrivateStaticFinalJavaMethod() throws Exception {
    Field field = EXPRESSION_CREATOR.getDeclaredField("BUILD");
    assertTrue(Modifier.isPrivate(field.getModifiers()));
    assertTrue(Modifier.isStatic(field.getModifiers()));
    assertTrue(Modifier.isFinal(field.getModifiers()));
    assertSame(org.lgna.project.ast.JavaMethod.class, field.getType());
  }

  @Test
  public void declaredMethodCount_isAtLeastTwelve() {
    assertTrue(EXPRESSION_CREATOR.getDeclaredMethods().length >= 12);
  }

  @Test
  public void helperMethodsReturnExpression() throws Exception {
    String[] names = {
        "createPositionExpression",
        "createOrientationExpression",
        "createScaleExpression",
        "createSizeExpression",
        "createColorExpression",
        "createFontExpression",
        "createPaintExpression",
        "createPoseExpression",
        "createJointIdExpression",
        "createImageSourceExpression",
        "createImagePaintExpression"
    };

    Class<?>[] parameterTypes = {
        org.lgna.story.Position.class,
        org.lgna.story.Orientation.class,
        org.lgna.story.Scale.class,
        org.lgna.story.Size.class,
        org.lgna.story.Color.class,
        org.lgna.story.Font.class,
        org.lgna.story.Paint.class,
        org.lgna.story.Pose.class,
        org.lgna.story.resources.JointId.class,
        org.lgna.story.ImageSource.class,
        org.lgna.story.ImagePaint.class
    };

    for (int i = 0; i < names.length; i++) {
      Method method = declaredMethod(names[i], parameterTypes[i]);
      assertEquals(EXPRESSION_TYPE, method.getReturnType());
    }
  }

  @Test
  public void helperMethodsAreDeclaredOnStageExpressionCreator() {
    String[] expectedNames = {
        "createPositionExpression",
        "createOrientationExpression",
        "createScaleExpression",
        "createSizeExpression",
        "createColorExpression",
        "createFontExpression",
        "createPaintExpression",
        "createPoseExpression",
        "createJointIdExpression",
        "createImageSourceExpression",
        "createImagePaintExpression",
        "createCustomExpression"
    };

    for (String expectedName : expectedNames) {
      boolean found = false;
      for (Method method : EXPRESSION_CREATOR.getDeclaredMethods()) {
        if (method.getName().equals(expectedName)) {
          found = true;
          break;
        }
      }
      assertTrue("Missing declared method: " + expectedName, found);
    }
  }

  @Test
  public void helperMethodsStayPrivateWhileOverrideRemainsProtected() throws Exception {
    String[] helperNames = {
        "createPositionExpression",
        "createOrientationExpression",
        "createScaleExpression",
        "createSizeExpression",
        "createColorExpression",
        "createFontExpression",
        "createPaintExpression",
        "createPoseExpression",
        "createJointIdExpression",
        "createImageSourceExpression",
        "createImagePaintExpression"
    };

    Class<?>[] helperParameterTypes = {
        org.lgna.story.Position.class,
        org.lgna.story.Orientation.class,
        org.lgna.story.Scale.class,
        org.lgna.story.Size.class,
        org.lgna.story.Color.class,
        org.lgna.story.Font.class,
        org.lgna.story.Paint.class,
        org.lgna.story.Pose.class,
        org.lgna.story.resources.JointId.class,
        org.lgna.story.ImageSource.class,
        org.lgna.story.ImagePaint.class
    };

    for (int i = 0; i < helperNames.length; i++) {
      assertTrue(Modifier.isPrivate(declaredMethod(helperNames[i], helperParameterTypes[i]).getModifiers()));
    }
    assertTrue(Modifier.isProtected(declaredMethod("createCustomExpression", Object.class).getModifiers()));
  }

  @Test
  public void declaredMethodsIncludeRequestedShape() {
    long privateCount = Arrays.stream(EXPRESSION_CREATOR.getDeclaredMethods())
        .filter(method -> Modifier.isPrivate(method.getModifiers()))
        .count();
    long protectedCount = Arrays.stream(EXPRESSION_CREATOR.getDeclaredMethods())
        .filter(method -> Modifier.isProtected(method.getModifiers()))
        .count();

    assertTrue(privateCount >= 11);
    assertTrue(protectedCount >= 1);
  }
}
