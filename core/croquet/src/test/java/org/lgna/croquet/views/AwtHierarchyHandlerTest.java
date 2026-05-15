package org.lgna.croquet.views;

import org.junit.Test;

import java.awt.*;
import java.awt.event.HierarchyEvent;
import java.io.IOException;
import java.lang.reflect.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * TDD tests for the AwtHierarchyHandler extraction from AwtComponentView.
 *
 * <p>Verifies the extraction contract:
 * <ul>
 *   <li>AwtHierarchyHandler.java exists as a package-private final class</li>
 *   <li>AwtComponentView.java is reduced to under 500 lines</li>
 *   <li>Hierarchy lifecycle fields/methods moved to handler</li>
 *   <li>Protected hooks remain in AwtComponentView for subclass overrides</li>
 *   <li>Displayability tracking fires correct callbacks on transitions</li>
 *   <li>Parent-change detection fires correct add/remove callbacks</li>
 *   <li>No duplicate transitions on repeated identical state</li>
 *   <li>handleHierarchyChanged remains overridable (ReturnToSceneTypeButton)</li>
 * </ul>
 *
 * <p>These tests FAIL until the extraction is implemented.</p>
 */
public class AwtHierarchyHandlerTest {

  private static final String VIEWS_PACKAGE = "org.lgna.croquet.views";
  private static final String SOURCE_DIR = "core/croquet/src/main/java/org/lgna/croquet/views";

  // ── Test helper: recording AwtComponentView ────────────────────────────

  /**
   * Concrete AwtComponentView that records lifecycle hook invocations.
   */
  private static class RecordingView extends AwtComponentView<java.awt.Panel> {
    final List<String> hookCalls = new ArrayList<>();
    private final java.awt.Panel panel = new java.awt.Panel();

    @Override
    protected java.awt.Panel createAwtComponent() {
      return panel;
    }

    @Override
    protected void checkEventDispatchThread() {
      // suppress EDT check for testing
    }

    @Override
    protected void handleDisplayable() {
      hookCalls.add("handleDisplayable");
    }

    @Override
    protected void handleUndisplayable() {
      hookCalls.add("handleUndisplayable");
    }

    @Override
    protected void handleAddedTo(AwtComponentView<?> parent) {
      hookCalls.add("handleAddedTo");
    }

    @Override
    protected void handleRemovedFrom(AwtComponentView<?> parent) {
      hookCalls.add("handleRemovedFrom");
    }
  }

  // ── Stub components for displayability control ─────────────────────────

  private static Component displayableComponent() {
    return new Canvas() {
      @Override public boolean isDisplayable() { return true; }
    };
  }

  private static Component nonDisplayableComponent() {
    return new Canvas() {
      @Override public boolean isDisplayable() { return false; }
    };
  }

  // ════════════════════════════════════════════════════════════════════════
  // STRUCTURAL TESTS — file & line count
  // ════════════════════════════════════════════════════════════════════════

  @Test
  public void handlerFile_exists() {
    Path source = findSourceFile("AwtHierarchyHandler.java");
    assertTrue("AwtHierarchyHandler.java must exist", Files.exists(source));
  }

  @Test
  public void awtComponentView_underFiveHundredLines() throws IOException {
    Path source = findSourceFile("AwtComponentView.java");
    long lineCount = Files.lines(source).count();
    assertTrue("AwtComponentView.java must be under 500 lines. Actual: " + lineCount,
        lineCount < 500);
  }

  // ════════════════════════════════════════════════════════════════════════
  // STRUCTURAL TESTS — AwtHierarchyHandler class shape
  // ════════════════════════════════════════════════════════════════════════

  @Test
  public void handlerClass_loads() throws ClassNotFoundException {
    Class.forName(VIEWS_PACKAGE + ".AwtHierarchyHandler");
  }

  @Test
  public void handlerClass_isPackagePrivate() throws ClassNotFoundException {
    Class<?> cls = Class.forName(VIEWS_PACKAGE + ".AwtHierarchyHandler");
    int mods = cls.getModifiers();
    assertFalse("Must not be public", Modifier.isPublic(mods));
    assertFalse("Must not be protected", Modifier.isProtected(mods));
    assertFalse("Must not be private", Modifier.isPrivate(mods));
  }

  @Test
  public void handlerClass_isFinal() throws ClassNotFoundException {
    Class<?> cls = Class.forName(VIEWS_PACKAGE + ".AwtHierarchyHandler");
    assertTrue("Must be final", Modifier.isFinal(cls.getModifiers()));
  }

