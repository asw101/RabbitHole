package org.lgna.project.ast;

import org.junit.Test;

import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * TDD contract tests for AstMethodLookupHelpers.
 * These tests define the behavioral contract that the extracted helper class
 * must satisfy. They will FAIL until AstMethodLookupHelpers is created.
 */
public class AstMethodLookupHelpersTest {

  // ── lookupMethod ──────────────────────────────────────────────────────

  @Test
  public void lookupMethodFindsStringTrim() {
    JavaMethod method = AstMethodLookupHelpers.lookupMethod(String.class, "trim");
    assertNotNull("Should find String.trim()", method);
    assertEquals("trim", method.getName());
  }

  @Test
  public void lookupMethodFindsStringValueOfInt() {
    JavaMethod method = AstMethodLookupHelpers.lookupMethod(String.class, "valueOf", int.class);
    assertNotNull("Should find String.valueOf(int)", method);
    assertEquals("valueOf", method.getName());
  }

  @Test
  public void lookupMethodFindsStringSubstringTwoArgs() {
    JavaMethod method = AstMethodLookupHelpers.lookupMethod(String.class, "substring", int.class, int.class);
    assertNotNull("Should find String.substring(int,int)", method);
    assertEquals("substring", method.getName());
  }

  @Test
  public void lookupMethodFindsNoArgMethod() {
    JavaMethod method = AstMethodLookupHelpers.lookupMethod(Object.class, "hashCode");
    assertNotNull("Should find Object.hashCode()", method);
    assertEquals("hashCode", method.getName());
  }

  @Test
  public void lookupMethodResultMatchesJavaMethodGetInstance() {
    // Contract: lookupMethod must delegate to JavaMethod.getInstance
    JavaMethod viaHelper = AstMethodLookupHelpers.lookupMethod(String.class, "length");
    JavaMethod viaDirect = JavaMethod.getInstance(String.class, "length");
    assertSame("lookupMethod must return same instance as JavaMethod.getInstance",
        viaDirect, viaHelper);
  }

  // ── getSingleAbstractMethod ───────────────────────────────────────────

  @Test
  public void getSingleAbstractMethodFindsRunnableRun() {
    JavaType runnableType = JavaType.getInstance(Runnable.class);
    AbstractMethod sam = AstMethodLookupHelpers.getSingleAbstractMethod(runnableType);
    assertNotNull("Runnable has a single abstract method", sam);
    assertEquals("run", sam.getName());
  }

  @Test
  public void getSingleAbstractMethodReturnsAbstractMethod() {
    JavaType runnableType = JavaType.getInstance(Runnable.class);
    AbstractMethod sam = AstMethodLookupHelpers.getSingleAbstractMethod(runnableType);
    assertTrue("The returned method must be abstract", sam.isAbstract());
  }

  @Test
  public void getSingleAbstractMethodWorksWithComparable() {
    JavaType comparableType = JavaType.getInstance(Comparable.class);
    AbstractMethod sam = AstMethodLookupHelpers.getSingleAbstractMethod(comparableType);
    assertNotNull("Comparable has a single abstract method", sam);
    assertEquals("compareTo", sam.getName());
  }

  // ── getOverridenMethod ────────────────────────────────────────────────

  @Test
  public void getOverridenMethodFindsParentMethod() {
    // String.toString() overrides Object.toString()
    JavaMethod stringToString = JavaMethod.getInstance(String.class, "toString");
    AbstractMethod overridden = AstMethodLookupHelpers.getOverridenMethod(stringToString);
    assertNotNull("String.toString() overrides Object.toString()", overridden);
    assertEquals("toString", overridden.getName());
  }

  @Test
  public void getOverridenMethodReturnsNullWhenNoOverride() {
    // String.length() does not override any parent method
    JavaMethod stringLength = JavaMethod.getInstance(String.class, "length");
    AbstractMethod overridden = AstMethodLookupHelpers.getOverridenMethod(stringLength);
    assertNull("String.length() does not override a parent method", overridden);
  }

  @Test
  public void getOverridenMethodReturnsDifferentInstance() {
    JavaMethod stringToString = JavaMethod.getInstance(String.class, "toString");
    AbstractMethod overridden = AstMethodLookupHelpers.getOverridenMethod(stringToString);
    assertNotNull(overridden);
    // The overridden method should come from Object, not String
    assertNotSame("Overridden method should be from a parent type, not same instance",
        stringToString, overridden);
  }

  // ── getAllInvokedMethods ──────────────────────────────────────────────

  @Test
  public void getAllInvokedMethodsReturnsEmptyForEmptyBody() {
    UserMethod emptyMethod = new UserMethod("doNothing", JavaType.VOID_TYPE,
        new UserParameter[]{}, new BlockStatement());
    Set<UserMethod> invoked = AstMethodLookupHelpers.getAllInvokedMethods(emptyMethod);
    assertNotNull(invoked);
    assertTrue("Empty method body invokes no user methods", invoked.isEmpty());
  }

  @Test
  public void getAllInvokedMethodsFindsDirectInvocation() {
    // Create a target method
    UserMethod target = new UserMethod("helper", JavaType.VOID_TYPE,
        new UserParameter[]{}, new BlockStatement());

    // Create a caller that invokes the target
    MethodInvocation invocation = new MethodInvocation(new ThisExpression(), target);
    BlockStatement body = new BlockStatement(new ExpressionStatement(invocation));
    UserMethod caller = new UserMethod("main", JavaType.VOID_TYPE,
        new UserParameter[]{}, body);

    Set<UserMethod> invoked = AstMethodLookupHelpers.getAllInvokedMethods(caller);
    assertNotNull(invoked);
    assertTrue("Should find the directly invoked user method", invoked.contains(target));
  }

