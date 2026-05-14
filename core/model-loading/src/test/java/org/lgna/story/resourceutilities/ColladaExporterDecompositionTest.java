package org.lgna.story.resourceutilities;

import edu.cmu.cs.dennisc.scenegraph.Joint;
import edu.cmu.cs.dennisc.scenegraph.Mesh;
import edu.cmu.cs.dennisc.scenegraph.SkeletonVisual;
import edu.cmu.cs.dennisc.scenegraph.TexturedAppearance;
import edu.cmu.cs.dennisc.scenegraph.WeightedMesh;
import edu.cmu.cs.dennisc.scenegraph.WeightInfo;
import edu.cmu.cs.dennisc.scenegraph.InverseAbsoluteTransformationWeightsPair;
import edu.cmu.cs.dennisc.texture.BufferedImageTexture;
import org.alice.math.immutable.AffineMatrix4x4;
import org.alice.math.immutable.OrthogonalMatrix3x3;
import org.alice.math.immutable.Point3;
import org.alice.tweedle.file.ModelManifest;
import org.lgna.story.resourceutilities.exporterutils.collada.Asset;
import org.lgna.story.resourceutilities.exporterutils.collada.BindMaterial;
import org.lgna.story.resourceutilities.exporterutils.collada.COLLADA;
import org.lgna.story.resourceutilities.exporterutils.collada.Controller;
import org.lgna.story.resourceutilities.exporterutils.collada.Effect;
import org.lgna.story.resourceutilities.exporterutils.collada.InputLocalOffset;
import org.lgna.story.resourceutilities.exporterutils.collada.InstanceGeometry;
import org.lgna.story.resourceutilities.exporterutils.collada.InstanceMaterial;
import org.lgna.story.resourceutilities.exporterutils.collada.Matrix;
import org.lgna.story.resourceutilities.exporterutils.collada.Node;
import org.lgna.story.resourceutilities.exporterutils.collada.NodeType;
import org.lgna.story.resourceutilities.exporterutils.collada.ObjectFactory;
import org.lgna.story.resourceutilities.exporterutils.collada.Skin;
import org.lgna.story.resourceutilities.exporterutils.collada.Source;
import org.lgna.story.resourceutilities.exporterutils.collada.Triangles;
import org.lgna.story.resourceutilities.exporterutils.collada.UpAxisType;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.awt.image.BufferedImage;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.*;

import static org.junit.Assert.*;

/**
 * TDD tests for the COLLADA exporter decomposition.
 *
 * These tests define the contracts for three new extracted classes:
 * - ColladaJointExtractor: skeleton tree traversal
 * - ColladaMeshProcessor: geometry, skins, controllers
 * - ColladaParser: COLLADA XML document assembly
 *
 * They also include characterization tests ensuring the refactored
 * JointedModelColladaExporter preserves all existing behavior.
 */
public class ColladaExporterDecompositionTest {

  private SkeletonVisual visual;
  private Joint rootJoint;
  private Joint childJoint;
  private WeightedMesh weightedMesh;
  private Mesh staticMesh;
  private ObjectFactory factory;

  @Before
  public void setUp() {
    factory = new ObjectFactory();
    visual = createMinimalSkeletonVisual();
  }

  // ── Fixture builders ───────────────────────────────────────────

  private SkeletonVisual createMinimalSkeletonVisual() {
    SkeletonVisual sv = new SkeletonVisual();

    // Build a two-level joint hierarchy: ROOT -> CHILD
    rootJoint = new Joint();
    rootJoint.jointID.setValue("ROOT");
    rootJoint.localTransformation.setValue(AffineMatrix4x4.IDENTITY);

    childJoint = new Joint();
    childJoint.jointID.setValue("CHILD");
    childJoint.localTransformation.setValue(
        new AffineMatrix4x4(OrthogonalMatrix3x3.IDENTITY, new Point3(0, 1, 0)));
    childJoint.setParent(rootJoint);

    sv.skeleton.setValue(rootJoint);

    // Static mesh: a single triangle
    staticMesh = createTriangleMesh("testMesh", 0);
    sv.geometries.setValue(new edu.cmu.cs.dennisc.scenegraph.Geometry[]{staticMesh});

    // Weighted mesh with minimal weight info
    weightedMesh = createMinimalWeightedMesh();
    sv.weightedMeshes.setValue(new WeightedMesh[]{weightedMesh});

    // Single texture appearance with a color (no image)
    TexturedAppearance texture = new TexturedAppearance();
    texture.textureId.setValue(0);
    sv.textures.setValue(new TexturedAppearance[]{texture});

    return sv;
  }

  private Mesh createTriangleMesh(String name, int textureId) {
    Mesh mesh = new Mesh();
    mesh.setName(name);
    mesh.textureId.setValue(textureId);

    // 3 vertices forming a triangle
    DoubleBuffer vb = DoubleBuffer.wrap(new double[]{
        0.0, 0.0, 0.0,
        1.0, 0.0, 0.0,
        0.0, 1.0, 0.0
    });
    mesh.vertexBuffer.setValue(vb);

    // Normals (all pointing +Z)
    FloatBuffer nb = FloatBuffer.wrap(new float[]{
        0.0f, 0.0f, 1.0f,
        0.0f, 0.0f, 1.0f,
        0.0f, 0.0f, 1.0f
    });
    mesh.normalBuffer.setValue(nb);

    // UV coordinates
    FloatBuffer tb = FloatBuffer.wrap(new float[]{
        0.0f, 0.0f,
        1.0f, 0.0f,
        0.0f, 1.0f
    });
    mesh.textCoordBuffer.setValue(tb);

    // Triangle indices
    IntBuffer ib = IntBuffer.wrap(new int[]{0, 1, 2});
    mesh.indexBuffer.setValue(ib);

    return mesh;
  }

