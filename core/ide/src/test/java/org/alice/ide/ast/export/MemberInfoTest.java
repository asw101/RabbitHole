package org.alice.ide.ast.export;

import org.junit.Test;
import org.lgna.project.ast.*;

import static org.junit.Assert.*;

/**
 * Tests for {@link MemberInfo} — constructor, declaration access, and
 * basic ProjectInfo integration for member declarations.
 *
 * MemberInfo requires a ProjectInfo to construct, which in turn needs a Project.
 * We build a minimal Project with one NamedUserType containing a method and field.
 */
public class MemberInfoTest {

  // ---- MemberInfo for a UserMethod ----

  @Test
  public void memberInfo_forMethod_declarationIsCorrect() {
    ProjectAndTypes pat = createMinimalProject();
    ProjectInfo projectInfo = new ProjectInfo(pat.project);
    TypeInfo typeInfo = projectInfo.getInfoForType(pat.sceneType);
    assertNotNull(typeInfo);

    UserMethod method = pat.sceneType.methods.get(0);
    MethodInfo methodInfo = typeInfo.getInfoForMethod(method);
    assertNotNull(methodInfo);
    assertSame(method, methodInfo.getDeclaration());
  }

  @Test
  public void memberInfo_forMethod_nameMatchesDeclaration() {
    ProjectAndTypes pat = createMinimalProject();
    ProjectInfo projectInfo = new ProjectInfo(pat.project);
    TypeInfo typeInfo = projectInfo.getInfoForType(pat.sceneType);

    UserMethod method = pat.sceneType.methods.get(0);
    MethodInfo methodInfo = typeInfo.getInfoForMethod(method);
    assertEquals("testMethod", methodInfo.getDeclaration().getName());
  }

  // ---- MemberInfo for a UserField ----

  @Test
  public void memberInfo_forField_declarationIsCorrect() {
    ProjectAndTypes pat = createMinimalProject();
    ProjectInfo projectInfo = new ProjectInfo(pat.project);
    TypeInfo typeInfo = projectInfo.getInfoForType(pat.sceneType);

    UserField field = pat.sceneType.fields.get(0);
    FieldInfo fieldInfo = typeInfo.getInfoForField(field);
    assertNotNull(fieldInfo);
    assertSame(field, fieldInfo.getDeclaration());
  }

  @Test
  public void memberInfo_forField_nameMatchesDeclaration() {
    ProjectAndTypes pat = createMinimalProject();
    ProjectInfo projectInfo = new ProjectInfo(pat.project);
    TypeInfo typeInfo = projectInfo.getInfoForType(pat.sceneType);

    UserField field = pat.sceneType.fields.get(0);
    FieldInfo fieldInfo = typeInfo.getInfoForField(field);
    assertEquals("testField", fieldInfo.getDeclaration().getName());
  }

  // ---- ProjectInfo references ----

  @Test
  public void memberInfo_getProjectInfo_returnsCorrectInstance() {
    ProjectAndTypes pat = createMinimalProject();
    ProjectInfo projectInfo = new ProjectInfo(pat.project);
    TypeInfo typeInfo = projectInfo.getInfoForType(pat.sceneType);

    UserMethod method = pat.sceneType.methods.get(0);
    MethodInfo methodInfo = typeInfo.getInfoForMethod(method);
    assertSame(projectInfo, methodInfo.getProjectInfo());
  }

  // ---- helper ----

  private static class ProjectAndTypes {
    final org.lgna.project.Project project;
    final NamedUserType programType;
    final NamedUserType sceneType;

    ProjectAndTypes(org.lgna.project.Project project, NamedUserType programType, NamedUserType sceneType) {
      this.project = project;
      this.programType = programType;
      this.sceneType = sceneType;
    }
  }

  private static ProjectAndTypes createMinimalProject() {
    // Create a scene type with one method and one field
    NamedUserType sceneType = new NamedUserType();
    sceneType.name.setValue("TestScene");
    sceneType.superType.setValue(JavaType.getInstance(Object.class));

    UserMethod method = new UserMethod();
    method.name.setValue("testMethod");
    method.returnType.setValue(JavaType.VOID_TYPE);
    method.managementLevel.setValue(ManagementLevel.NONE);
    method.body.setValue(new BlockStatement());
    sceneType.methods.add(method);

    UserField field = new UserField();
    field.name.setValue("testField");
    field.valueType.setValue(JavaType.getInstance(String.class));
    field.initializer.setValue(new NullLiteral());
    sceneType.fields.add(field);

    // Create a program type that references the scene type
    NamedUserType programType = new NamedUserType();
    programType.name.setValue("TestProgram");
    programType.superType.setValue(JavaType.getInstance(Object.class));

    UserField sceneField = new UserField();
    sceneField.name.setValue("myScene");
    sceneField.valueType.setValue(sceneType);
    sceneField.initializer.setValue(new NullLiteral());
    programType.fields.add(sceneField);

    org.lgna.project.Project project = new org.lgna.project.Project(programType, org.lgna.project.Project.SceneCameraType.WindowCamera);
    return new ProjectAndTypes(project, programType, sceneType);
  }
}
