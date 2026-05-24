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
}
