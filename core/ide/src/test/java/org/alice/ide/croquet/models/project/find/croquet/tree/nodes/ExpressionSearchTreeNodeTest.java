package org.alice.ide.croquet.models.project.find.croquet.tree.nodes;

import org.junit.Before;
import org.junit.Test;
import org.lgna.project.ast.*;

import static org.junit.Assert.*;

/**
 * Tests for {@link ExpressionSearchTreeNode} and {@link DeclarationSeachTreeNode}
 * — typed wrappers for SearchTreeNode.
 */
public class ExpressionSearchTreeNodeTest {

  private SearchTreeNode root;

  @Before
  public void setUp() {
    root = new SearchTreeNode(null);
  }

  // ---- ExpressionSearchTreeNode construction ----

  @Test
  public void construct_withNullLiteral_succeeds() {
    NullLiteral expr = new NullLiteral();
    ExpressionSearchTreeNode node = new ExpressionSearchTreeNode(root, expr);
    assertNotNull(node);
  }

  @Test
  public void construct_withIntegerLiteral_succeeds() {
    IntegerLiteral expr = new IntegerLiteral(42);
    ExpressionSearchTreeNode node = new ExpressionSearchTreeNode(root, expr);
    assertNotNull(node);
  }

  // ---- getValue ----

  @Test
  public void getValue_returnsExpression() {
    NullLiteral expr = new NullLiteral();
    ExpressionSearchTreeNode node = new ExpressionSearchTreeNode(root, expr);
    assertSame(expr, node.getValue());
  }

  @Test
  public void getValue_integerLiteral() {
    IntegerLiteral expr = new IntegerLiteral(99);
    ExpressionSearchTreeNode node = new ExpressionSearchTreeNode(root, expr);
    assertSame(expr, node.getValue());
  }

  @Test
  public void getValue_doubleLiteral() {
    DoubleLiteral expr = new DoubleLiteral(3.14);
    ExpressionSearchTreeNode node = new ExpressionSearchTreeNode(root, expr);
    assertSame(expr, node.getValue());
  }

  @Test
  public void getValue_booleanLiteral() {
    BooleanLiteral expr = new BooleanLiteral(true);
    ExpressionSearchTreeNode node = new ExpressionSearchTreeNode(root, expr);
    assertSame(expr, node.getValue());
  }

  @Test
  public void getValue_stringLiteral() {
    StringLiteral expr = new StringLiteral("hello");
    ExpressionSearchTreeNode node = new ExpressionSearchTreeNode(root, expr);
    assertSame(expr, node.getValue());
  }

  // ---- getParent ----

  @Test
  public void getParent_returnsRoot() {
    NullLiteral expr = new NullLiteral();
    ExpressionSearchTreeNode node = new ExpressionSearchTreeNode(root, expr);
    assertSame(root, node.getParent());
  }

  @Test
  public void getParent_underDeclarationNode() {
    UserMethod method = new UserMethod();
    DeclarationSeachTreeNode declNode = new DeclarationSeachTreeNode(root, method);
    root.addChild(declNode);

    NullLiteral expr = new NullLiteral();
    ExpressionSearchTreeNode exprNode = new ExpressionSearchTreeNode(declNode, expr);
    declNode.addChild(exprNode);

    assertSame(declNode, exprNode.getParent());
  }

  // ---- isLeaf ----

  @Test
  public void isLeaf_noChildren_returnsTrue() {
    ExpressionSearchTreeNode node = new ExpressionSearchTreeNode(root, new NullLiteral());
    assertTrue(node.getIsLeaf());
  }

  @Test
  public void isLeaf_withChildren_returnsFalse() {
    ExpressionSearchTreeNode node = new ExpressionSearchTreeNode(root, new NullLiteral());
    node.addChild(new SearchTreeNode(node));
    assertFalse(node.getIsLeaf());
  }

  // ---- toString ----

  @Test
  public void toString_returnsNonNull() {
    ExpressionSearchTreeNode node = new ExpressionSearchTreeNode(root, new NullLiteral());
    assertNotNull(node.toString());
  }

  // ---- DeclarationSeachTreeNode interop ----

  @Test
  public void declarationNode_getValue_returnsDeclaration() {
    UserMethod method = new UserMethod();
    method.name.setValue("searchMe");
    DeclarationSeachTreeNode node = new DeclarationSeachTreeNode(root, method);
    assertSame(method, node.getValue());
  }

  @Test
  public void declarationNode_toString_returnsNonNull() {
    UserMethod method = new UserMethod();
    method.name.setValue("searchMe");
    DeclarationSeachTreeNode node = new DeclarationSeachTreeNode(root, method);
    assertNotNull(node.toString());
  }

  @Test
  public void declarationNode_withField_getValue() {
    UserField field = new UserField();
    field.name.setValue("testField");
    DeclarationSeachTreeNode node = new DeclarationSeachTreeNode(root, field);
    assertSame(field, node.getValue());
  }

  // ---- mixed tree ----

  @Test
  public void mixedTree_declarationWithExpressionChildren() {
    UserMethod method = new UserMethod();
    method.name.setValue("myMethod");
    DeclarationSeachTreeNode declNode = new DeclarationSeachTreeNode(root, method);
    root.addChild(declNode);

    NullLiteral ref1 = new NullLiteral();
    NullLiteral ref2 = new NullLiteral();
    ExpressionSearchTreeNode e1 = new ExpressionSearchTreeNode(declNode, ref1);
    ExpressionSearchTreeNode e2 = new ExpressionSearchTreeNode(declNode, ref2);
    declNode.addChild(e1);
    declNode.addChild(e2);

    assertEquals(2, declNode.getChildren().size());
    assertSame(ref1, e1.getValue());
    assertSame(ref2, e2.getValue());
    assertFalse(declNode.getIsLeaf());
  }

  @Test
  public void mixedTree_rootWithMultipleDeclarations() {
    UserMethod m1 = new UserMethod();
    m1.name.setValue("method1");
    UserField f1 = new UserField();
    f1.name.setValue("field1");

    DeclarationSeachTreeNode dn1 = new DeclarationSeachTreeNode(root, m1);
    DeclarationSeachTreeNode dn2 = new DeclarationSeachTreeNode(root, f1);
    root.addChild(dn1);
    root.addChild(dn2);

    assertEquals(2, root.getChildren().size());
    assertSame(m1, dn1.getValue());
    assertSame(f1, dn2.getValue());
  }

  // ---- childrenContains with expressions ----

  @Test
  public void childrenContains_matchingExpression() {
    NullLiteral expr = new NullLiteral();
    ExpressionSearchTreeNode child = new ExpressionSearchTreeNode(root, expr);
    root.addChild(child);
    assertTrue(root.childrenContains(expr));
  }

  @Test
  public void childrenContains_nonMatchingExpression() {
    NullLiteral expr = new NullLiteral();
    ExpressionSearchTreeNode child = new ExpressionSearchTreeNode(root, expr);
    root.addChild(child);
    assertFalse(root.childrenContains(new IntegerLiteral(1)));
  }
}
