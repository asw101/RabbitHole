package org.alice.ide.frametitle;

import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;

public class AliceIdeFrameTitleGeneratorLogicTest {
  @Test
  public void generateTitleIncludesApplicationVersionAndFile() {
    assertEquals("Alice 3.0 World.a3p", AliceIdeFrameTitleGeneratorLogic.generateTitle("Alice", "3.0", new File("World.a3p"), false, true));
  }

  @Test
  public void generateTitleMarksBackupAndDirtyProjects() {
    assertEquals("Alice 3.0 World.a3p*", AliceIdeFrameTitleGeneratorLogic.generateTitle("Alice", "3.0", new File("World.a3p"), true, true));
    assertEquals("Alice 3.0 World.a3p*", AliceIdeFrameTitleGeneratorLogic.generateTitle("Alice", "3.0", new File("World.a3p"), false, false));
  }
}
