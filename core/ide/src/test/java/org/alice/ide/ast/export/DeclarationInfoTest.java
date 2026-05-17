package org.alice.ide.ast.export;

import org.junit.Assume;
import org.junit.Test;
import org.lgna.project.Project;
import org.lgna.project.ast.*;
import org.lgna.story.SScene;

import java.util.*;

import static org.junit.Assert.*;

public class DeclarationInfoTest {

  private static NamedUserType createSceneType() {
    NamedUserType type = new NamedUserType();
    type.name.setValue("TestScene");
    type.superType.setValue(JavaType.getInstance(SScene.class));
    return type;
  }

  private static ProjectInfo createProjectInfoWith(NamedUserType sceneType) {
    NamedUserType programType = new NamedUserType();
    programType.name.setValue("Program");
    programType.superType.setValue(JavaType.getInstance(org.lgna.story.SProgram.class));
    Set<NamedUserType> types = new HashSet<>();
    types.add(sceneType);
    Project project = new Project(programType, types, Collections.emptySet(), Project.SceneCameraType.WindowCamera);
    return new ProjectInfo(project);
  }

  @Test
  public void methodInfoDeclarationReturnsCorrectMethod() {
    Assume.assumeFalse(java.awt.GraphicsEnvironment.isHeadless());
    NamedUserType type = createSceneType();
    UserMethod method = new UserMethod();
    method.name.setValue("myMethod");
    method.returnType.setValue(JavaType.VOID_TYPE);
    method.body.setValue(new BlockStatement());
    type.methods.add(method);

    ProjectInfo info = createProjectInfoWith(type);
    TypeInfo typeInfo = info.getInfoForType(type);
    MethodInfo methodInfo = typeInfo.getInfoForMethod(method);
    assertNotNull(methodInfo);
    assertEquals("myMethod", methodInfo.getDeclaration().getName());
  }

  @Test
  public void fieldInfoDeclarationReturnsCorrectField() {
    Assume.assumeFalse(java.awt.GraphicsEnvironment.isHeadless());
    NamedUserType type = createSceneType();
    UserField field = new UserField();
    field.name.setValue("someField");
    field.valueType.setValue(JavaType.getInstance(org.lgna.story.SModel.class));
    type.fields.add(field);

    ProjectInfo info = createProjectInfoWith(type);
    TypeInfo typeInfo = info.getInfoForType(type);
    FieldInfo fieldInfo = typeInfo.getInfoForField(field);
    assertNotNull(fieldInfo);
    assertEquals("someField", fieldInfo.getDeclaration().getName());
  }

  @Test
  public void resetRequiredDoesNotThrowOnMemberInfo() {
    Assume.assumeFalse(java.awt.GraphicsEnvironment.isHeadless());
    NamedUserType type = createSceneType();
    UserMethod method = new UserMethod();
    method.name.setValue("aMethod");
    method.returnType.setValue(JavaType.VOID_TYPE);
    method.body.setValue(new BlockStatement());
    type.methods.add(method);

    ProjectInfo info = createProjectInfoWith(type);
    TypeInfo typeInfo = info.getInfoForType(type);
    MethodInfo methodInfo = typeInfo.getInfoForMethod(method);
    assertNotNull(methodInfo);
    methodInfo.resetRequired();
  }

  @Test
  public void appendDesiredInitiallyEmpty() {
    Assume.assumeFalse(java.awt.GraphicsEnvironment.isHeadless());
    NamedUserType type = createSceneType();
    UserField field = new UserField();
    field.name.setValue("aField");
    field.valueType.setValue(JavaType.getInstance(org.lgna.story.SModel.class));
    type.fields.add(field);

    ProjectInfo info = createProjectInfoWith(type);
    TypeInfo typeInfo = info.getInfoForType(type);
    FieldInfo fieldInfo = typeInfo.getInfoForField(field);
    assertNotNull(fieldInfo);

    List<DeclarationInfo<?>> desired = new ArrayList<>();
    fieldInfo.appendDesired(desired);
    // Nothing has been selected yet, so desired list should be empty
    assertTrue("Desired list should be empty when nothing is selected", desired.isEmpty());
  }

  @Test
  public void getProjectInfoReturnsOwningProjectInfo() {
    Assume.assumeFalse(java.awt.GraphicsEnvironment.isHeadless());
    NamedUserType type = createSceneType();
    UserMethod method = new UserMethod();
    method.name.setValue("testProjRef");
    method.returnType.setValue(JavaType.VOID_TYPE);
    method.body.setValue(new BlockStatement());
    type.methods.add(method);

    ProjectInfo info = createProjectInfoWith(type);
    TypeInfo typeInfo = info.getInfoForType(type);
    MethodInfo methodInfo = typeInfo.getInfoForMethod(method);
    assertNotNull(methodInfo);
    assertSame("getProjectInfo should return the owning ProjectInfo", info, methodInfo.getProjectInfo());
  }
}
