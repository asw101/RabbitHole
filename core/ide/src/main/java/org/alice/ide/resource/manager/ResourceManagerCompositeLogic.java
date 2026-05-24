package org.alice.ide.resource.manager;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

final class ResourceManagerCompositeLogic {
  static final class ListenerDelta<T> {
    private final List<T> addedItems;
    private final List<T> removedItems;

    ListenerDelta(List<T> addedItems, List<T> removedItems) {
      this.addedItems = addedItems;
      this.removedItems = removedItems;
    }

    List<T> getAddedItems() {
      return this.addedItems;
    }

    List<T> getRemovedItems() {
      return this.removedItems;
    }
  }

  static final class SelectionState {
    private final boolean renameEnabled;
    private final String renameToolTipText;
    private final boolean reloadEnabled;
    private final String reloadToolTipText;
    private final boolean removeEnabled;
    private final String removeToolTipText;

    SelectionState(boolean renameEnabled, String renameToolTipText, boolean reloadEnabled, String reloadToolTipText, boolean removeEnabled, String removeToolTipText) {
      this.renameEnabled = renameEnabled;
      this.renameToolTipText = renameToolTipText;
      this.reloadEnabled = reloadEnabled;
      this.reloadToolTipText = reloadToolTipText;
      this.removeEnabled = removeEnabled;
      this.removeToolTipText = removeToolTipText;
    }

    boolean isRenameEnabled() {
      return this.renameEnabled;
    }

    String getRenameToolTipText() {
      return this.renameToolTipText;
    }

    boolean isReloadEnabled() {
      return this.reloadEnabled;
    }

    String getReloadToolTipText() {
      return this.reloadToolTipText;
    }

    boolean isRemoveEnabled() {
      return this.removeEnabled;
    }

    String getRemoveToolTipText() {
      return this.removeToolTipText;
    }
  }

  private ResourceManagerCompositeLogic() {
    throw new AssertionError();
  }

  static <T> ListenerDelta<T> computeListenerDelta(Collection<T> previousItems, Collection<T> currentItems) {
    List<T> addedItems = new LinkedList<>();
    for (T item : currentItems) {
      if (!previousItems.contains(item)) {
        addedItems.add(item);
      }
    }

    List<T> removedItems = new LinkedList<>();
    for (T item : previousItems) {
      if (!currentItems.contains(item)) {
        removedItems.add(item);
      }
    }
    return new ListenerDelta<>(addedItems, removedItems);
  }

  static SelectionState createSelectionState(boolean isSelected, boolean isReferenced, String defaultToolTipText, String referencedToolTipText) {
    if (!isSelected) {
      return new SelectionState(false, defaultToolTipText, false, defaultToolTipText, false, defaultToolTipText);
    }
    String removeToolTipText = isReferenced ? referencedToolTipText : null;
    return new SelectionState(true, null, true, null, !isReferenced, removeToolTipText);
  }
}
