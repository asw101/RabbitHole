package org.alice.ide.x.croquet;

import org.alice.ide.croquet.edits.ast.ExpressionPropertyEdit;
import org.alice.ide.x.croquet.edits.SceneEditorUpdatingExpressionPropertyEdit;
import org.junit.Test;
import org.lgna.croquet.history.UserActivity;
import org.lgna.project.ast.Expression;
import org.lgna.project.ast.ExpressionProperty;
import org.lgna.project.ast.InstanceCreation;
import org.lgna.project.ast.NullLiteral;
import org.lgna.project.ast.SimpleArgument;
import org.lgna.project.ast.UserField;
import org.lgna.project.ast.UserParameter;
import org.lgna.story.resources.ModelResource;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class SceneEditorUpdatingArgumentCascadeBehaviorTest {
  @Test
  public void getInstanceCachesBySimpleArgumentAndExposesArgumentExpressionProperty() {
    SimpleArgument argument = new SimpleArgument(new UserParameter("resource", ModelResource.class), new NullLiteral());

    SceneEditorUpdatingArgumentCascade first = SceneEditorUpdatingArgumentCascade.getInstance(argument);
    SceneEditorUpdatingArgumentCascade second = SceneEditorUpdatingArgumentCascade.getInstance(argument);

    assertSame(first, second);
    assertSame(argument, first.getArgument());
    assertSame(argument.expression, first.getExpressionProperty());
  }

  @Test
  public void getInstanceKeepsDistinctArgumentNodesSeparate() {
    UserParameter parameter = new UserParameter("resource", ModelResource.class);
    SimpleArgument firstArgument = new SimpleArgument(parameter, new NullLiteral());
    SimpleArgument secondArgument = new SimpleArgument(parameter, new NullLiteral());

    assertNotSame(SceneEditorUpdatingArgumentCascade.getInstance(firstArgument), SceneEditorUpdatingArgumentCascade.getInstance(secondArgument));
  }

  @Test
  public void createExpressionPropertyEditUsesSceneEditorUpdatingEditForModelResourceFieldInitializer() throws Exception {
    UserParameter parameter = new UserParameter("resource", ModelResource.class);
    FieldInitializerCase initializerCase = createFieldInitializerCase(parameter);
    SceneEditorUpdatingArgumentCascade cascade = SceneEditorUpdatingArgumentCascade.getInstance(initializerCase.argument);

    ExpressionPropertyEdit edit = invokeCreateExpressionPropertyEdit(cascade, initializerCase.argument, new NullLiteral());

    assertTrue(edit instanceof SceneEditorUpdatingExpressionPropertyEdit);
    assertSame(initializerCase.field, getSceneEditorField((SceneEditorUpdatingExpressionPropertyEdit) edit));
  }

  @Test
  public void createExpressionPropertyEditFallsBackForNonModelResourceArguments() throws Exception {
    UserParameter parameter = new UserParameter("count", Number.class);
    FieldInitializerCase initializerCase = createFieldInitializerCase(parameter);
    SceneEditorUpdatingArgumentCascade cascade = SceneEditorUpdatingArgumentCascade.getInstance(initializerCase.argument);

    ExpressionPropertyEdit edit = invokeCreateExpressionPropertyEdit(cascade, initializerCase.argument, new NullLiteral());

    assertSame(ExpressionPropertyEdit.class, edit.getClass());
  }

  private static ExpressionPropertyEdit invokeCreateExpressionPropertyEdit(SceneEditorUpdatingArgumentCascade cascade, SimpleArgument argument, Expression nextExpression) throws Exception {
    Method method = SceneEditorUpdatingArgumentCascade.class.getDeclaredMethod(
        "createExpressionPropertyEdit",
        UserActivity.class,
        ExpressionProperty.class,
        Expression.class,
        Expression.class
    );
    method.setAccessible(true);
    return (ExpressionPropertyEdit) method.invoke(cascade, null, argument.expression, argument.expression.getValue(), nextExpression);
  }

  private static UserField getSceneEditorField(SceneEditorUpdatingExpressionPropertyEdit edit) throws Exception {
    Field field = SceneEditorUpdatingExpressionPropertyEdit.class.getDeclaredField("field");
    field.setAccessible(true);
    return (UserField) field.get(edit);
  }

  private static FieldInitializerCase createFieldInitializerCase(UserParameter parameter) {
    SimpleArgument argument = new SimpleArgument(parameter, new NullLiteral());
    InstanceCreation instanceCreation = new InstanceCreation();
    instanceCreation.requiredArguments.add(argument);
    UserField field = new UserField("character", Object.class, instanceCreation);
    return new FieldInitializerCase(field, argument);
  }

  private static final class FieldInitializerCase {
    private final UserField field;
    private final SimpleArgument argument;

    private FieldInitializerCase(UserField field, SimpleArgument argument) {
      this.field = field;
      this.argument = argument;
    }
  }
}
