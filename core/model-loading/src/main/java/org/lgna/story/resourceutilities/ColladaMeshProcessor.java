package org.lgna.story.resourceutilities;

import edu.cmu.cs.dennisc.scenegraph.WeightedMesh;
import org.lgna.story.resourceutilities.exporterutils.collada.*;
import org.lgna.story.resourceutilities.exporterutils.collada.Geometry;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Processes meshes, skins, controllers, and visual scene nodes for COLLADA export.
 * Stub — implementation pending in Step 8.
 */
class ColladaMeshProcessor {

  ColladaMeshProcessor(ObjectFactory factory,
                       Map<edu.cmu.cs.dennisc.scenegraph.Geometry, String> meshNameMap,
                       Map<Integer, String> materialNameMap) {
    throw new UnsupportedOperationException("Not yet implemented");
  }

  void addGeometriesForMesh(List<Geometry> geometries, edu.cmu.cs.dennisc.scenegraph.Mesh sgMesh) {
    throw new UnsupportedOperationException("Not yet implemented");
  }

  void addControllersForMesh(List<Controller> controllers, WeightedMesh sgWeightedMesh,
                             Function<String, String> jointIdentifier) {
    throw new UnsupportedOperationException("Not yet implemented");
  }

  void addVisualSceneNodesForMesh(List<Node> sceneNodes, edu.cmu.cs.dennisc.scenegraph.Mesh sgMesh) {
    throw new UnsupportedOperationException("Not yet implemented");
  }

  void addVisualSceneNodesForWeightedMesh(List<Node> sceneNodes, WeightedMesh sgWeightedMesh) {
    throw new UnsupportedOperationException("Not yet implemented");
  }
}
