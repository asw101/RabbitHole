package org.alice.ide.cascade.fillerinners;

import org.lgna.croquet.CascadeBlankChild;
import org.lgna.croquet.CascadeLineSeparator;
import org.lgna.project.ast.*;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Deep tests for {@link BooleanFillerInner} — appendItems branches for
 * ConditionalInfixExpression, LogicalComplement, relational types, and base case.
 */
public class BooleanFillerInnerDeepTest {

  private BooleanFillerInner filler;

  @org.junit.Before
  public void setUp() {
    filler = new BooleanFillerInner();
  }

  // ---- appendItems: base case (isTop=false, no prevExpression) ----

  @Test
  public void appendItems_notTopNoPrev_addsTrueFalseLiterals() {
    List<CascadeBlankChild> items = new ArrayList<>();

    filler.appendItems(items, null, false, null);

    // Should contain at least: true literal, false literal, separator
    assertTrue("Should have items", items.size() >= 2);
  }

  @Test
  public void appendItems_notTopNoPrev_minimumItemCount() {
    List<CascadeBlankChild> items = new ArrayList<>();

    filler.appendItems(items, null, false, null);

    // true, false, separator = 3 items minimum
    assertEquals("Base case should have exactly 3 items (true, false, separator)", 3, items.size());
  }

  // ---- appendItems: isTop with prevExpression but no special type ----

  @Test
  public void appendItems_topWithPrevNullLiteral_addsMoreItems() {
    List<CascadeBlankChild> items = new ArrayList<>();
    NullLiteral prev = new NullLiteral();

    filler.appendItems(items, null, true, prev);

    // Should have more items than the base case due to:
    // true, false, separator, randomBoolean, separator,
    // logicalComplement items, separator, conditional items, separator,
    // relational menus
    assertTrue("Top with prevExpression should add many items", items.size() > 3);
  }

  // ---- appendItems: isTop with ConditionalInfixExpression prevExpression ----

  @Test
  public void appendItems_topWithConditionalPrev_addsReplaceOperatorItems() {
    List<CascadeBlankChild> items = new ArrayList<>();

    // Create a ConditionalInfixExpression as prev expression
    ConditionalInfixExpression condExpr = new ConditionalInfixExpression(
        new BooleanLiteral(true),
        ConditionalInfixExpression.Operator.AND,
        new BooleanLiteral(false));

    filler.appendItems(items, null, true, condExpr);

    // The conditional branch adds ReplaceOperator FillIns (one per non-matching operator),
    // separator, reduce-to-left, reduce-to-right, separator, then continues with normal items.
    // ConditionalInfixExpression.Operator has AND and OR, so 1 replace + reduce items added.
    assertTrue("Conditional prev should add replace-operator and reduce items", items.size() > 10);
  }

  // ---- appendItems: isTop with LogicalComplement prevExpression ----

  @Test
  public void appendItems_topWithLogicalComplementPrev_addsReduceToInner() {
    List<CascadeBlankChild> items = new ArrayList<>();

    LogicalComplement logComp = new LogicalComplement(new BooleanLiteral(true));

    filler.appendItems(items, null, true, logComp);

    // Should add ReduceToInnerOperandInPreviousLogicalComplementFillIn and a separator,
    // plus all the normal top-with-prev items
    assertTrue("LogicalComplement prev should add reduce-to-inner items", items.size() > 5);
  }

  // ---- addRelationalType affects appendItems ----

  @Test
  public void appendItems_afterAddRelationalType_addsRelationalObjectMenu() {
    filler.addRelationalType(JavaType.DOUBLE_OBJECT_TYPE);

    List<CascadeBlankChild> items = new ArrayList<>();
    filler.appendItems(items, null, true, new NullLiteral());

    // Count items — with a relational type added, there should be a
    // RelationalObjectCascadeMenu in the output
    List<CascadeBlankChild> withoutRelational = new ArrayList<>();
    BooleanFillerInner fillerNoRel = new BooleanFillerInner();
    fillerNoRel.appendItems(withoutRelational, null, true, new NullLiteral());

    assertTrue("Adding relational type should produce more items",
        items.size() > withoutRelational.size());
  }

  @Test
  public void appendItems_multipleRelationalTypes_addsMultipleMenus() {
    filler.addRelationalType(JavaType.DOUBLE_OBJECT_TYPE);
    filler.addRelationalType(JavaType.INTEGER_OBJECT_TYPE);

    List<CascadeBlankChild> items = new ArrayList<>();
    filler.appendItems(items, null, true, new NullLiteral());

    List<CascadeBlankChild> oneRelational = new ArrayList<>();
    BooleanFillerInner fillerOneRel = new BooleanFillerInner();
    fillerOneRel.addRelationalType(JavaType.DOUBLE_OBJECT_TYPE);
    fillerOneRel.appendItems(oneRelational, null, true, new NullLiteral());

    assertTrue("Two relational types should produce more items than one",
        items.size() > oneRelational.size());
  }

  // ---- isAssignableTo type checks ----

  @Test
  public void isAssignableTo_booleanPrimitive_returnsTrue() {
    assertTrue(filler.isAssignableTo(JavaType.BOOLEAN_OBJECT_TYPE));
  }

  @Test
  public void isAssignableTo_objectType_returnsTrue() {
    assertTrue(filler.isAssignableTo(JavaType.OBJECT_TYPE));
  }

  @Test
  public void isAssignableTo_integerType_returnsFalse() {
    assertFalse(filler.isAssignableTo(JavaType.INTEGER_OBJECT_TYPE));
  }

  // ---- appendItems: isTop=false with prevExpression skips top-only items ----

  @Test
  public void appendItems_notTopWithPrev_onlyAddsTrueFalse() {
    List<CascadeBlankChild> items = new ArrayList<>();

    filler.appendItems(items, null, false, new NullLiteral());

    // Not top → no random, no logical complement, no conditional, no relational
    // Just: true, false, separator
    assertEquals("Non-top should only have true, false, separator", 3, items.size());
  }

  // ---- separator count validation ----

  @Test
  public void appendItems_baseCase_containsExactlyOneSeparator() {
    List<CascadeBlankChild> items = new ArrayList<>();

    filler.appendItems(items, null, false, null);

    long separatorCount = items.stream()
        .filter(item -> item instanceof CascadeLineSeparator)
        .count();
    assertEquals("Base case should have exactly 1 separator", 1, separatorCount);
  }
}
