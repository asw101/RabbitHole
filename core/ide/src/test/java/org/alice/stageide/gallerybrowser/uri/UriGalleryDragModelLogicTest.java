package org.alice.stageide.gallerybrowser.uri;

import org.alice.ide.ast.export.type.FieldInfo;
import org.alice.ide.ast.export.type.FunctionInfo;
import org.alice.ide.ast.export.type.TypeSummary;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class UriGalleryDragModelLogicTest {
  @Test
  public void buildTypeSummaryToolTipTextFormatsProceduresFunctionsAndProperties() {
    TypeSummary summary = new TypeSummary(3.1, "Dragon", List.of("org.example.Dragon"), null,
        List.of("flap"), List.of(new FunctionInfo("Number", "speed")), List.of(new FieldInfo("Color", "paint")));

    String tooltip = UriGalleryDragModelLogic.buildTypeSummaryToolTipText(summary, "dragon.a3c");

    assertTrue(tooltip.contains("add from file: <strong>dragon.a3c</strong>"));
    assertTrue(tooltip.contains("<em>procedures:</em>"));
    assertTrue(tooltip.contains("<strong>flap</strong>"));
    assertTrue(tooltip.contains("Number <strong>speed</strong>"));
    assertTrue(tooltip.contains("Color <strong>paint</strong>"));
    assertTrue(tooltip.endsWith("</html>"));
  }

  @Test
  public void buildTypeSummaryToolTipTextFallsBackWhenNothingInterestingExists() {
    TypeSummary summary = new TypeSummary(3.1, "Empty", List.of(), null, List.of(), List.of(), List.of());

    assertEquals("<html>nothing of note</html>", UriGalleryDragModelLogic.buildTypeSummaryToolTipText(summary, null));
  }

  @Test
  public void createLocalizedTextPrefersTypeNameForInterfaceResources() {
    String text = UriGalleryDragModelLogic.createLocalizedText("Dragon", "new Dragon", true,
        "new ???", null, null, "from dragon.a3c");

    assertEquals("Dragon", text);
  }

  @Test
  public void createLocalizedTextAppendsImportSourceWhenBasenameDiffers() {
    String text = UriGalleryDragModelLogic.createLocalizedText("Dragon", "new Dragon", false,
        "new ???", "dragon-copy.a3c", "dragon-copy", "from dragon-copy.a3c");

    assertEquals("<html>new Dragon <em>from dragon-copy.a3c</em></html>", text);
  }
}
