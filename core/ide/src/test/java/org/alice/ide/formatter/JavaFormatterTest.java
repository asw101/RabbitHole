package org.alice.ide.formatter;

import org.junit.Test;
import org.lgna.project.ast.ArithmeticInfixExpression;
import org.lgna.project.ast.BlockStatement;
import org.lgna.project.ast.BooleanLiteral;
import org.lgna.project.ast.ConditionalInfixExpression;
import org.lgna.project.ast.IntegerLiteral;
import org.lgna.project.ast.RelationalInfixExpression;
import org.lgna.project.ast.UserMethod;
import org.lgna.project.ast.UserParameter;
import org.lgna.project.ast.WhileLoop;

import static org.junit.Assert.*;

public class JavaFormatterTest {
  private final JavaFormatter formatter = JavaFormatter.getInstance();

  private static UserMethod createMethod(String name) {
    return new UserMethod(name, String.class, new UserParameter[0], new BlockStatement());
  }

  @Test
  public void getInstanceReturnsSingleton() {
    assertSame(JavaFormatter.getInstance(), JavaFormatter.getInstance());
  }

  @Test
  public void getHeaderTextForCodeUsesJavaMethodTemplate() {
    assertEquals(
        "</getReturnType()/> </getName()/> ( </getParameters()/> ) {",
        formatter.getHeaderTextForCode(createMethod("getLabel")));
  }

  @Test
  public void getTrailerTextForCodeReturnsClosingBrace() {
    assertEquals("}", formatter.getTrailerTextForCode(createMethod("getLabel")));
  }

  @Test
  public void getTemplateTextReturnsMappedWhileLoopTemplate() {
    assertEquals("while( </conditional/> ) {\n\t</body/>\n}", formatter.getTemplateText(WhileLoop.class));
  }

  @Test
  public void getInfixExpressionTextFormatsIntegerDivide() {
    ArithmeticInfixExpression expression = new ArithmeticInfixExpression(
        new IntegerLiteral(6),
        ArithmeticInfixExpression.Operator.INTEGER_DIVIDE,
        new IntegerLiteral(3),
        Integer.class);

    assertEquals("</leftOperand/> / </rightOperand/>", formatter.getInfixExpressionText(expression));
  }

  @Test
  public void getInfixExpressionTextFormatsConditionalAnd() {
    ConditionalInfixExpression expression = new ConditionalInfixExpression(
        new BooleanLiteral(true),
        ConditionalInfixExpression.Operator.AND,
        new BooleanLiteral(false));

    assertEquals("</leftOperand/> && </rightOperand/>", formatter.getInfixExpressionText(expression));
  }

  @Test
  public void getInfixExpressionTextFormatsEquals() {
    RelationalInfixExpression expression = new RelationalInfixExpression(
        new IntegerLiteral(1),
        RelationalInfixExpression.Operator.EQUALS,
        new IntegerLiteral(1),
        Integer.class,
        Integer.class);

    assertEquals("</leftOperand/> == </rightOperand/>", formatter.getInfixExpressionText(expression));
  }

  @Test
  public void keywordTextMatchesJavaSyntax() {
    assertEquals("this", formatter.getTextForThis());
    assertEquals("null", formatter.getTextForNull());
    assertEquals("final", formatter.getFinalText());
  }

  @Test
  public void javaFormattingFlagsUseJavaConventions() {
    assertTrue(formatter.isTypeExpressionDesired());
    assertEquals("new %s( %s )", formatter.getNewFormat());
  }
}
