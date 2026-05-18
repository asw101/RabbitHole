package org.alice.stageide.sceneeditor;

import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

public class SceneEditorHelperExtendedTest {

  private static Class<?> load(String name) throws Exception {
    return Class.forName(name, false, SceneEditorHelperExtendedTest.class.getClassLoader());
  }

  private static void assertPackagePrivate(Class<?> cls) {
    assertFalse(Modifier.isPublic(cls.getModifiers()));
    assertFalse(Modifier.isProtected(cls.getModifiers()));
    assertFalse(Modifier.isPrivate(cls.getModifiers()));
  }

  private static void assertSingletonNoArg(Class<?> cls) throws Exception {
    Method getInstance = cls.getMethod("getInstance");
    Object first = getInstance.invoke(null);
    Object second = getInstance.invoke(null);
    assertTrue(Modifier.isPublic(getInstance.getModifiers()));
    assertTrue(Modifier.isStatic(getInstance.getModifiers()));
    assertSame(first, second);
  }

  @Test
  public void sceneEditorFieldManager_hasRequestedFieldsAndMethods() throws Exception {
    Class<?> cls = SceneEditorFieldManager.class;
    Field editor = cls.getDeclaredField("editor");
    Field codeGenerator = cls.getDeclaredField("codeGenerator");
    Method setSelectedFieldOnManipulator = cls.getDeclaredMethod("setSelectedFieldOnManipulator", org.lgna.project.ast.UserField.class);
    Method setSelectedExpressionOnManipulator = cls.getDeclaredMethod("setSelectedExpressionOnManipulator", org.lgna.project.ast.Expression.class);

    assertPackagePrivate(cls);
    assertTrue(Modifier.isFinal(editor.getModifiers()));
    assertTrue(Modifier.isFinal(codeGenerator.getModifiers()));
    assertPackagePrivate(setSelectedFieldOnManipulator.getDeclaringClass());
    assertPackagePrivate(setSelectedExpressionOnManipulator.getDeclaringClass());
  }

  @Test
  public void sceneEditorLifecycleManager_isPackagePrivate_andHasEditorField() throws Exception {
    Class<?> cls = SceneEditorLifecycleManager.class;
    Field editor = cls.getDeclaredField("editor");

    assertPackagePrivate(cls);
    assertTrue(Modifier.isPrivate(editor.getModifiers()));
    assertTrue(Modifier.isFinal(editor.getModifiers()));
  }

  @Test
  public void sceneFieldCodeGenerator_isPackagePrivate() throws Exception {
    Class<?> cls = SceneFieldCodeGenerator.class;
    assertPackagePrivate(cls);
    assertTrue(Modifier.isFinal(cls.getDeclaredField("editor").getModifiers()));
  }

  @Test
  public void sceneEditorInitializer_isPackagePrivate() throws Exception {
    Class<?> cls = SceneEditorInitializer.class;
    assertPackagePrivate(cls);
    assertTrue(Modifier.isPrivate(cls.getDeclaredField("editor").getModifiers()));
  }

  @Test
  public void sceneEditorListeners_isPackagePrivate() {
    assertPackagePrivate(SceneEditorListeners.class);
  }

  @Test
  public void sceneEditorDropReceptor_isPackagePrivate() throws Exception {
    Class<?> cls = SceneEditorDropReceptor.class;
    assertPackagePrivate(cls);
    assertTrue(Modifier.isPrivate(cls.getDeclaredField("editor").getModifiers()));
  }

  @Test
  public void sceneRenderTargetListener_isPackagePrivate() throws Exception {
    Class<?> cls = SceneRenderTargetListener.class;
    assertPackagePrivate(cls);
    Field editor = cls.getDeclaredField("editor");
    assertTrue(Modifier.isFinal(editor.getModifiers()));
  }

  @Test
  public void setUpMethodGenerator_loads() {
    assertTrue(Modifier.isPublic(SetUpMethodGenerator.class.getModifiers()));
  }

  @Test
  public void storytellingSceneEditor_isPublic_andExposesRequestedMethods() throws Exception {
    assertTrue(Modifier.isPublic(StorytellingSceneEditor.class.getModifiers()));
    assertNotNull(StorytellingSceneEditor.class.getMethod("getInstance"));
    assertNotNull(StorytellingSceneEditor.class.getMethod("isStartingCameraView"));
    assertNotNull(StorytellingSceneEditor.class.getMethod("isVrActive"));

    boolean foundPrivateConstructor = false;
    for (Constructor<?> constructor : StorytellingSceneEditor.class.getDeclaredConstructors()) {
      if (Modifier.isPrivate(constructor.getModifiers())) {
        foundPrivateConstructor = true;
      }
    }
    assertTrue(foundPrivateConstructor);
  }

  @Test
  public void lookingGlassPanel_loadsAndExtendsCompassPointSpringPanel() throws Exception {
    Class<?> cls = LookingGlassPanel.class;
    assertPackagePrivate(cls);
    assertTrue(load("org.lgna.croquet.views.CompassPointSpringPanel").isAssignableFrom(cls));
  }

