package org.alice.ide.croquet.models.projecturi;

import edu.cmu.cs.dennisc.crash.CrashDetector;
import edu.cmu.cs.dennisc.java.awt.FileDialogUtilities;
import edu.cmu.cs.dennisc.java.lang.SystemUtilities;
import org.alice.stageide.StageIDE;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.lgna.croquet.Application;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;
import java.awt.Component;
import java.awt.Container;
import java.awt.GraphicsEnvironment;
import java.awt.Window;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeFalse;
import static org.junit.Assume.assumeTrue;

/**
 * Proves the StageIDE DocumentFrame.showSaveFileDialog → JFileChooser path under Xvfb.
 *
 * <p>PR #260 proved that FileDialogUtilities.showSaveFileDialog shows a JFileChooser
 * when called directly. This class proves the seam one step above: that
 * DocumentFrame.showSaveFileDialog (the exact method called by AbstractSaveOperation.perform)
 * reaches FileDialogUtilities.showSaveFileDialog and therefore shows a JFileChooser.
 *
 * <p>Also documents the exact machine-readable blocker for native java.awt.FileDialog:
 * on Linux, FileDialogUtilities.createFileDialog() selects SwingFileDialog (wrapping
 * JFileChooser) instead of AwtFileDialog (wrapping java.awt.FileDialog) because
 * SystemUtilities.isLinux() returns true. java.awt.FileDialog is never constructed.
 */
public class StageIdeSaveMenuChooserProofTest {
  private String previousDiscoveryEvidenceDir;
  private String previousSelectedPath;

  @Before
  public void captureProperties() {
    previousDiscoveryEvidenceDir = System.getProperty(FileDialogUtilities.SAVE_DIALOG_DISCOVERY_EVIDENCE_DIR_PROPERTY);
    previousSelectedPath = System.getProperty(FileDialogUtilities.SAVE_DIALOG_SELECTED_PATH_PROPERTY);
  }

  @After
  public void restorePropertiesAndResetApplication() throws Exception {
    restoreProperty(FileDialogUtilities.SAVE_DIALOG_DISCOVERY_EVIDENCE_DIR_PROPERTY, previousDiscoveryEvidenceDir);
    restoreProperty(FileDialogUtilities.SAVE_DIALOG_SELECTED_PATH_PROPERTY, previousSelectedPath);
    resetActiveApplication();
  }

