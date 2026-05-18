package org.alice.ide.croquet.edits.ast;

import org.alice.ide.croquet.CroquetTestSupport;
import org.junit.Test;
import org.lgna.project.ast.*;

import static org.junit.Assert.*;

/**
 * Tests for {@link DeclareMethodEdit} — edit that adds/removes methods on UserType.
 * Limited to construction and accessor contracts; doOrRedo/undo requires IDE context.
 */
public class DeclareMethodEditTest {

  private NamedUserType testType() {
    return CroquetTestSupport.namedType("TestType");
  }

  // ---- construction ----

  @Test
  public void construct_withNullUserActivity_succeeds() {
    DeclareMethodEdit edit = new DeclareMethodEdit(
        null, testType(), "myMethod", JavaType.VOID_TYPE, new BlockStatement());
    assertNotNull(edit);
  }

  @Test
  public void construct_withoutBody_succeeds() {
    DeclareMethodEdit edit = new DeclareMethodEdit(
        null, testType(), "myMethod", JavaType.VOID_TYPE);
    assertNotNull(edit);
  }

  @Test
  public void construct_withEmptyMethodName_succeeds() {
    DeclareMethodEdit edit = new DeclareMethodEdit(
        null, testType(), "", JavaType.VOID_TYPE);
    assertNotNull(edit);
  }

  // ---- getters ----

  @Test
  public void getDeclaringType_returnsType() {
    NamedUserType type = testType();
    DeclareMethodEdit edit = new DeclareMethodEdit(
        null, type, "myMethod", JavaType.VOID_TYPE, new BlockStatement());
    assertSame(type, edit.getDeclaringType());
  }

  @Test
  public void getMethodName_returnsName() {
    DeclareMethodEdit edit = new DeclareMethodEdit(
        null, testType(), "myMethod", JavaType.VOID_TYPE, new BlockStatement());
    assertEquals("myMethod", edit.getMethodName());
  }

  @Test
  public void getReturnType_returnsType() {
    DeclareMethodEdit edit = new DeclareMethodEdit(
        null, testType(), "myMethod", JavaType.VOID_TYPE, new BlockStatement());
    assertSame(JavaType.VOID_TYPE, edit.getReturnType());
  }

  // ---- different return types ----

  @Test
  public void construct_withIntegerReturnType() {
    DeclareMethodEdit edit = new DeclareMethodEdit(
        null, testType(), "getCount", JavaType.INTEGER_OBJECT_TYPE, new BlockStatement());
    assertSame(JavaType.INTEGER_OBJECT_TYPE, edit.getReturnType());
  }

  @Test
  public void construct_withStringReturnType() {
    DeclareMethodEdit edit = new DeclareMethodEdit(
        null, testType(), "getName", JavaType.STRING_TYPE, new BlockStatement());
    assertSame(JavaType.STRING_TYPE, edit.getReturnType());
  }

  @Test
  public void construct_withBooleanReturnType() {
    DeclareMethodEdit edit = new DeclareMethodEdit(
        null, testType(), "isValid", JavaType.BOOLEAN_OBJECT_TYPE, new BlockStatement());
    assertSame(JavaType.BOOLEAN_OBJECT_TYPE, edit.getReturnType());
  }

  // ---- multiple methods ----

  @Test
  public void construct_multipleEdits_independent() {
    NamedUserType type = testType();
    DeclareMethodEdit edit1 = new DeclareMethodEdit(null, type, "method1", JavaType.VOID_TYPE);
    DeclareMethodEdit edit2 = new DeclareMethodEdit(null, type, "method2", JavaType.VOID_TYPE);
    assertNotSame(edit1, edit2);
    assertEquals("method1", edit1.getMethodName());
    assertEquals("method2", edit2.getMethodName());
  }

  // ---- null parameters ----

  @Test
  public void construct_withNullMethodName_succeeds() {
    DeclareMethodEdit edit = new DeclareMethodEdit(null, testType(), null, JavaType.VOID_TYPE);
    assertNull(edit.getMethodName());
  }

  @Test
  public void construct_differentTypes_returnCorrectType() {
    NamedUserType type1 = CroquetTestSupport.namedType("Type1");
    NamedUserType type2 = CroquetTestSupport.namedType("Type2");

    DeclareMethodEdit edit1 = new DeclareMethodEdit(null, type1, "m1", JavaType.VOID_TYPE);
    DeclareMethodEdit edit2 = new DeclareMethodEdit(null, type2, "m2", JavaType.VOID_TYPE);

    assertSame(type1, edit1.getDeclaringType());
    assertSame(type2, edit2.getDeclaringType());
  }

  // ---- body parameter ----

  @Test
  public void construct_withBody_succeeds() {
    DeclareMethodEdit edit = new DeclareMethodEdit(
        null, testType(), "myMethod", JavaType.VOID_TYPE, new BlockStatement());
    assertNotNull(edit);
  }

  @Test
  public void construct_withNullBody_succeeds() {
    DeclareMethodEdit edit = new DeclareMethodEdit(
        null, testType(), "myMethod", JavaType.VOID_TYPE, null);
    assertNotNull(edit);
  }
}
