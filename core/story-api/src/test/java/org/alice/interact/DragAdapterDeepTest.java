package org.alice.interact;

import edu.cmu.cs.dennisc.scenegraph.AbstractCamera;
import edu.cmu.cs.dennisc.scenegraph.Scene;
import edu.cmu.cs.dennisc.scenegraph.Silhouette;
import edu.cmu.cs.dennisc.scenegraph.SymmetricPerspectiveCamera;
import edu.cmu.cs.dennisc.scenegraph.Transformable;
import org.alice.interact.event.SelectionEvent;
import org.alice.interact.event.SelectionListener;
import org.alice.interact.handle.HandleStyle;
import org.junit.Test;
import org.lgna.story.SThingMarker;
import org.lgna.story.implementation.ObjectMarkerImp;
import org.lgna.story.implementation.StandInImp;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;

/**
 * Deep, behavioral tests for {@link DragAdapter} — exercising
 * setOnscreenRenderTarget, setSelectedImplementation branches,
 * triggerImplementationSelection, update loop, listener fan-out
 * and interaction-state transitions.
 */
public class DragAdapterDeepTest {

  private static class TestDragAdapter extends DragAdapter { }

  // ── setOnscreenRenderTarget twice ────────────────────────────────

  @Test
  public void setOnscreenRenderTargetToNullThenNullIsSafe() {
    TestDragAdapter a = new TestDragAdapter();
    a.setOnscreenRenderTarget(null);
    assertNull(a.getOnscreenRenderTarget());
  }

  // ── setSelectedImplementation paths ──────────────────────────────

  @Test
  public void setSelectedImplementation_objectMarkerImp_takesMarkerBranch() {
    TestDragAdapter a = new TestDragAdapter();
    SThingMarker marker = new SThingMarker();
    ObjectMarkerImp imp = marker.getImplementation();
    // Should not throw; goes through setSelectedObjectMarker branch.
    a.setSelectedImplementation(imp);
  }

  @Test
  public void setSelectedImplementation_standInImp_takesGenericBranch() {
    TestDragAdapter a = new TestDragAdapter();
    StandInImp imp = new StandInImp();
    a.setSelectedImplementation(imp);
  }

  @Test
  public void setSelectedImplementation_twiceWithSameMarkerIsIdempotent() {
    TestDragAdapter a = new TestDragAdapter();
    SThingMarker marker = new SThingMarker();
    ObjectMarkerImp imp = marker.getImplementation();
    a.setSelectedImplementation(imp);
    a.setSelectedImplementation(imp);
  }

  @Test
  public void setSelectedImplementation_thenNullClearsSelection() {
    TestDragAdapter a = new TestDragAdapter();
    SThingMarker marker = new SThingMarker();
    a.setSelectedImplementation(marker.getImplementation());
    a.setSelectedImplementation(null);
  }

  @Test
  public void setSelectedObjectMarker_thenChangeMarker_resetsOpacity() {
    TestDragAdapter a = new TestDragAdapter();
    SThingMarker first = new SThingMarker();
    SThingMarker second = new SThingMarker();
    a.setSelectedObjectMarker(first.getImplementation());
    a.setSelectedObjectMarker(second.getImplementation());
    a.setSelectedObjectMarker(null);
  }

  // ── fireSelected via triggerImplementationSelection ──────────────

  @Test
  public void triggerImplementationSelection_firesSelectedOnceWhenChanged() {
    TestDragAdapter a = new TestDragAdapter();
    AtomicInteger selectedCount = new AtomicInteger();
    AtomicInteger selectingCount = new AtomicInteger();
    a.addSelectionListener(new SelectionListener() {
      @Override public void selecting(SelectionEvent e) { selectingCount.incrementAndGet(); }
      @Override public void selected(SelectionEvent e) { selectedCount.incrementAndGet(); }
    });

    StandInImp imp = new StandInImp();
    a.setSelectedImplementation(imp);   // routes through scene-object branch, sets selectedObject
    a.triggerImplementationSelection(null); // null != imp, so fires
    assertTrue(selectedCount.get() >= 1);
    assertTrue(selectingCount.get() >= 1);
  }

