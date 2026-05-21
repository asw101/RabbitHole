package org.alice.ide.typemanager;

import org.junit.Test;
import org.lgna.project.ast.Expression;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.NamedUserConstructor;
import org.lgna.project.ast.NamedUserType;
import org.lgna.project.ast.NullLiteral;

import static org.junit.Assert.assertEquals;

public class TypeManagerCreateTypeWithExpressionsTest {
  public static class CustomStoryType {
    public CustomStoryType(String value) {
    }
  }

  @Test
  public void createTypeFor_usesExplicitArgumentExpressionsInsteadOfParameters() throws Exception {
    NamedUserType type = TypeManagerTestSupport.invokeCreateTypeFor(JavaType.getInstance(CustomStoryType.class), "ExplicitArgsType", null, new Expression[]{new NullLiteral()});
    NamedUserConstructor constructor = type.constructors.get(0);
    assertEquals(0, constructor.requiredParameters.size());
    assertEquals(1, constructor.body.getValue().constructorInvocationStatement.getValue().requiredArguments.size());
  }
}
