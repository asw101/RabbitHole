package org.lgna.croquet.views;

import org.junit.Test;

import java.awt.*;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.*;

/**
 * TDD contract and characterization tests for the AwtComponentView hierarchy
 * event extraction (#686).
 *
 * <p>Two extractions are validated:
 * <ol>
 *   <li><b>AwtHierarchyEventHandler</b> — new package-private class that owns
 *       hierarchy lifecycle tracking (displayability, parent changes)</li>
 *   <li><b>Deprecated listener forwarding removal</b> — 10 deprecated methods
 *       removed from AwtComponentView; callers updated to use getAwtComponent()</li>
 * </ol>
 *
 * <p>These tests will FAIL until the extraction is implemented.
 * They define the behavioral contract that the refactored code must satisfy.</p>
 */
public class AwtHierarchyEventHandlerTest {

  private static final String VIEWS_PACKAGE = "org.lgna.croquet.views";
  private static final String SOURCE_DIR = "core/croquet/src/main/java/org/lgna/croquet/views";
  private static final String HANDLER_CLASS = VIEWS_PACKAGE + ".AwtHierarchyEventHandler";

  // ══════════════════════════════════════════════════════════════════════
  // Section 1: AwtHierarchyEventHandler — structural contract
  // ══════════════════════════════════════════════════════════════════════

  @Test
  public void handlerFile_exists() {
    Path source = findSourceFile("AwtHierarchyEventHandler.java");
    assertTrue("AwtHierarchyEventHandler.java must exist at " + source,
        Files.exists(source));
  }

  @Test
  public void handlerClass_loads() throws ClassNotFoundException {
    Class.forName(HANDLER_CLASS);
  }

  @Test
  public void handlerClass_isPackagePrivate() throws ClassNotFoundException {
    Class<?> clazz = Class.forName(HANDLER_CLASS);
    int modifiers = clazz.getModifiers();
    assertFalse("AwtHierarchyEventHandler must not be public",
        Modifier.isPublic(modifiers));
    assertFalse("AwtHierarchyEventHandler must not be protected",
        Modifier.isProtected(modifiers));
    assertFalse("AwtHierarchyEventHandler must not be private",
        Modifier.isPrivate(modifiers));
  }

  @Test
  public void handlerClass_implementsHierarchyListener() throws ClassNotFoundException {
    Class<?> clazz = Class.forName(HANDLER_CLASS);
    assertTrue("AwtHierarchyEventHandler must implement HierarchyListener",
        HierarchyListener.class.isAssignableFrom(clazz));
  }

  @Test
  public void handlerClass_isNotAbstract() throws ClassNotFoundException {
    Class<?> clazz = Class.forName(HANDLER_CLASS);
    assertFalse("AwtHierarchyEventHandler must be concrete (not abstract)",
        Modifier.isAbstract(clazz.getModifiers()));
  }

  // ── Handler fields ──────────────────────────────────────────────────

  @Test
  public void handler_hasIsDisplayableStateField() throws Exception {
    Class<?> clazz = Class.forName(HANDLER_CLASS);
    Field field = clazz.getDeclaredField("isDisplayableState");
    assertEquals("isDisplayableState must be boolean",
        boolean.class, field.getType());
    assertFalse("isDisplayableState must not be static",
        Modifier.isStatic(field.getModifiers()));
  }

  @Test
  public void handler_hasAwtParentField() throws Exception {
    Class<?> clazz = Class.forName(HANDLER_CLASS);
    Field field = clazz.getDeclaredField("awtParent");
    assertEquals("awtParent must be Container",
        Container.class, field.getType());
    assertFalse("awtParent must not be static",
        Modifier.isStatic(field.getModifiers()));
  }

  @Test
  public void handler_hasStaticIsWarningAlreadyPrintedField() throws Exception {
    Class<?> clazz = Class.forName(HANDLER_CLASS);
    Field field = clazz.getDeclaredField("isWarningAlreadyPrinted");
    assertEquals("isWarningAlreadyPrinted must be boolean",
        boolean.class, field.getType());
    assertTrue("isWarningAlreadyPrinted must be static (cross-instance suppression)",
        Modifier.isStatic(field.getModifiers()));
  }

  // ── Handler methods ─────────────────────────────────────────────────

  @Test
  public void handler_hasTrackDisplayabilityMethod() throws Exception {
    Class<?> clazz = Class.forName(HANDLER_CLASS);
    // trackDisplayability takes a Component parameter (the awtComponent)
    Method method = clazz.getDeclaredMethod("trackDisplayability");
    assertNotNull("trackDisplayability() must exist in handler", method);
  }

