package org.lgna.story.resourceutilities;

import com.dddviewr.collada.Collada;
import com.dddviewr.collada.controller.Controller;
import com.dddviewr.collada.controller.Skin;
import com.dddviewr.collada.controller.VertexWeights;
import com.dddviewr.collada.geometry.Geometry;
import com.dddviewr.collada.geometry.Primitives;
import com.dddviewr.collada.geometry.Triangles;
import com.dddviewr.collada.materials.Material;
import com.jogamp.common.nio.Buffers;
import edu.cmu.cs.dennisc.scenegraph.InverseAbsoluteTransformationWeightsPair;
import edu.cmu.cs.dennisc.scenegraph.Mesh;
import edu.cmu.cs.dennisc.scenegraph.WeightInfo;
import edu.cmu.cs.dennisc.scenegraph.WeightedMesh;
import org.alice.math.immutable.AffineMatrix4x4;

import java.util.*;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Builds Alice scene-graph meshes from COLLADA geometry, controllers, and skins.
 * Package-private delegate extracted from {@link JointedModelColladaImporter}.
 */
class ColladaImportMeshBuilder {

  private final Orientation orientation;
  private final Logger logger;

  ColladaImportMeshBuilder(Orientation orientation, Logger logger) {
    this.orientation = orientation;
    this.logger = logger;
  }

  static AffineMatrix4x4 floatArrayToAliceMatrix(float[] floatData, Orientation orientation) throws ModelLoadingException {
    double[] doubleData = new double[floatData.length];
    for (int i = 0; i < floatData.length; i++) {
      doubleData[i] = floatData[i];
    }
    if (doubleData.length == 12 || doubleData.length == 16) {
      return orientation.orientMatrixToAlice(AffineMatrix4x4.createFromRowMajorArray(doubleData));
    }
    throw new ModelLoadingException("Error converting collada matrix to Alice matrix. Expected array of size 12 or 16, instead got " + floatData.length);
  }

  static int getMaterialIndex(String materialId, Collada colladaModel) {
    if (materialId == null) {
      return -1;
    }
    int index = 0;
    for (Material material : colladaModel.getLibraryMaterials().getMaterials()) {
      if (materialId.equals(material.getId())) {
        return index;
      }
      index++;
    }
    return -1;
  }

  static Controller getControllerForGeometry(Geometry geometry, Collada colladaModel) {
    if (colladaModel.getLibraryControllers() != null && colladaModel.getLibraryControllers().getControllers() != null) {
      for (Controller controller : colladaModel.getLibraryControllers().getControllers()) {
        Geometry foundGeometry = colladaModel.findGeometry(controller.getSkin().getSource());
        if (foundGeometry == geometry) {
          return controller;
        }
      }
    }
    return null;
  }

  List<Mesh> createAliceMeshesFromCollada(Collada colladaModel) throws ModelLoadingException {
    List<Geometry> geometries = colladaModel.getLibraryGeometries().getGeometries();
    List<Mesh> meshes = new ArrayList<>();
    for (Geometry geometry : geometries) {
      Mesh mesh = createAliceSGMeshFromGeometry(geometry, colladaModel);
      if (mesh != null) {
        meshes.add(mesh);
      }
    }
    return meshes;
  }

  Mesh createAliceSGMeshFromGeometry(Geometry geometry, Collada colladaModel) throws ModelLoadingException {
    Controller meshController = getControllerForGeometry(geometry, colladaModel);
    Mesh sgMesh;
    if (meshController != null) {
      sgMesh = new WeightedMesh();
    } else {
      sgMesh = new Mesh();
    }
    sgMesh.setName(geometry.getName());
    final float[] normals = geometry.getMesh().getNormalData();
    if (normals == null) {
      throw new ModelLoadingException("No normal data found in model.");
    }
    orientation.orientNormals(normals, sgMesh.normalBuffer);

    float[] colladaVertices = geometry.getMesh().getPositionData();
    double[] doubleVertexData = orientation.orientVertices(colladaVertices, sgMesh.vertexBuffer);
    final float[] coordData = geometry.getMesh().getTexCoordData();
    if (coordData == null) {
      throw new ModelLoadingException("No texture coordinate data found in model.");
    }
    sgMesh.textCoordBuffer.setValue(Buffers.newDirectFloatBuffer(coordData));

    Triangles tris = null;
    for (Primitives p : geometry.getMesh().getPrimitives()) {
      if (p instanceof Triangles triangles) {
        if (tris == null) {
          tris = triangles;
        } else {
          logger.log(Level.WARNING, "Converting mesh '" + geometry.getName() + "': Unsupported primitive count: Found extra triangle primitives, only processing the first.");
        }
      } else {
        logger.log(Level.WARNING, "Converting mesh '" + geometry.getName() + "': Unsupported primitive type " + p.getClass() + ". Skipping this geometry.");
      }
    }
    if (tris == null) {
      logger.log(Level.WARNING, "Error converting mesh " + geometry.getName() + ". No triangle primitive data found. Skipping entire mesh.");
      return null;
    }
    sgMesh.indexBuffer.setValue(Buffers.newDirectIntBuffer(tris.getData()));
    sgMesh.textureId.setValue(getMaterialIndex(tris.getMaterial(), colladaModel));

    if (sgMesh instanceof WeightedMesh mesh) {
      recordWeights(mesh, meshController, doubleVertexData);
    }
    return sgMesh;
  }

