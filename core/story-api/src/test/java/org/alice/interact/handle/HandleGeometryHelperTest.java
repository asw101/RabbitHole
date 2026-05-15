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
package org.alice.interact.handle;

import edu.cmu.cs.dennisc.scenegraph.AbstractTransformable;
import edu.cmu.cs.dennisc.scenegraph.Transformable;
import edu.cmu.cs.dennisc.scenegraph.scale.Scalable;
import org.alice.math.immutable.AffineMatrix4x4;
import org.alice.math.immutable.AxisAlignedBox;
import org.alice.math.immutable.OrthogonalMatrix3x3;
import org.alice.math.immutable.Point3;
import org.alice.math.immutable.Vector3;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Behavioral unit tests for HandleGeometryHelper.
 *
 * Tests the pure-function geometry utilities extracted from ManipulationHandle3D:
 * - getTransformationForAxis: axis-to-orientation matrix conversion
 * - computeObjectScale: bounding-box to scale factor with clamping
 * - calculateCameraRelativeOpacity: distance-based opacity fade
 * - getManipulatedObjectBox: bounding-box with fallback default
 * - getScalable: safe cast / bonus-data retrieval
 * - invertParentScale: parent-orientation normalization
 */
public class HandleGeometryHelperTest {

  private static final double EPSILON = 1e-9;

  // ===== getTransformationForAxis =====

  @Test
  public void getTransformationForAxis_positiveY_returnsIdentityOrientation() {
    AffineMatrix4x4 result = HandleGeometryHelper.getTransformationForAxis(Vector3.POSITIVE_Y_AXIS);
    assertNotNull("Result should not be null for +Y axis", result);
    OrthogonalMatrix3x3 orientation = result.orientation();
    // +Y aligned: should produce identity orientation
    assertEquals(1.0, orientation.getRight().x(), EPSILON);
    assertEquals(1.0, orientation.getUp().y(), EPSILON);
    assertEquals(1.0, orientation.getBackward().z(), EPSILON);
  }

  @Test
  public void getTransformationForAxis_negativeY_returnsFlippedOrientation() {
    AffineMatrix4x4 result = HandleGeometryHelper.getTransformationForAxis(Vector3.NEGATIVE_Y_AXIS);
    assertNotNull("Result should not be null for -Y axis", result);
    // -Y: should be rotated 180° around X-axis
    OrthogonalMatrix3x3 orientation = result.orientation();
    assertEquals(-1.0, orientation.getUp().y(), EPSILON);
  }

  @Test
  public void getTransformationForAxis_positiveX_producesNonIdentity() {
    AffineMatrix4x4 result = HandleGeometryHelper.getTransformationForAxis(Vector3.POSITIVE_X_AXIS);
    assertNotNull("Result should not be null for +X axis", result);
    OrthogonalMatrix3x3 orientation = result.orientation();
    // Up axis should be +X in the result
    assertEquals(1.0, orientation.getUp().x(), EPSILON);
    assertEquals(0.0, orientation.getUp().y(), EPSILON);
    assertEquals(0.0, orientation.getUp().z(), EPSILON);
  }

  @Test
  public void getTransformationForAxis_positiveZ_producesNonIdentity() {
    AffineMatrix4x4 result = HandleGeometryHelper.getTransformationForAxis(Vector3.POSITIVE_Z_AXIS);
    assertNotNull("Result should not be null for +Z axis", result);
    OrthogonalMatrix3x3 orientation = result.orientation();
    // Up axis should be +Z in the result
    assertEquals(0.0, orientation.getUp().x(), EPSILON);
    assertEquals(0.0, orientation.getUp().y(), EPSILON);
    assertEquals(1.0, orientation.getUp().z(), EPSILON);
  }

  @Test
  public void getTransformationForAxis_translationIsZero() {
    AffineMatrix4x4 result = HandleGeometryHelper.getTransformationForAxis(Vector3.POSITIVE_X_AXIS);
    assertEquals(0.0, result.translation().x(), EPSILON);
    assertEquals(0.0, result.translation().y(), EPSILON);
    assertEquals(0.0, result.translation().z(), EPSILON);
  }

  // ===== computeObjectScale =====