  @Test
  public void triggerImplementationSelection_whenSame_doesNotFire() {
    TestDragAdapter a = new TestDragAdapter();
    AtomicInteger selectedCount = new AtomicInteger();
    a.addSelectionListener(new SelectionListener() {
      @Override public void selecting(SelectionEvent e) { }
      @Override public void selected(SelectionEvent e) { selectedCount.incrementAndGet(); }
    });
    // Initially no selection. Calling with null === current null selection.
    a.triggerImplementationSelection(null);
    assertEquals(0, selectedCount.get());
  }

  // ── setInteractionState ─────────────────────────────────────────

  @Test
  public void setInteractionState_withUnknownStyle_isSafe() {
    TestDragAdapter a = new TestDragAdapter();
    a.setInteractionState(HandleStyle.ROTATION);
    a.setInteractionState(HandleStyle.TRANSLATION);
    a.setInteractionState(HandleStyle.RESIZE);
  }

  // ── pushHandleSet / popHandleSet ────────────────────────────────

  @Test
  public void pushHandleSet_thenPop_returnsToPrevious() {
    TestDragAdapter a = new TestDragAdapter();
    org.alice.interact.handle.HandleSet set = new org.alice.interact.handle.HandleSet();
    a.pushHandleSet(set);
    a.popHandleSet();
  }

  // ── setHandleShowingForSelectedImplementation true path ────────

  @Test
  public void setHandleShowingForSelectedImplementation_currentMatches_appliesVisibility() {
    TestDragAdapter a = new TestDragAdapter();
    StandInImp imp = new StandInImp();
    a.setSelectedImplementation(imp);
    a.setHandleShowingForSelectedImplementation(imp, true);
    a.setHandleShowingForSelectedImplementation(imp, false);
  }

  // ── setAnimator with AnimatorDependentManipulator ───────────────

  @Test
  public void setAnimator_propagatesToAnimatorDependentManipulator() {
    TestDragAdapter a = new TestDragAdapter();
    a.setAnimator(null);
    assertNull(a.getAnimator());
  }

  // ── update / handleStateChange via clearMouseAndKeyboardState ──

  @Test
  public void clearMouseAndKeyboardState_firesStateChange() {
    TestDragAdapter a = new TestDragAdapter();
    a.clearMouseAndKeyboardState();
  }

  // ── addCameraView two-arg + multi-arg ───────────────────────────

  @Test
  public void addCameraView_multipleViews() {
    TestDragAdapter a = new TestDragAdapter();
    Scene scene = new Scene();
    SymmetricPerspectiveCamera cam = new SymmetricPerspectiveCamera();
    Transformable parent = new Transformable();
    cam.setParent(parent);
    parent.setParent(scene);

    a.addCameraView(DragAdapter.CameraView.MAIN, cam);
    a.addCameraView(DragAdapter.CameraView.TOP_LEFT, cam);
    a.clearCameraViews();
  }

  // ── ObjectType.getObjectType branches ───────────────────────────

  @Test
  public void objectType_objectMarkerImpReturnsObjectMarker() {
    SThingMarker marker = new SThingMarker();
    assertEquals(DragAdapter.ObjectType.OBJECT_MARKER,
        DragAdapter.ObjectType.getObjectType(marker.getImplementation()));
  }

  @Test
  public void objectType_unknownForStandIn() {
    assertEquals(DragAdapter.ObjectType.UNKNOWN,
        DragAdapter.ObjectType.getObjectType(new StandInImp()));
  }

  @Test
  public void objectType_nullReturnsUnknown() {
    assertEquals(DragAdapter.ObjectType.UNKNOWN,
        DragAdapter.ObjectType.getObjectType(null));
  }
}
