package org.alice.interact.manipulator;

import edu.cmu.cs.dennisc.scenegraph.Transformable;
import org.alice.interact.InputState;
import org.alice.interact.event.ManipulationEvent;
import org.alice.interact.handle.HandleSet;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Construction and inherited-method tests for all concrete manipulator subclasses
 * that have no-arg constructors. Each manipulator is instantiated headlessly and
 * exercised through the AbstractManipulator API surface.
 */
public class ManipulatorConstructionTest {

  // ═══════════════════════════════════════════════════════════════════════
  //  Helper: exercise the full AbstractManipulator API on any instance
  // ═══════════════════════════════════════════════════════════════════════

  private void exerciseManipulatorApi(AbstractManipulator m, String label) {
    // Initial state
    assertNotNull(label + " should not be null", m);
    assertFalse(label + " hasStarted", m.hasStarted());
    assertFalse(label + " hasUpdated", m.hasUpdated());
    assertNull(label + " mainManipulationEvent", m.getMainManipulationEvent());
    assertFalse(label + " doesManipulatedObjectHaveHandles", m.doesManipulatedObjectHaveHandles());
    assertFalse(label + " isUndoable", m.isUndoable());

    // toString
    String str = m.toString();
    assertNotNull(label + " toString", str);

    // Manipulation events
    Iterable<ManipulationEvent> events = m.getManipulationEvents();
    assertNotNull(label + " getManipulationEvents", events);

    ManipulationEvent evt = new ManipulationEvent(
        ManipulationEvent.EventType.Translate, null, null);
    m.addManipulationEvent(evt);
    m.removeManipulationEvent(evt);
    m.clearManipulationEvents();

    // setHasUpdated round-trip
    m.setHasUpdated(true);
    assertTrue(label + " hasUpdated after set true", m.hasUpdated());
    m.setHasUpdated(false);
    assertFalse(label + " hasUpdated after set false", m.hasUpdated());

    // setDragAdapter(null)
    m.setDragAdapter(null);

    // dataUpdate, timeUpdate when not started — safe no-ops
    InputState current = new InputState();
    InputState prev = new InputState();
    m.dataUpdateManipulator(current, prev);
    m.timeUpdateManipulator(0.016, current);

    // clickManipulator when not started — safe no-op
    m.clickManipulator(current, prev);

    // undoRedoBeginManipulation (safe without adapter)
    m.undoRedoBeginManipulation();
  }

  // ═══════════════════════════════════════════════════════════════════════
  //  ObjectRotateDragManipulator
  // ═══════════════════════════════════════════════════════════════════════

  @Test
  public void objectRotateDragManipulatorConstruction() {
    ObjectRotateDragManipulator m = new ObjectRotateDragManipulator();
    exerciseManipulatorApi(m, "ObjectRotateDragManipulator");
  }

  @Test
  public void objectRotateDragManipulatorCameraAccessors() {
    ObjectRotateDragManipulator m = new ObjectRotateDragManipulator();
    assertNull(m.getCamera());
    m.setCamera(null);
    assertNull(m.getCamera());
    assertNotNull(m.getDesiredCameraView());
    m.setDesiredCameraView(null);
    assertNotNull(m.getDesiredCameraView());
    assertNull(m.getOnscreenRenderTarget());
    m.setOnscreenRenderTarget(null);
    assertNull(m.getOnscreenRenderTarget());
  }

  @Test
  public void objectRotateDragManipulatorUndoRedoDescription() {
    ObjectRotateDragManipulator m = new ObjectRotateDragManipulator();
    assertNotNull(m.getUndoRedoDescription());
  }

  @Test
  public void objectRotateDragManipulatorStartReturnsFalseWithNoTarget() {
    ObjectRotateDragManipulator m = new ObjectRotateDragManipulator();
    InputState input = new InputState();
    boolean started = m.startManipulator(input);
    assertFalse("Should not start without a target", started);
  }

