package org.alice.ide.properties.adapter;

import edu.cmu.cs.dennisc.property.InstanceProperty;
import edu.cmu.cs.dennisc.property.event.PropertyListener;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

public class AbstractInstancePropertyAdapterStructureTest {
  @Test
  public void classIsPublicAbstractAndExtendsAbstractPropertyAdapter() {
    assertTrue(Modifier.isPublic(AbstractInstancePropertyAdapter.class.getModifiers()));
    assertTrue(Modifier.isAbstract(AbstractInstancePropertyAdapter.class.getModifiers()));
    assertEquals(AbstractPropertyAdapter.class, AbstractInstancePropertyAdapter.class.getSuperclass());
  }

  @Test
  public void constructorIncludesInstancePropertyAndExpressionState() throws Exception {
    Constructor<AbstractInstancePropertyAdapter> constructor = AbstractInstancePropertyAdapter.class.getConstructor(
        String.class, Object.class, InstanceProperty.class, org.alice.ide.croquet.models.StandardExpressionState.class);
    assertTrue(Modifier.isPublic(constructor.getModifiers()));
  }

  @Test
  public void listenerHooksRemainProtected() throws Exception {
    Method addPropertyListener = AbstractInstancePropertyAdapter.class.getDeclaredMethod("addPropertyListener", PropertyListener.class);
    Method removePropertyListener = AbstractInstancePropertyAdapter.class.getDeclaredMethod("removePropertyListener", PropertyListener.class);

    assertTrue(Modifier.isProtected(addPropertyListener.getModifiers()));
    assertTrue(Modifier.isProtected(removePropertyListener.getModifiers()));
  }
}
