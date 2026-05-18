package org.alice.ide.ast.export;

import org.junit.Assume;
import org.junit.Test;
import org.lgna.project.Project;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.NamedUserType;
import org.lgna.project.ast.NullLiteral;
import org.lgna.project.ast.UserField;

import java.awt.GraphicsEnvironment;

import static org.junit.Assert.*;

public class TypeInfoTest {
  private NamedUserType createTestType() {
    NamedUserType type = new NamedUserType();
    type.name.setValue("TestType");
    type.superType.setValue(JavaType.getInstance(Object.class));
    return type;
  }

  private ProjectInfo createProjectInfo() {
    NamedUserType programType = new NamedUserType();
    programType.name.setValue("Program");
    programType.superType.setValue(JavaType.getInstance(Object.class));
    NamedUserType sceneType = createTestType();
    UserField sf = new UserField();
    sf.name.setValue("scene");
    sf.valueType.setValue(sceneType);
    sf.initializer.setValue(new NullLiteral());
    programType.fields.add(sf);
    return new ProjectInfo(new Project(programType, Project.SceneCameraType.WindowCamera));
  }

  @Test
  public void constructor_withProjectInfoAndType() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    ProjectInfo pInfo = createProjectInfo();
    NamedUserType type = createTestType();
    TypeInfo info = new TypeInfo(pInfo, type);
    assertNotNull(info);
    assertSame(type, info.getDeclaration());
  }

  @Test
  public void getMethodInfos_initiallyEmpty() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    ProjectInfo pInfo = createProjectInfo();
    NamedUserType type = createTestType();
    TypeInfo info = new TypeInfo(pInfo, type);
    assertNotNull(info.getMethodInfos());
  }

  @Test
  public void getFieldInfos_initiallyEmpty() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    ProjectInfo pInfo = createProjectInfo();
    NamedUserType type = createTestType();
    TypeInfo info = new TypeInfo(pInfo, type);
    assertNotNull(info.getFieldInfos());
  }

  @Test
  public void getConstructorInfos_initiallyEmpty() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    ProjectInfo pInfo = createProjectInfo();
    NamedUserType type = createTestType();
    TypeInfo info = new TypeInfo(pInfo, type);
    assertNotNull(info.getConstructorInfos());
  }

  @Test
  public void getProjectInfo_returnsSameInstance() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    ProjectInfo pInfo = createProjectInfo();
    NamedUserType type = createTestType();
    TypeInfo info = new TypeInfo(pInfo, type);
    assertSame(pInfo, info.getProjectInfo());
  }
}
