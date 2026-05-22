package org.alice.ide.cascade.fillerinners;

import org.alice.ide.croquet.models.ast.cascade.resource.ImageResourceExpressionFillIn;
import org.alice.ide.testing.ProjectContextTestCase;
import org.junit.Test;
import org.lgna.croquet.CascadeBlankChild;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class ImageResourceFillerInnerProjectContextTest extends ProjectContextTestCase {
  @Test
  public void appendItemsIncludesLoadedImageResource() {
    List<CascadeBlankChild> items = new ArrayList<>();

    new ImageResourceFillerInner().appendItems(items, null, true, null);

    assertTrue(items.toString(), items.contains(ImageResourceExpressionFillIn.getInstance(fixture.imageResource)));
  }
}
