package org.alice.stageide.gallerybrowser.search.core;

final class SearchGalleryWorkerLogic {
  private SearchGalleryWorkerLogic() {
    throw new AssertionError();
  }

  static String[] createTerms(String filter) {
    return filter.toLowerCase().replaceAll("\\W|_", " ").split("\\s+");
  }

  static boolean allTermsMatch(String[] terms, String[] tags, String searchText) {
    for (String term : terms) {
      boolean termFound = false;
      if (tags != null) {
        for (String tag : tags) {
          if (tag.toLowerCase().contains(term)) {
            termFound = true;
            break;
          }
        }
      }
      if (!termFound && (searchText == null || searchText.isEmpty() || !searchText.toLowerCase().contains(term))) {
        return false;
      }
    }
    return true;
  }
}
