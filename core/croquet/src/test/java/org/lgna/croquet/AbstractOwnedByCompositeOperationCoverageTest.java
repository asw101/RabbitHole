package org.lgna.croquet;

import org.junit.Before;
import org.junit.Test;
import org.lgna.croquet.history.UserActivity;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;

import static org.junit.Assert.*;

public class AbstractOwnedByCompositeOperationCoverageTest {
  private Group group;
  private RecordingInitializer initializer;
  private RecordingCompositeInvocationHandler handler;
  private OperationOwningComposite<?> composite;
  private TestAbstractOwnedByCompositeOperation operation;

  @Before
  public void setUp() {
    group = Group.getInstance(CroquetTestUtils.nextTestUUID(), "abstractOwnedByCompositeCoverage");
    initializer = new RecordingInitializer();
    handler = new RecordingCompositeInvocationHandler();
    composite = createComposite(handler);
    operation = new TestAbstractOwnedByCompositeOperation(group, CroquetTestUtils.nextTestUUID(), initializer, composite, "subKey");
  }

  // ── Class structure ───────────────────────────────────────────────

  @Test
  public void class_isPublicAbstractAndExtendsOperation() {
    assertTrue(Modifier.isPublic(AbstractOwnedByCompositeOperation.class.getModifiers()));
    assertTrue(Modifier.isAbstract(AbstractOwnedByCompositeOperation.class.getModifiers()));
    assertEquals(Operation.class, AbstractOwnedByCompositeOperation.class.getSuperclass());
  }

  @Test
  public void constructor_and_initializerField_matchSource() throws Exception {
    Constructor<AbstractOwnedByCompositeOperation> constructor =
        AbstractOwnedByCompositeOperation.class.getConstructor(Group.class, java.util.UUID.class, Initializer.class);
    Field initializerField = AbstractOwnedByCompositeOperation.class.getDeclaredField("initializer");
    assertTrue(Modifier.isPublic(constructor.getModifiers()));
    assertEquals(Initializer.class, initializerField.getType());
    assertTrue(Modifier.isPrivate(initializerField.getModifiers()));
    assertTrue(Modifier.isFinal(initializerField.getModifiers()));
  }

  // ── Abstract and final hooks ──────────────────────────────────────

  @Test
  public void abstractHooks_areProtectedAbstract() throws Exception {
    Method getComposite = AbstractOwnedByCompositeOperation.class.getDeclaredMethod("getComposite");
    Method getClassUsedForLocalization = AbstractOwnedByCompositeOperation.class.getDeclaredMethod("getClassUsedForLocalization");
    Method getSubKeyForLocalization = AbstractOwnedByCompositeOperation.class.getDeclaredMethod("getSubKeyForLocalization");
    assertTrue(Modifier.isProtected(getComposite.getModifiers()) && Modifier.isAbstract(getComposite.getModifiers()));
    assertTrue(Modifier.isProtected(getClassUsedForLocalization.getModifiers()) && Modifier.isAbstract(getClassUsedForLocalization.getModifiers()));
    assertTrue(Modifier.isProtected(getSubKeyForLocalization.getModifiers()) && Modifier.isAbstract(getSubKeyForLocalization.getModifiers()));
  }

  @Test
  public void performInActivity_isProtectedFinalVoid() throws Exception {
    Method performInActivity = AbstractOwnedByCompositeOperation.class.getDeclaredMethod("performInActivity", UserActivity.class);
    assertEquals(void.class, performInActivity.getReturnType());
    assertTrue(Modifier.isProtected(performInActivity.getModifiers()));
    assertTrue(Modifier.isFinal(performInActivity.getModifiers()));
  }

  // ── Runtime behavior ──────────────────────────────────────────────

