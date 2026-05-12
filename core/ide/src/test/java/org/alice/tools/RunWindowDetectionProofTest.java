package org.alice.tools;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.Window;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeFalse;

/**
 * Proves that Alice's Run window can be detected via AWT window enumeration
 * under Xvfb. This is the RabbitHole side of Run window detection that
 * eatme issue #246 needs (RabbitHole issue #511).
 *
 * <p>The test creates a synthetic JFrame (simulating the Run window), makes
 * it visible, then polls {@link Window#getWindows()} to detect it by title.
 * On success the evidence artifact records the window title and identity
 * hash; on failure it records the reason detection failed.</p>
 */
public class RunWindowDetectionProofTest {

  private static final String RUN_WINDOW_TITLE = "Run Alice — Detection Proof";
  private static final String ARTIFACT_NAME = "run-window-detection.json";
  private static final String SCHEMA_VERSION = "eatme.alice-run-window-detection/v1";
  private static final String CONTRACT_SCOPE = "run-window-detection";
  private static final long DETECTION_TIMEOUT_MS = 10_000;
  private static final long POLL_INTERVAL_MS = 100;
  private static final String[] DOES_NOT_CLAIM = {
      "rendering-correctness",
      "grading",
      "save",
      "full-ui-automation",
      "active-rendering",
      "run-execution",
      "world-execution-correctness"
  };

  @Rule
  public TemporaryFolder temporaryFolder = new TemporaryFolder();

  @Test
  public void detectsRunWindowAfterItBecomesVisible() throws Exception {
    assumeFalse("Requires a display (Xvfb or physical); skipped on headless CI",
        GraphicsEnvironment.isHeadless());

    Path evidenceDir = temporaryFolder.newFolder("run-window-detection").toPath();
    Path artifact = EatmeRunWindowEvidence.artifactPath(evidenceDir, ARTIFACT_NAME);

    JFrame frame = new JFrame(RUN_WINDOW_TITLE);
    JPanel content = new JPanel();
    content.setPreferredSize(new Dimension(320, 240));
    frame.getContentPane().add(content, BorderLayout.CENTER);
    frame.pack();

    try {
      SwingUtilities.invokeAndWait(() -> frame.setVisible(true));

      DetectionResult result = pollForWindow(RUN_WINDOW_TITLE);
      writeEvidence(evidenceDir, artifact, result);

      // Verify evidence artifact was written
      assertTrue("Evidence artifact must exist", Files.isRegularFile(artifact));
      assertTrue("Evidence artifact must not be empty", Files.size(artifact) > 0);
      String json = Files.readString(artifact);

      // The window we created must be detected
      assertTrue(json, json.contains("\"status\": \"detected\""));
      assertTrue(json, json.contains("\"schema_version\": \"" + SCHEMA_VERSION + "\""));
      assertTrue(json, json.contains("\"window_title\": \"" + EatmeRunWindowEvidence.escapeJson(RUN_WINDOW_TITLE) + "\""));
      assertTrue(json, json.contains("\"window_id\": \""));
      assertTrue(json, json.contains("\"contract_scope\": \"" + CONTRACT_SCOPE + "\""));

      // Verify does-not-claim assertions
      for (String claim : DOES_NOT_CLAIM) {
        assertTrue("Evidence must declare does_not_claim: " + claim, json.contains("\"" + claim + "\""));
      }
      assertTrue(json, json.contains("\"rendering_correctness_claimed\": false"));
      assertTrue(json, json.contains("\"grading_claimed\": false"));
      assertTrue(json, json.contains("\"save_claimed\": false"));
      assertTrue(json, json.contains("\"full_ui_automation_claimed\": false"));
    } finally {
      frame.dispose();
    }
  }

