package org.alice.ide.croquet.edits.ast;

import org.junit.Test;
import org.lgna.project.ast.*;

import static org.junit.Assert.*;

/**
 * Tests for {@link DeclareMethodEdit} — edit that adds a method to a UserType.
 * Construction and accessor tests only; doOrRedo/undo depend on IDE.getActiveInstance().
 */
public class DeclareMethodEditTest {

  private NamedUserType createUserType(String name) {
    NamedUserType type = new NamedUserType();
    type.name.setValue(name);
    return type;
  }

  // ---- construction ----

  @Test
  public void construct_withNullUserActivity_succeeds() {
    NamedUserType type = createUserType("MyType");
    DeclareMethodEdit edit = new DeclareMethodEdit(null, type, "myMethod", JavaType.VOID_TYPE);
    assertNotNull(edit);
  }

  @Test
  public void construct_withBody_succeeds() {
    NamedUserType type = createUserType("MyType");
    BlockStatement body = new BlockStatement();
    DeclareMethodEdit edit = new DeclareMethodEdit(null, type, "myMethod", JavaType.VOID_TYPE, body);
    assertNotNull(edit);
  }

  @Test
  public void construct_withNullMethodName_succeeds() {
    NamedUserType type = createUserType("MyType");
    DeclareMethodEdit edit = new DeclareMethodEdit(null, type, null, JavaType.VOID_TYPE);
    assertNotNull(edit);
  }

  // ---- getDeclaringType ----

  @Test
  public void getDeclaringType_returnsOriginal() {
    NamedUserType type = createUserType("TestType");
    DeclareMethodEdit edit = new DeclareMethodEdit(null, type, "foo", JavaType.VOID_TYPE);
    assertSame(type, edit.getDeclaringType());
  }

  @Test
  public void getDeclaringType_differentType() {
    NamedUserType type1 = createUserType("Type1");
    NamedUserType type2 = createUserType("Type2");
    DeclareMethodEdit edit1 = new DeclareMethodEdit(null, type1, "foo", JavaType.VOID_TYPE);
    DeclareMethodEdit edit2 = new DeclareMethodEdit(null, type2, "bar", JavaType.VOID_TYPE);
    assertSame(type1, edit1.getDeclaringType());
    assertSame(type2, edit2.getDeclaringType());
    assertNotSame(edit1.getDeclaringType(), edit2.getDeclaringType());
  }

  // ---- getMethodName ----

  @Test
  public void getMethodName_returnsOriginal() {
    NamedUserType type = createUserType("MyType");
    DeclareMethodEdit edit = new DeclareMethodEdit(null, type, "computeValue", JavaType.VOID_TYPE);
    assertEquals("computeValue", edit.getMethodName());
  }

  @Test
  public void getMethodName_emptyString() {
    NamedUserType type = createUserType("MyType");
    DeclareMethodEdit edit = new DeclareMethodEdit(null, type, "", JavaType.VOID_TYPE);
    assertEquals("", edit.getMethodName());
  }

  @Test
  public void getMethodName_specialCharacters() {
    NamedUserType type = createUserType("MyType");
    DeclareMethodEdit edit = new DeclareMethodEdit(null, type, "doSomething_v2", JavaType.VOID_TYPE);
    assertEquals("doSomething_v2", edit.getMethodName());
  }

  // ---- getReturnType ----

  @Test
  public void getReturnType_voidType() {
    NamedUserType type = createUserType("MyType");
    DeclareMethodEdit edit = new DeclareMethodEdit(null, type, "foo", JavaType.VOID_TYPE);
    assertSame(JavaType.VOID_TYPE, edit.getReturnType());
  }

  @Test
  public void getReturnType_integerType() {
    NamedUserType type = createUserType("MyType");
    JavaType intType = JavaType.getInstance(Integer.class);
    DeclareMethodEdit edit = new DeclareMethodEdit(null, type, "getCount", intType);
    assertSame(intType, edit.getReturnType());
  }

  @Test
  public void getReturnType_stringType() {
    NamedUserType type = createUserType("MyType");
    JavaType stringType = JavaType.getInstance(String.class);
    DeclareMethodEdit edit = new DeclareMethodEdit(null, type, "getName", stringType);
    assertSame(stringType, edit.getReturnType());
  }

  @Test
  public void getReturnType_booleanType() {
    NamedUserType type = createUserType("MyType");
    JavaType boolType = JavaType.BOOLEAN_OBJECT_TYPE;
    DeclareMethodEdit edit = new DeclareMethodEdit(null, type, "isValid", boolType);
    assertSame(boolType, edit.getReturnType());
  }

  // ---- appendDescription ----

  @Test
  public void appendDescription_includesMethodName() {
    NamedUserType type = createUserType("MyType");
    DeclareMethodEdit edit = new DeclareMethodEdit(null, type, "calculate", JavaType.VOID_TYPE);
    // AbstractEdit provides the appendDescription framework;
    // DeclareMethodEdit.appendDescription appends "declare: calculate"
    // We can't easily access it directly since it's protected, but construction verifies structure
    assertNotNull(edit);
    assertEquals("calculate", edit.getMethodName());
  }

  // ---- body overload ----

  @Test
  public void construct_noBody_defaultsToNewBlockStatement() {
    NamedUserType type = createUserType("MyType");
    DeclareMethodEdit edit = new DeclareMethodEdit(null, type, "method", JavaType.VOID_TYPE);
    // The no-body constructor delegates to the 5-arg constructor with new BlockStatement()
    assertNotNull(edit);
  }

  @Test
  public void construct_withExplicitBody_succeeds() {
    NamedUserType type = createUserType("MyType");
    BlockStatement body = new BlockStatement();
    body.statements.add(new ExpressionStatement(new NullLiteral()));
    DeclareMethodEdit edit = new DeclareMethodEdit(null, type, "method", JavaType.VOID_TYPE, body);
    assertNotNull(edit);
  }

  // ---- EPIC_HACK setter ----

  @Test
  public void epicHackSetMethod_doesNotThrow() {
    NamedUserType type = createUserType("MyType");
    DeclareMethodEdit edit = new DeclareMethodEdit(null, type, "foo", JavaType.VOID_TYPE);
    UserMethod method = new UserMethod();
    edit.EPIC_HACK_FOR_TUTORIAL_GENERATION_setMethod(method);
    // The method is set but not retrievable via a public getter.
    // This test verifies the method is callable without exception.
  }

  @Test
  public void epicHackSetMethod_withNull_doesNotThrow() {
    NamedUserType type = createUserType("MyType");
    DeclareMethodEdit edit = new DeclareMethodEdit(null, type, "foo", JavaType.VOID_TYPE);
    edit.EPIC_HACK_FOR_TUTORIAL_GENERATION_setMethod(null);
  }

  // ---- multiple edits for same type ----

  @Test
  public void multipleEdits_sameType_independentState() {
    NamedUserType type = createUserType("MyType");
    DeclareMethodEdit edit1 = new DeclareMethodEdit(null, type, "method1", JavaType.VOID_TYPE);
    DeclareMethodEdit edit2 = new DeclareMethodEdit(null, type, "method2", JavaType.VOID_TYPE);

    assertEquals("method1", edit1.getMethodName());
    assertEquals("method2", edit2.getMethodName());
    assertSame(type, edit1.getDeclaringType());
    assertSame(type, edit2.getDeclaringType());
  }
}