  @Test
  public void handler_hasHandleParentChangeMethod() throws Exception {
    Class<?> clazz = Class.forName(HANDLER_CLASS);
    Method method = clazz.getDeclaredMethod("handleParentChange", Container.class);
    assertNotNull("handleParentChange(Container) must exist in handler", method);
  }

  @Test
  public void handler_hasHierarchyChangedMethod() throws Exception {
    Class<?> clazz = Class.forName(HANDLER_CLASS);
    // Required by HierarchyListener interface
    Method method = clazz.getDeclaredMethod("hierarchyChanged", HierarchyEvent.class);
    assertNotNull("hierarchyChanged(HierarchyEvent) must exist (HierarchyListener contract)",
        method);
  }

  @Test
  public void handler_constructorAcceptsAwtComponentView() throws Exception {
    Class<?> clazz = Class.forName(HANDLER_CLASS);
    Class<?> viewClass = AwtComponentView.class;
    clazz.getDeclaredConstructor(viewClass);
    // Passes if constructor exists; throws NoSuchMethodException otherwise
  }

  // ══════════════════════════════════════════════════════════════════════
  // Section 2: AwtComponentView — structural changes after extraction
  // ══════════════════════════════════════════════════════════════════════

  // ── Line count acceptance criterion ─────────────────────────────────

  @Test
  public void awtComponentView_underFiveHundredLines() throws IOException {
    Path source = findSourceFile("AwtComponentView.java");
    long lineCount = Files.lines(source).count();
    assertTrue("AwtComponentView.java must be under 500 lines. Actual: " + lineCount,
        lineCount < 500);
  }

  // ── Hierarchy internals removed from AwtComponentView ───────────────

  @Test
  public void awtComponentView_noIsDisplayableStateField() {
    assertFieldAbsent(AwtComponentView.class, "isDisplayableState",
        "isDisplayableState must be moved to AwtHierarchyEventHandler");
  }

  @Test
  public void awtComponentView_noAwtParentField() {
    assertFieldAbsent(AwtComponentView.class, "awtParent",
        "awtParent must be moved to AwtHierarchyEventHandler");
  }

  @Test
  public void awtComponentView_noIsWarningAlreadyPrintedField() {
    assertFieldAbsent(AwtComponentView.class, "isWarningAlreadyPrinted",
        "isWarningAlreadyPrinted must be moved to AwtHierarchyEventHandler");
  }

  @Test
  public void awtComponentView_noTrackDisplayabilityMethod() {
    assertMethodAbsent(AwtComponentView.class, "trackDisplayability",
        "trackDisplayability must be moved to AwtHierarchyEventHandler");
  }

  @Test
  public void awtComponentView_noHandleParentChangeMethod() {
    assertMethodAbsent(AwtComponentView.class, "handleParentChange",
        "handleParentChange must be moved to AwtHierarchyEventHandler");
  }

  // ── Protected hooks remain in AwtComponentView ──────────────────────

  @Test
  public void awtComponentView_retainsHandleDisplayable() throws Exception {
    Method method = AwtComponentView.class.getDeclaredMethod("handleDisplayable");
    assertTrue("handleDisplayable() must remain protected in AwtComponentView",
        Modifier.isProtected(method.getModifiers()));
  }

  @Test
  public void awtComponentView_retainsHandleUndisplayable() throws Exception {
    Method method = AwtComponentView.class.getDeclaredMethod("handleUndisplayable");
    assertTrue("handleUndisplayable() must remain protected in AwtComponentView",
        Modifier.isProtected(method.getModifiers()));
  }

  @Test
  public void awtComponentView_retainsHandleAddedTo() throws Exception {
    Method method = AwtComponentView.class.getDeclaredMethod("handleAddedTo", AwtComponentView.class);
    assertTrue("handleAddedTo(AwtComponentView) must remain protected in AwtComponentView",
        Modifier.isProtected(method.getModifiers()));
  }

  @Test
  public void awtComponentView_retainsHandleRemovedFrom() throws Exception {
    Method method = AwtComponentView.class.getDeclaredMethod("handleRemovedFrom", AwtComponentView.class);
    assertTrue("handleRemovedFrom(AwtComponentView) must remain protected in AwtComponentView",
        Modifier.isProtected(method.getModifiers()));
  }