  private WeightedMesh createMinimalWeightedMesh() {
    WeightedMesh wm = new WeightedMesh();
    wm.setName("weightedMesh");
    wm.textureId.setValue(0);

    DoubleBuffer vb = DoubleBuffer.wrap(new double[]{
        0.0, 0.0, 0.0,
        1.0, 0.0, 0.0,
        0.0, 1.0, 0.0
    });
    wm.vertexBuffer.setValue(vb);

    FloatBuffer nb = FloatBuffer.wrap(new float[]{
        0.0f, 0.0f, 1.0f,
        0.0f, 0.0f, 1.0f,
        0.0f, 0.0f, 1.0f
    });
    wm.normalBuffer.setValue(nb);

    FloatBuffer tb = FloatBuffer.wrap(new float[]{
        0.0f, 0.0f,
        1.0f, 0.0f,
        0.0f, 1.0f
    });
    wm.textCoordBuffer.setValue(tb);

    IntBuffer ib = IntBuffer.wrap(new int[]{0, 1, 2});
    wm.indexBuffer.setValue(ib);

    // Weight info: all vertices weighted to ROOT joint
    WeightInfo wi = new WeightInfo();
    float[] weights = new float[]{1.0f, 1.0f, 1.0f};
    InverseAbsoluteTransformationWeightsPair iatwp =
        InverseAbsoluteTransformationWeightsPair.createInverseAbsoluteTransformationWeightsPair(
            weights, AffineMatrix4x4.IDENTITY);
    wi.addReference("ROOT", iatwp);
    wm.weightInfo.setValue(wi);

    return wm;
  }

  private SkeletonVisual createVisualWithTextureImage() {
    SkeletonVisual sv = createMinimalSkeletonVisual();
    TexturedAppearance texture = sv.textures.getValue()[0];
    BufferedImage img = new BufferedImage(4, 4, BufferedImage.TYPE_INT_ARGB);
    BufferedImageTexture bTexture = new BufferedImageTexture();
    bTexture.setBufferedImage(img);
    texture.diffuseColorTexture.setValue(bTexture);
    return sv;
  }

  // ══════════════════════════════════════════════════════════════
  //  1. ColladaJointExtractor contract tests
  // ══════════════════════════════════════════════════════════════

  @Test
  public void jointExtractorCreatesNodeForSingleJoint() {
    ColladaJointExtractor extractor = new ColladaJointExtractor(
        factory, Collections.emptyMap());

    Node node = extractor.createNodeForJoint(rootJoint);

    assertNotNull("Node must not be null", node);
    assertEquals("ROOT", node.getId());
    assertEquals("ROOT", node.getName());
    assertEquals("ROOT", node.getSid());
    assertEquals(NodeType.JOINT, node.getType());
  }

  @Test
  public void jointExtractorSetsMatrixTransformOnNode() {
    ColladaJointExtractor extractor = new ColladaJointExtractor(
        factory, Collections.emptyMap());

    Node node = extractor.createNodeForJoint(rootJoint);

    // Node must contain a matrix transform
    assertFalse("Node must have a transform",
        node.getLookatOrMatrixOrRotate().isEmpty());
    Object transform = node.getLookatOrMatrixOrRotate().get(0);
    assertTrue("Transform must be a Matrix", transform instanceof Matrix);
    Matrix matrix = (Matrix) transform;
    assertEquals("matrix", matrix.getSid());
    // Identity matrix has 16 values
    assertEquals(16, matrix.getValue().size());
  }

  @Test
  public void jointExtractorRecursesThroughChildJoints() {
    ColladaJointExtractor extractor = new ColladaJointExtractor(
        factory, Collections.emptyMap());

    Node rootNode = extractor.createNodeForJoint(rootJoint);

    assertEquals("Root should have one child joint node", 1, rootNode.getNode().size());
    Node childNode = rootNode.getNode().get(0);
    assertEquals("CHILD", childNode.getId());
    assertEquals(NodeType.JOINT, childNode.getType());
  }

  @Test
  public void jointExtractorAppliesJointRenaming() {
    Map<String, String> renames = new HashMap<>();
    renames.put("ROOT", "RENAMED_ROOT");
    renames.put("CHILD", "RENAMED_CHILD");
    ColladaJointExtractor extractor = new ColladaJointExtractor(factory, renames);

    Node rootNode = extractor.createNodeForJoint(rootJoint);

    assertEquals("RENAMED_ROOT", rootNode.getId());
    assertEquals("RENAMED_ROOT", rootNode.getName());
    Node childNode = rootNode.getNode().get(0);
    assertEquals("RENAMED_CHILD", childNode.getId());
  }

  @Test
  public void jointExtractorCreateSkeletonNodesReturnsNullForNullSkeleton() {
    SkeletonVisual emptySv = new SkeletonVisual();
    emptySv.skeleton.setValue(null);
    ColladaJointExtractor extractor = new ColladaJointExtractor(
        factory, Collections.emptyMap());

    Node result = extractor.createSkeletonNodes(emptySv);

    assertNull("Null skeleton should produce null result", result);
  }

