package org.alice.ide.croquet.codecs;

import edu.cmu.cs.dennisc.property.InstanceProperty;
import org.junit.Test;
import org.lgna.project.ast.*;

import static org.junit.Assert.*;

/**
 * Tests for {@link PropertyOfNodeCodec} — cached codec for InstanceProperty of AST nodes.
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class PropertyOfNodeCodecTest {

  // ---- getInstance factory ----

  @Test
  public void getInstance_returnsNonNull() {
    PropertyOfNodeCodec codec = PropertyOfNodeCodec.getInstance(ExpressionProperty.class);
    assertNotNull(codec);
  }

  @Test
  public void getInstance_sameClass_consistent() {
    PropertyOfNodeCodec c1 = PropertyOfNodeCodec.getInstance(ExpressionProperty.class);
    PropertyOfNodeCodec c2 = PropertyOfNodeCodec.getInstance(ExpressionProperty.class);
    assertNotNull(c1);
    assertNotNull(c2);
  }

  // ---- getValueClass ----

  @Test
  public void getValueClass_returnsExpressionProperty() {
    PropertyOfNodeCodec codec = PropertyOfNodeCodec.getInstance(ExpressionProperty.class);
    assertEquals(ExpressionProperty.class, codec.getValueClass());
  }

  // ---- ItemCodec interface compliance ----

  @Test
  public void implementsItemCodec() {
    PropertyOfNodeCodec codec = PropertyOfNodeCodec.getInstance(ExpressionProperty.class);
    assertTrue(codec instanceof org.lgna.croquet.ItemCodec);
  }

  // ---- multiple getInstance calls ----

  @Test
  public void getInstance_calledMultipleTimes_returnsNonNull() {
    for (int i = 0; i < 5; i++) {
      PropertyOfNodeCodec codec = PropertyOfNodeCodec.getInstance(ExpressionProperty.class);
      assertNotNull("Call " + i, codec);
    }
  }

  @Test
  public void getValueClass_consistent() {
    PropertyOfNodeCodec c1 = PropertyOfNodeCodec.getInstance(ExpressionProperty.class);
    PropertyOfNodeCodec c2 = PropertyOfNodeCodec.getInstance(ExpressionProperty.class);
    assertEquals(c1.getValueClass(), c2.getValueClass());
  }

  @Test
  public void getValueClass_isCorrectType() {
    PropertyOfNodeCodec codec = PropertyOfNodeCodec.getInstance(ExpressionProperty.class);
    Class cls = codec.getValueClass();
    assertNotNull(cls);
    assertEquals("ExpressionProperty", cls.getSimpleName());
  }
}
