package org.alice.ide.properties.uicontroller;

import org.alice.ide.properties.adapter.AbstractPropertyAdapter;
import org.junit.Test;
import org.lgna.croquet.views.Panel;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class PropertyAdapterControllerStructureTest {
  @Test
  public void typeIsPublicInterfaceWithExpectedConstant() {
    assertTrue(PropertyAdapterController.class.isInterface());
    assertTrue(Modifier.isPublic(PropertyAdapterController.class.getModifiers()));
    assertEquals(28, PropertyAdapterController.MIN_ADAPTER_HEIGHT);
  }

  @Test
  public void declaredMethodsMatchExpectedNames() {
    assertEquals(
        Arrays.asList("getPanel", "getPropertyAdapter", "getPropertyType", "setPropertyAdapter"),
        Arrays.stream(PropertyAdapterController.class.getDeclaredMethods())
            .map(Method::getName)
            .sorted()
            .collect(Collectors.toList()));
  }

  @Test
  public void methodContractsUsePropertyAdapterAndPanelTypes() throws Exception {
    Method setPropertyAdapter = PropertyAdapterController.class.getMethod("setPropertyAdapter", AbstractPropertyAdapter.class);
    Method getPanel = PropertyAdapterController.class.getMethod("getPanel");

    assertEquals(void.class, setPropertyAdapter.getReturnType());
    assertEquals(Panel.class, getPanel.getReturnType());
  }
}
