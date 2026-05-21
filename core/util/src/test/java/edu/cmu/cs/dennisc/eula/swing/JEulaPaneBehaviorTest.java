package edu.cmu.cs.dennisc.eula.swing;

import org.junit.Test;

import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import java.awt.Component;
import java.awt.Container;

import static org.junit.Assert.*;

public class JEulaPaneBehaviorTest {

  @Test
  public void okButtonStartsDisabledUntilAccepted() {
    JEulaPane pane = new JEulaPane("license");
    JButton okButton = (JButton) findButton(pane, "OK");

    assertNotNull(okButton);
    assertFalse(okButton.isEnabled());
    assertFalse(pane.isAccepted());
  }

  @Test
  public void selectingAcceptEnablesOk() {
    JEulaPane pane = new JEulaPane("license");
    JCheckBox accept = (JCheckBox) findButton(pane, "I accept the terms in the License Agreement");
    JButton okButton = (JButton) findButton(pane, "OK");

    accept.setSelected(true);

    assertTrue(okButton.isEnabled());
    assertFalse(pane.isAccepted());
  }

  @Test
  public void cancelButtonExistsAndPaneStartsUnaccepted() {
    JEulaPane pane = new JEulaPane("license");
    AbstractButton cancelButton = findButton(pane, "Cancel");

    assertNotNull(cancelButton);
    assertFalse(pane.isAccepted());
  }

  private static AbstractButton findButton(Container root, String text) {
    for (Component component : root.getComponents()) {
      if (component instanceof AbstractButton button && text.equals(button.getText())) {
        return button;
      }
      if (component instanceof Container child) {
        AbstractButton found = findButton(child, text);
        if (found != null) {
          return found;
        }
      }
    }
    return null;
  }
}
