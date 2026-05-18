package org.alice.ide.ast.export;

import org.lgna.project.Project;
import org.lgna.project.Project.SceneCameraType;
import org.lgna.project.ast.*;
import org.junit.Test;

import java.util.Collection;

import static org.junit.Assert.*;

public class TypeInfoTest {

  private NamedUserType createSimpleType(String name) {
    NamedUserType type = new NamedUserType();
    type.name.setValue(name);
    type.superType.setValue(JavaType.getInstance(Object.class));
    return type;
  }

  private Project createProjectWithTypes(NamedUserType... types) {
    Project project = new Project(types[0], SceneCameraType.WindowCamera);
    for (int i = 1; i < types.length; i++) {
      project.getNamedUserTypes().add(types[i]);
    }
    return project;
  }

  @Test
  public void typeInfoCreatedForSimpleType() {
    NamedUserType type = createSimpleType("TestClass");
    Project project = createProjectWithTypes(type);
    ProjectInfo projectInfo = new ProjectInfo(project);
    TypeInfo info = projectInfo.getInfoForType(type);
    assertNotNull("Should have TypeInfo for the type", info);
    assertEquals("TestClass", info.getDeclaration().getName());
  }

  @Test
  public void typeInfoHasEmptyConstructorsForSimpleType() {
    NamedUserType type = createSimpleType("NoConstructorType");
    Project project = createProjectWithTypes(type);
    ProjectInfo projectInfo = new ProjectInfo(project);
    TypeInfo info = projectInfo.getInfoForType(type);
    assertNotNull(info);
    Collection<ConstructorInfo> constructors = info.getConstructorInfos();
    assertNotNull(constructors);
    // NamedUserType with no constructors added => 0
    assertEquals(0, constructors.size());
  }

  @Test
  public void typeInfoHasEmptyMethodsForSimpleType() {
    NamedUserType type = createSimpleType("NoMethodType");
    Project project = createProjectWithTypes(type);
    ProjectInfo projectInfo = new ProjectInfo(project);
    TypeInfo info = projectInfo.getInfoForType(type);
    assertNotNull(info);
    Collection<MethodInfo> methods = info.getMethodInfos();
    assertNotNull(methods);
    assertEquals(0, methods.size());
  }

  @Test
  public void typeInfoHasEmptyFieldsForSimpleType() {
    NamedUserType type = createSimpleType("NoFieldType");
    Project project = createProjectWithTypes(type);
    ProjectInfo projectInfo = new ProjectInfo(project);
    TypeInfo info = projectInfo.getInfoForType(type);
    assertNotNull(info);
    Collection<FieldInfo> fields = info.getFieldInfos();
    assertNotNull(fields);
    assertEquals(0, fields.size());
  }

  @Test
  public void typeInfoTracksMethod() {
    NamedUserType type = createSimpleType("WithMethod");
    UserMethod method = new UserMethod();
    method.name.setValue("doSomething");
    method.returnType.setValue(JavaType.VOID_TYPE);
    method.body.setValue(new BlockStatement());
    type.methods.add(method);

    Project project = createProjectWithTypes(type);
    ProjectInfo projectInfo = new ProjectInfo(project);
    TypeInfo info = projectInfo.getInfoForType(type);
    assertNotNull(info);
    assertEquals(1, info.getMethodInfos().size());
    MethodInfo methodInfo = info.getInfoForMethod(method);
    assertNotNull("Should find info for the added method", methodInfo);
  }

  @Test
  public void typeInfoTracksField() {
    NamedUserType type = createSimpleType("WithField");
    UserField field = new UserField();
    field.name.setValue("myField");
    field.valueType.setValue(JavaType.getInstance(String.class));
    type.fields.add(field);

    Project project = createProjectWithTypes(type);
    ProjectInfo projectInfo = new ProjectInfo(project);
    TypeInfo info = projectInfo.getInfoForType(type);
    assertNotNull(info);
    assertEquals(1, info.getFieldInfos().size());
    FieldInfo fieldInfo = info.getInfoForField(field);
    assertNotNull("Should find info for the added field", fieldInfo);
  }

  @Test
  public void superTypeInfoIsNullForJavaSuperType() {
    NamedUserType type = createSimpleType("ChildOfJava");
    Project project = createProjectWithTypes(type);
    ProjectInfo projectInfo = new ProjectInfo(project);
    TypeInfo info = projectInfo.getInfoForType(type);
    assertNotNull(info);
    // Super type is JavaType (Object.class), not a UserType, so superTypeInfo is null
    assertNull("Super type info should be null for Java super type", info.getSuperTypeInfo());
  }

  @Test
  public void projectInfoTracksMultipleTypes() {
    NamedUserType type1 = createSimpleType("Alpha");
    NamedUserType type2 = createSimpleType("Beta");
    Project project = createProjectWithTypes(type1, type2);
    ProjectInfo projectInfo = new ProjectInfo(project);
    Collection<TypeInfo> infos = projectInfo.getTypeInfos();
    assertEquals(2, infos.size());
    assertNotNull(projectInfo.getInfoForType(type1));
    assertNotNull(projectInfo.getInfoForType(type2));
  }
}
