package org.alice.ide.ast.export;

import org.junit.Assume;
import org.junit.Test;
import org.lgna.project.Project;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.NamedUserType;
import org.lgna.project.ast.UserField;

import java.awt.GraphicsEnvironment;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

public class ProjectInfoTest {

  @Test
  public void constructionWithSimpleProjectContainingOneNamedUserTypeSucceeds() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    ProjectFixture fixture = createProjectFixture();

    ProjectInfo info = new ProjectInfo(fixture.project);

    assertNotNull(info);
  }

  @Test
  public void getTypeInfosReturnsNonEmptyCollection() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    ProjectFixture fixture = createProjectFixture();

    ProjectInfo info = new ProjectInfo(fixture.project);

    assertFalse(info.getTypeInfos().isEmpty());
  }

  @Test
  public void getInfoForTypeReturnsTypeInfoForExistingType() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    ProjectFixture fixture = createProjectFixture();

    ProjectInfo info = new ProjectInfo(fixture.project);
    TypeInfo typeInfo = info.getInfoForType(fixture.sceneType);

    assertNotNull(typeInfo);
    assertSame(fixture.sceneType, typeInfo.getDeclaration());
  }

  @Test
  public void getInfoForTypeReturnsNullForNonExistingType() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    ProjectFixture fixture = createProjectFixture();

    ProjectInfo info = new ProjectInfo(fixture.project);
    NamedUserType unknown = new NamedUserType();
    unknown.name.setValue("UnknownType");
    unknown.superType.setValue(JavaType.getInstance(Object.class));

    assertNull(info.getInfoForType(unknown));
  }

  @Test
  public void isInTheMidstOfChangeIsFalseInitially() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    ProjectFixture fixture = createProjectFixture();

    ProjectInfo info = new ProjectInfo(fixture.project);

    assertFalse(info.isInTheMidstOfChange());
  }

  @Test
  public void getTypeInfosAsTreeReturnsNonNullRoot() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    ProjectFixture fixture = createProjectFixture();

    ProjectInfo info = new ProjectInfo(fixture.project);

    assertNotNull(info.getTypeInfosAsTree());
  }

  private static ProjectFixture createProjectFixture() {
    NamedUserType sceneType = new NamedUserType();
    sceneType.name.setValue("MyScene");
    sceneType.superType.setValue(JavaType.getInstance(Object.class));

    NamedUserType programType = new NamedUserType();
    programType.name.setValue("MyProgram");
    programType.superType.setValue(JavaType.getInstance(Object.class));

    UserField sceneField = new UserField();
    sceneField.name.setValue("myScene");
    sceneField.valueType.setValue(sceneType);
    programType.fields.add(sceneField);

    Set<NamedUserType> types = new HashSet<NamedUserType>();
    types.add(sceneType);

    return new ProjectFixture(new Project(programType, types, Collections.<org.lgna.common.Resource>emptySet(), Project.SceneCameraType.WindowCamera), sceneType);
  }

  private static class ProjectFixture {
    private final Project project;
    private final NamedUserType sceneType;

    private ProjectFixture(Project project, NamedUserType sceneType) {
      this.project = project;
      this.sceneType = sceneType;
    }
  }
}
