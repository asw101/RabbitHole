package org.lgna.story.resourceutilities;

import edu.cmu.cs.dennisc.scenegraph.InverseAbsoluteTransformationWeightsPair;
import edu.cmu.cs.dennisc.scenegraph.WeightInfo;
import edu.cmu.cs.dennisc.scenegraph.WeightedMesh;
import org.alice.math.immutable.AffineMatrix4x4;
import org.lgna.story.resourceutilities.exporterutils.collada.*;
import org.lgna.story.resourceutilities.exporterutils.collada.Geometry;
import org.lgna.story.resourceutilities.exporterutils.collada.Mesh;
import org.lgna.story.resourceutilities.exporterutils.collada.Skin.VertexWeights;
import org.lgna.story.resourceutilities.exporterutils.collada.Source.TechniqueCommon;

import java.math.BigInteger;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;

/**
 * Processes meshes, skins, controllers, and visual scene nodes for COLLADA export.
 */
class ColladaMeshProcessor {

  private static final boolean FLIP_COORDINATE_SPACE = true;
  private static final boolean SCALE_MODEL = false;
  private static final double MODEL_SCALE = 1.0;

  private final ObjectFactory factory;
  private final Map<edu.cmu.cs.dennisc.scenegraph.Geometry, String> meshNameMap;
  private final Map<Integer, String> materialNameMap;

  ColladaMeshProcessor(ObjectFactory factory,
                       Map<edu.cmu.cs.dennisc.scenegraph.Geometry, String> meshNameMap,
                       Map<Integer, String> materialNameMap) {
    this.factory = factory;
    this.meshNameMap = meshNameMap;
    this.materialNameMap = materialNameMap;
  }

  // ── ListInitializer hierarchy ──────────────────────────────────

  private interface ListInitializer {
    void initializeList(List<Double> toInitialize);
  }

  private static class DoubleBufferInitializeList implements ListInitializer {
    private final DoubleBuffer db;
    DoubleBufferInitializeList(DoubleBuffer db) { this.db = db; }
    @Override
    public void initializeList(List<Double> toInitialize) {
      db.rewind();
      final int N = db.limit();
      for (int i = 0; i < N; i++) {
        toInitialize.add(db.get(i));
      }
    }
  }

  private static class FloatBufferInitializeList implements ListInitializer {
    private final FloatBuffer fb;
    FloatBufferInitializeList(FloatBuffer fb) { this.fb = fb; }
    @Override
    public void initializeList(List<Double> toInitialize) {
      fb.rewind();
      final int N = fb.limit();
      for (int i = 0; i < N; i++) {
        toInitialize.add(Double.valueOf(fb.get(i)));
      }
    }
  }

  private static class FloatListInitializeList implements ListInitializer {
    private final List<Float> fl;
    FloatListInitializeList(List<Float> fl) { this.fl = fl; }
    @Override
    public void initializeList(List<Double> toInitialize) {
      for (int i = 0; i < fl.size(); i++) {
        toInitialize.add(fl.get(i).doubleValue());
      }
    }
  }

  // ── Source creation helpers ────────────────────────────────────

  private Param createParam(String name, String type) {
    Param param = factory.createParam();
    if (name != null) {
      param.setName(name);
    }
    param.setType(type);
    return param;
  }

  private Accessor createAccessorForArray(String arrayId, String arrayType, int arraySize, int stride) {
    Accessor accessor = factory.createAccessor();
    accessor.setSource("#" + arrayId);
    if (stride > 1) {
      accessor.setStride(BigInteger.valueOf(stride));
    }
    accessor.setCount(BigInteger.valueOf(arraySize / stride));
    if (stride == 3) {
      accessor.getParam().add(createParam("X", arrayType));
      accessor.getParam().add(createParam("Y", arrayType));
      accessor.getParam().add(createParam("Z", arrayType));
    } else if (stride == 2) {
      accessor.getParam().add(createParam("S", arrayType));
      accessor.getParam().add(createParam("T", arrayType));
    } else {
      accessor.getParam().add(createParam(null, arrayType));
    }
    return accessor;
  }

  private TechniqueCommon createTechniqueCommonForArray(String arrayId, String arrayType, int arraySize, int stride) {
    TechniqueCommon accessorTechnique = factory.createSourceTechniqueCommon();
    accessorTechnique.setAccessor(createAccessorForArray(arrayId, arrayType, arraySize, stride));
    return accessorTechnique;
  }

