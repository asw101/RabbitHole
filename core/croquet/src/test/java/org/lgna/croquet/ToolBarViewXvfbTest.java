package org.lgna.croquet;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.rules.Timeout;
import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.Timeout;
import org.lgna.croquet.data.MutableListData;
import org.junit.Rule;
import org.junit.rules.Timeout;
import org.lgna.croquet.views.ComboBox;
import org.junit.Rule;
import org.junit.rules.Timeout;
import org.lgna.croquet.views.Label;
import org.junit.Rule;
import org.junit.rules.Timeout;
import org.lgna.croquet.views.ToolBarView;
import org.junit.Rule;
import org.junit.rules.Timeout;

import javax.swing.JComponent;
import org.junit.Rule;
import org.junit.rules.Timeout;
import java.util.Arrays;
import org.junit.Rule;
import org.junit.rules.Timeout;

import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.rules.Timeout;

public class ToolBarViewXvfbTest {

  @Rule
  public Timeout globalTimeout = Timeout.seconds(30);
  @BeforeClass
  public static void installApplication() {
    XvfbCroquetTestSupport.installApplication();
  }

  @Test
  public void toolbarViewCreatesButtonsLabelsCombosAndCompositeViews() {
    XvfbCroquetTestSupport.tryOnEdt(() -> {
      XvfbCroquetTestSupport.ClobberingOperation operation = new XvfbCroquetTestSupport.ClobberingOperation("Launch");
      MutableListData<String> items = new MutableListData<>(CroquetTestUtils.STRING_CODEC, new String[]{"one", "two"});
      MutableDataSingleSelectListState<String> listState = new MutableDataSingleSelectListState<>(Application.DOCUMENT_UI_GROUP, java.util.UUID.randomUUID(), 0, items) {
      };
      XvfbCroquetTestSupport.TestSimpleComposite subComposite = new XvfbCroquetTestSupport.TestSimpleComposite("sub");
      PlainStringValue text = new PlainStringValue(java.util.UUID.randomUUID()) {
      };
      text.setText("Text");

      TestToolBarComposite composite = new TestToolBarComposite(operation, listState, subComposite, text);
      ToolBarView view = composite.getView();

      assertTrue(view.getAwtComponent().getComponentCount() >= 4);
      assertNotNull(findChild(view.getAwtComponent(), ComboBox.class));
      assertNotNull(findChild(view.getAwtComponent(), javax.swing.JLabel.class));
    });
  }

  private static Object findChild(JComponent root, Class<?> type) {
    for (java.awt.Component component : root.getComponents()) {
      if (type == ComboBox.class && component instanceof javax.swing.JComboBox) {
        return component;
      }
      if (type == javax.swing.JLabel.class && component instanceof javax.swing.JLabel) {
        return component;
      }
      if (component instanceof JComponent child) {
        Object nested = findChild(child, type);
        if (nested != null) {
          return nested;
        }
      }
    }
    return null;
  }

  private static final class TestToolBarComposite extends ToolBarComposite {
    private final java.lang.Iterable<? extends Element> elements;

    private TestToolBarComposite(Element... elements) {
      super(java.util.UUID.randomUUID());
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
