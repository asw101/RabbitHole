package org.alice.ide.instancefactory.croquet;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class InstanceFactoryStateLogicBehaviorTest {
  @Test
  public void buildParametersVariablesConstantsTextKeepsRequestedSectionOrder() {
    assertEquals("code parameters, variables, constants",
        InstanceFactoryStateLogic.buildParametersVariablesConstantsText("code", true, true, true));
  }

  @Test
  public void buildParametersVariablesConstantsTextOmitsUnrequestedSectionsWithoutExtraCommas() {
    assertEquals("code variables",
        InstanceFactoryStateLogic.buildParametersVariablesConstantsText("code", false, true, false));
  }

  @Test
  public void buildParametersVariablesConstantsTextRetainsCurrentTrailingSpaceWhenNothingIsAvailable() {
    assertEquals("code ",
        InstanceFactoryStateLogic.buildParametersVariablesConstantsText("code", false, false, false));
  }
}
