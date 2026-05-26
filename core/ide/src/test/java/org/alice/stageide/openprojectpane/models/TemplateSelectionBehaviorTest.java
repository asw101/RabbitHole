package org.alice.stageide.openprojectpane.models;

import org.alice.ide.projecturi.ProjectSnapshot;
import org.alice.nonfree.NebulousIde;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TemplateSelectionBehaviorTest {
  @Test
  public void grassTemplateSnapshotRoundTripsSchemeFragmentAndLocalizedName() {
    ProjectSnapshot snapshot = TemplateUriState.Template.GRASS.getProjectSnapshot();

    assertEquals(TemplateUriState.BLANK_SCHEME, snapshot.getUri().getScheme());
    assertEquals(TemplateUriState.Template.GRASS.name(), snapshot.getUri().getFragment());
    assertEquals(TemplateUriState.Template.GRASS, snapshot.getUriFragment());
    assertEquals(TemplateUriState.getLocalizedName("GRASS"), snapshot.getText());
  }

  @Test
  public void templateStateExposesSelectableTemplatesInDeclarationOrder() {
    TemplateUriState state = TemplateUriState.getInstance();
    int expectedIndex = 0;

    for (TemplateUriState.Template template : TemplateUriState.Template.values()) {
      if (!NebulousIde.nonfree.isNonFreeEnabled() && template == TemplateUriState.Template.ROOM) {
        continue;
      }
      assertEquals(template, state.getItemAt(expectedIndex).getUriFragment());
      expectedIndex++;
    }

    assertEquals(expectedIndex, state.getItemCount());
  }
}