  @Test
  public void performInActivity_setsCompletionModel_andInvokesInitializerBeforeComposite() {
    UserActivity activity = new UserActivity();
    operation.invokePerformInActivity(activity);
    assertSame(operation, activity.getCompletionModel());
    assertSame(composite, initializer.lastValue);
    assertEquals(1, initializer.callCount);
    assertSame(activity, handler.lastActivity);
    assertEquals(1, handler.performCount);
    assertEquals("initializer>perform>", handler.log.toString());
  }

  @Test
  public void performInActivity_withNullInitializer_stillPerformsComposite() {
    TestAbstractOwnedByCompositeOperation withoutInitializer =
        new TestAbstractOwnedByCompositeOperation(group, CroquetTestUtils.nextTestUUID(), null, composite, "otherKey");
    UserActivity activity = new UserActivity();
    handler.reset();
    withoutInitializer.invokePerformInActivity(activity);
    assertSame(withoutInitializer, activity.getCompletionModel());
    assertEquals(1, handler.performCount);
    assertEquals("perform>", handler.log.toString());
  }

  @Test
  public void inheritedOperationApi_remainsUsableOnConcreteSubclass() {
    assertSame(group, operation.getGroup());
    assertNotNull(operation.getMigrationId());
    assertNotNull(operation.getImp());
    assertNotNull(operation.getMenuItemPrepModel());
    assertSame(operation.getMenuItemPrepModel(), operation.getMenuItemPrepModel());
  }

  @SuppressWarnings("unchecked")
  private OperationOwningComposite<?> createComposite(RecordingCompositeInvocationHandler invocationHandler) {
    return (OperationOwningComposite<?>) Proxy.newProxyInstance(getClass().getClassLoader(),
        new Class<?>[]{OperationOwningComposite.class}, invocationHandler);
  }

  private final class RecordingInitializer implements Initializer<OperationOwningComposite<?>> {
    private OperationOwningComposite<?> lastValue; private int callCount;
    @Override public void initialize(OperationOwningComposite<?> value) { lastValue = value; callCount++; handler.log.append("initializer>"); }
  }

  private static final class RecordingCompositeInvocationHandler implements InvocationHandler {
    private UserActivity lastActivity; private int performCount; private final StringBuilder log = new StringBuilder();
    @Override public Object invoke(Object proxy, Method method, Object[] args) {
      String name = method.getName();
      if ("equals".equals(name)) return proxy == args[0];
      if ("hashCode".equals(name)) return System.identityHashCode(proxy);
      if ("toString".equals(name)) return "RecordingCompositeProxy";
      if ("perform".equals(name)) { lastActivity = (UserActivity) args[0]; performCount++; log.append("perform>"); return null; }
      if ("modifyNameIfNecessary".equals(name)) return args[0];
      if ("appendUserRepr".equals(name) && args != null) { ((StringBuilder) args[0]).append("proxy"); return null; }
      if ("getCardId".equals(name)) return CroquetTestUtils.nextTestUUID();
      if ("contains".equals(name)) return false;
      if (method.getReturnType() == boolean.class) return false;
      return null;
    }
    private void reset() { lastActivity = null; performCount = 0; log.setLength(0); }
  }

  private static final class TestAbstractOwnedByCompositeOperation extends AbstractOwnedByCompositeOperation<OperationOwningComposite<?>> {
    private final OperationOwningComposite<?> composite; private final String subKey;
    private TestAbstractOwnedByCompositeOperation(Group group, java.util.UUID migrationId,
        Initializer<OperationOwningComposite<?>> initializer, OperationOwningComposite<?> composite, String subKey) {
      super(group, migrationId, initializer); this.composite = composite; this.subKey = subKey;
    }
    @Override protected OperationOwningComposite<?> getComposite() { return composite; }
    @Override protected Class<? extends Element> getClassUsedForLocalization() { return TestAbstractOwnedByCompositeOperation.class; }
    @Override protected String getSubKeyForLocalization() { return subKey; }
    void invokePerformInActivity(UserActivity activity) { this.performInActivity(activity); }
    @Override protected void localize() { }
  }
}
