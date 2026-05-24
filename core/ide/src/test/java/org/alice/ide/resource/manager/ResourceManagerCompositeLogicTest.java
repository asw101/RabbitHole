package org.alice.ide.resource.manager;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.*;

public class ResourceManagerCompositeLogicTest {
  @Test
  public void computeListenerDeltaTracksAddedAndRemovedResources() {
    ResourceManagerCompositeLogic.ListenerDelta<String> delta = ResourceManagerCompositeLogic.computeListenerDelta(Arrays.asList("a", "b"), Arrays.asList("b", "c"));

    assertEquals(Collections.singletonList("c"), delta.getAddedItems());
    assertEquals(Collections.singletonList("a"), delta.getRemovedItems());
  }

  @Test
  public void createSelectionStateDisablesEverythingWithoutSelection() {
    ResourceManagerCompositeLogic.SelectionState state = ResourceManagerCompositeLogic.createSelectionState(false, false, "select a resource", "resource is referenced");

    assertFalse(state.isRenameEnabled());
    assertEquals("select a resource", state.getRenameToolTipText());
    assertFalse(state.isReloadEnabled());
    assertEquals("select a resource", state.getReloadToolTipText());
    assertFalse(state.isRemoveEnabled());
    assertEquals("select a resource", state.getRemoveToolTipText());
  }

  @Test
  public void createSelectionStatePreventsRemovingReferencedResources() {
    ResourceManagerCompositeLogic.SelectionState state = ResourceManagerCompositeLogic.createSelectionState(true, true, "select a resource", "resource is referenced");

    assertTrue(state.isRenameEnabled());
    assertNull(state.getRenameToolTipText());
    assertTrue(state.isReloadEnabled());
    assertNull(state.getReloadToolTipText());
    assertFalse(state.isRemoveEnabled());
    assertEquals("resource is referenced", state.getRemoveToolTipText());
  }

  @Test
  public void createSelectionStateAllowsActionsForUnreferencedSelection() {
    ResourceManagerCompositeLogic.SelectionState state = ResourceManagerCompositeLogic.createSelectionState(true, false, "select a resource", "resource is referenced");

    assertTrue(state.isRenameEnabled());
    assertTrue(state.isReloadEnabled());
    assertTrue(state.isRemoveEnabled());
    assertNull(state.getRemoveToolTipText());
  }
}
