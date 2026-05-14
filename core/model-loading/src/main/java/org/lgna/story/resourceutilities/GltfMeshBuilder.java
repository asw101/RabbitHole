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

import de.javagl.jgltf.impl.v2.GlTF;
import de.javagl.jgltf.impl.v2.Mesh;
import de.javagl.jgltf.impl.v2.MeshPrimitive;
import de.javagl.jgltf.impl.v2.Skin;
import de.javagl.jgltf.model.GltfConstants;
import de.javagl.jgltf.model.Optionals;
import de.javagl.jgltf.model.impl.creation.BufferStructureBuilder;
import de.javagl.jgltf.model.io.Buffers;
import edu.cmu.cs.dennisc.java.util.BufferUtilities;
import edu.cmu.cs.dennisc.scenegraph.BlendShape;
import edu.cmu.cs.dennisc.scenegraph.Geometry;
import edu.cmu.cs.dennisc.scenegraph.InverseAbsoluteTransformationWeightsPair;
import edu.cmu.cs.dennisc.scenegraph.SkeletonVisual;
import edu.cmu.cs.dennisc.scenegraph.WeightInfo;
import edu.cmu.cs.dennisc.scenegraph.WeightedMesh;
import org.alice.math.immutable.AffineMatrix4x4;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Builds GLTF meshes, skins, and morph targets.
 * Extracted from {@link JointedModelGltfExporter}.
 */
class GltfMeshBuilder {

  private static final int INDICES_COMPONENT_TYPE = GltfConstants.GL_UNSIGNED_SHORT;

  private final SkeletonVisual visual;
  private final Map<Integer, Integer> textureMaterialMap;
  final Map<String, Integer> meshSkinMap;
  private final Map<String, String> renamedJoints;

  GltfMeshBuilder(SkeletonVisual visual, Map<Integer, Integer> textureMaterialMap,
                  Map<String, Integer> meshSkinMap, Map<String, String> renamedJoints) {
    this.visual = visual;
    this.textureMaterialMap = textureMaterialMap;
    this.meshSkinMap = meshSkinMap;
    this.renamedJoints = renamedJoints;
  }

  List<Mesh> createMeshes(BufferStructureBuilder bufferStructureBuilder, GlTF gltf, Map<String, Integer> jointNodes) {
    List<Mesh> meshes = new ArrayList<>();

    for (Geometry g : visual.geometries.getValue()) {
      if (g instanceof edu.cmu.cs.dennisc.scenegraph.Mesh sgMesh) {
        MeshPrimitive[] meshPrimitives = createMeshPrimitives(sgMesh, bufferStructureBuilder);
        addMeshes(meshes, sgMesh.getName(), meshPrimitives);
      }
    }
    for (WeightedMesh sgWM : visual.weightedMeshes.getValue()) {
      Skin skin = addSkin(sgWM, bufferStructureBuilder, gltf, jointNodes);
      MeshPrimitive[] meshPrimitives = createWeightedMeshPrimitives(sgWM, bufferStructureBuilder, jointNodes, skin);
      addMeshes(meshes, sgWM.getName(), meshPrimitives);
    }
    return meshes;
  }

