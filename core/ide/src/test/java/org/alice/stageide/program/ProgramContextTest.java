package org.alice.stageide.program;

import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

/**
 * Tests for {@link ProgramContext} — abstract class structure,
 * constructor contract, method surface, adapter registration,
 * and field layout.
 *
 * Also verifies {@link RunProgramContext} as the concrete subclass.
 *
 * Refactored: expanded from 1-test stub to comprehensive coverage
 * of the abstract contract and its sole concrete subclass.
 */
public class ProgramContextTest {

  private static final Class<?> PROGRAM_CTX = ProgramContext.class;

  // ---- class modifiers --------------------------------------------------

  @Test
  public void classIsAbstract() {
    assertTrue(Modifier.isAbstract(PROGRAM_CTX.getModifiers()));
  }

  @Test
  public void classIsPublic() {
    assertTrue(Modifier.isPublic(PROGRAM_CTX.getModifiers()));
  }

  @Test
  public void classIsNotFinal() {
    assertFalse(Modifier.isFinal(PROGRAM_CTX.getModifiers()));
  }

  @Test
  public void classIsNotInterface() {
    assertFalse(PROGRAM_CTX.isInterface());
  }

  // ---- constructor contract ---------------------------------------------

  @Test
  public void constructor_acceptsNamedUserType() throws NoSuchMethodException {
    Constructor<?> ctor = PROGRAM_CTX.getDeclaredConstructor(
        org.lgna.project.ast.NamedUserType.class);
    assertNotNull(ctor);
    assertTrue("constructor must be public", Modifier.isPublic(ctor.getModifiers()));
  }

  @Test
  public void constructor_onlyOnePublic() {
    long publicCtorCount = Arrays.stream(PROGRAM_CTX.getDeclaredConstructors())
        .filter(c -> Modifier.isPublic(c.getModifiers()))
        .count();
    assertEquals("ProgramContext should have exactly 1 public constructor", 1, publicCtorCount);
  }

  // ---- public accessor methods ------------------------------------------

  @Test
  public void getProgramInstanceMethod_exists() throws NoSuchMethodException {
    Method m = PROGRAM_CTX.getMethod("getProgramInstance");
    assertTrue(Modifier.isPublic(m.getModifiers()));
    assertEquals(org.lgna.project.virtualmachine.UserInstance.class, m.getReturnType());
  }

  @Test
  public void getProgramMethod_exists() throws NoSuchMethodException {
    Method m = PROGRAM_CTX.getMethod("getProgram");
    assertTrue(Modifier.isPublic(m.getModifiers()));
    assertEquals(org.lgna.story.SProgram.class, m.getReturnType());
  }

  @Test
  public void getProgramImpMethod_exists() throws NoSuchMethodException {
    Method m = PROGRAM_CTX.getMethod("getProgramImp");
    assertTrue(Modifier.isPublic(m.getModifiers()));
    assertEquals(org.lgna.story.implementation.ProgramImp.class, m.getReturnType());
  }

  @Test
  public void getVirtualMachineMethod_exists() throws NoSuchMethodException {
    Method m = PROGRAM_CTX.getMethod("getVirtualMachine");
    assertTrue(Modifier.isPublic(m.getModifiers()));
    assertEquals(org.lgna.project.virtualmachine.VirtualMachine.class, m.getReturnType());
  }

  @Test
  public void getOnscreenRenderTargetMethod_exists() throws NoSuchMethodException {
    Method m = PROGRAM_CTX.getMethod("getOnscreenRenderTarget");
    assertTrue(Modifier.isPublic(m.getModifiers()));
  }

  @Test
  public void setActiveSceneMethod_exists() throws NoSuchMethodException {
    Method m = PROGRAM_CTX.getMethod("setActiveScene");
    assertTrue(Modifier.isPublic(m.getModifiers()));
    assertEquals(void.class, m.getReturnType());
  }

  @Test
  public void cleanUpProgramMethod_exists() throws NoSuchMethodException {
    Method m = PROGRAM_CTX.getMethod("cleanUpProgram");
    assertTrue(Modifier.isPublic(m.getModifiers()));
    assertEquals(void.class, m.getReturnType());
  }

  // ---- protected template methods ---------------------------------------

  @Test
  public void createProgramInstanceMethod_isProtected() throws NoSuchMethodException {
    Method m = PROGRAM_CTX.getDeclaredMethod("createProgramInstance",
        org.lgna.project.ast.NamedUserType.class);
    assertTrue(Modifier.isProtected(m.getModifiers()));
  }

  @Test
  public void createVirtualMachineMethod_isProtected() throws NoSuchMethodException {
    Method m = PROGRAM_CTX.getDeclaredMethod("createVirtualMachine");
    assertTrue(Modifier.isProtected(m.getModifiers()));
  }

  // ---- package-private rendering methods --------------------------------

  @Test
  public void disableRenderingMethod_exists() throws NoSuchMethodException {
    Method m = PROGRAM_CTX.getDeclaredMethod("disableRendering");
    assertNotNull(m);
    assertFalse("disableRendering must not be public",
        Modifier.isPublic(m.getModifiers()));
  }

  // ---- private fields ---------------------------------------------------

