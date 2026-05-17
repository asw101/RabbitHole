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

import edu.cmu.cs.dennisc.color.Color4f;
import edu.cmu.cs.dennisc.java.util.BufferUtilities;
import edu.cmu.cs.dennisc.java.util.logging.Logger;
import edu.cmu.cs.dennisc.scenegraph.*;
import edu.cmu.cs.dennisc.texture.BufferedImageTexture;
import org.alice.math.immutable.AffineMatrix4x4;
import org.lgna.story.implementation.alice.AliceResourceClassUtilities;
import org.lgna.story.resources.JointId;
import org.lgna.story.resources.JointedModelResource;

import java.awt.image.BufferedImage;
import java.util.*;

/**
 * Export orchestration: builds skeleton hierarchy, loads textures, and
 * assembles meshes from JNI data via Model's package-private accessors.
 * Owns the Material enum (sole consumer).
 */
class SkeletonExporter {

  enum Material {
    NONE(0, null),
    SIMPLE_TEXTURE(1, new Color4f(1f, 1f, 1f, 1f)),
    GLASS(2, new Color4f(179f / 255f, 223f / 255f, 242f / 255f, 128f / 255f)),
    METAL(3, new Color4f(150f / 255f, 150f / 255f, 160f / 255f, 1f)),
    STONE(4, new Color4f(135f / 255f, 130f / 255f, 130f / 255f, 1f));

    private final int value;
    final Color4f color;
    Material(int value, Color4f color) {
      this.value = value;
      this.color = color;
    }

    private static final Material[] BY_VALUE;
    static {
      int max = 0;
      for (Material m : values()) {
        max = Math.max(max, m.value);
      }
      BY_VALUE = new Material[max + 1];
      for (Material m : values()) {
        BY_VALUE[m.value] = m;
      }
    }

    static Material getMaterialForValue(int value) {
      return (value >= 0 && value < BY_VALUE.length) ? BY_VALUE[value] : null;
    }
  }

  private SkeletonExporter() {
  }

  static SkeletonVisual export(Model model, JointedModelResource resource) {
    model.doPrepareForExporting();

    List<JointId> resourceJointIds = AliceResourceClassUtilities.getJoints(resource.getClass());
    Joint skeleton = createSkeleton(model, resourceJointIds);

    List<TexturedAppearance> textures = new ArrayList<>();
    Map<String, Integer> textureNameToIdMap = new HashMap<>();
    String[] textureIds = model.fetchTextureIds();
    int idCount = 0;
    for (String textureId : textureIds) {
      int materialType = model.fetchMaterialType(textureId);
      Material material = Material.getMaterialForValue(materialType);
      if (material != null) {
        TexturedAppearance texturedAppearance = new TexturedAppearance();
        texturedAppearance.diffuseColor.setValue(material.color);
        texturedAppearance.textureId.setValue(idCount);
        if (material == Material.SIMPLE_TEXTURE) {
          int width = model.fetchImageWidth(textureId);
          int height = model.fetchImageHeight(textureId);
          int bytesPerPixel = model.fetchBytesPerPixel(textureId);
          byte[] imageData = new byte[width * height * bytesPerPixel];
          model.fetchImageData(textureId, imageData);
          BufferedImage bufferedImage = NebulousTexture.createBufferedImageFromNebulousData(imageData, width, height, bytesPerPixel);
          BufferedImageTexture bufferedImageTexture = new BufferedImageTexture();
          bufferedImageTexture.setBufferedImage(bufferedImage);
          texturedAppearance.diffuseColorTexture.setValue(bufferedImageTexture);
        }
        textures.add(texturedAppearance);
      }
      textureNameToIdMap.put(textureId, idCount);
      idCount++;
    }

    List<Mesh> unWeightedMeshes = new ArrayList<>();
    List<WeightedMesh> weightedMeshes = new ArrayList<>();
    List<String> unweightedMeshIds = new ArrayList<>(Arrays.asList(model.fetchUnweightedMeshIds()));
    String[] weightedMeshIds = model.fetchWeightedMeshIds();
    for (String meshId : weightedMeshIds) {
      if (isActuallyWeightedToJoints(model, meshId, resourceJointIds)) {
        WeightedMesh mesh = new WeightedMesh();
        mesh.setName(meshId);
        initializeMesh(model, meshId, mesh, resourceJointIds, textureNameToIdMap);
        weightedMeshes.add(mesh);
      } else {
        unweightedMeshIds.add(meshId);
      }
    }
    for (String meshId : unweightedMeshIds) {
      Mesh mesh = new Mesh();
      initializeMesh(model, meshId, mesh, resourceJointIds, textureNameToIdMap);
      unWeightedMeshes.add(mesh);
    }

    SkeletonVisual skeletonVisual = new SkeletonVisual();
    skeletonVisual.setName(model.fetchName());
    skeletonVisual.frontFacingAppearance.setValue(new SimpleAppearance());
    skeletonVisual.skeleton.setValue(skeleton);
    skeletonVisual.geometries.setValue(unWeightedMeshes.toArray(new Mesh[0]));
    skeletonVisual.weightedMeshes.setValue(weightedMeshes.toArray(new WeightedMesh[0]));
    skeletonVisual.textures.setValue(textures.toArray(new TexturedAppearance[0]));

    return skeletonVisual;
  }