  private void addAnyMorphTargets(MeshPrimitive meshPrimitive, edu.cmu.cs.dennisc.scenegraph.Mesh sgMesh, BufferStructureBuilder builder) {
    for (BlendShape blend : visual.blendShapes.getOrDefault(sgMesh, Collections.emptyList())) {
      Map<String, Integer> targets = new HashMap<>();

      int positionsAccessorIndex = builder.getNumAccessorModels();
      DoubleBuffer vertexBuffer = blend.vertexBuffer;
      vertexBuffer.rewind();
      FloatBuffer objVertices = BufferUtilities.createDirectFloatBuffer(GltfBufferUtils.convertToFloatArray(vertexBuffer));
      builder.createAccessorModel("positionsAccessor_" + positionsAccessorIndex,
          GltfConstants.GL_FLOAT, "VEC3", Buffers.createByteBufferFrom(objVertices));
      builder.createArrayBufferViewModel(sgMesh.getName() + blend.index + "_positions_bufferView");
      targets.put("POSITION", positionsAccessorIndex);

      FloatBuffer normalBuffer = blend.normalBuffer;
      normalBuffer.rewind();
      FloatBuffer objNormals = BufferUtilities.copyFloatBuffer(normalBuffer);
      if (objNormals.hasRemaining()) {
        GltfBufferUtils.normalize(objNormals);
        int normalsAccessorIndex = builder.getNumAccessorModels();
        builder.createAccessorModel("normalsAccessor_" + normalsAccessorIndex,
            GltfConstants.GL_FLOAT, "VEC3", Buffers.createByteBufferFrom(objNormals));
        builder.createArrayBufferViewModel(sgMesh.getName() + blend.index + "_normals_bufferView");
        targets.put("NORMAL", normalsAccessorIndex);
      }
      meshPrimitive.addTargets(targets);
    }
  }

  private void addMeshes(List<Mesh> meshes, String prefix, MeshPrimitive[] meshPrimitives) {
    boolean hasMultiplePrimitives = meshPrimitives.length > 1;
    for (int i = 0, meshPrimitivesLength = meshPrimitives.length; i < meshPrimitivesLength; i++) {
      Mesh mesh = new Mesh();
      mesh.addPrimitives(meshPrimitives[i]);
      String subMeshName = prefix;
      if (hasMultiplePrimitives) {
        subMeshName = prefix + "_" + i;
        meshSkinMap.put(subMeshName, meshSkinMap.get(prefix));
      }
      mesh.setName(subMeshName);
      meshes.add(mesh);
    }
  }

  private MeshPrimitive[] createMeshPrimitives(edu.cmu.cs.dennisc.scenegraph.Mesh sgMesh, BufferStructureBuilder builder) {
    List<Integer> referencedTextureIds = sgMesh.getReferencedTextureIds();
    MeshPrimitive[] result = new MeshPrimitive[referencedTextureIds.size()];
    boolean hasMultipleTextureIds = referencedTextureIds.size() > 1;

    int texCoordsAccessorIndex = -1;
    int normalsAccessorIndex = -1;

    // Add the vertices (positions) from the mesh to the buffer structure
    int positionsAccessorIndex = builder.getNumAccessorModels();
    DoubleBuffer vertexBuffer = sgMesh.vertexBuffer.getValue();
    vertexBuffer.rewind();
    FloatBuffer objVertices = BufferUtilities.createDirectFloatBuffer(GltfBufferUtils.convertToFloatArray(vertexBuffer));
    builder.createAccessorModel("positionsAccessor_" + positionsAccessorIndex,
            GltfConstants.GL_FLOAT, "VEC3", Buffers.createByteBufferFrom(objVertices));
    builder.createArrayBufferViewModel(sgMesh.getName() + "_positions_bufferView");

    // Add the texture coordinates from the mesh to the buffer structure
    FloatBuffer objTexCoords = sgMesh.textCoordBuffer.getValue();
    objTexCoords.rewind();
    if (objTexCoords.hasRemaining()) {
      texCoordsAccessorIndex = builder.getNumAccessorModels();
      builder.createAccessorModel("texCoordsAccessor_" + texCoordsAccessorIndex,
              GltfConstants.GL_FLOAT, "VEC2", Buffers.createByteBufferFrom(objTexCoords));
      builder.createArrayBufferViewModel(sgMesh.getName() + "_textCoords_bufferView");
    }

    // Add the normals from the mesh to the buffer structure
    FloatBuffer normalBuffer = sgMesh.normalBuffer.getValue();
    normalBuffer.rewind();
    FloatBuffer objNormals = BufferUtilities.copyFloatBuffer(normalBuffer);
    if (objNormals.hasRemaining()) {
      GltfBufferUtils.normalize(objNormals);
      normalsAccessorIndex = builder.getNumAccessorModels();
      builder.createAccessorModel("normalsAccessor_" + normalsAccessorIndex,
              GltfConstants.GL_FLOAT, "VEC3", Buffers.createByteBufferFrom(objNormals));
      builder.createArrayBufferViewModel(sgMesh.getName() + "_normals_bufferView");
    }

    int i = 0;

    for (Integer textureId : referencedTextureIds) {
      result[i] = createMeshPrimitive(sgMesh, builder, textureId, hasMultipleTextureIds, positionsAccessorIndex, texCoordsAccessorIndex, normalsAccessorIndex);
      i++;
    }

    return result;
  }

