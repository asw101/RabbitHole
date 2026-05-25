package org.lgna.croquet.views;

import org.junit.BeforeClass;
import org.junit.Test;
import org.lgna.croquet.Application;
import org.lgna.croquet.BoundedIntegerState;
import org.lgna.croquet.CroquetTestUtils;
import org.lgna.croquet.Element;
import org.lgna.croquet.XvfbCroquetTestSupport;

import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.util.Hashtable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class SliderHeadlessBehaviorTest {
  @BeforeClass
  public static void ensureApplication() {
    CroquetTestUtils.ensureTestApplication();
  }

  @Test
  public void slider_mirrors_orientation_ticks_labels_and_inversion_headlessly() {
    XvfbCroquetTestSupport.onEdt(() -> {
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
      return null;
    });
  }

  private static final class TestBoundedIntegerState extends BoundedIntegerState {
    private TestBoundedIntegerState() {
      super(new Details(Application.DOCUMENT_UI_GROUP, CroquetTestUtils.nextTestUUID()).minimum(0).maximum(10).initialValue(5).stepSize(1));
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
