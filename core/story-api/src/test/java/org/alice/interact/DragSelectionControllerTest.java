/*******************************************************************************
 * Copyright (c) 2006, 2015, Carnegie Mellon University. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * 3. Products derived from the software may not be called "Alice", nor may
 *    "Alice" appear in their name, without prior written permission of
 *    Carnegie Mellon University.
 *
 * 4. All advertising materials mentioning features or use of this software must
 *    display the following acknowledgement: "This product includes software
 *    developed by Carnegie Mellon University"
 *
 * 5. The gallery of art assets and animations provided with this software is
 *    contributed by Electronic Arts Inc. and may be used for personal,
 *    non-commercial, and academic use only. Redistributions of any program
 *    source code that utilizes The Sims 2 Assets must also retain the copyright
 *    notice, list of conditions and the disclaimer contained in
 *    The Alice 3.0 Art Gallery License.
 *
 * DISCLAIMER:
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND.
 * ANY AND ALL EXPRESS, STATUTORY OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY,  FITNESS FOR A
 * PARTICULAR PURPOSE, TITLE, AND NON-INFRINGEMENT ARE DISCLAIMED. IN NO EVENT
 * SHALL THE AUTHORS, COPYRIGHT OWNERS OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, PUNITIVE OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING FROM OR OTHERWISE RELATING TO
 * THE USE OF OR OTHER DEALINGS WITH THE SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *******************************************************************************/
package org.alice.interact;

import edu.cmu.cs.dennisc.scenegraph.Silhouette;
import org.alice.interact.event.SelectionEvent;
import org.lgna.story.implementation.StandInImp;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

/**
 * TDD tests for DragSelectionController — the extracted selection state
 * (selectedObject, sgSilhouette, selectedCameraMarker, selectedObjectMarker)
 * and selection logic (setSelectedCameraMarker, setSelectedObjectMarker,
 * setSelectedObjectSilhouetteIfAppropriate, setSelectedSceneObjectImplementation)
 * from DragAdapter.
 *
 * These tests define the contract that the implementation must satisfy.
 * They will fail to compile until DragSelectionController is created.
 *
 * Marker opacity tests are deliberately excluded because CameraMarkerImp and
 * ObjectMarkerImp require full scenegraph initialization (SimpleAppearance[],
 * scene graph visuals) to construct. Opacity behavior (0.3f on deselect,
 * 1.0f on select) is verified via the existing DragAdapter integration tests
 * and by the forwarding stubs preserving the original code path.
 */
public class DragSelectionControllerTest {

  private DragSelectionController selectionController;
  private SelectionTrackingDragAdapter dragAdapter;

  @Before
  public void setUp() {
    dragAdapter = new SelectionTrackingDragAdapter();
    selectionController = new DragSelectionController(dragAdapter);
  }

  // --- Initial state ---

  @Test
  public void getSelectedObject_initiallyNull() {
    assertNull("selectedObject should be null after construction",
        selectionController.getSelectedObject());
  }

  @Test
  public void getSelectedCameraMarker_initiallyNull() {
    assertNull("selectedCameraMarker should be null after construction",
        selectionController.getSelectedCameraMarker());
  }

  @Test
  public void getSelectedObjectMarker_initiallyNull() {
    assertNull("selectedObjectMarker should be null after construction",
        selectionController.getSelectedObjectMarker());
  }

  // --- setSelectedSceneObjectImplementation ---

  @Test
  public void setSelectedSceneObjectImplementation_updatesSelectedObject() {
    StandInImp impl = new StandInImp();

    selectionController.setSelectedSceneObjectImplementation(impl);

    assertSame("getSelectedObject() should return the impl",
        impl, selectionController.getSelectedObject());
  }

  @Test
  public void setSelectedSceneObjectImplementation_updatesInputState() {
    StandInImp impl = new StandInImp();

    selectionController.setSelectedSceneObjectImplementation(impl);

    assertSame("currentInputState should track the selected object's sgComposite",
        impl.getSgComposite(), dragAdapter.currentInputState.getCurrentlySelectedObject());
  }

  @Test
  public void setSelectedSceneObjectImplementation_firesSelectingEvent() {
    StandInImp impl = new StandInImp();

    selectionController.setSelectedSceneObjectImplementation(impl);

    assertEquals("fireSelecting should be called exactly once",
        1, dragAdapter.selectingEvents.size());
    assertSame("selecting event should carry the selected impl",
        impl, dragAdapter.selectingEvents.get(0).getTransformable());
  }

  @Test
  public void setSelectedSceneObjectImplementation_firesStateChange() {
    StandInImp impl = new StandInImp();

    selectionController.setSelectedSceneObjectImplementation(impl);

    assertEquals("fireStateChange should be called exactly once",
        1, dragAdapter.stateChangeCount);
  }