  private MeshPrimitive createMeshPrimitive(edu.cmu.cs.dennisc.scenegraph.Mesh sgMesh, BufferStructureBuilder builder, Integer textureId, boolean hasMultipleTextureIds, int positionsAccessorIndex, int texCoordsAccessorIndex, int normalsAccessorIndex) {
    MeshPrimitive meshPrimitive = new MeshPrimitive();
    meshPrimitive.setMode(GltfConstants.GL_TRIANGLES);

    if (textureMaterialMap.containsKey(textureId)) {
      meshPrimitive.setMaterial(textureMaterialMap.get(textureId));
    } else {
      System.err.println("Missing material for texture id " + textureId);
    }

    // Add the indices data from the mesh to the buffer structure
    int indicesAccessorIndex = builder.getNumAccessorModels();
    IntBuffer indexBuffer = sgMesh.indexBuffer.getValue();
    // Filter indices based on textureId if the mesh has multiple textures
    IntBuffer filteredIndexBuffer = hasMultipleTextureIds ? filterIndexBufferByTextureId(indexBuffer, textureId, sgMesh.textureIdArray) : indexBuffer;
    filteredIndexBuffer.rewind();
    builder.createAccessorModel("indicesAccessor_" + indicesAccessorIndex,
            INDICES_COMPONENT_TYPE, "SCALAR", Buffers.castToShortByteBuffer(filteredIndexBuffer));
    builder.createArrayElementBufferViewModel(sgMesh.getName() + "_indices_bufferView");

    meshPrimitive.setIndices(indicesAccessorIndex);
    meshPrimitive.addAttributes("POSITION", positionsAccessorIndex);
    meshPrimitive.addAttributes("TEXCOORD_0", texCoordsAccessorIndex);
    meshPrimitive.addAttributes("NORMAL", normalsAccessorIndex);
    addAnyMorphTargets(meshPrimitive, sgMesh, builder);

    return meshPrimitive;
  }

  private IntBuffer filterIndexBufferByTextureId(IntBuffer indexBuffer, Integer textureId, ArrayList<Integer> textureIdArray) {
    indexBuffer.rewind();

    // We don't know how many indices we are going to end up with so just allocate a buffer large enough to fit them all
    IntBuffer filteredBuffer = ByteBuffer.allocateDirect((Integer.SIZE / 8) * indexBuffer.remaining()).order(ByteOrder.nativeOrder()).asIntBuffer();

    while (indexBuffer.hasRemaining()) {
      int index = indexBuffer.get();
      int indexTextureId = textureIdArray.get(index);
      if (indexTextureId == textureId) {
        filteredBuffer.put(index);
      }
    }

    filteredBuffer.flip();

    // Resize the buffer to just the valid entries. JGLTF buffer operations assume limit() == capacity().
    return BufferUtilities.copyIntBuffer(filteredBuffer);
  }

  private static class JointWeightQuad {
    int[] jointData;
    float[] weightData;
    int offset;

    JointWeightQuad(int length, int offset) {
      jointData = new int[4 * length];
      weightData = new float[4 * length];
      this.offset = offset;
    }
  }

