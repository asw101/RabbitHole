package org.alice.ide.cascade.fillerinners;

import org.lgna.croquet.CascadeBlankChild;
import org.lgna.project.annotations.IntegerValueDetails;
import org.lgna.project.annotations.NumberValueDetails;
import org.lgna.project.ast.*;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Deep tests for {@link IntegerFillerInner}, {@link DoubleFillerInner}, and
 * {@link AbstractNumberFillerInner} — getLiterals edge cases, appendItems with
 * ArithmeticInfixExpression previous expression, and custom ValueDetails.
 */
public class NumberFillerInnerDeepTest {

  // ---- IntegerFillerInner.getLiterals ----

  @Test
  public void integerGetLiterals_nullDetails_returnsDefault0123() {
    int[] literals = IntegerFillerInner.getLiterals(null);
    assertArrayEquals(new int[]{0, 1, 2, 3}, literals);
  }

  @Test
  public void integerGetLiterals_customDetails_returnsCustomValues() {
    IntegerValueDetails details = new IntegerValueDetails() {
      @Override
      public int[] getLiterals() {
        return new int[]{10, 20, 30};
      }

      @Override
      public Integer getMinimumValue() { return 0; }

      @Override
      public Integer getMaximumValue() { return 100; }

      @Override
      public Class<Integer> getSupportedCls() { return Integer.class; }
    };
    int[] literals = IntegerFillerInner.getLiterals(details);
    assertArrayEquals(new int[]{10, 20, 30}, literals);
  }

  @Test
  public void integerGetLiterals_emptyDetails_returnsEmpty() {
    IntegerValueDetails details = new IntegerValueDetails() {
      @Override
      public int[] getLiterals() {
        return new int[0];
      }

      @Override
      public Integer getMinimumValue() { return 0; }

      @Override
      public Integer getMaximumValue() { return 0; }

      @Override
      public Class<Integer> getSupportedCls() { return Integer.class; }
    };
    int[] literals = IntegerFillerInner.getLiterals(details);
    assertEquals(0, literals.length);
  }

  // ---- DoubleFillerInner.getLiterals ----

  @Test
  public void doubleGetLiterals_nullDetails_returnsDefault6Values() {
    double[] literals = DoubleFillerInner.getLiterals(null);
    assertEquals(6, literals.length);
    assertEquals(0.0, literals[0], 0.001);
    assertEquals(0.25, literals[1], 0.001);
    assertEquals(0.5, literals[2], 0.001);
    assertEquals(1.0, literals[3], 0.001);
    assertEquals(2.0, literals[4], 0.001);
    assertEquals(10.0, literals[5], 0.001);
  }

  @Test
  public void doubleGetLiterals_customDetails_returnsCustomValues() {
    NumberValueDetails details = new NumberValueDetails() {
      @Override
      public double[] getLiterals() {
        return new double[]{0.1, 0.2, 0.3};
      }

      @Override
      public Double getMinimumValue() { return 0.0; }

      @Override
      public Double getMaximumValue() { return 1.0; }

      @Override
      public Class<Number> getSupportedCls() { return Number.class; }
    };
    double[] literals = DoubleFillerInner.getLiterals(details);
    assertArrayEquals(new double[]{0.1, 0.2, 0.3}, literals, 0.001);
  }

  @Test
  public void doubleGetLiterals_emptyDetails_returnsEmpty() {
    NumberValueDetails details = new NumberValueDetails() {
      @Override
      public double[] getLiterals() {
        return new double[0];
      }

      @Override
      public Double getMinimumValue() { return 0.0; }

      @Override
      public Double getMaximumValue() { return 0.0; }

      @Override
      public Class<Number> getSupportedCls() { return Number.class; }
    };
    double[] literals = DoubleFillerInner.getLiterals(details);
    assertEquals(0, literals.length);
  }

  // ---- AbstractNumberFillerInner.appendItems with ArithmeticInfixExpression ----

  @Test
  public void integerAppendItems_withArithmeticPrev_addsReplaceAndReduceItems() {
    IntegerFillerInner filler = new IntegerFillerInner();
    List<CascadeBlankChild> items = new ArrayList<>();

    ArithmeticInfixExpression arithExpr = new ArithmeticInfixExpression(
        new IntegerLiteral(2),
        ArithmeticInfixExpression.Operator.PLUS,
        new IntegerLiteral(3),
        Integer.class);

    filler.appendItems(items, null, true, arithExpr);

    // The super.appendItems (AbstractNumberFillerInner) adds replace-operator + reduce items
    // when prev is ArithmeticInfixExpression, then IntegerFillerInner adds literals + menus
    assertTrue("Should have many items with arithmetic prev", items.size() > 5);
  }

  @Test
  public void doubleAppendItems_withArithmeticPrev_addsReplaceAndReduceItems() {
    DoubleFillerInner filler = new DoubleFillerInner();
    List<CascadeBlankChild> items = new ArrayList<>();

    ArithmeticInfixExpression arithExpr = new ArithmeticInfixExpression(
        new DoubleLiteral(1.5),
        ArithmeticInfixExpression.Operator.TIMES,
        new DoubleLiteral(2.0),
        Double.class);

    filler.appendItems(items, null, true, arithExpr);

    assertTrue("Should have many items with arithmetic prev", items.size() > 5);
  }

