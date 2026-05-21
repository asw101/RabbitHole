package org.alice.stageide.modelresource;

import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.assertTrue;

public class TreeUtilitiesCreateIconFactoriesTest {
  @Test
  public void source_capsCollectedImageFactoriesAtFive() throws Exception {
    String source = Files.readString(Path.of("src/main/java/org/alice/stageide/modelresource/TreeUtilities.java"));
    assertTrue(source.contains("iconFactory instanceof AbstractSingleSourceImageIconFactory imageIconFactory"));
    assertTrue(source.contains("iconFactories.add(imageIconFactory);"));
    assertTrue(source.contains("if (iconFactories.size() == 5)"));
  }
}
