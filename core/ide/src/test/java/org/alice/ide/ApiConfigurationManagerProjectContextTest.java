package org.alice.ide;

import edu.cmu.cs.dennisc.tree.Node;
import org.alice.ide.testing.ProjectContextTestCase;
import org.junit.Test;
import org.lgna.project.ast.NamedUserType;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ApiConfigurationManagerProjectContextTest extends ProjectContextTestCase {
  @Test
  public void namedUserTypesTreeIncludesLoadedProjectTypes() {
    Node<NamedUserType> tree = IDE.getActiveInstance().getApiConfigurationManager()
        .getNamedUserTypesAsTreeFilteredForSelection();

    assertNotNull(tree);
    List<String> childNames = tree.getChildren().stream()
        .map(child -> child.getValue().getName())
        .collect(Collectors.toList());
    assertTrue(childNames.toString(), childNames.contains(fixture.sceneType.getName()));
    assertTrue(childNames.toString(), childNames.contains(fixture.actorType.getName()));
    assertFalse(childNames.toString(), childNames.contains(fixture.programType.getName()));
  }
}
