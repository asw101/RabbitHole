package org.lgna.story.resourceutilities;

import com.dddviewr.collada.Collada;
import com.dddviewr.collada.nodes.Node;
import com.dddviewr.collada.visualscene.*;
import edu.cmu.cs.dennisc.scenegraph.*;
import org.alice.math.immutable.*;
import org.alice.math.immutable.AngleInDegrees;
import org.lgna.story.implementation.JointedModelImp.VisualData;
import org.lgna.story.resources.ImplementationAndVisualType;
import org.lgna.story.resources.JointedModelResource;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JointedModelColladaImporter {
  private final File colladaModelFile;
  private final Logger modelLoadingLogger;
  private final File rootPath;
  private Orientation orientation;

  public JointedModelColladaImporter(File colladaModelFile, Logger modelLoadingLogger) {
    this.colladaModelFile = colladaModelFile;
    this.modelLoadingLogger = modelLoadingLogger;
    this.rootPath = colladaModelFile.getParentFile();
  }

  private static boolean nodeIsJoint(Node n) {
    return (n.getType() != null && n.getType().equals("JOINT"));
  }

  private Node findNodeNamedRoot(List<Node> nodes) {
    for (Node n : nodes) {
      if (nodeIsJoint(n) && getJointIdentifier(n).equalsIgnoreCase("root")) {
        return n;
      }
    }
    for (Node n : nodes) {
      Node childRoot = findNodeNamedRoot(n.getChildNodes());
      if (childRoot != null) {
        return childRoot;
      }
    }
    return null;
  }

  private static Node findFirstJoint(List<Node> nodes) {
    for (Node n : nodes) {
      if (nodeIsJoint(n)) {
        return n;
      }
    }
    for (Node n : nodes) {
      Node childRoot = findFirstJoint(n.getChildNodes());
      if (childRoot != null) {
        return childRoot;
      }
    }
    return null;
  }

  private AffineMatrix4x4 collapsedRootTransform(List<Node> nodes, Node root) throws ModelLoadingException {
    if (nodes.contains(root)) {
      return AffineMatrix4x4.IDENTITY;
    }
    for (Node n : nodes) {
      AffineMatrix4x4 childTransform = collapsedRootTransform(n.getChildNodes(), root);
      if (childTransform != null) {
        final AffineMatrix4x4 nodeTransform = getNodeTransform(n);
        return nodeTransform.times(childTransform);
      }
    }
    return null;
  }

  private Node findRootNode(List<Node> nodes) {
    Node rootNode = findNodeNamedRoot(nodes);
    if (rootNode != null) {
      return rootNode;
    }
    rootNode = findFirstJoint(nodes);
    if (rootNode != null) {
      return rootNode;
    }
    return null;
  }

  private AffineMatrix4x4 colladaMatrixToAliceMatrix(Matrix m) throws ModelLoadingException {
    return ColladaImportMeshBuilder.floatArrayToAliceMatrix(m.getData(), orientation);
  }

  private Joint createAliceSkeletonFromNode(Node node) throws ModelLoadingException {
    Joint j = new Joint();
    j.jointID.setValue(getJointIdentifier(node));
    j.setName(getJointIdentifier(node));

    j.localTransformation.setValue(getNodeTransform(node));
    for (Node child : node.getChildNodes()) {
      if (nodeIsJoint(child)) {
        Joint childJoint = createAliceSkeletonFromNode(child);
        childJoint.setParent(j);
      }
    }
    return j;
  }

  private String getJointIdentifier(Node node) {
    String sid = node.getSid();
    String name = node.getName();
    if (sid == null || "".equals(sid)) {
      return name;
    }
    if (name != null && !"".equals(name) && !name.equals(sid)) {
      modelLoadingLogger.log(Level.WARNING, "Incoming Joint has scoped ID (sid) '" + sid + "' that differs from name '" + name + "'. Using sid as identifier. This may conflict with export assumptions.");
    }
    return sid;
  }

  private AffineMatrix4x4 getNodeTransform(Node node) throws ModelLoadingException {
    AffineMatrix4x4 aliceMatrix = AffineMatrix4x4.IDENTITY;
    for (int i = 0; i < node.getXforms().size(); i++) {
      BaseXform xform = node.getXforms().get(i);
      if (xform instanceof Matrix matrix) {
        aliceMatrix = colladaMatrixToAliceMatrix(matrix);
      } else if (xform instanceof Translate translate) {
        // TODO orient to Alice
        aliceMatrix = aliceMatrix.withTranslation(new Point3(translate.getX(), translate.getY(), translate.getZ()));
      } else if (xform instanceof Scale scale) {
        // TODO orient to Alice
        OrthogonalMatrix3x3 scaleMatrix = new OrthogonalMatrix3x3(
            new Vector3(scale.getX(), 0, 0),
            new Vector3(0, scale.getY(), 0),
            new Vector3(0, 0, scale.getZ()));
        aliceMatrix = new AffineMatrix4x4((OrthogonalMatrix3x3) aliceMatrix.orientation().times(scaleMatrix), aliceMatrix.translation());
      } else if (xform instanceof Rotate rotate) {
        // TODO orient to Alice
        Vector3 axis = new Vector3(rotate.getX(), rotate.getY(), rotate.getZ());
        aliceMatrix = new AffineMatrix4x4(
            aliceMatrix.orientation().applyRotationAboutArbitraryAxis(axis, new AngleInDegrees(rotate.getAngle())),
            aliceMatrix.translation());
      }
    }
    return aliceMatrix;
  }

  public static SkeletonVisual loadAliceModel(JointedModelResource resource) {
    VisualData<JointedModelResource> v = ImplementationAndVisualType.ALICE.getFactory(resource).createVisualData();
    SkeletonVisual sv = (SkeletonVisual) v.getSgVisuals()[0];
    return sv;
  }

  private Collada readColladaModel() throws ModelLoadingException {
    Collada colladaModel;
    try {
      colladaModel = Collada.readFile(colladaModelFile.getAbsolutePath());
    } catch (SAXException | IOException e) {
      throw new ModelLoadingException("Failed to load collada file " + colladaModelFile, e);
    } catch (ClassCastException e) {
      throw new ModelLoadingException("Failed to load collada file " + colladaModelFile + ".\nIf there are nested animations they should be removed.", e);
    }
    orientation = Orientation.forUpAxis(colladaModel.getUpAxis());
    colladaModel.deindexMeshes();
    return colladaModel;
  }

  public SkeletonVisual loadSkeletonVisual() throws ModelLoadingException {
    assert modelLoadingLogger != null;

    Collada colladaModel = readColladaModel();

    VisualScene scene = colladaModel.getLibraryVisualScenes().getScene(colladaModel.getScene().getInstanceVisualScene().getUrl());
    if (scene == null) {
      throw new ModelLoadingException("Error processing model: No scene found.");
    }

    Node rootNode = findRootNode(scene.getNodes());
    Joint aliceSkeleton = null;
    if (rootNode != null) {
      aliceSkeleton = createAliceSkeletonFromNode(rootNode);
      AffineMatrix4x4 rootTransform = collapsedRootTransform(scene.getNodes(), rootNode);
      if (rootTransform != null && !rootTransform.isIdentity()) {
        rootTransform = rootTransform.times(aliceSkeleton.getLocalTransformation());
        aliceSkeleton.setLocalTransformation(rootTransform);
      }
    }

    ColladaImportMeshBuilder meshBuilder = new ColladaImportMeshBuilder(orientation, modelLoadingLogger);
    List<Mesh> aliceMeshes = meshBuilder.createAliceMeshesFromCollada(colladaModel);
    if (aliceMeshes.isEmpty()) {
      throw new ModelLoadingException("Error processing model: No valid meshes found.");
    }

    SkeletonVisual skeletonVisual = new SkeletonVisual();
    skeletonVisual.setName(baseFileName(colladaModelFile.getName()));
    skeletonVisual.frontFacingAppearance.setValue(new SimpleAppearance());
    skeletonVisual.skeleton.setValue(aliceSkeleton);
    List<Mesh> aliceGeometry = new ArrayList<>();
    List<WeightedMesh> aliceWeightedMeshes = new ArrayList<>();
    for (Mesh mesh : aliceMeshes) {
      if (mesh instanceof WeightedMesh weightedMesh) {
        weightedMesh.skeleton.setValue(aliceSkeleton);
        aliceWeightedMeshes.add(weightedMesh);
      } else {
        aliceGeometry.add(mesh);
      }
    }

    skeletonVisual.geometries.setValue(aliceGeometry.toArray(new Mesh[aliceGeometry.size()]));
    skeletonVisual.weightedMeshes.setValue(aliceWeightedMeshes.toArray(new WeightedMesh[aliceWeightedMeshes.size()]));

    float extraScale = colladaModel.getUnit().getMeter();
    if (extraScale != 1.0f) {
      skeletonVisual.scale(extraScale);
    }

    ColladaImportMaterialLoader materialLoader = new ColladaImportMaterialLoader(modelLoadingLogger);
    List<TexturedAppearance> sgTextureAppearances = materialLoader.createAliceMaterialsFromCollada(colladaModel, rootPath, aliceMeshes);
    skeletonVisual.textures.setValue(sgTextureAppearances.toArray(new TexturedAppearance[sgTextureAppearances.size()]));

    UtilitySkeletonVisualAdapter skeletonVisualAdapter = new UtilitySkeletonVisualAdapter();
    skeletonVisualAdapter.initialize(skeletonVisual);
    skeletonVisualAdapter.processWeightedMesh();
    skeletonVisualAdapter.initializeJointBoundingBoxes();
    AxisAlignedBox absoluteBBox = skeletonVisualAdapter.getAbsoluteBoundingBox();
    if (skeletonVisual.geometries.getValue() != null) {
      for (edu.cmu.cs.dennisc.scenegraph.Geometry g : skeletonVisual.geometries.getValue()) {
        absoluteBBox.union(g.getAxisAlignedMinimumBoundingBox());
      }
    }
    skeletonVisual.baseBoundingBox.setValue(absoluteBBox);
    skeletonVisualAdapter.handleReleased();
    skeletonVisual.setTracker(null);

    return skeletonVisual;
  }

  private String baseFileName(String fileName) {
    if (fileName == null || fileName.isEmpty()) {
      return "Model";
    }
    int pos = fileName.lastIndexOf(".");
    if (pos < 0) {
      return  fileName;
    }
    return fileName.substring(0, pos);
  }
}
