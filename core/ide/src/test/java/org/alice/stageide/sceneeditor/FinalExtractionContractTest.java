package org.alice.stageide.sceneeditor;

import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

/**
 * Cross-cutting integration contract tests for the final SSE extraction
 * (issue #545): SceneEditorInitializer + SceneEditorLifecycleManager.
 *
 * Verifies:
 *   - SSE line count under 500
 *   - New delegate files exist
 *   - 10 private fields widened to package-private
 *   - 2 private methods widened to package-private
 *   - 2 protected-method forwarding methods added
 *   - Constants and private methods removed from SSE
 *   - lifecycleManager field added to SSE
 *   - initializeComponents delegates to SceneEditorInitializer
 *   - Public API surface fully preserved
 *   - Inner class count unchanged at 2
 *
 * Pure reflection + source analysis — no GUI, no singleton instantiation.
 * These tests FAIL until the extraction is implemented.
 */
public class FinalExtractionContractTest {

  private static final String SSE_FQCN = "org.alice.stageide.sceneeditor.StorytellingSceneEditor";
  private static final String INIT_FQCN = "org.alice.stageide.sceneeditor.SceneEditorInitializer";
  private static final String LCM_FQCN = "org.alice.stageide.sceneeditor.SceneEditorLifecycleManager";

  private static Class<?> sseClazz;
  private static final Path SSE_SRC = findSourceFile("StorytellingSceneEditor.java");

  @BeforeClass
  public static void loadClasses() {
    try {
      sseClazz = Class.forName(SSE_FQCN);
    } catch (ClassNotFoundException e) {
      fail("StorytellingSceneEditor not found: " + e.getMessage());
    }
  }

  // ── Line count target ────────────────────────────────────────────

  @Test
  public void sseLineCount_under500() throws IOException {
    if (!Files.exists(SSE_SRC)) {
      fail("SSE source not found at: " + SSE_SRC);
    }
    long lineCount = Files.lines(SSE_SRC).count();
    assertTrue("StorytellingSceneEditor must be under 500 lines, found " + lineCount,
        lineCount < 500);
  }

  // ── New delegate files exist ─────────────────────────────────────

  @Test
  public void sceneEditorInitializerFile_exists() {
    Path initPath = SSE_SRC.getParent().resolve("SceneEditorInitializer.java");
    assertTrue("SceneEditorInitializer.java must exist", Files.exists(initPath));
  }

  @Test
  public void sceneEditorLifecycleManagerFile_exists() {
    Path lcmPath = SSE_SRC.getParent().resolve("SceneEditorLifecycleManager.java");
    assertTrue("SceneEditorLifecycleManager.java must exist", Files.exists(lcmPath));
  }

  // ── New delegate classes load ────────────────────────────────────

  @Test
  public void sceneEditorInitializerClass_loads() {
    try {
      Class.forName(INIT_FQCN);
    } catch (ClassNotFoundException e) {
      fail("SceneEditorInitializer class must be loadable: " + e.getMessage());
    }
  }

  @Test
  public void sceneEditorLifecycleManagerClass_loads() {
    try {
      Class.forName(LCM_FQCN);
    } catch (ClassNotFoundException e) {
      fail("SceneEditorLifecycleManager class must be loadable: " + e.getMessage());
    }
  }

  // ── lifecycleManager field on SSE ────────────────────────────────

  @Test
  public void sseHasLifecycleManagerField() {
    try {
      sseClazz.getDeclaredField("lifecycleManager");
    } catch (NoSuchFieldException e) {
      fail("SSE must have a 'lifecycleManager' field");
    }
  }

  @Test
  public void lifecycleManagerField_typeIsCorrect() {
    try {
      Field f = sseClazz.getDeclaredField("lifecycleManager");
      assertEquals("lifecycleManager must be of type SceneEditorLifecycleManager",
          LCM_FQCN, f.getType().getName());
    } catch (NoSuchFieldException e) {
      fail("Missing 'lifecycleManager' field");
    }
  }

  // ── 10 fields widened from private to package-private ────────────

  @Test
  public void isInitialized_isPackagePrivate() {
    assertFieldIsPackagePrivate("isInitialized");
  }

  @Test
  public void animator_isPackagePrivate() {
    assertFieldIsPackagePrivate("animator");
  }

  @Test
  public void expandButton_isPackagePrivate() {
    assertFieldIsPackagePrivate("expandButton");
  }

  @Test
  public void contractButton_isPackagePrivate() {
    assertFieldIsPackagePrivate("contractButton");
  }

