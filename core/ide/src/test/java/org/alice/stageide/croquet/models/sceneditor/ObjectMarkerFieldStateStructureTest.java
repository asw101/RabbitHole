package org.alice.stageide.croquet.models.sceneditor;

import org.junit.Test;
import org.lgna.croquet.MutableDataSingleSelectListState;
import org.lgna.project.ast.UserField;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;

import static org.junit.Assert.*;

public class ObjectMarkerFieldStateStructureTest {
  @Test
  public void classExtendsMutableDataSingleSelectListStateOfUserField() {
    ParameterizedType type = (ParameterizedType) ObjectMarkerFieldState.class.getGenericSuperclass();
    assertEquals(MutableDataSingleSelectListState.class, type.getRawType());
    assertEquals(UserField.class, type.getActualTypeArguments()[0]);
  }

  @Test
  public void constructorIsPrivateSingletonStyle() throws Exception {
    Constructor<ObjectMarkerFieldState> constructor = ObjectMarkerFieldState.class.getDeclaredConstructor();
    assertTrue(Modifier.isPrivate(constructor.getModifiers()));
  }

  @Test
  public void getInstanceIsPublicStaticFactory() throws Exception {
    Method method = ObjectMarkerFieldState.class.getMethod("getInstance");
    assertTrue(Modifier.isPublic(method.getModifiers()));
    assertTrue(Modifier.isStatic(method.getModifiers()));
    assertEquals(ObjectMarkerFieldState.class, method.getReturnType());
    assertSame(ObjectMarkerFieldState.getInstance(), ObjectMarkerFieldState.getInstance());
  }
}
