package org.alice.ide.ast.export;

import org.junit.Test;
import org.lgna.project.Project;
import org.lgna.project.ast.*;

import java.util.Collection;

import static org.junit.Assert.*;

/**
 * Coverage tests for {@link ProjectInfo}.
 * Behavioral tests constructing with simple Project instances.
 */
public class ProjectInfoCoverageTest {

  private NamedUserType createMinimalType(String name) {
    NamedUserType type = new NamedUserType();
    type.name.setValue(name);
    return type;
  }

  private Project createProject(NamedUserType programType) {
    return new Project(programType, Project.SceneCameraType.WindowCamera);
  }

  @Test
  public void constructWithSimpleProject_notNull() {
    ProjectInfo info = new ProjectInfo(createProject(createMinimalType("Prog")));
    assertNotNull(info);
  }

  @Test
  public void getTypeInfos_containsAtLeastProgramType() {
    NamedUserType type = createMinimalType("Prog");
    ProjectInfo info = new ProjectInfo(createProject(type));
    assertFalse(info.getTypeInfos().isEmpty());
  }

  @Test
  public void getInfoForType_forProgramType_notNull() {
    NamedUserType type = createMinimalType("Prog");
    ProjectInfo info = new ProjectInfo(createProject(type));
    assertNotNull(info.getInfoForType(type));
  }

  @Test
  public void getInfoForType_forUnknownType_returnsNull() {
    NamedUserType type = createMinimalType("Prog");
    ProjectInfo info = new ProjectInfo(createProject(type));
    NamedUserType unknown = createMinimalType("Unknown");
    assertNull(info.getInfoForType(unknown));
  }

  @Test
  public void isInTheMidstOfChange_initiallyFalse() {
    ProjectInfo info = new ProjectInfo(createProject(createMinimalType("Prog")));
    assertFalse(info.isInTheMidstOfChange());
  }

  @Test
  public void update_setsAndResetsChange() {
    ProjectInfo info = new ProjectInfo(createProject(createMinimalType("Prog")));
    info.update();
    assertFalse(info.isInTheMidstOfChange());
  }

  @Test
  public void getTypeInfosAsTree_notNull() {
    ProjectInfo info = new ProjectInfo(createProject(createMinimalType("Prog")));
    assertNotNull(info.getTypeInfosAsTree());
  }

  @Test
  public void typeInfosCount_matchesProjectTypes() {
    NamedUserType type = createMinimalType("Prog");
    ProjectInfo info = new ProjectInfo(createProject(type));
    Collection<TypeInfo> infos = info.getTypeInfos();
    assertEquals(1, infos.size());
  }

  @Test
  public void projectWithField_typeInfoHasFieldInfo() {
    NamedUserType type = createMinimalType("Prog");
    UserField field = new UserField();
    field.name.setValue("score");
    field.valueType.setValue(JavaType.getInstance(Integer.class));
    type.fields.add(field);
    ProjectInfo info = new ProjectInfo(createProject(type));
    TypeInfo typeInfo = info.getInfoForType(type);
    assertFalse(typeInfo.getFieldInfos().isEmpty());
  }

  @Test
  public void projectWithMethod_typeInfoHasMethodInfo() {
    NamedUserType type = createMinimalType("Prog");
    UserMethod method = new UserMethod();
    method.name.setValue("run");
    method.returnType.setValue(JavaType.VOID_TYPE);
    method.managementLevel.setValue(ManagementLevel.NONE);
    method.body.setValue(new BlockStatement());
    type.methods.add(method);
    ProjectInfo info = new ProjectInfo(createProject(type));
    TypeInfo typeInfo = info.getInfoForType(type);
    assertFalse(typeInfo.getMethodInfos().isEmpty());
  }

  @Test
  public void update_multipleCallsDoNotThrow() {
    ProjectInfo info = new ProjectInfo(createProject(createMinimalType("Prog")));
    info.update();
    info.update();
    info.update();
    assertFalse(info.isInTheMidstOfChange());
  }
}
