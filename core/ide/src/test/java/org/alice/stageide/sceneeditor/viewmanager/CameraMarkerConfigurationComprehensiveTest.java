package org.alice.stageide.sceneeditor.viewmanager;

import edu.cmu.cs.dennisc.animation.Animation;
import edu.cmu.cs.dennisc.animation.AnimationObserver;
import edu.cmu.cs.dennisc.animation.Animator;
import edu.cmu.cs.dennisc.animation.FrameObserver;
import edu.cmu.cs.dennisc.property.InstanceProperty;
import edu.cmu.cs.dennisc.scenegraph.AbstractCamera;
import edu.cmu.cs.dennisc.scenegraph.SimpleAppearance;
import edu.cmu.cs.dennisc.scenegraph.SymmetricPerspectiveCamera;
import edu.cmu.cs.dennisc.scenegraph.Transformable;
import edu.cmu.cs.dennisc.scenegraph.Visual;
import org.alice.math.immutable.AffineMatrix4x4;
import org.alice.math.immutable.AxisAlignedBox;
import org.alice.math.immutable.Dimension3;
import org.alice.stageide.sceneeditor.CameraOption;
import org.junit.Test;
import org.lgna.story.CameraMarker;
import org.lgna.story.implementation.CameraMarkerImp;
import org.lgna.story.implementation.ProgramImp;
import org.lgna.story.implementation.SceneImp;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

public class CameraMarkerConfigurationComprehensiveTest {

  @Test public void classIsAbstract() { assertTrue(Modifier.isAbstract(CameraMarkerConfiguration.class.getModifiers())); }
  @Test public void trackerFieldIsFinal() throws Exception { assertTrue(Modifier.isFinal(CameraMarkerConfiguration.class.getDeclaredField("tracker").getModifiers())); }
  @Test public void cameraOptionFieldIsFinal() throws Exception { assertTrue(Modifier.isFinal(CameraMarkerConfiguration.class.getDeclaredField("cameraOption").getModifiers())); }
  @Test public void markerImpFieldIsFinal() throws Exception { assertTrue(Modifier.isFinal(CameraMarkerConfiguration.class.getDeclaredField("markerImp").getModifiers())); }
  @Test public void pointOfViewAnimationFieldExists() throws Exception { assertEquals("pointOfViewAnimation", CameraMarkerConfiguration.class.getDeclaredField("pointOfViewAnimation").getName()); }

  @Test
  public void constructorSetsCoreFieldsAndCallsInitialize() throws Exception {
    RecordingAnimator animator = new RecordingAnimator();
    CameraMarkerTracker tracker = tracker(animator);
    FakeCameraMarker marker = marker();
    TestConfiguration configuration = new TestConfiguration(tracker, marker);
    assertSame(tracker, configuration.tracker);
    assertEquals(CameraOption.FRONT, configuration.cameraOption);
    assertSame(marker.getImplementation(), configuration.markerImp);
    assertEquals(1, configuration.initializeCount);
  }

  @Test
  public void constructorNamesMarkerUsingCameraOption() throws Exception {
    TestConfiguration configuration = new TestConfiguration(tracker(new RecordingAnimator()), marker());
    assertEquals(MarkerUtilities.getNameForCamera(CameraOption.FRONT), configuration.marker.getName());
  }

  @Test
  public void isActiveReturnsTrueWhenTrackerMatches() throws Exception {
    CameraMarkerTracker tracker = tracker(new RecordingAnimator());
    TestConfiguration configuration = new TestConfiguration(tracker, marker());
    setField(CameraMarkerTracker.class, tracker, "activeMarker", configuration);
    assertTrue(configuration.isActiveForTest());
  }

  @Test
  public void isActiveReturnsFalseWhenTrackerDoesNotMatch() throws Exception {
    assertFalse(new TestConfiguration(tracker(new RecordingAnimator()), marker()).isActiveForTest());
  }

  @Test
  public void updateCameraToNewMarkerLocationInvokesAnimateWithCurrentCamera() throws Exception {
    UpdateRecordingConfiguration configuration = new UpdateRecordingConfiguration(tracker(new RecordingAnimator()), marker());
    configuration.updateCameraToNewMarkerLocation();
    assertTrue(configuration.animateCalled);
    assertSame(configuration.camera, configuration.previousCamera);
  }

