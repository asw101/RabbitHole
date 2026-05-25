package org.alice.stageide.perspectives;

import org.junit.Test;

import static org.junit.Assert.*;

public class SetupScenePerspectiveTest {
  @Test
  public void constructorStoresProjectDocumentFrameAndMenuBar() {
    SetupScenePerspective perspective = new SetupScenePerspective(null, null);

    assertNull(perspective.getProjectDocumentFrame());
    assertNull(perspective.getMenuBarComposite());
  }

  @Test
  public void setupScenePerspectiveHasNoFocusedCodeDropReceptor() {
    SetupScenePerspective perspective = new SetupScenePerspective(null, null);

    assertNull(perspective.getCodeDropReceptorInFocus());
  }

  @Test
  public void toolbarCompositeIsNullWhenToolbarPreferenceIsHidden() {
    SetupScenePerspective perspective = new SetupScenePerspective(null, null);

    assertNull(perspective.getToolBarComposite());
  }
}
