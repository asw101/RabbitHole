package org.alice.stageide.modelresource;

import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.assertTrue;

public class TreeUtilitiesFindByKeyTest {
  @Test
  public void source_showsRecursiveFindByKeyAlgorithm() throws Exception {
    String source = Files.readString(Path.of("src/main/java/org/alice/stageide/modelresource/TreeUtilitiesLogic.java"));
    assertTrue(source.contains("resourceKey.equals(root.getResourceKey())"));
    assertTrue(source.contains("ResourceNode checkChild = findByKey(child, resourceKey);"));
    assertTrue(source.contains("return null;"));
  }
}
