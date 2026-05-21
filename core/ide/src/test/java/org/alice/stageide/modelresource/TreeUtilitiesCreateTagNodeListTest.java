package org.alice.stageide.modelresource;

import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.assertTrue;

public class TreeUtilitiesCreateTagNodeListTest {
  @Test
  public void source_movesNestedTagsBeneathParentNodes() throws Exception {
    String source = Files.readString(Path.of("src/main/java/org/alice/stageide/modelresource/TreeUtilities.java"));
    assertTrue(source.contains("int lastIndex = tag.lastIndexOf(TagKey.SEPARATOR);"));
    assertTrue(source.contains("String parentTag = tag.substring(0, lastIndex);"));
    assertTrue(source.contains("parentToBeNode.addNodeChild(0, resourceNode);"));
  }
}
