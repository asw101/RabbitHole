package org.alice.ide.testing;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;

public final class JacocoReflectionSupport {
  private JacocoReflectionSupport() {
  }

  public static Method[] declaredMethods(Class<?> type) {
    return Arrays.stream(type.getDeclaredMethods())
        .filter(method -> !"$jacocoInit".equals(method.getName()))
        .toArray(Method[]::new);
  }

  public static Field[] declaredFields(Class<?> type) {
    return Arrays.stream(type.getDeclaredFields())
        .filter(field -> !"$jacocoData".equals(field.getName()))
        .toArray(Field[]::new);
  }
}
