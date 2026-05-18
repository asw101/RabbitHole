package org.lgna.croquet;

import org.junit.Before;
import org.junit.Test;
import org.lgna.croquet.imp.cascade.BlankNode;
import org.lgna.croquet.imp.cascade.ItemNode;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JPanel;
import java.awt.Dimension;
import java.util.function.Supplier;

import static org.junit.Assert.*;

public class CascadeItemTest {

  private TestCascadeItem item;
  private DummyItemNode node;

  @Before
  public void setUp() {
    item = new TestCascadeItem();
    node = new DummyItemNode("transient", "created");
  }

  @Test
  public void getItemCount_alwaysReturnsOne() {
    assertEquals(1, item.getItemCount());
  }

  @Test
  public void getItemAt_zero_returnsSelf() {
    assertSame(item, item.getItemAt(0));
  }

  @Test
  public void getTransientValue_readsNodeTransientValue() {
    assertEquals("transient", item.getTransientValue(node));
  }

  @Test
  public void createValue_readsNodeCreatedValue() {
    assertEquals("created", item.createValue(node));
  }

  @Test
  public void getMenuItemIcon_nullProxy_returnsNull() {
    item.setProxySupplier(() -> null);

    assertNull(item.getMenuItemIcon(node));
  }

  @Test
  public void getMenuItemIcon_zeroSizedProxy_returnsNull() {
    item.setProxySupplier(() -> sizedPanel(0, 0));

    assertNull(item.getMenuItemIcon(node));
    assertFalse(item.isDirtyFlag());
  }

  @Test
  public void getMenuItemIcon_cleanItem_reusesCachedIcon() {
    Icon first = item.getMenuItemIcon(node);
    Icon second = item.getMenuItemIcon(node);

    assertNotNull(first);
    assertSame(first, second);
    assertEquals(1, item.getProxyCreateCount());
    assertFalse(item.isDirtyFlag());
  }

  @Test
  public void markDirty_forcesFreshIconCreation() {
    Icon first = item.getMenuItemIcon(node);

    item.markDirty();
    Icon second = item.getMenuItemIcon(node);

    assertNotNull(first);
    assertNotNull(second);
    assertNotSame(first, second);
    assertEquals(2, item.getProxyCreateCount());
  }

  @Test
  public void relocalize_marksItemDirtyAgain() {
    item.getMenuItemIcon(node);

    item.relocalize();

    assertTrue(item.isDirtyFlag());
  }

  @Test
  public void getMenuItemText_defaultsToNull() {
    assertNull(item.getMenuItemText());
  }

  private static JPanel sizedPanel(int width, int height) {
    JPanel panel = new JPanel();
    panel.setPreferredSize(new Dimension(width, height));
    return panel;
  }

  private static final class TestCascadeItem extends CascadeItem<String, String> {
    private int proxyCreateCount;
    private Supplier<JComponent> proxySupplier = () -> sizedPanel(24, 12);

    private TestCascadeItem() {
      super(CroquetTestUtils.nextTestUUID());
    }

    private void setProxySupplier(Supplier<JComponent> proxySupplier) {
      this.proxySupplier = proxySupplier;
    }

    private int getProxyCreateCount() {
      return this.proxyCreateCount;
    }

    private boolean isDirtyFlag() {
      return this.isDirty();
    }

    @Override
    public String getTransientValue(ItemNode<? super String, String> node) {
      return (String) node.getTransientValue();
    }

    @Override
    public String createValue(ItemNode<? super String, String> node) {
      return (String) node.createValue();
    }

    @Override
    protected JComponent createMenuItemIconProxy(ItemNode<? super String, String> node) {
      this.proxyCreateCount++;
      return this.proxySupplier.get();
    }
  }

  private static final class DummyItemNode implements ItemNode<String, String> {
    private final String transientValue;
    private final String createdValue;

    private DummyItemNode(String transientValue, String createdValue) {
      this.transientValue = transientValue;
      this.createdValue = createdValue;
    }

    @Override
    public BlankNode<String> getBlankStepAt(int index) {
      return null;
    }

    @Override
    public String getTransientValue() {
      return this.transientValue;
    }

    @Override
    public String createValue() {
      return this.createdValue;
    }
  }
}
