package org.alice.ide.croquet.models.projecturi;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.lgna.project.io.IoUtilities;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SaveProjectOperationTest {
  @Rule
  public TemporaryFolder temporaryFolder = new TemporaryFolder();

  @Test
  public void savePromptsWhenThereIsNoCurrentFile() {
    SaveProjectOperation operation = SaveProjectOperation.getInstance();

    assertTrue(operation.isPromptNecessary(null));
  }

  @Test
  public void saveUsesCurrentFileWhenItCanBeWritten() throws Exception {
    SaveProjectOperation operation = SaveProjectOperation.getInstance();
    File currentProject = temporaryFolder.newFile("world.a3p");

    assertFalse(operation.isPromptNecessary(currentProject));
  }

  @Test
  public void savePromptsWhenCurrentFileCannotBeWritten() {
    SaveProjectOperation operation = SaveProjectOperation.getInstance();
    File missingProject = new File(temporaryFolder.getRoot(), "missing.a3p");

    assertTrue(operation.isPromptNecessary(missingProject));
  }

  @Test
  public void saveUsesProjectExtensionAndClobbersToolbarText() {
    SaveProjectOperation operation = SaveProjectOperation.getInstance();

    assertEquals(IoUtilities.PROJECT_EXTENSION, operation.getExtension());
    assertTrue(operation.isToolBarTextClobbered());
  }
}