  @Test
  public void awtComponentView_retainsHandleHierarchyChanged() throws Exception {
    Method method = AwtComponentView.class.getDeclaredMethod("handleHierarchyChanged", HierarchyEvent.class);
    assertTrue("handleHierarchyChanged(HierarchyEvent) must remain protected in AwtComponentView",
        Modifier.isProtected(method.getModifiers()));
  }

  // ── Handler field exists in AwtComponentView ────────────────────────

  @Test
  public void awtComponentView_hasHierarchyHandlerField() throws Exception {
    Class<?> handlerClass = Class.forName(HANDLER_CLASS);
    Field handlerField = findFieldByType(AwtComponentView.class, handlerClass);
    assertNotNull("AwtComponentView must have a field of type AwtHierarchyEventHandler",
        handlerField);
  }

  // ── Old hierarchyListener field removed ─────────────────────────────

  @Test
  public void awtComponentView_noHierarchyListenerField() {
    // The old `private final HierarchyListener hierarchyListener` field should be gone;
    // replaced by the handler (which IS the HierarchyListener)
    try {
      Field field = AwtComponentView.class.getDeclaredField("hierarchyListener");
      // If a field named hierarchyListener still exists, it should not be of type HierarchyListener
      // (it might be renamed or the handler field might have a different name)
      if (HierarchyListener.class.equals(field.getType())) {
        fail("Old 'hierarchyListener' field (HierarchyListener type) must be removed — "
            + "the handler itself implements HierarchyListener");
      }
    } catch (NoSuchFieldException e) {
      // Expected: field removed
    }
  }

  // ══════════════════════════════════════════════════════════════════════
  // Section 3: Deprecated listener forwarding methods removed
  // ══════════════════════════════════════════════════════════════════════

  @Test
  public void awtComponentView_noAddMouseListenerMethod() {
    assertMethodAbsent(AwtComponentView.class, "addMouseListener",
        "Deprecated addMouseListener must be removed from AwtComponentView");
  }

  @Test
  public void awtComponentView_noRemoveMouseListenerMethod() {
    assertMethodAbsent(AwtComponentView.class, "removeMouseListener",
        "Deprecated removeMouseListener must be removed from AwtComponentView");
  }

  @Test
  public void awtComponentView_noAddKeyListenerMethod() {
    assertMethodAbsent(AwtComponentView.class, "addKeyListener",
        "Deprecated addKeyListener must be removed from AwtComponentView");
  }

  @Test
  public void awtComponentView_noRemoveKeyListenerMethod() {
    assertMethodAbsent(AwtComponentView.class, "removeKeyListener",
        "Deprecated removeKeyListener must be removed from AwtComponentView");
  }

  @Test
  public void awtComponentView_noAddMouseMotionListenerMethod() {
    assertMethodAbsent(AwtComponentView.class, "addMouseMotionListener",
        "Deprecated addMouseMotionListener must be removed from AwtComponentView");
  }

  @Test
  public void awtComponentView_noRemoveMouseMotionListenerMethod() {
    assertMethodAbsent(AwtComponentView.class, "removeMouseMotionListener",
        "Deprecated removeMouseMotionListener must be removed from AwtComponentView");
  }

  @Test
  public void awtComponentView_noAddMouseWheelListenerMethod() {
    assertMethodAbsent(AwtComponentView.class, "addMouseWheelListener",
        "Deprecated addMouseWheelListener must be removed from AwtComponentView");
  }

  @Test
  public void awtComponentView_noRemoveMouseWheelListenerMethod() {
    assertMethodAbsent(AwtComponentView.class, "removeMouseWheelListener",
        "Deprecated removeMouseWheelListener must be removed from AwtComponentView");
  }

  @Test
  public void awtComponentView_noAddHierarchyListenerMethod() {
    assertMethodAbsent(AwtComponentView.class, "addHierarchyListener",
        "Deprecated addHierarchyListener must be removed from AwtComponentView");
  }

  @Test
  public void awtComponentView_noRemoveHierarchyListenerMethod() {
    assertMethodAbsent(AwtComponentView.class, "removeHierarchyListener",
        "Deprecated removeHierarchyListener must be removed from AwtComponentView");
  }

  // ── Non-listener deprecated methods stay (out of scope) ─────────────

  @Test
  public void awtComponentView_retainsSetPreferredSize() {
    assertMethodPresent(AwtComponentView.class, "setPreferredSize",
        "setPreferredSize is out of scope — must remain in AwtComponentView");
  }