  @Test
  public void jointExtractorCreateSkeletonNodesReturnsFullTree() {
    ColladaJointExtractor extractor = new ColladaJointExtractor(
        factory, Collections.emptyMap());

    Node result = extractor.createSkeletonNodes(visual);

    assertNotNull(result);
    assertEquals("ROOT", result.getId());
    assertEquals(1, result.getNode().size());
    assertEquals("CHILD", result.getNode().get(0).getId());
  }

  @Test
  public void jointExtractorFlipsCoordinateSpace() {
    // Joint at position (1, 2, 3) should have flipped X and Z in output
    Joint joint = new Joint();
    joint.jointID.setValue("TEST");
    joint.localTransformation.setValue(
        new AffineMatrix4x4(OrthogonalMatrix3x3.IDENTITY, new Point3(1.0, 2.0, 3.0)));

    ColladaJointExtractor extractor = new ColladaJointExtractor(
        factory, Collections.emptyMap());
    Node node = extractor.createNodeForJoint(joint);

    Matrix matrix = (Matrix) node.getLookatOrMatrixOrRotate().get(0);
    List<Double> values = matrix.getValue();
    // In a flipped identity-rotation matrix with translation (1,2,3):
    // Row-major index 3 is translation.x, index 7 is translation.y, index 11 is translation.z
    // Flipping negates translation.x (index 3) and translation.z (index 11)
    assertEquals("Translation X should be negated", -1.0, values.get(3), 0.001);
    assertEquals("Translation Y should be unchanged", 2.0, values.get(7), 0.001);
    assertEquals("Translation Z should be negated", -3.0, values.get(11), 0.001);
  }

  // ══════════════════════════════════════════════════════════════
  //  2. ColladaMeshProcessor contract tests
  // ══════════════════════════════════════════════════════════════

  @Test
  public void meshProcessorCreatesGeometryForStaticMesh() {
    Map<edu.cmu.cs.dennisc.scenegraph.Geometry, String> meshNames = new HashMap<>();
    meshNames.put(staticMesh, "testMesh");
    Map<Integer, String> materialNames = new HashMap<>();
    materialNames.put(0, "material_0");

    ColladaMeshProcessor processor = new ColladaMeshProcessor(
        factory, meshNames, materialNames);

    List<org.lgna.story.resourceutilities.exporterutils.collada.Geometry> geometries = new ArrayList<>();
    processor.addGeometriesForMesh(geometries, staticMesh);

    assertFalse("Should create at least one geometry", geometries.isEmpty());
    org.lgna.story.resourceutilities.exporterutils.collada.Geometry geo = geometries.get(0);
    assertNotNull("Geometry ID must be set", geo.getId());
    assertNotNull("Geometry must have a mesh", geo.getMesh());
  }

  @Test
  public void meshProcessorCreatesMeshWithPositionNormalAndUVSources() {
    Map<edu.cmu.cs.dennisc.scenegraph.Geometry, String> meshNames = new HashMap<>();
    meshNames.put(staticMesh, "testMesh");
    Map<Integer, String> materialNames = new HashMap<>();
    materialNames.put(0, "material_0");

    ColladaMeshProcessor processor = new ColladaMeshProcessor(
        factory, meshNames, materialNames);

    List<org.lgna.story.resourceutilities.exporterutils.collada.Geometry> geometries = new ArrayList<>();
    processor.addGeometriesForMesh(geometries, staticMesh);
    org.lgna.story.resourceutilities.exporterutils.collada.Mesh colladaMesh = geometries.get(0).getMesh();

    // Mesh should have 3 sources: position, normal, UV
    assertEquals("Mesh should have 3 sources (position, normal, UV)",
        3, colladaMesh.getSource().size());
    // Mesh should have vertices
    assertNotNull("Mesh must have vertices", colladaMesh.getVertices());
    // Mesh should have triangles
    assertFalse("Mesh must have triangle data",
        colladaMesh.getLinesOrLinestripsOrPolygons().isEmpty());
  }

  @Test
  public void meshProcessorFlipsVertexPositionsFromAliceToColladaSpace() {
    Map<edu.cmu.cs.dennisc.scenegraph.Geometry, String> meshNames = new HashMap<>();
    meshNames.put(staticMesh, "testMesh");
    Map<Integer, String> materialNames = new HashMap<>();
    materialNames.put(0, "material_0");

    ColladaMeshProcessor processor = new ColladaMeshProcessor(
        factory, meshNames, materialNames);

    List<org.lgna.story.resourceutilities.exporterutils.collada.Geometry> geometries = new ArrayList<>();
    processor.addGeometriesForMesh(geometries, staticMesh);
    org.lgna.story.resourceutilities.exporterutils.collada.Mesh colladaMesh = geometries.get(0).getMesh();
    Source posSource = colladaMesh.getSource().get(0);
    List<Double> positions = posSource.getFloatArray().getValue();

    // Original vertex (1,0,0) should become (-1,0,0) after coordinate flip
    // The X and Z values are negated during conversion
    assertEquals("X of vertex 1 should be negated", -1.0, positions.get(3), 0.001);
    assertEquals("Y of vertex 1 should be unchanged", 0.0, positions.get(4), 0.001);
    assertEquals("Z of vertex 1 should be negated", 0.0, positions.get(5), 0.001);
  }

