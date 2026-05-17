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

import org.junit.Test;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Characterization tests for MeshBuilder — the pure computational core
 * extracted from Model.initializeMesh and Model.createWeightInfo.
 *
 * These tests encode the exact behavior of the Sims-to-Alice index remapping
 * and weight remapping algorithms. They must pass identically before and after
 * the extraction refactor to guarantee no behavioral change.
 *
 * All data arrays mirror the Sims format:
 *   - Indices are interleaved triplets: (uvIndex, normalIndex, vertexIndex)
 *   - UV indices point to 2-float pairs in the UV array
 *   - Normal indices point to 3-float triples in the normal array
 *   - Vertex indices point to 3-float triples in the vertex array
 */
public class MeshBuilderTest {

  // ── remapIndices: single texture, single triangle ───────────────────────

  @Test
  public void remapIndices_singleTextureSingleTriangle_producesUnifiedBuffers() {
    // 2 source vertices: v0=(1,2,3) at offset 0, v1=(4,5,6) at offset 3
    float[] vertices = {1f, 2f, 3f, 4f, 5f, 6f};
    // 2 source normals: n0=(0,0,1) at offset 0, n1=(0,1,0) at offset 3
    float[] normals = {0f, 0f, 1f, 0f, 1f, 0f};
    // 2 source UVs: uv0=(0,0) at offset 0, uv1=(1,1) at offset 2
    float[] uvs = {0f, 0f, 1f, 1f};

    // One triangle: 3 triplets (uv, normal, vertex)
    // Point 0: uv0, n0, v0  →  indices [0, 0, 0]
    // Point 1: uv1, n1, v1  →  indices [2, 3, 3]
    // Point 2: uv0, n0, v1  →  indices [0, 0, 3]
    Map<String, int[]> textureIdToIndices = new LinkedHashMap<>();
    textureIdToIndices.put("texA", new int[]{0, 0, 0, 2, 3, 3, 0, 0, 3});

    RemappedMeshData result = MeshBuilder.remapIndices(textureIdToIndices, vertices, normals, uvs);

    // 9 triplet elements / 3 = 3 unified indices
    assertEquals(3, result.indices.length);

    // Unified vertices (doubles): v0, v1, v1
    assertArrayEquals(new double[]{1, 2, 3, 4, 5, 6, 4, 5, 6}, result.vertices, 0.0001);

    // Unified normals: n0, n1, n0
    assertArrayEquals(new float[]{0, 0, 1, 0, 1, 0, 0, 0, 1}, result.normals, 0.0001f);

    // Unified UVs: uv0, uv1, uv0
    assertArrayEquals(new float[]{0, 0, 1, 1, 0, 0}, result.uvs, 0.0001f);

    // Sequential indices
    assertArrayEquals(new int[]{0, 1, 2}, result.indices);
  }

  @Test
  public void remapIndices_singleTextureSingleTriangle_producesCorrectTextureAssignment() {
    float[] vertices = {1f, 2f, 3f, 4f, 5f, 6f};
    float[] normals = {0f, 0f, 1f, 0f, 1f, 0f};
    float[] uvs = {0f, 0f, 1f, 1f};
    Map<String, int[]> textureIdToIndices = new LinkedHashMap<>();
    textureIdToIndices.put("texA", new int[]{0, 0, 0, 2, 3, 3, 0, 0, 3});

    RemappedMeshData result = MeshBuilder.remapIndices(textureIdToIndices, vertices, normals, uvs);

    // All 3 unified indices should reference texture "texA"
    assertArrayEquals(new String[]{"texA", "texA", "texA"}, result.textureIdsPerIndex);
  }

  @Test
  public void remapIndices_singleTextureSingleTriangle_producesCorrectVertexMappings() {
    float[] vertices = {1f, 2f, 3f, 4f, 5f, 6f};
    float[] normals = {0f, 0f, 1f, 0f, 1f, 0f};
    float[] uvs = {0f, 0f, 1f, 1f};
    Map<String, int[]> textureIdToIndices = new LinkedHashMap<>();
    textureIdToIndices.put("texA", new int[]{0, 0, 0, 2, 3, 3, 0, 0, 3});

    RemappedMeshData result = MeshBuilder.remapIndices(textureIdToIndices, vertices, normals, uvs);

    // oldVertexIndexToNewIndex: maps old vertex (vertexIndex/3) → last new index
    // vertex 0 (index 0/3=0) first appears at new index 0
    // vertex 1 (index 3/3=1) appears at new index 1, then again at 2 → last wins
    assertEquals(Integer.valueOf(0), result.oldVertexIndexToNewIndex.get(0));
    assertEquals(Integer.valueOf(2), result.oldVertexIndexToNewIndex.get(1));

    // newIndexToOldVertex: maps new index → old vertex (vertexIndex/3)
    assertEquals(Integer.valueOf(0), result.newIndexToOldVertex.get(0));
    assertEquals(Integer.valueOf(1), result.newIndexToOldVertex.get(1));
    assertEquals(Integer.valueOf(1), result.newIndexToOldVertex.get(2));
  }

