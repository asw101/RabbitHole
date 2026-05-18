package org.lgna.project.ast;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class JavaTypeDeepTest {
  private interface Marker {
  }

  private interface SecondaryMarker {
  }

  private abstract static class AbstractFixture implements Marker {
    public int number;
    private String hidden = "hidden";

    public AbstractFixture() {
    }

    public AbstractFixture(String hidden) {
      this.hidden = hidden;
    }

    protected String inherited(String value) {
      return value;
    }
  }

  private static class ConcreteFixture extends AbstractFixture implements Runnable, SecondaryMarker {
    public static final double STATIC_RATIO = 3.5;
    public String label = "label";

    public ConcreteFixture() {
    }

    public ConcreteFixture(String hidden) {
      super(hidden);
    }

    public int ownMethod() {
      return 7;
    }

    @Override
    public void run() {
    }
  }

  private static final class FinalFixture {
  }

  private static strictfp class StrictFixture {
  }

  private enum SampleEnum {
    ALPHA,
    BETA
  }

  private static final class OuterFixture {
    private static final class NestedFixture {
    }
  }

  @Test
  public void getInstanceReturnsNullForNullInputs() {
    assertNull(JavaType.getInstance((Class<?>) null));
    assertNull(JavaType.getInstance((ClassReflectionProxy) null));
  }

  @Test
  public void getInstanceCachesByClass() {
    JavaType first = JavaType.getInstance(String.class);
    JavaType second = JavaType.getInstance(String.class);

    assertSame(first, second);
    assertSame(JavaType.STRING_TYPE, first);
  }

  @Test
  public void getInstanceCachesByReflectionProxy() {
    ClassReflectionProxy proxy = new ClassReflectionProxy(ConcreteFixture.class);

    JavaType byProxy = JavaType.getInstance(proxy);
    JavaType byClass = JavaType.getInstance(ConcreteFixture.class);

    assertSame(byClass, byProxy);
    assertSame(byProxy, JavaType.getInstance(proxy));
  }

  @Test
  public void primitiveAndStandardConstantsResolveExpectedTypes() {
    assertSame(JavaType.VOID_TYPE, JavaType.getInstance(Void.TYPE));
    assertSame(JavaType.BOOLEAN_PRIMITIVE_TYPE, JavaType.getInstance(Boolean.TYPE));
    assertSame(JavaType.INTEGER_PRIMITIVE_TYPE, JavaType.getInstance(Integer.TYPE));
    assertSame(JavaType.DOUBLE_PRIMITIVE_TYPE, JavaType.getInstance(Double.TYPE));
    assertSame(JavaType.STRING_TYPE, JavaType.getInstance(String.class));
    assertSame(JavaType.OBJECT_TYPE, JavaType.getInstance(Object.class));
  }

  @Test
  public void getInstancesPreservesOrderForClasses() {
    JavaType[] types = JavaType.getInstances(new Class<?>[] {String.class, Integer.TYPE, Boolean.class});

    assertArrayEquals(
        new JavaType[] {
            JavaType.STRING_TYPE,
            JavaType.INTEGER_PRIMITIVE_TYPE,
            JavaType.BOOLEAN_OBJECT_TYPE
        },
        types);
  }

  @Test
  public void getInstancesPreservesOrderForReflectionProxies() {
    JavaType[] types = JavaType.getInstances(new ClassReflectionProxy[] {
        new ClassReflectionProxy(String.class),
        new ClassReflectionProxy(ConcreteFixture.class)
    });

    assertSame(JavaType.STRING_TYPE, types[0]);
    assertSame(JavaType.getInstance(ConcreteFixture.class), types[1]);
  }

  @Test
  public void getNameUsesSimpleName() {
    assertEquals("String", JavaType.STRING_TYPE.getName());
    assertEquals("ConcreteFixture", JavaType.getInstance(ConcreteFixture.class).getName());
    assertEquals("int", JavaType.INTEGER_PRIMITIVE_TYPE.getName());
  }

  @Test
  public void packageAndReflectionProxyReifyWrappedClass() {
    JavaType type = JavaType.getInstance(ConcreteFixture.class);

    assertEquals(ConcreteFixture.class.getPackage().getName(), type.getPackage().getName());
    assertSame(ConcreteFixture.class, type.getClassReflectionProxy().getReification());
  }

  @Test
  public void contentEqualityAndEquivalenceTrackWrappedClass() {
    JavaType concrete = JavaType.getInstance(ConcreteFixture.class);

    assertTrue(concrete.contentEquals(ConcreteFixture.class));
    assertFalse(concrete.contentEquals(String.class));
    assertTrue(concrete.isEquivalentTo(JavaType.getInstance(ConcreteFixture.class)));
    assertFalse(concrete.isEquivalentTo(JavaType.getInstance(String.class)));
    assertFalse(concrete.isEquivalentTo("not a type"));
  }

  @Test
  public void wrapperNormalizationMakesPrimitiveAndWrapperAssignable() {
    assertTrue(JavaType.INTEGER_PRIMITIVE_TYPE.isAssignableFrom(JavaType.INTEGER_OBJECT_TYPE));
    assertTrue(JavaType.INTEGER_OBJECT_TYPE.isAssignableFrom(JavaType.INTEGER_PRIMITIVE_TYPE));
    assertTrue(JavaType.BOOLEAN_PRIMITIVE_TYPE.isAssignableTo(JavaType.BOOLEAN_OBJECT_TYPE));
    assertFalse(JavaType.INTEGER_PRIMITIVE_TYPE.isAssignableFrom(JavaType.DOUBLE_OBJECT_TYPE));
  }

  @Test
  public void assignabilityTracksJavaInheritance() {
    JavaType abstractType = JavaType.getInstance(AbstractFixture.class);
    JavaType concreteType = JavaType.getInstance(ConcreteFixture.class);

    assertTrue(abstractType.isAssignableFrom(concreteType));
    assertTrue(concreteType.isAssignableTo(abstractType));
    assertFalse(concreteType.isAssignableFrom(abstractType));
    assertFalse(JavaType.STRING_TYPE.isAssignableFrom(concreteType));
  }

  @Test
  public void declaredConstructorsContainExpectedOverloadsAndAreCached() {
    JavaType type = JavaType.getInstance(ConcreteFixture.class);

    List<JavaConstructor> constructors = type.getDeclaredConstructors();
    List<JavaConstructor> constructorsAgain = type.getDeclaredConstructors();

    assertSame(constructors, constructorsAgain);
    assertNotNull(type.getDeclaredConstructor());
    assertNotNull(type.getDeclaredConstructor(String.class));
    assertEquals(2, constructors.size());
  }

  @Test
  public void declaredMethodsContainPublicAndProtectedMethodsAndAreCached() {
    JavaType type = JavaType.getInstance(ConcreteFixture.class);

    List<JavaMethod> methods = type.getDeclaredMethods();
    List<JavaMethod> methodsAgain = type.getDeclaredMethods();

    assertSame(methods, methodsAgain);
    assertTrue(methods.stream().anyMatch(m -> "ownMethod".equals(m.getName())));
    assertTrue(methods.stream().anyMatch(m -> "run".equals(m.getName())));
    // inherited() is declared on AbstractFixture, not ConcreteFixture
    assertFalse(methods.stream().anyMatch(m -> "inherited".equals(m.getName())));
  }

  @Test
  public void declaredMethodListIsUnmodifiable() {
    List<JavaMethod> methods = JavaType.getInstance(String.class).getDeclaredMethods();

    try {
      methods.add(null);
      fail();
    } catch (UnsupportedOperationException expected) {
    }
  }

  @Test
  public void declaredFieldsContainExpectedMembersAndAreCached() {
    JavaType type = JavaType.getInstance(ConcreteFixture.class);

    List<JavaField> fields = type.getDeclaredFields();
    List<JavaField> fieldsAgain = type.getDeclaredFields();

    assertSame(fields, fieldsAgain);
    assertTrue(fields.stream().anyMatch(f -> "STATIC_RATIO".equals(f.getName())));
    assertTrue(fields.stream().anyMatch(f -> "label".equals(f.getName())));
  }

  @Test
  public void declaredFieldListIsUnmodifiable() {
    List<JavaField> fields = JavaType.getInstance(ConcreteFixture.class).getDeclaredFields();

    try {
      fields.clear();
      fail();
    } catch (UnsupportedOperationException expected) {
    }
  }

  @Test
  public void interfaceAbstractEnumAndArrayFlagsReflectUnderlyingClass() {
    assertTrue(JavaType.getInstance(Marker.class).isInterface());
    assertTrue(JavaType.getInstance(AbstractFixture.class).isAbstract());
    assertTrue(JavaType.getInstance(SampleEnum.class).isEnum());
    assertTrue(JavaType.getInstance(String[].class).isArray());
    assertFalse(JavaType.getInstance(ConcreteFixture.class).isEnum());
  }

  @Test
  public void componentTypeAndArrayTypeRoundTrip() {
    JavaType arrayType = JavaType.getInstance(String[].class);

    assertSame(JavaType.STRING_TYPE, arrayType.getComponentType());
    assertSame(arrayType, JavaType.STRING_TYPE.getArrayType());
    assertSame(JavaType.getInstance(String[][].class), arrayType.getArrayType());
  }

  @Test
  public void superTypeAndEnclosingTypeResolve() {
    JavaType concrete = JavaType.getInstance(ConcreteFixture.class);
    JavaType nested = JavaType.getInstance(OuterFixture.NestedFixture.class);

    assertSame(JavaType.getInstance(AbstractFixture.class), concrete.getSuperType());
    assertSame(JavaType.getInstance(OuterFixture.class), nested.getEnclosingType());
    assertNull(JavaType.OBJECT_TYPE.getSuperType());
  }

  @Test
  public void directInterfacesAreExposed() {
    JavaType[] interfaces = JavaType.getInstance(ConcreteFixture.class).getInterfaces();

    assertEquals(2, interfaces.length);
    assertSame(JavaType.getInstance(Runnable.class), interfaces[0]);
    assertSame(JavaType.getInstance(SecondaryMarker.class), interfaces[1]);
  }

  @Test
  public void modifiersAndAccessLevelReflectWrappedClass() {
    assertEquals(AccessLevel.PRIVATE, JavaType.getInstance(OuterFixture.NestedFixture.class).getAccessLevel());
    assertTrue(JavaType.getInstance(OuterFixture.NestedFixture.class).isStatic());
    assertTrue(JavaType.getInstance(FinalFixture.class).isFinal());
    // JDK 17+ (JEP 306) removed ACC_STRICT; strictfp is always on
    assertFalse(JavaType.getInstance(StrictFixture.class).isStrictFloatingPoint());
    assertTrue(JavaType.INTEGER_PRIMITIVE_TYPE.isPrimitive());
  }

  @Test
  public void userAuthoredAndNamePropertyBehaveAsExpected() {
    JavaType type = JavaType.getInstance(ConcreteFixture.class);

    assertFalse(type.isUserAuthored());
    assertNull(type.getNamePropertyIfItExists());
  }
}
