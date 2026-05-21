package org.alice.ide.croquet.edits.ast;

import org.junit.Test;
import org.lgna.croquet.*;
import org.lgna.croquet.edits.AbstractEdit;
import org.lgna.croquet.history.UserActivity;
import org.lgna.project.ast.*;

import java.lang.reflect.Field;

import static org.junit.Assert.*;

public class FillInMoreEditTest {

  @Test
  public void construct_withNullUserActivity_succeeds() {
    FillInMoreEdit edit = new FillInMoreEdit((UserActivity) null, new StringLiteral("detail"));
    assertNotNull(edit);
  }

  @Test
  public void construct_withArgumentExpression_storesProvidedInstance() throws Exception {
    Expression argumentExpression = new StringLiteral("detail");
    FillInMoreEdit edit = new FillInMoreEdit((UserActivity) null, argumentExpression);
    assertSame(argumentExpression, readField(edit, "argumentExpression"));
  }

  @Test
  public void construct_withNullArgumentExpression_preservesNull() throws Exception {
    FillInMoreEdit edit = new FillInMoreEdit((UserActivity) null, null);
    assertNull(readField(edit, "argumentExpression"));
  }

  @Test
  public void construct_withStringLiteral_preservesIdentity() throws Exception {
    StringLiteral argumentExpression = new StringLiteral("text");
    FillInMoreEdit edit = new FillInMoreEdit((UserActivity) null, argumentExpression);
    assertSame(argumentExpression, readField(edit, "argumentExpression"));
  }

  @Test
  public void construct_withDoubleLiteral_preservesIdentity() throws Exception {
    DoubleLiteral argumentExpression = new DoubleLiteral(4.5);
    FillInMoreEdit edit = new FillInMoreEdit((UserActivity) null, argumentExpression);
    assertSame(argumentExpression, readField(edit, "argumentExpression"));
  }

  @Test
  public void construct_withNullLiteral_preservesIdentity() throws Exception {
    NullLiteral argumentExpression = new NullLiteral();
    FillInMoreEdit edit = new FillInMoreEdit((UserActivity) null, argumentExpression);
    assertSame(argumentExpression, readField(edit, "argumentExpression"));
  }

  @Test
  public void construct_withBooleanLiteral_preservesIdentity() throws Exception {
    BooleanLiteral argumentExpression = new BooleanLiteral(true);
    FillInMoreEdit edit = new FillInMoreEdit((UserActivity) null, argumentExpression);
    assertSame(argumentExpression, readField(edit, "argumentExpression"));
  }

  @Test
  public void construct_withMethodInvocation_preservesIdentity() throws Exception {
    MethodInvocation argumentExpression = new MethodInvocation(new StringLiteral("hello"), JavaMethod.getInstance(String.class, "trim"));
    FillInMoreEdit edit = new FillInMoreEdit((UserActivity) null, argumentExpression);
    assertSame(argumentExpression, readField(edit, "argumentExpression"));
  }

  @Test
  public void multipleInstances_withDifferentExpressions_keepSeparateState() throws Exception {
    FillInMoreEdit first = new FillInMoreEdit((UserActivity) null, new StringLiteral("first"));
    FillInMoreEdit second = new FillInMoreEdit((UserActivity) null, new IntegerLiteral(2));

    assertNotSame(readField(first, "argumentExpression"), readField(second, "argumentExpression"));
  }

  @Test
  public void construct_withIntegerLiteral_preservesIdentity() throws Exception {
    IntegerLiteral argumentExpression = new IntegerLiteral(9);
    FillInMoreEdit edit = new FillInMoreEdit((UserActivity) null, argumentExpression);
    assertSame(argumentExpression, readField(edit, "argumentExpression"));
  }

  @Test
  public void storedExpression_afterMutation_remainsSameObjectReference() throws Exception {
    StringLiteral argumentExpression = new StringLiteral("before");
    FillInMoreEdit edit = new FillInMoreEdit((UserActivity) null, argumentExpression);
    argumentExpression.value.setValue("after");

    assertSame(argumentExpression, readField(edit, "argumentExpression"));
    assertEquals("after", ((StringLiteral) readField(edit, "argumentExpression")).value.getValue());
  }

  @Test
  public void sharedExpression_acrossTwoEdits_retainsSameReferenceInEachEdit() throws Exception {
    Expression sharedExpression = new IntegerLiteral(8);
    FillInMoreEdit first = new FillInMoreEdit((UserActivity) null, sharedExpression);
    FillInMoreEdit second = new FillInMoreEdit((UserActivity) null, sharedExpression);

    assertSame(sharedExpression, readField(first, "argumentExpression"));
    assertSame(sharedExpression, readField(second, "argumentExpression"));
  }

  @Test
  public void extendsAbstractEdit_afterConstruction_returnsTrue() {
    FillInMoreEdit edit = new FillInMoreEdit((UserActivity) null, new IntegerLiteral(3));
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
