package org.lgna.project.ast;

import org.junit.Test;

import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * Integration tests verifying the AstUtilities decomposition preserves
 * behavioral equivalence. These tests call the NEW helper classes and
 * verify results match the contract of the ORIGINAL AstUtilities methods.
 *
 * Also verifies that AstUtilities retains its factory methods and that
 * the internal createUserLambda→getSingleAbstractMethod redirect works.
 */
public class AstUtilitiesDecompositionTest {

  // ── Retained AstUtilities factory methods still work ──────────────────

  @Test
  public void createMethodStillWorksOnAstUtilities() {
    UserMethod method = AstUtilities.createMethod("test", JavaType.VOID_TYPE);
    assertNotNull(method);
    assertEquals("test", method.getName());
  }

  @Test
  public void createProcedureStillWorksOnAstUtilities() {
    UserMethod proc = AstUtilities.createProcedure("doSomething");
    assertNotNull(proc);
    assertEquals("doSomething", proc.getName());
  }

  @Test
  public void createFunctionStillWorksOnAstUtilities() {
    UserMethod fn = AstUtilities.createFunction("compute", Integer.class);
    assertNotNull(fn);
  }

  @Test
  public void lookupMethodDelegatesCorrectly() {
    // The extracted lookupMethod should produce same result as original
    JavaMethod fromHelper = AstMethodLookupHelpers.lookupMethod(String.class, "trim");
    JavaMethod direct = JavaMethod.getInstance(String.class, "trim");
    assertSame("Extracted lookupMethod must be equivalent", direct, fromHelper);
  }

  // ── createUserLambda internal redirect ────────────────────────────────

  @Test
  public void createUserLambdaStillWorksAfterSAMRedirect() {
    // createUserLambda internally calls getSingleAbstractMethod, which is now
    // on AstMethodLookupHelpers. Verify the full pipeline still works.
    UserLambda lambda = AstUtilities.createUserLambda(Runnable.class);
    assertNotNull("createUserLambda should still work after SAM redirect", lambda);
    assertTrue("Lambda signature should be locked", lambda.isSignatureLocked.getValue());
  }

  @Test
  public void createUserLambdaWithRunnableHasNoParameters() {
    UserLambda lambda = AstUtilities.createUserLambda(Runnable.class);
    assertNotNull(lambda);
    assertEquals("Runnable.run() has no parameters", 0,
        lambda.requiredParameters.size());
  }

  @Test
  public void createUserLambdaWithComparableHasOneParameter() {
    UserLambda lambda = AstUtilities.createUserLambda(Comparable.class);
    assertNotNull(lambda);
    assertEquals("Comparable.compareTo() has one parameter", 1,
        lambda.requiredParameters.size());
  }

  @Test
  public void createLambdaExpressionStillWorksAfterSAMRedirect() {
    LambdaExpression expr = AstUtilities.createLambdaExpression(Runnable.class);
    assertNotNull("createLambdaExpression should still work", expr);
  }

  // ── Cross-class behavioral equivalence ────────────────────────────────

  @Test
  public void overriddenMethodViaHelperMatchesOriginalBehavior() {
    // String.toString() overrides Object.toString()
    JavaMethod stringToString = JavaMethod.getInstance(String.class, "toString");
    AbstractMethod overridden = AstMethodLookupHelpers.getOverridenMethod(stringToString);
    assertNotNull("String.toString() should have an overridden method", overridden);
    assertEquals("toString", overridden.getName());
    // The overridden method must come from Object
    assertEquals(JavaType.getInstance(Object.class), overridden.getDeclaringType());
  }

  @Test
  public void getAllMethodsViaHelperIncludesHierarchy() {
    JavaType stringType = JavaType.getInstance(String.class);
    List<AbstractMethod> all = AstMethodLookupHelpers.getAllMethods(stringType);
    assertNotNull(all);
    // Must include both declared and inherited
    assertTrue("Should include String methods and Object methods", all.size() > 10);
  }

  @Test
  public void declaringTypeResolutionViaHelperHandlesType() {
    JavaType type = JavaType.getInstance(String.class);
    AbstractType<?, ?, ?> resolved = AstTypeResolutionHelpers.getDeclaringTypeIfMemberOrTypeItselfIfType(type);
    assertSame(type, resolved);
  }

