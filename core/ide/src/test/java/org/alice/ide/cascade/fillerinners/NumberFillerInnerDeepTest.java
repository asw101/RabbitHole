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

  private IntegerFillerInner intFiller;
  private DoubleFillerInner doubleFiller;

  @org.junit.Before
  public void setUp() {
    intFiller = new IntegerFillerInner();
    doubleFiller = new DoubleFillerInner();
  }

  private static IntegerValueDetails intDetails(int[] literals) {
    return new IntegerValueDetails() {
      @Override public int[] getLiterals() { return literals; }
      @Override public Integer getMinimumValue() { return 0; }
      @Override public Integer getMaximumValue() { return 100; }
      @Override public Class<Integer> getSupportedCls() { return Integer.class; }
    };
  }

  private static NumberValueDetails doubleDetails(double[] literals) {
    return new NumberValueDetails() {
      @Override public double[] getLiterals() { return literals; }
      @Override public Double getMinimumValue() { return 0.0; }
      @Override public Double getMaximumValue() { return 1.0; }
      @Override public Class<Number> getSupportedCls() { return Number.class; }
    };
  }

  // ---- IntegerFillerInner.getLiterals ----

  @Test
  public void integerGetLiterals_nullDetails_returnsDefault0123() {
    int[] literals = IntegerFillerInner.getLiterals(null);
    assertArrayEquals(new int[]{0, 1, 2, 3}, literals);
  }

  @Test
  public void integerGetLiterals_customDetails_returnsCustomValues() {
    int[] literals = IntegerFillerInner.getLiterals(intDetails(new int[]{10, 20, 30}));
    assertArrayEquals(new int[]{10, 20, 30}, literals);
  }

  @Test
  public void integerGetLiterals_emptyDetails_returnsEmpty() {
    int[] literals = IntegerFillerInner.getLiterals(intDetails(new int[0]));
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
    double[] literals = DoubleFillerInner.getLiterals(doubleDetails(new double[]{0.1, 0.2, 0.3}));
    assertArrayEquals(new double[]{0.1, 0.2, 0.3}, literals, 0.001);
  }

  @Test
  public void doubleGetLiterals_emptyDetails_returnsEmpty() {
    double[] literals = DoubleFillerInner.getLiterals(doubleDetails(new double[0]));
    assertEquals(0, literals.length);
  }

  // ---- AbstractNumberFillerInner.appendItems with ArithmeticInfixExpression ----

  @Test
  public void integerAppendItems_withArithmeticPrev_addsReplaceAndReduceItems() {
    List<CascadeBlankChild> items = new ArrayList<>();

    ArithmeticInfixExpression arithExpr = new ArithmeticInfixExpression(
        new IntegerLiteral(2),
        ArithmeticInfixExpression.Operator.PLUS,
        new IntegerLiteral(3),
        Integer.class);

    intFiller.appendItems(items, null, true, arithExpr);

    // The super.appendItems (AbstractNumberFillerInner) adds replace-operator + reduce items
    // when prev is ArithmeticInfixExpression, then IntegerFillerInner adds literals + menus
    assertTrue("Should have many items with arithmetic prev", items.size() > 5);
  }

  @Test
  public void doubleAppendItems_withArithmeticPrev_addsReplaceAndReduceItems() {
    List<CascadeBlankChild> items = new ArrayList<>();

    ArithmeticInfixExpression arithExpr = new ArithmeticInfixExpression(
        new DoubleLiteral(1.5),
        ArithmeticInfixExpression.Operator.TIMES,
        new DoubleLiteral(2.0),
        Double.class);

    doubleFiller.appendItems(items, null, true, arithExpr);

    assertTrue("Should have many items with arithmetic prev", items.size() > 5);
  }

  @Test
  public void integerAppendItems_withArithmeticPrev_hasMoreItemsThanWithout() {
    List<CascadeBlankChild> withArithPrev = new ArrayList<>();
    ArithmeticInfixExpression arithExpr = new ArithmeticInfixExpression(
        new IntegerLiteral(1),
        ArithmeticInfixExpression.Operator.MINUS,
        new IntegerLiteral(2),
        Integer.class);
    intFiller.appendItems(withArithPrev, null, true, arithExpr);

    List<CascadeBlankChild> withNullLitPrev = new ArrayList<>();
    intFiller.appendItems(withNullLitPrev, null, true, new NullLiteral());

    assertTrue("Arithmetic prev should produce more items than NullLiteral prev",
        withArithPrev.size() > withNullLitPrev.size());
  }

  // ---- appendItems: not-top does not add arithmetic items ----

  @Test
  public void integerAppendItems_notTop_noArithmeticItems() {
    List<CascadeBlankChild> topItems = new ArrayList<>();
    ArithmeticInfixExpression arithExpr = new ArithmeticInfixExpression(
        new IntegerLiteral(1),
        ArithmeticInfixExpression.Operator.PLUS,
        new IntegerLiteral(2),
        Integer.class);
    intFiller.appendItems(topItems, null, true, arithExpr);

    List<CascadeBlankChild> notTopItems = new ArrayList<>();
    intFiller.appendItems(notTopItems, null, false, arithExpr);

    assertTrue("Top should have more items than non-top with same arithmetic prev",
        topItems.size() > notTopItems.size());
  }

  // ---- IntegerFillerInner isAssignableTo ----

  @Test
  public void integerFillerInner_isAssignableToNumber() {
    assertTrue(intFiller.isAssignableTo(JavaType.getInstance(Number.class)));
  }

  @Test
  public void integerFillerInner_notAssignableToDouble() {
    assertFalse(intFiller.isAssignableTo(JavaType.getInstance(Double.class)));
  }

  // ---- DoubleFillerInner isAssignableTo ----

  @Test
  public void doubleFillerInner_isAssignableToNumber() {
    assertTrue(doubleFiller.isAssignableTo(JavaType.getInstance(Number.class)));
  }

  @Test
  public void doubleFillerInner_notAssignableToInteger() {
    assertFalse(doubleFiller.isAssignableTo(JavaType.getInstance(Integer.class)));
  }

}
