package org.lgna.croquet.views;

import org.junit.Test;
import org.lgna.croquet.CroquetTestUtils;
import org.lgna.croquet.data.MutableListData;

import javax.swing.InputMap;
import javax.swing.JComponent;
import java.awt.Dimension;

import static org.junit.Assert.*;

public class MutableListBehaviorTest {
  @Test
  public void dataMutation_createsButtonsAndUpdatesLayout() {
    MutableListData<String> data = new MutableListData<>(CroquetTestUtils.STRING_CODEC, new String[]{"alpha", "beta"});
    TestMutableList view = new TestMutableList(data);

    data.internalAddItem("gamma");
    view.getAwtComponent().setSize(160, 120);
    view.getAwtComponent().doLayout();

    assertEquals(3, view.getAwtComponent().getComponentCount());
    assertEquals("gamma", ((TestMutableList.TestButton) view.getAwtComponent().getComponent(2)).getText());
    assertTrue(view.getAwtComponent().getComponent(0).getHeight() > 0);
  }

  @Test
  public void clearSelection_andKeyboardRegistration_updateSwingState() {
    MutableListData<String> data = new MutableListData<>(CroquetTestUtils.STRING_CODEC, new String[]{"alpha"});
    TestMutableList view = new TestMutableList(data);
    TestMutableList.TestButton button = (TestMutableList.TestButton) view.getAwtComponent().getComponent(0);
    button.setSelected(true);

    view.clearSelection();
    view.registerKeyboardActions();
    InputMap map = view.getAwtComponent().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);

    assertFalse(button.isSelected());
    assertNotNull(map.get(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_DELETE, 0)));

    view.unregisterKeyboardActions();
    assertNull(map.get(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_DELETE, 0)));
  }

  private static final class TestMutableList extends MutableList<String> {
    private TestMutableList(MutableListData<String> data) {
      super(data);
    }

    @Override
    protected JItemAtIndexButton createJItemAtIndexButton(int index) {
      return new TestButton(this, index);
    }

    private static final class TestButton extends JItemAtIndexButton {
      private final TestMutableList owner;
      private final int index;

      private TestButton(TestMutableList owner, int index) {
        this.owner = owner;
        this.index = index;
        this.setPreferredSize(new Dimension(80, 24));
      }

      @Override
      public void update() {
        if (this.index < owner.getData().getItemCount()) {
          this.setText(owner.getData().getItemAt(this.index));
        } else {
          this.setText(null);
        }
      }
    }
  }
}
