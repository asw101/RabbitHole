package org.alice.ide.ast.export;

import org.junit.Assume;
import org.junit.Test;
import org.lgna.project.Project;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.NamedUserType;
import org.lgna.project.ast.UserField;

import java.awt.GraphicsEnvironment;

import static org.junit.Assert.*;

public class FieldInfoTest {

  @Test
  public void constructionViaTypeInfoCreatesFieldInfo() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    FieldFixture fixture = createFieldFixture();

    assertNotNull(fixture.typeInfo.getInfoForField(fixture.firstField));
  }

  @Test
  public void getDeclarationReturnsTheUserField() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    FieldFixture fixture = createFieldFixture();

    FieldInfo fieldInfo = fixture.typeInfo.getInfoForField(fixture.firstField);

    assertSame(fixture.firstField, fieldInfo.getDeclaration());
  }

  @Test
  public void multipleFieldsInOneTypeEachGetTheirOwnFieldInfo() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    FieldFixture fixture = createFieldFixture();

    FieldInfo firstInfo = fixture.typeInfo.getInfoForField(fixture.firstField);
    FieldInfo secondInfo = fixture.typeInfo.getInfoForField(fixture.secondField);

    assertNotNull(firstInfo);
    assertNotNull(secondInfo);
    assertNotSame(firstInfo, secondInfo);
  }

  private static FieldFixture createFieldFixture() {
    NamedUserType type = new NamedUserType();
    type.name.setValue("FieldType");
    type.superType.setValue(JavaType.getInstance(Object.class));

    UserField firstField = createField("firstField");
    UserField secondField = createField("secondField");
    type.fields.add(firstField);
    type.fields.add(secondField);

    ProjectInfo projectInfo = new ProjectInfo(new Project(type, Project.SceneCameraType.WindowCamera));
    return new FieldFixture(projectInfo.getInfoForType(type), firstField, secondField);
  }

  private static UserField createField(String name) {
    UserField field = new UserField();
    field.name.setValue(name);
    field.valueType.setValue(JavaType.getInstance(String.class));
    return field;
  }

  private static class FieldFixture {
    private final TypeInfo typeInfo;
    private final UserField firstField;
    private final UserField secondField;

    private FieldFixture(TypeInfo typeInfo, UserField firstField, UserField secondField) {
      this.typeInfo = typeInfo;
      this.firstField = firstField;
      this.secondField = secondField;
    }
  }
}
