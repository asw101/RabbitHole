package org.alice.ide.croquet.models.print;

import org.junit.Test;
import org.lgna.croquet.AbstractElement;

import java.util.UUID;

import static org.junit.Assert.*;

public class PrintOperationsBehaviorTest {
  @Test
  public void printOperations_createFreshInstancesWithStableMigrationIds() {
    PrintAllOperation firstAll = new PrintAllOperation();
    PrintAllOperation secondAll = new PrintAllOperation();

    assertNotSame(firstAll, secondAll);
    assertEquals(UUID.fromString("6a205070-e6e0-4827-a059-ffd15b7350a3"), firstAll.getMigrationId());
    assertEquals(firstAll.getMigrationId(), secondAll.getMigrationId());
    assertEquals(UUID.fromString("097b41bf-d1ea-4991-a0d6-0fae51be35ef"), new PrintCurrentCodeOperation().getMigrationId());
    assertEquals(UUID.fromString("b38997ea-e970-416e-86db-58623d1c3352"), new PrintSceneEditorOperation().getMigrationId());
  }

  @Test
  public void printOperations_localizedLabelsAndShortcuts_areAvailable() {
    assertEquals("Print All...", AbstractElement.findLocalizedText(PrintAllOperation.class, null));
    assertEquals("VK_A", AbstractElement.findLocalizedText(PrintAllOperation.class, "mnemonic"));
    assertEquals("Print Current Code...", AbstractElement.findLocalizedText(PrintCurrentCodeOperation.class, null));
    assertEquals("VK_P", AbstractElement.findLocalizedText(PrintCurrentCodeOperation.class, "accelerator"));
    assertEquals("VK_C", AbstractElement.findLocalizedText(PrintCurrentCodeOperation.class, "mnemonic"));
    assertEquals("Print Scene Editor...", AbstractElement.findLocalizedText(PrintSceneEditorOperation.class, null));
    assertEquals("VK_S", AbstractElement.findLocalizedText(PrintSceneEditorOperation.class, "mnemonic"));
  }
}
