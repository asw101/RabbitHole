package org.alice.ide.croquet.models.cascade.literals;

import org.junit.Test;
import org.lgna.project.ast.BooleanLiteral;
import org.lgna.project.ast.DoubleLiteral;
import org.lgna.project.ast.IntegerLiteral;
import org.lgna.project.ast.NullLiteral;
import org.lgna.project.ast.StringLiteral;

import static org.junit.Assert.*;

public class LiteralFillInBehaviorTest {
  @Test
  public void booleanLiteralFillInsExposeCachedTransientValuesButCreateFreshLiterals() {
    BooleanLiteralFillIn trueFillIn = BooleanLiteralFillIn.getInstance(true);
    BooleanLiteralFillIn falseFillIn = BooleanLiteralFillIn.getInstance(false);

    assertSame(trueFillIn, BooleanLiteralFillIn.getInstance(true));
    assertSame(falseFillIn, BooleanLiteralFillIn.getInstance(false));

    BooleanLiteral transientTrue = trueFillIn.getTransientValue(null);
    BooleanLiteral createdTrue = trueFillIn.createValue(null);
    BooleanLiteral createdFalse = falseFillIn.createValue(null);

    assertTrue(transientTrue.value.getValue());
    assertTrue(createdTrue.value.getValue());
    assertFalse(createdFalse.value.getValue());
    assertNotSame(transientTrue, createdTrue);
  }

  @Test
  public void integerAndDoubleFillInsCopyTheirNumericValuesIntoNewExpressions() {
    IntegerLiteralFillIn integerFillIn = IntegerLiteralFillIn.getInstance(42);
    DoubleLiteralFillIn doubleFillIn = DoubleLiteralFillIn.getInstance(3.25);

    assertSame(integerFillIn, IntegerLiteralFillIn.getInstance(42));
    assertSame(doubleFillIn, DoubleLiteralFillIn.getInstance(3.25));
    assertNotSame(integerFillIn, IntegerLiteralFillIn.getInstance(7));
    assertNotSame(doubleFillIn, DoubleLiteralFillIn.getInstance(9.5));

    IntegerLiteral transientInteger = integerFillIn.getTransientValue(null);
    IntegerLiteral createdInteger = integerFillIn.createValue(null);
    DoubleLiteral transientDouble = doubleFillIn.getTransientValue(null);
    DoubleLiteral createdDouble = doubleFillIn.createValue(null);

    assertEquals(42, transientInteger.value.getValue().intValue());
    assertEquals(42, createdInteger.value.getValue().intValue());
    assertEquals(3.25, transientDouble.value.getValue(), 0.0);
    assertEquals(3.25, createdDouble.value.getValue(), 0.0);
    assertNotSame(transientInteger, createdInteger);
    assertNotSame(transientDouble, createdDouble);
  }

  @Test
  public void stringLiteralFillInCachesByTextButReturnsFreshCopiesWhenChosen() {
    StringLiteralFillIn fillIn = StringLiteralFillIn.getInstance("hello world");

    assertSame(fillIn, StringLiteralFillIn.getInstance("hello world"));
    assertNotSame(fillIn, StringLiteralFillIn.getInstance("goodbye"));

    StringLiteral transientLiteral = fillIn.getTransientValue(null);
    StringLiteral createdLiteral = fillIn.createValue(null);

    assertEquals("hello world", transientLiteral.value.getValue());
    assertEquals("hello world", createdLiteral.value.getValue());
    assertNotSame(transientLiteral, createdLiteral);
  }

  @Test
  public void nullLiteralFillInRemainsSingletonAndBuildsNewNullLiteralValues() {
    NullLiteralFillIn fillIn = NullLiteralFillIn.getInstance();

    assertSame(fillIn, NullLiteralFillIn.getInstance());

    NullLiteral transientLiteral = fillIn.getTransientValue(null);
    NullLiteral createdLiteral = fillIn.createValue(null);

    assertNotNull(transientLiteral);
    assertNotNull(createdLiteral);
    assertNotSame(transientLiteral, createdLiteral);
  }
}
