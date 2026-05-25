package org.alice.ide.instancefactory.croquet;

import org.junit.Test;

import static org.junit.Assert.*;

public class InstanceFactoryStateDeepBehaviorTest {
  @Test
  public void buildParametersVariablesConstantsTextSupportsOnlyParameters() {
    assertEquals("doStuff parameters", InstanceFactoryStateLogic.buildParametersVariablesConstantsText("doStuff", true, false, false));
  }

  @Test
  public void buildParametersVariablesConstantsTextSupportsVariablesWithoutParameters() {
    assertEquals("doStuff variables", InstanceFactoryStateLogic.buildParametersVariablesConstantsText("doStuff", false, true, false));
  }
}
