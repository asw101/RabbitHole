package org.alice.ide.croquet.models.projecturi;

import edu.cmu.cs.dennisc.crash.CrashDetector;
import org.alice.ide.croquet.models.menubar.FileMenuModel;
import org.alice.stageide.StageIDE;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.lgna.croquet.Application;
import org.lgna.croquet.StandardMenuItemPrepModel;
import org.lgna.croquet.views.Menu;

import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;
import java.awt.Component;
import java.awt.GraphicsEnvironment;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeFalse;

/**
 * Proves that the rendered File JMenu — built from the real FileMenuModel — contains a JMenuItem
 * whose Action is SaveProjectOperation's swing action, and that doClick() on that item dispatches
 * into the save action through the OperationSwingModel path.
 *
 * <p>This closes the structural gap between:
 * <ul>
 *   <li>AliceMenuBarContractTest (FileMenuModel is in the menu bar), and
 *   <li>StageIdeSaveMenuItemDispatchProofTest (Save menu item doClick dispatches when constructed
 *       directly via getMenuItemPrepModel().createMenuItemAndAddTo(fakeContainer)).
 * </ul>
 *
 * <p>Still unproven after this test: the user physically navigating the real rendered JMenuBar
 * (clicking "File" then "Save" in the on-screen menu), and the full save-to-disk completion with
 * FileDialog interaction.
 */
public class FileMenuSaveNavigationProofTest {
  private String previousEvidenceDir;
  private String previousProofOnly;

  @Before
  public void captureProperties() {
    previousEvidenceDir = System.getProperty(SaveOperationCompletionEvidence.EVIDENCE_DIR_PROPERTY);
    previousProofOnly = System.getProperty(SaveOperationCompletionEvidence.PROOF_ONLY_PROPERTY);
  }

  @After
  public void restorePropertiesAndActiveApplication() throws Exception {
    restoreProperty(SaveOperationCompletionEvidence.EVIDENCE_DIR_PROPERTY, previousEvidenceDir);
    restoreProperty(SaveOperationCompletionEvidence.PROOF_ONLY_PROPERTY, previousProofOnly);
    resetActiveApplication();
  }

  @Test
  public void renderedFileMenuContainsSaveMenuItemThatDispatchesIntoSaveAction() throws Exception {
    assumeFalse("requires Xvfb or another headful AWT display", GraphicsEnvironment.isHeadless());

    Path evidenceDir = newTestDir();
    System.setProperty(SaveOperationCompletionEvidence.EVIDENCE_DIR_PROPERTY, evidenceDir.toString());
    System.setProperty(SaveOperationCompletionEvidence.PROOF_ONLY_PROPERTY, "true");

    resetActiveApplication();
    SwingUtilities.invokeAndWait(() -> {
      StageIDE ide = null;
      try {
        ide = new StageIDE(new CrashDetector(FileMenuSaveNavigationProofTest.class));
        ide.initialize(new String[0]);

        FileMenuModel fileMenuModel = findFileMenuModel(ide);
        assertNotNull("FileMenuModel not found in AliceMenuBar children", fileMenuModel);

        Menu croquetMenu = fileMenuModel.createMenu();
        JMenu jMenu = croquetMenu.getAwtComponent();

        Action saveAction = SaveProjectOperation.getInstance().getImp().getSwingModel().getAction();
        JMenuItem saveMenuItem = findMenuItemByAction(jMenu, saveAction);
        assertNotNull(
            "No JMenuItem connected to SaveProjectOperation found in rendered File JMenu",
            saveMenuItem);

        saveMenuItem.doClick();
      } finally {
        if (ide != null
            && ide.getDocumentFrame() != null
            && ide.getDocumentFrame().getFrame() != null) {
          ide.getDocumentFrame().getFrame().release();
        }
      }
    });

    String json = Files.readString(
        evidenceDir.resolve(SaveOperationCompletionEvidence.SAVE_ACTION_INVOCATION_PROOF_ARTIFACT));
    assertTrue(json, json.contains("\"status\": \"menu_item_dispatched\""));
    assertTrue(json, json.contains("\"menu_item_dispatch\": true"));
    assertTrue(json, json.contains("\"trigger_class\": \"org.lgna.croquet.triggers.ActionEventTrigger\""));
    assertTrue(json, json.contains("\"view_controller_class\": \"org.lgna.croquet.views.MenuItem\""));
    assertTrue(json, json.contains("\"awt_source_class\": \"javax.swing.JMenuItem\""));
    assertTrue(json, json.contains("Save dialog displayed"));
    assertFalse(Files.exists(evidenceDir.resolve(SaveOperationCompletionEvidence.ARTIFACT)));
  }

  private static FileMenuModel findFileMenuModel(StageIDE ide) {
    for (StandardMenuItemPrepModel child :
        ide.getDocumentFrame().getCodePerspective().getMenuBarComposite().getChildren()) {
      if (child instanceof FileMenuModel) {
        return (FileMenuModel) child;
      }
    }
    return null;
  }

  private static JMenuItem findMenuItemByAction(JMenu jMenu, Action targetAction) {
    for (int i = 0; i < jMenu.getMenuComponentCount(); i++) {
      Component component = jMenu.getMenuComponent(i);
      if (component instanceof JMenuItem menuItem && menuItem.getAction() == targetAction) {
        return menuItem;
      }
    }
    return null;
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
        "file-menu-save-navigation-proof-test",
        UUID.randomUUID().toString()));
  }
}
