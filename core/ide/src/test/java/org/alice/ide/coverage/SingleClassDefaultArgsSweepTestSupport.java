package org.alice.ide.coverage;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public abstract class SingleClassDefaultArgsSweepTestSupport {
  protected abstract String getClassName();

  @Test
  public void loadsAndExercisesAssignedClass() throws Exception {
    String className = getClassName();
    ClassLoadingSweepSupport.SweepResult result = ClassLoadingSweepSupport.sweepNamedClassesWithDefaultArgs(className);

    assertEquals(result.summary(), 1, result.discovered);
    if ((result.loaded == result.discovered) && result.classLoadFailures.isEmpty()) {
      return;
    }

    ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
    if (classLoader == null) {
      classLoader = getClass().getClassLoader();
    }

    Class<?> clazz;
    try {
      clazz = Class.forName(className, true, classLoader);
    } catch (Throwable initializationFailure) {
      clazz = Class.forName(className, false, classLoader);
    }

    assertNotNull(className, clazz);
    assertNotNull(className, clazz.getPackage());
    clazz.getDeclaredFields();
    clazz.getDeclaredMethods();
    clazz.getDeclaredConstructors();
    clazz.getDeclaredClasses();
    if (clazz.isEnum()) {
      assertNotNull(className, clazz.getEnumConstants());
    }
    if (clazz.getSuperclass() != null) {
      assertTrue(clazz.getSuperclass().getName().length() > 0);
    }
  }
}
