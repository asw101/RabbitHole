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

public class ProjectInfoTest {
  private Project createMinimalProject() {
    NamedUserType sceneType = new NamedUserType();
    sceneType.name.setValue("MyScene");
    sceneType.superType.setValue(JavaType.getInstance(Object.class));

    NamedUserType programType = new NamedUserType();
    programType.name.setValue("MyProgram");
    programType.superType.setValue(JavaType.getInstance(Object.class));

    UserField sceneField = new UserField();
    sceneField.name.setValue("myScene");
    sceneField.valueType.setValue(sceneType);
    sceneField.initializer.setValue(new NullLiteral());
    programType.fields.add(sceneField);

    return new Project(programType, Project.SceneCameraType.WindowCamera);
  }

  @Test
  public void constructor_doesNotThrow() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    Project project = createMinimalProject();
    ProjectInfo info = new ProjectInfo(project);
    assertNotNull(info);
  }

  @Test
  public void getTypeInfos_returnsNonNull() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    Project project = createMinimalProject();
    ProjectInfo info = new ProjectInfo(project);
    assertNotNull(info.getTypeInfos());
  }

  @Test
  public void isInTheMidstOfChange_initiallyFalse() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    Project project = createMinimalProject();
    ProjectInfo info = new ProjectInfo(project);
    assertFalse(info.isInTheMidstOfChange());
  }
}
