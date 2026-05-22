package org.alice.ide.icons;

import org.junit.Test;
import org.lgna.project.ast.JavaType;
import org.lgna.story.resources.ModelResource;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import static org.junit.Assert.*;

public class IconFactoryManagerResourceDeclarationBehaviorTest {
  private enum StubEnumResource implements ModelResource {
    ONE,
    TWO
  }

  private static final class StubResource implements ModelResource {
  }

  private static Object createInner(String simpleName, Class<?>[] parameterTypes, Object... args) throws Exception {
    Class<?> cls = Class.forName("org.alice.ide.icons.IconFactoryManager$" + simpleName);
    Constructor<?> constructor = cls.getDeclaredConstructor(parameterTypes);
    constructor.setAccessible(true);
    return constructor.newInstance(args);
  }

  private static Object invokeZeroArgMethod(Object target, String name) throws Exception {
    Class<?> cls = target.getClass();
    while (cls != null) {
      try {
        Method method = cls.getDeclaredMethod(name);
        method.setAccessible(true);
        return method.invoke(target);
      } catch (NoSuchMethodException noSuchMethodException) {
        cls = cls.getSuperclass();
      }
    }
    throw new NoSuchMethodException(name);
  }

  @Test
  public void resourceTypeEqualityAndIdentifiersAreValueBased() throws Exception {
    Object first = createInner("ResourceType", new Class<?>[] {Class.class}, StubEnumResource.class);
    Object second = createInner("ResourceType", new Class<?>[] {Class.class}, StubEnumResource.class);
    Object different = createInner("ResourceType", new Class<?>[] {Class.class}, StubResource.class);

    assertEquals(first, second);
    assertEquals(first.hashCode(), second.hashCode());
    assertNotEquals(first, different);
    assertSame(StubEnumResource.class, invokeZeroArgMethod(first, "getModelResourceClass"));
    assertNull(invokeZeroArgMethod(first, "getModelResourceName"));
  }

  @Test
  public void resourceEnumConstantAndInstanceUseValueEquality() throws Exception {
    ModelResource enumResource = StubEnumResource.ONE;
    ModelResource instanceResource = new StubResource();
    Object enumConstantA = createInner("ResourceEnumConstant", new Class<?>[] {ModelResource.class}, enumResource);
    Object enumConstantB = createInner("ResourceEnumConstant", new Class<?>[] {ModelResource.class}, enumResource);
    Object instanceA = createInner("ResourceInstance", new Class<?>[] {ModelResource.class}, instanceResource);
    Object instanceB = createInner("ResourceInstance", new Class<?>[] {ModelResource.class}, instanceResource);

    assertEquals(enumConstantA, enumConstantB);
    assertEquals(enumConstantA.hashCode(), enumConstantB.hashCode());
    assertEquals(instanceA, instanceB);
    assertEquals(instanceA.hashCode(), instanceB.hashCode());
    assertEquals("ONE", invokeZeroArgMethod(enumConstantA, "getModelResourceName"));
    assertSame(StubEnumResource.class, invokeZeroArgMethod(enumConstantA, "getModelResourceClass"));
  }

  @Test
  public void nullTypeAndMissingRegistrationsFallBackSafely() {
    assertSame(IconFactoryManager.getIconFactoryForType(null), org.lgna.croquet.icon.EmptyIconFactory.getInstance());
    assertNull(IconFactoryManager.getRegisteredIconFactory(JavaType.getInstance(Void.class)));
  }
}
