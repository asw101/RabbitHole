package org.alice.stageide.perspectives;

import org.junit.Test;

import static org.junit.Assert.*;

public class CodePerspectiveTest {
  @Test
  public void constructorStoresProjectDocumentFrame() {
    CodePerspective perspective = new CodePerspective(null, null);

    assertNull(perspective.getProjectDocumentFrame());
  }

  @Test
  public void constructorStoresMenuBarComposite() {
    CodePerspective perspective = new CodePerspective(null, null);

    assertNull(perspective.getMenuBarComposite());
  }

  @Test
  public void getToolBarCompositeReturnsNullWhenToolbarIsHidden() {
    CodePerspective perspective = new CodePerspective(null, null);

    assertNull(perspective.getToolBarComposite());
  }
}