  private MeshPrimitive[] createWeightedMeshPrimitives(WeightedMesh sgWM, BufferStructureBuilder builder, Map<String, Integer> jointNodes, Skin skin) {
    MeshPrimitive[] meshPrimitives = createMeshPrimitives(sgWM, builder);
    JointedModelGltfExporter.VertexWeights[] vertexWeights = computeWeightsByVertex(sgWM, jointNodes, skin, renamedJoints);
    int highestJointCount = computeHighestJointCount(vertexWeights);
    Integer[] orderedIndices = increasingArray(highestJointCount);
    int quadCount = (highestJointCount + 3) / 4;
    JointWeightQuad[] quads = new JointWeightQuad[quadCount];
    for (int i = 0; i < quadCount; i++) {
      quads[i] = new JointWeightQuad(vertexWeights.length, i);
    }
    for (int i = 0; i < vertexWeights.length; i++) {
      JointedModelGltfExporter.VertexWeights vertexWeight = vertexWeights[i];
      if (null == vertexWeight) {
        continue;
      }
      List<Integer> joints = vertexWeight.jointIndices;
      List<Float> weights = vertexWeight.weights;
      int jointCount = joints.size();
      Integer[] indexRemapping = jointCount > 4 ? indicesInDecreasingOrder(weights) : orderedIndices;
      for (int j = 0; j < jointCount; j++) {
        final JointWeightQuad quad = quads[j / 4];
        int index = 4 * i + j % 4;
        quad.jointData[index] = joints.get(indexRemapping[j]);
        quad.weightData[index] = weights.get(indexRemapping[j]);
      }
    }
    for (int i = 0; i < quadCount; i++) {
      buildJointWeightBuffers(sgWM, builder, meshPrimitives, quads[i]);
    }
    return meshPrimitives;
  }

  private static Integer[] indicesInDecreasingOrder(List<Float> values) {
    Integer[] indexes = increasingArray(values.size());
    Comparator<Integer> comparator = Comparator.comparing(values::get).reversed();
    Arrays.sort(indexes, comparator);
    return indexes;
  }

  private static Integer[] increasingArray(int length) {
    Integer[] indexes = new Integer[length];
    for (int i = 0; i < length; i++) {
      indexes[i] = i;
    }
    return indexes;
  }

  private static int computeHighestJointCount(JointedModelGltfExporter.VertexWeights[] vertexWeights) {
    int highestJointCount = 0;

    for (JointedModelGltfExporter.VertexWeights vertexWeight : vertexWeights) {
      if (null == vertexWeight) {
        continue;
      }

      int numJoints = vertexWeight.jointIndices.size();

      if (numJoints > highestJointCount) {
        highestJointCount = numJoints;
      }
    }

    return highestJointCount;
  }

  private void buildJointWeightBuffers(WeightedMesh sgWM, BufferStructureBuilder builder, MeshPrimitive[] meshPrimitives, JointWeightQuad quad) {
    int jointsAccessorIndex = builder.getNumAccessorModels();
    IntBuffer joints = BufferUtilities.createDirectIntBuffer(quad.jointData);
    builder.createAccessorModel("jointsAccessor_" + jointsAccessorIndex,
                                GltfConstants.GL_UNSIGNED_SHORT, "VEC4", Buffers.castToShortByteBuffer(joints));
    builder.createArrayBufferViewModel(sgWM.getName() + "_joints_bufferView" + quad.offset);

    int weightsAccessorIndex = builder.getNumAccessorModels();
    FloatBuffer weights = BufferUtilities.createDirectFloatBuffer(quad.weightData);
    builder.createAccessorModel("weightsAccessor_" + weightsAccessorIndex,
                                GltfConstants.GL_FLOAT, "VEC4", Buffers.createByteBufferFrom(weights));
    builder.createArrayBufferViewModel(sgWM.getName() + "_weights_bufferView" + quad.offset);

    for (MeshPrimitive meshPrimitive : meshPrimitives) {
      meshPrimitive.addAttributes("JOINTS_" + quad.offset, jointsAccessorIndex);
      meshPrimitive.addAttributes("WEIGHTS_" + quad.offset, weightsAccessorIndex);
    }
  }

