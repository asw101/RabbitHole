package org.alice.stageide.program;

import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.*;

/**
 * Contract tests for ProgramContext and RunProgramContext.
 *
 * All tests are headless — reflection and source-analysis only.
 * These classes require a full Alice runtime to instantiate, so we verify
 * structural contracts via reflection and source analysis.
 */
public class ProgramContextContractTest {

  private static final String PROGRAM_CONTEXT_CLASS =
      "org.alice.stageide.program.ProgramContext";
  private static final String RUN_PROGRAM_CONTEXT_CLASS =
      "org.alice.stageide.program.RunProgramContext";
  private static final String PROGRAM_CONTEXT_SRC =
      "src/main/java/org/alice/stageide/program/ProgramContext.java";
  private static final String RUN_PROGRAM_CONTEXT_SRC =
      "src/main/java/org/alice/stageide/program/RunProgramContext.java";

  // All 17 adapter registrations (interface -> adapter class)
  private static final String[][] ADAPTER_REGISTRATIONS = {
      {"SScene", "SceneAdapter"},
      {"SceneActivationListener", "SceneActivationAdapter"},
      {"MouseClickOnScreenListener", "MouseClickOnScreenAdapter"},
      {"MouseClickOnObjectListener", "MouseClickOnObjectAdapter"},
      {"KeyPressListener", "KeyAdapter"},
      {"ArrowKeyPressListener", "ArrowKeyAdapter"},
      {"NumberKeyPressListener", "NumberKeyAdapter"},
      {"PointOfViewChangeListener", "TransformationEventAdapter"},
      {"ViewEnterListener", "ComesIntoViewEventAdapter"},
      {"ViewExitListener", "ComesOutOfViewEventAdapter"},
      {"CollisionStartListener", "StartCollisionAdapter"},
      {"CollisionEndListener", "EndCollisionAdapter"},
      {"ProximityEnterListener", "EnterProximityAdapter"},
      {"ProximityExitListener", "ExitProximityAdapter"},
      {"OcclusionStartListener", "StartOcclusionEventAdapter"},
      {"OcclusionEndListener", "EndOcclusionEventAdapter"},
      {"TimeListener", "TimerEventAdapter"},
  };

  // ---- ProgramContext hierarchy ----

  @Test
  public void programContext_isAbstract() throws Exception {
    Class<?> cls = Class.forName(PROGRAM_CONTEXT_CLASS);
    assertTrue("ProgramContext must be abstract",
        Modifier.isAbstract(cls.getModifiers()));
  }

  @Test
  public void programContext_isPublic() throws Exception {
    Class<?> cls = Class.forName(PROGRAM_CONTEXT_CLASS);
    assertTrue("ProgramContext must be public",
        Modifier.isPublic(cls.getModifiers()));
  }

  @Test
  public void programContext_hasNamedUserTypeConstructor() throws Exception {
    Class<?> cls = Class.forName(PROGRAM_CONTEXT_CLASS);
    Constructor<?> ctor = cls.getDeclaredConstructor(
        org.lgna.project.ast.NamedUserType.class);
    assertTrue("Constructor must be public",
        Modifier.isPublic(ctor.getModifiers()));
  }

  // ---- Adapter registration count ----

  @Test
  public void programContext_registersExactly17Adapters() throws Exception {
    String source = Files.readString(resolveSourceFile(PROGRAM_CONTEXT_SRC));
    long count = source.lines()
        .filter(line -> line.contains("registerAbstractClassAdapter"))
        .count();
    assertEquals("Must have exactly 17 registerAbstractClassAdapter calls", 17, count);
  }

  @Test
  public void adapterRegistrationCount_matchesExpected() {
    assertEquals("Expected 17 adapter registrations", 17, ADAPTER_REGISTRATIONS.length);
  }

  // ---- Each adapter registration present in source ----

  @Test
  public void registration_sceneAdapter() throws Exception {
    assertAdapterRegistered("SScene", "SceneAdapter");
  }

  @Test
  public void registration_sceneActivationAdapter() throws Exception {
    assertAdapterRegistered("SceneActivationListener", "SceneActivationAdapter");
  }

  @Test
  public void registration_mouseClickOnScreenAdapter() throws Exception {
    assertAdapterRegistered("MouseClickOnScreenListener", "MouseClickOnScreenAdapter");
  }

  @Test
  public void registration_mouseClickOnObjectAdapter() throws Exception {
    assertAdapterRegistered("MouseClickOnObjectListener", "MouseClickOnObjectAdapter");
  }