  @Test
  public void animateToTargetViewMatchingTransformStartsTrackingImmediately() throws Exception {
    RecordingAnimator animator = new RecordingAnimator();
    TestConfiguration configuration = new TestConfiguration(tracker(animator), marker());
    configuration.targetTransform = AffineMatrix4x4.IDENTITY;
    configuration.animateToTargetView(null);
    assertEquals(1, configuration.switchToCameraCount);
    assertEquals(1, configuration.startTrackingCount);
    assertEquals(0, animator.invokeLaterCount);
  }

  @Test
  public void animateToTargetViewDifferentTransformQueuesAnimation() throws Exception {
    RecordingAnimator animator = new RecordingAnimator();
    TestConfiguration configuration = new TestConfiguration(tracker(animator), marker());
    configuration.targetTransform = AffineMatrix4x4.createTranslation(1, 2, 3);
    configuration.animateToTargetView(null);
    assertEquals(1, configuration.switchToCameraCount);
    assertEquals(0, configuration.startTrackingCount);
    assertEquals(1, animator.invokeLaterCount);
  }

  @Test
  public void queuedAnimationIsStoredOnConfiguration() throws Exception {
    RecordingAnimator animator = new RecordingAnimator();
    TestConfiguration configuration = new TestConfiguration(tracker(animator), marker());
    configuration.targetTransform = AffineMatrix4x4.createTranslation(4, 5, 6);
    configuration.animateToTargetView(null);
    Field field = CameraMarkerConfiguration.class.getDeclaredField("pointOfViewAnimation");
    field.setAccessible(true);
    assertNotNull(field.get(configuration));
  }

  @Test
  public void completingQueuedAnimationStartsTracking() throws Exception {
    RecordingAnimator animator = new RecordingAnimator();
    TestConfiguration configuration = new TestConfiguration(tracker(animator), marker());
    configuration.targetTransform = AffineMatrix4x4.createTranslation(7, 8, 9);
    configuration.animateToTargetView(null);
    animator.lastAnimation.complete(null);
    assertEquals(1, configuration.startTrackingCount);
  }

  @Test
  public void animateToTargetViewSwitchesCameraBeforeAnimation() throws Exception {
    RecordingAnimator animator = new RecordingAnimator();
    TestConfiguration configuration = new TestConfiguration(tracker(animator), marker());
    configuration.targetTransform = AffineMatrix4x4.createTranslation(10, 11, 12);
    configuration.animateToTargetView(null);
    assertEquals(1, configuration.switchToCameraCount);
  }

  @Test
  public void helperMethodsExistWithExpectedVisibility() throws Exception {
    assertTrue(Modifier.isProtected(CameraMarkerConfiguration.class.getDeclaredMethod("initialize").getModifiers()));
    assertTrue(Modifier.isProtected(CameraMarkerConfiguration.class.getDeclaredMethod("centerOn", AxisAlignedBox.class).getModifiers()));
    assertTrue(Modifier.isAbstract(CameraMarkerConfiguration.class.getDeclaredMethod("getTargetTransform").getModifiers()));
  }

  private static CameraMarkerTracker tracker(Animator animator) throws Exception {
    CameraMarkerTracker tracker = alloc(CameraMarkerTracker.class);
    setField(CameraMarkerTracker.class, tracker, "animator", animator);
    return tracker;
  }

  private static FakeCameraMarker marker() throws Exception {
    FakeCameraMarker marker = new FakeCameraMarker();
    FakeCameraMarkerImp imp = alloc(FakeCameraMarkerImp.class);
    Transformable root = new Transformable();
    Transformable composite = new Transformable();
    composite.setParent(root);
    imp.abstraction = marker;
    imp.sgComposite = composite;
    imp.paintAppearances = new SimpleAppearance[]{new SimpleAppearance()};
    imp.opacityAppearances = imp.paintAppearances;
    imp.visuals = new Visual[0];
    imp.scaleProperties = new InstanceProperty[0];
    imp.scale = Dimension3.UNIT_SIZE;
    marker.implementation = imp;
    return marker;
  }

  private static <T> T alloc(Class<T> type) throws Exception { return type.cast(unsafe().allocateInstance(type)); }
  private static void setField(Class<?> type, Object target, String name, Object value) throws Exception { Field field = type.getDeclaredField(name); field.setAccessible(true); field.set(target, value); }
  private static sun.misc.Unsafe unsafe() throws Exception { Field field = sun.misc.Unsafe.class.getDeclaredField("theUnsafe"); field.setAccessible(true); return (sun.misc.Unsafe) field.get(null); }

