package org.alice.ide.ast.export.type;

import org.junit.Test;
import java.lang.reflect.Modifier;
import static org.junit.Assert.*;

public class TypeSummaryTest {
  @Test
  public void isFinalClass() {
    assertTrue(Modifier.isFinal(TypeSummary.class.getModifiers()));
  }
  @Test
  public void currentVersionIsPositive() {
    assertTrue(TypeSummary.CURRENT_VERSION > 0);
  }
  @Test
  public void minimumAcceptableVersionIsPositive() {
    assertTrue(TypeSummary.MINIMUM_ACCEPTABLE_VERSION > 0);
  }
  @Test
  public void currentVersionNotLessThanMinimum() {
    assertTrue(TypeSummary.CURRENT_VERSION >= TypeSummary.MINIMUM_ACCEPTABLE_VERSION);
  }
  @Test
  public void currentVersionIs3Point1() {
    assertEquals(3.1, TypeSummary.CURRENT_VERSION, 0.001);
  }
}
