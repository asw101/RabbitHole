package org.alice.ide.ast.export;

import org.junit.Test;
import org.lgna.project.Project;
import org.lgna.project.ast.BlockStatement;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.NamedUserType;
import org.lgna.project.ast.NullLiteral;
import org.lgna.project.ast.NamedUserConstructor;
import org.lgna.project.ast.UserConstructor;
import org.lgna.project.ast.UserField;
import org.lgna.project.ast.UserMethod;
import org.lgna.project.ast.UserParameter;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

public class TypeInfoDeepTest {

  private NamedUserType createType(String name) {
    NamedUserType type = new NamedUserType();
    type.name.setValue(name);
    return type;
  }

  private ProjectInfo createProjectInfoFor(NamedUserType type) {
    NamedUserType programType = createType("Program");
    programType.fields.add(new UserField("ref" + type.getName(), type, new NullLiteral()));
    return new ProjectInfo(new Project(programType, Project.SceneCameraType.WindowCamera));
  }

  private TypeInfo createTypeInfo(NamedUserType type) {
    return createProjectInfoFor(type).getInfoForType(type);
  }

  @Test
  public void classIsPublic() {
    assertTrue(Modifier.isPublic(TypeInfo.class.getModifiers()));
  }

  @Test
  public void classIsConcrete() {
    assertFalse(Modifier.isAbstract(TypeInfo.class.getModifiers()));
  }

  @Test
  public void extendsDeclarationInfo() {
    assertTrue(DeclarationInfo.class.isAssignableFrom(TypeInfo.class));
  }

  @Test
  public void simpleNameMatchesSource() {
    assertEquals("TypeInfo", TypeInfo.class.getSimpleName());
  }

  @Test
  public void packageNameMatchesSource() {
    assertEquals("org.alice.ide.ast.export", TypeInfo.class.getPackage().getName());
  }

  @Test
  public void constructorExists() throws Exception {
    assertNotNull(TypeInfo.class.getDeclaredConstructor(ProjectInfo.class, org.lgna.project.ast.UserType.class));
  }

  @Test
  public void constructorIsPublic() throws Exception {
    Constructor<?> constructor = TypeInfo.class.getDeclaredConstructor(ProjectInfo.class, org.lgna.project.ast.UserType.class);
    assertTrue(Modifier.isPublic(constructor.getModifiers()));
  }

  @Test
  public void getDeclarationReturnsSameType() {
    NamedUserType type = createType("SingleType");
    assertSame(type, createTypeInfo(type).getDeclaration());
  }

  @Test
  public void getSuperTypeInfoReturnsNullForJavaSuperType() {
    NamedUserType type = createType("Child");
    type.superType.setValue(JavaType.OBJECT_TYPE);
    assertNull(createTypeInfo(type).getSuperTypeInfo());
  }

  @Test
  public void getSuperTypeInfoReturnsParentForUserSuperType() {
    NamedUserType parent = createType("Parent");
    parent.superType.setValue(JavaType.OBJECT_TYPE);
    NamedUserType child = createType("Child");
    child.superType.setValue(parent);
    ProjectInfo projectInfo = createProjectInfoFor(child);
    TypeInfo childInfo = projectInfo.getInfoForType(child);
    assertSame(projectInfo.getInfoForType(parent), childInfo.getSuperTypeInfo());
  }

  @Test
  public void emptyTypeHasNoConstructorInfos() {
    assertTrue(createTypeInfo(createType("NoConstructors")).getConstructorInfos().isEmpty());
  }

  @Test
  public void singleConstructorIsExposed() {
    NamedUserType type = createType("WithCtor");
    NamedUserConstructor constructor = new NamedUserConstructor();
    constructor.body.setValue(new org.lgna.project.ast.ConstructorBlockStatement());
    type.constructors.add(constructor);
    assertEquals(1, createTypeInfo(type).getConstructorInfos().size());
  }

  @Test
  public void getInfoForConstructorReturnsMappedInfo() {
    NamedUserType type = createType("CtorLookup");
    NamedUserConstructor constructor = new NamedUserConstructor();
    constructor.body.setValue(new org.lgna.project.ast.ConstructorBlockStatement());
    type.constructors.add(constructor);
    TypeInfo info = createTypeInfo(type);
    assertNotNull(info.getInfoForConstructor(constructor));
  }

  @Test
  public void methodInfosExposeProcedureMethod() {
    NamedUserType type = createType("WithMethod");
    type.methods.add(new UserMethod("doThing", JavaType.VOID_TYPE, new UserParameter[0], new BlockStatement()));
    assertEquals(1, createTypeInfo(type).getMethodInfos().size());
  }

  @Test
  public void getInfoForMethodReturnsMappedInfo() {
    NamedUserType type = createType("MethodLookup");
    UserMethod method = new UserMethod("doThing", JavaType.VOID_TYPE, new UserParameter[0], new BlockStatement());
    type.methods.add(method);
    TypeInfo info = createTypeInfo(type);
    assertNotNull(info.getInfoForMethod(method));
  }

  @Test
  public void fieldInfosExposeUserField() {
    NamedUserType type = createType("WithField");
    UserField field = new UserField("count", JavaType.INTEGER_OBJECT_TYPE, new NullLiteral());
    type.fields.add(field);
    assertEquals(1, createTypeInfo(type).getFieldInfos().size());
  }

  @Test
  public void getInfoForFieldReturnsMappedInfo() {
    NamedUserType type = createType("FieldLookup");
    UserField field = new UserField("name", JavaType.STRING_TYPE, new NullLiteral());
    type.fields.add(field);
    TypeInfo info = createTypeInfo(type);
    assertNotNull(info.getInfoForField(field));
  }

  @Test
  public void constructorInfosCollectionIsUnmodifiable() {
    NamedUserType type = createType("CtorImmutable");
    NamedUserConstructor constructor = new NamedUserConstructor();
    constructor.body.setValue(new org.lgna.project.ast.ConstructorBlockStatement());
    type.constructors.add(constructor);
    try {
      createTypeInfo(type).getConstructorInfos().clear();
      fail("Expected UnsupportedOperationException");
    } catch (UnsupportedOperationException expected) {
      assertNotNull(expected);
    }
  }

  @Test
  public void methodInfosCollectionIsUnmodifiable() {
    NamedUserType type = createType("MethodImmutable");
    type.methods.add(new UserMethod("jump", JavaType.VOID_TYPE, new UserParameter[0], new BlockStatement()));
    try {
      createTypeInfo(type).getMethodInfos().clear();
      fail("Expected UnsupportedOperationException");
    } catch (UnsupportedOperationException expected) {
      assertNotNull(expected);
    }
  }

  @Test
  public void fieldInfosCollectionIsUnmodifiable() {
    NamedUserType type = createType("FieldImmutable");
    type.fields.add(new UserField("title", JavaType.STRING_TYPE, new NullLiteral()));
    try {
      createTypeInfo(type).getFieldInfos().clear();
      fail("Expected UnsupportedOperationException");
    } catch (UnsupportedOperationException expected) {
      assertNotNull(expected);
    }
  }

  @Test
  public void resetRequiredCanBeInvoked() {
    createTypeInfo(createType("ResetType")).resetRequired();
  }

  @Test
  public void updateSwingCanBeInvoked() {
    createTypeInfo(createType("SwingType")).updateSwing();
  }

  @Test
  public void getProjectInfoReturnsOwningProjectInfo() {
    NamedUserType type = createType("ProjectOwned");
    ProjectInfo projectInfo = createProjectInfoFor(type);
    assertSame(projectInfo, projectInfo.getInfoForType(type).getProjectInfo());
  }
}