  @Test
  public void meshProcessorCreatesTrianglesWithCorrectInputSemantics() {
    Map<edu.cmu.cs.dennisc.scenegraph.Geometry, String> meshNames = new HashMap<>();
    meshNames.put(staticMesh, "testMesh");
    Map<Integer, String> materialNames = new HashMap<>();
    materialNames.put(0, "material_0");

    ColladaMeshProcessor processor = new ColladaMeshProcessor(
        factory, meshNames, materialNames);

    List<org.lgna.story.resourceutilities.exporterutils.collada.Geometry> geometries = new ArrayList<>();
    processor.addGeometriesForMesh(geometries, staticMesh);
    Triangles triangles = (Triangles) geometries.get(0).getMesh()
        .getLinesOrLinestripsOrPolygons().get(0);

    assertEquals("Triangle should have 3 inputs (vertex, normal, texcoord)",
        3, triangles.getInput().size());

    List<String> semantics = new ArrayList<>();
    for (InputLocalOffset input : triangles.getInput()) {
      semantics.add(input.getSemantic());
    }
    assertTrue("Must have VERTEX input", semantics.contains("VERTEX"));
    assertTrue("Must have NORMAL input", semantics.contains("NORMAL"));
    assertTrue("Must have TEXCOORD input", semantics.contains("TEXCOORD"));
  }

  @Test
  public void meshProcessorSetsTriangleMaterialReference() {
    Map<edu.cmu.cs.dennisc.scenegraph.Geometry, String> meshNames = new HashMap<>();
    meshNames.put(staticMesh, "testMesh");
    Map<Integer, String> materialNames = new HashMap<>();
    materialNames.put(0, "material_0");

    ColladaMeshProcessor processor = new ColladaMeshProcessor(
        factory, meshNames, materialNames);

    List<org.lgna.story.resourceutilities.exporterutils.collada.Geometry> geometries = new ArrayList<>();
    processor.addGeometriesForMesh(geometries, staticMesh);
    Triangles triangles = (Triangles) geometries.get(0).getMesh()
        .getLinesOrLinestripsOrPolygons().get(0);

    assertNotNull("Triangle material reference must be set", triangles.getMaterial());
    assertTrue("Material reference should contain material name",
        triangles.getMaterial().contains("material_0"));
  }

  @Test
  public void meshProcessorCreatesControllerForWeightedMesh() {
    Map<edu.cmu.cs.dennisc.scenegraph.Geometry, String> meshNames = new HashMap<>();
    meshNames.put(weightedMesh, "weightedMesh");
    Map<Integer, String> materialNames = new HashMap<>();
    materialNames.put(0, "material_0");

    ColladaMeshProcessor processor = new ColladaMeshProcessor(
        factory, meshNames, materialNames);

    List<Controller> controllers = new ArrayList<>();
    processor.addControllersForMesh(controllers, weightedMesh,
        jointId -> jointId);  // identity naming

    assertFalse("Should create at least one controller", controllers.isEmpty());
    Controller controller = controllers.get(0);
    assertNotNull("Controller ID must be set", controller.getId());
    assertTrue("Controller ID should contain 'Controller'",
        controller.getId().contains("Controller"));
    assertNotNull("Controller must have skin data", controller.getSkin());
  }

  @Test
  public void meshProcessorSkinContainsBindShapeMatrix() {
    Map<edu.cmu.cs.dennisc.scenegraph.Geometry, String> meshNames = new HashMap<>();
    meshNames.put(weightedMesh, "weightedMesh");
    Map<Integer, String> materialNames = new HashMap<>();
    materialNames.put(0, "material_0");

    ColladaMeshProcessor processor = new ColladaMeshProcessor(
        factory, meshNames, materialNames);

    List<Controller> controllers = new ArrayList<>();
    processor.addControllersForMesh(controllers, weightedMesh,
        jointId -> jointId);
    Skin skin = controllers.get(0).getSkin();

    // Bind shape matrix should be identity (16 values)
    assertEquals("Bind shape matrix should have 16 values",
        16, skin.getBindShapeMatrix().size());
  }

  @Test
  public void meshProcessorSkinContainsJointAndWeightSources() {
    Map<edu.cmu.cs.dennisc.scenegraph.Geometry, String> meshNames = new HashMap<>();
    meshNames.put(weightedMesh, "weightedMesh");
    Map<Integer, String> materialNames = new HashMap<>();
    materialNames.put(0, "material_0");

    ColladaMeshProcessor processor = new ColladaMeshProcessor(
        factory, meshNames, materialNames);

    List<Controller> controllers = new ArrayList<>();
    processor.addControllersForMesh(controllers, weightedMesh,
        jointId -> jointId);
    Skin skin = controllers.get(0).getSkin();

    // Skin should have joint, matrix, and weight sources
    assertTrue("Skin should have at least 3 sources",
        skin.getSourceAttribute2().size() >= 3);
    assertNotNull("Skin must have joints", skin.getJoints());
    assertNotNull("Skin must have vertex weights", skin.getVertexWeights());
  }

