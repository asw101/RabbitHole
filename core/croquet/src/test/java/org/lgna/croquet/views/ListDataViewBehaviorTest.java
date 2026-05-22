package org.lgna.croquet.views;

import org.junit.Test;
import org.lgna.croquet.CroquetTestUtils;
import org.lgna.croquet.ListDataComposite;
import org.lgna.croquet.data.MutableListData;

import javax.swing.JLabel;
import java.util.UUID;

import static org.junit.Assert.*;

public class ListDataViewBehaviorTest {
  @Test
  public void handleDisplayable_refreshesItemsFromCompositeData() {
    MutableListData<String> data = new MutableListData<>(CroquetTestUtils.STRING_CODEC, new String[]{"alpha", "beta"});
    TestComposite composite = new TestComposite(data);
    TestListDataView view = composite.createView();

    view.handleDisplayable();
    view.refreshIfNecessary();

    assertEquals(2, view.getAwtComponent().getComponentCount());
  }

  @Test
  public void dataMutation_refreshesWhileDisplayable_butNotAfterUndisplayable() {
    MutableListData<String> data = new MutableListData<>(CroquetTestUtils.STRING_CODEC, new String[]{"alpha"});
    TestComposite composite = new TestComposite(data);
    TestListDataView view = composite.createView();
    view.handleDisplayable();
    view.refreshIfNecessary();

    data.internalAddItem("beta");
    view.refreshIfNecessary();
    assertEquals(2, view.getAwtComponent().getComponentCount());

    view.handleUndisplayable();
    data.internalAddItem("gamma");
    view.refreshIfNecessary();
    assertEquals(2, view.getAwtComponent().getComponentCount());
  }

  private static final class TestComposite extends ListDataComposite<String, TestListDataView> {
    private TestComposite(MutableListData<String> data) {
      super(UUID.fromString("00000000-0000-0000-0000-00000000a123"), data);
    }

    @Override
    protected ScrollPane createScrollPaneIfDesired() {
      return null;
    }

    @Override
    protected TestListDataView createView() {
      return new TestListDataView(this);
    }
  }

  private static final class TestListDataView extends ListDataView<String> {
    private TestListDataView(TestComposite composite) {
      super(composite);
    }

    @Override
    protected SwingComponentView<?> createComponentForItem(String item) {
      return new TestLabelView(item);
    }

    @Override
    protected java.awt.LayoutManager createLayoutManager(javax.swing.JPanel jPanel) {
      return new java.awt.FlowLayout();
    }
  }

  private static final class TestLabelView extends SwingComponentView<JLabel> {
    private final String text;

    private TestLabelView(String text) {
      this.text = text;
    }

    @Override
    protected JLabel createAwtComponent() {
      return new JLabel(this.text);
    }
  }
}
