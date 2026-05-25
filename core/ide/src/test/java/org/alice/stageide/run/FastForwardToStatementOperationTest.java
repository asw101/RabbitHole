package org.alice.stageide.run;

import org.junit.Test;
import org.lgna.project.ast.ExpressionStatement;
import org.lgna.project.ast.NullLiteral;
import org.lgna.project.ast.Statement;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

public class FastForwardToStatementOperationTest {
  private static Statement createStatement() {
    return new ExpressionStatement(new NullLiteral());
  }

  @Test
  public void constructorStoresTargetStatement() throws Exception {
    Statement statement = createStatement();
    FastForwardToStatementOperation operation = new FastForwardToStatementOperation(statement);

    Field field = FastForwardToStatementOperation.class.getDeclaredField("statement");
    field.setAccessible(true);

    assertSame(statement, field.get(operation));
  }

  @Test
  public void postWithoutPreLeavesRunProgramContextNull() throws Exception {
    FastForwardToStatementOperation operation = new FastForwardToStatementOperation(createStatement());

    operation.post();

    Field field = FastForwardToStatementOperation.class.getDeclaredField("runProgramContext");
    field.setAccessible(true);
    assertNull(field.get(operation));
  }

  @Test
  public void getUpToDateProgramTypeReturnsNullWithoutActiveIde() throws Exception {
    Method method = RunComposite.class.getDeclaredMethod("getUpToDateProgramTypeFromActiveIde");
    method.setAccessible(true);

    assertNull(method.invoke(null));
  }
}
