package org.alice.ide.croquet.edits.ast;

import org.junit.Before;
import org.junit.Test;
import org.lgna.croquet.*;
import org.lgna.croquet.edits.AbstractEdit;
import org.lgna.project.ast.*;

import java.lang.reflect.Field;

import static org.junit.Assert.*;

public class ExpressionPropertyEditTest {
  private ExpressionProperty property;
  private Expression prevExpression;
  private Expression nextExpression;

  private static Object readField(Object instance, String fieldName) {
    try {
      Field field = instance.getClass().getDeclaredField(fieldName);
      field.setAccessible(true);
      return field.get(instance);
    } catch (ReflectiveOperationException roe) {
      throw new AssertionError(roe);
    }
  }

  @Before
  public void setUp() {
    prevExpression = new NullLiteral();
    nextExpression = new IntegerLiteral(42);
    property = new ExpressionStatement(prevExpression).expression;
  }

  @Test
  public void construct_withNullUserActivity_succeeds() {
    ExpressionPropertyEdit edit = new ExpressionPropertyEdit(null, property, prevExpression, nextExpression);
    assertNotNull(edit);
  }

  @Test
  public void construct_preservesExpressionPropertyAndExpressions() {
    ExpressionPropertyEdit edit = new ExpressionPropertyEdit(null, property, prevExpression, nextExpression);

    assertSame(property, readField(edit, "expressionProperty"));
    assertSame(prevExpression, readField(edit, "prevExpression"));
    assertSame(nextExpression, readField(edit, "nextExpression"));
  }

  @Test
  public void construct_withNullExpressionProperty_preservesNull() {
    ExpressionPropertyEdit edit = new ExpressionPropertyEdit(null, null, prevExpression, nextExpression);

    assertNull(readField(edit, "expressionProperty"));
  }

  @Test
  public void construct_withNullPrevExpression_preservesNull() {
    ExpressionPropertyEdit edit = new ExpressionPropertyEdit(null, property, null, nextExpression);

    assertNull(readField(edit, "prevExpression"));
  }

  @Test
  public void construct_withNullNextExpression_preservesNull() {
    ExpressionPropertyEdit edit = new ExpressionPropertyEdit(null, property, prevExpression, null);

    assertNull(readField(edit, "nextExpression"));
  }

  @Test
  public void construct_multipleInstances_keepFieldsIndependent() {
    ExpressionProperty firstProperty = new ExpressionStatement(new StringLiteral("first")).expression;
    ExpressionProperty secondProperty = new ExpressionStatement(new StringLiteral("second")).expression;
    ExpressionPropertyEdit firstEdit = new ExpressionPropertyEdit(null, firstProperty, new StringLiteral("a"), new StringLiteral("b"));
    ExpressionPropertyEdit secondEdit = new ExpressionPropertyEdit(null, secondProperty, new IntegerLiteral(1), new IntegerLiteral(2));

    assertSame(firstProperty, readField(firstEdit, "expressionProperty"));
    assertSame(secondProperty, readField(secondEdit, "expressionProperty"));
    assertNotSame(readField(firstEdit, "nextExpression"), readField(secondEdit, "nextExpression"));
  }

  @Test
  public void construct_extendsAbstractEdit() {
    ExpressionPropertyEdit edit = new ExpressionPropertyEdit(null, property, prevExpression, nextExpression);

    assertTrue(edit instanceof AbstractEdit);
  }

  @Test
  public void appendDescription_viaTerseDescription_containsSetPrefixAndArrow() {
    ExpressionPropertyEdit edit = new ExpressionPropertyEdit(null, property, prevExpression, nextExpression);
    String description = edit.getTerseDescription();

    assertTrue(description.startsWith("set: "));
    assertTrue(description.contains("===>"));
  }

  @Test
  public void getRedoPresentation_containsSetPrefix() {
    ExpressionPropertyEdit edit = new ExpressionPropertyEdit(null, property, prevExpression, nextExpression);

    assertTrue(edit.getRedoPresentation().startsWith("Redo:set: "));
  }

  @Test
  public void getUndoPresentation_containsSetPrefix() {
    ExpressionPropertyEdit edit = new ExpressionPropertyEdit(null, property, prevExpression, nextExpression);

    assertTrue(edit.getUndoPresentation().startsWith("Undo:set: "));
  }

  @Test
  public void getDetailedDescription_containsClassName() {
    ExpressionPropertyEdit edit = new ExpressionPropertyEdit(null, property, prevExpression, nextExpression);

    assertTrue(edit.getDetailedDescription().contains(ExpressionPropertyEdit.class.getName()));
  }

  @Test
  public void toString_matchesDetailedDescription() {
    ExpressionPropertyEdit edit = new ExpressionPropertyEdit(null, property, prevExpression, nextExpression);

    assertEquals(edit.getDetailedDescription(), edit.toString());
  }
}
