package org.alice.stageide.sceneeditor.views;

import java.awt.Point;
import java.awt.Rectangle;

final class InstanceFactorySelectionPanelLayoutLogic {
  static final class OverflowLayout {
    private final int firstOverflowIndex;
    private final int selectedIndex;
    private final int overflowControlIndex;
    private final Point selectedLocation;
    private final Point overflowControlLocation;

    private OverflowLayout(int firstOverflowIndex, int selectedIndex, int overflowControlIndex,
                           Point selectedLocation, Point overflowControlLocation) {
      this.firstOverflowIndex = firstOverflowIndex;
      this.selectedIndex = selectedIndex;
      this.overflowControlIndex = overflowControlIndex;
      this.selectedLocation = selectedLocation;
      this.overflowControlLocation = overflowControlLocation;
    }

    int getSelectedIndex() {
      return this.selectedIndex;
    }

    Point getSelectedLocation() {
      return this.selectedLocation == null ? null : new Point(this.selectedLocation);
    }

    Point getOverflowControlLocation() {
      return this.overflowControlLocation == null ? null : new Point(this.overflowControlLocation);
    }

    boolean shouldHideOverflowControl() {
      return this.firstOverflowIndex == -1;
    }

    boolean shouldCollapse(int componentIndex) {
      return (this.firstOverflowIndex > 0)
          && (componentIndex >= (this.firstOverflowIndex - 1))
          && (componentIndex < this.overflowControlIndex)
          && (componentIndex != this.selectedIndex);
    }
  }

  private InstanceFactorySelectionPanelLayoutLogic() {
    throw new AssertionError();
  }

  static int getIndentedX(int xOffset, int componentIndex, int indent) {
    return componentIndex > 0 ? xOffset + indent : xOffset;
  }

  static OverflowLayout analyzeOverflow(Rectangle[] bounds, boolean[] selectedStates, int containerHeight, int bottomInset) {
    int overflowControlIndex = bounds.length - 1;
    int firstOverflowIndex = -1;
    int selectedIndex = -1;
    for (int i = 0; i < overflowControlIndex; i++) {
      Rectangle boundsI = bounds[i];
      if ((firstOverflowIndex == -1) && ((boundsI.y + boundsI.height) >= (containerHeight - bottomInset))) {
        firstOverflowIndex = i;
      }
      if (selectedStates[i]) {
        selectedIndex = i;
      }
    }
    if (firstOverflowIndex <= 0) {
      return new OverflowLayout(firstOverflowIndex, selectedIndex, overflowControlIndex, null, null);
    }

    Point anchorLocation = bounds[firstOverflowIndex - 1].getLocation();
    Point selectedLocation = null;
    Point overflowControlLocation = new Point(anchorLocation);
    if (selectedIndex >= (firstOverflowIndex - 1)) {
      selectedLocation = new Point(anchorLocation);
      Rectangle selectedBounds = bounds[selectedIndex];
      overflowControlLocation.translate(selectedBounds.width, 0);
    }
    return new OverflowLayout(firstOverflowIndex, selectedIndex, overflowControlIndex, selectedLocation, overflowControlLocation);
  }
}
