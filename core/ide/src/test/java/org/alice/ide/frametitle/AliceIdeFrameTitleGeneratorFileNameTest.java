package org.alice.ide.frametitle;

import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertTrue;

public class AliceIdeFrameTitleGeneratorFileNameTest {
  @Test
  public void generateTitle_includesMainProjectFilePath() {
    File file = new File("WorldFile.a3p");
    AliceIdeFrameTitleGenerator generator = new AliceIdeFrameTitleGenerator();
    String title = generator.generateTitle(AliceIdeFrameTitleGeneratorTestSupport.loader(file, false), true);
    assertTrue(title.contains(file.toString()));
  }
}
