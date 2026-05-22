package org.alice.ide.resource.manager;

import org.junit.Test;

import static org.junit.Assert.*;

public class ResourceSingleSelectTableRowStateTest {

  @Test
  public void columnIndexConstants_matchExpectedOrder() {
    assertEquals(0, ResourceSingleSelectTableRowState.NAME_COLUMN_INDEX);
    assertEquals(1, ResourceSingleSelectTableRowState.TYPE_COLUMN_INDEX);
    assertEquals(2, ResourceSingleSelectTableRowState.IS_REFERENCED_COLUMN_INDEX);
  }

  @Test
  public void newState_startsEmpty() {
    ResourceSingleSelectTableRowState state = new ResourceSingleSelectTableRowState();
    assertEquals(0, state.getItemCount());
  }

  @Test
  public void reloadTableModel_withNullProject_keepsStateEmpty() {
    ResourceSingleSelectTableRowState state = new ResourceSingleSelectTableRowState();
    state.reloadTableModel(null);
    assertEquals(0, state.getItemCount());
  }

  @Test
  public void getItems_isEmptyForFreshState() {
    ResourceSingleSelectTableRowState state = new ResourceSingleSelectTableRowState();
    assertTrue(state.getItems().isEmpty());
  }

  @Test
  public void createTable_returnsNonNullTableView() {
    ResourceSingleSelectTableRowState state = new ResourceSingleSelectTableRowState();
    assertNotNull(state.createTable());
  }
}
