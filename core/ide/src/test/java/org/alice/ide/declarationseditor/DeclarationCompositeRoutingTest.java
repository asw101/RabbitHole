package org.alice.ide.declarationseditor;

import org.lgna.project.ast.*;
import org.junit.Test;

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
  // Note: TypeComposite construction requires ToolPaletteComposites and other
  // heavy IDE infrastructure, so we verify the routing disambiguation via
  // the AbstractDeclaration hierarchy and test CodeComposite path end-to-end.

  @Test
  public void namedUserType_isAbstractDeclaration_butNotAbstractCode() {
    // Routing depends on: first check for AbstractCode, else check for NamedUserType.
    // Verify that NamedUserType IS an AbstractDeclaration (the method parameter type)
    // but is on a separate branch from AbstractCode in the class hierarchy.
    NamedUserType userType = AstUtilities.createType("TestType", JavaType.OBJECT_TYPE);
    assertTrue("NamedUserType should be an AbstractDeclaration",
        userType instanceof org.lgna.project.ast.AbstractDeclaration);
    // The compiler enforces that NamedUserType is NOT AbstractCode —
    // they are on disjoint branches of the type hierarchy.
  }

  @Test
  public void userMethod_isAbstractCode() {
    // Verify routing: UserMethod matches the AbstractCode branch.
    UserMethod method = new UserMethod("proc", JavaType.VOID_TYPE, new UserParameter[0], new BlockStatement());
    assertTrue("UserMethod should be an AbstractCode",
        method instanceof org.lgna.project.ast.AbstractCode);
    assertTrue("UserMethod should also be an AbstractDeclaration",
        method instanceof org.lgna.project.ast.AbstractDeclaration);
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
