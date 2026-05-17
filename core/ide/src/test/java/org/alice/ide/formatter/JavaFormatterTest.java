package org.alice.ide.formatter;

import org.junit.Test;
import org.lgna.project.ast.*;

import static org.junit.Assert.*;

/**
 * Tests for {@link JavaFormatter} — Java code formatting via singleton.
 */
public class JavaFormatterTest {

  private final JavaFormatter formatter = JavaFormatter.getInstance();

  // ---- singleton ----

  @Test
  public void getInstance_returnsSameInstance() {
    assertSame(JavaFormatter.getInstance(), JavaFormatter.getInstance());
  }

  @Test
  public void getInstance_isNotNull() {
    assertNotNull(JavaFormatter.getInstance());
  }

  // ---- text for null/this ----

  @Test
  public void getTextForNull_returnsNull() {
    assertEquals("null", formatter.getTextForNull());
  }

  @Test
  public void getTextForThis_returnsThis() {
    assertEquals("this", formatter.getTextForThis());
  }

  // ---- isTypeExpressionDesired ----

  @Test
  public void isTypeExpressionDesired_returnsTrue() {
    assertTrue(formatter.isTypeExpressionDesired());
  }

  // ---- getFinalText ----

  @Test
  public void getFinalText_returnsFinal() {
    assertEquals("final", formatter.getFinalText());
  }

  // ---- getNewFormat ----

  @Test
  public void getNewFormat_containsNew() {
    String fmt = formatter.getNewFormat();
    assertTrue(fmt.contains("new"));
    assertTrue(fmt.contains("%s"));
  }

  // ---- getTrailerTextForCode ----

  @Test
  public void getTrailerTextForCode_returnsClosingBrace() {
    UserMethod method = new UserMethod();
    method.name.setValue("test");
    method.returnType.setValue(JavaType.VOID_TYPE);
    method.managementLevel.setValue(ManagementLevel.NONE);
    assertEquals("}", formatter.getTrailerTextForCode(method));
  }

  // ---- getHeaderTextForCode ----

  @Test
  public void getHeaderTextForCode_procedure_returnsTemplate() {
    UserMethod method = new UserMethod();
    method.name.setValue("doSomething");
    method.returnType.setValue(JavaType.VOID_TYPE);
    method.managementLevel.setValue(ManagementLevel.NONE);
    String header = formatter.getHeaderTextForCode(method);
    assertNotNull(header);
    // JavaFormatter returns template syntax: "</getReturnType()/> </getName()/>"
    assertTrue(header.contains("getReturnType") || header.contains("getName"));
  }

  // ---- toString ----

  @Test
  public void toString_returnsJava() {
    assertEquals("Java", formatter.toString());
  }

  // ---- getTemplateText ----

  @Test
  public void getTemplateText_returnsNonNullForKnownTypes() {
    // WhileLoop has a template defined
    String template = formatter.getTemplateText(WhileLoop.class);
    // template may be from map or from base; just verify non-null
    assertNotNull(template);
  }

  // ---- getInfixExpressionText with arithmetic ----

  @Test
  public void getInfixExpressionText_arithmeticPlus() {
    ArithmeticInfixExpression expr = new ArithmeticInfixExpression(
        new DoubleLiteral(1.0),
        ArithmeticInfixExpression.Operator.PLUS,
        new DoubleLiteral(2.0),
        JavaType.getInstance(Double.class));
    String text = formatter.getInfixExpressionText(expr);
    assertNotNull(text);
    assertTrue(text.contains("+"));
  }
}
