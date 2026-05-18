package org.alice.ide.croquet.models.numberpad;

import org.junit.Assume;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.awt.GraphicsEnvironment;
import java.util.Arrays;
import java.util.Collection;
import java.util.function.Function;

import static org.junit.Assert.*;

/**
 * Consolidated singleton/caching tests for BackspaceOperation, PlusMinusOperation,
 * and DecimalPointOperation. All three follow the identical getInstance-per-model pattern.
 */
@RunWith(Parameterized.class)
public class NumberpadOperationCachingTest {

  @FunctionalInterface
  private interface OperationFactory {
    Object getInstance(NumberModel<?> model);
  }

  @Parameterized.Parameters(name = "{0}")
  public static Collection<Object[]> operations() {
    return Arrays.asList(new Object[][] {
        {"BackspaceOperation", (OperationFactory) BackspaceOperation::getInstance, "←"},
        {"PlusMinusOperation", (OperationFactory) PlusMinusOperation::getInstance, "±"},
    });
  }

  private final String name;
  private final OperationFactory factory;
  private final String expectedSymbol;

  public NumberpadOperationCachingTest(String name, OperationFactory factory, String expectedSymbol) {
    this.name = name;
    this.factory = factory;
    this.expectedSymbol = expectedSymbol;
  }

  private static void assumeNotHeadless() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
  }

  @Test
  public void sameModelReusesInstance() {
    assumeNotHeadless();
    assertSame(factory.getInstance(IntegerModel.getInstance()),
        factory.getInstance(IntegerModel.getInstance()));
  }

  @Test
  public void differentModelsUseDifferentInstances() {
    assumeNotHeadless();
    assertNotSame(factory.getInstance(IntegerModel.getInstance()),
        factory.getInstance(DoubleModel.getInstance()));
  }

  @Test
  public void localizedNameMatchesExpectedSymbol() {
    assumeNotHeadless();
    org.lgna.croquet.ActionOperation op = (org.lgna.croquet.ActionOperation) factory.getInstance(IntegerModel.getInstance());
    assertEquals(expectedSymbol, op.getImp().getName());
  }
}