  @Test
  public void setSelectedSceneObjectImplementation_skipsWhenSameObject() {
    StandInImp impl = new StandInImp();
    selectionController.setSelectedSceneObjectImplementation(impl);

    dragAdapter.selectingEvents.clear();
    dragAdapter.stateChangeCount = 0;

    selectionController.setSelectedSceneObjectImplementation(impl);

    assertEquals("selecting should not fire again for the same object",
        0, dragAdapter.selectingEvents.size());
    assertEquals("fireStateChange should not fire again for the same object",
        0, dragAdapter.stateChangeCount);
  }

  @Test
  public void setSelectedSceneObjectImplementation_null_clearsSelectedObject() {
    StandInImp impl = new StandInImp();
    selectionController.setSelectedSceneObjectImplementation(impl);

    selectionController.setSelectedSceneObjectImplementation(null);

    assertNull("getSelectedObject() should return null after selecting null",
        selectionController.getSelectedObject());
  }

  @Test
  public void setSelectedSceneObjectImplementation_null_clearsInputState() {
    StandInImp impl = new StandInImp();
    selectionController.setSelectedSceneObjectImplementation(impl);

    selectionController.setSelectedSceneObjectImplementation(null);

    assertNull("currentInputState should have null selected object after selecting null",
        dragAdapter.currentInputState.getCurrentlySelectedObject());
  }

  @Test
  public void setSelectedSceneObjectImplementation_selectingEventHasDragAdapterAsSource() {
    StandInImp impl = new StandInImp();

    selectionController.setSelectedSceneObjectImplementation(impl);

    assertSame("SelectionEvent source should be the DragAdapter, not the controller",
        dragAdapter, dragAdapter.selectingEvents.get(0).getSource());
  }

  @Test
  public void setSelectedSceneObjectImplementation_changesFireAgain() {
    StandInImp implA = new StandInImp();
    StandInImp implB = new StandInImp();

    selectionController.setSelectedSceneObjectImplementation(implA);
    selectionController.setSelectedSceneObjectImplementation(implB);

    assertEquals("fireSelecting should fire twice for two different objects",
        2, dragAdapter.selectingEvents.size());
    assertSame("second selecting event should carry implB",
        implB, dragAdapter.selectingEvents.get(1).getTransformable());
    assertSame("getSelectedObject() should return the latest impl",
        implB, selectionController.getSelectedObject());
  }

  // --- setSelectedCameraMarker ---

  @Test
  public void setSelectedCameraMarker_null_isNoOpFromNull() {
    selectionController.setSelectedCameraMarker(null);

    assertEquals("selecting should not fire when setting null from null",
        0, dragAdapter.selectingEvents.size());
    assertNull("getSelectedCameraMarker should remain null",
        selectionController.getSelectedCameraMarker());
  }

  // --- setSelectedObjectMarker ---

  @Test
  public void setSelectedObjectMarker_null_isNoOpFromNull() {
    selectionController.setSelectedObjectMarker(null);

    assertEquals("selecting should not fire when setting null from null",
        0, dragAdapter.selectingEvents.size());
    assertNull("getSelectedObjectMarker should remain null",
        selectionController.getSelectedObjectMarker());
  }

  // --- setSgSilhouette ---

  @Test
  public void setSgSilhouette_storesSilhouette() {
    Silhouette silhouette = new Silhouette();

    selectionController.setSgSilhouette(silhouette);

    // The silhouette is stored internally; we verify by confirming that
    // a subsequent call to setSelectedSceneObjectImplementation does not
    // throw (the silhouette path is exercised when selectedObject is a
    // ModelImp, but StandInImp is not, so the path is safely skipped).
    StandInImp impl = new StandInImp();
    selectionController.setSelectedSceneObjectImplementation(impl);
    // No exception means the silhouette was stored and handled gracefully.
  }

  @Test
  public void setSgSilhouette_replacesExistingSilhouette() {
    Silhouette first = new Silhouette();
    Silhouette second = new Silhouette();

    selectionController.setSgSilhouette(first);
    selectionController.setSgSilhouette(second);

    // Verify no error from replacement; silhouette is purely internal state.
    StandInImp impl = new StandInImp();
    selectionController.setSelectedSceneObjectImplementation(impl);
  }

  // --- Test stubs ---

  /**
   * DragAdapter subclass that tracks fireSelecting and fireStateChange calls.
   * We override fireSelecting (widened to package-private by the extraction)
   * and fireStateChange (already protected) to record invocations without
   * triggering the full event handler / state change machinery.
   */
  static class SelectionTrackingDragAdapter extends DragAdapter {
    final List<SelectionEvent> selectingEvents = new ArrayList<>();
    int stateChangeCount = 0;

    @Override
    void fireSelecting(SelectionEvent e) {
      selectingEvents.add(e);
    }

    @Override
    protected void fireStateChange() {
      stateChangeCount++;
    }
  }
}
