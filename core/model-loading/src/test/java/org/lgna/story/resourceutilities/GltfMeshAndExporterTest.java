package org.lgna.story.resourceutilities;

import de.javagl.jgltf.impl.v2.Asset;
import de.javagl.jgltf.impl.v2.GlTF;
import de.javagl.jgltf.impl.v2.MeshPrimitive;
import de.javagl.jgltf.impl.v2.Node;
import de.javagl.jgltf.impl.v2.Skin;
import de.javagl.jgltf.model.impl.creation.BufferStructure;
import de.javagl.jgltf.model.impl.creation.BufferStructureBuilder;
import de.javagl.jgltf.model.io.v2.GltfAssetV2;
import edu.cmu.cs.dennisc.java.util.zip.DataSource;
import edu.cmu.cs.dennisc.scenegraph.BlendShape;
import edu.cmu.cs.dennisc.scenegraph.InverseAbsoluteTransformationWeightsPair;
import edu.cmu.cs.dennisc.scenegraph.Joint;
import edu.cmu.cs.dennisc.scenegraph.SkeletonVisual;
import edu.cmu.cs.dennisc.scenegraph.TexturedAppearance;
import edu.cmu.cs.dennisc.scenegraph.WeightInfo;
import edu.cmu.cs.dennisc.scenegraph.WeightedMesh;
import org.alice.math.immutable.AffineMatrix4x4;
import org.junit.Test;

