package org.lgna.story.implementation.eventhandling;

import org.alice.math.immutable.AffineMatrix4x4;
import org.alice.math.immutable.Point3;
import org.alice.math.immutable.Vector4;
import org.junit.Test;
import org.lgna.story.MultipleEventPolicy;
import org.lgna.story.SScene;
import org.lgna.story.SThing;
import org.lgna.story.SThingMarker;
import org.lgna.story.event.AbstractEvent;
import org.lgna.story.event.PointOfViewChangeListener;
import org.lgna.story.event.PointOfViewEvent;
import org.lgna.story.implementation.CameraImp;
import org.lgna.story.implementation.OrthographicCameraImp;
import org.lgna.story.implementation.SceneImp;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import edu.cmu.cs.dennisc.scenegraph.event.AbsoluteTransformationEvent;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class TransformationAndDetectorTest {

  @Test
  public void transformationChangedHandlerMapsAbsoluteTransformationEventsToAbstractions() {
    TrackingTransformationChangedHandler handler = new TrackingTransformationChangedHandler();
    SThingMarker marker = new SThingMarker();

    handler.absoluteTransformationChanged(
        new AbsoluteTransformationEvent(marker.getImplementation().getSgComposite()));

    assertSame(marker, handler.lastChecked);
  }

  @Test
  public void transformationChangedHandlerSkipsChecksWhileSilenced() {
    TrackingTransformationChangedHandler handler = new TrackingTransformationChangedHandler();
    SThingMarker marker = new SThingMarker();

    handler.silenceListeners();
    handler.absoluteTransformationChanged(
        new AbsoluteTransformationEvent(marker.getImplementation().getSgComposite()));
    assertEquals(null, handler.lastChecked);

    handler.restoreListeners();
    handler.absoluteTransformationChanged(
        new AbsoluteTransformationEvent(marker.getImplementation().getSgComposite()));
    assertSame(marker, handler.lastChecked);
  }

  @Test
  public void transformationHandlerDeduplicatesObservedModels() {
    SyncTransformationHandler handler = new SyncTransformationHandler();
    SThingMarker marker = new SThingMarker();

    handler.addTransformationListener(event -> {
    }, new SThing[]{marker});
    handler.addTransformationListener(event -> {
    }, new SThing[]{marker});

    assertEquals(1, handler.getModelList().size());
    assertSame(marker, handler.getModelList().get(0));
  }

  @Test
  public void transformationHandlerDispatchesOnlyForTheChangedEntity() {
    SyncTransformationHandler handler = new SyncTransformationHandler();
    SThingMarker alpha = new SThingMarker();
    SThingMarker beta = new SThingMarker();
    List<SThing> calls = new ArrayList<>();

    PointOfViewChangeListener alphaListener = event -> calls.add(event.getEntity());
    PointOfViewChangeListener betaListener = event -> calls.add(event.getEntity());
    handler.addTransformationListener(alphaListener, new SThing[]{alpha});
    handler.addTransformationListener(betaListener, new SThing[]{beta});

    handler.absoluteTransformationChanged(
        new AbsoluteTransformationEvent(alpha.getImplementation().getSgComposite()));
    assertEquals(List.of(alpha), calls);

    handler.absoluteTransformationChanged(
        new AbsoluteTransformationEvent(beta.getImplementation().getSgComposite()));
    assertEquals(List.of(alpha, beta), calls);
  }

  @Test
  public void isInViewReturnsTrueWhenAnyProjectedPointIsVisibleAndInFront() throws Exception {
    boolean visible = invokeIsInView(
        new Dimension(100, 100),
        new Point[]{new Point(50, 50)},
        new Point3[]{new Point3(0, 0, -1)});

    assertTrue(visible);
  }

  @Test
  public void isInViewReturnsFalseWhenVisiblePointIsBehindTheCamera() throws Exception {
    boolean visible = invokeIsInView(
        new Dimension(100, 100),
        new Point[]{new Point(50, 50)},
        new Point3[]{new Point3(0, 0, 1)});

    assertFalse(visible);
  }

  @Test
  public void isInViewReturnsTrueWhenTheBoundsStraddleBothHorizontalSides() throws Exception {
    boolean visible = invokeIsInView(
        new Dimension(100, 100),
        new Point[]{new Point(-10, 40), new Point(110, 60)},
        new Point3[]{new Point3(0, 0, -1), new Point3(0, 0, -1)});

    assertTrue(visible);
  }

  @Test
  public void projectedBoundsInViewReflectsEntityTranslation() {
    ProjectionSceneImp scene = new ProjectionSceneImp(new TestScene());
    CameraImp<?> camera = new OrthographicCameraImp();
    SThingMarker left = attachMarker(scene, 0, 0, 0);
    SThingMarker right = attachMarker(scene, 5, 0, 0);

    Rectangle leftBounds = ExposedAabbOcclusionDetector.bounds(camera, left);
    Rectangle rightBounds = ExposedAabbOcclusionDetector.bounds(camera, right);

    assertTrue(leftBounds.width >= 0);
    assertTrue(rightBounds.width >= 0);
    assertNotEquals(leftBounds.getCenterX(), rightBounds.getCenterX(), 0.0);
  }

  @Test
  public void doTheseOccludeUsesProjectedRectangleIntersection() {
    ProjectionSceneImp scene = new ProjectionSceneImp(new TestScene());
    CameraImp<?> camera = new OrthographicCameraImp();
    SThingMarker overlappingA = attachMarker(scene, 0, 0, 0);
    SThingMarker overlappingB = attachMarker(scene, 0, 0, 0);
    SThingMarker separate = attachMarker(scene, 10, 0, 0);

    assertTrue(AabbOcclusionDetector.doTheseOcclude(camera, overlappingA, overlappingB));
    assertFalse(AabbOcclusionDetector.doTheseOcclude(camera, overlappingA, separate));
  }

  private static boolean invokeIsInView(
      Dimension surfaceSize,
      Point[] awtPoints,
      Point3[] relativeToCamera) throws Exception {
    Method method = IsInViewDetector.class.getDeclaredMethod(
        "isInView",
        Dimension.class,
        Point[].class,
        Point3[].class);
    method.setAccessible(true);
    return (Boolean) method.invoke(null, surfaceSize, awtPoints, relativeToCamera);
  }

  private static SThingMarker attachMarker(SceneImp scene, double x, double y, double z) {
    SThingMarker marker = new SThingMarker();
    marker.getImplementation().setVehicle(scene);
    marker.getImplementation().setLocalTransformation(
        AffineMatrix4x4.createTranslation(x, y, z));
    return marker;
  }

  private static final class TrackingTransformationChangedHandler
      extends TransformationChangedHandler<Runnable, AbstractEvent> {
    private SThing lastChecked;

    @Override
    protected void check(SThing changedEntity) {
      this.lastChecked = changedEntity;
    }

    @Override
    protected void fire(Runnable listener, AbstractEvent event) {
      listener.run();
    }
  }

  private static final class SyncTransformationHandler extends TransformationHandler {
    @Override
    protected void fireEvent(
        PointOfViewChangeListener listener,
        PointOfViewEvent event) {
      fire(listener, event);
    }

    @Override
    protected void fireEvent(
        PointOfViewChangeListener listener,
        PointOfViewEvent event,
        Object multiEventLock) {
      fire(listener, event);
    }
  }

  private static final class ExposedAabbOcclusionDetector extends AabbOcclusionDetector {
    private static Rectangle bounds(CameraImp<?> camera, SThing object) {
      return projectedBoundsInView(camera, object);
    }
  }

  private static final class ProjectionSceneImp extends SceneImp {
    private ProjectionSceneImp(SScene abstraction) {
      super(abstraction);
    }

    @Override
    public Point transformToAwt(Vector4 xyzw, CameraImp<?> cameraImp) {
      return new Point(
          (int) Math.round(xyzw.x() * 100.0),
          (int) Math.round(xyzw.y() * 100.0));
    }
  }

  private static final class TestScene extends SScene {
    @Override
    public void handleActiveChanged(Boolean isActive, Integer activationCount) {
    }
  }
}
