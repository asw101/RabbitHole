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

import edu.cmu.cs.dennisc.scenegraph.AbstractTransformable;
import edu.cmu.cs.dennisc.scenegraph.Silhouette;
import edu.cmu.cs.dennisc.scenegraph.Visual;
import org.alice.interact.event.SelectionEvent;
import org.alice.interact.handle.HandleManager;
import org.lgna.story.implementation.AbstractTransformableImp;
import org.lgna.story.implementation.CameraMarkerImp;
import org.lgna.story.implementation.ModelImp;
import org.lgna.story.implementation.ObjectMarkerImp;
import org.lgna.story.implementation.PerspectiveCameraMarkerImp;

/**
 * Package-private delegate owning selection state and selection logic extracted
 * from {@link DragAdapter}. Follows the same delegate pattern as
 * {@link DragEventHandler} and {@link DragCameraController}.
 */
class DragSelectionController {

  private final DragAdapter dragAdapter;
  private AbstractTransformableImp selectedObject;
  private Silhouette sgSilhouette;
  private CameraMarkerImp selectedCameraMarker;
  private ObjectMarkerImp selectedObjectMarker;

  DragSelectionController(DragAdapter dragAdapter) {
    this.dragAdapter = dragAdapter;
  }

  AbstractTransformableImp getSelectedObject() {
    return this.selectedObject;
  }

  CameraMarkerImp getSelectedCameraMarker() {
    return this.selectedCameraMarker;
  }

  ObjectMarkerImp getSelectedObjectMarker() {
    return this.selectedObjectMarker;
  }

  void setSgSilhouette(Silhouette sgSilhouette) {
    this.sgSilhouette = sgSilhouette;
  }

  void setSelectedCameraMarker(CameraMarkerImp selected) {
    if (selected != this.selectedCameraMarker) {
      this.dragAdapter.fireSelecting(new SelectionEvent(this.dragAdapter, selected));
      if (this.selectedCameraMarker != null) {
        this.selectedCameraMarker.opacity.setValue(.3f);
        if (this.selectedCameraMarker instanceof PerspectiveCameraMarkerImp imp) {
          imp.setDetailedViewShowing(false);
        }
      }
      this.selectedCameraMarker = selected;
      if (selected != null) {
        selected.opacity.setValue(1f);
        if (this.dragAdapter.hasSceneEditor() && (selected instanceof PerspectiveCameraMarkerImp imp)) {
          imp.setDetailedViewShowing(true);
        }
      }
    }
  }

  void setSelectedObjectMarker(ObjectMarkerImp selected) {
    if (selected != this.selectedObjectMarker) {
      this.dragAdapter.fireSelecting(new SelectionEvent(this.dragAdapter, selected));
      if (this.selectedObjectMarker != null) {
        this.selectedObjectMarker.opacity.setValue(.3f);
      }
      this.selectedObjectMarker = selected;
      if (selected != null) {
        selected.opacity.setValue(1f);
      }
    }
  }

  private void setSelectedObjectSilhouetteIfAppropriate(boolean isHaloed) {
    if (this.sgSilhouette != null) {
      if (this.selectedObject instanceof ModelImp modelImp) {
        for (Visual sgVisual : modelImp.getSgVisuals()) {
          sgVisual.silouette.setValue(isHaloed ? this.sgSilhouette : null);
        }
      }
    }
  }

  void setSelectedSceneObjectImplementation(AbstractTransformableImp selected) {
    if (this.selectedObject != selected) {
      this.dragAdapter.fireSelecting(new SelectionEvent(this.dragAdapter, selected));
      this.setSelectedObjectSilhouetteIfAppropriate(false);
      AbstractTransformable sgTransformable = selected != null ? selected.getSgComposite() : null;
      HandleManager handleManager = this.dragAdapter.getHandleManager();
      if (HandleManager.isSelectable(sgTransformable)) {
        handleManager.setHandlesShowing(true);
        handleManager.setSelectedObject(sgTransformable);
      } else {
        handleManager.setSelectedObject(null);
      }
      this.dragAdapter.currentInputState.setCurrentlySelectedObject(sgTransformable);
      this.dragAdapter.currentInputState.setTimeCaptured();
      this.selectedObject = selected;
      this.setSelectedObjectSilhouetteIfAppropriate(true);
      this.dragAdapter.fireStateChange();
    }
  }
}
