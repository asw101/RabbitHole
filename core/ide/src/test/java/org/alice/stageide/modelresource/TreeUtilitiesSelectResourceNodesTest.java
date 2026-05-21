package org.alice.stageide.modelresource;

import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.assertTrue;

public class TreeUtilitiesSelectResourceNodesTest {
  @Test
  public void source_showsQualifierCheckBeforeRecursiveTraversal() throws Exception {
    String source = Files.readString(Path.of("src/main/java/org/alice/stageide/modelresource/TreeUtilities.java"));
    assertTrue(source.contains("if (qualifier.test(node))"));
    assertTrue(source.contains("selectedNodes.add(node);"));
    assertTrue(source.contains("selectResourceNodes(child, selectedNodes, qualifier);"));
  }
}
