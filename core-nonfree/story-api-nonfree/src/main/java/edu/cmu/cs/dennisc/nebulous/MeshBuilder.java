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
package edu.cmu.cs.dennisc.nebulous;

import java.util.Map;

/**
 * Pure static methods for index remapping and weight remapping during
 * Sims-to-Alice mesh export. All methods are free of JNI and side effects,
 * making them independently testable.
 */
class MeshBuilder {

  private MeshBuilder() {
  }

  /**
   * Remaps Sims triplet indices (uv, normal, vertex interleaved) into
   * unified Alice index buffers. Each Sims triplet produces one unified
   * vertex with its own normal and UV coordinates.
   *
   * @param textureIdToIndices map from texture name to raw Sims indices array
   * @param vertices           raw vertex positions (3 floats per vertex)
   * @param normals            raw normals (3 floats per normal)
   * @param uvs                raw UVs (2 floats per UV)
   * @return remapped mesh data with unified buffers and index mappings
   */
  static RemappedMeshData remapIndices(Map<String, int[]> textureIdToIndices,
                                       float[] vertices, float[] normals, float[] uvs) {
    // TODO: Extract logic from Model.initializeMesh
    throw new UnsupportedOperationException("Not yet implemented");
  }

  /**
   * Remaps vertex weights from old (Sims) index space to new (Alice) index
   * space. Computes the required output array size internally from the
   * overlap of oldVertexIndexToNewIndex keys.
   *
   * @param vertexWeights           original per-vertex weights from JNI
   * @param oldVertexIndexToNewIndex mapping from old vertex index to new unified index
   * @param newIndexToOldVertex      mapping from new unified index to old vertex index
   * @return remapped weights array sized to (maxNewIndex + 1)
   */
  static float[] remapWeights(float[] vertexWeights,
                              Map<Integer, Integer> oldVertexIndexToNewIndex,
                              Map<Integer, Integer> newIndexToOldVertex) {
    // TODO: Extract logic from Model.createWeightInfo
    throw new UnsupportedOperationException("Not yet implemented");
  }
}