  private Source createFloatArraySourceFromInitializer(ListInitializer initializer, String name, int stride, double scale, boolean flipXandZ) {
    Source source = factory.createSource();
    source.setId(name);
    FloatArray floatArray = factory.createFloatArray();
    floatArray.setId(name + "-array");
    List<Double> values = floatArray.getValue();
    initializer.initializeList(values);
    if (flipXandZ || scale != 1.0) {
      double flipScale = flipXandZ ? scale * -1.0 : scale;
      for (int i = 0; i < values.size(); i += 3) {
        values.set(i, values.get(i) * flipScale);
        values.set(i + 1, values.get(i + 1) * scale);
        values.set(i + 2, values.get(i + 2) * flipScale);
      }
    }
    int valueCount = values.size();
    floatArray.setCount(BigInteger.valueOf(valueCount));
    TechniqueCommon accessorTechnique = createTechniqueCommonForArray(floatArray.getId(), "float", floatArray.getValue().size(), stride);
    source.setFloatArray(floatArray);
    source.setTechniqueCommon(accessorTechnique);
    return source;
  }

  // ── Input helpers ─────────────────────────────────────────────

  private InputLocal createInputLocal(String semantic, String sourceName) {
    InputLocal inputLocal = factory.createInputLocal();
    inputLocal.setSemantic(semantic);
    inputLocal.setSource("#" + sourceName);
    return inputLocal;
  }

  private InputLocalOffset createInputLocalOffset(String semantic, String sourceName, int offset) {
    InputLocalOffset inputLocalOffset = factory.createInputLocalOffset();
    inputLocalOffset.setSemantic(semantic);
    inputLocalOffset.setSource("#" + sourceName);
    inputLocalOffset.setOffset(BigInteger.valueOf(offset));
    return inputLocalOffset;
  }

  // ── Mesh naming helpers ───────────────────────────────────────

  private String getMeshIdForMeshName(String meshName) {
    return meshName + "-id";
  }

  private String getMeshTextureId(edu.cmu.cs.dennisc.scenegraph.Mesh mesh, Integer usedTextureId) {
    String meshName = meshNameMap.get(mesh);
    if (mesh.getReferencedTextureIds().size() > 1) {
      meshName = meshName + "-" + usedTextureId;
    }
    return meshName;
  }

  private String getMaterialIDForIndex(Integer index) {
    return materialNameMap.get(index) + "_shader";
  }

  private String getInstanceMaterialSymbolForIndex(Integer index) {
    return getMaterialIDForIndex(index);
  }

  // ── Geometry creation ─────────────────────────────────────────

  void addGeometriesForMesh(List<Geometry> geometries, edu.cmu.cs.dennisc.scenegraph.Mesh sgMesh) {
    for (Integer textureId : sgMesh.getReferencedTextureIds()) {
      geometries.add(geometryForMeshAndTexture(sgMesh, textureId));
    }
  }

  private Geometry geometryForMeshAndTexture(edu.cmu.cs.dennisc.scenegraph.Mesh sgMesh, Integer textureId) {
    Geometry geometry = factory.createGeometry();
    String meshName = getMeshTextureId(sgMesh, textureId);
    geometry.setId(getMeshIdForMeshName(meshName));
    geometry.setName(meshName + "mesh");
    geometry.setMesh(createMesh(sgMesh, textureId, meshName));
    return geometry;
  }

  private Mesh createMesh(edu.cmu.cs.dennisc.scenegraph.Mesh sgMesh, Integer textureId, String meshName) {
    Mesh mesh = factory.createMesh();
    String positionName = meshName + "-POSITION";
    double scale = SCALE_MODEL ? MODEL_SCALE : 1.0;
    Source positionSource = createFloatArraySourceFromInitializer(new DoubleBufferInitializeList(sgMesh.vertexBuffer.getValue()), positionName, 3, scale, FLIP_COORDINATE_SPACE);
    mesh.getSource().add(positionSource);

    String normalName = meshName + "-NORMAL";
    Source normalSource = createFloatArraySourceFromInitializer(new FloatBufferInitializeList(sgMesh.normalBuffer.getValue()), normalName, 3, 1.0, FLIP_COORDINATE_SPACE);
    mesh.getSource().add(normalSource);

    String uvName = meshName + "-UV";
    Source uvSource = createFloatArraySourceFromInitializer(new FloatBufferInitializeList(sgMesh.textCoordBuffer.getValue()), uvName, 2, 1.0, false);
    mesh.getSource().add(uvSource);

    String vertexName = meshName + "-VERTEX";
    Vertices vertices = factory.createVertices();
    vertices.setId(vertexName);
    vertices.getInput().add(createInputLocal("POSITION", positionName));
    mesh.setVertices(vertices);

    Triangles triangles = createTriangles(sgMesh, vertexName, normalName, uvName, textureId);
    triangles.setMaterial(getInstanceMaterialSymbolForIndex(textureId));
    mesh.getLinesOrLinestripsOrPolygons().add(triangles);
    return mesh;
  }