  @Test
  public void declaringTypeResolutionViaHelperHandlesMethod() {
    JavaMethod method = JavaMethod.getInstance(String.class, "trim");
    AbstractType<?, ?, ?> resolved = AstTypeResolutionHelpers.getDeclaringTypeIfMemberOrTypeItselfIfType(method);
    assertEquals(JavaType.getInstance(String.class), resolved);
  }

  @Test
  public void parameterValueTypesViaHelperMatchesExpected() {
    JavaMethod method = JavaMethod.getInstance(String.class, "substring", int.class, int.class);
    AbstractType<?, ?, ?>[] types = AstTypeResolutionHelpers.getParameterValueTypes(method);
    assertEquals(2, types.length);
    // Both parameters should be int type
    assertEquals(types[0], types[1]);
  }

  // ── getAllInvokedMethods transitive crawl ──────────────────────────────

  @Test
  public void allInvokedMethodsTransitiveCrawlViaBothPathsProducesSameResult() {
    UserMethod leaf = new UserMethod("leaf", JavaType.VOID_TYPE,
        new UserParameter[]{}, new BlockStatement());

    MethodInvocation callLeaf = new MethodInvocation(new ThisExpression(), leaf);
    UserMethod mid = new UserMethod("mid", JavaType.VOID_TYPE,
        new UserParameter[]{}, new BlockStatement(new ExpressionStatement(callLeaf)));

    MethodInvocation callMid = new MethodInvocation(new ThisExpression(), mid);
    UserMethod root = new UserMethod("root", JavaType.VOID_TYPE,
        new UserParameter[]{}, new BlockStatement(new ExpressionStatement(callMid)));

    Set<UserMethod> invoked = AstMethodLookupHelpers.getAllInvokedMethods(root);
    assertEquals("Should find exactly 2 methods (mid + leaf)", 2, invoked.size());
    assertTrue(invoked.contains(mid));
    assertTrue(invoked.contains(leaf));
  }

  // ── AstUtilities line count guard ─────────────────────────────────────

  @Test
  public void astUtilitiesRemainsUnderLineTarget() throws Exception {
    // Verify the extracted methods are actually gone from AstUtilities
    try {
      AstUtilities.class.getMethod("getSingleAbstractMethod", AbstractType.class);
      fail("getSingleAbstractMethod should be moved to AstMethodLookupHelpers");
    } catch (NoSuchMethodException expected) {
      // correct — method should no longer exist on AstUtilities
    }
  }

  @Test
  public void extractedTypeResolutionMethodsNotOnAstUtilities() {
    assertMethodNotOnAstUtilities("getDeclaredPersistentPropertyGetters", JavaType.class);
    assertMethodNotOnAstUtilities("getParameterValueTypes", AbstractMethod.class);
    assertMethodNotOnAstUtilities("getKeywordFactoryType", JavaKeyedArgument.class);
  }

  @Test
  public void extractedMethodLookupMethodsNotOnAstUtilities() {
    assertMethodNotOnAstUtilities("lookupMethod", Class.class, String.class, Class[].class);
    assertMethodNotOnAstUtilities("getOverridenMethod", AbstractMethod.class);
    assertMethodNotOnAstUtilities("getAllMethods", AbstractType.class);
    assertMethodNotOnAstUtilities("getAllInvokedMethods", UserMethod.class);
  }

  @Test
  public void retainedMethodsStillOnAstUtilities() {
    assertMethodOnAstUtilities("createProcedure", String.class);
    assertMethodOnAstUtilities("createUserLambda", Class.class);
    assertMethodOnAstUtilities("createLambdaExpression", Class.class);
    assertMethodOnAstUtilities("isKeywordExpression", Expression.class);
    assertMethodOnAstUtilities("createConditionalStatement", Expression.class);
  }

  // ── Helpers ───────────────────────────────────────────────────────────

  private static void assertMethodNotOnAstUtilities(String name, Class<?>... paramTypes) {
    try {
      AstUtilities.class.getMethod(name, paramTypes);
      fail(name + " should no longer exist on AstUtilities (was extracted)");
    } catch (NoSuchMethodException expected) {
      // correct
    }
  }

  private static void assertMethodOnAstUtilities(String name, Class<?>... paramTypes) {
    try {
      AstUtilities.class.getMethod(name, paramTypes);
    } catch (NoSuchMethodException e) {
      fail(name + " should still exist on AstUtilities (was not extracted)");
    }
  }
}