  @Test
  public void handler_constructorAcceptsOwner() throws Exception {
    Class<?> cls = Class.forName(VIEWS_PACKAGE + ".AwtHierarchyHandler");
    Constructor<?> ctor = cls.getDeclaredConstructor(AwtComponentView.class);
    assertNotNull("Constructor(AwtComponentView<?>) must exist", ctor);
  }

  @Test
  public void handler_hasProcessHierarchyEventMethod() throws Exception {
    Class<?> cls = Class.forName(VIEWS_PACKAGE + ".AwtHierarchyHandler");
    Method m = cls.getDeclaredMethod("processHierarchyEvent", HierarchyEvent.class);
    assertNotNull(m);
  }

  @Test
  public void handler_hasTrackDisplayabilityMethod() throws Exception {
    Class<?> cls = Class.forName(VIEWS_PACKAGE + ".AwtHierarchyHandler");
    Method m = cls.getDeclaredMethod("trackDisplayability", Component.class);
    assertNotNull(m);
  }

  @Test
  public void handler_hasHandleParentChangeMethod() throws Exception {
    Class<?> cls = Class.forName(VIEWS_PACKAGE + ".AwtHierarchyHandler");
    Method m = cls.getDeclaredMethod("handleParentChange", Container.class);
    assertNotNull(m);
  }

  // ════════════════════════════════════════════════════════════════════════
  // STRUCTURAL TESTS — AwtHierarchyHandler fields
  // ════════════════════════════════════════════════════════════════════════

  @Test
  public void handler_hasOwnerField() throws Exception {
    Class<?> cls = Class.forName(VIEWS_PACKAGE + ".AwtHierarchyHandler");
    Field f = cls.getDeclaredField("owner");
    assertEquals(AwtComponentView.class, f.getType());
  }

  @Test
  public void handler_hasIsDisplayableStateField() throws Exception {
    Class<?> cls = Class.forName(VIEWS_PACKAGE + ".AwtHierarchyHandler");
    Field f = cls.getDeclaredField("isDisplayableState");
    assertEquals(boolean.class, f.getType());
  }

  @Test
  public void handler_hasAwtParentField() throws Exception {
    Class<?> cls = Class.forName(VIEWS_PACKAGE + ".AwtHierarchyHandler");
    Field f = cls.getDeclaredField("awtParent");
    assertEquals(Container.class, f.getType());
  }

  @Test
  public void handler_isWarningAlreadyPrinted_isStatic() throws Exception {
    Class<?> cls = Class.forName(VIEWS_PACKAGE + ".AwtHierarchyHandler");
    Field f = cls.getDeclaredField("isWarningAlreadyPrinted");
    assertTrue("Must be static", Modifier.isStatic(f.getModifiers()));
  }

  // ════════════════════════════════════════════════════════════════════════
  // STRUCTURAL TESTS — fields/methods moved OUT of AwtComponentView
  // ════════════════════════════════════════════════════════════════════════

  @Test
  public void awtComponentView_noIsDisplayableStateField() {
    assertFieldAbsent(AwtComponentView.class, "isDisplayableState",
        "isDisplayableState must be in AwtHierarchyHandler");
  }

  @Test
  public void awtComponentView_noAwtParentField() {
    assertFieldAbsent(AwtComponentView.class, "awtParent",
        "awtParent must be in AwtHierarchyHandler");
  }

  @Test
  public void awtComponentView_noIsWarningAlreadyPrintedField() {
    assertFieldAbsent(AwtComponentView.class, "isWarningAlreadyPrinted",
        "isWarningAlreadyPrinted must be in AwtHierarchyHandler");
  }

  @Test
  public void awtComponentView_noTrackDisplayabilityMethod() {
    assertMethodAbsent(AwtComponentView.class, "trackDisplayability",
        "trackDisplayability must be in AwtHierarchyHandler");
  }

  @Test
  public void awtComponentView_noHandleParentChangeMethod() {
    assertMethodAbsent(AwtComponentView.class, "handleParentChange",
        "handleParentChange must be in AwtHierarchyHandler");
  }

  // ════════════════════════════════════════════════════════════════════════
  // STRUCTURAL TESTS — hooks/fields RETAINED in AwtComponentView
  // ════════════════════════════════════════════════════════════════════════

