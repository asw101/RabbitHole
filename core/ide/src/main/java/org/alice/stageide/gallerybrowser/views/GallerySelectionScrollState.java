package org.alice.stageide.gallerybrowser.views;

import edu.cmu.cs.dennisc.java.util.Maps;
import org.alice.stageide.modelresource.ResourceNode;

import java.util.Map;

final class GallerySelectionScrollState {
  private final Map<ResourceNode, Integer> mapNodeToHorizontalScrollPosition = Maps.newHashMap();

  int rememberAndGetNextPosition(ResourceNode previousValue, ResourceNode nextValue, int currentScrollPosition) {
    this.mapNodeToHorizontalScrollPosition.put(previousValue, currentScrollPosition);
    Integer nextScrollPosition = this.mapNodeToHorizontalScrollPosition.get(nextValue);
    return nextScrollPosition != null ? nextScrollPosition : 0;
  }
}