  @Test
  public void getAllInvokedMethodsFindsTransitiveInvocations() {
    // C calls B, B calls A — getAllInvokedMethods(C) should find both A and B
    UserMethod methodA = new UserMethod("a", JavaType.VOID_TYPE,
        new UserParameter[]{}, new BlockStatement());

    MethodInvocation invokeA = new MethodInvocation(new ThisExpression(), methodA);
    UserMethod methodB = new UserMethod("b", JavaType.VOID_TYPE,
        new UserParameter[]{}, new BlockStatement(new ExpressionStatement(invokeA)));

    MethodInvocation invokeB = new MethodInvocation(new ThisExpression(), methodB);
    UserMethod methodC = new UserMethod("c", JavaType.VOID_TYPE,
        new UserParameter[]{}, new BlockStatement(new ExpressionStatement(invokeB)));

    Set<UserMethod> invoked = AstMethodLookupHelpers.getAllInvokedMethods(methodC);
    assertTrue("Should find directly invoked method B", invoked.contains(methodB));
    assertTrue("Should find transitively invoked method A", invoked.contains(methodA));
  }

  @Test
  public void getAllInvokedMethodsDoesNotIncludeSeed() {
    UserMethod self = new UserMethod("doNothing", JavaType.VOID_TYPE,
        new UserParameter[]{}, new BlockStatement());
    Set<UserMethod> invoked = AstMethodLookupHelpers.getAllInvokedMethods(self);
    assertFalse("Seed method should not be in the result set", invoked.contains(self));
  }

  @Test
  public void getAllInvokedMethodsHandlesCyclicInvocations() {
    // Create two methods that call each other — must not infinite-loop
    UserMethod methodA = new UserMethod("a", JavaType.VOID_TYPE,
        new UserParameter[]{}, new BlockStatement());
    UserMethod methodB = new UserMethod("b", JavaType.VOID_TYPE,
        new UserParameter[]{}, new BlockStatement());

    // Wire A to call B
    MethodInvocation invokeB = new MethodInvocation(new ThisExpression(), methodB);
    methodA.body.getValue().statements.add(new ExpressionStatement(invokeB));

    // Wire B to call A
    MethodInvocation invokeA = new MethodInvocation(new ThisExpression(), methodA);
    methodB.body.getValue().statements.add(new ExpressionStatement(invokeA));

    Set<UserMethod> invoked = AstMethodLookupHelpers.getAllInvokedMethods(methodA);
    // Should terminate and contain both methods
    assertTrue("Should find B", invoked.contains(methodB));
    // A is found transitively via B→A, but not as the seed
  }

  // ── getAllMethods ─────────────────────────────────────────────────────

  @Test
  public void getAllMethodsReturnsNonEmptyForObject() {
    JavaType objectType = JavaType.getInstance(Object.class);
    List<AbstractMethod> methods = AstMethodLookupHelpers.getAllMethods(objectType);
    assertNotNull(methods);
    assertFalse("Object should have at least some methods", methods.isEmpty());
  }

  @Test
  public void getAllMethodsIncludesDeclaredAndInheritedMethods() {
    JavaType stringType = JavaType.getInstance(String.class);
    List<AbstractMethod> methods = AstMethodLookupHelpers.getAllMethods(stringType);
    assertNotNull(methods);

    // String has its own methods plus inherited Object methods
    boolean hasStringMethod = false;
    boolean hasObjectMethod = false;
    for (AbstractMethod m : methods) {
      if ("length".equals(m.getName())) {
        hasStringMethod = true;
      }
      if ("hashCode".equals(m.getName())) {
        hasObjectMethod = true;
      }
    }
    assertTrue("Should include String's own method 'length'", hasStringMethod);
    assertTrue("Should include inherited method 'hashCode'", hasObjectMethod);
  }

  @Test
  public void getAllMethodsCountExceedsDeclaredMethodsCount() {
    JavaType stringType = JavaType.getInstance(String.class);
    List<AbstractMethod> allMethods = AstMethodLookupHelpers.getAllMethods(stringType);
    List<JavaMethod> declaredMethods = stringType.getDeclaredMethods();

    assertTrue("Total methods (including inherited) should exceed declared methods",
        allMethods.size() > declaredMethods.size());
  }

  // ── Class structure contract ──────────────────────────────────────────

  @Test
  public void classIsFinal() {
    assertTrue("AstMethodLookupHelpers should be final",
        java.lang.reflect.Modifier.isFinal(AstMethodLookupHelpers.class.getModifiers()));
  }

  @Test(expected = java.lang.reflect.InvocationTargetException.class)
  public void constructorThrowsAssertionError() throws Exception {
    java.lang.reflect.Constructor<?> ctor = AstMethodLookupHelpers.class.getDeclaredConstructor();
    ctor.setAccessible(true);
    ctor.newInstance();
  }

  @Test
  public void allPublicMethodsAreStatic() {
    for (java.lang.reflect.Method m : AstMethodLookupHelpers.class.getDeclaredMethods()) {
      if (java.lang.reflect.Modifier.isPublic(m.getModifiers())) {
        assertTrue("Public method " + m.getName() + " must be static",
            java.lang.reflect.Modifier.isStatic(m.getModifiers()));
      }
    }
  }
}
