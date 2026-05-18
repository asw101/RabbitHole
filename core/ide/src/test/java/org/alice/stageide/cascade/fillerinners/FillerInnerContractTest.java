package org.alice.stageide.cascade.fillerinners;

import org.alice.ide.cascade.fillerinners.ExpressionFillerInner;
import org.junit.Test;
import org.lgna.project.ast.AbstractType;
import org.lgna.project.ast.JavaType;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

/**
 * Contract tests for all filler-inner classes in
 * {@code org.alice.stageide.cascade.fillerinners}.
 *
 * Verifies: class loading, ExpressionFillerInner inheritance,
 * default-constructor availability, type binding, and
 * isAssignableTo consistency.
 */
public class FillerInnerContractTest {

  private static final String PKG = "org.alice.stageide.cascade.fillerinners.";

  // Cached to avoid 22 redundant Class[] allocations per test run
  private static final Class<?>[] APPEND_ITEMS_PARAM_TYPES = {
      java.util.List.class,
      org.lgna.project.annotations.ValueDetails.class,
      boolean.class,
      org.lgna.project.ast.Expression.class
  };

  // ---- helper -----------------------------------------------------------

  private Class<?> load(String simpleName) throws ClassNotFoundException {
    return Class.forName(PKG + simpleName);
  }

  private ExpressionFillerInner instantiate(String simpleName) throws Exception {
    Class<?> cls = load(simpleName);
    Constructor<?> ctor = cls.getConstructor();
    return (ExpressionFillerInner) ctor.newInstance();
  }

  private void assertFillerInnerContract(String simpleName, Class<?> expectedStoryType) throws Exception {
    Class<?> cls = load(simpleName);
    assertTrue(simpleName + " must extend ExpressionFillerInner",
        ExpressionFillerInner.class.isAssignableFrom(cls));
    assertFalse(simpleName + " must not be abstract",
        Modifier.isAbstract(cls.getModifiers()));

    Constructor<?> ctor = cls.getConstructor();
    assertTrue("default ctor must be public", Modifier.isPublic(ctor.getModifiers()));

    ExpressionFillerInner instance = (ExpressionFillerInner) ctor.newInstance();
    assertNotNull(instance);

    AbstractType<?, ?, ?> expectedType = JavaType.getInstance(expectedStoryType);
    assertTrue(simpleName + ".isAssignableTo(" + expectedStoryType.getSimpleName() + ")",
        instance.isAssignableTo(expectedType));

    Method appendItems = cls.getMethod("appendItems", APPEND_ITEMS_PARAM_TYPES);
    assertNotNull(appendItems);
  }

  // ---- event listener filler-inners -------------------------------------

  @Test
  public void arrowKeyListenerFillerInner_contract() throws Exception {
    assertFillerInnerContract("ArrowKeyListenerFillerInner",
        org.lgna.story.event.ArrowKeyPressListener.class);
  }

  @Test
  public void keyListenerFillerInner_contract() throws Exception {
    assertFillerInnerContract("KeyListenerFillerInner",
        org.lgna.story.event.KeyPressListener.class);
  }

  @Test
  public void numberKeyListenerFillerInner_contract() throws Exception {
    assertFillerInnerContract("NumberKeyListenerFillerInner",
        org.lgna.story.event.NumberKeyPressListener.class);
  }

  @Test
  public void startCollisionListenerFillerInner_contract() throws Exception {
    assertFillerInnerContract("StartCollisionListenerFillerInner",
        org.lgna.story.event.CollisionStartListener.class);
  }

  @Test
  public void endCollisionListenerFillerInner_contract() throws Exception {
    assertFillerInnerContract("EndCollisionListenerFillerInner",
        org.lgna.story.event.CollisionEndListener.class);
  }

  @Test
  public void enterProximityEventListenerFillerInner_contract() throws Exception {
    assertFillerInnerContract("EnterProximityEventListenerFillerInner",
        org.lgna.story.event.ProximityEnterListener.class);
  }

  @Test
  public void exitProximityEventListenerFillerInner_contract() throws Exception {
    assertFillerInnerContract("ExitProximityEventListenerFillerInner",
        org.lgna.story.event.ProximityExitListener.class);
  }

  @Test
  public void comesIntoViewEventListenerFillerInner_contract() throws Exception {
    assertFillerInnerContract("ComesIntoViewEventListenerFillerInner",
        org.lgna.story.event.ViewEnterListener.class);
  }

  @Test
  public void leavesViewEventListenerFillerInner_contract() throws Exception {
    assertFillerInnerContract("LeavesViewEventListenerFillerInner",
        org.lgna.story.event.ViewExitListener.class);
  }

  @Test
  public void startOcclusionEventListenerFillerInner_contract() throws Exception {
    assertFillerInnerContract("StartOcclusionEventListenerFillerInner",
        org.lgna.story.event.OcclusionStartListener.class);
  }

  @Test
  public void endOcclusionEventListenerFillerInner_contract() throws Exception {
    assertFillerInnerContract("EndOcclusionEventListenerFillerInner",
        org.lgna.story.event.OcclusionEndListener.class);
  }

  @Test
  public void mouseClickOnObjectFillerInner_contract() throws Exception {
    assertFillerInnerContract("MouseClickOnObjectFillerInner",
        org.lgna.story.event.MouseClickOnObjectListener.class);
  }

  @Test
  public void mouseClickedOnScreenFillerInner_contract() throws Exception {
    assertFillerInnerContract("MouseClickedOnScreenFillerInner",
        org.lgna.story.event.MouseClickOnScreenListener.class);
  }

  @Test
  public void timerEventListenerFillerInner_contract() throws Exception {
    assertFillerInnerContract("TimerEventListenerFillerInner",
        org.lgna.story.event.TimeListener.class);
  }

