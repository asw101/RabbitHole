package org.alice.stageide.cascade.fillerinners;

import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Contract tests for the 16 event-listener filler-inner classes.
 * All tests are headless — reflection and source-analysis only, no GUI instantiation.
 *
 * Verifies: class hierarchy, constructor targets, appendItems contract,
 * naming conventions, and structural consistency across the family.
 */
public class FillerInnerEventListenerContractTest {

  private static final String BASE_PACKAGE = "org.alice.stageide.cascade.fillerinners";
  private static final String SRC_DIR = "src/main/java/org/alice/stageide/cascade/fillerinners";

  // All 16 event-listener filler-inner class names and their expected listener targets
  private static final String[][] EVENT_LISTENER_CLASSES = {
      {"ArrowKeyListenerFillerInner", "org.lgna.story.event.ArrowKeyPressListener"},
      {"ComesIntoViewEventListenerFillerInner", "org.lgna.story.event.ViewEnterListener"},
      {"EndCollisionListenerFillerInner", "org.lgna.story.event.CollisionEndListener"},
      {"EndOcclusionEventListenerFillerInner", "org.lgna.story.event.OcclusionEndListener"},
      {"EnterProximityEventListenerFillerInner", "org.lgna.story.event.ProximityEnterListener"},
      {"ExitProximityEventListenerFillerInner", "org.lgna.story.event.ProximityExitListener"},
      {"KeyListenerFillerInner", "org.lgna.story.event.KeyPressListener"},
      {"LeavesViewEventListenerFillerInner", "org.lgna.story.event.ViewExitListener"},
      {"MouseClickOnObjectFillerInner", "org.lgna.story.event.MouseClickOnObjectListener"},
      {"MouseClickedOnScreenFillerInner", "org.lgna.story.event.MouseClickOnScreenListener"},
      {"NumberKeyListenerFillerInner", "org.lgna.story.event.NumberKeyPressListener"},
      {"SceneActivationEventFillerInner", "org.lgna.story.event.SceneActivationListener"},
      {"StartCollisionListenerFillerInner", "org.lgna.story.event.CollisionStartListener"},
      {"StartOcclusionEventListenerFillerInner", "org.lgna.story.event.OcclusionStartListener"},
      {"TimerEventListenerFillerInner", "org.lgna.story.event.TimeListener"},
      {"TransformationListenerFillerInner", "org.lgna.story.event.PointOfViewChangeListener"},
  };

  // ---- Hierarchy: all extend ExpressionFillerInner ----

  @Test
  public void arrowKeyListener_extendsExpressionFillerInner() throws Exception {
    assertExtendsExpressionFillerInner("ArrowKeyListenerFillerInner");
  }

  @Test
  public void comesIntoViewListener_extendsExpressionFillerInner() throws Exception {
    assertExtendsExpressionFillerInner("ComesIntoViewEventListenerFillerInner");
  }

  @Test
  public void endCollisionListener_extendsExpressionFillerInner() throws Exception {
    assertExtendsExpressionFillerInner("EndCollisionListenerFillerInner");
  }

  @Test
  public void endOcclusionListener_extendsExpressionFillerInner() throws Exception {
    assertExtendsExpressionFillerInner("EndOcclusionEventListenerFillerInner");
  }

  @Test
  public void enterProximityListener_extendsExpressionFillerInner() throws Exception {
    assertExtendsExpressionFillerInner("EnterProximityEventListenerFillerInner");
  }

  @Test
  public void exitProximityListener_extendsExpressionFillerInner() throws Exception {
    assertExtendsExpressionFillerInner("ExitProximityEventListenerFillerInner");
  }

  @Test
  public void keyListener_extendsExpressionFillerInner() throws Exception {
    assertExtendsExpressionFillerInner("KeyListenerFillerInner");
  }

  @Test
  public void leavesViewListener_extendsExpressionFillerInner() throws Exception {
    assertExtendsExpressionFillerInner("LeavesViewEventListenerFillerInner");
  }

  @Test
  public void mouseClickOnObject_extendsExpressionFillerInner() throws Exception {
    assertExtendsExpressionFillerInner("MouseClickOnObjectFillerInner");
  }

  @Test
  public void mouseClickedOnScreen_extendsExpressionFillerInner() throws Exception {
    assertExtendsExpressionFillerInner("MouseClickedOnScreenFillerInner");
  }

  @Test
  public void numberKeyListener_extendsExpressionFillerInner() throws Exception {
    assertExtendsExpressionFillerInner("NumberKeyListenerFillerInner");
  }

  @Test
  public void sceneActivationEvent_extendsExpressionFillerInner() throws Exception {
    assertExtendsExpressionFillerInner("SceneActivationEventFillerInner");
  }

