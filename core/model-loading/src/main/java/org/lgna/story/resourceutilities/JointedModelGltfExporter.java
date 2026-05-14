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

import de.javagl.jgltf.impl.v2.Asset;
import de.javagl.jgltf.impl.v2.GlTF;
import de.javagl.jgltf.impl.v2.Mesh;
import de.javagl.jgltf.impl.v2.Node;
import de.javagl.jgltf.impl.v2.Scene;
import de.javagl.jgltf.impl.v2.Skin;
import de.javagl.jgltf.model.GltfModel;
import de.javagl.jgltf.model.GltfModels;
import de.javagl.jgltf.model.Optionals;
import de.javagl.jgltf.model.impl.creation.BufferStructure;
import de.javagl.jgltf.model.impl.creation.BufferStructureBuilder;
import de.javagl.jgltf.model.impl.creation.BufferStructureGltfV2;
import de.javagl.jgltf.model.impl.creation.BufferStructures;
import de.javagl.jgltf.model.io.GltfModelWriter;
import de.javagl.jgltf.model.io.GltfReference;
import de.javagl.jgltf.model.io.GltfReferenceResolver;
import de.javagl.jgltf.model.io.v2.GltfAssetV2;
import edu.cmu.cs.dennisc.java.util.zip.DataSource;
import edu.cmu.cs.dennisc.scenegraph.Component;
import edu.cmu.cs.dennisc.scenegraph.Joint;
import edu.cmu.cs.dennisc.scenegraph.SkeletonVisual;
import edu.cmu.cs.dennisc.scenegraph.WeightedMesh;
import org.alice.math.immutable.AffineMatrix4x4;
import org.alice.tweedle.file.ModelManifest;
import org.lgna.project.io.JointedModelExporter;

import java.io.IOException;
import java.net.URI;
import java.nio.DoubleBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Exports a {@link SkeletonVisual} as a glTF binary (.glb) model.
 *
 * <p>Material/texture work is delegated to {@link GltfMaterialWriter},
 * mesh/skin/morph-target construction to {@link GltfMeshBuilder},
 * and buffer utilities to {@link GltfBufferUtils}.</p>
 */
public class JointedModelGltfExporter implements JointedModelExporter {

  private static final String MODEL_EXTENSION = "glb";
  private static final String IMAGE_EXTENSION = "png";

  private final SkeletonVisual visual;
  private final Map<String, String> renamedJoints;
  private final String resourcePath;
  private final String fullResourceName;

  public JointedModelGltfExporter(SkeletonVisual sv, ModelManifest.ModelVariant modelVariant, String modelName, String resourcePath, Map<String, String> renamedJoints) {
    this.visual = sv;
    this.resourcePath = resourcePath;
    this.renamedJoints = renamedJoints;
    this.fullResourceName = modelVariant == null ? modelName : modelVariant.textureSet;
  }

  @Override
  public DataSource createStructureDataSource() {
    final String name = resourcePath + "/" + fullResourceName + "." + MODEL_EXTENSION;
    return ModelExportDataSources.create(name, os -> {
      GltfModelWriter writer = new GltfModelWriter();
      final GltfModel model = createModel();
      writer.writeBinary(model, os);
    });
  }

  @Override
  public String getStructureFileName(DataSource structureDataSource) {
    return ModelExportDataSources.structureFileNameRelativeTo(resourcePath, structureDataSource);
  }

  @Override
  public String getStructureExtension() {
    return IMAGE_EXTENSION;
  }

  @Override
  public void addImageDataSources(List<DataSource> dataSources, ModelManifest modelManifest, Map<Integer, String> resourceMap) {
  }

  // ── Model assembly ──────────────────────────────────────────────────

  private static Asset createAssetDetail() {
    Asset asset = new Asset();
    asset.setGenerator("Alice3 Exporter");
    asset.setVersion("2.0");
    return asset;
  }

