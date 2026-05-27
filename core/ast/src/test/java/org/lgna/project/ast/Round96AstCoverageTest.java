package org.lgna.project.ast;

import org.junit.Test;
import org.lgna.project.annotations.ClassTemplate;
import org.lgna.project.code.CodeOrganizer;

import java.lang.reflect.Method;
import java.util.List;

import static org.junit.Assert.*;

public class Round96AstCoverageTest {
  static final class KeywordFactory {
    public static KeywordValue tone() {
      return new KeywordValue();
    }

    public static KeywordValue duration() {
      return new KeywordValue();
    }

    static KeywordValue hidden() {
      return new KeywordValue();
    }

    public KeywordValue instanceValue() {
      return new KeywordValue();
    }

    public static String wrongType() {
      return "wrong";
    }
  }

  @ClassTemplate(keywordFactoryCls = KeywordFactory.class)
  static final class KeywordValue {
  }

  static final class KeyedFixture {
    public void keyed(KeywordValue... details) {
    }

    public int getValue() {
      return 1;
    }

    public void setValue(int value) {
    }
  }

  private static final class RecordingProcessor implements AstProcessor {
    private boolean sawSuperReference;

    @Override
    public CodeOrganizer getNewCodeOrganizerForTypeName(String typeName) {
      return null;
    }

    @Override
    public void processSuperReference() {
      this.sawSuperReference = true;
    }
  }

  private static Method declaredMethod(
      Class<?> declaringClass,
      String name,
      Class<?>... parameterTypes) {
    try {
      return declaringClass.getDeclaredMethod(name, parameterTypes);
    } catch (NoSuchMethodException exception) {
      throw new AssertionError(exception);
    }
  }

  private static JavaMethod javaMethod(
      Class<?> declaringClass,
      String name,
      Class<?>... parameterTypes) {
    return JavaMethod.getInstance(
        declaredMethod(declaringClass, name, parameterTypes));
  }

  @Test
  public void anonymousUserTypeCreatesAndCachesImplicitConstructor() {
    UserMethod method = AstUtilities.createProcedure("step");
    UserField field = new UserField(
        "count",
        JavaType.INTEGER_OBJECT_TYPE,
        new NullLiteral());
    AnonymousUserType type = new AnonymousUserType(
        Object.class,
        new UserMethod[] {method},
        new UserField[] {field});

    List<AnonymousUserConstructor> first = type.getDeclaredConstructors();
    List<AnonymousUserConstructor> second = type.getDeclaredConstructors();

    assertEquals(1, first.size());
    assertSame(first, second);
    assertNotSame(first.get(0), AnonymousUserConstructor.get(type));
    assertSame(type, first.get(0).getDeclaringType());
    assertTrue(first.get(0).isSignatureLocked());
    assertTrue(first.get(0).getRequiredParameters().isEmpty());
    assertNull(first.get(0).getNextLongerInChain());
    assertNull(first.get(0).getNextShorterInChain());
    assertNull(first.get(0).getAccessLevel());
    assertNull(first.get(0).getVisibility());
    assertNull(first.get(0).getVariableLengthParameter());
    assertNull(first.get(0).getKeyedParameter());
    assertTrue(first.get(0).isUserAuthored());

    try {
      first.get(0).evaluate(null, null, new Object[0]);
      fail("anonymous constructor evaluation should remain unsupported");
    } catch (RuntimeException exception) {
      assertEquals("todo", exception.getMessage());
    }
  }

  @Test
  public void anonymousUserTypeExposesUserTypeTraits() {
    UserMethod method = AstUtilities.createProcedure("step");
    UserField field = new UserField(
        "count",
        JavaType.INTEGER_OBJECT_TYPE,
        new NullLiteral());
    AnonymousUserType type = new AnonymousUserType(
        Object.class,
        new UserMethod[] {method},
        new UserField[] {field});

    assertSame(JavaType.getInstance(Object.class), type.getSuperType());
    assertEquals(0, type.getInterfaces().length);
    assertSame(method, type.getDeclaredMethods().get(0));
    assertSame(field, type.getDeclaredFields().get(0));
    assertNull(type.getKeywordFactoryType());
    assertNull(type.getNamePropertyIfItExists());
    assertNull(type.getPackage());
    assertNull(type.getAccessLevel());
    assertTrue(type.isFollowToSuperClassDesired());
    assertFalse(type.isConsumptionBySubClassDesired());
    assertFalse(type.isPrimitive());
    assertFalse(type.isInterface());
    assertTrue(type.isUserAuthored());
    assertFalse(type.isArray());
    assertFalse(type.isEnum());
    assertFalse(type.isAbstract());
    assertFalse(type.isStatic());
    assertTrue(type.isFinal());
    assertFalse(type.isStrictFloatingPoint());
    assertNull(type.getComponentType());
    assertNotNull(type.getArrayType());
  }

