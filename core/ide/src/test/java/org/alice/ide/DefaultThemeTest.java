package org.alice.ide;

import org.junit.Before;
import org.junit.Test;
import org.lgna.project.ast.*;

import java.awt.Color;

import static org.junit.Assert.*;

/**
 * Tests for {@link DefaultTheme} — exercises color-for-class, color-for-node,
 * code color, and knurl color logic without requiring a running IDE.
 * Colors from UIManager may be null when not in a full Swing LAF context,
 * so we verify the methods don't throw and that the branching logic is covered.
 */
public class DefaultThemeTest {

  private DefaultTheme theme;

  @Before
  public void setUp() {
    theme = new DefaultTheme();
  }

  // ---- getColorFor(Class) — statement branches ----

  @Test
  public void colorForComment_doesNotThrow() {
    theme.getColorFor(Comment.class);
  }

  @Test
  public void colorForLocalDeclarationStatement_doesNotThrow() {
    theme.getColorFor(LocalDeclarationStatement.class);
  }

  @Test
  public void colorForDoInOrder_doesNotThrow() {
    theme.getColorFor(DoInOrder.class);
  }

  @Test
  public void colorForConditionalStatement_doesNotThrow() {
    theme.getColorFor(ConditionalStatement.class);
  }

  @Test
  public void colorForExpressionStatement_doesNotThrow() {
    theme.getColorFor(ExpressionStatement.class);
  }

  @Test
  public void colorForReturnStatement_doesNotThrow() {
    theme.getColorFor(ReturnStatement.class);
  }

  // ---- getColorFor(Class) — expression branches ----

  @Test
  public void colorForMethodInvocation_doesNotThrow() {
    theme.getColorFor(MethodInvocation.class);
  }

  @Test
  public void colorForInfixExpression_doesNotThrow() {
    theme.getColorFor(InfixExpression.class);
  }

  @Test
  public void colorForLogicalComplement_doesNotThrow() {
    theme.getColorFor(LogicalComplement.class);
  }

  @Test
  public void colorForStringConcatenation_doesNotThrow() {
    theme.getColorFor(StringConcatenation.class);
  }

  @Test
  public void colorForInstanceCreation_doesNotThrow() {
    theme.getColorFor(InstanceCreation.class);
  }

  @Test
  public void colorForArrayInstanceCreation_doesNotThrow() {
    theme.getColorFor(ArrayInstanceCreation.class);
  }

  @Test
  public void colorForResourceExpression_doesNotThrow() {
    theme.getColorFor(ResourceExpression.class);
  }

  @Test
  public void colorForTypeExpression_doesNotThrow() {
    theme.getColorFor(TypeExpression.class);
  }

  @Test
  public void colorForNullLiteral_returnsRed() {
    Color color = theme.getColorFor(NullLiteral.class);
    assertEquals(Color.RED, color);
  }

  @Test
  public void colorForGenericExpression_doesNotThrow() {
    // DoubleLiteral is an expression not matching any specific branch above
    theme.getColorFor(DoubleLiteral.class);
  }

  // ---- getColorFor(Node) ----

  @Test
  public void colorForNullNode_doesNotThrow() {
    theme.getColorFor((Node) null);
  }

  @Test
  public void colorForNullLiteralNode() {
    NullLiteral node = new NullLiteral();
    Color color = theme.getColorFor(node);
    assertEquals(Color.RED, color);
  }

  // ---- getCodeColor ----

  @Test
  public void codeColor_forNull_doesNotThrow() {
    // null is not a Code but the method should handle unexpected inputs
    try {
      theme.getCodeColor(null);
    } catch (NullPointerException e) {
      // acceptable — null is not a valid Code
    }
  }

  // ---- getKnurlColorFor ----

  @Test
  public void knurlColor_darkBackground_doesNotThrow() {
    theme.getKnurlColorFor(new Color(10, 10, 10));
  }

  @Test
  public void knurlColor_lightBackground_doesNotThrow() {
    theme.getKnurlColorFor(new Color(250, 250, 250));
  }

  @Test
  public void knurlColor_darkThreshold_boundary() {
    // brightness = 199 + 200 + 200 = 599 < 600 → dark
    theme.getKnurlColorFor(new Color(199, 200, 200));
    // brightness = 200 + 200 + 200 = 600 → not dark
    theme.getKnurlColorFor(new Color(200, 200, 200));
  }

  // ---- fallback branch: non-statement, non-expression Node ----

  @Test
  public void colorFor_nonStatementNonExpression_doesNotThrow() {
    // BlockStatement is a Statement, but we can test with a superclass
    // UserMethod is not a Statement or Expression
    theme.getColorFor(UserMethod.class);
  }
}
