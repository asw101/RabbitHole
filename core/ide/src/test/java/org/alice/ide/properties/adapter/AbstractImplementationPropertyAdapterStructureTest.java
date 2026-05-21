package org.alice.ide.properties.adapter;

import org.junit.Test;
import org.lgna.story.implementation.Property;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

public class AbstractImplementationPropertyAdapterStructureTest {
  @Test
  public void classIsPublicAbstractAndExtendsAbstractPropertyAdapter() {
    assertTrue(Modifier.isPublic(AbstractImplementationPropertyAdapter.class.getModifiers()));
    assertTrue(Modifier.isAbstract(AbstractImplementationPropertyAdapter.class.getModifiers()));
    assertEquals(AbstractPropertyAdapter.class, AbstractImplementationPropertyAdapter.class.getSuperclass());
  }

  @Test
  public void constructorIncludesPropertyAndExpressionState() throws Exception {
    Constructor<AbstractImplementationPropertyAdapter> constructor = AbstractImplementationPropertyAdapter.class.getConstructor(
        String.class, Object.class, Property.class, org.alice.ide.croquet.models.StandardExpressionState.class);
    assertTrue(Modifier.isPublic(constructor.getModifiers()));
  }

  @Test
  public void changeHandlingMethodsRemainProtected() throws Exception {
    Method addPropertyListener = AbstractImplementationPropertyAdapter.class.getDeclaredMethod("addPropertyListener", Property.Listener.class);
    Method handleInternalValueChanged = AbstractImplementationPropertyAdapter.class.getDeclaredMethod("handleInternalValueChanged");

    assertTrue(Modifier.isProtected(addPropertyListener.getModifiers()));
    assertTrue(Modifier.isProtected(handleInternalValueChanged.getModifiers()));
  }
}
