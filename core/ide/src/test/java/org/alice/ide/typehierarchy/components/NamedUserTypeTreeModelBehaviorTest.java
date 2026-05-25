package org.alice.ide.typehierarchy.components;

import edu.cmu.cs.dennisc.tree.DefaultNode;
import org.junit.Test;
import org.lgna.project.ProgramTypeUtilities;
import org.lgna.project.Project;
import org.lgna.project.ast.AbstractType;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.NamedUserType;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;

import static org.junit.Assert.*;

public class NamedUserTypeTreeModelBehaviorTest {
  @Test
  public void treeModelTraversesInheritanceTreeBuiltFromNamedUserTypes() {
    NamedUserType base = createType("Base", JavaType.OBJECT_TYPE);
    NamedUserType child = createType("Child", base);
    NamedUserType grandchild = createType("Grandchild", child);
    NamedUserType sibling = createType("Sibling", JavaType.OBJECT_TYPE);
    Project project = new Project(
        base,
        new LinkedHashSet<>(Arrays.asList(base, child, grandchild, sibling)),
        Collections.emptySet(),
        Project.SceneCameraType.WindowCamera);
    DefaultNode<NamedUserType> root = ProgramTypeUtilities.getNamedUserTypesAsTree(project);
    DefaultNode<NamedUserType> baseNode = root.get(base);
    DefaultNode<NamedUserType> childNode = root.get(child);
    DefaultNode<NamedUserType> grandchildNode = root.get(grandchild);
    DefaultNode<NamedUserType> siblingNode = root.get(sibling);
    NamedUserTypeTreeModel model = new NamedUserTypeTreeModel();

    assertNotNull(baseNode);
    assertNotNull(childNode);
    assertNotNull(grandchildNode);
    assertNotNull(siblingNode);
    assertEquals(2, model.getChildCount(root));
    assertSame(childNode, model.getChild(baseNode, 0));
    assertSame(grandchildNode, model.getChild(childNode, 0));
    assertEquals(0, model.getIndexOfChild(baseNode, childNode));
    assertFalse(model.isLeaf(baseNode));
    assertFalse(model.isLeaf(childNode));
    assertTrue(model.isLeaf(grandchildNode));
    assertTrue(model.isLeaf(siblingNode));
  }

  @Test
  public void refreshWithoutActiveIdeIsANoOp() {
    NamedUserTypeTreeModel model = new NamedUserTypeTreeModel();

    assertNull(model.getRoot());
    model.refresh();
    assertNull(model.getRoot());
  }

  private static NamedUserType createType(String name, AbstractType<?, ?, ?> superType) {
    NamedUserType type = new NamedUserType();
    type.name.setValue(name);
    type.superType.setValue(superType);
    return type;
  }
}
