package org.alice.ide.ast.type.croquet;

import org.alice.ide.testing.ProjectContextTestCase;
import org.alice.stageide.StageIDE;
import org.junit.Test;
import org.lgna.croquet.FileDialogValueCreator;
import org.lgna.croquet.Triggerable;
import org.lgna.croquet.history.UserActivity;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ImportTypeIteratingOperationProjectContextTest extends ProjectContextTestCase {
  @Test
  public void firstStepUsesStageIdeTypesDirectory() throws Exception {
    ImportTypeIteratingOperation operation = new ImportTypeIteratingOperation(fixture.sceneType);
    Method method = ImportTypeIteratingOperation.class.getDeclaredMethod("getNext", java.util.List.class);
    method.setAccessible(true);

    Triggerable triggerable = (Triggerable) method.invoke(operation, Collections.<UserActivity>emptyList());
    assertTrue(triggerable instanceof FileDialogValueCreator);

    Field directoryField = FileDialogValueCreator.class.getDeclaredField("directory");
    directoryField.setAccessible(true);
    assertEquals(StageIDE.getActiveInstance().getTypesDirectory(), (File) directoryField.get(triggerable));
  }
}
