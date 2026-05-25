package org.alice.stageide.apis.story.event;

import org.junit.Test;
import org.lgna.project.ast.AbstractMethod;
import org.lgna.project.ast.Lambda;
import org.lgna.project.virtualmachine.LambdaContext;
import org.lgna.project.virtualmachine.UserInstance;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.junit.Assert.*;

public class AbstractAdapterBehaviorTest {
  private interface SingleMethodListener {
    void fired(String value);
  }

  private interface MultiMethodListener {
    void first();

    void second();
  }

  private static final class CapturingContext implements LambdaContext {
    private Lambda lambda;
    private AbstractMethod method;
    private UserInstance userInstance;
    private Object[] arguments;

    @Override
    public void invokeEntryPoint(Lambda lambda, AbstractMethod singleAbstractMethod, UserInstance thisInstance, Object... arguments) {
      this.lambda = lambda;
      this.method = singleAbstractMethod;
      this.userInstance = thisInstance;
      this.arguments = arguments;
    }
  }

  private static final class SingleMethodAdapter extends AbstractAdapter implements SingleMethodListener {
    SingleMethodAdapter(LambdaContext context, Lambda lambda, UserInstance userInstance) {
      super(context, lambda, userInstance);
    }

    @Override
    public void fired(String value) {
      invokeEntryPoint(value);
    }
  }

  private static final class MultiMethodAdapter extends AbstractAdapter implements MultiMethodListener {
    MultiMethodAdapter(LambdaContext context, Lambda lambda, UserInstance userInstance) {
      super(context, lambda, userInstance);
    }

    @Override
    public void first() {
      invokeEntryPoint("first");
    }

    @Override
    public void second() {
      invokeEntryPoint("second");
    }
  }

  @Test
  public void singleMethodInterfaceResolvesSingleAbstractMethod() throws Exception {
    SingleMethodAdapter adapter = new SingleMethodAdapter(new CapturingContext(), null, allocateUserInstance());

    assertNotNull(getSingleAbstractMethod(adapter));
  }

  @Test
  public void invokeEntryPointDelegatesResolvedMethodUserInstanceAndArguments() throws Exception {
    CapturingContext context = new CapturingContext();
    UserInstance userInstance = allocateUserInstance();
    SingleMethodAdapter adapter = new SingleMethodAdapter(context, null, userInstance);

    adapter.fired("hello");

    assertSame(getSingleAbstractMethod(adapter), context.method);
    assertSame(userInstance, context.userInstance);
    assertArrayEquals(new Object[] {"hello"}, context.arguments);
    assertNull(context.lambda);
  }

  @Test
  public void multiMethodInterfaceLeavesSingleAbstractMethodUnset() throws Exception {
    CapturingContext context = new CapturingContext();
    MultiMethodAdapter adapter = new MultiMethodAdapter(context, null, allocateUserInstance());

    assertNull(getSingleAbstractMethod(adapter));

    adapter.first();
    assertNull(context.method);
  }

  private static AbstractMethod getSingleAbstractMethod(AbstractAdapter adapter) throws Exception {
    Field field = AbstractAdapter.class.getDeclaredField("singleAbstractMethod");
    field.setAccessible(true);
    return (AbstractMethod) field.get(adapter);
  }

  private static UserInstance allocateUserInstance() throws Exception {
    Class<?> unsafeClass = Class.forName("sun.misc.Unsafe");
    Field field = unsafeClass.getDeclaredField("theUnsafe");
    field.setAccessible(true);
    Object unsafe = field.get(null);
    Method allocateInstance = unsafeClass.getMethod("allocateInstance", Class.class);
    return (UserInstance) allocateInstance.invoke(unsafe, UserInstance.class);
  }
}
