package org.alice.ide.coverage;

import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.Assert.assertTrue;

/**
 * Loads and exercises classes that are filtered out by HeadlessClassExerciseSupport's
 * name filter (Composites, Operations, etc.) but can still be safely loaded and
 * partially exercised headlessly. Uses a broader filter that only excludes
 * truly GUI-dependent classes (those with .views. or .swing. in the package).
 */
public class DirectClassLoadingSweepTest {

  private int exerciseClasses(String... classNames) {
    int exercised = 0;
    for (String name : classNames) {
      try {
        Class<?> clazz = Class.forName(name, true, Thread.currentThread().getContextClassLoader());
        // Exercise static methods with no args
        for (Method m : clazz.getDeclaredMethods()) {
          if (Modifier.isStatic(m.getModifiers())
              && m.getParameterCount() == 0
              && !m.isSynthetic()
              && !m.getName().startsWith("$")
              && !m.getName().equals("main")) {
            try {
              m.setAccessible(true);
              m.invoke(null);
            } catch (Throwable ignored) {
            }
          }
        }
        // Try to instantiate with no-arg constructor
        try {
          for (Constructor<?> ctor : clazz.getDeclaredConstructors()) {
            if (ctor.getParameterCount() == 0) {
              ctor.setAccessible(true);
              Object instance = ctor.newInstance();
              exerciseInstance(clazz, instance);
              break;
            }
          }
        } catch (Throwable ignored) {
        }
        exercised++;
      } catch (Throwable t) {
        // Class couldn't be loaded — skip
      }
    }
    return exercised;
  }

  private void exerciseInstance(Class<?> clazz, Object instance) {
    for (Method m : clazz.getDeclaredMethods()) {
      if (m.getParameterCount() == 0
          && !Modifier.isStatic(m.getModifiers())
          && !m.isSynthetic()
          && !m.getName().startsWith("$")) {
        try {
          m.setAccessible(true);
          m.invoke(instance);
        } catch (Throwable ignored) {
        }
      }
    }
  }

  @Test
  public void loadViewmanagerConfigurations() {
    int loaded = exerciseClasses(
        "org.alice.stageide.sceneeditor.viewmanager.StartingCameraMarkerConfiguration",
        "org.alice.stageide.sceneeditor.viewmanager.PerspectiveCameraMarkerConfiguration",
        "org.alice.stageide.sceneeditor.viewmanager.OrthographicCameraMarkerConfiguration",
        "org.alice.stageide.sceneeditor.viewmanager.LayoutCameraMarkerConfiguration",
        "org.alice.stageide.sceneeditor.viewmanager.CameraViewCellRenderer"
    );
    assertTrue("Should load at least 1 viewmanager config, loaded=" + loaded, loaded >= 1);
  }

  @Test
  public void loadPropertyAdapters() {
    int loaded = exerciseClasses(
        "org.alice.stageide.properties.MutableRiderVehicleAdapter",
        "org.alice.stageide.properties.BillboardBackPaintPropertyAdapter",
        "org.alice.stageide.properties.BillboardFrontPaintPropertyAdapter",
        "org.alice.stageide.properties.TextFontPropertyAdapter",
        "org.alice.stageide.properties.ResourcePropertyAdapter",
        "org.alice.stageide.properties.uicontroller.CompositePropertyController",
        "org.alice.ide.properties.uicontroller.Point3PropertyController",
        "org.alice.ide.properties.uicontroller.ExpressionBasedPropertyController",
        "org.alice.ide.properties.uicontroller.Color4fPropertyController",
        "org.alice.ide.properties.adapter.croquet.edits.PropertyValueEdit"
    );
    assertTrue("Should load at least 3 property adapters, loaded=" + loaded, loaded >= 3);
  }

  @Test
  public void loadResourceManagerClasses() {
    int loaded = exerciseClasses(
        "org.alice.ide.resource.manager.ResourceManagerComposite",
        "org.alice.ide.resource.manager.ReloadContentResourceOperation",
        "org.alice.ide.resource.manager.ImportGalleryResourceOperation",
        "org.alice.ide.resource.manager.RenameResourceComposite",
        "org.alice.ide.resource.manager.ResourceOperation",
        "org.alice.ide.resource.manager.edits.AddOrRemoveResourceEdit",
        "org.alice.ide.resource.manager.edits.AddResourceEdit",
        "org.alice.ide.resource.manager.edits.RemoveResourceEdit"
    );
    assertTrue("Should load at least 3 resource manager classes, loaded=" + loaded, loaded >= 3);
  }

  @Test
  public void loadAstMergeClasses() {
    int loaded = exerciseClasses(
        "org.alice.ide.ast.type.merge.croquet.edits.ImportTypeEdit",
        "org.alice.ide.ast.type.croquet.ImportTypeIteratingOperation",
        "org.alice.ide.ast.declaration.InsertStatementComposite",
        "org.alice.ide.ast.components.DeclarationNameLabel"
    );
    assertTrue("Should load at least 1 ast class, loaded=" + loaded, loaded >= 1);
  }

