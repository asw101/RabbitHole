package org.lgna.croquet.views;

import org.junit.Test;

import java.awt.GraphicsEnvironment;

import static org.junit.Assert.*;

/**
 * TDD tests for Frame headless guard.
 *
 * <p>{@code Frame.applicationRootFrame} is initialized via
 * {@code new Frame(WindowStack.getRootFrame())} in a static initializer.
 * After the WindowStack headless guard, {@code getRootFrame()} returns
 * {@code null} in headless mode. Without the Frame guard, passing
 * {@code null} to {@code AbstractWindow(JFrame)} puts {@code null} into
 * a {@code WeakHashMap} key, causing a {@code NullPointerException}.
 *
 * <p>The fix guards {@code applicationRootFrame} so it is {@code null}
 * in headless environments, avoiding the WeakHashMap NPE.
 *
 * <p>Before the fix, this test fails in headless CI:
 * <ul>
 *   <li>If WindowStack is unfixed: {@code ExceptionInInitializerError}</li>
 *   <li>If WindowStack is fixed but Frame isn't: {@code NullPointerException}
 *       from {@code WeakHashMap.put(null, this)}</li>
 * </ul>
 */
public class FrameHeadlessTest {

  @Test
  public void getApplicationRootFrame_returnsNullInHeadless() {
    if (GraphicsEnvironment.isHeadless()) {
      // In headless mode, applicationRootFrame should be null to avoid
      // NPE from AbstractWindow's WeakHashMap.put(null, this).
      assertNull("applicationRootFrame should be null in headless",
          Frame.getApplicationRootFrame());
    }
  }

  @Test
  public void getApplicationRootFrame_returnsFrameInHeaded() {
    if (!GraphicsEnvironment.isHeadless()) {
      Frame rootFrame = Frame.getApplicationRootFrame();
      assertNotNull("applicationRootFrame should be non-null in headed environments",
          rootFrame);
      assertNotNull("applicationRootFrame should wrap a JFrame",
          rootFrame.getAwtComponent());
    }
  }
}