  private static class TestConfiguration extends CameraMarkerConfiguration<CameraMarkerImp> {
    private final FakeCameraMarker marker; protected AbstractCamera camera; private AffineMatrix4x4 targetTransform = AffineMatrix4x4.IDENTITY;
    private int initializeCount; private int switchToCameraCount; private int startTrackingCount;
    private TestConfiguration(CameraMarkerTracker tracker, FakeCameraMarker marker) { super(tracker, marker, CameraOption.FRONT, "front"); this.marker = marker; this.camera = new SymmetricPerspectiveCamera(); this.camera.setParent(new Transformable()); }
    private boolean isActiveForTest() { return isActive(); }
    @Override protected void initialize() { initializeCount++; }
    @Override protected void centerOn(AxisAlignedBox box) { }
    @Override void resetForScene(SceneImp sceneImp, AffineMatrix4x4 startingView) { }
    @Override protected void stopTrackingCamera() { }
    @Override protected void switchToCamera() { switchToCameraCount++; }
    @Override protected AbstractCamera getCamera() { return camera; }
    @Override protected AffineMatrix4x4 getTargetTransform() { return targetTransform; }
    @Override protected void startTrackingCamera() { startTrackingCount++; }
    @Override void updatePicturePlaneFromCamera() { }
  }

  private static final class UpdateRecordingConfiguration extends TestConfiguration {
    private boolean animateCalled; private AbstractCamera previousCamera;
    private UpdateRecordingConfiguration(CameraMarkerTracker tracker, FakeCameraMarker marker) { super(tracker, marker); }
    @Override void animateToTargetView(AbstractCamera previousCamera) { animateCalled = true; this.previousCamera = previousCamera; }
  }

  private static final class FakeCameraMarker extends CameraMarker {
    private FakeCameraMarkerImp implementation; private String name;
    @Override public FakeCameraMarkerImp getImplementation() { return implementation; }
    @Override public String getName() { return name; }
    @Override public void setName(String name) { this.name = name; }
  }

  private static final class FakeCameraMarkerImp extends CameraMarkerImp {
    private FakeCameraMarker abstraction; private Transformable sgComposite; private SimpleAppearance[] paintAppearances;
    private SimpleAppearance[] opacityAppearances; private Visual[] visuals; private InstanceProperty[] scaleProperties; private Dimension3 scale;
    private FakeCameraMarkerImp() { super(null); }
    @Override public ProgramImp getProgram() { return null; }
    @Override protected void createVisuals() { }
    @Override public FakeCameraMarker getAbstraction() { return abstraction; }
    @Override public Transformable getSgComposite() { return sgComposite; }
    @Override protected SimpleAppearance[] getSgPaintAppearances() { return paintAppearances; }
    @Override protected SimpleAppearance[] getSgOpacityAppearances() { return opacityAppearances; }
    @Override public Visual[] getSgVisuals() { return visuals; }
    @Override protected InstanceProperty[] getScaleProperties() { return scaleProperties; }
    @Override public Dimension3 getScale() { return scale; }
    @Override public void setScale(Dimension3 scale) { this.scale = scale; }
    @Override public void setSize(Dimension3 size) { }
  }

  private static final class RecordingAnimator implements Animator {
    private Animation lastAnimation; private int invokeLaterCount;
    @Override public double getCurrentTime() { return 0; }
    @Override public double getSpeedFactor() { return 1; }
    @Override public void setSpeedFactor(double speedFactor) { }
    @Override public void update() { }
    @Override public void invokeLater(Animation animation, AnimationObserver animationObserver) { invokeLaterCount++; lastAnimation = animation; }
    @Override public void invokeAndWait(Animation animation, AnimationObserver animationObserver) throws InterruptedException, InvocationTargetException { }
    @Override public void invokeAndWait_ThrowRuntimeExceptionsIfNecessary(Animation animation, AnimationObserver animationObserver) { }
    @Override public void addFrameObserver(FrameObserver runnable) { }
    @Override public void removeFrameObserver(FrameObserver runnable) { }
    @Override public void completeAll() { }
    @Override public void cancelAnimation() { }
  }
}
