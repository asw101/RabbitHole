package org.alice.ide.croquet.models.project.find.croquet.tree.nodes;

import org.junit.Before;
import org.junit.Test;
import org.lgna.project.ast.*;

import static org.junit.Assert.*;

/**
 * Tests for {@link SearchTreeNode} — tree data structure for search results.
 * Covers construction, parent/child management, getValue, getIsLeaf,
 * sibling navigation, and toString.
 */
public class SearchTreeNodeTest {

  private SearchTreeNode root;

  @Before
  public void setUp() {
    root = new SearchTreeNode(null);
  }

  // ---- construction ----

  @Test
  public void construct_withNullParent_succeeds() {
    SearchTreeNode node = new SearchTreeNode(null);
    assertNotNull(node);
  }

  @Test
  public void construct_withParent_succeeds() {
    SearchTreeNode child = new SearchTreeNode(root);
    assertNotNull(child);
  }

  // ---- getParent ----

  @Test
  public void getParent_root_returnsNull() {
    assertNull(root.getParent());
  }

  @Test
  public void getParent_child_returnsParent() {
    SearchTreeNode child = new SearchTreeNode(root);
    assertSame(root, child.getParent());
  }

  @Test
  public void getParent_grandchild_returnsChild() {
    SearchTreeNode child = new SearchTreeNode(root);
    SearchTreeNode grandchild = new SearchTreeNode(child);
    assertSame(child, grandchild.getParent());
  }

  // ---- getValue ----

  @Test
  public void getValue_baseNode_returnsNull() {
    assertNull(root.getValue());
  }

  // ---- children management ----

  @Test
  public void getChildren_initiallyEmpty() {
    assertTrue(root.getChildren().isEmpty());
  }

  @Test
  public void addChild_addsToList() {
    SearchTreeNode child = new SearchTreeNode(root);
    root.addChild(child);
    assertEquals(1, root.getChildren().size());
    assertSame(child, root.getChildren().get(0));
  }

  @Test
  public void addChild_multipleChildren() {
    SearchTreeNode c1 = new SearchTreeNode(root);
    SearchTreeNode c2 = new SearchTreeNode(root);
    SearchTreeNode c3 = new SearchTreeNode(root);
    root.addChild(c1);
    root.addChild(c2);
    root.addChild(c3);
    assertEquals(3, root.getChildren().size());
  }

  @Test
  public void removeAllChildren_clearsAll() {
    root.addChild(new SearchTreeNode(root));
    root.addChild(new SearchTreeNode(root));
    root.addChild(new SearchTreeNode(root));
    assertEquals(3, root.getChildren().size());

    root.removeAllChildren();
    assertTrue(root.getChildren().isEmpty());
  }

  @Test
  public void removeAllChildren_onEmpty_doesNotThrow() {
    root.removeAllChildren();
    assertTrue(root.getChildren().isEmpty());
  }

  // ---- getIsLeaf ----

  @Test
  public void getIsLeaf_noChildren_returnsTrue() {
    assertTrue(root.getIsLeaf());
  }

  @Test
  public void getIsLeaf_withChildren_returnsFalse() {
    root.addChild(new SearchTreeNode(root));
    assertFalse(root.getIsLeaf());
  }

  @Test
  public void getIsLeaf_afterRemoveAll_returnsTrue() {
    root.addChild(new SearchTreeNode(root));
    assertFalse(root.getIsLeaf());
    root.removeAllChildren();
    assertTrue(root.getIsLeaf());
  }

  // ---- childrenContains ----

  @Test
  public void childrenContains_emptyList_returnsFalse() {
    assertFalse(root.childrenContains("something"));
  }

  @Test
  public void childrenContains_withMatchingChild_returnsTrue() {
    NullLiteral expr = new NullLiteral();
    ExpressionSearchTreeNode child = new ExpressionSearchTreeNode(root, expr);
    root.addChild(child);
    assertTrue(root.childrenContains(expr));
  }

  @Test
  public void childrenContains_withNonMatchingValue_returnsFalse() {
    NullLiteral expr = new NullLiteral();
    ExpressionSearchTreeNode child = new ExpressionSearchTreeNode(root, expr);
    root.addChild(child);
    assertFalse(root.childrenContains(new IntegerLiteral(42)));
  }

  // ---- getChildForReference ----

  @Test
  public void getChildForReference_found_returnsChild() {
    NullLiteral expr = new NullLiteral();
    ExpressionSearchTreeNode child = new ExpressionSearchTreeNode(root, expr);
    root.addChild(child);
    assertSame(child, root.getChildForReference(expr));
  }

  @Test
  public void getChildForReference_notFound_returnsNull() {
    NullLiteral expr = new NullLiteral();
    ExpressionSearchTreeNode child = new ExpressionSearchTreeNode(root, expr);
    root.addChild(child);
    assertNull(root.getChildForReference(new IntegerLiteral(99)));
  }

  @Test
  public void getChildForReference_emptyList_returnsNull() {
    assertNull(root.getChildForReference("anything"));
  }

  // ---- getLocationAmongstSiblings ----

