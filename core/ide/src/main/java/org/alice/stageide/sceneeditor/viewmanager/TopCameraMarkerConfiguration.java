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

package org.alice.stageide.sceneeditor.viewmanager;

import edu.cmu.cs.dennisc.scenegraph.SymmetricPerspectiveCamera;
import org.alice.math.immutable.AffineMatrix4x4;
import org.alice.math.immutable.AxisAlignedBox;
import org.alice.math.immutable.ClippedZPlane;
import org.alice.math.immutable.OrthogonalMatrix3x3;
import org.alice.math.immutable.Point3;
import org.alice.math.immutable.Vector3;
import org.alice.stageide.sceneeditor.CameraOption;

class TopCameraMarkerConfiguration extends OrthographicCameraMarkerConfiguration {
  private static final double DEFAULT_TOP_CAMERA_Y_OFFSET = 10.0;

  TopCameraMarkerConfiguration(CameraMarkerTracker tracker) {
    super(tracker, CameraOption.TOP, "top");
  }

  @Override
  protected void initialize() {
    AffineMatrix4x4 topTransform = new AffineMatrix4x4(
        new OrthogonalMatrix3x3(Vector3.NEGATIVE_X_AXIS, Vector3.POSITIVE_Z_AXIS, Vector3.POSITIVE_Y_AXIS),
        new Point3(0, 10, -10));
    assert topTransform.orientation().isNormalized();
    markerImp.setLocalTransformation(topTransform);
    markerImp.setPicturePlane(ClippedZPlane.createWithHeight(16));
  }

  @Override
  protected void centerOn(AxisAlignedBox box) {
    double targetHeight = box.getHeight();
    double targetWidth = box.getWidth();
    double targetDepth = box.getDepth();
    Point3 targetTranslation = box.getCenter();

    // Translation helps clip things that might be over our object, based on how large it is. (It doesn't always succeed)
    AffineMatrix4x4 topTransform = new AffineMatrix4x4(
        new OrthogonalMatrix3x3(Vector3.NEGATIVE_X_AXIS, Vector3.POSITIVE_Z_AXIS, Vector3.POSITIVE_Y_AXIS),
        new Point3(targetTranslation.x(),
            targetTranslation.y() + (targetHeight != 0 ? tracker.clampCameraValue(targetHeight * DEFAULT_TOP_CAMERA_Y_OFFSET) : DEFAULT_TOP_CAMERA_Y_OFFSET),
            targetTranslation.z()));
    markerImp.setLocalTransformation(topTransform);
    // PicturePlane controls how much of the scene is in our view, aka 'zoom'
    double height = tracker.clampPictureValue(Math.max(targetDepth, SymmetricPerspectiveCamera.DEFAULT_WIDTH_TO_HEIGHT_RATIO * targetWidth));
    markerImp.setPicturePlane(ClippedZPlane.createWithHeight(height));
  }
}
