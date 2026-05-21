package org.alice.ide.properties.adapter;

import org.junit.Test;
import org.lgna.story.implementation.SceneImp;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

public class SceneFogDensityAdapterStructureTest {
  @Test
  public void classExtendsAbstractPropertyAdapter() {
    assertEquals(AbstractPropertyAdapter.class, SceneFogDensityAdapter.class.getSuperclass());
    assertTrue(Modifier.isPublic(SceneFogDensityAdapter.class.getModifiers()));
  }

  @Test
  public void constructorRequiresSceneImpAndExpressionState() throws Exception {
    Constructor<SceneFogDensityAdapter> constructor = SceneFogDensityAdapter.class.getConstructor(
        SceneImp.class, org.alice.ide.croquet.models.StandardExpressionState.class);
    assertTrue(Modifier.isPublic(constructor.getModifiers()));
  }

  @Test
  public void propertyMethodsUseDoubleContract() throws Exception {
    Method getPropertyType = SceneFogDensityAdapter.class.getMethod("getPropertyType");
    Method getValueCopyIfMutable = SceneFogDensityAdapter.class.getMethod("getValueCopyIfMutable");

    assertEquals(Class.class, getPropertyType.getReturnType());
    assertEquals(Double.class, getValueCopyIfMutable.getReturnType());
  }
}
