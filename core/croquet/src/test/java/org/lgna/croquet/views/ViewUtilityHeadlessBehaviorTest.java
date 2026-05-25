package org.lgna.croquet.views;

import org.junit.BeforeClass;
import org.junit.Test;
import org.lgna.croquet.Application;
import org.lgna.croquet.CroquetTestUtils;
import org.lgna.croquet.Element;
import org.lgna.croquet.GapToolBarSeparator;
import org.lgna.croquet.MutableDataSingleSelectListState;
import org.lgna.croquet.PlainStringValue;
import org.lgna.croquet.PushToolBarSeparator;
import org.lgna.croquet.ToolBarComposite;
import org.lgna.croquet.XvfbCroquetTestSupport;
import org.lgna.croquet.data.MutableListData;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import java.awt.Color;
import java.awt.TexturePaint;
import java.awt.image.BufferedImage;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class ViewUtilityHeadlessBehaviorTest {
  @BeforeClass
  public static void ensureApplication() {
    CroquetTestUtils.ensureTestApplication();
  }

  @Test
  public void paintUtilities_cache_texture_paints_with_drawn_pixels() {
    TexturePaint disabled = (TexturePaint) PaintUtilities.getDisabledTexturePaint();
    TexturePaint copy = PaintUtilities.getCopyTexturePaint();

    assertSame(disabled, PaintUtilities.getDisabledTexturePaint());
    assertSame(copy, PaintUtilities.getCopyTexturePaint());
    assertTrue(hasInk(disabled.getImage()));
    assertTrue(hasInk(copy.getImage()));
  }

  @Test
  public void componentManager_tracks_components_and_repaint_lifecycle() {
    XvfbCroquetTestSupport.onEdt(() -> {
      XvfbCroquetTestSupport.NamedOperation operation = new XvfbCroquetTestSupport.NamedOperation("Run");
      Button button = operation.createButton();
      button.getAwtComponent().setVisible(true);

      ComponentManager.addComponent(operation, button);
      assertEquals(1, ComponentManager.getComponents(operation).size());
      assertSame(button, ComponentManager.getFirstComponent(operation, true));

      ComponentManager.repaintAllComponents(operation);
      ComponentManager.revalidateAndRepaintAllComponents(operation);
      ComponentManager.removeComponent(operation, button);
      assertEquals(0, ComponentManager.getComponents(operation).size());
      return null;
    });
  }

  @Test
  public void toolBarView_builds_operation_combo_composite_and_text_components() {
    XvfbCroquetTestSupport.onEdt(() -> {
      XvfbCroquetTestSupport.ClobberingOperation operation = new XvfbCroquetTestSupport.ClobberingOperation("Launch");
      MutableListData<String> items = new MutableListData<>(CroquetTestUtils.STRING_CODEC, new String[]{"one", "two"});
      MutableDataSingleSelectListState<String> listState = new MutableDataSingleSelectListState<>(Application.DOCUMENT_UI_GROUP, CroquetTestUtils.nextTestUUID(), 0, items) {
      };
      XvfbCroquetTestSupport.TestSimpleComposite subComposite = new XvfbCroquetTestSupport.TestSimpleComposite("sub");
      PlainStringValue text = new PlainStringValue(CroquetTestUtils.nextTestUUID()) {
      };
      text.setText("Text");

      TestToolBarComposite composite = new TestToolBarComposite(operation, listState, subComposite, text);
      ToolBarView view = composite.getView();

      assertTrue(view.getAwtComponent().getComponentCount() >= 4);
      assertNotNull(findChild(view.getAwtComponent(), javax.swing.JComboBox.class));
      assertNotNull(findChild(view.getAwtComponent(), JLabel.class));
      JButton button = (JButton) findChild(view.getAwtComponent(), JButton.class);
      assertEquals("Launch", button.getToolTipText());
      return null;
    });
  }

  private static boolean hasInk(BufferedImage image) {
    for (int y = 0; y < image.getHeight(); y++) {
      for (int x = 0; x < image.getWidth(); x++) {
        if ((image.getRGB(x, y) >>> 24) != 0) {
          return true;
        }
      }
    }
    return false;
  }

  private static java.awt.Component findChild(JComponent root, Class<?> type) {
    for (java.awt.Component component : root.getComponents()) {
      if (type.isInstance(component)) {
        return component;
      }
      if (component instanceof JComponent child) {
        java.awt.Component nested = findChild(child, type);
        if (nested != null) {
          return nested;
        }
      }
    }
    return null;
  }

  private static final class TestToolBarComposite extends ToolBarComposite {
    private final Iterable<? extends Element> elements;

    private TestToolBarComposite(Element... elements) {
      super(CroquetTestUtils.nextTestUUID());
      this.elements = Arrays.asList(elements[0], GapToolBarSeparator.getInstance(), elements[1], PushToolBarSeparator.getInstance(), elements[2], elements[3]);
    }

    @Override
    public Iterable<? extends Element> getSubElements() {
      return this.elements;
    }

    @Override
    protected ToolBarView createView() {
      return new ToolBarView(this) {
      };
    }
  }
}