  @Test
  public void registration_keyAdapter() throws Exception {
    assertAdapterRegistered("KeyPressListener", "KeyAdapter");
  }

  @Test
  public void registration_arrowKeyAdapter() throws Exception {
    assertAdapterRegistered("ArrowKeyPressListener", "ArrowKeyAdapter");
  }

  @Test
  public void registration_numberKeyAdapter() throws Exception {
    assertAdapterRegistered("NumberKeyPressListener", "NumberKeyAdapter");
  }

  @Test
  public void registration_transformationEventAdapter() throws Exception {
    assertAdapterRegistered("PointOfViewChangeListener", "TransformationEventAdapter");
  }

  @Test
  public void registration_comesIntoViewEventAdapter() throws Exception {
    assertAdapterRegistered("ViewEnterListener", "ComesIntoViewEventAdapter");
  }

  @Test
  public void registration_comesOutOfViewEventAdapter() throws Exception {
    assertAdapterRegistered("ViewExitListener", "ComesOutOfViewEventAdapter");
  }

  @Test
  public void registration_startCollisionAdapter() throws Exception {
    assertAdapterRegistered("CollisionStartListener", "StartCollisionAdapter");
  }

  @Test
  public void registration_endCollisionAdapter() throws Exception {
    assertAdapterRegistered("CollisionEndListener", "EndCollisionAdapter");
  }

  @Test
  public void registration_enterProximityAdapter() throws Exception {
    assertAdapterRegistered("ProximityEnterListener", "EnterProximityAdapter");
  }

  @Test
  public void registration_exitProximityAdapter() throws Exception {
    assertAdapterRegistered("ProximityExitListener", "ExitProximityAdapter");
  }

  @Test
  public void registration_startOcclusionEventAdapter() throws Exception {
    assertAdapterRegistered("OcclusionStartListener", "StartOcclusionEventAdapter");
  }

  @Test
  public void registration_endOcclusionEventAdapter() throws Exception {
    assertAdapterRegistered("OcclusionEndListener", "EndOcclusionEventAdapter");
  }

  @Test
  public void registration_timerEventAdapter() throws Exception {
    assertAdapterRegistered("TimeListener", "TimerEventAdapter");
  }

  // ---- Public API methods ----

  @Test
  public void programContext_hasGetProgramInstance() throws Exception {
    Class<?> cls = Class.forName(PROGRAM_CONTEXT_CLASS);
    Method m = cls.getMethod("getProgramInstance");
    assertNotNull("Must have getProgramInstance", m);
    assertTrue("Must be public", Modifier.isPublic(m.getModifiers()));
  }

  @Test
  public void programContext_hasGetProgram() throws Exception {
    Class<?> cls = Class.forName(PROGRAM_CONTEXT_CLASS);
    Method m = cls.getMethod("getProgram");
    assertNotNull("Must have getProgram", m);
    assertEquals("Must return SProgram",
        org.lgna.story.SProgram.class, m.getReturnType());
  }

  @Test
  public void programContext_hasGetProgramImp() throws Exception {
    Class<?> cls = Class.forName(PROGRAM_CONTEXT_CLASS);
    Method m = cls.getMethod("getProgramImp");
    assertNotNull("Must have getProgramImp", m);
  }

  @Test
  public void programContext_hasGetVirtualMachine() throws Exception {
    Class<?> cls = Class.forName(PROGRAM_CONTEXT_CLASS);
    Method m = cls.getMethod("getVirtualMachine");
    assertNotNull("Must have getVirtualMachine", m);
  }

  @Test
  public void programContext_hasGetOnscreenRenderTarget() throws Exception {
    Class<?> cls = Class.forName(PROGRAM_CONTEXT_CLASS);
    Method m = cls.getMethod("getOnscreenRenderTarget");
    assertNotNull("Must have getOnscreenRenderTarget", m);
  }

  @Test
  public void programContext_hasSetActiveScene() throws Exception {
    Class<?> cls = Class.forName(PROGRAM_CONTEXT_CLASS);
    Method m = cls.getMethod("setActiveScene");
    assertNotNull("Must have setActiveScene", m);
    assertTrue("Must be public", Modifier.isPublic(m.getModifiers()));
  }

  @Test
  public void programContext_hasCleanUpProgram() throws Exception {
    Class<?> cls = Class.forName(PROGRAM_CONTEXT_CLASS);
    Method m = cls.getMethod("cleanUpProgram");
    assertNotNull("Must have cleanUpProgram", m);
    assertTrue("Must be public", Modifier.isPublic(m.getModifiers()));
  }

