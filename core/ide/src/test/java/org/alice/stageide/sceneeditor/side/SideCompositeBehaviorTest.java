package org.alice.stageide.sceneeditor.side;

import org.alice.interact.handle.HandleStyle;
import org.junit.Test;
import org.lgna.croquet.BooleanState;

import javax.swing.Action;

import static org.junit.Assert.*;

public class SideCompositeBehaviorTest {

  @Test
  public void defaultsToDefaultHandleStyleWithSnapAndJointsDisabled() {
    SideComposite composite = new SideComposite();

    assertEquals(HandleStyle.DEFAULT, composite.getHandleStyleState().getValue());
    assertFalse(composite.getIsSnapEnabledState().getValue());
    assertFalse(composite.getAreJointsShowingState().getValue());
  }

  @Test
  public void defaultHandleStyleSelectionMarksOnlyDefaultItemSelected() {
    SideComposite composite = new SideComposite();
    BooleanState defaultSelected = composite.getHandleStyleState().getItemSelectedState(HandleStyle.DEFAULT);

    assertTrue(defaultSelected.getValue());
    for (HandleStyle handleStyle : HandleStyle.values()) {
      if (handleStyle != HandleStyle.DEFAULT) {
        assertFalse(composite.getHandleStyleState().getItemSelectedState(handleStyle).getValue());
      }
    }
    assertSame(defaultSelected, composite.getHandleStyleState().getItemSelectedState(HandleStyle.DEFAULT));
  }

  @Test
  public void initializeIfNecessaryAssignsHandleIconsAndTooltips() {
    SideComposite composite = new SideComposite();

    composite.initializeIfNecessary();

    for (HandleStyle handleStyle : HandleStyle.values()) {
      BooleanState selectedState = composite.getHandleStyleState().getItemSelectedState(handleStyle);
      Object toolTip = selectedState.getImp().getSwingModel().getAction().getValue(Action.SHORT_DESCRIPTION);

      assertSame(handleStyle.getIcon(), selectedState.getIconFor(true));
      assertSame(handleStyle.getIcon(), selectedState.getIconFor(false));
      assertEquals(handleStyle.getToolTipText(), toolTip);
    }
  }

  @Test
  public void gettersReturnStableSubComposites() {
    SideComposite composite = new SideComposite();

    assertSame(composite.getSnapDetailsToolPaletteCoreComposite(), composite.getSnapDetailsToolPaletteCoreComposite());
    assertSame(composite.getObjectPropertiesTab(), composite.getObjectPropertiesTab());
    assertSame(composite.getObjectMarkersTab(), composite.getObjectMarkersTab());
    assertSame(composite.getCameraMarkersTab(), composite.getCameraMarkersTab());
  }
}