  protected GltfModel createModel() {
    GlTF gltf = new GlTF();
    gltf.setAsset(createAssetDetail());

    Node origin = new Node();
    origin.setName(fullResourceName);
    int originIndex = Optionals.of(gltf.getNodes()).size();
    gltf.addNodes(origin);

    Map<String, Integer> jointNodes = addSkeletonNodes(gltf);
    if (jointNodes.size() > 0) {
      // First node after origin will be root joint of skeleton
      origin.addChildren(originIndex + 1);
    }

    Path tempDir;
    Map<Integer, Integer> textureMaterialMap;
    try {
      tempDir = Files.createTempDirectory(null);
      GltfMaterialWriter materialWriter = new GltfMaterialWriter(fullResourceName);
      textureMaterialMap = materialWriter.createAndAddTextureComponents(tempDir, gltf, visual.textures.getValue());
    } catch (IOException e) {
      throw new RuntimeException("Failed to create temp directory and write model textures. Unable to complete export.", e);
    }

    HashMap<String, Integer> meshSkinMap = new HashMap<>();
    GltfMeshBuilder meshBuilder = new GltfMeshBuilder(visual, textureMaterialMap, meshSkinMap, renamedJoints);
    BufferStructureBuilder bufferStructureBuilder = new BufferStructureBuilder();

    final List<Mesh> meshes = meshBuilder.createMeshes(bufferStructureBuilder, gltf, jointNodes);

    int bufferCounter = bufferStructureBuilder.getNumBufferModels();
    String bufferName = fullResourceName + "buffer" + bufferCounter;
    String uri = bufferName + ".bin";
    bufferStructureBuilder.createBufferModel(bufferName, uri);

    // Transfer the data from the buffer structure into the glTF
    BufferStructure bufferStructure = bufferStructureBuilder.build();
    gltf.setAccessors(BufferStructureGltfV2.createAccessors(bufferStructure));
    gltf.setBufferViews(BufferStructureGltfV2.createBufferViews(bufferStructure));
    gltf.setBuffers(BufferStructureGltfV2.createBuffers(bufferStructure));

    for (Mesh mesh : meshes) {
      int meshIndex = Optionals.of(gltf.getMeshes()).size();
      int nodeIndex = Optionals.of(gltf.getNodes()).size();
      gltf.addMeshes(mesh);
      Node meshNode = new Node();
      meshNode.setName(mesh.getName());
      meshNode.setMesh(meshIndex);
      if (meshSkinMap.containsKey(mesh.getName())) {
        meshNode.setSkin(meshSkinMap.get(mesh.getName()));
      }
      origin.addChildren(nodeIndex);
      gltf.addNodes(meshNode);
    }

    Scene scene = new Scene();
    scene.setName(fullResourceName);
    scene.addNodes(originIndex);

    int sceneIndex = Optionals.of(gltf.getScenes()).size();
    gltf.addScenes(scene);
    gltf.setScene(sceneIndex);

    GltfAssetV2 gltfAsset = new GltfAssetV2(gltf, null);
    resolveImages(tempDir, gltfAsset);
    resolveBuffers(bufferStructure, gltfAsset);

    return GltfModels.create(gltfAsset);
  }

  private void resolveImages(Path tempDir, GltfAssetV2 _gltfAsset) {
    List<GltfReference> _refList = _gltfAsset.getImageReferences();
    URI _baseUri = tempDir.toAbsolutePath().toUri();
    GltfReferenceResolver.resolveAll(_refList, _baseUri);
  }

  private void resolveBuffers(BufferStructure bufferStructure, GltfAssetV2 gltfAsset) {
    List<GltfReference> bufferReferences = gltfAsset.getBufferReferences();
    BufferStructures.resolve(bufferReferences, bufferStructure);
  }

  // ── Skeleton traversal ──────────────────────────────────────────────

  private Map<String, Integer> addSkeletonNodes(GlTF gltf) {
    Joint rootJoint = visual.skeleton.getValue();
    Map<String, Integer> jointIndices = new HashMap<>();
    if (rootJoint != null) {
      addNodeForJoint(rootJoint, gltf, jointIndices);
    }
    return jointIndices;
  }

  private int addNodeForJoint(Joint joint, GlTF gltf, Map<String, Integer> jointIndices) {
    Node node = createJointNode(joint);
    int nodeIndex = Optionals.of(gltf.getNodes()).size();
    gltf.addNodes(node);
    jointIndices.put(node.getName(), nodeIndex);

    for (Component c : joint.getComponents()) {
      if (c instanceof Joint childJoint) {
        int childIndex = addNodeForJoint(childJoint, gltf, jointIndices);
        node.addChildren(childIndex);
      }
    }
    return nodeIndex;
  }

  private Node createJointNode(Joint joint) {
    Node node = new Node();
    node.setName(renamedJoints.getOrDefault(joint.jointID.getValue(), joint.jointID.getValue()));

    final AffineMatrix4x4 jointTransform = joint.localTransformation.getValue();
    float[] matrixValues = new float[16];
    jointTransform.writeColumnMajorArray16(matrixValues);
    node.setMatrix(matrixValues);
    return node;
  }

  // ── Public helpers (preserved for API compatibility) ─────────────────

  public static class VertexWeights {
    public final List<Integer> jointIndices = new ArrayList<>();
    public final List<Float> weights = new ArrayList<>();

    void addJointWeight(int jointIndex, float weight) {
      jointIndices.add(jointIndex);
      weights.add(weight);
    }
  }

  public VertexWeights[] getWeightsByVertex(WeightedMesh sgWM, Map<String, Integer> jointNodes, Skin skin) {
    return GltfMeshBuilder.computeWeightsByVertex(sgWM, jointNodes, skin, renamedJoints);
  }

  public static float[] convertToFloatArray(DoubleBuffer buf) {
    return GltfBufferUtils.convertToFloatArray(buf);
  }
}
