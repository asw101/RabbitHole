package org.alice.ide.frametitle;

import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertTrue;

public class AliceIdeFrameTitleGeneratorBackupStateTest {
  @Test
  public void generateTitle_appendsAsteriskForBackupLoader() {
    AliceIdeFrameTitleGenerator generator = new AliceIdeFrameTitleGenerator();
    String title = generator.generateTitle(AliceIdeFrameTitleGeneratorTestSupport.loader(new File("BackupWorld.a3p"), true), true);
    assertTrue(title.endsWith("*"));
  }
}
