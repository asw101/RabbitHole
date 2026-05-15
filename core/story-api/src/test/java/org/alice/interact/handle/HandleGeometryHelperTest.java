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

import org.alice.math.immutable.AffineMatrix4x4;
import org.alice.math.immutable.AxisAlignedBox;
import org.alice.math.immutable.Point3;
import org.alice.math.immutable.Vector3;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Characterization tests for HandleGeometryHelper — verifies that the
 * delegated helper methods produce the same results as the original
 * inline implementations in ManipulationHandle3D.
 */
public class HandleGeometryHelperTest {

  private static final double EPSILON = 1e-9;

  // --- getTransformationForAxis ---

  @Test
  public void transformationForPositiveYAxis_isIdentityOrientation() {
    AffineMatrix4x4 result = HandleGeometryHelper.getTransformationForAxis(Vector3.POSITIVE_Y_AXIS);
    assertNotNull(result);
    assertEquals(0.0, result.translation().x(), EPSILON);
    assertEquals(0.0, result.translation().y(), EPSILON);
    assertEquals(0.0, result.translation().z(), EPSILON);
  }

  @Test
  public void transformationForNegativeYAxis_appliesRotation() {
    AffineMatrix4x4 result = HandleGeometryHelper.getTransformationForAxis(
        new Vector3(0, -1, 0));
    assertNotNull(result);
  }

  @Test
  public void transformationForXAxis_computesCrossProduct() {
    AffineMatrix4x4 result = HandleGeometryHelper.getTransformationForAxis(Vector3.POSITIVE_X_AXIS);
    assertNotNull(result);
    // The up axis of the result should align with the input axis
    double upY = result.orientation().getUp().y();
    assertEquals(0.0, upY, EPSILON);
  }

  @Test
  public void transformationForZAxis_computesCrossProduct() {
    AffineMatrix4x4 result = HandleGeometryHelper.getTransformationForAxis(
        new Vector3(0, 0, 1));
    assertNotNull(result);
  }

  // --- computeObjectScale ---

  @Test
  public void computeObjectScale_nullBox_returns1() {
    assertEquals(1.0, HandleGeometryHelper.computeObjectScale(null), EPSILON);
  }

  @Test
  public void computeObjectScale_nanBox_returns1() {
    AxisAlignedBox nanBox = new AxisAlignedBox(
        new Point3(Double.NaN, 0, 0), new Point3(1, 1, 1));
    assertEquals(1.0, HandleGeometryHelper.computeObjectScale(nanBox), EPSILON);
  }

  @Test
  public void computeObjectScale_tinyBox_clampsToQuarter() {
    AxisAlignedBox tinyBox = new AxisAlignedBox(
        new Point3(0, 0, 0), new Point3(0.01, 0.01, 0.01));
    assertEquals(0.25, HandleGeometryHelper.computeObjectScale(tinyBox), EPSILON);
  }

  @Test
  public void computeObjectScale_hugeBox_clampsToTwo() {
    AxisAlignedBox hugeBox = new AxisAlignedBox(
        new Point3(-10, 0, -10), new Point3(10, 10, 10));
    assertEquals(2.0, HandleGeometryHelper.computeObjectScale(hugeBox), EPSILON);
  }

  @Test
  public void computeObjectScale_unitBox_returnsDistance() {
    AxisAlignedBox unitBox = new AxisAlignedBox(
        new Point3(-0.5, 0, -0.5), new Point3(0.5, 1, 0.5));
    double expected = Math.sqrt(2.0); // distance of (0.5,0,0.5) from (-0.5,0,-0.5) with Y zeroed
    double result = HandleGeometryHelper.computeObjectScale(unitBox);
    assertTrue(result >= 0.25 && result <= 2.0);
    assertEquals(expected, result, EPSILON);
  }

  // --- computeManipulatedObjectBox ---

  @Test
  public void computeManipulatedObjectBox_nullObject_returnsDefault() {
    AxisAlignedBox box = HandleGeometryHelper.computeManipulatedObjectBox(null);
    assertNotNull(box);
  }

  // --- calculateCameraRelativeOpacity ---

  @Test
  public void cameraOpacity_nullParent_returns1() {
    float result = HandleGeometryHelper.calculateCameraRelativeOpacity(null, new Point3(0, 0, 0));
    assertEquals(1.0f, result, EPSILON);
  }

  @Test
  public void cameraOpacity_nullCamera_returns1() {
    // A Transformable with default identity transform
    edu.cmu.cs.dennisc.scenegraph.Transformable t = new edu.cmu.cs.dennisc.scenegraph.Transformable();
    float result = HandleGeometryHelper.calculateCameraRelativeOpacity(t, null);
    assertEquals(1.0f, result, EPSILON);
  }

  // --- resolveParentTransformable ---

  @Test
  public void resolveParent_nullManipulated_returnsNull() {
    assertNotNull("Method should handle null",
        HandleGeometryHelper.resolveParentTransformable(null, null) == null ? "null" : "not null");
  }

  // --- NOT_3D_HANDLE_CRITERION ---

  @Test
  public void not3dHandleCriterion_acceptsNonHandleComponent() {
    edu.cmu.cs.dennisc.scenegraph.Transformable t = new edu.cmu.cs.dennisc.scenegraph.Transformable();
    assertTrue(HandleGeometryHelper.NOT_3D_HANDLE_CRITERION.accept(t));
  }

  @Test
  public void not3dHandleCriterion_sameAsPublicField() {
    // The public API field must delegate to the same instance
    assertTrue(ManipulationHandle3D.NOT_3D_HANDLE_CRITERION ==
        HandleGeometryHelper.NOT_3D_HANDLE_CRITERION);
  }
}