  @Test
  public void integerAppendItems_withArithmeticPrev_hasMoreItemsThanWithout() {
    IntegerFillerInner filler = new IntegerFillerInner();

    List<CascadeBlankChild> withArithPrev = new ArrayList<>();
    ArithmeticInfixExpression arithExpr = new ArithmeticInfixExpression(
        new IntegerLiteral(1),
        ArithmeticInfixExpression.Operator.MINUS,
        new IntegerLiteral(2),
        Integer.class);
    filler.appendItems(withArithPrev, null, true, arithExpr);

    List<CascadeBlankChild> withNullLitPrev = new ArrayList<>();
    filler.appendItems(withNullLitPrev, null, true, new NullLiteral());

    assertTrue("Arithmetic prev should produce more items than NullLiteral prev",
        withArithPrev.size() > withNullLitPrev.size());
  }

  // ---- appendItems: not-top does not add arithmetic items ----

  @Test
  public void integerAppendItems_notTop_noArithmeticItems() {
    IntegerFillerInner filler = new IntegerFillerInner();

    List<CascadeBlankChild> topItems = new ArrayList<>();
    ArithmeticInfixExpression arithExpr = new ArithmeticInfixExpression(
        new IntegerLiteral(1),
        ArithmeticInfixExpression.Operator.PLUS,
        new IntegerLiteral(2),
        Integer.class);
    filler.appendItems(topItems, null, true, arithExpr);

    List<CascadeBlankChild> notTopItems = new ArrayList<>();
    filler.appendItems(notTopItems, null, false, arithExpr);

    assertTrue("Top should have more items than non-top with same arithmetic prev",
        topItems.size() > notTopItems.size());
  }

  // ---- IntegerFillerInner isAssignableTo ----

  @Test
  public void integerFillerInner_isAssignableToNumber() {
    IntegerFillerInner filler = new IntegerFillerInner();
    assertTrue(filler.isAssignableTo(JavaType.getInstance(Number.class)));
  }

  @Test
  public void integerFillerInner_notAssignableToDouble() {
    IntegerFillerInner filler = new IntegerFillerInner();
    assertFalse(filler.isAssignableTo(JavaType.getInstance(Double.class)));
  }

  // ---- DoubleFillerInner isAssignableTo ----

  @Test
  public void doubleFillerInner_isAssignableToNumber() {
    DoubleFillerInner filler = new DoubleFillerInner();
    assertTrue(filler.isAssignableTo(JavaType.getInstance(Number.class)));
  }

  @Test
  public void doubleFillerInner_notAssignableToInteger() {
    DoubleFillerInner filler = new DoubleFillerInner();
    assertFalse(filler.isAssignableTo(JavaType.getInstance(Integer.class)));
  }

  // ---- ArithmeticInfixExpression.Operator coverage ----

  @Test
  public void arithmeticOperator_plus_operatesCorrectly() {
    Number result = ArithmeticInfixExpression.Operator.PLUS.operate(2, 3);
    assertEquals(5, result.intValue());
  }

  @Test
  public void arithmeticOperator_minus_operatesCorrectly() {
    Number result = ArithmeticInfixExpression.Operator.MINUS.operate(5, 3);
    assertEquals(2, result.intValue());
  }

  @Test
  public void arithmeticOperator_times_operatesCorrectly() {
    Number result = ArithmeticInfixExpression.Operator.TIMES.operate(4, 3);
    assertEquals(12, result.intValue());
  }

  @Test
  public void arithmeticOperator_realDivide_operatesCorrectly() {
    Number result = ArithmeticInfixExpression.Operator.REAL_DIVIDE.operate(7.0, 2.0);
    assertEquals(3.5, result.doubleValue(), 0.001);
  }

  @Test
  public void arithmeticOperator_integerDivide_operatesCorrectly() {
    Number result = ArithmeticInfixExpression.Operator.INTEGER_DIVIDE.operate(7, 2);
    assertEquals(3, result.intValue());
  }

  @Test
  public void arithmeticOperator_realRemainder_operatesCorrectly() {
    Number result = ArithmeticInfixExpression.Operator.REAL_REMAINDER.operate(7.0, 3.0);
    assertEquals(1.0, result.doubleValue(), 0.001);
  }

  @Test
  public void arithmeticOperator_integerRemainder_operatesCorrectly() {
    Number result = ArithmeticInfixExpression.Operator.INTEGER_REMAINDER.operate(7, 3);
    assertEquals(1, result.intValue());
  }

  @Test
  public void arithmeticOperator_plus_withDoubles() {
    Number result = ArithmeticInfixExpression.Operator.PLUS.operate(1.5, 2.5);
    assertEquals(4.0, result.doubleValue(), 0.001);
  }

  @Test
  public void arithmeticOperator_plus_mixedIntegerAndDouble() {
    Number result = ArithmeticInfixExpression.Operator.PLUS.operate(1, 2.5);
    assertEquals(3.5, result.doubleValue(), 0.001);
  }

  @Test
  public void arithmeticOperator_getSymbol() {
    assertEquals("+", ArithmeticInfixExpression.Operator.PLUS.getSymbol());
    assertEquals("-", ArithmeticInfixExpression.Operator.MINUS.getSymbol());
    assertEquals("*", ArithmeticInfixExpression.Operator.TIMES.getSymbol());
    assertEquals("/", ArithmeticInfixExpression.Operator.REAL_DIVIDE.getSymbol());
    assertEquals("%", ArithmeticInfixExpression.Operator.REAL_REMAINDER.getSymbol());
  }
}
