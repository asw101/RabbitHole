package org.alice.ide.properties.uicontroller;

import org.alice.ide.properties.adapter.AbstractPropertyAdapter;
import org.junit.Test;
import org.lgna.croquet.views.BorderPanel;
import org.lgna.croquet.views.Panel;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

public class ExpressionBasedPropertyControllerStructureTest {
  @Test
  public void classExtendsBorderPanelAndImplementsPropertyAdapterController() {
    assertEquals(BorderPanel.class, ExpressionBasedPropertyController.class.getSuperclass());
    assertTrue(PropertyAdapterController.class.isAssignableFrom(ExpressionBasedPropertyController.class));
    assertTrue(Modifier.isPublic(ExpressionBasedPropertyController.class.getModifiers()));
  }

  @Test
  public void constructorAcceptsAbstractPropertyAdapter() throws Exception {
    Constructor<ExpressionBasedPropertyController> constructor = ExpressionBasedPropertyController.class.getConstructor(AbstractPropertyAdapter.class);
    assertTrue(Modifier.isPublic(constructor.getModifiers()));
  }

  @Test
  public void keyMethodsReturnExpectedTypes() throws Exception {
    Method getPanel = ExpressionBasedPropertyController.class.getMethod("getPanel");
    Method internalRefresh = ExpressionBasedPropertyController.class.getDeclaredMethod("internalRefresh");

    assertEquals(Panel.class, getPanel.getReturnType());
    assertEquals(void.class, internalRefresh.getReturnType());
    assertTrue(Modifier.isProtected(internalRefresh.getModifiers()));
  }
}
