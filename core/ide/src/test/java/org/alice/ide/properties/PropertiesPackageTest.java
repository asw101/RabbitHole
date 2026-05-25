package org.alice.ide.properties;

import org.alice.ide.properties.adapter.AbstractPropertyAdapter;
import org.alice.ide.properties.uicontroller.AdapterControllerUtilities;
import org.alice.ide.properties.uicontroller.BlankPropertyController;
import org.alice.ide.properties.uicontroller.PropertyAdapterController;
import org.alice.ide.properties.uicontroller.StringPropertyController;
import org.junit.Test;

import javax.swing.SwingUtilities;

import static org.junit.Assert.*;

public class PropertiesPackageTest {
  private static final class StringTestAdapter extends AbstractPropertyAdapter<String, Object> {
    private StringTestAdapter() {
      super("Display Name", new Object(), null);
    }

    @Override
    public String getValue() {
      return "hello";
    }

    @Override
    public Class<String> getPropertyType() {
      return String.class;
    }

    @Override
    public String getValueCopyIfMutable() {
      return getValue();
    }
  }

  @Test
  public void localizedStringGracefullyHandlesNullKeys() {
    assertNull(AbstractPropertyAdapter.getLocalizedString(null));
  }

  @Test
  public void adapterControllerUtilitiesSelectsStringAndBlankControllers() throws Exception {
    final PropertyAdapterController<?>[] controllers = new PropertyAdapterController<?>[2];

    SwingUtilities.invokeAndWait(() -> {
      controllers[0] = AdapterControllerUtilities.getValuePanelForPropertyAdapter(new StringTestAdapter());
      controllers[1] = AdapterControllerUtilities.getValuePanelForPropertyAdapter(null);
    });

    assertTrue(controllers[0] instanceof StringPropertyController);
    assertTrue(controllers[1] instanceof BlankPropertyController);
  }
}
