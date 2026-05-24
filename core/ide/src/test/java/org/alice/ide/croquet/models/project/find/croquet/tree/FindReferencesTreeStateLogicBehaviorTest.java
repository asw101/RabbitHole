package org.alice.ide.croquet.models.project.find.croquet.tree;

import org.alice.ide.croquet.models.project.find.croquet.tree.nodes.SearchTreeNode;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class FindReferencesTreeStateLogicBehaviorTest {
  private static void assertConstructorThrowsAssertionError(Class<?> type) throws Exception {
    Constructor<?> constructor = type.getDeclaredConstructor();
    constructor.setAccessible(true);
    try {
      constructor.newInstance();
      fail("Expected constructor to reject instantiation");
    } catch (InvocationTargetException ite) {
      assertTrue(ite.getCause() instanceof AssertionError);
    }
  }

  @Test
  public void utilityConstructorRejectsInstantiation() throws Exception {
    assertConstructorThrowsAssertionError(FindReferencesTreeStateLogic.class);
  }

  @Test
  public void groupReferencesReturnsEmptyListForNullSearchResults() {
    assertTrue(FindReferencesTreeStateLogic.groupReferences(null).isEmpty());
  }

  @Test
  public void treeNavigationStopsAtTheTopAndBottomEdges() {
    SearchTreeNode root = new SearchTreeNode(null);
    SearchTreeNode declaration = new SearchTreeNode(root);
    SearchTreeNode reference = new SearchTreeNode(declaration);
    declaration.addChild(reference);
    root.addChild(declaration);

    assertSame(declaration, FindReferencesTreeStateLogic.moveSelectedUpOne(root, declaration));
    assertSame(reference, FindReferencesTreeStateLogic.moveSelectedDownOne(root, reference));
    assertSame(declaration, FindReferencesTreeStateLogic.selectAtCoordinates(root, 0, -1));
  }
}