  @Test
  public void startCollisionListener_extendsExpressionFillerInner() throws Exception {
    assertExtendsExpressionFillerInner("StartCollisionListenerFillerInner");
  }

  @Test
  public void startOcclusionListener_extendsExpressionFillerInner() throws Exception {
    assertExtendsExpressionFillerInner("StartOcclusionEventListenerFillerInner");
  }

  @Test
  public void timerEventListener_extendsExpressionFillerInner() throws Exception {
    assertExtendsExpressionFillerInner("TimerEventListenerFillerInner");
  }

  @Test
  public void transformationListener_extendsExpressionFillerInner() throws Exception {
    assertExtendsExpressionFillerInner("TransformationListenerFillerInner");
  }

  // ---- Constructor: no-arg public ----

  @Test
  public void allEventListeners_haveNoArgConstructor() throws Exception {
    for (String[] entry : EVENT_LISTENER_CLASSES) {
      Class<?> cls = Class.forName(BASE_PACKAGE + "." + entry[0]);
      Constructor<?>[] constructors = cls.getDeclaredConstructors();
      boolean hasNoArg = Arrays.stream(constructors)
          .anyMatch(c -> c.getParameterCount() == 0);
      assertTrue(entry[0] + " must have a no-arg constructor", hasNoArg);
    }
  }

  @Test
  public void allEventListeners_constructorsArePublic() throws Exception {
    for (String[] entry : EVENT_LISTENER_CLASSES) {
      Class<?> cls = Class.forName(BASE_PACKAGE + "." + entry[0]);
      Constructor<?> ctor = cls.getDeclaredConstructor();
      assertTrue(entry[0] + " constructor must be public",
          Modifier.isPublic(ctor.getModifiers()));
    }
  }

  // ---- appendItems method contract ----

  @Test
  public void allEventListeners_declareAppendItems() throws Exception {
    for (String[] entry : EVENT_LISTENER_CLASSES) {
      Class<?> cls = Class.forName(BASE_PACKAGE + "." + entry[0]);
      Method appendItems = cls.getMethod("appendItems",
          java.util.List.class,
          org.lgna.project.annotations.ValueDetails.class,
          boolean.class,
          org.lgna.project.ast.Expression.class);
      assertNotNull(entry[0] + " must declare appendItems", appendItems);
    }
  }

  @Test
  public void allEventListeners_appendItemsIsPublic() throws Exception {
    for (String[] entry : EVENT_LISTENER_CLASSES) {
      Class<?> cls = Class.forName(BASE_PACKAGE + "." + entry[0]);
      Method appendItems = cls.getMethod("appendItems",
          java.util.List.class,
          org.lgna.project.annotations.ValueDetails.class,
          boolean.class,
          org.lgna.project.ast.Expression.class);
      assertTrue(entry[0] + ".appendItems must be public",
          Modifier.isPublic(appendItems.getModifiers()));
    }
  }

  // ---- Concrete (non-abstract) ----

  @Test
  public void allEventListeners_areConcrete() throws Exception {
    for (String[] entry : EVENT_LISTENER_CLASSES) {
      Class<?> cls = Class.forName(BASE_PACKAGE + "." + entry[0]);
      assertFalse(entry[0] + " must not be abstract",
          Modifier.isAbstract(cls.getModifiers()));
    }
  }

  @Test
  public void allEventListeners_arePublic() throws Exception {
    for (String[] entry : EVENT_LISTENER_CLASSES) {
      Class<?> cls = Class.forName(BASE_PACKAGE + "." + entry[0]);
      assertTrue(entry[0] + " must be public",
          Modifier.isPublic(cls.getModifiers()));
    }
  }

  // ---- Listener target type verification via source analysis ----

  @Test
  public void arrowKeyListener_targetsArrowKeyPressListener() throws Exception {
    assertSourceContainsSuperCall("ArrowKeyListenerFillerInner", "ArrowKeyPressListener");
  }

  @Test
  public void comesIntoViewListener_targetsViewEnterListener() throws Exception {
    assertSourceContainsSuperCall("ComesIntoViewEventListenerFillerInner", "ViewEnterListener");
  }

  @Test
  public void endCollisionListener_targetsCollisionEndListener() throws Exception {
    assertSourceContainsSuperCall("EndCollisionListenerFillerInner", "CollisionEndListener");
  }

  @Test
  public void endOcclusionListener_targetsOcclusionEndListener() throws Exception {
    assertSourceContainsSuperCall("EndOcclusionEventListenerFillerInner", "OcclusionEndListener");
  }

