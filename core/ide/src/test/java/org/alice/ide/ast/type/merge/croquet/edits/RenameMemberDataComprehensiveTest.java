package org.alice.ide.ast.type.merge.croquet.edits;

import org.junit.Test;
import org.lgna.project.ast.*;

import static org.junit.Assert.*;

/**
 * Comprehensive tests for {@link RenameMemberData}.
 */
public class RenameMemberDataComprehensiveTest {

  private static UserMethod createMethod(String name) {
    UserMethod method = new UserMethod();
    method.name.setValue(name);
    method.returnType.setValue(JavaType.VOID_TYPE);
    return method;
  }

  private static UserField createField(String name) {
    UserField field = new UserField();
    field.name.setValue(name);
    field.valueType.setValue(JavaType.getInstance(Object.class));
    return field;
  }

  @Test
  public void construct_withMethod() {
    UserMethod method = createMethod("oldName");
    RenameMemberData data = new RenameMemberData(method, "newName");
    assertNotNull(data);
  }

  @Test
  public void getMember_returnsSameMethod() {
    UserMethod method = createMethod("m");
    RenameMemberData data = new RenameMemberData(method, "n");
    assertSame(method, data.getMember());
  }

  @Test
  public void getNextName_returnsConstructionArg() {
    UserMethod method = createMethod("m");
    RenameMemberData data = new RenameMemberData(method, "newName");
    assertEquals("newName", data.getNextName());
  }

  @Test
  public void construct_withField() {
    UserField field = createField("oldField");
    RenameMemberData data = new RenameMemberData(field, "newField");
    assertNotNull(data);
    assertSame(field, data.getMember());
    assertEquals("newField", data.getNextName());
  }

  @Test
  public void twoInstances_areDifferent() {
    UserMethod m = createMethod("m");
    RenameMemberData d1 = new RenameMemberData(m, "a");
    RenameMemberData d2 = new RenameMemberData(m, "b");
    assertNotSame(d1, d2);
  }

  @Test
  public void differentMembers_differentData() {
    UserMethod m1 = createMethod("a");
    UserMethod m2 = createMethod("b");
    RenameMemberData d1 = new RenameMemberData(m1, "x");
    RenameMemberData d2 = new RenameMemberData(m2, "y");
    assertNotSame(d1.getMember(), d2.getMember());
    assertNotEquals(d1.getNextName(), d2.getNextName());
  }

  @Test
  public void construct_withEmptyNextName() {
    UserMethod method = createMethod("m");
    RenameMemberData data = new RenameMemberData(method, "");
    assertEquals("", data.getNextName());
  }

  @Test
  public void construct_withUnicodeNextName() {
    UserMethod method = createMethod("m");
    RenameMemberData data = new RenameMemberData(method, "新名前");
    assertEquals("新名前", data.getNextName());
  }

  @Test
  public void className_isRenameMemberData() {
    assertEquals("RenameMemberData", RenameMemberData.class.getSimpleName());
  }

  @Test
  public void packageName_isCorrect() {
    assertEquals("org.alice.ide.ast.type.merge.croquet.edits", RenameMemberData.class.getPackage().getName());
  }

  @Test
  public void construct_nextNameSameAsMemberName() {
    UserMethod method = createMethod("same");
    RenameMemberData data = new RenameMemberData(method, "same");
    assertEquals("same", data.getNextName());
  }

  @Test
  public void multipleConstructions_succeed() {
    UserMethod method = createMethod("base");
    for (int i = 0; i < 10; i++) {
      RenameMemberData data = new RenameMemberData(method, "name" + i);
      assertEquals("name" + i, data.getNextName());
    }
  }
}