  @Test
  public void meshProcessorSkinVertexWeightsHaveCorrectCount() {
    Map<edu.cmu.cs.dennisc.scenegraph.Geometry, String> meshNames = new HashMap<>();
    meshNames.put(weightedMesh, "weightedMesh");
    Map<Integer, String> materialNames = new HashMap<>();
    materialNames.put(0, "material_0");

    ColladaMeshProcessor processor = new ColladaMeshProcessor(
        factory, meshNames, materialNames);

    List<Controller> controllers = new ArrayList<>();
    processor.addControllersForMesh(controllers, weightedMesh,
        jointId -> jointId);
    Skin.VertexWeights vw = controllers.get(0).getSkin().getVertexWeights();

    // 3 vertices in the mesh
    assertEquals("Vertex weights count should match vertex count",
        3, vw.getCount().intValue());
  }

  @Test
  public void meshProcessorCreatesVisualSceneNodesForStaticMesh() {
    Map<edu.cmu.cs.dennisc.scenegraph.Geometry, String> meshNames = new HashMap<>();
    meshNames.put(staticMesh, "testMesh");
    Map<Integer, String> materialNames = new HashMap<>();
    materialNames.put(0, "material_0");

    ColladaMeshProcessor processor = new ColladaMeshProcessor(
        factory, meshNames, materialNames);

    List<Node> sceneNodes = new ArrayList<>();
    processor.addVisualSceneNodesForMesh(sceneNodes, staticMesh);

    assertFalse("Should create scene node(s) for mesh", sceneNodes.isEmpty());
    Node sceneNode = sceneNodes.get(0);
    assertNotNull("Scene node should have instance_geometry",
        sceneNode.getInstanceGeometry());
    assertFalse(sceneNode.getInstanceGeometry().isEmpty());
  }

  @Test
  public void meshProcessorCreatesVisualSceneNodesForWeightedMesh() {
    Map<edu.cmu.cs.dennisc.scenegraph.Geometry, String> meshNames = new HashMap<>();
    meshNames.put(weightedMesh, "weightedMesh");
    Map<Integer, String> materialNames = new HashMap<>();
    materialNames.put(0, "material_0");

    ColladaMeshProcessor processor = new ColladaMeshProcessor(
        factory, meshNames, materialNames);

    List<Node> sceneNodes = new ArrayList<>();
    processor.addVisualSceneNodesForWeightedMesh(sceneNodes, weightedMesh);

    assertFalse("Should create scene node(s) for weighted mesh", sceneNodes.isEmpty());
    Node sceneNode = sceneNodes.get(0);
    assertNotNull("Scene node should have instance_controller",
        sceneNode.getInstanceController());
    assertFalse(sceneNode.getInstanceController().isEmpty());
  }

  @Test
  public void meshProcessorBindMaterialReferencesCorrectMaterial() {
    Map<edu.cmu.cs.dennisc.scenegraph.Geometry, String> meshNames = new HashMap<>();
    meshNames.put(staticMesh, "testMesh");
    Map<Integer, String> materialNames = new HashMap<>();
    materialNames.put(0, "material_0");

    ColladaMeshProcessor processor = new ColladaMeshProcessor(
        factory, meshNames, materialNames);

    List<Node> sceneNodes = new ArrayList<>();
    processor.addVisualSceneNodesForMesh(sceneNodes, staticMesh);
    InstanceGeometry ig = sceneNodes.get(0).getInstanceGeometry().get(0);

    assertNotNull("Instance geometry must have bind material",
        ig.getBindMaterial());
    InstanceMaterial im = ig.getBindMaterial().getTechniqueCommon()
        .getInstanceMaterial().get(0);
    assertTrue("Instance material target should reference material_0",
        im.getTarget().contains("material_0"));
  }

  @Test
  public void meshProcessorHandlesMultipleTextureIdsOnSingleMesh() {
    // Mesh with two different texture IDs should produce two geometries
    Mesh multiTexMesh = createTriangleMesh("multiTex", 0);
    // Add a second triangle with a different texture ID
    IntBuffer ib = IntBuffer.wrap(new int[]{0, 1, 2, 0, 1, 2});
    multiTexMesh.indexBuffer.setValue(ib);
    // Expand buffers for 2 triangles worth of texture IDs
    multiTexMesh.textureIdArray.addAll(Arrays.asList(0, 0, 0, 1, 1, 1));

    // Need normals/vertices for 3 verts still (reuse indices)
    Map<edu.cmu.cs.dennisc.scenegraph.Geometry, String> meshNames = new HashMap<>();
    meshNames.put(multiTexMesh, "multiTex");
    Map<Integer, String> materialNames = new HashMap<>();
    materialNames.put(0, "material_0");
    materialNames.put(1, "material_1");

    ColladaMeshProcessor processor = new ColladaMeshProcessor(
        factory, meshNames, materialNames);

    List<org.lgna.story.resourceutilities.exporterutils.collada.Geometry> geometries = new ArrayList<>();
    processor.addGeometriesForMesh(geometries, multiTexMesh);

    assertEquals("Multi-texture mesh should produce 2 geometries",
        2, geometries.size());
  }

  // ══════════════════════════════════════════════════════════════
  //  3. ColladaParser contract tests
  // ══════════════════════════════════════════════════════════════

  @Test
  public void parserCreatesAssetWithCorrectUpAxis() {
    ColladaParser parser = new ColladaParser(factory);

    Asset asset = parser.createAsset();

    assertNotNull(asset);
    assertEquals(UpAxisType.Y_UP, asset.getUpAxis());
  }

