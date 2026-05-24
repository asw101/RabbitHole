package org.alice.stageide.modelresource;

import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.assertTrue;

public class TreeUtilitiesGroupThemeNodeListTest {
  @Test
  public void source_usesHouseholdSentinelForGroupAndThemeTrees() throws Exception {
    String source = Files.readString(Path.of("src/main/java/org/alice/stageide/modelresource/TreeUtilitiesLogic.java"));
    assertTrue(source.contains("return createTagNodeList(mapGroup, false, \"household\");"));
    assertTrue(source.contains("return createTagNodeList(mapTheme, true, \"household\");"));
  }
}
