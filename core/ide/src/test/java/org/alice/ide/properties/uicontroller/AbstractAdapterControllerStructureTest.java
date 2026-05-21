package org.alice.ide.properties.uicontroller;

import org.alice.ide.properties.adapter.AbstractPropertyAdapter;
import org.junit.Test;
import org.lgna.croquet.views.GridBagPanel;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

public class AbstractAdapterControllerStructureTest {
  @Test
  public void classIsAbstractAndExtendsGridBagPanel() {
    assertTrue(Modifier.isAbstract(AbstractAdapterController.class.getModifiers()));
    assertEquals(GridBagPanel.class, AbstractAdapterController.class.getSuperclass());
    assertTrue(PropertyAdapterController.class.isAssignableFrom(AbstractAdapterController.class));
  }

  @Test
  public void constructorAcceptsAbstractPropertyAdapter() throws Exception {
    Constructor<AbstractAdapterController> constructor = AbstractAdapterController.class.getConstructor(AbstractPropertyAdapter.class);
    assertTrue(Modifier.isPublic(constructor.getModifiers()));
  }

  @Test
  public void coreTemplateMethodsRemainProtected() throws Exception {
    Method setValueOnUI = AbstractAdapterController.class.getDeclaredMethod("setValueOnUI", Object.class);
    Method setValueOnData = AbstractAdapterController.class.getDeclaredMethod("setValueOnData", Object.class);

    assertTrue(Modifier.isProtected(setValueOnUI.getModifiers()));
    assertTrue(Modifier.isProtected(setValueOnData.getModifiers()));
  }
}
