package org.alice.ide.formatter;

import org.junit.Test;
import org.lgna.project.ast.ArithmeticInfixExpression;
import org.lgna.project.ast.AstUtilities;
import org.lgna.project.ast.BooleanLiteral;
import org.lgna.project.ast.ConditionalInfixExpression;
import org.lgna.project.ast.InstanceCreation;
import org.lgna.project.ast.IntegerLiteral;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.NamedUserConstructor;
import org.lgna.project.ast.RelationalInfixExpression;
import org.lgna.project.ast.UserMethod;
import org.lgna.project.ast.WhileLoop;

import static org.junit.Assert.assertEquals;

public class JavaFormatterBehaviorTest {
  private final JavaFormatter formatter = JavaFormatter.getInstance();

  @Test
  public void getHeaderTextForCodeUsesDifferentTemplatesForMethodsAndConstructors() {
    UserMethod method = AstUtilities.createMethod("walk", JavaType.VOID_TYPE);

    assertEquals("</getReturnType()/> </getName()/> ( </getParameters()/> ) {", formatter.getHeaderTextForCode(method));
    assertEquals("</getDeclaringType()/> ( </getParameters()/> ) {", formatter.getHeaderTextForCode(new NamedUserConstructor()));
  }

  @Test
  public void getTemplateTextReturnsJavaSpecificSnippetsForKnownNodeTypes() {
    assertEquals("while( </conditional/> ) {\n\t</body/>\n}", formatter.getTemplateText(WhileLoop.class));
    assertEquals("new </constructor/>( </requiredArguments/></variableArguments/></keyedArguments/> )", formatter.getTemplateText(InstanceCreation.class));
  }

  @Test
  public void getInfixExpressionTextUsesJavaOperatorTemplates() {
    ArithmeticInfixExpression remainder = new ArithmeticInfixExpression(
        new IntegerLiteral(5), ArithmeticInfixExpression.Operator.INTEGER_REMAINDER, new IntegerLiteral(2), JavaType.getInstance(Integer.class));
    ConditionalInfixExpression conjunction = new ConditionalInfixExpression(
        new BooleanLiteral(true), ConditionalInfixExpression.Operator.AND, new BooleanLiteral(false));
    RelationalInfixExpression notEquals = new RelationalInfixExpression(
        new IntegerLiteral(1), RelationalInfixExpression.Operator.NOT_EQUALS, new IntegerLiteral(2), Integer.class, Integer.class);

    assertEquals("</leftOperand/> % </rightOperand/>", formatter.getInfixExpressionText(remainder));
    assertEquals("</leftOperand/> && </rightOperand/>", formatter.getInfixExpressionText(conjunction));
    assertEquals("</leftOperand/> != </rightOperand/>", formatter.getInfixExpressionText(notEquals));
  }

  @Test
  public void javaFormatterUsesLiteralJavaKeywordsForNullThisAndConstruction() {
    assertEquals("null", formatter.getTextForNull());
    assertEquals("this", formatter.getTextForThis());
    assertEquals("new %s( %s )", formatter.getNewFormat());
  }
}
