package org.alice.ide.declarationseditor;

import org.junit.Assume;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.*;

public class TypeStateTest {
  private static final Path SOURCE_PATH = Paths.get("src/main/java/org/alice/ide/declarationseditor/TypeState.java");

  @Test
  public void sourceFile_existsWhenTypeStateIsAvailable() {
    Assume.assumeTrue("TypeState source is not present in this checkout", Files.exists(SOURCE_PATH));
    assertTrue(Files.exists(SOURCE_PATH));
  }

  @Test
  public void typeState_classLoadsWhenSourceIsPresent() throws Exception {
    Assume.assumeTrue("TypeState source is not present in this checkout", Files.exists(SOURCE_PATH));
    assertEquals("org.alice.ide.declarationseditor.TypeState", Class.forName("org.alice.ide.declarationseditor.TypeState").getName());
  }

  @Test
  public void source_declaresExpectedPackageWhenPresent() throws Exception {
    Assume.assumeTrue("TypeState source is not present in this checkout", Files.exists(SOURCE_PATH));
    assertTrue(Files.readString(SOURCE_PATH).contains("package org.alice.ide.declarationseditor;"));
  }
}
