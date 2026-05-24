package org.alice.ide.declarationseditor;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

final class DeclarationTabStateLogic {
  private DeclarationTabStateLogic() {
    throw new AssertionError();
  }

  static <T, K> List<T> buildOrderedItems(List<T> existingItems, T selectedItem, Function<T, K> typeExtractor, Predicate<T> isTypeComposite, Function<K, T> typeCompositeFactory) {
    Map<K, List<T>> groupedItems = new LinkedHashMap<>();
    List<T> orphans = new LinkedList<>();
    List<T> items = new LinkedList<>(existingItems);
    items.add(selectedItem);

    for (T item : items) {
      if (item != null) {
        K type = typeExtractor.apply(item);
        if (type != null) {
          List<T> grouped = groupedItems.computeIfAbsent(type, unused -> new LinkedList<>());
          if (isTypeComposite.test(item)) {
            grouped.add(0, item);
          } else {
            grouped.add(item);
          }
        } else {
          orphans.add(item);
        }
      }
    }

    List<T> orderedItems = new LinkedList<>();
    boolean isSeparatorDesired = false;
    for (Map.Entry<K, List<T>> entry : groupedItems.entrySet()) {
      if (isSeparatorDesired) {
        orderedItems.add(null);
      }
      T typeComposite = typeCompositeFactory.apply(entry.getKey());
      if (!entry.getValue().contains(typeComposite)) {
        orderedItems.add(typeComposite);
      }
      orderedItems.addAll(entry.getValue());
      isSeparatorDesired = true;
    }

    if (!orphans.isEmpty()) {
      orderedItems.add(null);
      orderedItems.addAll(orphans);
    }
    return orderedItems;
  }
}
