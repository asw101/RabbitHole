package org.alice.ide.cascade.fillerinners;

import org.alice.ide.custom.StringCustomExpressionCreatorComposite;
import org.junit.Before;
import org.junit.Test;
import org.lgna.croquet.CascadeBlankChild;
import org.lgna.croquet.CascadeLineSeparator;
import org.lgna.project.ast.NullLiteral;
import org.lgna.project.ast.StringLiteral;
import org.lgna.project.ast.JavaType;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.*;

public class StringFillerInnerComprehensiveTest {

  private StringFillerInner filler;

  @Before
  public void setUp() throws Exception {
    filler = new StringFillerInner();
    clearRecentValues();
  }

  @Test
  public void stringFillerInner_isPublicConcreteSubclass() {
    assertTrue(Modifier.isPublic(StringFillerInner.class.getModifiers()));
    assertFalse(Modifier.isAbstract(StringFillerInner.class.getModifiers()));
    assertTrue(ExpressionFillerInner.class.isAssignableFrom(StringFillerInner.class));
  }

  @Test
  public void getLiterals_returnsExpectedDefaultValue() {
    assertArrayEquals(new String[] {"hello"}, StringFillerInner.getLiterals());
  }

  @Test
  public void getLiterals_returnsFreshArrayEachCall() {
    assertNotSame(StringFillerInner.getLiterals(), StringFillerInner.getLiterals());
  }

  @Test
  public void stringFillerInner_isAssignableToStringType() {
    assertTrue(filler.isAssignableTo(JavaType.STRING_TYPE));
  }

  @Test
  public void stringFillerInner_isAssignableToObjectType() {
    assertTrue(filler.isAssignableTo(JavaType.OBJECT_TYPE));
  }

  @Test
  public void stringFillerInner_isNotAssignableToIntegerType() {
    assertFalse(filler.isAssignableTo(JavaType.INTEGER_OBJECT_TYPE));
  }

  @Test
  public void appendItems_withoutRecentValuesAddsLiteralSeparatorAndCreator() {
    List<CascadeBlankChild> items = new ArrayList<>();
    filler.appendItems(items, null, false, null);
    assertEquals(3, items.size());
  }

  @Test
  public void appendItems_withoutRecentValuesContainsExactlyOneSeparator() {
    List<CascadeBlankChild> items = new ArrayList<>();
    filler.appendItems(items, null, false, null);
    assertEquals(1, separatorCount(items));
  }

  @Test
  public void appendItems_withoutRecentValuesEndsWithCreatorFillIn() {
    List<CascadeBlankChild> items = new ArrayList<>();
    filler.appendItems(items, null, false, null);
    assertFalse(items.get(items.size() - 1) instanceof CascadeLineSeparator);
  }

  @Test
  public void appendItems_isIndependentOfIsTopFlag() {
    List<CascadeBlankChild> first = new ArrayList<>();
    List<CascadeBlankChild> second = new ArrayList<>();
    filler.appendItems(first, null, false, null);
    filler.appendItems(second, null, true, null);
    assertEquals(first.size(), second.size());
  }

  @Test
  public void appendItems_isIndependentOfPreviousExpression() {
    List<CascadeBlankChild> first = new ArrayList<>();
    List<CascadeBlankChild> second = new ArrayList<>();
    filler.appendItems(first, null, true, null);
    filler.appendItems(second, null, true, new NullLiteral());
    assertEquals(first.size(), second.size());
  }

  @Test
  public void appendItems_neverProducesNullEntries() {
    List<CascadeBlankChild> items = new ArrayList<>();
    filler.appendItems(items, null, true, new StringLiteral("x"));
    for (CascadeBlankChild item : items) {
      assertNotNull(item);
    }
  }

  @Test
  public void updateRecentValues_addsAdditionalFillInsBeyondBaseItems() {
    StringCustomExpressionCreatorComposite.getInstance().updateRecentValues("recent-value");
    List<CascadeBlankChild> items = new ArrayList<>();
    filler.appendItems(items, null, false, null);
    assertTrue(items.size() > 3);
  }

  @Test
  public void recentValues_addSecondSeparatorWhenNonLiteralRecentValueExists() {
    StringCustomExpressionCreatorComposite.getInstance().updateRecentValues("recent-value");
    List<CascadeBlankChild> items = new ArrayList<>();
    filler.appendItems(items, null, false, null);
    assertTrue(separatorCount(items) >= 2);
  }

  @Test
  public void duplicateLiteralRecentValue_doesNotAddExtraRecentEntry() {
    StringCustomExpressionCreatorComposite.getInstance().updateRecentValues("hello");
    List<CascadeBlankChild> items = new ArrayList<>();
    filler.appendItems(items, null, false, null);
    assertEquals(3, items.size());
  }

  @Test
  public void multipleRecentValues_areCappedByCompositeRules() {
    StringCustomExpressionCreatorComposite.getInstance().updateRecentValues("one");
    StringCustomExpressionCreatorComposite.getInstance().updateRecentValues("two");
    StringCustomExpressionCreatorComposite.getInstance().updateRecentValues("three");
    StringCustomExpressionCreatorComposite.getInstance().updateRecentValues("four");
    List<CascadeBlankChild> items = new ArrayList<>();
    filler.appendItems(items, null, false, null);
    assertTrue(items.size() >= 3);
  }

  @Test
  public void duplicateRecentValueIsMovedRatherThanDuplicated() throws Exception {
    StringCustomExpressionCreatorComposite composite = StringCustomExpressionCreatorComposite.getInstance();
    composite.updateRecentValues("repeat");
    composite.updateRecentValues("repeat");
    assertEquals(1, recentValues().size());
  }

  @Test
  public void stringFillerInner_declaresNoFieldsOfItsOwn() {
    assertEquals(0, StringFillerInner.class.getDeclaredFields().length);
  }

  @Test
  public void stringFillerInner_packageNameMatchesSource() {
    assertEquals("org.alice.ide.cascade.fillerinners", StringFillerInner.class.getPackage().getName());
  }

  @Test
  public void stringFillerInner_simpleNameMatchesSource() {
    assertEquals("StringFillerInner", StringFillerInner.class.getSimpleName());
  }

  @Test
  public void stringCustomExpressionCreatorSingleton_isAvailable() {
    assertNotNull(StringCustomExpressionCreatorComposite.getInstance());
  }

  @SuppressWarnings("unchecked")
  private static LinkedList<String> recentValues() throws Exception {
    Field field = org.alice.ide.custom.ExpressionWithRecentValuesCreatorComposite.class.getDeclaredField("recentValues");
    field.setAccessible(true);
    return (LinkedList<String>) field.get(StringCustomExpressionCreatorComposite.getInstance());
  }

  private static void clearRecentValues() throws Exception {
    recentValues().clear();
  }

  private static long separatorCount(List<CascadeBlankChild> items) {
    return items.stream().filter(CascadeLineSeparator.class::isInstance).count();
  }

  @Test
  public void clearRecentValues_restoresBaseItemCount() throws Exception {
    StringCustomExpressionCreatorComposite.getInstance().updateRecentValues("recent-value");
    clearRecentValues();
    List<CascadeBlankChild> items = new ArrayList<>();
    filler.appendItems(items, null, false, null);
    assertEquals(3, items.size());
  }

  @Test
  public void recentValuesField_onSuperclassIsProtectedFinal() throws Exception {
    Field field = org.alice.ide.custom.ExpressionWithRecentValuesCreatorComposite.class.getDeclaredField("recentValues");
    assertTrue(Modifier.isProtected(field.getModifiers()));
    assertTrue(Modifier.isFinal(field.getModifiers()));
  }

}
