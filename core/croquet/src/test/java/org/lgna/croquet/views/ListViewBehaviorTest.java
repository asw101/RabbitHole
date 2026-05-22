package org.lgna.croquet.views;

import edu.cmu.cs.dennisc.java.awt.Painter;
import org.junit.Test;
import org.lgna.croquet.CroquetTestUtils;
import org.lgna.croquet.Element;
import org.lgna.croquet.Group;
import org.lgna.croquet.SingleSelectListState;
import org.lgna.croquet.data.MutableListData;

import javax.swing.JLabel;
import javax.swing.ListCellRenderer;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;

public class ListViewBehaviorTest {
  private static final Group TEST_GROUP = Group.getInstance(
      UUID.fromString("00000000-0000-0000-0000-00000000b123"), "listView");

  @Test
  public void emptyConditionPainter_runsWhenModelIsEmpty() {
    TestSingleSelectListState state = new TestSingleSelectListState(new MutableListData<>(CroquetTestUtils.STRING_CODEC));
    state.getEmptyConditionText().setText("Nothing here");
    List<String> view = state.createList();
    AtomicInteger paints = new AtomicInteger();
    view.setEmptyConditionPainter(new Painter<List<String>>() {
      @Override
      public void paint(Graphics2D g2, List<String> listView, int width, int height) {
        paints.incrementAndGet();
      }
    });

    BufferedImage image = new BufferedImage(40, 20, BufferedImage.TYPE_INT_ARGB);
    view.getAwtComponent().setSize(40, 20);
    view.getAwtComponent().paint(image.getGraphics());

    assertEquals(1, paints.get());
  }

  @Test
  public void rendererOrientationAndDoubleClickListener_updateSwingList() {
    TestSingleSelectListState state = new TestSingleSelectListState(
        new MutableListData<>(CroquetTestUtils.STRING_CODEC, new String[]{"alpha", "beta"}));
    List<String> view = state.createList();
    ListCellRenderer<? super String> renderer = new javax.swing.DefaultListCellRenderer();

    view.setCellRenderer(renderer);
    view.setVisibleRowCount(7);
    view.setLayoutOrientation(List.LayoutOrientation.HORIZONTAL_WRAP);
    view.enableClickingDefaultButtonOnDoubleClick();
    int listenerCountAfterEnable = view.getAwtComponent().getMouseListeners().length;
    view.disableClickingDefaultButtonOnDoubleClick();

    assertSame(renderer, view.getCellRenderer());
    assertEquals(7, view.getVisibleRowCount());
    assertEquals(javax.swing.JList.HORIZONTAL_WRAP, view.getAwtComponent().getLayoutOrientation());
    assertTrue(listenerCountAfterEnable > view.getAwtComponent().getMouseListeners().length);
  }

  private static final class TestSingleSelectListState extends SingleSelectListState<String, MutableListData<String>> {
    private TestSingleSelectListState(MutableListData<String> data) {
      super(TEST_GROUP, CroquetTestUtils.nextTestUUID(), -1, data);
    }

    @Override
    protected Class<? extends Element> getClassUsedForLocalization() {
      return TestSingleSelectListState.class;
    }
  }
}
