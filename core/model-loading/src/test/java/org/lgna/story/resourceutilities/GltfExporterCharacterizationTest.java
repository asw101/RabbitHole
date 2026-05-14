/*******************************************************************************
 * Copyright (c) 2006, 2018, Carnegie Mellon University. All rights reserved.
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
package org.lgna.story.resourceutilities;

import org.junit.Test;
import org.lgna.project.io.JointedModelExporter;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Characterization tests that pin observable behaviour of the GLTF exporter
 * <em>before</em> the extraction refactor.  Any breakage here means the
 * refactored delegates diverged from the original logic.
 */
public class GltfExporterCharacterizationTest {

  // ── convertToFloatArray ───────────────────────────────────────────

  @Test
  public void convertToFloatArrayReturnsEmptyForNull() {
    float[] result = JointedModelGltfExporter.convertToFloatArray(null);
    assertNotNull(result);
    assertEquals(0, result.length);
  }

  @Test
  public void convertToFloatArrayConvertsDoublesToFloats() {
    DoubleBuffer buf = ByteBuffer.allocateDirect(6 * Double.BYTES)
        .order(ByteOrder.nativeOrder()).asDoubleBuffer();
    buf.put(new double[]{1.0, 2.0, 3.0, 4.0, 5.0, 6.0});
    buf.flip();

    float[] result = JointedModelGltfExporter.convertToFloatArray(buf);

    assertArrayEquals(new float[]{1f, 2f, 3f, 4f, 5f, 6f}, result, 0.0001f);
    // Buffer should be rewound after call
    assertEquals(0, buf.position());
  }

  // ── GltfBufferUtils.normalize ─────────────────────────────────────

  @Test
  public void normalizeScalesVectorsToUnitLength() {
    FloatBuffer normals = ByteBuffer.allocateDirect(3 * Float.BYTES)
        .order(ByteOrder.nativeOrder()).asFloatBuffer();
    normals.put(new float[]{3f, 0f, 4f});
    normals.flip();

    GltfBufferUtils.normalize(normals);

    assertEquals(0.6f, normals.get(0), 1e-5f);
    assertEquals(0.0f, normals.get(1), 1e-5f);
    assertEquals(0.8f, normals.get(2), 1e-5f);
  }

  @Test
  public void normalizeHandlesZeroLengthVector() {
    FloatBuffer normals = ByteBuffer.allocateDirect(3 * Float.BYTES)
        .order(ByteOrder.nativeOrder()).asFloatBuffer();
    normals.put(new float[]{0f, 0f, 0f});
    normals.flip();

    GltfBufferUtils.normalize(normals);

    // Default fallback is (1, 0, 0)
    assertEquals(1.0f, normals.get(0), 1e-5f);
    assertEquals(0.0f, normals.get(1), 1e-5f);
    assertEquals(0.0f, normals.get(2), 1e-5f);
  }

  @Test
  public void normalizeHandlesNaNComponents() {
    FloatBuffer normals = ByteBuffer.allocateDirect(3 * Float.BYTES)
        .order(ByteOrder.nativeOrder()).asFloatBuffer();
    normals.put(new float[]{Float.NaN, 1f, 0f});
    normals.flip();

    GltfBufferUtils.normalize(normals);

    // Default fallback is (1, 0, 0)
    assertEquals(1.0f, normals.get(0), 1e-5f);
    assertEquals(0.0f, normals.get(1), 1e-5f);
    assertEquals(0.0f, normals.get(2), 1e-5f);
  }

  // ── VertexWeights inner class preserved ───────────────────────────

  @Test
  public void vertexWeightsClassIsAccessible() {
    JointedModelGltfExporter.VertexWeights vw = new JointedModelGltfExporter.VertexWeights();
    assertNotNull(vw.jointIndices);
    assertNotNull(vw.weights);
    assertTrue(vw.jointIndices.isEmpty());
    assertTrue(vw.weights.isEmpty());
  }

  // ── Public API surface ────────────────────────────────────────────

  @Test
  public void classImplementsJointedModelExporter() {
    assertTrue(JointedModelExporter.class.isAssignableFrom(JointedModelGltfExporter.class));
  }
}