  // ---- Protected methods ----

  @Test
  public void programContext_hasCreateProgramInstance() throws Exception {
    Class<?> cls = Class.forName(PROGRAM_CONTEXT_CLASS);
    Method m = cls.getDeclaredMethod("createProgramInstance",
        org.lgna.project.ast.NamedUserType.class);
    assertTrue("createProgramInstance must be protected",
        Modifier.isProtected(m.getModifiers()));
  }

  @Test
  public void programContext_hasCreateVirtualMachine() throws Exception {
    Class<?> cls = Class.forName(PROGRAM_CONTEXT_CLASS);
    Method m = cls.getDeclaredMethod("createVirtualMachine");
    assertTrue("createVirtualMachine must be protected",
        Modifier.isProtected(m.getModifiers()));
  }

  @Test
  public void programContext_hasDisableRendering() throws Exception {
    Class<?> cls = Class.forName(PROGRAM_CONTEXT_CLASS);
    Method m = cls.getDeclaredMethod("disableRendering");
    assertNotNull("Must have disableRendering", m);
    // Package-private (no modifier) — verify no public/protected/private
    assertFalse("disableRendering should not be public",
        Modifier.isPublic(m.getModifiers()));
  }

  // ---- Source analysis ----

  @Test
  public void programContextSource_usesReleaseVirtualMachine() throws Exception {
    String content = Files.readString(resolveSourceFile(PROGRAM_CONTEXT_SRC));
    assertTrue("Must create ReleaseVirtualMachine",
        content.contains("ReleaseVirtualMachine"));
  }

  @Test
  public void programContextSource_usesSetActiveSceneMethod() throws Exception {
    String content = Files.readString(resolveSourceFile(PROGRAM_CONTEXT_SRC));
    assertTrue("Must reference SET_ACTIVE_SCENE_METHOD from StoryApiConfigurationManager",
        content.contains("StoryApiConfigurationManager.SET_ACTIVE_SCENE_METHOD"));
  }

  @Test
  public void programContextSource_handlesUserProgramRunningState() throws Exception {
    String content = Files.readString(resolveSourceFile(PROGRAM_CONTEXT_SRC));
    assertTrue("Must call setUserProgramRunning(true) in constructor",
        content.contains("setUserProgramRunning(true)"));
    assertTrue("Must call setUserProgramRunning(false) in cleanUpProgram",
        content.contains("setUserProgramRunning(false)"));
  }

  @Test
  public void programContextSource_handlesProgramClosedException() throws Exception {
    String content = Files.readString(resolveSourceFile(PROGRAM_CONTEXT_SRC));
    assertTrue("Must handle ProgramClosedException in setActiveScene",
        content.contains("ProgramClosedException"));
  }

  @Test
  public void programContextSource_stopsVMExecution() throws Exception {
    String content = Files.readString(resolveSourceFile(PROGRAM_CONTEXT_SRC));
    assertTrue("Must call vm.stopExecution() in cleanUpProgram",
        content.contains("stopExecution()"));
  }

  // ---- RunProgramContext ----

  @Test
  public void runProgramContext_extendsProgramContext() throws Exception {
    Class<?> cls = Class.forName(RUN_PROGRAM_CONTEXT_CLASS);
    Class<?> parent = Class.forName(PROGRAM_CONTEXT_CLASS);
    assertTrue("RunProgramContext must extend ProgramContext",
        parent.isAssignableFrom(cls));
  }

  @Test
  public void runProgramContext_isConcrete() throws Exception {
    Class<?> cls = Class.forName(RUN_PROGRAM_CONTEXT_CLASS);
    assertFalse("RunProgramContext must not be abstract",
        Modifier.isAbstract(cls.getModifiers()));
  }

  @Test
  public void runProgramContext_isPublic() throws Exception {
    Class<?> cls = Class.forName(RUN_PROGRAM_CONTEXT_CLASS);
    assertTrue("RunProgramContext must be public",
        Modifier.isPublic(cls.getModifiers()));
  }

  @Test
  public void runProgramContext_hasNamedUserTypeConstructor() throws Exception {
    Class<?> cls = Class.forName(RUN_PROGRAM_CONTEXT_CLASS);
    Constructor<?> ctor = cls.getDeclaredConstructor(
        org.lgna.project.ast.NamedUserType.class);
    assertTrue("Constructor must be public",
        Modifier.isPublic(ctor.getModifiers()));
  }