  private static Joint createSkeleton(Model model, List<JointId> resourceJointIds) {
    Joint rootJoint = null;
    resourceJointIds.sort(Comparator.comparingInt(JointId::hierarchyDepth));
    Map<String, Joint> jointById = new HashMap<>();
    for (JointId currentJointId : resourceJointIds) {
      Joint j = new Joint();
      String jointIdStr = currentJointId.toString();
      j.jointID.setValue(jointIdStr);
      j.setName(jointIdStr);
      j.localTransformation.setValue(model.getOriginalTransformationForJoint(currentJointId));
      jointById.put(jointIdStr, j);
      if (currentJointId.getParent() == null) {
        rootJoint = j;
      } else {
        Joint parent = jointById.get(currentJointId.getParent().toString());
        if (parent != null) {
          j.setParent(parent);
        }
      }
    }
    return rootJoint;
  }

  private static boolean isActuallyWeightedToJoints(Model model, String weightedMeshId, List<JointId> resourceJointIds) {
    for (JointId jointId : resourceJointIds) {
      if (model.checkMeshWeightedToJoint(weightedMeshId, jointId.toString())) {
        return true;
      }
    }
    return false;
  }

  private static void initializeMesh(Model model, String meshId, Mesh mesh,
                                     List<JointId> resourceJointIds, Map<String, Integer> textureNamesToIds) {
    String[] meshTextureIds = model.getTextureIdsForMesh(meshId);
    float[] vertices = (mesh instanceof WeightedMesh) ? model.fetchVerticesForMesh(meshId) : model.fetchUnweightedVerticesForMesh(meshId);
    float[] normals = (mesh instanceof WeightedMesh) ? model.fetchNormalsForMesh(meshId) : model.fetchUnweightedNormalsForMesh(meshId);
    float[] uvs = model.fetchUvsForMesh(meshId);

    Map<String, int[]> textureIdToIndices = new HashMap<>();
    for (String textureId : meshTextureIds) {
      int[] indices = model.getIndicesForMesh(meshId, textureId);
      textureIdToIndices.put(textureId, indices);
    }

    RemappedMeshData meshData = MeshBuilder.remapIndices(textureIdToIndices, vertices, normals, uvs);

    mesh.textureIdArray.ensureCapacity(meshData.indices.length);
    for (String texId : meshData.textureIdsPerIndex) {
      mesh.textureIdArray.add(textureNamesToIds.get(texId));
    }

    mesh.normalBuffer.setValue(BufferUtilities.createDirectFloatBuffer(meshData.normals));
    mesh.vertexBuffer.setValue(BufferUtilities.createDirectDoubleBuffer(meshData.vertices));
    mesh.textCoordBuffer.setValue(BufferUtilities.createDirectFloatBuffer(meshData.uvs));
    mesh.indexBuffer.setValue(BufferUtilities.createDirectIntBuffer(meshData.indices));
    mesh.textureId.setValue(textureNamesToIds.get(meshTextureIds[0]));

    if (mesh instanceof WeightedMesh weightedMesh) {
      WeightInfo weightInfo = createWeightInfo(model, meshId, resourceJointIds, meshData);
      weightedMesh.weightInfo.setValue(weightInfo);
    }
  }

  private static WeightInfo createWeightInfo(Model model, String meshId,
                                             List<JointId> resourceJointIds, RemappedMeshData meshData) {
    WeightInfo weightInfo = new WeightInfo();
    for (JointId joint : resourceJointIds) {
      if (model.checkMeshWeightedToJoint(meshId, joint.toString())) {
        double[] inverseAbsTransform = model.fetchInvAbsTrans(meshId, joint.toString());
        float[] vertexWeights = model.fetchVertexWeights(meshId, joint.toString(),
            joint.getParent() == null ? "" : joint.getParent().toString());

        logUnmappedVertices(model, meshId, vertexWeights, meshData.oldVertexIndexToNewIndex);

        float[] remappedWeights = MeshBuilder.remapWeights(vertexWeights,
            meshData.newIndexToOldVertex);

        AffineMatrix4x4 aliceInverseBindMatrix = AffineMatrix4x4.createFromColumnMajorArray12(inverseAbsTransform);
        InverseAbsoluteTransformationWeightsPair iawp =
            InverseAbsoluteTransformationWeightsPair.createInverseAbsoluteTransformationWeightsPair(
                remappedWeights, aliceInverseBindMatrix);
        if (iawp != null) {
          weightInfo.addReference(joint.toString(), iawp);
        }
      }
    }
    return weightInfo;
  }

  private static void logUnmappedVertices(Model model, String meshId,
                                          float[] vertexWeights, int[] oldVertexIndexToNewIndex) {
    for (int i = 0; i < vertexWeights.length; i++) {
      if (i >= oldVertexIndexToNewIndex.length || oldVertexIndexToNewIndex[i] == -1) {
        StringBuilder sb = new StringBuilder();
        sb.append("Model data will be missing some points\n\t");
        model.buildRepr(sb);
        sb.append("\n\tMesh ").append(meshId)
          .append(" has no mapping from old vertex ")
          .append(i)
          .append(" to a new one in the export");
        Logger.warning(sb);
      }
    }
  }
}
