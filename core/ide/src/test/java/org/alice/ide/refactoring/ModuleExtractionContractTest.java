package org.alice.ide.refactoring;

import org.junit.Test;

import java.io.InputStream;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * TDD contract tests for PR #833 (IK Poser) and PR #834 (Clipboard/DnD).
 *
 * These tests verify that module extraction preserved the expected classes
 * on the classpath. After extraction, classes move from core/ide into
 * core/ik-poser and core/clipboard-dnd respectively, but must remain
 * loadable from any module that declares the dependency.
 *
 * FAILS on develop: classes are in core/ide packages, not in extracted modules.
 * PASSES after PR #833/#834 merge: classes on classpath via new module deps.
 */
public class ModuleExtractionContractTest {

  // ── PR #833: IK Poser module extraction ───────────────────────────

  @Test
  public void ikPoserContext_interfaceLoadable() throws ClassNotFoundException {
    Class<?> cls = Class.forName("org.lgna.ik.poser.IkPoserContext");
    assertNotNull(cls);
    assertTrue("IkPoserContext must be an interface", cls.isInterface());
  }

  @Test
  public void ikBone_classLoadable() throws ClassNotFoundException {
    Class<?> cls = Class.forName("org.lgna.ik.Bone");
    assertNotNull(cls);
  }

  @Test
  public void ikChain_classLoadable() throws ClassNotFoundException {
    Class<?> cls = Class.forName("org.lgna.ik.Chain");
    assertNotNull(cls);
  }

  @Test
  public void fieldFinder_classLoadable() throws ClassNotFoundException {
    Class<?> cls = Class.forName("org.lgna.ik.poser.FieldFinder");
    assertNotNull(cls);
  }

  @Test
  public void poseAstUtilities_classLoadable() throws ClassNotFoundException {
    Class<?> cls = Class.forName("org.lgna.ik.poser.PoseAstUtilities");
    assertNotNull(cls);
  }

  @Test
  public void timeLineMath_classLoadable() throws ClassNotFoundException {
    Class<?> cls = Class.forName("org.lgna.ik.poser.animation.TimeLineMath");
    assertNotNull(cls);
  }

  @Test
  public void keyFrameData_classLoadable() throws ClassNotFoundException {
    Class<?> cls = Class.forName("org.lgna.ik.poser.animation.KeyFrameData");
    assertNotNull(cls);
  }

  @Test
  public void keyFrameStyles_classLoadable() throws ClassNotFoundException {
    Class<?> cls = Class.forName("org.lgna.ik.poser.animation.KeyFrameStyles");
    assertNotNull(cls);
  }

  @Test
  public void timeLine_classLoadable() throws ClassNotFoundException {
    Class<?> cls = Class.forName("org.lgna.ik.poser.animation.TimeLine");
    assertNotNull(cls);
  }

  @Test
  public void anchors_classLoadable() throws ClassNotFoundException {
    Class<?> cls = Class.forName("org.lgna.ik.poser.anchors.Anchors");
    assertNotNull(cls);
  }

  @Test
  public void poserSphereManipulator_classLoadable() throws ClassNotFoundException {
    Class<?> cls = Class.forName("org.lgna.ik.poser.PoserSphereManipulator");
    assertNotNull(cls);
  }

  // ── PR #834: Clipboard/DnD module extraction ─────────────────────

  @Test
  public void clipboard_classLoadable() throws ClassNotFoundException {
    Class<?> cls = Class.forName("org.alice.ide.clipboard.Clipboard");
    assertNotNull(cls);
  }

  @Test
  public void clipboardDnDProvider_classLoadable() throws ClassNotFoundException {
    Class<?> cls = Class.forName("org.alice.ide.clipboard.ClipboardDnDProvider");
    assertNotNull(cls);
  }

  @Test
  public void clipboardProvider_interfaceLoadable() throws ClassNotFoundException {
    Class<?> cls = Class.forName("org.alice.ide.clipboard.ClipboardProvider");
    assertNotNull(cls);
    assertTrue("ClipboardProvider must be an interface", cls.isInterface());
  }