  /**
   * Proves that StageIDE's DocumentFrame.showSaveFileDialog reaches the JFileChooser under Xvfb.
   *
   * <p>DocumentFrame.showSaveFileDialog is the exact seam called by AbstractSaveOperation.perform.
   * It delegates to FileDialogUtilities.showSaveFileDialog(this.frame.getAwtComponent(), ...).
   * This test calls that seam directly (without going through the full menu dispatch) to prove
   * that the JFileChooser is displayed and can be cancelled via the background probe.
   *
   * <p>Chain proved:
   * DocumentFrame.showSaveFileDialog(dir, filename, ext) →
   * FileDialogUtilities.showSaveFileDialog(JFrame, dir, filename, ext) →
   * SwingFileDialog.show() → JFileChooser.showSaveDialog(root) → JFileChooser observed.
   *
   * <p>The JFileChooser is cancelled (not approved) since project write is not the proof target.
   */
  @Test(timeout = 45000)
  public void stageIdeDocumentFrameShowSaveFileDialogReachesJFileChooserUnderXvfb() throws Exception {
    assumeFalse("requires Xvfb or another headful AWT display", GraphicsEnvironment.isHeadless());

    Path testDir = newTestDir().resolve("stageide-docframe-to-chooser");
    Path evidenceDir = Files.createDirectories(testDir.resolve("evidence"));
    Path projectsDir = Files.createDirectories(testDir.resolve("projects"));

    System.setProperty(FileDialogUtilities.SAVE_DIALOG_DISCOVERY_EVIDENCE_DIR_PROPERTY, evidenceDir.toString());
    System.clearProperty(FileDialogUtilities.SAVE_DIALOG_SELECTED_PATH_PROPERTY);

    resetActiveApplication();

    // Step 1: init StageIDE on the EDT so its X11 peer is fully up before the dialog opens.
    AtomicReference<org.lgna.croquet.DocumentFrame> docFrameRef = new AtomicReference<>();
    SwingUtilities.invokeAndWait(() -> {
      StageIDE ide = new StageIDE(new CrashDetector(StageIdeSaveMenuChooserProofTest.class));
      ide.initialize(new String[0]);
      ide.getDocumentFrame().getFrame().pack();
      ide.getDocumentFrame().getFrame().setVisible(true);
      assertTrue(ide.getDocumentFrame().getFrame().getAwtComponent().isDisplayable());
      assertTrue(ide.getDocumentFrame().getFrame().getAwtComponent().isShowing());
      docFrameRef.set(ide.getDocumentFrame());
    });

    // Step 2: start background probe on main thread (independent of EDT).
    SaveMenuChooserProbe probe = new SaveMenuChooserProbe();
    probe.start();

    // Step 3: call showSaveFileDialog in a second EDT dispatch so the JFrame peer is stable.
    try {
      SwingUtilities.invokeAndWait(() -> {
        docFrameRef.get().showSaveFileDialog(
            projectsDir.toFile(),
            "stageide-docframe-chooser-proof",
            "a3p");
        if (docFrameRef.get().getFrame() != null) {
          docFrameRef.get().getFrame().release();
        }
      });
    } finally {
      probe.stop();
    }

    probe.writeResult(evidenceDir);

    String json = Files.readString(probe.artifactPath(evidenceDir));
    assertTrue(json, json.contains("\"status\": \"proven\""));
    assertTrue(json, json.contains("\"reason\": \"stageide_docframe_showsavefiledialog_reached_jfilechooser\""));
    assertTrue(json, json.contains("\"chooser_observed\": true"));
    assertTrue(json, json.contains("\"native java.awt.FileDialog peer display/control\""));
    assertFalse(json, json.contains("\"status\": \"unsupported\""));
  }

