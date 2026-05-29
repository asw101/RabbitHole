package org.alice.ide.coverage;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

import static org.junit.Assert.assertTrue;

public class CroquetModelsMiscHeadlessSweepTest {

  @Rule
  public Timeout globalTimeout = Timeout.seconds(120);

  @Test
  public void exerciseCroquetCodecsAndEdits() {
    HeadlessClassExerciseSupport.SmokeStats stats = HeadlessClassExerciseSupport.exercise(
        "org.alice.ide.croquet.codecs.LocaleCodec",
        "org.alice.ide.croquet.codecs.NodeCodec",
        "org.alice.ide.croquet.codecs.PropertyOfNodeCodec",
        "org.alice.ide.croquet.codecs.ResourceCodec",
        "org.alice.ide.croquet.codecs.SingletonCodec",
        "org.alice.ide.croquet.codecs.StringCodec",
        "org.alice.ide.croquet.codecs.typeeditor.DeclarationCompositeCodec",
        "org.alice.ide.croquet.edits.DependentEdit"
    );
    assertTrue("Should load at least 3 classes, loaded=" + stats.getLoadedCount(),
        stats.getLoadedCount() >= 3);
  }

  @Test
  public void exerciseCroquetModelsProjecturi() {
    HeadlessClassExerciseSupport.SmokeStats stats = HeadlessClassExerciseSupport.exercise(
        "org.alice.ide.croquet.models.projecturi.BackupProjectOperationLogic",
        "org.alice.ide.croquet.models.projecturi.DirtyProjectNavigationPlan",
        "org.alice.ide.croquet.models.projecturi.EvidenceFileOperations",
        "org.alice.ide.croquet.models.projecturi.EvidenceJsonWriter",
        "org.alice.ide.croquet.models.projecturi.SaveOperationCompletionEvidence",
        "org.alice.ide.croquet.models.projecturi.SaveOperationFlow",
        "org.alice.ide.croquet.models.projecturi.SaveProofJsonDelegate"
    );
    assertTrue("Should load at least 3 classes, loaded=" + stats.getLoadedCount(),
        stats.getLoadedCount() >= 3);
  }

  @Test
  public void exerciseCroquetModelsHtml() {
    HeadlessClassExerciseSupport.SmokeStats stats = HeadlessClassExerciseSupport.exercise(
        "org.alice.ide.croquet.models.html.HtmlEncoder",
        "org.alice.ide.croquet.models.html.HtmlEncoderLogic",
        "org.alice.ide.croquet.models.html.HtmlProjectWriter",
        "org.alice.ide.croquet.models.html.SvgEncoder"
    );
    assertTrue("Should load at least 2 classes, loaded=" + stats.getLoadedCount(),
        stats.getLoadedCount() >= 2);
  }

  @Test
  public void exerciseCroquetModelsUiPreferences() {
    HeadlessClassExerciseSupport.SmokeStats stats = HeadlessClassExerciseSupport.exercise(
        "org.alice.ide.croquet.models.ui.preferences.IsEmphasizingClassesState",
        "org.alice.ide.croquet.models.ui.preferences.IsExposingReassignableStatusState",
        "org.alice.ide.croquet.models.ui.preferences.IsFullTypeHierarchyDesiredState",
        "org.alice.ide.croquet.models.ui.preferences.IsIncludingConstructors",
        "org.alice.ide.croquet.models.ui.preferences.IsIncludingImportAndExportType",
        "org.alice.ide.croquet.models.ui.preferences.IsIncludingManagedUserMethods",
        "org.alice.ide.croquet.models.ui.preferences.IsIncludingPackagePrivateUserMethods",
        "org.alice.ide.croquet.models.ui.preferences.IsIncludingPrivateUserMethods",
        "org.alice.ide.croquet.models.ui.preferences.IsIncludingProgramType",
        "org.alice.ide.croquet.models.ui.preferences.IsIncludingProtectedUserMethods",
        "org.alice.ide.croquet.models.ui.preferences.IsIncludingThisForFieldAccessesState",
        "org.alice.ide.croquet.models.ui.preferences.IsIncludingTypeFeedbackForExpressionsState",
        "org.alice.ide.croquet.models.ui.preferences.IsJavaCodeOnTheSideState",
        "org.alice.ide.croquet.models.ui.preferences.IsNullAllowedForFieldInitializers",
        "org.alice.ide.croquet.models.ui.preferences.IsNullAllowedForLocalInitializers"
    );
    assertTrue("Should load at least 5 classes, loaded=" + stats.getLoadedCount(),
        stats.getLoadedCount() >= 5);
  }

  @Test
  public void exerciseCroquetModelsNumberpad() {
    HeadlessClassExerciseSupport.SmokeStats stats = HeadlessClassExerciseSupport.exercise(
        "org.alice.ide.croquet.models.numberpad.DoubleModel",
        "org.alice.ide.croquet.models.numberpad.FloatModel",
        "org.alice.ide.croquet.models.numberpad.IntegerModel",
        "org.alice.ide.croquet.models.numberpad.NumberModel"
    );
    assertTrue("Should load at least 2 classes, loaded=" + stats.getLoadedCount(),
        stats.getLoadedCount() >= 2);
  }

  @Test
  public void exerciseCroquetModelsMiscellaneous() {
    HeadlessClassExerciseSupport.SmokeStats stats = HeadlessClassExerciseSupport.exercise(
        "org.alice.ide.croquet.models.ExpressionState",
        "org.alice.ide.croquet.models.FilteredListPropertySingleSelectListState",
        "org.alice.ide.croquet.models.IdeDragModel",
        "org.alice.ide.croquet.models.ResponsibleModel",
        "org.alice.ide.croquet.models.StandardExpressionState",
        "org.alice.ide.croquet.models.ui.formatter.FormatterState",
        "org.alice.ide.croquet.models.ui.locale.LocaleState",
        "org.alice.ide.croquet.models.ui.debug.components.TransactionHistoryTreeModel",
        "org.alice.ide.croquet.models.cascade.arithmetic.ArithmeticUtilities",
        "org.alice.ide.croquet.models.cascade.array.ArrayLengthSeparator",
        "org.alice.ide.croquet.models.cascade.blanks.TypeUnsetBlank",
        "org.alice.ide.croquet.models.cascade.number.IntegerBlank",
        "org.alice.ide.croquet.models.declaration.InitializerState",
        "org.alice.ide.croquet.models.declaration.InitializerStateOwner",
        "org.alice.ide.croquet.models.project.find.core.FindContentManager",
        "org.alice.ide.croquet.models.project.find.core.SearchResult",
        "org.alice.ide.croquet.models.project.find.core.astcrawler.FindCrawler",
        "org.alice.ide.croquet.models.project.find.core.criteria.AcceptIfNotGenerated",
        "org.alice.ide.croquet.models.project.find.croquet.tree.FindReferencesTreeState",
        "org.alice.ide.croquet.models.project.find.croquet.tree.FindReferencesTreeStateLogic",
        "org.alice.ide.croquet.models.project.stats.croquet.StatisticsMethodFrequencyTabCompositeHelper",
        "org.alice.ide.croquet.models.help.BugSubmitAttachment",
        "org.alice.ide.croquet.models.help.BugSubmitVisibility"
        // GalleryResourceUtilities and GalleryDragModel excluded: trigger resource loading dialog
    );
    assertTrue("Should load at least 5 classes, loaded=" + stats.getLoadedCount(),
        stats.getLoadedCount() >= 5);
  }
}
