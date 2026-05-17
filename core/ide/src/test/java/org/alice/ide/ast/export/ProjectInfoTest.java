package org.alice.ide.ast.export;

import org.junit.Assume;
import org.junit.Test;
import org.lgna.project.Project;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.NamedUserType;
import org.lgna.story.SScene;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

public class ProjectInfoTest {

  private static NamedUserType createProgramType() {
    NamedUserType programType = new NamedUserType();
    programType.name.setValue("Program");
    programType.superType.setValue(JavaType.getInstance(org.lgna.story.SProgram.class));
    return programType;
  }

  private static NamedUserType createSceneType() {
    NamedUserType sceneType = new NamedUserType();
    sceneType.name.setValue("MyScene");
    sceneType.superType.setValue(JavaType.getInstance(SScene.class));
    return sceneType;
  }

  @Test
  public void constructorWithEmptyTypesCreatesInfo() {
    Assume.assumeFalse(java.awt.GraphicsEnvironment.isHeadless());
    NamedUserType programType = createProgramType();
    Project project = new Project(programType, Project.SceneCameraType.WindowCamera);
    ProjectInfo info = new ProjectInfo(project);
    assertNotNull(info);
    assertNotNull(info.getTypeInfos());
  }

  @Test
  public void getInfoForTypeWithKnownTypeReturnsNonNull() {
    Assume.assumeFalse(java.awt.GraphicsEnvironment.isHeadless());
    NamedUserType programType = createProgramType();
    NamedUserType sceneType = createSceneType();
    Set<NamedUserType> types = new HashSet<>();
    types.add(sceneType);
    Project project = new Project(programType, types, Collections.emptySet(), Project.SceneCameraType.WindowCamera);
    ProjectInfo info = new ProjectInfo(project);
    TypeInfo typeInfo = info.getInfoForType(sceneType);
    assertNotNull("TypeInfo for sceneType should not be null", typeInfo);
  }

  @Test
  public void getInfoForTypeWithUnknownTypeReturnsNull() {
    Assume.assumeFalse(java.awt.GraphicsEnvironment.isHeadless());
    NamedUserType programType = createProgramType();
    Project project = new Project(programType, Project.SceneCameraType.WindowCamera);
    ProjectInfo info = new ProjectInfo(project);
    NamedUserType unknown = new NamedUserType();
    unknown.name.setValue("Unknown");
    unknown.superType.setValue(JavaType.getInstance(SScene.class));
    assertNull("TypeInfo for unknown type should be null", info.getInfoForType(unknown));
  }

  @Test
  public void typeInfosAsTreeIsNotNull() {
    Assume.assumeFalse(java.awt.GraphicsEnvironment.isHeadless());
    NamedUserType programType = createProgramType();
    Project project = new Project(programType, Project.SceneCameraType.WindowCamera);
    ProjectInfo info = new ProjectInfo(project);
    assertNotNull(info.getTypeInfosAsTree());
  }
}
