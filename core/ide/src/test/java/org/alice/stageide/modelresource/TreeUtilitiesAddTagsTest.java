package org.alice.stageide.modelresource;

import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.assertTrue;

public class TreeUtilitiesAddTagsTest {
  @Test
  public void source_showsAsteriskRemovalAndBucketAppend() throws Exception {
    String source = Files.readString(Path.of("src/main/java/org/alice/stageide/modelresource/TreeUtilities.java"));
    assertTrue(source.contains("if (tag.startsWith(\"*\"))"));
    assertTrue(source.contains("tag = tag.substring(1);"));
    assertTrue(source.contains("map.getInitializingIfAbsentToLinkedList(tag)"));
  }
}
