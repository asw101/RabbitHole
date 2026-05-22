package org.alice.ide.ast.export;

import org.alice.ide.testing.ProjectContextTestCase;
import org.alice.stageide.StageIDE;
import org.junit.Test;
import org.lgna.project.io.IoUtilities;

import java.io.File;
import java.lang.reflect.Method;

import static org.junit.Assert.assertEquals;

public class ExportTypeToFileDialogOperationProjectContextTest extends ProjectContextTestCase {
  @Test
  public void defaultsUseLoadedTypeAndStageDirectory() throws Exception {
    ExportTypeToFileDialogOperation operation = new ExportTypeToFileDialogOperation(fixture.sceneType);
    Method defaultDirectory = ExportTypeToFileDialogOperation.class.getDeclaredMethod("getDefaultDirectory");
    Method initialFilename = ExportTypeToFileDialogOperation.class.getDeclaredMethod("getInitialFilename");
    defaultDirectory.setAccessible(true);
    initialFilename.setAccessible(true);

    assertEquals(StageIDE.getActiveInstance().getTypesDirectory(), (File) defaultDirectory.invoke(operation));
    assertEquals(fixture.sceneType.getName() + "." + IoUtilities.TYPE_EXTENSION, initialFilename.invoke(operation));
  }
}
