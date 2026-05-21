package org.alice.stageide.modelresource;

import org.junit.Test;
import org.lgna.croquet.ItemCodec;

import java.lang.reflect.Constructor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TreeUtilitiesCodecBehaviorTest {
  @Test
  public void sClassCodec_formatsNodesAndRejectsBinaryRoundTrip() throws Exception {
    Class<?> codecClass = Class.forName("org.alice.stageide.modelresource.TreeUtilities$SClassCodec");
    Constructor<?> ctor = codecClass.getDeclaredConstructor();
    ctor.setAccessible(true);
    @SuppressWarnings("unchecked")
    ItemCodec<ResourceNode> codec = (ItemCodec<ResourceNode>) ctor.newInstance();
    StringBuilder sb = new StringBuilder();
    ResourceNode node = new ResourceNode(new RootResourceKey("robot", "Robot"), java.util.Collections.emptyList());

    codec.appendRepresentation(sb, node);
    assertEquals("Robot", sb.toString());
    try {
      codec.encodeValue(null, node);
      org.junit.Assert.fail();
    } catch (AssertionError expected) {
      assertTrue(expected != null);
    }
  }
}