  // ── remapIndices: multiple textures ─────────────────────────────────────

  @Test
  public void remapIndices_multipleTextures_concatenatesIndicesInOrder() {
    // 3 source vertices: v0=(1,0,0), v1=(0,1,0), v2=(0,0,1)
    float[] vertices = {1f, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 1f};
    // 3 source normals (same as vertices for simplicity)
    float[] normals = {1f, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 1f};
    // 3 source UVs
    float[] uvs = {0f, 0f, 0.5f, 0.5f, 1f, 1f};

    // Use LinkedHashMap to ensure insertion-order iteration
    Map<String, int[]> textureIdToIndices = new LinkedHashMap<>();
    // texA: 1 point → triplet (uv0, n0, v0)
    textureIdToIndices.put("texA", new int[]{0, 0, 0});
    // texB: 1 point → triplet (uv1, n1, v1)
    textureIdToIndices.put("texB", new int[]{2, 3, 3});

    RemappedMeshData result = MeshBuilder.remapIndices(textureIdToIndices, vertices, normals, uvs);

    assertEquals(2, result.indices.length);

    // First unified index from texA, second from texB
    assertEquals("texA", result.textureIdsPerIndex[0]);
    assertEquals("texB", result.textureIdsPerIndex[1]);

    // Vertices: v0 then v1
    assertArrayEquals(new double[]{1, 0, 0, 0, 1, 0}, result.vertices, 0.0001);

    // UVs: uv0 then uv1
    assertArrayEquals(new float[]{0, 0, 0.5f, 0.5f}, result.uvs, 0.0001f);
  }

  // ── remapIndices: empty input ───────────────────────────────────────────

  @Test
  public void remapIndices_emptyIndicesMap_producesEmptyOutput() {
    float[] vertices = {1f, 2f, 3f};
    float[] normals = {0f, 0f, 1f};
    float[] uvs = {0f, 0f};
    Map<String, int[]> textureIdToIndices = new LinkedHashMap<>();

    RemappedMeshData result = MeshBuilder.remapIndices(textureIdToIndices, vertices, normals, uvs);

    assertEquals(0, result.indices.length);
    assertEquals(0, result.vertices.length);
    assertEquals(0, result.normals.length);
    assertEquals(0, result.uvs.length);
    assertEquals(0, result.textureIdsPerIndex.length);
    assertTrue(result.oldVertexIndexToNewIndex.isEmpty());
    assertTrue(result.newIndexToOldVertex.isEmpty());
  }

  @Test
  public void remapIndices_emptyIndicesArray_producesEmptyOutput() {
    float[] vertices = {1f, 2f, 3f};
    float[] normals = {0f, 0f, 1f};
    float[] uvs = {0f, 0f};
    Map<String, int[]> textureIdToIndices = new LinkedHashMap<>();
    textureIdToIndices.put("texA", new int[]{});

    RemappedMeshData result = MeshBuilder.remapIndices(textureIdToIndices, vertices, normals, uvs);

    assertEquals(0, result.indices.length);
  }

  // ── remapIndices: index count arithmetic ────────────────────────────────

  @Test
  public void remapIndices_twoTriangles_produceSixUnifiedIndices() {
    // 4 vertices: enough data for any referenced index
    float[] vertices = new float[12]; // 4 * 3
    float[] normals = new float[12];
    float[] uvs = new float[8]; // 4 * 2

    // 2 triangles = 6 points = 18 triplet elements
    Map<String, int[]> textureIdToIndices = new LinkedHashMap<>();
    textureIdToIndices.put("texA", new int[]{
        0, 0, 0, 2, 3, 3, 4, 6, 6,   // triangle 1
        0, 0, 0, 4, 6, 6, 6, 9, 9    // triangle 2
    });

    RemappedMeshData result = MeshBuilder.remapIndices(textureIdToIndices, vertices, normals, uvs);

    // 18 / 3 = 6 unified indices
    assertEquals(6, result.indices.length);
    // Sequential: [0, 1, 2, 3, 4, 5]
    for (int i = 0; i < 6; i++) {
      assertEquals(i, result.indices[i]);
    }
  }

