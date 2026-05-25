package org.alice.stageide.gallerybrowser.uri;

import org.junit.Test;

import static org.junit.Assert.*;

public class UriGalleryDragModelDeepBehaviorTest {
  @Test
  public void appendStartIfNecessaryOnlyPrefixesMarkupOnce() {
    StringBuilder sb = new StringBuilder();

    UriGalleryDragModelLogic.appendStartIfNecessary(sb, "dragon.a3c");
    UriGalleryDragModelLogic.appendStartIfNecessary(sb, "dragon.a3c");

    assertEquals("<html>add from file: <strong>dragon.a3c</strong><p><p>", sb.toString());
  }

  @Test
  public void buildTypeSummaryToolTipTextReturnsUnknownForMissingSummary() {
    assertEquals("unknown", UriGalleryDragModelLogic.buildTypeSummaryToolTipText(null, "dragon.a3c"));
  }

  @Test
  public void createLocalizedTextUsesFallbackWhenTypeAlreadyMatchesImportName() {
    assertEquals("new ???",
        UriGalleryDragModelLogic.createLocalizedText("Dragon", null, false, "new ???", "Dragon.a3c", "Dragon", "from Dragon.a3c"));
  }
}