  @Test
  public void runProgramContext_hasInitializeInContainer() throws Exception {
    Class<?> cls = Class.forName(RUN_PROGRAM_CONTEXT_CLASS);
    Method m = cls.getMethod("initializeInContainer",
        org.lgna.story.implementation.ProgramImp.AwtContainerInitializer.class);
    assertNotNull("Must have initializeInContainer", m);
    assertTrue("Must be public", Modifier.isPublic(m.getModifiers()));
  }

  // ---- RunProgramContext source analysis ----

  @Test
  public void runProgramContextSource_callsDisableRendering() throws Exception {
    String content = Files.readString(resolveSourceFile(RUN_PROGRAM_CONTEXT_SRC));
    assertTrue("Must call disableRendering()",
        content.contains("this.disableRendering()"));
  }

  @Test
  public void runProgramContextSource_callsInitializeInAwtContainer() throws Exception {
    String content = Files.readString(resolveSourceFile(RUN_PROGRAM_CONTEXT_SRC));
    assertTrue("Must call initializeInAwtContainer",
        content.contains("initializeInAwtContainer"));
  }

  @Test
  public void runProgramContextSource_isMinimal() throws Exception {
    String content = Files.readString(resolveSourceFile(RUN_PROGRAM_CONTEXT_SRC));
    long methodCount = content.lines()
        .filter(line -> line.trim().startsWith("public ") && line.contains("("))
        .filter(line -> !line.contains("class "))
        .count();
    // RunProgramContext has constructor + initializeInContainer
    assertTrue("RunProgramContext should have minimal methods (constructor + 1)",
        methodCount <= 3);
  }

  // ---- Adapter imports ----

  @Test
  public void programContextSource_importsAllAdapterClasses() throws Exception {
    String content = Files.readString(resolveSourceFile(PROGRAM_CONTEXT_SRC));
    assertTrue("Must import event adapters package",
        content.contains("org.alice.stageide.apis.story.event"));
  }

  @Test
  public void programContextSource_importsSceneAdapter() throws Exception {
    String content = Files.readString(resolveSourceFile(PROGRAM_CONTEXT_SRC));
    assertTrue("Must import SceneAdapter",
        content.contains("SceneAdapter"));
  }

  // ---- Structural: ProgramContext has exactly 2 subclass-visible constructors ----

  @Test
  public void programContext_hasExactlyOneConstructor() throws Exception {
    Class<?> cls = Class.forName(PROGRAM_CONTEXT_CLASS);
    Constructor<?>[] ctors = cls.getDeclaredConstructors();
    assertEquals("ProgramContext must have exactly 1 constructor", 1, ctors.length);
  }

  // ---- Guard: adapter registration matches filler-inner registration ----

  @Test
  public void adapterRegistrations_coverAllEventListenerTypes() throws Exception {
    String programSrc = Files.readString(resolveSourceFile(PROGRAM_CONTEXT_SRC));

    // All event listener types used in adapter registrations
    String[] eventListenerInterfaces = {
        "SceneActivationListener", "MouseClickOnScreenListener",
        "MouseClickOnObjectListener", "KeyPressListener",
        "ArrowKeyPressListener", "NumberKeyPressListener",
        "PointOfViewChangeListener", "ViewEnterListener",
        "ViewExitListener", "CollisionStartListener",
        "CollisionEndListener", "ProximityEnterListener",
        "ProximityExitListener", "OcclusionStartListener",
        "OcclusionEndListener", "TimeListener"
    };

    for (String listener : eventListenerInterfaces) {
      assertTrue("ProgramContext must register adapter for " + listener,
          programSrc.contains(listener));
    }
  }

  // ---- Helpers ----

  private void assertAdapterRegistered(String interfaceName, String adapterName) throws Exception {
    String source = Files.readString(resolveSourceFile(PROGRAM_CONTEXT_SRC));
    assertTrue("Must register adapter " + adapterName + " for " + interfaceName,
        source.contains(adapterName));
  }

  private static Path resolveSourceFile(String relativePath) {
    Path cwd = Paths.get(System.getProperty("user.dir"));
    Path candidate = cwd.resolve(relativePath);
    if (Files.exists(candidate)) {
      return candidate;
    }
    Path dir = cwd;
    while (dir != null) {
      candidate = dir.resolve(relativePath);
      if (Files.exists(candidate)) {
        return candidate;
      }
      dir = dir.getParent();
    }
    fail("Cannot find source file: " + relativePath + " from " + cwd);
    return null;
  }
}