  @Test
  public void getLocationAmongstSiblings_firstChild_returns0() {
    SearchTreeNode child = new SearchTreeNode(root);
    root.addChild(child);
    assertEquals(0, child.getLocationAmongstSiblings());
  }

  @Test
  public void getLocationAmongstSiblings_secondChild_returns1() {
    SearchTreeNode c1 = new SearchTreeNode(root);
    SearchTreeNode c2 = new SearchTreeNode(root);
    root.addChild(c1);
    root.addChild(c2);
    assertEquals(0, c1.getLocationAmongstSiblings());
    assertEquals(1, c2.getLocationAmongstSiblings());
  }

  @Test
  public void getLocationAmongstSiblings_thirdChild_returns2() {
    SearchTreeNode c1 = new SearchTreeNode(root);
    SearchTreeNode c2 = new SearchTreeNode(root);
    SearchTreeNode c3 = new SearchTreeNode(root);
    root.addChild(c1);
    root.addChild(c2);
    root.addChild(c3);
    assertEquals(2, c3.getLocationAmongstSiblings());
  }

  // ---- sibling navigation ----

  @Test
  public void getYoungerSibling_returnsNextChild() {
    SearchTreeNode c1 = new SearchTreeNode(root);
    SearchTreeNode c2 = new SearchTreeNode(root);
    root.addChild(c1);
    root.addChild(c2);
    assertSame(c2, c1.getYoungerSibling());
  }

  @Test
  public void getOlderSibling_returnsPreviousChild() {
    SearchTreeNode c1 = new SearchTreeNode(root);
    SearchTreeNode c2 = new SearchTreeNode(root);
    root.addChild(c1);
    root.addChild(c2);
    assertSame(c1, c2.getOlderSibling());
  }

  @Test
  public void sibling_threeChildren_middleHasBoth() {
    SearchTreeNode c1 = new SearchTreeNode(root);
    SearchTreeNode c2 = new SearchTreeNode(root);
    SearchTreeNode c3 = new SearchTreeNode(root);
    root.addChild(c1);
    root.addChild(c2);
    root.addChild(c3);

    assertSame(c2, c1.getYoungerSibling());
    assertSame(c1, c2.getOlderSibling());
    assertSame(c3, c2.getYoungerSibling());
    assertSame(c2, c3.getOlderSibling());
  }

  // ---- toString ----

  @Test
  public void toString_root_returnsROOT() {
    assertEquals("ROOT", root.toString());
  }

  @Test
  public void toString_expressionNode_returnsValueToString() {
    NullLiteral expr = new NullLiteral();
    ExpressionSearchTreeNode node = new ExpressionSearchTreeNode(root, expr);
    assertNotNull(node.toString());
  }

  // ---- DeclarationSeachTreeNode (note: typo in original class name) ----

  @Test
  public void declarationNode_construct_succeeds() {
    UserMethod method = new UserMethod();
    method.name.setValue("testMethod");
    DeclarationSeachTreeNode node = new DeclarationSeachTreeNode(root, method);
    assertNotNull(node);
  }

  @Test
  public void declarationNode_getValue_returnsDeclaration() {
    UserMethod method = new UserMethod();
    method.name.setValue("testMethod");
    DeclarationSeachTreeNode node = new DeclarationSeachTreeNode(root, method);
    assertSame(method, node.getValue());
  }

  @Test
  public void declarationNode_getParent_returnsParent() {
    UserMethod method = new UserMethod();
    DeclarationSeachTreeNode node = new DeclarationSeachTreeNode(root, method);
    assertSame(root, node.getParent());
  }

  @Test
  public void declarationNode_isLeaf_whenNoChildren() {
    UserMethod method = new UserMethod();
    DeclarationSeachTreeNode node = new DeclarationSeachTreeNode(root, method);
    assertTrue(node.getIsLeaf());
  }

  @Test
  public void declarationNode_addChild_makesNonLeaf() {
    UserMethod method = new UserMethod();
    DeclarationSeachTreeNode node = new DeclarationSeachTreeNode(root, method);
    node.addChild(new SearchTreeNode(node));
    assertFalse(node.getIsLeaf());
  }

  @Test
  public void declarationNode_withField() {
    UserField field = new UserField();
    field.name.setValue("myField");
    DeclarationSeachTreeNode node = new DeclarationSeachTreeNode(root, field);
    assertSame(field, node.getValue());
  }

  // ---- tree depth ----

  @Test
  public void deepTree_parentChain() {
    SearchTreeNode level1 = new SearchTreeNode(root);
    SearchTreeNode level2 = new SearchTreeNode(level1);
    SearchTreeNode level3 = new SearchTreeNode(level2);

    root.addChild(level1);
    level1.addChild(level2);
    level2.addChild(level3);

    assertSame(root, level1.getParent());
    assertSame(level1, level2.getParent());
    assertSame(level2, level3.getParent());
    assertTrue(level3.getIsLeaf());
    assertFalse(level2.getIsLeaf());
    assertFalse(level1.getIsLeaf());
    assertFalse(root.getIsLeaf());
  }
}