  // ═══════════════════════════════════════════════════════════════════════
  //  MouseRelativeObjectDragManipulator
  // ═══════════════════════════════════════════════════════════════════════

  @Test
  public void mouseRelativeObjectDragManipulatorConstruction() {
    MouseRelativeObjectDragManipulator m = new MouseRelativeObjectDragManipulator();
    exerciseManipulatorApi(m, "MouseRelativeObjectDragManipulator");
  }

  @Test
  public void mouseRelativeObjectDragManipulatorCameraAccessors() {
    MouseRelativeObjectDragManipulator m = new MouseRelativeObjectDragManipulator();
    assertNull(m.getCamera());
    m.setCamera(null);
    assertNull(m.getCamera());
    assertNotNull(m.getDesiredCameraView());
    m.setDesiredCameraView(null);
    assertNotNull(m.getDesiredCameraView());
    assertNull(m.getOnscreenRenderTarget());
    m.setOnscreenRenderTarget(null);
    assertNull(m.getOnscreenRenderTarget());
  }

  @Test
  public void mouseRelativeObjectDragManipulatorUndoRedoDescription() {
    MouseRelativeObjectDragManipulator m = new MouseRelativeObjectDragManipulator();
    assertNotNull(m.getUndoRedoDescription());
  }

  @Test
  public void mouseRelativeObjectDragManipulatorStartReturnsFalseWithNoTarget() {
    MouseRelativeObjectDragManipulator m = new MouseRelativeObjectDragManipulator();
    InputState input = new InputState();
    boolean started = m.startManipulator(input);
    assertFalse(started);
  }

  // ═══════════════════════════════════════════════════════════════════════
  //  OmniDirectionalDragManipulator
  // ═══════════════════════════════════════════════════════════════════════

  @Test
  public void omniDirectionalDragManipulatorConstruction() {
    OmniDirectionalDragManipulator m = new OmniDirectionalDragManipulator();
    exerciseManipulatorApi(m, "OmniDirectionalDragManipulator");
  }

  @Test
  public void omniDirectionalDragManipulatorStartReturnsFalse() {
    OmniDirectionalDragManipulator m = new OmniDirectionalDragManipulator();
    boolean started = m.startManipulator(new InputState());
    assertFalse(started);
  }

  // ═══════════════════════════════════════════════════════════════════════
  //  ObjectTranslateDragManipulator
  // ═══════════════════════════════════════════════════════════════════════

  @Test
  public void objectTranslateDragManipulatorConstruction() {
    ObjectTranslateDragManipulator m = new ObjectTranslateDragManipulator();
    exerciseManipulatorApi(m, "ObjectTranslateDragManipulator");
  }

  @Test
  public void objectTranslateDragManipulatorCameraAccessors() {
    ObjectTranslateDragManipulator m = new ObjectTranslateDragManipulator();
    assertNull(m.getCamera());
    m.setCamera(null);
  }

  @Test
  public void objectTranslateDragManipulatorStartReturnsFalse() {
    ObjectTranslateDragManipulator m = new ObjectTranslateDragManipulator();
    boolean started = m.startManipulator(new InputState());
    assertFalse(started);
  }

  // ═══════════════════════════════════════════════════════════════════════
  //  LinearDragManipulator
  // ═══════════════════════════════════════════════════════════════════════

  @Test
  public void linearDragManipulatorConstruction() {
    LinearDragManipulator m = new LinearDragManipulator();
    exerciseManipulatorApi(m, "LinearDragManipulator");
  }

  @Test
  public void linearDragManipulatorCameraAccessors() {
    LinearDragManipulator m = new LinearDragManipulator();
    assertNull(m.getCamera());
    m.setCamera(null);
  }

  @Test
  public void linearDragManipulatorStartReturnsFalse() {
    LinearDragManipulator m = new LinearDragManipulator();
    boolean started = m.startManipulator(new InputState());
    assertFalse(started);
  }

  // ═══════════════════════════════════════════════════════════════════════
  //  ObjectGlobalHandleDragManipulator
  // ═══════════════════════════════════════════════════════════════════════