  @Test
  public void awtComponentView_retainsHandleDisplayable() throws Exception {
    Method m = AwtComponentView.class.getDeclaredMethod("handleDisplayable");
    assertTrue("Must be protected", Modifier.isProtected(m.getModifiers()));
  }

  @Test
  public void awtComponentView_retainsHandleUndisplayable() throws Exception {
    Method m = AwtComponentView.class.getDeclaredMethod("handleUndisplayable");
    assertTrue("Must be protected", Modifier.isProtected(m.getModifiers()));
  }

  @Test
  public void awtComponentView_retainsHandleAddedTo() throws Exception {
    Method m = AwtComponentView.class.getDeclaredMethod("handleAddedTo", AwtComponentView.class);
    assertTrue("Must be protected", Modifier.isProtected(m.getModifiers()));
  }

  @Test
  public void awtComponentView_retainsHandleRemovedFrom() throws Exception {
    Method m = AwtComponentView.class.getDeclaredMethod("handleRemovedFrom", AwtComponentView.class);
    assertTrue("Must be protected", Modifier.isProtected(m.getModifiers()));
  }

  @Test
  public void awtComponentView_retainsHandleHierarchyChanged() throws Exception {
    Method m = AwtComponentView.class.getDeclaredMethod("handleHierarchyChanged", HierarchyEvent.class);
    assertTrue("Must remain protected", Modifier.isProtected(m.getModifiers()));
  }

  @Test
  public void awtComponentView_retainsHierarchyListenerField() throws Exception {
    Field f = AwtComponentView.class.getDeclaredField("hierarchyListener");
    assertNotNull("hierarchyListener must stay in AwtComponentView", f);
  }

  @Test
  public void awtComponentView_hasHierarchyHandlerField() throws Exception {
    Class<?> handlerCls = Class.forName(VIEWS_PACKAGE + ".AwtHierarchyHandler");
    Field f = AwtComponentView.class.getDeclaredField("hierarchyHandler");
    assertEquals("hierarchyHandler field type must be AwtHierarchyHandler",
        handlerCls, f.getType());
  }

  // ════════════════════════════════════════════════════════════════════════
  // BEHAVIORAL TESTS — displayability tracking
  // ════════════════════════════════════════════════════════════════════════

  @Test
  public void trackDisplayability_nonDisplayableToNonDisplayable_noHookCalled() throws Exception {
    RecordingView view = new RecordingView();
    Object handler = createHandler(view);
    invokeTrackDisplayability(handler, nonDisplayableComponent());
    assertTrue("No hooks when staying non-displayable", view.hookCalls.isEmpty());
  }

  @Test
  public void trackDisplayability_nonDisplayableToDisplayable_firesHandleDisplayable() throws Exception {
    RecordingView view = new RecordingView();
    Object handler = createHandler(view);
    invokeTrackDisplayability(handler, displayableComponent());
    assertEquals(List.of("handleDisplayable"), view.hookCalls);
  }

  @Test
  public void trackDisplayability_displayableToDisplayable_noHookCalled() throws Exception {
    RecordingView view = new RecordingView();
    Object handler = createHandler(view);
    invokeTrackDisplayability(handler, displayableComponent());
    view.hookCalls.clear();
    invokeTrackDisplayability(handler, displayableComponent());
    assertTrue("No hooks when staying displayable", view.hookCalls.isEmpty());
  }

  @Test
  public void trackDisplayability_displayableToNonDisplayable_firesHandleUndisplayable() throws Exception {
    RecordingView view = new RecordingView();
    Object handler = createHandler(view);
    invokeTrackDisplayability(handler, displayableComponent());
    view.hookCalls.clear();
    invokeTrackDisplayability(handler, nonDisplayableComponent());
    assertEquals(List.of("handleUndisplayable"), view.hookCalls);
  }

  // ════════════════════════════════════════════════════════════════════════
  // BEHAVIORAL TESTS — parent-change detection
  // ════════════════════════════════════════════════════════════════════════

  @Test
  public void handleParentChange_nullToParent_firesHandleAddedTo() throws Exception {
    RecordingView view = new RecordingView();
    Object handler = createHandler(view);
    invokeHandleParentChange(handler, new java.awt.Panel());
    assertEquals(List.of("handleAddedTo"), view.hookCalls);
  }

  @Test
  public void handleParentChange_parentToNull_firesHandleRemovedFrom() throws Exception {
    RecordingView view = new RecordingView();
    Object handler = createHandler(view);
    invokeHandleParentChange(handler, new java.awt.Panel());
    view.hookCalls.clear();
    invokeHandleParentChange(handler, null);
    assertEquals(List.of("handleRemovedFrom"), view.hookCalls);
  }

