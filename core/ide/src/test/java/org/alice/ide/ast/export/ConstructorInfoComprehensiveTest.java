package org.alice.ide.ast.export;

import static org.alice.ide.testing.JacocoReflectionSupport.declaredMethods;

import org.junit.Test;
import org.lgna.project.Project;
import org.lgna.project.ast.BlockStatement;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.NamedUserType;
import org.lgna.project.ast.NullLiteral;
import org.lgna.project.ast.NamedUserConstructor;
import org.lgna.project.ast.UserConstructor;
import org.lgna.project.ast.UserField;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

public class ConstructorInfoComprehensiveTest {

  private NamedUserType createType(String name) {
    NamedUserType type = new NamedUserType();
    type.name.setValue(name);
    type.superType.setValue(JavaType.OBJECT_TYPE);
    return type;
  }

  private ProjectInfo createProjectInfoFor(NamedUserType type) {
    NamedUserType programType = createType("Program");
    programType.fields.add(new UserField("ref" + type.getName(), type, new NullLiteral()));
    return new ProjectInfo(new Project(programType, Project.SceneCameraType.WindowCamera));
  }

  private TypeInfo createTypeInfoWithConstructors(int count) {
    NamedUserType type = createType("HasConstructors" + count);
    for (int i = 0; i < count; i++) {
      NamedUserConstructor constructor = new NamedUserConstructor();
      constructor.body.setValue(new org.lgna.project.ast.ConstructorBlockStatement());
      type.constructors.add(constructor);
    }
    ProjectInfo projectInfo = createProjectInfoFor(type);
    return projectInfo.getInfoForType(type);
  }

  @Test
  public void classIsPublic() {
    assertTrue(Modifier.isPublic(ConstructorInfo.class.getModifiers()));
  }

  @Test
  public void classIsConcrete() {
    assertFalse(Modifier.isAbstract(ConstructorInfo.class.getModifiers()));
  }

  @Test
  public void classIsNotFinal() {
    assertFalse(Modifier.isFinal(ConstructorInfo.class.getModifiers()));
  }

  @Test
  public void classIsNotInterfaceOrEnum() {
    assertFalse(ConstructorInfo.class.isInterface());
    assertFalse(ConstructorInfo.class.isEnum());
  }

  @Test
  public void extendsMemberInfo() {
    assertTrue(MemberInfo.class.isAssignableFrom(ConstructorInfo.class));
  }

  @Test
  public void extendsDeclarationInfo() {
    assertTrue(DeclarationInfo.class.isAssignableFrom(ConstructorInfo.class));
  }

  @Test
  public void simpleNameMatchesSource() {
    assertEquals("ConstructorInfo", ConstructorInfo.class.getSimpleName());
  }

  @Test
  public void packageNameMatchesSource() {
    assertEquals("org.alice.ide.ast.export", ConstructorInfo.class.getPackage().getName());
  }

  @Test
  public void constructorExists() throws Exception {
    assertNotNull(ConstructorInfo.class.getDeclaredConstructor(ProjectInfo.class, UserConstructor.class));
  }

  @Test
  public void constructorIsPublic() throws Exception {
    Constructor<?> constructor = ConstructorInfo.class.getDeclaredConstructor(ProjectInfo.class, UserConstructor.class);
    assertTrue(Modifier.isPublic(constructor.getModifiers()));
  }

  @Test
  public void constructorHasTwoParameters() throws Exception {
    assertEquals(2, ConstructorInfo.class.getDeclaredConstructor(ProjectInfo.class, UserConstructor.class).getParameterCount());
  }

  @Test
  public void directConstructionIsPossible() {
    NamedUserType type = createType("DirectType");
    NamedUserConstructor constructor = new NamedUserConstructor();
    constructor.body.setValue(new org.lgna.project.ast.ConstructorBlockStatement());
    type.constructors.add(constructor);
    ProjectInfo projectInfo = createProjectInfoFor(type);
    assertNotNull(new ConstructorInfo(projectInfo, constructor));
  }

  @Test
  public void getDeclarationReturnsOriginalConstructor() {
    NamedUserType type = createType("CtorType");
    NamedUserConstructor constructor = new NamedUserConstructor();
    constructor.body.setValue(new org.lgna.project.ast.ConstructorBlockStatement());
    type.constructors.add(constructor);
    ProjectInfo projectInfo = createProjectInfoFor(type);
    ConstructorInfo info = new ConstructorInfo(projectInfo, constructor);
    assertSame(constructor, info.getDeclaration());
  }

  @Test
  public void getProjectInfoReturnsOriginalProjectInfo() {
    NamedUserType type = createType("ProjectType");
    NamedUserConstructor constructor = new NamedUserConstructor();
    constructor.body.setValue(new org.lgna.project.ast.ConstructorBlockStatement());
    type.constructors.add(constructor);
    ProjectInfo projectInfo = createProjectInfoFor(type);
    ConstructorInfo info = new ConstructorInfo(projectInfo, constructor);
    assertSame(projectInfo, info.getProjectInfo());
  }

  @Test
  public void getCheckBoxReturnsNonNullSwingComponent() {
    TypeInfo typeInfo = createTypeInfoWithConstructors(1);
    ConstructorInfo info = typeInfo.getConstructorInfos().iterator().next();
    assertNotNull(info.getCheckBox());
  }

  @Test
  public void getCheckBoxTextMatchesConstructorName() {
    TypeInfo typeInfo = createTypeInfoWithConstructors(1);
    ConstructorInfo info = typeInfo.getConstructorInfos().iterator().next();
    assertEquals(info.getDeclaration().getName(), info.getCheckBox().getText());
  }

  @Test
  public void typeInfoExposesSingleConstructorInfo() {
    assertEquals(1, createTypeInfoWithConstructors(1).getConstructorInfos().size());
  }

  @Test
  public void typeInfoExposesMultipleConstructorInfos() {
    assertEquals(2, createTypeInfoWithConstructors(2).getConstructorInfos().size());
  }

  @Test
  public void getInfoForConstructorReturnsMappedInstance() {
    NamedUserType type = createType("LookupType");
    NamedUserConstructor constructor = new NamedUserConstructor();
    constructor.body.setValue(new org.lgna.project.ast.ConstructorBlockStatement());
    type.constructors.add(constructor);
    TypeInfo typeInfo = createProjectInfoFor(type).getInfoForType(type);
    assertNotNull(typeInfo.getInfoForConstructor(constructor));
  }

  @Test
  public void constructorInfosCollectionIsUnmodifiable() {
    try {
      createTypeInfoWithConstructors(1).getConstructorInfos().clear();
      fail("Expected UnsupportedOperationException");
    } catch (UnsupportedOperationException expected) {
      assertNotNull(expected);
    }
  }

  @Test
  public void constructorInfoInstancesHaveExactType() {
    assertSame(ConstructorInfo.class, createTypeInfoWithConstructors(1).getConstructorInfos().iterator().next().getClass());
  }

  @Test
  public void classHasOneDeclaredConstructor() {
    assertEquals(1, ConstructorInfo.class.getDeclaredConstructors().length);
  }

  @Test
  public void classHasNoDeclaredMethods() {
    assertEquals(0, declaredMethods(ConstructorInfo.class).length);
  }

  @Test
  public void superclassIsMemberInfo() {
    assertEquals(MemberInfo.class, ConstructorInfo.class.getSuperclass());
  }


  @Test
  public void constructorInfosAreAssignableToDeclarationInfo() {
    assertTrue(DeclarationInfo.class.isAssignableFrom(ConstructorInfo.class));
  }

}