import java.lang.reflect.Method;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class GltfMeshAndExporterTest {
  @Test
  public void meshBuilderCreatesMeshesSkinsMorphTargetsAndWeightAttributes() throws Exception {
    SkeletonVisual visual = createVisual();
    JointedModelGltfExporter exporter = new JointedModelGltfExporter(visual, null, "robot", "resource/path", Collections.emptyMap());
    GlTF gltf = new GlTF();
    Map<String, Integer> jointNodes = invokeAddSkeletonNodes(exporter, gltf);

    Map<Integer, Integer> textureMaterialMap = new HashMap<>();
    textureMaterialMap.put(0, 10);
    textureMaterialMap.put(1, 11);
    textureMaterialMap.put(2, 12);

    Map<String, Integer> meshSkinMap = new HashMap<>();
    GltfMeshBuilder meshBuilder = new GltfMeshBuilder(visual, textureMaterialMap, meshSkinMap, Collections.emptyMap());
    BufferStructureBuilder bufferBuilder = new BufferStructureBuilder();

    List<de.javagl.jgltf.impl.v2.Mesh> meshes = meshBuilder.createMeshes(bufferBuilder, gltf, jointNodes);

    assertEquals(3, meshes.size());
    assertEquals(Arrays.asList("staticMesh_0", "staticMesh_1", "weightedMesh"), meshNames(meshes));
    assertEquals(1, gltf.getSkins().size());
    assertEquals(Integer.valueOf(0), meshSkinMap.get("weightedMesh"));

    de.javagl.jgltf.impl.v2.Mesh weightedMesh = findMesh(meshes, "weightedMesh");
    MeshPrimitive primitive = weightedMesh.getPrimitives().get(0);
    assertEquals(Integer.valueOf(12), primitive.getMaterial());
    assertTrue(primitive.getAttributes().containsKey("POSITION"));
    assertTrue(primitive.getAttributes().containsKey("NORMAL"));
    assertTrue(primitive.getAttributes().containsKey("TEXCOORD_0"));
    assertTrue(primitive.getAttributes().containsKey("JOINTS_0"));
    assertTrue(primitive.getAttributes().containsKey("WEIGHTS_0"));
    assertTrue(primitive.getAttributes().containsKey("JOINTS_1"));
    assertTrue(primitive.getAttributes().containsKey("WEIGHTS_1"));
    assertEquals(1, primitive.getTargets().size());
    assertTrue(primitive.getTargets().get(0).containsKey("POSITION"));
    assertTrue(primitive.getTargets().get(0).containsKey("NORMAL"));

    Skin skin = gltf.getSkins().get(0);
    assertEquals(6, skin.getJoints().size());
  }

  @Test
  public void meshBuilderAndExporterCoverHelperPaths() throws Exception {
    SkeletonVisual visual = createVisual();
    Map<String, String> renamedJoints = new HashMap<>();
    renamedJoints.put("ROOT", "ROOT_ALIAS");

    JointedModelGltfExporter exporter = new JointedModelGltfExporter(visual, null, "robot", "resource/path", renamedJoints);
    Asset asset = invokeCreateAssetDetail();
    assertEquals("Alice3 Exporter", asset.getGenerator());
    assertEquals("2.0", asset.getVersion());

    Node rootNode = invokeCreateJointNode(exporter, visual.skeleton.getValue());
    assertEquals("ROOT_ALIAS", rootNode.getName());

    GlTF gltf = new GlTF();
    Map<String, Integer> jointNodes = invokeAddSkeletonNodes(exporter, gltf);
    assertTrue(jointNodes.containsKey("ROOT_ALIAS"));

    WeightInfo wi = visual.weightedMeshes.getValue()[0].weightInfo.getValue();
    Skin skin = new Skin();
    skin.setJoints(new ArrayList<>(jointNodes.values()));
    JointedModelGltfExporter.VertexWeights[] weightsByVertex = exporter.getWeightsByVertex(visual.weightedMeshes.getValue()[0], jointNodes, skin);
    assertNotNull(weightsByVertex[0]);
    assertTrue(weightsByVertex[0].jointIndices.size() >= 4);

    assertArrayEquals(new float[]{1.0f, 2.0f, 3.0f},
        JointedModelGltfExporter.convertToFloatArray(DoubleBuffer.wrap(new double[]{1.0, 2.0, 3.0})), 0.0f);

    DataSource structureDataSource = ModelExportDataSources.create("resource/path/model.glb", os -> os.write(new byte[0]));
    assertEquals("model.glb", exporter.getStructureFileName(structureDataSource));
    assertEquals("png", exporter.getStructureExtension());
    exporter.addImageDataSources(new ArrayList<DataSource>(), null, new HashMap<Integer, String>());

    SkeletonVisual missingMaterialVisual = new SkeletonVisual();
    missingMaterialVisual.skeleton.setValue(visual.skeleton.getValue());
    missingMaterialVisual.geometries.setValue(new edu.cmu.cs.dennisc.scenegraph.Geometry[]{createStaticMesh()});
    missingMaterialVisual.weightedMeshes.setValue(new WeightedMesh[0]);
    missingMaterialVisual.textures.setValue(new TexturedAppearance[]{texture(99)});

    GltfMeshBuilder builder = new GltfMeshBuilder(missingMaterialVisual, Collections.emptyMap(), new HashMap<String, Integer>(), Collections.emptyMap());
    List<de.javagl.jgltf.impl.v2.Mesh> meshes = builder.createMeshes(new BufferStructureBuilder(), new GlTF(), jointNodes);
    assertNull(meshes.get(0).getPrimitives().get(0).getMaterial());

    Path imageDir = Paths.get("target", "gltf-exporter-helper-test");
    Files.createDirectories(imageDir);
    GltfAssetV2 gltfAsset = new GltfAssetV2(new GlTF(), null);
    invokeResolveImages(exporter, imageDir, gltfAsset);
    BufferStructureBuilder bufferStructureBuilder = new BufferStructureBuilder();
    bufferStructureBuilder.createBufferModel("buffer0", "buffer0.bin");
    BufferStructure bufferStructure = bufferStructureBuilder.build();
    invokeResolveBuffers(exporter, bufferStructure, gltfAsset);
    Files.deleteIfExists(imageDir);
  }

  private static SkeletonVisual createVisual() {
    SkeletonVisual visual = new SkeletonVisual();
    Joint root = joint("ROOT");
    Joint j1 = joint("JOINT1");
    Joint j2 = joint("JOINT2");
    Joint j3 = joint("JOINT3");
    Joint j4 = joint("JOINT4");
    Joint j5 = joint("JOINT5");
    j1.setParent(root);
    j2.setParent(root);
    j3.setParent(root);
    j4.setParent(root);
    j5.setParent(root);
    visual.skeleton.setValue(root);

    edu.cmu.cs.dennisc.scenegraph.Mesh staticMesh = createStaticMesh();
    WeightedMesh weightedMesh = createWeightedMesh(root);

    BlendShape blendShape = new BlendShape(0);
    blendShape.vertexBuffer = DoubleBuffer.wrap(new double[]{0.0, 0.0, 0.1, 1.0, 0.0, 0.1, 0.0, 1.0, 0.1});
    blendShape.normalBuffer = FloatBuffer.wrap(new float[]{0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f});
    visual.blendShapes.put(weightedMesh, Collections.singletonList(blendShape));

    visual.geometries.setValue(new edu.cmu.cs.dennisc.scenegraph.Geometry[]{staticMesh});
    visual.weightedMeshes.setValue(new WeightedMesh[]{weightedMesh});
    visual.textures.setValue(new TexturedAppearance[]{texture(0), texture(1), texture(2)});
    return visual;
  }

  private static edu.cmu.cs.dennisc.scenegraph.Mesh createStaticMesh() {
    edu.cmu.cs.dennisc.scenegraph.Mesh mesh = new edu.cmu.cs.dennisc.scenegraph.Mesh();
    mesh.setName("staticMesh");
    mesh.textureId.setValue(99);
    mesh.vertexBuffer.setValue(DoubleBuffer.wrap(new double[]{
        0.0, 0.0, 0.0,
        1.0, 0.0, 0.0,
        0.0, 1.0, 0.0,
        1.0, 1.0, 0.0,
        2.0, 1.0, 0.0,
        1.0, 2.0, 0.0
    }));
    mesh.normalBuffer.setValue(FloatBuffer.wrap(new float[]{
        0f, 0f, 1f, 0f, 0f, 1f, 0f, 0f, 1f,
        0f, 0f, 1f, 0f, 0f, 1f, 0f, 0f, 1f
    }));
    mesh.textCoordBuffer.setValue(FloatBuffer.wrap(new float[]{
        0f, 0f, 1f, 0f, 0f, 1f,
        0f, 0f, 1f, 0f, 0f, 1f
    }));
    mesh.indexBuffer.setValue(IntBuffer.wrap(new int[]{0, 1, 2, 3, 4, 5}));
    Collections.addAll(mesh.textureIdArray, 0, 0, 0, 1, 1, 1);
    return mesh;
  }

  private static WeightedMesh createWeightedMesh(Joint root) {
    WeightedMesh mesh = new WeightedMesh();
    mesh.setName("weightedMesh");
    mesh.textureId.setValue(2);
    mesh.skeleton.setValue(root);
    mesh.vertexBuffer.setValue(DoubleBuffer.wrap(new double[]{
        0.0, 0.0, 0.0,
        1.0, 0.0, 0.0,
        0.0, 1.0, 0.0
    }));
    mesh.normalBuffer.setValue(FloatBuffer.wrap(new float[]{
        0f, 0f, 1f,
        0f, 0f, 1f,
        0f, 0f, 1f
    }));
    mesh.textCoordBuffer.setValue(FloatBuffer.wrap(new float[]{
        0f, 0f,
        1f, 0f,
        0f, 1f
    }));
    mesh.indexBuffer.setValue(IntBuffer.wrap(new int[]{0, 1, 2}));

    WeightInfo weightInfo = new WeightInfo();
    weightInfo.addReference("ROOT", pair(0.10f, 0.0f, 0.0f));
    weightInfo.addReference("JOINT1", pair(0.20f, 0.30f, 0.0f));
    weightInfo.addReference("JOINT2", pair(0.25f, 0.0f, 0.0f));
    weightInfo.addReference("JOINT3", pair(0.15f, 0.0f, 0.0f));
    weightInfo.addReference("JOINT4", pair(0.17f, 0.0f, 0.0f));
    weightInfo.addReference("JOINT5", pair(0.13f, 0.0f, 0.0f));
    mesh.weightInfo.setValue(weightInfo);
    return mesh;
  }

  private static InverseAbsoluteTransformationWeightsPair pair(float a, float b, float c) {
    return InverseAbsoluteTransformationWeightsPair.createInverseAbsoluteTransformationWeightsPair(
        new float[]{a, b, c}, AffineMatrix4x4.IDENTITY);
  }

  private static Joint joint(String id) {
    Joint joint = new Joint();
    joint.jointID.setValue(id);
    joint.setName(id);
    joint.localTransformation.setValue(AffineMatrix4x4.IDENTITY);
    return joint;
  }

  private static TexturedAppearance texture(int textureId) {
    TexturedAppearance texture = new TexturedAppearance();
    texture.textureId.setValue(textureId);
    return texture;
  }

  private static List<String> meshNames(List<de.javagl.jgltf.impl.v2.Mesh> meshes) {
    List<String> names = new ArrayList<>();
    for (de.javagl.jgltf.impl.v2.Mesh mesh : meshes) {
      names.add(mesh.getName());
    }
    return names;
  }

  private static de.javagl.jgltf.impl.v2.Mesh findMesh(List<de.javagl.jgltf.impl.v2.Mesh> meshes, String name) {
    for (de.javagl.jgltf.impl.v2.Mesh mesh : meshes) {
      if (name.equals(mesh.getName())) {
        return mesh;
      }
    }
    fail("Missing mesh named " + name);
    return null;
  }

  @SuppressWarnings("unchecked")
  private static Map<String, Integer> invokeAddSkeletonNodes(JointedModelGltfExporter exporter, GlTF gltf) throws Exception {
    Method method = JointedModelGltfExporter.class.getDeclaredMethod("addSkeletonNodes", GlTF.class);
    method.setAccessible(true);
    return (Map<String, Integer>) method.invoke(exporter, gltf);
  }

  private static Node invokeCreateJointNode(JointedModelGltfExporter exporter, Joint joint) throws Exception {
    Method method = JointedModelGltfExporter.class.getDeclaredMethod("createJointNode", Joint.class);
    method.setAccessible(true);
    return (Node) method.invoke(exporter, joint);
  }

  private static Asset invokeCreateAssetDetail() throws Exception {
    Method method = JointedModelGltfExporter.class.getDeclaredMethod("createAssetDetail");
    method.setAccessible(true);
    return (Asset) method.invoke(null);
  }

  private static void invokeResolveImages(JointedModelGltfExporter exporter, Path directory, GltfAssetV2 asset) throws Exception {
    Method method = JointedModelGltfExporter.class.getDeclaredMethod("resolveImages", Path.class, GltfAssetV2.class);
    method.setAccessible(true);
    method.invoke(exporter, directory, asset);
  }

  private static void invokeResolveBuffers(JointedModelGltfExporter exporter, BufferStructure bufferStructure, GltfAssetV2 asset) throws Exception {
    Method method = JointedModelGltfExporter.class.getDeclaredMethod("resolveBuffers", BufferStructure.class, GltfAssetV2.class);
    method.setAccessible(true);
    method.invoke(exporter, bufferStructure, asset);
  }
}
