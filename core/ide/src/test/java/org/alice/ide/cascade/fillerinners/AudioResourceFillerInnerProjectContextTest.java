package org.alice.ide.cascade.fillerinners;

import org.alice.ide.croquet.models.ast.cascade.resource.AudioResourceExpressionFillIn;
import org.alice.ide.testing.ProjectContextTestCase;
import org.junit.Test;
import org.lgna.croquet.CascadeBlankChild;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class AudioResourceFillerInnerProjectContextTest extends ProjectContextTestCase {
  @Test
  public void appendItemsIncludesLoadedAudioResource() {
    List<CascadeBlankChild> items = new ArrayList<>();

    new AudioResourceFillerInner().appendItems(items, null, true, null);

    assertTrue(items.toString(), items.contains(AudioResourceExpressionFillIn.getInstance(fixture.audioResource)));
  }
}
