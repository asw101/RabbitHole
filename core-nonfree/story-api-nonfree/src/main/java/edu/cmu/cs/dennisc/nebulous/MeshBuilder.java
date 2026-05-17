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

import java.util.Arrays;
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
   * <p>Sims indices are stored as interleaved triplets: (uvIndex, normalIndex, vertexIndex).
   * Each triplet references separate source arrays. Alice uses a single index into
   * unified vertex/normal/UV buffers, so this method expands each triplet into a
   * standalone unified vertex.
   *
   * @param textureIdToIndices map from texture name to raw Sims indices array
   * @param vertices           raw vertex positions (3 floats per vertex)
   * @param normals            raw normals (3 floats per normal)
   * @param uvs                raw UVs (2 floats per UV)
   * @return remapped mesh data with unified buffers and index mappings
   */
  static RemappedMeshData remapIndices(Map<String, int[]> textureIdToIndices,
                                       float[] vertices, float[] normals, float[] uvs) {
    int originalIndexCount = 0;
    for (int[] indices : textureIdToIndices.values()) {
      originalIndexCount += indices.length;
    }

    int newIndexCount = originalIndexCount / 3;
    if (newIndexCount == 0) {
      return new RemappedMeshData(new double[0], new float[0], new float[0],
          new int[0], new String[0], new int[0], new int[0]);
    }

    double[] newVertices = new double[newIndexCount * 3];
    float[] newNormals = new float[newIndexCount * 3];
    float[] newUVs = new float[newIndexCount * 2];
    String[] newTextureIds = new String[newIndexCount];
    int[] newIndices = new int[newIndexCount];

    int oldVertexCount = vertices.length / 3;
    int[] oldVertexIndexToNewIndex = new int[oldVertexCount];
    Arrays.fill(oldVertexIndexToNewIndex, -1);
    int[] newIndexToOldVertex = new int[newIndexCount];

    int currentIndex = 0;
    for (Map.Entry<String, int[]> indicesEntry : textureIdToIndices.entrySet()) {
      int[] currentIndices = indicesEntry.getValue();
      String currentTextureId = indicesEntry.getKey();
      for (int i = 0; i < currentIndices.length; ) {
        int uvIndex = currentIndices[i];
        newUVs[currentIndex * 2] = uvs[uvIndex];
        newUVs[currentIndex * 2 + 1] = uvs[uvIndex + 1];
        i++;
        int normalIndex = currentIndices[i];
        newNormals[currentIndex * 3] = normals[normalIndex];
        newNormals[currentIndex * 3 + 1] = normals[normalIndex + 1];
        newNormals[currentIndex * 3 + 2] = normals[normalIndex + 2];
        i++;
        int vertexIndex = currentIndices[i];
        newVertices[currentIndex * 3] = vertices[vertexIndex];
        newVertices[currentIndex * 3 + 1] = vertices[vertexIndex + 1];
        newVertices[currentIndex * 3 + 2] = vertices[vertexIndex + 2];
        newTextureIds[currentIndex] = currentTextureId;
        // Last-wins: if the same old vertex appears multiple times, the last new index wins
        oldVertexIndexToNewIndex[vertexIndex / 3] = currentIndex;
        newIndexToOldVertex[currentIndex] = vertexIndex / 3;
        newIndices[currentIndex] = currentIndex;
        i++;
        currentIndex++;
      }
    }
    return new RemappedMeshData(newVertices, newNormals, newUVs, newIndices, newTextureIds,
        oldVertexIndexToNewIndex, newIndexToOldVertex);
  }

  /**
   * Remaps vertex weights from old (Sims) index space to new (Alice) index
   * space. Output array size equals newIndexToOldVertex.length.
   *
   * @param vertexWeights      original per-vertex weights from JNI
   * @param newIndexToOldVertex maps each new unified index to its old vertex index
   * @return remapped weights array, or empty if newIndexToOldVertex is empty
   */
  static float[] remapWeights(float[] vertexWeights, int[] newIndexToOldVertex) {
    if (newIndexToOldVertex.length == 0) {
      return new float[0];
    }
    float[] remappedWeights = new float[newIndexToOldVertex.length];
    for (int i = 0; i < remappedWeights.length; i++) {
      int oldVertexIndex = newIndexToOldVertex[i];
      if (oldVertexIndex >= vertexWeights.length) {
        remappedWeights[i] = 0;
      } else {
        remappedWeights[i] = vertexWeights[oldVertexIndex];
      }
    }
    return remappedWeights;
  }
}