  @Test
  public void instanceFactorySelectionPanel_isPackagePrivate() {
    assertFieldIsPackagePrivate("instanceFactorySelectionPanel");
  }

  @Test
  public void layoutCameraImp_isPackagePrivate() {
    assertFieldIsPackagePrivate("layoutCameraImp");
  }

  @Test
  public void mainCameraViewSelector_isPackagePrivate() {
    assertFieldIsPackagePrivate("mainCameraViewSelector");
  }

  @Test
  public void mainCameraViewTracker_isPackagePrivate() {
    assertFieldIsPackagePrivate("mainCameraViewTracker");
  }

  @Test
  public void savedSceneEditorViewSelection_isPackagePrivate() {
    assertFieldIsPackagePrivate("savedSceneEditorViewSelection");
  }

  @Test
  public void mainCameraMarkerList_isPackagePrivate() {
    assertFieldIsPackagePrivate("mainCameraMarkerList");
  }

  // ── 2 methods widened from private to package-private ────────────

  @Test
  public void setIsVrActive_isPackagePrivate() {
    assertMethodIsPackagePrivate("setIsVrActive");
  }

  @Test
  public void setCameras_isPackagePrivate() {
    assertMethodIsPackagePrivate("setCameras");
  }

  // ── 2 forwarding methods for protected parent access ─────────────

  @Test
  public void hasProgramInstanceForwardingMethod() {
    boolean found = Arrays.stream(sseClazz.getDeclaredMethods())
        .anyMatch(m -> m.getName().equals("getProgramInstanceInJavaForDelegate"));
    assertTrue("SSE must have getProgramInstanceInJavaForDelegate() forwarding method", found);
  }

  @Test
  public void hasSetInitialCodeStateForwardingMethod() {
    boolean found = Arrays.stream(sseClazz.getDeclaredMethods())
        .anyMatch(m -> m.getName().equals("setInitialCodeStateForFieldForDelegate"));
    assertTrue("SSE must have setInitialCodeStateForFieldForDelegate() forwarding method", found);
  }

  @Test
  public void programInstanceForwarding_isPackagePrivate() {
    assertMethodIsPackagePrivate("getProgramInstanceInJavaForDelegate");
  }

  @Test
  public void setInitialCodeStateForwarding_isPackagePrivate() {
    assertMethodIsPackagePrivate("setInitialCodeStateForFieldForDelegate");
  }

  // ── Constants removed from SSE ───────────────────────────────────

  @Test
  public void expandIconConstant_removedFromSSE() {
    Set<String> fieldNames = Arrays.stream(sseClazz.getDeclaredFields())
        .map(Field::getName)
        .collect(Collectors.toSet());
    assertFalse("EXPAND_ICON must be moved to SceneEditorInitializer",
        fieldNames.contains("EXPAND_ICON"));
  }

  @Test
  public void contractIconConstant_removedFromSSE() {
    Set<String> fieldNames = Arrays.stream(sseClazz.getDeclaredFields())
        .map(Field::getName)
        .collect(Collectors.toSet());
    assertFalse("CONTRACT_ICON must be moved to SceneEditorInitializer",
        fieldNames.contains("CONTRACT_ICON"));
  }

  @Test
  public void showJointedModelVisualizationsKey_removedFromSSE() {
    Set<String> fieldNames = Arrays.stream(sseClazz.getDeclaredFields())
        .map(Field::getName)
        .collect(Collectors.toSet());
    assertFalse("SHOW_JOINTED_MODEL_VISUALIZATIONS_KEY must be moved to SceneEditorLifecycleManager",
        fieldNames.contains("SHOW_JOINTED_MODEL_VISUALIZATIONS_KEY"));
  }

  // ── Private method removed from SSE ──────────────────────────────

  @Test
  public void useSceneAsVehicle_removedFromSSE() {
    boolean found = Arrays.stream(sseClazz.getDeclaredMethods())
        .anyMatch(m -> m.getName().equals("useSceneAsVehicleForDisconnectedModels"));
    assertFalse("useSceneAsVehicleForDisconnectedModels must be moved to SceneEditorLifecycleManager",
        found);
  }

  // ── SSE still declares key override methods (stubs) ──────────────

  @Test
  public void sseStillDeclares_initializeComponents() {
    assertSseDeclares("initializeComponents");
  }

  @Test
  public void sseStillDeclares_setActiveScene() {
    assertSseDeclares("setActiveScene");
  }

  @Test
  public void sseStillDeclares_addField() {
    assertSseDeclares("addField");
  }