  private Triangles createTriangles(edu.cmu.cs.dennisc.scenegraph.Mesh sgMesh, String verticesName, String normalsName, String UVsName, Integer textureId) {
    Triangles triangles = factory.createTriangles();
    triangles.getInput().add(createInputLocalOffset("VERTEX", verticesName, 0));
    triangles.getInput().add(createInputLocalOffset("NORMAL", normalsName, 1));
    InputLocalOffset texCoordInput = createInputLocalOffset("TEXCOORD", UVsName, 2);
    texCoordInput.setSet(BigInteger.valueOf(0));
    triangles.getInput().add(texCoordInput);

    List<BigInteger> triangleList = triangles.getP();
    IntBuffer ib = sgMesh.indexBuffer.getValue();
    final int N = sgMesh.indexBuffer.getValue().limit();
    int count = 0;
    for (int i = 0; i < N; i += 3) {
      if (sgMesh.getTextureId(i).equals(textureId)) {
        triangleList.add(BigInteger.valueOf(ib.get(i + 0)));
        triangleList.add(BigInteger.valueOf(ib.get(i + 0)));
        triangleList.add(BigInteger.valueOf(ib.get(i + 0)));
        triangleList.add(BigInteger.valueOf(ib.get(i + 1)));
        triangleList.add(BigInteger.valueOf(ib.get(i + 1)));
        triangleList.add(BigInteger.valueOf(ib.get(i + 1)));
        triangleList.add(BigInteger.valueOf(ib.get(i + 2)));
        triangleList.add(BigInteger.valueOf(ib.get(i + 2)));
        triangleList.add(BigInteger.valueOf(ib.get(i + 2)));
        count++;
      }
    }
    triangles.setCount(BigInteger.valueOf(count));
    return triangles;
  }

  // ── Controller/skin creation ──────────────────────────────────

  void addControllersForMesh(List<Controller> controllers, WeightedMesh sgWeightedMesh,
                             Function<String, String> jointIdentifier) {
    for (Integer usedTextureId : sgWeightedMesh.getReferencedTextureIds()) {
      String meshName = getMeshTextureId(sgWeightedMesh, usedTextureId);
      Controller controller = factory.createController();
      String controllerName = meshName + "Controller";
      controller.setId(controllerName);
      Skin skin = createSkin(sgWeightedMesh, controllerName, jointIdentifier);
      skin.setSourceAttribute("#" + getMeshIdForMeshName(meshName));
      controller.setSkin(skin);
      controllers.add(controller);
    }
  }

