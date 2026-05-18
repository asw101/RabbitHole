package org.alice.ide.instancefactory;

import org.alice.ide.instancefactory.croquet.ParametersVariablesAndConstantsSeparator;
import org.junit.Test;

import static org.junit.Assert.*;

public class ParametersVariablesAndConstantsSeparatorTest {
  @Test
  public void construction_doesNotThrow() {
    ParametersVariablesAndConstantsSeparator sep = new ParametersVariablesAndConstantsSeparator();
    assertNotNull(sep);
  }
}