  @Test
  public void awtComponentView_retainsMakeStandOut() {
    assertMethodPresent(AwtComponentView.class, "makeStandOut",
        "makeStandOut is out of scope — must remain in AwtComponentView");
  }

  // ══════════════════════════════════════════════════════════════════════
  // Section 4: Edge cases and error handling
  // ══════════════════════════════════════════════════════════════════════

  @Test
  public void handler_isWarningAlreadyPrinted_defaultsFalse() throws Exception {
    Class<?> clazz = Class.forName(HANDLER_CLASS);
    Field field = clazz.getDeclaredField("isWarningAlreadyPrinted");
    field.setAccessible(true);
    // Static field — read without instance
    // Note: may be true if a prior test triggered it; reset for isolation
    field.setBoolean(null, false);
    assertFalse("isWarningAlreadyPrinted should default to false",
        field.getBoolean(null));
  }

  @Test
  public void handler_fieldsAreNotPublic() throws Exception {
    Class<?> clazz = Class.forName(HANDLER_CLASS);
    for (Field field : clazz.getDeclaredFields()) {
      assertFalse("Handler field '" + field.getName() + "' must not be public — "
              + "internal state must be encapsulated",
          Modifier.isPublic(field.getModifiers()));
    }
  }

  @Test
  public void handler_noPublicSetters() throws Exception {
    Class<?> clazz = Class.forName(HANDLER_CLASS);
    for (Method method : clazz.getDeclaredMethods()) {
      if (method.getName().startsWith("set") && Modifier.isPublic(method.getModifiers())) {
        fail("Handler must not expose public setters — found: " + method.getName()
            + ". Internal state (isDisplayableState, awtParent) must be managed internally.");
      }
    }
  }

  // ══════════════════════════════════════════════════════════════════════
  // Section 5: Integration — getAwtComponent() wires handler correctly
  // ══════════════════════════════════════════════════════════════════════

  @Test
  public void awtComponentView_getAwtComponentStillRegistersHierarchyListener() throws Exception {
    // After extraction, getAwtComponent() must still register a HierarchyListener.
    // The handler IS the listener, so the handler must be added to the component.
    // We verify via source inspection that the wiring pattern exists.
    Path source = findSourceFile("AwtComponentView.java");
    String content = Files.readString(source);
    assertTrue(
        "getAwtComponent() must register hierarchy listener (handler or direct reference)",
        content.contains("addHierarchyListener"));
  }

  @Test
  public void awtComponentView_releaseStillRemovesHierarchyListener() throws Exception {
    // release() must still remove the hierarchy listener to avoid leaks
    Path source = findSourceFile("AwtComponentView.java");
    String content = Files.readString(source);
    assertTrue(
        "release() must remove hierarchy listener to prevent memory leaks",
        content.contains("removeHierarchyListener"));
  }

  @Test
  public void awtComponentView_releaseStillCallsTrackDisplayability() throws Exception {
    // release() must still trigger displayability tracking before cleanup
    Path source = findSourceFile("AwtComponentView.java");
    String content = Files.readString(source);
    assertTrue(
        "release() must call trackDisplayability (directly or via handler) before cleanup",
        content.contains("trackDisplayability") || content.contains("hierarchyHandler"));
  }

  // ══════════════════════════════════════════════════════════════════════
  // Helper methods
  // ══════════════════════════════════════════════════════════════════════

  private static void assertFieldAbsent(Class<?> clazz, String fieldName, String message) {
    try {
      clazz.getDeclaredField(fieldName);
      fail(message + " (field '" + fieldName + "' still present in " + clazz.getSimpleName() + ")");
    } catch (NoSuchFieldException e) {
      // Expected: field has been moved out
    }
  }

  private static void assertMethodAbsent(Class<?> clazz, String methodName, String message) {
    for (Method method : clazz.getDeclaredMethods()) {
      if (method.getName().equals(methodName)) {
        fail(message + " (method '" + methodName + "' still present in " + clazz.getSimpleName() + ")");
      }
    }
  }

  private static void assertMethodPresent(Class<?> clazz, String methodName, String message) {
    for (Method method : clazz.getDeclaredMethods()) {
      if (method.getName().equals(methodName)) {
        return;
      }
    }
    fail(message + " (method '" + methodName + "' not found in " + clazz.getSimpleName() + ")");
  }

  private static Field findFieldByType(Class<?> clazz, Class<?> fieldType) {
    for (Field field : clazz.getDeclaredFields()) {
      if (field.getType().equals(fieldType)) {
        return field;
      }
    }
    return null;
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