  private Skin createSkin(WeightedMesh sgWeightedMesh, String controllerName, Function<String, String> jointIdentifier) {
    Skin skin = factory.createSkin();

    double[] bindShapeMatrix = AffineMatrix4x4.IDENTITY.asRowMajorArray16();
    for (double element : bindShapeMatrix) {
      skin.getBindShapeMatrix().add(element);
    }

    WeightInfo wi = sgWeightedMesh.weightInfo.getValue();
    String jointSourceName = controllerName + "-Joints";
    Source jointSource = createJointSource(wi, jointSourceName, jointIdentifier);
    skin.getSourceAttribute2().add(jointSource);

    String matricesSourceName = controllerName + "-Matrices";
    Source matricesSource = createMatrixSource(wi, matricesSourceName);
    skin.getSourceAttribute2().add(matricesSource);

    int vertexCount = sgWeightedMesh.vertexBuffer.getValue().limit() / 3;
    VertexWeights vw = factory.createSkinVertexWeights();
    vw.setCount(BigInteger.valueOf(vertexCount));
    ColladaSkinWeights[] skinWeights = createSkinWeights(sgWeightedMesh);
    List<Float> weightArray = createWeightList(sgWeightedMesh);
    for (ColladaSkinWeights vertexWeights : skinWeights) {
      if (vertexWeights == null) {
        vw.getVcount().add(BigInteger.valueOf(0));
        continue;
      }
      vw.getVcount().add(BigInteger.valueOf(vertexWeights.jointIndices.size()));
      for (int j = 0; j < vertexWeights.jointIndices.size(); j++) {
        vw.getV().add(vertexWeights.jointIndices.get(j).longValue());
        vw.getV().add(vertexWeights.weightIndices.get(j).longValue());
      }
    }

    String weightsSourceName = controllerName + "-Weights";
    Source weightsSource = createFloatArraySourceFromInitializer(new FloatListInitializeList(weightArray), weightsSourceName, 1, 1.0, false);
    skin.getSourceAttribute2().add(weightsSource);

    vw.getInput().add(createInputLocalOffset("JOINT", jointSourceName, 0));
    vw.getInput().add(createInputLocalOffset("WEIGHT", weightsSourceName, 1));

    Skin.Joints joints = factory.createSkinJoints();
    joints.getInput().add(createInputLocal("JOINT", jointSourceName));
    joints.getInput().add(createInputLocal("INV_BIND_MATRIX", matricesSourceName));
    skin.setJoints(joints);
    skin.setVertexWeights(vw);

    return skin;
  }

  private Source createJointSource(WeightInfo wi, String jointSourceName, Function<String, String> jointIdentifier) {
    Source jointSource = factory.createSource();
    jointSource.setId(jointSourceName);
    NameArray jointNameArray = factory.createNameArray();
    jointNameArray.setId(jointSourceName + "-array");
    for (Entry<String, InverseAbsoluteTransformationWeightsPair> entry : wi.getMap().entrySet()) {
      jointNameArray.getValue().add(jointIdentifier.apply(entry.getKey()));
    }
    jointNameArray.setCount(BigInteger.valueOf(jointNameArray.getValue().size()));
    jointSource.setNameArray(jointNameArray);
    jointSource.setTechniqueCommon(createTechniqueCommonForArray(jointNameArray.getId(), "name", jointNameArray.getValue().size(), 1));
    return jointSource;
  }

  private Source createMatrixSource(WeightInfo wi, String matricesSourceName) {
    Source matricesSource = factory.createSource();
    matricesSource.setId(matricesSourceName);
    FloatArray matricesArray = factory.createFloatArray();
    matricesArray.setId(matricesSourceName + "-array");
    for (Entry<String, InverseAbsoluteTransformationWeightsPair> entry : wi.getMap().entrySet()) {
      InverseAbsoluteTransformationWeightsPair iatwp = entry.getValue();
      AffineMatrix4x4 inverseBindMatrix = iatwp.getInverseAbsoluteTransformation();
      if (SCALE_MODEL) {
        inverseBindMatrix = inverseBindMatrix.scaleTranslation(MODEL_SCALE);
      }
      double[] matrix = inverseBindMatrix.asRowMajorArray16();
      if (FLIP_COORDINATE_SPACE) {
        matrix = ColladaTransformUtilities.createFlippedRowMajorTransform(matrix);
      }
      for (double element : matrix) {
        matricesArray.getValue().add(element);
      }
    }
    int matrixCount = matricesArray.getValue().size();
    matricesArray.setCount(BigInteger.valueOf(matrixCount));
    matricesSource.setFloatArray(matricesArray);
    matricesSource.setTechniqueCommon(createTechniqueCommonForArray(matricesArray.getId(), "float4x4", matrixCount, 16));
    return matricesSource;
  }

  private List<Float> createWeightList(WeightedMesh sgWeightedMesh) {
    WeightInfo wi = sgWeightedMesh.weightInfo.getValue();
    List<Float> weightArray = new ArrayList<>();
    for (Entry<String, InverseAbsoluteTransformationWeightsPair> entry : wi.getMap().entrySet()) {
      InverseAbsoluteTransformationWeightsPair.WeightIterator weightIterator = entry.getValue().getIterator();
      while (weightIterator.hasNext()) {
        weightArray.add(weightIterator.next());
      }
    }
    return weightArray;
  }

