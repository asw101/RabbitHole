package org.alice.ide.croquet.edits.ast;

import org.junit.Test;
import org.lgna.croquet.*;
import org.lgna.croquet.edits.AbstractEdit;
import org.lgna.project.ast.*;

import java.lang.reflect.Field;

import static org.junit.Assert.*;

public class FillInExpressionListPropertyEditTest {

  private static final class DescribableFillInExpressionListPropertyEdit extends FillInExpressionListPropertyEdit {
    private DescribableFillInExpressionListPropertyEdit(Expression prevExpression, Expression nextExpression) {
      super(null, prevExpression, nextExpression);
    }

    private String describeForTest() {
      StringBuilder sb = new StringBuilder();
      this.appendDescription(sb, DescriptionStyle.TERSE);
      return sb.toString();
    }
  }

  @Test
  public void construct_withNullUserActivity_succeeds() {
    FillInExpressionListPropertyEdit edit = new FillInExpressionListPropertyEdit(null, new StringLiteral("before"), new StringLiteral("after"));
    assertNotNull(edit);
  }

  @Test
  public void construct_withPrevExpression_storesPreviousInstance() throws Exception {
    Expression prevExpression = new StringLiteral("before");
    FillInExpressionListPropertyEdit edit = new FillInExpressionListPropertyEdit(null, prevExpression, new StringLiteral("after"));
    assertSame(prevExpression, readField(edit, "prevExpression"));
  }

  @Test
  public void construct_withNextExpression_storesNextInstance() throws Exception {
    Expression nextExpression = new DoubleLiteral(2.5);
    FillInExpressionListPropertyEdit edit = new FillInExpressionListPropertyEdit(null, new DoubleLiteral(1.5), nextExpression);
    assertSame(nextExpression, readField(edit, "nextExpression"));
  }

  @Test
  public void construct_withNullPrevExpression_preservesNull() throws Exception {
    FillInExpressionListPropertyEdit edit = new FillInExpressionListPropertyEdit(null, null, new StringLiteral("after"));
    assertNull(readField(edit, "prevExpression"));
  }

  @Test
  public void construct_withNullNextExpression_preservesNull() throws Exception {
    FillInExpressionListPropertyEdit edit = new FillInExpressionListPropertyEdit(null, new StringLiteral("before"), null);
    assertNull(readField(edit, "nextExpression"));
  }

  @Test
  public void construct_withDifferentExpressionTypes_keepsEachIdentity() throws Exception {
    Expression prevExpression = new IntegerLiteral(7);
    Expression nextExpression = new BooleanLiteral(true);
    FillInExpressionListPropertyEdit edit = new FillInExpressionListPropertyEdit(null, prevExpression, nextExpression);

    assertSame(prevExpression, readField(edit, "prevExpression"));
    assertSame(nextExpression, readField(edit, "nextExpression"));
  }

  @Test
  public void construct_withSharedExpression_storesSameReferenceInBothFields() throws Exception {
    Expression sharedExpression = new StringLiteral("shared");
    FillInExpressionListPropertyEdit edit = new FillInExpressionListPropertyEdit(null, sharedExpression, sharedExpression);

    assertSame(sharedExpression, readField(edit, "prevExpression"));
    assertSame(sharedExpression, readField(edit, "nextExpression"));
  }

  @Test
  public void appendDescription_withLiterals_containsTransitionMarker() {
    DescribableFillInExpressionListPropertyEdit edit = new DescribableFillInExpressionListPropertyEdit(new StringLiteral("before"), new StringLiteral("after"));
    String description = edit.describeForTest();
    assertTrue(description.startsWith("set:"));
    assertTrue(description.contains("===>"));
  }

  @Test
  public void appendDescription_withLiterals_mentionsBothExpressions() {
    DescribableFillInExpressionListPropertyEdit edit = new DescribableFillInExpressionListPropertyEdit(new StringLiteral("left"), new StringLiteral("right"));
    String description = edit.describeForTest();
    assertTrue(description.contains("left"));
    assertTrue(description.contains("right"));
  }

  @Test
  public void appendDescription_withNullExpressions_includesNullText() {
    DescribableFillInExpressionListPropertyEdit edit = new DescribableFillInExpressionListPropertyEdit(null, null);
    String description = edit.describeForTest();
    assertTrue(description.startsWith("set:"));
    assertTrue(description.contains("null"));
  }

  @Test
  public void multipleInstances_afterConstruction_keepSeparateState() throws Exception {
    FillInExpressionListPropertyEdit first = new FillInExpressionListPropertyEdit(null, new StringLiteral("a"), new StringLiteral("b"));
    FillInExpressionListPropertyEdit second = new FillInExpressionListPropertyEdit(null, new IntegerLiteral(1), new IntegerLiteral(2));

    assertNotSame(readField(first, "prevExpression"), readField(second, "prevExpression"));
    assertNotSame(readField(first, "nextExpression"), readField(second, "nextExpression"));
  }

  @Test
  public void extendsAbstractEdit_afterConstruction_returnsTrue() {
    FillInExpressionListPropertyEdit edit = new FillInExpressionListPropertyEdit(null, new NullLiteral(), new IntegerLiteral(3));
    assertTrue(edit instanceof AbstractEdit);
  }

  private Object readField(Object target, String fieldName) throws Exception {
    Class<?> type = target.getClass();
    while (type != null) {
      try {
        Field field = type.getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(target);
      } catch (NoSuchFieldException nsfe) {
        type = type.getSuperclass();
      }
    }
    throw new NoSuchFieldException(fieldName);
  }
}
