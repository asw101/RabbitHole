package org.lgna.project.reflect;

import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class ClassInfoManagerTest {
  public static class ReflectionFixture {
    public ReflectionFixture() {
    }

    public ReflectionFixture(String value) {
    }

    public String echo(String value) {
      return value;
    }

    public int count() {
      return 3;
    }
  }

  @Test
  public void getInstanceReturnsNullForUnknownValues() {
    assertNull(ClassInfoManager.getInstance((String) null));
    assertNull(ClassInfoManager.getInstance((Class<?>) null));
    assertNull(ClassInfoManager.getInstance("missing.Type"));
    assertNull(ClassInfoManager.getMethodInfos((Class<?>) null));
  }

  @Test
  public void addClassInfosStoresAndReturnsSameCachedInfo() {
    ClassInfo info = createFixtureInfo();

    ClassInfoManager.addClassInfos(new ClassInfo[] {info});

    assertSame(info, ClassInfoManager.getInstance(ReflectionFixture.class.getName()));
    assertSame(info, ClassInfoManager.getInstance(ReflectionFixture.class));
    assertSame(ClassInfoManager.getInstance(ReflectionFixture.class), ClassInfoManager.getInstance(ReflectionFixture.class));
  }

  @Test
  public void getMethodInfosReturnsInitializedUnmodifiableList() {
    ClassInfo info = createFixtureInfo();
    ClassInfoManager.addClassInfos(new ClassInfo[] {info});

    List<MethodInfo> methods = ClassInfoManager.getMethodInfos(ReflectionFixture.class);

    assertEquals(2, methods.size());
    assertNotNull(methods.get(0).getMthd());
    try {
      methods.add(new MethodInfo("other", new String[0], new String[0]));
      fail();
    } catch (UnsupportedOperationException expected) {
    }
  }

  @Test
  public void lookupInfoFindsExactMethod() throws Exception {
    ClassInfo info = createFixtureInfo();
    ClassInfoManager.addClassInfos(new ClassInfo[] {info});
    Method echo = ReflectionFixture.class.getMethod("echo", String.class);

    MethodInfo methodInfo = info.lookupInfo(echo);

    assertNotNull(methodInfo);
    assertEquals("echo", methodInfo.getName());
    assertEquals(echo, methodInfo.getMthd());
    assertArrayEquals(new String[] {"value"}, methodInfo.getParameterNames());
  }

  @Test
  public void lookupInfoFindsExactConstructor() throws Exception {
    ClassInfo info = createFixtureInfo();
    ClassInfoManager.addClassInfos(new ClassInfo[] {info});
    Constructor<?> constructor = ReflectionFixture.class.getConstructor(String.class);

    ConstructorInfo constructorInfo = info.lookupInfo(constructor);

    assertNotNull(constructorInfo);
    assertEquals(constructor, constructorInfo.getCnstrctr());
    assertArrayEquals(new String[] {"value"}, constructorInfo.getParameterNames());
  }

  @Test
  public void methodInfoToStringIncludesMethodNameAndParameters() {
    MethodInfo methodInfo = new MethodInfo("echo", new String[] {String.class.getName()}, new String[] {"value"});

    assertTrue(methodInfo.toString().contains("echo"));
    assertTrue(methodInfo.toString().contains(String.class.getName()));
    assertTrue(methodInfo.toString().contains("value"));
  }

  @Test
  public void constructorInfoToStringIncludesParameterMetadata() {
    ConstructorInfo constructorInfo = new ConstructorInfo(new String[] {String.class.getName()}, new String[] {"value"});

    assertTrue(constructorInfo.toString().contains(String.class.getName()));
    assertTrue(constructorInfo.toString().contains("value"));
  }

  @Test
  public void classInfoToStringIncludesClassAndMethodNames() {
    ClassInfo info = createFixtureInfo();
    ClassInfoManager.addClassInfos(new ClassInfo[] {info});

    String text = info.toString();

    assertTrue(text.contains(ReflectionFixture.class.getName()));
    assertTrue(text.contains("echo"));
    assertTrue(text.contains("count"));
  }

  @Test
  public void missingClassInfoStillRegistersSafely() {
    ClassInfo missing = new ClassInfo(
        "missing.Type",
        Arrays.asList(new ConstructorInfo(new String[] {String.class.getName()}, new String[] {"value"})),
        Arrays.asList(new MethodInfo("ghost", new String[0], new String[0])));

    ClassInfoManager.addClassInfos(new ClassInfo[] {missing});

    assertSame(missing, ClassInfoManager.getInstance("missing.Type"));
    assertNull(missing.lookupInfo(getAnyFixtureMethod()));
    assertTrue(missing.toString().contains("missing.Type"));
  }

  @Test
  public void lookupInfoReturnsNullForUnknownMethod() throws Exception {
    ClassInfo info = createFixtureInfo();
    ClassInfoManager.addClassInfos(new ClassInfo[] {info});
    Method missing = Object.class.getMethod("toString");

    assertNull(info.lookupInfo(missing));
  }

  private static ClassInfo createFixtureInfo() {
    return new ClassInfo(
        ReflectionFixture.class.getName(),
        Arrays.asList(
            new ConstructorInfo(new String[0], new String[0]),
            new ConstructorInfo(new String[] {String.class.getName()}, new String[] {"value"})),
        Arrays.asList(
            new MethodInfo("echo", new String[] {String.class.getName()}, new String[] {"value"}),
            new MethodInfo("count", new String[0], new String[0])));
  }

  private static Method getAnyFixtureMethod() {
    try {
      return ReflectionFixture.class.getMethod("count");
    } catch (NoSuchMethodException e) {
      throw new AssertionError(e);
    }
  }
}
