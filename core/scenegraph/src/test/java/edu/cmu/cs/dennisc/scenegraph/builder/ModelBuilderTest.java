package edu.cmu.cs.dennisc.scenegraph.builder;

import edu.cmu.cs.dennisc.scenegraph.Geometry;
import edu.cmu.cs.dennisc.scenegraph.IndexedTriangleArray;
import edu.cmu.cs.dennisc.scenegraph.TexturedAppearance;
import edu.cmu.cs.dennisc.scenegraph.Transformable;
import edu.cmu.cs.dennisc.scenegraph.Vertex;
import edu.cmu.cs.dennisc.scenegraph.Visual;
import edu.cmu.cs.dennisc.texture.BufferedImageTexture;
import org.alice.math.immutable.AffineMatrix4x4;
import org.alice.math.immutable.Matrix3x3;
import org.alice.math.immutable.Vector3;
import org.junit.Test;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;

import static org.junit.Assert.*;

public class ModelBuilderTest {
  private static final double EPSILON = 0.000001;

  @Test
  public void newInstanceBuildTransformableAndReplaceGeometriesHandleMixedGeometryTypes() {
    Transformable source = createSourceModel();

    ModelBuilder builder = ModelBuilder.newInstance(source);
    assertEquals(2, builder.getGeometries().size());

    Transformable built = builder.buildTransformable();
    assertEquals(2, built.getComponentCount());
    assertEquals(1.0, built.localTransformation.getValue().translation().x(), EPSILON);

    Visual rootVisual = (Visual) built.getComponentAt(0);
    assertTrue(rootVisual.getGeometry() instanceof IndexedTriangleArray);
    Vertex scaledVertex = ((IndexedTriangleArray) rootVisual.getGeometry()).vertices.getValue()[0];
    assertEquals(2.0, scaledVertex.position.x(), EPSILON);
    assertEquals(6.0, scaledVertex.position.y(), EPSILON);
    assertEquals(12.0, scaledVertex.position.z(), EPSILON);

    Transformable builtChild = (Transformable) built.getComponentAt(1);
    Visual childVisual = (Visual) builtChild.getComponentAt(0);
    assertTrue(childVisual.getGeometry() instanceof IndexedTriangleArray);

    IndexedTriangleArray replacement = new IndexedTriangleArray();
    replacement.vertices.setValue(new Vertex[]{
        Vertex.createXYZIJKUV(9, 9, 9, 0, 1, 0, 0, 0),
        Vertex.createXYZIJKUV(10, 9, 9, 0, 1, 0, 1, 0),
        Vertex.createXYZIJKUV(9, 10, 9, 0, 1, 0, 0, 1)
    });
    replacement.polygonData.setValue(new int[]{0, 1, 2});

    HashMap<Geometry, Geometry> replacements = new HashMap<Geometry, Geometry>();
    replacements.put(rootVisual.getGeometry(), replacement);
    replacements.put(childVisual.getGeometry(), childVisual.getGeometry());
    builder.replaceGeometries(replacements);

    Transformable replaced = builder.buildTransformable();
    assertSame(replacement, ((Visual) replaced.getComponentAt(0)).getGeometry());
  }

  @Test
  public void encodeWritesFileAndCurrentDecoderFailureIsCharacterized() throws Exception {
    Transformable source = createSourceModel();
    ModelBuilder builder = ModelBuilder.newInstance(source);

    File file = new File("target/test-artifacts/model-builder/model-builder-test.zip");
    builder.encode(file);
    assertTrue(file.isFile());

    try {
      ModelBuilder.getInstance(file);
      fail("ModelBuilder.getInstance is expected to fail decoding encoded vertex data");
    } catch (RuntimeException expected) {
      assertNotNull(expected.getMessage());
    }

    ModelBuilder.forget(file);
  }

  private static Transformable createSourceModel() {
    Transformable root = new Transformable();
    root.setName("root");
    root.localTransformation.setValue(AffineMatrix4x4.createTranslation(1, 2, 3));

    IndexedTriangleArray indexedTriangleArray = new IndexedTriangleArray();
    indexedTriangleArray.vertices.setValue(new Vertex[]{
        Vertex.createXYZIJKUV(1, 2, 3, 0, 1, 0, 0, 0),
        Vertex.createXYZIJKUV(2, 2, 3, 0, 1, 0, 1, 0),
        Vertex.createXYZIJKUV(1, 3, 3, 0, 1, 0, 0, 1)
    });
    indexedTriangleArray.polygonData.setValue(new int[]{0, 1, 2});

    Visual rootVisual = new Visual();
    rootVisual.frontFacingAppearance.setValue(createAppearance(0x11AA33FF, true));
    rootVisual.scale.setValue(Matrix3x3.create(new Vector3(2, 0, 0), new Vector3(0, 3, 0), new Vector3(0, 0, 4)));
    rootVisual.setGeometry(indexedTriangleArray);
    root.addComponent(rootVisual);

    Transformable child = new Transformable();
    child.setName("child");
    child.localTransformation.setValue(AffineMatrix4x4.createTranslation(4, 5, 6));

    IndexedTriangleArray childGeometry = new IndexedTriangleArray();
    childGeometry.vertices.setValue(new Vertex[]{
        Vertex.createXYZIJKUV(0, 0, 0, 0, 1, 0, 0, 0),
        Vertex.createXYZIJKUV(1, 0, 0, 0, 1, 0, 1, 0),
        Vertex.createXYZIJKUV(0, 1, 0, 0, 1, 0, 0, 1)
    });
    childGeometry.polygonData.setValue(new int[]{0, 1, 2});

    Visual childVisual = new Visual();
    childVisual.frontFacingAppearance.setValue(createAppearance(0xFF5533AA, false));
    childVisual.setGeometry(childGeometry);
    child.addComponent(childVisual);

    root.addComponent(child);
    return root;
  }

  private static TexturedAppearance createAppearance(int argb, boolean alphaBlended) {
    BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
    image.setRGB(0, 0, argb);
    BufferedImageTexture texture = new BufferedImageTexture();
    texture.setBufferedImage(image);
    texture.setPotentiallyAlphaBlended(alphaBlended);

    TexturedAppearance appearance = new TexturedAppearance();
    appearance.setDiffuseColorTexture(texture);
    return appearance;
  }
}
