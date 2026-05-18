package org.alice.ide.croquet.models.project.find.croquet.tree.nodes;

import org.junit.Before;
import org.junit.Test;
import org.lgna.project.ast.*;

import static org.junit.Assert.*;

/**
 * Tests for {@link ExpressionSearchTreeNode} — tree node wrapping an Expression.
 * Covers getValue, parent relationships, and inheritance from SearchTreeNode.
 */
public class ExpressionSearchTreeNodeTest {

  private SearchTreeNode root;

  @Before
  public void setUp() {
    root = new SearchTreeNode(null);
  }

  // ---- construction ----

  @Test
  public void construct_succeeds() {
    Expression expr = new NullLiteral();
    ExpressionSearchTreeNode node = new ExpressionSearchTreeNode(root, expr);
    assertNotNull(node);
  }

  @Test
  public void construct_withNullExpression_succeeds() {
    ExpressionSearchTreeNode node = new ExpressionSearchTreeNode(root, null);
    assertNotNull(node);
  }

  @Test
  public void construct_withNullParent_succeeds() {
    Expression expr = new NullLiteral();
    ExpressionSearchTreeNode node = new ExpressionSearchTreeNode(null, expr);
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
    IntegerLiteral expr = new IntegerLiteral(42);
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
  public void getValue_stringLiteral() {
    StringLiteral expr = new StringLiteral("hello");
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
  public void getValue_nullExpression_returnsNull() {
    ExpressionSearchTreeNode node = new ExpressionSearchTreeNode(root, null);
    assertNull(node.getValue());
  }

  // ---- parent ----

  @Test
  public void getParent_returnsRoot() {
    ExpressionSearchTreeNode node = new ExpressionSearchTreeNode(root, new NullLiteral());
    assertSame(root, node.getParent());
  }

  @Test
  public void getParent_nestedUnderDeclaration() {
    UserMethod method = new UserMethod();
    method.name.setValue("test");
    DeclarationSeachTreeNode declNode = new DeclarationSeachTreeNode(root, method);

    Expression expr = new NullLiteral();
    ExpressionSearchTreeNode exprNode = new ExpressionSearchTreeNode(declNode, expr);

    assertSame(declNode, exprNode.getParent());
  }

  // ---- inheritance from SearchTreeNode ----

  @Test
  public void isInstanceOfSearchTreeNode() {
    ExpressionSearchTreeNode node = new ExpressionSearchTreeNode(root, new NullLiteral());
    assertTrue(node instanceof SearchTreeNode);
  }

  @Test
  public void getIsLeaf_noChildren_true() {
    ExpressionSearchTreeNode node = new ExpressionSearchTreeNode(root, new NullLiteral());
    assertTrue(node.getIsLeaf());
  }

  @Test
  public void addChild_makesNonLeaf() {
    ExpressionSearchTreeNode node = new ExpressionSearchTreeNode(root, new NullLiteral());
    node.addChild(new SearchTreeNode(node));
    assertFalse(node.getIsLeaf());
  }

  @Test
  public void getChildren_initiallyEmpty() {
    ExpressionSearchTreeNode node = new ExpressionSearchTreeNode(root, new NullLiteral());
    assertTrue(node.getChildren().isEmpty());
  }

  // ---- childrenContains with expression value ----

  @Test
  public void childrenContains_withSameExpression_true() {
    NullLiteral parentExpr = new NullLiteral();
    ExpressionSearchTreeNode parent = new ExpressionSearchTreeNode(root, parentExpr);

    IntegerLiteral childExpr = new IntegerLiteral(5);
    ExpressionSearchTreeNode child = new ExpressionSearchTreeNode(parent, childExpr);
    parent.addChild(child);

    assertTrue(parent.childrenContains(childExpr));
  }

  // ---- sibling navigation ----

  @Test
  public void siblings_expressionNodes() {
    NullLiteral e1 = new NullLiteral();
    IntegerLiteral e2 = new IntegerLiteral(1);
    ExpressionSearchTreeNode n1 = new ExpressionSearchTreeNode(root, e1);
    ExpressionSearchTreeNode n2 = new ExpressionSearchTreeNode(root, e2);
    root.addChild(n1);
    root.addChild(n2);

    assertSame(n2, n1.getYoungerSibling());
    assertSame(n1, n2.getOlderSibling());
  }

  // ---- toString ----

  @Test
  public void toString_returnsNonNull() {
    ExpressionSearchTreeNode node = new ExpressionSearchTreeNode(root, new NullLiteral());
    assertNotNull(node.toString());
  }

  @Test
  public void toString_withMethodInvocation() {
    UserMethod method = new UserMethod();
    method.name.setValue("doSomething");
    MethodInvocation invocation = new MethodInvocation();
    invocation.method.setValue(method);

    ExpressionSearchTreeNode node = new ExpressionSearchTreeNode(root, invocation);
    assertEquals("doSomething", node.toString());
  }

  // ---- different expression types coexist ----

  @Test
  public void multipleExpressionTypes_inSameTree() {
    NullLiteral nullExpr = new NullLiteral();
    IntegerLiteral intExpr = new IntegerLiteral(42);
    DoubleLiteral dblExpr = new DoubleLiteral(3.14);

    ExpressionSearchTreeNode n1 = new ExpressionSearchTreeNode(root, nullExpr);
    ExpressionSearchTreeNode n2 = new ExpressionSearchTreeNode(root, intExpr);
    ExpressionSearchTreeNode n3 = new ExpressionSearchTreeNode(root, dblExpr);

    root.addChild(n1);
    root.addChild(n2);
    root.addChild(n3);

    assertEquals(3, root.getChildren().size());
    assertSame(nullExpr, n1.getValue());
    assertSame(intExpr, n2.getValue());
    assertSame(dblExpr, n3.getValue());
  }
}