  @Test
  public void objectGlobalHandleDragManipulatorConstruction() {
    ObjectGlobalHandleDragManipulator m = new ObjectGlobalHandleDragManipulator();
    exerciseManipulatorApi(m, "ObjectGlobalHandleDragManipulator");
  }

  @Test
  public void objectGlobalHandleDragManipulatorCameraAccessors() {
    ObjectGlobalHandleDragManipulator m = new ObjectGlobalHandleDragManipulator();
    assertNull(m.getCamera());
    m.setCamera(null);
    assertNotNull(m.getDesiredCameraView());
    m.setDesiredCameraView(null);
  }

  @Test
  public void objectGlobalHandleDragManipulatorStartReturnsFalse() {
    ObjectGlobalHandleDragManipulator m = new ObjectGlobalHandleDragManipulator();
    boolean started = m.startManipulator(new InputState());
    assertFalse(started);
  }

  // ═══════════════════════════════════════════════════════════════════════
  //  CameraOrbitDragManipulator
  // ═══════════════════════════════════════════════════════════════════════

  @Test
  public void cameraOrbitDragManipulatorConstruction() {
    CameraOrbitDragManipulator m = new CameraOrbitDragManipulator();
    exerciseManipulatorApi(m, "CameraOrbitDragManipulator");
  }

  @Test
  public void cameraOrbitDragManipulatorCameraAccessors() {
    CameraOrbitDragManipulator m = new CameraOrbitDragManipulator();
    assertNull(m.getCamera());
    m.setCamera(null);
    assertNotNull(m.getDesiredCameraView());
    m.setDesiredCameraView(null);
  }

  @Test
  public void cameraOrbitDragManipulatorUndoRedoDescription() {
    CameraOrbitDragManipulator m = new CameraOrbitDragManipulator();
    assertNotNull(m.getUndoRedoDescription());
  }

  // ═══════════════════════════════════════════════════════════════════════
  //  CameraMoveDragManipulator
  // ═══════════════════════════════════════════════════════════════════════

  @Test
  public void cameraMoveDragManipulatorConstruction() {
    CameraMoveDragManipulator m = new CameraMoveDragManipulator();
    exerciseManipulatorApi(m, "CameraMoveDragManipulator");
  }

  @Test
  public void cameraMoveDragManipulatorCameraAccessors() {
    CameraMoveDragManipulator m = new CameraMoveDragManipulator();
    assertNull(m.getCamera());
    m.setCamera(null);
    assertNotNull(m.getDesiredCameraView());
    m.setDesiredCameraView(null);
    assertNull(m.getOnscreenRenderTarget());
    m.setOnscreenRenderTarget(null);
  }

  @Test
  public void cameraMoveDragManipulatorUndoRedoDescription() {
    CameraMoveDragManipulator m = new CameraMoveDragManipulator();
    assertNotNull(m.getUndoRedoDescription());
  }

  // ═══════════════════════════════════════════════════════════════════════
  //  CameraTiltDragManipulator
  // ═══════════════════════════════════════════════════════════════════════

  @Test
  public void cameraTiltDragManipulatorConstruction() {
    CameraTiltDragManipulator m = new CameraTiltDragManipulator();
    exerciseManipulatorApi(m, "CameraTiltDragManipulator");
  }

  @Test
  public void cameraTiltDragManipulatorCameraAccessors() {
    CameraTiltDragManipulator m = new CameraTiltDragManipulator();
    assertNull(m.getCamera());
    m.setCamera(null);
    assertNotNull(m.getDesiredCameraView());
    m.setDesiredCameraView(null);
    assertNull(m.getOnscreenRenderTarget());
    m.setOnscreenRenderTarget(null);
  }

  @Test
  public void cameraTiltDragManipulatorUndoRedoDescription() {
    CameraTiltDragManipulator m = new CameraTiltDragManipulator();
    assertNotNull(m.getUndoRedoDescription());
  }

