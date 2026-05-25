package org.alice.ide.resource.manager;

import org.alice.ide.testing.ProjectContextFixture;
import org.junit.Test;
import org.lgna.common.Resource;

import javax.swing.table.TableModel;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import static org.junit.Assert.*;

public class ResourceCatalogBehaviorTest {
  @Test
  public void reloadTableModelBuildsCatalogRowsForProjectResources() {
    ProjectContextFixture fixture = ProjectContextFixture.create();
    ResourceSingleSelectTableRowState state = new ResourceSingleSelectTableRowState();

    state.reloadTableModel(fixture.project);

    assertEquals(2, state.getItemCount());
    Set<Resource> resources = new LinkedHashSet<>();
    for (int i = 0; i < state.getItemCount(); i++) {
      resources.add(state.getItemAt(i));
    }
    assertEquals(new LinkedHashSet<>(Arrays.asList(fixture.audioResource, fixture.imageResource)), resources);

    TableModel tableModel = state.getSwingModel().getTableModel();
    Set<String> names = new LinkedHashSet<>();
    Set<Class<?>> types = new LinkedHashSet<>();
    for (int row = 0; row < state.getItemCount(); row++) {
      Resource resource = (Resource) tableModel.getValueAt(row, ResourceSingleSelectTableRowState.NAME_COLUMN_INDEX);
      names.add(resource.getName());
      types.add((Class<?>) tableModel.getValueAt(row, ResourceSingleSelectTableRowState.TYPE_COLUMN_INDEX));
      assertEquals(Boolean.FALSE, tableModel.getValueAt(row, ResourceSingleSelectTableRowState.IS_REFERENCED_COLUMN_INDEX));
    }

    assertEquals(new LinkedHashSet<>(Arrays.asList("context.wav", "context.png")), names);
    assertEquals(new LinkedHashSet<>(Arrays.asList(fixture.audioResource.getClass(), fixture.imageResource.getClass())), types);
  }

  @Test
  public void reloadTableModelWithNullProjectClearsCatalog() {
    ProjectContextFixture fixture = ProjectContextFixture.create();
    ResourceSingleSelectTableRowState state = new ResourceSingleSelectTableRowState();
    state.reloadTableModel(fixture.project);

    state.reloadTableModel(null);

    assertEquals(0, state.getItemCount());
    assertTrue(state.getItems().isEmpty());
  }
}
