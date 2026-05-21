package org.alice.stageide.croquet.models.sceneditor;

import org.junit.Test;
import org.lgna.croquet.MutableDataSingleSelectListState;
import org.lgna.project.ast.UserField;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;

import static org.junit.Assert.*;

public class CameraMarkerFieldStateStructureTest {
  @Test
  public void classExtendsMutableDataSingleSelectListStateOfUserField() {
    ParameterizedType type = (ParameterizedType) CameraMarkerFieldState.class.getGenericSuperclass();
    assertEquals(MutableDataSingleSelectListState.class, type.getRawType());
    assertEquals(UserField.class, type.getActualTypeArguments()[0]);
  }

  @Test
  public void constructorIsPrivateSingletonStyle() throws Exception {
    Constructor<CameraMarkerFieldState> constructor = CameraMarkerFieldState.class.getDeclaredConstructor();
    assertTrue(Modifier.isPrivate(constructor.getModifiers()));
  }

  @Test
  public void getInstanceIsPublicStaticFactory() throws Exception {
    Method method = CameraMarkerFieldState.class.getMethod("getInstance");
    assertTrue(Modifier.isPublic(method.getModifiers()));
    assertTrue(Modifier.isStatic(method.getModifiers()));
    assertEquals(CameraMarkerFieldState.class, method.getReturnType());
    assertSame(CameraMarkerFieldState.getInstance(), CameraMarkerFieldState.getInstance());
  }
}
