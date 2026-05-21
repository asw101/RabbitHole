package org.alice.stageide.croquet.models.sceneditor;

import org.alice.stageide.sceneeditor.CameraOption;
import org.junit.Test;
import org.lgna.croquet.EnumConstantState;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;

import static org.junit.Assert.*;

public class ViewListSelectionStateStructureTest {
  @Test
  public void classExtendsEnumConstantStateOfCameraOption() {
    ParameterizedType type = (ParameterizedType) ViewListSelectionState.class.getGenericSuperclass();
    assertEquals(EnumConstantState.class, type.getRawType());
    assertEquals(CameraOption.class, type.getActualTypeArguments()[0]);
  }

  @Test
  public void constructorIsPrivateSingletonStyle() throws Exception {
    Constructor<ViewListSelectionState> constructor = ViewListSelectionState.class.getDeclaredConstructor();
    assertTrue(Modifier.isPrivate(constructor.getModifiers()));
  }

  @Test
  public void getInstanceIsPublicStaticFactory() throws Exception {
    Method method = ViewListSelectionState.class.getMethod("getInstance");
    assertTrue(Modifier.isPublic(method.getModifiers()));
    assertTrue(Modifier.isStatic(method.getModifiers()));
    assertEquals(ViewListSelectionState.class, method.getReturnType());
    assertSame(ViewListSelectionState.getInstance(), ViewListSelectionState.getInstance());
  }
}
