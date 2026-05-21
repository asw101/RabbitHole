package org.alice.ide.ast.export;

import org.junit.Test;
import org.lgna.project.Project;
import org.lgna.project.ast.*;

import static org.junit.Assert.*;

/**
 * Coverage tests for {@link TypeInfo}.
 * Hybrid tests via ProjectInfo for member accessors.
 */
public class TypeInfoCoverageTest {

  private NamedUserType createMinimalType(String name) {
    NamedUserType type = new NamedUserType();
    type.name.setValue(name);
    return type;
  }

  private ProjectInfo createProjectInfoWith(NamedUserType type) {
    Project project = new Project(type, Project.SceneCameraType.WindowCamera);
    return new ProjectInfo(project);
  }

  @Test
  public void getDeclaration_returnsSameType() {
    NamedUserType type = createMinimalType("MyType");
    ProjectInfo projInfo = createProjectInfoWith(type);
    TypeInfo typeInfo = projInfo.getInfoForType(type);
    assertSame(type, typeInfo.getDeclaration());
  }

  @Test
  public void getSuperTypeInfo_noSuperUserType_returnsNull() {
    NamedUserType type = createMinimalType("Root");
    ProjectInfo projInfo = createProjectInfoWith(type);
    TypeInfo typeInfo = projInfo.getInfoForType(type);
    assertNull(typeInfo.getSuperTypeInfo());
  }

  @Test
  public void getFieldInfos_emptyType_returnsEmpty() {
    NamedUserType type = createMinimalType("Empty");
    ProjectInfo projInfo = createProjectInfoWith(type);
    TypeInfo typeInfo = projInfo.getInfoForType(type);
    assertTrue(typeInfo.getFieldInfos().isEmpty());
  }

  @Test
  public void getMethodInfos_emptyType_returnsEmpty() {
    NamedUserType type = createMinimalType("Empty");
    ProjectInfo projInfo = createProjectInfoWith(type);
    TypeInfo typeInfo = projInfo.getInfoForType(type);
    assertTrue(typeInfo.getMethodInfos().isEmpty());
  }

  @Test
  public void getConstructorInfos_emptyType_returnsEmpty() {
    NamedUserType type = createMinimalType("Empty");
    ProjectInfo projInfo = createProjectInfoWith(type);
    TypeInfo typeInfo = projInfo.getInfoForType(type);
    assertTrue(typeInfo.getConstructorInfos().isEmpty());
  }

  @Test
  public void getInfoForField_existingField_notNull() {
    NamedUserType type = createMinimalType("WithField");
    UserField field = new UserField();
    field.name.setValue("x");
    field.valueType.setValue(JavaType.getInstance(Double.class));
    type.fields.add(field);
    ProjectInfo projInfo = createProjectInfoWith(type);
    TypeInfo typeInfo = projInfo.getInfoForType(type);
    assertNotNull(typeInfo.getInfoForField(field));
  }

  @Test
  public void getInfoForField_unknownField_returnsNull() {
    NamedUserType type = createMinimalType("WithField");
    ProjectInfo projInfo = createProjectInfoWith(type);
    TypeInfo typeInfo = projInfo.getInfoForType(type);
    UserField unknown = new UserField();
    unknown.name.setValue("unknown");
    unknown.valueType.setValue(JavaType.getInstance(String.class));
    assertNull(typeInfo.getInfoForField(unknown));
  }

  @Test
  public void getInfoForMethod_existingMethod_notNull() {
    NamedUserType type = createMinimalType("WithMethod");
    UserMethod method = new UserMethod();
    method.name.setValue("doSomething");
    method.returnType.setValue(JavaType.VOID_TYPE);
    method.managementLevel.setValue(ManagementLevel.NONE);
    method.body.setValue(new BlockStatement());
    type.methods.add(method);
    ProjectInfo projInfo = createProjectInfoWith(type);
    TypeInfo typeInfo = projInfo.getInfoForType(type);
    assertNotNull(typeInfo.getInfoForMethod(method));
  }

  @Test
  public void getInfoForMethod_unknownMethod_returnsNull() {
    NamedUserType type = createMinimalType("WithMethod");
    ProjectInfo projInfo = createProjectInfoWith(type);
    TypeInfo typeInfo = projInfo.getInfoForType(type);
    UserMethod unknown = new UserMethod();
    unknown.name.setValue("unknown");
    assertNull(typeInfo.getInfoForMethod(unknown));
  }

  @Test
  public void resetRequired_doesNotThrow() {
    NamedUserType type = createMinimalType("Test");
    ProjectInfo projInfo = createProjectInfoWith(type);
    TypeInfo typeInfo = projInfo.getInfoForType(type);
    typeInfo.resetRequired();
  }

  @Test
  public void extendsDeclarationInfo() {
    assertTrue(DeclarationInfo.class.isAssignableFrom(TypeInfo.class));
  }
}
