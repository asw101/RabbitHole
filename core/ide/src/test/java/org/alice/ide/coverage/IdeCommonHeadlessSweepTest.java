package org.alice.ide.coverage;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

import static org.junit.Assert.assertTrue;

public class IdeCommonHeadlessSweepTest {

  @Rule
  public Timeout globalTimeout = Timeout.seconds(120);

  @Test
  public void exerciseIdeAstClasses() {
    HeadlessClassExerciseSupport.SmokeStats stats = HeadlessClassExerciseSupport.exercise(
        "org.alice.ide.ast.AstEventManager",
        "org.alice.ide.ast.CurrentThisExpression",
        "org.alice.ide.ast.EmptyExpression",
        "org.alice.ide.ast.ExpressionCreator",
        "org.alice.ide.ast.FieldInitializerInstanceCreationArgument0State",
        "org.alice.ide.ast.IdeExpression",
        "org.alice.ide.ast.IncompleteAstUtilities",
        "org.alice.ide.ast.PreviousValueExpression",
        "org.alice.ide.ast.PropertyState",
        "org.alice.ide.ast.SelectedInstanceFactoryExpression"
    );
    assertTrue("Should load at least 3 classes, loaded=" + stats.getLoadedCount(),
        stats.getLoadedCount() >= 3);
  }

  @Test
  public void exerciseIdeAstExportClasses() {
    HeadlessClassExerciseSupport.SmokeStats stats = HeadlessClassExerciseSupport.exercise(
        "org.alice.ide.ast.export.ConstructorInfo",
        "org.alice.ide.ast.export.DeclarationInfo",
        "org.alice.ide.ast.export.FieldInfo",
        "org.alice.ide.ast.export.MemberInfo",
        "org.alice.ide.ast.export.MethodInfo",
        "org.alice.ide.ast.export.ProjectInfo",
        "org.alice.ide.ast.export.TypeInfo",
        "org.alice.ide.ast.export.type.FieldInfo",
        "org.alice.ide.ast.export.type.FunctionInfo",
        "org.alice.ide.ast.export.type.ResourceInfo",
        "org.alice.ide.ast.export.type.TypeSummary",
        "org.alice.ide.ast.export.type.TypeSummaryDataSource",
        "org.alice.ide.ast.export.type.TypeXmlUtitlities"
    );
    assertTrue("Should load at least 5 classes, loaded=" + stats.getLoadedCount(),
        stats.getLoadedCount() >= 5);
  }

  @Test
  public void exerciseIdeIdentifierAndNameClasses() {
    HeadlessClassExerciseSupport.SmokeStats stats = HeadlessClassExerciseSupport.exercise(
        "org.alice.ide.identifier.IdentifierNameGenerator",
        "org.alice.ide.name.NameValidator",
        "org.alice.ide.name.validators.FieldNameValidator",
        "org.alice.ide.name.validators.LocalNameValidator",
        "org.alice.ide.name.validators.MarkerColorValidator",
        "org.alice.ide.name.validators.MemberNameValidator",
        "org.alice.ide.name.validators.MethodNameValidator",
        "org.alice.ide.name.validators.NodeNameValidator",
        "org.alice.ide.name.validators.ParameterNameValidator",
        "org.alice.ide.name.validators.ResourceNameValidator",
        "org.alice.ide.name.validators.TransientNameValidator"
        // TypeNameValidator excluded: triggers TreeUtilities.<clinit> → modal Dialog
    );
    assertTrue("Should load at least 5 classes, loaded=" + stats.getLoadedCount(),
        stats.getLoadedCount() >= 5);
  }

  @Test
  public void exerciseIdeTypeAndTypeManagerClasses() {
    HeadlessClassExerciseSupport.SmokeStats stats = HeadlessClassExerciseSupport.exercise(
        "org.alice.ide.type.AbstractExtendsTypeKey",
        "org.alice.ide.type.ExtendsTypeKey",
        "org.alice.ide.type.ExtendsTypeWithConstructorParameterTypeKey",
        "org.alice.ide.type.ExtendsTypeWithNamedType",
        "org.alice.ide.type.ExtendsTypeWithSuperArgumentFieldKey",
        "org.alice.ide.type.TypeCache",
        "org.alice.ide.type.TypeKey",
        "org.alice.ide.typemanager.ResourceTypeUtilities",
        "org.alice.ide.typemanager.TypeManager"
    );
    assertTrue("Should load at least 3 classes, loaded=" + stats.getLoadedCount(),
        stats.getLoadedCount() >= 3);
  }

  @Test
  public void exerciseStageideAstClasses() {
    HeadlessClassExerciseSupport.SmokeStats stats = HeadlessClassExerciseSupport.exercise(
        "org.alice.stageide.ast.BootstrapUtilities",
        "org.alice.stageide.ast.ExpressionCreator",
        "org.alice.stageide.ast.JointMethodArrayAccessInfo",
        "org.alice.stageide.ast.JointMethodUtilities",
        "org.alice.stageide.ast.JointedTypeInfo",
        "org.alice.stageide.ast.SceneAdapter",
        "org.alice.stageide.ast.StoryApiSpecificAstUtilities",
        "org.alice.stageide.ast.sort.OneShotSorter",
        "org.alice.stageide.ast.source.AudioSourceImportValueCreator",
        "org.alice.stageide.ast.source.ImageSourceImportValueCreator",
        "org.alice.stageide.ast.source.SourceImportValueCreator"
    );
    assertTrue("Should load at least 3 classes, loaded=" + stats.getLoadedCount(),
        stats.getLoadedCount() >= 3);
  }

  @Test
  public void exerciseStageideModelresourceClasses() {
    // Most modelresource classes trigger StorytellingResources.findAndLoadInstalledAliceResourcesIfNecessary()
    // via AliceResourceUtilities, which shows a modal FindResourcesPanel dialog.
    // Only exercise classes that don't touch the gallery resource loading path.
    HeadlessClassExerciseSupport.SmokeStats stats = HeadlessClassExerciseSupport.exercise(
        "org.alice.stageide.modelresource.ResourceNodeCodec",
        "org.alice.stageide.modelresource.TreeUtilitiesLogic"
    );
    assertTrue("Should load at least 1 class, loaded=" + stats.getLoadedCount(),
        stats.getLoadedCount() >= 1);
  }
}
