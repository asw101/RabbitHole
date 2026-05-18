package org.alice.ide.ast.export;

import org.junit.Assume;
import org.junit.Test;
import org.lgna.project.Project;
import org.lgna.project.ast.ConstructorBlockStatement;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.NamedUserConstructor;
import org.lgna.project.ast.NamedUserType;
import org.lgna.project.ast.UserParameter;

import javax.swing.JCheckBox;
import java.awt.GraphicsEnvironment;

import static org.junit.Assert.*;

public class DeclarationInfoTest {

  @Test
  public void getDeclarationReturnsTheConstructor() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    ConstructorFixture fixture = createConstructorFixture();

    assertSame(fixture.constructor, fixture.constructorInfo.getDeclaration());
  }

  @Test
  public void getProjectInfoReturnsTheOwningProjectInfo() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    ConstructorFixture fixture = createConstructorFixture();

    assertSame(fixture.projectInfo, fixture.constructorInfo.getProjectInfo());
  }

  @Test
  public void getCheckBoxReturnsNonNullJCheckBox() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    ConstructorFixture fixture = createConstructorFixture();

    JCheckBox checkBox = fixture.constructorInfo.getCheckBox();

    assertNotNull(checkBox);
  }

  @Test
  public void resetRequiredAndUpdateSwingDoNotThrow() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    ConstructorFixture fixture = createConstructorFixture();

    fixture.constructorInfo.resetRequired();
    fixture.constructorInfo.updateSwing();

    assertNotNull(fixture.constructorInfo.getCheckBox());
  }

  @Test
  public void updateSwingLeavesCheckBoxEnabledWhenNothingIsRequiredOrDesired() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    ConstructorFixture fixture = createConstructorFixture();

    fixture.constructorInfo.resetRequired();
    fixture.constructorInfo.updateSwing();

    assertTrue(fixture.constructorInfo.getCheckBox().isEnabled());
    assertFalse(fixture.constructorInfo.getCheckBox().isSelected());
  }

  private static ConstructorFixture createConstructorFixture() {
    NamedUserType type = new NamedUserType();
    type.name.setValue("T");
    type.superType.setValue(JavaType.getInstance(Object.class));

    NamedUserConstructor constructor = new NamedUserConstructor(new UserParameter[] {}, new ConstructorBlockStatement());
    type.constructors.add(constructor);

    ProjectInfo projectInfo = new ProjectInfo(new Project(type, Project.SceneCameraType.WindowCamera));
    TypeInfo typeInfo = projectInfo.getInfoForType(type);

    return new ConstructorFixture(projectInfo, constructor, typeInfo.getInfoForConstructor(constructor));
  }

  private static class ConstructorFixture {
    private final ProjectInfo projectInfo;
    private final NamedUserConstructor constructor;
    private final ConstructorInfo constructorInfo;

    private ConstructorFixture(ProjectInfo projectInfo, NamedUserConstructor constructor, ConstructorInfo constructorInfo) {
      this.projectInfo = projectInfo;
      this.constructor = constructor;
      this.constructorInfo = constructorInfo;
    }
  }
}