  @Test
  public void relatedHelperClasses_load() throws Exception {
    assertTrue(Modifier.isPublic(load("org.alice.stageide.type.croquet.SceneFieldsState").getModifiers()));
    assertTrue(Modifier.isPublic(load("org.alice.stageide.sceneeditor.draganddrop.SceneDropSite").getModifiers()));
    assertTrue(Modifier.isPublic(load("org.alice.stageide.perspectives.scenesetup.SceneLayoutComposite").getModifiers()));
    assertTrue(Modifier.isPublic(load("org.alice.stageide.type.croquet.data.SceneFieldListData").getModifiers()));
  }

  @Test
  public void handleSetupDelegate_loads() throws Exception {
    Class<?> cls = load("org.alice.stageide.sceneeditor.interact.HandleSetupDelegate");
    assertPackagePrivate(cls);
    Method setupHandles = cls.getDeclaredMethod("setupHandles", org.alice.interact.DragAdapter.class);
    assertTrue(Modifier.isStatic(setupHandles.getModifiers()));
  }

  @Test
  public void cameraMarkerConfigurationHierarchy_loads() throws Exception {
    Class<?> base = load("org.alice.stageide.sceneeditor.viewmanager.CameraMarkerConfiguration");
    Class<?> perspective = load("org.alice.stageide.sceneeditor.viewmanager.PerspectiveCameraMarkerConfiguration");
    Class<?> orthographic = load("org.alice.stageide.sceneeditor.viewmanager.OrthographicCameraMarkerConfiguration");
    Class<?> starting = load("org.alice.stageide.sceneeditor.viewmanager.StartingCameraMarkerConfiguration");
    Class<?> top = load("org.alice.stageide.sceneeditor.viewmanager.TopCameraMarkerConfiguration");
    Class<?> front = load("org.alice.stageide.sceneeditor.viewmanager.FrontCameraMarkerConfiguration");
    Class<?> side = load("org.alice.stageide.sceneeditor.viewmanager.SideCameraMarkerConfiguration");
    Class<?> layout = load("org.alice.stageide.sceneeditor.viewmanager.LayoutCameraMarkerConfiguration");

    assertTrue(Modifier.isAbstract(base.getModifiers()));
    assertTrue(Modifier.isAbstract(perspective.getModifiers()));
    assertTrue(Modifier.isAbstract(orthographic.getModifiers()));
    assertTrue(base.isAssignableFrom(perspective));
    assertTrue(base.isAssignableFrom(orthographic));
    assertSame(perspective, starting.getSuperclass());
    assertSame(orthographic, top.getSuperclass());
    assertSame(orthographic, front.getSuperclass());
    assertSame(orthographic, side.getSuperclass());
    assertSame(perspective, layout.getSuperclass());
  }

  @Test
  public void markerActionOperations_arePublicSingletons() throws Exception {
    Class<?>[] actionTypes = {
        load("org.alice.stageide.sceneeditor.viewmanager.MoveActiveCameraToMarkerActionOperation"),
        load("org.alice.stageide.sceneeditor.viewmanager.MoveMarkerToActiveCameraActionOperation"),
        load("org.alice.stageide.sceneeditor.viewmanager.MoveSelectedObjectToMarkerActionOperation"),
        load("org.alice.stageide.sceneeditor.viewmanager.MoveMarkerToSelectedObjectActionOperation")
    };

    for (Class<?> actionType : actionTypes) {
      assertTrue(Modifier.isPublic(actionType.getModifiers()));
      assertSingletonNoArg(actionType);
    }
  }

  @Test
  public void viewmanagerEditClasses_loadAndExtendAbstractEdit() throws Exception {
    Class<?> moveAndOrientToEdit = load("org.alice.stageide.sceneeditor.viewmanager.edits.MoveAndOrientToEdit");
    Class<?> moveTransformableEdit = load("org.alice.stageide.sceneeditor.viewmanager.edits.MoveTransformableEdit");
    Class<?> abstractEdit = load("org.lgna.croquet.edits.AbstractEdit");

    assertTrue(Modifier.isPublic(moveAndOrientToEdit.getModifiers()));
    assertTrue(Modifier.isPublic(moveTransformableEdit.getModifiers()));
    assertTrue(abstractEdit.isAssignableFrom(moveAndOrientToEdit));
    assertTrue(abstractEdit.isAssignableFrom(moveTransformableEdit));
  }

  @Test
  public void moveAndOrientToEdit_hasPublicConstructors() throws Exception {
    Class<?> cls = load("org.alice.stageide.sceneeditor.viewmanager.edits.MoveAndOrientToEdit");
    Constructor<?> liveConstructor = cls.getConstructor(org.lgna.croquet.history.UserActivity.class, org.lgna.story.SMovableTurnable.class, org.lgna.story.SThing.class);
    Constructor<?> decodeConstructor = cls.getConstructor(edu.cmu.cs.dennisc.codec.BinaryDecoder.class, Object.class);

    assertTrue(Modifier.isPublic(liveConstructor.getModifiers()));
    assertTrue(Modifier.isPublic(decodeConstructor.getModifiers()));
  }

  @Test
  public void moveTransformableEdit_hasPublicConstructor() throws Exception {
    Class<?> cls = load("org.alice.stageide.sceneeditor.viewmanager.edits.MoveTransformableEdit");
    Constructor<?> constructor = cls.getConstructor(
        org.lgna.croquet.history.UserActivity.class,
        org.lgna.story.implementation.TransformableImp.class,
        org.alice.math.immutable.AffineMatrix4x4.class);
    assertTrue(Modifier.isPublic(constructor.getModifiers()));
  }
}
