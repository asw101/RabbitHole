package org.alice.netbeans;

import org.junit.Assert;
import org.junit.Test;
import org.openide.WizardDescriptor;

import javax.swing.JComponent;
import java.io.File;
import java.util.NoSuchElementException;

public class Alice3ProjectTemplateWizardIteratorNavigationTest {
  @Test
  public void initializeCurrentAndUninitializeManageSingleStepWizardState() {
    Alice3ProjectTemplateWizardIterator iterator = Alice3ProjectTemplateWizardIterator.createIterator();
    WizardDescriptor wizard = new WizardDescriptor(new WizardDescriptor.Panel[0]);
    wizard.putProperty("projdir", new File("target/test-data/iterator-navigation/ImportedWorld"));
    wizard.putProperty("name", "ImportedWorld");

    iterator.initialize(wizard);

    Assert.assertEquals("1 of 1", iterator.name());
    Assert.assertFalse(iterator.hasNext());
    Assert.assertFalse(iterator.hasPrevious());
    WizardDescriptor.Panel panel = iterator.current();
    Assert.assertNotNull(panel);

    JComponent component = (JComponent) panel.getComponent();
    Assert.assertEquals(0, component.getClientProperty("WizardPanel_contentSelectedIndex"));
    Assert.assertNotNull(component.getClientProperty("WizardPanel_contentData"));

    try {
      iterator.nextPanel();
      Assert.fail("Expected nextPanel to reject advancing past the last panel");
    } catch (NoSuchElementException expected) {
      Assert.assertNotNull(expected);
    }

    try {
      iterator.previousPanel();
      Assert.fail("Expected previousPanel to reject moving before the first panel");
    } catch (NoSuchElementException expected) {
      Assert.assertNotNull(expected);
    }

    iterator.addChangeListener(event -> {
    });
    iterator.removeChangeListener(event -> {
    });
    iterator.uninitialize(wizard);
    Assert.assertNull(wizard.getProperty("projdir"));
    Assert.assertNull(wizard.getProperty("name"));
  }

}