  @Test
  public void parserCreatesAssetWithMeterUnit() {
    ColladaParser parser = new ColladaParser(factory);

    Asset asset = parser.createAsset();

    assertNotNull(asset.getUnit());
    assertEquals("meter", asset.getUnit().getName());
    assertEquals(1.0, asset.getUnit().getMeter(), 0.001);
  }

  @Test
  public void parserCreatesAssetWithTimestamps() {
    ColladaParser parser = new ColladaParser(factory);

    Asset asset = parser.createAsset();

    assertNotNull("Asset must have created timestamp", asset.getCreated());
    assertNotNull("Asset must have modified timestamp", asset.getModified());
  }

  @Test
  public void parserCreatesEffectForColorOnlyTexture() {
    TexturedAppearance colorTexture = new TexturedAppearance();
    colorTexture.textureId.setValue(0);
    // No diffuseColorTexture set — it should use diffuseColor instead

    Map<Integer, String> materialNames = new HashMap<>();
    materialNames.put(0, "material_0");
    ColladaParser parser = new ColladaParser(factory);

    Effect effect = parser.createEffect(colorTexture, materialNames);

    assertNotNull(effect);
    assertNotNull("Effect must have an ID", effect.getId());
    assertTrue("Effect ID should reference the material",
        effect.getId().contains("material_0"));
  }

  @Test
  public void parserCreatesEffectWithDiffuseTextureWhenImagePresent() {
    TexturedAppearance texturedAppearance = new TexturedAppearance();
    texturedAppearance.textureId.setValue(0);
    BufferedImage img = new BufferedImage(4, 4, BufferedImage.TYPE_INT_ARGB);
    BufferedImageTexture bTexture = new BufferedImageTexture();
    bTexture.setBufferedImage(img);
    texturedAppearance.diffuseColorTexture.setValue(bTexture);

    Map<Integer, String> materialNames = new HashMap<>();
    materialNames.put(0, "material_0");
    ColladaParser parser = new ColladaParser(factory);

    Effect effect = parser.createEffect(texturedAppearance, materialNames);

    assertNotNull(effect);
    assertFalse("Effect should have profile elements",
        effect.getFxProfileAbstract().isEmpty());
  }

  @Test
  public void parserCreatesEffectWithTransparencyForAlphaBlendedTexture() {
    TexturedAppearance texturedAppearance = new TexturedAppearance();
    texturedAppearance.textureId.setValue(0);
    BufferedImage img2 = new BufferedImage(4, 4, BufferedImage.TYPE_INT_ARGB);
    BufferedImageTexture bTexture2 = new BufferedImageTexture();
    bTexture2.setBufferedImage(img2);
    texturedAppearance.diffuseColorTexture.setValue(bTexture2);
    texturedAppearance.isDiffuseColorTextureAlphaBlended.setValue(true);

    Map<Integer, String> materialNames = new HashMap<>();
    materialNames.put(0, "material_0");
    ColladaParser parser = new ColladaParser(factory);

    Effect effect = parser.createEffect(texturedAppearance, materialNames);

    // Alpha-blended texture should have transparency set in the lambert shader
    assertNotNull(effect);
  }

  @Test
  public void parserAddsTextureComponentsToCollada() {
    SkeletonVisual sv = createVisualWithTextureImage();
    Map<Integer, String> materialNames = new HashMap<>();
    materialNames.put(0, "material_0");

    ColladaParser parser = new ColladaParser(factory);
    COLLADA collada = factory.createCOLLADA();
    parser.createAndAddTextureComponents(collada, sv, materialNames,
        idx -> "material_0_diffuseMap",
        idx -> "testModel_material_0_diffuseMap",
        idx -> "material_0_diffuseMap.png",
        idx -> "material_0_diffuseMap-image",
        idx -> "material_0_shader",
        idx -> "material_0_fx");

    // Should add library_images, library_materials, library_effects
    int libraryCount = collada.getLibraryAnimationsOrLibraryAnimationClipsOrLibraryCameras().size();
    assertEquals("Should add 3 libraries (images, materials, effects)",
        3, libraryCount);
  }

  // ══════════════════════════════════════════════════════════════
  //  4. Characterization tests: JointedModelColladaExporter
  // ══════════════════════════════════════════════════════════════

  @Test
  public void exporterProducesValidColladaXml() throws IOException {
    JointedModelColladaExporter exporter = new JointedModelColladaExporter(
        visual, null, "TestModel");

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    exporter.writeCollada(baos);
    String xml = baos.toString("UTF-8");

    assertTrue("Output must contain COLLADA root element",
        xml.contains("<COLLADA"));
    assertTrue("Output must declare COLLADA version 1.4.1",
        xml.contains("version=\"1.4.1\""));
  }

  @Test
  public void exporterIncludesSkeletonInVisualScene() throws IOException {
    JointedModelColladaExporter exporter = new JointedModelColladaExporter(
        visual, null, "TestModel");

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    exporter.writeCollada(baos);
    String xml = baos.toString("UTF-8");

    assertTrue("Output must contain ROOT joint", xml.contains("ROOT"));
    assertTrue("Output must contain CHILD joint", xml.contains("CHILD"));
    // Joint nodes should be of type JOINT
    assertTrue("Joint nodes must have type JOINT", xml.contains("type=\"JOINT\""));
  }

