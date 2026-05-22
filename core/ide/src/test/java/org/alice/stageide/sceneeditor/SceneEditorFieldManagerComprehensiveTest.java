package org.alice.stageide.sceneeditor;

import edu.cmu.cs.dennisc.scenegraph.AbstractCamera;
import org.alice.ide.ReasonToDisableSomeAmountOfRendering;
import org.alice.ide.instancefactory.InstanceFactory;
import org.alice.interact.InputState;
import org.alice.interact.event.SelectionEvent;
import org.alice.math.immutable.AffineMatrix4x4;
import org.junit.Test;
import org.lgna.project.ast.Expression;
import org.lgna.project.ast.Statement;
import org.lgna.project.ast.StatementListProperty;
import org.lgna.project.ast.UserField;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;

import static org.junit.Assert.*;

public class SceneEditorFieldManagerComprehensiveTest {

  private static Method method(String name, Class<?>... parameterTypes) throws Exception {
    Method method = SceneEditorFieldManager.class.getDeclaredMethod(name, parameterTypes);
    method.setAccessible(true);
    return method;
  }

  @Test
  public void classIsTopLevelAndPackagePrivate() {
    assertNull(SceneEditorFieldManager.class.getEnclosingClass());
    int modifiers = SceneEditorFieldManager.class.getModifiers();
    assertFalse(Modifier.isPublic(modifiers));
    assertFalse(Modifier.isPrivate(modifiers));
    assertFalse(Modifier.isProtected(modifiers));
  }

  @Test
  public void classIsNotFinal() {
    assertFalse(Modifier.isFinal(SceneEditorFieldManager.class.getModifiers()));
  }

  @Test
  public void constructorAcceptsStorytellingSceneEditor() throws Exception {
    Constructor<SceneEditorFieldManager> constructor = SceneEditorFieldManager.class.getDeclaredConstructor(StorytellingSceneEditor.class);
    assertEquals(1, constructor.getParameterTypes().length);
  }

  @Test
  public void editorFieldExistsAndIsFinal() throws Exception {
    Field field = SceneEditorFieldManager.class.getDeclaredField("editor");
    assertEquals(StorytellingSceneEditor.class, field.getType());
    assertTrue(Modifier.isFinal(field.getModifiers()));
  }

  @Test
  public void codeGeneratorFieldExistsAndIsFinal() throws Exception {
    Field field = SceneEditorFieldManager.class.getDeclaredField("codeGenerator");
    assertEquals(SceneFieldCodeGenerator.class, field.getType());
    assertTrue(Modifier.isFinal(field.getModifiers()));
  }

  @Test
  public void classDeclaresAtLeastCoreFields() {
    assertTrue(SceneEditorFieldManager.class.getDeclaredFields().length >= 2);
  }

  @Test
  public void selectionMethodsHaveExpectedSignatures() throws Exception {
    assertEquals(void.class, method("setSelectedFieldOnManipulator", UserField.class).getReturnType());
    assertEquals(void.class, method("setSelectedExpressionOnManipulator", Expression.class).getReturnType());
    assertEquals(void.class, method("setSelectedInstance", InstanceFactory.class).getReturnType());
    assertEquals(void.class, method("handleManipulatorSelection", SelectionEvent.class).getReturnType());
  }

  @Test
  public void cameraSwitchingMethodsHaveExpectedSignatures() throws Exception {
    assertEquals(void.class, method("switchToCamera", AbstractCamera.class).getReturnType());
    assertEquals(void.class, method("switchToOrthographicCamera").getReturnType());
    assertEquals(void.class, method("switchToPerspectiveCamera", AbstractCamera.class).getReturnType());
  }

  @Test
  public void markerSelectionMethodsHaveExpectedSignatures() throws Exception {
    assertEquals(void.class, method("handleCameraMarkerFieldSelection", UserField.class).getReturnType());
    assertEquals(void.class, method("handleObjectMarkerFieldSelection", UserField.class).getReturnType());
    assertEquals(void.class, method("setSelectedObjectMarker", UserField.class).getReturnType());
    assertEquals(void.class, method("setSelectedCameraMarker", UserField.class).getReturnType());
  }

