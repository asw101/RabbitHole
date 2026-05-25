package org.alice.ide.ast.export;

import org.junit.Test;
import org.lgna.project.Project;
import org.lgna.project.ast.BlockStatement;
import org.lgna.project.ast.ConstructorBlockStatement;
import org.lgna.project.ast.ExpressionStatement;
import org.lgna.project.ast.FieldAccess;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.NamedUserConstructor;
import org.lgna.project.ast.NamedUserType;
import org.lgna.project.ast.NullLiteral;
import org.lgna.project.ast.UserField;
import org.lgna.project.ast.UserMethod;
import org.lgna.project.ast.UserParameter;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertTrue;

public class MemberInfoBehaviorTest {
  private static NamedUserType createType(String name) {
    NamedUserType type = new NamedUserType();
    type.name.setValue(name);
    type.superType.setValue(JavaType.getInstance(Object.class));
    NamedUserConstructor constructor = new NamedUserConstructor();
    constructor.body.setValue(new ConstructorBlockStatement());
    type.constructors.add(constructor);
    return type;
  }

  private static ProjectInfo createProjectInfo(NamedUserType programType) {
    return new ProjectInfo(new Project(programType, Project.SceneCameraType.WindowCamera));
  }

  @Test
  public void fieldInfoUpdateDependencies_listsReferencedUserTypeInTooltip() {
    NamedUserType helperType = createType("Helper");
    NamedUserType programType = createType("Program");
    UserField helperField = new UserField("helper", helperType, new NullLiteral());
    programType.fields.add(helperField);

    ProjectInfo projectInfo = createProjectInfo(programType);
    FieldInfo fieldInfo = projectInfo.getInfoForType(programType).getInfoForField(helperField);
    fieldInfo.updateDependencies();

    assertTrue(fieldInfo.getCheckBox().getToolTipText().contains("Helper"));
  }

  @Test
  public void methodInfoUpdateRequired_marksDeclaringTypeAndFieldDependencies() {
    NamedUserType helperType = createType("Helper");
    NamedUserType programType = createType("Program");
    UserField helperField = new UserField("helper", helperType, new NullLiteral());
    programType.fields.add(helperField);

    UserMethod method = new UserMethod("useHelper", JavaType.VOID_TYPE, new UserParameter[0], new BlockStatement());
    method.body.getValue().statements.add(new ExpressionStatement(new FieldAccess(helperField)));
    programType.methods.add(method);

    ProjectInfo projectInfo = createProjectInfo(programType);
    TypeInfo programTypeInfo = projectInfo.getInfoForType(programType);
    TypeInfo helperTypeInfo = projectInfo.getInfoForType(helperType);
    FieldInfo fieldInfo = programTypeInfo.getInfoForField(helperField);
    MethodInfo methodInfo = programTypeInfo.getInfoForMethod(method);

    methodInfo.updateDependencies();
    Set<DeclarationInfo<?>> visited = new HashSet<>();
    methodInfo.updateRequired(visited);

    assertTrue(methodInfo.getCheckBox().getToolTipText().contains("helper"));
    assertTrue(visited.contains(methodInfo));
    assertTrue(visited.contains(programTypeInfo));
    assertTrue(visited.contains(helperTypeInfo));
    assertTrue(visited.contains(fieldInfo));
  }

  @Test
  public void constructorInfoUpdateDependencies_tracksReferencedFields() {
    NamedUserType helperType = createType("Helper");
    NamedUserType programType = createType("Program");
    UserField helperField = new UserField("helper", helperType, new NullLiteral());
    programType.fields.add(helperField);

    NamedUserConstructor constructor = programType.getDeclaredConstructors().getFirst();
    constructor.body.getValue().statements.add(new ExpressionStatement(new FieldAccess(helperField)));

    ProjectInfo projectInfo = createProjectInfo(programType);
    ConstructorInfo constructorInfo = projectInfo.getInfoForType(programType).getInfoForConstructor(constructor);
    constructorInfo.updateDependencies();

    assertTrue(constructorInfo.getCheckBox().getToolTipText().contains("helper"));
  }
}
