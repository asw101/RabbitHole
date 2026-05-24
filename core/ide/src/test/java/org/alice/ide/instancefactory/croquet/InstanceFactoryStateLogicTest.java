package org.alice.ide.instancefactory.croquet;

import org.junit.Test;

import static org.junit.Assert.*;

public class InstanceFactoryStateLogicTest {
  @Test
  public void buildParametersVariablesConstantsTextIncludesRequestedGroups() {
    assertEquals("doStuff parameters, variables, constants", InstanceFactoryStateLogic.buildParametersVariablesConstantsText("doStuff", true, true, true));
  }

  @Test
  public void buildParametersVariablesConstantsTextSkipsMissingGroups() {
    assertEquals("doStuff constants", InstanceFactoryStateLogic.buildParametersVariablesConstantsText("doStuff", false, false, true));
  }
}
