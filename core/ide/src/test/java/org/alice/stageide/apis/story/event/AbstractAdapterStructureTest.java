package org.alice.stageide.apis.story.event;

import org.junit.Test;
import org.lgna.project.ast.Lambda;
import org.lgna.project.virtualmachine.LambdaContext;
import org.lgna.project.virtualmachine.UserInstance;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class AbstractAdapterStructureTest {
  @Test
  public void classIsPublicAbstract() {
    assertTrue(Modifier.isPublic(AbstractAdapter.class.getModifiers()));
    assertTrue(Modifier.isAbstract(AbstractAdapter.class.getModifiers()));
  }

  @Test
  public void constructorTakesContextLambdaAndUserInstance() throws Exception {
    Constructor<AbstractAdapter> constructor = AbstractAdapter.class.getConstructor(LambdaContext.class, Lambda.class, UserInstance.class);
    assertTrue(Modifier.isPublic(constructor.getModifiers()));
  }

  @Test
  public void privateFinalFieldsMatchExpectedNames() {
    assertEquals(
        Arrays.asList("context", "lambda", "singleAbstractMethod", "userInstance"),
        Arrays.stream(AbstractAdapter.class.getDeclaredFields())
            .filter(field -> !field.isSynthetic())
            .map(Field::getName)
            .sorted()
            .collect(Collectors.toList()));
  }

  @Test
  public void invokeEntryPointIsProtectedVarArgs() throws Exception {
    Method method = AbstractAdapter.class.getDeclaredMethod("invokeEntryPoint", Object[].class);
    assertTrue(Modifier.isProtected(method.getModifiers()));
    assertTrue(method.isVarArgs());
  }
}