  @Test
  public void rightClickAndLifecycleMethodsHaveExpectedSignatures() throws Exception {
    assertEquals(void.class, method("showRightClickMenuForModel", InputState.class).getReturnType());
    assertEquals(void.class, method("showLookingGlassPanel").getReturnType());
    assertEquals(void.class, method("hideLookingGlassPanel").getReturnType());
    assertEquals(void.class, method("handleShowing").getReturnType());
    assertEquals(void.class, method("handleHiding").getReturnType());
  }

  @Test
  public void renderingMethodsHaveExpectedSignatures() throws Exception {
    assertEquals(void.class, method("enableRendering", ReasonToDisableSomeAmountOfRendering.class).getReturnType());
    assertEquals(void.class, method("disableRendering", ReasonToDisableSomeAmountOfRendering.class).getReturnType());
    assertEquals(void.class, method("preScreenCapture").getReturnType());
    assertEquals(void.class, method("postScreenCapture").getReturnType());
  }

  @Test
  public void handleVisibilityMethodHasExpectedSignature() throws Exception {
    assertEquals(void.class, method("setHandleVisibilityForObject", org.lgna.story.implementation.TransformableImp.class, boolean.class).getReturnType());
  }

  @Test
  public void cameraAndMarkerAccessorsHaveExpectedReturnTypes() throws Exception {
    assertEquals(AffineMatrix4x4.class, method("getTransformForNewCameraMarker").getReturnType());
    assertEquals(AffineMatrix4x4.class, method("getTransformForNewObjectMarker").getReturnType());
    assertEquals(org.lgna.story.Color.class, method("getColorForNewObjectMarker").getReturnType());
    assertEquals(org.lgna.story.Color.class, method("getColorForNewCameraMarker").getReturnType());
    assertEquals(AffineMatrix4x4.class, method("getGoodPointOfViewInSceneForObject", org.alice.math.immutable.AxisAlignedBox.class).getReturnType());
  }

  @Test
  public void markerLookupAndThumbnailMethodsHaveExpectedReturnTypes() throws Exception {
    assertEquals(org.lgna.story.implementation.MarkerImp.class, method("getMarkerForField", UserField.class).getReturnType());
    assertEquals(AbstractCamera.class, method("getSgCameraForCreatingThumbnails").getReturnType());
  }

  @Test
  public void codeGenerationDelegationMethodsHaveExpectedReturnTypes() throws Exception {
    assertEquals(Statement.class, method("getCurrentStateCodeForField", UserField.class).getReturnType());
    assertEquals(void.class, method("generateCodeForSetUp", StatementListProperty.class).getReturnType());
    assertEquals(Statement[].class, method("getDoStatementsForCopyField", UserField.class, UserField.class, AffineMatrix4x4.class).getReturnType());
    assertEquals(Statement[].class, method("getDoStatementsForAddField", UserField.class, AffineMatrix4x4.class).getReturnType());
    assertEquals(Statement[].class, method("getUndoStatementsForAddField", UserField.class).getReturnType());
  }

  @Test
  public void removeFieldDelegationMethodsHaveExpectedReturnTypes() throws Exception {
    assertEquals(Map.class, method("getRiders", UserField.class).getReturnType());
    assertEquals(Statement[].class, method("getDoStatementsForRemoveField", UserField.class, Map.class).getReturnType());
    assertEquals(Statement[].class, method("getUndoStatementsForRemoveField", UserField.class, Map.class).getReturnType());
  }

  @Test
  public void classDeclaresAtLeastThirtyMethods() {
    assertTrue(SceneEditorFieldManager.class.getDeclaredMethods().length >= 30);
  }

  @Test
  public void allDeclaredMethodsArePackagePrivateOrPrivate() {
    for (Method method : SceneEditorFieldManager.class.getDeclaredMethods()) {
      int modifiers = method.getModifiers();
      assertFalse(Modifier.isPublic(modifiers));
      assertFalse(Modifier.isProtected(modifiers));
    }
  }
}
