package org.alice.ide.croquet.edits.ast;

import org.junit.Test;
import org.lgna.croquet.*;
import org.lgna.croquet.edits.AbstractEdit;
import org.lgna.project.ast.*;

import java.lang.reflect.Field;

import static org.junit.Assert.*;

public class AddExpressionEditTest {
  private static Object readField(Object instance, String fieldName) {
    try {
      Field field = instance.getClass().getDeclaredField(fieldName);
      field.setAccessible(true);
      return field.get(instance);
    } catch (ReflectiveOperationException roe) {
      throw new AssertionError(roe);
    }
  }

  private static ArrayInstanceCreation createArray(Expression... expressions) {
    return new ArrayInstanceCreation(Object[].class, new Integer[]{expressions.length}, expressions);
  }

  @Test
  public void construct_withNullUserActivity_succeeds() {
    AddExpressionEdit edit = new AddExpressionEdit(null, createArray().expressions, new StringLiteral("hello"));
    assertNotNull(edit);
  }

  @Test
  public void construct_preservesExpressionListPropertyAndExpression() {
    ArrayInstanceCreation array = createArray();
    Expression expression = new StringLiteral("hello");
    AddExpressionEdit edit = new AddExpressionEdit(null, array.expressions, expression);

    assertSame(array.expressions, readField(edit, "expressionListProperty"));
    assertSame(expression, readField(edit, "expression"));
  }

  @Test
  public void construct_withNullExpressionListProperty_preservesNull() {
    AddExpressionEdit edit = new AddExpressionEdit(null, null, new StringLiteral("hello"));

    assertNull(readField(edit, "expressionListProperty"));
  }

  @Test
  public void construct_withNullExpression_preservesNull() {
    AddExpressionEdit edit = new AddExpressionEdit(null, createArray().expressions, null);

    assertNull(readField(edit, "expression"));
  }

  @Test
  public void construct_extendsAbstractEdit() {
    AddExpressionEdit edit = new AddExpressionEdit(null, createArray().expressions, new StringLiteral("hello"));

    assertTrue(edit instanceof AbstractEdit);
  }

  @Test
  public void doOrRedoInternal_whenDo_appendsExpression() {
    ArrayInstanceCreation array = createArray(new IntegerLiteral(1));
    Expression addedExpression = new IntegerLiteral(2);
    AddExpressionEdit edit = new AddExpressionEdit(null, array.expressions, addedExpression);

    edit.doOrRedoInternal(true);

    assertEquals(2, array.expressions.size());
    assertSame(addedExpression, array.expressions.get(1));
  }

  @Test
  public void doOrRedoInternal_preservesExistingExpressions() {
    Expression existingExpression = new IntegerLiteral(1);
    ArrayInstanceCreation array = createArray(existingExpression);
    Expression addedExpression = new IntegerLiteral(2);
    AddExpressionEdit edit = new AddExpressionEdit(null, array.expressions, addedExpression);

    edit.doOrRedoInternal(true);

    assertSame(existingExpression, array.expressions.get(0));
    assertSame(addedExpression, array.expressions.get(1));
  }

  @Test
  public void undoInternal_afterDo_removesAddedExpression() {
    ArrayInstanceCreation array = createArray(new IntegerLiteral(1));
    AddExpressionEdit edit = new AddExpressionEdit(null, array.expressions, new IntegerLiteral(2));

    edit.doOrRedoInternal(true);
    edit.undoInternal();

    assertEquals(1, array.expressions.size());
    assertTrue(array.expressions.get(0) instanceof IntegerLiteral);
  }

  @Test
  public void doUndoRedo_cycle_restoresExpressionAtOriginalIndex() {
    ArrayInstanceCreation array = createArray(new IntegerLiteral(1));
    Expression addedExpression = new IntegerLiteral(2);
    AddExpressionEdit edit = new AddExpressionEdit(null, array.expressions, addedExpression);

    edit.doOrRedoInternal(true);
    edit.undoInternal();
    edit.doOrRedoInternal(false);

    assertEquals(2, array.expressions.size());
    assertSame(addedExpression, array.expressions.get(1));
  }

  @Test
  public void doOrRedoInternal_whenRedoWithoutUndo_appendsAtCurrentEnd() {
    ArrayInstanceCreation array = createArray(new IntegerLiteral(1));
    Expression firstAddedExpression = new IntegerLiteral(2);
    Expression secondAddedExpression = new IntegerLiteral(3);
    AddExpressionEdit firstEdit = new AddExpressionEdit(null, array.expressions, firstAddedExpression);
    AddExpressionEdit secondEdit = new AddExpressionEdit(null, array.expressions, secondAddedExpression);

    firstEdit.doOrRedoInternal(true);
    secondEdit.doOrRedoInternal(false);

    assertEquals(3, array.expressions.size());
    assertSame(firstAddedExpression, array.expressions.get(1));
    assertSame(secondAddedExpression, array.expressions.get(2));
  }

  @Test
  public void appendDescription_viaTerseDescription_startsWithAddPrefix() {
    AddExpressionEdit edit = new AddExpressionEdit(null, createArray().expressions, new StringLiteral("hello"));

    assertTrue(edit.getTerseDescription().startsWith("add: "));
  }

  @Test
  public void getRedoPresentation_containsAddPrefix() {
    AddExpressionEdit edit = new AddExpressionEdit(null, createArray().expressions, new StringLiteral("hello"));

    assertTrue(edit.getRedoPresentation().startsWith("Redo:add: "));
  }

  @Test
  public void getDetailedDescription_containsClassName() {
    AddExpressionEdit edit = new AddExpressionEdit(null, createArray().expressions, new StringLiteral("hello"));

    assertTrue(edit.getDetailedDescription().contains(AddExpressionEdit.class.getName()));
  }
}
