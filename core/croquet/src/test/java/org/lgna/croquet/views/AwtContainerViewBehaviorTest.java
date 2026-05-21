package org.lgna.croquet.views;

import org.junit.Test;

import javax.swing.JLabel;
import javax.swing.JPanel;

import static org.junit.Assert.*;

public class AwtContainerViewBehaviorTest {
  @Test
  public void addAndRemoveComponents_updateContainerState() {
    TestContainer container = new TestContainer();
    LabelView child = new LabelView();

    container.addChild(child);
    assertEquals(1, container.getComponentCount());
    assertSame(child, container.getComponent(0));
    assertTrue(container.isAncestorOf(child));

    container.removeChild(child);
    assertEquals(0, container.getComponentCount());
  }

  @Test
  public void forgetAll_releasesChildMappings() {
    TestContainer container = new TestContainer();
    LabelView child = new LabelView();
    java.awt.Component oldComponent = child.getAwtComponent();
    container.addChild(child);

    container.forgetAll();

    assertEquals(0, container.getComponentCount());
    assertNotSame(child, AwtComponentView.lookup(oldComponent));
  }

  private static final class TestContainer extends AwtContainerView<JPanel> {
    @Override
    protected JPanel createAwtComponent() {
      return new JPanel();
    }

    private void addChild(AwtComponentView<?> child) {
      synchronized (this.getTreeLock()) {
        this.internalAddComponent(child);
      }
    }

    private void removeChild(AwtComponentView<?> child) {
      synchronized (this.getTreeLock()) {
        this.internalRemoveComponent(child);
      }
    }

    private void forgetAll() {
      synchronized (this.getTreeLock()) {
        this.internalForgetAndRemoveAllComponents();
      }
    }
  }

  private static final class LabelView extends AwtComponentView<JLabel> {
    @Override
    protected JLabel createAwtComponent() {
      return new JLabel("child");
    }
  }
}