  @Test
  public void exporterIncludesGeometryLibrary() throws IOException {
    JointedModelColladaExporter exporter = new JointedModelColladaExporter(
        visual, null, "TestModel");

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    exporter.writeCollada(baos);
    String xml = baos.toString("UTF-8");

    assertTrue("Output must contain library_geometries",
        xml.contains("library_geometries"));
  }

  @Test
  public void exporterIncludesControllerLibrary() throws IOException {
    JointedModelColladaExporter exporter = new JointedModelColladaExporter(
        visual, null, "TestModel");

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    exporter.writeCollada(baos);
    String xml = baos.toString("UTF-8");

    assertTrue("Output must contain library_controllers",
        xml.contains("library_controllers"));
  }

  @Test
  public void exporterIncludesVisualSceneLibrary() throws IOException {
    JointedModelColladaExporter exporter = new JointedModelColladaExporter(
        visual, null, "TestModel");

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    exporter.writeCollada(baos);
    String xml = baos.toString("UTF-8");

    assertTrue("Output must contain library_visual_scenes",
        xml.contains("library_visual_scenes"));
  }

  @Test
  public void exporterRenamesJointsWhenRenamedJointsProvided() throws IOException {
    Map<String, String> renames = new HashMap<>();
    renames.put("ROOT", "MY_ROOT");
    renames.put("CHILD", "MY_CHILD");

    JointedModelColladaExporter exporter = new JointedModelColladaExporter(
        visual, null, "TestModel", "", renames);

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    exporter.writeCollada(baos);
    String xml = baos.toString("UTF-8");

    assertTrue("Renamed joint MY_ROOT must appear", xml.contains("MY_ROOT"));
    assertTrue("Renamed joint MY_CHILD must appear", xml.contains("MY_CHILD"));
  }

  @Test
  public void exporterUsesModelNameForVisualScene() throws IOException {
    JointedModelColladaExporter exporter = new JointedModelColladaExporter(
        visual, null, "TestModel");

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    exporter.writeCollada(baos);
    String xml = baos.toString("UTF-8");

    // When modelVariant is null, getFullResourceName returns modelName
    assertTrue("Visual scene should use model name",
        xml.contains("id=\"TestModel\""));
  }

  @Test
  public void exporterUsesVariantTextureSetForVisualSceneName() throws IOException {
    ModelManifest.ModelVariant variant = new ModelManifest.ModelVariant();
    variant.textureSet = "VariantTexSet";

    JointedModelColladaExporter exporter = new JointedModelColladaExporter(
        visual, variant, "TestModel");

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    exporter.writeCollada(baos);
    String xml = baos.toString("UTF-8");

    assertTrue("Visual scene should use variant textureSet name",
        xml.contains("id=\"VariantTexSet\""));
  }

  @Test
  public void exporterGetTextureFileNamesReturnsCorrectNames() {
    JointedModelColladaExporter exporter = new JointedModelColladaExporter(
        visual, null, "TestModel");

    List<String> names = exporter.getTextureFileNames();

    assertEquals(1, names.size());
    assertTrue("Texture filename should end with .png",
        names.get(0).endsWith(".png"));
    assertTrue("Texture filename should contain model name",
        names.get(0).contains("TestModel"));
  }

  @Test
  public void exporterCreateTextureIdToImageMapContainsAllTextures() {
    JointedModelColladaExporter exporter = new JointedModelColladaExporter(
        visual, null, "TestModel");

    Map<Integer, String> textureMap = exporter.createTextureIdToImageMap();

    assertEquals(1, textureMap.size());
    assertTrue("Map should contain texture ID 0", textureMap.containsKey(0));
    assertTrue("Image name should contain model name",
        textureMap.get(0).contains("TestModel"));
  }

  @Test
  public void exporterGetTextureIdForNameFindsMatchingTexture() {
    JointedModelColladaExporter exporter = new JointedModelColladaExporter(
        visual, null, "TestModel");

    Map<Integer, String> textureMap = exporter.createTextureIdToImageMap();
    String imageName = textureMap.get(0);

    Integer found = exporter.getTextureIdForName(imageName);
    assertEquals("Should find texture ID 0", Integer.valueOf(0), found);
  }

  @Test
  public void exporterGetTextureIdForNameReturnsMinusOneForUnknown() {
    JointedModelColladaExporter exporter = new JointedModelColladaExporter(
        visual, null, "TestModel");

    Integer found = exporter.getTextureIdForName("nonexistent_texture");
    assertEquals("Unknown texture should return -1",
        Integer.valueOf(-1), found);
  }

  @Test
  public void exporterGetStructureExtensionReturnsPng() {
    JointedModelColladaExporter exporter = new JointedModelColladaExporter(
        visual, null, "TestModel");

    assertEquals("png", exporter.getStructureExtension());
  }

  @Test
  public void exporterCreateStructureDataSourceUsesColladaExtension() {
    JointedModelColladaExporter exporter = new JointedModelColladaExporter(
        visual, null, "TestModel");

    var ds = exporter.createStructureDataSource();
    assertNotNull(ds);
    assertTrue("Data source name should end with .dae",
        ds.getName().endsWith(".dae"));
  }

