package org.alice.stageide.modelresource;

import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.assertTrue;

public class TreeUtilitiesCopyTest {
  @Test
  public void source_showsRecursiveCopyAndSorting() throws Exception {
    String source = Files.readString(Path.of("src/main/java/org/alice/stageide/modelresource/TreeUtilitiesLogic.java"));
    assertTrue(source.contains("List<ResourceNode> dstChildNodes = copy(srcNode.getNodeChildren());"));
    assertTrue(source.contains("new ResourceNode(srcNode.getResourceKey(), dstChildNodes)"));
    assertTrue(source.contains("Collections.sort(dstNodes);"));
  }
}
