package org.lgna.story.resourceutilities;

import com.dddviewr.collada.Accessor;
import com.dddviewr.collada.Collada;
import com.dddviewr.collada.FloatArray;
import com.dddviewr.collada.Input;
import com.dddviewr.collada.NameArray;
import com.dddviewr.collada.Source;
import com.dddviewr.collada.Vcount;
import com.dddviewr.collada.controller.Controller;
import com.dddviewr.collada.controller.Joints;
import com.dddviewr.collada.controller.LibraryControllers;
import com.dddviewr.collada.controller.Skin;
import com.dddviewr.collada.controller.VertexWeights;
import com.dddviewr.collada.geometry.Geometry;
import com.dddviewr.collada.geometry.LibraryGeometries;
import com.dddviewr.collada.geometry.Lines;
import com.dddviewr.collada.geometry.Mesh;
import com.dddviewr.collada.geometry.Triangles;
import com.dddviewr.collada.geometry.Vertices;
import com.dddviewr.collada.materials.LibraryMaterials;
import com.dddviewr.collada.materials.Material;
import edu.cmu.cs.dennisc.scenegraph.WeightedMesh;
import org.junit.Test;

import java.util.logging.Logger;

import static org.junit.Assert.*;

public class ColladaImportMeshBuilderTest {
  @Test
  public void materialIndexAndControllerLookupHandleMissingValues() {
    Geometry geometry = createGeometry("geo-1", "body", true, true, true);
    Collada collada = createCollada(geometry);
    attachController(collada, geometry);

    assertEquals(0, ColladaImportMeshBuilder.getMaterialIndex("mat0", collada));
    assertEquals(-1, ColladaImportMeshBuilder.getMaterialIndex("missing", collada));
    assertEquals(-1, ColladaImportMeshBuilder.getMaterialIndex(null, collada));
    assertNotNull(ColladaImportMeshBuilder.getControllerForGeometry(geometry, collada));
    assertNull(
        ColladaImportMeshBuilder.getControllerForGeometry(
            createGeometry("geo-2", "other", true, true, true), collada
        )
    );
  }

  @Test
  public void createAliceSGMeshFromGeometryBuildsWeightedMeshAndWeights() throws Exception {
    Geometry geometry = createGeometry("geo-1", "body", true, true, true);
    Collada collada = createCollada(geometry);
    attachController(collada, geometry);

    ColladaImportMeshBuilder builder = new ColladaImportMeshBuilder(Orientation.forAlice(), Logger.getLogger("test"));
    edu.cmu.cs.dennisc.scenegraph.Mesh mesh = builder.createAliceSGMeshFromGeometry(geometry, collada);

    assertTrue(mesh instanceof WeightedMesh);
    assertEquals("body", mesh.getName());
    assertEquals(0, mesh.textureId.getValue().intValue());
    assertEquals(-1.0, mesh.vertexBuffer.getValue().get(0), 0.0001);
    assertEquals(2.0, mesh.vertexBuffer.getValue().get(1), 0.0001);
    assertEquals(-3.0, mesh.vertexBuffer.getValue().get(2), 0.0001);

    WeightedMesh weightedMesh = (WeightedMesh) mesh;
    assertEquals(1, weightedMesh.weightInfo.getValue().getMap().size());
    assertTrue(weightedMesh.weightInfo.getValue().getMap().containsKey("jointA"));
  }

  @Test
  public void createAliceSGMeshFromGeometryRejectsMissingNormals() {
    Geometry geometry = createGeometry("geo-1", "body", false, true, true);
    Collada collada = createCollada(geometry);
    ColladaImportMeshBuilder builder = new ColladaImportMeshBuilder(Orientation.forAlice(), Logger.getLogger("test"));

    try {
      builder.createAliceSGMeshFromGeometry(geometry, collada);
      fail("Expected missing normals to fail");
    } catch (ModelLoadingException exception) {
      assertTrue(exception.getMessage().contains("No normal data found"));
    }
  }

  @Test
  public void createAliceSGMeshFromGeometryRejectsMissingTextureCoordinates() {
    Geometry geometry = createGeometry("geo-1", "body", true, false, true);
    Collada collada = createCollada(geometry);
    ColladaImportMeshBuilder builder = new ColladaImportMeshBuilder(Orientation.forAlice(), Logger.getLogger("test"));

    try {
      builder.createAliceSGMeshFromGeometry(geometry, collada);
      fail("Expected missing texture coordinates to fail");
    } catch (ModelLoadingException exception) {
      assertTrue(exception.getMessage().contains("No texture coordinate data found"));
    }
  }

  @Test
  public void createAliceSGMeshFromGeometryReturnsNullWithoutTriangles() throws Exception {
    Geometry geometry = createGeometry("geo-1", "body", true, true, false);
    Collada collada = createCollada(geometry);
    ColladaImportMeshBuilder builder = new ColladaImportMeshBuilder(Orientation.forAlice(), Logger.getLogger("test"));

    assertNull(builder.createAliceSGMeshFromGeometry(geometry, collada));
  }