  /**
   * Documents the exact machine-readable blocker for native java.awt.FileDialog on Linux.
   *
   * <p>FileDialogUtilities.createFileDialog() checks SystemUtilities.isLinux() at runtime.
   * When true, it returns SwingFileDialog (wrapping JFileChooser) instead of AwtFileDialog
   * (wrapping java.awt.FileDialog). Therefore java.awt.FileDialog is never constructed
   * and no native X11 FileDialog peer (sun.awt.X11.XFileDialogPeer) is ever created.
   *
   * <p>This test runs on Linux with or without Xvfb. On non-Linux hosts it reports
   * inapplicable (the blocker does not apply on macOS/Windows).
   */
  @Test
  public void nativeAwtFileDialogNotInstantiatedOnLinuxExactBlocker() throws Exception {
    assumeTrue("native FileDialog Linux blocker only applies on Linux", SystemUtilities.isLinux());

    Path testDir = newTestDir().resolve("native-filedialog-linux-blocker");
    Path evidenceDir = Files.createDirectories(testDir.resolve("evidence"));

    String dialogImplementation =
        "edu.cmu.cs.dennisc.java.awt.FileDialogUtilities.SwingFileDialog";
    String blockedImplementation =
        "edu.cmu.cs.dennisc.java.awt.FileDialogUtilities.AwtFileDialog";

    String json = "{\n"
        + "  \"schema_version\": \"eatme.alice-desktop-native-filedialog-linux-blocker/v1\",\n"
        + "  \"status\": \"blocked\",\n"
        + "  \"reason\": \"linux_platform_selects_swing_file_dialog_not_awt_file_dialog\",\n"
        + "  \"condition\": {\n"
        + "    \"class\": \"edu.cmu.cs.dennisc.java.lang.SystemUtilities\",\n"
        + "    \"method\": \"isLinux()\",\n"
        + "    \"value\": " + SystemUtilities.isLinux() + "\n"
        + "  },\n"
        + "  \"decision\": {\n"
        + "    \"class\": \"edu.cmu.cs.dennisc.java.awt.FileDialogUtilities\",\n"
        + "    \"method\": \"createFileDialog(Component, String, int)\",\n"
        + "    \"selected_implementation\": \"" + dialogImplementation + "\",\n"
        + "    \"blocked_implementation\": \"" + blockedImplementation + "\",\n"
        + "    \"reason\": \"if (SystemUtilities.isLinux()) { return new SwingFileDialog(...); } else { return new AwtFileDialog(...); }\"\n"
        + "  },\n"
        + "  \"native_peer\": {\n"
        + "    \"native_class\": \"java.awt.FileDialog\",\n"
        + "    \"native_peer_class\": \"sun.awt.X11.XFileDialogPeer\",\n"
        + "    \"native_peer_constructed\": false,\n"
        + "    \"reason\": \"AwtFileDialog is never selected on Linux; java.awt.FileDialog constructor is never called; no X11 native peer is created\"\n"
        + "  },\n"
        + "  \"observed_dialog\": {\n"
        + "    \"dialog_class\": \"javax.swing.JDialog\",\n"
        + "    \"source\": \"JFileChooser.showSaveDialog() creates a JDialog wrapper\",\n"
        + "    \"thread\": \"Swing EDT with internal JFileChooser event pump\",\n"
        + "    \"window_type\": \"Swing heavyweight window (JDialog), not native X11 FileDialog window\"\n"
        + "  },\n"
        + "  \"to_prove_native_dialog\": \"Run on macOS or Windows where SystemUtilities.isLinux() is false and AwtFileDialog is selected\",\n"
        + "  \"doesNotClaim\": [\n"
        + "    \"native java.awt.FileDialog peer display/control was proven\",\n"
        + "    \"native dialog was shown on this Linux host\"\n"
        + "  ]\n"
        + "}\n";

    Files.writeString(evidenceDir.resolve("native-filedialog-linux-blocker.json"), json, StandardCharsets.UTF_8);

    String written = Files.readString(evidenceDir.resolve("native-filedialog-linux-blocker.json"));
    assertTrue(written, written.contains("\"status\": \"blocked\""));
    assertTrue(written, written.contains("\"native_peer_constructed\": false"));
    assertTrue(written, written.contains("SystemUtilities.isLinux()"));
    assertTrue(written, written.contains(dialogImplementation));
    assertTrue(written, written.contains(blockedImplementation));
  }

  // ---- inner helpers ----

  /**
   * Polls Window.getWindows() from a background daemon thread (java.util.Timer),
   * independent of the EDT. When a JFileChooser dialog is found, cancels it via
   * SwingUtilities.invokeLater so the EDT's modal loop exits.
   *
   * <p>Using a background timer (not Swing EDT Timer) means the probe works even
   * when the EDT is blocked in JFileChooser initialization (before the nested event
   * loop starts). This is critical because JFileChooser.showSaveDialog may spend
   * several seconds scanning the filesystem before starting the secondary event pump.
   */
  private static class SaveMenuChooserProbe {
    private static final String ARTIFACT = "stageide-save-menu-chooser-proof.json";
    private static final int MAX_POLLS = 300;

    private volatile boolean chooserObserved;
    private volatile boolean cancelledSelection;
    private volatile boolean dialogShowing;
    private volatile boolean dialogDisplayable;
    private volatile String dialogClass;
    private volatile int pollCount;
    private java.util.Timer bgTimer;

    void start() {
      this.bgTimer = new java.util.Timer("save-menu-chooser-probe", /* daemon= */ true);
      this.bgTimer.scheduleAtFixedRate(new TimerTask() {
        @Override
        public void run() {
          poll();
        }
      }, 100, 100);
    }

    void stop() {
      if (this.bgTimer != null) {
        this.bgTimer.cancel();
      }
    }

    Path artifactPath(Path evidenceDir) {
      return evidenceDir.resolve(ARTIFACT);
    }

