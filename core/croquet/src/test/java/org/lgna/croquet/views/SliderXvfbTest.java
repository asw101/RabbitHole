package org.lgna.croquet.views;

import org.junit.BeforeClass;
import org.junit.Test;
import org.lgna.croquet.*;

import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.util.Hashtable;
import java.util.UUID;

import static org.junit.Assert.*;

public class SliderXvfbTest {
  @BeforeClass
  public static void installApplication() {
    XvfbCroquetTestSupport.installApplication();
  }

  @Test
  public void sliderMirrorsOrientationTicksLabelsAndInversion() {
    XvfbCroquetTestSupport.tryOnEdt(() -> {
      Slider slider = new TestBoundedIntegerState().createSlider();
      Hashtable<Integer, javax.swing.JComponent> labels = new Hashtable<>();
      labels.put(0, new JLabel("min"));
      labels.put(10, new JLabel("max"));

      assertEquals(Slider.Orientation.HORIZONTAL, slider.getOrientation());
      assertEquals(Slider.Orientation.HORIZONTAL, Slider.Orientation.valueOf(SwingConstants.HORIZONTAL));
      assertEquals(Slider.Orientation.VERTICAL, Slider.Orientation.valueOf(SwingConstants.VERTICAL));
      assertNull(Slider.Orientation.valueOf(Integer.MIN_VALUE));

      slider.setOrientation(Slider.Orientation.VERTICAL);
      slider.setInverted(true);
      slider.setMinorTickSpacing(2);
      slider.setMajorTickSpacing(10);
      slider.setSnapToTicks(true);
      slider.setPaintTicks(true);
      slider.setPaintLabels(true);
      slider.setLabelTable(labels);

      assertEquals(Slider.Orientation.VERTICAL, slider.getOrientation());
      assertTrue(slider.getInverted());
      assertEquals(2, slider.getMinorTickSpacing());
      assertEquals(10, slider.getMajorTickSpacing());
      assertTrue(slider.getSnapToTicks());
      assertTrue(slider.getPaintTicks());
      assertTrue(slider.getPaintLabels());
      assertSame(labels, slider.getLabelTable());
    });
  }

  private static final class TestBoundedIntegerState extends BoundedIntegerState {
    private TestBoundedIntegerState() {
      super(new Details(Application.DOCUMENT_UI_GROUP, UUID.randomUUID()).minimum(0).maximum(10).initialValue(5).stepSize(1));
      CroquetTestUtils.removeSpinnerChangeListeners(this);
    }

    @Override
    protected Class<? extends Element> getClassUsedForLocalization() {
      return TestBoundedIntegerState.class;
    }

    @Override
    protected String getSubKeyForLocalization() {
      return "slider";
    }
  }
}
