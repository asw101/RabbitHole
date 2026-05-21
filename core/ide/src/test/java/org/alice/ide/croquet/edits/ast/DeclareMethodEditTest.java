package org.alice.ide.croquet.edits.ast;

import org.junit.Test;
import org.lgna.croquet.*;
import org.lgna.project.ast.*;

import java.lang.reflect.Field;

import static org.junit.Assert.*;

public class DeclareMethodEditTest {

  @Test
  public void construct_withNullUserActivity_succeeds() {
    DeclareMethodEdit edit = new DeclareMethodEdit(null, createType("Hero"), "move", JavaType.VOID_TYPE, new BlockStatement());
    assertNotNull(edit);
  }

  @Test
  public void getDeclaringType_withNamedUserType_returnsConstructionValue() {
    NamedUserType type = createType("WorldBuilder");
    DeclareMethodEdit edit = new DeclareMethodEdit(null, type, "build", JavaType.VOID_TYPE, new BlockStatement());
    assertSame(type, edit.getDeclaringType());
  }

  @Test
  public void getMethodName_withExplicitName_returnsConstructionValue() {
    DeclareMethodEdit edit = new DeclareMethodEdit(null, createType("NameHolder"), "calculateScore", JavaType.VOID_TYPE, new BlockStatement());
    assertEquals("calculateScore", edit.getMethodName());
  }

  @Test
  public void getMethodName_withBlankString_returnsBlankString() {
    DeclareMethodEdit edit = new DeclareMethodEdit(null, createType("BlankNameHolder"), "", JavaType.VOID_TYPE, new BlockStatement());
    assertEquals("", edit.getMethodName());
  }

  @Test
  public void getReturnType_withExplicitType_returnsConstructionValue() {
    AbstractType<?, ?, ?> returnType = JavaType.getInstance(String.class);
    DeclareMethodEdit edit = new DeclareMethodEdit(null, createType("ReturnTypeHolder"), "describe", returnType, new BlockStatement());
    assertSame(returnType, edit.getReturnType());
  }

  @Test
  public void construct_withConvenienceConstructor_createsNonNullBody() throws Exception {
    DeclareMethodEdit edit = new DeclareMethodEdit(null, createType("ConvenienceType"), "reset", JavaType.VOID_TYPE);
    BlockStatement body = (BlockStatement) readField(edit, "body");
    assertNotNull(body);
    assertEquals(0, body.statements.size());
  }

  @Test
  public void construct_withExplicitBody_preservesProvidedBodyInstance() throws Exception {
    BlockStatement body = new BlockStatement();
    body.statements.add(new Comment("body marker"));

    DeclareMethodEdit edit = new DeclareMethodEdit(null, createType("BodyHolder"), "commented", JavaType.VOID_TYPE, body);

    assertSame(body, readField(edit, "body"));
  }

  @Test
  public void construct_withNullDeclaringType_preservesNull() {
    DeclareMethodEdit edit = new DeclareMethodEdit(null, null, "orphan", JavaType.VOID_TYPE, new BlockStatement());
    assertNull(edit.getDeclaringType());
  }

  @Test
  public void construct_withNullReturnType_preservesNull() {
    DeclareMethodEdit edit = new DeclareMethodEdit(null, createType("NullReturnType"), "mystery", null, new BlockStatement());
    assertNull(edit.getReturnType());
  }

  @Test
  public void construct_withNullBody_preservesNullBodyReference() throws Exception {
    DeclareMethodEdit edit = new DeclareMethodEdit(null, createType("NullBodyType"), "optionalBody", JavaType.VOID_TYPE, null);
    assertNull(readField(edit, "body"));
  }

  @Test
  public void construct_withBodyContainingStatements_preservesStatementCount() throws Exception {
    BlockStatement body = new BlockStatement();
    body.statements.add(new Comment("first"));
    body.statements.add(new Comment("second"));

    DeclareMethodEdit edit = new DeclareMethodEdit(null, createType("StatementHolder"), "annotated", JavaType.VOID_TYPE, body);

    assertEquals(2, ((BlockStatement) readField(edit, "body")).statements.size());
  }

  @Test
  public void multipleInstances_withDifferentValues_remainIndependent() {
    NamedUserType firstType = createType("FirstType");
    NamedUserType secondType = createType("SecondType");
    AbstractType<?, ?, ?> firstReturnType = JavaType.getInstance(Integer.class);
    AbstractType<?, ?, ?> secondReturnType = JavaType.getInstance(Double.class);

    DeclareMethodEdit first = new DeclareMethodEdit(null, firstType, "firstMethod", firstReturnType, new BlockStatement());
    DeclareMethodEdit second = new DeclareMethodEdit(null, secondType, "secondMethod", secondReturnType, new BlockStatement());

    assertSame(firstType, first.getDeclaringType());
    assertEquals("firstMethod", first.getMethodName());
    assertSame(firstReturnType, first.getReturnType());
    assertSame(secondType, second.getDeclaringType());
    assertEquals("secondMethod", second.getMethodName());
    assertSame(secondReturnType, second.getReturnType());
  }

  private NamedUserType createType(String name) {
    NamedUserType type = new NamedUserType();
    type.name.setValue(name);
    type.superType.setValue(JavaType.getInstance(Object.class));
    return type;
  }

  private Object readField(Object target, String fieldName) throws Exception {
    Class<?> type = target.getClass();
    while (type != null) {
      try {
        Field field = type.getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(target);
      } catch (NoSuchFieldException nsfe) {
        type = type.getSuperclass();
      }
    }
    throw new NoSuchFieldException(fieldName);
  }
}