  @Test
  public void computeObjectScale_nullBox_returnsOne() {
    double scale = HandleGeometryHelper.computeObjectScale(null);
    assertEquals("Null bounding box should yield scale 1.0", 1.0, scale, EPSILON);
  }

  @Test
  public void computeObjectScale_nanBox_returnsOne() {
    AxisAlignedBox nanBox = new AxisAlignedBox(
        new Point3(Double.NaN, Double.NaN, Double.NaN),
        new Point3(Double.NaN, Double.NaN, Double.NaN));
    double scale = HandleGeometryHelper.computeObjectScale(nanBox);
    assertEquals("NaN bounding box should yield scale 1.0", 1.0, scale, EPSILON);
  }

  @Test
  public void computeObjectScale_tinyBox_clampsToMinimum() {
    // Box with very small XZ footprint
    AxisAlignedBox tinyBox = new AxisAlignedBox(
        new Point3(-0.01, 0, -0.01),
        new Point3(0.01, 0.5, 0.01));
    double scale = HandleGeometryHelper.computeObjectScale(tinyBox);
    assertEquals("Very small box should clamp to minimum 0.25", 0.25, scale, EPSILON);
  }

  @Test
  public void computeObjectScale_hugeBox_clampsToMaximum() {
    // Box with very large XZ footprint
    AxisAlignedBox hugeBox = new AxisAlignedBox(
        new Point3(-10, 0, -10),
        new Point3(10, 5, 10));
    double scale = HandleGeometryHelper.computeObjectScale(hugeBox);
    assertEquals("Very large box should clamp to maximum 2.0", 2.0, scale, EPSILON);
  }

  @Test
  public void computeObjectScale_unitBox_returnsExpectedScale() {
    // Unit box: (-1,0,-1) to (1,1,1) — XZ footprint diagonal is sqrt(4+4) ≈ 2.83
    AxisAlignedBox unitBox = new AxisAlignedBox(
        new Point3(-1, 0, -1),
        new Point3(1, 1, 1));
    double scale = HandleGeometryHelper.computeObjectScale(unitBox);
    // Y is zeroed: max=(1,0,1), min=(-1,0,-1), distance = sqrt(4+4) ≈ 2.828
    assertTrue("Unit box scale should be clamped to max 2.0", scale <= 2.0);
    assertTrue("Unit box scale should be at least 0.25", scale >= 0.25);
  }

  @Test
  public void computeObjectScale_yComponentIgnored() {
    // Two boxes differing only in Y should produce identical scale
    AxisAlignedBox box1 = new AxisAlignedBox(
        new Point3(-0.5, 0, -0.5),
        new Point3(0.5, 1, 0.5));
    AxisAlignedBox box2 = new AxisAlignedBox(
        new Point3(-0.5, 0, -0.5),
        new Point3(0.5, 100, 0.5));
    double scale1 = HandleGeometryHelper.computeObjectScale(box1);
    double scale2 = HandleGeometryHelper.computeObjectScale(box2);
    assertEquals("Y dimension should not affect computed scale", scale1, scale2, EPSILON);
  }

  // ===== calculateCameraRelativeOpacity =====

  @Test
  public void calculateCameraRelativeOpacity_nullCamera_returnsOne() {
    float opacity = HandleGeometryHelper.calculateCameraRelativeOpacity(
        new Point3(0, 0, 0), null);
    assertEquals("Null camera position should return 1.0f", 1.0f, opacity, EPSILON);
  }

  @Test
  public void calculateCameraRelativeOpacity_nullHandle_returnsOne() {
    float opacity = HandleGeometryHelper.calculateCameraRelativeOpacity(
        null, new Point3(0, 0, 5));
    assertEquals("Null handle position should return 1.0f", 1.0f, opacity, EPSILON);
  }

  @Test
  public void calculateCameraRelativeOpacity_veryClose_returnsZero() {
    // Distance < 0.2 → fully transparent
    Point3 handlePos = new Point3(0, 0, 0);
    Point3 cameraPos = new Point3(0.1, 0, 0);
    float opacity = HandleGeometryHelper.calculateCameraRelativeOpacity(handlePos, cameraPos);
    assertEquals("Very close distance (<0.2) should yield 0 opacity", 0.0f, opacity, EPSILON);
  }

