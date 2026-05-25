package edu.cmu.cs.dennisc.render.gl;


import edu.cmu.cs.dennisc.render.gl.imp.RenderContext;
import org.junit.Test;

import java.lang.reflect.Method;

import static org.junit.Assert.*;

public class ForgettableBindingContractTest {

  @Test
  public void implementors_receiveTheRenderContextPassedToForget() {
    RecordingBinding binding = new RecordingBinding();
    RenderContext context = new RenderContext();

    binding.forget(context);

    assertSame(context, binding.lastContext);
    assertEquals(1, binding.callCount);
  }

  @Test
  public void interface_declaresSingleForgetMethod() {
    Method[] methods = ForgettableBinding.class.getDeclaredMethods();

    assertEquals(1, methods.length);
    assertEquals("forget", methods[0].getName());
    assertEquals(RenderContext.class, methods[0].getParameterTypes()[0]);
  }

  private static final class RecordingBinding implements ForgettableBinding {
    private RenderContext lastContext;
    private int callCount;

    @Override
    public void forget(RenderContext rc) {
      this.lastContext = rc;
      this.callCount++;
    }
  }
}