  @Test
  public void sceneActivationEventFillerInner_contract() throws Exception {
    assertFillerInnerContract("SceneActivationEventFillerInner",
        org.lgna.story.event.SceneActivationListener.class);
  }

  @Test
  public void transformationListenerFillerInner_contract() throws Exception {
    assertFillerInnerContract("TransformationListenerFillerInner",
        org.lgna.story.event.PointOfViewChangeListener.class);
  }

  // ---- expression value filler-inners -----------------------------------

  @Test
  public void colorFillerInner_contract() throws Exception {
    assertFillerInnerContract("ColorFillerInner",
        org.lgna.story.Color.class);
  }

  @Test
  public void keyFillerInner_contract() throws Exception {
    assertFillerInnerContract("KeyFillerInner",
        org.lgna.story.Key.class);
  }

  @Test
  public void imagePaintFillerInner_contract() throws Exception {
    assertFillerInnerContract("ImagePaintFillerInner",
        org.lgna.story.ImagePaint.class);
  }

  @Test
  public void modelResourceFillerInner_contract() throws Exception {
    assertFillerInnerContract("ModelResourceFillerInner",
        org.lgna.story.resources.JointedModelResource.class);
  }

  // ---- source filler-inners (abstract base, concrete children) ----------

  @Test
  public void audioSourceFillerInner_contract() throws Exception {
    assertFillerInnerContract("AudioSourceFillerInner",
        org.lgna.story.AudioSource.class);
  }

  @Test
  public void imageSourceFillerInner_contract() throws Exception {
    assertFillerInnerContract("ImageSourceFillerInner",
        org.lgna.story.ImageSource.class);
  }

  // ---- cross-type assignability checks ----------------------------------

  @Test
  public void colorFillerInner_notAssignableToKey() throws Exception {
    ExpressionFillerInner fi = instantiate("ColorFillerInner");
    AbstractType<?, ?, ?> keyType = JavaType.getInstance(org.lgna.story.Key.class);
    assertFalse(fi.isAssignableTo(keyType));
  }

  @Test
  public void keyFillerInner_notAssignableToColor() throws Exception {
    ExpressionFillerInner fi = instantiate("KeyFillerInner");
    AbstractType<?, ?, ?> colorType = JavaType.getInstance(org.lgna.story.Color.class);
    assertFalse(fi.isAssignableTo(colorType));
  }

  @Test
  public void arrowKeyListenerFillerInner_notAssignableToTimer() throws Exception {
    ExpressionFillerInner fi = instantiate("ArrowKeyListenerFillerInner");
    AbstractType<?, ?, ?> timerType = JavaType.getInstance(org.lgna.story.event.TimeListener.class);
    assertFalse(fi.isAssignableTo(timerType));
  }

  @Test
  public void mouseClickOnObjectFillerInner_notAssignableToCollision() throws Exception {
    ExpressionFillerInner fi = instantiate("MouseClickOnObjectFillerInner");
    AbstractType<?, ?, ?> collisionType = JavaType.getInstance(org.lgna.story.event.CollisionStartListener.class);
    assertFalse(fi.isAssignableTo(collisionType));
  }

  @Test
  public void timerEventFillerInner_notAssignableToSceneActivation() throws Exception {
    ExpressionFillerInner fi = instantiate("TimerEventListenerFillerInner");
    AbstractType<?, ?, ?> sceneType = JavaType.getInstance(org.lgna.story.event.SceneActivationListener.class);
    assertFalse(fi.isAssignableTo(sceneType));
  }

  // ---- SourceFillerInner hierarchy check --------------------------------

  @Test
  public void sourceFillerInner_isAbstract() throws Exception {
    Class<?> cls = Class.forName(PKG + "SourceFillerInner");
    assertTrue(Modifier.isAbstract(cls.getModifiers()));
    assertTrue(ExpressionFillerInner.class.isAssignableFrom(cls));
  }

  @Test
  public void audioSourceFillerInner_extendsSourceFillerInner() throws Exception {
    Class<?> audio = load("AudioSourceFillerInner");
    Class<?> source = load("SourceFillerInner");
    assertTrue(source.isAssignableFrom(audio));
  }

  @Test
  public void imageSourceFillerInner_extendsSourceFillerInner() throws Exception {
    Class<?> image = load("ImageSourceFillerInner");
    Class<?> source = load("SourceFillerInner");
    assertTrue(source.isAssignableFrom(image));
  }

  // ---- all concrete filler-inners are public ----------------------------

  @Test
  public void allConcreteFillerInners_arePublic() throws ClassNotFoundException {
    // SourceFillerInner excluded — it's abstract
    String[] concreteNames = {
        "ArrowKeyListenerFillerInner", "AudioSourceFillerInner",
        "ColorFillerInner", "ComesIntoViewEventListenerFillerInner",
        "EndCollisionListenerFillerInner", "EndOcclusionEventListenerFillerInner",
        "EnterProximityEventListenerFillerInner", "ExitProximityEventListenerFillerInner",
        "ImagePaintFillerInner", "ImageSourceFillerInner",
        "KeyFillerInner", "KeyListenerFillerInner",
        "LeavesViewEventListenerFillerInner", "ModelResourceFillerInner",
        "MouseClickOnObjectFillerInner", "MouseClickedOnScreenFillerInner",
        "NumberKeyListenerFillerInner", "SceneActivationEventFillerInner",
        "StartCollisionListenerFillerInner", "StartOcclusionEventListenerFillerInner",
        "TimerEventListenerFillerInner", "TransformationListenerFillerInner"
    };
    for (String name : concreteNames) {
      Class<?> cls = load(name);
      assertTrue(name + " must be public", Modifier.isPublic(cls.getModifiers()));
    }
  }
}