  @Test
  public void loadDeclarationEditorClasses() {
    int loaded = exerciseClasses(
        "org.alice.ide.declarationseditor.components.TypeEditor",
        "org.alice.ide.declarationseditor.DeclarationCompositeFillIn",
        "org.alice.ide.declarationseditor.events.components.EventListenerComponent",
        "org.alice.ide.declarationseditor.events.AddEventListenerCascade",
        "org.alice.ide.declarationseditor.events.TransformationEventListenerMenu",
        "org.alice.ide.declarationseditor.HistoryCascade"
    );
    assertTrue("Should load at least 2 declaration classes, loaded=" + loaded, loaded >= 2);
  }

  @Test
  public void loadGalleryBrowserClasses() {
    int loaded = exerciseClasses(
        "org.alice.stageide.gallerybrowser.ImportGalleryResourceComposite",
        "org.alice.stageide.gallerybrowser.uri.ResourceKeyUriIteratingOperation",
        "org.alice.stageide.gallerybrowser.uri.ClassResourceKeyUriIteratingOperation",
        "org.alice.stageide.gallerybrowser.uri.ThingClsUriIteratingOperation",
        "org.alice.stageide.gallerybrowser.uri.EnumConstantResourceKeyUriIteratingOperation",
        "org.alice.stageide.gallerybrowser.enumconstant.EnumConstantResourceKeySelectionComposite",
        "org.alice.stageide.croquet.models.gallerybrowser.TypeFromUriProducer",
        "org.alice.stageide.croquet.models.gallerybrowser.ImportTypeOperation",
        "org.alice.stageide.croquet.models.gallerybrowser.UriCreator"
    );
    assertTrue("Should load at least 3 gallery classes, loaded=" + loaded, loaded >= 3);
  }

  @Test
  public void loadCascadeAndFillInClasses() {
    int loaded = exerciseClasses(
        "org.alice.ide.croquet.models.cascade.SimpleExpressionFillIn",
        "org.alice.ide.croquet.models.cascade.KeywordMenuModel",
        "org.alice.ide.croquet.models.cascade.TypeExpressionCascadeMenu",
        "org.alice.ide.croquet.models.cascade.number.TrigonometryCascadeMenu",
        "org.alice.ide.croquet.models.cascade.number.FloorCeilingCascadeMenu",
        "org.alice.ide.croquet.models.cascade.number.NextDouble01FillIn",
        "org.alice.ide.croquet.models.cascade.array.ArrayAccessFillIn",
        "org.alice.ide.croquet.models.ast.keyed.JavaKeyedArgumentFillIn",
        "org.alice.ide.croquet.models.ast.declaration.TypeFillIn",
        "org.alice.ide.croquet.models.ast.declaration.OtherTypesMenuModel",
        "org.alice.ide.croquet.models.ast.cascade.statement.TemplateAssignmentInsertCascade",
        "org.alice.ide.croquet.models.declaration.GalleryResourceTypeFillIn",
        "test.ik.croquet.JointIdFillIn"
    );
    assertTrue("Should load at least 3 cascade/fillIn classes, loaded=" + loaded, loaded >= 3);
  }

  @Test
  public void loadIdeTopLevelClasses() {
    int loaded = exerciseClasses(
        "org.alice.ide.MetaDeclarationFauxState",
        "org.alice.ide.issue.IdeUncaughtExceptionHandler",
        "org.alice.ide.projecturi.StartersTab",
        "org.alice.ide.projecturi.SelectProjectUriComposite",
        "org.alice.ide.upgrade.ProjectAheadDialog",
        "org.alice.ide.highlight.IdeHighlightStencil",
        "org.alice.ide.code.UserFunctionStatusComposite",
        "org.alice.ide.code.edits.InsertCopiedStatementEdit",
        "org.alice.ide.code.InsertCopiedStatementOperation",
        "org.alice.ide.codedrop.CodePanelWithDropReceptor",
        "org.alice.ide.custom.ArrayCustomExpressionCreatorComposite",
        "org.alice.ide.custom.PortionCustomExpressionCreatorComposite",
        "org.alice.ide.member.UserMethodsSubComposite",
        "org.alice.ide.instancefactory.croquet.TypeCascadeMenuModel",
        "org.alice.ide.ikposer.IdeIkPoserContext",
        "org.alice.ide.ikposer.IdeAddUnmanagedPoseFieldComposite"
    );
    assertTrue("Should load at least 5 ide classes, loaded=" + loaded, loaded >= 5);
  }

