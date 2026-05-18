package org.alice.ide.ast.export;

import org.junit.Assume;
import org.junit.Test;
import org.lgna.project.Project;
import org.lgna.project.ast.BlockStatement;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.NamedUserType;
import org.lgna.project.ast.UserField;
import org.lgna.project.ast.UserMethod;

import java.awt.GraphicsEnvironment;

import static org.junit.Assert.*;

public class TypeInfoTest {

  @Test
  public void constructionCreatesTypeInfoWithCorrectDeclaration() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    TypeFixture fixture = createTypeFixture();

    TypeInfo info = createProjectInfo(fixture.type).getInfoForType(fixture.type);

    assertNotNull(info);
    assertSame(fixture.type, info.getDeclaration());
  }

  @Test
  public void getConstructorInfosReturnsACollection() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    TypeFixture fixture = createTypeFixture();

    TypeInfo info = createProjectInfo(fixture.type).getInfoForType(fixture.type);

    assertNotNull(info.getConstructorInfos());
  }

  @Test
  public void getMethodInfosReturnsCollectionForTypeWithMethods() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    TypeFixture fixture = createTypeFixture();

    TypeInfo info = createProjectInfo(fixture.type).getInfoForType(fixture.type);

    assertEquals(1, info.getMethodInfos().size());
    assertNotNull(info.getInfoForMethod(fixture.method));
  }

  @Test
  public void getFieldInfosReturnsCollectionForTypeWithFields() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    TypeFixture fixture = createTypeFixture();

    TypeInfo info = createProjectInfo(fixture.type).getInfoForType(fixture.type);

    assertEquals(1, info.getFieldInfos().size());
    assertNotNull(info.getInfoForField(fixture.field));
  }

  @Test
  public void getSuperTypeInfoReturnsNullForJavaSuperType() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    TypeFixture fixture = createTypeFixture();

    TypeInfo info = createProjectInfo(fixture.type).getInfoForType(fixture.type);

    assertNull(info.getSuperTypeInfo());
  }

  private static ProjectInfo createProjectInfo(NamedUserType type) {
    return new ProjectInfo(new Project(type, Project.SceneCameraType.WindowCamera));
  }

  private static TypeFixture createTypeFixture() {
    NamedUserType type = new NamedUserType();
    type.name.setValue("TestType");
    type.superType.setValue(JavaType.getInstance(Object.class));

    UserMethod method = new UserMethod();
    method.name.setValue("doSomething");
    method.returnType.setValue(JavaType.VOID_TYPE);
    method.body.setValue(new BlockStatement());
    type.methods.add(method);

    UserField field = new UserField();
    field.name.setValue("myField");
    field.valueType.setValue(JavaType.getInstance(String.class));
    type.fields.add(field);

    return new TypeFixture(type, method, field);
  }

  private static class TypeFixture {
    private final NamedUserType type;
    private final UserMethod method;
    private final UserField field;

    private TypeFixture(NamedUserType type, UserMethod method, UserField field) {
      this.type = type;
      this.method = method;
      this.field = field;
    }
  }
}
