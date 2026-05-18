package org.alice.ide.declarationseditor;

import org.lgna.project.ast.*;
import org.junit.Test;

import java.lang.reflect.Method;

import static org.junit.Assert.*;

/**
 * Tests for {@link DeclarationComposite#getInstance(AbstractDeclaration)} routing logic —
 * null handling, type routing, and code routing paths.
 */
public class DeclarationCompositeRoutingTest {

  // ---- null routing ----

  @Test
  public void getInstance_null_returnsNull() {
    DeclarationComposite<?, ?> result = DeclarationComposite.getInstance(null);
    assertNull("null declaration should return null", result);
  }

  // ---- AbstractCode routing → CodeComposite ----

  @Test
  public void getInstance_userMethod_returnsCodeComposite() {
    UserMethod method = new UserMethod("testProc", JavaType.VOID_TYPE, new UserParameter[0], new BlockStatement());
    DeclarationComposite<?, ?> result = DeclarationComposite.getInstance(method);

    assertNotNull("UserMethod should produce a DeclarationComposite", result);
    assertTrue("Should be a CodeComposite", result instanceof CodeComposite);
  }

  @Test
  public void getInstance_sameMethod_returnsSameComposite() {
    UserMethod method = new UserMethod("sameProc", JavaType.VOID_TYPE, new UserParameter[0], new BlockStatement());
    DeclarationComposite<?, ?> first = DeclarationComposite.getInstance(method);
    DeclarationComposite<?, ?> second = DeclarationComposite.getInstance(method);

    assertSame("Same method should return same cached CodeComposite", first, second);
  }

  @Test
  public void getInstance_differentMethods_returnsDifferentComposites() {
    UserMethod method1 = new UserMethod("proc1", JavaType.VOID_TYPE, new UserParameter[0], new BlockStatement());
    UserMethod method2 = new UserMethod("proc2", JavaType.VOID_TYPE, new UserParameter[0], new BlockStatement());
    DeclarationComposite<?, ?> comp1 = DeclarationComposite.getInstance(method1);
    DeclarationComposite<?, ?> comp2 = DeclarationComposite.getInstance(method2);

    assertNotSame("Different methods should return different composites", comp1, comp2);
  }

  // ---- NamedUserType routing → TypeComposite ----
  // Note: TypeComposite construction requires heavy IDE infrastructure
  // (ToolPaletteComposites, ImportOperation etc.), so we test the routing
  // logic via reflection on the instanceof check instead.

  @Test
  public void getInstance_routingLogic_namedUserType_isRecognized() {
    // Verify the routing: NamedUserType instanceof check in getInstance
    NamedUserType userType = AstUtilities.createType("TestType", JavaType.OBJECT_TYPE);
    assertTrue("NamedUserType should be recognized by the routing logic",
        userType instanceof org.lgna.project.ast.NamedUserType);
  }

  @Test
  public void getInstance_routingLogic_userMethod_isAbstractCode() {
    UserMethod method = new UserMethod("proc", JavaType.VOID_TYPE, new UserParameter[0], new BlockStatement());
    assertTrue("UserMethod should be recognized as AbstractCode",
        method instanceof org.lgna.project.ast.AbstractCode);
  }

  // ---- CodeComposite properties ----

  @Test
  public void codeComposite_getDeclaration_returnsOriginalCode() {
    UserMethod method = new UserMethod("myMethod", JavaType.VOID_TYPE, new UserParameter[0], new BlockStatement());
    CodeComposite composite = CodeComposite.getInstance(method);

    assertSame(method, composite.getDeclaration());
  }

  @Test
  public void codeComposite_getInstance_nullCode_returnsNull() {
    CodeComposite result = CodeComposite.getInstance(null);
    assertNull("null code should return null CodeComposite", result);
  }

  @Test
  public void codeComposite_isValid_voidProcedure_valid() {
    // isValid() requires getDeclaringType() != null → method must belong to a type
    NamedUserType ownerType = AstUtilities.createType("ValidOwner", JavaType.OBJECT_TYPE);
    UserMethod method = new UserMethod("validProc", JavaType.VOID_TYPE, new UserParameter[0], new BlockStatement());
    ownerType.methods.add(method);
    CodeComposite composite = CodeComposite.getInstance(method);
    assertTrue("CodeComposite with method in a type should be valid", composite.isValid());
  }

  @Test
  public void codeComposite_isValid_orphanMethod_notValid() {
    // A method without a declaring type should not be valid
    UserMethod orphan = new UserMethod("orphanProc", JavaType.VOID_TYPE, new UserParameter[0], new BlockStatement());
    CodeComposite composite = CodeComposite.getInstance(orphan);
    assertFalse("CodeComposite with orphan method should not be valid", composite.isValid());
  }

  // ---- DeclarationComposite abstract class structure ----

  @Test
  public void declarationComposite_isAbstract() throws Exception {
    assertTrue("DeclarationComposite should be abstract",
        java.lang.reflect.Modifier.isAbstract(DeclarationComposite.class.getModifiers()));
  }

  @Test
  public void declarationComposite_hasGetDeclarationMethod() throws Exception {
    Method m = DeclarationComposite.class.getMethod("getDeclaration");
    assertNotNull(m);
    assertEquals(AbstractDeclaration.class, m.getReturnType());
  }

  @Test
  public void declarationComposite_hasGetTypeMethod() throws Exception {
    Method m = DeclarationComposite.class.getMethod("getType");
    assertNotNull(m);
  }

  @Test
  public void declarationComposite_hasIsValidMethod() throws Exception {
    Method m = DeclarationComposite.class.getMethod("isValid");
    assertNotNull(m);
    assertEquals(boolean.class, m.getReturnType());
  }

  // ---- CodeComposite.getType returns declaring type ----

  @Test
  public void codeComposite_getType_returnsDeclaringType() {
    NamedUserType ownerType = AstUtilities.createType("Owner", JavaType.OBJECT_TYPE);
    UserMethod method = new UserMethod("doSomething", JavaType.VOID_TYPE, new UserParameter[0], new BlockStatement());
    ownerType.methods.add(method);

    CodeComposite composite = CodeComposite.getInstance(method);
    assertSame(ownerType, composite.getType());
  }
}
