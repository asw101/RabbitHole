package org.alice.ide.ast.export;

import org.junit.Assume;
import org.junit.Test;
import org.lgna.project.Project;
import org.lgna.project.ast.BlockStatement;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.NamedUserType;
import org.lgna.project.ast.UserMethod;

import java.awt.GraphicsEnvironment;

import static org.junit.Assert.*;

public class MethodInfoTest {

  @Test
  public void constructionViaTypeInfoCreatesMethodInfo() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    MethodFixture fixture = createMethodFixture();

    assertNotNull(fixture.typeInfo.getInfoForMethod(fixture.firstMethod));
  }

  @Test
  public void getDeclarationReturnsTheUserMethod() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    MethodFixture fixture = createMethodFixture();

    MethodInfo methodInfo = fixture.typeInfo.getInfoForMethod(fixture.firstMethod);

    assertSame(fixture.firstMethod, methodInfo.getDeclaration());
  }

  @Test
  public void multipleMethodsInOneTypeEachGetTheirOwnMethodInfo() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    MethodFixture fixture = createMethodFixture();

    MethodInfo firstInfo = fixture.typeInfo.getInfoForMethod(fixture.firstMethod);
    MethodInfo secondInfo = fixture.typeInfo.getInfoForMethod(fixture.secondMethod);

    assertNotNull(firstInfo);
    assertNotNull(secondInfo);
    assertNotSame(firstInfo, secondInfo);
  }

  private static MethodFixture createMethodFixture() {
    NamedUserType type = new NamedUserType();
    type.name.setValue("MethodType");
    type.superType.setValue(JavaType.getInstance(Object.class));

    UserMethod firstMethod = createMethod("doSomething");
    UserMethod secondMethod = createMethod("doOtherThing");
    type.methods.add(firstMethod);
    type.methods.add(secondMethod);

    ProjectInfo projectInfo = new ProjectInfo(new Project(type, Project.SceneCameraType.WindowCamera));
    return new MethodFixture(projectInfo.getInfoForType(type), firstMethod, secondMethod);
  }

  private static UserMethod createMethod(String name) {
    UserMethod method = new UserMethod();
    method.name.setValue(name);
    method.returnType.setValue(JavaType.VOID_TYPE);
    method.body.setValue(new BlockStatement());
    return method;
  }

  private static class MethodFixture {
    private final TypeInfo typeInfo;
    private final UserMethod firstMethod;
    private final UserMethod secondMethod;

    private MethodFixture(TypeInfo typeInfo, UserMethod firstMethod, UserMethod secondMethod) {
      this.typeInfo = typeInfo;
      this.firstMethod = firstMethod;
      this.secondMethod = secondMethod;
    }
  }
}
