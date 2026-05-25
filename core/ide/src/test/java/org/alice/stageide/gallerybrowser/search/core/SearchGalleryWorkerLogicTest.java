package org.alice.stageide.gallerybrowser.search.core;

import org.junit.Test;

import static org.junit.Assert.*;

public class SearchGalleryWorkerLogicTest {
  @Test
  public void createTermsSplitsOnPunctuationAndUnderscores() {
    assertArrayEquals(new String[] {"red", "dragon", "wing"}, SearchGalleryWorkerLogic.createTerms("Red-dragon_wing"));
  }

  @Test
  public void createTermsPreservesCurrentEmptyFilterBehavior() {
    assertArrayEquals(new String[] {""}, SearchGalleryWorkerLogic.createTerms(""));
  }

  @Test
  public void allTermsMatchUsesTagsAndSearchTextTogether() {
    assertTrue(SearchGalleryWorkerLogic.allTermsMatch(new String[] {"blue", "wing"}, new String[] {"Blue Dragon"}, "wide wing span"));
    assertFalse(SearchGalleryWorkerLogic.allTermsMatch(new String[] {"blue", "tail"}, new String[] {"Blue Dragon"}, "wide wing span"));
  }

  @Test
  public void allTermsMatchFallsBackToCaseInsensitiveSearchTextWhenTagsMissing() {
    assertTrue(SearchGalleryWorkerLogic.allTermsMatch(new String[] {"forest", "fox"}, null, "Forest Fox statue"));
    assertFalse(SearchGalleryWorkerLogic.allTermsMatch(new String[] {"forest", "owl"}, null, "Forest Fox statue"));
  }

  @Test
  public void allTermsMatchAllowsDifferentTermsToComeFromTagsAndSearchText() {
    assertTrue(SearchGalleryWorkerLogic.allTermsMatch(new String[] {"magic", "rabbit", "wand"}, new String[] {"Rabbit"}, "magic wand"));
    assertFalse(SearchGalleryWorkerLogic.allTermsMatch(new String[] {"magic", "rabbit", "hat"}, new String[] {"Rabbit"}, "magic wand"));
  }
}
