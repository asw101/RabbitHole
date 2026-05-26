package org.alice.ide.ast.export;

import org.junit.Test;
import org.lgna.project.Project;
import org.lgna.project.ast.AstUtilities;
import org.lgna.project.ast.BlockStatement;
import org.lgna.project.ast.Comment;
import org.lgna.project.ast.ExpressionStatement;
import org.lgna.project.ast.FieldAccess;
import org.lgna.project.ast.JavaMethod;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.MethodInvocation;
import org.lgna.project.ast.NamedUserType;
import org.lgna.project.ast.NullLiteral;
import org.lgna.project.ast.ThisExpression;
import org.lgna.project.ast.UserField;
import org.lgna.project.ast.UserMethod;
import org.lgna.project.ast.UserParameter;
import org.lgna.story.SProgram;
import org.lgna.story.SScene;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ProjectInfoDependencyBehaviorTest {

  @Test
  public void selectingMethodMarksReferencedMembersAndTypesRequired() {
    ExportFixture fixture = ExportFixture.create();

    assertOptional(fixture.exportedMethodInfo);
    assertOptional(fixture.helperMethodInfo);
    assertOptional(fixture.helperFieldInfo);
    assertOptional(fixture.helperTypeInfo);

    fixture.exportedMethodInfo.getCheckBox().setSelected(true);

    assertDesired(fixture.exportedMethodInfo);
    assertRequired(fixture.sceneTypeInfo);
    assertRequired(fixture.helperMethodInfo);
    assertRequired(fixture.helperFieldInfo);
    assertRequired(fixture.helperTypeInfo);
    assertOptional(fixture.unrelatedMethodInfo);
  }

  @Test
  public void clearingSelectionReleasesTransitiveRequirements() {
    ExportFixture fixture = ExportFixture.create();

    fixture.exportedMethodInfo.getCheckBox().setSelected(true);
    fixture.exportedMethodInfo.getCheckBox().setSelected(false);

    assertOptional(fixture.exportedMethodInfo);
    assertOptional(fixture.sceneTypeInfo);
    assertOptional(fixture.helperMethodInfo);
    assertOptional(fixture.helperFieldInfo);
    assertOptional(fixture.helperTypeInfo);
  }

  @Test
  public void selectingDependencyFreeMethodLeavesOtherMembersOptional() {
    ExportFixture fixture = ExportFixture.create();

    fixture.unrelatedMethodInfo.getCheckBox().setSelected(true);

    assertDesired(fixture.unrelatedMethodInfo);
    assertRequired(fixture.sceneTypeInfo);
    assertOptional(fixture.exportedMethodInfo);
    assertOptional(fixture.helperMethodInfo);
    assertOptional(fixture.helperFieldInfo);
    assertOptional(fixture.helperTypeInfo);
  }

  private static void assertDesired(DeclarationInfo<?> info) {
    assertTrue(info.getCheckBox().isSelected());
    assertTrue(info.getCheckBox().isEnabled());
  }

  private static void assertRequired(DeclarationInfo<?> info) {
    assertTrue(info.getCheckBox().isSelected());
    assertFalse(info.getCheckBox().isEnabled());
  }

  private static void assertOptional(DeclarationInfo<?> info) {
    assertFalse(info.getCheckBox().isSelected());
    assertTrue(info.getCheckBox().isEnabled());
  }

  private static final class ExportFixture {
    private final TypeInfo sceneTypeInfo;
    private final TypeInfo helperTypeInfo;
    private final MethodInfo exportedMethodInfo;
    private final MethodInfo unrelatedMethodInfo;
    private final MethodInfo helperMethodInfo;
    private final FieldInfo helperFieldInfo;

    private ExportFixture(ProjectInfo projectInfo, NamedUserType sceneType, NamedUserType helperType,
        UserMethod exportedMethod, UserMethod unrelatedMethod, UserMethod helperMethod, UserField helperField) {
      this.sceneTypeInfo = projectInfo.getInfoForType(sceneType);
      this.helperTypeInfo = projectInfo.getInfoForType(helperType);
      this.exportedMethodInfo = this.sceneTypeInfo.getInfoForMethod(exportedMethod);
      this.unrelatedMethodInfo = this.sceneTypeInfo.getInfoForMethod(unrelatedMethod);
      this.helperMethodInfo = this.sceneTypeInfo.getInfoForMethod(helperMethod);
      this.helperFieldInfo = this.sceneTypeInfo.getInfoForField(helperField);
    }

    private static ExportFixture create() {
      NamedUserType sceneType = AstUtilities.createType("Scene", JavaType.getInstance(SScene.class));
      NamedUserType helperType = AstUtilities.createType("HelperActor", JavaType.OBJECT_TYPE);
      NamedUserType programType = AstUtilities.createType("Program", JavaType.getInstance(SProgram.class));
      programType.fields.add(new UserField("myScene", sceneType));

      UserField helperField = new UserField("helperActor", helperType, new NullLiteral());
      sceneType.fields.add(helperField);

      UserMethod helperMethod = new UserMethod(
          "helperLogic",
          JavaType.VOID_TYPE,
          new UserParameter[0],
          new BlockStatement(new Comment("helper dependency")));
      sceneType.methods.add(helperMethod);

      UserMethod unrelatedMethod = new UserMethod(
          "unrelatedLogic",
          JavaType.VOID_TYPE,
          new UserParameter[0],
          new BlockStatement(new Comment("no dependencies")));
      sceneType.methods.add(unrelatedMethod);

      MethodInvocation helperFieldToString = new MethodInvocation(
          new FieldAccess(new ThisExpression(), helperField),
          JavaMethod.getInstance(Object.class, "toString"));
      UserMethod exportedMethod = new UserMethod(
          "exportedLogic",
          JavaType.VOID_TYPE,
          new UserParameter[0],
          new BlockStatement(
              AstUtilities.createMethodInvocationStatement(new ThisExpression(), helperMethod),
              new ExpressionStatement(helperFieldToString)));
      sceneType.methods.add(exportedMethod);

      ProjectInfo projectInfo = new ProjectInfo(new Project(programType, Project.SceneCameraType.WindowCamera));
      return new ExportFixture(projectInfo, sceneType, helperType, exportedMethod, unrelatedMethod, helperMethod, helperField);
    }
  }
}