  // ── remapIndices: vertex data fidelity ──────────────────────────────────

  @Test
  public void remapIndices_preservesExactVertexValues() {
    // Source data with distinctive values to verify no swaps
    float[] vertices = {10f, 20f, 30f, 40f, 50f, 60f};
    float[] normals = {0.1f, 0.2f, 0.3f, 0.4f, 0.5f, 0.6f};
    float[] uvs = {0.11f, 0.22f, 0.33f, 0.44f};

    // Single point: uv1, n1, v1
    Map<String, int[]> textureIdToIndices = new LinkedHashMap<>();
    textureIdToIndices.put("tex", new int[]{2, 3, 3});

    RemappedMeshData result = MeshBuilder.remapIndices(textureIdToIndices, vertices, normals, uvs);

    // Vertex at offset 3: (40, 50, 60)
    assertArrayEquals(new double[]{40, 50, 60}, result.vertices, 0.0001);
    // Normal at offset 3: (0.4, 0.5, 0.6)
    assertArrayEquals(new float[]{0.4f, 0.5f, 0.6f}, result.normals, 0.0001f);
    // UV at offset 2: (0.33, 0.44)
    assertArrayEquals(new float[]{0.33f, 0.44f}, result.uvs, 0.0001f);
  }

  // ── remapWeights: basic 1:1 mapping ─────────────────────────────────────

  @Test
  public void remapWeights_simpleMapping_transfersWeightsCorrectly() {
    // Original weights: vertex 0 → 0.5, vertex 1 → 0.8
    float[] vertexWeights = {0.5f, 0.8f};

    // From the remapIndices example above:
    // oldVertexIndexToNewIndex: {0: 0, 1: 2}
    Map<Integer, Integer> oldVertexIndexToNewIndex = new HashMap<>();
    oldVertexIndexToNewIndex.put(0, 0);
    oldVertexIndexToNewIndex.put(1, 2);

    // newIndexToOldVertex: {0: 0, 1: 1, 2: 1}
    Map<Integer, Integer> newIndexToOldVertex = new HashMap<>();
    newIndexToOldVertex.put(0, 0);
    newIndexToOldVertex.put(1, 1);
    newIndexToOldVertex.put(2, 1);

    float[] result = MeshBuilder.remapWeights(vertexWeights, oldVertexIndexToNewIndex, newIndexToOldVertex);

    // maxNewIndex = max(0, 2) = 2 → result length = 3
    assertEquals(3, result.length);
    // new[0] = old[0] = 0.5
    assertEquals(0.5f, result[0], 0.0001f);
    // new[1] = old[1] = 0.8
    assertEquals(0.8f, result[1], 0.0001f);
    // new[2] = old[1] = 0.8
    assertEquals(0.8f, result[2], 0.0001f);
  }

  // ── remapWeights: out-of-range old vertex → zero weight ─────────────────

  @Test
  public void remapWeights_oldVertexBeyondWeightsLength_returnsZero() {
    // Only 1 weight value
    float[] vertexWeights = {0.5f};

    // Old vertex 0 → new 0, old vertex 5 → new 1 (5 is beyond weights length)
    Map<Integer, Integer> oldVertexIndexToNewIndex = new HashMap<>();
    oldVertexIndexToNewIndex.put(0, 0);
    oldVertexIndexToNewIndex.put(5, 1);

    Map<Integer, Integer> newIndexToOldVertex = new HashMap<>();
    newIndexToOldVertex.put(0, 0);
    newIndexToOldVertex.put(1, 5);

    float[] result = MeshBuilder.remapWeights(vertexWeights, oldVertexIndexToNewIndex, newIndexToOldVertex);

    assertEquals(2, result.length);
    assertEquals(0.5f, result[0], 0.0001f);
    // Old vertex 5 is beyond weights array → 0
    assertEquals(0f, result[1], 0.0001f);
  }

  // ── remapWeights: unmapped old vertices ignored in size calculation ──────

