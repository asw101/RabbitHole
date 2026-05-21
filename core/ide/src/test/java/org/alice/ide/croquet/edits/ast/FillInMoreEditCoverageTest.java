package org.alice.ide.croquet.edits.ast;

import org.junit.Test;
import org.lgna.project.ast.JavaMethod;
import org.lgna.project.ast.MethodInvocation;
import org.lgna.project.ast.SimpleArgument;
import org.lgna.project.ast.StringLiteral;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.junit.Assert.*;

public class FillInMoreEditCoverageTest {
  @Test
  public void privateGetArgumentAt_returnsRequestedRequiredArgument() throws Exception {
    SimpleArgument first = new SimpleArgument(JavaMethod.getInstance(String.class, "substring", int.class, int.class).getRequiredParameters().get(0), new StringLiteral("1"));
    MethodInvocation invocation = new MethodInvocation(new StringLiteral("hello"),
        JavaMethod.getInstance(String.class, "substring", int.class, int.class), first);
    FillInMoreEdit edit = new FillInMoreEdit((org.lgna.croquet.history.UserActivity) null, new StringLiteral("tail"));
    Method method = FillInMoreEdit.class.getDeclaredMethod("getArgumentAt", MethodInvocation.class, int.class);
    method.setAccessible(true);
    assertSame(invocation.requiredArguments.get(0), method.invoke(edit, invocation, 0));
  }

  @Test
  public void constructor_storesArgumentExpressionIdentity() throws Exception {
    StringLiteral literal = new StringLiteral("detail");
    FillInMoreEdit edit = new FillInMoreEdit((org.lgna.croquet.history.UserActivity) null, literal);
    Field field = FillInMoreEdit.class.getDeclaredField("argumentExpression");
    field.setAccessible(true);
    assertSame(literal, field.get(edit));
  }

  @Test
  public void extendsAbstractEdit() {
    assertTrue(org.lgna.croquet.edits.AbstractEdit.class.isAssignableFrom(FillInMoreEdit.class));
  }
}