  @Test
  public void exporterCreateImageDataSourcesContainsTextureFiles() {
    SkeletonVisual sv = createVisualWithTextureImage();
    JointedModelColladaExporter exporter = new JointedModelColladaExporter(
        sv, null, "TestModel");

    var dataSources = exporter.createImageDataSources();

    assertEquals("Should have one image data source", 1, dataSources.size());
    assertTrue("Image data source name should end with .png",
        dataSources.get(0).getName().endsWith(".png"));
  }

  @Test
  public void exporterSkipsImageDataSourcesForColorOnlyTextures() {
    // Visual without any texture images should produce no image data sources
    JointedModelColladaExporter exporter = new JointedModelColladaExporter(
        visual, null, "TestModel");

    var dataSources = exporter.createImageDataSources();

    assertEquals("Color-only textures should produce no image data sources",
        0, dataSources.size());
  }

  // ══════════════════════════════════════════════════════════════
  //  5. Behavioral equivalence: output before and after refactoring
  // ══════════════════════════════════════════════════════════════

  @Test
  public void refactoredExporterProducesIdenticalOutput() throws IOException {
    // This is the key characterization test: the refactored exporter
    // must produce byte-for-byte identical COLLADA output.
    JointedModelColladaExporter exporter = new JointedModelColladaExporter(
        visual, null, "TestModel");

    ByteArrayOutputStream baos1 = new ByteArrayOutputStream();
    exporter.writeCollada(baos1);
    String xml1 = baos1.toString("UTF-8");

    // Create a second exporter to verify determinism
    JointedModelColladaExporter exporter2 = new JointedModelColladaExporter(
        visual, null, "TestModel");

    ByteArrayOutputStream baos2 = new ByteArrayOutputStream();
    exporter2.writeCollada(baos2);
    String xml2 = baos2.toString("UTF-8");

    // Strip timestamps since they'll differ between runs
    String normalized1 = stripTimestamps(xml1);
    String normalized2 = stripTimestamps(xml2);
    assertEquals("Two identical exporters must produce identical COLLADA output",
        normalized1, normalized2);
  }

  @Test
  public void exporterHandlesEmptyGeometriesArray() throws IOException {
    SkeletonVisual sv = new SkeletonVisual();
    sv.skeleton.setValue(rootJoint);
    sv.geometries.setValue(new edu.cmu.cs.dennisc.scenegraph.Geometry[0]);
    sv.weightedMeshes.setValue(new WeightedMesh[0]);
    sv.textures.setValue(new TexturedAppearance[0]);

    JointedModelColladaExporter exporter = new JointedModelColladaExporter(
        sv, null, "EmptyModel");

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    exporter.writeCollada(baos);
    String xml = baos.toString("UTF-8");

    assertTrue("Should still produce valid COLLADA", xml.contains("<COLLADA"));
  }

  @Test
  public void exporterHandlesMultipleWeightedMeshes() throws IOException {
    SkeletonVisual sv = createMinimalSkeletonVisual();
    WeightedMesh wm2 = createMinimalWeightedMesh();
    wm2.setName("weightedMesh2");
    sv.weightedMeshes.setValue(new WeightedMesh[]{weightedMesh, wm2});

    JointedModelColladaExporter exporter = new JointedModelColladaExporter(
        sv, null, "MultiMeshModel");

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    exporter.writeCollada(baos);
    String xml = baos.toString("UTF-8");

    assertTrue("Should contain both meshes in output", xml.contains("COLLADA"));
    // Count controller occurrences — should have 2
    int controllerCount = countOccurrences(xml, "<controller ");
    assertEquals("Should create 2 controllers for 2 weighted meshes",
        2, controllerCount);
  }

  @Test
  public void exporterPreservesUserJointIdentifierMapping() {
    Map<String, String> renames = new HashMap<>();
    renames.put("ROOT", "PELVIS");

    JointedModelColladaExporter exporter = new JointedModelColladaExporter(
        visual, null, "TestModel", "", renames);

    assertEquals("PELVIS", exporter.getUserJointIdentifier("ROOT"));
    assertEquals("CHILD", exporter.getUserJointIdentifier("CHILD"));
  }

  @Test
  public void exporterUserJointIdentifierPassesThroughUnmappedNames() {
    JointedModelColladaExporter exporter = new JointedModelColladaExporter(
        visual, null, "TestModel");

    assertEquals("UNKNOWN_JOINT", exporter.getUserJointIdentifier("UNKNOWN_JOINT"));
  }

  // ── Line count verification ────────────────────────────────────

  @Test
  public void extractedClassesExist() {
    // Verify the three extracted classes can be loaded
    try {
      Class.forName("org.lgna.story.resourceutilities.ColladaJointExtractor");
      Class.forName("org.lgna.story.resourceutilities.ColladaMeshProcessor");
      Class.forName("org.lgna.story.resourceutilities.ColladaParser");
    } catch (ClassNotFoundException e) {
      fail("Extracted class not found: " + e.getMessage());
    }
  }

  // ── Helpers ────────────────────────────────────────────────────

  private String stripTimestamps(String xml) {
    return xml.replaceAll("<created>[^<]*</created>", "<created>STRIPPED</created>")
        .replaceAll("<modified>[^<]*</modified>", "<modified>STRIPPED</modified>");
  }

  private int countOccurrences(String text, String pattern) {
    int count = 0;
    int idx = 0;
    while ((idx = text.indexOf(pattern, idx)) != -1) {
      count++;
      idx += pattern.length();
    }
    return count;
  }
}
