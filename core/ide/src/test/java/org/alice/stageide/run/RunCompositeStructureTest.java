package org.alice.stageide.run;

import org.junit.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

/**
 * Structural tests for run-related classes.
 */
public class RunCompositeStructureTest {

  // ---- RunComposite ----

  @Test
  public void runComposite_classIsAccessible() {
    assertNotNull(RunComposite.class);
  }

  @Test
  public void runComposite_isPublic() {
    assertTrue(Modifier.isPublic(RunComposite.class.getModifiers()));
  }

  @Test
  public void runComposite_isNotAbstract() {
    assertFalse(Modifier.isAbstract(RunComposite.class.getModifiers()));
  }

  @Test
  public void runComposite_hasGetInstanceMethod() throws Exception {
    Method m = RunComposite.class.getMethod("getInstance");
    assertNotNull(m);
    assertTrue(Modifier.isStatic(m.getModifiers()));
    assertTrue(Modifier.isPublic(m.getModifiers()));
  }

  @Test
  public void runComposite_getInstanceReturnType() throws Exception {
    Method m = RunComposite.class.getMethod("getInstance");
    assertEquals(RunComposite.class, m.getReturnType());
  }

  @Test
  public void runComposite_hasGetLaunchOperationMethod() throws Exception {
    Method m = RunComposite.class.getMethod("getLaunchOperation");
    assertNotNull(m);
    assertFalse(Modifier.isStatic(m.getModifiers()));
  }

  // ---- FastForwardToStatementOperation ----

  @Test
  public void fastForwardToStatementOperation_classIsAccessible() {
    assertNotNull(FastForwardToStatementOperation.class);
  }

  @Test
  public void fastForwardToStatementOperation_isPublic() {
    assertTrue(Modifier.isPublic(FastForwardToStatementOperation.class.getModifiers()));
  }

  @Test
  public void fastForwardToStatementOperation_isNotAbstract() {
    assertFalse(Modifier.isAbstract(FastForwardToStatementOperation.class.getModifiers()));
  }

  // ---- RunIcon ----

  @Test
  public void runIcon_classIsAccessible() throws ClassNotFoundException {
    Class<?> cls = Class.forName("org.alice.stageide.run.views.icons.RunIcon");
    assertNotNull(cls);
  }

  @Test
  public void runIcon_isPublic() throws ClassNotFoundException {
    Class<?> cls = Class.forName("org.alice.stageide.run.views.icons.RunIcon");
    assertTrue(Modifier.isPublic(cls.getModifiers()));
  }

  // ---- All run classes are public ----

  @Test
  public void allRunClasses_arePublic() {
    assertTrue(Modifier.isPublic(RunComposite.class.getModifiers()));
    assertTrue(Modifier.isPublic(FastForwardToStatementOperation.class.getModifiers()));
  }

  @Test
  public void allRunClasses_areNotFinal() {
    assertFalse(Modifier.isFinal(RunComposite.class.getModifiers()));
    assertFalse(Modifier.isFinal(FastForwardToStatementOperation.class.getModifiers()));
  }
}
