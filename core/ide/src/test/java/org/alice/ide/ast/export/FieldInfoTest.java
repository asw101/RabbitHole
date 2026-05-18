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

public class FieldInfoTest {
  private ProjectInfo createProjectInfo() {
    NamedUserType programType = new NamedUserType();
    programType.name.setValue("Program");
    programType.superType.setValue(JavaType.getInstance(Object.class));
    NamedUserType sceneType = new NamedUserType();
    sceneType.name.setValue("Scene");
    sceneType.superType.setValue(JavaType.getInstance(Object.class));
    UserField sf = new UserField();
    sf.name.setValue("scene");
    sf.valueType.setValue(sceneType);
    sf.initializer.setValue(new NullLiteral());
    programType.fields.add(sf);
    return new ProjectInfo(new Project(programType, Project.SceneCameraType.WindowCamera));
  }

  @Test
  public void constructor_setsDeclaration() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    ProjectInfo pInfo = createProjectInfo();
    UserField field = new UserField();
    field.name.setValue("testField");
    field.valueType.setValue(JavaType.getInstance(String.class));
    FieldInfo info = new FieldInfo(pInfo, field);
    assertSame(field, info.getDeclaration());
  }

  @Test
  public void getProjectInfo_returnsSameInstance() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    ProjectInfo pInfo = createProjectInfo();
    UserField field = new UserField();
    field.name.setValue("f");
    field.valueType.setValue(JavaType.getInstance(Integer.class));
    FieldInfo info = new FieldInfo(pInfo, field);
    assertSame(pInfo, info.getProjectInfo());
  }

  @Test
  public void getCheckBox_returnsNonNull() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    ProjectInfo pInfo = createProjectInfo();
    UserField field = new UserField();
    field.name.setValue("boxField");
    field.valueType.setValue(JavaType.getInstance(String.class));
    FieldInfo info = new FieldInfo(pInfo, field);
    assertNotNull(info.getCheckBox());
  }
}
