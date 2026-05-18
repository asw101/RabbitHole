package org.alice.ide.ast.export;

import org.junit.Assume;
import org.junit.Test;
import org.lgna.project.Project;
import org.lgna.project.ast.ConstructorBlockStatement;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.NamedUserConstructor;
import org.lgna.project.ast.NamedUserType;
import org.lgna.project.ast.UserParameter;

import java.awt.GraphicsEnvironment;

import static org.junit.Assert.*;

public class ConstructorInfoTest {

  @Test
  public void constructionViaTypeInfoCreatesConstructorInfo() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    ConstructorFixture fixture = createConstructorFixture();

    assertNotNull(fixture.typeInfo.getInfoForConstructor(fixture.firstConstructor));
  }

  @Test
  public void getDeclarationReturnsTheUserConstructor() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    ConstructorFixture fixture = createConstructorFixture();

    ConstructorInfo constructorInfo = fixture.typeInfo.getInfoForConstructor(fixture.firstConstructor);

    assertSame(fixture.firstConstructor, constructorInfo.getDeclaration());
  }

  @Test
  public void multipleConstructorsInOneTypeEachGetTheirOwnConstructorInfo() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    ConstructorFixture fixture = createConstructorFixture();

    ConstructorInfo firstInfo = fixture.typeInfo.getInfoForConstructor(fixture.firstConstructor);
    ConstructorInfo secondInfo = fixture.typeInfo.getInfoForConstructor(fixture.secondConstructor);

    assertNotNull(firstInfo);
    assertNotNull(secondInfo);
    assertNotSame(firstInfo, secondInfo);
  }

  private static ConstructorFixture createConstructorFixture() {
    NamedUserType type = new NamedUserType();
    type.name.setValue("ConstructorType");
    type.superType.setValue(JavaType.getInstance(Object.class));

    NamedUserConstructor firstConstructor = createConstructor();
    NamedUserConstructor secondConstructor = createConstructor();
    type.constructors.add(firstConstructor);
    type.constructors.add(secondConstructor);

    ProjectInfo projectInfo = new ProjectInfo(new Project(type, Project.SceneCameraType.WindowCamera));
    return new ConstructorFixture(projectInfo.getInfoForType(type), firstConstructor, secondConstructor);
  }

  private static NamedUserConstructor createConstructor() {
    return new NamedUserConstructor(new UserParameter[] {}, new ConstructorBlockStatement());
  }

  private static class ConstructorFixture {
    private final TypeInfo typeInfo;
    private final NamedUserConstructor firstConstructor;
    private final NamedUserConstructor secondConstructor;

    private ConstructorFixture(TypeInfo typeInfo, NamedUserConstructor firstConstructor, NamedUserConstructor secondConstructor) {
      this.typeInfo = typeInfo;
      this.firstConstructor = firstConstructor;
      this.secondConstructor = secondConstructor;
    }
  }
}