  @Test
  public void sseStillDeclares_handleProjectOpened() {
    assertSseDeclares("handleProjectOpened");
  }

  // ── initializeComponents delegates to SceneEditorInitializer ─────

  @Test
  public void initializeComponentsSource_delegatesToInitializer() throws IOException {
    if (!Files.exists(SSE_SRC)) {
      fail("SSE source not found at: " + SSE_SRC);
    }
    boolean found = Files.lines(SSE_SRC)
        .anyMatch(line -> line.contains("SceneEditorInitializer"));
    assertTrue("initializeComponents must delegate to SceneEditorInitializer", found);
  }

  // ── setActiveScene delegates to lifecycleManager ─────────────────

  @Test
  public void setActiveSceneSource_delegatesToLifecycleManager() throws IOException {
    if (!Files.exists(SSE_SRC)) {
      fail("SSE source not found at: " + SSE_SRC);
    }
    boolean found = Files.lines(SSE_SRC)
        .anyMatch(line -> line.contains("lifecycleManager.activateScene"));
    assertTrue("setActiveScene must delegate to lifecycleManager.activateScene()", found);
  }

  // ── handleProjectOpened delegates to lifecycleManager ─────────────

  @Test
  public void handleProjectOpenedSource_delegatesToLifecycleManager() throws IOException {
    if (!Files.exists(SSE_SRC)) {
      fail("SSE source not found at: " + SSE_SRC);
    }
    boolean found = Files.lines(SSE_SRC)
        .anyMatch(line -> line.contains("lifecycleManager.prepareForProject"));
    assertTrue("handleProjectOpened must delegate to lifecycleManager.prepareForProject()", found);
  }

  // ── Inner class count unchanged ──────────────────────────────────

  @Test
  public void innerClassCount_still2() {
    assertEquals("SSE must still have exactly 2 inner classes "
            + "(SingletonHolder + SceneEditorProgramImp)",
        2, sseClazz.getDeclaredClasses().length);
  }

  // ── Public API surface preserved ─────────────────────────────────

  @Test
  public void publicMethodCount_atLeast35() {
    long count = Arrays.stream(sseClazz.getDeclaredMethods())
        .filter(m -> Modifier.isPublic(m.getModifiers()))
        .count();
    assertTrue("Expected ≥35 public methods on SSE (full API preserved), found " + count,
        count >= 35);
  }

  // ── Aggregate field count adjusted ───────────────────────────────

  @Test
  public void declaredFieldCount_atLeast20() {
    int count = sseClazz.getDeclaredFields().length;
    // Removed 3 (EXPAND_ICON, CONTRACT_ICON, SHOW_JOINTED_MODEL_VISUALIZATIONS_KEY)
    // Added 1 (lifecycleManager); initializer is ephemeral (local variable)
    // Net: -2 from baseline; should still be ≥ 20
    assertTrue("Expected ≥20 declared fields after extraction, found " + count,
        count >= 20);
  }

  // ── Helpers ──────────────────────────────────────────────────────

  private static Path findSourceFile(String filename) {
    String relPath = "core/ide/src/main/java/org/alice/stageide/sceneeditor/" + filename;
    Path cwd = Paths.get("").toAbsolutePath();
    for (Path dir = cwd; dir != null; dir = dir.getParent()) {
      Path candidate = dir.resolve(relPath);
      if (Files.exists(candidate)) {
        return candidate;
      }
    }
    return Paths.get(relPath);
  }

  private static void assertFieldIsPackagePrivate(String name) {
    try {
      Field f = sseClazz.getDeclaredField(name);
      int mods = f.getModifiers();
      assertFalse(name + " must not be public", Modifier.isPublic(mods));
      assertFalse(name + " must not be private", Modifier.isPrivate(mods));
      assertFalse(name + " must not be protected", Modifier.isProtected(mods));
    } catch (NoSuchFieldException e) {
      fail("Missing field: " + name);
    }
  }

  private static void assertMethodIsPackagePrivate(String name) {
    boolean found = Arrays.stream(sseClazz.getDeclaredMethods())
        .filter(m -> m.getName().equals(name))
        .anyMatch(m -> {
          int mods = m.getModifiers();
          return !Modifier.isPublic(mods)
              && !Modifier.isPrivate(mods)
              && !Modifier.isProtected(mods);
        });
    assertTrue(name + " must be package-private", found);
  }

  private static void assertSseDeclares(String name) {
    boolean found = Arrays.stream(sseClazz.getDeclaredMethods())
        .anyMatch(m -> m.getName().equals(name));
    assertTrue("SSE must still declare method: " + name, found);
  }
}
