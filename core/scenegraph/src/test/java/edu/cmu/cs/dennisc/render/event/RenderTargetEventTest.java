package edu.cmu.cs.dennisc.render.event;

import edu.cmu.cs.dennisc.render.RenderTarget;
import org.junit.Test;

import java.lang.reflect.Proxy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class RenderTargetEventTest {
  @Test
  public void resizeEventRetainsTargetAndDimensions() {
    RenderTarget target = createRenderTarget();

    RenderTargetResizeEvent event = new RenderTargetResizeEvent(target, 640, 480);

    assertSame(target, event.getTypedSource());
    assertEquals(640, event.getWidth());
    assertEquals(480, event.getHeight());
    assertFalse(event.isReservedForReuse());
  }

  @Test
  public void initializeEventRetainsTargetAndDimensions() {
    RenderTarget target = createRenderTarget();

    RenderTargetInitializeEvent event = new RenderTargetInitializeEvent(target, 800, 600);

    assertSame(target, event.getTypedSource());
    assertEquals(800, event.getWidth());
    assertEquals(600, event.getHeight());
  }

  @Test
  public void displayChangeEventRetainsTargetAndFlags() {
    RenderTarget target = createRenderTarget();

    RenderTargetDisplayChangeEvent event = new RenderTargetDisplayChangeEvent(target, true, false);

    assertSame(target, event.getTypedSource());
    assertTrue(event.isModeChanged());
    assertFalse(event.isDeviceChanged());
  }

  private static RenderTarget createRenderTarget() {
    return (RenderTarget) Proxy.newProxyInstance(
        RenderTarget.class.getClassLoader(),
        new Class<?>[]{RenderTarget.class},
        (proxy, method, args) -> {
          Class<?> returnType = method.getReturnType();
          if (Boolean.TYPE.equals(returnType)) {
            return false;
          }
          if (Integer.TYPE.equals(returnType)) {
            return 0;
          }
          return null;
        }
    );
  }
}