  static JointedModelGltfExporter.VertexWeights[] computeWeightsByVertex(
      WeightedMesh sgWM, Map<String, Integer> jointNodes, Skin skin, Map<String, String> renamedJoints) {
    WeightInfo wi = sgWM.weightInfo.getValue();
    int vertexCount = sgWM.vertexBuffer.getValue().limit() / 3;
    JointedModelGltfExporter.VertexWeights[] skinWeights = new JointedModelGltfExporter.VertexWeights[vertexCount];
    final Map<String, InverseAbsoluteTransformationWeightsPair> weightsPairMap = wi.getMap();
    for (Map.Entry<String, Integer> jointEntry : jointNodes.entrySet()) {
      InverseAbsoluteTransformationWeightsPair iatwp = weightsPairMap.get(getAliceJointIdentifier(jointEntry.getKey(), renamedJoints));
      if (iatwp == null) {
        continue;
      }
      InverseAbsoluteTransformationWeightsPair.WeightIterator weightIterator = iatwp.getIterator();
      while (weightIterator.hasNext()) {
        int vertexIndex = weightIterator.getIndex();
        if (skinWeights[vertexIndex] == null) {
          skinWeights[vertexIndex] = new JointedModelGltfExporter.VertexWeights();
        }
        final Integer jointIndex = jointEntry.getValue();
        int skinJointIndex = skin.getJoints().indexOf(jointIndex);
        skinWeights[vertexIndex].addJointWeight(skinJointIndex, weightIterator.next());
      }
    }
    return skinWeights;
  }

  private Skin addSkin(WeightedMesh sgWM, BufferStructureBuilder builder, GlTF gltf, Map<String, Integer> jointNodes) {
    Skin skin = new Skin();
    // Add the Inverse Bind Matrices from the mesh to the buffer structure
    int ibmIndex = builder.getNumAccessorModels();
    final Set<Map.Entry<String, InverseAbsoluteTransformationWeightsPair>> entries = sgWM.weightInfo.getValue().getMap().entrySet();
    float[] matrices = new float[16 * entries.size()];
    float[] matrix = new float[16];
    int index = 0;
    for (Map.Entry<String, InverseAbsoluteTransformationWeightsPair> entry : entries) {
      skin.addJoints(jointNodes.get(getUserJointIdentifier(entry.getKey())));
      AffineMatrix4x4 inverseBindMatrix = entry.getValue().getInverseAbsoluteTransformation();
      inverseBindMatrix.writeColumnMajorArray16(matrix);
      System.arraycopy(matrix, 0, matrices, index, 16);
      index += 16;
    }

    FloatBuffer ibmBuffer = BufferUtilities.createDirectFloatBuffer(matrices);
    builder.createAccessorModel("ibmsAccessor_" + ibmIndex, GltfConstants.GL_FLOAT, "MAT4", Buffers.createByteBufferFrom(ibmBuffer));

    builder.createBufferViewModel(sgWM.getName() + "_skin_bufferView", null);

    skin.setInverseBindMatrices(ibmIndex);
    meshSkinMap.put(sgWM.getName(), Optionals.of(gltf.getSkins()).size());
    gltf.addSkins(skin);
    return skin;
  }

  private String getUserJointIdentifier(String jointIdentifier) {
    return renamedJoints.getOrDefault(jointIdentifier, jointIdentifier);
  }

  private static String getAliceJointIdentifier(String userJointIdentifier, Map<String, String> renamedJoints) {
    return renamedJoints.entrySet().stream()
        .filter(entry -> entry.getValue().equals(userJointIdentifier)).findFirst().map(Map.Entry::getKey)
        .orElse(userJointIdentifier);
  }
}
