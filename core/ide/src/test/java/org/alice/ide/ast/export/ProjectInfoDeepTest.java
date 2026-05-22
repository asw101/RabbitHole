package org.alice.ide.ast.export;

import edu.cmu.cs.dennisc.tree.DefaultNode;
import edu.cmu.cs.dennisc.tree.Node;
import org.junit.Test;
import org.lgna.project.Project;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.NamedUserType;
import org.lgna.project.ast.NullLiteral;
import org.lgna.project.ast.UserField;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.ArrayDeque;
import java.util.Deque;

import static org.junit.Assert.*;

public class ProjectInfoDeepTest {

  private NamedUserType createType(String name) {
    NamedUserType type = new NamedUserType();
    type.name.setValue(name);
    return type;
  }

  private ProjectInfo createProjectInfo() {
    NamedUserType parent = createType("AlphaParent");
    parent.superType.setValue(JavaType.OBJECT_TYPE);
    NamedUserType child = createType("BetaChild");
    child.superType.setValue(parent);
    NamedUserType program = createType("Program");
    program.fields.add(new UserField("child", child, new NullLiteral()));
    return new ProjectInfo(new Project(program, Project.SceneCameraType.WindowCamera));
  }

  private DefaultNode<TypeInfo> findNode(Node<TypeInfo> root, String typeName) {
    Deque<Node<TypeInfo>> queue = new ArrayDeque<>();
    queue.add(root);
    while (!queue.isEmpty()) {
      Node<TypeInfo> node = queue.removeFirst();
      if (node.getValue() != null && typeName.equals(node.getValue().getDeclaration().getName())) {
        return (DefaultNode<TypeInfo>) node;
      }
      queue.addAll(node.getChildren());
    }
    return null;
  }

  @Test
  public void classIsPublic() {
    assertTrue(Modifier.isPublic(ProjectInfo.class.getModifiers()));
  }

  @Test
  public void classIsConcrete() {
    assertFalse(Modifier.isAbstract(ProjectInfo.class.getModifiers()));
  }

  @Test
  public void classIsNotFinal() {
    assertFalse(Modifier.isFinal(ProjectInfo.class.getModifiers()));
  }

  @Test
  public void simpleNameMatchesSource() {
    assertEquals("ProjectInfo", ProjectInfo.class.getSimpleName());
  }

  @Test
  public void packageNameMatchesSource() {
    assertEquals("org.alice.ide.ast.export", ProjectInfo.class.getPackage().getName());
  }

  @Test
  public void constructorExists() throws Exception {
    assertNotNull(ProjectInfo.class.getDeclaredConstructor(Project.class));
  }

  @Test
  public void constructorIsPublic() throws Exception {
    Constructor<?> constructor = ProjectInfo.class.getDeclaredConstructor(Project.class);
    assertTrue(Modifier.isPublic(constructor.getModifiers()));
  }

  @Test
  public void getTypeInfosIncludesProgramType() {
    ProjectInfo info = createProjectInfo();
    assertNotNull(findNode(info.getTypeInfosAsTree(), "Program"));
  }

  @Test
  public void getTypeInfosIncludesChildType() {
    ProjectInfo info = createProjectInfo();
    assertNotNull(findNode(info.getTypeInfosAsTree(), "BetaChild"));
  }

  @Test
  public void getTypeInfosIncludesParentType() {
    ProjectInfo info = createProjectInfo();
    assertNotNull(findNode(info.getTypeInfosAsTree(), "AlphaParent"));
  }

  @Test
  public void getTypeInfosCountMatchesReachableNamedTypes() {
    assertEquals(3, createProjectInfo().getTypeInfos().size());
  }

  @Test
  public void unknownTypeLookupReturnsNull() {
    NamedUserType unknown = createType("Unknown");
    assertNull(createProjectInfo().getInfoForType(unknown));
  }

  @Test
  public void rootTreeNodeExists() {
    assertNotNull(createProjectInfo().getTypeInfosAsTree());
  }

  @Test
  public void rootTreeNodeHasNullValue() {
    assertNull(createProjectInfo().getTypeInfosAsTree().getValue());
  }

  @Test
  public void rootTreeHasTwoChildren() {
    assertEquals(2, createProjectInfo().getTypeInfosAsTree().getChildren().size());
  }

  @Test
  public void childTypeIsNestedUnderParentNode() {
    Node<TypeInfo> root = createProjectInfo().getTypeInfosAsTree();
    DefaultNode<TypeInfo> parentNode = findNode(root, "AlphaParent");
    DefaultNode<TypeInfo> childNode = findNode(root, "BetaChild");
    assertTrue(parentNode.getChildren().contains(childNode));
  }

  @Test
  public void missingTypeLookupInTreeReturnsNull() {
    assertNull(findNode(createProjectInfo().getTypeInfosAsTree(), "MissingType"));
  }

  @Test
  public void rootChildrenAreAlphabeticallyOrdered() {
    java.util.List<? extends Node<TypeInfo>> children = createProjectInfo().getTypeInfosAsTree().getChildren();
    assertTrue(children.get(0).getValue().getDeclaration().getName().compareTo(children.get(1).getValue().getDeclaration().getName()) < 0);
  }

  @Test
  public void isInTheMidstOfChangeStartsFalse() {
    assertFalse(createProjectInfo().isInTheMidstOfChange());
  }

  @Test
  public void updateLeavesChangeFlagFalseAfterCompletion() {
    ProjectInfo info = createProjectInfo();
    info.update();
    assertFalse(info.isInTheMidstOfChange());
  }

  @Test
  public void updateCanRunMultipleTimes() {
    ProjectInfo info = createProjectInfo();
    info.update();
    info.update();
    info.update();
    assertFalse(info.isInTheMidstOfChange());
  }

  @Test
  public void typeInfosCollectionIsUnmodifiable() {
    try {
      createProjectInfo().getTypeInfos().clear();
      fail("Expected UnsupportedOperationException");
    } catch (UnsupportedOperationException expected) {
      assertNotNull(expected);
    }
  }

  @Test
  public void childTypeInfoResolvesParentTypeInfo() {
    ProjectInfo info = createProjectInfo();
    assertSame(findNode(info.getTypeInfosAsTree(), "AlphaParent").getValue(), findNode(info.getTypeInfosAsTree(), "BetaChild").getValue().getSuperTypeInfo());
  }

  @Test
  public void programTypeInfoHasNullSuperTypeInfo() {
    ProjectInfo info = createProjectInfo();
    assertNull(findNode(info.getTypeInfosAsTree(), "Program").getValue().getSuperTypeInfo());
  }

  @Test
  public void parentTypeInfoHasNullSuperTypeInfo() {
    ProjectInfo info = createProjectInfo();
    assertNull(findNode(info.getTypeInfosAsTree(), "AlphaParent").getValue().getSuperTypeInfo());
  }

  @Test
  public void allValuesInTypeCollectionAreTypeInfoInstances() {
    for (TypeInfo info : createProjectInfo().getTypeInfos()) {
      assertSame(TypeInfo.class, info.getClass());
    }
  }

  @Test
  public void selectingTypeCheckboxIsStableAcrossUpdate() {
    ProjectInfo info = createProjectInfo();
    TypeInfo childInfo = findNode(info.getTypeInfosAsTree(), "BetaChild").getValue();
    childInfo.getCheckBox().getModel().setSelected(true);
    info.update();
    assertTrue(childInfo.getCheckBox().getModel().isSelected());
  }
}