  private static Collada createCollada(Geometry geometry) {
    Collada collada = new Collada();
    LibraryGeometries libraryGeometries = new LibraryGeometries();
    libraryGeometries.addGeometry(geometry);
    collada.setLibraryGeometries(libraryGeometries);

    LibraryMaterials libraryMaterials = new LibraryMaterials();
    libraryMaterials.addMaterial(new Material("mat0", "mat0"));
    collada.setLibraryMaterials(libraryMaterials);
    return collada;
  }

  private static void attachController(Collada collada, Geometry geometry) {
    Skin skin = new Skin("#" + geometry.getId());
    skin.parseBindShapeMatrix(new StringBuilder("1 0 0 0 0 1 0 0 0 0 1 0 0 0 0 1"));
    skin.addSource(nameSource("joint-source", new String[]{"jointA"}));
    skin.addSource(floatSource("inverse-bind-source", new float[]{
        1, 0, 0, 0,
        0, 1, 0, 0,
        0, 0, 1, 0,
        0, 0, 0, 1,
    }, 16));
    skin.addSource(floatSource("weight-source", new float[]{1.0f, 0.5f, 0.25f}, 1));

    Joints joints = new Joints();
    joints.addInput(new Input("JOINT", "#joint-source"));
    joints.addInput(new Input("INV_BIND_MATRIX", "#inverse-bind-source"));
    skin.setJoints(joints);

    VertexWeights vertexWeights = new VertexWeights(3);
    Input jointInput = new Input("JOINT", "#joint-source");
    jointInput.setOffset(0);
    Input weightInput = new Input("WEIGHT", "#weight-source");
    weightInput.setOffset(1);
    vertexWeights.addInput(jointInput);
    vertexWeights.addInput(weightInput);
    Vcount vcount = new Vcount();
    vcount.setData(new int[]{1, 1, 1});
    vertexWeights.setVcount(vcount);
    vertexWeights.setData(new int[]{0, 0, 0, 1, 0, 2});
    skin.setVertexWeights(vertexWeights);

    Controller controller = new Controller("controller0", "controller0");
    controller.setSkin(skin);
    LibraryControllers libraryControllers = new LibraryControllers();
    libraryControllers.addController(controller);
    collada.setLibraryControllers(libraryControllers);
  }

  private static Geometry createGeometry(
      String id,
      String name,
      boolean includeNormals,
      boolean includeTexcoords,
      boolean includeTriangles
  ) {
    Mesh mesh = new Mesh();
    mesh.addSource(floatSource("positions", new float[]{
        1, 2, 3,
        4, 5, 6,
        7, 8, 9,
    }, 3));
    if (includeNormals) {
      mesh.addSource(floatSource("normals", new float[]{
          0, 0, 1,
          0, 1, 0,
          1, 0, 0,
      }, 3));
    }
    if (includeTexcoords) {
      mesh.addSource(floatSource("texcoords", new float[]{
          0, 0,
          1, 0,
          0, 1,
      }, 2));
    }
    Vertices vertices = new Vertices("vertices");
    vertices.addInput(new Input("POSITION", "#positions"));
    mesh.setVertices(vertices);
    if (includeTriangles) {
      Triangles triangles = new Triangles("mat0", 1);
      Input vertexInput = new Input("VERTEX", "#vertices");
      vertexInput.setOffset(0);
      triangles.addInput(vertexInput);
      if (includeNormals) {
        Input normalInput = new Input("NORMAL", "#normals");
        normalInput.setOffset(0);
        triangles.addInput(normalInput);
      }
      if (includeTexcoords) {
        Input texcoordInput = new Input("TEXCOORD", "#texcoords");
        texcoordInput.setOffset(0);
        triangles.addInput(texcoordInput);
      }
      triangles.setData(new int[]{0, 1, 2});
      mesh.addPrimitives(triangles);
    } else {
      Lines lines = new Lines("mat0", 1);
      Input vertexInput = new Input("VERTEX", "#vertices");
      vertexInput.setOffset(0);
      lines.addInput(vertexInput);
      if (includeNormals) {
        Input normalInput = new Input("NORMAL", "#normals");
        normalInput.setOffset(0);
        lines.addInput(normalInput);
      }
      if (includeTexcoords) {
        Input texcoordInput = new Input("TEXCOORD", "#texcoords");
        texcoordInput.setOffset(0);
        lines.addInput(texcoordInput);
      }
      lines.setData(new int[]{0, 1, 2});
      mesh.addPrimitives(lines);
    }
    Geometry geometry = new Geometry(id, name);
    geometry.setMesh(mesh);
    return geometry;
  }

  private static Source floatSource(String id, float[] data, int stride) {
    Source source = new Source(id, id);
    FloatArray array = new FloatArray(id + "-array", data.length);
    array.setData(data);
    source.setFloatArray(array);
    source.setAccessor(new Accessor("#" + array.getId(), data.length / stride, stride));
    return source;
  }

  private static Source nameSource(String id, String[] data) {
    Source source = new Source(id, id);
    NameArray array = new NameArray(id + "-array", data.length);
    array.setData(data);
    source.setNameArray(array);
    source.setAccessor(new Accessor("#" + array.getId(), data.length, 1));
    return source;
  }
}
