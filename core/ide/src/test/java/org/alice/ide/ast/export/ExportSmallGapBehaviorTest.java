package org.alice.ide.ast.export;

import org.junit.Test;
import org.lgna.project.Project;
import org.lgna.project.ast.BlockStatement;
import org.lgna.project.ast.ConstructorBlockStatement;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.NamedUserConstructor;
import org.lgna.project.ast.NamedUserType;
import org.lgna.project.ast.SuperConstructorInvocationStatement;
import org.lgna.project.ast.UserField;
import org.lgna.project.ast.UserMethod;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class ExportSmallGapBehaviorTest {
  private static ProjectInfo createProjectInfo() {
    NamedUserType programType = new NamedUserType();
    programType.name.setValue("Program");
    programType.superType.setValue(JavaType.OBJECT_TYPE);

    UserField field = new UserField();
    field.name.setValue("message");
    field.valueType.setValue(JavaType.getInstance(String.class));
    programType.fields.add(field);

    UserMethod method = new UserMethod();
    method.name.setValue("doIt");
    method.returnType.setValue(JavaType.VOID_TYPE);
    method.body.setValue(new BlockStatement());
    programType.methods.add(method);

    NamedUserConstructor constructor = new NamedUserConstructor();
    ConstructorBlockStatement constructorBody = new ConstructorBlockStatement();
    constructorBody.constructorInvocationStatement.setValue(new SuperConstructorInvocationStatement());
    constructor.body.setValue(constructorBody);
    programType.constructors.add(constructor);

    return new ProjectInfo(new Project(programType, Project.SceneCameraType.WindowCamera));
  }

  private static TypeInfo onlyType(ProjectInfo info) {
    return info.getTypeInfos().iterator().next();
  }

  @Test
  public void selectingMemberMarksItsDeclaringTypeRequired() {
    ProjectInfo info = createProjectInfo();
    TypeInfo typeInfo = onlyType(info);
    MethodInfo methodInfo = typeInfo.getMethodInfos().iterator().next();

    methodInfo.getCheckBox().setSelected(true);

    assertTrue(methodInfo.getCheckBox().isSelected());
    assertTrue(methodInfo.getCheckBox().isEnabled());
    assertTrue(typeInfo.getCheckBox().isSelected());
    assertFalse(typeInfo.getCheckBox().isEnabled());
  }

  @Test
  public void deselectingMemberClearsRequiredSelectionFromType() {
    ProjectInfo info = createProjectInfo();
    TypeInfo typeInfo = onlyType(info);
    FieldInfo fieldInfo = typeInfo.getFieldInfos().iterator().next();

    fieldInfo.getCheckBox().setSelected(true);
    fieldInfo.getCheckBox().setSelected(false);

    assertFalse(fieldInfo.getCheckBox().isSelected());
    assertFalse(typeInfo.getCheckBox().isSelected());
    assertTrue(typeInfo.getCheckBox().isEnabled());
  }

  @Test
  public void appendDesiredIncludesTypeAndAnySelectedMembers() {
    ProjectInfo info = createProjectInfo();
    TypeInfo typeInfo = onlyType(info);
    ConstructorInfo constructorInfo = typeInfo.getConstructorInfos().iterator().next();

    typeInfo.getCheckBox().setSelected(true);
    constructorInfo.getCheckBox().setSelected(true);

    List<DeclarationInfo<?>> desired = new ArrayList<>();
    typeInfo.appendDesired(desired);

    assertTrue(desired.contains(typeInfo));
    assertTrue(desired.contains(constructorInfo));
    assertEquals(2, desired.size());
  }

  @Test
  public void memberDependencyToolTipsAreInitializedDuringProjectBuild() {
    ProjectInfo info = createProjectInfo();
    TypeInfo typeInfo = onlyType(info);

    assertNotNull(typeInfo.getFieldInfos().iterator().next().getCheckBox().getToolTipText());
    assertNotNull(typeInfo.getMethodInfos().iterator().next().getCheckBox().getToolTipText());
    assertNotNull(typeInfo.getConstructorInfos().iterator().next().getCheckBox().getToolTipText());
  }

  @Test
  public void typeInfoMapsReturnTheSameMemberInfoInstancesForTheirDeclarations() {
    ProjectInfo info = createProjectInfo();
    TypeInfo typeInfo = onlyType(info);
    ConstructorInfo constructorInfo = typeInfo.getConstructorInfos().iterator().next();
    MethodInfo methodInfo = typeInfo.getMethodInfos().iterator().next();
    FieldInfo fieldInfo = typeInfo.getFieldInfos().iterator().next();

    assertSame(constructorInfo, typeInfo.getInfoForConstructor(constructorInfo.getDeclaration()));
    assertSame(methodInfo, typeInfo.getInfoForMethod(methodInfo.getDeclaration()));
    assertSame(fieldInfo, typeInfo.getInfoForField(fieldInfo.getDeclaration()));
  }
}
