package org.alice.ide.croquet.models.projecturi;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.lgna.project.io.IoUtilities;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SaveAsProjectOperationTest {
  @Rule
  public TemporaryFolder temporaryFolder = new TemporaryFolder();

  @Test
  public void saveAsAlwaysPromptsForDestination() throws Exception {
    SaveAsProjectOperation operation = SaveAsProjectOperation.getInstance();
    File writableProject = temporaryFolder.newFile("world.a3p");
    File missingProject = new File(temporaryFolder.getRoot(), "missing.a3p");

    assertTrue(operation.isPromptNecessary(null));
    assertTrue(operation.isPromptNecessary(writableProject));
    assertTrue(operation.isPromptNecessary(missingProject));
  }

  @Test
  public void saveAsUsesProjectExtension() {
    SaveAsProjectOperation operation = SaveAsProjectOperation.getInstance();

    assertEquals(IoUtilities.PROJECT_EXTENSION, operation.getExtension());
  }
}
