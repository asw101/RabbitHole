package org.alice.ide.x.croquet;

import org.alice.ide.croquet.models.ast.cascade.ArgumentCascade;
import org.alice.ide.croquet.models.ast.cascade.ExpressionPropertyCascade;
import org.alice.ide.x.SceneEditorUpdatingProjectEditorAstI18nFactory;
import org.junit.Test;
import org.lgna.project.ast.InstanceCreation;
import org.lgna.project.ast.NullLiteral;
import org.lgna.project.ast.SimpleArgument;
import org.lgna.project.ast.UserField;
import org.lgna.project.ast.UserParameter;
import org.lgna.story.resources.ModelResource;

import java.lang.reflect.Method;

import static org.junit.Assert.assertSame;

public class SceneEditorUpdatingProjectEditorAstI18nFactoryBehaviorTest {
  @Test
  public void modelResourceFieldInitializerUsesCroquetSpecificCascade() throws Exception {
    FieldInitializerCase initializerCase = createFieldInitializerCase(new UserParameter("resource", ModelResource.class));

    ExpressionPropertyCascade cascade = invokeGetArgumentCascade(initializerCase.argument);

    assertSame(SceneEditorUpdatingArgumentCascade.getInstance(initializerCase.argument), cascade);
  }

  @Test
  public void nonModelResourceFieldInitializerFallsBackToDefaultArgumentCascade() throws Exception {
    FieldInitializerCase initializerCase = createFieldInitializerCase(new UserParameter("count", Number.class));

    ExpressionPropertyCascade cascade = invokeGetArgumentCascade(initializerCase.argument);

    assertSame(ArgumentCascade.getInstance(initializerCase.argument), cascade);
  }

  @Test
  public void modelResourceArgumentWithoutFieldInitializerFallsBackToDefaultArgumentCascade() throws Exception {
    SimpleArgument argument = new SimpleArgument(new UserParameter("resource", ModelResource.class), new NullLiteral());

    ExpressionPropertyCascade cascade = invokeGetArgumentCascade(argument);

    assertSame(ArgumentCascade.getInstance(argument), cascade);
  }

  @Test
  public void repeatedFactoryLookupsReuseCroquetCascadeInstance() throws Exception {
    FieldInitializerCase initializerCase = createFieldInitializerCase(new UserParameter("resource", ModelResource.class));

    ExpressionPropertyCascade first = invokeGetArgumentCascade(initializerCase.argument);
    ExpressionPropertyCascade second = invokeGetArgumentCascade(initializerCase.argument);

    assertSame(first, second);
    assertSame(SceneEditorUpdatingArgumentCascade.getInstance(initializerCase.argument), first);
  }

  private static ExpressionPropertyCascade invokeGetArgumentCascade(SimpleArgument argument) throws Exception {
    Method method = SceneEditorUpdatingProjectEditorAstI18nFactory.class.getDeclaredMethod("getArgumentCascade", SimpleArgument.class);
    method.setAccessible(true);
    return (ExpressionPropertyCascade) method.invoke(SceneEditorUpdatingProjectEditorAstI18nFactory.getInstance(), argument);
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