  @Test
  public void programInstanceField_exists() throws NoSuchFieldException {
    Field f = PROGRAM_CTX.getDeclaredField("programInstance");
    assertTrue(Modifier.isPrivate(f.getModifiers()));
    assertTrue(Modifier.isFinal(f.getModifiers()));
  }

  @Test
  public void vmField_exists() throws NoSuchFieldException {
    Field f = PROGRAM_CTX.getDeclaredField("vm");
    assertTrue(Modifier.isPrivate(f.getModifiers()));
    assertTrue(Modifier.isFinal(f.getModifiers()));
  }

  // ---- public method set completeness -----------------------------------

  @Test
  public void publicMethods_includeExpectedSet() {
    Set<String> publicMethods = Arrays.stream(PROGRAM_CTX.getDeclaredMethods())
        .filter(m -> Modifier.isPublic(m.getModifiers()))
        .map(Method::getName)
        .collect(Collectors.toSet());

    String[] expected = {
        "getProgramInstance", "getProgram", "getProgramImp",
        "getVirtualMachine", "getOnscreenRenderTarget",
        "setActiveScene", "cleanUpProgram"
    };
    for (String name : expected) {
      assertTrue("missing public method: " + name, publicMethods.contains(name));
    }
  }

  // ---- RunProgramContext ------------------------------------------------

  @Test
  public void runProgramContext_extendsProgramContext() {
    assertTrue(PROGRAM_CTX.isAssignableFrom(RunProgramContext.class));
  }

  @Test
  public void runProgramContext_isNotAbstract() {
    assertFalse(Modifier.isAbstract(RunProgramContext.class.getModifiers()));
  }

  @Test
  public void runProgramContext_constructor_acceptsNamedUserType() throws NoSuchMethodException {
    Constructor<?> ctor = RunProgramContext.class.getConstructor(
        org.lgna.project.ast.NamedUserType.class);
    assertTrue(Modifier.isPublic(ctor.getModifiers()));
  }

  @Test
  public void runProgramContext_initializeInContainerMethod_exists() throws NoSuchMethodException {
    Method m = RunProgramContext.class.getMethod("initializeInContainer",
        org.lgna.story.implementation.ProgramImp.AwtContainerInitializer.class);
    assertTrue(Modifier.isPublic(m.getModifiers()));
  }

  @Test
  public void runProgramContext_inheritedGetProgramInstance() throws NoSuchMethodException {
    Method m = RunProgramContext.class.getMethod("getProgramInstance");
    assertTrue(Modifier.isPublic(m.getModifiers()));
  }

  @Test
  public void runProgramContext_inheritedGetVirtualMachine() throws NoSuchMethodException {
    Method m = RunProgramContext.class.getMethod("getVirtualMachine");
    assertTrue(Modifier.isPublic(m.getModifiers()));
  }

  @Test
  public void runProgramContext_inheritedSetActiveScene() throws NoSuchMethodException {
    Method m = RunProgramContext.class.getMethod("setActiveScene");
    assertTrue(Modifier.isPublic(m.getModifiers()));
  }

  @Test
  public void runProgramContext_inheritedCleanUpProgram() throws NoSuchMethodException {
    Method m = RunProgramContext.class.getMethod("cleanUpProgram");
    assertTrue(Modifier.isPublic(m.getModifiers()));
  }

  @Test
  public void runProgramContext_inheritedGetOnscreenRenderTarget() throws NoSuchMethodException {
    Method m = RunProgramContext.class.getMethod("getOnscreenRenderTarget");
    assertTrue(Modifier.isPublic(m.getModifiers()));
  }

  // ---- adapter registration patterns ------------------------------------

  private static final String[] ADAPTER_CLASSES = {
      "org.alice.stageide.apis.story.event.SceneActivationAdapter",
      "org.alice.stageide.apis.story.event.MouseClickOnScreenAdapter",
      "org.alice.stageide.apis.story.event.MouseClickOnObjectAdapter",
      "org.alice.stageide.apis.story.event.KeyAdapter",
      "org.alice.stageide.apis.story.event.ArrowKeyAdapter",
      "org.alice.stageide.apis.story.event.NumberKeyAdapter",
      "org.alice.stageide.apis.story.event.TransformationEventAdapter",
      "org.alice.stageide.apis.story.event.ComesIntoViewEventAdapter",
      "org.alice.stageide.apis.story.event.ComesOutOfViewEventAdapter",
      "org.alice.stageide.apis.story.event.StartCollisionAdapter",
      "org.alice.stageide.apis.story.event.EndCollisionAdapter",
      "org.alice.stageide.apis.story.event.EnterProximityAdapter",
      "org.alice.stageide.apis.story.event.ExitProximityAdapter",
      "org.alice.stageide.apis.story.event.StartOcclusionEventAdapter",
      "org.alice.stageide.apis.story.event.EndOcclusionEventAdapter",
      "org.alice.stageide.apis.story.event.TimerEventAdapter"
  };

  @Test
  public void allAdapterClasses_exist() throws ClassNotFoundException {
    for (String className : ADAPTER_CLASSES) {
      assertNotNull(className + " must be loadable", Class.forName(className));
    }
  }

  @Test
  public void sceneAdapterClass_exists() throws ClassNotFoundException {
    Class.forName("org.alice.stageide.ast.SceneAdapter");
  }
}
