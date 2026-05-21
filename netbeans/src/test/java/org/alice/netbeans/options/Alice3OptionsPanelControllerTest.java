package org.alice.netbeans.options;

import org.junit.Assert;
import org.junit.Test;
import org.netbeans.spi.options.OptionsPanelController;
import org.openide.util.Lookup;

import javax.swing.JCheckBox;
import javax.swing.SwingUtilities;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

public class Alice3OptionsPanelControllerTest {
  @Test
  public void staticPreferenceAccessorsReadStoredValues() throws Exception {
    Preferences preferences = preferences();
    clearPreferences(preferences);
    preferences.putBoolean(Alice3OptionsPanelController.COLLAPSE_IMPORTS_KEY, false);
    preferences.putBoolean(Alice3OptionsPanelController.COLLAPSE_BOILER_PLATE_METHODS_KEY, false);
    preferences.putBoolean(Alice3OptionsPanelController.OFFER_CLEAN_SLATE_METHODS_KEY, true);
    preferences.flush();

    Assert.assertFalse(Alice3OptionsPanelController.isImportCollapsingDesired());
    Assert.assertFalse(Alice3OptionsPanelController.isBoilerPlateMethodCollapsingDesired());
    Assert.assertTrue(Alice3OptionsPanelController.isOfferingCleanSlateDesired());
  }

  @Test
  public void updateChangedAndApplyChangesRoundTripCheckboxState() throws Exception {
    Preferences preferences = preferences();
    clearPreferences(preferences);
    preferences.putBoolean(Alice3OptionsPanelController.COLLAPSE_IMPORTS_KEY, true);
    preferences.putBoolean(Alice3OptionsPanelController.COLLAPSE_BOILER_PLATE_METHODS_KEY, false);
    preferences.putBoolean(Alice3OptionsPanelController.OFFER_CLEAN_SLATE_METHODS_KEY, true);
    preferences.flush();

    Alice3OptionsPanelController controller = new Alice3OptionsPanelController();
    List<PropertyChangeEvent> events = new ArrayList<>();
    PropertyChangeListener listener = events::add;
    controller.addPropertyChangeListener(listener);

    SwingUtilities.invokeAndWait(controller::update);
    Alice3Panel panel = (Alice3Panel) controller.getComponent(Lookup.EMPTY);
    Assert.assertSame(panel, controller.getComponent(Lookup.EMPTY));
    Assert.assertTrue(readCheckBox(panel, "jCollapseImportsCheckBox").isSelected());
    Assert.assertFalse(readCheckBox(panel, "jCollapseBoilerPlateMethodsCheckBox").isSelected());
    Assert.assertTrue(readCheckBox(panel, "jOfferCleanSlateCheckBox").isSelected());
    Assert.assertTrue(controller.isValid());
    Assert.assertNull(controller.getHelpCtx());
    Assert.assertFalse(controller.isChanged());

    SwingUtilities.invokeAndWait(() -> {
      readCheckBox(panel, "jCollapseImportsCheckBox").setSelected(false);
      readCheckBox(panel, "jCollapseBoilerPlateMethodsCheckBox").setSelected(true);
      readCheckBox(panel, "jOfferCleanSlateCheckBox").setSelected(false);
      controller.changed();
    });

    Assert.assertTrue(controller.isChanged());
    Assert.assertTrue(events.stream().anyMatch(event -> OptionsPanelController.PROP_CHANGED.equals(event.getPropertyName())));
    Assert.assertTrue(events.stream().anyMatch(event -> OptionsPanelController.PROP_VALID.equals(event.getPropertyName())));

    controller.applyChanges();
    SwingUtilities.invokeAndWait(() -> {
    });

    Assert.assertFalse(controller.isChanged());
    Assert.assertFalse(preferences.getBoolean(Alice3OptionsPanelController.COLLAPSE_IMPORTS_KEY, true));
    Assert.assertTrue(preferences.getBoolean(Alice3OptionsPanelController.COLLAPSE_BOILER_PLATE_METHODS_KEY, false));
    Assert.assertFalse(preferences.getBoolean(Alice3OptionsPanelController.OFFER_CLEAN_SLATE_METHODS_KEY, true));

    controller.cancel();
    controller.removePropertyChangeListener(listener);
  }

  private static JCheckBox readCheckBox(Alice3Panel panel, String fieldName) {
    try {
      java.lang.reflect.Field field = Alice3Panel.class.getDeclaredField(fieldName);
      field.setAccessible(true);
      return (JCheckBox) field.get(panel);
    } catch (ReflectiveOperationException e) {
      throw new RuntimeException(e);
    }
  }

  private static Preferences preferences() {
    return Preferences.userNodeForPackage(Alice3Panel.class);
  }

  private static void clearPreferences(Preferences preferences) throws Exception {
    preferences.remove(Alice3OptionsPanelController.COLLAPSE_IMPORTS_KEY);
    preferences.remove(Alice3OptionsPanelController.COLLAPSE_BOILER_PLATE_METHODS_KEY);
    preferences.remove(Alice3OptionsPanelController.OFFER_CLEAN_SLATE_METHODS_KEY);
    preferences.flush();
  }
}
