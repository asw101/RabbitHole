package org.lgna.story.resourceutilities;

import edu.cmu.cs.dennisc.scenegraph.SkeletonVisual;
import edu.cmu.cs.dennisc.scenegraph.WeightedMesh;
import org.lgna.story.resourceutilities.exporterutils.collada.BindMaterial;
import org.lgna.story.resourceutilities.exporterutils.collada.Controller;
import org.lgna.story.resourceutilities.exporterutils.collada.InstanceController;
import org.lgna.story.resourceutilities.exporterutils.collada.Node;
import org.lgna.story.resourceutilities.exporterutils.collada.ObjectFactory;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ColladaMeshProcessorWeightedNodeCoverageTest {
  @Test
  public void weightedMeshControllersAndSceneNodesShareControllerAndMaterialNames() {
    WeightedMesh weightedMesh = weightedMesh();
    ColladaMeshProcessor processor = new ColladaMeshProcessor(
        new ObjectFactory(),
        Map.of(weightedMesh, "weightedMesh"),
        Map.of(2, "material_2")
    );

    List<Controller> controllers = new ArrayList<>();
    processor.addControllersForMesh(controllers, weightedMesh, jointId -> jointId.toLowerCase());
    List<Node> nodes = new ArrayList<>();
    processor.addVisualSceneNodesForWeightedMesh(nodes, weightedMesh);

    assertEquals(1, controllers.size());
    assertEquals("weightedMeshController", controllers.get(0).getId());
    assertEquals("#weightedMesh-id", controllers.get(0).getSkin().getSourceAttribute());

    assertEquals(1, nodes.size());
    Node node = nodes.get(0);
    assertEquals("weightedMesh", node.getId());
    assertEquals("weightedMesh", node.getSid());
    assertEquals("weightedMesh", node.getName());

    InstanceController instanceController = node.getInstanceController().get(0);
    assertEquals("#weightedMeshController", instanceController.getUrl());
    BindMaterial.TechniqueCommon materials = instanceController.getBindMaterial().getTechniqueCommon();
    assertEquals(1, materials.getInstanceMaterial().size());
    assertEquals("material_2_shader", materials.getInstanceMaterial().get(0).getSymbol());
    assertEquals("material_2_shader", materials.getInstanceMaterial().get(0).getTarget());
    assertTrue(materials.getInstanceMaterial().get(0).getBindVertexInput().stream()
        .anyMatch(input -> "UVMap".equals(input.getSemantic()) && "TEXCOORD".equals(input.getInputSemantic())));
  }

  private static WeightedMesh weightedMesh() {
    SkeletonVisual visual = ExporterTestFixtures.createVisual(false);
    return visual.weightedMeshes.getValue()[0];
  }
}
