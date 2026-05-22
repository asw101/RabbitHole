package org.alice.ide.cascade.fillerinners;

import org.junit.Before;
import org.junit.Test;
import org.lgna.croquet.CascadeBlankChild;
import org.lgna.croquet.CascadeLineSeparator;
import org.lgna.project.ast.BooleanLiteral;
import org.lgna.project.ast.ConditionalInfixExpression;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.LogicalComplement;
import org.lgna.project.ast.NullLiteral;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class BooleanFillerInnerComprehensiveTest {

  private BooleanFillerInner filler;

  @Before
  public void setUp() {
    filler = new BooleanFillerInner();
  }

  @Test
  public void booleanFillerInner_isPublicConcreteSubclass() {
    assertTrue(Modifier.isPublic(BooleanFillerInner.class.getModifiers()));
    assertFalse(Modifier.isAbstract(BooleanFillerInner.class.getModifiers()));
    assertTrue(ExpressionFillerInner.class.isAssignableFrom(BooleanFillerInner.class));
  }

  @Test
  public void booleanFillerInner_declaresPrivateFinalRelationalTypesField() throws Exception {
    Field field = BooleanFillerInner.class.getDeclaredField("relationalTypes");
    assertTrue(Modifier.isPrivate(field.getModifiers()));
    assertTrue(Modifier.isFinal(field.getModifiers()));
  }

  @Test
  public void booleanFillerInner_isAssignableToBooleanType() {
    assertTrue(filler.isAssignableTo(JavaType.BOOLEAN_OBJECT_TYPE));
  }

  @Test
  public void booleanFillerInner_isAssignableToObjectType() {
    assertTrue(filler.isAssignableTo(JavaType.OBJECT_TYPE));
  }

  @Test
  public void booleanFillerInner_isNotAssignableToIntegerType() {
    assertFalse(filler.isAssignableTo(JavaType.INTEGER_OBJECT_TYPE));
  }

  @Test
  public void addRelationalType_acceptsAdditionalType() {
    filler.addRelationalType(JavaType.INTEGER_OBJECT_TYPE);
    assertNotNull(filler);
  }

  @Test
  public void appendItems_nonTopNoPreviousExpressionAddsBaseItemsOnly() {
    List<CascadeBlankChild> items = new ArrayList<>();
    filler.appendItems(items, null, false, null);
    assertEquals(3, items.size());
  }

  @Test
  public void appendItems_nonTopNoPreviousExpressionHasSingleSeparator() {
    List<CascadeBlankChild> items = new ArrayList<>();
    filler.appendItems(items, null, false, null);
    assertEquals(1, separatorCount(items));
  }

  @Test
  public void appendItems_nonTopWithPreviousExpressionStillUsesBaseItemsOnly() {
    List<CascadeBlankChild> items = new ArrayList<>();
    filler.appendItems(items, null, false, new NullLiteral());
    assertEquals(3, items.size());
  }

  @Test
  public void appendItems_topWithPreviousExpressionAddsMoreThanBaseCase() {
    List<CascadeBlankChild> base = new ArrayList<>();
    List<CascadeBlankChild> enriched = new ArrayList<>();
    filler.appendItems(base, null, false, null);
    filler.appendItems(enriched, null, true, new NullLiteral());
    assertTrue(enriched.size() > base.size());
  }

  @Test
  public void appendItems_topWithPreviousExpressionAddsMultipleSeparators() {
    List<CascadeBlankChild> items = new ArrayList<>();
    filler.appendItems(items, null, true, new NullLiteral());
    assertTrue(separatorCount(items) >= 3);
  }

  @Test
  public void appendItems_conditionalPreviousExpressionAddsOperatorReplacementBranch() {
    List<CascadeBlankChild> items = new ArrayList<>();
    ConditionalInfixExpression previous = new ConditionalInfixExpression(
        new BooleanLiteral(true),
        ConditionalInfixExpression.Operator.AND,
        new BooleanLiteral(false));
    filler.appendItems(items, null, true, previous);
    assertTrue(items.size() > 10);
  }

  @Test
  public void appendItems_logicalComplementPreviousExpressionAddsInnerReductionBranch() {
    List<CascadeBlankChild> items = new ArrayList<>();
    filler.appendItems(items, null, true, new LogicalComplement(new BooleanLiteral(true)));
    assertTrue(items.size() > 5);
  }

  @Test
  public void conditionalPreviousExpressionProducesMoreItemsThanPlainPreviousExpression() {
    List<CascadeBlankChild> plain = new ArrayList<>();
    List<CascadeBlankChild> conditional = new ArrayList<>();
    filler.appendItems(plain, null, true, new NullLiteral());
    filler.appendItems(conditional, null, true,
        new ConditionalInfixExpression(new BooleanLiteral(true), ConditionalInfixExpression.Operator.OR, new BooleanLiteral(false)));
    assertTrue(conditional.size() > plain.size());
  }

  @Test
  public void logicalComplementPreviousExpressionProducesMoreItemsThanPlainPreviousExpression() {
    List<CascadeBlankChild> plain = new ArrayList<>();
    List<CascadeBlankChild> logical = new ArrayList<>();
    filler.appendItems(plain, null, true, new NullLiteral());
    filler.appendItems(logical, null, true, new LogicalComplement(new BooleanLiteral(true)));
    assertTrue(logical.size() > plain.size());
  }

  @Test
  public void addedRelationalTypeIncreasesItemCountForTopPreviousExpression() {
    List<CascadeBlankChild> plain = new ArrayList<>();
    List<CascadeBlankChild> enriched = new ArrayList<>();
    filler.appendItems(plain, null, true, new NullLiteral());
    filler.addRelationalType(JavaType.DOUBLE_OBJECT_TYPE);
    filler.appendItems(enriched, null, true, new NullLiteral());
    assertTrue(enriched.size() > plain.size());
  }

  @Test
  public void twoRelationalTypesIncreaseItemCountMoreThanOneRelationalType() {
    BooleanFillerInner oneType = new BooleanFillerInner();
    BooleanFillerInner twoTypes = new BooleanFillerInner();
    oneType.addRelationalType(JavaType.DOUBLE_OBJECT_TYPE);
    twoTypes.addRelationalType(JavaType.DOUBLE_OBJECT_TYPE);
    twoTypes.addRelationalType(JavaType.INTEGER_OBJECT_TYPE);

    List<CascadeBlankChild> oneItems = new ArrayList<>();
    List<CascadeBlankChild> twoItems = new ArrayList<>();
    oneType.appendItems(oneItems, null, true, new NullLiteral());
    twoTypes.appendItems(twoItems, null, true, new NullLiteral());
    assertTrue(twoItems.size() > oneItems.size());
  }

  @Test
  public void appendItems_neverProducesNullEntries() {
    List<CascadeBlankChild> items = new ArrayList<>();
    filler.appendItems(items, null, true, new NullLiteral());
    for (CascadeBlankChild item : items) {
      assertNotNull(item);
    }
  }

  @Test
  public void appendItems_baseCaseStartsWithLiteralEntries() {
    List<CascadeBlankChild> items = new ArrayList<>();
    filler.appendItems(items, null, false, null);
    assertFalse(items.get(0) instanceof CascadeLineSeparator);
    assertFalse(items.get(1) instanceof CascadeLineSeparator);
    assertTrue(items.get(2) instanceof CascadeLineSeparator);
  }

  @Test
  public void booleanFillerInner_declaresOnlyRelationalTypesField() {
    assertEquals(1, BooleanFillerInner.class.getDeclaredFields().length);
  }

  @Test
  public void booleanFillerInner_packageNameMatchesSource() {
    assertEquals("org.alice.ide.cascade.fillerinners", BooleanFillerInner.class.getPackage().getName());
  }

  @Test
  public void booleanFillerInner_simpleNameMatchesSource() {
    assertEquals("BooleanFillerInner", BooleanFillerInner.class.getSimpleName());
  }

  private static long separatorCount(List<CascadeBlankChild> items) {
    return items.stream().filter(CascadeLineSeparator.class::isInstance).count();
  }

  @Test
  public void relationalTypesAlsoIncreaseLogicalComplementBranchItemCount() {
    List<CascadeBlankChild> plain = new ArrayList<>();
    List<CascadeBlankChild> enriched = new ArrayList<>();
    filler.appendItems(plain, null, true, new LogicalComplement(new BooleanLiteral(true)));
    filler.addRelationalType(JavaType.DOUBLE_OBJECT_TYPE);
    filler.appendItems(enriched, null, true, new LogicalComplement(new BooleanLiteral(true)));
    assertTrue(enriched.size() > plain.size());
  }

}