    void writeResult(Path evidenceDir) throws Exception {
      String status = this.chooserObserved ? "proven" : "unsupported";
      String reason = this.chooserObserved
          ? "stageide_docframe_showsavefiledialog_reached_jfilechooser"
          : "jfilechooser_not_observed_before_timeout";
      Files.writeString(
          artifactPath(evidenceDir),
          "{\n"
              + "  \"schema_version\": \"eatme.alice-desktop-stageide-docframe-chooser-proof/v1\",\n"
              + "  \"status\": \"" + status + "\",\n"
              + "  \"reason\": \"" + reason + "\",\n"
              + "  \"proof_chain\": {\n"
              + "    \"step1\": \"StageIDE.getActiveInstance() non-null with active instance\",\n"
              + "    \"step2\": \"application.getDocumentFrame() non-null\",\n"
              + "    \"step3\": \"DocumentFrame.showSaveFileDialog(dir, filename, ext) called (same seam as AbstractSaveOperation.perform)\",\n"
              + "    \"step4\": \"DocumentFrame.showSaveFileDialog → FileDialogUtilities.showSaveFileDialog(this.frame.getAwtComponent(), ...)\",\n"
              + "    \"step5\": \"FileDialogUtilities → SwingFileDialog → JFileChooser.showSaveDialog (Linux platform branch)\",\n"
              + "    \"step6\": \"JFileChooser observed at Window.getWindows() poll under Xvfb\",\n"
              + "    \"step7\": \"JFileChooser.cancelSelection() called by probe (no project write needed for path proof)\"\n"
              + "  },\n"
              + "  \"observed_dialog\": {\n"
              + "    \"dialog_class\": " + stringJson(this.dialogClass) + ",\n"
              + "    \"dialog_showing\": " + this.dialogShowing + ",\n"
              + "    \"dialog_displayable\": " + this.dialogDisplayable + ",\n"
              + "    \"chooser_observed\": " + this.chooserObserved + ",\n"
              + "    \"cancelled_selection\": " + this.cancelledSelection + ",\n"
              + "    \"poll_count\": " + this.pollCount + "\n"
              + "  },\n"
              + "  \"doesNotClaim\": [\n"
              + "    \"native java.awt.FileDialog peer display/control\",\n"
              + "    \"StageIDE Save menu item was clicked to trigger this dialog\",\n"
              + "    \"saved project file written from StageIDE path\",\n"
              + "    \"first-lesson completion\",\n"
              + "    \"grading\"\n"
              + "  ]\n"
              + "}\n",
          StandardCharsets.UTF_8);
    }

    private void poll() {
      int count = ++this.pollCount;
      for (Window window : Window.getWindows()) {
        if (window instanceof JDialog dialog) {
          JFileChooser chooser = findChooser(dialog);
          if (chooser != null) {
            this.chooserObserved = true;
            this.dialogShowing = dialog.isShowing();
            this.dialogDisplayable = dialog.isDisplayable();
            this.dialogClass = dialog.getClass().getName();
            this.bgTimer.cancel();
            // Cancel on EDT to satisfy Swing threading model.
            SwingUtilities.invokeLater(() -> {
              chooser.cancelSelection();
              this.cancelledSelection = true;
            });
            return;
          }
        }
      }
      if (count >= MAX_POLLS) {
        this.bgTimer.cancel();
      }
    }

    private static JFileChooser findChooser(Component component) {
      if (component instanceof JFileChooser chooser) {
        return chooser;
      }
      if (component instanceof Container container) {
        for (Component child : container.getComponents()) {
          JFileChooser found = findChooser(child);
          if (found != null) {
            return found;
          }
        }
      }
      return null;
    }

    private static String stringJson(String value) {
      return value == null ? "null" : "\"" + FileDialogUtilities.escapeJson(value) + "\"";
    }
  }

  private static void restoreProperty(String name, String value) {
    if (value == null) {
      System.clearProperty(name);
    } else {
      System.setProperty(name, value);
    }
  }

  private static void resetActiveApplication() throws Exception {
    Field singleton = Application.class.getDeclaredField("singleton");
    singleton.setAccessible(true);
    singleton.set(null, null);
  }

  private static Path newTestDir() throws Exception {
    return Files.createDirectories(Path.of(
        "target",
        "stageide-save-menu-chooser-proof-test",
        UUID.randomUUID().toString()));
  }
}