  @Test
  public void remapWeights_unmappedOldVertices_ignoredForMaxCalculation() {
    // 3 old vertices with weights, but only vertex 0 and 2 have mappings
    float[] vertexWeights = {0.3f, 0.6f, 0.9f};

    // Only vertices 0 and 2 are mapped; vertex 1 is unmapped (missing data)
    Map<Integer, Integer> oldVertexIndexToNewIndex = new HashMap<>();
    oldVertexIndexToNewIndex.put(0, 0);
    oldVertexIndexToNewIndex.put(2, 1);

    Map<Integer, Integer> newIndexToOldVertex = new HashMap<>();
    newIndexToOldVertex.put(0, 0);
    newIndexToOldVertex.put(1, 2);

    float[] result = MeshBuilder.remapWeights(vertexWeights, oldVertexIndexToNewIndex, newIndexToOldVertex);

    // maxNewIndex from mapped keys = max(0, 1) = 1 → length = 2
    assertEquals(2, result.length);
    assertEquals(0.3f, result[0], 0.0001f);
    assertEquals(0.9f, result[1], 0.0001f);
  }

  // ── remapWeights: identity mapping ──────────────────────────────────────

  @Test
  public void remapWeights_identityMapping_preservesOriginalWeights() {
    float[] vertexWeights = {0.1f, 0.2f, 0.3f, 0.4f};

    // Identity: old vertex i → new index i
    Map<Integer, Integer> oldVertexIndexToNewIndex = new HashMap<>();
    Map<Integer, Integer> newIndexToOldVertex = new HashMap<>();
    for (int i = 0; i < 4; i++) {
      oldVertexIndexToNewIndex.put(i, i);
      newIndexToOldVertex.put(i, i);
    }

    float[] result = MeshBuilder.remapWeights(vertexWeights, oldVertexIndexToNewIndex, newIndexToOldVertex);

    assertEquals(4, result.length);
    assertArrayEquals(vertexWeights, result, 0.0001f);
  }

  // ── remapWeights: reversed mapping ──────────────────────────────────────

  @Test
  public void remapWeights_reversedMapping_reversesWeightOrder() {
    float[] vertexWeights = {0.1f, 0.2f, 0.3f};

    // Reversed: old 0→new 2, old 1→new 1, old 2→new 0
    Map<Integer, Integer> oldVertexIndexToNewIndex = new HashMap<>();
    oldVertexIndexToNewIndex.put(0, 2);
    oldVertexIndexToNewIndex.put(1, 1);
    oldVertexIndexToNewIndex.put(2, 0);

    Map<Integer, Integer> newIndexToOldVertex = new HashMap<>();
    newIndexToOldVertex.put(0, 2);
    newIndexToOldVertex.put(1, 1);
    newIndexToOldVertex.put(2, 0);

    float[] result = MeshBuilder.remapWeights(vertexWeights, oldVertexIndexToNewIndex, newIndexToOldVertex);

    assertEquals(3, result.length);
    assertEquals(0.3f, result[0], 0.0001f);
    assertEquals(0.2f, result[1], 0.0001f);
    assertEquals(0.1f, result[2], 0.0001f);
  }

  // ── remapWeights: single vertex ─────────────────────────────────────────

  @Test
  public void remapWeights_singleVertex_producesLengthOneArray() {
    float[] vertexWeights = {1.0f};

    Map<Integer, Integer> oldVertexIndexToNewIndex = new HashMap<>();
    oldVertexIndexToNewIndex.put(0, 0);

    Map<Integer, Integer> newIndexToOldVertex = new HashMap<>();
    newIndexToOldVertex.put(0, 0);

    float[] result = MeshBuilder.remapWeights(vertexWeights, oldVertexIndexToNewIndex, newIndexToOldVertex);

    assertEquals(1, result.length);
    assertEquals(1.0f, result[0], 0.0001f);
  }

  // ── remapWeights: no mapped vertices → empty result ─────────────────────

  @Test
  public void remapWeights_noMappedVertices_producesEmptyArray() {
    // Weights exist but nothing maps into the new index space
    float[] vertexWeights = {0.5f, 0.8f};

    Map<Integer, Integer> oldVertexIndexToNewIndex = new HashMap<>();
    Map<Integer, Integer> newIndexToOldVertex = new HashMap<>();

    float[] result = MeshBuilder.remapWeights(vertexWeights, oldVertexIndexToNewIndex, newIndexToOldVertex);

    // No mapped vertices → maxVertexIndex stays 0 with no entries → length 0
    assertEquals(0, result.length);
  }

