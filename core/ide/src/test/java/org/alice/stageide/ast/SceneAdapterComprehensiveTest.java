package org.alice.stageide.ast;

import org.junit.Test;
import org.lgna.project.ast.AbstractMethod;
import org.lgna.project.ast.BlockStatement;
import org.lgna.project.ast.NamedUserConstructor;
import org.lgna.project.ast.NamedUserType;
import org.lgna.project.ast.UserField;
import org.lgna.project.ast.UserMethod;
import org.lgna.project.ast.UserPackage;
import org.lgna.project.ast.UserParameter;
import org.lgna.project.virtualmachine.MethodContext;
import org.lgna.story.SScene;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

public class SceneAdapterComprehensiveTest {

  private static NamedUserType createType() {
    UserMethod method = new UserMethod(
        "handleActiveChanged",
        void.class,
        new UserParameter[]{
            new UserParameter("isActive", Boolean.class),
            new UserParameter("activeCount", Integer.class)
        },
        new BlockStatement());
    return new NamedUserType(
        "TestScene",
        new UserPackage("test"),
        SScene.class,
        new NamedUserConstructor[0],
        new UserMethod[]{method},
        new UserField[0]);
  }

  private static Object getField(Object target, String name) throws Exception {
    Field field = SceneAdapter.class.getDeclaredField(name);
    field.setAccessible(true);
    return field.get(target);
  }

  @Test
  public void classExtendsSScene() {
    assertEquals(SScene.class, SceneAdapter.class.getSuperclass());
  }

  @Test
  public void contextFieldIsPrivateFinal() throws Exception {
    Field field = SceneAdapter.class.getDeclaredField("context");
    assertTrue(Modifier.isPrivate(field.getModifiers()));
    assertTrue(Modifier.isFinal(field.getModifiers()));
  }

  @Test
  public void typeFieldIsPrivateFinal() throws Exception {
    Field field = SceneAdapter.class.getDeclaredField("type");
    assertTrue(Modifier.isPrivate(field.getModifiers()));
    assertTrue(Modifier.isFinal(field.getModifiers()));
  }

  @Test
  public void constructorTakesMethodContextUserTypeAndObjectArray() throws Exception {
    assertNotNull(SceneAdapter.class.getConstructor(MethodContext.class, org.lgna.project.ast.UserType.class, Object[].class));
  }

  @Test
  public void constructorStoresContextReference() throws Exception {
    RecordingMethodContext context = new RecordingMethodContext();
    NamedUserType type = createType();
    SceneAdapter adapter = new SceneAdapter(context, type, new Object[0]);
    assertSame(context, getField(adapter, "context"));
  }

  @Test
  public void constructorStoresTypeReference() throws Exception {
    RecordingMethodContext context = new RecordingMethodContext();
    NamedUserType type = createType();
    SceneAdapter adapter = new SceneAdapter(context, type, new Object[0]);
    assertSame(type, getField(adapter, "type"));
  }

  @Test
  public void handleActiveChangedMethodExists() throws Exception {
    Method method = SceneAdapter.class.getMethod("handleActiveChanged", Boolean.class, Integer.class);
    assertEquals(void.class, method.getReturnType());
  }

  @Test
  public void handleActiveChangedDelegatesToMethodContext() {
    RecordingMethodContext context = new RecordingMethodContext();
    NamedUserType type = createType();
    SceneAdapter adapter = new SceneAdapter(context, type, new Object[0]);

    adapter.handleActiveChanged(Boolean.TRUE, Integer.valueOf(3));

    assertEquals(1, context.invokeCount);
  }

  @Test
  public void handleActiveChangedPassesDeclaredMethod() {
    RecordingMethodContext context = new RecordingMethodContext();
    NamedUserType type = createType();
    SceneAdapter adapter = new SceneAdapter(context, type, new Object[0]);

    adapter.handleActiveChanged(Boolean.TRUE, Integer.valueOf(5));

    assertSame(type.getDeclaredMethod("handleActiveChanged", Boolean.class, Integer.class), context.lastMethod);
  }

  @Test
  public void handleActiveChangedPassesArgumentsInOrder() {
    RecordingMethodContext context = new RecordingMethodContext();
    SceneAdapter adapter = new SceneAdapter(context, createType(), new Object[0]);

    adapter.handleActiveChanged(Boolean.FALSE, Integer.valueOf(9));

    assertEquals(Boolean.FALSE, context.lastArguments[0]);
    assertEquals(Integer.valueOf(9), context.lastArguments[1]);
  }

  @Test
  public void handleActiveChangedCanBeCalledRepeatedly() {
    RecordingMethodContext context = new RecordingMethodContext();
    SceneAdapter adapter = new SceneAdapter(context, createType(), new Object[0]);

    adapter.handleActiveChanged(Boolean.TRUE, Integer.valueOf(1));
    adapter.handleActiveChanged(Boolean.FALSE, Integer.valueOf(2));

    assertEquals(2, context.invokeCount);
    assertEquals(Boolean.FALSE, context.lastArguments[0]);
    assertEquals(Integer.valueOf(2), context.lastArguments[1]);
  }

  @Test
  public void preserveStateAndEventListenersIsCallable() {
    SceneAdapter adapter = new SceneAdapter(new RecordingMethodContext(), createType(), new Object[0]);
    adapter.preserveStateAndEventListeners();
  }

  @Test
  public void restoreStateAndEventListenersIsCallable() {
    SceneAdapter adapter = new SceneAdapter(new RecordingMethodContext(), createType(), new Object[0]);
    adapter.restoreStateAndEventListeners();
  }

  @Test
  public void preserveMethodDeclaredOnSceneAdapter() throws Exception {
    assertEquals(SceneAdapter.class, SceneAdapter.class.getMethod("preserveStateAndEventListeners").getDeclaringClass());
  }

  @Test
  public void restoreMethodDeclaredOnSceneAdapter() throws Exception {
    assertEquals(SceneAdapter.class, SceneAdapter.class.getMethod("restoreStateAndEventListeners").getDeclaringClass());
  }

  @Test
  public void contextInvocationReceivesUserMethod() {
    RecordingMethodContext context = new RecordingMethodContext();
    SceneAdapter adapter = new SceneAdapter(context, createType(), new Object[0]);

    adapter.handleActiveChanged(Boolean.TRUE, Integer.valueOf(4));

    assertTrue(context.lastMethod instanceof UserMethod);
  }

  private static final class RecordingMethodContext implements MethodContext {
    private AbstractMethod lastMethod;
    private Object[] lastArguments;
    private int invokeCount;

    @Override
    public void invokeEntryPoint(AbstractMethod method, Object... arguments) {
      invokeCount++;
      lastMethod = method;
      lastArguments = arguments;
    }
  }
}
