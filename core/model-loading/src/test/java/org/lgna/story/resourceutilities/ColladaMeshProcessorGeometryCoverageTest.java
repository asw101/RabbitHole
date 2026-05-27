package org.lgna.story.resourceutilities;

import edu.cmu.cs.dennisc.scenegraph.Mesh;
import edu.cmu.cs.dennisc.scenegraph.SkeletonVisual;
import org.lgna.story.resourceutilities.exporterutils.collada.Geometry;
import org.lgna.story.resourceutilities.exporterutils.collada.InstanceGeometry;
import org.lgna.story.resourceutilities.exporterutils.collada.InstanceMaterial;
import org.lgna.story.resourceutilities.exporterutils.collada.Node;
import org.lgna.story.resourceutilities.exporterutils.collada.ObjectFactory;
import org.lgna.story.resourceutilities.exporterutils.collada.Triangles;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ColladaMeshProcessorGeometryCoverageTest {
  @Test
  public void multiTextureMeshesCreateDistinctGeometryIdsAndTriangleCounts() {
    Mesh mesh = staticMesh();
    ColladaMeshProcessor processor = new ColladaMeshProcessor(
        new ObjectFactory(),
        Map.of(mesh, "staticMesh"),
        Map.of(0, "material_0", 1, "material_1")
    );

    List<Geometry> geometries = new ArrayList<>();
    processor.addGeometriesForMesh(geometries, mesh);

    assertEquals(2, geometries.size());
    assertEquals("staticMesh-0-id", geometries.get(0).getId());
    assertEquals("staticMesh-1-id", geometries.get(1).getId());

    Triangles first = (Triangles) geometries.get(0).getMesh().getLinesOrLinestripsOrPolygons().get(0);
    Triangles second = (Triangles) geometries.get(1).getMesh().getLinesOrLinestripsOrPolygons().get(0);
    assertEquals(1, first.getCount().intValue());
    assertEquals(1, second.getCount().intValue());
    assertEquals(9, first.getP().size());
    assertEquals(9, second.getP().size());
  }

  @Test
  public void visualSceneNodesBindUvInputsAndMaterialTargetsPerTexture() {
    Mesh mesh = staticMesh();
    ColladaMeshProcessor processor = new ColladaMeshProcessor(
        new ObjectFactory(),
        Map.of(mesh, "staticMesh"),
        Map.of(0, "material_0", 1, "material_1")
    );

    List<Node> nodes = new ArrayList<>();
    processor.addVisualSceneNodesForMesh(nodes, mesh);

    assertEquals(2, nodes.size());
    InstanceGeometry secondGeometry = nodes.get(1).getInstanceGeometry().get(0);
    InstanceMaterial secondMaterial = secondGeometry.getBindMaterial()
        .getTechniqueCommon()
        .getInstanceMaterial()
        .get(0);

    assertEquals("#staticMesh-1-id", secondGeometry.getUrl());
    assertTrue(secondMaterial.getTarget().contains("material_1"));
    assertEquals("UVMap", secondMaterial.getBindVertexInput().get(0).getSemantic());
    assertEquals("TEXCOORD", secondMaterial.getBindVertexInput().get(0).getInputSemantic());
  }

  private static Mesh staticMesh() {
    SkeletonVisual visual = ExporterTestFixtures.createVisual(false);
    return (Mesh) visual.geometries.getValue()[0];
  }
}