  @Test
  public void enterProximityListener_targetsProximityEnterListener() throws Exception {
    assertSourceContainsSuperCall("EnterProximityEventListenerFillerInner", "ProximityEnterListener");
  }

  @Test
  public void exitProximityListener_targetsProximityExitListener() throws Exception {
    assertSourceContainsSuperCall("ExitProximityEventListenerFillerInner", "ProximityExitListener");
  }

  @Test
  public void keyListener_targetsKeyPressListener() throws Exception {
    assertSourceContainsSuperCall("KeyListenerFillerInner", "KeyPressListener");
  }

  @Test
  public void leavesViewListener_targetsViewExitListener() throws Exception {
    assertSourceContainsSuperCall("LeavesViewEventListenerFillerInner", "ViewExitListener");
  }

  @Test
  public void mouseClickOnObject_targetsMouseClickOnObjectListener() throws Exception {
    assertSourceContainsSuperCall("MouseClickOnObjectFillerInner", "MouseClickOnObjectListener");
  }

  @Test
  public void mouseClickedOnScreen_targetsMouseClickOnScreenListener() throws Exception {
    assertSourceContainsSuperCall("MouseClickedOnScreenFillerInner", "MouseClickOnScreenListener");
  }

  @Test
  public void numberKeyListener_targetsNumberKeyPressListener() throws Exception {
    assertSourceContainsSuperCall("NumberKeyListenerFillerInner", "NumberKeyPressListener");
  }

  @Test
  public void sceneActivationEvent_targetsSceneActivationListener() throws Exception {
    assertSourceContainsSuperCall("SceneActivationEventFillerInner", "SceneActivationListener");
  }

  @Test
  public void startCollisionListener_targetsCollisionStartListener() throws Exception {
    assertSourceContainsSuperCall("StartCollisionListenerFillerInner", "CollisionStartListener");
  }

  @Test
  public void startOcclusionListener_targetsOcclusionStartListener() throws Exception {
    assertSourceContainsSuperCall("StartOcclusionEventListenerFillerInner", "OcclusionStartListener");
  }

  @Test
  public void timerEventListener_targetsTimeListener() throws Exception {
    assertSourceContainsSuperCall("TimerEventListenerFillerInner", "TimeListener");
  }

  @Test
  public void transformationListener_targetsPointOfViewChangeListener() throws Exception {
    assertSourceContainsSuperCall("TransformationListenerFillerInner", "PointOfViewChangeListener");
  }

  // ---- Naming convention checks ----

  @Test
  public void allEventListeners_nameEndWithFillerInner() throws Exception {
    for (String[] entry : EVENT_LISTENER_CLASSES) {
      assertTrue(entry[0] + " name must end with FillerInner",
          entry[0].endsWith("FillerInner"));
    }
  }

  @Test
  public void eventListenerCount_isExactly16() {
    assertEquals("Expected exactly 16 event-listener filler-inners",
        16, EVENT_LISTENER_CLASSES.length);
  }

  // ---- Cross-family: collision listeners come in Start/End pairs ----

  @Test
  public void collisionListeners_haveStartEndPair() throws Exception {
    Class.forName(BASE_PACKAGE + ".StartCollisionListenerFillerInner");
    Class.forName(BASE_PACKAGE + ".EndCollisionListenerFillerInner");
  }

  @Test
  public void occlusionListeners_haveStartEndPair() throws Exception {
    Class.forName(BASE_PACKAGE + ".StartOcclusionEventListenerFillerInner");
    Class.forName(BASE_PACKAGE + ".EndOcclusionEventListenerFillerInner");
  }

  @Test
  public void proximityListeners_haveEnterExitPair() throws Exception {
    Class.forName(BASE_PACKAGE + ".EnterProximityEventListenerFillerInner");
    Class.forName(BASE_PACKAGE + ".ExitProximityEventListenerFillerInner");
  }

  @Test
  public void viewListeners_haveComesInLeavePair() throws Exception {
    Class.forName(BASE_PACKAGE + ".ComesIntoViewEventListenerFillerInner");
    Class.forName(BASE_PACKAGE + ".LeavesViewEventListenerFillerInner");
  }

  // ---- Source file existence ----

  @Test
  public void allEventListenerSourceFiles_exist() {
    for (String[] entry : EVENT_LISTENER_CLASSES) {
      Path srcPath = resolveSourceFile(SRC_DIR + "/" + entry[0] + ".java");
      assertTrue(entry[0] + ".java source file must exist",
          Files.exists(srcPath));
    }
  }

  // ---- Each source file references its adapter FillIn ----