  @Test
  public void handleParentChange_parentToNewParent_firesBothHooks() throws Exception {
    RecordingView view = new RecordingView();
    Object handler = createHandler(view);
    invokeHandleParentChange(handler, new java.awt.Panel());
    view.hookCalls.clear();
    invokeHandleParentChange(handler, new java.awt.Panel());
    assertEquals("Must fire remove then add",
        List.of("handleRemovedFrom", "handleAddedTo"), view.hookCalls);
  }

  @Test
  public void handleParentChange_nullToNull_noHookCalled() throws Exception {
    RecordingView view = new RecordingView();
    Object handler = createHandler(view);
    invokeHandleParentChange(handler, null);
    assertTrue("No hooks when parent stays null", view.hookCalls.isEmpty());
  }

  // ════════════════════════════════════════════════════════════════════════
  // BEHAVIORAL TESTS — processHierarchyEvent dispatch
  // ════════════════════════════════════════════════════════════════════════

  @Test
  public void processHierarchyEvent_displayabilityChanged_noTransition() throws Exception {
    RecordingView view = new RecordingView();
    Component awtComponent = view.getAwtComponent();
    Object handler = getHandlerFromView(view);
    view.hookCalls.clear();

    // Panel is not displayable, isDisplayableState is false → no transition
    HierarchyEvent event = new HierarchyEvent(
        awtComponent, HierarchyEvent.HIERARCHY_CHANGED,
        awtComponent, null,
        HierarchyEvent.DISPLAYABILITY_CHANGED);

    invokeProcessHierarchyEvent(handler, event);
    assertTrue("No transition when component stays non-displayable",
        view.hookCalls.isEmpty());
  }

  @Test
  public void processHierarchyEvent_displayabilityChanged_firesUndisplayableOnTransition() throws Exception {
    RecordingView view = new RecordingView();
    Component awtComponent = view.getAwtComponent();
    Object handler = getHandlerFromView(view);

    // Force isDisplayableState to true to simulate becoming undisplayable
    setDisplayableState(handler, true);
    view.hookCalls.clear();

    HierarchyEvent event = new HierarchyEvent(
        awtComponent, HierarchyEvent.HIERARCHY_CHANGED,
        awtComponent, null,
        HierarchyEvent.DISPLAYABILITY_CHANGED);

    invokeProcessHierarchyEvent(handler, event);
    assertEquals("Transition from displayable to non-displayable must fire hook",
        List.of("handleUndisplayable"), view.hookCalls);
  }

  @Test
  public void processHierarchyEvent_displayabilityChanged_differentComponent_ignored() throws Exception {
    RecordingView view = new RecordingView();
    view.getAwtComponent();
    Object handler = getHandlerFromView(view);
    view.hookCalls.clear();

    Component otherComponent = new java.awt.Panel();
    HierarchyEvent event = new HierarchyEvent(
        otherComponent, HierarchyEvent.HIERARCHY_CHANGED,
        otherComponent, null,
        HierarchyEvent.DISPLAYABILITY_CHANGED);

    invokeProcessHierarchyEvent(handler, event);
    assertTrue("Different component's displayability must not trigger owner hooks",
        view.hookCalls.isEmpty());
  }

  @Test
  public void processHierarchyEvent_parentChanged_firesHandleAddedTo() throws Exception {
    RecordingView view = new RecordingView();
    Component awtComponent = view.getAwtComponent();
    Object handler = getHandlerFromView(view);
    view.hookCalls.clear();

    Container newParent = new java.awt.Panel();
    HierarchyEvent event = new HierarchyEvent(
        awtComponent, HierarchyEvent.HIERARCHY_CHANGED,
        awtComponent, newParent,
        HierarchyEvent.PARENT_CHANGED);

    invokeProcessHierarchyEvent(handler, event);
    assertTrue("PARENT_CHANGED with new parent must fire handleAddedTo",
        view.hookCalls.contains("handleAddedTo"));
  }