  @Test
  public void calculateCameraRelativeOpacity_mediumDistance_returnsFraction() {
    // Distance ~0.35 (between 0.2 and 0.5) → partial opacity
    Point3 handlePos = new Point3(0, 0, 0);
    Point3 cameraPos = new Point3(0.35, 0, 0);
    float opacity = HandleGeometryHelper.calculateCameraRelativeOpacity(handlePos, cameraPos);
    assertTrue("Medium distance should yield partial opacity > 0", opacity > 0.0f);
    assertTrue("Medium distance should yield partial opacity < 1", opacity < 1.0f);
  }

  @Test
  public void calculateCameraRelativeOpacity_farDistance_returnsOne() {
    // Distance >= 0.5 → fully opaque
    Point3 handlePos = new Point3(0, 0, 0);
    Point3 cameraPos = new Point3(5, 0, 0);
    float opacity = HandleGeometryHelper.calculateCameraRelativeOpacity(handlePos, cameraPos);
    assertEquals("Far distance (>=0.5) should yield full opacity", 1.0f, opacity, EPSILON);
  }

  @Test
  public void calculateCameraRelativeOpacity_exactBoundary_returnsOne() {
    // Distance exactly 0.5 → should be fully opaque
    Point3 handlePos = new Point3(0, 0, 0);
    Point3 cameraPos = new Point3(0.5, 0, 0);
    float opacity = HandleGeometryHelper.calculateCameraRelativeOpacity(handlePos, cameraPos);
    assertEquals("Distance exactly 0.5 should yield full opacity", 1.0f, opacity, EPSILON);
  }

  // ===== getManipulatedObjectBox =====

  @Test
  public void getManipulatedObjectBox_nullObject_returnsDefault() {
    AxisAlignedBox box = HandleGeometryHelper.getManipulatedObjectBox(null);
    assertNotNull("Null object should return default bounding box", box);
    assertEquals("Default box min x", -1.0, box.minimum().x(), EPSILON);
    assertEquals("Default box min y", 0.0, box.minimum().y(), EPSILON);
    assertEquals("Default box min z", -1.0, box.minimum().z(), EPSILON);
    assertEquals("Default box max x", 1.0, box.maximum().x(), EPSILON);
    assertEquals("Default box max y", 1.0, box.maximum().y(), EPSILON);
    assertEquals("Default box max z", 1.0, box.maximum().z(), EPSILON);
  }

  // ===== getScalable =====

  @Test
  public void getScalable_nullObject_returnsNull() {
    Scalable result = HandleGeometryHelper.getScalable(null);
    assertNull("Null object should return null Scalable", result);
  }

  @Test
  public void getScalable_nonScalableObject_returnsNull() {
    Transformable plain = new Transformable();
    Scalable result = HandleGeometryHelper.getScalable(plain);
    assertNull("Non-Scalable object without bonus data should return null", result);
  }

  // ===== invertParentScale =====

  @Test
  public void invertParentScale_nullParent_preservesOrientation() {
    AffineMatrix4x4 local = AffineMatrix4x4.IDENTITY;
    AffineMatrix4x4 result = HandleGeometryHelper.invertParentScale(local, null);
    assertNotNull("Result should not be null with null parent", result);
    // With null parent, orientation should be normalized identity
    assertEquals(1.0, result.orientation().getRight().x(), EPSILON);
    assertEquals(1.0, result.orientation().getUp().y(), EPSILON);
    assertEquals(1.0, result.orientation().getBackward().z(), EPSILON);
  }

  @Test
  public void invertParentScale_preservesTranslation() {
    Point3 translation = new Point3(3.0, 4.0, 5.0);
    AffineMatrix4x4 local = new AffineMatrix4x4(OrthogonalMatrix3x3.IDENTITY, translation);
    AffineMatrix4x4 result = HandleGeometryHelper.invertParentScale(local, null);
    assertEquals("Translation x should be preserved", 3.0, result.translation().x(), EPSILON);
    assertEquals("Translation y should be preserved", 4.0, result.translation().y(), EPSILON);
    assertEquals("Translation z should be preserved", 5.0, result.translation().z(), EPSILON);
  }
}