  @Test
  public void keyedArgumentListOnlyCountsPublicStaticDetailMethods() {
    JavaMethod keyedMethod = javaMethod(
        KeyedFixture.class,
        "keyed",
        KeywordValue[].class);
    JavaMethod toneMethod = javaMethod(KeywordFactory.class, "tone");
    JavaMethod durationMethod = javaMethod(KeywordFactory.class, "duration");
    MethodInvocation invocation = new MethodInvocation(
        new NullLiteral(),
        keyedMethod,
        new SimpleArgument[0],
        null,
        null);

    assertNull(new JavaKeyedArgument().getKeyMethod());
    assertFalse(invocation.keyedArguments.areAllOptionalArgumentsFilled());

    invocation.keyedArguments.add(
        new JavaKeyedArgument(keyedMethod.getKeyedParameter(), toneMethod));
    assertSame(toneMethod, invocation.keyedArguments.get(0).getKeyMethod());
    assertFalse(invocation.keyedArguments.areAllOptionalArgumentsFilled());

    invocation.keyedArguments.add(
        new JavaKeyedArgument(
            keyedMethod.getKeyedParameter(),
            durationMethod));
    assertTrue(invocation.keyedArguments.areAllOptionalArgumentsFilled());
  }

  @Test
  public void superExpressionResolvesParentSuperTypeAndDispatchesProcessor() {
    NamedUserType parent = AstUtilities.createType(
        "Parent",
        JavaType.getInstance(Object.class));
    NamedUserType child = AstUtilities.createType("Child", parent);
    UserMethod method = AstUtilities.createProcedure("inspect");
    SuperExpression expression = new SuperExpression();
    RecordingProcessor processor = new RecordingProcessor();

    method.body.getValue().statements.add(new ExpressionStatement(expression));
    child.methods.add(method);

    assertSame(parent, expression.getType());
    assertEquals("super", expression.getRepr());

    expression.process(processor);

    assertTrue(processor.sawSuperReference);
  }

  @Test
  public void packageReflectionProxyReifiesAndFallsBackToNames() {
    Package langPackage = String.class.getPackage();
    PackageReflectionProxy reified = new PackageReflectionProxy(langPackage);
    PackageReflectionProxy fromName = new PackageReflectionProxy("java.lang");
    PackageReflectionProxy missing = new PackageReflectionProxy("missing.pkg");

    assertEquals("java.lang", reified.getName());
    assertSame(langPackage, reified.getReification());
    assertSame(langPackage, fromName.getReification());
    assertEquals(reified, fromName);
    assertEquals(reified.hashCode(), fromName.hashCode());
    assertEquals(
        "PackageReflectionProxy[name=java.lang]",
        reified.toString());

    assertEquals("missing.pkg", missing.getName());
    assertNull(missing.getReification());
    assertEquals(missing, new PackageReflectionProxy("missing.pkg"));
    assertNotEquals(missing, reified);
  }

  @Test
  public void enumPoliciesAndGetterSetterPairsReportConfiguration() {
    JavaType beanType = JavaType.getInstance(KeyedFixture.class);
    JavaMethod getter = beanType.getDeclaredMethod("getValue");
    JavaMethod setter = beanType.getDeclaredMethod("setValue", Integer.TYPE);
    JavaGetterSetterPair pair = new JavaGetterSetterPair(getter, setter);

    assertTrue(DecodeIdPolicy.PRESERVE_IDS.isIdPreserved());
    assertFalse(DecodeIdPolicy.NEW_IDS.isIdPreserved());

    assertFalse(ManagementLevel.NONE.isGenerated());
    assertFalse(ManagementLevel.NONE.isManaged());
    assertTrue(ManagementLevel.GENERATED.isGenerated());
    assertFalse(ManagementLevel.GENERATED.isManaged());
    assertTrue(ManagementLevel.MANAGED.isGenerated());
    assertTrue(ManagementLevel.MANAGED.isManaged());

    assertSame(getter, pair.getGetter());
    assertSame(setter, pair.getSetter());
  }
}
