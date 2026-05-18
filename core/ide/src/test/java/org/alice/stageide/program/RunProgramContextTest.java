package org.alice.stageide.program;

import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

/**
 * Tests for {@link RunProgramContext} — hierarchy, constructor,
 * and API surface.
 */
public class RunProgramContextTest {

  // ---- class structure --------------------------------------------------

  @Test
  public void classIsLoadable() throws ClassNotFoundException {
    Class.forName("org.alice.stageide.program.RunProgramContext");
  }

  @Test
  public void extendsProgramContext() {
    assertTrue(ProgramContext.class.isAssignableFrom(RunProgramContext.class));
  }

  @Test
  public void classIsNotAbstract() {
    assertFalse(Modifier.isAbstract(RunProgramContext.class.getModifiers()));
  }

  // ---- constructor ------------------------------------------------------

  @Test
  public void constructor_acceptsNamedUserType() throws NoSuchMethodException {
    Constructor<?> ctor = RunProgramContext.class.getConstructor(
        org.lgna.project.ast.NamedUserType.class);
    assertTrue(Modifier.isPublic(ctor.getModifiers()));
  }

  // ---- initializeInContainer method -------------------------------------

  @Test
  public void initializeInContainerMethod_exists() throws NoSuchMethodException {
    Method m = RunProgramContext.class.getMethod("initializeInContainer",
        org.lgna.story.implementation.ProgramImp.AwtContainerInitializer.class);
    assertTrue(Modifier.isPublic(m.getModifiers()));
  }

  // ---- inherited methods from ProgramContext ----------------------------

  @Test
  public void getProgramInstanceMethod_inherited() throws NoSuchMethodException {
    Method m = RunProgramContext.class.getMethod("getProgramInstance");
    assertTrue(Modifier.isPublic(m.getModifiers()));
  }

  @Test
  public void getVirtualMachineMethod_inherited() throws NoSuchMethodException {
    Method m = RunProgramContext.class.getMethod("getVirtualMachine");
    assertTrue(Modifier.isPublic(m.getModifiers()));
  }

  @Test
  public void setActiveSceneMethod_inherited() throws NoSuchMethodException {
    Method m = RunProgramContext.class.getMethod("setActiveScene");
    assertTrue(Modifier.isPublic(m.getModifiers()));
  }

  @Test
  public void cleanUpProgramMethod_inherited() throws NoSuchMethodException {
    Method m = RunProgramContext.class.getMethod("cleanUpProgram");
    assertTrue(Modifier.isPublic(m.getModifiers()));
  }

  @Test
  public void getOnscreenRenderTargetMethod_inherited() throws NoSuchMethodException {
    Method m = RunProgramContext.class.getMethod("getOnscreenRenderTarget");
    assertTrue(Modifier.isPublic(m.getModifiers()));
  }

  // ---- ProgramContext is abstract ---------------------------------------

  @Test
  public void programContext_isAbstract() {
    assertTrue(Modifier.isAbstract(ProgramContext.class.getModifiers()));
  }
}
