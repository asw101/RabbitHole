package org.alice.ide.croquet.models.cascade;

import org.junit.Test;
import org.lgna.project.ast.FieldAccess;
import org.lgna.project.ast.LocalAccess;
import org.lgna.project.ast.ParameterAccess;
import org.lgna.project.ast.ThisExpression;
import org.lgna.project.ast.TypeExpression;
import org.lgna.project.ast.UserField;
import org.lgna.project.ast.UserLocal;
import org.lgna.project.ast.UserParameter;

import static org.junit.Assert.*;

public class ExpressionAccessFillInBehaviorTest {
  @Test
  public void thisExpressionFillInReusesTransientValueButCreatesFreshExpressions() {
    ThisExpressionFillIn fillIn = ThisExpressionFillIn.getInstance();

    assertSame(fillIn, ThisExpressionFillIn.getInstance());

    ThisExpression transientValue = fillIn.getTransientValue(null);
    ThisExpression createdValue = fillIn.createValue(null);

    assertSame(transientValue, fillIn.getTransientValue(null));
    assertNotSame(transientValue, createdValue);
  }

  @Test
  public void parameterAndLocalAccessFillInsCacheByDeclarationAndCreateFreshAccesses() {
    UserParameter parameter = new UserParameter("count", Integer.class);
    ParameterAccessFillIn parameterFillIn = ParameterAccessFillIn.getInstance(parameter);
    ParameterAccess transientParameterAccess = parameterFillIn.getTransientValue(null);
    ParameterAccess createdParameterAccess = parameterFillIn.createValue(null);

    assertSame(parameterFillIn, ParameterAccessFillIn.getInstance(parameter));
    assertSame(parameter, transientParameterAccess.parameter.getValue());
    assertSame(parameter, createdParameterAccess.parameter.getValue());
    assertNotSame(transientParameterAccess, createdParameterAccess);

    UserLocal local = new UserLocal("label", String.class, false);
    LocalAccessFillIn localFillIn = LocalAccessFillIn.getInstance(local);
    LocalAccess transientLocalAccess = localFillIn.getTransientValue(null);
    LocalAccess createdLocalAccess = localFillIn.createValue(null);

    assertSame(localFillIn, LocalAccessFillIn.getInstance(local));
    assertSame(local, transientLocalAccess.local.getValue());
    assertSame(local, createdLocalAccess.local.getValue());
    assertNotSame(transientLocalAccess, createdLocalAccess);
  }

  @Test
  public void fieldAccessFillInsPreserveFieldIdentityForThisAndStaticAccesses() throws NoSuchFieldException {
    UserField userField = new UserField("score", Integer.class, null);
    ThisFieldAccessFillIn thisFillIn = ThisFieldAccessFillIn.getInstance(userField);
    FieldAccess transientThisAccess = thisFillIn.getTransientValue(null);
    FieldAccess createdThisAccess = thisFillIn.createValue(null);

    assertSame(thisFillIn, ThisFieldAccessFillIn.getInstance(userField));
    assertSame(userField, transientThisAccess.field.getValue());
    assertSame(userField, createdThisAccess.field.getValue());
    assertNotSame(transientThisAccess, createdThisAccess);

    StaticFieldAccessFillIn staticFillIn = StaticFieldAccessFillIn.getInstance(System.class, "out");
    FieldAccess transientStaticAccess = staticFillIn.getTransientValue(null);
    FieldAccess createdStaticAccess = staticFillIn.createValue(null);

    assertSame(staticFillIn, StaticFieldAccessFillIn.getInstance(System.class.getField("out")));
    assertTrue(transientStaticAccess.expression.getValue() instanceof TypeExpression);
    assertTrue(createdStaticAccess.expression.getValue() instanceof TypeExpression);
    assertSame(transientStaticAccess.field.getValue(), createdStaticAccess.field.getValue());
    assertSame(((TypeExpression) transientStaticAccess.expression.getValue()).value.getValue(),
        ((TypeExpression) createdStaticAccess.expression.getValue()).value.getValue());
    assertNotSame(transientStaticAccess, createdStaticAccess);
  }
}
