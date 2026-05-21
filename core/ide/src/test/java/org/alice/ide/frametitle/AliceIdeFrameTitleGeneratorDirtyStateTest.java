package org.alice.ide.frametitle;

import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertTrue;

public class AliceIdeFrameTitleGeneratorDirtyStateTest {
  @Test
  public void generateTitle_appendsAsteriskForDirtyDocument() {
    AliceIdeFrameTitleGenerator generator = new AliceIdeFrameTitleGenerator();
    String title = generator.generateTitle(AliceIdeFrameTitleGeneratorTestSupport.loader(new File("DirtyWorld.a3p"), false), false);
    assertTrue(title.endsWith("*"));
  }
}