  @Test
  public void processHierarchyEvent_parentChangedButSameParent_noHookFired() throws Exception {
    RecordingView view = new RecordingView();
    Component awtComponent = view.getAwtComponent();
    Object handler = getHandlerFromView(view);

    // Set handler's awtParent to the same parent the event will report
    Container sameParent = new java.awt.Panel();
    invokeHandleParentChange(handler, sameParent);
    view.hookCalls.clear();

    HierarchyEvent event = new HierarchyEvent(
        awtComponent, HierarchyEvent.HIERARCHY_CHANGED,
        awtComponent, sameParent,
        HierarchyEvent.PARENT_CHANGED);

    invokeProcessHierarchyEvent(handler, event);
    assertTrue("Same parent must not trigger hooks (exercises isWarningAlreadyPrinted)",
        view.hookCalls.isEmpty());
  }

  // ════════════════════════════════════════════════════════════════════════
  // BEHAVIORAL TESTS — subclass override preserved
  // ════════════════════════════════════════════════════════════════════════

  @Test
  public void handleHierarchyChanged_remainsOverridable() {
    // Verify a subclass can override handleHierarchyChanged with a super call.
    // This must compile and run — confirms the method stays protected & virtual.
    final boolean[] overrideCalled = {false};

    AwtComponentView<java.awt.Panel> subclassView = new AwtComponentView<java.awt.Panel>() {
      @Override
      protected java.awt.Panel createAwtComponent() { return new java.awt.Panel(); }

      @Override
      protected void checkEventDispatchThread() { }

      @Override
      protected void handleHierarchyChanged(HierarchyEvent e) {
        overrideCalled[0] = true;
        super.handleHierarchyChanged(e);
      }
    };

    assertNotNull("Subclass with overridden handleHierarchyChanged must be creatable",
        subclassView);
  }

  // ════════════════════════════════════════════════════════════════════════
  // HELPER METHODS
  // ════════════════════════════════════════════════════════════════════════

  private Object createHandler(AwtComponentView<?> owner) throws Exception {
    Class<?> cls = Class.forName(VIEWS_PACKAGE + ".AwtHierarchyHandler");
    Constructor<?> ctor = cls.getDeclaredConstructor(AwtComponentView.class);
    ctor.setAccessible(true);
    return ctor.newInstance(owner);
  }

  private Object getHandlerFromView(AwtComponentView<?> view) throws Exception {
    Field f = AwtComponentView.class.getDeclaredField("hierarchyHandler");
    f.setAccessible(true);
    return f.get(view);
  }

  private void invokeTrackDisplayability(Object handler, Component component) throws Exception {
    Method m = handler.getClass().getDeclaredMethod("trackDisplayability", Component.class);
    m.setAccessible(true);
    m.invoke(handler, component);
  }

  private void invokeHandleParentChange(Object handler, Container newParent) throws Exception {
    Method m = handler.getClass().getDeclaredMethod("handleParentChange", Container.class);
    m.setAccessible(true);
    m.invoke(handler, newParent);
  }

  private void invokeProcessHierarchyEvent(Object handler, HierarchyEvent event) throws Exception {
    Method m = handler.getClass().getDeclaredMethod("processHierarchyEvent", HierarchyEvent.class);
    m.setAccessible(true);
    m.invoke(handler, event);
  }

  private void setDisplayableState(Object handler, boolean state) throws Exception {
    Field f = handler.getClass().getDeclaredField("isDisplayableState");
    f.setAccessible(true);
    f.setBoolean(handler, state);
  }

  private static void assertFieldAbsent(Class<?> cls, String name, String message) {
    try {
      cls.getDeclaredField(name);
      fail(message + " (field '" + name + "' still in " + cls.getSimpleName() + ")");
    } catch (NoSuchFieldException e) {
      // expected — field has been moved
    }
  }

  private static void assertMethodAbsent(Class<?> cls, String name, String message) {
    for (Method m : cls.getDeclaredMethods()) {
      if (m.getName().equals(name)) {
        fail(message + " (method '" + name + "' still in " + cls.getSimpleName() + ")");
      }
    }
  }

  private static Path findSourceFile(String filename) {
    Path candidate = Paths.get(SOURCE_DIR, filename);
    if (Files.exists(candidate)) {
      return candidate;
    }
    Path cwd = Paths.get(System.getProperty("user.dir"));
    candidate = cwd.resolve(SOURCE_DIR).resolve(filename);
    if (Files.exists(candidate)) {
      return candidate;
    }
    Path dir = cwd;
    while (dir != null) {
      if (Files.exists(dir.resolve(".git"))) {
        candidate = dir.resolve(SOURCE_DIR).resolve(filename);
        if (Files.exists(candidate)) {
          return candidate;
        }
        break;
      }
      dir = dir.getParent();
    }
    return Paths.get(SOURCE_DIR, filename);
  }
}
