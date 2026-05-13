package org.alice.ide.croquet.models.projecturi;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.lgna.project.io.IoUtilities;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ExportProjectOperationTest {
  @Rule
  public TemporaryFolder temporaryFolder = new TemporaryFolder();

  @Test
  public void exportAlwaysPromptsForDestination() throws Exception {
    ExportProjectOperation operation = new ExportProjectOperation();
    File writableProject = temporaryFolder.newFile("world.a3p");
    File missingProject = new File(temporaryFolder.getRoot(), "missing.a3p");

    assertTrue(operation.isPromptNecessary(null));
    assertTrue(operation.isPromptNecessary(writableProject));
    assertTrue(operation.isPromptNecessary(missingProject));
  }

  @Test
  public void exportUsesExportExtensionAndClobbersToolbarText() {
    ExportProjectOperation operation = new ExportProjectOperation();

    assertEquals(IoUtilities.EXPORT_EXTENSION, operation.getExtension());
    assertTrue(operation.isToolBarTextClobbered());
  }
}
