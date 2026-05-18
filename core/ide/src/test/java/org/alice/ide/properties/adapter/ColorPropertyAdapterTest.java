package org.alice.ide.properties.adapter;

import org.junit.Test;
import org.lgna.story.Color;
import org.lgna.story.implementation.Property;

import static org.junit.Assert.*;

public class ColorPropertyAdapterTest {

  private static final class TestColorProperty extends Property<Color> {
    private Color value;

    private TestColorProperty(Color value) {
      super(null, Color.class);
      this.value = value;
    }

    @Override
    public Color getValue() {
      return this.value;
    }

    @Override
    protected void handleSetValue(Color value) {
      this.value = value;
    }

    @Override
    protected Color interpolate(Color a, Color b, double portion) {
      return portion < 0.5 ? a : b;
    }
  }

  @Test
  public void defaultConstructor_usesColorRepresentation() {
    ColorPropertyAdapter<Object> adapter = new ColorPropertyAdapter<>(new Object(), new TestColorProperty(Color.RED), null);

    assertEquals("Color", adapter.getRepr());
  }

  @Test
  public void customConstructor_preservesRepresentation() {
    ColorPropertyAdapter<Object> adapter = new ColorPropertyAdapter<>("Paint", new Object(), new TestColorProperty(Color.RED), null);

    assertEquals("Paint", adapter.getRepr());
  }

  @Test
  public void getUndoRedoDescription_returnsColor() {
    ColorPropertyAdapter<Object> adapter = new ColorPropertyAdapter<>(new Object(), new TestColorProperty(Color.RED), null);

    assertEquals("Color", adapter.getUndoRedoDescription());
  }

  @Test
  public void getValueCopyIfMutable_returnsSameColorInstance() {
    ColorPropertyAdapter<Object> adapter = new ColorPropertyAdapter<>(new Object(), new TestColorProperty(Color.RED), null);

    assertSame(adapter.getValue(), adapter.getValueCopyIfMutable());
  }

  @Test
  public void setValue_updatesUnderlyingProperty() {
    TestColorProperty property = new TestColorProperty(Color.RED);
    ColorPropertyAdapter<Object> adapter = new ColorPropertyAdapter<>(new Object(), property, null);

    adapter.setValue(Color.BLUE);

    PropertyAdapterTestHelper.waitForValue(property::getValue, Color.BLUE);
    assertEquals(Color.BLUE, property.getValue());
  }
}
