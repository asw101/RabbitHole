package org.alice.ide.ast.export;

import org.junit.Test;
import org.lgna.project.ast.NamedUserType;

import java.io.File;
import java.lang.reflect.Method;
import java.util.UUID;

import static org.junit.Assert.*;

public class ExportTypeToFileDialogOperationCoverageTest {
  private static final class TestableExportTypeToFileDialogOperation extends ExportTypeToFileDialogOperation {
    private TestableExportTypeToFileDialogOperation(NamedUserType type) {
      super(type);
    }

    private String invokePrivate(String name) throws Exception {
      Method method = ExportTypeToFileDialogOperation.class.getDeclaredMethod(name);
      method.setAccessible(true);
      return (String) method.invoke(this);
    }

    private void writeTo(File file) throws Exception {
      this.handleFile(file);
    }
  }

  @Test
  public void getInitialFilename_usesTypeNameAndExtension() throws Exception {
    NamedUserType type = new NamedUserType();
    type.name.setValue("Hero");
    TestableExportTypeToFileDialogOperation operation = new TestableExportTypeToFileDialogOperation(type);
    assertEquals("Hero." + operation.invokePrivate("getExtension"), operation.invokePrivate("getInitialFilename"));
  }

  @Test
  public void getExtension_returnsNonEmptyString() throws Exception {
    NamedUserType type = new NamedUserType();
    type.name.setValue("Hero");
    TestableExportTypeToFileDialogOperation operation = new TestableExportTypeToFileDialogOperation(type);
    assertFalse(operation.invokePrivate("getExtension").isEmpty());
  }

  @Test
  public void constructor_setsFolderIcon() {
    NamedUserType type = new NamedUserType();
    type.name.setValue("Hero");
    ExportTypeToFileDialogOperation operation = new ExportTypeToFileDialogOperation(type);
    assertNotNull(operation.getButtonIcon());
  }

  @Test
  public void handleFile_writesTypeSummaryFile() throws Exception {
    NamedUserType type = new NamedUserType();
    type.name.setValue("ExportedType");
    TestableExportTypeToFileDialogOperation operation = new TestableExportTypeToFileDialogOperation(type);
    File file = new File("target/export-type-" + UUID.randomUUID() + ".a3c");
    file.getParentFile().mkdirs();
    if (file.exists()) {
      assertTrue(file.delete());
    }
    operation.writeTo(file);
    assertTrue(file.exists());
    assertTrue(file.length() > 0L);
    assertTrue(file.delete());
  }
}