  private void recordWeights(WeightedMesh sgMesh, Controller meshController, double[] vertices) throws ModelLoadingException {
    float[] bindMatrixData = meshController.getSkin().getBindShapeMatrix();
    if (bindMatrixData != null) {
      AffineMatrix4x4 bindMatrix = floatArrayToAliceMatrix(bindMatrixData, orientation);
      double[] bindSpaceVertices = new double[vertices.length];
      for (int i = 0; i < vertices.length; i += 3) {
        bindMatrix.transformPoint3(bindSpaceVertices, i, vertices, i);
      }
      sgMesh.vertexBuffer.setValue(Buffers.newDirectDoubleBuffer(bindSpaceVertices));
    }
    sgMesh.weightInfo.setValue(createWeightInfoForController(meshController));
  }

  private WeightInfo createWeightInfoForController(Controller meshController) throws ModelLoadingException {
    Skin skin = meshController.getSkin();
    Map<Integer, float[]> jointWeightMap = getJointWeightMap(skin);
    String[] jointData = skin.getJointData();
    if (jointData == null) {
      throw new ModelLoadingException("Error converting mesh " + meshController.getName() + ", no joint data found on mesh skin.");
    }
    float[] inverseBindMatrixData = skin.getInvBindMatrixData();
    if (inverseBindMatrixData == null) {
      throw new ModelLoadingException("Error converting mesh " + meshController.getName() + ", no inverse bind matrix data found on mesh skin.");
    }
    WeightInfo weightInfo = new WeightInfo();
    for (Entry<Integer, float[]> jointAndWeights : jointWeightMap.entrySet()) {
      int jointIndex = jointAndWeights.getKey();
      String jointId = jointData[jointIndex];
      float[] inverseBindMatrix = Arrays.copyOfRange(inverseBindMatrixData, 16 * jointIndex, 16 * jointIndex + 16);
      AffineMatrix4x4 aliceInverseBindMatrix = floatArrayToAliceMatrix(inverseBindMatrix, orientation);
      InverseAbsoluteTransformationWeightsPair iawp = InverseAbsoluteTransformationWeightsPair.createInverseAbsoluteTransformationWeightsPair(jointAndWeights.getValue(), aliceInverseBindMatrix);
      if (iawp != null) {
        weightInfo.addReference(jointId, iawp);
      }
    }
    return weightInfo;
  }

  private static Map<Integer, float[]> getJointWeightMap(Skin skin) {
    VertexWeights vertexWeights = skin.getVertexWeights();
    float[] weightData = skin.getWeightData();
    int[] vertexWeightData = vertexWeights.getData();
    Map<Integer, float[]> jointWeightMap = new HashMap<>();
    int entryCount = 0;
    for (int vertex = 0; vertex < vertexWeights.getCount(); vertex++) {
      int boneCount = vertexWeights.getVcount().getData()[vertex];
      for (int bone = 0; bone < boneCount; bone++) {
        int jointIndex = vertexWeightData[entryCount * 2];
        int weightIndex = vertexWeightData[entryCount * 2 + 1];
        float[] weightArray;
        if (jointWeightMap.containsKey(jointIndex)) {
          weightArray = jointWeightMap.get(jointIndex);
        } else {
          weightArray = new float[vertexWeights.getCount()];
          jointWeightMap.put(jointIndex, weightArray);
        }
        weightArray[vertex] = weightData[weightIndex];
        entryCount++;
      }
    }
    return jointWeightMap;
  }
}