  private static DetectionResult pollForWindow(String expectedTitle) {
    long deadline = System.currentTimeMillis() + DETECTION_TIMEOUT_MS;
    while (System.currentTimeMillis() < deadline) {
      for (Window window : Window.getWindows()) {
        if (window instanceof JFrame jframe
            && expectedTitle.equals(jframe.getTitle())
            && jframe.isShowing()) {
          String windowId = "0x" + Integer.toHexString(System.identityHashCode(jframe));
          return new DetectionResult(true, jframe.getTitle(), windowId, null);
        }
      }
      try {
        Thread.sleep(POLL_INTERVAL_MS);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        return new DetectionResult(false, null, null,
            "Detection interrupted before window appeared");
      }
    }
    return new DetectionResult(false, null, null,
        "Window with title '" + expectedTitle + "' not detected within " + DETECTION_TIMEOUT_MS + "ms");
  }

  private static void writeEvidence(Path evidenceDir, Path artifact, DetectionResult result) throws IOException {
    String json;
    if (result.detected) {
      json = successJson(result.windowTitle, result.windowId);
    } else {
      json = failureJson(result.failureReason);
    }
    writeArtifactAtomically(evidenceDir, artifact, json);
  }

  private static String successJson(String windowTitle, String windowId) {
    return "{\n"
        + "  \"schema_version\": \"" + SCHEMA_VERSION + "\",\n"
        + "  \"status\": \"detected\",\n"
        + "  \"contract_scope\": \"" + CONTRACT_SCOPE + "\",\n"
        + "  \"artifact\": \"" + ARTIFACT_NAME + "\",\n"
        + "  \"window_title\": \"" + EatmeRunWindowEvidence.escapeJson(windowTitle) + "\",\n"
        + "  \"window_id\": \"" + EatmeRunWindowEvidence.escapeJson(windowId) + "\",\n"
        + claimsJson()
        + doesNotClaimJson()
        + "}\n";
  }

  private static String failureJson(String failureReason) {
    return "{\n"
        + "  \"schema_version\": \"" + SCHEMA_VERSION + "\",\n"
        + "  \"status\": \"not_detected\",\n"
        + "  \"contract_scope\": \"" + CONTRACT_SCOPE + "\",\n"
        + "  \"artifact\": \"" + ARTIFACT_NAME + "\",\n"
        + "  \"failure_reason\": \"" + EatmeRunWindowEvidence.escapeJson(failureReason) + "\",\n"
        + claimsJson()
        + doesNotClaimJson()
        + "}\n";
  }

  private static String claimsJson() {
    return "  \"rendering_correctness_claimed\": false,\n"
        + "  \"grading_claimed\": false,\n"
        + "  \"save_claimed\": false,\n"
        + "  \"full_ui_automation_claimed\": false,\n"
        + "  \"active_rendering_claimed\": false,\n"
        + "  \"run_execution_claimed\": false,\n"
        + "  \"world_execution_claimed\": false,\n";
  }

  private static String doesNotClaimJson() {
    StringBuilder json = new StringBuilder("  \"does_not_claim\": [\n");
    for (int i = 0; i < DOES_NOT_CLAIM.length; i++) {
      json.append("    \"").append(DOES_NOT_CLAIM[i]).append("\"");
      if (i + 1 < DOES_NOT_CLAIM.length) {
        json.append(",");
      }
      json.append("\n");
    }
    json.append("  ]\n");
    return json.toString();
  }

  private static void writeArtifactAtomically(Path evidenceDir, Path artifact, String content) throws IOException {
    if (Files.isSymbolicLink(artifact)) {
      throw new IOException("Evidence artifact refuses to overwrite symlink: " + artifact);
    }
    Path temp = Files.createTempFile(evidenceDir, ARTIFACT_NAME, ".tmp");
    try {
      Files.writeString(temp, content, StandardCharsets.UTF_8);
      Files.move(temp, artifact, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
    } finally {
      Files.deleteIfExists(temp);
    }
  }

  private record DetectionResult(boolean detected, String windowTitle, String windowId, String failureReason) {
  }
}