  // ═══════════════════════════════════════════════════════════════════════
  //  AbstractManipulator start/data/time/end lifecycle via InputState
  // ═══════════════════════════════════════════════════════════════════════

  @Test
  public void startManipulatorWithoutTransformableReturnsFalse() {
    ObjectRotateDragManipulator m = new ObjectRotateDragManipulator();
    InputState input = new InputState();
    boolean started = m.startManipulator(input);
    assertFalse("startManipulator should return false without target", started);
  }

  @Test
  public void fullLifecycleOnMouseRelativeManipulator() {
    MouseRelativeObjectDragManipulator m = new MouseRelativeObjectDragManipulator();
    InputState current = new InputState();
    InputState prev = new InputState();
    // startManipulator returns false without camera — exercises the path
    boolean started = m.startManipulator(current);
    assertFalse(started);
    // data/time update safe when not started
    m.dataUpdateManipulator(current, prev);
    m.timeUpdateManipulator(0.016, current);
    m.clickManipulator(current, prev);
  }

  @Test
  public void fullLifecycleOnObjectTranslateManipulator() {
    ObjectTranslateDragManipulator m = new ObjectTranslateDragManipulator();
    InputState current = new InputState();
    InputState prev = new InputState();
    boolean started = m.startManipulator(current);
    assertFalse(started);
    m.dataUpdateManipulator(current, prev);
    m.timeUpdateManipulator(0.016, current);
  }

  @Test
  public void fullLifecycleOnLinearDragManipulator() {
    LinearDragManipulator m = new LinearDragManipulator();
    InputState current = new InputState();
    InputState prev = new InputState();
    boolean started = m.startManipulator(current);
    assertFalse(started);
    m.dataUpdateManipulator(current, prev);
    m.timeUpdateManipulator(0.016, current);
  }

  @Test
  public void fullLifecycleOnObjectGlobalHandleDragManipulator() {
    ObjectGlobalHandleDragManipulator m = new ObjectGlobalHandleDragManipulator();
    InputState current = new InputState();
    InputState prev = new InputState();
    boolean started = m.startManipulator(current);
    assertFalse(started);
    m.dataUpdateManipulator(current, prev);
    m.timeUpdateManipulator(0.016, current);
  }

  @Test
  public void fullLifecycleOnOmniDirectionalDragManipulator() {
    OmniDirectionalDragManipulator m = new OmniDirectionalDragManipulator();
    InputState current = new InputState();
    InputState prev = new InputState();
    boolean started = m.startManipulator(current);
    assertFalse(started);
    m.dataUpdateManipulator(current, prev);
    m.timeUpdateManipulator(0.016, current);
  }

  // ═══════════════════════════════════════════════════════════════════════
  //  InputState independent tests (covers InputState construction)
  // ═══════════════════════════════════════════════════════════════════════

  @Test
  public void inputStateDefaultConstruction() {
    InputState s = new InputState();
    assertNotNull(s);
    assertNull(s.getInputEvent());
    assertFalse(s.getIsDragEvent());
  }

  @Test
  public void inputStateCopyConstruction() {
    InputState original = new InputState();
    original.setIsDragEvent(true);
    InputState copy = new InputState(original);
    assertTrue(copy.getIsDragEvent());
  }

  @Test
  public void inputStateSetAndGetDragEvent() {
    InputState s = new InputState();
    s.setIsDragEvent(true);
    assertTrue(s.getIsDragEvent());
    s.setIsDragEvent(false);
    assertFalse(s.getIsDragEvent());
  }

  @Test
  public void inputStateSetInputEvent() {
    InputState s = new InputState();
    s.setInputEvent(null);
    assertNull(s.getInputEvent());
  }

  @Test
  public void inputStateGetRolloverHandle() {
    InputState s = new InputState();
    assertNull(s.getRolloverHandle());
  }

  @Test
  public void inputStateGetPickCamera() {
    InputState s = new InputState();
    assertNull(s.getPickCamera());
  }
}
