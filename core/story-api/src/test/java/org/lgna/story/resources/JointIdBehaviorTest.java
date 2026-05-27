package org.lgna.story.resources;

import org.junit.Test;
import org.lgna.project.annotations.Visibility;

import java.lang.reflect.Field;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

public class JointIdBehaviorTest {

  @Test
  public void publicStaticFinalFieldAndToStringUseContainingFieldName() {
    Field field = BipedResource.LEFT_HIP.getPublicStaticFinalFld();

    assertEquals("LEFT_HIP", field.getName());
    assertEquals("LEFT_HIP", BipedResource.LEFT_HIP.toString());
    assertSame(field, BipedResource.LEFT_HIP.getPublicStaticFinalFld());
  }

  @Test
  public void visibilityComesFromTheFieldTemplateAnnotation() {
    assertEquals(
        Visibility.COMPLETELY_HIDDEN,
        BipedResource.ROOT.getVisibility());
  }

  @Test
  public void hierarchyDepthTracksParentChains() {
    assertEquals(
        BipedResource.LEFT_KNEE.hierarchyDepth() + 1,
        BipedResource.LEFT_ANKLE.hierarchyDepth());
    assertEquals(
        BipedResource.LEFT_KNEE,
        BipedResource.LEFT_ANKLE.getParent());
    assertEquals(
        BipedResource.LEFT_HIP,
        BipedResource.LEFT_KNEE.getParent());
  }

  @Test
  public void descendantComparisonRecognizesAncestorsDescendantsAndPeers() {
    assertEquals(1, BipedResource.LEFT_HIP.descendantComparison(BipedResource.LEFT_ANKLE));
    assertEquals(-1, BipedResource.LEFT_ANKLE.descendantComparison(BipedResource.LEFT_HIP));
    assertEquals(0, BipedResource.LEFT_HIP.descendantComparison(BipedResource.RIGHT_HIP));
  }
}