  @Test
  public void clipboardProvider_spiMetaInfServiceExists() {
    InputStream stream = getClass().getClassLoader()
        .getResourceAsStream("META-INF/services/org.alice.ide.clipboard.ClipboardProvider");
    assertNotNull("SPI descriptor must exist for ClipboardProvider", stream);
  }

  @Test
  public void dragReceptorState_classLoadable() throws ClassNotFoundException {
    Class<?> cls = Class.forName("org.alice.ide.clipboard.DragReceptorState");
    assertNotNull(cls);
    assertTrue("DragReceptorState must be an enum", cls.isEnum());
  }

  @Test
  public void copyFromClipboardOperation_classLoadable() throws ClassNotFoundException {
    Class<?> cls = Class.forName("org.alice.ide.clipboard.CopyFromClipboardOperation");
    assertNotNull(cls);
  }

  @Test
  public void pasteFromClipboardOperation_classLoadable() throws ClassNotFoundException {
    Class<?> cls = Class.forName("org.alice.ide.clipboard.PasteFromClipboardOperation");
    assertNotNull(cls);
  }

  @Test
  public void cutToClipboardOperation_classLoadable() throws ClassNotFoundException {
    Class<?> cls = Class.forName("org.alice.ide.clipboard.CutToClipboardOperation");
    assertNotNull(cls);
  }

  @Test
  public void copyToClipboardOperation_classLoadable() throws ClassNotFoundException {
    Class<?> cls = Class.forName("org.alice.ide.clipboard.CopyToClipboardOperation");
    assertNotNull(cls);
  }

  @Test
  public void clipboardBoardRenderer_classLoadable() throws ClassNotFoundException {
    Class<?> cls = Class.forName("org.alice.ide.clipboard.icons.ClipboardBoardRenderer");
    assertNotNull(cls);
  }

  @Test
  public void clipboardIcon_classLoadable() throws ClassNotFoundException {
    Class<?> cls = Class.forName("org.alice.ide.clipboard.icons.ClipboardIcon");
    assertNotNull(cls);
  }

  @Test
  public void gradientPaintFactory_classLoadable() throws ClassNotFoundException {
    Class<?> cls = Class.forName("org.alice.ide.clipboard.icons.GradientPaintFactory");
    assertNotNull(cls);
  }

  @Test
  public void copyOperation_classLoadable() throws ClassNotFoundException {
    Class<?> cls = Class.forName("org.alice.ide.croquet.models.clipboard.CopyOperation");
    assertNotNull(cls);
  }

  @Test
  public void cutOperation_classLoadable() throws ClassNotFoundException {
    Class<?> cls = Class.forName("org.alice.ide.croquet.models.clipboard.CutOperation");
    assertNotNull(cls);
  }

  @Test
  public void pasteOperation_classLoadable() throws ClassNotFoundException {
    Class<?> cls = Class.forName("org.alice.ide.croquet.models.clipboard.PasteOperation");
    assertNotNull(cls);
  }

  // ── Clipboard i18n resources ──────────────────────────────────────

  @Test
  public void clipboardResourceBundle_existsOnClasspath() {
    InputStream stream = getClass().getClassLoader()
        .getResourceAsStream("org/alice/ide/clipboard/croquet.properties");
    assertNotNull("Clipboard resource bundle must be on classpath", stream);
  }

  // ── IK Poser data classes ─────────────────────────────────────────

  @Test
  public void anchorEvent_classLoadable() throws ClassNotFoundException {
    Class.forName("org.lgna.ik.poser.anchors.events.AnchorEvent");
  }

  @Test
  public void anchorListener_classLoadable() throws ClassNotFoundException {
    Class.forName("org.lgna.ik.poser.anchors.events.AnchorListener");
  }

  @Test
  public void poserGenerationException_classLoadable() throws ClassNotFoundException {
    Class<?> cls = Class.forName("org.lgna.ik.poser.PoserGenerationException");
    assertTrue("PoserGenerationException must extend Exception",
        Exception.class.isAssignableFrom(cls));
  }

  @Test
  public void cannotCreateExpressionException_classLoadable() throws ClassNotFoundException {
    Class<?> cls = Class.forName("org.lgna.ik.poser.CannotCreateExpressionException");
    assertTrue("CannotCreateExpressionException must extend Exception",
        Exception.class.isAssignableFrom(cls));
  }
}