  // ── remapWeights: fan-out (one old vertex → multiple new indices) ───────

  @Test
  public void remapWeights_fanOut_duplicatesWeightForAllNewIndices() {
    // Single old vertex with weight 0.75
    float[] vertexWeights = {0.75f};

    // Old vertex 0 maps to new index 2 (last wins in oldVertexIndexToNewIndex)
    Map<Integer, Integer> oldVertexIndexToNewIndex = new HashMap<>();
    oldVertexIndexToNewIndex.put(0, 2);

    // But all 3 new indices refer back to old vertex 0
    Map<Integer, Integer> newIndexToOldVertex = new HashMap<>();
    newIndexToOldVertex.put(0, 0);
    newIndexToOldVertex.put(1, 0);
    newIndexToOldVertex.put(2, 0);

    float[] result = MeshBuilder.remapWeights(vertexWeights, oldVertexIndexToNewIndex, newIndexToOldVertex);

    // maxNewIndex = 2 → length = 3
    assertEquals(3, result.length);
    // All indices map back to old vertex 0 → all get weight 0.75
    assertEquals(0.75f, result[0], 0.0001f);
    assertEquals(0.75f, result[1], 0.0001f);
    assertEquals(0.75f, result[2], 0.0001f);
  }

  // ── Integration: remapIndices output feeds remapWeights ─────────────────

  @Test
  public void integration_remapIndicesOutputFeedsRemapWeights() {
    // Set up a realistic small mesh: 1 triangle with 3 distinct vertices
    float[] vertices = {0f, 0f, 0f, 1f, 0f, 0f, 0f, 1f, 0f};
    float[] normals = {0f, 0f, 1f, 0f, 0f, 1f, 0f, 0f, 1f};
    float[] uvs = {0f, 0f, 1f, 0f, 0.5f, 1f};

    // 3 points with separate indices into each array
    Map<String, int[]> textureIdToIndices = new LinkedHashMap<>();
    textureIdToIndices.put("body", new int[]{
        0, 0, 0,   // point 0: uv0, n0, v0
        2, 3, 3,   // point 1: uv1, n1, v1
        4, 6, 6    // point 2: uv2, n2, v2
    });

    RemappedMeshData meshData = MeshBuilder.remapIndices(textureIdToIndices, vertices, normals, uvs);

    // Feed mapping into weight remapping
    // Weights per original vertex: v0=0.2, v1=0.5, v2=1.0
    float[] originalWeights = {0.2f, 0.5f, 1.0f};

    float[] remapped = MeshBuilder.remapWeights(
        originalWeights,
        meshData.oldVertexIndexToNewIndex,
        meshData.newIndexToOldVertex
    );

    // With 3 distinct vertices mapping 1:1, weights transfer directly
    assertEquals(3, remapped.length);
    assertEquals(0.2f, remapped[0], 0.0001f);
    assertEquals(0.5f, remapped[1], 0.0001f);
    assertEquals(1.0f, remapped[2], 0.0001f);
  }

  // ── remapIndices: shared vertex gets last-wins mapping ──────────────────

  @Test
  public void remapIndices_sharedVertex_oldToNewMappingUsesLastOccurrence() {
    float[] vertices = {10f, 20f, 30f}; // 1 vertex
    float[] normals = {0f, 0f, 1f};     // 1 normal
    float[] uvs = {0f, 0f, 1f, 1f};     // 2 UVs

    // 2 points both referencing vertex 0 but with different UVs
    Map<String, int[]> textureIdToIndices = new LinkedHashMap<>();
    textureIdToIndices.put("tex", new int[]{
        0, 0, 0,   // point 0: uv0, n0, v0
        2, 0, 0    // point 1: uv1, n0, v0 (same vertex, different UV)
    });

    RemappedMeshData result = MeshBuilder.remapIndices(textureIdToIndices, vertices, normals, uvs);

    assertEquals(2, result.indices.length);

    // oldVertexIndexToNewIndex: vertex 0/3=0 → last occurrence is new index 1
    assertEquals(Integer.valueOf(1), result.oldVertexIndexToNewIndex.get(0));

    // Both new indices map back to old vertex 0
    assertEquals(Integer.valueOf(0), result.newIndexToOldVertex.get(0));
    assertEquals(Integer.valueOf(0), result.newIndexToOldVertex.get(1));
  }
}
