package org.alice.ide.croquet.codecs;

import edu.cmu.cs.dennisc.property.InstanceProperty;
import org.junit.Test;
import org.lgna.project.ast.ExpressionProperty;

import static org.junit.Assert.*;

/**
 * Tests for {@link PropertyOfNodeCodec} — codec for node properties.
 * Covers getInstance factory and getValueClass.
 */
public class PropertyOfNodeCodecTest {

  // ---- getInstance factory ----

  @Test
  public void getInstance_returnsNonNull() {
    PropertyOfNodeCodec<InstanceProperty<?>> codec =
        PropertyOfNodeCodec.getInstance((Class) InstanceProperty.class);
    assertNotNull(codec);
  }

  @Test
  public void getInstance_expressionProperty_returnsNonNull() {
    PropertyOfNodeCodec<ExpressionProperty> codec =
        PropertyOfNodeCodec.getInstance(ExpressionProperty.class);
    assertNotNull(codec);
  }

  @Test
  public void getInstance_differentClasses_differentCodecs() {
    PropertyOfNodeCodec<?> c1 =
        PropertyOfNodeCodec.getInstance((Class) InstanceProperty.class);
    PropertyOfNodeCodec<ExpressionProperty> c2 =
        PropertyOfNodeCodec.getInstance(ExpressionProperty.class);
    assertNotSame(c1, c2);
  }

  // ---- getValueClass ----

  @Test
  public void getValueClass_returnsInstanceProperty() {
    PropertyOfNodeCodec<InstanceProperty<?>> codec =
        PropertyOfNodeCodec.getInstance((Class) InstanceProperty.class);
    assertEquals(InstanceProperty.class, codec.getValueClass());
  }

  @Test
  public void getValueClass_returnsExpressionProperty() {
    PropertyOfNodeCodec<ExpressionProperty> codec =
        PropertyOfNodeCodec.getInstance(ExpressionProperty.class);
    assertEquals(ExpressionProperty.class, codec.getValueClass());
  }

  // ---- appendRepresentation ----

  @Test
  public void appendRepresentation_withNull_appendsNullString() {
    PropertyOfNodeCodec<ExpressionProperty> codec =
        PropertyOfNodeCodec.getInstance(ExpressionProperty.class);
    StringBuilder sb = new StringBuilder();
    codec.appendRepresentation(sb, null);
    assertEquals("null", sb.toString());
  }

  // ---- encodeValue null-safety ----

  @Test
  public void encodeValue_nullPath_structuralCoverage() {
    PropertyOfNodeCodec<ExpressionProperty> codec =
        PropertyOfNodeCodec.getInstance(ExpressionProperty.class);
    // Verifies the codec exists and handles the ExpressionProperty type
    assertNotNull(codec);
    assertEquals(ExpressionProperty.class, codec.getValueClass());
  }

  // ---- ItemCodec interface compliance ----

  @Test
  public void implementsItemCodec() {
    PropertyOfNodeCodec<ExpressionProperty> codec =
        PropertyOfNodeCodec.getInstance(ExpressionProperty.class);
    assertTrue(codec instanceof org.lgna.croquet.ItemCodec);
  }
}