  @Test
  public void arrowKeyListener_referencesAdapterFillIn() throws Exception {
    assertSourceContains("ArrowKeyListenerFillerInner", "ArrowKeyAdapterFillIn");
  }

  @Test
  public void mouseClickOnObject_referencesAdapterFillIn() throws Exception {
    assertSourceContains("MouseClickOnObjectFillerInner", "MouseClickOnObjectFillerInner.getInstance");
  }

  @Test
  public void keyListener_referencesAdapterFillIn() throws Exception {
    assertSourceContains("KeyListenerFillerInner", "AdapterFillIn");
  }

  // ---- No-arg constructor does not throw ----

  @Test
  public void allEventListeners_constructWithoutException() throws Exception {
    for (String[] entry : EVENT_LISTENER_CLASSES) {
      Class<?> cls = Class.forName(BASE_PACKAGE + "." + entry[0]);
      try {
        cls.getDeclaredConstructor().newInstance();
      } catch (Exception e) {
        fail(entry[0] + " no-arg constructor threw: " + e.getMessage());
      }
    }
  }

  // ---- Listener target class is loadable ----

  @Test
  public void allListenerTargetClasses_areLoadable() throws Exception {
    for (String[] entry : EVENT_LISTENER_CLASSES) {
      Class<?> targetClass = Class.forName(entry[1]);
      assertNotNull(entry[0] + " target class " + entry[1] + " must be loadable", targetClass);
      assertTrue(entry[1] + " must be an interface",
          targetClass.isInterface());
    }
  }

  // ---- No duplicate listener targets across the family ----

  @Test
  public void allEventListeners_haveUniqueListenerTargets() {
    long uniqueCount = Arrays.stream(EVENT_LISTENER_CLASSES)
        .map(entry -> entry[1])
        .distinct()
        .count();
    assertEquals("All event listener filler-inners must have unique listener targets",
        EVENT_LISTENER_CLASSES.length, uniqueCount);
  }

  // ---- No static state (all instance-based) ----

  @Test
  public void allEventListeners_haveNoStaticFields() throws Exception {
    for (String[] entry : EVENT_LISTENER_CLASSES) {
      Class<?> cls = Class.forName(BASE_PACKAGE + "." + entry[0]);
      long staticFieldCount = Arrays.stream(cls.getDeclaredFields())
          .filter(f -> Modifier.isStatic(f.getModifiers()))
          .count();
      assertEquals(entry[0] + " should have no static fields",
          0, staticFieldCount);
    }
  }

  // ---- isAssignableTo is callable (inherited from ExpressionFillerInner) ----

  @Test
  public void allEventListeners_haveIsAssignableToMethod() throws Exception {
    for (String[] entry : EVENT_LISTENER_CLASSES) {
      Class<?> cls = Class.forName(BASE_PACKAGE + "." + entry[0]);
      Method m = cls.getMethod("isAssignableTo", org.lgna.project.ast.AbstractType.class);
      assertNotNull(entry[0] + " must have isAssignableTo", m);
    }
  }

  // ---- Guard: source file line count stability ----

  @Test
  public void allEventListenerSources_haveReasonableLength() throws Exception {
    for (String[] entry : EVENT_LISTENER_CLASSES) {
      Path srcPath = resolveSourceFile(SRC_DIR + "/" + entry[0] + ".java");
      if (Files.exists(srcPath)) {
        List<String> lines = Files.readAllLines(srcPath);
        assertTrue(entry[0] + ".java should have at least 50 lines (license + class)",
            lines.size() >= 50);
        assertTrue(entry[0] + ".java should have fewer than 200 lines (simple class)",
            lines.size() < 200);
      }
    }
  }

  // ---- Helpers ----

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

  private void assertExtendsExpressionFillerInner(String className) throws Exception {
    Class<?> cls = Class.forName(BASE_PACKAGE + "." + className);
    Class<?> expectedSuper = Class.forName("org.alice.ide.cascade.fillerinners.ExpressionFillerInner");
    assertTrue(className + " must extend ExpressionFillerInner",
        expectedSuper.isAssignableFrom(cls));
  }

  private void assertSourceContainsSuperCall(String className, String listenerName) throws Exception {
    Path srcPath = resolveSourceFile(SRC_DIR + "/" + className + ".java");
    String content = Files.readString(srcPath);
    assertTrue(className + " source must reference " + listenerName,
        content.contains(listenerName));
  }

  private void assertSourceContains(String className, String expectedText) throws Exception {
    Path srcPath = resolveSourceFile(SRC_DIR + "/" + className + ".java");
    String content = Files.readString(srcPath);
    assertTrue(className + " source must contain '" + expectedText + "'",
        content.contains(expectedText));
  }
}
