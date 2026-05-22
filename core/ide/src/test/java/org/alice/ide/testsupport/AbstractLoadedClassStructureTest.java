package org.alice.ide.testsupport;

import org.junit.Test;

import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public abstract class AbstractLoadedClassStructureTest {
  protected abstract String getClassName();

  protected String getExpectedSuperclassSimpleName() {
    return null;
  }

  protected boolean isExpectedAbstract() {
    return false;
  }

  protected int getMinimumDeclaredMethodCount() {
    return 1;
  }

  protected int getMinimumDeclaredConstructorCount() {
    return 1;
  }

  protected int getMinimumDeclaredFieldCount() {
    return 0;
  }

  protected String[] getExpectedInterfaceSimpleNames() {
    return new String[0];
  }

  private Class<?> loadClass() throws ClassNotFoundException {
    return Class.forName(getClassName());
  }

  @Test
  public void classHasExpectedHierarchy() throws Exception {
    Class<?> cls = loadClass();
    if (getExpectedSuperclassSimpleName() != null) {
      assertEquals(getExpectedSuperclassSimpleName(), cls.getSuperclass().getSimpleName());
    }
    assertEquals(isExpectedAbstract(), Modifier.isAbstract(cls.getModifiers()));

    Set<String> interfaceNames = Arrays.stream(cls.getInterfaces())
        .map(Class::getSimpleName)
        .collect(Collectors.toSet());
    for (String interfaceName : getExpectedInterfaceSimpleNames()) {
      assertTrue("Expected interface " + interfaceName, interfaceNames.contains(interfaceName));
    }
  }

  @Test
  public void classDeclaresExpectedMembers() throws Exception {
    Class<?> cls = loadClass();
    assertTrue("Declared method count", cls.getDeclaredMethods().length >= getMinimumDeclaredMethodCount());
    assertTrue("Declared constructor count", cls.getDeclaredConstructors().length >= getMinimumDeclaredConstructorCount());
    assertTrue("Declared field count", cls.getDeclaredFields().length >= getMinimumDeclaredFieldCount());
  }
}
