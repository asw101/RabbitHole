package org.alice.ide.frametitle;

import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;

public class AliceIdeFrameTitleGeneratorLogicEdgeTest {
  @Test
  public void generateTitleKeepsUntitledProjectsReadable() {
    assertEquals("Alice 3.0 ", AliceIdeFrameTitleGeneratorLogic.generateTitle("Alice", "3.0", null, false, true));
    assertEquals("Alice 3.0 *", AliceIdeFrameTitleGeneratorLogic.generateTitle("Alice", "3.0", null, true, true));
  }

  @Test
  public void generateTitleAppendsNullInputsLiterally() {
    assertEquals("null null World.a3p*",
        AliceIdeFrameTitleGeneratorLogic.generateTitle(null, null, new File("World.a3p"), false, false));
  }
}
