package org.alice.ide.ast.type.merge.croquet.edits;

import org.junit.Test;
import org.lgna.project.ast.UserField;
import org.lgna.project.ast.JavaType;
import static org.junit.Assert.*;

public class RenameMemberDataTest {
  @Test
  public void constructWithMemberAndNextName() {
    UserField field = createField("original");
    RenameMemberData data = new RenameMemberData(field, "renamed");
    assertSame(field, data.getMember());
    assertEquals("renamed", data.getNextName());
  }
  @Test
  public void prevNameIsNullByDefault() {
    UserField field = createField("test");
    RenameMemberData data = new RenameMemberData(field, "newName");
    assertNull(data.getPrevName());
  }
  @Test
  public void setPrevNameAndGet() {
    UserField field = createField("test");
    RenameMemberData data = new RenameMemberData(field, "newName");
    data.setPrevName("oldName");
    assertEquals("oldName", data.getPrevName());
  }
  @Test
  public void getMember_returnsSameInstance() {
    UserField field = createField("f");
    RenameMemberData data = new RenameMemberData(field, "g");
    assertSame(field, data.getMember());
  }
  @Test
  public void getNextName_returnsConstructorArg() {
    UserField field = createField("f");
    RenameMemberData data = new RenameMemberData(field, "target");
    assertEquals("target", data.getNextName());
  }
  private static UserField createField(String name) {
    UserField field = new UserField();
    field.name.setValue(name);
    field.valueType.setValue(JavaType.getInstance(String.class));
    return field;
  }
}
