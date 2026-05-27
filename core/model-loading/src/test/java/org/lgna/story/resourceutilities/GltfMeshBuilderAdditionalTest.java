package org.lgna.story.resourceutilities;

import de.javagl.jgltf.impl.v2.GlTF;
import de.javagl.jgltf.impl.v2.MeshPrimitive;
import de.javagl.jgltf.impl.v2.Skin;
import de.javagl.jgltf.model.impl.creation.BufferStructureBuilder;
import edu.cmu.cs.dennisc.scenegraph.WeightedMesh;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class GltfMeshBuilderAdditionalTest {
  @Test
  public void computeWeightsByVertexTranslatesRenamedJointIdentifiers() {
    WeightedMesh weightedMesh = ExporterTestFixtures.createVisual(false).weightedMeshes.getValue()[0];
    Map<String, Integer> jointNodes = new LinkedHashMap<>();
    jointNodes.put("ROOT_ALIAS", 10);
    jointNodes.put("JOINT1_ALIAS", 11);
    jointNodes.put("JOINT2_ALIAS", 12);
    jointNodes.put("JOINT3_ALIAS", 13);
    jointNodes.put("JOINT4_ALIAS", 14);
    jointNodes.put("JOINT5_ALIAS", 15);
    Skin skin = new Skin();
    skin.setJoints(new ArrayList<>(jointNodes.values()));

    JointedModelGltfExporter.VertexWeights[] weights = GltfMeshBuilder.computeWeightsByVertex(
        weightedMesh,
        jointNodes,
        skin,
        Map.of(
            "ROOT", "ROOT_ALIAS",
            "JOINT1", "JOINT1_ALIAS",
            "JOINT2", "JOINT2_ALIAS",
            "JOINT3", "JOINT3_ALIAS",
            "JOINT4", "JOINT4_ALIAS",
            "JOINT5", "JOINT5_ALIAS"));

    assertNotNull(weights[0]);
    assertEquals(List.of(0, 1, 2, 3, 4, 5), weights[0].jointIndices);
    assertEquals(6, weights[0].weights.size());
    assertNotNull(weights[1]);
    assertEquals(1, weights[1].jointIndices.size());
  }

  @Test
  public void createMeshesSplitsStaticPrimitivesAndBuildsJointWeightQuads() {
    GlTF gltf = new GlTF();
    Map<Integer, Integer> textureMaterialMap = Map.of(0, 10, 1, 11, 2, 12);
    Map<String, Integer> meshSkinMap = new HashMap<>();
    GltfMeshBuilder meshBuilder = new GltfMeshBuilder(
        ExporterTestFixtures.createVisual(false),
        textureMaterialMap,
        meshSkinMap,
        Map.of());

    List<de.javagl.jgltf.impl.v2.Mesh> meshes = meshBuilder.createMeshes(new BufferStructureBuilder(), gltf, Map.of(
        "ROOT", 0,
        "JOINT1", 1,
        "JOINT2", 2,
        "JOINT3", 3,
        "JOINT4", 4,
        "JOINT5", 5));

    assertEquals(List.of("staticMesh_0", "staticMesh_1", "weightedMesh"), meshNames(meshes));
    MeshPrimitive weightedPrimitive = meshes.get(2).getPrimitives().get(0);
    assertTrue(weightedPrimitive.getAttributes().containsKey("JOINTS_0"));
    assertTrue(weightedPrimitive.getAttributes().containsKey("WEIGHTS_0"));
    assertTrue(weightedPrimitive.getAttributes().containsKey("JOINTS_1"));
    assertTrue(weightedPrimitive.getAttributes().containsKey("WEIGHTS_1"));
    assertEquals(Integer.valueOf(0), meshSkinMap.get("weightedMesh"));
    assertEquals(1, gltf.getSkins().size());
  }

  private static List<String> meshNames(List<de.javagl.jgltf.impl.v2.Mesh> meshes) {
    List<String> names = new ArrayList<>();
    for (de.javagl.jgltf.impl.v2.Mesh mesh : meshes) {
      names.add(mesh.getName());
    }
    return names;
  }
}