  private static class ColladaSkinWeights {
    final List<Integer> weightIndices = new ArrayList<>();
    final List<Integer> jointIndices = new ArrayList<>();
  }

  private ColladaSkinWeights[] createSkinWeights(WeightedMesh sgWeightedMesh) {
    WeightInfo wi = sgWeightedMesh.weightInfo.getValue();
    int vertexCount = sgWeightedMesh.vertexBuffer.getValue().limit() / 3;
    ColladaSkinWeights[] skinWeights = new ColladaSkinWeights[vertexCount];
    int jointIndex = 0;
    int weightIndex = 0;
    for (Entry<String, InverseAbsoluteTransformationWeightsPair> entry : wi.getMap().entrySet()) {
      InverseAbsoluteTransformationWeightsPair.WeightIterator weightIterator = entry.getValue().getIterator();
      while (weightIterator.hasNext()) {
        int vertexIndex = weightIterator.getIndex();
        if (skinWeights[vertexIndex] == null) {
          skinWeights[vertexIndex] = new ColladaSkinWeights();
        }
        ColladaSkinWeights vertexWeights = skinWeights[vertexIndex];
        vertexWeights.jointIndices.add(jointIndex);
        vertexWeights.weightIndices.add(weightIndex++);
        weightIterator.next();
      }
      jointIndex++;
    }
    return skinWeights;
  }

  // ── Visual scene nodes ────────────────────────────────────────

  private Node createVisualSceneNode(String name) {
    Node visualSceneNode = factory.createNode();
    visualSceneNode.setName(name);
    visualSceneNode.setId(name);
    visualSceneNode.setSid(name);
    return visualSceneNode;
  }

  private BindMaterial createBindMaterialForMaterialIndex(Integer materialIndex) {
    BindMaterial bindMaterial = factory.createBindMaterial();
    BindMaterial.TechniqueCommon techniqueCommon = factory.createBindMaterialTechniqueCommon();
    InstanceMaterial instanceMaterial = factory.createInstanceMaterial();
    instanceMaterial.setSymbol(getInstanceMaterialSymbolForIndex(materialIndex));
    instanceMaterial.setTarget(getMaterialIDForIndex(materialIndex));
    InstanceMaterial.BindVertexInput bindVertexInput = factory.createInstanceMaterialBindVertexInput();
    bindVertexInput.setSemantic("UVMap");
    bindVertexInput.setInputSet(BigInteger.ZERO);
    bindVertexInput.setInputSemantic("TEXCOORD");
    instanceMaterial.getBindVertexInput().add(bindVertexInput);
    techniqueCommon.getInstanceMaterial().add(instanceMaterial);
    bindMaterial.setTechniqueCommon(techniqueCommon);
    return bindMaterial;
  }

  void addVisualSceneNodesForMesh(List<Node> sceneNodes, edu.cmu.cs.dennisc.scenegraph.Mesh sgMesh) {
    for (Integer usedTextureId : sgMesh.getReferencedTextureIds()) {
      String meshName = getMeshTextureId(sgMesh, usedTextureId);
      Node visualSceneNode = createVisualSceneNode(meshName);
      String geometryURL = "#" + getMeshIdForMeshName(meshName);
      InstanceGeometry instanceGeometry = factory.createInstanceGeometry();
      instanceGeometry.setUrl(geometryURL);
      instanceGeometry.setBindMaterial(createBindMaterialForMaterialIndex(usedTextureId));
      visualSceneNode.getInstanceGeometry().add(instanceGeometry);
      sceneNodes.add(visualSceneNode);
    }
  }

  void addVisualSceneNodesForWeightedMesh(List<Node> sceneNodes, WeightedMesh sgWeightedMesh) {
    for (Integer usedTextureId : sgWeightedMesh.getReferencedTextureIds()) {
      String meshName = getMeshTextureId(sgWeightedMesh, usedTextureId);
      Node visualSceneNode = createVisualSceneNode(meshName);
      String controllerURL = "#" + meshName + "Controller";
      InstanceController instanceController = factory.createInstanceController();
      instanceController.setUrl(controllerURL);
      instanceController.setBindMaterial(createBindMaterialForMaterialIndex(usedTextureId));
      visualSceneNode.getInstanceController().add(instanceController);
      sceneNodes.add(visualSceneNode);
    }
  }
}