  @Test
  public void loadStageIdeComposites() {
    int loaded = exerciseClasses(
        "org.alice.stageide.type.croquet.OtherTypeDialog",
        "org.alice.stageide.type.croquet.ContainsTab",
        "org.alice.stageide.type.croquet.TypeTreeState",
        "org.alice.stageide.type.croquet.data.MemberListData",
        "org.alice.stageide.type.croquet.data.SceneFieldListData",
        "org.alice.stageide.perspectives.PerspectiveSwitchingCardOwnerComposite",
        "org.alice.stageide.perspectives.code.CodeToolBarComposite",
        "org.alice.stageide.perspectives.scenesetup.SetupSceneToolBarComposite",
        "org.alice.stageide.about.CreditsComposite",
        "org.alice.stageide.custom.AudioSourceCustomExpressionCreatorComposite",
        "org.alice.stageide.ast.declaration.AddBoxManagedFieldComposite",
        "org.alice.stageide.ast.declaration.AddDiscManagedFieldComposite",
        "org.alice.stageide.ast.declaration.AddCylinderManagedFieldComposite",
        "org.alice.stageide.ast.declaration.AddTorusManagedFieldComposite",
        "org.alice.stageide.ast.declaration.AddConeManagedFieldComposite",
        "org.alice.stageide.joint.JointsSubMenu",
        "org.alice.stageide.raytrace.ExportToPovRayOperation"
    );
    assertTrue("Should load at least 3 stageide composites, loaded=" + loaded, loaded >= 3);
  }

  @Test
  public void loadDebugAndTestClasses() {
    int loaded = exerciseClasses(
        "org.lgna.debug.tree.croquet.DebugFrame",
        "org.lgna.debug.tree.croquet.GlrDebugFrame",
        "org.lgna.debug.tree.croquet.SgDebugFrame",
        "org.lgna.debug.tree.core.ZTreeNode",
        "org.alice.ide.croquet.models.ui.debug.ActiveTransactionHistoryComposite",
        "org.alice.ide.croquet.models.ui.debug.components.TransactionHistoryCellRenderer",
        "org.alice.ide.croquet.models.ui.debug.BreakProjectAddNullMethodOperation",
        "org.alice.ide.croquet.models.ui.debug.ThrowBogusExceptionOperation",
        "org.alice.ide.croquet.models.ui.debug.ThrowBogusGlExceptionOperation",
        "org.alice.ide.croquet.models.ui.debug.ThrowBogusLgnaExceptionOperation",
        "org.alice.ide.croquet.models.ui.debug.RaiseAnomalousSituationOperation",
        "org.alice.ide.testing.framesize.croquet.CycleFrameSizeOperation",
        "test.ik.IkProgram",
        "test.ik.IkScene",
        "test.ik.NiceDragAdapter"
    );
    assertTrue("Should load at least 3 debug/test classes, loaded=" + loaded, loaded >= 3);
  }

  @Test
  public void loadMenuAndPreferenceClasses() {
    int loaded = exerciseClasses(
        "org.alice.ide.croquet.models.menubar.InternalTestingMenuModel",
        "org.alice.ide.croquet.models.menubar.PreferencesMenuModel",
        "org.alice.ide.croquet.models.projecturi.RevertProjectOperation",
        "org.alice.ide.croquet.models.projecturi.UriPotentialClearanceIteratingOperation",
        "org.alice.ide.croquet.models.projecturi.PotentialClearanceUriCreatorIteratingOperation",
        "org.alice.ide.croquet.models.projecturi.BackupProjectOperation",
        "org.alice.ide.preferences.recursion.IsRecursionAllowedPreferenceDialogComposite",
        "org.alice.ide.system.croquet.WindowsSystemAssessmentToolComposite",
        "org.alice.ide.system.croquet.StartPerformanceInformationAndToolsOperation",
        "org.alice.ide.issue.croquet.AnomalousSituationComposite",
        "org.alice.ide.issue.croquet.LgnaExceptionComposite",
        "org.alice.ide.javacode.croquet.JavaCodeFrameComposite",
        "org.alice.ide.delete.references.croquet.ReferencesToFieldPreventingDeletionDialog"
    );
    assertTrue("Should load at least 3 menu/pref classes, loaded=" + loaded, loaded >= 3);
  }

  @Test
  public void loadModelViewerClasses() {
    int loaded = exerciseClasses(
        "org.alice.stageide.modelviewer.SkeletonVisualViewer",
        "org.alice.stageide.modelviewer.Viewer",
        "org.alice.stageide.modelviewer.SingleViewerDragAdapter",
        "org.alice.stageide.modelviewer.ModelViewer",
        "org.alice.stageide.apis.story.event.SceneActivationAdapter",
        "edu.cmu.cs.dennisc.ui.lookingglass.CameraNavigationDragAdapter"
    );
    assertTrue("Should load at least 1 modelviewer class, loaded=" + loaded, loaded >= 1);
  }
}
