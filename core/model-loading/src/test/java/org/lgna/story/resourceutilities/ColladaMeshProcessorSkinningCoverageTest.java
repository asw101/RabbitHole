package org.lgna.story.resourceutilities;

import edu.cmu.cs.dennisc.scenegraph.SkeletonVisual;
import edu.cmu.cs.dennisc.scenegraph.WeightedMesh;
import org.lgna.story.resourceutilities.exporterutils.collada.Controller;
import org.lgna.story.resourceutilities.exporterutils.collada.NameArray;
import org.lgna.story.resourceutilities.exporterutils.collada.ObjectFactory;
import org.lgna.story.resourceutilities.exporterutils.collada.Skin;
import org.lgna.story.resourceutilities.exporterutils.collada.Source;
import org.junit.Test;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ColladaMeshProcessorSkinningCoverageTest {
  @Test
  public void jointIdentifierFunctionRenamesJointSourceAndLeavesUnweightedVerticesAtZero() {
    WeightedMesh weightedMesh = weightedMesh();
    ColladaMeshProcessor processor = new ColladaMeshProcessor(
        new ObjectFactory(),
        Map.of(weightedMesh, "weightedMesh"),
        Map.of(2, "material_2")
    );

    List<Controller> controllers = new ArrayList<>();
    processor.addControllersForMesh(controllers, weightedMesh, jointId -> "alias_" + jointId);
    Skin skin = controllers.get(0).getSkin();

    NameArray jointNames = skin.getSourceAttribute2().stream()
        .map(Source::getNameArray)
        .filter(nameArray -> nameArray != null)
        .findFirst()
        .orElseThrow();

    assertTrue(jointNames.getValue().stream().allMatch(name -> name.startsWith("alias_")));
    assertEquals(List.of(6L, 1L, 0L), skin.getVertexWeights().getVcount().stream().map(BigInteger::longValue).toList());
  }

  @Test
  public void weightAndMatrixSourcesReflectStoredSkinningData() {
    WeightedMesh weightedMesh = weightedMesh();
    ColladaMeshProcessor processor = new ColladaMeshProcessor(
        new ObjectFactory(),
        Map.of(weightedMesh, "weightedMesh"),
        Map.of(2, "material_2")
    );

    List<Controller> controllers = new ArrayList<>();
    processor.addControllersForMesh(controllers, weightedMesh, jointId -> jointId);
    Skin skin = controllers.get(0).getSkin();

    Source matrixSource = skin.getSourceAttribute2().stream()
        .filter(source -> source.getId().endsWith("-Matrices"))
        .findFirst()
        .orElseThrow();
    Source weightSource = skin.getSourceAttribute2().stream()
        .filter(source -> source.getId().endsWith("-Weights"))
        .findFirst()
        .orElseThrow();

    assertEquals(6 * 16, matrixSource.getFloatArray().getValue().size());
    assertEquals(7, weightSource.getFloatArray().getValue().size());
    assertEquals(14, skin.getVertexWeights().getV().size());
    assertEquals(3, skin.getVertexWeights().getCount().intValue());
  }

  private static WeightedMesh weightedMesh() {
    SkeletonVisual visual = ExporterTestFixtures.createVisual(false);
    return visual.weightedMeshes.getValue()[0];
  }
}
