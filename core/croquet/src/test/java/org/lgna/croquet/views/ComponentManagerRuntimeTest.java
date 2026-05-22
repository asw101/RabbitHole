package org.lgna.croquet.views;

import org.junit.Test;
import org.lgna.croquet.Application;
import org.lgna.croquet.XvfbCroquetTestSupport;

import static org.junit.Assert.*;

public class ComponentManagerRuntimeTest {
  @Test
  public void addRemoveAndLookupTrackRegisteredComponents() {
    XvfbCroquetTestSupport.tryOnEdt(() -> {
      XvfbCroquetTestSupport.NamedOperation operation = new XvfbCroquetTestSupport.NamedOperation("component-manager");
      Button button = operation.createButton();
      Label label = new Label("label");
      javax.swing.JFrame frame = new javax.swing.JFrame("components");
      try {
        javax.swing.JPanel panel = new javax.swing.JPanel();
        panel.add(button.getAwtComponent());
        panel.add(label.getAwtComponent());
        frame.setContentPane(panel);
        frame.pack();
        frame.setVisible(true);

        ComponentManager.addComponent(operation, button);
        ComponentManager.addComponent(operation, label);

        assertSame(button, ComponentManager.getFirstComponent(operation, true));
        assertNotNull(ComponentManager.getFirstComponent(operation, Label.class, true));

        ComponentManager.revalidateAndRepaintAllComponents(operation);
        ComponentManager.removeComponent(operation, button);
        ComponentManager.removeComponent(operation, label);

        assertTrue(ComponentManager.getComponents(operation).isEmpty());
      } finally {
        frame.dispose();
      }
    });
  }
}
